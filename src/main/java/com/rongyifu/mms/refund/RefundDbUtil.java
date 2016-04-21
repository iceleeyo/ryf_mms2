package com.rongyifu.mms.refund;

import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ErrorCodes;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("rawtypes")
public class RefundDbUtil extends PubDao {

	public int UpdateIOnlineRefund(OnlineRefundBean refund) {
		String status = refund.getOrderStatus();
		int id = refund.getId();
		String errorMsg = refund.getRefundFailureReason();
		String Bath = refund.getRefBatch();
		String sql = null;
		// 银行返回处理中修改联机状态
		if ((RefundUtil.ORDER_STATUS_PROCESSED).equals(status)) {
			sql = "update refund_log set online_refund_state = '" + status+ "' where id=" + id+ " and refund_type=1  online_refund_id= '" + Bath + "'";
		} else if ((RefundUtil.QUERT_BANK_FAILURE).equals(status)) {
			sql = "update refund_log set online_refund_state = '" + status+ "' where id=" + id + " and online_refund_id= '" + Bath+ "' and refund_type=1 and online_refund_reason='"+ errorMsg + "'";
		} else if ((RefundUtil.ORDER_STATUS_FAILURE).equals(status)) {
			sql = "update refund_log set online_refund_state = '" + status+ "' where id=" + id + " and online_refund_id= '" + Bath+ "' refund_type=1 and online_refund_reason='"+ ErrorCodes.Alipay_Refund.get(errorMsg) + "'";
		}

		Map<String, String> paramsMap = LogUtil.createParamsMap();
		paramsMap.put("联机退款", sql);
		LogUtil.printInfoLog("RefundDbUtil", "UpdateIOnlineRefund", "",
				paramsMap);
		return update(sql.toString());
	}
	
	/**
	 * 保存退款批次号
	 * @param refundBean
	 */
	public void saveOnlineRefundId(OnlineRefundBean refundBean){
		Integer id = refundBean.getId(); // 退款表主键ID
		String batchNo = refundBean.getRefBatch(); // 退款批次号
		String sql = "update refund_log set online_refund_id = " + Ryt.addQuotes(batchNo)+ " where id=" + id;
		update(sql);
	}
}
