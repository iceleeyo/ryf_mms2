package com.rongyifu.mms.bean;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
public class QkRisk {

	// Fields
	private String mid;
	
	//新增信用卡快捷支付用户风控
	private Integer singleLimit; 
	private Integer dayLimit; 
	private Integer daySuccCount; 
	private Integer dayFailCount; 
	private String cardType; 
	
	
		

	

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Integer getSingleLimit() {
		return singleLimit;
	}

	public void setSingleLimit(Integer singleLimit) {
		this.singleLimit = singleLimit;
	}

	public Integer getDayLimit() {
		return dayLimit;
	}

	public void setDayLimit(Integer dayLimit) {
		this.dayLimit = dayLimit;
	}

	public Integer getDaySuccCount() {
		return daySuccCount;
	}

	public void setDaySuccCount(Integer daySuccCount) {
		this.daySuccCount = daySuccCount;
	}

	public Integer getDayFailCount() {
		return dayFailCount;
	}

	public void setDayFailCount(Integer dayFailCount) {
		this.dayFailCount = dayFailCount;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

//	/* 
//	 * 通过该方法
//	 */
//	@Override
//	public String toString() { 
//		JsonConfig config = new JsonConfig();  
//		DefaultValueProcessor dvp = new DefaultValueProcessor(){
//			@Override
//			public Object getDefaultValue(Class type) {
//				return "";
//			}
//		};
//	    config.registerDefaultValueProcessor(Integer.class, dvp); 
//	    config.registerDefaultValueProcessor(Short.class, dvp);
//	    config.registerDefaultValueProcessor(Long.class, dvp);
//	    config.registerDefaultValueProcessor(String.class, dvp);
//	    config.registerDefaultValueProcessor(Byte.class, dvp);
//	    config.registerDefaultValueProcessor(Character.class, dvp);
//	    config.registerDefaultValueProcessor(Float.class, dvp);
//	    config.registerDefaultValueProcessor(Double.class, dvp);
//		return JSONObject.fromObject(this,config).toString();
//	}
	
}