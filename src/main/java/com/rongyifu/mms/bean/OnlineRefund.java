package com.rongyifu.mms.bean;


public class OnlineRefund {
	private Long id;//
	private String mid;//商户号
	private Integer orgMdate;//原交易商户日期
	private String orgOid;//原交易商户订单号
	private Integer orgSysDate;//原交易日期
	private Integer orgSysTime;//原交易时间
	private Long tseq;//原交易电银流水号
	private Integer gate;//原交易网关号
	private Integer gid;//原交易支付渠道
	private Long orgAmt;//原交易金额
	private String orgBkSeq;//原交易银行流水
	private String refOid;//退款订单号
	private Long refAmt;//退款金额
	private String bgRetUrl;//退款结果通知地址
	private String merPriv;//商户私有域
	private Integer refDate;//退款受理日期
	private Integer refTime;//退款受理时间
	private Integer reqBkDate;//请求银行日期
	private Integer reqBkTime;//请求银行时间
	private Integer bkRespDate;//银行响应日期
	private Integer bkRespTime;//银行响应时间
	private Integer refStatus;//退款状态
	private String refBkSeq;//银行退款流水号
	private Integer settleDate;//清算日期
	private String errorMsg;//退款失败原因
	private String onlineRefundId;//联机退款单号
	private String ip;//退款发起者IP
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getOrgMdate() {
		return orgMdate;
	}
	public void setOrgMdate(Integer orgMdate) {
		this.orgMdate = orgMdate;
	}
	public String getOrgOid() {
		return orgOid;
	}
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}
	public Integer getOrgSysDate() {
		return orgSysDate;
	}
	public void setOrgSysDate(Integer orgSysDate) {
		this.orgSysDate = orgSysDate;
	}
	public Integer getOrgSysTime() {
		return orgSysTime;
	}
	public void setOrgSysTime(Integer orgSysTime) {
		this.orgSysTime = orgSysTime;
	}
	public Long getTseq() {
		return tseq;
	}
	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public Long getOrgAmt() {
		return orgAmt;
	}
	public void setOrgAmt(Long orgAmt) {
		this.orgAmt = orgAmt;
	}
	public String getOrgBkSeq() {
		return orgBkSeq;
	}
	public void setOrgBkSeq(String orgBkSeq) {
		this.orgBkSeq = orgBkSeq;
	}
	public String getRefOid() {
		return refOid;
	}
	public void setRefOid(String refOid) {
		this.refOid = refOid;
	}
	public Long getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(Long refAmt) {
		this.refAmt = refAmt;
	}
	public String getBgRetUrl() {
		return bgRetUrl;
	}
	public void setBgRetUrl(String bgRetUrl) {
		this.bgRetUrl = bgRetUrl;
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}
	public Integer getRefDate() {
		return refDate;
	}
	public void setRefDate(Integer refDate) {
		this.refDate = refDate;
	}
	public Integer getRefTime() {
		return refTime;
	}
	public void setRefTime(Integer refTime) {
		this.refTime = refTime;
	}
	public Integer getReqBkDate() {
		return reqBkDate;
	}
	public void setReqBkDate(Integer reqBkDate) {
		this.reqBkDate = reqBkDate;
	}
	public Integer getReqBkTime() {
		return reqBkTime;
	}
	public void setReqBkTime(Integer reqBkTime) {
		this.reqBkTime = reqBkTime;
	}
	public Integer getBkRespDate() {
		return bkRespDate;
	}
	public void setBkRespDate(Integer bkRespDate) {
		this.bkRespDate = bkRespDate;
	}
	public Integer getBkRespTime() {
		return bkRespTime;
	}
	public void setBkRespTime(Integer bkRespTime) {
		this.bkRespTime = bkRespTime;
	}
	public Integer getRefStatus() {
		return refStatus;
	}
	public void setRefStatus(Integer refStatus) {
		this.refStatus = refStatus;
	}
	public String getRefBkSeq() {
		return refBkSeq;
	}
	public void setRefBkSeq(String refBkSeq) {
		this.refBkSeq = refBkSeq;
	}
	public Integer getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(Integer settleDate) {
		this.settleDate = settleDate;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getOnlineRefundId() {
		return onlineRefundId;
	}
	public void setOnlineRefundId(String onlineRefundId) {
		this.onlineRefundId = onlineRefundId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
}
