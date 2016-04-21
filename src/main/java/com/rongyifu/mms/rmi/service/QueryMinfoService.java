package com.rongyifu.mms.rmi.service;

import java.util.Map;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号获取商户信息接口
 * @author wufei
 *
 */
public class QueryMinfoService implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		Minfo minfo = null;
		try {
			minfo = new MerInfoDao().getOneMinfo(mid);
		} catch (Exception e) {
			LogUtil.printErrorLog("根据商户号获取商户信息出现异常", e);
		}
		return minfo;
	}

}
