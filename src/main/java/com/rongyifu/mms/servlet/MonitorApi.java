package com.rongyifu.mms.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongyifu.mms.modules.sysmanage.service.MonitorConfigService;
import com.rongyifu.mms.utils.LogUtil;

public class MonitorApi extends HttpServlet {
	
	public MonitorApi(){
		super();
	}

	@Override
	public void destroy() {
		
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * This method is called when a form has its tag value method equals to get.
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * 参数：网关号gid，商户号mid
	 * 
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Map<String,String> params = genParams(request);
		OutputStream out = null;
		String outStr;
		try {
			try {
				LogUtil.printInfoLog(getClass().getName(), "doPost", "调用查询监控数据接口", params);
				outStr = new MonitorConfigService().doMonitor(params);
			} catch (Exception e) {
				LogUtil.printErrorLog(getClass().getName(), "doPost", e.getMessage(), params);
				outStr = "ERROR "+e.getMessage();
			}
			LogUtil.printInfoLog(getClass().getName(), "doPost", "响应: "+outStr);
			out = response.getOutputStream();
			out.write(outStr.getBytes("UTF-8"));
			out.flush();
		}finally{
			if (null != out) {
				out.close();
				out = null;
			}
		}
	}
	
	private Map<String,String> genParams(HttpServletRequest request) {
		Map<String,String> p = new HashMap<String,String>();
		Enumeration<?> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String k = (String) enumeration.nextElement();
			String v = request.getParameter(k).trim();
			if(null!=k && !k.isEmpty()){
				p.put(k,v);
			} 
		}
		return p;
	}
}
