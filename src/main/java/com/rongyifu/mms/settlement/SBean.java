package com.rongyifu.mms.settlement;

public class SBean {

	private String gate;
	private String tseq;// 电银流水号的
	private String bkSeq;// 银行流水号
	private String amt;// 交易金额
	private String merOid;// 商户订单号
	
	private String date;// 日期
	/**
	 * 对账标记:
	 *     1	根据电银流水号进行对账
	 *     2	根据系统日期、支付渠道、银行流水号进行对账
	 *     3	根据银行网关、商户订单号进行对账
	 *     4	根据系统日期、支付渠道、特殊字段进行对账
	 *     5	根据商户日期/系统日期、银行流水号、银行网关进行对账
	 */
	private int flag = 0;
	private String bkFee;//银行手续费
	
	private String mid;//商户订单号
	private String p1;
	private String p2;
	public SBean() {
		super();
	}
	
	
	
	
	
	
	public String getBkFee() {
		return bkFee;
	}


	



	public void setBkFee(String bkFee) {
		this.bkFee = bkFee;
	}






	public String getMid() {
		return mid;
	}


	public void setMid(String mid) {
		this.mid = mid;
	}




	public String getDate() {
		return date;
	}




	public void setDate(String date) {
		this.date = date==null? null : date.trim();
	}




	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate==null? null : gate.trim();
	}

	public String getTseq() {
		return tseq;
	}

	public void setTseq(String tseq) {
		this.tseq = tseq==null? null : tseq.trim();
	}

	public String getBkSeq() {
		return bkSeq;
	}

	public void setBkSeq(String bkSeq) {
		this.bkSeq = bkSeq==null? null : bkSeq.trim();
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt==null? null : amt.trim();
	}

	public String getMerOid() {
		return merOid;
	}

	public void setMerOid(String merOid) {
		this.merOid = merOid==null? null : merOid.trim();
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
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
	
}
