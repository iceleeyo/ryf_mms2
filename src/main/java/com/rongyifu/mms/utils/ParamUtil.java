package com.rongyifu.mms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ParamUtil {

	private static final String DefFileName = "param.properties";

	private static Properties Defproperties = null;

	static {
		Defproperties = initProperties();
	}
	
	/**
	 * 读取配置文件
	 * @param paramName 参数名称
	 * @return 返回参数的值
	 */
	public static synchronized String getPropertie(String paramName) {
		return Defproperties.getProperty(paramName);
	}
	/**
	 * 读取配置文件
	 * @param paramName 参数名称，fileName文件名称
	 * @return 参数的值
	 */
	public static synchronized String getProperties(String paramName, String fileName) {
		try {
			// 定义一个properties文件的名字
			// 定义一个properties对象
			Properties properties = new Properties();
			// 读取properties
			InputStream file = ParamUtil.class.getClassLoader().getResourceAsStream(fileName);
			// 加载properties文件
			properties.load(file);
			// 读取properties中的某一项
			return properties.getProperty(paramName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取配置文件
	 * @return
	 */
	private static synchronized Properties initProperties() {

		try {
			// 定义一个properties文件的名字
			// 定义一个properties对象
			Properties properties = new Properties();
			// 读取properties
			InputStream file = ParamUtil.class.getClassLoader().getResourceAsStream(DefFileName);
			// 加载properties文件
			properties.load(file);

			return properties;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}


}