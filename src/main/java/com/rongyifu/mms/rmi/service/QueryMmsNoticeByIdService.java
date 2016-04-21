package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 通知信息详情查询接口
 * @author wufei
 *
 */
public class QueryMmsNoticeByIdService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		Integer id = (Integer) params.get("id");
		
		MMSNotice mmsNotice = new SystemDao().getMessageById(id);
		JSONObject pageObj = new JSONObject();
		pageObj.put("mmsNotice", mmsNotice);
		return pageObj;
	}

}
