package com.rongyifu.mms.bean;

public class MergeDetail {

		private String mid;		//商户号
		private String abbrev;		//简称
		private Integer bankType;   //类型
		private Long payAmt;       //交易金额
		private Integer bankFee;   //银行手续费
		private Long refAmt;       //退款金额
		private Integer bkFee;		//应收银行退回手续费
		private Integer bkFeeReal;  //实收银行退回手续费
		private String bkName;		//简称
		
		public MergeDetail() {
		}

		public String getBkName() {
			return bkName;
		}
		
		public void setBkName(String bkName) {
			this.bkName = bkName;
		}
		public Integer getBkFee() {
			return bkFee == null ? 0 : bkFee;
		}
		public void setBkFee(Integer bkFee) {
			this.bkFee = bkFee;
		}
		public Integer getBkFeeReal() {
			return bkFeeReal == null ? 0 : bkFeeReal; 
		}
		public void setBkFeeReal(Integer bkFeeReal) {
			this.bkFeeReal = bkFeeReal;
		}
		public String getMid() {
			return mid;
		}
		public void setMid(String mid) {
			this.mid = mid;
		}
		public String getAbbrev() {
			return abbrev;
		}
		public void setAbbrev(String abbrev) {
			this.abbrev = abbrev;
		}
//		public Long getAmount() {
//			return amount == null ? 0 : amount;
//		}
//		public void setAmount(Long amount) {
//			this.amount = amount;
//		}
		public Long getPayAmt() {
			return payAmt == null ? 0 : payAmt;
		}

		public void setPayAmt(Long payAmt) {
			this.payAmt = payAmt;
		}
		public Integer getBankFee() {
			return bankFee == null ? 0 : bankFee;
		}

		public void setBankFee(Integer bankFee) {
			this.bankFee = bankFee;
		}
		public Long getRefAmt() {
			return refAmt == null ? 0 : refAmt;
		}
		public void setRefAmt(Long refAmt) {
			this.refAmt = refAmt;
		}
		public Integer getBankType() {
			return bankType == null ? 0 : bankType;
		}
		public void setBankType(Integer bankType) {
			this.bankType = bankType;
		}

}
