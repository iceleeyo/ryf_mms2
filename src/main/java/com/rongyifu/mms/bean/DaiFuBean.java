package com.rongyifu.mms.bean;

import java.util.List;

public class DaiFuBean {
	
	
	
	private String bkNo;// 银行行号
	private String bkName;// 银行名称
	private List<DaiFu> sobjs;// 数据正确元素
	private List<DaiFu> fobjs;// 数据错误元素
	private String errMsg;// 错误信息
	private AccInfos acc;// 账户对象
	private boolean flag;//
	private int sum_lines;
	private  double sum_amt; //交易金额
	private double fee_amt;//手续费
	private BatchB2B batch;//B2B批量付款订单信息
	private long sumAmt;

	public BatchB2B getBatch() {
		return batch;
	}


	public void setBatch(BatchB2B batch) {
		this.batch = batch;
	}

	public double getSum_amt() {
		return sum_amt;
	}

	public void setSum_amt(double sum_amt) {
		this.sum_amt = sum_amt;
	}

	public double getFee_amt() {
		return fee_amt;
	}

	public void setFee_amt(double fee_amt) {
		this.fee_amt = fee_amt;
	}

	public int getSum_lines() {
		return sum_lines;
	}

	public void setSum_lines(int sum_lines) {
		this.sum_lines = sum_lines;
	}

	

	public Long getSumAmt() {
		return sumAmt;
	}

	public void setSumAmt(Long sumAmt) {
		this.sumAmt = sumAmt;
	}

	public String getBkNo() {
		return bkNo;
	}

	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}

	public String getBkName() {
		return bkName;
	}

	public void setBkName(String bkName) {
		this.bkName = bkName;
	}

	public List<DaiFu> getSobjs() {
		return sobjs;
	}

	public void setSobjs(List<DaiFu> sobjs) {
		this.sobjs = sobjs;
	}

	public List<DaiFu> getFobjs() {
		return fobjs;
	}

	public void setFobjs(List<DaiFu> fobjs) {
		this.fobjs = fobjs;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public AccInfos getAcc() {
		return acc;
	}

	public void setAcc(AccInfos acc) {
		this.acc = acc;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		if(flag) this.errMsg = "";
		this.flag = flag;
	}

	public DaiFuBean() {
		super();
		setErr(null);
	}
	
	public void setErr(String errMsg) {
		this.errMsg = errMsg;
		this.flag = false;
	}

}
