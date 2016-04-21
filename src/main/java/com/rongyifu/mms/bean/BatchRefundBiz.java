package com.rongyifu.mms.bean;

import java.util.List;

public class BatchRefundBiz {

	private String mid; // 批量退款的商户
	private String reason; // 批量退款的原因
	private List<String[]> refData;// 申请退款金额
	private String errorMsg; // 错误信息

	private String data; // 错误信息
	
	private String totle; // 错误信息

	private List<BatchRefundBean> refundData;

	boolean flag;

	public BatchRefundBiz() {
		super();
	}

	public String getTotle() {
		return totle;
	}

	public void setTotle(String totle) {
		this.totle = totle;
	}

	public List<BatchRefundBean> getRefundData() {
		return refundData;
	}

	public void setRefundData(List<BatchRefundBean> refundData) {
		this.refundData = refundData;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<String[]> getRefData() {
		return refData;
	}

	public void setRefData(List<String[]> refData) {
		this.refData = refData;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
