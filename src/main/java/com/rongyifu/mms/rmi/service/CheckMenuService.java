package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 操作员权限查询接口
 * @author wufei
 *
 */
public class CheckMenuService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		Integer operId = (Integer) params.get("operId");
		
		String authStr = new OperAuthDao().checkMenu(mid, operId);
		JSONObject pageObj = new JSONObject();
		pageObj.put("authStr", authStr);
		return pageObj;
	}

}
