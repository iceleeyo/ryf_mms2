package com.rongyifu.mms.bean;

public class FeeCalcMode {
	
	private Integer id;
	private String mid;
	private Integer gate;
	private String calcMode;
	private String gateName;
	private String gateId;
	private String bkFeeMode;
	private Integer transMode;
	private Integer authorType;
	private Integer state;//当前状态
	private Integer toState;//开启还是关闭
	private Integer gid;
	private Integer type;//申请类型
	private Integer status;//申请状态
	private Long effectiveTime;//生效时间
	private String toCalcMode;//申请的计费模式
	private Integer toGid;//申请的计费模式
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getToCalcMode() {
		return toCalcMode;
	}

	public void setToCalcMode(String toCalcMode) {
		this.toCalcMode = toCalcMode;
	}

	public Integer getToGid() {
		return toGid;
	}

	public void setToGid(Integer toGid) {
		this.toGid = toGid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getToState() {
		return toState;
	}

	public void setToState(Integer toState) {
		this.toState = toState;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Long effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Integer getAuthorType() {
		return authorType;
	}

	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}

	public String getBkFeeMode() {
		return bkFeeMode;
	}

	public void setBkFeeMode(String bkFeeMode) {
		this.bkFeeMode = bkFeeMode;
	}

	public String getGateId() {
		return gateId;
	}

	public void setGateId(String gateId) {
		this.gateId = gateId;
	}

	public Integer getTransMode() {
		return transMode;
	}

	public void setTransMode(Integer transMode) {
		this.transMode = transMode;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
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

	public FeeCalcMode() {
		super();
	}

	public String getCalcMode() {
		return calcMode;
	}

	public void setCalcMode(String calcMode) {
		this.calcMode = calcMode;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Override
	public String toString(){
		
		return this.getBkFeeMode()+this.getCalcMode()+this.getGateName();
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}
	
}
