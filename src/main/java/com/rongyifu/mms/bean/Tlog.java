package com.rongyifu.mms.bean;

public class Tlog {
	
	private String tseq;
	private String mid;
	private String model;
	private Integer gate;
	private Integer payAmt;
	private Integer amount;
	private String p1;//中信POS流水
	private String p2;//中信批次号
	private String p7;//中信授权号
	private String pay_count;
	private String againPay_status;
	
	
	
	public String getPay_count() {
		return pay_count;
	}
	public void setPay_count(String pay_count) {
		this.pay_count = pay_count;
	}
	public String getAgainPay_status() {
		return againPay_status;
	}
	public void setAgainPay_status(String againPay_status) {
		this.againPay_status = againPay_status;
	}
	public String getP7() {
		return p7;
	}
	public void setP7(String p7) {
		this.p7 = p7;
	}
	public String getP1() {
		return p1;
	}
	public void setP1(String p1) {
		this.p1 = p1;
	}
	public String getP2() {
		return p2;
	}
	public void setP2(String p2) {
		this.p2 = p2;
	}
	public Integer getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(Integer payAmt) {
		this.payAmt = payAmt;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}

}
