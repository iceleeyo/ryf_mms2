package com.rongyifu.mms.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

public class LoginCheck {
	
	/**
	 * 检查登陆合法性
	 */
	public static LoginUser checkLegality(HttpSession session){
		LoginUser loginUser = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		if(loginUser == null)
			return null;
		
		// 检查是否已修改密码
		if(loginUser.isUpdatePwd()){
			printLog(loginUser);
			removeSession(session);
			return null;
		}
		// 检查是否已验证动态令牌
		if(loginUser.isTokenActive()){ // 动态令牌是否启用
			if(!loginUser.isTokenVerifySuccess()){ // 动态令牌是否验证成功
				printLog(loginUser);
				removeSession(session);
				return null;
			}
		}
		
		return loginUser;
	}
	/**
	 * 清除session
	 * @param session
	 */
	private static void removeSession(HttpSession session){
		session.removeAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		session.removeAttribute(WebConstants.SESSION_LOGGED_ON_MID);
	}
	
	private static void printLog(LoginUser loginUser){
		Map<String, String> map = LogUtil.createParamsMap();
		map.put("mid", loginUser.getMid());
		map.put("operId", String.valueOf(loginUser.getOperId()));
		map.put("isUpdatePwd", String.valueOf(loginUser.isUpdatePwd()));
		map.put("isTokenActive", String.valueOf(loginUser.isTokenActive()));
		map.put("isTokenVerifySuccess", String.valueOf(loginUser.isTokenVerifySuccess()));
		LogUtil.printInfoLog("checkLegality", map);
	}
	
	/**
	 * 获取商户后台地址
	 * @return
	 */
	public static String getMbsUrl(){
		String mbsUrl = ParamCache.getStrParamByName("MBS_LOGIN_URL");
		if(Ryt.empty(mbsUrl))
			return null;
		else
			return mbsUrl.trim();
	}
}
