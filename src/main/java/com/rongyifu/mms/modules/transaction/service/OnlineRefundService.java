package com.rongyifu.mms.modules.transaction.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rytong.encrypto.provider.RsaEncrypto;

import org.directwebremoting.io.FileTransfer;

import cert.CertUtil;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RefundDao;
import com.rongyifu.mms.modules.transaction.dao.OnlineRefundDao;
import com.rongyifu.mms.quartz.jobs.RefundSyncJob;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.bean.OnlineRefund;

public class OnlineRefundService {
	private OnlineRefundDao onlinerefund =new OnlineRefundDao();
	/*
	 * 联机退款查询
	 */
	public CurrentPage<RefundLog> queryLJRefundJBLogs(Integer pageNo,String mid, String stat,Integer bdate, Integer edate, Integer mstate,String tseq,String gid) {
//		int pageSize = ParamCache.getIntParamByName("pageSize");
		int pageSize = 50; //修改单页显示条数为50
		return onlinerefund.queryLJRefundJBLogs(pageNo, pageSize, mid, stat,bdate, edate,mstate,tseq,gid);

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
			List<RefundLog> refundLogList=onlinerefund.queryTodayOnlineRefundList(mid);
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
			String[] str1 = { "", "制表人:",onlinerefund.getLoginUserName(), "制表日期:", String.valueOf(DateUtil.today()), "", "复核人:", "", "日期:", "", "","","", "" , "", "", "", "", "" };
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
		for (RefundLog refLog : refLogList) {
			int refId = refLog.getId();
			String tableName = refLog.getSys_date() == nowdate ? Constant.TLOG : Constant.HLOG ;
			int refundAmt=0;
			try {
				 refundAmt = onlinerefund.queryRefundAmt(tableName, refLog.getTseq());
				
			} catch (Exception e) {
				tableName=tableName.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
				 refundAmt = onlinerefund.queryRefundAmt(tableName, refLog.getTseq());
			}
			
			try {
				onlinerefund.refundCancel(refLog, tableName, refundAmt);
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
		onlinerefund.saveOperLog("经办撤销", "经办撤销完成");
		if (errorBuff.length() != 0) {
			errorBuff.deleteCharAt(errorBuff.lastIndexOf(",")).append("的记录经办撤销失败!");
			returnStr = returnStr + "\n" + "退款流水号为" + errorBuff.toString();
		}
		return returnStr;
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
		Map<String, Integer> tseqMap = onlinerefund.queryRefundAmtIsExceedTransAmt(tseqList);
		
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
			int isRefundFee=onlinerefund.getRefundFee(theMid);
			long balance = onlinerefund.getBalance(theMid);
			
			long ref=getFee(isRefundFee,OrgAmt,refbalance,refMerFee);
			
			// 可以退款的订单,进行退款数据库操作
			if (balance >= (refbalance-ref)) {
				int refFee = onlinerefund.getRefundFee(theMid);
				ArrayList<String> sqlsList = new ArrayList<String>();
				for (RefundLog bean : theList) {
					try {
						// 校验退款金额是否超过交易金额
						if(!checkRefundAmt(tseqMap, bean.getTseq())){
							checkRefundAmtException.append(bean.getTseq() + ",");
							continue;
						}
						
						// 联机退款人工经办：重置联机退款字段
						bean.setRefundType(2); // 设置退款类型：联机退款-人工经办
						onlinerefund.manualHandlingRefund(bean, refFee, sqlsList);
					} catch (Exception e) {
						exceptionBuff.append(bean.getId() + ",");
						isException = true;
						
						LogUtil.printErrorLog("OnlineRefundmentService", "refundException", "refundId=" + bean.getId() + " msg=" + e.getMessage());
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

		onlinerefund.saveOperLog( "运行经办", "运行经办完成");
		
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
			LogUtil.printInfoLog("OnlineRefundmentService", "verifyAccount", returnStr + "\n电银流水号：" + tseqList);

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
	
	
	/**
	 * 联机退款订单同步
	 */
	public String OnlinerefundStateSynchro(List<RefundLog> refundlog) {
		String str = null;
		for (RefundLog refundLog2 : refundlog) {
			int state = onlinerefund.OnlinerefundStateSynchro(refundLog2.getId(), refundLog2.getTseq());
			if (state != 1) {
				return str = "状态已经更新！";
			} else {
				try {
					ArrayList<RefundLog> aList2 = new ArrayList<RefundLog>();
					aList2.add(refundLog2);
					String result = OnlineRefund(aList2);
					LogUtil.printInfoLog("OnlineRefundmentService", "OnlinerefundStateSynchro", result);
					return str = "订单已经同步！";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return str;

	}

    public final static Object lock = new Object();

	/**
	 *联机退款 判断一个商户的多条订单的总退款金额 是否大于
	 * 该商户在minfo表中的余额 
	 * 向refund_log 联机退款的流水号;
	 * 接入银行退款接口
	 * 更新minfo表中的余额
	 * @param refLogList 传过来的参数 key 为 退款单号 value 为退款单对象
	 */
	public String OnlineRefund (List<RefundLog> refLogList) {
        String returnStr = "联机退款完成！";
        synchronized(lock){
            StringBuffer noMotionHandle = new StringBuffer();// 因余额不足不能完成联机退款的商户
            StringBuffer exceptionBuff = new StringBuffer(); // 因系统异常不能完成联机退款的商户
            StringBuffer checkRefundAmtException = new StringBuffer();//记录校验退款金额失败的信息
            StringBuffer noticeException = new StringBuffer();//记录通知商户产生异常的信息
            String tseqList = "";// 电银流水号列表,多个流水号以逗号分隔

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
            Map<String, Integer> tseqMap = onlinerefund.queryRefundAmtIsExceedTransAmt(tseqList);

            // 判断余额
            for (String theMid : midRefundLogListMap.keySet()) {
                boolean isException = false;
                OnlineRefundBean onlinerefundbean = null;
                List<RefundLog> theList = midRefundLogListMap.get(theMid);
                long refbalance = 0;
                long refMerFee=0;
                long OrgAmt=0;
                for (RefundLog r : theList) {
                    refbalance += r.getRef_amt();
                    refMerFee +=r.getMerFee();
                    OrgAmt+=r.getOrgAmt();
                }
                int isRefundFee=onlinerefund.getRefundFee(theMid);
                long balance = onlinerefund.getBalance(theMid);

                long ref=getFee(isRefundFee,OrgAmt,refbalance,refMerFee);

                // 可以退款的订单,进行退款数据库操作
                if (balance >= (refbalance-ref)) {
                    int refFee = onlinerefund.getRefundFee(theMid);//onlinerefund类在
                    ArrayList<String> sqlsList = new ArrayList<String>();
                    for (RefundLog bean : theList) {
                        try {
                            // 校验退款金额是否超过交易金额
                            if(!checkRefundAmt(tseqMap, bean.getTseq())){
                                checkRefundAmtException.append(bean.getTseq() + ",");
                                continue;
                            }

                            onlinerefundbean=onlinerefund.OnlinerefundHandle(bean);// 接入银行退款接口

                            String status=onlinerefundbean.getOrderStatus();
                            String bathNo=onlinerefundbean.getRefBatch();
                            String errorMsg=onlinerefundbean.getRefundFailureReason();
                            errorMsg = Ryt.empty(errorMsg) ? "" : errorMsg;
                            int id = onlinerefundbean.getId();
                            String sql = null;
                            if((RefundUtil.ORDER_STATUS_ACCEPT).equals(status)){
                                sql = "update refund_log set online_refund_state =" +RefundUtil.ORDER_STATUS_PROCESSED+ ", refund_type=1, online_refund_id = '" +bathNo + "' where id=" + id;
                                //追加update 条件：and (online_refund_state<> suc and online_refund_state <> fail)
                                sql+=" and online_refund_state <> "+RefundUtil.ORDER_STATUS_SUCCESS +" and online_refund_state <> "+RefundUtil.ORDER_STATUS_FAILURE+";";
                                returnStr+="\n退款流水号为：'"+bean.getId()+"'联机退款处理中！";
                            }else if((RefundUtil.ORDER_STATUS_FAILURE).equals(status)){
                                sql="update refund_log set online_refund_state = " + status+ ", refund_type=1 ,online_refund_reason=" + Ryt.addQuotes(errorMsg) + ", online_refund_id = '" +bathNo + "' where id=" + id;
                                returnStr+="\n退款流水号为：'"+bean.getId()+"'联机退款失败,失败原因:" + errorMsg;

                            }else if((RefundUtil.ORDER_STATUS_PROCESSED).equals(status)){
                                sql = "update refund_log set online_refund_state =" +status+ ", refund_type=1, online_refund_id = '" +bathNo + "' where id=" + id;
                                //追加update 条件：and (online_refund_state<> suc and online_refund_state <> fail)
                                sql+=" and online_refund_state <> "+RefundUtil.ORDER_STATUS_SUCCESS +" and online_refund_state <> "+RefundUtil.ORDER_STATUS_FAILURE+";";
                                returnStr+="\n退款流水号为：'"+bean.getId()+"'银行处理中！";

                            }else if((RefundUtil.QUERT_BANK_FAILURE).equals(status)){
                                 sql = "update refund_log set online_refund_state = " + status+ ", refund_type=1 , online_refund_reason="+ Ryt.addQuotes(errorMsg) + ", online_refund_id = '" +bathNo + "' where id=" + id;
                                returnStr+="\n退款流水号为：'"+bean.getId()+"'请求银行失败,失败原因:"+errorMsg;

                            }else if((RefundUtil.ORDER_STATUS_SUCCESS).equals(status)){
                                bean.setOnlineRefundId(onlinerefundbean.getRefBatch());
                                bean.setOnlineRefundState(2);
                                bean.setRefundType(1);
                                onlinerefund.onlinerefundHandle(bean, refFee,sqlsList);// 完成退款经办的数据库操作
                            }

                            if(sql!=null){
                                onlinerefund.update(sql);
                            }

                        } catch (Exception e) {
                            exceptionBuff.append(bean.getId() + ",");
                            isException = true;

                            LogUtil.printErrorLog("OnlineRefundmentService", "refundException", "refundId=" + bean.getId() + " msg=" + e.getMessage());
                            e.printStackTrace();	//通常e.printStackTrace()的日志打印在catalind.out日志文件里面
                        }
                        try {
                            if(!isException&&(RefundUtil.ORDER_STATUS_SUCCESS).equals(onlinerefundbean.getOrderStatus())){
                            	// 联机退款成功信息同步给清算系统
                            	RefundSyncJob.addJob(bean.getMid(), String.valueOf(bean.getId()), RefundSyncJob.REFUND_TYPE_LJ);
    							
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

            onlinerefund.saveOperLog( "联机退款", "联机退款完成");
            if (noMotionHandle.length() != 0) {
                noMotionHandle.deleteCharAt(noMotionHandle.lastIndexOf(","));
                returnStr += "\n商户号[" + noMotionHandle.toString() + "]：商户账户余额不足，不能联机退款！";
            }
            if (exceptionBuff.length() != 0) {
                exceptionBuff.deleteCharAt(exceptionBuff.lastIndexOf(","));
                returnStr += "\n退款流水号[" + exceptionBuff.toString() + "]：系统异常联机退款失败！";
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
                LogUtil.printInfoLog("OnlineRefundmentService", "OnlineRefund", returnStr + "\n电银流水号：" + tseqList);
        }

		return returnStr;
	}

	/**
	 * 校验退款总额是否超过交易金额 true：未超过，可以退款 false：超过，不能退款
	 * 
	 * @param tseqMap
	 * @param tseq
	 * @return
	 */
	private boolean checkRefundAmt(Map<String, Integer> tseqMap, String tseq) {
		if (tseqMap.containsKey(tseq)) {
			Integer refFlag = tseqMap.get(tseq);
			return refFlag.intValue() == 0;
		} else
			return true;
	}
	
		 /**
			 *联机银联退货  判断一个商户的多条订单的总退款金额 是否大于
			 * 该商户在minfo表中的余额 
			 * 向refund_log 联机银联退款的流水号;
			 * 接入银行退款接口
			 * 更新minfo表中的余额
			 * @param refLogList 传过来的参数 key 为 退款单号 value 为退款单对象
		 */
	public String unionpay_onlineRefund(List<RefundLog> refLogList) {
				String returnStr =null;
				StringBuffer noMotionHandle = new StringBuffer();// 因余额不足不能完成银联联机退款的商户
				StringBuffer exceptionBuff = new StringBuffer(); // 因系统异常不能银联完成联机退款的商户

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
					int isRefundFee=onlinerefund.getRefundFee(theMid);
					long balance = onlinerefund.getBalance(theMid);
					
					long ref=getFee(isRefundFee,OrgAmt,refbalance,refMerFee);
					
					// 可以退款的订单,进行退款数据库操作
					if (balance >= (refbalance-ref)) {
						//退款是否退手续费 0:不退 1：退
						int refFee = onlinerefund.getRefundFee(theMid);
						ArrayList<String> sqlsList = new ArrayList<String>();
						for (RefundLog bean : theList) {
							try {

								onlinerefundbean=onlinerefund.OnlinerefundHandle(bean);// 接入银行退款接口
								
								String status=onlinerefundbean.getOrderStatus();
								String Bath=onlinerefundbean.getRefBatch();
								String errorMsg=onlinerefundbean.getRefundFailureReason();
								int id = onlinerefundbean.getId();
                                String sql = null;
								
								if((RefundUtil.ORDER_STATUS_ACCEPT).equals(status)){
									 sql = "update refund_log set online_refund_state =" +RefundUtil.ORDER_STATUS_PROCESSED+ ", refund_type=1 where id=" + id+ "  and online_refund_id= '" +Bath + "'";
									returnStr="退款流水号为：'"+bean.getId()+"'退款申请被接受！";							
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
									onlinerefund.refundHandle(bean, refFee, sqlsList);// 完成退款经办的数据库操作									
									returnStr="退款成功！";
								}
								
								if(sql!=null){
									onlinerefund.update(sql);
								}
								
							} catch (Exception e) {
								exceptionBuff.append(bean.getId() + ",");
								returnStr = "联机银联退货异常!";
								e.printStackTrace();				
								isException = true;
							}
							try {
								
								//通知商户后台
								if(!isException&&(RefundUtil.ORDER_STATUS_SUCCESS).equals(onlinerefundbean.getOrderStatus()))
									requestBgRetUrl(bean,"S");
							} catch (Exception e) {
								//e.printStackTrace();
								returnStr+="，流水号为["+bean.getTseq()+"]的订单通知商户后台失败！";
							}
						}
					} else {// 余额不足不可以退款的订单
						noMotionHandle.append(theMid + ",");
					}
				}

				onlinerefund.saveOperLog( "银联联机退款", "银联联机退款完成");
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
	/**
	 * 联机退款结果查询
	 * @param pageNo 页码
	 * @param param 参数
	 * @return
	 */
	public CurrentPage<OnlineRefund> queryOnlineRefunds(Integer pageNo, Map<String, String> param) {

		return onlinerefund.queryOnlineRefunds(pageNo, AppParam.getPageSize(), param);


	}
	
	/**
	 * 联机退款详情
	 * @param id  主键
	 * @return
	 */
	public OnlineRefund getOnlineRefund(long id){
		
		return onlinerefund.getOnlineRefund(id);
		
	}
	
	public FileTransfer downloadDetail(Map<String, String> param) throws Exception {
		  CurrentPage<OnlineRefund> onlineRefundPage=onlinerefund.queryOnlineRefunds(1,-1, param );
		  	List<OnlineRefund> onlineRefundList=onlineRefundPage.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			//Map<Integer, String> authorType = AppParam.authorType;
			long totleRefundAmt = 0;
			//String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
			//String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
			list.add("序号,退款流水号,原电银流水号,商户号,商户简称,原商户订单号,原支付渠道,原支付银行,原订单金额(元),退款金额(元),联机退款发起日期,退款状态"
				.split(","));
			int i = 0;
			String gateRoute = "";
			String rtype = "";
			long totlePayAmt = 0;
			for (OnlineRefund  r: onlineRefundList) {
				if (r.getGid() != null&& !String.valueOf(r.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(r.getGid());
				}else{
					gateRoute = "";
				}
				if(r.getRefStatus()==1){
					rtype="待处理";
				}
				else if(r.getRefStatus()==2){
					rtype="退款成功";
				}
				else if(r.getRefStatus()==3){
					rtype="退款失败";
				}
				else{
					rtype="";
				}
				String[] str = {
						(i + 1) + "",
						//r.getRefBkSeq() + "",
						r.getId() + "",
						r.getTseq() + "",
						r.getMid(),
						r.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(r.getMid()),
						r.getOrgOid() + "",
						gateRoute + "",
						gates.get(r.getGate()) + "",
						Ryt.div100(r.getOrgAmt()),
						Ryt.div100(r.getRefAmt()),
						r.getReqBkDate() + "",
						rtype};
				totleRefundAmt += r.getRefAmt();
				totlePayAmt += r.getOrgAmt();
				i += 1;
				list.add(str);
			}
			String[] str = { "总计:" + i + "条记录", "", "", "", "","" ,"","", Ryt.div100(totlePayAmt),Ryt.div100(totleRefundAmt), "", ""};
			list.add(str);
			String filename = "ONLINEREFUND_" + DateUtil.today() + ".xlsx";
			String name = "交易明细表";
		   return new DownloadFile().downloadXLSXFileBase(list, filename, name);
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
