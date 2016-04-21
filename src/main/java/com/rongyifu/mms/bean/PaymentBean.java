package com.rongyifu.mms.bean;

public class PaymentBean {

	String oldSerialNo;//原流水号
	String receAccNo;//收款人账号
	String receBankNo;//收款方行号
	String state;//状态
	String errMsg;//错误信息
	String money;//金额
	String payAccNo;//付款人账号
	String rypSerialNo;//电银流水号
	String payBankNo;//付款行号
	public String getOldSerialNo() {
		return oldSerialNo;
	}
	public void setOldSerialNo(String oldSerialNo) {
		this.oldSerialNo = oldSerialNo;
	}
	public String getReceAccNo() {
		return receAccNo;
	}
	public void setReceAccNo(String receAccNo) {
		this.receAccNo = receAccNo;
	}
	public String getReceBankNo() {
		return receBankNo;
	}
	public void setReceBankNo(String receBankNo) {
		this.receBankNo = receBankNo;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getPayAccNo() {
		return payAccNo;
	}
	public void setPayAccNo(String payAccNo) {
		this.payAccNo = payAccNo;
	}
	public String getRypSerialNo() {
		return rypSerialNo;
	}
	public void setRypSerialNo(String rypSerialNo) {
		this.rypSerialNo = rypSerialNo;
	}
	public String getPayBankNo() {
		return payBankNo;
	}
	public void setPayBankNo(String payBankNo) {
		this.payBankNo = payBankNo;
	}
	
}
