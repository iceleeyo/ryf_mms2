package com.rongyifu.mms.rmi.service;

import java.util.Map;

import org.mortbay.log.Log;

import net.sf.json.JSONObject;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 操作员密码修改<查询操作员原密码>-同步给商户后台
 * @author chen.kaixueqing
 */
public class OperInfoSelectPassService implements IRemoteServiceProcessor {
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		
		Object mtype = params.get("mtype");
		if (mtype == null) {
			Log.info("类型不能为空");
			return null;
		}
		Object operId = params.get("operId");
		if (operId == null) {
			Log.info("操作员号不能为空");
			return null;
		}
		String mid = (String)params.get("mid");
		if(Ryt.empty(mid)){
			return "商户号不能为空";
		}
		
		String resultCode = new MerOperDao().getOldPass(Integer.valueOf(mtype.toString()), Integer.valueOf(operId.toString()), mid);
		JSONObject pageObj = new JSONObject();//总json对象
		pageObj.put("resultCode", resultCode);
		return pageObj;
		}
}
