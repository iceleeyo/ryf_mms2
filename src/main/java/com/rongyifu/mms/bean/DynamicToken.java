package com.rongyifu.mms.bean;

import java.io.Serializable;
import java.util.Date;

import com.rongyifu.mms.utils.DateUtil;

public class DynamicToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 动态令牌启用
	 */
	public static final int STATUS_ACTIVE = 1;
	/**
	 * 动态令牌未启用
	 */
	public static final int STATUS_NOT_ACTIVE = 0;
	
	private Integer id;
	private Integer system;
	private String tokenSn;
	private String userName;
	private String mid;
	private Integer operId;
	private Integer status;
	private Date updateTime;
	private Date bindTime;
	private String operName;
	private Date disableTime;
	private Date enableTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSystem() {
		return system;
	}
	public void setSystem(Integer system) {
		this.system = system;
	}
	public String getTokenSn() {
		return tokenSn;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getOperId() {
		return operId;
	}
	public void setOperId(Integer operId) {
		this.operId = operId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public String getUpdateTimeStr() {
		String dateStr = "";
		if(null != updateTime){
			dateStr = DateUtil.format(updateTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getBindTime() {
		return bindTime;
	}
	public String getBindTimeStr() {
		String dateStr = "";
		if(null != bindTime){
			dateStr = DateUtil.format(bindTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public void setTokenSn(String tokenSn) {
		this.tokenSn = tokenSn;
	}
	public Date getDisableTime() {
		return disableTime;
	}
	public void setDisableTime(Date disableTime) {
		this.disableTime = disableTime;
	}
	public String getDisableTimeStr() {
		String dateStr = "";
		if(null != disableTime){
			dateStr = DateUtil.format(disableTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	public Date getEnableTime() {
		return enableTime;
	}
	public void setEnableTime(Date enableTime) {
		this.enableTime = enableTime;
	}	
	public String getEnableTimeStr() {
		String dateStr = "";
		if(null != enableTime){
			dateStr = DateUtil.format(enableTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
}
