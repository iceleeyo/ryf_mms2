package com.rongyifu.mms.bean;

public class RiskLog {
	
	private Long tseq; 
	private Integer addDate; 
	private Long riskAmount; 
	private String addRemarks;
	private Integer verifyDate; 
	private String verifyRemarks; 
	private Integer confirmDate; 
	private String confirmRemarks;
	private Integer rstate;
	private Integer operId ;
	public Long getTseq() {
		return tseq;
	}
	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}
	public Integer getAddDate() {
		return addDate;
	}
	public void setAddDate(Integer addDate) {
		this.addDate = addDate;
	}
	public Long getRiskAmount() {
		return riskAmount;
	}
	public void setRiskAmount(Long riskAmount) {
		this.riskAmount = riskAmount;
	}
	public String getAddRemarks() {
		return addRemarks;
	}
	public void setAddRemarks(String addRemarks) {
		this.addRemarks = addRemarks;
	}
	public Integer getVerifyDate() {
		return verifyDate;
	}
	public void setVerifyDate(Integer verifyDate) {
		this.verifyDate = verifyDate;
	}
	public String getVerifyRemarks() {
		return verifyRemarks;
	}
	public void setVerifyRemarks(String verifyRemarks) {
		this.verifyRemarks = verifyRemarks;
	}
	public Integer getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Integer confirmDate) {
		this.confirmDate = confirmDate;
	}
	public String getConfirmRemarks() {
		return confirmRemarks;
	}
	public void setConfirmRemarks(String confirmRemarks) {
		this.confirmRemarks = confirmRemarks;
	}
	public Integer getRstate() {
		return rstate;
	}
	public void setRstate(Integer rstate) {
		this.rstate = rstate;
	}
	public Integer getOperId() {
		return operId;
	}
	public void setOperId(Integer operId) {
		this.operId = operId;
	}
	
	
	
	

}
