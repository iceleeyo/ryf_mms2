package com.rongyifu.mms.bean;

import com.rongyifu.mms.common.Ryt;

public class FeeLiqBath {

	// Fields

	private Integer id;
	private Integer liqDate;
	private String lastBatch;
	private Integer lastLiqDate;
	private Integer genDate;
	private String mid;
	private Long transAmt;
	private Long refAmt;
	private Integer feeAmt;
	private Long liqAmt;
	private Integer remiDate;
	private Short liqCond;
	private String liqLimit;
	private Short liqType;
	private Short state;
	private String batch;
	private Long manualAdd;
	private Long manualSub;
	private Integer purCnt;
	private Integer refCnt;
	private Integer addCnt;
	private Integer subCnt;
	private Integer refFee;
	private String abbrev;

	private Integer tabDate;//制表日期
	private String bankAcctName;//银行账户名 minfo
	private String bankAcct;//银行开户帐号 minfo
	private String aid;//账户号acc_infos
	private String aname;//账户名 acc_infos
	private Integer liqObj;//结算对象
	private Short category;//商户类别
	private Short liqGid;//结算渠道
	private Integer gid;//支付渠道
	private String dlsCode;//代理商号
	private Integer isTodayLiq;//是否T+0结算
	private String userId;//账户系统返回的user_id
	private Long accMdate;//账户系统同步时间
	private Short accState;//账户系统同步状态
	private String accNotice;//账户系统返回的原因 (错误时才有)
	private String merName;//商户名称
	
	
	
	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Short getLiqGid() {
		return liqGid;
	}

	public void setLiqGid(Short liqGid) {
		this.liqGid = liqGid;
	}

	public Short getCategory() {
		return category;
	}

	public void setCategory(Short category) {
		this.category = category;
	}
	
	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public Integer getTabDate() {
		return tabDate;
	}

	public void setTabDate(Integer tabDate) {
		this.tabDate = tabDate;
	}

	public String getBankAcctName() {
		return bankAcctName;
	}

	public void setBankAcctName(String bankAcctName) {
		this.bankAcctName = bankAcctName;
	}

	public String getBankAcct() {
		return Ryt.minfoGetHandle(bankAcct);
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = Ryt.minfoSetHandle(bankAcct);
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public Integer getLiqObj() {
		return liqObj;
	}

	public void setLiqObj(Integer liqObj) {
		this.liqObj = liqObj;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	private String bankName;//银行名称
	private String bankBranch;//支行名称
	// Constructors

	/** default constructor */
	public FeeLiqBath() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLiqDate() {
		return liqDate;
	}

	public void setLiqDate(Integer liqDate) {
		this.liqDate = liqDate;
	}


	public Integer getLastLiqDate() {
		return lastLiqDate;
	}

	public void setLastLiqDate(Integer lastLiqDate) {
		this.lastLiqDate = lastLiqDate;
	}

	public Integer getGenDate() {
		return genDate;
	}

	public void setGenDate(Integer genDate) {
		this.genDate = genDate;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Long getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Long transAmt) {
		this.transAmt = transAmt;
	}

	public Long getRefAmt() {
		return refAmt;
	}

	public void setRefAmt(Long refAmt) {
		this.refAmt = refAmt;
	}

	public Integer getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}

	public Long getLiqAmt() {
		return liqAmt;
	}

	public void setLiqAmt(Long liqAmt) {
		this.liqAmt = liqAmt;
	}

	public Integer getRemiDate() {
		return remiDate;
	}

	public void setRemiDate(Integer remiDate) {
		this.remiDate = remiDate;
	}

	public Short getLiqCond() {
		return liqCond;
	}

	public void setLiqCond(Short liqCond) {
		this.liqCond = liqCond;
	}

	public String getLiqLimit() {
		return liqLimit;
	}

	public void setLiqLimit(String liqLimit) {
		this.liqLimit = liqLimit;
	}

	public Short getLiqType() {
		return liqType;
	}

	public void setLiqType(Short liqType) {
		this.liqType = liqType;
	}

	public Short getState() {
		return state;
	}

	public void setState(Short state) {
		this.state = state;
	}


	public String getLastBatch() {
		return lastBatch;
	}

	public void setLastBatch(String lastBatch) {
		this.lastBatch = lastBatch;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Long getManualAdd() {
		return manualAdd;
	}

	public void setManualAdd(Long manualAdd) {
		this.manualAdd = manualAdd;
	}

	public Long getManualSub() {
		return manualSub;
	}

	public void setManualSub(Long manualSub) {
		this.manualSub = manualSub;
	}

	public Integer getPurCnt() {
		return purCnt;
	}

	public void setPurCnt(Integer purCnt) {
		this.purCnt = purCnt;
	}

	public Integer getRefCnt() {
		return refCnt;
	}

	public void setRefCnt(Integer refCnt) {
		this.refCnt = refCnt;
	}

	public Integer getAddCnt() {
		return addCnt;
	}

	public void setAddCnt(Integer addCnt) {
		this.addCnt = addCnt;
	}

	public Integer getSubCnt() {
		return subCnt;
	}

	public void setSubCnt(Integer subCnt) {
		this.subCnt = subCnt;
	}

	public Integer getRefFee() {
		return refFee;
	}

	public void setRefFee(Integer refFee) {
		this.refFee = refFee;
	}

	public String getDlsCode() {
		return dlsCode;
	}

	public void setDlsCode(String dlsCode) {
		this.dlsCode = dlsCode;
	}

	public Integer getIsTodayLiq() {
		return isTodayLiq;
	}

	public void setIsTodayLiq(Integer isTodayLiq) {
		this.isTodayLiq = isTodayLiq;
	}

	public Long getAccMdate() {
		return accMdate;
	}

	public void setAccMdate(Long accMdate) {
		this.accMdate = accMdate;
	}

	public Short getAccState() {
		return accState;
	}

	public void setAccState(Short accState) {
		this.accState = accState;
	}

	public String getAccNotice() {
		return accNotice;
	}

	public void setAccNotice(String accNotice) {
		this.accNotice = accNotice;
	}
}