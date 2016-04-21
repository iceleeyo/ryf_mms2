package com.rongyifu.mms.rmi.service;

import java.util.Map;
import net.sf.json.JSONObject;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.service.MerchantService;


/**
 * 操作员修改-同步给商户后台
 * @author chen.kaixueqing
 */
public class OperInfoUpdateService implements IRemoteServiceProcessor {
	public Object doRequest(Map<String, Object> params) {
		String state =(String) params.get("state");
		String operEmail = (String)params.get("operEmail");
		String operTel = (String)params.get("operTel");
		String operName = (String)params.get("operName");
		String operId = (String)params.get("operId");
		String mid = (String)params.get("mid");
		
		if(Ryt.empty(operName)){
			return "操作员姓名不能为空";
		}
		
		if(Ryt.empty(mid)){
			return "商户号不能为空";
		}
		String resultCode = new MerchantService().editOperInfo(state, operEmail, operTel, operName, operId, mid,0);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", resultCode);
		return jsonObject;
		}
}
