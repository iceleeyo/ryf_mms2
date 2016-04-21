package com.rongyifu.mms.bean;

import java.io.Serializable;

public class OperLog implements Serializable{
	private Long id;
	private String mid;
	private Integer sysDate;
	private Integer sysTime;
	private Integer operId;
	private String action;
	private String actionDesc;
	private Integer mtype;
	
	private String name;
	private String oper_name;
	private String operIp;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOper_name() {
		return oper_name;
	}
	public void setOper_name(String oper_name) {
		this.oper_name = oper_name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getMtype() {
		return mtype;
	}
	public void setMtype(Integer mtype) {
		this.mtype = mtype;
	}
	public Integer getOperId() {
		return operId;
	}
	public void setOperId(Integer operId) {
		this.operId = operId;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
	public Integer getSysTime() {
		return sysTime;
	}
	public void setSysTime(Integer sysTime) {
		this.sysTime = sysTime;
	}
	public String getOperIp() {
		return operIp;
	}
	public void setOperIp(String operIp) {
		this.operIp = operIp;
	}
}
