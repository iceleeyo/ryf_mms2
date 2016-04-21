package com.rongyifu.mms.bean;

public class StartType {

	private Integer type;
	private String name;
	
	public StartType(Integer type,String name){
		this.type=type;
		this.name=name;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
