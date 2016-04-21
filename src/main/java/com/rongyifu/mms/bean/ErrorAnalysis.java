package com.rongyifu.mms.bean;

public class ErrorAnalysis {
	
	private Integer id;
	private String tseq ;     
	private String bkMerOid;    
	private Long bkAmount;        
	private Integer bkDate;              
	private Integer payDate;         
	private String bkSeq;     
	private Integer errorType;           
	private Integer state;       
	private Integer checkDate;        
	private Integer solveDate;         
	private Integer solveOper;
	private String solveRemark;
	
	private String mid;
	private Integer amount;
	private Integer gate;
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public String getBkMerOid() {
		return bkMerOid;
	}
	public void setBkMerOid(String bkMerOid) {
		this.bkMerOid = bkMerOid;
	}
	public Long getBkAmount() {
		return bkAmount;
	}
	public void setBkAmount(Long bkAmount) {
		this.bkAmount = bkAmount;
	}
	public Integer getBkDate() {
		return bkDate;
	}
	public void setBkDate(Integer bkDate) {
		this.bkDate = bkDate;
	}
	public Integer getPayDate() {
		return payDate;
	}
	public void setPayDate(Integer payDate) {
		this.payDate = payDate;
	}
	public String getBkSeq() {
		return bkSeq;
	}
	public void setBkSeq(String bkSeq) {
		this.bkSeq = bkSeq;
	}
	public Integer getErrorType() {
		return errorType;
	}
	public void setErrorType(Integer errorType) {
		this.errorType = errorType;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Integer checkDate) {
		this.checkDate = checkDate;
	}
	public Integer getSolveDate() {
		return solveDate;
	}
	public void setSolveDate(Integer solveDate) {
		this.solveDate = solveDate;
	}
	public Integer getSolveOper() {
		return solveOper;
	}
	public void setSolveOper(Integer solveOper) {
		this.solveOper = solveOper;
	}
	public String getSolveRemark() {
		return solveRemark;
	}
	public void setSolveRemark(String solveRemark) {
		this.solveRemark = solveRemark;
	} 
	
}
