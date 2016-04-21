package com.rongyifu.mms.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.cookie.DateUtils;

import cn.com.infosec.util.Base64;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.Account;
import com.rongyifu.mms.bean.AdjustAccount;
import com.rongyifu.mms.bean.DailySheet;
import com.rongyifu.mms.bean.ErrorAnalysis;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.FeeLiqLog;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.InCome;
import com.rongyifu.mms.bean.LostOrder;
import com.rongyifu.mms.bean.MerAccount;
import com.rongyifu.mms.bean.MergeDetail;
import com.rongyifu.mms.bean.MidFtp;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.RyfFtp;
import com.rongyifu.mms.bean.SettleDetail;
import com.rongyifu.mms.bean.SettleMinfo;
import com.rongyifu.mms.bean.Tlog;
import com.rongyifu.mms.bean.TradeStatistics;
import com.rongyifu.mms.bean.TransferMoney;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.RecPay;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

@SuppressWarnings("unchecked")
public class SettlementDao extends PubDao {
	
	public List<SettleMinfo> queryLiqFailList(Integer category, Integer bToDate, Integer eToDate, String mid, Integer bLiqDate, Integer eLiqDate,Integer reason){
		StringBuilder sqlFetchRows = new StringBuilder("SELECT id,mid,name,to_date expDate,liq_date,liq_time,reason,category FROM liq_fail_record WHERE 1=1");
		if(StringUtils.isNotBlank(mid)){
			sqlFetchRows.append(" AND mid = ").append(Ryt.addQuotes(mid));
		}
		if(category != null){
			sqlFetchRows.append(" AND category = ").append(category);
		}
		if(bToDate != null){
			sqlFetchRows.append(" AND to_date >= ").append(bToDate);
		}
		if(eToDate != null){
			sqlFetchRows.append(" AND to_date <= ").append(eToDate);
		}
		if(category != null){
			sqlFetchRows.append(" AND category = ").append(category);
		}
		if(bLiqDate != null){
			sqlFetchRows.append(" AND liq_date >= ").append(bLiqDate);
		}
		if(eLiqDate != null){
			sqlFetchRows.append(" AND liq_date <= ").append(eLiqDate);
		}
		if(reason != null){
			sqlFetchRows.append(" AND reason = ").append(reason);
		}
		sqlFetchRows.append(" ORDER BY reason ASC");
		return query(sqlFetchRows.toString(), SettleMinfo.class);
	}
	
