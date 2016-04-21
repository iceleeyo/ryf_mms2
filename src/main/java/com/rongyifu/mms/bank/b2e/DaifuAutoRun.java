package com.rongyifu.mms.bank.b2e;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.common.Constant.DaiFuTransType;
import com.rongyifu.mms.common.Constant.DaifuPstate;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.ewp.CancelPos;
import com.rongyifu.mms.ewp.SendEmailServer;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class DaifuAutoRun {

	private  AdminZHDao dao = new AdminZHDao();
	private TransactionDao transactionDao = new TransactionDao();
	private  B2EProcess b2e = null;
	
	/**
	 * 中国银行银企直连签到
	 */
	public  String sign() {
		// 暂时屏蔽中行签到
		if(1 == 1)
			return null;
		
		if(!Ryt.isStartService("启动中国银行银企直连签到服务"))
			return "hostName不匹配,拒绝启动服务";
		
		try {
			B2EGate g = dao.getOneB2EGate(B2ETrCode.BOC_GID);
			b2e = new B2EProcess(g, B2ETrCode.SIGN);
			B2ERet ret = b2e.submit();
			if (ret.isSucc()) {
				Object o = ret.getResult();
				if (o != null) {
					B2ESignResult result = (B2ESignResult) o;
					String sql = "update b2e_gate set busi_no = '"
							+ result.getDate() + "' ,token = '"
							+ result.getToken() + "' where gid = "
							+ B2ETrCode.BOC_GID;
					dao.update(sql);
					LogUtil.printInfoLog("DaifuAutoRun", "sign", "boc sign success! token: " + result.getToken());
					return "签到成功";
				}
				return "签到成功,token更新失败";
			} else {
				LogUtil.printInfoLog("DaifuAutoRun", "sign", "boc sign fail! errorMsg: " + ret.getMsg());
				// 签到失败发报警邮件
				sendWarnMail(ret.getMsg());
				return "签到失败";
			}
		} catch (Exception e) {
			LogUtil.printInfoLog("DaifuAutoRun", "sign", "boc sign occur exception! errorMsg: " + e.getMessage());
			// 签到异常发报警邮件
			sendWarnMail(e.getMessage());
			e.printStackTrace();
		}
		return "签到失败";
	}
	
	/**
	 * 发送中行签名失败报警邮件
	 */
	private void sendWarnMail(String errorMsg){
		String title = "代付交易报警";
		String content = "中国银行银企直连签到失败，失败原因：" + errorMsg;
		content += "\n注意：如果签到失败次数过多，UKey可能会被锁，请尽快联系相关人员进行处理。";
		
		try {
			CallPayPocessor.sendMail(content, title, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 每十分钟执行一次，进行交易结果的查询
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		if(!Ryt.isStartService("启动代付查询服务"))
			return;
		
		String sql = "select * from tr_orders where ptype in (5,6,7,8) and state =" + DaiFuTransState.PAY_PROCESSING + " and pstate=" + DaifuPstate.AUDIT_SUCCESS+"  and is_pay=1";//追加条件 is_pay =1  是否支付
		List<TrOrders> l = dao.query(sql, TrOrders.class);
		LogUtil.printInfoLog("DaifuAutoRun", "run", "代付交易查询订单："+l.size() + "笔\n" + sql);
		if (l.isEmpty())
			return;
		
		for (TrOrders o : l) {
			if (null == o || null==o.getGate())
				continue;
			
			try {
				Thread.sleep(1000); // 查询间隔1秒钟
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}			
			int gate = o.getGate();
			String[] orderInfo =null;
			try{
				orderInfo= transactionDao.queryOrderByOid(o.getUid(), o.getOid(), o.getOrgOid());
				if(orderInfo==null || orderInfo.length <=0)continue;
			}catch (Exception e) {
				LogUtil.printErrorLog("DaifuAutoRun", "run", "error"+e.getMessage(), e);
				continue;
			}
			String tableName = orderInfo[0];	// 表名
			String tseq = orderInfo[1];			// 电银流水号
			StringBuffer queryTermidsql=new StringBuffer("select * from ").append(tableName);
			queryTermidsql.append(" where tseq=").append(Ryt.addQuotes(tseq));
			OrderInfo info=(OrderInfo)transactionDao.query(queryTermidsql.toString(), OrderInfo.class).get(0);
			FeeCalcMode feeCalMode = null;
			Integer gid = 0;
			if (info.getData_source()!=6){ // 非POS转账
				try {
					feeCalMode = dao.getFeeModeByGate(o.getUid(),gate+"");
				} catch (Exception e1) {
					LogUtil.printErrorLog("DaifuAutoRun", "run", "查询网关错误,商户:"+o.getUid()+" 网关号:"+gate, e1);
				}
				if (feeCalMode==null) 
					continue;
				
				gid = feeCalMode.getGid();
			} else { // POS转账，除了盛京，其他银行都走交行超级网银
				gid = gate == 40006 ? 40006 : 40000;
			}
			
			if (gid == 0)
				continue;

			B2EGate g = dao.getOneB2EGate(gid);
			if (null == g)
				continue;

			// 查询订单状态
			int code = B2ETrCode.QUERY_ORDER_STATE;
			//农行银企直连特殊处理
			if(g.getGid()==40004){
				if(Ryt.empty(info.getP11())){
					code=B2ETrCode.ABC_QUERY_STATE_E;
				}else if(B2ETrCode.ABC_QUERY_STATE_E== Integer.parseInt(info.getP11())){
					code=B2ETrCode.ABC_QUERY_STATE_E;
				}else if(B2ETrCode.ABC_QUERY_STATE_L== Integer.parseInt(info.getP11())){
					code=B2ETrCode.ABC_QUERY_STATE_L;
				}
			}
			b2e = new B2EProcess(g, code);
			b2e.setOrders(o);
			
			try {

				B2ERet r = b2e.submit();
				B2ETradeResult result = r.getResult()==null ? null : (B2ETradeResult) r.getResult();
				// 如果银行返回还是处理中状态，则不需要执行后续的代码
				if (result !=null &&  DaiFuTransState.PAY_PROCESSING == result.getState()) 
					continue;
				else if(result == null)continue;
				
				String sql4=RecordLiveAccount.calUsableBalance(o.getAid(), o.getAid(), o.getPayAmt(), Constant.RecPay.INCREASE);
				if (r.isSucc()) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("bankDate", r.getBank_date());
					params.put("bankTime", r.getBank_time());
					params.put("errorMsg", r.getErrorMsg());
					params.put("msg", r.getMsg());
					params.put("resCode", r.getRes_code());
					params.put("statusInfo", r.getStatusInfo());
					params.put("trCode", r.getTrCode());
					params.put("gid", String.valueOf(r.getGid()));
					params.put("transStatus",String.valueOf(r.getTransStatus()));
					params.put("isSucc", String.valueOf(r.isSucc()));
					params.put("State",String.valueOf(result.getState()));
					params.put("bankSeq",String.valueOf(result.getBankSeq()));
//					params.put("result.getErrMsg()",String.valueOf(result.getErrMsg()));
					params.put("oid", String.valueOf(o.getOid()));
					LogUtil.printInfoLog("DaifuAutoRun", "run", "代付结果", params);
					
					// 获取代付交易类型名称
					String remark = Constant.getDaifuTransTypeName(o.getPtype());
					
					// 更新tr_orders表订单状态
					StringBuffer sql2 = new StringBuffer();
					sql2.append("update tr_orders set ");
					sql2.append("bank_date = ").append(r.getBank_date()).append(",");
					sql2.append("bank_time = ").append(DateUtil.getUTCTime(r.getBank_time()==null?"0":r.getBank_time())).append(",");
					sql2.append("state = ").append(result.getState()).append(",");
					sql2.append("tseq = '").append(result.getBankSeq()).append("',");
					sql2.append("remark = concat(remark, '-"+ r.getMsg() + "') ");
					sql2.append(" where oid = '").append(o.getOid()).append("'");
					sql2.append(" and state!=").append(DaiFuTransState.PAY_SUCCESS);
					
					// 更新tlog/hlog表订单状态
					StringBuffer sql3 = new StringBuffer();
					sql3.append("update " + tableName +"  set tstat=").append(result.getState() == DaiFuTransState.PAY_SUCCESS ? PayState.SUCCESS:PayState.FAILURE).append(",error_msg=").append(Ryt.addQuotes(r.getMsg()));
					if(result.getState()==DaiFuTransState.PAY_SUCCESS){
						String bkFeeMode=dao.getBkFeeModeByGate(o.getUid(), String.valueOf(o.getGate()));
						String bkFeeAmt=ChargeMode.reckon(bkFeeMode,o.getTransAmt());
						sql3.append(" ,bk_seq1=").append(Ryt.addQuotes(result.getBankSeq()));
						sql3.append(" ,bk_date=").append(r.getBank_date());
						sql3.append(" ,bank_fee=").append(bkFeeAmt);
						sql3.append(" ,bk_recv=").append(DateUtil.getUTCTime(r.getBank_time()==null?"0":r.getBank_time()));
					}
					sql3.append(" where mid = " + Ryt.addQuotes(o.getUid()));
					sql3.append("   and tseq =" + tseq);
					sql3.append("   and tstat!=" + PayState.SUCCESS);

					// 代发 计入账户流水
					// 同时吧可用余额增加上去
					// 代发
					if (o.getPtype() == DaiFuTransType.PAY_TO_PERSON || o.getPtype() == DaiFuTransType.PAY_TO_ENTERPRISE) {
						if (result.getState() == B2ETrCode.PAY_SUCC) {
							
							// ptype==7 修改tlog状态 插入流水
							AccSeqs param=new AccSeqs();
							param.setTrAmt(o.getTransAmt());
							param.setAid(o.getAid());
							param.setUid(o.getUid());
							param.setTrFee(o.getTransFee());
							param.setAmt(o.getPayAmt());
							param.setTbId(tseq);
							param.setTbName(tableName);
							param.setRemark(remark);
							// 插入流水表 tlog-流水
							List<String> sqlList = RecordLiveAccount.handleBalanceForTX(param);
							// 修改余额
							sqlList.add(sql3.toString());
							// 修改交易账户 结算账户余额
							sqlList.add(sql2.toString());
							String[] sqls = sqlList.toArray(new String[sqlList.size()]);
							dao.batchSqlTransaction(sqls);
							
							// 记录日志
							recordLog(sqls, "订单["+o.getOid()+"]代付成功");							
							
						} else if(result.getState() == B2ETrCode.PAY_Fail) { // 交易失败不记流水
							int date = DateUtil.today();
							int time= DateUtil.getCurrentUTCSeconds();
							List<String> sqls = new ArrayList<String>();
							String sql1=dao.updateTrOrdersState(o.getOid(), Constant.DaiFuTransState.PAY_FAILURE, "", "" + "付款失败:" + r.getMsg(),String.valueOf(date),String.valueOf(time),"", r.getMsg(),null,1);
							// 更新表tlog/hlog
							String sqlF = transactionDao.updateOrderStat(tableName, tseq,Constant.PayState.FAILURE,"付款失败,"+r.getMsg());
							sqls.add(sql1.toString());
							sqls.add(sqlF.toString());
							sqls.add(sql4);
							String[] sqls2 = sqls.toArray(new String[sqls.size()]);
							dao.batchSqlTransaction(sqls2);
							//注释撤销服务
							/*Integer orderDate=o.getSysDate();
							if (orderDate==date && info.getData_source()==6 ){
								String merId=o.getUid();
								String userCode="00000000000";//用户号
								String termid=info.getP8();//终端号
								String psam=info.getP12();//设备号
								int len=o.getOrgOid().length();
								String bill=o.getOrgOid().substring(0, len-2);//Pos交易订单号
								String tOutCardNo=o.getAccNo();String tReference=info.getP11();
								String tAuthCode="000000";String tTrace=info.getP9();
								Integer bk_date=Integer.parseInt(r.getBank_date());
								Integer bk_time=DateUtil.getUTCTime(r.getBank_time());
								CancelPos cancel_Pos=new CancelPos(psam, termid, userCode, bill, merId,tOutCardNo,tReference,tAuthCode,tTrace,1,bk_date,bk_time,"","",tseq);
								Thread thread=new Thread(cancel_Pos);
								thread.start();
							}*/
							// 记录日志
							recordLog(sqls2, "1、订单["+o.getOid()+"]代付失败，失败原因：" + r.getMsg());
						}
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void exec_pay() {
		TrOrders o = dao
				.queryForObject(
						"select * from tr_orders,fee_calc_mode  where tr_orders.uid=fee_calc_mode.mid and tr_orders.gate=fee.calc_mode.gate  ",
						TrOrders.class);
		if (null == o)
			return;
		// fee_
		B2EGate g = dao.getOneB2EGate(o.getGate());
		if (null == g)
			return;
		B2EProcess b2e = new B2EProcess(g, B2ETrCode.PAY_TO_OTHER);
		String sql5 = "update tr_orders set state=9  where oid='" + o.getOid()
				+ "'";
		dao.update(sql5);
		b2e.setOrders(o);
		B2ERet ret = b2e.submit();

		Map<String, String> params = new HashMap<String, String>();
		params.put("bankDate", ret.getBank_date());
		params.put("bankTime", ret.getBank_time());
		params.put("errorMsg", ret.getErrorMsg());
		params.put("msg", ret.getMsg());
		params.put("resCode", ret.getRes_code());
		params.put("statusInfo", ret.getStatusInfo());
		params.put("trCode", ret.getTrCode());
		params.put("gid", String.valueOf(ret.getGid()));
		// params.put("result", (String)ret.getResult());
		params.put("transStatus", String.valueOf(ret.getTransStatus()));
		params.put("isSucc", String.valueOf(ret.isSucc()));
		LogUtil.printInfoLog("DaifuAutoRun", "exec_pay", "代付结果", params);

		if (ret.isSucc()) {
			String bkSeq = (String) ret.getResult();
			dao.updateTrOrdersState(o.getGate(), o.getOid(), 5, bkSeq,
					dao.getLoginUserName() + ",代发经办成功");
		} else {
			// msg.append(o.getOid()).append(":").append(ret.getMsg()).append("\n");
			int i = dao.updateTrOrdersState(o.getGate(), o.getOid(), 6, "",
					dao.getLoginUserName() + ",代发经办失败:" + ret.getMsg());
			if (i == 1) {
				String sql4 = "update acc_infos set balance = balance + "
						+ o.getTransAmt() + " where aid = '" + o.getAid() + "'";
				dao.update(sql4);
			}

		}
	}

	private void recordLog(String sqls[], String remark){
		Map<String, String> logMap = LogUtil.createParamsMap();
		int k = 0;
		for(String item : sqls){
			logMap.put("sql[" + k + "]", item);
			k++;
		}
		LogUtil.printInfoLog("DaifuAutoRun", "run", remark, logMap);
	}
	
	/***
	 * Pos转账报警机制   
	 * 30 分钟内出现10笔 请求银行失败  发送报警邮件
	 */
	public void exec_df_alarm(){
		LogUtil.printInfoLog("DaifuAutoRun", "exec_df_alarm", "begin");
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.MINUTE, curr.get(Calendar.MINUTE)-30);
		Integer oldTime=DateUtil.getCurrentUTCSeconds(curr.getTime());//当前时间-30分钟
		Integer nowTime=DateUtil.getCurrentUTCSeconds();//当前时间
		Integer today=DateUtil.today();
		Integer yestoday=Integer.parseInt(DateUtil.yestodayStr().replace("-", ""));
		StringBuffer sql=new StringBuffer("select tseq from tlog where  ");
		if(oldTime>84600){
			sql.append("((sys_time >").append(oldTime).append(" and sys_date=").append(yestoday).append(" ) or");
			sql.append(" (").append("sys_time <").append(nowTime).append(" and sys_date=").append(today).append(" ) ) and ");
		}else{
			sql.append(" sys_time >").append(oldTime).append(" and ");
			sql.append("sys_date =").append(today).append(" and");
		}
		sql.append("  data_source=6  and ");
		sql.append("  tstat in (").append(Constant.PayState.FAILURE);
		sql.append(",").append(Constant.PayState.REQ_FAILURE).append(") ");
		List<String> list=dao.queryForStringList(sql.toString());
		if (list.size()>10){
			/*异步发送报警邮件*/
			StringBuffer tseqs=new StringBuffer();
			for (String tseq : list) {
				tseqs.append(tseq).append(",");
			}
			String content="30分钟内失败"+list.size()+"笔,交易流水号：\r\n"+tseqs.toString();
			String title="代付交易报警"+DateUtil.today();
			String receive="";
			LogUtil.printInfoLog("DaifuAutoRun", "exec_df_alarm", "邮件信息：\r\ntitle="+title+"\r\ncontent="+content);
			SendEmailServer emailServer=new SendEmailServer(content, title, receive);
			Thread thread=new Thread(emailServer);
			thread.start();
		}
		
	}
}
