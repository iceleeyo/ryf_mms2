package com.rongyifu.mms.rmi.service;

import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

public class RemoteServiceImpl implements IRemoteService{
	
	@Override
	public Object invokeMethod(Map<String, Object> args) {
		LogUtil.printInfoLog(args);
		
		String serviceName = (String) args.get("serviceName");
		if(Ryt.empty(serviceName))
			return null;
		
		String className = RemoteServiceList.getServiceProcessor(serviceName);
		if(Ryt.empty(className))
			return null;
		try {
			Class<?> clazz = Class.forName(className);
			IRemoteServiceProcessor processor = (IRemoteServiceProcessor) clazz.newInstance();
			return processor.doRequest(args);
		} catch (Exception e) {
			LogUtil.printErrorLog("className=" + className, e);
			return null;
		}
	}

}
