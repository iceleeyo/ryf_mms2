package com.rongyifu.mms.bean;

public class SettleHlog {

	private long tseq;     //融易通流水号
	private int amount;  //融易通实际交易金额
	private int payAmt;  //融易通实际交易金额
	private int gate;	 //交易网关
	private int bkChk;	 //银行对帐标志
	private int tstat;   //交易状态0–初始状态1–待支付2–成功3–失败
	private String bkFeeModel;
	private String oid;
	private Integer gid;
	
	
    private String bkSeq; //银行流水号
    private String bkAmount; //银行实际交易金额
    

    
    
    
	public int getPayAmt() {
		return payAmt;
	}



	public void setPayAmt(int payAmt) {
		this.payAmt = payAmt;
	}



	public String getOid() {
		return oid;
	}



	public void setOid(String oid) {
		this.oid = oid;
	}



	public SettleHlog(String bkSeq, String bkAmount) {
		super();
		this.bkSeq = bkSeq;
		this.bkAmount = bkAmount;
	}
	
	

	public long getTseq() {
		return tseq;
	}


	public void setTseq(long tseq) {
		this.tseq = tseq;
	}


	public SettleHlog() {
		super();
	}


	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}


	public int getGate() {
		return gate;
	}

	public void setGate(int gate) {
		this.gate = gate;
	}

	public int getBkChk() {
		return bkChk;
	}

	public void setBkChk(int bkChk) {
		this.bkChk = bkChk;
	}

	public int getTstat() {
		return tstat;
	}

	public void setTstat(int tstat) {
		this.tstat = tstat;
	}

	public String getBkSeq() {
		return bkSeq;
	}

	public void setBkSeq(String bkSeq) {
		this.bkSeq = bkSeq;
	}

	public String getBkAmount() {
		return bkAmount;
	}

	public void setBkAmount(String bkAmount) {
		this.bkAmount = bkAmount;
	}



	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getBkFeeModel() {
		return bkFeeModel;
	}

	public void setBkFeeModel(String bkFeeModel) {
		this.bkFeeModel = bkFeeModel;
	}


}
