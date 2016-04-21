package com.rongyifu.mms.settlement;

public class AmountBean {


	private Long pAmount;//支付金额
	private Long rAmount;//退款金额
	private Integer bankFee;//银行手续费
	private Integer purConut;//支付笔数
	private Integer refConut;//退款笔数
	private String bankName;//银行
	private Integer gate;
	
	private Integer refFee;//退回的手续费金额
	private Integer bkRefFee;//银行退回的手续费金额
	
	
	
	public Integer getBkRefFee() {
		return bkRefFee;
	}

	public void setBkRefFee(Integer bkRefFee) {
		this.bkRefFee = bkRefFee;
	}

	public Long getpAmount() {
		return pAmount;
	}

	public void setpAmount(Long pAmount) {
		this.pAmount = pAmount;
	}

	public Long getrAmount() {
		return rAmount;
	}

	public void setrAmount(Long rAmount) {
		this.rAmount = rAmount;
	}

	public Integer getRefFee() {
		return refFee;
	}

	public void setRefFee(Integer refFee) {
		this.refFee = refFee;
	}

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	
	
	public Long getPAmount() {
		return pAmount;
	}

	public void setPAmount(Long amount) {
		pAmount = amount;
	}

	public Long getRAmount() {
		return rAmount;
	}

	public void setRAmount(Long amount) {
		rAmount = amount;
	}

	public Integer getBankFee() {
		return bankFee;
	}

	public void setBankFee(Integer bankFee) {
		this.bankFee = bankFee;
	}

	public Integer getPurConut() {
		return purConut;
	}

	public void setPurConut(Integer purConut) {
		this.purConut = purConut;
	}

	public Integer getRefConut() {
		return refConut;
	}

	public void setRefConut(Integer refConut) {
		this.refConut = refConut;
	}

	
}
