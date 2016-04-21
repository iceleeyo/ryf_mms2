package com.rongyifu.mms.bean;

public class FeeLiqLog {

	private  String mid;          
	private Integer gate;              
	private String batch ;             
	private Integer purCnt;             
	private Long purAmt;           
	private Integer refCnt;            
	private Long refAmt;          
	private Long amount;            
	private Integer feeRatio;       
	private Integer feeAmt;           
	private Long liqAmt;        
	private Integer liqType;
	private Integer refFee;
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public Integer getPurCnt() {
		return purCnt;
	}
	public void setPurCnt(Integer purCnt) {
		this.purCnt = purCnt;
	}
	public Long getPurAmt() {
		return purAmt;
	}
	public void setPurAmt(Long purAmt) {
		this.purAmt = purAmt;
	}
	public Integer getRefCnt() {
		return refCnt;
	}
	public void setRefCnt(Integer refCnt) {
		this.refCnt = refCnt;
	}
	public Long getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(Long refAmt) {
		this.refAmt = refAmt;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Integer getFeeRatio() {
		return feeRatio;
	}
	public void setFeeRatio(Integer feeRatio) {
		this.feeRatio = feeRatio;
	}
	public Integer getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Long getLiqAmt() {
		return liqAmt;
	}
	public void setLiqAmt(Long liqAmt) {
		this.liqAmt = liqAmt;
	}
	public Integer getLiqType() {
		return liqType;
	}
	public void setLiqType(Integer liqType) {
		this.liqType = liqType;
	}
	public Integer getRefFee() {
		return refFee;
	}
	public void setRefFee(Integer refFee) {
		this.refFee = refFee;
	}   
	
}
