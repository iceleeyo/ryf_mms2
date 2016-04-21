package com.rongyifu.mms.bean;


public class BatchB2B {
	private String batchNumber;
	private double orderAmt;//订单金额
	private double feeAmt;//手续费
	private double allrAmt;//付款总金额
	private int count;//交易笔数
	private String orderDescribe;
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getOrderDescribe() {
		return orderDescribe;
	}
	public void setOrderDescribe(String orderDescribe) {
		this.orderDescribe = orderDescribe;
	}
	public double getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(double orderAmt) {
		this.orderAmt = orderAmt;
	}
	public double getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(double feeAmt) {
		this.feeAmt = feeAmt;
	}
	public double getAllrAmt() {
		return allrAmt;
	}
	public void setAllrAmt(double allrAmt) {
		this.allrAmt = allrAmt;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
