package com.rongyifu.mms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.rongyifu.mms.bean.Elog;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.TlogCollect;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.ewp.InvokerEwpPub;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.PaginationHelper;
import com.rongyifu.mms.web.WebConstants;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TransactionDao extends PubDao {
	
	
	public List<OrderInfo> queryFailedRecordForDownload(String mid, String oid,Integer gate,Integer tstat, Integer type, String tseq, Double amount,Integer bdate, Integer edate){
		StringBuilder sqlFetchRows1 = new StringBuilder("SELECT tseq,mid,oid,mdate,amount,tstat,type,gate,fee_amt,sys_date FROM hlog WHERE is_notice <> 1");
		StringBuilder sqlFetchRows2 = new StringBuilder("SELECT tseq,mid,oid,mdate,amount,tstat,type,gate,fee_amt,sys_date FROM tlog WHERE is_notice <> 1");
		StringBuilder condition = new StringBuilder();
		if(StringUtils.isNotBlank(tseq)){
			condition.append(" AND tseq = ").append(Ryt.sql(tseq));
		}
		if(StringUtils.isNotBlank(mid)){
			condition.append(" AND mid = ").append(Ryt.addQuotes(mid));
		}
		if(StringUtils.isNotBlank(oid)){
			condition.append(" AND oid = ").append(Ryt.addQuotes(oid));
		}
		if(null != amount){
			String str = amount*100+"";
			condition.append(" AND amount = ").append(str.substring(0,str.lastIndexOf(".")));
		}
		if(null != gate){
			condition.append(" AND gate = ").append(gate);
		}
		if(null != tstat){
			condition.append(" AND tstat = ").append(tstat);
		}
		if(null != type){
			condition.append(" AND type = ").append(type);
		}
		if(null != bdate){
			condition.append(" AND sys_date >= ").append(bdate);
		}
		if(null != edate){
			condition.append(" AND sys_date <= ").append(edate);
		}
		sqlFetchRows1.append(condition);
		sqlFetchRows2.append(condition);
		StringBuilder sqlFetchRows = new StringBuilder("(").append(sqlFetchRows1).append(") UNION ALL (").append(sqlFetchRows2).append(")");
		return query(sqlFetchRows.toString(), OrderInfo.class);
	}
	
	/**
	 * @return
	 * 掉单交易查询
	 */
	public CurrentPage<OrderInfo> queryNotifyFailedOrderRecord(Integer pageNo, String mid, String oid, Integer gate,Integer tstat, Integer type, String tseq, Double amount,Integer bdate, Integer edate){
		StringBuilder sqlFetchRows1 = new StringBuilder("SELECT tseq,mid,oid,mdate,amount,tstat,type,gate,fee_amt,sys_date FROM hlog WHERE is_notice <> 1 ");
		StringBuilder sqlCountRows1 = new StringBuilder("SELECT COUNT(*) FROM hlog WHERE is_notice <> 1");
		StringBuilder sqlFetchRows2 = new StringBuilder("SELECT tseq,mid,oid,mdate,amount,tstat,type,gate,fee_amt,sys_date FROM tlog WHERE is_notice <> 1");
		StringBuilder sqlCountRows2 = new StringBuilder("SELECT COUNT(*) FROM tlog WHERE is_notice <> 1");
		StringBuilder condition = new StringBuilder();
		if(StringUtils.isNotBlank(tseq)){
			condition.append(" AND tseq = ").append(Ryt.sql(tseq));
		}
		if(StringUtils.isNotBlank(mid)){
			condition.append(" AND mid = ").append(Ryt.addQuotes(mid));
		}
		if(StringUtils.isNotBlank(oid)){
			condition.append(" AND oid = ").append(Ryt.addQuotes(oid));
		}
		if(null != amount){
			String str = amount*100+"";
			condition.append(" AND amount = ").append(str.substring(0,str.lastIndexOf(".")));
		}
		if(null != gate){
			condition.append(" AND gate = ").append(gate);
		}
		if(null != tstat){
			condition.append(" AND tstat = ").append(tstat);
		}
		if(null != type){
			condition.append(" AND type = ").append(type);
		}
		if(null != bdate){
			condition.append(" AND sys_date >= ").append(bdate);
		}
		if(null != edate){
			condition.append(" AND sys_date <= ").append(edate);
		}
		sqlFetchRows1.append(condition);
		sqlCountRows1.append(condition);
		sqlFetchRows2.append(condition);
		sqlCountRows2.append(condition);
		StringBuilder sqlFetchRows = new StringBuilder("(").append(sqlFetchRows1).append(") UNION ALL (").append(sqlFetchRows2).append(")");
		StringBuilder sqlCountRows = new StringBuilder("SELECT ((").append(sqlCountRows1).append(")+(").append(sqlCountRows2).append("))");
		return queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), pageNo, OrderInfo.class);
	}

	/**
	 * 明细页面单个对象
	 * @param tseq
	 * @param table(hlog or tlog)
	 * @return Hlog
	 */
	public Hlog queryHlogById(String tseq, String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select tseq,mid,oid,mdate,amount,type,gate,sys_date,sys_time,");
		sb.append("tstat,fee_amt,bank_fee,author_type,bk_url,mer_priv from ");
		sb.append(Ryt.sql(table));
		sb.append("  where tseq = " + Ryt.addQuotes(tseq));
		return queryForObject(sb.toString(),Hlog.class);
	}
	
	public Hlog getHlogById(String tseq, String table){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(Ryt.sql(table));
		sb.append("  where tseq = " + Ryt.addQuotes(tseq));
		return queryForObject(sb.toString(),Hlog.class);
	}
	
	public int updateSysTime(String tseq,String chekNo){
		int nTime = DateUtil.getCurrentUTCSeconds();
		String sql = "update tlog set sys_time = " + nTime + " where tseq =  " +Ryt.addQuotes(tseq);
		 update(sql);
		
		
		String sql2 = "update mlog set chk_no = '" + chekNo + "' where tseq =  " +Ryt.addQuotes(tseq);
		 update(sql2);
		
		return nTime;
	}
	/**
	 * 拼接SQL 汇总页 
	 * @param mid
	 * @param moreMid
	 * @param oid
	 * @param sysdate_b
	 * @param sysdate_e
	 * @param systime_b
	 * @param systime_e
	 * @return
	 */
	public CurrentPage<TlogCollect> querySql4Collect(int pageNo, String mid, String timeType, String gate,
					String date_b, String date_e) {
		mid=Ryt.addQuotes(mid);
		timeType=Ryt.sql(timeType);
		gate=Ryt.sql(gate);
		date_b=Ryt.sql(date_b);
		date_e=Ryt.sql(date_e);
		StringBuilder selectSqlCollect = new StringBuilder();
		String chosen = "sum(pay_suc_cnt) as pay_suc_cnt, sum(pay_suc_amt) as pay_suc_amt,"
						+ "sum(pay_fai_cnt) as pay_fai_cnt,sum(pay_fai_amt) as pay_fai_amt,sum(cnl_suc_cnt) as cnl_suc_cnt,"
						+ "sum(cnl_suc_amt) as cnl_suc_amt,sum(cnl_fai_cnt) as cnl_fai_cnt, sum(cnl_fai_amt) as cnl_fai_amt ";

		// 从汇总表查数�?拼接SQL语句
		if (timeType.equals("day")) {
			selectSqlCollect.append("select mid,sys_date," + chosen + "  from tlog_collect where ");
		} else if (timeType.equals("month")) {
			selectSqlCollect.append("select mid,year(sys_date)*100+month(sys_date) as sys_date," + chosen
							+ "  from tlog_collect where");
		} else if (timeType.equals("season")) {
			selectSqlCollect.append("select mid,concat(year(sys_date) ,((MONTH(sys_date)-1) DIV 3) +1 ) as sys_date ,"
							+ chosen + "  from tlog_collect where");
		} else {
			selectSqlCollect.append("select mid,year(sys_date) as sys_date," + chosen + " from tlog_collect  where");
		}

		// mid
		if (!Ryt.empty(mid)) {
			selectSqlCollect.append(" mid= " + mid + " and ");
		}
		// 系统日期
		if (!Ryt.empty(date_b)) {
			selectSqlCollect.append(" sys_date >= " + date_b + " and ");
		}
		if (!Ryt.empty(date_e)) {
			selectSqlCollect.append(" sys_date <= " + date_e + " and ");
		}
		// ?���?
		if (!Ryt.empty(gate)) {
			selectSqlCollect.append(" gate = " + gate + " and ");
		}
		
		String selectList = selectSqlCollect.toString();

		if (timeType.equals("day")) {
			selectList = selectList.substring(0, (selectList.length() - 5))
							+ "  group by sys_date,mid order by sys_date desc ";
		} else if (timeType.equals("month")) {
			selectList = selectList.substring(0, (selectList.length() - 5))
							+ "  group by year(sys_date)*100+month(sys_date),mid order by year(sys_date)*100+month(sys_date) desc ";
		} else if (timeType.equals("season")) {
			selectList = selectList.substring(0, (selectList.length() - 5))
							+ "  group by year(sys_date),((MONTH(sys_date)-1) DIV 3) +1,mid order by year(sys_date) desc ";
		} else {
			selectList = selectList.substring(0, (selectList.length() - 5))
							+ "  group by year(sys_date),mid order by year(sys_date) desc ";
		}
		String selectCount = " select count(*) from ( " + selectList + " ) temp";
		PaginationHelper<TlogCollect> ph = new PaginationHelper<TlogCollect>();
		CurrentPage<TlogCollect> p = ph.fetchPage(getJdbcTemplate(), selectCount, selectList, new Object[] {}, pageNo, AppParam.getPageSize(), new BeanPropertyRowMapper(TlogCollect.class));
		return p;
	}

	/**
	 * 拼接SQL 原始日志页面
	 * @param mid
	 * @param moreMid
	 * @param oid
	 * @param sysdate_b
	 * @param sysdate_e
	 * @param systime_b
	 * @param systime_e
	 * @return
	 */
	public CurrentPage<Elog> querySql4Log(int pageNo, String mid, String oid, String sysdate_b, String sysdate_e,
					String systime_b, String systime_e) {

		StringBuilder sb = new StringBuilder();
		sb.append("select mdate,mid,oid,amount,type,ip,sys_date,sys_time from elog where");

		// 系统日期
		if (!Ryt.empty(sysdate_b)) {
			sb.append(" sys_date >= " + sysdate_b + " and ");
		}
		if (!Ryt.empty(sysdate_e)) {
			sb.append(" sys_date <= " + sysdate_e + " and ");
		}
		// 订单
		if (!Ryt.empty(oid)) {
			sb.append(" oid = " + Ryt.addQuotes(oid) + " and ");
		}
		// mid
		if (!Ryt.empty(mid)) {
			sb.append(" mid= " + Ryt.addQuotes(mid) + " and ");
		}
		// 系统时间
		if (!Ryt.empty(systime_b)) {
			sb.append(" sys_time >= " + systime_b + "*3600 and ");
		}
		if (!Ryt.empty(systime_e)) {
			sb.append(" sys_time <= " + systime_e + "*3600 and ");
		}
		String selectList = sb.toString();
		selectList = selectList.substring(0, (selectList.length() - 5));
		String selectCount = " select count(*) from ( " + selectList + " )  temp";
		selectList = selectList + " order by sys_date desc,sys_time desc";

		return queryForPage( selectCount, selectList, pageNo, AppParam.getPageSize(),Elog.class);
	}

	/**
	 * 判断是否为管理员
	 * @return
	 */
	public String querySql4Special() {
		String sql = "select * from  hlog  where ";
		WebContext wc = WebContextFactory.get();
		HttpSession hs = wc.getSession();
		String loginMid =hs.getAttribute(WebConstants.SESSION_LOGGED_ON_MID).toString();
		if (!"1".equals(loginMid)) {
			sql += " mid = '" + loginMid + "' and ";
		}
		return sql;
	}

	/**
	 * 单笔查询
	 * @param gate 银行
	 * @param bk_date 银行日期
	 * @param bk_seq1 银行流水1
	 * @param bk_seq2 银行流水2
	 * @return
	 */
	public Hlog querySql4SpecialBankId(String gate, String bk_date, String bk_seq1, String bk_seq2) {

		StringBuilder sb = new StringBuilder();
		sb.append(querySql4Special());
		if (!Ryt.empty(bk_date)) {
			sb.append(" bk_date = " + bk_date + " and ");
		}
		// 网关
		if (!Ryt.empty(gate)) {
			sb.append(" gate = " + gate + " and ");
		}
		// 时间
		if (!Ryt.empty(bk_seq1)) {
			sb.append(" bk_seq1 = " + Ryt.addQuotes(bk_seq1) + " and ");
		}
		if (!Ryt.empty(bk_seq2)) {
			sb.append(" bk_seq2 = " + Ryt.addQuotes(bk_seq2) + " and ");
		}
		String selectHlog = sb.delete((sb.length() - 5), sb.length()).toString() + " limit 1";
		return queryForObject(selectHlog,Hlog.class);
	}

	/**
	 * 单笔查询
	 * @param mid 商户号
	 * @param mdate 订单号
	 * @param oid 时间
	 * @return
	* @throws Exception 
 	 */
 	public OrderInfo queryHlogByMer(String mid, Integer mdate, String oid) throws Exception {
 		oid=Ryt.sql(oid);
		StringBuilder sb = new StringBuilder();
			sb.append(querySql4Special());
			if (mid != null && !mid.trim().equals("")) {
				sb.append(" mid = '"+Ryt.checkSingleQuotes(mid)+"'"+" and ");
			}
			if (oid != null && !oid.equals("")) {
				sb.append(" oid = '" + Ryt.checkSingleQuotes(oid) + "' and ");
			}
			if (mdate != null && !mdate.equals("")) {
				sb.append(" mdate = " + mdate + " and ");
			}
			String selectHlog = sb.delete((sb.length() - 5), sb.length()).toString() + " order by tseq desc limit 1 " ;
			OrderInfo hlog=null;
 		try {
			hlog=queryForObjectThrowException(selectHlog, OrderInfo.class);
 		} catch (Exception e) {			
 			Map<String, String> params = new HashMap<String, String>();
 			params.put("mid", mid);
			params.put("mdate", String.valueOf(mdate));
			params.put("oid", oid);
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("TransactionDao", "queryHlogByMer(String mid, Integer mdate, String oid)", "", params);
			
			throw new Exception(e.getMessage());
		}
		return hlog;
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

	/**
	 * 单笔查询 融易通流水号
	 * @param tseq
	 * @return
	 */
	public OrderInfo queryHlogByBlogTseq(String table,String tseq,String mid) {
		StringBuffer sqlBuff=new StringBuffer("select h.*,tr.trans_proof from ");
		sqlBuff.append(table).append(" as h left join tr_orders as tr on h.oid=tr.oid where 1=1 ");
		if (!Ryt.empty(tseq)) {
			sqlBuff.append(" and h.tseq=").append(Ryt.addQuotes(tseq));
		}
		if(!Ryt.empty(mid)){
			sqlBuff.append(" and h.mid=").append(Ryt.addQuotes(mid));
		}
		return queryForObject(sqlBuff.toString(), OrderInfo.class);
	}
	
	
	public int queryTransLimit (String mid){
		return  queryForInt("select trans_limit  from minfo where id = "+Ryt.addQuotes(mid));
	}
	
	/** 
	 * 手机支付订单生成
	 * @param merId
	 * @param ordId
	 * @param transAmt *100的�?
	 * @param phoneNumber
	 * @param payTimePeriod
	 * @return
	 */
	public String doPhonePay(String merId,String ordId,long transAmt,String phoneNo,String payTimePeriod,int operId){
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("type", "phonePay");
		paramMap.put("sign", MD5.getMD5("RYFphonePay".getBytes()));
		paramMap.put("mid", merId);
		paramMap.put("phoneNo", phoneNo);
		paramMap.put("payTimePeriod", payTimePeriod);
		paramMap.put("userOid", ordId);
		paramMap.put("transAmt", transAmt);
		return InvokerEwpPub.doEntry(paramMap);
	}
	/** 
	 * 手机订单查询(页面)
	 * @param page
	 * @param mid
	 * @param oid
	 * @param state
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 */
	public CurrentPage<Hlog> queryPhonePays(int page, String mid, String oid, String tstat, String dateBegin, String dateEnd,String operId) {
		
		StringBuffer selecSql = new StringBuffer();
		selecSql.append("select tseq,oid,amount,sys_date,sys_time,tstat,phone_no,trans_period,oper_id  " );
		selecSql.append("from tlog where type= 2 and mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(oid)) {
			selecSql.append(" and oid = ").append(Ryt.addQuotes(oid));
		}
		if (!Ryt.empty(tstat)) {
			selecSql.append(" and tstat = " + tstat );
		}
		if (!Ryt.empty(dateBegin)) {
			selecSql.append(" and sys_date >=" + dateBegin );
		} 
		if (!Ryt.empty(dateEnd)) {
			selecSql.append(" and sys_date <=" + dateEnd );
		}
		if (!Ryt.empty(operId)) {
			selecSql.append(" and oper_id = " + operId );
		}
		
		String sql = selecSql.toString() + " union " + selecSql.toString().replaceAll("tlog", "hlog") + " order by tseq desc";

		String countsql = "select count(*) from ("+sql+") as temp";
		
		return queryForPage(countsql, sql, page, AppParam.getPageSize(),Hlog.class);
	}
	
	/** 
	 * 手机订单查询(下载)
	 * @param mid
	 * @param oid
	 * @param state
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 */
	public List<Hlog> queryPhonePaysList(String mid, String oid, String tstat, String dateBegin, String dateEnd,String operId) {
		StringBuffer selecSql = new StringBuffer();
		selecSql.append("select tseq,oid,amount,sys_date,sys_time,tstat,phone_no,trans_period,oper_id  " );
		selecSql.append("from tlog where type= 2 and mid = ").append(mid);
		if (!Ryt.empty(oid)) {
			selecSql.append(" and oid = ").append(Ryt.addQuotes(oid));
		}
		if (!Ryt.empty(tstat)) {
			selecSql.append(" and tstat = " + tstat );
		}
		if (!Ryt.empty(dateBegin)) {
			selecSql.append(" and sys_date >=" + dateBegin );
		} 
		if (!Ryt.empty(dateEnd)) {
			selecSql.append(" and sys_date <=" + dateEnd );
		}
		if (!Ryt.empty(operId)) {
			selecSql.append(" and oper_id = " + operId );
		}
		String sql = selecSql.toString() + " union " + selecSql.toString().replaceAll("tlog", "hlog") + " order by tseq desc";
		return query(sql, Hlog.class);
	}
	/**
	@@ -412,10 +421,12 @@
	 	 * @return
	 	 */
	 	public OrderInfo queryHlogByKeys(Integer gateRouteId,String bkSeq) {
	       String sql = "select * from hlog where gid=? and (bk_seq1 = ? or bk_seq2=?) order by tseq desc limit 1 " ;
 	       OrderInfo hlog=null;
 	       try {
	    	   hlog=  queryForObject(sql , new Object[]{gateRouteId,bkSeq,bkSeq},OrderInfo.class);
 		   } catch (Exception e) {
 //			e.printStackTrace();
 		   }
		  return hlog;
	}
	/**
	 * 商户原始日志
	 * @param mid oid bdate edate
	 * @return
	 */
	public CurrentPage<Hlog> queryMerHlog(Integer pageNo, String mid, String oid, Integer bdate, Integer edate,Integer mstate) {
		StringBuffer condition = new StringBuffer();
		condition.append(" from elog e,minfo m where e.id >0 and e.mid=m.id ");
		if (!Ryt.empty(mid)) condition.append(" and e.mid = '"+ mid+"'");
		// 系统日期
		if (bdate != null) condition.append(" and e.sys_date >= " + bdate);
		if (edate != null) condition.append(" and e.sys_date <= " + edate);
		// 订单
		if (!Ryt.empty(oid)) condition.append(" and e.oid = " + Ryt.addQuotes(oid));
		if(mstate!=null) condition.append(" and m.mstate >= " + mstate);
		String sqlCount = " SELECT  COUNT(*)  " + condition.toString();
		String sql = " select e.mdate,e.mid,e.oid,e.amount,e.type,e.ip,e.sys_date,e.sys_time,e.gate_id " + condition.toString()
						+ " order by sys_date desc,sys_time desc";
		return queryForPage(sqlCount, sql, pageNo, AppParam.getPageSize(),Hlog.class);
		
	}
	/**
	 * 手机支付订单查询
	 * @param 
	 * @return
	 */
	public CurrentPage<OrderInfo> queryPhonePay(Integer pageNo,Integer pageSize, String table, String mid,
								Integer tstat,Integer bdate,Integer edate, String operid) {
		StringBuffer condition = new StringBuffer();
		if (table.equals(Constant.TLOG)) {
			condition.append(" From tlog t,mlog m WHERE m.chk_no !='' AND t.tseq=m.tseq AND t.mid = ");
			condition.append(Ryt.addQuotes(mid));
		} else {
			condition.append(" From hlog t,mlog m WHERE m.chk_no !='' AND t.tseq=m.tseq AND t.mid = ");
			condition.append(Ryt.addQuotes(mid));
			if (bdate != null) condition.append(" and t.sys_date >=" + bdate);
			if (edate != null) condition.append(" and t.sys_date <=" + edate);
		}
		if (tstat != null) condition.append(" and t.tstat =" + tstat);
		if (operid != null && !Ryt.empty(operid)) condition.append(" and m.oper_id = " + operid);
		String sqlCount = " select count(*) " + condition.toString();
		StringBuffer selSql = new StringBuffer();
		selSql.append("select t.mid as mid,t.tseq as tseq,t.oid as oid ,t.amount as amount ,");
		selSql.append("t.sys_date as sys_date,t.sys_time as sys_time,t.tstat as tstat,");
		selSql.append("m.mobile_no as mobile_no,m.time_period as trans_period,m.oper_id as oper_id");
		String sql = selSql.toString() + condition.toString() + " order by t.tseq desc";
		String amtTotleSql = " select sum(amount) " + condition.toString();
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtTotleSql);
		return queryForPage(sqlCount, sql, pageNo,pageSize, OrderInfo.class,sumSQLMap);
	}
	/**
	 * 手机支付订单查询(Mlog)
	 * @param 
	 * @return
	 */
	public CurrentPage<OrderInfo> queryMlogList(Integer pageNo,Integer pageSize, String mid, Integer bdate, Integer edate, Integer tstat, String tseq,
			Integer cardType, String cardVal, long payAmount,Integer mstate) {
		  String sql=getCreditCardPaySql( mid,  bdate,  edate,  tstat,  tseq, cardType,  cardVal,  payAmount,mstate);
		String sqlCount = "select count(*) from  (" + sql + ") as c";
		String sqlCountTotle = "select sum(pay_amount) from (" + sql + ") as s";
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		return queryForPage(sqlCount,sql, pageNo,pageSize,OrderInfo.class,sumSQLMap) ;
	}
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo,Integer pageSize, String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gid, String bkseq,Integer mstate) {

		StringBuffer condition=getConditionSql( "tlog",mid,gate,tstat,type,oid,gid,null,null,null,tseq,bkseq,null,mstate);
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

	/**
	 * 根据条件查询交易明细
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo,Integer pageSize, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,Integer mstate) {
		StringBuffer condition=getConditionSql( "hlog",mid,gate,tstat,type,oid,gid,date,bdate,edate,tseq,null,bkCheck,mstate);
		// sqlCount总共多少�?sql查询要显示的数据
		String sqlCount = " SELECT  COUNT(a.tseq) " + condition.toString();
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.pay_amt,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,error_msg "
						+ condition.toString() + " order by sys_date desc,sys_time desc";
		// 交易总金额为[0],系统手续费总金额[1]
		String amtSumSql = " SELECT sum(a.amount)  " + condition.toString();
		String sysAtmFeeSumSql = " SELECT sum(a.fee_amt)" + condition.toString();
		Map<String,String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount,sql, pageNo,pageSize,OrderInfo.class,sumSQLMap);
	}
	/**
	 * 根据条件查询代付??�史交易明细
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail(Integer pageNo,
			Integer pageSize, String mid, Integer gate, Integer tstat,
			String oid, Integer gid, String date, Integer bdate, Integer edate,
			String tseq, Integer bkCheck, String bkseq,Integer mstate,Integer type,String batchNo) {
		StringBuffer condition = getpaymentSql("tlog", "hlog", mid, gate,
				tstat, oid, gid, date, bdate, edate, tseq, bkCheck, bkseq,mstate,type,batchNo);
		// sqlCount总共多少�?sql查询要显示的?���?
		String sqlCount = " SELECT  COUNT(a.tseq) from  ("
				+ condition.toString() + ") as a";
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,a.p8,a.p1,a.p2,a.p3,a.error_msg from ("
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
	/**
	 * 根据条件查询失败交易备份，同�?
	 * @return CurrentPage
	 */
	public CurrentPage<Hlog> queryBlogs(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer authtype, String date, Integer bdate, Integer edate,String tseq,Integer mstate) {
		StringBuffer condition=getConditionSql2( "blog",mid,gate,tstat,type,oid,authtype,date,bdate,edate,tseq,null,null,mstate);
		// sqlCount总共多少�?sql查询要显示的数据
		String sqlCount = " SELECT  COUNT(b.tseq) " + condition.toString();
		String sql = "SELECT b.tseq,b.mid,b.oid,b.mdate,b.amount,b.type,b.gate,b.gid,b.sys_date,b.sys_time,b.tstat,b.bk_chk,b.fee_amt,b.bank_fee,b.bk_seq1,b.bk_seq2,b.Phone_no,b.error_code,error_msg "
						+ condition.toString() + " order by tseq desc";
		// 交易总金额为[0],系统手续费总金额[1]
		String amtSumSql = " SELECT sum(b.amount)  " + condition.toString();
		String sysAtmFeeSumSql = " SELECT sum(b.fee_amt)" + condition.toString();
		Map<String,String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount,sql, pageNo,Hlog.class,sumSQLMap) ;
	}
	//当天交易查询的sql条件
	private StringBuffer getConditionSql(String queryTable,String mid, Integer gate, Integer tstat, Integer type,
			String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,String bkseq,Integer bkCheck,Integer mstate){
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
		return condition;
	}
