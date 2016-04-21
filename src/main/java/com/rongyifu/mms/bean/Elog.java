package com.rongyifu.mms.bean;

public class Elog {
	private Long id;
	private Integer version;
	private Integer mdate;
	private String mid;
	private String oid;
	private Long amount;
	private Short type;
	private Long ip;
	private Long orgOid;
	private Integer orgDate;
	private Integer sysDate;
	private Integer sysTime;
	private Integer gateId;
	private String cardInfo;
	private String merPriv;
	private String bkUrl;
	private String fgUrl;
	
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public String getBkUrl() {
		return bkUrl;
	}
	public void setBkUrl(String bkUrl) {
		this.bkUrl = bkUrl;
	}
	public String getCardInfo() {
		return cardInfo;
	}
	public void setCardInfo(String cardInfo) {
		this.cardInfo = cardInfo;
	}
	public String getFgUrl() {
		return fgUrl;
	}
	public void setFgUrl(String fgUrl) {
		this.fgUrl = fgUrl;
	}
	public Integer getGateId() {
		return gateId;
	}
	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIp() {
		return ip;
	}
	public void setIp(Long ip) {
		this.ip = ip;
	}
	public Integer getMdate() {
		return mdate;
	}
	public void setMdate(Integer mdate) {
		this.mdate = mdate;
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Integer getOrgDate() {
		return orgDate;
	}
	public void setOrgDate(Integer orgDate) {
		this.orgDate = orgDate;
	}
	public Long getOrgOid() {
		return orgOid;
	}
	public void setOrgOid(Long orgOid) {
		this.orgOid = orgOid;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
	public Integer getSysTime() {
		return sysTime;
	}
	public void setSysTime(Integer sysTime) {
		this.sysTime = sysTime;
	}
	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
