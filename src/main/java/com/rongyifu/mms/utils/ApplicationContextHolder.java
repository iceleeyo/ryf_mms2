package com.rongyifu.mms.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

//@Lazy(value=false)
//@Component
/**
 * 用来获取Spring ApplicationContext的工具类
 */
public class ApplicationContextHolder implements ApplicationContextAware {
	private static ApplicationContext ctx;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext(){
		return ctx;
	}
}