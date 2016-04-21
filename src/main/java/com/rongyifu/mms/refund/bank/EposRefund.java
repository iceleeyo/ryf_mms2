package com.rongyifu.mms.refund.bank;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundDbUtil;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 易宝联机退款处理类
 * 
 * @author shdy
 * 
 */
public class EposRefund implements IOnlineRefund {
    private final static String url = "ref/epos_quickpay_refund";

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

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("refAmt", refAmt);
        m.put("bkTseq", bkTseq);
        m.put("orgTseq", refundId);
        m.put("gid", refundBean.getGid());
        String respParams = Ryt.requestWithPost(m, ewpPath + "/" + url);

        Map<String, String> mLog = new HashMap<String, String>();
        mLog.put("online_refund_id", refundId);
        mLog.put("response", respParams);
        LogUtil.printInfoLog("EposRefund", "executeRefund", "params", mLog);

        if (respParams == null || respParams.equals("")) {
            refundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
            refundBean.setRefundFailureReason("请求银行失败！");
        } else {
            String stat = respParams.trim();
            if (stat.equals("1")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
            } else if (stat.equals("2")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("账户状态无效");
            } else if (stat.equals("7")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("该订单不支持退款");
            } else if (stat.equals("10")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("退款金额超限");
            } else if (stat.equals("18")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("余额不足");
            } else if (stat.equals("50")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("订单不存在");
            } else if (stat.equals("10803")) {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
                refundBean.setRefundFailureReason("退款系统异常");
            } else {
                refundBean.setOrderStatus(RefundUtil.ORDER_STATUS_PROCESSED);
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
