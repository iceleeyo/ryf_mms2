package com.rongyifu.mms.bean;

public class AccInfos {
	
	private String aid;
	private String uid;
	private String aname;
	private Integer initDate;
	private short state;
	private short payFlag;
	private Long transLimit;
	private Long monthLimit;
	private Integer accMonthCount;
	private Long accMonthAmt;
	private Integer daySuccessCount;
	private Integer dayFailCount;
	private Long allBalance;
	private String czFeeMode;
	private String tixianFeeMode;
	private String daifaFeeMode;
	private String daifuFeeMode;
	private Long balance;
	private Integer acc_type;
	private Integer initdate;
	private Integer merTradeType;
	private String name;//Minfo商户名称
	private Integer dkMonthCount;//同一扣款卡号月累计成功次数
	private Long dkMonthAmt;//同一扣款卡号月累计成功金额（元）
	
	
	public Integer getDkMonthCount() {
		return dkMonthCount;
	}
	public void setDkMonthCount(Integer dkMonthCount) {
		this.dkMonthCount = dkMonthCount;
	}
	public Long getDkMonthAmt() {
		return dkMonthAmt;
	}
	public void setDkMonthAmt(Long dkMonthAmt) {
		this.dkMonthAmt = dkMonthAmt;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAcc_type() {
		return acc_type;
	}
	public void setAcc_type(Integer acc_type) {
		this.acc_type = acc_type;
	}
	public Long getMonthLimit() {
		return monthLimit;
	}
	public void setMonthLimit(Long monthLimit) {
		this.monthLimit = monthLimit;
	}
	public Integer getAccMonthCount() {
		return accMonthCount;
	}
	public void setAccMonthCount(Integer accMonthCount) {
		this.accMonthCount = accMonthCount;
	}
	public Long getAccMonthAmt() {
		return accMonthAmt;
	}
	public void setAccMonthAmt(Long accMonthAmt) {
		this.accMonthAmt = accMonthAmt;
	}
	public String getCzFeeMode() {
		return czFeeMode;
	}
	public void setCzFeeMode(String czFeeMode) {
		this.czFeeMode = czFeeMode;
	}
	public String getTixianFeeMode() {
		return tixianFeeMode;
	}
	public void setTixianFeeMode(String tixianFeeMode) {
		this.tixianFeeMode = tixianFeeMode;
	}
	public String getDaifaFeeMode() {
		return daifaFeeMode;
	}
	public void setDaifaFeeMode(String daifaFeeMode) {
		this.daifaFeeMode = daifaFeeMode;
	}
	public String getDaifuFeeMode() {
		return daifuFeeMode;
	}
	public void setDaifuFeeMode(String daifuFeeMode) {
		this.daifuFeeMode = daifuFeeMode;
	}
	public Integer getMerTradeType() {
		return merTradeType;
	}
	public void setMerTradeType(Integer merTradeType) {
		this.merTradeType = merTradeType;
	}
	public AccInfos(String aid, String uid, String aname, Integer initDate) {
		super();
		this.aid = aid;
		this.uid = uid;
		this.aname = aname;
		this.initDate = initDate;
	}
	public AccInfos(String aid, String aname) {
		super();
		this.aid = aid;
		this.aname = aname;
	}
	public AccInfos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	public Integer getInitDate() {
		return initDate;
	}
	public void setInitDate(Integer initDate) {
		this.initDate = initDate;
	}
	public short getState() {
		return state;
	}
	public void setState(short state) {
		this.state = state;
	}
	public short getPayFlag() {
		return payFlag;
	}
	public void setPayFlag(short payFlag) {
		this.payFlag = payFlag;
	}
	public Long getTransLimit() {
		return transLimit;
	}
	public void setTransLimit(Long transLimit) {
		this.transLimit = transLimit;
	}
	public Integer getDaySuccessCount() {
		return daySuccessCount;
	}
	public void setDaySuccessCount(Integer daySuccessCount) {
		this.daySuccessCount = daySuccessCount;
	}
	public Integer getDayFailCount() {
		return dayFailCount;
	}
	public void setDayFailCount(Integer dayFailCount) {
		this.dayFailCount = dayFailCount;
	}
	public Long getAllBalance() {
		return allBalance;
	}
	public void setAllBalance(Long allBalance) {
		this.allBalance = allBalance;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Integer getInitdate() {
		return initdate;
	}
	public void setInitdate(Integer initdate) {
		this.initdate = initdate;
	}
	
}
