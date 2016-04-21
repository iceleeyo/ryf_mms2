package com.rongyifu.mms.bean;
//用于结算确认和结算发起的交易明细和结算查询中的明细
public class AccSyncDetail {
	private String inUserId;//入账用户id
	private String type;
	private String seqNo;
	private String orderId;
	private Integer orderDate;
	private Long amount;
	private Long fee;
	
	
	public String getInUserId() {
		return inUserId;
	}
	public void setInUserId(String inUserId) {
		this.inUserId = inUserId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getFee() {
		return fee;
	}
	public void setFee(Long fee) {
		this.fee = fee;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Integer orderDate) {
		this.orderDate = orderDate;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
}
