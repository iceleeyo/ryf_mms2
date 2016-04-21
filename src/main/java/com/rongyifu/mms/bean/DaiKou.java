package com.rongyifu.mms.bean;

public class DaiKou {
	
	private String dkAccNo;//扣款人帐号
	private String dkAccName;//扣款人账户名
	private String dkBankNo;//行号
	private short dkIDType;//证件类型
	private String dkIDNo;//证件号
	private short dkKZType;//卡折标识
	private double dkAmt=0;//金额
	private String remark;//用途
	private String errMsg;//错误信息
	
	public String getDkAccNo() {
		return dkAccNo;
	}
	public void setDkAccNo(String dkAccNo) {
		this.dkAccNo = dkAccNo;
	}
	public String getDkAccName() {
		return dkAccName;
	}
	public void setDkAccName(String dkAccName) {
		this.dkAccName = dkAccName;
	}
	public String getDkBankNo() {
		return dkBankNo;
	}
	public void setDkBankNo(String dkBankNo) {
		this.dkBankNo = dkBankNo;
	}
	public short getDkIDType() {
		return dkIDType;
	}
	public void setDkIDType(short dkIDType) {
		this.dkIDType = dkIDType;
	}
	public String getDkIDNo() {
		return dkIDNo;
	}
	public void setDkIDNo(String dkIDNo) {
		this.dkIDNo = dkIDNo;
	}
	public short getDkKZType() {
		return dkKZType;
	}
	public void setDkKZType(short dkKZType) {
		this.dkKZType = dkKZType;
	}
	public double getDkAmt() {
		return dkAmt;
	}
	public void setDkAmt(double dkAmt) {
		this.dkAmt = dkAmt;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
