package com.rongyifu.mms.rmi.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.rongyifu.mms.utils.LogUtil;

/**
 * 服务处理注册类
 * @author Admin
 *
 */
public class RemoteServiceList {
	
	private RemoteServiceList(){}
	
	private static final String propPath = "/remoteServiceList.properties";
	
	private static final Map<String, String> serviceList = new HashMap<String, String>();
	
	static{
		initServiceList();
	}
	
	/**
	 * 获取服务列表
	 * @return
	 */
	public static Map<String, String> getServiceList(){
		return serviceList;
	}
	
	/**
	 * 获取服务处理类
	 * @param serviceName	服务名
	 * @return
	 */
	public static String getServiceProcessor(String serviceName){
		return serviceList.get(serviceName);
	}
	
	/**
	 * 初始化serviceList
	 */
	private static void initServiceList(){
		Properties props = loadProperties();
		for(Entry<Object, Object> item : props.entrySet()){
			String key = item.getKey().toString();
			String value = item.getValue().toString();
			serviceList.put(key, value);
		}
		
		LogUtil.printInfoLog("RemoteServiceList初始化完成", serviceList);
	}
	
	/**
	 * 加载配置文件
	 * @return
	 */
	private static Properties loadProperties() {
		Properties props = null;
		InputStream in = null;
		
		try {
			in = RemoteServiceList.class.getResourceAsStream(propPath);
			props = new Properties();
			props.load(in);			
		} catch (Exception e) {
			LogUtil.printErrorLog("remoteServiceList.properties加载失败", e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.printErrorLog("InputStream关闭异常", e);
				}
		}
		
		return props;
	}
}
