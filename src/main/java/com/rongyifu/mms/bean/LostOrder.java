package com.rongyifu.mms.bean;

public class LostOrder {

	private Integer transMode;// 交易方式
	private Integer authorType;// 发起类型
	private String calcMode;// 商户计费公式
	private String bkFeeMode;// 银行计费公式
	private String gateId;// 第三方网关号
	private Integer liqType;// 第三方网关号
	private String mid;//
	private Integer gate;//
	private Integer gid;//支付渠道
	private String  merFee; //商户手续费 
	public Integer getLiqType() {
		return liqType;
	}

	public void setLiqType(Integer liqType) {
		this.liqType = liqType;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public String getGateId() {
		return gateId;
	}

	public void setGateId(String gateId) {
		this.gateId = gateId;
	}

	public String getCalcMode() {
		return calcMode;
	}

	public void setCalcMode(String calcMode) {
		this.calcMode = calcMode;
	}

	public String getBkFeeMode() {
		return bkFeeMode;
	}

	public void setBkFeeMode(String bkFeeMode) {
		this.bkFeeMode = bkFeeMode;
	}

	public LostOrder() {
		super();
	}

	public Integer getTransMode() {
		return transMode;
	}

	public void setTransMode(Integer transMode) {
		this.transMode = transMode;
	}

	public Integer getAuthorType() {
		return authorType;
	}

	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getMerFee() {
		return merFee;
	}

	public void setMerFee(String merFee) {
		this.merFee = merFee;
	}


}
