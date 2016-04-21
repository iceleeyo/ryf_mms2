package com.rongyifu.mms.refund.bank;

import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundDbUtil;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.JsonUtil;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 盛京联机退款处理类
 * 
 * @author shdy
 * 
 */
public class SJRefund implements IOnlineRefund {
    private final static String url = "/ref/refund_entry";

    @Override
    public OnlineRefundBean executeRefund(Object obj) {
        OnlineRefundBean refundBean = (OnlineRefundBean) obj;
        String refAmt = Ryt.div100(String.valueOf(refundBean.getRefAmt()));
        String bkTseq = refundBean.getBkTseq();
        String ewpPath = ParamCache.getStrParamByName("EWP_PATH");
        String refundId = handle_orderNum(String.valueOf(refundBean.getId()));
        refundBean.setRefBatch(refundId);
        // 保存退款批次号
        new RefundDbUtil().saveOnlineRefundId(refundBean);
        // ewp请求地址
        String requestUrl = ewpPath + url;
        // 请求ewp接口
        Map<String, Object> m = new TreeMap<String, Object>();
        m.put("gidFlag", "sj");
        m.put("refAmt", refAmt);
        m.put("bkTseq", bkTseq);
        m.put("refundId", refundId);
        m.put("gid", refundBean.getGid());
        String respParams = Ryt.requestWithPost(m, requestUrl);
        
        m.put("requestUrl", requestUrl);
        m.put("response", respParams);
        LogUtil.printInfoLog("executeRefund", m);
        
        // 处理退款结果
        Map<String, Object> resMap = JsonUtil.getMap4Json(respParams);
        String retStatus = String.valueOf(resMap.get("retStatus"));
        if(Ryt.empty(retStatus)){ // 返回异常
        	refundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
            refundBean.setRefundFailureReason("请求银行失败！");
        } else if ("0".equals(retStatus)){ // 返回正常
        	String refundStatus = String.valueOf(resMap.get("refundStatus"));
        	if(RefundUtil.ORDER_STATUS_SUCCESS.equals(refundStatus))
        		refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
        	else {
        		String retMsg = String.valueOf(resMap.get("retMsg"));
        		refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
        		refundBean.setRefundFailureReason(retMsg);
        	}
        } else if ("1".equals(retStatus)){ // 返回异常
        		String retMsg = String.valueOf(resMap.get("retMsg"));
        		refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
        		refundBean.setRefundFailureReason(retMsg);
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
