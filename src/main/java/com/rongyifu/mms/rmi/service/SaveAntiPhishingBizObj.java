package com.rongyifu.mms.rmi.service;
import java.util.Map;

import com.rongyifu.mms.modules.mermanage.dao.AntiPhishingDao;
import com.rongyifu.mms.utils.LogUtil;
//防伪信息设置接口
public class SaveAntiPhishingBizObj implements IRemoteServiceProcessor {
	
	@Override
	public Object doRequest(Map<String, Object> args) {
		AntiPhishingDao dao = new AntiPhishingDao();
		String antiPhishingStr = String.valueOf(args.get("antiPhishingStr"));
		String mid = String.valueOf(args.get("mid"));
		int operId = Integer.parseInt(String.valueOf(args.get("operId")));
		String returnMsg = null;
		LogUtil.printInfoLog(getClass().getName()+"调用防伪信息设置接口", args);
		try{
			dao.doSave( mid, operId, antiPhishingStr);
			returnMsg = "设置成功";
		} catch(Exception e){
			returnMsg = "设置失败";
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doSave", "", e);
		}
		
		return returnMsg;
	}

}
