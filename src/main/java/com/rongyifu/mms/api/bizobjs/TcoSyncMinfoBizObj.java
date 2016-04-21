package com.rongyifu.mms.api.bizobjs;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.datasync.MerInfoAlterSync;

public class TcoSyncMinfoBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String mid = params.get("mid");
		if(StringUtils.isBlank(mid)){
			throw new Exception("商户号不能为空");
		}
		
		/**
		 * 1：同步给pos管理平台（默认）
		 * 2：同步给清算系统
		 * 3：同步给账户系统/P2P系统
		 * 组合同步示例：
		 * 	12：同步给pos管理平台、清算系统
		 * 	123：全部都同步
		 */
		String syncType = params.get("syncType");
		if(StringUtils.isBlank(syncType) || "123".indexOf(syncType) == -1)
			syncType = "1";
		
		return new MerInfoAlterSync().syncMinfo(mid, syncType);
	}

}
