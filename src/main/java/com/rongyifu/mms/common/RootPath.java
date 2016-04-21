package com.rongyifu.mms.common;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.WebContextFactory;

public class RootPath {
	private static String rootpath = RootPath.class.getResource("/").getPath();

	public static String getRootpath() {
		return rootpath;
	}

	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	//ContextPath ：项目名或/
	public static String getContextPath(){
		HttpServletRequest  request= WebContextFactory.get().getHttpServletRequest();
		return request.getContextPath();
	}
	//端口
	public static int getServerPort(){
		HttpServletRequest  request= WebContextFactory.get().getHttpServletRequest();
		return request.getServerPort();
	}
	//ip或网址
	public static String getServerName(){
		HttpServletRequest  request= WebContextFactory.get().getHttpServletRequest();
		return request.getServerName();
	}
}
