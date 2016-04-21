package com.rongyifu.mms.bean;

import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 代付交易记录
 */
public class DfTransaction {
	private String tseq;
	private String ip;
	private String accountId;
	private String orderId;
	private Long transAmt;
	private Short type;
	private Integer gate;
	private Integer gid;
	private Integer sysDate;
	private Integer sysTime;
	private Short tstat;
	private String merPriv;
	private Integer bkSendDate;
	private Integer bkSendTime;
	private Integer bkDate;
	private Integer bkTime;
	private String bkUrl;
	private String bkSeq;
	private String errorCode;
	private String errorMsg;
	private String accNo;//收款人账号 数据库为base64加密保存 需解码
	private String accName;//收款人户名 数据库为base64加密保存 需解码
	private String bkNo;
	private Short cardFlag;
	private String purpose;
	private String areaProv;
	private String areaCity;
	private Short isNotice;
	private Short dataSource;
	private String p1;
	private String p2;
	private String p3;
	
	//
	private Integer bdate;
	private Integer edate;
	
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Long getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(Long transAmt) {
		this.transAmt = transAmt;
	}
	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
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
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
//---------------日期格式化函数---------------------
	public String getSysDateStr(){
		if(this.sysDate == null ||this.sysDate == 0){
			return "";
		}
		return DateUtil.formatDate(this.sysDate) +" "+ DateUtil.getStringTime(this.sysTime);
	}
	public String getBkDateStr(){
		if(this.bkDate == null ||this.bkDate == 0){
			return "";
		}
		return DateUtil.formatDate(this.bkDate) +" "+ DateUtil.getStringTime(this.bkTime);
	}
	public String getBkSendDateStr(){
		if(this.bkSendDate == null ||this.bkSendDate == 0){
			return "";
		}
		return DateUtil.formatDate(this.bkSendDate) +" "+ DateUtil.getStringTime(this.bkSendTime);
	}
//---------------日期格式化函数---------------------
	public Integer getSysTime() {
		return sysTime;
	}
	public void setSysTime(Integer sysTime) {
		this.sysTime = sysTime;
	}
	public Short getTstat() {
		return tstat;
	}
	public void setTstat(Short tstat) {
		this.tstat = tstat;
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}
	public Integer getBkSendDate() {
		return bkSendDate;
	}
	public void setBkSendDate(Integer bkSendDate) {
		this.bkSendDate = bkSendDate;
	}
	public Integer getBkSendTime() {
		return bkSendTime;
	}
	public void setBkSendTime(Integer bkSendTime) {
		this.bkSendTime = bkSendTime;
	}
	public Integer getBkDate() {
		return bkDate;
	}
	public void setBkDate(Integer bkDate) {
		this.bkDate = bkDate;
	}
	public Integer getBkTime() {
		return bkTime;
	}
	public void setBkTime(Integer bkTime) {
		this.bkTime = bkTime;
	}
	public String getBkUrl() {
		return bkUrl;
	}
	public void setBkUrl(String bkUrl) {
		this.bkUrl = bkUrl;
	}
	public String getBkSeq() {
		return bkSeq;
	}
	public void setBkSeq(String bkSeq) {
		this.bkSeq = bkSeq;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getAccNo() {//判断是否进行base64解密
		return Base64.decIfPossible(accNo);
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getAccName() {//判断是否进行base64解密
		return Base64.decIfPossible(accName);
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getBkNo() {
		return bkNo;
	}
	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}
	public Short getCardFlag() {
		return cardFlag;
	}
	public void setCardFlag(Short cardFlag) {
		this.cardFlag = cardFlag;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getAreaProv() {
		return areaProv;
	}
	public void setAreaProv(String areaProv) {
		this.areaProv = areaProv;
	}
	public String getAreaCity() {
		return areaCity;
	}
	public void setAreaCity(String areaCity) {
		this.areaCity = areaCity;
	}
	public Short getIsNotice() {
		return isNotice;
	}
	public void setIsNotice(Short isNotice) {
		this.isNotice = isNotice;
	}
	public Short getDataSource() {
		return dataSource;
	}
	public void setDataSource(Short dataSource) {
		this.dataSource = dataSource;
	}
	public String getP1() {
		return p1;
	}
	public void setP1(String p1) {
		this.p1 = p1;
	}
	public String getP2() {
		return p2;
	}
	public void setP2(String p2) {
		this.p2 = p2;
	}
	public String getP3() {
		return p3;
	}
	public void setP3(String p3) {
		this.p3 = p3;
	}
	public Integer getBdate() {
		return bdate;
	}
	public void setBdate(Integer bdate) {
		this.bdate = bdate;
	}
	public Integer getEdate() {
		return edate;
	}
	public void setEdate(Integer edate) {
		this.edate = edate;
	}
}
