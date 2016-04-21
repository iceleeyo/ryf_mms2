package com.rongyifu.mms.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;

public class Minfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private String id;
	private String name;
	private String abbrev;
	private String payPwd;
	private Integer provId;
	private Integer beginDate;
	private Integer expDate;
	private Integer beginFee;
	private Integer annualFee;
	private Integer cautionMoney;
	private String faxNo;
	private String signatory;
	private Short merChkFlag;
	private Short refundFlag;
	private Short liqType;
	private Short liqPeriod;
	private Integer transLimit;
	private String corpCode;
	private String regCode;
	private String addr;
	private String zip;
	private String mdesc;
	private Short mstate;
	private Integer openDate;
	private Long lastUpdate;
	private String webUrl;
	private Integer liqLimit;
	private String bankName;
	private String bankAcct;
	private Integer bankProvId;
	private String bankBranch;
	private String bankAcctName;
	private Short category;
	private String contact0;
	private String tel0;
	private String cell0;
	private String email0;
	private String contact1;
	private String tel1;
	private String email1;
	private String contact2;
	private String tel2;
	private String email2;
	private String contact3;
	private String tel3;
	private String email3;
	private String contact4;
	private String tel4;
	private String email4;
	private String contact5;
	private String tel5;
	private String email5;
	private String publicKey;
	private Integer balance;
	private String cell1;
	private String cell2;
	private String cell3;
	private String cell4;
	private String cell5;
	private String lastBatch;
	private Integer lastLiqDate;

	private Integer accSuccCount;
	private Integer accFailCount;
	private Integer phoneSuccCount;
	private Integer phoneFailCount;
	private Integer idSuccCount;
	private Integer idFailCount;
	
	private Integer merTradeType;
	private Integer refundFee;
	private Integer codeExpDate;
	private String corpName;
	
	private String idType;
	private String idNo;
	private String openBkNo;
	private String openBkName;
	private Short liqObj;
	private Short liqState;
	private Short manLiq;
		
	private Short autoDfState;
	private String pbkProvId;
	private String pbkName;
	private String pbkBranch;
	private String pbkNo;
	private String pbkAccNo;
	private String pbkAccName;
	
	private Integer gateId;
	private Integer pbkGateId;
	
	private Integer whiteAccSuccCount;
	private Integer whiteAccFailCount;
	private Integer whitePhoneSuccCount;
	private Integer whitePhoneFailCount;
	private Integer whiteIdSuccCount;
	private Integer whiteIdFailCount;
	private Short status;
	private Long effectiveTime;
	private Integer isEffectNow;
	private String mid;
	private String dlsCode;
	
	private Integer isSgdf; //新增字段 是否手工代付
	private String bkNo;//银行行号 阳耀峰 2014-11-03
	private String upmpMid;//银联商户号 yang.yaofeng 2014-11-17
	private Integer isPtop;//新增字段 是否为p2p托管业务 0否 1是
	public static final int IS_P2P = 1;
	public static final int IS_NOT_P2P = 0;
	
	private String userId;//账户系统返回的字段\
	//商户重要信息审核 申请/审核 操作员id、时间
	private String applyOperName;
	private String checkOperName;
	private Date applyTime;
	private Date checkTime;
	
	private List<QkRisk> qkrisks;
	
	
	

	public List<QkRisk> getQkrisks() {
		return qkrisks;
	}

	public void setQkrisks(List<QkRisk> qkrisks) {
		this.qkrisks = qkrisks;
	}

	public String getApplyOperName() {
		return applyOperName;
	}

	public void setApplyOperName(String applyOperName) {
		this.applyOperName = applyOperName;
	}

	public String getCheckOperName() {
		return checkOperName;
	}

	public void setCheckOperName(String checkOperName) {
		this.checkOperName = checkOperName;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public String getApplyTimeStr() {
		String dateTimeStr = "";
		try {
			if(null != applyTime){
				dateTimeStr = DateUtil.format(applyTime, "yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
		}
		return dateTimeStr;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public String getCheckTimeStr() {
		String dateTimeStr = "";
		try {
			if(null != checkTime){
				dateTimeStr = DateUtil.format(checkTime, "yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
		}
		return dateTimeStr;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getIsPtop() {
		return isPtop;
	}

	public void setIsPtop(Integer isPtop) {
		this.isPtop = isPtop;
	}

	public Integer getIsSgdf() {
		return isSgdf;
	}

	public void setIsSgdf(Integer isSgdf) {
		this.isSgdf = isSgdf;
	}

	public String getOpenBkName() {
		return openBkName;
	}

	public void setOpenBkName(String openBkName) {
		this.openBkName = openBkName;
	}

	public Integer getWhiteAccSuccCount() {
		return whiteAccSuccCount;
	}

	public void setWhiteAccSuccCount(Integer whiteAccSuccCount) {
		this.whiteAccSuccCount = whiteAccSuccCount;
	}

	public Integer getWhiteAccFailCount() {
		return whiteAccFailCount;
	}

	public void setWhiteAccFailCount(Integer whiteAccFailCount) {
		this.whiteAccFailCount = whiteAccFailCount;
	}

	public Integer getWhitePhoneSuccCount() {
		return whitePhoneSuccCount;
	}

	public void setWhitePhoneSuccCount(Integer whitePhoneSuccCount) {
		this.whitePhoneSuccCount = whitePhoneSuccCount;
	}

	public Integer getWhitePhoneFailCount() {
		return whitePhoneFailCount;
	}

	public void setWhitePhoneFailCount(Integer whitePhoneFailCount) {
		this.whitePhoneFailCount = whitePhoneFailCount;
	}

	public Integer getWhiteIdSuccCount() {
		return whiteIdSuccCount;
	}

	public void setWhiteIdSuccCount(Integer whiteIdSuccCount) {
		this.whiteIdSuccCount = whiteIdSuccCount;
	}

	public Integer getWhiteIdFailCount() {
		return whiteIdFailCount;
	}

	public void setWhiteIdFailCount(Integer whiteIdFailCount) {
		this.whiteIdFailCount = whiteIdFailCount;
	}

	public Integer getGateId() {
		return gateId;
	}

	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}

	public Integer getPbkGateId() {
		return pbkGateId;
	}

	public void setPbkGateId(Integer pbkGateId) {
		this.pbkGateId = pbkGateId;
	}

	public Short getAutoDfState() {
		return autoDfState;
	}

	public void setAutoDfState(Short autoDfState) {
		this.autoDfState = autoDfState;
	}

	public String getPbkProvId() {
		return pbkProvId;
	}

	public void setPbkProvId(String pbkProvId) {
		this.pbkProvId = pbkProvId;
	}

	public String getPbkName() {
		return pbkName;
	}

	public void setPbkName(String pbkName) {
		this.pbkName = pbkName;
	}

	public String getPbkBranch() {
		return pbkBranch;
	}

	public void setPbkBranch(String pbkBranch) {
		this.pbkBranch = pbkBranch;
	}

	public String getPbkNo() {
		return pbkNo;
	}

	public void setPbkNo(String pbkNo) {
		this.pbkNo = pbkNo;
	}

	public String getPbkAccNo() {
		return Ryt.minfoGetHandle(pbkAccNo);
	}

	public void setPbkAccNo(String pbkAccNo) {
		this.pbkAccNo = Ryt.minfoSetHandle(pbkAccNo);
	}

	public String getPbkAccName() {
		return pbkAccName;
	}

	public void setPbkAccName(String pbkAccName) {
		this.pbkAccName = pbkAccName;
	}

	public Short getManLiq() {
		return manLiq;
	}

	public void setManLiq(Short manLiq) {
		this.manLiq = manLiq;
	}

	public Short getLiqState() {
		return liqState;
	}

	public void setLiqState(Short liqState) {
		this.liqState = liqState;
	}

	private String dfConfig;//代付配置
	private String dkConfig;//代扣配置
	private String multiProcessDay;//批量操作�?
	private String multiProcessTime;//批量处理时�??
	private Integer amtLimitD;//日累计交易限�?
	private Integer amtLimitM;//月累计交易限�?
	private Integer cardLimit;//信用卡单笔限�?
	public Short getLiqObj() {
		return liqObj;
	}

	public void setLiqObj(Short liqObj) {
		this.liqObj = liqObj;
	}

	// private Integer dayAmount;
	private String merType;


	/** default constructor */
	public Minfo() {
	}

	public Integer getMerTradeType() {
		return merTradeType;
	}

	public void setMerTradeType(Integer merTradeType) {
		this.merTradeType = merTradeType;
	}
	public String getPayPwd() {
		return payPwd;
	}

	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}

	public String getMerType() {
		return merType;
	}

	public void setMerType(String merType) {
		this.merType = merType;
	}

	public String getCell1() {
		return Ryt.minfoGetHandle(cell1);
	}

	public void setCell1(String cell1) {
		this.cell1 = Ryt.minfoSetHandle(cell1);
	}

	public String getCell2() {
		return Ryt.minfoGetHandle(cell2);
	}

	public void setCell2(String cell2) {
		this.cell2 = Ryt.minfoSetHandle(cell2);
	}

	public String getCell3() {
		return Ryt.minfoGetHandle(cell3);
	}

	public void setCell3(String cell3) {
		this.cell3 = Ryt.minfoSetHandle(cell3);
	}

	public String getCell4() {
		return Ryt.minfoGetHandle(cell4);
	}

	public void setCell4(String cell4) {
		this.cell4 = Ryt.minfoSetHandle(cell4);
	}

	public String getCell5() {
		return Ryt.minfoGetHandle(cell5);
	}

	public void setCell5(String cell5) {
		this.cell5 = Ryt.minfoSetHandle(cell5);
	}

	public Integer getAccSuccCount() {
		return accSuccCount;
	}

	public void setAccSuccCount(Integer accSuccCount) {
		this.accSuccCount = accSuccCount;
	}

	public Integer getAccFailCount() {
		return accFailCount;
	}

	public void setAccFailCount(Integer accFailCount) {
		this.accFailCount = accFailCount;
	}

	public Integer getPhoneSuccCount() {
		return phoneSuccCount;
	}

	public void setPhoneSuccCount(Integer phoneSuccCount) {
		this.phoneSuccCount = phoneSuccCount;
	}

	public Integer getPhoneFailCount() {
		return phoneFailCount;
	}

	public void setPhoneFailCount(Integer phoneFailCount) {
		this.phoneFailCount = phoneFailCount;
	}

	public Integer getIdSuccCount() {
		return idSuccCount;
	}

	public void setIdSuccCount(Integer idSuccCount) {
		this.idSuccCount = idSuccCount;
	}

	public Integer getIdFailCount() {
		return idFailCount;
	}

	public void setIdFailCount(Integer idFailCount) {
		this.idFailCount = idFailCount;
	}

	public String getAbbrev() {
		return abbrev;
	}

	public String getAddr() {
		return addr;
	}

	public Integer getAnnualFee() {
		return annualFee;
	}

	public Integer getBalance() {
		return balance;
	}

	public String getBankAcct() {
		return Ryt.minfoGetHandle(bankAcct);
	}

	public String getBankAcctName() {
		return bankAcctName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public String getBankName() {
		return bankName;
	}

	public Integer getProvId() {
		return provId;
	}

	public void setProvId(Integer provId) {
		this.provId = provId;
	}

	public Integer getBankProvId() {
		return bankProvId;
	}

	public void setBankProvId(Integer bankProvId) {
		this.bankProvId = bankProvId;
	}

	public Integer getBeginDate() {
		return beginDate;
	}

	public Integer getBeginFee() {
		return beginFee;
	}

	public Short getCategory() {
		return category;
	}

	public Integer getCautionMoney() {
		return cautionMoney;
	}

	public String getCell0() {
		return  Ryt.minfoGetHandle(cell0);
	}

	public String getContact0() {
		return contact0;
	}

	public String getContact1() {
		return contact1;
	}

	public String getContact2() {
		return contact2;
	}

	public String getContact3() {
		return contact3;
	}

	public String getContact4() {
		return contact4;
	}

	public String getContact5() {
		return contact5;
	}

	public String getCorpCode() {
		return corpCode;
	}


	public String getEmail0() {
		return email0;
	}

	public String getEmail1() {
		return email1;
	}

	public String getEmail2() {
		return email2;
	}

	public String getEmail3() {
		return email3;
	}

	public String getEmail4() {
		return email4;
	}

	public String getEmail5() {
		return email5;
	}

	public Integer getExpDate() {
		return expDate;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public String getId() {
		return id;
	}

	public String getLastBatch() {
		return lastBatch;
	}

	public Integer getLastLiqDate() {
		return lastLiqDate;
	}


	public Integer getLiqLimit() {
		return liqLimit == null ? 0 : liqLimit;
	}

	public Short getLiqPeriod() {
		return liqPeriod;
	}

	public Short getLiqType() {
		return liqType;
	}


	public String getMdesc() {
		return mdesc;
	}

	public Short getMerChkFlag() {
		return merChkFlag;
	}

	public Short getMstate() {
		return mstate;
	}

	public String getName() {
		return name;
	}

	public Integer getOpenDate() {
		return openDate;
	}


	public String getPublicKey() {
		return publicKey;
	}

	public Short getRefundFlag() {
		return refundFlag;
	}

	public String getRegCode() {
		return regCode;
	}

	public String getSignatory() {
		return signatory;
	}

	public String getTel0() {
		return Ryt.minfoGetHandle(tel0);
	}

	public String getTel1() {
		return Ryt.minfoGetHandle(tel1);
	}

	public String getTel2() {
		return Ryt.minfoGetHandle(tel2);
	}

	public String getTel3() {
		return Ryt.minfoGetHandle(tel3);
	}

	public String getTel4() {
		return Ryt.minfoGetHandle(tel4);
	}

	public String getTel5() {
		return Ryt.minfoGetHandle(tel5);
	}

	public Integer getTransLimit() {
		return transLimit;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public String getZip() {
		return zip;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public void setAnnualFee(Integer annualFee) {
		this.annualFee = annualFee;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = Ryt.minfoSetHandle(bankAcct);
	}

	public void setBankAcctName(String bankAcctName) {
		this.bankAcctName = bankAcctName;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setBeginDate(Integer beginDate) {
		this.beginDate = beginDate;
	}

	public void setBeginFee(Integer beginFee) {
		this.beginFee = beginFee;
	}

	public void setCategory(Short category) {
		this.category = category;
	}

	public void setCautionMoney(Integer cautionMoney) {
		this.cautionMoney = cautionMoney;
	}

	public void setCell0(String cell0) {
		this.cell0 = Ryt.minfoSetHandle(cell0);
	}

	public void setContact0(String contact0) {
		this.contact0 = contact0;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	public void setContact3(String contact3) {
		this.contact3 = contact3;
	}

	public void setContact4(String contact4) {
		this.contact4 = contact4;
	}

	public void setContact5(String contact5) {
		this.contact5 = contact5;
	}

	public void setCorpCode(String corpCode) {
		this.corpCode = corpCode;
	}

	public void setEmail0(String email0) {
		this.email0 = email0;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public void setEmail4(String email4) {
		this.email4 = email4;
	}

	public void setEmail5(String email5) {
		this.email5 = email5;
	}

	public void setExpDate(Integer expDate) {
		this.expDate = expDate;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLastBatch(String lastBatch) {
		this.lastBatch = lastBatch;
	}

	public void setLastLiqDate(Integer lastLiqDate) {
		this.lastLiqDate = lastLiqDate;
	}

	public void setLiqLimit(Integer liqLimit) {
		this.liqLimit = liqLimit;
	}

	public void setLiqPeriod(Short liqPeriod) {
		this.liqPeriod = liqPeriod;
	}

	public void setLiqType(Short liqType) {
		this.liqType = liqType;
	}

	public void setMdesc(String mdesc) {
		this.mdesc = mdesc;
	}

	public void setMerChkFlag(Short merChkFlag) {
		this.merChkFlag = merChkFlag;
	}

	public void setMstate(Short mstate) {
		mstate=mstate==null?1:mstate;
		this.mstate = mstate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOpenDate(Integer openDate) {
		this.openDate = openDate;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setRefundFlag(Short refundFlag) {
		this.refundFlag = refundFlag;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	public void setSignatory(String signatory) {
		this.signatory = signatory;
	}

	public void setTel0(String tel0) {
		this.tel0 = Ryt.minfoSetHandle(tel0);
	}

	public void setTel1(String tel1) {
		this.tel1 = Ryt.minfoSetHandle(tel1);
	}

	public void setTel2(String tel2) {
		this.tel2 = Ryt.minfoSetHandle(tel2);
	}

	public void setTel3(String tel3) {
		this.tel3 = Ryt.minfoSetHandle(tel3);
	}

	public void setTel4(String tel4) {
		this.tel4 = Ryt.minfoSetHandle(tel4);
	}

	public void setTel5(String tel5) {
		this.tel5 = Ryt.minfoSetHandle(tel5);
	}

	public void setTransLimit(Integer transLimit) {
		this.transLimit = transLimit;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Integer getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(Integer refundFee) {
		this.refundFee = refundFee;
	}
	public Integer getCodeExpDate() {
		return codeExpDate;
	}

	public void setCodeExpDate(Integer codeExpDate) {
		this.codeExpDate = codeExpDate;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return Ryt.minfoGetHandle(idNo);
	}

	public void setIdNo(String idNo) {
		this.idNo=Ryt.minfoSetHandle(idNo);
	}

	public String getOpenBkNo() {
		return openBkNo;
	}

	public void setOpenBkNo(String openBkNo) {
		this.openBkNo = openBkNo;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getDfConfig() {
		return dfConfig;
	}

	public void setDfConfig(String dfConfig) {
		this.dfConfig = dfConfig;
	}

	public String getDkConfig() {
		return dkConfig;
	}

	public void setDkConfig(String dkConfig) {
		this.dkConfig = dkConfig;
	}

	public String getMultiProcessDay() {
		return multiProcessDay;
	}

	public void setMultiProcessDay(String multiProcessDay) {
		this.multiProcessDay = multiProcessDay;
	}

	public String getMultiProcessTime() {
		return multiProcessTime;
	}

	public void setMultiProcessTime(String multiProcessTime) {
		this.multiProcessTime = multiProcessTime;
	}

	public Integer getAmtLimitD() {
		return amtLimitD;
	}

	public void setAmtLimitD(Integer amtLimitD) {
		this.amtLimitD = amtLimitD;
	}

	public Integer getAmtLimitM() {
		return amtLimitM;
	}

	public void setAmtLimitM(Integer amtLimitM) {
		this.amtLimitM = amtLimitM;
	}

	public Integer getCardLimit() {
		return cardLimit;
	}

	public void setCardLimit(Integer cardLimit) {
		this.cardLimit = cardLimit;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Long getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Long effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Integer getIsEffectNow() {
		return isEffectNow;
	}

	public void setIsEffectNow(Integer isEffectNow) {
		this.isEffectNow = isEffectNow;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getDlsCode() {
		return dlsCode;
	}

	public void setDlsCode(String dlsCode) {
		this.dlsCode = dlsCode;
	}

	public String getBkNo() {
		return bkNo;
	}

	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}

	public String getUpmpMid() {
		return upmpMid;
	}

	public void setUpmpMid(String upmpMid) {
		this.upmpMid = upmpMid;
	}

	/* 
	 * 通过该方法
	 */
	@Override
	public String toString() { 
		JsonConfig config = new JsonConfig();  
		DefaultValueProcessor dvp = new DefaultValueProcessor(){
			@Override
			public Object getDefaultValue(Class type) {
				return "";
			}
		};
	    config.registerDefaultValueProcessor(Integer.class, dvp); 
	    config.registerDefaultValueProcessor(Short.class, dvp);
	    config.registerDefaultValueProcessor(Long.class, dvp);
	    config.registerDefaultValueProcessor(String.class, dvp);
	    config.registerDefaultValueProcessor(Byte.class, dvp);
	    config.registerDefaultValueProcessor(Character.class, dvp);
	    config.registerDefaultValueProcessor(Float.class, dvp);
	    config.registerDefaultValueProcessor(Double.class, dvp);
		return JSONObject.fromObject(this,config).toString();
	}
	
}