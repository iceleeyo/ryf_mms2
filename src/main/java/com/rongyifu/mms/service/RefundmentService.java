package com.rongyifu.mms.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rytong.encrypto.provider.RsaEncrypto;

import org.directwebremoting.io.FileTransfer;

import cert.CertUtil;

import com.rongyifu.mms.bean.BatchRefundBean;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.RefundOB;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RefundDao;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.quartz.jobs.RefundSyncJob;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.ErrorCodes;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class RefundmentService {
	
	private SettlementDao settlementDao = new SettlementDao();
	private RefundDao refundDao = new RefundDao();
	
	public List<BatchRefundBean> uploadBatchRefundFil(String fileContent,String tt){
		if(fileContent==null||tt==null) return null;
		List<BatchRefundBean> aList = new ArrayList<BatchRefundBean>();
		try {
			String[] lines = fileContent.split("\r\n");
			if(tt.equals("BYTSEQ")){//按流水号
				for(String aLine : lines){
					String[] data = aLine.split(",");
					if(data.length!=2) return null;
					try {
						Long.parseLong(data[0].trim());
						Integer.parseInt(Ryt.mul100(data[1].trim()));
					} catch (Exception e) {
						return null;
					}
					aList.add(new BatchRefundBean(data[0].trim(),data[1].trim()));
				}
			}else{
			
				for(String aLine : lines){
					
					String[] data = aLine.split(",");
					if(data.length!=4) return null;
					try {
						Long.parseLong(data[0].trim());
//						Integer.parseInt(data[1].trim());
						Integer.parseInt(data[2].trim());
						Integer.parseInt(Ryt.mul100(data[3].trim()));
					} catch (Exception e) {
						return null;
					}
					aList.add(new BatchRefundBean(data[0].trim(),data[1].trim(),data[2].trim(),data[3].trim()));
				}
				
				
			}
			
		} catch (Exception e) {
			return null;
		}
		return aList;
	}
	
	public List<BatchRefundBean> doBatchRefund(List<BatchRefundBean> dataList,String longMid,String refundReason ,String tt){

//		int succcount = 0;
//		long amt = 0;
		for (BatchRefundBean bean : dataList) {
			if (bean.getState() == 1) continue;
			
			RefundOB refundOb = null;
			if("BYTSEQ".equals(tt.trim())){
				
				refundOb = getRefundOBByTseq2(bean.getInitTseq(), longMid);
			}else{
				refundOb =getRefundOBByMer(bean.getInitMerOid(),bean.getInitMid(),Integer.parseInt(bean.getInitMdate()),longMid);
			}
			
			if (refundOb == null) {
				bean.setState(1);
				bean.setErrMsg("订单不存在");
				continue;
			}
			if (refundOb.isCanRefund()) {
				// 退款申请
				refundOb.setApplyRrefundAmount(bean.getInitRefAmt());// 申请退款的金额
				refundOb.setRefundReason(refundReason);
				refundOb.setNotRefuntReason(applyRefund(refundOb));// 申请退款处理
			}
		
			// 判断code
			if(refundOb.getCode()==0){
				bean.setState(0);// 退款申请成功
//				succcount++;
//				amt += Long.parseLong(Ryt.mul100(bean.getInitRefAmt()));
			}else{
				bean.setState(1); // 原交易不可以退款
				bean.setErrMsg(refundOb.getNotRefuntReason());
			}
		}
		return dataList;
	}


	/**
	 * 经办中撤销退款 1、将hlog表中的退款总额减回去 2、更新退款表中订单的状态为撤销退款 经办时间 撤销退款原因
	 * @param loginmid
	 * @param loginuid
	 * @param tseqs
	 * @return
	 */
	public String retroversionRefund(List<RefundLog> refLogList) {
		String returnStr = "经办撤销完成!";
		StringBuffer errorBuff = new StringBuffer();
		int nowdate = DateUtil.today();
		String tseqList = "";
		for (RefundLog refLog : refLogList) {
			int refId = refLog.getId();
			if(Ryt.empty(tseqList))
				tseqList += refLog.getTseq();
			else
				tseqList += "," + refLog.getTseq();
			
			String tableName = refLog.getSys_date() == nowdate ? Constant.TLOG : Constant.HLOG ;
			int refundAmt=0;
			try {
				 refundAmt = refundDao.queryRefundAmt(tableName, refLog.getTseq());
				
			} catch (Exception e) {
				tableName=tableName.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
				 refundAmt = refundDao.queryRefundAmt(tableName, refLog.getTseq());
			}
			
			try {
				refundDao.refundCancel(refLog, tableName, refundAmt);
			} catch (Exception e) {
				errorBuff.append(refId + ",");
				returnStr = "经办撤销异常!";
				//e.printStackTrace();
			}
			try {
				requestBgRetUrl(refLog, "C");
			} catch (Exception e) {
			}
		}
		settlementDao.saveOperLog("退款经办", "["+tseqList+"] 退款撤销完成");
		if (errorBuff.length() != 0) {
			errorBuff.deleteCharAt(errorBuff.lastIndexOf(",")).append("的记录经办撤销失败!");
			returnStr = returnStr + "\n" + "退款流水号为" + errorBuff.toString();
		}
		return returnStr;
	}

	/**
	 * 运行经办 判断一个商户的多条订单的总退款金额 是否大于
	 * 该商户在minfo表中的余额 
	 * 更新refund_log 状态为退款完成, 经办时间;
	 * 向account表中插入数据;
	 * 更新minfo表中的余额
	 * @param refLogList 传过来的参数 key 为 退款单号 value 为退款单对象
	 */
	public String verifyAccount(List<RefundLog> refLogList) {

		String returnStr = "经办完成!";
		StringBuffer noMotionHandle = new StringBuffer();// 因余额不足不能完成经办的商户
		StringBuffer exceptionBuff = new StringBuffer(); // 因系统异常不能完成经办的商户
		StringBuffer checkRefundAmtException = new StringBuffer();//记录校验退款金额失败的信息
		StringBuffer noticeException = new StringBuffer();//记录通知商户产生异常的信息
		String tseqList = "";// 电银流水号列表,多个流水号以逗号分隔
		
		// mid的所有RefundLog List
		HashMap<String, ArrayList<RefundLog>> midRefundLogListMap = new HashMap<String, ArrayList<RefundLog>>();
		for (RefundLog refLog : refLogList) {
			List<RefundLog> theList = midRefundLogListMap.get(refLog.getMid());
			if (theList != null) {
				theList.add(refLog);
			} else {
				ArrayList<RefundLog> aList2 = new ArrayList<RefundLog>();
				aList2.add(refLog);
				midRefundLogListMap.put(refLog.getMid(), aList2);
			}
			
			if(Ryt.empty(tseqList))
				tseqList += refLog.getTseq();
			else
				tseqList += "," + refLog.getTseq();
		}
		
		// 统计各个订单总退款金额是否超过交易金额
		Map<String, Integer> tseqMap = refundDao.queryRefundAmtIsExceedTransAmt(tseqList);
		
		// 判断余额
		for (String theMid : midRefundLogListMap.keySet()) {
			boolean isException = false;
			
			List<RefundLog> theList = midRefundLogListMap.get(theMid);
			long refbalance = 0;
			long refMerFee=0;
			long OrgAmt=0;
			for (RefundLog r : theList) {
				refbalance += r.getRef_amt();
				refMerFee +=r.getMerFee();
				OrgAmt+=r.getOrgAmt();
			}
			int isRefundFee=refundDao.getRefundFee(theMid);
			long balance = refundDao.getBalance(theMid);
			
			long ref=getFee(isRefundFee,OrgAmt,refbalance,refMerFee);
			
			// 可以退款的订单,进行退款数据库操作
			if (balance >= (refbalance-ref)) {
				int refFee = refundDao.getRefundFee(theMid);
				ArrayList<String> sqlsList = new ArrayList<String>();
				for (RefundLog bean : theList) {
					try {
						// 校验退款金额是否超过交易金额
						if(!checkRefundAmt(tseqMap, bean.getTseq())){
							checkRefundAmtException.append(bean.getTseq() + ",");
							continue;
						}
						
						refundDao.refundHandle(bean, refFee,sqlsList);// 完成退款经办的数据库操作
					} catch (Exception e) {
						exceptionBuff.append(bean.getId() + ",");
						isException = true;
						
						LogUtil.printErrorLog("RefundmentService", "refundException", "refundId=" + bean.getId() + " msg=" + e.getMessage());
						e.printStackTrace();
					}
					try {
						if(!isException){
							// 退款经办成功信息同步给清算系统
							RefundSyncJob.addJob(bean.getMid(), String.valueOf(bean.getId()), RefundSyncJob.REFUND_TYPE_JB);
							
							requestBgRetUrl(bean,"S");
						}
					} catch (Exception e) {
						noticeException.append(bean.getId() + ",");
					}
				}
			} else {// 余额不足不可以退款的订单
				noMotionHandle.append(theMid + ",");
			}
		}

		refundDao.saveOperLog("退款经办", "[" + tseqList + "] 退款经办完成");
		
		if (noMotionHandle.length() != 0) {
			noMotionHandle.deleteCharAt(noMotionHandle.lastIndexOf(","));
			returnStr += "\n商户号[" + noMotionHandle.toString() + "]：商户账户余额不足,不能经办！";
		}
		if (exceptionBuff.length() != 0) {
			exceptionBuff.deleteCharAt(exceptionBuff.lastIndexOf(","));
			returnStr += "\n退款流水号[" + exceptionBuff.toString() + "]：系统异常退款经办失败！";
		}
		if(checkRefundAmtException.length() > 0){
			checkRefundAmtException.deleteCharAt(checkRefundAmtException.lastIndexOf(","));
			returnStr += "\n电银流水号[" + checkRefundAmtException.toString() + "]：退款总金额超过交易金额！";
		}
		if(noticeException.length() > 0){
			noticeException.deleteCharAt(noticeException.lastIndexOf(","));
			returnStr += "\n退款流水号[" + noticeException.toString() + "]：通知商户后台失败！";
		}
		
		if(!Ryt.empty(returnStr))
			LogUtil.printInfoLog("RefundmentService", "verifyAccount", returnStr + "\n电银流水号：" + tseqList);
		
		return returnStr;
	}
	
	/**
	 * 校验退款总额是否超过交易金额
	 * 		true：未超过，可以退款
	 * 		false：超过，不能退款
	 * @param tseqMap
	 * @param tseq
	 * @return
	 */
	private boolean checkRefundAmt(Map<String, Integer> tseqMap, String tseq){
		if (tseqMap.containsKey(tseq)) {
			Integer refFlag = tseqMap.get(tseq);
			return refFlag.intValue() == 0;
		} else
			return true;
	}
	
	/**
	 *联机退款 判断一个商户的多条订单的总退款金额 是否大于
	 * 该商户在minfo表中的余额 
	 * 向refund_log 联机退款的流水号;
	 * 接入银行退款接口
	 * 更新minfo表中的余额
	 * @param refLogList 传过来的参数 key 为 退款单号 value 为退款单对象
	 */
	public String OnlineRefund(List<RefundLog> refLogList) {
		String returnStr =null;
		StringBuffer noMotionHandle = new StringBuffer();// 因余额不足不能完成联机退款的商户
		StringBuffer exceptionBuff = new StringBuffer(); // 因系统异常不能完成联机退款的商户

		HashMap<String, ArrayList<RefundLog>> midRefundLogListMap = new HashMap<String, ArrayList<RefundLog>>();
		for (RefundLog refLog : refLogList) {
			List<RefundLog> theList = midRefundLogListMap.get(refLog.getMid());
			if (theList != null) {
				theList.add(refLog);
			} else {
				ArrayList<RefundLog> aList2 = new ArrayList<RefundLog>();
				aList2.add(refLog);
				midRefundLogListMap.put(refLog.getMid(), aList2);
			}
		}
		
		String tseqList = "";
		// 判断余额
		for (String theMid : midRefundLogListMap.keySet()) {
			boolean isException = false;
			OnlineRefundBean onlinerefundbean =new OnlineRefundBean();
			List<RefundLog> theList = midRefundLogListMap.get(theMid);
			long refbalance = 0;
			long refMerFee=0;
			long OrgAmt=0;
			for (RefundLog r : theList) {
				refbalance += r.getRef_amt();
				refMerFee +=r.getMerFee();
				OrgAmt+=r.getOrgAmt();
			}
			int isRefundFee=refundDao.getRefundFee(theMid);
			long balance = refundDao.getBalance(theMid);
			
			long ref=getFee(isRefundFee,OrgAmt,refbalance,refMerFee);
			
			// 可以退款的订单,进行退款数据库操作
			if (balance >= (refbalance-ref)) {
				int refFee = refundDao.getRefundFee(theMid);
				ArrayList<String> sqlsList = new ArrayList<String>();
				
				for (RefundLog bean : theList) {
					if(Ryt.empty(tseqList))
						tseqList += bean.getTseq();
					else
						tseqList += "," + bean.getTseq();
					
					try {
						onlinerefundbean=refundDao.OnlinerefundHandle(bean);// 接入银行退款接口
						String status=onlinerefundbean.getOrderStatus();
						String Bath=onlinerefundbean.getRefBatch();
						String errorMsg=onlinerefundbean.getRefundFailureReason();
						int id = onlinerefundbean.getId();
						String sql = null;
						if((RefundUtil.ORDER_STATUS_ACCEPT).equals(status)){
							 sql = "update refund_log set online_refund_state =" +RefundUtil.ORDER_STATUS_PROCESSED+ ", refund_type=1 where id=" + id+ "  and online_refund_id= '" +Bath + "'";
							returnStr="退款流水号为：'"+bean.getId()+"'退款申请被接受！";
						}else if((RefundUtil.ORDER_STATUS_FAILURE).equals(status)){
							 sql="update refund_log set online_refund_state = " + status+ ", refund_type=1 ,online_refund_reason='"+ ErrorCodes.Alipay_Refund.get(errorMsg) + "' where id=" + id + " and online_refund_id= '" + Bath+ "'";
							returnStr="退款流水号为：'"+bean.getId()+"'联机退款失败,失败原因:'"+ErrorCodes.Alipay_Refund.get(onlinerefundbean.getRefundFailureReason())+"'！";
						}
						else if((RefundUtil.ORDER_STATUS_PROCESSED).equals(status)){
							 sql = "update refund_log set online_refund_state =" +status+ ", refund_type=1 where id=" + id+ "  and online_refund_id= '" +Bath + "'";
							returnStr="退款流水号为：'"+bean.getId()+"'银行处理中！";
						}else if((RefundUtil.QUERT_BANK_FAILURE).equals(status)){
							 sql = "update refund_log set online_refund_state = " + status+ ", refund_type=1 , online_refund_reason='"+ errorMsg + "' where id=" + id + " and online_refund_id= '" + Bath+ "'";
							returnStr="退款流水号为：'"+bean.getId()+"'请求银行失败,失败原因:'"+onlinerefundbean.getRefundFailureReason()+"'！";
						}else if((RefundUtil.ORDER_STATUS_SUCCESS).equals(status)){
							bean.setOnlineRefundId(onlinerefundbean.getRefBatch());
							bean.setOnlineRefundState(2);
							bean.setRefundType(1);
							refundDao.refundHandle(bean, refFee,sqlsList);// 完成退款经办的数据库操作
							returnStr="退款成功！";
						}
						
						if(sql!=null){
							refundDao.update(sql);
						}
						
					} catch (Exception e) {
						exceptionBuff.append(bean.getId() + ",");
						returnStr = "联机退款异常!";
						e.printStackTrace();				
						isException = true;
					}
					try {
						if(!isException&&(RefundUtil.ORDER_STATUS_SUCCESS).equals(onlinerefundbean.getOrderStatus())){
							// 联机退款成功信息同步给清算系统
							RefundSyncJob.addJob(bean.getMid(), String.valueOf(bean.getId()), RefundSyncJob.REFUND_TYPE_LJ);
							
							requestBgRetUrl(bean,"S");
						}
					} catch (Exception e) {
						//e.printStackTrace();
						returnStr+="，流水号为["+bean.getTseq()+"]的订单通知商户后台失败！";
					}
				}
			} else {// 余额不足不可以退款的订单
				noMotionHandle.append(theMid + ",");
			}
		}

		refundDao.saveOperLog("联机退款", "["+tseqList+"] 联机退款完成");
		if (noMotionHandle.length() != 0) {
			noMotionHandle.deleteCharAt(noMotionHandle.lastIndexOf(",")).append("商户账户余额不足,不能联机退款");
			returnStr = returnStr + "\n" + noMotionHandle.toString();
		}
		if (exceptionBuff.length() != 0) {
			exceptionBuff.deleteCharAt(exceptionBuff.lastIndexOf(",")).append("的记录因系统异常联机退款失败!");
			returnStr = returnStr + "\n" + "退款流水号为:" + exceptionBuff.toString();
		}

		return returnStr;
	}
	private long getFee(int merFlag, long transAmt, long refundAmt, long merFee){
		long refundFee = 0L;
		// 1可退 0 不能退手续费
		if(merFlag == 1){
			refundFee = (refundAmt/transAmt)*merFee; 
		} 
		
		return refundFee;
	}
	
	
	/*
	 * 联机退款订单同步
	 * 
	 */
	 public String OnlinerefundStateSynchro(List<RefundLog> refundlog){
		 String str=null;
		for (RefundLog refundLog2 : refundlog) {
			int state=refundDao.OnlinerefundStateSynchro(refundLog2.getId(),refundLog2.getTseq());
			if(state!=1){
				return str="状态已经更新！";
			}else{
					try {
						ArrayList<RefundLog> aList2 = new ArrayList<RefundLog>();
						aList2.add(refundLog2);
						RefundmentService refundmentservice = new RefundmentService();
						String result = refundmentservice.OnlineRefund(aList2);
						LogUtil.printInfoLog("RefundmentService", "OnlinerefundStateSynchro", result);
						return str = "订单已经同步！";
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}	
		return str;
		 
	   }
	@SuppressWarnings("deprecation")
	private void requestBgRetUrl(RefundLog bean, String refundState) throws Exception{
		String bgRetUrl = bean.getBgRetUrl();
		if(null==bgRetUrl || bgRetUrl.length()<8) return;
		int d = DateUtil.today();
		String mdate = bean.getOrg_mdate()+"";
		String amount = "0";
		String refund_amt = "0";
		
		String refundAmt = Ryt.div100(bean.getRef_amt());
		
		String pSigna = "";       // 签名字符串
		String pSigna2 = "";
//		JdbcTemplate jt = new JdbcConn().getJdbcTemplate();
		String tableName=bean.getSys_date()==d ? Constant.TLOG : Constant.HLOG;
		
		String sql = "select oid,mdate,amount,refund_amt from " + tableName +" where tseq = " + bean.getTseq();
		try {
			
			RefundDao dao = new RefundDao();
			Map<String,Object> m = dao.queryForMap(sql);
			if(m.size()==0){
				tableName=tableName.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
				sql="select oid,mdate,amount,refund_amt from " + tableName +" where tseq = " + bean.getTseq();
				m=dao.queryForMap(sql);
			}
			if(m.get("mdate")!=null) mdate = m.get("mdate").toString();
			if(m.get("amount")!=null) amount = Ryt.div100(m.get("amount").toString());
			if(m.get("refund_amt")!=null) refund_amt =  Ryt.div100(m.get("refund_amt").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer requestUrl = new StringBuffer();   //请求地址
		requestUrl.append(bgRetUrl);
		
//		merId+ordId + merDate + transAmt + refundAmt  +reffundDate+tseq;
		String chkValueText = bean.getMid() + bean.getOrg_oid() + mdate + amount +  refundAmt + d + bean.getTseq();
		String chkValueText2 = chkValueText + refundState ;
		String privateKey = RypCommon.getKey(CertUtil.getCertPath("privatekey"));
		try {
			pSigna = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText.getBytes(), privateKey));
			pSigna2 = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText2.getBytes(), privateKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestUrl.append("?merId=").append(bean.getMid());
		requestUrl.append("&ordId=").append(bean.getOrg_oid());
		requestUrl.append("&merDate=").append(mdate);
		requestUrl.append("&transType=R");
		requestUrl.append("&transAmt=").append(amount);
		requestUrl.append("&refundAmt=").append(refundAmt);
		requestUrl.append("&reffundCount=").append(refund_amt);
		requestUrl.append("&reffundDate=").append(d);
		requestUrl.append("&tseq=").append(bean.getTseq());
		requestUrl.append("&refundState=").append(refundState);
		requestUrl.append("&pSigna=").append(pSigna);
		requestUrl.append("&refundOid=").append(bean.getOid());
		requestUrl.append("&pSigna2=").append(pSigna2);
		requestUrl.append("&merPriv=").append(Ryt.empty(bean.getMerPriv()) ? "" : bean.getMerPriv().trim());
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestUrl", requestUrl.toString());
		try {
			String ret = RypCommon.httpRequest(requestUrl.toString());
			params.put("ret", ret);
			LogUtil.printInfoLog("RefundmentService", "requestBgRetUrl", "The refund notice merchant", params);
		} catch (Exception e) {
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("RefundmentService", "requestBgRetUrl", "The refund notice merchant failure", params);
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	/**
	 * 退款单详细
	 * @param rl
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List queryRefundAndHlog(int id, String tseq, int sys_date) {
		List l = null;
		try {
			l = refundDao.query4ref_amt_sum(id, tseq, sys_date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * 操作成功 1、修改退款表中改订单的状态为成功 ，保存审核时间； 
	 * 去掉 操作失败失败时 1、将hlog表中更改总申请退款金额 2、更新退款表更新审核时间，审核失败的理由，更新状态为操作失败；
	 * @param loginmid
	 * @param loginuid
	 * @param subtype 传过来的 操作的类型 success为审核成功 failure 为审核失败
	 * @param tseqs
	 * @return
	 */
	public String verifyRefund(List<RefundLog> refLogList) {

		int nowdate = DateUtil.today();
		StringBuffer ids = new StringBuffer("(");
		for (RefundLog item : refLogList) {
			ids.append(item.getId()).append(",");
		}
		ids.delete(ids.length() - 1, ids.length());
		ids.append(")");

		try {
			refundDao.verifyRefund(nowdate, ids.toString());
			refundDao.saveOperLog("审核操作成功", "审核操作成功,ids:"+ids.toString());
			
			// 退款审核信息同步给清算系统
			for (RefundLog item : refLogList)
				RefundSyncJob.addJob(item.getMid(), String.valueOf(item.getId()), RefundSyncJob.REFUND_TYPE_SH);
		} catch (Exception e) {
			LogUtil.printErrorLog("RefundmentService", "verifyRefund", "审核操作失败"+ids.toString()+"，失败原因："+e.getMessage());
			refundDao.saveOperLog("审核操作失败", "审核操作失败");
			return "审核操作失败";
		}
		return "审核操作成功";
	}

	// -----------------------------------------jsp商户管理------------------------------------------
	/**
	 * 商户撤销退款 1、更新退款表将需要撤销的订单的状态改为商户撤销的状态 2、更新Hlog表的总申请退款金额
	 * @param rList,页面显示的一页所有查询出来的List
	 * @param ids 被选中的所有对象的Id（1,2,3,4,5）的格式
	 * @return
	 */
	public String verifyFail(List<RefundLog> rList, String ids) {
		if (ids == null) {
			return "error";
		}
		String[] refundId = ids.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
		// 要处理的对象
		Map<String, RefundLog> selectRefundLog = new HashMap<String, RefundLog>();
		for (String id : refundId) {
			for (RefundLog rl : rList) {
				if (rl.getId() == Integer.parseInt(id)) {
					selectRefundLog.put(id, rl);
				}
			}
		}
		String returnStr = "退款撤销完成!";
		StringBuffer errorBuff = new StringBuffer();
		int nowdate = DateUtil.today();
		for (String rid : refundId) {
			RefundLog rl = selectRefundLog.get(rid);
			StringBuilder updateHlogSql = new StringBuilder();
			String t=rl.getSys_date() == DateUtil.today() ? Constant.TLOG : Constant.HLOG;
			try{
				refundDao.queryForInt("select fee_amt from "+t+" where tseq="+rl.getTseq());
			}catch (Exception e) {
				t=t.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
			}
			updateHlogSql.append("update ").append(t);
			updateHlogSql.append(" h set refund_amt = refund_amt-").append(rl.getRef_amt());
			updateHlogSql.append(" where h.tseq = " + rl.getTseq());
			// 更新退款表将需要撤销的订单的状态改为 失败的状态 商户撤销状态为7
			StringBuilder updateRefLog = new StringBuilder();
			updateRefLog.append("update refund_log r set r.req_date=").append(nowdate);
			updateRefLog.append(", r.stat=7  where r.id = ").append(rid).append(" and r.stat=5");

			List<String> batchSqlList = new ArrayList<String>();// 批处理
			batchSqlList.add(updateHlogSql.toString());
			batchSqlList.add(updateRefLog.toString());
			try {
				// 批处理
				refundDao.batchSqlTransaction2(batchSqlList);
			} catch (Exception e) {
				errorBuff.append(rid + ",");
			}
			
			try {
				// 批处理
//				requestBgRetUrl(bean, refundState)
			} catch (Exception e) {
				errorBuff.append(rid + ",");
			}
		}
		// 记录日志
		refundDao.saveOperLog("退款撤销", "退款撤销完成,ids:" + ids);
		if (errorBuff.length() != 0) {
			errorBuff.deleteCharAt(errorBuff.lastIndexOf(",")).append("的记录退款撤销失败!");
			returnStr = returnStr + "\n" + "退款流水号为" + errorBuff.toString();
		}
		return returnStr;
	}

	/**
	 * 确认退款 将该退款单的状态改为商户已提交
	 * @param refundIds ,页面选中的药进行操作的ids，(1,2,3)
	 * @return
	 */
	public String verifySure(String refundIds) {
		if (refundIds == null) {
			return "操作失败!";
		}
		String returnStr = "确认退款完成";
		int nowdate = DateUtil.today();
		try {
			// 更新退款表将需要退款的订单的的状态改为已提交的 状态为1
			refundDao.updateRefund2Verify(nowdate, refundIds);
		} catch (Exception e) {
			returnStr = "退款确认操作失败";
			
			LogUtil.printErrorLog("RefundmentService", "verifySure", "退款确认操作失败["+refundIds+"]，失败原因："+e.getMessage());
			
//			e.printStackTrace();
		}
		refundDao.saveOperLog("确认退款", returnStr + refundIds);
		return returnStr;
	}

	/**
	 * 退款申请页面查询 单个交易对象
	 * @param merOid 商户订单号
	 * @param mid 商户号
	 * @param merDate 商户日期
	 * @return RefundOB 退款页面显示的对象 RefundOB
	 */
	public RefundOB getRefundOBByMer(String merOid, String mid, int merDate, String sessionMid) {
		if (merOid == "" ||Ryt.empty(mid)) return null;
		RefundOB refundOB = refundDao.queryRefundOBByMer(merOid, mid, merDate, sessionMid);
		canRefunded(refundOB);
		return refundOB;
	}

	/**
	 * 根据不同情况判断该笔订单能否退款
	 * @param refundOB
	 * @return
	 */
	private void canRefunded(RefundOB refundOB) {
		if (refundOB == null) return;
		int date = DateUtil.today();
		//int[] flagArray = new int[] { DateUtil.getLastMonthDate(date), DateUtil.getTwoYearsBefore(date) };
		// 是否当天交易
		refundOB.setTodayTrans(refundOB.getSysDate() == date);
		boolean canRefund = false;
		String notRefuntReason = "可退款";
		refundOB.setCode(1);
		if (refundOB.getMidRefundFlag() == Constant.NOT_ALLOW_REFUND_FLAG) {
			// 商户的退款标示不允许退款
			notRefuntReason = "该商户不允许退款";
		} else if (refundOB.getTstat() != Constant.TRANS_SUCCES) {
			// 不是成功订单不允许退款
			notRefuntReason = "订单并未支付成功，不可退款";

		} else if (refundOB.getAmount() - refundOB.getRefundAmt() <= 0) {
			// 可退余额不足不允许退款
			notRefuntReason = "可退余额不足";

		}  else if (refundOB.getAuthorType() == Constant.UMP_AUTH_TYPE) {
			// 19pay的订单不允许退款
			notRefuntReason = "该订单不可退款";

		} else {
			canRefund = true;
			refundOB.setCode(0);
		}
		refundOB.setCanRefund(canRefund);
		refundOB.setNotRefuntReason(notRefuntReason);
	}

	/**
	 * 退款申请页面查询 单个交易对象
	 * @param tseq 流水号
	 * @return RefundOB 退款页面显示的对象 RefundOB
	 */
	public RefundOB getRefundOBByTseq(String tseq) {
		RefundOB refundOB = refundDao.queryRefundOBByTseq(tseq,refundDao.getLoginUser().getMid());
		canRefunded(refundOB);
		return refundOB;
	}

	/**
	 * 退款申请页面查询 单个交易对象用于批量退款
	 * @param tseq 流水号
	 * @return RefundOB 退款页面显示的对象 RefundOB
	 */
	public RefundOB getRefundOBByTseq2(String tseq, String sessionMid) {
		RefundOB refundOB = refundDao.queryRefundOBByTseq(tseq, sessionMid);
		canRefunded(refundOB);
		return refundOB;
	}

	/**
	 * 退款申请页面 确定退款
	 * @param hlog
	 * @param refundMoeny
	 * @param loginuid
	 * @param refundReason
	 * @return
	 */
	public String applyRefund(RefundOB refundOB) {
		if (refundOB == null) {
			return "退款申请失败！";
		}
		if (refundOB.getTstat() != Constant.TRANS_SUCCES) {
			refundOB.setCode(1);
			return "退款申请失败，请确认该订单是否为支付成功交易！";
		}
		if (refundOB.getAmount() < Ryt.mul100toInt(refundOB.getApplyRrefundAmount())) {
			refundOB.setCode(2);
			return "退款金额不可以大于支付金额！";
		}
		if (refundOB.getAuthorType() == Constant.UMP_AUTH_TYPE) {
			refundOB.setCode(1);
			return "该订单不允许退款！";
		}

		long refundMoney = Ryt.mul100toInt(refundOB.getApplyRrefundAmount());
		
		/*多加一次查询已经退款的金额*/
		String t = refundOB.getSysDate() == DateUtil.today() ? Constant.TLOG : Constant.HLOG;
		long refundAmt = 0;
		try {
			refundAmt = refundDao.queryForLong("select refund_amt from " + t + " where tseq = " + refundOB.getTseq());
		} catch (Exception e) {
			t=t.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
			refundAmt = refundDao.queryForLong("select refund_amt from " + t + " where tseq = " + refundOB.getTseq());
		}
		refundOB.setRefundAmt(refundAmt);
		
		long canRefund = refundOB.getAmount() - refundOB.getRefundAmt();
		if (refundMoney <= 0) {
			refundOB.setCode(2);
			return "申请退款金额必须大于0!";
		}
		if (canRefund < refundMoney) {
			refundOB.setCode(2);
			return "可退款余额不足";
		}
		String retStr = "退款申请成功！";
		String refundOid = "";
		try {
			refundOid = refundDao.refundApply(refundOB, refundMoney);
			refundOB.setCode(0);
			retStr = "退款申请成功";
		} catch (Exception e) {
			refundOB.setCode(3);
			e.printStackTrace();
			retStr = "退款申请操作失败";
		}
		try {
			refundDao.saveOperLog("申请退款", retStr + "," + refundOid);
		} catch (Exception e) {
		}
		return retStr;
	}

	/**
	 * 退款查询
     * @return CurrentPage<RefundLog>
	 */	 
	public CurrentPage<RefundLog> queryRefundLogs(Integer pageNo, String mid, String stat, String tseq,Integer dateState,
								Integer bdate,Integer edate, Integer gate, String orgid, Integer vstate,Integer authorType,Integer mstate,Integer refundType) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
	   return refundDao.queryRefundLogs(pageNo,pageSize, mid, stat, tseq, 
							dateState, bdate, edate, gate, orgid, vstate, authorType,mstate,refundType);
		   
	}
	
	/**
	 * 退款经办查询
     * @return CurrentPage<RefundLog>
	 */	 
	public CurrentPage<RefundLog> queryRefundJBLogs(Integer pageNo, String mid, String stat, String tseq,Integer dateState,
								Integer bdate,Integer edate, Integer gate, String orgid, Integer vstate,Integer authorType,Integer mstate) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
	   return refundDao.queryRefundJBLogs(pageNo,pageSize, mid, stat, tseq, 
							dateState, bdate, edate, gate, orgid, vstate, authorType,mstate);
		   
	}
	
	
	/*
	 * 联机退款查询
	 */
	public CurrentPage<RefundLog> queryLJRefundJBLogs(Integer pageNo,String mid, String stat,Integer bdate, Integer edate, Integer mstate) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
		return refundDao.queryLJRefundJBLogs(pageNo, pageSize, mid, stat,bdate, edate,mstate);

	}
	/**
	 * 根据Id查询RefundLog
	 * @param id
	 * @return RefundLog
	 */
	public RefundLog queryRefundLogById(Integer id) {
		return refundDao.queryRefundLogById(id);
	}
	/**
	 * 联机退款下载
	 * @throws Exception 
	 */
	public FileTransfer downOnlineRefundMotions(String mid) throws Exception {
		try {
			

		ArrayList<String[]> list = new ArrayList<String[]>();
		// 退款运行经办页面下载
		String[] title2 = { "序号","退款流水号", "原电银流水号","商户号","商户订单号","原商户订单号",  "原银行订单号",
				"原支付渠道", "原交易日期", "原订单金额(元)", "原交易银行","原实际交易金额(元)", "退款金额(元)","退还商户手续费(元)","优惠金额(元)","差额(元)", "退款日期",
				"授权码" ,"退款人签字栏"};
		list.add(title2);
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			List<RefundLog> refundLogList=refundDao.queryTodayOnlineRefundList(mid);
			long refAmount = 0;
			long refMerFee=0;
			for (int j = 0; j < refundLogList.size(); j++) {
				RefundLog refundLog=refundLogList.get(j);
				String[] strArr={
						String.valueOf(j+1),
						String.valueOf(refundLog.getId()),
						String.valueOf(refundLog.getTseq()),
						String.valueOf(refundLog.getMid()),
						refundLog.getOid(),
						refundLog.getOrg_oid(),
						refundLog.getOrgBkSeq(),
						gateRouteMap.get(refundLog.getGid()),
						changeToString(refundLog.getOrg_mdate()),
						Ryt.div100(refundLog.getOrgAmt()),
						gates.get(refundLog.getGate()),
						Ryt.div100(refundLog.getOrgPayAmt()),
						Ryt.div100(refundLog.getRef_amt()),
						Ryt.div100(refundLog.getMerFee()),
						Ryt.div100(refundLog.getPre_amt1()),
						Ryt.div100(refundLog.getPreAmt()),
						changeToString(refundLog.getPro_date()),
						refundLog.getAuthNo(),
						""
				};
				list.add(strArr);
				refAmount+=refundLog.getRef_amt();
				refMerFee+=refundLog.getMerFee();
			}
			// 退款运行经办添加统计
			String[] nullstr = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ,"", "", ""};
			String[] str = { "总计:", refundLogList.size() + "条记录", "", "", "",  "", "", "","", "",  "",Ryt.div100(refAmount),Ryt.div100(refMerFee), "", "", "", "", "", "" };
			String[] str1 = { "", "制表人:",refundDao.getLoginUserName(), "制表日期:", String.valueOf(DateUtil.today()), "", "复核人:", "", "日期:", "", "","","", "" , "", "", "", "", "" };
			String[] str2 = { "", "退款录入:", "", "日期:", "", "", "退款复核:", "", "日期:", "", "","","", "", "" , "", "", "", "" };
			String[] str3 = { "", "", "", "", "", "", "结算主管:", "", "日期:", "","", "","", "", "" , "", "", "", "" };
			list.add(nullstr);
			list.add(str);
			list.add(str1);
			list.add(str2);
			list.add(str3);
			String filename = "REFUNDLOG_" + DateUtil.getIntDateTime() + ".xlsx";
			return new DownloadFile().downloadXLSXFileBase(list, filename, "退款报表");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 为退款经办下载表
	 * @throws Exception 
	 */
	public FileTransfer downloadRefundMotions(String mid) throws Exception {
		try {
			

		ArrayList<String[]> list = new ArrayList<String[]>();
		// 退款运行经办页面下载
		String[] title2 = { "序号","退款流水号", "原电银流水号","商户号","商户订单号","原商户订单号",  "原银行订单号",
				"原支付渠道", "原交易日期", "原订单金额(元)", "原交易银行","原实际交易金额(元)", "退款金额(元)","退还商户手续费(元)","优惠金额(元)","差额(元)", "经办日期",
				"授权码" ,"退款人签字栏"};
		list.add(title2);
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			List<RefundLog> refundLogList=refundDao.queryTodayRefundList(mid);
			long refAmount = 0;
			long refMerFee=0;
			for (int j = 0; j < refundLogList.size(); j++) {
				RefundLog refundLog=refundLogList.get(j);
				String[] strArr={
						String.valueOf(j+1),
						String.valueOf(refundLog.getId()),
						String.valueOf(refundLog.getTseq()),
						String.valueOf(refundLog.getMid()),
						refundLog.getOid(),
						refundLog.getOrg_oid(),
						refundLog.getOrgBkSeq(),
						gateRouteMap.get(refundLog.getGid()),
						changeToString(refundLog.getOrg_mdate()),
						Ryt.div100(refundLog.getOrgAmt()),
						gates.get(refundLog.getGate()),
						Ryt.div100(refundLog.getOrgPayAmt()),
						Ryt.div100(refundLog.getRef_amt()),
						Ryt.div100(refundLog.getMerFee()),
						Ryt.div100(refundLog.getPre_amt1()),
						Ryt.div100(refundLog.getPreAmt()),
						changeToString(refundLog.getPro_date()),
						refundLog.getAuthNo(),
						""
				};
				list.add(strArr);
				refAmount+=refundLog.getRef_amt();
				refMerFee+=refundLog.getMerFee();
			}
			// 退款运行经办添加统计
			String[] nullstr = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" ,"", "", ""};
			String[] str = { "总计:", refundLogList.size() + "条记录", "", "", "",  "", "", "","", "",  "",Ryt.div100(refAmount),Ryt.div100(refMerFee), "", "", "", "", "", "" };
			String[] str1 = { "", "制表人:",refundDao.getLoginUserName(), "制表日期:", String.valueOf(DateUtil.today()), "", "复核人:", "", "日期:", "", "","","", "" , "", "", "", "", "" };
			String[] str2 = { "", "退款录入:", "", "日期:", "", "", "退款复核:", "", "日期:", "", "","","", "", "" , "", "", "", "" };
			String[] str3 = { "", "", "", "", "", "", "结算主管:", "", "日期:", "","", "","", "", "" , "", "", "", "" };
			list.add(nullstr);
			list.add(str);
			list.add(str1);
			list.add(str2);
			list.add(str3);
			String filename = "REFUNDLOG_" + DateUtil.getIntDateTime() + ".xlsx";
			return new DownloadFile().downloadXLSXFileBase(list, filename, "退款报表");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获得下载的List<RefundLog>
	 * @return
	 */
	private List<RefundLog> getRefundList(String mid, String stat, String tseq,Integer dateState, Integer bdate,
			Integer edate, Integer gate, String orgid, Integer vstate,Integer gateRouteId,Integer mstate,Integer refundType){
		CurrentPage<RefundLog> refundLogPage=refundDao.queryRefundLogs(1,-1, mid, stat, tseq, 
				dateState, bdate, edate, gate, orgid, vstate, gateRouteId,mstate,refundType);
		return refundLogPage.getPageItems();
		
	}
	/**
	 * 退款报表下载（查询）
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downloadReturn(String mid, String stat, String tseq,Integer dateState,
			Integer bdate,Integer edate, Integer gate, String orgid, Integer vstate,Integer authorType,Integer mstate,Integer refundType) throws Exception{
		String sessionMid =settlementDao.getLoginUser().getMid();
		List<RefundLog> resultlist=getRefundList(mid, stat, tseq, dateState, bdate, edate, gate, orgid, vstate,authorType ,mstate,refundType);
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> mstat = AppParam.h_mer_refund_tstat;
		String[] title = { "序号","退款流水号","原电银流水号","商户号","原商户订单号","原银行流水号","原交易日期",
				"原交易金额（元）","原交易银行","退款金额（元）","卡号","退还商户手续费(元)","经办日期","退款确认日期","退款状态 ", !"1".equals(sessionMid) ? "" :"原订单优惠金额(元)","授权码" };
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(title);
		long refAmount = 0;
		long refMerFee=0;
		for (int j = 0; j < resultlist.size(); j++) {
			RefundLog refundLog=resultlist.get(j);
			String[] strArr={
					String.valueOf(j+1),
					String.valueOf(refundLog.getId()),
					String.valueOf(refundLog.getTseq()),
					String.valueOf(refundLog.getMid()),
					refundLog.getOrg_oid(),
					refundLog.getOrgBkSeq(),
					String.valueOf(refundLog.getOrg_mdate()),
					Ryt.div100(refundLog.getOrgAmt()),
					gates.get(refundLog.getGate()),
					Ryt.div100(refundLog.getRef_amt()==0||"".equals(refundLog.getRef_amt())?0:refundLog.getRef_amt()),
					Ryt.empty(refundLog.getCard_no()) ? "":refundLog.getCard_no(),
					Ryt.div100(refundLog.getMerFee()),
					changeToString(refundLog.getPro_date()),
					changeToString(refundLog.getReq_date()),
					mstat.get(refundLog.getStat()),
					!"1".equals(sessionMid) ? "" : Ryt.div100(refundLog.getPre_amt1()),
				   Ryt.empty(refundLog.getAuthNo()) ? "":refundLog.getAuthNo(),
			
			};
			refAmount+=refundLog.getRef_amt()==0||"".equals(refundLog.getRef_amt())?0:refundLog.getRef_amt();
			refMerFee+=refundLog.getMerFee();
			//preAmt+=refundLog.getPreAmt();
			list.add(strArr);
		}
		String[] nullstr = { "","", "", "", "", "", "","","", "","", "", "", "", "", "", "" };
		list.add(nullstr);
		String[] str = { "总计:" + resultlist.size() + "条记录","","","","","","","", "",
				Ryt.div100(refAmount),Ryt.div100(refMerFee),"","", "","", "", "" };
		list.add(str);
		String filename = "MERREFUNDQUERY_" + DateUtil.getIntDateTime() + ".xlsx";
		String name = "退款报表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}
  /**
   * 退款审核的下载
 * @throws Exception 
 * @throws Exception 
   */
   public FileTransfer downloadRefundVerify( String mid, String stat, String tseq,Integer dateState, Integer bdate,
			Integer edate, Integer gate, String orgid, Integer vstate,Integer gateRouteId,Integer mstate) throws Exception{

	   Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> gateRouteMap=RYFMapUtil.getGateRouteMap();
	   List<RefundLog> resultlist=getRefundList(mid, stat, tseq, dateState, bdate, edate, gate, orgid, vstate, gateRouteId,mstate,null);
		final String[] title={"序号","退款流水号","原系统流水号","商户号","原商户订单号","原银行流水号","原支付渠道",
				"原交易日期","原交易金额（元）","原交易银行","退款金额（元）","退还商户手续费(元)","优惠金额(元)","审核日期"};
		ArrayList<String[]> strList = new ArrayList<String[]>();
		strList.add(title);
		long refAmount = 0;
		long refMerFee=0;
		for (int j = 0; j < resultlist.size(); j++) {
			RefundLog refundLog=resultlist.get(j);
			String[] strArr={
					String.valueOf(j+1),
					String.valueOf(refundLog.getId()),
					String.valueOf(refundLog.getTseq()),
					String.valueOf(refundLog.getMid()),
					refundLog.getOrg_oid(),
					refundLog.getOrgBkSeq(),
					gateRouteMap.get(refundLog.getGid()),
					changeToString(refundLog.getOrg_mdate()),
					Ryt.div100(refundLog.getOrgAmt()),
					gates.get(refundLog.getGate()),
					Ryt.div100(refundLog.getRef_amt()),
					Ryt.div100(refundLog.getMerFee()),
					Ryt.div100(refundLog.getPreAmt()),
					changeToString(refundLog.getRef_date())
					//mstat.get(refundLog.getStat())
			};
			refAmount+=refundLog.getRef_amt();
			refMerFee+=refundLog.getMerFee();
			strList.add(strArr);
		}
		String[] str = { "总计:" + resultlist.size() + "条记录", "","", "", "", "", "","",
						"", "",
						Ryt.div100(refAmount),
						Ryt.div100(refMerFee),"","" };
		strList.add(str);
		String filename = "RefundVerify"+ DateUtil.getIntDateTime()+ ".xlsx";
		String name = "退款报表";
		return new DownloadFile().downloadXLSXFileBase(strList, filename, name);
   }
   /**
    * 把null和0转换成空格
    * @param obj
    * @return
    */
   private String changeToString(Object obj){
	   if(obj==null||obj.toString().equals("0")){
		   return "";
	   }else{
		   return obj.toString();
	   }
   }

}
