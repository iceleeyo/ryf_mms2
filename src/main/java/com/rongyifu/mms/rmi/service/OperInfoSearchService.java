package com.rongyifu.mms.rmi.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.CurrentPage;

/**
 *  操作员查询-同步给商户后台
 * @author chen.kaixueqing
 */
public class OperInfoSearchService implements IRemoteServiceProcessor {

	public Object doRequest(Map<String, Object> params) {
		String mid = (String) params.get("mid");
		Integer pageNo = (Integer) params.get("page_no");
		
		if(Ryt.empty(mid))
			return "商户号不能为空";
		
		if(pageNo <= 0){
			return "参数错误: pageNo = " + pageNo;
		}
		CurrentPage<OperInfo> page = new MerchantService().getOpers4Object(mid, null, pageNo);
		JSONObject pageObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		JsonConfig jsonConfig = new JsonConfig();  
		List<OperInfo> list = page.getPageItems();
		for (OperInfo operInfo : list) {
			jsonArr.add(JSONObject.fromObject(operInfo),jsonConfig);
		}
		pageObj.put("operInfo", jsonArr);
		pageObj.put("pageNo", pageNo);
		pageObj.put("pageSize", page.getPageSize());
		pageObj.put("totalRecord", page.getPageTotle());
		pageObj.put("pagesAvailable", page.getPagesAvailable());
		return pageObj;
	}

}
