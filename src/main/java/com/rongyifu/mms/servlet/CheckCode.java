package com.rongyifu.mms.servlet;


import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rongyifu.mms.utils.ValidateCode;

@SuppressWarnings("serial")
public class CheckCode extends HttpServlet{
	  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	        response.setContentType("image/jpeg");
	        response.setHeader("Pragma","No-cache");
	        response.setHeader("Cache-Control","no-cache");
	        response.setDateHeader("Expires", 0);      
	        HttpSession session=request.getSession();
	        ValidateCode vCode = new ValidateCode(60,20,4,100);
	        // 将认证码存入SESSION
			session.setAttribute("rand", vCode.getCode());
			vCode.write(response.getOutputStream());
	    }
	    
	    @Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        processRequest(request, response);
	    }

	  
	    @Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        processRequest(request, response);
	    }

	   
	    @Override
		public String getServletInfo() {
	        return "Short description";
	    }
}
