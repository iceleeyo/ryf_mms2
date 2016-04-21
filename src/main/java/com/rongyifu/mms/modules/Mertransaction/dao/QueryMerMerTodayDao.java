package com.rongyifu.mms.modules.Mertransaction.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class QueryMerMerTodayDao extends PubDao{
	private  PubDao pubdao=new PubDao();
	
	public int updateNotifyStatus(String inStr,String table,int isNotice){
		StringBuilder sql = new StringBuilder("UPDATE ").append(Ryt.sql(table)).append(" SET is_notice = ");
		sql.append(isNotice).append(" WHERE tseq IN ").append(inStr);
		return pubdao.update(sql.toString());
	}
	
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo,Integer pageSize, String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gid, String bkseq,Integer mstate,String begintrantAmt,String endtrantAmt) {
  
		StringBuffer condition=getConditionSql( "tlog",mid,gate,tstat,type,oid,gid,null,null,null,tseq,bkseq,null,mstate,begintrantAmt,endtrantAmt);
		// sqlCount总共多少�?sql查询要显示的数据
		String sqlCount = " SELECT  COUNT(a.tseq) " + condition.toString();
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,a.error_msg "
						+ condition.toString() + " ORDER BY tseq DESC";
		// 交易总金额为[0],系统手续费总金额[1]
		String sqlCountTotle = " SELECT sum(a.amount)  " + condition.toString();
		String sqlSysAtmFeeTotle = " SELECT sum(a.fee_amt)" + condition.toString();
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sqlSysAtmFeeTotle);
		return queryForPage(sqlCount,sql, pageNo,pageSize,OrderInfo.class,sumSQLMap);
	}
	
	//当天交易查询的sql条件
	private StringBuffer getConditionSql(String queryTable,String mid, Integer gate, Integer tstat, Integer type,
				String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,String bkseq,Integer bkCheck,
				Integer mstate,String begintrantAmt,String endtrantAmt){
			StringBuffer condition = new StringBuffer();
			//String a="(select * from "+queryTable+" where type!=11) as a ";
			condition.append(" FROM ").append(queryTable+" as a, minfo m ").append(" WHERE  a.mid=m.id ");
			if (!Ryt.empty(mid)) condition.append(" AND a.mid = "+ Ryt.addQuotes(mid));
			if (gate != null) condition.append(" AND a.gate = " + gate);
			if (tstat != null) condition.append(" AND a.tstat = " + tstat);
			if (type != null){
				condition.append(" AND a.type = " + type);
			}else{
				condition.append(" AND a.type not in (11,12,16,17,14) ");
			}
			if (!Ryt.empty(tseq)) condition.append(" AND a.tseq = " + Ryt.addQuotes(tseq));
			if (gid != null) condition.append(" AND a.gid = " + gid);
			if (!Ryt.empty(oid)) condition.append(" AND a.oid like " + Ryt.addQuotes("%"+oid+"%"));
			if (date != null) {
				if (bdate != null) condition.append(" AND " + date + " >= " + bdate);
				if (edate != null) condition.append(" AND " + date + " <= " + edate);
			}
			if (!Ryt.empty(bkseq)) {
				condition.append(" AND (a.bk_seq1 = " + Ryt.addQuotes(bkseq));
				condition.append(" OR a.bk_seq2 = " + Ryt.addQuotes(bkseq));
				condition.append(" ) ");
			}
			if(bkCheck!=null){
				condition.append(" AND a.bk_chk= " + bkCheck);
			}
			if(mstate!=null){
				condition.append(" AND m.mstate= " + mstate);
			}
			if(!Ryt.empty(begintrantAmt)){
				condition.append(" AND a.amount>= " + Ryt.mul100toInt(begintrantAmt));
			}
			if(!Ryt.empty(endtrantAmt)){
				condition.append(" AND a.amount<=" + Ryt.mul100toInt(endtrantAmt));
			}
			return condition;
	}
			
	/**
	 * 明细页面单个对象
	 * @param tseq
	 * @param table(hlog or tlog)
	 * @return Hlog
	 */
	public Hlog queryHlogById(String tseq, String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("(select tseq,mid,oid,mdate,amount,type,gate,sys_date,sys_time,");
		sb.append("tstat,fee_amt,bank_fee,author_type,bk_url,mer_priv from ");
		sb.append("tlog");
		sb.append("  where tseq = ").append(Ryt.sql(tseq)).append(") UNION ALL(");
		sb.append("select tseq,mid,oid,mdate,amount,type,gate,sys_date,sys_time,");
		sb.append("tstat,fee_amt,bank_fee,author_type,bk_url,mer_priv from ");
		sb.append("hlog");
		sb.append("  where tseq = ").append(Ryt.sql(tseq)).append(")  limit 1");
		return queryForObject(sb.toString(),Hlog.class);
	}

}
