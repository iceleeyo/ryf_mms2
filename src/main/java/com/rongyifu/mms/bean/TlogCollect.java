package com.rongyifu.mms.bean;

public class TlogCollect {
	private String mid;
	private Integer gate;
	private Integer sysDate;
	private Integer paySucCnt;
	private Long paySucAmt;
	private Integer payFaiCnt;
	private Long payFaiAmt;
	private Integer cnlSucCnt;
	private Long cnlSucAmt;
	private Integer cnlFaiCnt;
	private Long cnlFaiAmt;
	
	public TlogCollect() {
		super();
	}
	public Long getCnlFaiAmt() {
		return cnlFaiAmt;
	}
	public void setCnlFaiAmt(Long cnlFaiAmt) {
		this.cnlFaiAmt = cnlFaiAmt;
	}
	public Integer getCnlFaiCnt() {
		return cnlFaiCnt;
	}
	public void setCnlFaiCnt(Integer cnlFaiCnt) {
		this.cnlFaiCnt = cnlFaiCnt;
	}
	public Long getCnlSucAmt() {
		return cnlSucAmt;
	}
	public void setCnlSucAmt(Long cnlSucAmt) {
		this.cnlSucAmt = cnlSucAmt;
	}
	public Integer getCnlSucCnt() {
		return cnlSucCnt;
	}
	public void setCnlSucCnt(Integer cnlSucCnt) {
		this.cnlSucCnt = cnlSucCnt;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Long getPayFaiAmt() {
		return payFaiAmt;
	}
	public void setPayFaiAmt(Long payFaiAmt) {
		this.payFaiAmt = payFaiAmt;
	}
	public Integer getPayFaiCnt() {
		return payFaiCnt;
	}
	public void setPayFaiCnt(Integer payFaiCnt) {
		this.payFaiCnt = payFaiCnt;
	}
	public Long getPaySucAmt() {
		return paySucAmt;
	}
	public void setPaySucAmt(Long paySucAmt) {
		this.paySucAmt = paySucAmt;
	}
	public Integer getPaySucCnt() {
		return paySucCnt;
	}
	public void setPaySucCnt(Integer paySucCnt) {
		this.paySucCnt = paySucCnt;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
}
