package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 用户成功登录后记录客户端ip和登录时间
 * @author wufei
 *
 */
public class UpdateUserLoginTimeService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		String uid = (String) params.get("uid");
		String loginIp = (String) params.get("loginIp");
		
		new MerOperDao().updateUserLoginTime(mid, uid, loginIp);
		JSONObject pageObj = new JSONObject();
		pageObj.put("result", 1);
		return pageObj;
	}

}
