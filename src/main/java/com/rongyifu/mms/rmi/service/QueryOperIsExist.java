package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 操作员新增时根据商户号查询操作员是否存在
 * @author wufei
 *
 */
public class QueryOperIsExist implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String minfo_id = (String) params.get("minfo_id");
		
		boolean flag = new OperAuthDao().isExistOper(minfo_id);
		JSONObject pageObj = new JSONObject();
		pageObj.put("flag", flag);
		return pageObj;
	}

}
