package com.rongyifu.mms.api.bizobjs;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.modules.sysmanage.service.BankInfoService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class BankInfoBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String bkName = params.get("bkName");
		String pageNoStr = params.get("pageNo");
		String gid = params.get("gid");
		String areaNo = params.get("areaNo");
		String pageSizeStr = params.get("pageSize");
		String bkNo = params.get("bkNo");//联行行号
		if(StringUtils.isEmpty(gid)){
			throw new Exception("参数gid不能为空.");
		}
		/*if(StringUtils.isEmpty(areaNo)){
			throw new Exception("参数areaNo不能为空.");
		}*/
		if(StringUtils.isEmpty(pageNoStr)){
			throw new Exception("参数pageNo不能为空.");
		}
		Integer pageNo = Integer.valueOf(pageNoStr);
		if(pageNo <= 0){
			throw new Exception("参数错误: pageNo = " + pageNo);
		}
		
		int pageSize = 15;//分页默认取15条数据
		if(StringUtils.isNotBlank(pageSizeStr)){
			int size = Integer.parseInt(pageSizeStr);
			if(size <= 100 && size > 0)	{
				pageSize = size;
			}else{
				throw new Exception("pageSize超出取值范围.");
			}
		}
		BankNoInfo bni = new BankNoInfo();
		bni.setBkName(bkName);//银行名称
		bni.setGid(gid);//网关号
		bni.setCityId(areaNo);//城市编码
		bni.setBkNo(bkNo);
		LogUtil.printInfoLog(getClass().getName(), "doBiz", "调用查询联行号接口", params);
		CurrentPage<BankNoInfo> page = new BankInfoService().queryForPage1(bni, pageNo,pageSize);
		JSONObject pageObj = new JSONObject();//总json对象
		JSONArray jsonArr = new JSONArray();
		//过滤掉不需要的属性
		JsonConfig jsonConfig = new JsonConfig();  
        jsonConfig.setExcludes(new String[] {"id", "superBkNo", "provId", "cityId", "type"});
		List<BankNoInfo> banks = page.getPageItems();
		for (BankNoInfo bankNoInfo : banks) {
			jsonArr.add(JSONObject.fromObject(bankNoInfo),jsonConfig);
		}
		pageObj.put("banks", jsonArr);
		pageObj.put("bkName", bkName);
		pageObj.put("pageNo", pageNo);
		pageObj.put("pageSize", page.getPageSize());
		pageObj.put("totalRecord", page.getPageTotle());
		pageObj.put("pagesAvailable", page.getPagesAvailable());
		return pageObj;
	}

}
