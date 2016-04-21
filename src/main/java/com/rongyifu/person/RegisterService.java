package com.rongyifu.person;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.exception.RytException;

public class RegisterService {
	
	private RegisterDao registerDao=new RegisterDao();
	WebContext webContext=WebContextFactory.get();
	HttpSession session =webContext.getSession();
/*	public String doPhonePay(String phoneNo) {
		String msg = "";
		if (Ryt.empty(phoneNo)) {
			return "手机号不能为空";
		}
		if (Ryt.empty(phoneNo) || phoneNo.trim().length() != 11) {
			return "手机号格式不正确";
		}
		if (!(phoneNo.startsWith("13") || phoneNo.startsWith("15") || phoneNo.startsWith("18"))) {
			return "手机号格式不正确";
		}
		try {
			msg = registerDao.doPhonePay(phoneNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "发送失败";
		}
		if (msg.toLowerCase().contains("success")) {
			return "ok";
		} else {
			return "发送失败";
		}
	}
*/	
	
	public void confirm(String txtMobile,String txtCheckcode){
		Util utilThis = new Util(webContext.getScriptSession());
		ScriptBuffer sbuff=new ScriptBuffer();
		try {
			if (Ryt.empty(txtCheckcode)) throw new RytException("请输入验证码");
			if (!txtCheckcode.matches("[0-9]{4}")) throw new RytException("验证码是四位数字");
			String rand = (String) session.getAttribute("rand");
			if (null == rand) throw new RytException("验证码失效");
			if(!rand.equals(txtCheckcode)) throw new RytException("验证码错误");
			if(registerDao.getJdbcTemplate().queryForInt("select count(*) from per_infos where uid="+txtMobile)>0)throw new RytException("该手机号已经被注册！");
			session.removeAttribute("rand");
			session.removeAttribute("tel");
			session.setAttribute("tel", txtMobile);
			sbuff.appendScript("window.location.href='register_mobile.jsp'");
			utilThis.addScript(sbuff);
			doPhonePay(txtMobile);
		 } catch (Exception e) {
			utilThis.setValue("check_txtCheckcode", e.getMessage());
			utilThis.addFunctionCall("flushImg");
			utilThis.setValue("txtCheckcode", "");
		}
	}
	private void doPhonePay(String txtMobile) {
		Util utilThis2 = new Util(webContext.getScriptSession());
		try {
		if (Ryt.empty(txtMobile)) {
			throw new RytException("手机号不能为空");
		}
		if (Ryt.empty(txtMobile) || txtMobile.trim().length() != 11) {
			throw new RytException( "手机号格式不正确");
		}
		if (!(txtMobile.startsWith("13") || txtMobile.startsWith("15") || txtMobile.startsWith("18"))) {
			throw new RytException( "手机号格式不正确");
		}
		 registerDao.doPhonePay(txtMobile);
		} catch (Exception e) {
			utilThis2.setValue("check_txtmobile", e.getMessage());
			utilThis2.addFunctionCall("flushImg");
			utilThis2.setValue("check_txtmobile", "");	
		
		}
	
	}
	
	public String checkTelIn(String txtMobile){
		String msg="";
		if(registerDao.checkTelIn(txtMobile)>0)msg="have";
		else msg="ok";
		return msg;
	}
	
	public void registerSuccess(String loginPwd,String payPwd,String name,Integer sex,String idNo,Integer tel){
		Util utilThis = new Util(webContext.getScriptSession());
		ScriptBuffer sbuff=new ScriptBuffer();
		registerDao.registerSuccess(loginPwd,payPwd,name,sex,idNo,tel);
		sbuff.appendScript("window.location.href='register_mobileInfo.jsp'");
		utilThis.addScript(sbuff);
	}
}
