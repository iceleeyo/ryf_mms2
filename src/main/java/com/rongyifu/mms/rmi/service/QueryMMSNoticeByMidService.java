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
 * 根据商户号查询消息通知接口
 * @author wufei
 *
 */
public class QueryMMSNoticeByMidService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		
		List<MMSNotice> list = new SystemDao().queryMMSNotice(mid);
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
