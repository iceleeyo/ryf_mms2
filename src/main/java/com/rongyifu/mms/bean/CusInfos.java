package com.rongyifu.mms.bean;

import java.util.List;

public class CusInfos {
	private String cid;
	private Integer ctype;
	private String cname;
	private Integer tradeType;
	private List<UserBkAcc> userBkAccSet;
	private List<CusContactInfos> cusContactInfosSet;
	
	public List<UserBkAcc> getUserBkAccSet() {
		return userBkAccSet;
	}
	public void setUserBkAccSet(List<UserBkAcc> userBkAccSet) {
		this.userBkAccSet = userBkAccSet;
	}
	public List<CusContactInfos> getCusContactInfosSet() {
		return cusContactInfosSet;
	}
	public void setCusContactInfosSet(List<CusContactInfos> cusContactInfosSet) {
		this.cusContactInfosSet = cusContactInfosSet;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public Integer getCtype() {
		return ctype;
	}
	public void setCtype(Integer ctype) {
		this.ctype = ctype;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Integer getTradeType() {
		return tradeType;
	}
	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
}
