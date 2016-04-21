package com.rongyifu.mms.api.bizobjs;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.AreaCode;
import com.rongyifu.mms.modules.sysmanage.service.AreaCodeService;
import com.rongyifu.mms.utils.LogUtil;

public class AreaInfoBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String typeStr = params.get("type");
		if(StringUtils.isBlank(typeStr)){
			throw new Exception("参数type不能为空.");
		}
		Integer type = Integer.valueOf(typeStr);
		String provNo = null;
		//过滤掉不需要的属性
		JsonConfig jsonConfig = new JsonConfig();  
	    if (type == 0) {//查询省份列表
			jsonConfig.setExcludes(new String[] { "attrCityNo","attrCityName","areaNo","areaName","areaLevel"});
		}else if(type == 1){//查询
			jsonConfig.setExcludes(new String[] { "attrCityNo","attrCityName" });
			provNo = params.get("provNo");
			if(StringUtils.isBlank(provNo)){
				throw new Exception("当type=1时, provNo不能为空");
			}
		}else{
			throw new Exception("错误的type值: "+type);
		}
		LogUtil.printInfoLog(getClass().getName(), "doBiz", "调用查询地区编码接口", params);
		List<AreaCode> list = new AreaCodeService().cascadeQuery(type, provNo);
		JSONArray jsonArr = new JSONArray();
		for (AreaCode ac : list) {
			jsonArr.add(JSONObject.fromObject(ac),jsonConfig);
		}
		return jsonArr;
	}

}
