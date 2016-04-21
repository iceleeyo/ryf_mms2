package com.rongyifu.mms.dao;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;

public class GenerationOfBuckleDao  extends PubDao{
	/**
	 * 保存单笔代扣订单信息
	 * @param dkAccNo 扣款人帐号
	 * @param dkAccName 扣款人户名
	 * @param dkBankNo 开户银行号
	 * @param dkBankName 开户银行名
	 * @param dkIDType 扣款人证件类型
	 * @param dkIDNo 扣款人证件号
	 * @param dkKZType 卡折标志
	 * @param dkAmt 交易金额
	 * @param remark 备注
	 * @return 订单信息
	 * @throws Exception 
	 */
	public int[] saveOrderOfDBDaiKou(String tseq,String dkAccNo,String dkAccName,String dkBankNo,String dkBankName,String dkIDType,
			String dkIDNo,Integer dkKZType,double dkAmt,String remark) throws Exception{
		//商户号
		String mid=getLoginUserMid();
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		//保存到数据库的交易金额乘以100
		String saveAmount=Ryt.mul100(dkAmt+"");
		//保存到数据库的支付总金额乘以100
//		String payAmt=Ryt.mul100((dkAmt+feeAmt)+"");
		StringBuffer hlogSql=new StringBuffer("insert into tlog(tstat,version,ip,gate,mdate,sys_date,init_sys_date,sys_time,mid,amount,type,pay_amt,p1,p2,p3,p4,p5,p6,p7) values(");
		hlogSql.append(Constant.PayState.WAIT_PAY+",");
		hlogSql.append(10+",");
		hlogSql.append("3232251650,");
		hlogSql.append("4000,");
		hlogSql.append(date+",");
		hlogSql.append(date+",");
		hlogSql.append(date+",");
		hlogSql.append(time+",");
		hlogSql.append(Ryt.addQuotes(mid)+",");
		hlogSql.append(saveAmount+",");
		hlogSql.append(Constant.TlogTransType.PROXY_PAYABLE_PRIVATE+",");
		hlogSql.append(saveAmount+",");
//		hlogSql.append(Ryt.mul100(feeAmt+"")+",");
		hlogSql.append(Ryt.addQuotes(dkAccNo)+",");
		hlogSql.append(Ryt.addQuotes(dkAccName)+",");
		hlogSql.append(Ryt.addQuotes(dkBankNo)+",");
		hlogSql.append(Ryt.addQuotes(dkIDType)+",");
		hlogSql.append(Ryt.addQuotes(dkIDNo)+",");
		hlogSql.append(Ryt.addQuotes(dkKZType+"")+",");
		hlogSql.append(Ryt.addQuotes(Ryt.sql(remark))+")");
		StringBuffer trorderSql=new StringBuffer("insert into tr_orders(oid,uid,aid,aname,init_time,ptype,gate,trans_amt," +
				"pay_amt,to_acc_name,to_acc_no,to_bk_name,to_bk_no,remark) values(");
		trorderSql.append(Ryt.addQuotes(tseq)+",");
		trorderSql.append(Ryt.addQuotes(mid)+",");
		trorderSql.append(Ryt.addQuotes(mid)+",");
		trorderSql.append(Ryt.addQuotes(queryJSZHYE(mid).getAname())+",");
		trorderSql.append(DateUtil.getIntDateTime()+",");
		trorderSql.append(Constant.DaiFuTransType.PROXY_PAYABLE_PRIVATE+",");
		trorderSql.append("4000,");
		trorderSql.append(saveAmount+",");
//		trorderSql.append(Ryt.mul100(feeAmt+"")+",");
		trorderSql.append(saveAmount+",");
		trorderSql.append(Ryt.addQuotes(dkAccName)+",");
		trorderSql.append(Ryt.addQuotes(dkAccNo)+",");
		trorderSql.append(Ryt.addQuotes(dkBankName)+",");
		trorderSql.append(Ryt.addQuotes(dkBankNo)+",");
		trorderSql.append(Ryt.addQuotes(remark)+")");
		return batchSqlTransaction(new String[]{hlogSql.toString(),trorderSql.toString()});
	}
	/**
	 * 查询结算账户信息
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public AccInfos queryJSZHYE(String mid){
		StringBuffer sql=new StringBuffer("select * from acc_infos where uid=aid and uid=");
		sql.append(Ryt.addQuotes(Ryt.sql(mid)));
		return queryForObject(sql.toString(),AccInfos.class);
	}
}
