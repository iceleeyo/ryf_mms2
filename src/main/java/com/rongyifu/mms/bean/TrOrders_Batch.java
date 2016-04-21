package com.rongyifu.mms.bean;

public class TrOrders_Batch {
	private String uid;
	private String aid;
	private String aname;
	private int count;
	private double trans_sum_amt;//总的交易金额
	private double trans_sum_fee;//总的手续费
	private double trans_sum_payamt;//结算总金额
	private String trans_proof;//付款凭证
	private String trans_flow;
	private String oid;
	private short ptype;
	private String sysDate;
	private Integer state;
	private double recharge_amt;
	private Integer pstate;
	public Integer getPstate() {
		return pstate;
	}
	public void setPstate(Integer pstate) {
		this.pstate = pstate;
	}
	public double getRecharge_amt() {
		return recharge_amt;
	}
	public void setRecharge_amt(double recharge_amt) {
		this.recharge_amt = recharge_amt;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getTrans_flow() {
		return trans_flow;
	}
	public void setTrans_flow(String trans_flow) {
		this.trans_flow = trans_flow;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public short getPtype() {
		return ptype;
	}
	public void setPtype(short ptype) {
		this.ptype = ptype;
	}
	public String getSysDate() {
		return sysDate;
	}
	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}
	public double getTrans_sum_payamt() {
		return trans_sum_payamt;
	}
	public void setTrans_sum_payamt(double trans_sum_payamt) {
		this.trans_sum_payamt = trans_sum_payamt;
	}
	public String getTrans_proof() {
		return trans_proof;
	}
	public void setTrans_proof(String trans_proof) {
		this.trans_proof = trans_proof;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getTrans_sum_amt() {
		return trans_sum_amt;
	}
	public void setTrans_sum_amt(double trans_sum_amt) {
		this.trans_sum_amt = trans_sum_amt;
	}
	public double getTrans_sum_fee() {
		return trans_sum_fee;
	}
	public void setTrans_sum_fee(double trans_sum_fee) {
		this.trans_sum_fee = trans_sum_fee;
	}
	
}
