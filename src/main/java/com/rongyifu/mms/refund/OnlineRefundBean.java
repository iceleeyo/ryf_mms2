package com.rongyifu.mms.refund;

public class OnlineRefundBean {
	//退款流水号
	private Integer id;
	// 原银行流水号
	private String bkTseq;
	// 联机退款状态
	private String orderStatus;
	// 联机退款批次号
	private String refBatch;
	// 原交易流水号
	private String orgTseq;
	// 退款金额
	private double refAmt;
	// 退款原因
	private String refundReason;
	// 退款失败原因
    private String refundFailureReason;
    
    private Integer gid;
    
    
   //2014年3月7日 16:53:22廉艳举 新增加
    private String orderNumber;
    private String merId;
    private String qn;//查询流水号
    
    private Integer orgOrderDate; //原始订单日期
    //2014年6月23日 林毅衡 新增
    private Integer sysTime;
    private Integer sysDate;
  
    public Integer getSysTime(){
    	return sysTime;
    }
    public Integer getSysDate(){
    	return sysDate;
    }
    
	public Integer getOrgOrderDate() {
		return orgOrderDate;
	}
	public void setOrgOrderDate(Integer orgOrderDate) {
		this.orgOrderDate = orgOrderDate;
	}
	public String getQn() {
		return qn;
	}
	public void setQn(String qn) {
		this.qn = qn;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Integer getGid() {
		return gid;
	}
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBkTseq() {
		return bkTseq;
	}
	public void setBkTseq(String bkTseq) {
		this.bkTseq = bkTseq;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getRefBatch() {
		return refBatch;
	}
	public void setRefBatch(String refBatch) {
		this.refBatch = refBatch;
	}
	public String getOrgTseq() {
		return orgTseq;
	}
	public void setOrgTseq(String orgTseq) {
		this.orgTseq = orgTseq;
	}
	public double getRefAmt() {
		return refAmt;
	}
	public void setRefAmt(double refAmt) {
		this.refAmt = refAmt;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getRefundFailureReason() {
		return refundFailureReason;
	}
	public void setRefundFailureReason(String refundFailureReason) {
		this.refundFailureReason = refundFailureReason;
	}
	public void setSysTime(Integer sys_time){
		this.sysTime=sys_time;
	}
	public void setSysDate(Integer sys_date){
		this.sysDate=sys_date;
	}

}