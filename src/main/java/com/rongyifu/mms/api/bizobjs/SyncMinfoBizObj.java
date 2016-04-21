package com.rongyifu.mms.api.bizobjs;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.datasync.MerInfoAlterSync;

public class SyncMinfoBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String mid = params.get("mid");
		if(StringUtils.isBlank(mid)){
			throw new Exception("商户号不能为空");
		}
		return new MerInfoAlterSync().syncMinfo2(mid);
	}

}
