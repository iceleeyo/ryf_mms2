package com.rongyifu.mms.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rongyifu.mms.common.RootPath;
import com.rongyifu.mms.utils.LogUtil;

public class GetSuperBankId extends HttpServlet {
	
	private static final long serialVersionUID = -8350767410685411124L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String code="utf-8";
		String path=RootPath.getRootpath().replace("%20", " ")+"superBank/data/superBank.txt";
		File inputFile=new File(path);
		PrintWriter out=resp.getWriter();
		String line=null;
		InputStreamReader inputStreamReader=null;
		BufferedReader buReader=null;
		String type=req.getParameter("type");//类型 1/2/3/4/5/6....
		StringBuffer buffer=null;
		try {
			if(!inputFile.exists()){
				LogUtil.printErrorLog("[" + path + "]资源文件不存在！");
				return;
			}
			inputStreamReader=new InputStreamReader(new FileInputStream(inputFile),code);
			buReader=new BufferedReader(inputStreamReader);
			buffer=new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><banks>");
			while ((line=buReader.readLine())!=null) {
				String[] values=line.split(",");
				/**根据类型选取数据**/
				if(values[2].contains(type)){
					buffer.append("<bank id=\"").append(values[0]).append("\"  abbre=\"").append(values[3]).append("\" >").append(values[1]).append("</bank>");
				}
			}
			
			buffer.append("</banks>");
			out.print(buffer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(buReader!=null)buReader.close();
				if(inputStreamReader!=null)inputStreamReader.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		out.flush();
		out.close();
		
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

}
