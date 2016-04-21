package com.rongyifu.mms.bean;

public class BankQueryBean {
	// 订单状态
	private String orderStatus;
	// 银行流水
	private String bankSeq;
	// 银行日期
	private String bankDate;
	// 银行时间
	private String bankTime;
	// 错误信息
	private String errorMsg;
	// 商户手续费
	private int merFee;
	
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getBankSeq() {
		return bankSeq;
	}

	public void setBankSeq(String bankSeq) {
		this.bankSeq = bankSeq;
	}

	public String getBankDate() {
		return bankDate;
	}

	public void setBankDate(String bankDate) {
		this.bankDate = bankDate;
	}

	public String getBankTime() {
		return bankTime;
	}

	public void setBankTime(String bankTime) {
		this.bankTime = bankTime;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getMerFee() {
		return merFee;
	}

	public void setMerFee(int merFee) {
		this.merFee = merFee;
	}

}
