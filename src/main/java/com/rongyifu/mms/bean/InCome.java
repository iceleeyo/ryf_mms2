package com.rongyifu.mms.bean;

public class InCome {
	
	private Integer liqDate;
	private String mid;
//	private Integer gate;
//	private Integer type;
	private Long amount;
	private Long refAmt;
	private Integer income;
	private Integer feeAmt;
	private Integer bankFee;
//	private Integer liqType;
	private Integer refFee;
	private Integer BkRefFee;
	
	
	private String batch;
	
	
	
	
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public Integer getLiqDate() {
		return liqDate;
	}
	public void setLiqDate(Integer liqDate) {
		this.liqDate = liqDate;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(Long refAmt) {
		this.refAmt = refAmt;
	}
	public Integer getIncome() {
		return income;
	}
	public void setIncome(Integer income) {
		this.income = income;
	}
	public Integer getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Integer getBankFee() {
		return bankFee;
	}
	public void setBankFee(Integer bankFee) {
		this.bankFee = bankFee;
	}
	public Integer getRefFee() {
		return refFee;
	}
	public void setRefFee(Integer refFee) {
		this.refFee = refFee;
	}
	public Integer getBkRefFee() {
		return BkRefFee;
	}
	public void setBkRefFee(Integer bkRefFee) {
		BkRefFee = bkRefFee;
	}

	
	
}
