package com.rongyifu.mms.bean;

public class DateBlLog {
	private Integer id;
	private String objId;
	private Integer objType;
	private String name;
	private Long bfBl;
	private Long  merFee;
	private Long balance;
	private Integer liqDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	public Integer getObjType() {
		return objType;
	}
	public void setObjType(Integer objType) {
		this.objType = objType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBfBl() {
		return bfBl;
	}
	public void setBfBl(Long bfBl) {
		this.bfBl = bfBl;
	}
	public Long getMerFee() {
		return merFee;
	}
	public void setMerFee(Long merFee) {
		this.merFee = merFee;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Integer getLiqDate() {
		return liqDate;
	}
	public void setLiqDate(Integer liqDate) {
		this.liqDate = liqDate;
	}
	
}
