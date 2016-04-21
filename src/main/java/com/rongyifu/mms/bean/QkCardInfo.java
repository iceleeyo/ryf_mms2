package com.rongyifu.mms.bean;

import java.util.Date;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.DateUtil;

//快捷卡信息表
public class QkCardInfo {

	private String mid;
	private String abbrev;
	private String authCode;
	private String userId; 
	private Integer gateId; 
	private String cardType; 
	private String cardNo; 
	private String cardSuffix; 
	private String pidType;
	private String pidNo;
	private String phoneNo;
	private String cardName;
	private String period;
	private Date bindTime;
	private Date regiTime;
	private String code;
	private String dataSource;
	SystemDao sysDao = new SystemDao();
	
	public Date getRegiTime() {
		return regiTime;
	}
	public void setRegiTime(Date regiTime) {
		this.regiTime = regiTime;
	}
	
	public String getRegiTimeStr() {
		String dateStr = "";
		if(null != regiTime){
			dateStr = DateUtil.format(regiTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	
	
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getGateId() {
		return gateId;
	}
	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardNo() {
		 LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,Ryt.minfoGetHandle(cardNo));
	}
	public void setCardNo(String cardNo) {
		this.cardNo = Ryt.minfoSetHandle(cardNo);
	}
	public String getCardSuffix() {
		return cardSuffix;
	}
	public void setCardSuffix(String cardSuffix) {
		this.cardSuffix = cardSuffix;
	}
	public String getPidType() {
		return  Ryt.minfoGetHandle(pidType);
	}
	public void setPidType(String pidType) {
		this.pidType = Ryt.minfoSetHandle(pidType);
		//this.pidType = pidType;
	}
	public String getPidNo() {
		//return pidNo;
		LoginUser user=sysDao.getLoginUser();
		return	Ryt.getProperty(user,Ryt.minfoGetHandle(pidNo));
	}
	public void setPidNo(String pidNo) {
		//this.pidNo = pidNo;
		this.pidNo = Ryt.minfoSetHandle(pidNo);
	}
	public String getPhoneNo() {
		  LoginUser user=sysDao.getLoginUser();
	       // return Ryt.getProperty(user,phoneNo);
		return	Ryt.getProperty(user,Ryt.minfoGetHandle(phoneNo));
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = Ryt.minfoSetHandle(phoneNo);
	}
	public String getCardName() {
		//return cardName;
		return	Ryt.minfoGetHandle(cardName);
	}
	public void setCardName(String cardName) {
		//this.cardName = cardName;
		this.cardName = Ryt.minfoSetHandle(cardName);
	}
	public String getPeriod() {
		//return period;
		return	Ryt.minfoGetHandle(period);
	}
	public void setPeriod(String period) {
		//this.period = period;
		this.period = Ryt.minfoSetHandle(period);
	}
	public Date getBindTime() {
		return bindTime;
	}
	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
	}
	public String getBindTimeStr() {
		String dateStr = "";
		if(null != bindTime){
			dateStr = DateUtil.format(bindTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getAbbrev() {
		return abbrev;
	}
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
}