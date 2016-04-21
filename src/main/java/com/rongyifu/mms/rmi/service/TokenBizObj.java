package com.rongyifu.mms.rmi.service;
import java.util.Map;

import com.rongyifu.mms.bean.DynamicToken;
import com.rongyifu.mms.modules.merchant.dao.DynamicTokenDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 动态令牌获取接口
 * @author wufei
 *
 */
public class TokenBizObj implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> args) {
		String mid = String.valueOf(args.get("mid"));
		int operId = Integer.parseInt(String.valueOf(args.get("operId")));
		LogUtil.printInfoLog(getClass().getName()+"调用获取动态令牌接口", args);
		DynamicToken dynamicToken = new DynamicTokenDao().getConfigByUser(mid, operId);
		return dynamicToken;
	}

}
