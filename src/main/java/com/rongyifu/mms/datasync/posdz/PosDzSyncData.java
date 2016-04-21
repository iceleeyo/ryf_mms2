package com.rongyifu.mms.datasync.posdz;

import java.util.ArrayList;
import java.util.List;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.datasync.IDataSync;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class PosDzSyncData implements IDataSync {
	
	private String bid; 			// 提交订单商户号 
	private String merId;			// 结算商户号 
	private String ordId; 			// 订单号
	private int orderDate; 			// 商户订日期		格式：YYYYMMDD
	private int sysDate; 			// 系统日期		格式：YYYYMMDD
	private int sysTime; 			// 系统时间		格式：hhmmss
	private long transAmt;			// 订单金额		单位:元
	private long payAmt;			// 支付金额 		实际支付的金额
	private String gateId;			// 支付网关号 	
	private int transState;			// 交易状态		1-待支付，2-成功，3-失败
	private String bkSeq;			// 银行流水号
	private int bkFee;			    // 银行手续费
	private String cardNo;			// 支付卡号
//	private String busiType;		// 业务类型		01- 收单业务 02- 转账业务
	private int merFee;				// 商户手续费
	private String gid;				// 支付渠道
	private String transType;		// POS交易类型
	private String terminal;		// 终端号
	private String orgCode;         // 代理商号
	private String bkFeeMode;
	private int bkDate;             //银行清算日期
	
	public String getBid() {
		return bid;
	}


	public void setBid(String bid) {
		this.bid = bid;
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


	public int getOrderDate() {
		return orderDate;
	}


	public void setOrderDate(int orderDate) {
		this.orderDate = orderDate;
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


	public long getTransAmt() {
		return transAmt;
	}


	public void setTransAmt(long transAmt) {
		this.transAmt = transAmt;
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


	public int getBkFe() {
		return bkFee;
	}


	public void setBkFee(int bkFee) {
		this.bkFee = bkFee;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	


	/*public String getBusiType() {
		return busiType;
	}


	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
*/
	
	public String getBkFeeMode() {
		return bkFeeMode;
	}


	public void setBkFeeMode(String bkFeeMode) {
		this.bkFeeMode = bkFeeMode;
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


	public String getTransType() {
		return transType;
	}


	public void setTransType(String transType) {
		this.transType = transType;
	}


	public String getTerminal() {
		return terminal;
	}


	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}


	public String getOrgCode() {
		return orgCode;
	}


	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}



	@Override
	public List<String> getSql(String flowNo, String serviceType) {
		List<String> sqlList = new ArrayList<String>();
		
		String tseq = Ryt.genOidBySysTime();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into hlog(tseq,version,ip,mdate,mid,bid,oid,amount,type,gate,author_type,");
		sql.append("sys_date,init_sys_date,sys_time,fee_amt,tstat,bk_flag,bk_send,");
		sql.append("bk_seq1,bk_seq2,trans_period,card_no,gid,pay_amt,data_source,p1,p2,p4,p9,p10,bk_chk,bank_fee,bk_fee_model,bk_recv,bk_date) ");
		sql.append(" values(" + tseq + ",10,0,");
		sql.append(orderDate + ",");
		sql.append(Ryt.addQuotes(merId) + ",");
		sql.append(Ryt.addQuotes(bid) + ",");
		sql.append(Ryt.addQuotes(ordId) + ",");
		sql.append(transAmt + ",");
		sql.append("10," + gateId + ",0,");
		sql.append(sysDate + ",");
		sql.append(sysDate + ",");
		sql.append(sysTime + ",");
		sql.append(merFee + ","); 
		sql.append(transState + ",1,");
		sql.append(sysTime + ",");
		sql.append(Ryt.addQuotes(bkSeq) + ",");
		sql.append(Ryt.addQuotes(bkSeq) + ",0,");
		sql.append(Ryt.addQuotes(cardNo) + ",");
		sql.append(gid + ",");
		sql.append(payAmt + ",3,");
		sql.append(transType + ",");
		sql.append(Ryt.addQuotes(terminal) + ",");
		sql.append(Ryt.addQuotes(orgCode) + ",");
		sql.append(Ryt.addQuotes(flowNo)+","); //对账状态为勾兑成功
		sql.append(Ryt.addQuotes("") + ",1,");
		sql.append(bkFee+",");
		sql.append(Ryt.addQuotes(bkFeeMode)+",");
		sql.append(bkDate+",");
        sql.append(bkDate).append(");");
//		sql.append(Ryt.addQuotes(busiType) + ")");
		
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
			
			List<String> list = null;
			
			list = RecordLiveAccount.recordAccSeqsAndCalLiqBalance(params);
			
			if(list != null)
				sqlList.addAll(list);		
		}
		
		return sqlList;
	}

    /**
     * @return the bkDate
     */
    public int getBkDate() {
        return bkDate;
    }

    /**
     * @param bkDate the bkDate to set
     */
    public void setBkDate(int bkDate) {
        this.bkDate = bkDate;
    }
	
}
