package com.rongyifu.mms.bean;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.refund.RefundUtil;

public class RefundLog {
	private Integer id;
	private String tseq;
	private String mdate;
	private String mid;
	private String oid;
	private Integer org_mdate;
	private String org_oid;
	private Long ref_amt;
	private Integer sys_date;
	private Integer sys_time;
	private Integer gate;
	private String card_no;
	private String user_name;
	private Integer req_date;
	private Integer pro_date;
	private Integer ref_date;
	private Integer stat;
	private String reason;
	private String etro_reason;
	private String refund_reason;
	private String batch;//批次号
	private Integer authorType;//正在使用的
	private String merPriv;
	private Integer vstate;
	private String bgRetUrl;
	private Integer merFee;
	private Integer bkFee;
	private Integer bkFeeReal;
	private String orgBkSeq;
	private Long orgAmt;
	private Integer preAmt;
	private Integer merTradeType;//所属行业(数据分析用到)
	private Integer gid;
	
	private Long orgPayAmt;
	private Integer pre_amt1;
	private String authNo;
	private String onlineRefundId;
	private Integer onlineRefundState;
	private String onlineRefundReason;
	private Integer refundType;
	SystemDao sysDao=new SystemDao();
	public String getOnlineRefundId() {
		return onlineRefundId;
	}
	public void setOnlineRefundId(String onlineRefundId) {
		this.onlineRefundId = onlineRefundId;
	}
	public Integer getOnlineRefundState() {
		return onlineRefundState;
	}
	public void setOnlineRefundState(Integer onlineRefundState) {
		this.onlineRefundState = onlineRefundState;
	}
	public String getOnlineRefundReason() {
		return Ryt.empty(onlineRefundReason) ? "" : onlineRefundReason;
	}
	public void setOnlineRefundReason(String onlineRefundReason) {
		this.onlineRefundReason = onlineRefundReason;
	}
	public Integer getRefundType() {
		if (RefundUtil.getRefundGateList().containsKey(String.valueOf(gid))) {
			if (refundType.intValue() == Constant.RefundType.MANUAL)
				refundType = Constant.RefundType.ONLINE;
		} else
			refundType = Constant.RefundType.MANUAL;
		return refundType;
	}
	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}
	public String getAuthNo() {
		return authNo;
	}
	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}
	public String getBgRetUrl() {
		return bgRetUrl;
	}
	public void setBgRetUrl(String bgRetUrl) {
		this.bgRetUrl = bgRetUrl;
	}
	public Integer getVstate() {
		return vstate;
	}
	public void setVstate(Integer vstate) {
		this.vstate = vstate;
	}
	public Integer getAuthorType() {
		return authorType;
	}
	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getRefund_reason() {
		return refund_reason;
	}
	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public String getMdate() {
		return mdate;
	}
	public void setMdate(String mdate) {
		this.mdate = mdate;
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
	public Integer getOrg_mdate() {
		return org_mdate ;
	}
	public void setOrg_mdate(Integer org_mdate) {
		this.org_mdate = org_mdate;
	}
	public String getOrg_oid() {
		return org_oid;
	}
	public void setOrg_oid(String org_oid) {
		this.org_oid = org_oid;
	}
	public Long getRef_amt() {
		return ref_amt;
	}
	public void setRef_amt(Long ref_amt) {
		this.ref_amt = ref_amt;
	}
	public Integer getSys_date() {
		return sys_date;
	}
	public void setSys_date(Integer sys_date) {
		this.sys_date = sys_date;
	}
	public Integer getSys_time(){
		return sys_time;
	}
	public void setSys_time(Integer sys_time){
		this.sys_time=sys_time;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public String getCard_no() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,card_no);
	}
	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Integer getReq_date() {
		return req_date;
	}
	public void setReq_date(Integer req_date) {
		this.req_date = req_date;
	}
	public Integer getPro_date() {
		return pro_date;
	}
	public void setPro_date(Integer pro_date) {
		this.pro_date = pro_date;
	}
	public Integer getRef_date() {
		return ref_date ;
	}
	public void setRef_date(Integer ref_date) {
		this.ref_date = ref_date;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getEtro_reason() {
		return etro_reason;
	}
	public void setEtro_reason(String etro_reason) {
		this.etro_reason = etro_reason;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMerFee() {
		return merFee;
	}
	public void setMerFee(Integer merFee) {
		this.merFee = merFee;
	}
	public Integer getBkFee() {
		return bkFee;
	}
	public void setBkFee(Integer bkFee) {
		this.bkFee = bkFee;
	}
	public Integer getBkFeeReal() {
		return bkFeeReal;
	}
	public void setBkFeeReal(Integer bkFeeReal) {
		this.bkFeeReal = bkFeeReal;
	}
	public String getOrgBkSeq() {
		return orgBkSeq;
	}
	public void setOrgBkSeq(String orgBkSeq) {
		this.orgBkSeq = orgBkSeq;
	}
	public Long getOrgAmt() {
		return orgAmt;
	}
	public void setOrgAmt(Long orgAmt) {
		this.orgAmt = orgAmt;
	}
	public Integer getPreAmt() {
		return preAmt;
	}
	public void setPreAmt(Integer preAmt) {
		this.preAmt = preAmt;
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
	public Long getOrgPayAmt() {
		return orgPayAmt;
	}
	public void setOrgPayAmt(Long orgPayAmt) {
		this.orgPayAmt = orgPayAmt;
	}
	public Integer getPre_amt1() {
		return pre_amt1;
	}
	public void setPre_amt1(Integer preAmt1) {
		pre_amt1 = preAmt1;
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}
	
}
