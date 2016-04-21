package com.rongyifu.mms.rmi.service;

import java.util.Map;

/**
 * 服务处理接口
 * @author Admin
 *
 */
public interface IRemoteServiceProcessor {
	
	Object doRequest(Map<String, Object> args);
}
