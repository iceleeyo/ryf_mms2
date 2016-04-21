package com.rongyifu.mms.bean;

public class OperAuthInfo  {

	// Fields

	private String mid;
	private String operId;
	private String authId;
	private Short state;
	private String resv;

	// Constructors

	/** default constructor */
	public OperAuthInfo() {
	}

	/** full constructor */
	public OperAuthInfo(String mid, String operId, String authId,
			Short state, String resv) {
		this.mid = mid;
		this.operId = operId;
		this.authId = authId;
		this.state = state;
		this.resv = resv;
	}

	// Property accessors

	public String getMid() {
		return this.mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getOperId() {
		return this.operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

	public String getAuthId() {
		return this.authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public Short getState() {
		return this.state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	public String getResv() {
		return this.resv;
	}

	public void setResv(String resv) {
		this.resv = resv;
	}

	

}