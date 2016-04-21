package com.rongyifu.mms.datasync;

import java.util.ArrayList;
import java.util.List;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class VasSyncData implements IDataSync {
	
	private String merId;			// 商户号
	private String ordId;			// 订单号
	private String orderDate;		// 交易日期
	private long transAmt;			// 交易金额
	private long preAmt;			// 差价
	private long preAmt1;			// 优惠金额
	private long payAmt;			// 支付金额
	private String gateId;			// 支付网关
	private int transState;			// 交易状态		1-待支付，2-成功，3-失败
	private String bkSeq;			// 银行流水号
	private int sysDate;			// 支付日期
	private int sysTime;			// 支付时间
	private int merFee;				// 商户手续费
	private String gid;				// 支付渠道
	private String bkFeeMode;		// 银行手续费公式
	private int transMode;			// 交易类型
	
	@Override
	public List<String> getSql(String flowNo, String serviceType) {
		List<String> sqlList = new ArrayList<String>();
		
		String tseq = Ryt.genOidBySysTime();
		int bkResTime = DateUtil.getCurrentUTCSeconds();
		int bkResDate = DateUtil.today();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into hlog(tseq,version,ip,mdate,mid,bid,oid,amount,type,gate,author_type,");
		sql.append("sys_date,init_sys_date,sys_time,fee_amt,tstat,bk_flag,bk_send,bk_recv,bk_date,");
		sql.append("bk_seq1,bk_seq2,trans_period,gid,bk_fee_model,pay_amt,data_source,p9) ");
		sql.append(" values(" + tseq + ",10,0,");
		sql.append(orderDate + ",");
		sql.append(Ryt.addQuotes(merId) + ",");
		sql.append(Ryt.addQuotes(merId) + ",");
		sql.append(Ryt.addQuotes(ordId) + ",");
		sql.append(transAmt + ",");
		sql.append(transMode + ",");
		sql.append(gateId + ",0,");
		sql.append(sysDate + ",");
		sql.append(sysDate + ",");
		sql.append(sysTime + ",");
		sql.append(merFee + ","); 
		sql.append(transState + ",1,");
		sql.append(sysTime + ",");
		//sql.append(bkResTime + ",");
		sql.append(sysTime + ",");
		sql.append(sysDate + ",");
		sql.append(Ryt.addQuotes(bkSeq) + ",");
		sql.append(Ryt.addQuotes(bkSeq) + ",0,");
		sql.append(gid + ",");
		sql.append(Ryt.addQuotes(bkFeeMode) + ",");
		sql.append(payAmt + ",4,");
		sql.append(Ryt.addQuotes(flowNo) + ")");
		
		sqlList.add(sql.toString());
		
		if(transState == Constant.PayState.SUCCESS){
			AccSeqs params = new AccSeqs();
			params.setUid(merId);
			params.setAid(merId);
			params.setTrAmt(transAmt);
			params.setTrFee(merFee);
			params.setAmt(transAmt - merFee);
			params.setRecPay((short)Constant.RecPay.INCREASE);
			params.setTbName(Constant.HLOG);
			params.setTbId(tseq);
			params.setRemark(serviceType + "同步");
						
			// 账户流水记未结算金额
			List<String> list = RecordLiveAccount.recordAccSeqsAndCalLiqBalance(params);			
			if(list != null)
				sqlList.addAll(list);		
		}
		
		return sqlList;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getOrdId() {
		return ordId;
	}

	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public long getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(long transAmt) {
		this.transAmt = transAmt;
	}

	public long getPreAmt() {
		return preAmt;
	}

	public void setPreAmt(long preAmt) {
		this.preAmt = preAmt;
	}

	public long getPreAmt1() {
		return preAmt1;
	}

	public void setPreAmt1(long preAmt1) {
		this.preAmt1 = preAmt1;
	}

	public long getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(long payAmt) {
		this.payAmt = payAmt;
	}

	public String getGateId() {
		return gateId;
	}

	public void setGateId(String gateId) {
		this.gateId = gateId;
	}

	public int getTransState() {
		return transState;
	}

	public void setTransState(int transState) {
		this.transState = transState;
	}

	public String getBkSeq() {
		return bkSeq;
	}

	public void setBkSeq(String bkSeq) {
		this.bkSeq = bkSeq;
	}

	public int getSysDate() {
		return sysDate;
	}

	public void setSysDate(int sysDate) {
		this.sysDate = sysDate;
	}

	public int getSysTime() {
		return sysTime;
	}

	public void setSysTime(int sysTime) {
		this.sysTime = sysTime;
	}

	public int getMerFee() {
		return merFee;
	}

	public void setMerFee(int merFee) {
		this.merFee = merFee;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getBkFeeMode() {
		return bkFeeMode;
	}

	public void setBkFeeMode(String bkFeeMode) {
		this.bkFeeMode = bkFeeMode;
	}

	public int getTransMode() {
		return transMode;
	}

	public void setTransMode(int transMode) {
		this.transMode = transMode;
	}

}
