package com.rongyifu.mms.bean;

public class BatchRefundBean {

	private String initMid;// 申请退款商户号
	private String initMerOid;// 申请退款商户订单号
	private String initMdate;// 申请退款商户日期
	private String initTseq; // 原订单流水号
	private String initRefAmt;// 申请退款金额
	private int state;// 申请退款结果，0 成功，1失败
	private String errMsg;// 申请退款失败的原因

	public BatchRefundBean() {
		super();
	}

	public String getInitMid() {
		return initMid;
	}

	public void setInitMid(String initMid) {
		this.initMid = initMid == null ? "" : initMid;
	}

	public String getInitMerOid() {
		return initMerOid;

	}

	public void setInitMerOid(String initMerOid) {

		this.initMerOid = initMerOid == null ? "" : initMerOid;
	}

	public String getInitMdate() {
		return initMdate;
	}

	public void setInitMdate(String initMdate) {
		this.initMdate = initMdate == null ? "" : initMdate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public BatchRefundBean(String initTseq, String initRefAmt) {
		super();
		this.initTseq = initTseq;
		this.initRefAmt = initRefAmt;
	}

	public BatchRefundBean(String initMid, String initMerOid, String initMdate, String initRefAmt) {
		super();
		this.initMid = initMid;
		this.initMerOid = initMerOid;
		this.initMdate = initMdate;
		this.initRefAmt = initRefAmt;
	}

	public String getInitTseq() {
		return initTseq;
	}

	public void setInitTseq(String initTseq) {
		this.initTseq = initTseq == null ? "" : initTseq;
	}

	public String getInitRefAmt() {
		return initRefAmt;
	}

	public void setInitRefAmt(String initRefAmt) {
		this.initRefAmt = initRefAmt;
	}

}
