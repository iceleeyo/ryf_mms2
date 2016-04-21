package com.rongyifu.mms.bean;

public class MerAccount {
	private String mid;//商户号
	private Short category;//商户类别
	private Long previousBalance;//上期账户余额;
	private Integer beginTrantDate;//起始交易日期
	private Integer endTrantDate;//结束交易日期
	private Long transAmt;//交易金额
	private Integer feeAmt;//系统手续费；
	private Long refAmt;//退款金额；
	private Integer refFeeAmt;//退回手续费
	private Long liqAmt;//结算金额
	private Long manualAdd;//调增
	private Long manualSub;//调减
	private Long currentBalance;//本期账户余额;
	private Integer rec_pay;//收支标识
	private String tbName;
	private long balance;
	private String all_balance;
	private String abbrev;
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Short getCategory() {
		return category;
	}
	public void setCategory(Short category) {
		this.category = category;
	}
	public Long getPreviousBalance() {
		return previousBalance;
	}
	public void setPreviousBalance(Long previousBalance) {
		this.previousBalance = previousBalance;
	}
	public Integer getBeginTrantDate() {
		return beginTrantDate;
	}
	public void setBeginTrantDate(Integer beginTrantDate) {
		this.beginTrantDate = beginTrantDate;
	}
	public Integer getEndTrantDate() {
		return endTrantDate;
	}
	public void setEndTrantDate(Integer endTrantDate) {
		this.endTrantDate = endTrantDate;
	}
	public Long getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(Long transAmt) {
		this.transAmt = transAmt;
	}
	public Integer getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Long getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(Long refAmt) {
		this.refAmt = refAmt;
	}
	public Integer getRefFeeAmt() {
		return refFeeAmt;
	}
	public void setRefFeeAmt(Integer refFeeAmt) {
		this.refFeeAmt = refFeeAmt;
	}
	public Long getLiqAmt() {
		return liqAmt;
	}
	public void setLiqAmt(Long liqAmt) {
		this.liqAmt = liqAmt;
	}
	public Long getManualAdd() {
		return manualAdd;
	}
	public void setManualAdd(Long manualAdd) {
		this.manualAdd = manualAdd;
	}
	public Long getManualSub() {
		return manualSub;
	}
	public void setManualSub(Long manualSub) {
		this.manualSub = manualSub;
	}
	public Long getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(Long currentBalance) {
		this.currentBalance = currentBalance;
	}
	public Integer getRec_pay() {
		return rec_pay;
	}
	public void setRec_pay(Integer rec_pay) {
		this.rec_pay = rec_pay;
	}
	public String getTbName() {
		return tbName;
	}
	public void setTbName(String tbName) {
		this.tbName = tbName;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public String getAll_balance() {
		return all_balance;
	}
	public void setAll_balance(String all_balance) {
		this.all_balance = all_balance;
	}
	public String getAbbrev() {
		return abbrev;
	}
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
	
	
}
