package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("serial")
public class Invoker extends HttpServlet {

	private Map<String, String> p;

	/**
	 * Constructor of the object.
	 */
	public Invoker() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
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
	 * The doPost method of the servlet. <br>
	 * This method is called when a form has its tag value method equals to post.
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		if(request.getParameterMap().size() == 0){
			response.getWriter().print("<H1>This is MMS!</H1>");
			return;
		}
		
		String params = genParams(request);
		LogUtil.printInfoLog("Invoker", "doPost", "IP:" + request.getRemoteAddr() + " | params:" + params);
		p.put("ip", request.getRemoteAddr());//添加请求IP
		PrintWriter out = response.getWriter();
		String res = EWPService.call(p);
		out.print(res);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		p = new HashMap<String, String>();
	}

	/**
	 * 把request对象里边的所有参数放在p里边
	 */
	private String genParams(HttpServletRequest request) {
		Enumeration<?> enumeration = request.getParameterNames();
		StringBuffer ps = new StringBuffer();
		while (enumeration.hasMoreElements()) {
			String k = (String) enumeration.nextElement();
			String v = request.getParameter(k);
			if(null!=k && !k.isEmpty()){
				p.put(k,v);
				ps.append("&"+k+"="+v);
			} 
		}
		
		return ps.toString();
	}

}