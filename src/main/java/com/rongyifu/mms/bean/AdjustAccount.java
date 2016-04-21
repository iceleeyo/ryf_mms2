package com.rongyifu.mms.bean;

/**
 * @author xiaomin
 *
 */
public class AdjustAccount {

	private Integer id;
	private String mid;
	private Integer submitOperid;
	private Integer type;
	private Integer submitDate;
	private Integer submitTime;
	private Long account;
	private Integer auditDate;
	private Integer auditTime;
	private Integer state;
	private Integer auditOperid;
	private String  reason;
	private String batch;
	
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getSubmitOperid() {
		return submitOperid;
	}
	public void setSubmitOperid(Integer submitOperid) {
		this.submitOperid = submitOperid;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Integer submitDate) {
		this.submitDate = submitDate;
	}
	public Integer getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Integer submitTime) {
		this.submitTime = submitTime;
	}
	public Long getAccount() {
		return account;
	}
	public void setAccount(Long account) {
		this.account = account;
	}
	public Integer getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Integer auditDate) {
		this.auditDate = auditDate;
	}
	public Integer getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Integer auditTime) {
		this.auditTime = auditTime;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getAuditOperid() {
		return auditOperid;
	}
	public void setAuditOperid(Integer auditOperid) {
		this.auditOperid = auditOperid;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
