package com.rongyifu.mms.ewp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bank.b2e.B2EProcess;
import com.rongyifu.mms.bank.b2e.B2ERet;
import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.MerZHDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class CallDaiFu {
	protected MerZHDao zhDao = new MerZHDao();
	protected AdminZHDao dao = new AdminZHDao();
	protected TransactionDao transactionDao = new TransactionDao();
	protected SystemDao sysDao=new SystemDao();

	public B2ERet callBank(TrOrders trOrders, int gate, int trCode,String tseq) throws Exception {
		B2EGate g = dao.getOneB2EGate(gate);
		B2EProcess b2eProcess = new B2EProcess(g, trCode);
		b2eProcess.setOrders(trOrders);
		B2ERet ret = b2eProcess.submit();
		try {
			String bkDate = ret.getBank_date();
			String bkTime = ret.getBank_time();
			
			String[] orderInfo = transactionDao.queryOrderByOid(trOrders.getUid(), trOrders.getOrgOid());
			String tableName = orderInfo[0];	// 表名
//			String tseq1 = orderInfo[1];		// 电银流水号
			if (ret.isSucc()) {
				List<String> sqlList = new ArrayList<String>();
				String sql1 = null;
				String sql2 = null;
				// 交行超级网银中间状态处理
				if (ret.getTransStatus() == 2) { 
					sql1 = dao.updateTrOrdersRetInfo(trOrders.getOid(),
							ret.getStatusInfo(), 1);
					/*sql2 = this.updateTlogStat(trOrders.getUid(),
							trOrders.getOrgOid(), Constant.PayState.WAIT_PAY,ret.getErrorMsg());*/
				} else {
					String bkFeeAmt="0";
					String bkSeq = (String) ret.getResult();
					sql1 = dao.updateTrOrdersState(trOrders.getOid(), Constant.DaiFuTransState.PAY_SUCCESS, bkSeq, "" + "付款成功", bkDate,
							bkTime, ret.getRes_code(), ret.getErrorMsg(), "",1);
					sql2 = this.updateTlogStat(trOrders.getUid(),
							trOrders.getOrgOid(), Constant.PayState.SUCCESS,bkSeq,bkDate,bkTime,bkFeeAmt);
					
					// 记录流水
					AccSeqs param = new AccSeqs();
					param.setTbName(tableName);
					param.setTbId(tseq);
					param.setUid(trOrders.getUid());
					param.setAid(trOrders.getUid());
					param.setTrAmt(trOrders.getTransAmt());
					param.setTrFee(trOrders.getTransFee());
					param.setAmt(trOrders.getPayAmt());
					param.setRemark("转账汇款");
					param.setRecPay((short) Constant.RecPay.REDUCE);
					sqlList = RecordLiveAccount.handleBalanceForTX(param);
				}
				sqlList.add(sql1);
				if(ret.getTransStatus()!=2)sqlList.add(sql2);
				String[] sqlsList = sqlList.toArray(new String[sqlList.size()]);
//				dao.batchSqlTransaction(sqlsList,"type");
				execBatchTrans(sqlsList, "type");//新增 录入流水同步锁
				
				// 记录日志
				Map<String, String> logMap = LogUtil.createParamsMap();
				int k = 0;
				for(String item : sqlsList){
					logMap.put("sql[" + k + "]", item);
					k++;
				}
				LogUtil.printInfoLog("callDaifu", "callBank", "转账处理完成", logMap);
			} else {
				int date = DateUtil.today();
				int time= DateUtil.getCurrentUTCSeconds();
				Integer tstat=null;/*ret.getTransStatus == 3  请求银行失败    1 交易失败  */
				if(ret.getTransStatus()==3){
					tstat=Constant.PayState.REQ_FAILURE;
				}else if(ret.getTransStatus()==1){
					tstat=Constant.PayState.FAILURE;
				}
				String sql1=dao.updateTrOrdersState(trOrders.getOid(), Constant.DaiFuTransState.PAY_FAILURE, "", "" + "付款失败:" + ret.getErrorMsg(),String.valueOf(date),String.valueOf(time),"",ret.getMsg(),null,1);
				String sql2= transactionDao.updateOrderStat(tableName, tseq,tstat,""+ret.getMsg());
				String sql3 = RecordLiveAccount.calUsableBalance(trOrders.getUid(), trOrders.getAid(), trOrders.getPayAmt(), Constant.RecPay.INCREASE);
				String[] sqls={sql1,sql2,sql3};
//				dao.batchSqlTransaction(sqls);
				execBatchTrans(sqls);//同步执行修改 新增同步锁
				
				// 记录日志
				Map<String, String> logMap = LogUtil.createParamsMap();
				int k = 0;
				for(String item : sqls){
					logMap.put("sql[" + k + "]", item);
					k++;
				}
				LogUtil.printInfoLog("callDaifu", "callBank", "转账失败", logMap);
			}
			
		} catch (Exception e) {
			Map<String, String> map=new HashMap<String, String>();
			map.put("tseq", tseq);
			map.put("oid", trOrders.getOid());
			LogUtil.printErrorLog("CallDaiFu", "callBank", "代付异常",map,e);
			throw new Exception("转账汇款异常");
			
		}
		return ret;
	}

	public  TrOrders getOrders(String merName, String oid,
			String transAmt, String feeAmt, String merId, String toAccNo,
			String toAccName, String tobankName, String tobankId,
			String providId, String cardFlag, String payAmt, String state,
			String accNo, String accName, short ptype, int gid, String pstate) {
		TrOrders orders = new TrOrders();
		String tseq=Ryt.genOidBySysTime();
		AccInfos accinfo=zhDao.queryJSZHYE(merId).get(0);
		orders.setAccName(accName);
		orders.setAccNo(accNo);
		orders.setAname(merName);
		orders.setAid(accinfo.getAid());
		orders.setCardFlag(Short.valueOf(cardFlag));
		orders.setTransAmt(Long.parseLong(transAmt));
		orders.setOid(tseq);
		orders.setTransFee(Integer.parseInt(feeAmt));
		orders.setUid(merId);
		orders.setToAccNo(toAccNo);
		orders.setToAccName(toAccName);
		orders.setToBkName(tobankName);
		orders.setToBkNo(tobankId);
		orders.setToProvId(Integer.parseInt(providId));
		orders.setPayAmt(Long.parseLong(payAmt));
		orders.setState(Short.valueOf(state));
		orders.setPtype(ptype);
		orders.setGate(gid);
		orders.setOrgOid(oid);
		orders.setPstate(Short.valueOf(pstate));
		return orders;
	}

	/***
	 * 保存订单
	 * 
	 * @param trOrders
	 */
	public void saveTrOrder(TrOrders trOrders) {
		zhDao.addSinglePay_N(trOrders.getAname(), trOrders.getTransFee(),
				trOrders.getTransAmt(), trOrders.getOid(), trOrders.getUid(),
				trOrders.getToAccNo(), trOrders.getToAccName(),
				trOrders.getToBkName(), trOrders.getToBkNo(),
				trOrders.getToProvId(), trOrders.getCardFlag().intValue(),
				trOrders.getPayAmt(), trOrders.getState().intValue(),
				trOrders.getPriv(), trOrders.getAccNo(), trOrders.getAccName(),
				trOrders.getGate(), trOrders.getOrgOid(), trOrders.getAid(),
				trOrders.getPstate());
	}

	/**
	 * 保存tlog
	 * 
	 * @param ip
	 * @param mdate
	 * @param mid
	 * @param oid
	 * @param amount
	 * @param type
	 * @param sys_date
	 * @param init_sys_date
	 * @param sys_time
	 * @param gate
	 * @param termid 终端号 保存至P8单位
	 * 
	 */
	public void saveTlog(String ip, String mdate, String mid, String oid,
			String amount, String type, int sys_date, int init_sys_date,
			int sys_time, int gate,String toAccNo,String toAccName,String toBkNo,String termid,String trace,String reference,String psam,Integer gid,Integer tstat,String msg) {
		transactionDao.insertTlog(ip, mdate, mid, oid, amount, type, sys_date,
				init_sys_date, sys_time, gate,termid,toAccNo,toAccName,toBkNo,trace,reference,psam,gid,tstat,msg);
	}

	/***
	 * 更新状态
	 * 
	 * @param mid
	 * @param oid
	 * @param stat
	 */
	public String updateTlogStat(String mid, String oid, int stat,String bkseq,String bk_date,String bk_time,String bkFeeAmt) {
		int bkDate=DateUtil.today();
		int bkTime=DateUtil.now();
		if(null!=bk_date && !"".equals(bk_date)){
			bkDate=Integer.parseInt(bk_date);
		}
		
		if(null==bk_time || "".equals(bk_time)){
			bk_time=String.valueOf(bkTime);
		}
        
        if(null==bkseq || "".equals(bkseq)){
            bkseq=String.valueOf(bkseq);
        }
        
		return transactionDao.updateTlogStat(mid, oid, stat,bkseq,bkDate,bk_time,bkFeeAmt);
	}
	
	/***
	 * 更新状态
	 * 
	 * @param mid
	 * @param oid
	 * @param stat
	 */
	public String updateTlogStat(String mid, String oid, int stat,String errorMsg) {
		return transactionDao.updateTlogStat(mid, oid, stat,errorMsg);
	}

	public String[] queryTseq(String oid){
		String result=transactionDao.queryTseq(oid);
		return result.split(",");
	}
	/****
	 * 产生12位流水 
	 * @param Tseq
	 * @return
	 */
	public String retTseq(String Tseq){
		String  t0="000000000000";
		int len=Tseq.length();
//		String HHMMSS=String.valueOf(DateUtil.now());
//		String ss=HHMMSS.substring(0,HHMMSS.length()-4);
		if(len<12 && len >0){
			t0=t0.substring(len, 12);
			Tseq=t0+Tseq;
		}else if(len>12){
			Tseq=Tseq.substring(0, 12);
		}
		String result=Tseq;
		return result;
		
	}
	
	public B2ERet getB2eRet(){
		B2ERet ret=new B2ERet();
		ret.setBank_date("20130221");
		ret.setBank_time("191919");
		ret.setErr("fasdfasd");
		ret.setErrorMsg("buxiaode");
		ret.setSucc(true);
		ret.setTrCode("210224");
		ret.setResult("faskldfjalsd");
		ret.setTransStatus(2);
		return ret;
	}
	
	/****
	 * 执行同步事务
	 * @param sqls
	 * @param type
	 */
	public synchronized void execBatchTrans(String[] sqls,String type){
		try {
			dao.batchSqlTransaction(sqls, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/****
	 * 执行同步事务
	 * @param sqls
	 * @param type
	 */
	public synchronized void execBatchTrans(String[] sqls){
		try {
			dao.batchSqlTransaction(sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
