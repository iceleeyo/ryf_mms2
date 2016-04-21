package com.rongyifu.mms.bean;


public class BkAccount {
	private Integer id;
	private String accNo;
	private String bkNo;
	private String bkAbbv;
	private Integer bkType;
	private String accName;
	private String currency;
	private Integer operDate;
	private Integer accType;
	private String bkName;
	private Long bkBl;
	private Long bfBl;
	private String errorMsg;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public Integer getBkType() {
		return bkType;
	}
	public void setBkType(Integer bkType) {
		this.bkType = bkType;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getOperDate() {
		return operDate;
	}
	public void setOperDate(Integer operDate) {
		this.operDate = operDate;
	}
	public Integer getAccType() {
		return accType;
	}
	public void setAccType(Integer accType) {
		this.accType = accType;
	}
	public String getBkName() {
		return bkName;
	}
	public void setBkName(String bkName) {
		this.bkName = bkName;
	}
	public String getBkNo() {
		return bkNo;
	}
	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}
	public String getBkAbbv() {
		return bkAbbv;
	}
	public void setBkAbbv(String bkAbbv) {
		this.bkAbbv = bkAbbv;
	}
	public Long getBkBl() {
		return bkBl;
	}
	public void setBkBl(Long bkBl) {
		this.bkBl = bkBl;
	}
	public Long getBfBl() {
		return bfBl;
	}
	public void setBfBl(Long bfBl) {
		this.bfBl = bfBl;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
