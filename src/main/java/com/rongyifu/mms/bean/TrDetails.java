package com.rongyifu.mms.bean;

import java.io.Serializable;
import java.util.Date;

public class TrDetails implements Serializable {

	
	private static final long serialVersionUID = 5536464492713728274L;
	
	private Integer id;//主键
	private Integer gid;//银行标识 对应电银渠道号
	private String bkSerialNo;//银行流水号
	private String acno;//帐号
	private String acname;//户名
	private String trBkNo;//交易行号
	private String curCode;//货币码
	private Integer trDate;//交易日期 yyyyMMdd
	private Integer trTime;//交易时间 HHmmss
	private Date trTimestamp;//交易时间戳
	private Integer oldTrDate;//原交易日期
	private String oppAcno;//对方帐号
	private String oppAcname;//对方帐号户名
	private String oppBkNo;//对方行号
	private String oppBkName;//对方帐号开户行
	private String oppCurCode;//对方货币码
	private Integer jdFlag;//借贷标志
	private Long rcvamt;//借入金额
	private Long payamt;//借出金额
	private Long amt;//交易金额
	private Long feeAmt;//手续费
	private Long balance;//余额
	private Long lastBalance;//上笔余额
	private Long freezeAmt;//冻结金额
	private String summary;//摘要
	private String postscript;//附言
	private String certType;//凭证种类
	private String certBatchNo;//凭证号码
	private String certNo;//凭证号码
	private String oldSerialNo;//被冲销流水号
	private String hostSerialNo;//主机流水号
	private Short trType;//交易类别标识
	private Short chFlag;//钞汇标识
	private Short bkFlag;//他行标识
	private Short areaFlag;//同城异地标识
	private Short trFrom;//交易来源标识
	private Short trFlag;//发生额标志
	private Short cashFlag;//现转标志
	private String trCode;//交易码
	private String userNo;//柜员号
	private String subNo;//传票号
	private String reserved1;//备注字段1
	private String reserved2;//备注字段2
	
	//报文里面多出来的四个字段
	private String trBankName;
	private String bankNo;
	private String bankName;
	private String printCount;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public String getBkSerialNo() {
		return bkSerialNo;
	}
	public void setBkSerialNo(String bkSerialNo) {
		this.bkSerialNo = bkSerialNo;
	}
	public String getAcno() {
		return acno;
	}
	public void setAcno(String acno) {
		this.acno = acno;
	}
	public String getAcname() {
		return acname;
	}
	public void setAcname(String acname) {
		this.acname = acname;
	}
	public String getTrBkNo() {
		return trBkNo;
	}
	public void setTrBkNo(String trBkNo) {
		this.trBkNo = trBkNo;
	}
	public String getCurCode() {
		return curCode;
	}
	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}
	public Integer getTrDate() {
		return trDate;
	}
	public void setTrDate(Integer trDate) {
		this.trDate = trDate;
	}
	public Integer getTrTime() {
		return trTime;
	}
	public void setTrTime(Integer trTime) {
		this.trTime = trTime;
	}
	public Date getTrTimestamp() {
		return trTimestamp;
	}
	public void setTrTimestamp(Date trTimestamp) {
		this.trTimestamp = trTimestamp;
	}
	public Integer getOldTrDate() {
		return oldTrDate;
	}
	public void setOldTrDate(Integer oldTrDate) {
		this.oldTrDate = oldTrDate;
	}
	public String getOppAcno() {
		return oppAcno;
	}
	public void setOppAcno(String oppAcno) {
		this.oppAcno = oppAcno;
	}
	public String getOppAcname() {
		return oppAcname;
	}
	public void setOppAcname(String oppAcname) {
		this.oppAcname = oppAcname;
	}
	public String getOppBkNo() {
		return oppBkNo;
	}
	public void setOppBkNo(String oppBkNo) {
		this.oppBkNo = oppBkNo;
	}
	public String getOppBkName() {
		return oppBkName;
	}
	public void setOppBkName(String oppBkName) {
		this.oppBkName = oppBkName;
	}
	public String getOppCurCode() {
		return oppCurCode;
	}
	public void setOppCurCode(String oppCurCode) {
		this.oppCurCode = oppCurCode;
	}
	public Integer getJdFlag() {
		return jdFlag;
	}
	public void setJdFlag(Integer jdFlag) {
		this.jdFlag = jdFlag;
	}
	public Long getRcvamt() {
		return rcvamt;
	}
	public void setRcvamt(Long rcvamt) {
		this.rcvamt = rcvamt;
	}
	public Long getPayamt() {
		return payamt;
	}
	public void setPayamt(Long payamt) {
		this.payamt = payamt;
	}
	public Long getAmt() {
		return amt;
	}
	public void setAmt(Long amt) {
		this.amt = amt;
	}
	public Long getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Long feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Long getLastBalance() {
		return lastBalance;
	}
	public void setLastBalance(Long lastBalance) {
		this.lastBalance = lastBalance;
	}
	public Long getFreezeAmt() {
		return freezeAmt;
	}
	public void setFreezeAmt(Long freezeAmt) {
		this.freezeAmt = freezeAmt;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getPostscript() {
		return postscript;
	}
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertBatchNo() {
		return certBatchNo;
	}
	public void setCertBatchNo(String certBatchNo) {
		this.certBatchNo = certBatchNo;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getOldSerialNo() {
		return oldSerialNo;
	}
	public void setOldSerialNo(String oldSerialNo) {
		this.oldSerialNo = oldSerialNo;
	}
	public String getHostSerialNo() {
		return hostSerialNo;
	}
	public void setHostSerialNo(String hostSerialNo) {
		this.hostSerialNo = hostSerialNo;
	}
	public Short getTrType() {
		return trType;
	}
	public void setTrType(Short trType) {
		this.trType = trType;
	}
	public Short getChFlag() {
		return chFlag;
	}
	public void setChFlag(Short chFlag) {
		this.chFlag = chFlag;
	}
	public Short getBkFlag() {
		return bkFlag;
	}
	public void setBkFlag(Short bkFlag) {
		this.bkFlag = bkFlag;
	}
	public Short getAreaFlag() {
		return areaFlag;
	}
	public void setAreaFlag(Short areaFlag) {
		this.areaFlag = areaFlag;
	}
	public Short getTrFrom() {
		return trFrom;
	}
	public void setTrFrom(Short trFrom) {
		this.trFrom = trFrom;
	}
	public Short getTrFlag() {
		return trFlag;
	}
	public void setTrFlag(Short trFlag) {
		this.trFlag = trFlag;
	}
	public Short getCashFlag() {
		return cashFlag;
	}
	public void setCashFlag(Short cashFlag) {
		this.cashFlag = cashFlag;
	}
	public String getTrCode() {
		return trCode;
	}
	public void setTrCode(String trCode) {
		this.trCode = trCode;
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getSubNo() {
		return subNo;
	}
	public void setSubNo(String subNo) {
		this.subNo = subNo;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getTrBankName() {
		return trBankName;
	}
	public void setTrBankName(String trBankName) {
		this.trBankName = trBankName;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPrintCount() {
		return printCount;
	}
	public void setPrintCount(String printCount) {
		this.printCount = printCount;
	}
}
