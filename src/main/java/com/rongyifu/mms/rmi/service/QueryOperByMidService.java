package com.rongyifu.mms.rmi.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号获取操作员接口
 * @author wufei
 *
 */
public class QueryOperByMidService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		
		Map<Integer, String> map = new MerOperDao().getHashOper(mid);
		
		Map<String, Object> operMap = new HashMap<String, Object>();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			operMap.put(entry.getKey().toString(), entry.getValue());
		}
		
		JSONObject pageObj = new JSONObject();
		pageObj.put("operMap", operMap);
		return pageObj;
	}

}
