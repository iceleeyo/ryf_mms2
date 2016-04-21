package com.rongyifu.mms.refund.bank;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.LogUtil;
/****
 * 快钱联机退款处理类
 * @author shdy
 *
 */
public class KqRefund implements IOnlineRefund {
	private final static String url="ref/ivr_refund";
	@Override
	public OnlineRefundBean executeRefund(Object obj) {
		OnlineRefundBean refundBean=(OnlineRefundBean)obj;
		String refAmt=Ryt.div100(String.valueOf(refundBean.getRefAmt()));
		String bkTseq=refundBean.getBkTseq();
		String ewpPath=ParamCache.getStrParamByName("EWP_PATH");
		Integer orgOrderGid=refundBean.getGid();
		String refundId = handle_orderNum(String.valueOf(refundBean.getId()));
		refundBean.setRefBatch(refundId);
		
		Map<String, Object> m=new HashMap<String, Object>();
		m.put("refAmt", refAmt);
		m.put("bkTseq", bkTseq);
		m.put("orgOrderGid", orgOrderGid);
		m.put("transType", "refund_kq"); /***交易类型 refund_kq 快钱退款***/
		m.put("refundId", refundId);
		String respParams=Ryt.requestWithPost(m, ewpPath+"/"+url);
		
		Map<String, String> mLog=new HashMap<String, String>();
		mLog.put("orgOrderTseq", refundBean.getOrgTseq());
		mLog.put("response", respParams);
		LogUtil.printInfoLog("KqRefund", "executeRefund", "params", mLog);
		
		if(respParams==null || respParams.equals("")){
			refundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
			refundBean.setRefundFailureReason("请求银行失败！");
		}else{
			String[] respInfos=respParams.split("\\|");
			String stat=respInfos[0];String result=respInfos[1];
			if(stat.equals("success")){
				refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
			}else if(stat.equals("fail")){
				refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
				refundBean.setRefundFailureReason(result);
			}else if(stat.equals("wait")){
				refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_PROCESSED);
			}else if(stat.equals("exception")){
				refundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
				refundBean.setRefundFailureReason(result);
			}
		}
		return refundBean;
	}
	
	public static String handle_orderNum(String tseq) {
		String num = "0000000000";
		int len = tseq.length();
		if (len < 10) {
			return num.substring(0, 10 - len) + tseq;
		} else {
			return tseq;
		}
	}
}
