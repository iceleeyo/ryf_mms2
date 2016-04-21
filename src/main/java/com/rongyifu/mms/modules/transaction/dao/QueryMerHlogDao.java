package com.rongyifu.mms.modules.transaction.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class QueryMerHlogDao extends PubDao {
	private PubDao dao = new PubDao();
 	
 	/**
 	 * 根据条件查询交易明细
 	 * @return CurrentPage
 	 */
 
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo,Integer pageSize,Map<String, String> param) {
	String tableName = "hlog";
	if(null != param.get("isBackupTable") && 1 == Integer.parseInt(param.get("isBackupTable"))){
		tableName = "hlog_201503";//hlog备份表
	}
	param.put("bkseq", null);
	StringBuffer condition=getConditionSql(tableName,param);
	// sqlCount总共多少�?sql查询要显示的数据
	String sqlCount = " SELECT COUNT(a.tseq) " + condition.toString();
	String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.pay_amt,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,error_msg,a.p10," +
			"case when a.p15='1' then '接口' when a.p15='2' then 'web'  when a.p15='3' then 'wap' when a.p15='4' then '控件' else  p15 end p15 "
					+ condition.toString() + " order by sys_date desc,sys_time desc";
	// 交易总金额为[0],系统手续费总金额[1]
	String amtSumSql = " SELECT sum(a.amount)  " + condition.toString();
	String sysAtmFeeSumSql = " SELECT sum(a.fee_amt)" + condition.toString();
		Map<String,String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount,sql, pageNo,pageSize,OrderInfo.class,sumSQLMap);
}
			
			private StringBuffer getConditionSql(String queryTable,Map<String, String> param){
				StringBuffer condition = new StringBuffer();
				//String a="(select * from "+queryTable+" where type!=11) as a ";
				condition.append(" FROM ").append(queryTable+" as a, minfo m ").append(" WHERE  a.mid=m.id ");
				if (!Ryt.empty(param.get("mid"))) condition.append(" AND a.mid = "+ Ryt.addQuotes(param.get("mid")));
				if (!Ryt.empty(param.get("gate"))) condition.append(" AND a.gate = " + param.get("gate"));
				if (!Ryt.empty(param.get("tstat")) ) condition.append(" AND a.tstat = " + param.get("tstat"));
				if (!Ryt.empty(param.get("type"))){
					condition.append(" AND a.type = " + param.get("type"));
				}else{
					condition.append(" AND a.type not in (11,12,16,17,14) ");
				}
				if (!Ryt.empty(param.get("tseq"))) condition.append(" AND a.tseq = " + Ryt.addQuotes(param.get("tseq")));
				if (!Ryt.empty(param.get("p15"))) {
					condition.append(" AND a.p15 = " + Ryt.addQuotes(param.get("p15")));
				}
				if (!Ryt.empty(param.get("gateRouteId")) ) condition.append(" AND a.gid = " + param.get("gateRouteId"));
				if (!Ryt.empty(param.get("oid"))) condition.append(" AND a.oid like " + Ryt.addQuotes("%"+param.get("oid")+"%"));
				if (!Ryt.empty(param.get("date")) ) {
					if (!Ryt.empty(param.get("bdate"))) condition.append(" AND " + param.get("date") + " >= " + param.get("bdate"));
					if (!Ryt.empty(param.get("edate"))) condition.append(" AND " + param.get("date") + " <= " + param.get("edate"));
				}

				if (!Ryt.empty(param.get("beginDate"))) condition.append(" AND  a.p10   >= " + param.get("beginDate"));
				if ( !Ryt.empty(param.get("endDate"))) condition.append(" AND  a.p10  <= " + param.get("endDate"));

				if (!Ryt.empty(param.get("bkseq"))) {
					condition.append(" AND (a.bk_seq1 = " + Ryt.addQuotes(param.get("bkseq")));
					condition.append(" OR a.bk_seq2 = " + Ryt.addQuotes(param.get("bkseq")));
					condition.append(" ) ");
				}
				if(!Ryt.empty(param.get("bkCheck"))){
					condition.append(" AND a.bk_chk= " + param.get("bkCheck"));
				}
				if(!Ryt.empty(param.get("mstate"))){
					condition.append(" AND m.mstate= " + param.get("mstate"));
				}
				if(!Ryt.empty(param.get("begintrantAmt"))){
					condition.append(" AND a.amount>= " + Ryt.mul100toInt(param.get("begintrantAmt")));
				}
				if(!Ryt.empty(param.get("endtrantAmt"))){
					condition.append(" AND a.amount<=" + Ryt.mul100toInt(param.get("endtrantAmt")));
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
				sqlBuff.append(table).append(" as h left join tr_orders as tr on h.oid=tr.oid where 1=1 ");
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
