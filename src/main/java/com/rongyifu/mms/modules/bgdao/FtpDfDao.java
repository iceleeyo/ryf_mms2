package com.rongyifu.mms.modules.bgdao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.api.b2b.order.Order;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

/***
 * FTP 代付 dao
 * @author shdy
 *
 */
public class FtpDfDao extends PubDao {

	/****
	 * 获取联行号
	 * @param gate
	 * @param accProvId
	 * @param gid 支付渠道
	 * @return
	 */
	public String getRecBankNo(String gate,String gid,String accProvId){
		String gate2=gate.substring(2);
		String type=null;
		if(gate.contains("003") && gid.equals("40001")){
			type="02";
		}else{
			type="01";
		}
		StringBuffer sql=new StringBuffer("select bk_no from bank_no_info ");
		sql.append(" where gid=").append(Ryt.addQuotes(gate2));
		sql.append(" and prov_id=").append(accProvId);
		sql.append(" and  type=").append(type).append(";");
		return this.queryForString(sql.toString());
	}
	
	/***
	 * 获取支付网关
	 * @param gid
	 * @return
	 */
	public B2EGate getB2eGate(Integer gid){
		StringBuffer sql=new StringBuffer("select * from b2e_gate");
		sql.append(" where gid=").append(gid).append(";");
		return this.queryForObject(sql.toString(), B2EGate.class);
	}
	
	/***
	 * 修改	申请再次支付状态
	 * @return
	 */
	public boolean updateIsAgainPay(List<OrderInfo> os,Integer againPayStatus){
		boolean res=false;
		StringBuffer sql=new StringBuffer("update tlog set ");
		sql.append(" againPay_status=").append(againPayStatus);
		sql.append(" where tseq in ( ");
		String tmp="";
		for (OrderInfo orderInfo : os) {
			tmp+=Ryt.addQuotes(orderInfo.getTseq())+",";
		}
		tmp=tmp.substring(0, tmp.length()-1);
		sql.append(tmp).append(" )");
		Integer affectLines=this.update(sql.toString());
		if(affectLines==os.size()){
			res=true;
		}
		return res;
	}
	
	
	
	public CurrentPage<OrderInfo> querypaymentHlogDetail(Integer pageNo,
			Integer pageSize, String mid, Integer gate, Integer tstat,
			String oid, Integer gid, String date, Integer bdate, Integer edate,
			String tseq, Integer bkCheck, String bkseq,Integer mstate,Integer type,String batchNo) {
		StringBuffer condition = getpaymentSql("tlog", "hlog", mid, gate,
				tstat, oid, gid, date, bdate, edate, tseq, bkCheck, bkseq,mstate,type,batchNo);
		// sqlCount总共多少�?sql查询要显示的?���?
		String sqlCount = " SELECT  COUNT(a.tseq) from  ("
				+ condition.toString() + ") as a";
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,a.p8,a.p1,a.p2,a.p3,a.error_msg,a.againPay_status as againPay_status,a.pay_amt as payAmt from ("
				+ condition.toString()
				+ ") as a order by sys_date desc,sys_time desc";
		// 交易总金额为[0],系统手续费总金额[1]
		String amtSumSql = " SELECT -sum(ABS(a.amount)) from ("
				+ condition.toString() + ") as a";
		String sysAtmFeeSumSql = " SELECT sum(a.fee_amt) from ("
				+ condition.toString() + ") as a";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount, sql, pageNo, pageSize, OrderInfo.class,
				sumSQLMap);
	}
	
