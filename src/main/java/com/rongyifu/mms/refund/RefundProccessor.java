package com.rongyifu.mms.refund;

import java.util.Map;

import com.rongyifu.mms.utils.LogUtil;

public class RefundProccessor {
	
	public OnlineRefundBean proccess(OnlineRefundBean onlineRefundBean){
		IOnlineRefund proccessor = null;
		try {
			proccessor = getProccessClass(String.valueOf(onlineRefundBean.getGid()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(proccessor == null){
			LogUtil.printErrorLog("RefundProccessor", "proccess", "退款处理类配置错误！");
			onlineRefundBean.setRefundFailureReason("退款处理类配置错误!");
			onlineRefundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
			return onlineRefundBean;
		}
		
		return proccessor.executeRefund(onlineRefundBean);
	}
	
	private IOnlineRefund getProccessClass(String gateId) throws Exception{
		Map<String, String> gateList = RefundUtil.getRefundGateList();
		String proccessClass = gateList.get(gateId);
		return (IOnlineRefund) Class.forName(proccessClass).newInstance();
	}
}
