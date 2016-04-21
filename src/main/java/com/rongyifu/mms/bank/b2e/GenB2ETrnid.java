package com.rongyifu.mms.bank.b2e;

import com.rongyifu.mms.utils.DateUtil;

public class GenB2ETrnid {
	
	public GenB2ETrnid() {
		super();
	}

	private static long value = DateUtil.now();
	
	private static long orderoid = DateUtil.now();

	public static synchronized String getTrace() {
		String res = String.valueOf(value);
		value++;
		if (value > 999999999999l) {
			value = DateUtil.now();
		}
		return res;
	}
	
	public static synchronized String getRandOid() {
		String res = String.valueOf(orderoid);
		if(res.length()==5)res="0"+res;
		orderoid++;
		if (orderoid > 999999) {
			orderoid = DateUtil.now();
		}
		return res;
	}

}
