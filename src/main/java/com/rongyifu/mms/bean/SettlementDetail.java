package com.rongyifu.mms.bean;
//结算单明细数据
public class SettlementDetail {
	private Integer merDate;
	private String orderId;
	private String transAmt;
	private String feeAmt;
	private String transType;
	private String tseq;
	public Integer getMerDate() {
		return merDate;
	}
	public void setMerDate(Integer merDate) {
		this.merDate = merDate;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	
	

}
