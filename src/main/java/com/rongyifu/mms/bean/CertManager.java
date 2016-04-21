package com.rongyifu.mms.bean;

import java.util.Date;

import com.rongyifu.mms.utils.DateUtil;

public class CertManager {
	
	private String mid;
	private String algorithm;
	private String subject;
	private Integer notBefore;
	private Integer notAfter;
	private Date importTime;
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public String getNotBeforeStr() {
		String dateStr = "";
		if(null != notBefore && notBefore != 0){
			dateStr = DateUtil.formatDate(notBefore);
		}
		return dateStr;
	}
	public void setNotBefore(Integer notBefore) {
		this.notBefore = notBefore;
	}
	public String getNotAfterStr() {
		String dateStr = "";
		if(null != notAfter && notAfter != 0){
			dateStr = DateUtil.formatDate(notAfter);
		}
		return dateStr;
	}
	public void setNotAfter(Integer notAfter) {
		this.notAfter = notAfter;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getImportTimeStr() {
		String dateStr = "";
		if(null != importTime){
			dateStr = DateUtil.format(importTime, "yyyy-MM-dd HH:mm:ss");
		}
		return dateStr;
	}
	public Date getImportTime() {
		return importTime;
	}
	public void setImportTime(Date importTime) {
		this.importTime = importTime;
	}
	public Integer getNotBefore() {
		return notBefore;
	}
	public Integer getNotAfter() {
		return notAfter;
	}
}
