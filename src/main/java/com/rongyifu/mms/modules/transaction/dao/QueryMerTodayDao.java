package com.rongyifu.mms.modules.transaction.dao;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class QueryMerTodayDao extends PubDao {
	private PubDao dao = new PubDao();
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo,Integer pageSize, String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gid, String bkseq,Integer mstate,String begintrantAmt,String endtrantAmt,String p15) {

		StringBuffer condition=getConditionSql( "tlog",mid,gate,tstat,type,oid,gid,null,null,null,tseq,bkseq,null,mstate,begintrantAmt,endtrantAmt,p15);
		// sqlCount总共多少�?sql查询要显示的数据
		String sqlCount = " SELECT  COUNT(a.tseq) " + condition.toString();
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,a.error_msg," +
				"case when a.p15='1' then '接口' when a.p15='2' then 'web'  when a.p15='3' then 'wap' when a.p15='4' then '控件' else  p15 end p15 "
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
				Integer mstate,String begintrantAmt,String endtrantAmt,String p15){
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
			if (!Ryt.empty(p15)) condition.append(" AND a.p15 = " + Ryt.addQuotes(p15));
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
		 * 单笔查询 融易通流水号
		 * @param tseq
		 * @return
		 */
		public OrderInfo queryHlogByTseq(String table,String tseq,String mid) {
			StringBuffer sqlBuff=new StringBuffer("select h.*,tr.trans_proof from ");
			StringBuffer sqlBuffer2=new StringBuffer("select t.*,tr2.trans_proof from ");
			sqlBuff.append("hlog").append(" as h left join tr_orders as tr on h.oid=tr.oid where 1=1 ");
			sqlBuffer2.append("tlog").append(" as t left join tr_orders as tr2 on t.oid=tr2.oid where 1=1 ");
			if (!Ryt.empty(tseq)) {
				sqlBuff.append(" and h.tseq=").append(Ryt.addQuotes(tseq));
				sqlBuffer2.append(" and t.tseq=").append(Ryt.addQuotes(tseq));
			}
			if(!Ryt.empty(mid)){
				sqlBuff.append(" and h.mid=").append(Ryt.addQuotes(mid));
				sqlBuffer2.append(" and t.mid=").append(Ryt.addQuotes(mid));
			}
			StringBuffer sql=new StringBuffer("select * from (");
			sql.append(sqlBuff).append("  union all ").append(sqlBuffer2).append(" ) as a ");
			return queryForObject(sql.toString(), OrderInfo.class);
		}

}
