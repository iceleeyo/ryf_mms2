package com.rongyifu.mms.bean;

import java.io.Serializable;
import java.util.Map;

import com.rongyifu.mms.dao.MerInfoDao;



/**
 * @author yzy
 *
 */
public class LoginUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mid;
	private Integer operId;
	private String operName;
	private Integer state;
	private String logined;
	private Integer mtype;
	private Integer expDate;
	private String abbrev;
	private String auth;
	private Integer role;
	private Integer operState;
	private String md5Pwd;
	
	private Integer errCount;
	private Integer lastUpdat;
	private Long errTime;
	private String antiPhishingStr;
	private String lastLoginIp;
	
	/**
	 * 是否需要修改密码
	 */
	private boolean isUpdatePwd = false;
	
	/**
	 * 动态令牌是否启用，默认false
	 */
	private boolean isTokenActive = false;
	/**
	 * 动态令牌是否验证成功，默认false
	 */
	private boolean isTokenVerifySuccess = false;
	
	public String getAntiPhishingStr() {
		return antiPhishingStr;
	}

	public void setAntiPhishingStr(String antiPhishingStr) {
		this.antiPhishingStr = antiPhishingStr;
	}

	public Long getErrTime() {
		return errTime;
	}

	public void setErrTime(Long errTime) {
		this.errTime = errTime;
	}

	public Integer getErrCount() {
		return errCount;
	}

	public void setErrCount(Integer errCount) {
		this.errCount = errCount;
	}

	public Integer getLastUpdat() {
		return lastUpdat;
	}

	public void setLastUpdat(Integer lastUpdat) {
		this.lastUpdat = lastUpdat;
	}



	public String getMd5Pwd() {
		return md5Pwd;
	}

	public void setMd5Pwd(String md5Pwd) {
		this.md5Pwd = md5Pwd;
	}

	public Integer getOperState() {
		return operState;
	}

	public void setOperState(Integer operState) {
		this.operState = operState;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public LoginUser() {
		super();
	}
	
	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
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
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getLogined() {
		return logined;
	}
	public void setLogined(String logined) {
		this.logined = logined;
	}
	public Integer getMtype() {
		return mtype;
	}
	public void setMtype(Integer mtype) {
		this.mtype = mtype;
	}
	public Integer getExpDate() {
		return expDate;
	}
	public void setExpDate(Integer expDate) {
		this.expDate = expDate;
	}
	
//	public  String getBalance() {
//		int blance = new MerInfoDao().getMinfoBalance(this.mid);
//		return Ryt.div100(blance);
//	}
	
	public  Map<Integer,String> getThisMerOpers() {
		return new MerInfoDao().getAllopersMap(this.mid);
	}

	public boolean isUpdatePwd() {
		return isUpdatePwd;
	}

	public void setUpdatePwd(boolean isUpdatePwd) {
		this.isUpdatePwd = isUpdatePwd;
	}

	public boolean isTokenActive() {
		return isTokenActive;
	}

	public void setTokenActive(boolean isTokenActive) {
		this.isTokenActive = isTokenActive;
	}

	public boolean isTokenVerifySuccess() {
		return isTokenVerifySuccess;
	}

	public void setTokenVerifySuccess(boolean isTokenVerifySuccess) {
		this.isTokenVerifySuccess = isTokenVerifySuccess;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

}
