package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 权限分配操作接口
 * @author wufei
 *
 */
public class AddMenuService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String authstr = (String) params.get("authstr");
		String mid = (String) params.get("mid");
		Integer operId = (Integer) params.get("operId");
		
		new OperAuthDao().addMenu(authstr, mid, operId);
		JSONObject pageObj = new JSONObject();
		pageObj.put("result", 1);
		return pageObj;
	}

}
