package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 商户重要信息修改
 * @author wufei
 *
 */
public class UpdateImportantMsgService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		Minfo minfo = (Minfo) params.get("minfo");
		
		int row = new MerOperDao().updateImportantMsg(minfo);
		JSONObject pageObj = new JSONObject();
		pageObj.put("row", row);
		return pageObj;
	}

}
