package com.rongyifu.mms.utils;

import java.util.*;
import net.sf.json.JSONObject;

public class JsonUtil {
	
	/**
	 * 从json HASH表达式中获取一个map，该map支持嵌套功能
	 * @param jsonString
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, Object> getMap4Json(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator keyIter = jsonObject.keys();
		String key;
		Object value;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		while (keyIter.hasNext()) {
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;
	}

	

	/**
	 * 将java对象转换成json字符串
	 * @param javaObj
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj) {
		JSONObject json;
		json = JSONObject.fromObject(javaObj);
		return json.toString();
	}
	
}