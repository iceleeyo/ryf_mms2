package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据商户号查询商户重要信息接口
 * @author wufei
 *
 */
public class QueryImportentMsgByMidService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		
		Minfo minfo = new MerInfoDao().getImportentMsgByMid(mid);
		JSONObject pageObj = new JSONObject();
		pageObj.put("minfo", minfo);
		return pageObj;
	}

}
