package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号和操作员ID查询权限接口
 * @author wufei
 *
 */
public class QueryAuthByMidAndOperIdService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		Integer operId = (Integer) params.get("operId");
		
		String authOld = new OperAuthDao().findAuth(mid, String.valueOf(operId));
		JSONObject pageObj = new JSONObject();
		pageObj.put("authOld", authOld);
		return pageObj;
	}

}
