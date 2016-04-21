package com.rongyifu.mms.bean;

public class TradeStatistics {
	
	private Integer sysDate;
	private Short transMode;
	private Integer gate;
	private String gateName;
	private Short tstat;
	private Integer gid;
	private String mid;
	private Integer count;
	private Long amount;
	private Long payAmt;
	private Long feeAmt;
	private Long bankAmt;
	private Integer successCount;
	private Integer totalCount;
	private Long successAmt;
	private String name;//商户名
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
	public Short getTransMode() {
		return transMode;
	}
	public void setTransMode(Short transMode) {
		this.transMode = transMode;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public String getGateName() {
		return gateName;
	}
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	public Short getTstat() {
		return tstat;
	}
	public void setTstat(Short tstat) {
		this.tstat = tstat;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getCount() {
		return count==null?0:count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Long getAmount() {
		return amount==null?0:amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getPayAmt() {
		return payAmt==null?0:payAmt;
	}
	public void setPayAmt(Long payAmt) {
		this.payAmt = payAmt;
	}
	public Long getFeeAmt() {
		return feeAmt==null?0:payAmt;
	}
	public void setFeeAmt(Long feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Long getBankAmt() {
		return bankAmt==null?0:payAmt;
	}
	public void setBankAmt(Long bankAmt) {
		this.bankAmt = bankAmt;
	}
	public Integer getSuccessCount() {
		return successCount==null?0:successCount;
	}
	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}
	public Integer getTotalCount() {
		return totalCount==null?0:totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Long getSuccessAmt() {
		return successAmt==null?0:successAmt;
	}
	public void setSuccessAmt(Long successAmt) {
		this.successAmt = successAmt;
	}
}
