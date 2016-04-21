package com.rongyifu.mms.bean;

public class GlobalParams {
	private Integer parId;
	
	private String parName;
	
	private String parValue;
	
	private String parDesc;

	private Integer parEdit;

	public String getParDesc() {
		return parDesc;
	}

	public Integer getParEdit() {
		return parEdit;
	}

	public Integer getParId() {
		return parId;
	}

	public String getParName() {
		return parName;
	}

	public String getParValue() {
		return parValue;
	}

	public void setParDesc(String parDesc) {
		this.parDesc = parDesc;
	}

	public void setParEdit(Integer parEdit) {
		this.parEdit = parEdit;
	}

	public void setParId(Integer parId) {
		this.parId = parId;
	}

	public void setParName(String parName) {
		this.parName = parName;
	}
	
	public void setParValue(String parValue) {
		this.parValue = parValue;
	}
}
