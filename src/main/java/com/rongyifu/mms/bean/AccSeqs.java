package com.rongyifu.mms.bean;

public class AccSeqs {
	private String uid;
	private String aid;
	private Integer trDate;
	private Integer trTime;
	private long trAmt;
	private int trFee;
	private long amt;
	private short recPay;
	private String trFlag;
	private String tbName;
	private String tbId;// 流水号
	private String remark;
	private long allBalance; // 未结算金额
	private long balance; // 可用余额
	private String trans_flow;// 关联订单表 获取的批次号
	private String oid; // 关联订单表获取的订单号
	private short ptype;
	private long init_time;// 订单初始化日期
    private String abbrev;//商户简称
	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public long getInit_time() {
		return init_time;
	}

	public void setInit_time(long init_time) {
		this.init_time = init_time;
	}

	public String getTrans_flow() {
		return trans_flow;
	}

	public void setTrans_flow(String trans_flow) {
		this.trans_flow = trans_flow;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public short getPtype() {
		return ptype;
	}

	public void setPtype(short ptype) {
		this.ptype = ptype;
	}

	public long getTrAmt() {
		return trAmt;
	}

	public void setTrAmt(long trAmt) {
		this.trAmt = trAmt;
	}

	public int getTrFee() {
		return trFee;
	}

	public void setTrFee(int trFee) {
		this.trFee = trFee;
	}

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	public String getTbId() {
		return tbId;
	}

	public void setTbId(String tbId) {
		this.tbId = tbId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public long getAmt() {
		return amt;
	}

	public void setAmt(long amt) {
		this.amt = amt;
	}

	public short getRecPay() {
		return recPay;
	}

	public void setRecPay(short recPay) {
		this.recPay = recPay;
	}

	public String getTrFlag() {
		return trFlag;
	}

	public void setTrFlag(String trFlag) {
		this.trFlag = trFlag;
	}

	public long getAllBalance() {
		return allBalance;
	}

	public void setAllBalance(long allBalance) {
		this.allBalance = allBalance;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

}
