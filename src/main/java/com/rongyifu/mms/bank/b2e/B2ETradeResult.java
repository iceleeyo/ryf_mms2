package com.rongyifu.mms.bank.b2e;

import java.util.HashMap;
import java.util.Map;

public class B2ETradeResult {
	
	private String oid;
	private String bankSeq;
	private String amt;
	// 2-交易成功
	// 6-付款失败
	private int state;
	private String errMsg;
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getBankSeq() {
		return bankSeq;
	}
	public void setBankSeq(String bankSeq) {
		this.bankSeq = bankSeq;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
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
	public B2ETradeResult() {
		super();
	}
	
	public B2ETradeResult(String oid, String bankSeq, int state, String errMsg) {
		super();
		this.oid = oid;
		this.bankSeq = bankSeq;
		this.state = state;
		this.errMsg = errMsg;
	}
	
	public Map<String, String> getKvMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("B2ETradeResult.oid", oid);
		map.put("B2ETradeResult.bankSeq", bankSeq);
		map.put("B2ETradeResult.amt", amt);
		map.put("B2ETradeResult.state", String.valueOf(state));
		map.put("B2ETradeResult.errMsg", errMsg);
		
		return map;
	}
}
