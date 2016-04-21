package com.rongyifu.mms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.sms.SmsProcessor;
import com.rongyifu.mms.utils.EmaySMS;
import com.rongyifu.mms.utils.MD5;

public class SendSMS extends HttpServlet {
	
	private static final long serialVersionUID = -3351707832315031565L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out=response.getWriter();
		String phone = request.getParameter("phoneNo");
		String sign = request.getParameter("sign");
		String content = SmsProcessor.processContent(request);
		String rdno = request.getParameter("no");
		
		if (Ryt.empty(phone) || Ryt.empty(content) || Ryt.empty(rdno)) {
			out.print("1");
			return;
		}
		if (!verify(phone, content, sign, rdno)) {
			out.print("1");
			return;
		}

		int flag=EmaySMS.sendSMS(new String[] {phone}, content);
		out.print(flag);
	}

	private boolean verify(String phone, String content, String sign, String no) {
		// 组织验证数据
		String verify = no + phone;
		String md5Str = MD5.getMD5(verify.getBytes());
		if (md5Str.equals(sign)) {
			return true;
		}
		return false;
	}

}
