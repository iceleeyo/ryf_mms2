package com.rongyifu.mms.api.bizobjs;
import java.util.Map;
import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.dao.MerOperDao;
//获取mmsip接口
public class GetIpBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		return ParamCache.getStrParamByName("mmsip");
	}

}
