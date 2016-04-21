package com.rongyifu.mms.bean;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.utils.LogUtil;

public class Mlog {

	private Long tseq;
	private String mid;
	private Integer operId;
	private Integer sysDate;
	private String mobileNo;
	private String phoneNo;
	private String payCard;
	private String payId;
	private Long payAmount;
	private Integer tstat;
	private String chkNo;
	private Integer timePeriod;
	private Integer transType;
	private String field;
	private Integer fieldType;
    SystemDao sysDao = new SystemDao();
	
	public Integer getFieldType() {
		return fieldType;
	}

	public void setFieldType(Integer fieldType) {
		this.fieldType = fieldType;
	}

	public String getField() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,field);
	}

	public void setField(String field) {
		this.field = field;
	}

	public Integer getTransType() {
		return transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}

	public Long getTseq() {
		return tseq;
	}

	public void setTseq(Long tseq) {
		this.tseq = tseq;
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

	public Integer getSysDate() {
		return sysDate;
	}

	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}

	public String getMobileNo() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,mobileNo);
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = Ryt.empty(mobileNo) ? "" : mobileNo ;
	}

	public String getPhoneNo() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,phoneNo);
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPayCard() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,payCard);
	}

	
	MenuService menuService = new MenuService();

	public void setPayCard(String payCard) {
		this.payCard=payCard;

	}

	public String getPayId() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,payId);
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public Long getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Long payAmount) {
		this.payAmount = payAmount;
	}

	public Integer getTstat() {
		return tstat;
	}

	public void setTstat(Integer tstat) {
		this.tstat = tstat;
	}

	public String getChkNo() {
		return chkNo;
	}

	public void setChkNo(String chkNo) {
		this.chkNo = chkNo;
	}

	public Integer getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(Integer timePeriod) {
		this.timePeriod = timePeriod;
	}

    public String getField(int ex){
        return Ryt.desDec(field);
    }

}
