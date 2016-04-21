package com.rongyifu.mms.bean;

public class MinfoNoticeSync {
	private Integer id;
	private String mid; // 商户号
	private String name; //商户名称
	private String batch; // 批次號  
	private Integer liqDate; // 結算時間
	private String syncState; // 0成功 1 失败  2处理中
	private String reason;
	
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	public String getSyncState() {
		return syncState;
	}
	public void setSyncState(String syncState) {
		this.syncState = syncState;
	}
	
	

}