	//代付历史交易查询的条�?
	@SuppressWarnings("unused")
	private StringBuffer getpaymentSql(String queryTable,String queryTable2,String mid, Integer gate, Integer tstat,
			String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,String bkseq,Integer mstate,Integer type,String batchNo){
		StringBuffer paymentcondition = new StringBuffer();
		paymentcondition.append(" select * FROM ").append(queryTable+" as t,minfo m").append(" WHERE  t.mid=m.id ");
		if (!Ryt.empty(mid)) paymentcondition.append(" AND t.mid = "+ Ryt.addQuotes(mid));
		if (gate != null) {
			String gate1 = gate.toString();
			String gate2 = gate1.substring(2);
				String gate3;
				if (type == 11) {
					gate3 = "71";
				} else if (type == 12) {
					gate3 = "72";
				} else if (type == 16) {
					gate3 = "73";    
				}else if(type == 17){
					gate3 = "74";
				}
				else {
					gate3 = "";
				}
				paymentcondition.append(" AND t.gate like ").append(Ryt.addQuotes(
						"%" +gate3+gate2 + "%"));
			}
		paymentcondition.append(" AND t.againPay_status=2"); //追加查询条件  申请再次支付状态为2的数据
		if (!Ryt.empty(tseq)) paymentcondition.append(" AND t.tseq = " + Ryt.addQuotes(String.valueOf(tseq)));
		if (gid != null) paymentcondition.append(" AND t.gid = " + gid);
		if (!Ryt.empty(oid)) paymentcondition.append(" AND t.oid = " + Ryt.addQuotes(oid));
		if (date != null ) {
			if (bdate != null) paymentcondition.append(" AND t." + date + " >= " + bdate);
			if (edate != null) paymentcondition.append(" AND t." + date + " <= " + edate);
		}
		if (!Ryt.empty(bkseq)) {
			paymentcondition.append(" AND (t.bk_seq1 = " + Ryt.addQuotes(bkseq));
			paymentcondition.append(" OR t.bk_seq2 = " + Ryt.addQuotes(bkseq));
			paymentcondition.append(" ) ");
		}
		if(bkCheck!=null){
			paymentcondition.append(" AND t.bk_chk= " + bkCheck);
		}
		if (type ==null )type=-1;
		if( type>0 ){
			paymentcondition.append(" AND t.type="+type);
		} else {
			paymentcondition.append(" AND t.type in(11,12,16,17)");
		}
		if(!Ryt.empty(batchNo)){
			paymentcondition.append(" AND t.p8="+Ryt.addQuotes(batchNo));
		}
		paymentcondition.append(" AND t.mid=m.id and data_source <> 8 ");
		if (mstate != null) paymentcondition.append(" AND m.mstate = " + mstate);
		StringBuffer paymentcondition1 = new StringBuffer();
		paymentcondition1 .append("select * FROM ").append(queryTable2+" as h,minfo m1").append(" WHERE  h.mid=m1.id");
		if (!Ryt.empty(mid)) paymentcondition1.append(" AND h.mid = "+ Ryt.addQuotes(mid));
		if (gate != null) {
			String gate1 = gate.toString();
			String gate2 = gate1.substring(2);
				String gate3;
				if(type == null){
					gate3="";
				}else	if (type == 11) {
					gate3 = "71";
				} else if (type == 12) {
					gate3 = "72";
				} else if (type == 16) {
					gate3 = "73";    
				}else if(type == 17){
					gate3 = "74";
				}
				else {
					gate3 = "";
				}
				paymentcondition1.append(" AND h.gate like ").append(Ryt.addQuotes(
						"%" +gate3+gate2 + "%"));
			}
		
		if (!Ryt.empty(tseq)) paymentcondition1.append(" AND h.tseq = " + Ryt.addQuotes(String.valueOf(tseq)));
		if (gid != null) paymentcondition1.append(" AND h.gid = " + gid);
		if (!Ryt.empty(oid)) paymentcondition1.append(" AND h.oid = " + Ryt.addQuotes(oid));
		if (date != null) {
			if (bdate != null) paymentcondition1.append(" AND h." + date + " >= " + bdate);
			if (edate != null) paymentcondition1.append(" AND h." + date + " <= " + edate);
		}
		if (!Ryt.empty(bkseq)) {
			paymentcondition1.append(" AND (h.bk_seq1 = " + Ryt.addQuotes(bkseq));
			paymentcondition1.append(" OR h.bk_seq2 = " + Ryt.addQuotes(bkseq));
			paymentcondition1.append(" ) ");
		}
		if(bkCheck!=null){
			paymentcondition1.append(" AND h.bk_chk= " + bkCheck);
		}
		if(type>0){
			paymentcondition1.append(" AND h.type="+type);
		} else {
			paymentcondition1.append(" AND h.type in(11,12,16,17)");
		}
		if(!Ryt.empty(batchNo)){
			paymentcondition1.append(" AND h.p8="+Ryt.addQuotes(batchNo));
		}
		if (mstate != null) paymentcondition1.append(" AND m1.mstate = " + mstate);
		paymentcondition1.append(" AND h.mid=m1.id and data_source <> 8 ");
		paymentcondition.append(" union all ").append(paymentcondition1);
		return paymentcondition;
	}
	
	public OrderInfo getOrderDetail(String tseq){
		StringBuffer sql=new StringBuffer("select * from tlog ");
		sql.append(" where tseq=").append(Ryt.addQuotes(tseq));
		sql.append(" union all	");
		sql.append("select * from hlog ");
		sql.append(" where tseq=").append(Ryt.addQuotes(tseq));
		return (OrderInfo) this.queryForObject(sql.toString(), Order.class);
	}
	
}
