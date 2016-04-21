package com.rongyifu.mms.bean;
//用于结算确认和结算发起的交易明细和结算查询中的明细
public class SettleDetail {
	private String tseq;
	private Integer gate;
	private Integer amount;
	private Integer refAmt;
	private Integer tradeAmt;
	private Integer feeAmt;
	private Integer merFee;
	
	//同步下载明细
	private String mid;
	private String outUserId;
	private String oid;
	private String sysDate;

	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getOutUserId() {
		return outUserId;
	}
	public void setOutUserId(String outUserId) {
		this.outUserId = outUserId;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getSysDate() {
		return sysDate;
	}
	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(Integer tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public Integer getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Integer getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(Integer refAmt) {
		this.refAmt = refAmt;
	}
	public Integer getMerFee() {
		return merFee;
	}
	public void setMerFee(Integer merFee) {
		this.merFee = merFee;
	}
	
}
