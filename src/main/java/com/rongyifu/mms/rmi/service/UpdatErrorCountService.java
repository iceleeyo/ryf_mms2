package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 记录登录错误次数
 * @author wufei
 *
 */
public class UpdatErrorCountService implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String newErrCount = (String) params.get("newErrCount");
		String mid = (String) params.get("mid");
		String operId = (String) params.get("operId");
		String mType = (String) params.get("mType");
		
		int result = new MerOperDao().updatErrorCount(Integer.valueOf(newErrCount),mid, Integer.valueOf(operId), Integer.valueOf(mType));
		JSONObject pageObj = new JSONObject();
		pageObj.put("result", result);
		return pageObj;
	}

}
