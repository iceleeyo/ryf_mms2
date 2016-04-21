package com.rongyifu.mms.bean;


public class OperInfo {

	private String mid;
	private Integer operId;
	private String operPass;
	private String operName;
	private String operTel;
	private String operEmail;
	private Integer regDate;
	private Integer state;
	private String LoginIn;
	private Integer mtype;
	private String mName;
	private String antiPhishingStr;
	
	public Integer getMtype() {
		return mtype;
	}

	public void setMtype(Integer mtype) {
		this.mtype = mtype;
	}

	public String getLoginIn() {
		return LoginIn;
	}

	public void setLoginIn(String loginIn) {
		LoginIn = loginIn;
	}

	// Constructors
	/** default constructor */
	public OperInfo() {
	}

	/** minimal constructor */
	public OperInfo(Integer regDate) {
		this.regDate = regDate;
	}
	/** full constructor */
	public String getMid() {
		return this.mid;
	}



	public void setMid(String
			mid) {
		this.mid = mid;
	}

	public Integer getOperId() {
		return this.operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
	}

	public String getOperPass() {
		return this.operPass;
	}

	public void setOperPass(String operPass) {
		this.operPass = operPass;
	}

	public String getOperName() {
		return this.operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getOperTel() {
		return this.operTel;
	}

	public void setOperTel(String operTel) {
		this.operTel = operTel;
	}

	public String getOperEmail() {
		return this.operEmail;
	}

	public void setOperEmail(String operEmail) {
		this.operEmail = operEmail;
	}

	public Integer getRegDate() {
		return this.regDate;
	}

	public void setRegDate(Integer regDate) {
		this.regDate = regDate;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getAntiPhishingStr() {
		return antiPhishingStr;
	}

	public void setAntiPhishingStr(String antiPhishingStr) {
		this.antiPhishingStr = antiPhishingStr;
	}

}