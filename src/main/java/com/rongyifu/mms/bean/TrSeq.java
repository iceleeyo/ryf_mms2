package com.rongyifu.mms.bean;

public class TrSeq {
	private Long id;
	private String objId;
	private Integer trDate;
	private Integer trTime;
	private Integer trAmt;
	private Integer trType;
	private String trFlag;
	private Integer liqDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	public Integer getTrDate() {
		return trDate;
	}
	public void setTrDate(Integer trDate) {
		this.trDate = trDate;
	}
	public Integer getTrTime() {
		return trTime;
	}
	public void setTrTime(Integer trTime) {
		this.trTime = trTime;
	}
	public Integer getTrAmt() {
		return trAmt;
	}
	public void setTrAmt(Integer trAmt) {
		this.trAmt = trAmt;
	}
	public Integer getTrType() {
		return trType;
	}
	public void setTrType(Integer trType) {
		this.trType = trType;
	}
	public String getTrFlag() {
		return trFlag;
	}
	public void setTrFlag(String trFlag) {
		this.trFlag = trFlag;
	}
	public Integer getLiqDate() {
		return liqDate;
	}
	public void setLiqDate(Integer liqDate) {
		this.liqDate = liqDate;
	}
	
}
