package com.rongyifu.mms.bean;


import org.directwebremoting.annotations.DataTransferObject;

import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.ErrorCodes;

@DataTransferObject
public class Hlog {
	
	private String tseq;
	private Integer version;
	private Long ip;
	private Integer mdate;
	private String mid;
	private String  bid;
	private String oid;
	private Long amount;
	private Long payAmt;
	private Short type;
	private Integer gate;
	private Integer sysDate;
	private Integer initSysDate;
	private Integer sysTime;
	private String batch;
	private Integer feeAmt;
	private Integer bankFee;
	private Short tstat;
	private Integer bkFlag;
	private Long orgSeq;
	private Long refSeq;
	private Long refundAmt;
	private String merPriv;
	private Integer bkSend;
	private Integer bkRecv;
	private String bkUrl;
	private String fgUrl;
	private Short bkChk;
	private Integer bkDate;
	private String bk_seq1;
	private String bk_seq2;
	private String bkResp;
	private String mobileNo;
	private Integer transPeriod;
	private String cardNo;
	private String errorCode;
	private String error_msg;
	private String gateId;
	private String againPay_state;
	private String pay_count;
	private String p3;//pos数据同步：代理商号
	private String p10;//pos数据同步：业务类型 01 收单业务 02转账业务
	private String p9;//pos数据同步：批次号
	