//失败交易备份查询sql语句
	private StringBuffer getConditionSql2(String queryTable,String mid, Integer gate, Integer tstat, Integer type,
			String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,String bkseq,Integer bkCheck,Integer mstate){
		StringBuffer condition = new StringBuffer();
		condition.append(" FROM ").append(queryTable+" as b, minfo as m").append(" WHERE b.tseq > 0 and b.mid=m.id");
		if (!Ryt.empty(mid)) condition.append(" AND b.mid = "+ Ryt.addQuotes(mid));
		if (gate != null) condition.append(" AND b.gate = " + gate);
		if (tstat != null) condition.append(" AND b.tstat = " + tstat);
		if (type != null) condition.append(" AND b.type = " + type);
		if (!Ryt.empty(tseq)) condition.append(" AND b.tseq = " + Ryt.addQuotes(tseq));
		if (gid != null) condition.append(" AND b.gid = " + gid);
		if (!Ryt.empty(oid)) condition.append(" AND b.oid = " + Ryt.addQuotes(oid));
		if (date != null) {
			if (bdate != null) condition.append(" AND b." + date + " >= " + bdate);
			if (edate != null) condition.append(" AND b." + date + " <= " + edate);
		}
		if (!Ryt.empty(bkseq)) {
			condition.append(" AND (b.bk_seq1 = " + Ryt.addQuotes(bkseq));
			condition.append(" OR b.bk_seq2 = " + Ryt.addQuotes(bkseq));
			condition.append(" ) ");
		}
		if(bkCheck!=null){
			condition.append(" AND b.bk_chk= " + bkCheck);
		}
		if(mstate!=null)condition.append(" and m.mstate=").append(mstate);
		return condition;
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
				String gate3 = "";
				if(null == type){
				}else if (type == 11) {
					gate3 = "71";
				} else if (type == 12) {
					gate3 = "72";
				} else if (type == 16) {
					gate3 = "73";    
				}else if(type == 17){
					gate3 = "74";
				}
				paymentcondition.append(" AND t.gate like ").append(Ryt.addQuotes(
						"%" +gate3+gate2 + "%"));
			}
		if (tstat != null) paymentcondition.append(" AND t.tstat = " + tstat);
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
		if (tstat != null) paymentcondition1.append(" AND h.tstat = " + tstat);
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
	//数据分析查询（历史交易）
	public CurrentPage<OrderInfo> hlogAnalysis(int pageNo, String mid, int bdate, int edate, Integer tstat, int merTradeType,Integer mstate ){
		StringBuffer sqlBuff = new StringBuffer(" from hlog h,minfo m where h.mid=m.id");
		if (!mid.trim().equals("")) sqlBuff.append(" and h.mid=").append(Ryt.addQuotes(mid));
		if (bdate != 0) sqlBuff.append(" and h.sys_date>=").append(bdate);
		if (edate != 0) sqlBuff.append(" and h.sys_date<=").append(edate);
		if (tstat != null) sqlBuff.append(" and h.tstat=").append(tstat);
		if (merTradeType != 0) sqlBuff.append(" and m.mer_trade_type=").append(merTradeType);
		if(mstate !=null)sqlBuff.append(" and m.mstate=").append(mstate);
		String sql = "select h.tseq,h.mid,h.oid,h.amount,h.tstat,h.type,h.gate,h.gid,h.fee_amt,h.bank_fee,h.sys_date,h.sys_time,m.mer_trade_type"
						+ sqlBuff.toString();
		String sqlCount = "select count(*)" + sqlBuff.toString();
		String amtSumSql = "select sum(h.amount)" + sqlBuff.toString();
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		return queryForPage(sqlCount,sql, pageNo,OrderInfo.class,sumSQLMap) ;
	}
	//数据分析查询（退款）
	public CurrentPage<RefundLog> refundAnalysis(int pageNo, String mid, int bdate, int edate, Integer state,
			int merTradeType,Integer mstate) {
		StringBuffer sqlBuff = new StringBuffer(" from refund_log r ,minfo m where r.mid=m.id ");
		if (!mid.trim().equals("")) sqlBuff.append(" and r.mid=").append(Ryt.addQuotes(mid));
		if (bdate != 0) sqlBuff.append(" and r.pro_date>=").append(bdate);
		if (edate != 0) sqlBuff.append(" and r.pro_date<=").append(edate);
		if (state != null) {
			if (state == 1) sqlBuff.append(" and r.stat in(2,3)");// 成功
			if (state == 2) sqlBuff.append(" and r.stat in(4,6)");// 失败
		}
		if (merTradeType != 0) sqlBuff.append(" and m.mer_trade_type=").append(merTradeType);
		if( mstate !=null) sqlBuff.append(" and m.mstate=").append(mstate);
		String sql = "select r.*,m.mer_trade_type " + sqlBuff.toString();
		String sqlCount = "select count(*)" + sqlBuff.toString();
		String amtSumSql = "select sum(ref_amt)" + sqlBuff.toString();
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		return queryForPage(sqlCount,sql, pageNo,RefundLog.class,sumSQLMap);
	}
	//信用卡支付的sql语句
	private String getCreditCardPaySql(String mid, Integer bdate, Integer edate, Integer tstat, String tseq,
			Integer cardType, String cardVal, long payAmount,Integer mstate){
		cardVal=Ryt.sql(cardVal);
		StringBuffer sqlCondition = new StringBuffer();
		if (!Ryt.empty(mid)) sqlCondition.append(" and m.mid=").append(Ryt.addQuotes(mid));
		if (bdate != 0) sqlCondition.append(" and m.sys_date>=").append(bdate);
		if (edate != 0) sqlCondition.append(" and m.sys_date<=").append(edate);
		if (tstat != null) sqlCondition.append(" and m.tstat=").append(tstat);
		if (!Ryt.empty(tseq)) sqlCondition.append(" and m.tseq=").append(Ryt.addQuotes(String.valueOf(tseq)));
		if (cardVal != null && !cardVal.equals("") && cardType != 0) {// cardType 1为卡�?2身份证号 3手机�?
			if (cardType == 1) sqlCondition.append(" and m.pay_card=").append(Ryt.addQuotes(cardVal));
			if (cardType == 2) sqlCondition.append(" and m.Pay_id=").append(Ryt.addQuotes(cardVal));
			if (cardType == 3) sqlCondition.append(" and m.mobile_no=").append(Ryt.addQuotes(cardVal));
		}
		if(mstate!=null) sqlCondition.append(" and mo.mstate=").append(mstate);
		String groupSql="";
		if (payAmount!=0)groupSql=" and m.pay_card in (select pay_card from mlog m where pay_card!='' "+ sqlCondition.toString()
				+"  group by pay_card HAVING sum(m.pay_amount)>= "+payAmount+")";
		
		String sql = "select m.tseq,m.sys_date,m.mid,m.pay_amount,m.tstat,m.pay_card,m.pay_id,m.mobile_no from mlog m ,minfo mo where m.mid=mo.id "+groupSql+sqlCondition.toString();
		return sql; 
	}
	
	
	//TODO 
	public int updateGid(long tseq,int gid){
		
		//!,SQL
		String sql=("update hlog set gid="+gid+" where tseq="+Ryt.addQuotes(String.valueOf(tseq)));
//		update(sql);
		
		//2
//		String s2 = "update hlog set gid=? where tseq=?";
//		String[] args2 = {gid+"",tseq+""};
		 //update(s2, args2);
		
		
		
		//3
//		String s3 = "update hlog set gid=? where tseq=?";
//		String[] args3 = {gid+"",tseq+""};
//		
//		int[] t3 = {Types.INTEGER,Types.BIGINT};
//		update(s3, args3,t3);
		//
		
		
		return update(sql);
	}
	
	/***
	 * 代付数据直接保存到hlog
	 * @param ip
	 * @param mdate
	 * @param mid
	 * @param oid
	 * @param amount
	 * @param type
	 * @param sys_date
	 * @param init_sys_date
	 * @param sys_time
	 * @param gate
	 * @return
	 */
	public int insertHlog(String ip,String mdate,String mid,String oid,String amount,String type,int sys_date,int init_sys_date,int sys_time,int gate,String trans_flow,String toAccNo,String toAccName,String toBkNo,Integer gid) {
		StringBuffer sql=new StringBuffer();
		/*p1 收款人账*/ /*p2 收款人户*/ /* p3 收款人开户行*/
		sql.append(" insert into hlog (version,ip,tseq,mdate,mid,oid,amount,type,sys_date,init_sys_date,sys_time,gate,tstat,gid,pay_amt,is_liq,p8,p1,p2,p3,data_source)");
		sql.append(" values(10,").append(ip).append(",'").append(Ryt.genOidBySysTime()).append("',").append(mdate).append(",").append(Ryt.addQuotes(mid)).append(",").append(Ryt.addQuotes(oid)).append(",").append(amount).append(",").append(type).append(",");
		sql.append(sys_date).append(",").append(init_sys_date).append(",").append(DateUtil.getUTCTime(String.valueOf(sys_time))).append(",").append(gate).append(",").append(" 1").append(",").append(gid).append(",").append(amount).append(",").append(" 1");
		if(trans_flow != null && !Ryt.empty(trans_flow)){
			sql.append(",'"+trans_flow+"'");
		}else {
			sql.append(",''");
		}
		if(!Ryt.empty(toAccNo)){
			sql.append(",'"+toAccNo+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toAccName)){
			sql.append(",'"+toAccName+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toBkNo)){
			sql.append(",'"+toBkNo+"'");
		}else{
			sql.append(",''");
		}
		sql.append(",5)");
		return update(sql.toString());
	}
	
	/***
	 * 代付数据直接保存到hlog
	 * @param ip
	 * @param mdate
	 * @param mid
	 * @param oid
	 * @param amount
	 * @param type
	 * @param sys_date
	 * @param init_sys_date
	 * @param sys_time
	 * @param gate
	 * @return
	 */
	public int insertHlog(String ip,String mdate,String mid,String oid,String amount,String type,int sys_date,int init_sys_date,int sys_time,int gate,String trans_flow,String toAccNo,String toAccName,String toBkNo,String feeAmt,Integer gid,String payAmt) {
		StringBuffer sql=new StringBuffer();
		/*p1 收款人账*/ /*p2 收款人户*/ /* p3 收款人开户行*/
		sql.append(" insert into hlog (version,ip,tseq,mdate,mid,oid,amount,type,sys_date,init_sys_date,sys_time,gate,tstat,gid,pay_amt,is_liq,p8,p1,p2,p3,data_source,fee_amt)");
		sql.append(" values(10,").append(ip).append(",'").append(Ryt.genOidBySysTime()).append("',").append(mdate).append(",").append(Ryt.addQuotes(mid)).append(",").append(Ryt.addQuotes(oid)).append(",").append(amount).append(",").append(type).append(",");
		sql.append(sys_date).append(",").append(init_sys_date).append(",").append(DateUtil.getUTCTime(String.valueOf(sys_time))).append(",").append(gate).append(",").append("0").append(",").append(gid).append(",").append(payAmt).append(",").append(" 1");
		if(trans_flow != null && !Ryt.empty(trans_flow)){
			sql.append(",'"+trans_flow+"'");
		}else {
			sql.append(",''");
		}
		if(!Ryt.empty(toAccNo)){
			sql.append(",'"+toAccNo+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toAccName)){
			sql.append(",'"+toAccName+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toBkNo)){
			sql.append(",'"+toBkNo+"'");
		}else{
			sql.append(",''");
		}
		sql.append(",5,"+feeAmt+")");
		return update(sql.toString());
	}
	
	/***
	 * 代付数据保存到tlog
	 * @param ip
	 * @param mdate
	 * @param mid
	 * @param oid
	 * @param amount
	 * @param type
	 * @param sys_date
	 * @param init_sys_date
	 * @param sys_time
	 * @param gate
	 * @param reference
	 * @return
	 */
	public int insertTlog(String ip,String mdate,String mid,String oid,String amount,String type,int sys_date,int init_sys_date,int sys_time,Integer gate,String termid,String toAccNo,String toAccName,String toBkNo,String p9,String reference,String psam,Integer gid,Integer tstat,String msg) {
		StringBuffer sql=new StringBuffer();
		/*p1 收款人�??/ /*p2 收款?��???/ /* p3 收款人开户行*/
		sql.append(" insert into tlog (version,ip,mdate,mid,oid,amount,type,sys_date,init_sys_date,sys_time,gate,tstat,gid,pay_amt,is_liq,p8,p1,p2,p3,data_source,p9,p11,p12,error_msg)");
		sql.append(" values(10,").append(ip).append(",").append(mdate).append(",").append(Ryt.addQuotes(mid)).append(",").append(Ryt.addQuotes(oid)).append(",").append(amount).append(",").append(type).append(",");
		sql.append(sys_date).append(",").append(init_sys_date).append(",").append(DateUtil.getUTCTime(String.valueOf(sys_time))).append(",").append(gate).append(",").append(tstat).append(",").append(gid).append(",").append(amount).append(",1");
		if(termid != null && !Ryt.empty(termid)){
			sql.append(",'"+termid+"'");
		}else {
			sql.append(",''");
		}
		if(!Ryt.empty(toAccNo)){
			sql.append(",'"+toAccNo+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toAccName)){
			sql.append(",'"+toAccName+"'");
		}else{
			sql.append(",''");
		}
		if(!Ryt.empty(toBkNo)){
			sql.append(",'"+toBkNo+"'");
		}else{
			sql.append(",''");
		}
		sql.append(",").append("6").append(",").append(Ryt.addQuotes(p9));//追加数据来源  data_source   SK转账6
		sql.append(" , ").append(Ryt.addQuotes(reference)).append(",").append(Ryt.addQuotes(psam));
		sql.append(",").append(Ryt.addQuotes(msg));
		sql.append(")");
		return update(sql.toString());
	}
	
	
	public String getInsertHlogSql(String ip,String mdate,String mid,String oid,String amount,String type,int sys_date,int init_sys_date,int sys_time,int gate,String trans_flow,String accNo,
			String accName,String bkNo,int feeAmt,int gid,String trAmt) {
		StringBuffer sql=new StringBuffer();
		sql.append(" insert into hlog (version,ip,tseq,mdate,mid,oid,amount,type,sys_date,init_sys_date,sys_time,gate,tstat,gid,pay_amt,is_liq,p1,p2,p3,p8,data_source,fee_amt)");
		sql.append(" values(10,").append(ip).append(",'").append(Ryt.genOidBySysTime()).append("',").append(mdate).append(",").append(Ryt.addQuotes(mid)).append(",").append(Ryt.addQuotes(oid)).append(",").append(amount).append(",").append(type).append(",");
		sql.append(sys_date).append(",").append(init_sys_date).append(",").append(DateUtil.getUTCTime(String.valueOf(sys_time))).append(",").append(gate).append(",").append("0").append(",").append(gid).append(",").append(trAmt).append(",").append(" 1");
		sql.append(","+Ryt.addQuotes(accNo));
		sql.append(","+Ryt.addQuotes(accName));
		sql.append(","+Ryt.addQuotes(bkNo));
		if(trans_flow != null && !Ryt.empty(trans_flow)){
			sql.append(",'"+trans_flow+"'");
		}else {
			sql.append(",''");
		}
		sql.append(",5,"+feeAmt+")");
		return sql.toString();
	}
	
	
	/****
	 * 更新tlog状态
	 * @param mid
	 * @param oid
	 * @param stat
	 * @return
	 */
	public String updateTlogStat(String mid ,String oid,int stat,String tseq,int bk_date,String bk_time,String bkFeeAmt){
		StringBuffer sql=new StringBuffer();
		sql.append("update tlog set tstat=").append(stat).append(", bk_seq1=").append(Ryt.addQuotes(tseq)).append(",bk_recv=").append(DateUtil.getUTCTime(bk_time));
		sql.append(",bk_date=").append(bk_date);
		sql.append(" ,bk_flag=").append("1");
		sql.append(" ,bank_fee=").append(bkFeeAmt);
		sql.append(" where mid=").append(Ryt.addQuotes(mid)).append(" and ").append("oid=").append(Ryt.addQuotes(oid));
		sql.append("   and tstat != " + PayState.SUCCESS);
		return sql.toString();
	}
	/****
	 * 更新Hlog状�?
	 * @param mid
	 * @param oid
	 * @param stat
	 * @return
	 */
	public String updateHlogStat(String mid ,String oid,int stat,String tseq,int bk_date,String bk_time){
		StringBuffer sql=new StringBuffer();
		sql.append(" update hlog set tstat=").append(stat).append(", bk_seq1=").append(Ryt.addQuotes(tseq)).append(",bk_recv=").append(DateUtil.getUTCTime(bk_time));
		sql.append(",bk_date=").append(bk_date);
		sql.append(" ,bk_flag=").append("1");
		sql.append(" where mid=").append(Ryt.addQuotes(mid)).append(" and ").append("oid=").append(Ryt.addQuotes(oid));
		sql.append("   and tstat != " + PayState.SUCCESS);
		return sql.toString();
	}
	
	/***
	 * 更新订单状�?
	 * @param tableName
	 * @param tseq
	 * @param stat
	 * @param bkSeq
	 * @param bk_date
	 * @param bk_time
	 * @return
	 */
	public String updateOrderStat(String tableName, String tseq, int stat, String bkSeq, int bk_date, String bk_time,String bkFeeAmt){
		StringBuffer sql=new StringBuffer();
		sql.append("update " + tableName + " set tstat=").append(stat);
		if(!Ryt.empty(bkSeq))sql.append(", bk_seq1=").append(Ryt.addQuotes(bkSeq));
		if(!Ryt.empty(bk_time))sql.append(",bk_recv=").append(DateUtil.getUTCTime(bk_time));
		sql.append(" ,bk_date=").append(bk_date);
		sql.append(" ,bk_flag=1");
		sql.append(" ,bank_fee=").append(bkFeeAmt);
		sql.append(" where tseq=").append(Ryt.addQuotes(tseq));
		sql.append("   and tstat != " + PayState.SUCCESS);
		return sql.toString();
	}
	
	/****
	 * 更新tlog状�?
	 * @param mid
	 * @param oid
	 * @param stat
	 * @return
	 */
	public String updateTlogStat(String mid ,String oid,int stat,String errorMsg){
		StringBuffer sql=new StringBuffer();
		sql.append(" update tlog set tstat=").append(stat).append(",error_msg=").append(Ryt.addQuotes(errorMsg)).append(" ,bk_flag=1 ");
		sql.append("  where mid=").append(Ryt.addQuotes(mid)).append(" and ").append(" oid=").append(Ryt.addQuotes(oid));
		sql.append("    and tstat != " + PayState.SUCCESS);
		return sql.toString();
	}
	
	/****
	 * 更新订单状�?
	 * @param tableName
	 * @param mid
	 * @param oid
	 * @param stat
	 * @param errorMsg
	 * @return
	 */
	public String updateOrderStat(String tableName, String tseq,int stat,String errorMsg){
		StringBuffer sql=new StringBuffer();
		sql.append(" update " + tableName + " set tstat=").append(stat).append(",error_msg=").append(Ryt.addQuotes(errorMsg)).append(" ,bk_flag=1 ");
		sql.append(" where tseq=").append(Ryt.addQuotes(tseq));
		sql.append("   and tstat != " + PayState.SUCCESS);
		return sql.toString();
	}
	
	/***
	 * 
	 * @param oid
	 * @return tseq,oid
	 */
	public String queryTseq(String oid){
		StringBuffer sql=new StringBuffer();
		sql.append("select concat(tl.tseq,',',tr.oid)  from tlog tl,tr_orders tr where tl.oid=tr.org_oid and tl.oid=").append(Ryt.addQuotes(oid));
		LogUtil.printInfoLog("TransactionDao", "getTseq", "Sql:"+sql.toString());
		String result=new String(this.getJdbcTemplate().queryForObject(sql.toString(),String.class));
		return result;
	}
	/****
	 * 修改单笔对私代付同步录入订到hlog 
	 * @return
	 */
	public int updateHlog(String oid,String aid, String payAmt,long transAmt,int transFee,String toAccNo,String toAccName,String toBkNo){
		StringBuffer sql=new StringBuffer();
		sql.append("update hlog set amount=").append(transAmt).append(",pay_amt=").append(payAmt).append(",fee_amt=").append(transFee).append(",is_liq=").append("1");
		if(!Ryt.empty(toAccNo))sql.append(",p1=").append(Ryt.addQuotes(toAccNo));
		if(!Ryt.empty(toAccName))sql.append(",p2=").append(Ryt.addQuotes(toAccName));
		if(!Ryt.empty(toBkNo))sql.append(",p3=").append(Ryt.addQuotes(toBkNo));
		sql.append(" where oid=").append(Ryt.addQuotes(oid));
		return update(sql.toString());
	}
	
	/****
	 * 修改单笔对私代付同步录入订到hlog(修改代付网关后新增方法) 
	 * @return
	 */
	public int updateHlog(String oid,String aid, String payAmt,long transAmt,int transFee,String toAccNo,String toAccName,String toBkNo,String gate,Integer gid){
		StringBuffer sql=new StringBuffer();
		sql.append("update hlog set amount=").append(transAmt).append(",pay_amt=").append(payAmt).append(",fee_amt=").append(transFee).append(",is_liq=").append("1");
		sql.append(",gate=").append(gate);
		sql.append(",gid=").append(gid);
		if(!Ryt.empty(toAccNo))sql.append(",p1=").append(Ryt.addQuotes(toAccNo));
		if(!Ryt.empty(toAccName))sql.append(",p2=").append(Ryt.addQuotes(toAccName));
		if(!Ryt.empty(toBkNo))sql.append(",p3=").append(Ryt.addQuotes(toBkNo));
		sql.append(" where oid=").append(Ryt.addQuotes(oid));
		return update(sql.toString());
	}
	
	
	/***
	 * 
	 * @param oid
	 * @return
	 */
	public String queryHlog_Tseq(String oid){
		String result=null;
		StringBuffer sql=new StringBuffer();
		sql.append("select tseq from hlog where oid=").append(Ryt.addQuotes(oid));
		result=queryForString(sql.toString());
		return result;
	}
	
	/***
	 * 根据商户订单号查询电银流水号
	 * @param mid
	 * @param oid
	 * @return [ 订单表名�?电银流水�?]
	 */
	public String[] queryOrderByOid(String mid, String oid) {
		String tseq = null;
		String table = Constant.HLOG;
		try {
			tseq = getJdbcTemplate().queryForObject("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid), String.class);
			if(Ryt.empty(tseq)){
				table = Constant.TLOG;
				tseq = getJdbcTemplate().queryForObject("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid), String.class);
			}
		} catch (Exception e) {
			table = Constant.TLOG;
			tseq = getJdbcTemplate().queryForObject("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid), String.class);
		}
		return new String[]{table, tseq};
	}
	
	/***
	 * �??��商户订单号查询电银流水号
	 * @param mid
	 * @param oid
	 * @return [ 订单表名�?电银流水�?]
	 */
	public String[] queryOrderByOid(String mid, String oid, String orgOid) {
		String tseq = null;
		String table = Constant.HLOG;
		String sql = null;
		try {
			sql = "select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and (oid=" + Ryt.addQuotes(oid) + " or oid="+ Ryt.addQuotes(orgOid) +")";
			tseq = queryForStringThrowException(sql);
			
			if(Ryt.empty(tseq)){
				table = Constant.TLOG;
				sql = "select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and (oid=" + Ryt.addQuotes(oid) + " or oid="+ Ryt.addQuotes(orgOid) +")";
				tseq = queryForStringThrowException(sql);
			}
			
		} catch (Exception e) {
			LogUtil.printInfoLog("TransactionDao", "queryOrderByOid", sql);
			table = Constant.TLOG;
			sql = "select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and (oid=" + Ryt.addQuotes(oid) + " or oid="+ Ryt.addQuotes(orgOid) +")";
			tseq = queryForStringThrowException(sql);
		}
		return new String[]{table, tseq};
	}
	
	/**
	 * 根据条件查询线下充值交易明�?
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail_cz(Integer pageNo,
			Integer pageSize,String mid,Integer tstat,
			String date, Integer bdate, Integer edate,String tseq,String oid,Integer mstate,int pstate) {
		StringBuffer condition = getpaymentSql_cz("tlog", "hlog", mid,tstat,date,bdate,edate,tseq,oid,mstate,pstate);
		// sqlCount总共多少???sql查询要显示的�??��
		String sqlCount = " SELECT  COUNT(a.tseq) from  ("
				+ condition.toString() + ") as a left join tr_orders as tr on a.oid=tr.oid where 1=1 ";
		String sql = "SELECT a.tseq,a.mid,a.oid,a.mdate,a.amount,a.type,a.gate,a.gid,a.sys_date,a.sys_time,a.tstat,a.bk_chk,a.fee_amt,a.bank_fee,a.bk_seq1,a.bk_seq2,a.Phone_no,a.error_code,a.p8,a.p1,a.p2,tr.audit_remark,tr.pstate,tr.orgn_remark,tr.cert_remark,tr.recharge_amt,tr.trans_proof ,a.error_msg from ("
				+ condition.toString()
				+ ") as a left join tr_orders as tr on a.oid=tr.oid where 1=1 ";
		if(pstate >0){
			sqlCount+=" and tr.pstate="+pstate;
			sql+=" and tr.pstate="+pstate;
		}
		
		sql += " order by a.sys_date desc,a.sys_time desc";
		
		// 交易总金额为[0],系统手续费总金额[1]
		String amtSumSql = " SELECT sum(a.amount) from ("
				+ condition.toString() + ") as a";
		String sysAtmFeeSumSql = " SELECT sum(a.fee_amt) from ("
				+ condition.toString() + ") as a";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount, sql, pageNo, pageSize, OrderInfo.class,
				sumSQLMap);
	}
	
	//代付线下充值交易查询的条件
		private StringBuffer getpaymentSql_cz(String queryTable,String queryTable2,String mid,Integer tstat,
				String date, Integer bdate, Integer edate,String tseq,String oid,Integer mstate,Integer pstate){
			StringBuffer paymentcondition = new StringBuffer();
			paymentcondition.append(" select * FROM ").append(queryTable+" as t,minfo m").append(" WHERE t.type=14 and t.mid=m.id ");
			if (!Ryt.empty(mid)) paymentcondition.append(" AND t.mid = "+ Ryt.addQuotes(mid));
			if (tstat != null) paymentcondition.append(" AND t.tstat = " + tstat);
			if (!Ryt.empty(tseq)) paymentcondition.append(" AND t.tseq = " + Ryt.addQuotes(String.valueOf(tseq)));
			if (!Ryt.empty(oid)) paymentcondition.append(" AND t.oid = " + Ryt.addQuotes(oid));
			if (date != null ) {
				if (bdate != null) paymentcondition.append(" AND t." + date + " >= " + bdate);
				if (edate != null) paymentcondition.append(" AND t." + date + " <= " + edate);
			}
			paymentcondition.append(" AND t.mid=m.id");
			if (mstate != null) paymentcondition.append(" AND m.mstate = " + mstate);
			StringBuffer paymentcondition1 = new StringBuffer();
			paymentcondition1 .append("select * FROM ").append(queryTable2+" as h,minfo m1").append(" WHERE h.type=14 and  h.mid=m1.id ");
			if (!Ryt.empty(mid)) paymentcondition1.append(" AND h.mid = "+ Ryt.addQuotes(mid));
			if (tstat != null) paymentcondition1.append(" AND h.tstat = " + tstat);
			if (!Ryt.empty(tseq)) paymentcondition1.append(" AND h.tseq = " + Ryt.addQuotes(String.valueOf(tseq)));
			if (!Ryt.empty(oid)) paymentcondition1.append(" AND h.oid = " + Ryt.addQuotes(oid));
			if (date != null) {
				if (bdate != null) paymentcondition1.append(" AND h." + date + " >= " + bdate);
				if (edate != null) paymentcondition1.append(" AND h." + date + " <= " + edate);
			}
			if (mstate != null) paymentcondition1.append(" AND m1.mstate = " + mstate);
			paymentcondition1.append(" AND h.mid=m1.id");
			paymentcondition.append(" union all ").append(paymentcondition1);
		
			return paymentcondition;
		}
}