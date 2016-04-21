package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ocx.AESWithJCE;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

import com.rongyifu.mms.bean.DynamicToken;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.exception.RytException;
import com.rongyifu.mms.listener.MySessionContext;
import com.rongyifu.mms.modules.merchant.dao.DynamicTokenDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.DynamicCodeUtils;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.ParamUtil;
import com.rongyifu.mms.web.WebConstants;


public class LoginService {
	private MerOperDao merOperDao = new MerOperDao();
	private DynamicTokenDao tokenDao = new DynamicTokenDao();
	WebContext webContext=WebContextFactory.get();
	HttpSession session =webContext.getSession();
	static Logger logger = Logger.getLogger(LoginService.class.getName());
	
	/**
	 * 获取密码MD5值
	 * @param pwd	base64 密码密文数据
	 * @return
	 */
	public String getMd5Pwd(String pwd){
		String mcrypt_key_1=(String)session.getAttribute("mcrypt_key");//获取session中随机因子
		String password=AESWithJCE.getResult(mcrypt_key_1, pwd);//调用解密接口。mcrypt_key_1为获取的32位随机数，password1为密码的密文
		session.removeAttribute("mcrypt_key");//解密后清除session中随机数，防止重放攻击
		
		return MD5.getMD5(password.getBytes());
	}
	
	/**
	 * 管理平台管理员登录
	 * @param uid
	 * @param pwd
	 * @param role
	 * @param ckpwd
	 */
	public void adminLogin(String uid, String pwd1,String role, String ckpwd){
		String pwd = getMd5Pwd(pwd1);
		Util utilThis = new Util(webContext.getScriptSession());
		ScriptBuffer sbuff=new ScriptBuffer();
		HttpServletRequest request = webContext.getHttpServletRequest();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("role", role);
		params.put("remoteAddr", request.getRemoteAddr());
		try {
			String result = doCheckAddr("admin");
			if (("error").equals(result)) {
				sbuff.appendScript("window.location.href='../unaccess_error.jsp'");
			} else {
				String mid = "1";
				LoginUser loginuser = validateLogin(mid, uid, pwd, role, ckpwd);
				LogUtil.printInfoLog("LoginService", "adminLogin", "admin user login", params);
				if (DateUtil.today() - loginuser.getLastUpdat() >= WebConstants.USER_PWD_PERIOD) {
					loginuser.setUpdatePwd(true); // 需要修改密码才能登陆
					sbuff.appendScript("window.location.href='../update_pwd.jsp'");
				} else {
					//判断是否启用动态令牌 若启用跳转动态令牌校验页面
					if(tokenDao.isTokenActive(mid,Integer.valueOf(uid))){
						loginuser.setTokenActive(true); // 需要验证动态令牌才能登陆
						sbuff.appendScript("window.location.href='../validate_token.jsp'");
					}else{
						sbuff.appendScript("window.location.href='A_0_index.jsp'");
					}
				}
			}
		
			utilThis.addScript(sbuff);
		 } catch (Exception e) {
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("LoginService", "adminLogin","admin user login error", params);

			utilThis.setValue("logmsg", e.getMessage());
			utilThis.addFunctionCall("flushImg");
			utilThis.setValue("ckpwd", "");
		}
	}
	
