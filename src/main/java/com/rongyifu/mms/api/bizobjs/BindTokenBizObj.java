package com.rongyifu.mms.api.bizobjs;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.DynamicToken;
import com.rongyifu.mms.modules.merchant.SystemType;
import com.rongyifu.mms.modules.merchant.dao.DynamicTokenDao;
import com.rongyifu.mms.utils.DynamicCodeUtils;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 绑定动态令牌接口
 */
public class BindTokenBizObj implements BizObj {
	
	private DynamicTokenDao dao = new DynamicTokenDao();

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
		String tokenSn = params.get("tokenSn");//动态令牌序列号
		if (StringUtils.isBlank(tokenSn)) throw new Exception("动态令牌不能为空");
		if (!dao.isRegistered(tokenSn)) {
			String userName = generateUserName(tokenSn);//用于和动态令牌绑定的用户名
			String system = params.get("system");//系统标识
//			if (StringUtils.isBlank(userName)) throw new Exception("用户名不能为空");
			if (StringUtils.isBlank(system)) throw new Exception("系统标识不能为空");
			SystemType systemType = null;
			try {
				systemType = SystemType.getByCode(Integer.valueOf(system));
			} catch (Exception e) {
				throw new Exception("未知的系统标识");
			}
			if (!DynamicCodeUtils.tokenBind(userName, tokenSn)) 
				throw new Exception("绑定动态令牌失败");
			DynamicToken token = new DynamicToken();
			token.setTokenSn(tokenSn);
			token.setUserName(userName);
			token.setSystem(systemType.getCode());
			if (1 != dao.addToken(token)) {
				LogUtil.printErrorLog(getClass().getCanonicalName(), "doBiz", "动态令牌绑定成功,但融易付系统存档失败");
				throw new Exception("动态令牌绑定成功,但融易付系统存档失败");
			}
		}
		return "动态令牌绑定成功";
	}
	
	/**
	 * 生成账户名
	 * @param tokenSn
	 * @return
	 */
	private String generateUserName(String tokenSn){
		return "SYS_" + tokenSn;
	}
}
