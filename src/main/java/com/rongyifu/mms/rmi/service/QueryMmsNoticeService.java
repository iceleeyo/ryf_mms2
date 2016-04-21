package com.rongyifu.mms.rmi.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 消息通知查询接口
 * @author wufei
 *
 */
public class QueryMmsNoticeService implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		Integer begin_date = (Integer) params.get("begin_date");
		Integer end_date = (Integer) params.get("end_date");
		
		MMSNotice mmsNotice = new MMSNotice();
		mmsNotice.setMid(mid);
		mmsNotice.setBeginDate(begin_date);
		mmsNotice.setEndDate(end_date);
		
		List<MMSNotice> list = new SystemDao().getMessage(mmsNotice);
		JSONObject pageObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		JsonConfig jsonConfig = new JsonConfig();  
		for (MMSNotice notice : list) {
			jsonArr.add(JSONObject.fromObject(notice),jsonConfig);
		}
		pageObj.put("mmsNotice", jsonArr);
		return pageObj;
	}

}
