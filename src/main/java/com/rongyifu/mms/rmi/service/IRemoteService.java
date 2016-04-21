package com.rongyifu.mms.rmi.service;

import java.util.Map;

/**
 * 服务受理接口
 * @author Admin
 *
 */
public interface IRemoteService {
	
	public Object invokeMethod(Map<String, Object> args);
}
