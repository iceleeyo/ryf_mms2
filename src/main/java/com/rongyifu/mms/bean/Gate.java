package com.rongyifu.mms.bean;

public class Gate {

	private Integer id; // 主键
	private Integer rytGate;// 融易通网关号
	private Integer statFlag;// 状态标志
	private Integer transMode;// 交易方式
	private String gateDescShort;
	private Integer refundFlag;// 是否支持联机退款
	private String gateDesc;
	private Integer authorType;// 发起类型
	private String gateId;
	private String feeModel;// 计费公式
	private Integer feeFlag; //银行是否退还手续费
	private Integer gid;//支付渠道
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRytGate() {
		return rytGate;
	}

	public void setRytGate(Integer rytGate) {
		this.rytGate = rytGate;
	}

	public Integer getStatFlag() {
		return statFlag;
	}

	public void setStatFlag(Integer statFlag) {
		this.statFlag = statFlag;
	}

	public Integer getTransMode() {
		return transMode;
	}

	public void setTransMode(Integer transMode) {
		this.transMode = transMode;
	}

	public String getGateDescShort() {
		return gateDescShort;
	}

	public void setGateDescShort(String gateDescShort) {
		this.gateDescShort = gateDescShort;
	}

	public Integer getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(Integer refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getGateDesc() {
		return gateDesc;
	}

	public void setGateDesc(String gateDesc) {
		this.gateDesc = gateDesc;
	}

	public Integer getAuthorType() {
		return authorType;
	}

	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}

	public String getGateId() {
		return gateId;
	}

	public void setGateId(String gateId) {
		this.gateId = gateId;
	}

	public String getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(String feeModel) {
		this.feeModel = feeModel;
	}

	public Integer getFeeFlag() {
		return feeFlag;
	}

	public void setFeeFlag(Integer feeFlag) {
		this.feeFlag = feeFlag;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Gate() {
		super();
	}

}
