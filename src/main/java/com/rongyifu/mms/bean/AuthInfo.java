package com.rongyifu.mms.bean;

import java.util.List;

public class AuthInfo {
	
	private Integer authId;
	private Integer authLevel;
	private Integer parentId;
	private Integer haveSon;
	private Integer authPriority;
	private String authDesc;
	private String authDescEng;
	private Integer state;
	private Integer openToMer;
	private String action;
	private Integer isShown;
	private Integer authIndex;
	private Integer authType;
	
	private List<AuthInfo> childs;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<AuthInfo> getChilds() {
		return childs;
	}
	public void setChilds(List<AuthInfo> childs) {
		this.childs = childs;
	}
	public Integer getAuthId() {
		return authId;
	}
	public void setAuthId(Integer authId) {
		this.authId = authId;
	}
	public Integer getAuthLevel() {
		return authLevel;
	}
	public void setAuthLevel(Integer authLevel) {
		this.authLevel = authLevel;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getHaveSon() {
		return haveSon;
	}
	public void setHaveSon(Integer haveSon) {
		this.haveSon = haveSon;
	}
	public Integer getAuthPriority() {
		return authPriority;
	}
	public void setAuthPriority(Integer authPriority) {
		this.authPriority = authPriority;
	}
	public String getAuthDesc() {
		return authDesc;
	}
	public void setAuthDesc(String authDesc) {
		this.authDesc = authDesc;
	}
	public String getAuthDescEng() {
		return authDescEng;
	}
	public void setAuthDescEng(String authDescEng) {
		this.authDescEng = authDescEng;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getOpenToMer() {
		return openToMer;
	}
	public void setOpenToMer(Integer openToMer) {
		this.openToMer = openToMer;
	}
	public Integer getIsShown() {
		return isShown;
	}
	public void setIsShown(Integer isShown) {
		this.isShown = isShown;
	}
	public Integer getAuthIndex() {
		return authIndex;
	}
	public void setAuthIndex(Integer authIndex) {
		this.authIndex = authIndex;
	}
	public Integer getAuthType() {
		return authType;
	}
	public void setAuthType(Integer authType) {
		this.authType = authType;
	}
	
	
}