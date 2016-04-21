package com.rongyifu.mms.bean;

public class RiskBean {
	
	private Long tseq; 
	private String mid;
	private String  mname; 
	private String oid;
	private Integer gateId;
	private String gateName;
	private Integer sysDate;
	private Integer amount;
	private Integer state;
	private String payCard;
	private String payMobile;
	private String payIdNo;
	
	public Long getTseq() {
		return tseq;
	}
	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Integer getGateId() {
		return gateId;
	}
	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	public String getGateName() {
		return gateName;
	}
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getPayCard() {
		return payCard;
	}
	public void setPayCard(String payCard) {
		this.payCard = payCard;
	}
	public String getPayMobile() {
		return payMobile;
	}
	public void setPayMobile(String payMobile) {
		this.payMobile = payMobile;
	}
	public String getPayIdNo() {
		return payIdNo;
	}
	public void setPayIdNo(String payIdNo) {
		this.payIdNo = payIdNo;
	}
	
}
