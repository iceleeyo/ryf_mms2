package com.rongyifu.mms.sms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rongyifu.mms.utils.RYFMapUtil;

/**
 * 支付成功短信
 *
 */
public class SmsTradeSuccess implements ISmsContent{

	@Override
	public String getContent(HttpServletRequest request) {
		String mid = request.getParameter("mid");// 商户号
		String orderId = request.getParameter("orderId"); // 订单号
		String transAmt = request.getParameter("transAmt"); // 交易金额
		
		// 获取商户简称
		RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<String, String> minfo = obj.getMinfoMap();
		String abbrev = minfo.get(mid);
		
		// 组装短信内容
		String smsContent = "您在" + abbrev + "订单号为" + orderId + "的交易：" + transAmt +"元支付成功！";
		return smsContent;
	}
}
