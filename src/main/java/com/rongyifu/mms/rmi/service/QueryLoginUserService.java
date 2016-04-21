package com.rongyifu.mms.rmi.service;

import java.util.Map;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号和操作员ID获取登录用户信息
 * @author wufei
 *
 */
public class QueryLoginUserService implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		String uid = (String) params.get("uid");
		
		LoginUser loginuser = null;
		try {
			loginuser = new MerOperDao().getLoginedUser(mid, uid);
		} catch (Exception e) {
			LogUtil.printErrorLog("根据商户号和操作员ID获取登录用户信息", e);
		}
		return loginuser;
	}
	
}
