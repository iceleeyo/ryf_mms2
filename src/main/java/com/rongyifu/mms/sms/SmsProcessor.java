package com.rongyifu.mms.sms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rongyifu.mms.utils.LogUtil;

public class SmsProcessor {
	
	private SmsProcessor(){}
	
	/**
	 * 短信模板
	 */
	private static Map<String, String> smsTemplate = new HashMap<String, String>();
	
	static{
		smsTemplate.put("TRADE_SUCCESS", "com.rongyifu.mms.sms.SmsTradeSuccess");
	}
	
	/**
	 * 获取短信内容
	 * @param request
	 * @return
	 */
	public static String processContent(HttpServletRequest request){
		String content = request.getParameter("content");
		String className = smsTemplate.get(content);
		// 如果短信模板不存在，则content值就是短信内容
		if(className == null)
			return content;
		
		// 生成短信内容
		try {
			ISmsContent sms = (ISmsContent) Class.forName(className).newInstance();
			return sms.getContent(request);
		} catch (Exception e) {
			LogUtil.printErrorLog(SmsProcessor.class.getCanonicalName(), "processContent", content, e);
			return null;
		} 
	}
}