	/**
	 * 校验动态令牌
	 * 成功跳转主页 
	 * 失败注销登录
	 */
	public void validateToken(String dynamicCode){
		Util utilThis = new Util(webContext.getScriptSession());
		try {
			if (null == session || session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER) == null) return;
			LoginUser loginuser = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
			String mid = loginuser.getMid();
			ScriptBuffer sbuff = new ScriptBuffer();
			//动态令牌校验失败
			DynamicToken token = tokenDao.getConfigByUser(mid,loginuser.getOperId());
			if(null == token) throw new Exception("没有找到安全令牌配置信息");
			if(!DynamicCodeUtils.checkDynamicCode(token.getTokenSn(), dynamicCode)){
				//动态令牌校验失败
				//1.注销会话
				try {
					session.invalidate();
				} catch (Exception e) {
				}
				//2.跳转失败页面	
				sbuff.appendScript("window.location.href='unaccess_error.jsp'");
			}else if (!"1".equals(mid)) {
				loginuser.setTokenVerifySuccess(true); // 动态令牌验证成功
				sbuff.appendScript("window.location.href='mer/M_0_index.jsp'");
			} else {
				loginuser.setTokenVerifySuccess(true); // 动态令牌验证成功
				sbuff.appendScript("window.location.href='admin/A_0_index.jsp'");
			}
			utilThis.addScript(sbuff);
		} catch (Exception e) {
			utilThis.setValue("logmsg", e.getMessage());
			try {
				session.invalidate();
			} catch (Exception e1) {
			}
		}
	
	}
	
	/**
	 * 商户管平台理员登录
	 * @param mid
	 * @param uid
	 * @param pwd
	 * @param role
	 * @param ckpwd
	 * @return
	 */
	public String merUserLogin(String mid, String uid, String pwd1, String role, String ckpwd) {
		String pwd = getMd5Pwd(pwd1);
		Util utilThis = new Util(webContext.getScriptSession());
		ScriptBuffer sbuff=new ScriptBuffer();
		HttpServletRequest request = webContext.getHttpServletRequest();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("mid", mid);
		params.put("uid", uid);
		params.put("role", role);
		params.put("remoteAddr", request.getRemoteAddr());
		try {
			LogUtil.printInfoLog("LoginService", "merUserLogin", "merchant user login", params);
				
			LoginUser loginuser=validateLogin(mid,uid, pwd, role, ckpwd);
			if (DateUtil.today() - loginuser.getLastUpdat() >= WebConstants.USER_PWD_PERIOD) {
				loginuser.setUpdatePwd(true); // 需要修改密码才能登陆系统
				sbuff.appendScript("window.location.href='update_pwd.jsp'");
			} else {
				//判断是否启用动态令牌 若启用跳转动态令牌校验页面
				if(tokenDao.isTokenActive(mid,Integer.valueOf(uid))){
					loginuser.setTokenActive(true); // 需要验证动态令牌才能登陆
					sbuff.appendScript("window.location.href='validate_token.jsp'");
				}else{
					sbuff.appendScript("window.location.href='mer/M_0_index.jsp'");
				}
			}
			utilThis.addScript(sbuff);
		} catch (Exception e) {
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("LoginService", "merUserLogin", "merchant user login error", params, e);
			
			utilThis.setValue("logmsg", e.getMessage());
			utilThis.addFunctionCall("flushImg");
			utilThis.setValue("ckpwd", "");
		}

		return null;
	}
	/**
	 * 企业用户登陆
	 * @param cusName
	 * @param ckpwd
	 * @return
	 */
	public String cusUserLogin(String  cusName,String  ckpwd){
		Util utilThis = new Util(webContext.getScriptSession());
		ScriptBuffer sbuff = new ScriptBuffer();
		try {
			validateCusLogin(cusName, ckpwd);
			sbuff.appendScript("window.location.href='cus/index.jsp'");
			utilThis.addScript(sbuff);
		} catch (Exception e) {
			utilThis.setValue("logmsg", e.getMessage());
			utilThis.addFunctionCall("flushImg");
			utilThis.setValue("ckpwd", "");
		}

		return null;
	}
	
	private void validateCusLogin(String cusName, String ckpwd)
			throws Exception {
		if (Ryt.empty(cusName))
			throw new RytException("请输入企业名");
		if (Ryt.empty(ckpwd))
			throw new RytException("请输入验证码");
		if (!ckpwd.matches("[0-9]{4}"))
			throw new RytException("验证码是四位数字");
		// 验证码验证
		String rand = (String) session.getAttribute("rand");
		if (null == rand)
			throw new RytException("验证码失效");
		if (!rand.equals(ckpwd))
			throw new RytException("验证码错误");

		if (session.getAttribute(WebConstants.SESSION_LOGGED_ON_CUS_NAME) != null) {
			session.removeAttribute(WebConstants.SESSION_LOGGED_ON_CUS_NAME);
		}
		session.removeAttribute("rand");

		session.setAttribute(WebConstants.SESSION_LOGGED_ON_CUS_NAME, cusName);
	}
	/**
	 * 修改密码
	 * @param oldPwd
	 * @param newPwd
	 * @param verifyPwd
	 * @return
	 */
	public void updatePwd(String oldPwd, String newPwd, String verifyPwd, String antiPhishingStr) {
		Util utilThis = new Util(webContext.getScriptSession());
		try {
			if (null == session || session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER) == null)
				return;
			LoginUser loginuser = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
			doCheckUpdatePwd(oldPwd, newPwd, verifyPwd, loginuser, antiPhishingStr);
			String mid = loginuser.getMid();
			ScriptBuffer sbuff = new ScriptBuffer();
			if (!"1".equals(mid)) {
				if (!tokenDao.isTokenActive(mid,loginuser.getOperId())) {
					loginuser.setTokenActive(false);
					sbuff.appendScript("window.location.href='mer/M_0_index.jsp'");
				}else{//校验动态令牌
					loginuser.setTokenActive(true);
					sbuff.appendScript("window.location.href='validate_token.jsp'");
				}
			} else {
				if (!tokenDao.isTokenActive(mid,loginuser.getOperId())) {
					loginuser.setTokenActive(false);
					sbuff.appendScript("window.location.href='admin/A_0_index.jsp'");
				}else{//校验动态令牌
					loginuser.setTokenActive(true);
					sbuff.appendScript("window.location.href='validate_token.jsp'");
				}
			}
			utilThis.addScript(sbuff);
			
			loginuser.setUpdatePwd(false);
		} catch (Exception e) {
			utilThis.setValue("logmsg", e.getMessage());
		}
	}
	//登录验证
	private LoginUser validateLogin(String mid ,String  uid,String  md5pwd,String  role,String  ckpwd) throws Exception{
		doLoginCheckInput(mid,uid, md5pwd, role, ckpwd);//检查输入是否完整
		if (session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER) != null) {
			session.removeAttribute(WebConstants.SESSION_LOGGED_ON_USER);
			session.removeAttribute(WebConstants.SESSION_LOGGED_ON_MID);
		}
		session.removeAttribute("rand");
		
		LoginUser loginuser =merOperDao.getLoginedUser(mid, uid);
		//String md5pwd = MD5.getMD5(pwd.getBytes());
		doCheckOper(loginuser,md5pwd);//检查操作员、密码
		
		// 记录登陆时间和登陆IP
		String loginIp = webContext.getHttpServletRequest().getRemoteHost();
		merOperDao.updateUserLoginTime(mid, uid, loginIp);//修改登录时间、登陆IP
		// 记录登陆日志
		merOperDao.saveOperLog(loginuser.getMid(),loginuser.getOperId(),"登录系统", "登录商户为 "+mid+" 操作员为 "+uid+" 登陆成功",webContext.getHttpServletRequest());// 记录日志
		
		// 1.判断该用户是否已经登录如果有登录，则将之前登录的用户踢下线
		MySessionContext.addSession(session,mid+"|"+uid);
		// 2.保存用户信息到session
		session.setAttribute(WebConstants.SESSION_LOGGED_ON_USER, loginuser);
		session.setAttribute(WebConstants.SESSION_LOGGED_ON_MID, mid);
		return loginuser;
	}
	
	//输入完整和正确性验证
	private void doLoginCheckInput(String mid,String uid, String pwd, String role, String ckpwd) throws Exception {
		if (Ryt.empty(mid) || "1".equals(mid)&&role.equals("mer")||!"1".equals(mid)&&role.equals("admin")){
			throw new RytException("非法登录");
		}
		if (Ryt.empty(uid)) throw new RytException("请输入操作员号");
		if (Ryt.empty(pwd)) throw new RytException("请输入密码");
		//if (pwd.trim().length() < 6 || pwd.trim().length() > 15) throw new RytException("密码的长度为6-15位");
		if (Ryt.empty(ckpwd)) throw new RytException("请输入验证码");
		if (!ckpwd.matches("[0-9]{1,8}")) throw new RytException("操作员号只能是数字");
		if (!ckpwd.matches("[0-9]{4}")) throw new RytException("验证码是四位数字");
		//验证码验证
		String rand = (String) session.getAttribute("rand");
		if (null == rand) throw new RytException("验证码失效");
		if(!rand.equals(ckpwd)) throw new RytException("验证码错误");
	}
	
	//操作员验证、密码提示
	private void doCheckOper(LoginUser loginuser,String md5pwd) throws Exception{
		//if (loginuser == null)throw new RytException("操作员不存在");
		if (loginuser == null)throw new RytException("输入的信息有误,请重新输入");
		if (!"1".equals(loginuser.getMid())&&loginuser.getExpDate() < DateUtil.today()){
			throw new RytException("商户已过期");
		}
		if (loginuser.getOperState() == 1)throw new RytException("此操作员号已被关闭");
		if (!loginuser.getMd5Pwd().equals(md5pwd)) {
			int newErrCount = 0 ;
			if(DateUtil.today() == loginuser.getErrTime())
				newErrCount = loginuser.getErrCount() + 1;
			else
				newErrCount = 1;
			if (newErrCount >= WebConstants.ALLOW_ERR_LOGIN_PWD_COUNT) {
				merOperDao.closeOper(newErrCount,loginuser.getMid(), loginuser.getOperId(), loginuser.getMtype());
				merOperDao.saveOperLog(loginuser.getMid(),loginuser.getOperId(),"登录系统", "该操作员已被关闭",webContext.getHttpServletRequest());
				throw new RytException("此操作员号已被关闭，请联系管理员。");
			} else {
				merOperDao.updatErrorCount(newErrCount,loginuser.getMid(), loginuser.getOperId(), loginuser.getMtype());
				merOperDao.saveOperLog(loginuser.getMid(),loginuser.getOperId(),"登录系统", "商户为"+loginuser.getMid()+" 操作员为"+loginuser.getOperId()+" "+"密码错误，还有"+(WebConstants.ALLOW_ERR_LOGIN_PWD_COUNT - newErrCount)+"次可尝试",webContext.getHttpServletRequest());
				//throw new RytException("密码错误，还有"+(WebConstants.ALLOW_ERR_LOGIN_PWD_COUNT - newErrCount)+"次可尝试");
				throw new RytException("输入的信息有误,请重新输入");
			}
		}
	}
	//检查密码（修改密码）
	private void doCheckUpdatePwd(String oldPwd, String newPwd, String verifyPwd,LoginUser loginuser, String antiPhishingStr) throws Exception {
		if (Ryt.empty(oldPwd)) throw new RytException("请输入原密码");
		if (Ryt.empty(newPwd)) throw new RytException("请输入新密码");
		if (Ryt.empty(verifyPwd)) throw new RytException("请确认新密码");
		//if (opwd.trim().length() < 6 || opwd.trim().length() > 15) throw new RytException("原密码的长度为6-15位");
		//if (npwd.trim().length() < 6 || npwd.trim().length() > 15) throw new RytException("新密码的长度为6-15位");
		if (oldPwd.equals(newPwd)) throw new RytException("新密码不能和原密码一致");
		if (!newPwd.equals(verifyPwd)) throw new RytException("两次密码不一致");
		//String md5pwd = MD5.getMD5(opwd.getBytes());
		if(!oldPwd.equals(loginuser.getMd5Pwd())) throw new RytException("原密码错误");
		int count=merOperDao.updateOperPwd(newPwd,loginuser.getMid(),loginuser.getOperId(),antiPhishingStr);
		// 更新session中的防伪信息
		loginuser.setAntiPhishingStr(antiPhishingStr);
		if(count!=1) throw new RytException("修改密码失败");
	}
	
	/**
	 * 检查IP
	 * mod admin -> 管理后台 
	 * mod mer -> 商户后台
	 */
	public void doCheckAddr(HttpServletRequest request, String mod)
			throws Exception {
		String ip = null;
		String ips = ParamUtil.getProperties(mod, "accessIpList.properties");
		List<String> listIp = new ArrayList<String>();
		if (listIp.size() == 0) {
			if (ips != null || !("").equals(ips)) {
				for (String ipstr : ips.split(",")) {
					listIp.add(ipstr);
				}
			}
		}
		if (request.getHeader("x-forwarded-for") == null) {
			ip = request.getRemoteAddr();
		} else {
			ip = request.getHeader("x-forwarded-for");
		}
		
		if (ip != null && !checkIP(ip, listIp)) {
			logger.info("此IP[" + ip + "]禁止访问！");
			throw new RytException("禁止访问");
		} else {
			logger.info("访问者IP：" + ip);
		}

	}

	/***
	 * 适用情况 -> 白名单 192.168.21.* check
	 * 
	 * @param Ip
	 * @param list
	 * @return
	 */
	public boolean checkIP(String Ip, List<String> list) {
		if(list.size()<=1 && list.get(0).equals("")){
			return true;
		}
		for (String string : list) {
			if (string.contains("*")) {
				if(Ip.contains(":"))return false;
				String IpNew = Ip.substring(0, Ip.lastIndexOf("."));
				String str = string.substring(0, string.lastIndexOf("."));
				if (IpNew.equals(str))
					return true;
			}
			if (string.equals(Ip))
				return true;
		}
		return false;
	}
	
	
	/**
	 * 检查IP
	 * mod admin -> 管理后台 
	 * mod mer -> 商户后台
	 */
	public String doCheckAddr(String mod)
			throws Exception {
		String ip = null;
		List<String> listIp=new ArrayList<String>();
		String ips = ParamUtil.getProperties(mod, "accessIpList.properties").trim();
			if (ips != null || !("").equals(ips)) {
				for (String ipstr : ips.split(",")) {
					listIp.add(ipstr);
				}
			}
		HttpServletRequest request=webContext.getHttpServletRequest();
		if (request.getHeader("x-forwarded-for") == null) {
			ip = request.getRemoteAddr();
		} else {
			ip = request.getHeader("x-forwarded-for");
		}
		logger.info("访问IP："+ip);
		
		if (ip != null && !checkIP(ip, listIp)) {
			logger.info("此IP[" + ip + "]禁止访问！");
			return "error";
		}
		
		return "success";

	}
}
