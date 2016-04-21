package com.rongyifu.mms.rmi.service;

import java.util.Map;
import net.sf.json.JSONObject;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.service.MerchantService;

/**
 * 操作员密码修改-同步给商户后台
 * @author chen.kaixueqing
 */
public class OperInfoUpdatePassService implements IRemoteServiceProcessor {
	public Object doRequest(Map<String, Object> params) {
		String mid = (String)params.get("mid");
		String operId = (String)params.get("operId");
		String opass= (String)params.get("opass");
		String npass= (String)params.get("npass");
		
		if(Ryt.empty(mid)){
			return "商户号不能为空";
		}
		
		if(Ryt.empty(opass)){
			return "原密码不能为空";
		}
		
		if(Ryt.empty(npass)){
			return "新密码不能为空";
		}
		String resultCode = new MerchantService().editPass(mid, operId,opass, npass);
		JSONObject pageObj = new JSONObject();//总json对象
		pageObj.put("resultCode", resultCode);
		return pageObj;
		}
}
