package com.rongyifu.mms.rmi.service;

import java.util.Map;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 操作员新增-同步给商户后台
 * @author chen.kaixueqing
 */
public class OperInfoAddService implements IRemoteServiceProcessor {

	@SuppressWarnings("deprecation")
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String action = (String) params.get("action");
		String ostate = (String) params.get("ostate");
		String oper_email = (String) params.get("oper_email");
		String oper_tel = (String) params.get("oper_tel");
		String oper_name = (String) params.get("oper_name");
		String operpass = (String) params.get("operpass");
		Integer operid = (Integer) params.get("operid");
		String minfo_id = (String) params.get("minfo_id");
		Integer mtype = (Integer) params.get("mtype");
		Integer role = (Integer) params.get("role");
		String auth = (String) params.get("auth");
		
		int effectNum = 0;
		try {
			new MerOperDao().add(action, ostate, oper_email, oper_tel, oper_name, operpass, operid, minfo_id, mtype, role, auth);
			effectNum = 1;
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "add", "", e);
		}
		return effectNum;
	}
}
