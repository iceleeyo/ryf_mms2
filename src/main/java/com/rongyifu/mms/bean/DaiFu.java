package com.rongyifu.mms.bean;

/**
 * @author Administrator
 *
 */
public class DaiFu {

	private String accNo;
	private String accName;
	private String bkNo;
	private String trAmt;
	private String trFee;
	private String use;
	private boolean flag;
	private String errMsg;
	private Integer toProvId;
	private short cardFlag;
	private String errProvId;//错误省份ID 用在B2B批量浮夸错误信息显示
	
	private String openBkNo;//联行行号；
//	public String toString() {
//		return this.accNo + this.accName + this.bkNo+ this.errMsg+this.trAmt;
//
//	}

	public String getOpenBkNo() {
		return openBkNo;
	}

	public void setOpenBkNo(String openBkNo) {
		this.openBkNo = openBkNo;
	}

	public void setErr(String msg) {
		this.flag = false;
		setErrMsg(msg);

	}

	public String getErrProvId() {
		return errProvId;
	}

	public void setErrProvId(String errProvId) {
		this.errProvId = errProvId;
	}

	public DaiFu() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getBkNo() {
		return bkNo;
	}

	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}

	public String getTrAmt() {
		return trAmt;
	}

	public void setTrAmt(String trAmt) {
		this.trAmt = trAmt;
	}

	public String getTrFee() {
		return trFee;
	}

	public void setTrFee(String trFee) {
		this.trFee = trFee;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		
		if(flag) this.errMsg = "";
		this.flag = flag;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = "<font color='red'>" + errMsg + "</font>";
		;
	}

	public Integer getToProvId() {
		return toProvId;
	}

	public void setToProvId(Integer toProvId) {
		this.toProvId = toProvId;
	}

	public short getCardFlag() {
		return cardFlag;
	}

	public void setCardFlag(short cardFlag) {
		this.cardFlag = cardFlag;
	}
	

}
