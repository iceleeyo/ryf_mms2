package com.rongyifu.mms.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.logicalcobwebs.proxool.ProxoolFacade;

//import com.rongyifu.mms.bank.citic.CiticLoginService;
//import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("serial")
public class ProxoolServlet extends HttpServlet {

	public void init() throws ServletException {
//		System.out.println("init mms ProxoolServlet......");
//		new PubDao().getConnection();
//		try {
//			CiticLoginService.getInstance().login();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void destroy() {
		ProxoolFacade.shutdown();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
