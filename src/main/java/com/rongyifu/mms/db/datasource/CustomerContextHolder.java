package com.rongyifu.mms.db.datasource;

import org.springframework.util.Assert;

public class CustomerContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	
	public static final String MASTER = "MASTER";
	
	public static final String SLAVE = "SLAVE";
	
	/**
	 * 设置主从库
	 * @param customerType
	 */
	public static void setCustomerType(String customerType) {
		Assert.notNull(customerType, "customerType cannot be null");
		contextHolder.set(customerType);
	}
	
	/**
	 * 设置主库
	 */
	public static void setMaster(){
		setCustomerType(MASTER);
	}
	
	/**
	 * 设置从库
	 */
	public static void setSlave(){
		setCustomerType(SLAVE);
	}
	
	public static String getCustomerType() {
		return (String) contextHolder.get();
	}

	public static void clearCustomerType() {
		contextHolder.remove();
	}
}
