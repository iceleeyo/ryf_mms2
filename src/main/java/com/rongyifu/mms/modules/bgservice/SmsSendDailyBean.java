package com.rongyifu.mms.modules.bgservice;

public class SmsSendDailyBean {
	private Integer type;//产品类型
	private Integer successConut;//成功笔数
	private Integer count;//总笔数
	private String sumSuccessAmount;//成功总金额
	private Double successRate;//成功率；
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSuccessConut() {
		return successConut;
	}
	public void setSuccessConut(Integer successConut) {
		this.successConut = successConut;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getSumSuccessAmount() {
		return sumSuccessAmount;
	}
	public void setSumSuccessAmount(String sumSuccessAmount) {
		this.sumSuccessAmount = sumSuccessAmount;
	}
	public Double getSuccessRate() {
		return successRate;
	}
	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}


}
