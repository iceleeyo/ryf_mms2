package com.rongyifu.mms.exception;
/**
 *定义CMB出现的错误异常 
 * @author cody  2010-5-20
 *
 */
@SuppressWarnings("serial")
public class CMBException extends Exception {
	public CMBException(){
		super();
	}
	public CMBException(String msg){
		super(msg);
	}
}
