package com.rongyifu.mms.bean;

import org.directwebremoting.annotations.DataTransferObject;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.Base64;

@DataTransferObject
public class OrderInfo {

    private String tseq;
    private Integer version;
    private Long ip;
    private Integer mdate;
    private String mid;
    private String bid;
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
    private String payCard;
    private String payId;
    private Long payAmount;
    private String chkNo;
    private Integer timePeriod;
    private String p1;
    private String p2;
    private String p3;
    private String p4;
    private String p5;
    private String p6;
    private String p7;
    private String p8;
    private String p9;
    private String p10;
    private String p11;
    private String p12;
    private String p15;
    private String aid;
    private String auditRemark;
    private Long rechargeAmt;
    private short pstate;
    private String orgn_remark;//经办意见
    private String cert_remark;//凭证审核意见
    private String trans_proof;
    private String name;
    private Integer cancel_state;//撤销状态
    private String cancel_reason;//撤销意见
    private Integer data_source;
    private String bank_branch;//新增 开户银行名称 收款人名称
    private Integer againPay_status;//新增再次代付申请状态  默认0  ， 1 申请状态  ,  2 申请中 , 3 申请通过
    private Integer againPay_date;
    SystemDao sysDao = new SystemDao();

    public Integer getAgainPay_date() {
        return againPay_date;
    }

    public void setAgainPay_date(Integer againPay_date) {
        this.againPay_date = againPay_date;
    }

    public Integer getAgainPay_status() {
        return againPay_status;
    }

    public void setAgainPay_status(Integer againPay_status) {
        this.againPay_status = againPay_status;
    }

    public String getBank_branch() {
        return bank_branch;
    }

    public void setBank_branch(String bank_branch) {
        this.bank_branch = bank_branch;
    }

    public Integer getData_source() {
        return data_source;
    }

    public void setData_source(Integer data_source) {
        this.data_source = data_source;
    }

    public String getP12() {
        return p12;
    }

    public void setP12(String p12) {
        this.p12 = p12;
    }

    public Integer getCancel_state() {
        return cancel_state;
    }

    public void setCancel_state(Integer cancel_state) {
        this.cancel_state = cancel_state;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrans_proof() {
        return trans_proof;
    }

    public void setTrans_proof(String trans_proof) {
        this.trans_proof = trans_proof;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Long getRechargeAmt() {
        return rechargeAmt;
    }

    public void setRechargeAmt(Long rechargeAmt) {
        this.rechargeAmt = rechargeAmt;
    }

    public short getPstate() {
        return pstate;
    }

    public void setPstate(short pstate) {
        this.pstate = pstate;
    }

    public String getOrgn_remark() {
        return orgn_remark;
    }

    public void setOrgn_remark(String orgn_remark) {
        this.orgn_remark = orgn_remark;
    }

    public String getCert_remark() {
        return cert_remark;
    }

    public void setCert_remark(String cert_remark) {
        this.cert_remark = cert_remark;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        if (error_msg == null) {
            this.error_msg = "";
        } else {
            this.error_msg = error_msg;
        }
    }
    private String phoneNo;
    private Integer authorType;
    private Integer operId;
    private Integer merTradeType;
    private Integer gid;
    private Integer preAmt;
    private String bkFeeModel;
    private Integer preAmt1;

// //	for get bank name
//	private String gate_desc_short;
//	//for minfo name
//	private String abbrev;
    public String getPayCard() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,payCard);
    }

    public void setPayCard(String payCard) {
        this.payCard=payCard;
//        LoginUser user = sysDao.getLoginUser();
//        if (user == null) {
//            this.payCard = "";
//            return;
//        }
//
//        String auth = user.getAuth();
//        if (auth == null) {
//            this.payCard = "";
//        } else if (auth.length() > 105 && auth.charAt(105) == '1') {
//            this.payCard = payCard;
//        } else {
//            if (payCard == null) {
//                this.payCard = "";
//            } else if (payCard.length() > 10) {
//                this.payCard = payCard.substring(0, 6) + "******" + payCard.substring(payCard.length() - 4, payCard.length());
//            } else {
//                this.payCard = payCard;
//            }
//        }
    }

