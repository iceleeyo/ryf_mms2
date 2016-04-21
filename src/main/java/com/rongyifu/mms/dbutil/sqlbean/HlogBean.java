package com.rongyifu.mms.dbutil.sqlbean;

import com.rongyifu.mms.common.*;
import com.rongyifu.mms.dbutil.ISqlBean;
import com.rongyifu.mms.dbutil.SqlGenerator;

public class HlogBean implements ISqlBean {

	private Long tseq;
	private Integer version;
	private Long ip;
	private Integer mdate;
	private String mid;	
	private String bid;	
	private String oid;	
	private Long amount;	
	private Long pay_amt;	
	private Integer type;	
	private Integer gate;	
	private Integer sys_date;	
	private Integer init_sys_date;	
	private Integer sys_time;	
	private String batch;	
	private Integer fee_amt;	
	private Integer bank_fee;	
	private Integer tstat;	
	private Integer bk_flag;	
	private Long org_seq;	
	private Long ref_seq;	
	private Long refund_amt;	
	private String mer_priv;	
	private Integer bk_send;	
	private Integer bk_recv;	
	private String bk_url;	
	private String fg_url;	
	private Integer bk_chk;	
	private Integer bk_date;	
	private String bk_seq1;	
	private String bk_seq2;	
	private String bk_resp;	
	private String mobile_no;	
	private Integer trans_period;	
	private String card_no;	
	private String error_code;	
	private Integer author_type;	
	private String phone_no;			
	private Integer oper_id;	
	private Integer gid;	
	private Integer pre_amt;	
	private String bk_fee_model;			
	private Integer pre_amt1;	
	private String error_msg;			
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
	private String p13;			
	private String p14;			
	private String p15;	
	private Integer is_liq;	
	private Integer is_notice;	
	private Integer data_source;
	private Integer againPay_status;
	private Integer againPay_date;
	
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


	@Override
	public String getTableName() {
		return Constant.HLOG;
	}

	public Long getTseq() {
		return tseq;
	}

	public void setTseq(Long tseq) {
		this.tseq = tseq;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getPay_amt() {
		return pay_amt;
	}

	public void setPay_amt(Long pay_amt) {
		this.pay_amt = pay_amt;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public Integer getSys_date() {
		return sys_date;
	}

	public void setSys_date(Integer sys_date) {
		this.sys_date = sys_date;
	}

	public Integer getInit_sys_date() {
		return init_sys_date;
	}

	public void setInit_sys_date(Integer init_sys_date) {
		this.init_sys_date = init_sys_date;
	}

	public Integer getSys_time() {
		return sys_time;
	}

	public void setSys_time(Integer sys_time) {
		this.sys_time = sys_time;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Integer getFee_amt() {
		return fee_amt;
	}

	public void setFee_amt(Integer fee_amt) {
		this.fee_amt = fee_amt;
	}

	public Integer getBank_fee() {
		return bank_fee;
	}

	public void setBank_fee(Integer bank_fee) {
		this.bank_fee = bank_fee;
	}

	public Integer getTstat() {
		return tstat;
	}

	public void setTstat(Integer tstat) {
		this.tstat = tstat;
	}

	public Integer getBk_flag() {
		return bk_flag;
	}

	public void setBk_flag(Integer bk_flag) {
		this.bk_flag = bk_flag;
	}

	public Long getOrg_seq() {
		return org_seq;
	}

	public void setOrg_seq(Long org_seq) {
		this.org_seq = org_seq;
	}

	public Long getRef_seq() {
		return ref_seq;
	}

	public void setRef_seq(Long ref_seq) {
		this.ref_seq = ref_seq;
	}

	public Long getRefund_amt() {
		return refund_amt;
	}

	public void setRefund_amt(Long refund_amt) {
		this.refund_amt = refund_amt;
	}

	public String getMer_priv() {
		return mer_priv;
	}

	public void setMer_priv(String mer_priv) {
		this.mer_priv = mer_priv;
	}

	public Integer getBk_send() {
		return bk_send;
	}

	public void setBk_send(Integer bk_send) {
		this.bk_send = bk_send;
	}

	public Integer getBk_recv() {
		return bk_recv;
	}

	public void setBk_recv(Integer bk_recv) {
		this.bk_recv = bk_recv;
	}

	public String getBk_url() {
		return bk_url;
	}

	public void setBk_url(String bk_url) {
		this.bk_url = bk_url;
	}

	public String getFg_url() {
		return fg_url;
	}

	public void setFg_url(String fg_url) {
		this.fg_url = fg_url;
	}

	public Integer getBk_chk() {
		return bk_chk;
	}

	public void setBk_chk(Integer bk_chk) {
		this.bk_chk = bk_chk;
	}

	public Integer getBk_date() {
		return bk_date;
	}

	public void setBk_date(Integer bk_date) {
		this.bk_date = bk_date;
	}

	public String getBk_seq1() {
		return bk_seq1;
	}

	public void setBk_seq1(String bk_seq1) {
		this.bk_seq1 = bk_seq1;
	}

	public String getBk_seq2() {
		return bk_seq2;
	}

	public void setBk_seq2(String bk_seq2) {
		this.bk_seq2 = bk_seq2;
	}

	public String getBk_resp() {
		return bk_resp;
	}

	public void setBk_resp(String bk_resp) {
		this.bk_resp = bk_resp;
	}

	public String getMobile_no() {
		return SqlGenerator.handleGetFuncSD(mobile_no);
	}

	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}

	public Integer getTrans_period() {
		return trans_period;
	}

	public void setTrans_period(Integer trans_period) {
		this.trans_period = trans_period;
	}

	public String getCard_no() {
		return SqlGenerator.handleGetFuncSD(card_no);
	}

	public void setCard_no(String card_no) {
		this.card_no =card_no;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public Integer getAuthor_type() {
		return author_type;
	}

	public void setAuthor_type(Integer author_type) {
		this.author_type = author_type;
	}

	public String getPhone_no() {
		return SqlGenerator.handleGetFuncSD(phone_no);
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public Integer getOper_id() {
		return oper_id;
	}

	public void setOper_id(Integer oper_id) {
		this.oper_id = oper_id;
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

	public String getBk_fee_model() {
		return bk_fee_model;
	}

	public void setBk_fee_model(String bk_fee_model) {
		this.bk_fee_model = bk_fee_model;
	}

	public Integer getPre_amt1() {
		return pre_amt1;
	}

	public void setPre_amt1(Integer pre_amt1) {
		this.pre_amt1 = pre_amt1;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public String getP1() {
		return  SqlGenerator.handleGetFuncSD(p1);
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

	public String getP11() {
		return p11;
	}

	public void setP11(String p11) {
		this.p11 = p11;
	}

	public String getP12() {
		return p12;
	}

	public void setP12(String p12) {
		this.p12 = p12;
	}

	public Integer getIs_liq() {
		return is_liq;
	}

	public void setIs_liq(Integer is_liq) {
		this.is_liq = is_liq;
	}

	public Integer getIs_notice() {
		return is_notice;
	}

	public void setIs_notice(Integer is_notice) {
		this.is_notice = is_notice;
	}

	public Integer getData_source() {
		return data_source;
	}

	public void setData_source(Integer data_source) {
		this.data_source = data_source;
	}

	public String getP13() {
		return p13;
	}

	public void setP13(String p13) {
		this.p13 = p13;
	}

	public String getP14() {
		return p14;
	}

	public void setP14(String p14) {
		this.p14 = p14;
	}

	public String getP15() {
		return p15;
	}

	public void setP15(String p15) {
		this.p15 = p15;
	}

}
