package com.rongyifu.mms.bank.b2e;

public class B2ESignResult {

	
	private String token;
	
	private String date;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public B2ESignResult() {
		super();
	}

	public B2ESignResult(String token, String date) {
		super();
		this.token = token;
		this.date = date;
	}
	
	
}
