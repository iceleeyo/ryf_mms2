package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 商户数字证书导入接口
 * @author wufei
 *
 */
public class AddMerRsaFileService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String mid = (String) params.get("mid");
		String rsaFile = (String) params.get("rsaFile");
		
		String resultCode = new MerchantService().addMerRsaFile(mid, rsaFile);
		JSONObject pageObj = new JSONObject();
		pageObj.put("resultCode", resultCode);
		return pageObj;
	}

}
