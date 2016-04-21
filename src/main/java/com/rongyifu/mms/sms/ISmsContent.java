package com.rongyifu.mms.sms;

import javax.servlet.http.HttpServletRequest;

public interface ISmsContent {
	
	String getContent(HttpServletRequest request);
}
