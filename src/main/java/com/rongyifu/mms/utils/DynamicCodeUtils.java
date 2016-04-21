package com.rongyifu.mms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import com.rongyifu.mms.api.BizMapper;
import com.rypay.dynamicode.DynamicodeProcessor;
import com.rypay.dynamicode.ReturnInfo;

public class DynamicCodeUtils {
	
	private static final String CONFIG_FILE = "/param.properties";
	private static DynamicodeProcessor processor;

	static{
		init();
	}
	
	public static void init(){
		try {
			Map<String,String> configMap = loadProp(CONFIG_FILE);
			String host = configMap.get("token_server_host");
			String port = configMap.get("token_server_port"); 
			String key = configMap.get("token_server_key"); 
			processor = new DynamicodeProcessor(host, port, key);
		} catch (Exception e) {
			LogUtil.printErrorLog(DynamicCodeUtils.class.getCanonicalName(), "init", e.getMessage(), e);
		}
	}
	
	private static Map<String,String> loadProp(String path) throws IOException{
		Map<String,String> map = new HashMap<String,String>();
		InputStream is = null;
		try {
			Properties p = new Properties();
			is = BizMapper.class.getResourceAsStream(path);
			p.load(is);
			Set<Entry<Object, Object>> set = p.entrySet();
			for (Entry<Object, Object> entry : set) {
				map.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} finally {
			if(null != is){
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.printErrorLog(DynamicCodeUtils.class.getCanonicalName(), "loadProp", e.getMessage(), e);
				}
			}
		}
		return map;
	}
	
	/**
	 * 绑定动态令牌
	 * @param userName
	 * @param tokensn
	 * @throws Exception 
	 */
	public static boolean tokenBind(String userName, String tokensn) throws Exception{
		ReturnInfo rslt = processor.tokenBind(userName, tokensn);
		Map<String, String> map = LogUtil.createParamsMap();
		map.put("userName", userName);
		map.put("tokensn", tokensn);
		map.put("isSuccess", String.valueOf(rslt.isSuccess()));
		map.put("errorMsg", rslt.getRetMsg());
		LogUtil.printInfoLog(DynamicCodeUtils.class.getCanonicalName(), "tokenBind", "", map);
		if(!rslt.isSuccess()){			
			throw new Exception("绑定动态令牌失败");
		}
		
		return true;
	}
	
	/**
	 * 解绑动态令牌
	 * @param userName 解绑用户名 和绑定时的用户名一致 
	 * @throws Exception 
	 */
	public static boolean tokenUnbind(String userName) throws Exception{
		boolean flag = false;
		try {
			ReturnInfo rslt = processor.tokenUnbind(userName);
			flag = rslt.isSuccess();
		} catch (Exception e) {
			LogUtil.printErrorLog(DynamicCodeUtils.class.getCanonicalName(), "tokenUnbind", "解绑动态令牌失败", e);
			throw new Exception("解绑动态令牌失败");
		}
		return flag;
	}
	
	/**
	 * 根据序列号验证
	 * @param tokenSn 动态令牌序列号
	 * @param dynamicCode 动态码
	 * @throws Exception 
	 */
	public static boolean checkDynamicCode(String tokenSn,String dynamicCode) throws Exception{
		Map<String, String> logMap = LogUtil.createParamsMap();
		logMap.put("tokenSn", tokenSn);
		logMap.put("dynamicCode", dynamicCode);
		
		boolean flag = false;
		try {
			ReturnInfo rslt = processor.checkDynamicPwd(tokenSn, dynamicCode);
			flag = rslt.isSuccess();
			
			logMap.put("isSuccess", String.valueOf(rslt.isSuccess()));
			logMap.put("retMsg", rslt.getRetMsg());
			LogUtil.printInfoLog(DynamicCodeUtils.class.getCanonicalName(), "checkDynamicCode", "验证动态口令", logMap);
		} catch (Exception e) {
			LogUtil.printErrorLog(DynamicCodeUtils.class.getCanonicalName(), "checkDynamicCode", "验证动态口令失败", logMap, e);
			throw new Exception("验证动态令牌失败");
		}
		return flag;
	}
	
	/**
	 * 根据用户名验证
	 * @param userName 动态令牌绑定用户名
	 * @param dynamicCode 动态码
	 */
	public static void checkDynamicCodeByUserName(String userName,String dynamicCode){
		throw new UnsupportedOperationException();
	}

}
