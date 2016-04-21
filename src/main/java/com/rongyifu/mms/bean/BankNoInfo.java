package com.rongyifu.mms.bean;

public class BankNoInfo {
	
	private int id;
	private String  bkNo;
	private String bkName;
	private String superBkNo;
	private String provId;
	private String cityId;
	private String type;
	private String gid;
	private String superBankName;
	
	public String getSuperBankName() {
		return superBankName;
	}
	public void setSuperBankName(String superBankName) {
		this.superBankName = superBankName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBkNo() {
		return bkNo;
	}
	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}
	public String getBkName() {
		return bkName;
	}
	public void setBkName(String bkName) {
		this.bkName = bkName;
	}
	public String getSuperBkNo() {
		return superBkNo;
	}
	public void setSuperBkNo(String superBkNo) {
		this.superBkNo = superBkNo;
	}
	public String getProvId() {
		return provId;
	}
	public void setProvId(String provId) {
		this.provId = provId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}

}
