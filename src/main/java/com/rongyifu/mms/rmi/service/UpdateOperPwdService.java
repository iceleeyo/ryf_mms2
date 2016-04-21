package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 修改操作员密码接口
 * @author wufei
 *
 */
public class UpdateOperPwdService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String newPwd = (String) params.get("newPwd");
		String mid = (String) params.get("mid");
		Integer operId = (Integer) params.get("operId");
		String antiPhishingStr = (String) params.get("antiPhishingStr");
		
		int count = new MerOperDao().updateOperPwd(newPwd, mid, operId, antiPhishingStr);
		JSONObject pageObj = new JSONObject();
		pageObj.put("count", count);
		return pageObj;
	}

}
