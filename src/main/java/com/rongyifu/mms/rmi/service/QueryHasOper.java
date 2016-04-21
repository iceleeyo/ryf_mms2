package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 操作员新增时根据操作员ID、商户号和商户类型查询操作员是否被占用
 * @author wufei
 *
 */
public class QueryHasOper implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		Integer operid = (Integer) params.get("operid");
		String minfo_id = (String) params.get("minfo_id");
		Integer mtype = (Integer) params.get("mtype");
		
		int count = new OperAuthDao().hasOper(operid, minfo_id, mtype);
		JSONObject pageObj = new JSONObject();
		pageObj.put("count", count);
		return pageObj;
	}

}