	public CurrentPage<SettleMinfo> queryLiqFailRecord(Integer category, Integer bToDate, Integer eToDate, String mid, Integer bLiqDate, Integer eLiqDate, Integer reason,Integer pageNo){
		StringBuilder sqlFetchRows = new StringBuilder("SELECT id,mid,name,to_date expDate,liq_date,liq_time,reason,category FROM liq_fail_record WHERE 1=1");
		StringBuilder sqlCountRows = new StringBuilder("SELECT COUNT(*) FROM liq_fail_record WHERE 1=1");
		StringBuilder successCountSql = new StringBuilder("SELECT COUNT(*) FROM fee_liq_bath f, minfo m WHERE f.mid = m.id");
		StringBuilder condition = new StringBuilder();
		if(StringUtils.isNotBlank(mid)){
			condition.append(" AND mid = ").append(Ryt.addQuotes(mid));
			successCountSql.append(" AND f.mid= ").append(Ryt.addQuotes(mid));
		}
		if(category != null){
			condition.append(" AND category = ").append(category);
			successCountSql.append(" AND m.category = ").append(category);
		}
		if(bToDate != null){
			condition.append(" AND to_date >= ").append(bToDate);
			successCountSql.append(" AND f.Liq_date >= ").append(bToDate);
		}
		if(eToDate != null){
			condition.append(" AND to_date <= ").append(eToDate);
			successCountSql.append(" AND f.Liq_date <= ").append(eToDate);
		}
		if(category != null){
			condition.append(" AND category = ").append(category);
		}
		if(bLiqDate != null){
			condition.append(" AND liq_date >= ").append(bLiqDate);
			successCountSql.append(" AND f.gen_date >= ").append(bLiqDate);
		}
		if(eLiqDate != null){
			condition.append(" AND liq_date <= ").append(eLiqDate);
			successCountSql.append(" AND f.gen_date <= ").append(eLiqDate);
		}
		if(reason != null){
			condition.append(" AND reason = ").append(reason);
//			successCountSql.append(" AND f.gen_date <= ").append(eLiqDate);
		}
		sqlCountRows.append(condition);
		sqlFetchRows.append(condition).append(" ORDER BY reason ASC");
		CurrentPage<SettleMinfo> rslt = queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), pageNo, SettleMinfo.class);
		int successCount = queryForInt(successCountSql.toString());
		rslt.setSumResult("successCount", successCount);
		return rslt;
	}
	
	public int addFailRecord(SettleMinfo sm,Integer toDate){
		String sql =  "INSERT INTO liq_fail_record (mid,category,name,to_date,liq_date,liq_time,reason) VALUES(?,?,?,?,?,?,?)";
		String liqDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		Object[] args = new Object[]{sm.getMid(),sm.getCategory(),sm.getName(),toDate,liqDate,DateUtil.getCurrentUTCSeconds(),sm.getReason()};
		int[] argTypes = new int[]{Types.VARCHAR,Types.TINYINT,Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.TINYINT};
		return update(sql, args, argTypes);
	}
	
	/**
	 * 按商户号和结算截至日期查询商户结算信息 单个查询 不考虑该商户是否在结算周期内
	 * @param mid 商户号
	 * @param toDate 截至日期
	 * @return 
	 */
	public SettleMinfo getLiqMinfoByMid(String mid,int toDate){
		StringBuffer selsql = new StringBuffer();
		selsql.append("select id as mid,abbrev as name,category, ifnull(liq_type,0) as liqType,ifnull(liq_period,0) as liqPeriod,");
		selsql.append("ifnull(liq_limit,0) as liqLimit,last_batch as lastBatch, ifnull(last_liq_date,0) as lastLiqDate,mstate as mstate,");
		selsql.append("ifnull(exp_date,0) as expDate from minfo where 1=1");
		selsql.append( " AND id =").append(Ryt.addQuotes(mid)).append(" AND mstate = 0 and liq_state=0 and exp_date >= ").append(toDate);
		selsql.append(" AND last_liq_date<").append(toDate);
		return queryForObject(selsql.toString(), SettleMinfo.class);
	}
	
	/**
	 * 批量查询 只查询结算周期内的商户
	 */
	public List<SettleMinfo> getLiqMinfos(int toDate,List<Integer> categories){
		String dateStr = String.valueOf(toDate);
		int week = DateUtil.dayCount(dateStr);
		int day = Integer.parseInt(dateStr.substring(6, 8));
		StringBuffer selsql = new StringBuffer();
		selsql.append("select id as mid,abbrev as name,category, ifnull(liq_type,0) as liqType,ifnull(liq_period,0) as liqPeriod,");
		selsql.append("ifnull(liq_limit,0) as liqLimit,last_batch as lastBatch, ifnull(last_liq_date,0) as lastLiqDate,mstate as mstate,");
		selsql.append("ifnull(exp_date,0) as expDate from minfo ");
		selsql.append( " where mstate = 0").append(" and liq_state=0").append(getCategoryCondition(categories));
		selsql.append(" AND last_liq_date<").append(toDate).append(" and exp_date >= ").append(toDate);
		if (day == 1 || (week == 1 && (day == 2 || day == 3))) {
			// doSql = insql;
		} else {
			if (week == 1) {// 每周一次 在周一处理
				selsql.append(" AND ( liq_period = 1  OR liq_period = 2 )");
			} else if (week == 2 || week == 4) {//每周两次 周二和周四处理
				selsql.append( " AND ( liq_period = 1  OR liq_period = 3 )");
			} else {//每天清算一次
				selsql.append(" AND liq_period = 1");
			}
		}
		LogUtil.printInfoLog("SettlementDao", "getLiqMinfos", selsql.toString());
		return query(selsql.toString(), SettleMinfo.class);
	}
	
	private String getCategoryCondition(List<Integer> categories){
		StringBuilder con = new StringBuilder(" AND category IN (");
		for (Integer cat : categories) {
			con.append(cat+",");
		}
		return con.subSequence(0, con.lastIndexOf(","))+")";
	}
	
	/**
	 * 分页显示某已类型的商户结算信息
	 * @param type
	 * @param category
	 * @param toDate
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<SettleMinfo> showLiqDetails(String mid,Integer type,Integer category, Integer toDate, Integer pageNo){
		StringBuilder sqlFetchRows = new StringBuilder("SELECT id mid, abbrev name,last_liq_date FROM minfo WHERE  mstate = 0 AND liq_state=0 AND exp_date >= ").append(toDate);
		sqlFetchRows.append(" AND category=").append(category).append(" AND last_liq_date<").append(toDate);
		StringBuilder sqlCountRows = new StringBuilder("SELECT count(*) FROM minfo WHERE  mstate = 0 AND liq_state=0 AND exp_date >= ").append(toDate);
		sqlCountRows.append(" AND category=").append(category).append(" AND last_liq_date<").append(toDate);
		if(StringUtils.isNotBlank(mid)){
			sqlFetchRows.append(" AND id = ").append(Ryt.addQuotes(mid));
			sqlCountRows.append(" AND id = ").append(Ryt.addQuotes(mid));
		}
		String condition = getLiqPeriodCondition(toDate,type);
		sqlFetchRows.append(condition).append(" ORDER BY id ASC");
		sqlCountRows.append(condition);
		return queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), pageNo, SettleMinfo.class);
	}
	
	/**
	 * 获取按商户类型汇总的结算信息
	 * @param toDate
	 * @return
	 */
	public Map<Integer,Map<String,Integer>> showLiqInfoByType(int toDate){
		Map<Integer,Map<String,Integer>> map = new HashMap<Integer,Map<String,Integer>>();
		int[] types = new int[]{0,1,2,3};//0–RYF商户 1–VAS商户 2–POS商户 3-POS代理商
		StringBuilder inSql;
		StringBuilder outSql;
		for (int i = 0; i < types.length; i++) {
			//总商户数
			outSql = new StringBuilder("SELECT COUNT(*) FROM minfo WHERE mstate = 0 AND liq_state=0 AND exp_date >= ").append(toDate);
			outSql.append(" AND last_liq_date<").append(toDate);
			outSql.append(" AND category=").append(types[i]);
			String outCondition = getLiqPeriodCondition(toDate,1);
			outSql.append(outCondition);
			//结算周期内的商户数
			inSql = new StringBuilder("SELECT COUNT(*) FROM minfo WHERE mstate = 0 AND liq_state=0 AND exp_date >= ").append(toDate);
			inSql.append(" AND category=").append(types[i]).append(" AND last_liq_date<").append(toDate);
			String inCondition = getLiqPeriodCondition(toDate,0);
			inSql.append(inCondition);
			int inCount = queryForInt(inSql.toString());
			int outCount = queryForInt(outSql.toString());
			Map<String,Integer> map1 = new HashMap<String,Integer>();
			map1.put("inCount", inCount);
			map1.put("outCount", outCount);
			map.put(types[i], map1);
		}
		return map;
	}
	
	/**
	 * @param toDate
	 * @param type 0 查周期内 1 查周期外
	 * @return 
	 */
	private String getLiqPeriodCondition(int toDate,int type){
		String condition = "";
		String dateStr = String.valueOf(toDate);
		int week = DateUtil.dayCount(dateStr);//星期几
		int day = Integer.parseInt(dateStr.substring(6, 8));//月份中的第几天
		if (day == 1 || (week == 1 && (day == 2 || day == 3))) {
			if(type==0){//周期内
				//每月的第一天 或者 周一且(每月的第2或者第三天) 所有用户都在周期内
			}else{//周期外
				condition = " and 0=1";
			}
		} else {//其他时间
			if (week == 1) {// 每周一次 在周一处理
				condition = type==0?" and ( liq_period = 1  or liq_period = 2 )":" and !( liq_period = 1  or liq_period = 2 )";
			} else if (week == 2 || week == 4) {// 每周两次 周二和周四处理
				condition =  type==0?" and ( liq_period = 1  or liq_period = 3 )":" and !( liq_period = 1  or liq_period = 3 )";
			} else {//每天清算一次
				condition = type==0?" and liq_period = 1":" and liq_period != 1";
			}
		}
		return condition;
	}
	
	/**
	 * 结算确认的详细信息
	 * @param batch 结算批次号
	 * @return 返回一个list 所选中的记录以及该条记录信息的详细
	 */
	public List<SettleDetail> getSettleDetail(String batch) {
		batch = Ryt.sql(batch);
		String paySql = "select tseq,gate,amount,0 as ref_amt,amount as tradeAmt,fee_amt ,0 as mer_fee from hlog where type!=4 and bk_chk=1 and batch='"
				+ batch+"'";
		String refSql = "select id as tseq,gate,0 as amount,ref_amt,(-1)*ref_amt as tradeAmt,0 as fee_amt,mer_fee from refund_log where batch='"
				+ batch+"'";
		List<SettleDetail> detailList = query(paySql,SettleDetail.class);
		detailList.addAll(query(refSql,SettleDetail.class));
		return detailList;
	}
	//查询退款交易以及成功交易
	public List<Map<String,Object>> queryHlogList(String batch, String gate) {
		StringBuilder paySql = new StringBuilder("select mid,mdate,oid,amount,type,fee_amt, sys_date,tseq, gate from  hlog ");
		paySql.append("where tstat=2 and type!=4 and gate =  ").append(Ryt.sql(gate));
		paySql.append(" and batch = '").append(Ryt.sql(batch)).append("'");
		
		StringBuilder refSql = new StringBuilder("select mid,mdate,oid,ref_amt as amount,4 as type,0 as fee_amt ,");
		refSql.append("mdate as sys_date,id as tseq, gate from refund_log where batch='").append(Ryt.sql(batch));
		refSql.append("' and gate=").append(Ryt.sql(gate));
		List<Map<String,Object>> allList= queryForList(paySql.toString());
		allList.addAll( queryForList(refSql.toString()));
		return allList;
	}
	/**
	 * 管理后台商户结算单查询下载明细 (20140603改造后的版本)
	 * @param batch
	 * @return
	 */
	public List<Map<String,Object>> queryHlogList(String batch) {
			StringBuilder paySql = new StringBuilder("select mid,mdate,oid,amount,type,fee_amt, sys_date,tseq, gate from  hlog ");
			paySql.append("where tstat=2 and type!=4 ");
			paySql.append(" and batch = '").append(Ryt.sql(batch)).append("'");
			
			StringBuilder refSql = new StringBuilder("select mid,mdate,oid,ref_amt as amount,4 as type,0 as fee_amt ,");
			refSql.append("mdate as sys_date,id as tseq, gate from refund_log where batch=").append(Ryt.addQuotes(Ryt.sql(batch)));
			List<Map<String,Object>> allList= queryForList(paySql.toString());
			allList.addAll( queryForList(refSql.toString()));
			return allList;
	}

	public List<Hlog> queryHlog(String batch, String gate) {
		
		StringBuilder paySql = new StringBuilder("select mid,mdate,oid,amount,type,fee_amt, sys_date,tseq, gate from  hlog ");
		paySql.append("where tstat=2 and type!=4 and gate =  ").append(Ryt.sql(gate));
		paySql.append(" and batch = '").append(Ryt.sql(batch)).append("'");
		
		StringBuilder refSql = new StringBuilder("select mid,mdate,oid,ref_amt as amount,4 as type,0 as fee_amt ,");
		refSql.append("mdate as sys_date,id as tseq, gate from refund_log where batch='").append(Ryt.sql(batch));
		refSql.append("' and gate=").append(Ryt.sql(gate));
		
		List<Hlog> allList = query(paySql.toString(), Hlog.class);
		allList.addAll(query(refSql.toString(), Hlog.class) );
		return allList;
	}
		
	/**
	 * 管理后台商户结算单查询查看明细 (20140603改造后的版本)
	 * @param batch 批次号
	 * @return
	 */
		public List<Hlog> queryHlog(String batch) {
		
		StringBuilder paySql = new StringBuilder("select mid,mdate,oid,amount,type,fee_amt, sys_date,tseq, gate from  hlog ");
		paySql.append("where tstat=2 and type!=4 ");
		paySql.append(" and batch = '").append(Ryt.sql(batch)).append("'");
		
		StringBuilder refSql = new StringBuilder("select mid,mdate,oid,ref_amt as amount,4 as type,0 as fee_amt ,");
		refSql.append("mdate as sys_date,id as tseq, gate from refund_log where batch='").append(Ryt.sql(batch));
		refSql.append("'");
		
		List<Hlog> allList = query(paySql.toString(), Hlog.class);
		allList.addAll(query(refSql.toString(), Hlog.class) );
		return allList;
	}

	public List<DailySheet> getAllDailySheets(String mid, int beginDate,int endDate) {
		StringBuilder sql = new StringBuilder("select m.abbrev,m.liq_type,d.* ");
		sql.append("from daily_collect d,minfo m ");
		sql.append(" where d.mid=m.id and d.liq_date>=").append(beginDate);
		sql.append(" and d.liq_date<=").append(endDate);
		if (mid != null && !mid.equals(""))
			sql.append( " and d.mid=").append(Ryt.sql(mid));
		return query(sql.toString(), DailySheet.class );
	}


	public List<FeeLiqLog> queryLiqFeeLog(String batch) {
	    String sql = "select * from  fee_liq_log  where batch  = '"
						+ Ryt.sql(batch) + "' and pur_amt+ref_amt !=0";
		return query(sql, FeeLiqLog.class );
	}
	
	public List queryLiqFeeLogList(String batch) {
		String sql = "select * from  fee_liq_log  where batch  = '"
				+ Ryt.sql(batch) + "' and pur_amt+ref_amt !=0";
		return  queryForList(sql);
	}

	/**
	 * 目前只用到author_type，trans_mode 商户手续费计费公式，银行手续费计费公式
	 * 
	 * @param gate
	 * @param mid
	 * @return LostOrder
	 */
	public LostOrder queryLostOrderById( String mid, String gate, int gid) {

		LostOrder bean = new LostOrder();
		bean.setMid(mid);
		bean.setGid(gid);
//		String feeModel="";
//		try {
//			 feeModel=queryForString("select fee_model from gates where ryt_gate = "
//							+ Ryt.sql(gate) + " and gid = "
//							+ gid + " limit 1 ");
//		} catch (Exception e) {
//			 feeModel=queryForString("select fee_model from gates where gid = "
//							+ gid + " limit 1 ");
//		}
//		bean.setBkFeeMode(feeModel);
		try {
			String calcMode=queryForString("select calc_mode from fee_calc_mode where mid = '"+ mid+"'"
							+ " and gate =" + Ryt.sql(gate));
			bean.setCalcMode(calcMode);
		} catch (Exception e) {
			bean.setCalcMode("AMT*0");
		}
		//bean.setLiqType(queryForInt("select liq_type from minfo where id = "+ mid));
		return bean;
	}

	public Hlog queryLostOrderByTseq(String tseq) {
		String sql = " select t.mid,t.tseq,t.oid,t.sys_date,t.sys_time,t.amount,t.gate,t.gid  from tlog t where t.tseq = "
				+ Ryt.addQuotes(tseq);
		Hlog hlog =queryForObject(sql, Hlog.class);
		if (hlog == null) {
			hlog = queryForObject(sql.replaceAll("tlog", "hlog"), Hlog.class);
		}
		return hlog;
	}


	public void adjustAccount(String mid, int type, int loginuid, int date,
			int time, long account, String reason) {
		String insertSql = "insert into adjust_account (mid,submit_operid,type,submit_date,submit_time,account,state,reason) values (?,?,?,?,?,?,?,?)";
		Object[] obj = new Object[] { mid, loginuid, type, date, time, account,
				0, reason };
		int[] args = { Types.VARCHAR, Types.INTEGER, Types.TINYINT,
				Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.BIGINT,
				Types.VARCHAR };
		 update(insertSql, obj, args);
	}

	public AdjustAccount getAdjustAccountById(long id) {
		String sql = "select a.*,m.abbrev from adjust_account a,minfo m where a.mid=m.id and a.id = "
				+ id;
		return queryForObject(sql,AdjustAccount.class);
	}

	public List searchIncomeDownload(String mid, String gate, String type,
			String beginDate, String endDate) {
		StringBuffer strSql = new StringBuffer();
		strSql.append("select * from ( select f.liq_date,h.mid,h.type,sum(h.amount) as amount,sum(f.ref_amt) as ref_amt, ");
		strSql.append("sum(h.fee_amt-h.bank_fee-f.ref_fee+f.bk_ref_fee) as income,sum(h.fee_amt) as fee_amt,sum(h.bank_fee) as bank_fee,");
		strSql.append("sum(f.ref_fee) as ref_fee,sum(f.bk_ref_fee) as bk_ref_fee from hlog h, fee_liq_bath f where f.liq_date>=");
		strSql.append(Ryt.sql(beginDate)).append(" and f.liq_date<=").append(Ryt.sql(endDate));
		strSql.append(" and h.type not in(0,4) and f.batch=h.batch");
		if (!type.equals("-1") && !type.equals("")) {
			strSql.append(" and h.type=").append(Ryt.sql(type));
		}
		if (!gate.equals("-1") && !gate.equals("")) {
			strSql.append(" and h.gate in (").append(Ryt.sql(gate)).append(")");
		}
		if (!mid.equals("")) {
			strSql.append(" and h.mid=").append(Ryt.sql(mid));
		}
		strSql.append(" group by f.liq_date,h.mid,h.type ) as incomeTab");
		return  queryForList(strSql.toString());
	}

	/**
	 * 根据mid查询余额）
	 * @param mid
	 * @return
	 */
	public String getBalanceById(String mid) {

		try {
			return Ryt.div100(queryBalance(mid));
		} catch (Exception e) {
			return "0.00";
		}
	}
	/**
	 * 根据mid查询余额（没有除100的）
	 * @param mid
	 * @return
	 */
	public long get100BalanceById(String mid) {
		
		return queryBalance(String.valueOf(mid));

//		try {
//			return queryForInt("select balance from minfo where id = "+ mid);
//		} catch (Exception e) {
//			return 0;
//		}
	}

	public Map getMap(String mid, int intExpDate) throws Exception {
		Minfo minfo = getMinfoById(mid);
		int lastLiqDate = minfo.getLastLiqDate();
		String lastLiqDateBefore="";
		String intExpDateBefore="";
		if(lastLiqDate==0){
			lastLiqDateBefore="0";
		}else{
		lastLiqDateBefore=Ryt.getSpecifiedDayBefore(String.valueOf(lastLiqDate))+"82800";
		}
		intExpDateBefore=Ryt.getSpecifiedDayBefore(String.valueOf(intExpDate))+"82800";
		StringBuffer sql=new StringBuffer();
		sql.append("select min(bk_chk) as minBK, count(bk_chk) as allBK from hlog where tstat=2 and is_liq =0");
		sql.append(" AND  batch=0 and mid=").append(Ryt.addQuotes(mid));
		sql.append("  and ((bk_date * 100000 + bk_recv >=").append(lastLiqDateBefore);
		sql.append("  and bk_date * 100000 + bk_recv <").append(intExpDateBefore);
		sql.append(" and gid IN (55000,55001,90016))or (sys_date >= ").append(lastLiqDate);
		sql.append(" and sys_date < ").append(intExpDate).append(" and gid <> 55000 and gid<>55001 and gid<>90016))");
//		String sql="select min(bk_chk) as minBK, count(bk_chk) as allBK from hlog where tstat=2 and is_liq =0 AND  batch=0 and mid='"+mid+"' and" +
//				" ((sys_date * 100000 + sys_time >= "+lastLiqDateBefore+" and sys_date * 100000 + sys_time < "+intExpDateBefore+" and gid = 55000)"+
//		" or (sys_date >= "+ lastLiqDate+ "and sys_date < "+ intExpDate+ " and gid <> 55000))";
//		return  queryForMap("select min(bk_chk) as minBK, count(bk_chk) as allBK from hlog where tstat=2 and is_liq =0 and sys_date>="
//						+ lastLiqDate+ " and sys_date<"+ intExpDate+ " and mid='" + mid+"'");
		return  queryForMap(sql.toString());
	}
	
	/**
	 * flag=8 是有未完成的退款 flag=0 无未完成的退款
	 * @param mid
	 * @param intExpDate
	 * @return
	 * @throws Exception
	 */
	public int getIsRefunding(String mid, int intExpDate)throws Exception{
		int flag=8;
		Minfo minfo = getMinfoById(mid);
		int lastLiqDate = minfo.getLastLiqDate();
		String lastLiqDateBefore="";
		if(lastLiqDate==0){
			lastLiqDateBefore="0";
		}else{
		lastLiqDateBefore=String.valueOf(lastLiqDate);
		}
		
		String sql = "select  count(*) from  refund_log where stat =1   " +
				" and mid=" +mid + 
				" and req_date>= " +lastLiqDateBefore +
				" and req_date< " + intExpDate ; 
		int rowCount = queryForInt(sql);
		if(rowCount<=0){
			flag=0;
		}
		return flag;
		
	}

	public CurrentPage<ErrorAnalysis> searchSettleResult(int page, int bDate, int eDate, int errorType, int checkDate,int gate) {
		StringBuffer condsql = new StringBuffer(" from error_analysis e ");
		condsql.append(" where e.pay_date>=").append(bDate);
		condsql.append(" and e.pay_date<=").append(eDate);
		String sql = "select e.* ";
		if (errorType != -1) {
			condsql.append(" and e.error_type=" + errorType);
		}
		if (checkDate != 0) {
			condsql.append(" and e.check_date=" + checkDate);
		}
		if(gate != 0){
			condsql.append(" and e.gate=").append(gate);
		}
		condsql.append(" order by e.id desc");
		sql += condsql.toString();
		
		CurrentPage<ErrorAnalysis> p =  queryForPage( " select count(*) " + condsql.toString(), sql,	page, new AppParam().getPageSize(), ErrorAnalysis.class);

		List<ErrorAnalysis> list = p.getPageItems();
		for (ErrorAnalysis ea : list) {
			if (ea.getTseq() != null) {
				String sqlTseq = "select mid,gate,amount from hlog where tseq ="+ ea.getTseq();
				List li = queryForList(sqlTseq);
				if(li==null || li.size()==0){
					ea.setMid("0");
					ea.setGate(0);
					ea.setAmount(0);
					continue;
				} 
				Map m = (Map) li.get(0);
				ea.setMid(m.get("mid").toString());
				ea.setGate(Integer.parseInt(m.get("gate").toString()));
				ea.setAmount(Integer.parseInt(m.get("amount").toString()));
			}
		}
		p.setPageItems(list);
	   return p;
	}
	public List<ErrorAnalysis> searchSettleResultList(String bDate,String eDate, String errorType, String checkDate) {
		
		StringBuffer sql = new StringBuffer(" select e.* from error_analysis e");
		sql.append(" where e.pay_date>= ").append(Ryt.sql(bDate));
		sql.append(" and e.pay_date<= ").append(Ryt.sql(eDate));
		if (!errorType.equals("-1")) {
			sql.append(" and e.error_type=" + Ryt.sql(errorType));
		}
		if (!"".equals(checkDate)) {
			sql.append(" and e.check_date=" + Ryt.sql(checkDate));
		}
		sql.append(" order by e.id desc");

		List<ErrorAnalysis> list =  query(sql.toString(),ErrorAnalysis.class);
		for (ErrorAnalysis ea : list) {
			if (ea.getTseq() != null) {
				String sqlTseq = "select mid,gate,amount from hlog where tseq =" + ea.getTseq();
				List li =  queryForList(sqlTseq);
				if (li.size() > 0) {
					Map m = (Map) li.get(0);
					if (m.get("mid")!= null && !"".equals(m.get("mid").toString()))
						ea.setMid(m.get("mid").toString());
					if (m.get("gate")!= null && !"".equals(m.get("gate").toString()))
						ea.setGate(Integer.parseInt(m.get("gate").toString()));
					if (m.get("amount")!= null && !"".equals(m.get("amount").toString()))
						ea.setAmount(Integer.parseInt(m.get("amount").toString()));
				}
			}
		}
		return list;
	}

	public String confirmSolve(int loginmid, int loginuid, String remark,String seleteId) {
		remark = Ryt.sql(remark);
		seleteId = Ryt.sql(seleteId);
		String msg = "";
		int date = DateUtil.today();
		String[] tseqId = seleteId.split("&");
		for (int i = 0; i < tseqId.length; i++) {
			StringBuilder updatesql = new StringBuilder("update error_analysis ");
			updatesql.append("set state=1,solve_date= ").append(date).append(",");
			updatesql.append("solve_oper= ").append(loginuid).append(",");
			updatesql.append("solve_remark='").append(remark).append("' ");
			updatesql.append("where id= ").append(tseqId[i]);
			try {
				 update(updatesql.toString());
				saveOperLog("对账结果处理",remark + "成功");
			} catch (Exception e) {
				saveOperLog("对账结果处理",remark + "失败");
				return "操作失败,请重试！";
			}
			msg = "操作已成功！";
		}
		return msg;
	}
	public CurrentPage<AdjustAccount> queryAdjustList(int pageIndex,Integer pageSize, String mid, int type, int btdate, int etdate, int state,Integer mstate){
		StringBuilder pubSql = new StringBuilder(" from adjust_account a,minfo m where a.id>0 and a.mid=m.id ");
		if (!mid.trim().equals("")) pubSql.append(" and a.mid = ").append(Ryt.addQuotes(mid));
		if (type != 0) pubSql.append(" and a.type = ").append(type);
		if (btdate != 0) pubSql.append(" and a.submit_date >= ").append(btdate);
		if (etdate != 0) pubSql.append(" and a.submit_date <= ").append(etdate);
		if (state != -1) pubSql.append(" and a.state = ").append(state);
		if(mstate!=null)pubSql.append(" and m.mstate=").append(mstate);
		pubSql.append(" order by mid,submit_date DESC,submit_time DESC,a.id ");
		return queryForPage("select count(*) " + pubSql.toString(), "select a.* "+pubSql.toString(), pageIndex,pageSize, AdjustAccount.class);
	}
	/**
	 * 用于结算单查询，查询fee_liq_bath表
	 * @param mid  商户号
	 * @param state  结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate  查询起始日期
	 * @param endDate  查询结束日期
	 * @return
	 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int pageNo, String mid, int beginDate, int endDate) {
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb where flb.liq_date>= ");
		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
			pubSql.append(" and flb.state=3 and flb.mid=").append(Ryt.addQuotes(mid));
			return queryForPage("select count(*) " + pubSql.toString(), "select flb.* " + pubSql.toString(),pageNo,new AppParam().getPageSize(),FeeLiqBath.class);
			}
			
    /**
	 * 用于根据结算类型查询 结算单
	 * @param mid  商户号
	 * @param state  结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate  查询起始日期
	 * @param endDate  查询结束日期
	 * @return
	 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int pageNo,String merType,String mid,
			int beginDate, int endDate,Integer liqgid) {
		StringBuffer feildSql = new StringBuffer("select flb.*,m.liq_obj,m.category,m.abbrev ");
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb,minfo m,acc_infos acc where flb.mid=m.id and m.id=acc.uid and acc.uid =acc.aid and flb.liq_date>= ");

		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
		pubSql.append(" and flb.state in(2,3) and flb.liq_gid=").append(liqgid);
		if (!Ryt.empty(merType))
			pubSql.append(" and m.category= ").append(Integer.parseInt(merType));
		if (!Ryt.empty(mid))
			pubSql.append(" and m.id= ").append(Ryt.addQuotes(mid));
		if (liqgid == 2||liqgid==3) {
			feildSql.append(",m.bank_acct_name,m.bank_acct");// 银行账户
		} else if (liqgid == 1||liqgid==4) {
			feildSql.append(",acc.aid,acc.aname");// 电银账户
		}
		return queryForPage("select count(*) " + pubSql.toString(),feildSql.toString() + pubSql.toString(), pageNo,new AppParam().getPageSize(), FeeLiqBath.class);
	}
			
	public List<FeeLiqBath> queryPrintTableData(String merType,String mid, int beginDate,
			int endDate,Integer liqgid) {
		StringBuffer feildSql = new StringBuffer("select flb.*,m.liq_obj,m.category,m.abbrev ");
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb,minfo m,acc_infos acc where flb.mid=m.id and m.id=acc.uid and acc.uid =acc.aid and flb.liq_date>= ");
		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
		pubSql.append(" and flb.state in(2,3) and flb.liq_gid=").append(liqgid);
		if (!Ryt.empty(merType))
			pubSql.append(" and m.category= ").append(Integer.parseInt(merType));
		if (!Ryt.empty(mid))
			pubSql.append(" and m.id= ").append(Ryt.addQuotes(mid));
		if (liqgid == 2||liqgid==3) {
			feildSql.append(",m.bank_acct_name,m.bank_acct,m.bank_name,m.bank_branch ");// 银行账户
		} else if (liqgid == 1||liqgid==4) {
			feildSql.append(",acc.aid,acc.aname");// 电银账户
		}
		return query(feildSql.toString() + pubSql.toString(), FeeLiqBath.class);
	}
	
	/**
	 * 查询fee_liq_bath表并分页
	 * @param mid 商户号
	 * @param state 结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate查询结束日期
	 * @param batch查询批次号
	 * @param pageIndex 页码
	 * @return
	 */
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate, Integer liqGid) {
		String selSql = "select f.mid,f.trans_amt,f.ref_amt,(f.trans_amt-ref_amt)as tradeAmt,f.fee_amt,f.manual_add,f.manual_sub,f.liq_amt,";
		selSql += " f.batch,f.liq_date,f.state,f.ref_fee from fee_liq_bath f,minfo m";

		StringBuffer condSql = new StringBuffer(" where f.id > 0 and f.mid=m.id ");
		if (beginDate != 0)
			condSql.append(" and f.liq_date >= ").append(beginDate);
		if (endDate != 0)
			condSql.append(" and f.liq_date <= ").append(endDate);
		if (state == 0) {// 结算制表中 0 表示全部 结算确认中 -1表示全部
			condSql.append(" and f.state in (1,2)");
		} else if (state == -1) {
			condSql.append(" and f.state in (2,3)");
		} else {
			condSql.append(" and f.state =" + state);
		}
		if (!mid.trim().equals(""))
			condSql.append(" and f.mid='"+ mid+"'");
		if (!batch.trim().equals(""))
			condSql.append(" and f.batch='" + batch+"'");
		
		if(mstate!=null)condSql.append(" and m.mstate=").append(mstate);
		// 结算对象
		if(liqGid != null) condSql.append(" and f.liq_gid=").append(liqGid); 
		
		String querySql = selSql + condSql.toString();
		String countSql = "select count(f.id) from fee_liq_bath f,minfo m " + condSql.toString();
		return queryForPage(countSql, querySql, pageNo, 50, FeeLiqBath.class);
	}
	
	/**
	 * 根据商户名称（商户号）,日期，查询account表中明细
	 * @param mid 商户ID
	 * @param begin_date  查询起始日期
	 * @param end_date 查询结束日期
	 * @param pageIndex 查询页码
	 * @return
	 */
	public CurrentPage<Account> searchAccount(int pageNo,int pageSize, String mid, int begin_date, int end_date,Integer mstate) {
		String sql = "select a.* ,m.abbrev ,m.liq_type from account a,minfo m where ";
		String countsql = "select count(*) from account a,minfo m where ";
		if (!mid.equals("")) {
			sql += " a.mid='"+ mid +"' and ";
			countsql += " a.mid='" + mid + "' and ";
		}
		if(mstate!=null){
			sql += " m.mstate="+ mstate +" and ";
			countsql +=  "m.mstate=" + mstate + " and ";
		}
		sql += " a.mid=m.id and date>=" + begin_date + " and date<=" + end_date + " order by a.mid,a.id DESC";
		countsql += " a.mid=m.id and date>=" + begin_date + " and date<=" + end_date;
		return queryForPage(countsql, sql, pageNo,pageSize,Account.class);
	}
	/**
	 *  掉单手工提交 查询
	 * @param mid  商户号
	 * @param oId 订单号
	 * @param tseq 融易通流水号
	 * @param btdate 订单日期
	 * @param pageIndex  查询页码
	 * @return 符合条件订单list
	 */
	public CurrentPage<Hlog> queryLostOrder(int page, String mid, String oId, String tseq, int btdate) {
		// submit_lost_order.jsp 中调用
		//String table = DateUtil.today() == btdate ? " tlog t " : " hlog t ";
		
		String condi = " from tlog t where t.bk_send >0 and t.tstat=1 and gate!=0 ";
		if (oId != null && !oId.equals("")) {
			condi += " and t.oid = '" + Ryt.sql(oId) + "'";
		}
		if (!Ryt.empty(tseq)) {
			condi += " and t.tseq = " +Ryt.addQuotes(tseq);
		}
		condi += " and t.mid= '"+mid+"'";
		//condi += " and t.sys_date = " + btdate + " and t.mid= " + mid;
		String sql = " select t.mid,t.tseq,t.oid,t.sys_date,t.sys_time,t.amount,t.gate,t.gid " + condi
				+ " order by mid,tseq desc";
		String countsql = "select count(*) " + condi;
		return queryForPage(countsql, sql, page,Hlog.class);
	}
	/**
	 * 查询商户日结表(分页)
	 * @param mid 商户ID
	 * @param beginDate 查询起始时间
	 * @param endDate 查询结束时间
	 * @return 返回 封装日结表结果bean的list
	 */
	public CurrentPage<DailySheet> getDailySheet(int page, String mid, int beginDate, int endDate,Integer mstate) {
		String sql = "select m.abbrev,m.liq_type,d.* from daily_collect d,minfo m where d.mid=m.id and d.liq_date>="
				+ beginDate + " and d.liq_date<=" + endDate;
		String countsql = "select count(*) from daily_collect d,minfo m where d.mid=m.id and d.liq_date>=" + beginDate
				+ " and d.liq_date<=" + endDate;
		if (!mid.trim().equals("")) {
			sql += " and d.mid='"+mid+"'";
			countsql += " and d.mid='"+mid+"'";
		}
		if(mstate!=null){
			sql += " and m.mstate="+mstate;
			countsql += " and m.mstate="+mstate;
		}
		return queryForPage(countsql, sql, page,DailySheet.class);
	}
	private Map<String, Object> getMapValue(String sql) {
		try {
			return queryForMap(sql);
		} catch (Exception e) {
			return null;
		}
	}
	// 商户开通的网关
	public List<Integer> getMerGates(String mid){
	     return queryForIntegerList("select gate from fee_calc_mode where mid = '"+ mid+"'");
	}
	/**
	 * 获得支付总金额
	 * @param lastLiqDate
	 * @param lastBatch
	 * @param intExpDate
	 * @param mid
	 * @param gate
	 * @return
	 */
	public List<Map<String, Object>> getPayAmountMap(int lastLiqDate,String lastBatch,int intExpDate,String mid){
		StringBuilder paysql = new StringBuilder();
		paysql.append("select gate as gate, sum(h.amount) as psum,sum(h.fee_amt) as fsum, count(*) as pcount");
		paysql.append("  from hlog h");
		paysql.append(" where h.type not in (0,4) and h.tstat=2 and h.is_liq=0 and batch=0 and mid=" + Ryt.addQuotes(mid));
		paysql.append("   and ((h.sys_date >=" + lastLiqDate + " and h.sys_date <" + intExpDate + " and h.gid IN(55000,55001,90016))");
		paysql.append("    or  (h.sys_date >=" + lastLiqDate + " and h.sys_date <" + intExpDate + " and h.gid <> 55000 and h.gid<>55001 and h.gid<>90016 and h.bk_chk=1))");
		paysql.append(" group by gate");
		return queryForList(paysql.toString());
	}
	
	/**
	 * 获得支付总金额
	 * @param lastLiqDate
	 * @param lastBatch
	 * @param intExpDate
	 * @param mid
	 * @param gate
	 * @return
	 */
	public Map<String, Object> getPayAmountMap(int lastLiqDate,String lastBatch,int intExpDate,String mid,int gate){
		StringBuilder paysql = new StringBuilder();
		paysql.append(" select sum(h.amount) as psum,sum(h.fee_amt) as fsum, count(*) as pcount ");
		paysql.append(" from hlog h ");
		paysql.append(" where h.bk_chk=1 and  h.type not in (0,4) and h.tstat = 2 and h.is_liq=0 ");
		paysql.append(" and h.sys_date >= " ).append(lastLiqDate );
		paysql.append(" and h.sys_date < " ).append( intExpDate);
		paysql.append(" and h.mid = " ).append(Ryt.addQuotes(mid) );
		paysql.append( " and  h.batch=0 and h.gate = " ).append( gate);
		return getMapValue(paysql.toString());
	}
	/**
	 * 统计退款总金额
	 * @param lastLiqDate
	 * @param lastBatch
	 * @param intExpDate
	 * @param mid
	 * @param gate
	 * @return
	 */
	public Map<String, Object> getRefAmountMap(int lastLiqDate,String lastBatch,int intExpDate,String mid,int gate){
		StringBuilder refsql = new StringBuilder();
		refsql.append(" select sum(ref_amt) as rsum, count(*) as rcount,sum(mer_fee) as rfee ,sum(bk_fee_real) as bkfee from refund_log ");
		refsql.append(" where batch=0 and stat in (2,3,4) and mid=" ).append( Ryt.addQuotes(mid) ).append( " and gate=" ).append( gate);
		refsql.append(" and sys_date< " ).append( intExpDate).append( " and pro_date<= " ).append( intExpDate);
		return getMapValue(refsql.toString());
	}
	
	/**
	 * 统计退款总金额
	 * @param lastLiqDate
	 * @param lastBatch
	 * @param intExpDate
	 * @param mid
	 * @param gate
	 * @return
	 */
	public List<Map<String, Object>> getRefAmountMap(int lastLiqDate,String lastBatch,int intExpDate,String mid){
		StringBuilder refsql = new StringBuilder();
		refsql.append(" select gate as gate,sum(ref_amt) as rsum, count(*) as rcount,sum(mer_fee) as rfee ,sum(bk_fee_real) as bkfee from refund_log ");
		refsql.append(" where batch=0 and stat in (2,3,4) and mid=" ).append( Ryt.addQuotes(mid) );
		refsql.append(" and sys_date< " ).append( intExpDate).append( " and req_date< " ).append( intExpDate).append( " and pro_date<= " ).append( intExpDate);
		refsql.append(" group by gate");
		return queryForList(refsql.toString());
	}
	/**
	 * 手工调帐统计
	 * @param mid
	 * @param handType
	 * @return
	 */
	public Map<String, Object> getHandMap(String mid,int handType){
		StringBuilder mapSql = new StringBuilder();
		mapSql.append("select sum(a.account) as amt,count(a.id) as cnt ");
		mapSql.append("from adjust_account a ");
		mapSql.append(" where a.state=1 and a.batch=0 and  a.mid = ").append(Ryt.addQuotes(mid));
		mapSql.append(" and a.type =").append(handType);
		return getMapValue(mapSql.toString());
	}

	/**
	 * 对已制表的结算记录进行确认
	 * 
	 * @param batchs需要结算记录的批次号
	 * @return 确认的结果
	 */
	public int[] verifySettle(String[] batchs) {
		String[] sqls=new String[batchs.length];
		for (int i = 0; i < batchs.length; i++) {
			// 向操作员日志表插入记录；
			String sql = "update fee_liq_bath set state=3 where batch='" + Ryt.sql(batchs[i])+"'";
			sqls[i]=sql;
		}
		return batchSqlTransaction(sqls);
	}
	/**
	 * 手工调账审核成功
	 * @param action
	 * @param loginmid
	 * @param loginuid
	 * @param auditId
	 * @return
	 */
	public int[] auditSuccess(AdjustAccount adjust,long id,int isAdd,int loginuid) {
		int date = DateUtil.today();
		int time = DateUtil.getCurrentUTCSeconds();
		String updatesql = "update adjust_account set state=1 ,audit_operid=" + loginuid + " ,audit_date=" + date
			+ " ,audit_time=" + time + " where id=" + id;
			
//		StringBuffer insertAccountSql = new StringBuffer();
//		insertAccountSql.append("insert into account (mid,tseq,type,date,time,account,balance,amount) values (");
//		insertAccountSql.append(adjust.getMid()).append(",");
//		insertAccountSql.append(adjust.getId()).append(",");
//		insertAccountSql.append(adjust.getType() + 3).append(",");
//		insertAccountSql.append(date).append(",");
//		insertAccountSql.append(time).append(",");
//		insertAccountSql.append(adjust.getAccount()).append(",");
//		insertAccountSql.append(balance).append(",");
//		insertAccountSql.append(adjust.getAccount()).append(")");
		
		
	  //String accSeqSql = genAddAccSeqSql(adjust.getMid(), adjust.getMid(), adjust.getAccount(), 0, adjust.getAccount(),isAdd, "adjust_account", id+"", isAdd==0 ? "手工增加" : "手工减少");
		List<String> sqlList=genAddAccSeqSqls(adjust.getMid(), adjust.getMid(), adjust.getAccount(), 0, adjust.getAccount(),isAdd, Constant.ADJUST_ACCOUNT, id+"", isAdd==0 ? "手工增加" : "手工减少");
	//genAddAccSeqSql(adjust.getMid(), adjust.getMid(), adjust.getAccount(), 0, adjust.getAccount(),isAdd, "adjust_account", id, isAdd==0 ? "手工增加" : "手工减少");
				
//		String updateMinfoBalance = "update minfo set balance=" + balance + " where id=" + adjust.getMid();
//		String[] sqls = new String[] { updatesql, insertAccountSql.toString(), updateMinfoBalance };
		sqlList.add(updatesql);
		String[] sqlsList = sqlList.toArray(new String[sqlList.size()]);
		return batchSqlTransaction(sqlsList);
	}
	/**
	 * 手工调账审核失败
	 * @param action
	 * @param loginmid
	 * @param loginuid
	 * @param auditId
	 * @return
	 */
	public int auditFailure(String loginmid, int loginuid, long id) {
		int date = DateUtil.today();
		int time = DateUtil.getCurrentUTCSeconds();
		String updatesql = "update adjust_account set state=2 ,audit_operid=" + loginuid + " ,audit_date=" + date
		+ " ,audit_time=" + time + " where id=" + id;
		return update(updatesql);
	}	
	//收益表查询
	public List<InCome> searchIncome(String mid, int beginDate, int endDate,Integer mstate){
		StringBuffer sqlFeeLiqBath = new StringBuffer(" select ");
		sqlFeeLiqBath.append("f.batch as batch ,");//交易
		sqlFeeLiqBath.append("f.trans_amt  as amount ,");//交易
		sqlFeeLiqBath.append("f.ref_amt as refAmt ,");//退款
		sqlFeeLiqBath.append("f.fee_amt as feeAmt,");//交易手续费
		sqlFeeLiqBath.append("f.ref_fee as refFee,");//退回商户的手续费
		sqlFeeLiqBath.append("f.bk_ref_fee as BkRefFee ,");//银行退回的手续费
		sqlFeeLiqBath.append("f.liq_date as liqDate ,");
		sqlFeeLiqBath.append("f.mid as mid ");
		sqlFeeLiqBath.append(" from fee_liq_bath f ,minfo m where f.mid=m.id and ");
		sqlFeeLiqBath.append(" f.liq_date>=" + beginDate + " and f.liq_date<= ").append(endDate);
		if (!mid.trim().equals("")) {
			sqlFeeLiqBath.append(" and f.mid='"+ mid+"'");
		}
		if(mstate!=null){
			sqlFeeLiqBath.append(" and m.mstate=").append(mstate);	
		}
		return query(sqlFeeLiqBath.toString(),InCome.class);
	} 
	//根据批次号统计银行手续费
	public Integer getBankFeeByBatch(String batch){
		return queryForInt("select sum(h.bank_fee) from hlog h where h.batch = '" + batch+"'");
	}
	/**
	 * 在结算周期的商户
	 * @param mids
	 * @return
	 */
	public List<SettleMinfo> getInSettleMinfo(int date){
		String dateStr = String.valueOf(date);
		int week = DateUtil.dayCount(dateStr);
		int day = Integer.parseInt(dateStr.substring(6, 8));
		StringBuffer selsql = new StringBuffer();
		selsql.append("select id as mid,abbrev as name, ifnull(liq_type,0) as liqType,ifnull(liq_period,0) as liqPeriod,");
		selsql.append("ifnull(liq_limit,0) as liqLimit,last_batch as lastBatch, ifnull(last_liq_date,0) as lastLiqDate,mstate as mstate,");
		selsql.append("ifnull(exp_date,0) as expDate from minfo ");
		selsql.append( " where mstate = 0 and liq_state=0 and exp_date >= ").append(date);
		if (day == 1 || (week == 1 && (day == 2 || day == 3))) {
			// doSql = insql;
		} else {
			if (week == 1) {// 每周一次 在周一处理
				selsql.append(" and ( liq_period = 1  or liq_period = 2 )");
			} else if (week == 2 || week == 4) {// 每周两次 周二和周四处理
				selsql.append( " and ( liq_period = 1  or liq_period = 3 )");
			} else {//每天清算一次
				selsql.append(" and liq_period = 1");
			}
		}
		return query(selsql.toString(), SettleMinfo.class);
	}
	/**
	 * 不在结算周期的
	 * @param mids
	 * @return
	 */
	public List<SettleMinfo> getNoInSettleMinfo(String mids){
		StringBuffer selsql = new StringBuffer();
		selsql.append("select id as mid,abbrev as name, liq_type as liqType,liq_period as liqPeriod,");
		selsql.append("liq_limit as liqLimit,last_batch as lastBatch, last_liq_date as lastLiqDate,mstate as mstate,");
		selsql.append("exp_date as expDate from minfo ");
		selsql.append(" where mstate = 0 and liq_state=0 and liq_period is not null and id not in ").append(mids);
		return query(selsql.toString(), SettleMinfo.class);
	}
	/**
	 * 掉单手工确认
	 * @param tseq
	 * @param amount实际交易金额
	 * @param mfee实际手续费
	 * @return
	 */
	public int[] confirmLostOrderSuccess(String tseq, int merDate, String bank_seq,String amount,LostOrder bean,String mid){
		String mfee = ChargeMode.reckon(bean.getCalcMode(), amount,"0");
		int merfee = Integer.parseInt(Ryt.mul100(mfee));
		long amount2=Ryt.mul100toInt(amount);
		 String sql="update tlog set tstat=2,gid="+bean.getGid()+",bk_seq1="+Ryt.addQuotes(bank_seq)+",fee_amt="+merfee+" where tseq="+Ryt.addQuotes(tseq);
		 List<String> sqlsList=genAddAccSeqSqls(bean.getMid(),bean.getMid(), amount2, merfee,amount2-merfee , RecPay.INCREASE, "tlog", tseq,"支付");
		 sqlsList.add(sql);
	    String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
	    return batchSqlTransaction(sqls);
	   // update(sql, new Object[]{bean.getGid(),bank_seq,merfee,tseq});
		/*
		int sqlArrSize = merfee > 0 ?  3 : 2;
		String[] sqlArr=new String [sqlArrSize];
		
		StringBuffer sql = new StringBuffer();
		sql.append(" update ").append(tableName).append(" set tstat=2,gid=").append(bean.getGid());
		sql.append(",bk_seq1= ").append(Ryt.addQuotes(bank_seq));
		sql.append(",fee_amt= ").append(merfee);
		sql.append(" where tseq= ").append(tseq);
		
		sqlArr[0] = sql.toString();// 更新tlog或hlog表支付状态
		//商户资金流水表，及余额的sql语句
		sqlArr[1] = genAddAccSeqSql(bean.getMid(),bean.getMid(), Ryt.mul100toInt(amount),merfee, 0, "tlog", tseq, "支付");
		System.out.println(sqlArr[0]+"\n"+sqlArr[1] );
		// 调用事务处理掉单结果
		return batchSqlTransaction(sqlArr);
		*/
	}
	
	public int getPreAmtByTseq(String tableName,long tseq){
		String sql="select pre_amt from "+tableName+" where tseq="+tseq;
		return queryForInt(sql);
	}
	public int confirmListOrderFailure(int gid ,String bankSeq,String tseq,int merDate){
		String sql = "update tlog set tstat=3 ,gid=" + gid + " ,bk_seq1= "
		+ Ryt.addQuotes(bankSeq) + " where tseq=" +Ryt.addQuotes(tseq);
		if (merDate < DateUtil.today()) {
			sql = sql.replaceFirst("tlog", "hlog");
		}
		return update(sql);
	}
	/**
	 * 网关对应商户交易 资金归集
	 * @param gateRouteId
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public List<MergeDetail> payMergeToMer(int gateRouteId,String bkName, Integer bdate, Integer edate){
		
		StringBuffer paySql = new StringBuffer();
		paySql.append("select '"+ bkName +"' as bk_name ,h.mid as mid ,sum(h.bank_fee) as bank_fee,");
		paySql.append("sum(h.pay_amt) as pay_amt from hlog h where h.tstat = 2 and h.bk_chk=1 ");
		paySql.append("and h.sys_date BETWEEN ").append(bdate).append(" AND ").append(edate);
		paySql.append(" and h.gid= ").append(gateRouteId);
		paySql.append(" group by h.mid");
		return query(paySql.toString(),MergeDetail.class);
	}

	/**
	 * 应收银行交易 资金归集
	 * @param gateRouteId
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public Map<String,Object> payMerge(int gateRouteId, Integer bdate, Integer edate){
		StringBuffer paySql = new StringBuffer();
		paySql.append("select '"+ gateRouteId +"' as bank_type ,sum(h.bank_fee) as bank_fee,");
		paySql.append("sum(h.pay_amt) as pay_amt from hlog h where h.tstat = 2 and h.bk_chk=1 ");
		paySql.append("and h.sys_date BETWEEN ").append(bdate).append(" AND ").append(edate);
		paySql.append(" and h.gid=").append(gateRouteId);
		return queryForMap(paySql.toString());
	}
	/**
	 * 应收银行退款 资金归集
	 * @param gateRouteId
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public Map<String,Object> refMerge(int gateRouteId, Integer bdate, Integer edate){
		StringBuffer refSqlCon = new StringBuffer();
		refSqlCon.append(" select sum(r.ref_amt) as ref_amt ,sum(r.bk_fee) as bk_fee,sum(bk_fee_real) as bk_fee_real ");
		refSqlCon.append(" from refund_log r where r.vstate=1 and r.ref_date BETWEEN ").append(bdate).append(" AND ");
		refSqlCon.append(edate);
		refSqlCon.append(" and r.gid=").append(gateRouteId);
		return queryForMap(refSqlCon.toString());
		
	}

	//调账审核后记录存管银行数据
	/**
	public void createCGData(AdjustAccount adjust) {
		
		if(adjust==null) return;
		
		int temp = (adjust.getType() == 1 ? 4 : 5 ); //"增加" 4-调增5-调减
		StringBuffer sql2 = new StringBuffer();
		sql2.append("insert into tr_seq (liq_date,Obj_id,Tr_date,Tr_time,Tr_amt,Tr_type,tr_flag,sys_date)");
		sql2.append(" values (0,");
		sql2.append(adjust.getMid()).append(",");
		sql2.append(adjust.getSubmitDate()).append(",");
		sql2.append(adjust.getSubmitTime()).append(",");
		sql2.append(adjust.getAccount()).append(",");
		sql2.append(temp).append(",");//4-调增5-调减
		sql2.append(adjust.getId()).append(",");
		sql2.append(DateUtil.today());
		sql2.append(")");
		
		try {
			//增加结算金额流水
			update(sql2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	//结算确认后记录存管银行数据
	public void createCGData(String[] batchs) {
		for(String b : batchs)
			doCreateCGData(b);
	}
	
	
	public void doCreateCGData(String b) {
		
		String sql = " select * from fee_liq_bath where batch = " + b;
		FeeLiqBath o = queryForObject(sql,FeeLiqBath.class);
		if(o==null) return;
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append("insert into tr_seq (liq_date,Obj_id,Tr_date,Tr_time,Tr_amt,Tr_type,tr_flag,sys_date)");
		sql2.append(" values (0,");
		sql2.append(o.getMid()).append(",");
		sql2.append(o.getGenDate()).append(",");
		sql2.append(DateUtil.getCurrentUTCSeconds()).append(",");
		sql2.append(o.getLiqAmt()).append(",");
		sql2.append(6).append(",");//6-结算
		sql2.append(b).append(",");
		sql2.append(DateUtil.today());
		sql2.append(")");
		
		BkAccount bk = getCGBank();
		if(bk==null) return;
		
		String bkNo = bk.getBkNo();
		
		String sql3 = "update bk_account set bk_bl = bk_bl - " + o.getLiqAmt()+", bf_bl = bf_bl- " + o.getLiqAmt() + " where bk_no='"+bkNo+"'";
		
		try {
			//增加结算金额流水
			update(sql2.toString());
			//修改银行余额数据
			update(sql3);
		}catch (DuplicateKeyException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

**/
	public List<AccInfos> searchBalance(String mid){
		String sql="select * from acc_infos where uid ='"+mid+"' and aid='"+mid+"'";
		return query(sql,AccInfos.class);
	}
	
	/**
	 * 中信无磁无密请款总条数查询
	 * @param bathNo 批次号
	 * @return
	 */
	public int getCiticSumCount(String bathNo){
		String sql="select count(*) from tlog where tstat=2 and p2="+Ryt.addQuotes(bathNo);	
		return queryForInt(sql);
	}
	
	/**
	 * 中信无磁无密请款总金额查询
	 * @param bathNo 批次号
	 * @return
	 */
	public long getCiticSumAmt(String bacthNo){
		String sql="select sum(pay_amt) from tlog where tstat=2 and  p2="+Ryt.addQuotes(bacthNo);	
			return queryForLong(sql);
	}
	
	public List<Tlog> getCiticDataSet(String bacthNo){
		String sql="select tseq,pay_amt,p1,p2,p7 from tlog where tstat=2 and p2="+Ryt.addQuotes(bacthNo);
		return query(sql, Tlog.class);
	}
	/**
	 * 查询所有状态为成功的订单
	 * @param bathNo
	 * @return
	 */
	public List<Tlog> getCiticSucOrder(String bacthNo){
		String sql="select tseq from tlog where p2="+Ryt.addQuotes(bacthNo) +" and tstat=2";
		return query(sql, Tlog.class);
	}
	public int updatetCiticOrderState(String tseq,String infos,String bacthNo){
		String []info=infos.split("##");
		String sql="update tlog set tstat=2,bk_seq1="+Ryt.addQuotes(info[1])+",p7="+Ryt.addQuotes(info[0])+" where tseq="+Ryt.addQuotes(tseq)+" and p2="+Ryt.addQuotes(bacthNo);
		return update(sql);
	}
	/**
	 * 查询结算对象
	 * @param bathNo
	 * @return
	 */	
	
	public int quertliqobj(String mid){
		 String sql="select liq_obj from minfo where id ='"+mid+"'";
		 return queryForInt(sql);
	}
	
	/**
	 * 查询结算相关的属性
	 * @param bathNo
	 * @return
	 */	
	
	public Minfo quertliqbymid(String mid){
		 String sql="select liq_obj,man_liq,auto_df_state from minfo where id ='"+mid+"'";
		 return queryForObject(sql, Minfo.class);
	}
	
	/**
	 * 查询结算对象
	 * @param bathNo
	 * @return
	 */	
	
	public int querymanliq(String mid){
		 String sql="select man_liq from minfo where id ='"+mid+"'";
		 return queryForInt(sql);
	}
	
	public int queryLiqGid(String batch){
		 String sql="select liq_gid from fee_liq_bath where batch ='"+batch+"'";
		 return queryForInt(sql);
	}
	/**
	 * 结算对象为账户时
	 * @param bathNo
	 * @return
	 */	
	public int[] liqaccount(FeeLiqBath mid,String remark,String tlogsql) {
		AccSeqs acc = new AccSeqs();
		acc.setUid(mid.getMid());
		acc.setAid(mid.getMid());
		acc.setAmt(mid.getLiqAmt());
		acc.setTrAmt(mid.getLiqAmt());
		acc.setTrFee(0);
		acc.setTbName(Constant.FEE_LIQ_BATH);
		acc.setTbId(mid.getBatch());
		acc.setRemark(remark);
		List<String> sqlsList = RecordLiveAccount.LiqToAccount(acc);
		String sql = "update fee_liq_bath set state=3 where batch='" + Ryt.sql(mid.getBatch()) + "'";
		sqlsList.add(sql);
		sqlsList.add(tlogsql);
		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
		int[] liqcon = batchSqlTransaction(sqls);

		return liqcon;
	}
	
	public int[] liqbankcard(FeeLiqBath mid) {
		AccSeqs acc = new AccSeqs();
		acc.setUid(mid.getMid());
		acc.setAid(mid.getMid());
		acc.setAmt(mid.getLiqAmt());
		acc.setTrAmt(mid.getLiqAmt());
		acc.setTrFee(0);
		acc.setTbName(Constant.FEE_LIQ_BATH);
		acc.setTbId(mid.getBatch());
		acc.setRemark("结算到银行卡");
		List<String> sqlsList = RecordLiveAccount.LiqToBankCard(acc);
		String sql = "update fee_liq_bath set state=3 where batch='" + Ryt.sql(mid.getBatch()) + "'";
		sqlsList.add(sql);
		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
		int[] liqcon = batchSqlTransaction(sqls);

		return liqcon;
	}
	
	/*
	 * 划款结果查询
	 * 
	 */
	
	public  CurrentPage<TransferMoney> querytransfer(Integer pageNo,Integer pageSize,String mid,Integer bdate,Integer edate,String batchNo,Integer tstat){
		
		StringBuffer condition = getquerytransferSql(mid,bdate, edate,batchNo,tstat);
		String sqlCount = " SELECT  COUNT(b.tseq) from  ("+ condition.toString() + ") as b";
		String amtSumSql = " SELECT sum(ABS(b.amount)) from ("+ condition.toString() + ") as b";
		String sysAtmFeeSumSql = " SELECT sum(b.feeAmt) from ("+ condition.toString() + ") as b";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount, condition.toString(), pageNo, pageSize, TransferMoney.class,sumSQLMap);
	}
	
	private StringBuffer getquerytransferSql(String mid, Integer bdate,
			Integer edate, String batchNo, Integer tstat) {
		StringBuffer querytransferSql = new StringBuffer();
		querytransferSql.append("select a.tseq as tseq,a.mid as mid,a.p3 as bankNo,f.batch as batch ,");
		querytransferSql.append("f.liq_date as liqDate ,m.abbrev as name ,m.bank_name as bankName ,");
		querytransferSql.append("m.bank_acct_name as bankAcctName,m.bank_acct as bankAcct,");
		querytransferSql.append("a.tstat as tstat ,a.amount as amount,a.gate as gate ,a.gid as gid,");
		querytransferSql.append("a.error_msg as errorMsg,a.error_code as errorCode,a.fee_amt as feeAmt");
		querytransferSql.append(" from hlog a ,minfo m,fee_liq_bath f ");
		querytransferSql.append(" where a.mid=m.id and a.p9=f.batch and a.data_source=8 ");
		querytransferSql.append(" and f.liq_date>=").append(bdate);
		querytransferSql.append(" and f.liq_date<=").append(edate);
		querytransferSql.append(" and a.sys_date>=").append(bdate);
		querytransferSql.append(" and a.sys_date<=").append(edate);
		if (!Ryt.empty(mid)) {
			querytransferSql.append(" and f.mid=").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(batchNo)) {
			querytransferSql.append(" and f.batch=").append(
					Ryt.addQuotes(batchNo));
		}
		if (tstat != null) {
			querytransferSql.append(" and a.tstat=").append(tstat);
		}
		
		StringBuffer querytransferSql2 = new StringBuffer();
		querytransferSql2.append("select a.tseq as tseq,a.mid as mid,a.p3 as bankNo,f.batch as batch ,");
		querytransferSql2.append("f.liq_date as liqDate ,m.abbrev as name ,m.bank_name as bankName ,");
		querytransferSql2.append("m.bank_acct_name as bankAcctName,m.bank_acct as bankAcct,");
		querytransferSql2.append("a.tstat as tstat ,a.amount as amount,a.gate as gate ,a.gid as gid,");
		querytransferSql2.append("a.error_msg as errorMsg,a.error_code as errorCode,a.fee_amt as feeAmt");
		querytransferSql2.append(" from tlog a ,minfo m,fee_liq_bath f ");
		querytransferSql2.append(" where a.mid=m.id and a.p9=f.batch and a.data_source=8");
		querytransferSql2.append(" and f.liq_date>=").append(bdate);
		querytransferSql2.append(" and f.liq_date<=").append(edate);
		querytransferSql2.append(" and a.sys_date>=").append(bdate);
		querytransferSql2.append(" and a.sys_date<=").append(edate);
		if (!Ryt.empty(mid)) {
			querytransferSql2.append(" and f.mid=").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(batchNo)) {
			querytransferSql2.append(" and f.batch=").append(
					Ryt.addQuotes(batchNo));
		}
		if (tstat != null) {
			querytransferSql2.append(" and a.tstat=").append(tstat);
		}
		querytransferSql.append(" union all ").append(querytransferSql2);
		return querytransferSql;

	}
	
	/*
	 * 根据商户号查询代付的信息
	 */
	public Minfo queryAccBymid(String mid){
		StringBuffer sql = new StringBuffer();
		sql.append("select pbk_acc_name   as pbkAccName,");
		sql.append("       pbk_acc_no     as pbkAccNo,");
		sql.append("       pbk_no         as pbkNo,");
		sql.append("       pbk_gate_id    as pbkGateId,");
		sql.append("       pbk_prov_id    as pbkProvId,");
		sql.append("       pbk_name       as pbkName,");
		sql.append("       pbk_branch     as pbkBranch,");
		sql.append("       bank_acct_name as bankAcctName,");
		sql.append("       bank_acct      as bankAcct,");
		sql.append("       open_bk_no     as openBkNo,");
		sql.append("       gate_id        as gateId,");
		sql.append("       bank_prov_id   as bankProvId,");
		sql.append("       bank_name      as bankName,");
		sql.append("       bank_branch    as bankBranch");
		sql.append("  from minfo");
		sql.append(" where id = " + Ryt.addQuotes(mid));
		
		return queryForObject(sql.toString(), Minfo.class);	
	}
	
	/*
	 * 根据商户好查询配置允许访问的IP地址
	 */
	public String queryBytmsIp(String mid){
		String sql=" select par_value from global_params where par_name='TMS_IP'";
		return queryForString(sql);
	}
	
	/**
	 * 查询自动代付是否开启
	 * @param bathNo
	 * @return
	 */	
	
	public int queryautoDfState(String mid){
		 String sql="select auto_df_state from minfo where id ='"+mid+"'";
		 return queryForInt(sql);
	}
	
	/**
	 * 查询商户类型
	 * @param bathNo
	 * @return
	 */	
	
	public int querymertype(String mid){
		 String sql="select mer_type from minfo where id ='"+mid+"'";
		 return queryForInt(sql);
	}	
	/*
	 * 商户账务  初始化表mer_accounts (填充一个月的数据)
	 */
	public List<MerAccount> queryaccounts(Integer bdate, Integer edate){
		List<MerAccount> newItems=new ArrayList<MerAccount>();
		List<MerAccount> merlist = getZWInfoFromAccSeqs(bdate, edate, newItems);
		List<MerAccount> otherMer=getMerAccount(merlist,bdate);
		if(otherMer.size()>0){
			for (MerAccount mer : otherMer) {
				newItems.add(mer);
			}
		}
		return newItems;			
	}
	/***
	 * 查询指定日期的账务信息
	 * @param bdate
	 * @param edate
	 * @param newItems
	 * @return
	 */
	protected List<MerAccount> getZWInfoFromAccSeqs(Integer bdate,
			Integer edate, List<MerAccount> newItems) {
		List<MerAccount> merlist=queryAccountbalance( bdate,  edate);
		Map<String, MerAccount> nowMerLiqAmt=queryliaAmt( bdate,  edate);
		Map<String, MerAccount> MerAccountAmt=queryAcountAmt(bdate,  edate);
		for(MerAccount merAccount:merlist){
			 	String[] all_balance=merAccount.getAll_balance().split(",");
			 	long previousBalance,currentBalance,transAmt=0;
			 	String tb_name=null;Integer rec_pay=null;
			 	
			 	if(all_balance.length==1){
			 		String[] sStr=all_balance[0].split("\\*");
			 		tb_name=sStr[1];rec_pay=Integer.parseInt(sStr[2]);
			 		transAmt=Long.parseLong(sStr[3]);
			 		 previousBalance=Long.parseLong(sStr[0]);
			 		 currentBalance=Long.parseLong(sStr[0]);
			 	}else{
			 		String[] sStr=all_balance[0].split("\\*");
			 		tb_name=sStr[1];rec_pay=Integer.parseInt(sStr[2]);
			 		String[] bStr=all_balance[1].split("\\*");
			 		transAmt=Long.parseLong(sStr[3]);
			 		previousBalance=Long.parseLong(sStr[0]);
			 		currentBalance=Long.parseLong(bStr[0]);
			 	}
			 	if(tb_name.equals(Constant.FEE_LIQ_BATH) || tb_name.equals(Constant.FEE_LIQ_BATH+"(1)")){
			 		merAccount.setPreviousBalance(previousBalance);
			 	}else if(tb_name.equals(Constant.FEE_LIQ_BATH+"(2)")){
			 		merAccount.setPreviousBalance(previousBalance + transAmt);
			 	}else{
			 		merAccount.setPreviousBalance(rec_pay==0 ? previousBalance-transAmt :  previousBalance+transAmt);
			 	}
			 	
//			 	merAccount.setPreviousBalance(merAccount.getRec_pay()==0 ? previousBalance-merAccount.getTransAmt() :  previousBalance+merAccount.getTransAmt());
			 	merAccount.setTransAmt(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getTransAmt());
			 	merAccount.setFeeAmt(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getFeeAmt());
		    	merAccount.setCurrentBalance(currentBalance);
		    	merAccount.setLiqAmt(nowMerLiqAmt.get(merAccount.getMid())==null?0:nowMerLiqAmt.get(merAccount.getMid()).getLiqAmt());
		    	merAccount.setRefAmt(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getRefAmt());
		    	merAccount.setRefFeeAmt(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getRefFeeAmt());
		    	merAccount.setManualAdd(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getManualAdd());
		    	merAccount.setManualSub(MerAccountAmt.get(merAccount.getMid())==null?0:MerAccountAmt.get(merAccount.getMid()).getManualSub());
		    	merAccount.setBeginTrantDate(bdate);
		    	merAccount.setEndTrantDate(edate);
				newItems.add(merAccount);
		    }
		return merlist;
	}
	
	/*
	 * 商户账务查询上期账务
	 */
	public List<MerAccount> queryAccountbalance( Integer bdate, Integer edate){		
		Map<String,MerAccount> map=new HashMap<String, MerAccount>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select b.abbrev as abbrev,c.aid as mid,b.category as category, group_concat(concat((c.balance+c.all_balance),'*',c.tb_name,'*',c.rec_pay,'*',c.amt ) order by c.id separator  ',') all_balance ");
//		sqlBuffer.append(" ,(select tb_name from acc_seqs where id=b.sq) as tbName,(select rec_pay from acc_seqs where id=b.sq ) as rec_pay");
		sqlBuffer.append(" from  (select min(a.id) sq,max(a.id) bq ,m.abbrev as abbrev,m.category as category,a.rec_pay as rec_pay ");
		sqlBuffer.append("from minfo m,acc_seqs a ");
		sqlBuffer.append("where a.aid = m.id ");
		if (bdate != null)
			sqlBuffer.append(" and a.tr_date>=").append(bdate);
		if (edate != null)
			sqlBuffer.append(" and a.tr_date<=").append(edate);
		sqlBuffer.append(" group by aid) b, acc_seqs c ");
		sqlBuffer.append(" where b.sq = c.id or b.bq = c.id ");
		sqlBuffer.append(" group by c.aid");
		System.out.println("sql:"+sqlBuffer.toString());
		return query(sqlBuffer.toString(),MerAccount.class);
	}

	/*
	 * 商户账务查询结算金额
	 */
	public Map<String, MerAccount> queryliaAmt( Integer bdate, Integer edate){	
		Map<String,MerAccount> map=new HashMap<String, MerAccount>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select f.mid as mid,sum(f.liq_amt) as liqAmt  ");
		sqlBuffer.append(" from fee_liq_bath f,minfo m where f.mid=m.id and state=3");
		if (bdate != null)
			sqlBuffer.append(" and f.liq_date>=").append(bdate);
		if (edate != null)
			sqlBuffer.append(" and f.liq_date<=").append(edate);
		sqlBuffer.append(" group by f.mid;");
		List<MerAccount> mer=query(sqlBuffer.toString(), MerAccount.class);
		    for(MerAccount merAccount:mer){
		    	map.put(merAccount.getMid(), merAccount);
		    }	 
			 return map;			 
		
	}
	/*
	 * 商户账务查询金额
	 */
	public Map<String, MerAccount> queryAcountAmt(Integer bdate, Integer edate){	
		Map<String,MerAccount> map=new HashMap<String, MerAccount>();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select a.uid as mid,sum(a.tr_amt) as transAmt,sum(a.tr_fee) as feeAmt,a.rec_pay as rec_pay,a.tb_name as tbName");
		//添加子查询
		sqlBuffer.append( " from minfo m, acc_seqs a where a.aid=m.id  ");
		if (bdate != null)
			sqlBuffer.append(" and a.tr_date>=").append(bdate);
		if (edate != null)
			sqlBuffer.append(" and a.tr_date<=").append(edate);
		sqlBuffer.append(" group by a.uid,a.tb_name,a.rec_pay;");
		List<MerAccount> mer=query(sqlBuffer.toString(), MerAccount.class);
	    for(MerAccount merAccount:mer){
	    	long transAmt=0;Integer transFee=0;
	    	long refAmt=0;Integer refFee=0;
	    	long manualSub=0;long manualAdd=0;
	    	String tbName=merAccount.getTbName();
//	    	if(tbName.equals(Constant.FEE_LIQ_BATH)||tbName.equals(Constant.FEE_LIQ_BATH+"(1)") ||tbName.equals(Constant.FEE_LIQ_BATH+"(2)"))continue;
	    	Integer recPay=merAccount.getRec_pay();
	    	if(map.containsKey(merAccount.getMid())){
	    		MerAccount merAccount2=map.get(merAccount.getMid());
	    		//判断条件  tbName:tlog/hlog recPay :0 
	    		//交易金额 累加  交易手续费累加
	    		if((tbName.equals(Constant.TLOG)||tbName.equals(Constant.HLOG)) && recPay==0){
	    			transAmt=merAccount2.getTransAmt()+merAccount.getTransAmt();
	    			transFee=merAccount2.getFeeAmt()+merAccount.getFeeAmt();
	    			refAmt=merAccount2.getRefAmt();
	    			refFee=merAccount2.getRefFeeAmt();
	    			manualSub=merAccount2.getManualSub();
	    			manualAdd=merAccount2.getManualAdd();
	    		}else if(tbName.equals(Constant.ADJUST_ACCOUNT) && recPay==0){
	    			transAmt=merAccount2.getTransAmt();
	    			transFee=merAccount2.getFeeAmt();
	    			refAmt=merAccount2.getRefAmt();
	    			refFee=merAccount2.getRefFeeAmt();
	    			manualSub=merAccount2.getManualSub();
	    			manualAdd=merAccount2.getManualAdd()+merAccount.getTransAmt();
	    		}else if(tbName.equals(Constant.ADJUST_ACCOUNT) && recPay==1){
	    			transAmt=merAccount2.getTransAmt();
	    			transFee=merAccount2.getFeeAmt();
	    			refAmt=merAccount2.getRefAmt();
	    			refFee=merAccount2.getRefFeeAmt();
	    			manualSub=merAccount2.getManualSub()+merAccount.getTransAmt();
	    			manualAdd=merAccount2.getManualAdd();
	    		}else if(tbName.equals(Constant.REFUND_LOG)){
	    			transAmt=merAccount2.getTransAmt();
	    			transFee=merAccount2.getFeeAmt();
	    			refAmt=merAccount2.getRefAmt()+merAccount.getTransAmt();
	    			refFee=merAccount2.getRefFeeAmt()+merAccount.getFeeAmt();
	    			manualSub=merAccount2.getManualSub();
	    			manualAdd=merAccount2.getManualAdd();
	    		}else{
	    			transAmt=merAccount2.getTransAmt();
	    			transFee=merAccount2.getFeeAmt();
	    			refAmt=merAccount2.getRefAmt();
	    			refFee=merAccount2.getRefFeeAmt();
	    			manualSub=merAccount2.getManualSub();
	    			manualAdd=merAccount2.getManualAdd();
	    		}
	    		
	    	}else{
	    		
	    		if((tbName.equals(Constant.TLOG)||tbName.equals(Constant.HLOG)) && recPay==0){
	    			transAmt=merAccount.getTransAmt();
	    			transFee=merAccount.getFeeAmt();
	    		}else if(tbName.equals(Constant.ADJUST_ACCOUNT) && recPay==0){
	    			//判断条件tbName :adjust_account 
		    		//调账金额累加  调增 调减
	    			manualAdd=merAccount.getTransAmt();
	    		}else if(tbName.equals(Constant.ADJUST_ACCOUNT) && recPay==1){
	    			manualSub=merAccount.getTransAmt();
	    		}else if(tbName.equals(Constant.REFUND_LOG)){
	    			//判断条件 tbName :refund_log
	    			refAmt=merAccount.getTransAmt();
	    			refFee=merAccount.getFeeAmt();
	    		}
	    		
	    	}
	    	merAccount.setTransAmt(transAmt);
	    	merAccount.setFeeAmt(transFee);
	    	merAccount.setRefAmt(refAmt);
	    	merAccount.setRefFeeAmt(refFee);
	    	merAccount.setManualAdd(manualAdd);
	    	merAccount.setManualSub(manualSub);
	    	map.put(merAccount.getMid(), merAccount);
	    }	 
		 return map;			 
		
	}
	
	/**
	 * 通过网关获取手续费
	 * @param mid
	 * @param gate
	 * @return
	 * @throws Exception 
	 */
	public FeeCalcMode getFeeModeByGate(String mid,String gate) throws Exception{
		StringBuffer sql=new StringBuffer("select calc_mode,gid from fee_calc_mode where mid =");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and gate=");
		sql.append(gate);
		FeeCalcMode mode=queryForObject(sql.toString(),FeeCalcMode.class);
		if(null==mode)
			throw new Exception("该网关尚未配置");
		return mode;
	}
	
   //自动结算、自动代付订单录入tlog
	
	public Map<String,String> insertliqtlog(String mid,String payAmt,Integer gate,String AccNo,String AccName,String OpenBkNo,String dfType,
			String data_source,String liqBatch,String transType,String pbkProvId,String bankName,String bankBranch) throws Exception{
		FeeCalcMode feeCalcMode=getFeeModeByGate(mid,gate+""); 
		int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeCalcMode.getCalcMode(),payAmt));
		long trantAmt=Ryt.mul100toInt(payAmt)-transFee;
		java.text.DateFormat format3= new java.text.SimpleDateFormat("yyyyMMdd");    
		String date = format3.format(new Date());
		int time=Ryt.getCurrentUTCSeconds();
		String oid=Ryt.crateBatchNumber();
		Integer dfType1 = null;
		if(dfType.equals("A")){
			dfType1=11;
		}else if(dfType.equals("B")){
			dfType1=12;
		}
		String msg=null;
		if(data_source.equals("7")){
			msg="自动代付";
		}else if(data_source.equals("8")){
			msg="自动结算";
		}
		String bk_url="";
		String fg_url="";
		TlogBean tlog=new TlogBean();
		tlog.setVersion(10);
		tlog.setIp(new Long(10191));
		tlog.setMdate(Integer.parseInt(date));
		tlog.setMid(mid);
		tlog.setBid(mid);
		tlog.setOid(oid);
		tlog.setAmount(trantAmt);
		tlog.setType(dfType1);
		tlog.setGate(gate);
		tlog.setSys_date(Integer.parseInt(date));
		tlog.setInit_sys_date(Integer.parseInt(date));
		tlog.setSys_time(time);
		tlog.setTstat(0);
		tlog.setBk_flag(0);
		tlog.setMer_priv(Base64.encode(msg));
		tlog.setBk_url(bk_url);
		tlog.setFg_url(fg_url);
	    tlog.setTrans_period(30);
	    tlog.setPay_amt(Ryt.mul100toInt(payAmt));
	    tlog.setCard_no(AccNo);
	    tlog.setBk_send(time);
	    tlog.setP1(AccNo);
	    tlog.setP2(AccName);
	    tlog.setP3(OpenBkNo);
	    tlog.setP4(bankName);
	    tlog.setP5(bankBranch);
	    tlog.setP6("0");
	    tlog.setP7(Base64.encode(msg));
	    tlog.setP9(liqBatch);
	    tlog.setP10(pbkProvId);
	    tlog.setIs_liq(1);
	    tlog.setFee_amt(transFee);
	    tlog.setGid(feeCalcMode.getGid());
	    tlog.setData_source(Integer.parseInt(data_source));
		String sql=SqlGenerator.generateInsertSql(tlog);
		Map<String,String> map=new HashMap<String, String>();
		map.put("sql", sql);
		map.put("oid", oid);
		map.put("date", date.toString());
	    return map;
		
	}

	// 查询hlog流水号
	public String getTlogTseq(String mid, String oid, String date,Integer gate, String AccNo, String AccName, String OpenBkNo) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select tseq from tlog where mid=").append(Ryt.addQuotes(mid));
		sql.append(" and oid=").append(Ryt.addQuotes(oid));
		sql.append(" and mdate=").append(date);
		sql.append(" and gate=").append(gate);
		sql.append(" and p1=").append(Ryt.addQuotes(AccNo));
		sql.append(" and p2=").append(Ryt.addQuotes(AccName));
		sql.append(" and p3=").append(Ryt.addQuotes(OpenBkNo));

		return queryForString(sql.toString());

	}
	/****
	 * 账务查询
	 * 获取账户信息（不在查询范围内的商户）
	 * @return
	 */
	public List<MerAccount> getMerAccount(List<MerAccount> list,Integer bdate){
		StringBuffer csql=new StringBuffer();
		List<MerAccount> mer=new ArrayList<MerAccount>();
		 if(  list.size()>0){
			for (MerAccount merAccount : list) {
				String mid=merAccount.getMid();
				csql.append("'").append(mid).append("',");
			}
		}else{
			return mer;
		}
		mer=queryAccountbalance(bdate,csql.toString().substring(0,csql.length()-1));
		for (MerAccount merAccount : mer) {
			merAccount.setTransAmt(new Long(0));
			merAccount.setFeeAmt(0);
			merAccount.setRefAmt(new Long(0));
			merAccount.setRefFeeAmt(0);
			merAccount.setLiqAmt(new Long(0));
			merAccount.setManualAdd(new Long(0));
			merAccount.setManualSub(new Long(0));
			String[] all_params=null;
			if(null!=merAccount.getAll_balance()){
				all_params=merAccount.getAll_balance().split("\\*");
				String previousBalance=all_params[0];
				merAccount.setPreviousBalance(new Long(previousBalance));
			}else{
				merAccount.setPreviousBalance(new Long("0"));
			}
			merAccount.setCurrentBalance(new Long("0"));
		}
		return mer;
	}
	
	 /***
	  * 	商户账务查询
	  * @param pageNo
	  * @param pageSize
	  * @param mid
	  * @param category
	  * @param bdate
	  * @param edate
	  * @return
	  */
	public CurrentPage<MerAccount> 	queryMerAccounts(Integer pageNo,Integer pageSize,String mid, Integer category, Integer bdate, Integer edate){
		StringBuffer condition=new StringBuffer();
		if(!Ryt.empty(mid) && !"".equals(mid)){
			condition.append(" and mid=").append(Ryt.addQuotes(mid));
		}
		if(category!=null){
			condition.append(" and category=").append(category);
		}
		if (bdate != null)
			condition.append(" and beginTrantDate>=").append(bdate);
		if (edate != null)
			condition.append(" and endTrantDate<=").append(edate);
		String sqlFetchRows="select * from mer_accounts where 1=1 "+condition.toString();
		String sqlCountRows="select count(mid) from mer_accounts where 1=1 "+condition.toString();
		return queryForPage(sqlCountRows, sqlFetchRows, pageNo,pageSize, MerAccount.class);
	}
	
	/****
	 * 查询当月没交易，上期，本期账户余额查询方法
	 * @param bdate
	 * @return
	 */
	public List<MerAccount> queryAccountbalance( Integer bdate,String condition){
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select b.abbrev as abbrev,c.aid as mid,b.category as category, group_concat(concat((c.balance+c.all_balance),'*',c.tb_name,'*',c.rec_pay,'*',c.amt ) order by c.id separator  ',') all_balance ");
		sqlBuffer.append(" from  (select max(a.id) bq ,m.abbrev as abbrev,m.category as category,a.rec_pay as rec_pay ");
		sqlBuffer.append("from minfo m,acc_seqs a ");
		sqlBuffer.append("where a.aid = m.id ");
		if (bdate != null)
			sqlBuffer.append(" and a.tr_date<").append(bdate);
		sqlBuffer.append(" group by aid) b, acc_seqs c ");
		sqlBuffer.append(" where b.bq = c.id ");
		sqlBuffer.append(" and c.aid not in (").append(condition).append(")");
		sqlBuffer.append(" group by c.aid");
		return query(sqlBuffer.toString(),MerAccount.class);
	}
	
	public void queryMinfoForAccInfo(List<MerAccount> list){
		StringBuffer csql=new StringBuffer();
		List<MerAccount> mer=new ArrayList<MerAccount>();
		 if(  list.size()>0){
			for (MerAccount merAccount : list) {
				String mid=merAccount.getMid();
				csql.append("'").append(mid).append("',");
			}
		}else{
			return;
		}
		StringBuffer sql=new StringBuffer();
		String accType="2";
		sql.append("select m.abbrev as abbrev,a.uid as mid,m.category as category ,(a.all_balance+a.balance) as  previousBalance from acc_infos as a,minfo as m where  1=1 and a.uid=m.id");
		sql.append(" and acc_type=").append(accType);
		sql.append(" and a.uid not in (").append(csql.substring(0,csql.length()-1)).append(");");
		mer=query(sql.toString(), MerAccount.class);
		for (MerAccount merAccount : mer) {
			merAccount.setTransAmt(new Long(0));
			merAccount.setFeeAmt(0);
			merAccount.setRefAmt(new Long(0));
			merAccount.setRefFeeAmt(0);
			merAccount.setLiqAmt(new Long(0));
			merAccount.setManualAdd(new Long(0));
			merAccount.setManualSub(new Long(0));
			merAccount.setCurrentBalance(merAccount.getPreviousBalance());
			list.add(merAccount);
		}
		return;
	}
	
	/**
	 * 获取要上传到ftp的商户对账单信息(当前时间前一天的交易成功的订单)
	 * @param mid
	 * @param date
	 * @return
	 */
	public List<Hlog> madeSettlement(String mid,int date){
		String sql = "select mid,oid,mdate,amount,fee_amt,tseq,sys_date ,type,tstat from hlog where mid="+mid+" and tstat=2 and sys_date="+date;
		
		return query(sql, Hlog.class);
		
	}
	/**
	 * 获取所有要生成ftp对账单的用户
	 * @return
	 */
	public List<MidFtp> getMidFtpList(){
		String sql = "SELECT * from minfo_ftp";
		return query(sql, MidFtp.class);
		
	}
	
	/**
	 * 获取所有要生成ftp对账单的用户
	 *  @param mid
	 * @return
	 */
	public MidFtp getMidFtpByMid(String mid){
		String sql = "SELECT * from minfo_ftp where mid="+mid;
		return queryForObject(sql, MidFtp.class);
	}
	
	public RyfFtp getRyfFtpById(String id){
		String sql = "SELECT * from ryf_ftp where id="+Ryt.addQuotes(id);
		return queryForObject(sql, RyfFtp.class);
	}

	public CurrentPage<TradeStatistics> queryTradeStatisticByGid(Integer bdate,
			Integer edate, String gid, Integer pageNo,Integer pageSize) {
		// TODO Auto-generated method stub
		StringBuffer sql=new StringBuffer();
		
		StringBuffer condition=new StringBuffer();
		StringBuffer countSql=new StringBuffer();
		
		sql.append("select aa.*,gg.name as gateName from(select SUM(amount)amount,SUM(count)count,gid from trade_statistics  " +
				"where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2");
		if(StringUtils.isNotEmpty(gid)){
			condition.append(" and gid= ").append(gid);
		}
		if (bdate != null)
			condition.append(" and sys_date>=").append(bdate);
		if (edate != null)
			condition.append(" and sys_date<=").append(edate);
		condition.append(" GROUP BY gid");
		sql.append(condition).append(")aa ");
		sql.append("LEFT JOIN gate_route gg on aa.gid=gg.gid");
		countSql.append("select count(*) from (select SUM(amount)amount,SUM(count)count,gid from trade_statistics  " +
				"where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2");
		countSql.append(condition).append(")cc");
		String sqlCountTotle = " SELECT sum(dd.amount) from( " + sql.toString()+")dd";
		String sqlCountTotle1 = " SELECT sum(dd.count) from( " + sql.toString()+")dd";
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sqlCountTotle1);
		return queryForPage(countSql.toString(), sql.toString(), pageNo,pageSize, TradeStatistics.class,sumSQLMap);
	}

	public CurrentPage<TradeStatistics> queryTradeStatisticByMid(Integer bdate,
			Integer edate, String mid, Integer pageNo,Integer pageSize) {
		// TODO Auto-generated method stub
		StringBuffer sql=new StringBuffer();
		
		StringBuffer condition=new StringBuffer();
		StringBuffer countSql=new StringBuffer();
		
		sql.append("select SUM(amount)amount,SUM(count)count,mid from trade_statistics   " +
				"where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2");
		if(StringUtils.isNotEmpty(mid)){
			condition.append(" and mid= ").append(mid);
		}
		if (bdate != null)
			condition.append(" and sys_date>=").append(bdate);
		if (edate != null)
			condition.append(" and sys_date<=").append(edate);
		condition.append(" GROUP BY mid");
		sql.append(condition);
		countSql.append("select count(*) from(select SUM(amount)amount,SUM(count)count,mid from trade_statistics   " +
				"where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2");
		countSql.append(condition);
		countSql.append(")aa");
		String sqlCountTotle = " SELECT sum(aa.amount) from( " + sql.toString()+")aa";
		String sqlCountTotle11 = " SELECT sum(aa.count) from( " + sql.toString()+")aa";
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sqlCountTotle11);
		return queryForPage(countSql.toString(), sql.toString(), pageNo,pageSize, TradeStatistics.class,sumSQLMap);
	
	}

	public List<TradeStatistics> getStatisticsDetailGid( Integer bdate,
			Integer edate, String gid) {
		
		StringBuffer sql=new StringBuffer();
		sql.append("select aa.*,m.name,gg.name gateName from (select mid,gid,SUM(pay_amt)pay_amt from trade_statistics  where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2 ");
		if (bdate != null)
			sql.append(" and sys_date>=").append(bdate);
		if (edate != null)
			sql.append(" and sys_date<=").append(edate);
		if(StringUtils.isNotEmpty(gid)){
			sql.append(" and gid= ").append(gid);
		}
		sql.append(" GROUP BY mid,gid)aa ");
		sql.append(" LEFT JOIN minfo m on aa.mid=m.id ");
		sql.append(" LEFT JOIN gate_route gg on aa.gid=gg.gid");
		return query(sql.toString(), TradeStatistics.class);
		
	}
	
	public List<TradeStatistics> getStatisticsDetailMid( Integer bdate,
			Integer edate, String mid) {
		
		StringBuffer sql=new StringBuffer();
		sql.append("select aa.*,m.name,gg.name gateName from (select mid,gid,SUM(pay_amt)pay_amt from trade_statistics  where trans_mode IN (1,3,5,6,7,8,10,18) and  tstat= 2 ");
		if (bdate != null)
			sql.append(" and sys_date>=").append(bdate);
		if (edate != null)
			sql.append(" and sys_date<=").append(edate);
		if(StringUtils.isNotEmpty(mid)){
			sql.append(" and mid= ").append(mid);
		}
		sql.append(" GROUP BY mid,gid)aa ");
		sql.append(" LEFT JOIN minfo m on aa.mid=m.id ");
		sql.append(" LEFT JOIN gate_route gg on aa.gid=gg.gid");
		return query(sql.toString(), TradeStatistics.class);
	}
	 
}
