package com.rongyifu.mms.dbutil.sqlbean;

import com.rongyifu.mms.common.*;
import com.rongyifu.mms.dbutil.ISqlBean;
import com.rongyifu.mms.dbutil.SqlGenerator;

public class RefundLogBean implements ISqlBean {
	
	private Long id;
	private Long tseq;	
	private Integer mdate;	
	private String mid;	
	private String oid;	
	private Integer org_mdate;	
	private String org_oid;			
	private Long ref_amt;	
	private Integer sys_date;	
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
	private String batch;	
	private String bgRetUrl;			
	private Integer vstate;	
	private Integer author_type;	
	private Integer mer_fee;	
	private Integer bk_fee;	
	private Integer bk_fee_real;	
	private Long org_amt;	
	private String org_bk_seq;	
	private Integer gid;	
	private Integer pre_amt;	
	private Long org_pay_amt;	
	private Integer pre_amt1;	
	private String mer_priv;	
	private String p1;			
	private String online_refund_id;	
	private Integer online_refund_state;	
	private String online_refund_reason;	
	private Integer refund_type;	
	@Override
	public String getTableName() {
		return Constant.REFUND_LOG;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTseq() {
		return tseq;
	}

	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}

	public Integer getMdate() {
		return mdate;
	}

	public void setMdate(Integer mdate) {
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
		return org_mdate;
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

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public String getCard_no() {
		return SqlGenerator.handleGetFuncSD(card_no);
	}

	public void setCard_no(String card_no) {
		this.card_no =card_no;
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
		return ref_date;
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

	public String getRefund_reason() {
		return refund_reason;
	}

	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
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

	public Integer getAuthor_type() {
		return author_type;
	}

	public void setAuthor_type(Integer author_type) {
		this.author_type = author_type;
	}

	public Integer getMer_fee() {
		return mer_fee;
	}

	public void setMer_fee(Integer mer_fee) {
		this.mer_fee = mer_fee;
	}

	public Integer getBk_fee() {
		return bk_fee;
	}

	public void setBk_fee(Integer bk_fee) {
		this.bk_fee = bk_fee;
	}

	public Integer getBk_fee_real() {
		return bk_fee_real;
	}

	public void setBk_fee_real(Integer bk_fee_real) {
		this.bk_fee_real = bk_fee_real;
	}

	public Long getOrg_amt() {
		return org_amt;
	}

	public void setOrg_amt(Long org_amt) {
		this.org_amt = org_amt;
	}

	public String getOrg_bk_seq() {
		return org_bk_seq;
	}

	public void setOrg_bk_seq(String org_bk_seq) {
		this.org_bk_seq = org_bk_seq;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getPre_amt() {
		return pre_amt;
	}

	public void setPre_amt(Integer pre_amt) {
		this.pre_amt = pre_amt;
	}

	public Long getOrg_pay_amt() {
		return org_pay_amt;
	}

	public void setOrg_pay_amt(Long org_pay_amt) {
		this.org_pay_amt = org_pay_amt;
	}

	public Integer getPre_amt1() {
		return pre_amt1;
	}

	public void setPre_amt1(Integer pre_amt1) {
		this.pre_amt1 = pre_amt1;
	}

	public String getMer_priv() {
		return mer_priv;
	}

	public void setMer_priv(String mer_priv) {
		this.mer_priv = mer_priv;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getOnline_refund_id() {
		return online_refund_id;
	}

	public void setOnline_refund_id(String online_refund_id) {
		this.online_refund_id = online_refund_id;
	}

	public Integer getOnline_refund_state() {
		return online_refund_state;
	}

	public void setOnline_refund_state(Integer online_refund_state) {
		this.online_refund_state = online_refund_state;
	}

	public String getOnline_refund_reason() {
		return online_refund_reason;
	}

	public void setOnline_refund_reason(String online_refund_reason) {
		this.online_refund_reason = online_refund_reason;
	}

	public Integer getRefund_type() {
		return refund_type;
	}

	public void setRefund_type(Integer refund_type) {
		this.refund_type = refund_type;
	}

}
