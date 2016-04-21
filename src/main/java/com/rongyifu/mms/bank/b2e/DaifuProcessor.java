package com.rongyifu.mms.bank.b2e;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.ewp.SendEmailServer;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class DaifuProcessor implements ITaskProcessor{
	
	private static AdminZHDao dao = null;
	private TrOrders o = null;
	private TransactionDao transactionDao=new TransactionDao();
	
	public DaifuProcessor(AdminZHDao dao, TrOrders obj){
		this.dao = dao;
		this.o = obj;
	}
	
	@Override
	public void process() {
		LogUtil.printInfoLog("DaifuProcess", "Process", "订单[" + o.getOid() + "]付款开始");
		B2EGate g = dao.getOneB2EGate(o.getGate());
		if(null==g) 
			return;
		o.setAccName(g.getAccName());
		o.setAccNo(g.getAccNo());
		B2EProcess b2e = new B2EProcess(g,B2ETrCode.PAY_TO_OTHER);
//		Minfo minfo=dao.getMinfoById(o.getUid().toString());
		b2e.setOrders(o);
		B2ERet ret = b2e.submit();
		String bkDate=ret.getBank_date();
		String bkTime=ret.getBank_time();
		try {
			String[] orderInfo = transactionDao.queryOrderByOid(o.getUid(), o.getOid());
			String tableName = orderInfo[0];	// 表名
			String tseq = orderInfo[1];			// 电银流水号
			if (ret.isSucc()) {
				String sql1 = null;
				String sql2 = null;
				List<String> accTseq_sql=new ArrayList<String>();
				// 交行超级网银中间状态处理 
				if (ret.getTransStatus() == 2) { 
					sql1 = dao.updateTrOrdersRetInfo(o.getOid(),
							ret.getStatusInfo(), 1);
					if(g.getGid()	==	40004	&&	ret.getResult()	!=	null){
						//农行银企直连修改订单
						if(B2ETrCode.ABC_QUERY_STATE_E==(Integer)ret.getResult()){//异常处理 
							sql2="update "+tableName+" set p11="+B2ETrCode.ABC_QUERY_STATE_E +" where tseq="+Ryt.addQuotes(tseq);
						}else if(B2ETrCode.ABC_QUERY_STATE_L==(Integer)ret.getResult()){ //落地处理
							sql2="update "+tableName+" set p11="+B2ETrCode.ABC_QUERY_STATE_L +" where tseq="+Ryt.addQuotes(tseq);
						}
					}
					if(g.getGid()==40005 && null !=ret.getResult()){
						sql2="update tr_orders set tseq="+Ryt.addQuotes(ret.getResult().toString())+" where oid="+Ryt.addQuotes(o.getOid());//把银行流水存入订单表
					}
					LogUtil.printInfoLog("DaifuProcess", "Process", "订单[" + o.getOid() + "]付款处理中，等待查询接口获取付款结果！");
				} else {
					String bkFeeMode=dao.getBkFeeModeByGate(o.getUid(), String.valueOf(o.getGate()));
					String bkFeeAmt=ChargeMode.reckon(bkFeeMode,o.getTransAmt());
					String bkSeq = (String)(ret.getResult()==null?"":ret.getResult());
					sql1 = dao.updateTrOrdersState(o.getOid(), Constant.DaiFuTransState.PAY_SUCCESS, bkSeq, "" + "付款成功", bkDate,bkTime, ret.getRes_code(), ret.getMsg(), "",1);
					sql2 = transactionDao.updateOrderStat(tableName, tseq, Constant.PayState.SUCCESS,bkSeq,Integer.parseInt(bkDate==null?"0":bkDate),bkTime==null?"0":bkTime,bkFeeAmt);
					// 获取代付交易类型名称
					String remark = Constant.getDaifuTransTypeName(o.getPtype());
					
					AccSeqs param=new AccSeqs();
					param.setTbName(tableName);
					param.setTbId(tseq);
					param.setUid(o.getUid());
					param.setAid(o.getUid());
					param.setTrAmt(o.getTransAmt());
					param.setTrFee(o.getTransFee());
					param.setAmt(o.getPayAmt());
					param.setRemark(remark);
					param.setRecPay((short)Constant.RecPay.REDUCE);
					accTseq_sql=RecordLiveAccount.handleBalanceForTX(param);
					
				}
				accTseq_sql.add(sql1);
				if(ret.getTransStatus()!=2) accTseq_sql.add(sql2);
				else if(ret.getTransStatus()==2 && g.getGid()==40004)accTseq_sql.add(sql2);
				else if(ret.getTransStatus()==2 && g.getGid()==40005)accTseq_sql.add(sql2);
				String [] sqls=accTseq_sql.toArray(new String[accTseq_sql.size()]);
//				dao.batchSqlTransaction(slqs,"type");
				execBatchTrans(sqls, "DaifuProcessor");/**同步锁**/
				
				Map<String, String> logMap = LogUtil.createParamsMap();
				int k = 0;
				for(String item : accTseq_sql){
					logMap.put("sql[" + k + "]", item);
					k++;
				}
				LogUtil.printInfoLog("DaifuProcessor", "process", "代付成功", logMap);
			} else {
				int date = DateUtil.today();
				int time= DateUtil.getCurrentUTCSeconds();
				Integer tstat=null;
				if (ret.getTransStatus()==1){
					tstat=Constant.PayState.FAILURE;
				}else if(ret.getTransStatus()==3){
					tstat=Constant.PayState.REQ_FAILURE;
				}
				String sql3 =  RecordLiveAccount.calUsableBalance(o.getUid(), o.getAid(), o.getPayAmt(), Constant.RecPay.INCREASE);
				String sql1=dao.updateTrOrdersState(o.getOid(), tstat, "", "" + "付款失败:" + ret.getErrorMsg(),String.valueOf(date),String.valueOf(time),"",ret.getMsg(),null,1);
				String sql2=transactionDao.updateOrderStat(tableName, tseq, tstat,"付款失败,"+ret.getMsg());
				List<String> sqllist=new ArrayList<String>();
				/*请求银行失败 时   金额 保持冻结    交易失败时  金额加回去*/
				if (ret.getTransStatus()==3){
					sqllist.add(sql1);sqllist.add(sql2);
					/**请求银行失败发送报警邮件**/
					String toAccNo=o.getToAccNo();
					String toCardH=toAccNo.substring(0, 6);String toCardE=toAccNo.substring(toAccNo.length()-4, toAccNo.length());
					String toAccNoNew=toCardH+"******"+toCardE;//处理卡号  前6后4 中间6个*
					StringBuffer mailContent=new StringBuffer("商户号：").append(o.getUid()).append("\r\n");
					mailContent.append("交易类型：").append(AppParam.df_transType.get(o.getPtype().intValue())).append("\r\n");
					mailContent.append("代付流水号：").append(tseq).append("\r\n").append("支付渠道：").append(o.getGate()).append("\r\n");
					mailContent.append("交易金额：").append(Ryt.div100(o.getTransAmt())).append("\r\n").append("收款银行名称：").append(o.getToBkName()).append("\r\n");
					mailContent.append("收款账户名：").append(o.getToAccName()).append("\r\n").append("收款账户：").append(toAccNoNew).append("\r\n");
					mailContent.append("该代付交易请求银行失败！");
					String title="代付交易报警"+DateUtil.today();String receive="";
					SendEmailServer emailServer=new SendEmailServer(mailContent.toString(), title, receive);
					Thread thread=new Thread(emailServer);//发送报警邮件
					thread.start();
				}else if(ret.getTransStatus()==1){
					sqllist.add(sql1);sqllist.add(sql2);sqllist.add(sql3);
				}
				String[] sqls=sqllist.toArray(new String[sqllist.size()]);
//				dao.batchSqlTransaction(sqls);
				execBatchTrans(sqls,"DaifuProcessor");/***同步锁***/
				Map<String, String> logMap = LogUtil.createParamsMap();
				int k = 0;
				for(String item : sqls){
					logMap.put("sql[" + k + "]", item);
					k++;
				}
				LogUtil.printInfoLog("DaifuProcessor", "process", "代付失败", logMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.printInfoLog("DaifuProcess", "Process", "订单[" + o.getOid() + "]付款完成");
	}
	
	
	/****
	 * 执行同步事务
	 * @param sqls
	 * @param type
	 */
	public synchronized static void execBatchTrans(String[] sqls,String type){
		try {
			dao.batchSqlTransaction(sqls, type);
		} catch (Exception e) {
			Map<String, String> logMap = LogUtil.createParamsMap();
			int k = 0;
			for(String item : sqls){
				logMap.put("sql[" + k + "]", item);
				k++;
			}
			LogUtil.printErrorLog("DaifuProcessor", "execBatchTrans", "代付同步事务异常", logMap, e);
		}
	}
	
}