    public String getPayId() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,payId);
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Long getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Long payAmount) {
        this.payAmount = payAmount;
    }

    public String getChkNo() {
        return chkNo;
    }

    public void setChkNo(String chkNo) {
        this.chkNo = chkNo;
    }

    public Integer getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(Integer timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setPreAmt1(Integer preAmt1) {
        this.preAmt1 = preAmt1;
    }

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
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,phoneNo);
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

    public OrderInfo() {
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
        this.bkResp = bkResp == null ? "" : bkResp;
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
        return bk_seq2 == null ? "" : bk_seq2;
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
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,cardNo);
    }

    public void setCardNo(String cardNo) {
        this.cardNo=cardNo;
//        LoginUser user = sysDao.getLoginUser();
//        if (user == null) {
//            this.cardNo = "";
//            return;
//        }
//
//        String auth = user.getAuth();
//        if (auth == null) {
//            this.cardNo = "";
//        } else if (auth.length() > 105 && auth.charAt(105) == '1') {
//            this.cardNo = cardNo;
//        } else {
//            if (cardNo == null) {
//                this.cardNo = "";
//
//            } else if (cardNo.length() > 10) {
//                this.cardNo = cardNo.substring(0, 6) + "******" + cardNo.substring(cardNo.length() - 4, cardNo.length());
//            } else {
//                this.cardNo = cardNo;
//            }
//        }
    }

    public String getErrorCode() {//返回错误代码  pnr汇付，19pay（高阳）
//		if(gid==null || errorCode ==null){
//			return "";
//		}
//		if(1 == gid){
//			String msg = ErrorCodes.PNR_CODE.get(errorCode);
//			return msg==null ? errorCode : msg;
//		}else if(3 == gid){
//			String msg2 = ErrorCodes.I19Pay_CODE.get(errorCode);
//			return (msg2== null ? errorCode : msg2 );
//		}else if(4 == gid){
//			String msg4 = ErrorCodes.KQ_CODE.get(errorCode);
//			return (msg4== null ? errorCode : msg4 );
//
//		}else if(50100 == gid){
//				String msg4 = ErrorCodes.ICBC_NC_CODES.get(errorCode);
//				return (msg4== null ? errorCode : msg4 );
//		}else{
//			return errorCode;
//		}

        if (Ryt.empty(errorCode) || errorCode.startsWith("00")) {
            return "";
        }
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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
        if(Ryt.empty(merPriv))return "";
        try{
            merPriv=Base64.decodeToString(merPriv);
        }catch(Exception e){
        }finally{
            return merPriv;
        }
//        return Ryt.empty(merPriv) ? "" : Base64.decodeToString(merPriv);
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
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,mobileNo);
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

    public String getP8() {
        return p8;
    }

    public void setP8(String p8) {
        this.p8 = p8;
    }

    public String getP9() {
        return p9;
    }

    public void setP9(String p9) {
        this.p9 = p9;
    }

    public String getP10() {
        return p10;
    }

    public void setP10(String p10) {
        this.p10 = p10;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
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

    public String getP4() {
        return p4;
    }

    public void setP4(String p4) {
        this.p4 = p4;
    }

    public String getP5() {
        return p5;
    }

    public void setP5(String p5) {
        this.p5 = p5;
    }

    public String getP6() {
        return p6;
    }

    public void setP6(String p6) {
        this.p6 = p6;
    }

    public String getP7() {
        return p7;
    }

    public void setP7(String p7) {
        this.p7 = p7;
    }

    public String getP1() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,p1);
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP11() {
        return p11;
    }

    public void setP11(String p11) {
        this.p11 = p11;
    }

	public String getP15() {
		return p15;
	}

	public void setP15(String p15) {
		this.p15 = p15;
	}
}
