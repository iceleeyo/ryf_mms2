package com.rongyifu.mms.rmi.service;
import java.util.Map;

import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;
//日志保存接口
public class OperLogBizObj implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> args) {
		MerOperDao merOperDao = new MerOperDao();
		String action = String.valueOf(args.get("action"));
		String actionDesc = String.valueOf(args.get("actionDesc"));
		String mid = String.valueOf(args.get("mid"));
		int operId = Integer.parseInt(String.valueOf(args.get("operId")));
		String operIp = String.valueOf(args.get("operIp"));
		LogUtil.printInfoLog(getClass().getName()+"调用日志保存接口", args);
		return merOperDao.saveMinfoLog( mid, operId, action, actionDesc, operIp);
	}

}
