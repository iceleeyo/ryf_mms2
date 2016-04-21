package com.rongyifu.mms.bean;

/**
 * 代付网关配置
 */
public class DfGateRoute {
	private Integer id;
	private Short type;
	private Integer gateId;
	private Integer gid;
	private Short limitType;
	private Integer upperLimit;
	private Integer lowerLimit;
	private Short configType;
	
	private String gateName;//网关名
	private String routeName;//渠道名
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
	}
	public Integer getGateId() {
		return gateId;
	}
	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public Short getLimitType() {
		return limitType;
	}
	public void setLimitType(Short limitType) {
		this.limitType = limitType;
	}
	public Integer getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(Integer upperLimit) {
		this.upperLimit = upperLimit;
	}
	public Integer getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(Integer lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public Short getConfigType() {
		return configType;
	}
	public void setConfigType(Short configType) {
		this.configType = configType;
	}
	public String getGateName() {
		return gateName;
	}
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
}
