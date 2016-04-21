package com.rongyifu.mms.bank.b2e;

import java.util.HashMap;
import java.util.Map;


public class B2ERet {
	//解析XML的结果
	private Object result;
	//银企直连网关号
	private int gid;
	//错误信息
	private String msg;
	//交易成功标识
	private boolean succ;
	//银行返回的交易码
	private String trCode;
	// 交易结果: 0 成功；1 失败；2 中间状态（需要通过查询接口来确认交易结果）
	private int transStatus;
	// 状态信息
	private String statusInfo;
	//银行返回错误信息
	private String errorMsg;
	//银行返回的应答码
	private String res_code;
	//银行返回的时间
	private String bank_time;
	//银行返回的日期
	private String bank_date; 
	
	public String getTrCode() {
		return trCode;
	}

	public void setTrCode(String trCode) {
		this.trCode = trCode;
	}

	public void setErr(String errMsg){
		this.succ = true;
		this.transStatus=2;
		this.msg = errMsg;
		this.result = null;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public B2ERet() {
	}

	public int getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(int transStatus) {
		this.transStatus = transStatus;
	}

	public String getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getRes_code() {
		return res_code;
	}

	public void setRes_code(String res_code) {
		this.res_code = res_code;
	}

	public String getBank_time() {
		return bank_time;
	}

	public void setBank_time(String bank_time) {
		this.bank_time = bank_time;
	}

	public String getBank_date() {
		return bank_date;
	}

	public void setBank_date(String bank_date) {
		this.bank_date = bank_date;
	}
	
	public Map<String, String> getKvMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("B2ERet.gid", String.valueOf(gid));
		map.put("B2ERet.msg", msg);
		map.put("B2ERet.succ", String.valueOf(succ));
		map.put("B2ERet.trCode", trCode);
		map.put("B2ERet.transStatus", String.valueOf(transStatus));
		map.put("B2ERet.statusInfo", statusInfo);
		map.put("B2ERet.errorMsg", errorMsg);
		map.put("B2ERet.res_code", res_code);
		map.put("B2ERet.bank_time", bank_time);
		map.put("B2ERet.bank_date", bank_date);
		
		if(result != null && result instanceof B2ETradeResult){
			B2ETradeResult ret = (B2ETradeResult) result;
			map.putAll(ret.getKvMap());
		}
		
		return map;
	}
}
