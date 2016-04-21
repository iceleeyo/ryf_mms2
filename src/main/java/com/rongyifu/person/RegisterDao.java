package com.rongyifu.person;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.EmaySMS;

@SuppressWarnings("unchecked")
public class RegisterDao extends PubDao{
	WebContext webContext=WebContextFactory.get();
	HttpSession session =webContext.getSession();
	public void doPhonePay(String phoneNo){
		
		String r = String.valueOf(new Random().nextInt(1000000));
		DecimalFormat ft=new DecimalFormat("000000"); 
		String yzm=ft.format(Integer.parseInt(r));
//		System.out.println(yzm);
		session.setAttribute("yzm", yzm);
		EmaySMS.sendSMS(new String[]{phoneNo}, "您申请本手机号码作为您的融易通账户名，验证码为："+yzm+"。如非本人操作，请联系010-888888");
//		SMSUtil.send(phoneNo, "您申请本手机号码作为您的融易通账户名，验证码为："+yzm+"。如非本人操作，请联系010-888888");
		
		
	}
	
	public int checkTelIn(String phoneNo){
		String sql="select count(*) from per_infos where uid='"+phoneNo;
		sql+="'";
		return queryForInt(sql);
	}
	public void registerSuccess(String loginPwd,String payPwd,String name,Integer sex,String idNo,Integer tel){
		String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date());
		String uid=(String)session.getAttribute("tel");
		StringBuffer sql=new StringBuffer("insert into per_infos set uid='");
		sql.append(uid).append("',login_pwd='").append(loginPwd).append("', pay_pwd='").append(payPwd).append("', name='");
		sql.append(name).append("', gender=").append(sex).append(", id_no='").append(idNo);
		sql.append("', tel=").append(tel).append(",sys_date=").append(date);
		update(sql.toString());
		
	}


}
