package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号和操作员ID获取角色信息接口
 * @author wufei
 *
 */
public class QueryRoleByMidAndOperIdService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		Integer operId = (Integer) params.get("operId");
		
		int role = new OperAuthDao().getRole(mid, operId);
		JSONObject pageObj = new JSONObject();
		pageObj.put("role", role);
		return pageObj;
	}

}
