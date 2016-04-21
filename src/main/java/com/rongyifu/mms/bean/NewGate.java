package com.rongyifu.mms.bean;

public class NewGate {
    private Integer id;
	private Integer gate;
	private Integer transMode;
	private Integer refundFlag;
	private Integer authorType;
	private Integer position;
	private Integer statFlag;
	private String gateDesc;
	private String gateDescShort;
	private Integer feeType ;
    private Integer feeRate ;
	private String gateId;
	private String bankPreHost;
	private String logoUrl;
    private Integer chainTransFailure;
    private String feeModel;//计费公式
    
    private Integer rytGate ;
    
    
	public Integer getRytGate() {
		return rytGate;
	}

	public void setRytGate(Integer rytGate) {
		this.rytGate = rytGate;
	}

	public Integer getChainTransFailure() {
        return chainTransFailure;
    }

    public void setChainTransFailure(Integer chainTransFailure) {
        this.chainTransFailure = chainTransFailure;
    }

    public Integer getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatFlag() {
        return statFlag;
    }

    public void setStatFlag(Integer statFlag) {
        this.statFlag = statFlag;
    }

    public Integer getAuthorType() {
		return authorType;
	}

	public void setAuthorType(Integer authorType) {
		this.authorType = authorType;
	}

	public String getBankPreHost() {
		return bankPreHost;
	}

	public void setBankPreHost(String bankPreHost) {
		this.bankPreHost = bankPreHost;
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


	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(Integer refundFlag) {
		this.refundFlag = refundFlag;
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
		  if(this.getTransMode() == 0){
			  this.gateDescShort = gateDescShort;
		  }else if(this.getTransMode() == 1){
			  this.gateDescShort = gateDescShort+"(M)";	
		  }else if (this.getTransMode() == 2){
			  this.gateDescShort = gateDescShort+"(B2B协议)";
		  }else if(this.getTransMode() == 3){
			  this.gateDescShort = gateDescShort+"(B2B网上)";
		  }else if(this.getTransMode() == 4){
			  this.gateDescShort = gateDescShort+"(WAP)";
		  }else{
			  this.gateDescShort = gateDescShort;
		  }
	}

	public String getGateDesc() {
		return gateDesc;
	}

	public void setGateDesc(String gateDesc) {
		this.gateDesc = gateDesc;
	}

	public String getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(String feeModel) {
		this.feeModel = feeModel;
	}
  
}
