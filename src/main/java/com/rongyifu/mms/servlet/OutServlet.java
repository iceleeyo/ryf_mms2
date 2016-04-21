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

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rongyifu.mms.api.BizMapper;
import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.utils.LogUtil;

public class OutServlet extends HttpServlet {
	
    private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 6627585273076757774L;

	/**
	 * Constructor of the object.
	 */
	public OutServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * 外部接口统一入口,通过bizCode映射响应的业务实现类 命名：外部：TCO001 内部：TCI001..
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Map<String,String> params = genParams(request);
		LogUtil.printInfoLog("OutServlet", "doPost", "请求融易付对外接口", params);
		OutputStream out = null;
		String outStr = "";
		String transCode = "";
		try {
			JSONObject json = new JSONObject();
			try {
				transCode = params.get("transCode");
				if(StringUtils.isBlank(transCode)){
					throw new Exception("交易码不能为空");
				}else if(!transCode.startsWith("TCO")){
					throw new Exception("错误的交易码");
				}
				String bizClassName = BizMapper.getBizClassName(transCode);
				if(StringUtils.isBlank(bizClassName))
					throw new Exception("未知的交易码: "+transCode);
				Class<?> clazz = null;
				try {
					clazz = Class.forName(bizClassName);
				} catch (Exception e) {
					throw new Exception("未知的交易码: "+transCode);
				}
				if(null == clazz || !BizObj.class.isAssignableFrom(clazz))
					throw new Exception("错误的业务配置: "+transCode);
				BizObj bizObj = (BizObj) clazz.newInstance();
				//调用业务代码
				Object rslt = bizObj.doBiz(params);
				if(null != rslt){
					json.put("result", rslt);
				}
				json.put("status", RequestStatus.SUCESS.code);
				json.put("msg", RequestStatus.SUCESS.desc);
			}catch (Exception e) {
				json.put("status", RequestStatus.FAIL.code);
				String msg = e.getMessage();
				msg = StringUtils.isEmpty(msg)?RequestStatus.FAIL.desc:msg;
				json.put("msg", msg);
				logger.info(msg, e);
			}
			json.putAll(params);
			outStr = json.toString();
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
		String ip = request.getRemoteAddr();
		p.put("ip", ip);
		return p;
	}
	
	private enum RequestStatus{
		SUCESS(0,"OK"),
		
		FAIL(1,"FAIL");
		
		RequestStatus(int code,String desc){
			this.code = code;
			this.desc = desc;
		}
		
		int code;
		String desc;
	}

}
