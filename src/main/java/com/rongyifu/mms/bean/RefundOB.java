package com.rongyifu.mms.bean;

public class RefundOB {

	private String notRefuntReason;// 不能申请退款的原因
	private int refundCount;// 退款次数
	private int midRefundFlag;// 申请退款的该商户的退款标示
	private boolean canRefund;// 该订单是否可以申请退款
	private boolean todayTrans;// 该订单是否可以申请退款
	private String refundReason;//申请退款的原因
	//申请退款的金额 页面传来的实际值
	private String applyRrefundAmount;

	private String tseq;
	private Integer mdate;
	private String oid;
	private Long amount;
	private Long payAmt;
	private Integer gate;
	private Short type;
	private String mid;
	private Integer sysDate;
	private Integer sysTime;
	private Integer feeAmt;
	private Short tstat;
	private Long refundAmt;
	private Integer preAmt;
	private Integer authorType;
	private Integer gid;
	private Integer transPeriod;
    private Integer code;// 1-此交易不可以退款 2-退款金额超出可退金额 3-退款内容有误
    
    
    private Long orgAmt;
    private String orgBkSeq;
    private Long orgPayAmt;
    private Integer preAmt1;
    

	public Long getOrgAmt() {
		return orgAmt;
	}

	public void setOrgAmt(Long orgAmt) {
		this.orgAmt = orgAmt;
	}

	public String getOrgBkSeq() {
		return orgBkSeq;
	}

	public void setOrgBkSeq(String orgBkSeq) {
		this.orgBkSeq = orgBkSeq;
	}

	public String getApplyRrefundAmount() {
		return applyRrefundAmount;
	}

	public void setApplyRrefundAmount(String applyRrefundAmount) {
		this.applyRrefundAmount = applyRrefundAmount;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public boolean isTodayTrans() {
		return todayTrans;
	}

	public void setTodayTrans(boolean todayTrans) {
		this.todayTrans = todayTrans;
	}

	public String getNotRefuntReason() {
		return notRefuntReason;
	}

	public void setNotRefuntReason(String notRefuntReason) {
		this.notRefuntReason = notRefuntReason;
	}

	public int getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(int refundCount) {
		this.refundCount = refundCount;
	}

	public int getMidRefundFlag() {
		return midRefundFlag;
	}

	public void setMidRefundFlag(int midRefundFlag) {
		this.midRefundFlag = midRefundFlag;
	}

	public boolean isCanRefund() {
		return canRefund;
	}

	public void setCanRefund(boolean canRefund) {
		this.canRefund = canRefund;
	}

	public Integer getAuthorType() {
		return authorType;
	}

   public void setAuthorType(Integer authorType) {
    if(authorType==null){authorType=0;}
		this.authorType = authorType;
	}

	public RefundOB() {
		super();
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(Long payAmt) {
		this.payAmt = payAmt;
	}

	public Integer getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(Integer feeAmt) {
		this.feeAmt = feeAmt;
	}

	public Integer getGate() {
		return gate;
	}

	public void setGate(Integer gate) {
		this.gate = gate;
	}

	public Integer getMdate() {
		return mdate;
	}

	public void setMdate(Integer mdate) {
		this.mdate = mdate;
	}

	public  String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Long getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(Long refundAmt) {
		this.refundAmt = refundAmt;
	}

	public Integer getSysDate() {
		return sysDate;
	}

	public void setSysDate(Integer sysDate) {
		this.sysDate = sysDate;
	}

	public Integer getTransPeriod() {
		return transPeriod;
	}

	public void setTransPeriod(Integer transPeriod) {
		this.transPeriod = transPeriod;
	}

	public String getTseq() {
		return tseq;
	}

	public void setTseq(String tseq) {
		this.tseq = tseq;
	}

	public Short getTstat() {
		return tstat;
	}

	public void setTstat(Short tstat) {
		this.tstat = tstat;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getPreAmt() {
		return preAmt;
	}

	public void setPreAmt(Integer preAmt) {
		this.preAmt = preAmt;
	}

	public Long getOrgPayAmt() {
		return orgPayAmt;
	}

	public void setOrgPayAmt(Long orgPayAmt) {
		this.orgPayAmt = orgPayAmt;
	}

	public Integer getPreAmt1() {
		return preAmt1;
	}

	public void setPreAmt1(Integer preAmt1) {
		this.preAmt1 = preAmt1;
	}

	public Integer getSysTime() {
		return sysTime;
	}

	public void setSysTime(Integer sysTime) {
		this.sysTime = sysTime;
	}

}
