package com.rongyifu.mms.bean;

import com.rongyifu.mms.common.Ryt;

public class TrOrders {

	private String oid;  
	private String uid;         
	private String aid; 
	private String aname;
	private Long initTime;       
	private Integer sysDate;          
	private Integer sysTime;    
	private Integer operDate;
	private Short ptype;              
	private String orgOid;         
	private Long transAmt;
	private Integer transFee;
	private Long payAmt;           
	private String toUid;            
	private String toAid;
	private String toOid;
	private String tseq;               
	private Short state;                   
	private String remark;         	
	private String priv;
	private String bkNo;
	private Integer toProvId;
	private Short cardFlag;
	private String trans_flow;
	private String tstat;
	
	
	private Integer gate;
	private String accName;
	private String accNo;
	private String toAccName;
	private String toAccNo;
	private String toBkName ;
	private String toBkNo;
	private String smsMobiles;
	
	private String name;
	private String bankName;
	private String bankAcct;
	private String auditRemark;//审核意见
	private Long rechargeAmt;
	private short pstate;
	private String orgn_remark;//经办意见
	private String cert_remark;//凭证审核意见
//	private String toInfo;
	
	public String getOrgn_remark() {
		return orgn_remark;
	}
	public void setOrgn_remark(String orgn_remark) {
		this.orgn_remark = orgn_remark;
	}
	public String getCert_remark() {
		return cert_remark;
	}
	public void setCert_remark(String cert_remark) {
		this.cert_remark = cert_remark;
	}
	public short getPstate() {
		return pstate;
	}
	public void setPstate(short pstate) {
		this.pstate = pstate;
	}
	public String getAuditRemark() {
		return auditRemark;
	}
	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}
	public Long getRechargeAmt() {
		return rechargeAmt;
	}
	public void setRechargeAmt(Long rechargeAmt) {
		this.rechargeAmt = rechargeAmt;
	}
	public String getTrans_flow() {
		return trans_flow;
	}
	public void setTrans_flow(String trans_flow) {
		this.trans_flow = trans_flow;
	}
	public Integer getGate() {
		return gate;
	}
	public void setGate(Integer gate) {
		this.gate = gate;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	
	public String getToAccName() {
		return toAccName;
	}
	public void setToAccName(String toAccName) {
		this.toAccName = toAccName;
	}
	public String getToAccNo() {
		return toAccNo;
	}
	public void setToAccNo(String toAccNo) {
		this.toAccNo = toAccNo;
	}
	public String getToBkName() {
		return toBkName;
	}
	public void setToBkName(String toBkName) {
		this.toBkName = toBkName;
	}
	public String getToBkNo() {
		return toBkNo;
	}
	public void setToBkNo(String toBkNo) {
		this.toBkNo = toBkNo;
	}
	public String getSmsMobiles() {
		return smsMobiles;
	}
	public void setSmsMobiles(String smsMobiles) {
		this.smsMobiles = smsMobiles;
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
	public String getBankAcct() {
		return Ryt.minfoGetHandle(bankAcct);
	}
	public void setBankAcct(String bankAcct) {
		this.bankAcct = Ryt.minfoSetHandle(bankAcct);
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
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
	public Long getInitTime() {
		return initTime;
	}
	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}
	public Integer getSysDate() {
		return sysDate;
	}
	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}
	public Integer getSysTime() {
		return sysTime;
	}
	public void setSysTime(Integer sysTime) {
		this.sysTime = sysTime;
	}
	public Integer getOperDate() {
		return operDate;
	}
	public void setOperDate(Integer operDate) {
		this.operDate = operDate;
	}
	public Short getPtype() {
		return ptype;
	}
	public void setPtype(Short ptype) {
		this.ptype = ptype;
	}
	public String getOrgOid() {
		return orgOid;
	}
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}
	public Long getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(Long transAmt) {
		this.transAmt = transAmt;
	}
	public Integer getTransFee() {
		return transFee == null ? 0 : transFee;
	}
	public void setTransFee(Integer transFee) {
		this.transFee = transFee;
	}
	public Long getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(Long payAmt) {
		this.payAmt = payAmt;
	}
	public String getToUid() {
		return toUid;
	}
	public void setToUid(String toUid) {
		this.toUid = toUid;
	}
	public String getToAid() {
		return toAid;
	}
	public void setToAid(String toAid) {
		this.toAid = toAid;
	}
	public String getToOid() {
		return toOid;
	}
	public void setToOid(String toOid) {
		this.toOid = toOid;
	}
	public String getTseq() {
		return tseq;
	}
	public void setTseq(String tseq) {
		this.tseq = tseq;
	}
	public Short getState() {
		return state;
	}
	public void setState(Short state) {
		this.state = state;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPriv() {
		return priv;
	}
	public void setPriv(String priv) {
		this.priv = priv;
	} 
	public String getBkNo() {
		return bkNo;
	}
	public void setBkNo(String bkNo) {
		this.bkNo = bkNo;
	}
	public Integer getToProvId() {
		return toProvId;
	}
	public void setToProvId(Integer toProvId) {
		this.toProvId = toProvId;
	}
	public Short getCardFlag() {
		return cardFlag;
	}
	public void setCardFlag(Short cardFlag) {
		this.cardFlag = cardFlag;
	}
	public String getTstat() {
		return tstat;
	}
	public void setTstat(String tstat) {
		this.tstat = tstat;
	}
	
	
//	public String getToInfo() {
//		return toInfo;
//	}
//	public String getToInfo() {
//		
////		0-账户充
////		1-账户提现
////		2-充值撤销
////		3-付款到电银账
////		4-从电银账户收
////		5-付款到银行账
////		6-从银行账户收
////		7-付款到个人银行卡
////		8-结算划款
//		
//		if(this.ptype == 3 || this.ptype==4){
//			return this.toUid + "[" + this.toAid + "]";
//		}else if(this.ptype >= 5 ){
//			return this.toAccName + "[" + this.toAccNo + "]";
//		}else{
//			return "";
//		}
//		
//	}
//	public void setToInfo(String toInfo) {
////		this.toInfo = toInfo;
//	}
	 
}
