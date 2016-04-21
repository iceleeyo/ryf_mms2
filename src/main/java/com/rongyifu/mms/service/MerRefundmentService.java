package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.BatchRefundBean;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.RefundOB;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RefundDao;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MerRefundmentService {
	private SettlementDao settlementDao = new SettlementDao();
	private RefundDao refundDao = new RefundDao();
	private Logger logger =Logger.getLogger(MerRefundmentService.class);
	
	/**
	 * 获得下载的List<RefundLog>
	 * @return
	 */
	private List<RefundLog> getRefundList(String mid, String stat, String tseq,Integer dateState, Integer bdate,
			Integer edate, Integer gate, String orgid, Integer vstate,Integer gateRouteId){
		CurrentPage<RefundLog> refundLogPage=refundDao.queryRefundLogs(1,-1, mid, stat, tseq, 
				dateState, bdate, edate, gate, orgid, vstate, gateRouteId,null,null);
		return refundLogPage.getPageItems();
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
	
	
	   /**
		 * 退款报表下载（查询）
		 * @return
		 * @throws Exception 
		 */
		public FileTransfer downloadReturn(String mid, String stat, String tseq,Integer dateState, Integer bdate,
				Integer edate, Integer gate, String orgid, Integer vstate,Integer authorType) throws Exception{
			String sessionMid =settlementDao.getLoginUser().getMid();
			
			List<RefundLog> resultlist=getRefundList(mid, stat, tseq, dateState, bdate, edate, gate, orgid, vstate, authorType);
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			Map<Integer, String> mstat = AppParam.h_mer_refund_tstat;
			String[] title = { "序号","商户号","退款流水号","原电银流水号","原商户订单号","原银行流水号","原交易日期",
					"原交易金额（元）","原交易银行","退款金额（元）","退还商户手续费(元)","经办日期","退款确认日期","退款状态 ", !"1".equals(sessionMid) ? "" :"原订单优惠金额(元)" };
			ArrayList<String[]> list = new ArrayList<String[]>();
			list.add(title);
			long refAmount = 0;
			long refMerFee=0;
			for (int j = 0; j < resultlist.size(); j++) {
				RefundLog refundLog=resultlist.get(j);
				String[] strArr={
						String.valueOf(j+1),
						String.valueOf(refundLog.getMid()),
						String.valueOf(refundLog.getId()),
						String.valueOf(refundLog.getTseq()),
						refundLog.getOrg_oid(),
						refundLog.getOrgBkSeq(),
						String.valueOf(refundLog.getOrg_mdate()),
						Ryt.div100(refundLog.getOrgAmt()),
						gates.get(refundLog.getGate()),
						Ryt.div100(refundLog.getRef_amt()),
						Ryt.div100(refundLog.getMerFee()),
						changeToString(refundLog.getPro_date()),
						changeToString(refundLog.getReq_date()),
						mstat.get(refundLog.getStat()),
						!"1".equals(sessionMid) ? "" : Ryt.div100(refundLog.getPreAmt())
						
				};
				refAmount+=refundLog.getRef_amt();
				refMerFee+=refundLog.getMerFee();
				//preAmt+=refundLog.getPreAmt();
				list.add(strArr);
			}
			String[] nullstr = { "","", "", "", "", "", "","","", "","", "", "", "", "" };
			list.add(nullstr);
			String[] str = { "总计:" + resultlist.size() + "条记录","","","","","","","", "",
					Ryt.div100(refAmount),Ryt.div100(refMerFee),"","", "","" };
			list.add(str);
			String filename = "MERREFUNDQUERY_" + DateUtil.getIntDateTime() + ".xlsx";
			String name = "退款报表";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);

		}
	/**
	 * 退款查询
     * @return CurrentPage<RefundLog>
	 */	 
	public CurrentPage<RefundLog> queryRefundLogs(Integer pageNo, String mid, String stat, String tseq,Integer dateState,
								Integer bdate,Integer edate, Integer gate, String orgid, Integer vstate,Integer authorType) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
		return refundDao.queryRefundLogs(pageNo,pageSize, mid, stat, tseq, 
							dateState, bdate, edate, gate, orgid, vstate, authorType,null,null);
	}
	
	/**
	 * 根据Id查询RefundLog
	 * @param id
	 * @return RefundLog
	 */
	public RefundLog queryRefundLogById(Integer id) {
		return refundDao.queryRefundLogById(id);
	}
	
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
	 * 退款申请页面查询 单个交易对象
	 * @param merOid 商户订单号
	 * @param mid 商户号
	 * @param merDate 商户日期
	 * @return RefundOB 退款页面显示的对象 RefundOB
	 */
	public RefundOB getRefundOBByMer(String merOid, String mid, int merDate, String sessionMid) {
		if (merOid == "" || Ryt.empty(mid)) return null;
		RefundOB refundOB = refundDao.queryRefundOBByMer(merOid, mid, merDate, sessionMid);
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
		long time1 = DateUtil.today();  
		         long time2 = refundOB.getSysDate();
		        long diff = time1 - time2;
		        //long days=diff/(1000 * 60 * 60 * 24);  
				//System.out.println(time1);
				//System.out.println(time2);
				//System.out.println(diff);
				//System.out.println(days);
				if(diff>10000){
					return"退款已过期,请联系相关人员,谢谢!";
				}
		if (refundOB.getTstat() != Constant.TRANS_SUCCES) {
			refundOB.setCode(1);
			return "退款申请失败，请确认该订单是否为支付成功交易！";
		}
		if (refundOB.getOrgAmt() < Ryt.mul100toInt(refundOB.getApplyRrefundAmount())) {
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
		long refundAmt=0;
		try {
			refundAmt = refundDao.queryForLong("select refund_amt from " + t
					+ " where mid = " + Ryt.addQuotes(refundOB.getMid()) + " and tseq=" + Ryt.addQuotes(refundOB.getTseq()));
		} catch (Exception e) {
			refundAmt = refundDao.queryForLong("select refund_amt from "
					+ (t.equals(Constant.TLOG) ? Constant.HLOG : Constant.TLOG)
					+ " where mid = " + Ryt.addQuotes(refundOB.getMid()) + " and tseq=" + Ryt.addQuotes(refundOB.getTseq()));
		}
		refundOB.setRefundAmt(refundAmt);
		
		long canRefund = refundOB.getOrgAmt() - refundOB.getRefundAmt();
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
	
	public List<BatchRefundBean> doBatchRefund(List<BatchRefundBean> dataList,String longMid,String refundReason ,String tt){
//		int succcount = 0;
//		long amt = 0;
		for (BatchRefundBean bean : dataList) {
			logger.info("mid="+bean.getInitMid()+" oid="+bean.getInitMerOid()+" tseq="+bean.getInitTseq());
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
				refundDao.queryForInt("select fee_amt from "+t+" where tseq="+Ryt.addQuotes(rl.getTseq()));
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
		refundDao.saveOperLog("退款撤销", "退款撤销完成" + ids);
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
			e.printStackTrace();
		}
		refundDao.saveOperLog("确认退款", returnStr + refundIds);
		return returnStr;
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

		} else if (refundOB.getOrgAmt() - refundOB.getRefundAmt() <= 0) {
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

}
