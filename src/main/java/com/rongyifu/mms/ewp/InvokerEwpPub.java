package com.rongyifu.mms.ewp;

import java.util.Map;

import com.rongyifu.mms.common.Ryt;

public class InvokerEwpPub {
	
	private static final String EWPURI="mms_pub/trans_entry";
	
	
	public static String doEntry(Map<String, Object> paramMap){
		String url = Ryt.getEwpPath();//EWP地址
		return Ryt.requestWithPost(paramMap, url + EWPURI);
	}
	
}
