package com.rongyifu.mms.exception;

import org.apache.log4j.Logger;

public class B2EException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}



	private Logger logger  =  Logger.getLogger(RytException. class );
	public B2EException(){
		super();
	}
	public B2EException(String msg){
		super(msg);
		logger.error(msg);
	}
	
	
	public B2EException(String code ,String msg){
		super(msg);
		this.errorCode = code;
		logger.error(msg);
	}
	
	
	
	public B2EException(String msg, Throwable cause){
		super(msg, cause);
		logger.error(msg);
	}

}
