package com.rongyifu.mms.rmi.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class OperLogDownloadBizObj implements IRemoteServiceProcessor {
	@Override
	public Object doRequest(Map<String, Object> args) {
		String beginDate = String.valueOf(args.get("beginDate"));
		String endDate = String.valueOf(args.get("endDate"));
		//String pageNoStr = String.valueOf(args.get("pageNo"));
		String mid = String.valueOf(args.get("mid"));
		String operId = String.valueOf(args.get("operId"));
		if(StringUtils.isEmpty(String.valueOf(beginDate))){
			try {
				throw new Exception("参数beginDate不能为空");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(StringUtils.isEmpty(String.valueOf(endDate))){
			try {
				throw new Exception("参数endDate不能为空.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//LogUtil.printInfoLog(getClass().getName(), "doBiz", "调用查询操作员日志列表接口", args);
		LogUtil.printInfoLog(getClass().getName()+"调用下载操作员日志列表接口", args);
		CurrentPage<OperLog> page = new MerchantService().getMidOperLog(mid, operId, Integer.valueOf(beginDate), Integer.valueOf(endDate), 1,-1);
		JSONObject pageObj = new JSONObject();//总json对象
		JSONArray jsonArr = new JSONArray();
		//过滤掉不需要的属性
		JsonConfig jsonConfig = new JsonConfig();  
		List<OperLog> operLogList = page.getPageItems();
		for (OperLog OperLog : operLogList) {
			jsonArr.add(JSONObject.fromObject(OperLog),jsonConfig);
		}
		pageObj.put("operLogs", jsonArr);
		pageObj.put("totalRecord", page.getPageTotle());
		pageObj.put("pagesAvailable", page.getPagesAvailable());
		return pageObj;
	}

}
