package com.rongyifu.mms.exception;

import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("serial")
public class RytException extends Exception {
	public RytException() {
		super();
	}

	public RytException(String msg) {
		super(msg);
		LogUtil.printErrorLog("RytException", "RytException", msg);
	}

	public RytException(String msg, Throwable cause) {
		super(msg, cause);
		LogUtil.printErrorLog("RytException", "RytException", msg);
	}
}