package com.rongyifu.mms.rmi.service;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class OperLogSerachBizObj implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> args) {
		// TODO Auto-generated method stub
		String beginDate = String.valueOf(args.get("beginDate"));
		String endDate = String.valueOf(args.get("endDate"));
		String pageNoStr = String.valueOf(args.get("pageNo"));
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
		Integer pageNo =Integer.parseInt(String.valueOf(pageNoStr));
		if(pageNo <= 0){
			try {
				throw new Exception("参数错误: pageNo = " + pageNo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//LogUtil.printInfoLog(getClass().getName(), "doBiz", "调用查询操作员日志列表接口", "");
		LogUtil.printInfoLog(getClass().getName()+"调用查询操作员日志列表接口", args);
		CurrentPage<OperLog> page = new MerchantService().getMidOperLog(mid, operId, Integer.valueOf(String.valueOf(beginDate)), Integer.valueOf(String.valueOf(endDate)), pageNo,new AppParam().getPageSize());
		
		return page;
	}

}
