package com.rongyifu.mms.bean;

public class Account {

	// Fields
	private Long id;
	private String mid;
	private Long tseq;
	private Integer type;
	private Integer date;
	private Integer time;
	private Long account;
	private Integer fee;
	private Long balance;
	private Long amount;
	
	private String abbrev;
	private Integer liqType;
	


	public Account() {
	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public Integer getLiqType() {
		return liqType;
	}

	public void setLiqType(Integer liqType) {
		this.liqType = liqType;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getMid() {
		return this.mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}


	public Long getTseq() {
		return this.tseq;
	}

	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getDate() {
		return this.date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}


	public Integer getTime() {
		return this.time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	
	public Long getAccount() {
		return this.account;
	}

	public void setAccount(Long account) {
		this.account = account;
	}

	public Integer getFee() {
		return this.fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

	public Long getBalance() {
		return this.balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}


	public Long getAmount() {
		return this.amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

}