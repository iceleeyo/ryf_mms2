package com.rongyifu.mms.bean;

public class DailySheet {
	private String mid;            //商户号
	private int liqDate;        //结算日期
	private int liqType;        //结算方式
	private String abbrev;       //商户简称
	private Long netPay;        //网上支付交易
	private Long debitcardPay;  //借记卡支付交易
	private Long creditcardPay ;//creditcardPay; //信用卡支付交易creditcard_pay
	private Long btobPay;       //  交通银行BTOB  改为 语音支付
	private Long manualAdd;     //手工增加
	private Long manualSub;     //手工减少
	private Long refund;        //退款
	private Long feeAmt;        //系统手续费
	private Long bankFee;       //银行手续费
	private Long wapPay;       //WAP交易
	private Long refFee;		//系统退回手续费
	private Long bkRefFee;	//银行退回手续费

	public Long getCreditcardPay() {
		return creditcardPay == null ? 0 : creditcardPay;
	}
	public void setCreditcardPay(Long creditcardPay) {
		this.creditcardPay = creditcardPay;
	}
	public Long getDebitcardPay() {
		return debitcardPay == null ? 0 : debitcardPay;
	}
	public void setDebitcardPay(Long debitcardPay) {
		this.debitcardPay = debitcardPay;
	}
	public Long getWapPay() {
		return wapPay==null? 0 :wapPay;
	}
	public void setWapPay(Long wapPay) {
		this.wapPay = wapPay;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public int getLiqDate() {
		return liqDate;
	}
	public void setLiqDate(int liqDate) {
		this.liqDate = liqDate;
	}
	public int getLiqType() {
		return liqType;
	}
	public void setLiqType(int liqType) {
		this.liqType = liqType;
	}
	public String getAbbrev() {
		return abbrev;
	}
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	public Long getNetPay() {
		return netPay==null? 0 : netPay;
	}
	public void setNetPay(Long netPay) {
		this.netPay = netPay;
	}
	public Long getBtobPay() {
		return btobPay==null? 0 :btobPay;
	}
	public void setBtobPay(Long btobPay) {
		this.btobPay = btobPay;
	}
	public Long getManualAdd() {
		return manualAdd==null? 0 :manualAdd;
	}
	public void setManualAdd(Long manualAdd) {
		this.manualAdd = manualAdd;
	}
	public Long getManualSub() {
		return manualSub==null? 0 :manualSub;
	}
	public void setManualSub(Long manualSub) {
		this.manualSub = manualSub;
	}
	public Long getRefund() {
		return refund==null? 0 :refund;
	}
	public void setRefund(Long refund) {
		this.refund = refund;
	}
	public Long getFeeAmt() {
		return feeAmt==null? 0 :feeAmt;
	}
	public void setFeeAmt(Long feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Long getBankFee() {
		return bankFee==null? 0 :bankFee;
	}
	public void setBankFee(Long bankFee) {
		this.bankFee = bankFee;
	}
	public Long getRefFee() {
		return refFee==null? 0 :refFee;
	}
	public void setRefFee(Long refFee) {
		this.refFee = refFee;
	}
	public Long getBkRefFee() {
		return bkRefFee==null? 0 :bkRefFee;
	}
	public void setBkRefFee(Long bkRefFee) {
		this.bkRefFee = bkRefFee;
	}
	
}
