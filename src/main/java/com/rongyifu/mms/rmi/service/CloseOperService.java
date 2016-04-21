package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 登录错误次数超过指定次数关闭该用户
 * @author wufei
 *
 */
public class CloseOperService implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String newErrCount = (String) params.get("newErrCount");
		String mid = (String) params.get("mid");
		String operId = (String) params.get("operId");
		String mType = (String) params.get("mType");
		
		int result = new MerOperDao().closeOper(Integer.valueOf(newErrCount),mid, Integer.valueOf(operId), Integer.valueOf(mType));
		JSONObject pageObj = new JSONObject();
		pageObj.put("result", result);
		return pageObj;
	}

}
