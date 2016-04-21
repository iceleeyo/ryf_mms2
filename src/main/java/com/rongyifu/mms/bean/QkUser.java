package com.rongyifu.mms.bean;

import java.util.Date;

//快捷支付用户表
public class QkUser {
	private String userId; 
	private String mid; 
	
	private Date regiTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Date getRegiTime() {
		return regiTime;
	}

	public void setRegiTime(Date regiTime) {
		this.regiTime = regiTime;
	}
	
}