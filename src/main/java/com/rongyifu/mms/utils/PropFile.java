package com.rongyifu.mms.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取工具类
 * 
 * @author yuan.zhiyong
 *
 */
public class PropFile {
	private static Properties props = null;

	private PropFile() {

	}

	public static synchronized Properties getProps(String propFile) throws Exception {
		if (props == null) {
			props = new Properties();
			InputStream in = null;
			try {
				// in = new FileInputStream(propFile);
				in = PropFile.class.getResourceAsStream(propFile);
				props.load(in);
				return props;
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} else {
			return props;
		}
	}

}
