package com.rongyifu.mms.rmi.service;

import java.util.Map;

import net.sf.json.JSONObject;

import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.service.SysManageService;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 根据id查询系统通知消息接口
 * @author wufei
 *
 */
public class MmsNoticeService implements IRemoteServiceProcessor {

	@Override
	public Object doRequest(Map<String, Object> params) {
		LogUtil.printInfoLog(params);
		String id = (String) params.get("id");
		
		if(Ryt.empty(id))
			return null;
		
		MMSNotice mmsNotice = new SysManageService().getMessageById(id);
		JSONObject pageObj = new JSONObject();
		pageObj.put("mmsNotice", mmsNotice);
		return pageObj;
	}

}
