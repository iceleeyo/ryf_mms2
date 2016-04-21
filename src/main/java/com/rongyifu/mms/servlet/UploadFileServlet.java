package com.rongyifu.mms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongyifu.mms.common.UploadService;
import com.rongyifu.mms.utils.LogUtil;

public class UploadFileServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter prt=response.getWriter();
		String contentType=request.getHeader("content-type");
		String accept=request.getHeader("accept");
		Map<String, String> m=new HashMap<String, String>();
		m.put("content-type", contentType);
		m.put("accept", accept);
		LogUtil.printInfoLog("UploadFileServlet", "doGet", "uploadFile is start!",m);
		//实例化上传相关处理类
		UploadService uploadService=new UploadService();
		
		//上传文件
		String msg=uploadService.dispatch(request);
		prt.println(msg);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
		
}
