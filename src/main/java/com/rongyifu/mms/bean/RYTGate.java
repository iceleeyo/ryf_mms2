package com.rongyifu.mms.bean;

public class RYTGate {

	private Integer gate;
	private Integer transMode;
	private Integer statFlag;
	private String gateName;
	
	
	

	public RYTGate() {
		super();
	}

	public RYTGate(Integer gate, String gateName, Integer transMode, Integer statFlag) {
		super();
		this.gate = gate;
		this.transMode = transMode;
		this.statFlag = statFlag;
		this.gateName = gateName;
	}

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public Integer getTransMode() {
		return transMode;
	}

	public void setTransMode(Integer transMode) {
		this.transMode = transMode;
	}

	public Integer getStatFlag() {
		return statFlag;
	}

	public void setStatFlag(Integer statFlag) {
		this.statFlag = statFlag;
	}

	public String getGateName() {

		return gateName;
	}

	public void setGateName(String gateName) {
		// 0– 网上支付
		// 1– 直连支付
		// 2– B2B协议支付交易
		// 3– B2B网上支付交易
		// 4– WAP支付交易
		// 5---手机充值卡交易
//		if (transMode == 0)
//			this.gateName = gateName + "(网银)";
//		else if (transMode == 1)
//			this.gateName = gateName + "(信用卡)";
//		else if (transMode == 2)
//			this.gateName = gateName + "(B2B协议)";
//		else if (transMode == 3)
//			this.gateName = gateName + "(B2B网银)";
//		else if (transMode == 4)
//			this.gateName = gateName + "(WAP)";
//		else if (transMode == 5)
//			this.gateName = gateName + "(充值卡)";
//		else
			this.gateName = gateName;
	}

}
