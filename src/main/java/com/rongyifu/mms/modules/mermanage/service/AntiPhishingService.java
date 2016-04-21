package com.rongyifu.mms.modules.mermanage.service;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContextFactory;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.modules.mermanage.dao.AntiPhishingDao;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.web.WebConstants;

/**
 * 防伪信息设置 
 *
 */
public class AntiPhishingService {
	
	public String doSave(String antiPhishingStr){
		HttpSession session = WebContextFactory.get().getSession();
		LoginUser loginUser = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		String mid = loginUser.getMid();
		Integer operId = loginUser.getOperId();
		
		AntiPhishingDao dao = new AntiPhishingDao();
		String returnMsg = null;
		try{
			dao.doSave(mid, operId, antiPhishingStr);
			returnMsg = "设置成功";
			// 更新session中的防伪信息
			loginUser.setAntiPhishingStr(antiPhishingStr);
		} catch(Exception e){
			returnMsg = "设置失败";
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doSave", "", e);
		}
		
		// 记录日志
		String logInfo = "[商户号： "+mid+" 操作员： "+operId+" 防伪信息："+antiPhishingStr + "]";
		dao.saveOperLog("防伪信息设置", logInfo + " " + returnMsg);
		
		return returnMsg;
	}
}