	public String getAgainPay_state() {
		return againPay_state;
	}
	public void setAgainPay_state(String againPay_state) {
		this.againPay_state = againPay_state;
	}
	public String getPay_count() {
		return pay_count;
	}
	public void setPay_count(String pay_count) {
		this.pay_count = pay_count;
	}
	public String getGateId() {
		return gateId;
	}
	public void setGateId(String gateId) {
		this.gateId = gateId;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	private String phoneNo;
	
	private Integer authorType;
	
	private Integer operId;
	private Integer merTradeType;
	private Integer gid;
	
	
	private Integer preAmt;
	private String bkFeeModel;
	private Integer preAmt1;
	
//	//for get bank name
//	private String gate_desc_short;
//	//for minfo name
//	private String abbrev;
	
	public Integer getPreAmt() {
		return preAmt;
	}
	public void setPreAmt(Integer preAmt) {
		this.preAmt = preAmt;
	}
	public String getBkFeeModel() {
		return bkFeeModel;
	}
	public void setBkFeeModel(String bkFeeModel) {
		this.bkFeeModel = bkFeeModel;
	}
	public Integer getPreAmt1() {
		return preAmt1;
	}
	
	
	public void setPre_amt1(Integer preAmt1) {
		this.preAmt1 = preAmt1;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public Integer getOperId() {
		return operId;
	}
	public void setOperId(Integer operId) {
		this.operId = operId;
	}
	public Integer getAuthorType() {
		return authorType;
	}
	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}
//	public String getGate_desc_short() {
//		return gate_desc_short;
//	}
//	public void setGate_desc_short(String gate_desc_short) {
//		this.gate_desc_short = gate_desc_short;
//	}
//	public String getAbbrev() {
//		return abbrev;
//	}
//	public void setAbbrev(String abbrev) {
//		this.abbrev = abbrev;
//	}
	public Hlog() {
		super();
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
	public Long getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(Long payAmt) {
		this.payAmt = payAmt;
	}
	public Integer getBankFee() {
		return bankFee;
	}
	public void setBankFee(Integer bankFee) {
		this.bankFee = bankFee;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public Short getBkChk() {
		return bkChk;
	}
	public void setBkChk(Short bkChk) {
		this.bkChk = bkChk;
	}
	public Integer getBkDate() {
		return bkDate;
	}
	public void setBkDate(Integer bkDate) {
		this.bkDate = bkDate;
	}
	public Integer getBkFlag() {
		return bkFlag;
	}
	public void setBkFlag(Integer bkFlag) {
		this.bkFlag = bkFlag;
	}
	public Integer getBkRecv() {
		return bkRecv;
	}
	public void setBkRecv(Integer bkRecv) {
		this.bkRecv = bkRecv;
	}
	public String getBkResp() {
		return bkResp;
	}
	public void setBkResp(String bkResp) {
		this.bkResp = bkResp== null? "" : bkResp;
	}
	public Integer getBkSend() {
		return bkSend;
	}
	public void setBkSend(Integer bkSend) {
		this.bkSend = bkSend;
	}
    
	public String getBk_seq1() {
		return bk_seq1;
	}
	public void setBk_seq1(String bk_seq1) {
		this.bk_seq1 = bk_seq1 == null ? "" : bk_seq1;
	}
	public String getBk_seq2() {
		return bk_seq2==null ? "" : bk_seq2;
	}
	public void setBk_seq2(String bk_seq2) {
		this.bk_seq2 = bk_seq2;
	}
	public String getBkUrl() {
		return bkUrl;
	}
	public void setBkUrl(String bkUrl) {
		this.bkUrl = bkUrl;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getErrorCode() {//返回错误代码  pnr汇付，19pay（高阳）
		if(gid==null || errorCode ==null){ 
			return "";
		}
		if(1 == gid){
			String msg = ErrorCodes.PNR_CODE.get(errorCode);
			return msg==null ? errorCode : msg;
		}else if(3 == gid){
			String msg2 = ErrorCodes.I19Pay_CODE.get(errorCode);
			return (msg2== null ? errorCode : msg2 );
		}else if(4 == gid){
			String msg4 = ErrorCodes.KQ_CODE.get(errorCode);
			return (msg4== null ? errorCode : msg4 );
			
		}else if(50100 == gid){
				String msg4 = ErrorCodes.ICBC_NC_CODES.get(errorCode);
				return (msg4== null ? errorCode : msg4 );
		}else{
			return errorCode;
		}
	}
	public void setErrorCode(String errorCode) {
		this.errorCode=errorCode;
	}
	public Integer getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getFgUrl() {
		return fgUrl;
	}
	public void setFgUrl(String fgUrl) {
		this.fgUrl = fgUrl;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
/*	public String getGate_desc_short() {
		return gate_desc_short;
	}
	public void setGate_desc_short(String gate_desc_short) {
		this.gate_desc_short = gate_desc_short;
	}*/
	public Integer getInitSysDate() {
		return initSysDate;
	}
	public void setInitSysDate(Integer initSysDate) {
		this.initSysDate = initSysDate;
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
		
		return  (merPriv == null ? "" : Base64.decodeToString(merPriv));
		
		
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
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
/*	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Long getOrgSeq() {
		return orgSeq;
	}
	public void setOrgSeq(Long orgSeq) {
		this.orgSeq = orgSeq;
	}
	public Long getRefSeq() {
		return refSeq;
	}
	public void setRefSeq(Long refSeq) {
		this.refSeq = refSeq;
	}
	public Long getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(Long refundAmt) {
		this.refundAmt = refundAmt;
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
	public Integer getTransPeriod() {
		return transPeriod;
	}
	public void setTransPeriod(Integer transPeriod) {
		this.transPeriod = transPeriod;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public Short getTstat() {
		return tstat;
	}
	public void setTstat(Short tstat) {
		this.tstat = tstat;
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
	public Integer getMerTradeType() {
		return merTradeType;
	}
	public void setMerTradeType(Integer merTradeType) {
		this.merTradeType = merTradeType;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	/**
	 * pos数据同步：代理商号
	 */
	public String getP3() {
		return p3;
	}
	public void setP3(String p3) {
		this.p3 = p3;
	}
	/**
	 * pos数据同步：
	 * @return 业务类型 01 收单业务 02转账业务
	 */
	public String getP10() {
		return p10;
	}
	public void setP10(String p10) {
		this.p10 = p10;
	}
	/**
	 * pos数据同步：
	 * @return 批次号
	 */
	public String getP9() {
		return p9;
	}
	public void setP9(String p9) {
		this.p9 = p9;
	}
	
}
