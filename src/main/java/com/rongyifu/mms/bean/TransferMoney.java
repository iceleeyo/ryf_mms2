package com.rongyifu.mms.bean;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;

/*
 * 划款
 */
public class TransferMoney {
	private String tseq;//流水号
	private String mid;//商户号
	private String bankNo;//联行行号
	private String batch;//批次号
	private String liqDate;//结算日期
    private String name;//商户名称
    private String bankName;//开户银行名称
    private String bankAcctName;//开户账户名称
    private String bankAcct;//开户账户号
    private Short tstat;//支付状态
    private Double amount;//交易金额
    private Integer gate;//网关号
    private Integer gid;//支付渠道
    private String errorMsg;//错误信息
    private String errorCode;//错误码
    private Integer type;//代付类型
    private Double feeAmt;//商户手续费
    private String bankBranch;//开户支行名
    SystemDao sysDao=new SystemDao();
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public Double getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(Double feeAmt) {
		this.feeAmt = feeAmt;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getLiqDate() {
		return liqDate;
	}
	public void setLiqDate(String liqDate) {
		this.liqDate = liqDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAcctName() {
		return bankAcctName;
	}
	public void setBankAcctName(String bankAcctName) {
		this.bankAcctName = bankAcctName;
	}
	public String getBankAcct() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,bankAcct);
	}
	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}
	public Short getTstat() {
		return tstat;
	}
	public void setTstat(Short tstat) {
		this.tstat = tstat;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
    
   
}
