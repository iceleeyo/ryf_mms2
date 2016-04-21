package com.rongyifu.mms.modules.accmanage.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("rawtypes")
public class SgdfQueryDao extends PubDao{
	
	@SuppressWarnings("unchecked")
	public CurrentPage<OrderInfo> queryTlogInfo(Integer pageNo,Integer pageSize,String mid,String tseq,String batchNo,String ptype,Integer mstate,Integer againPayStatus,Integer bdate,Integer edate){
		StringBuffer sql=new StringBuffer("select tlog.* from ");
		sql.append(" ").append(Constant.TLOG).append(" tlog");
		sql.append(" left join ").append("minfo minfo");
		sql.append(" on tlog.mid= minfo.id");
		sql.append(" ").append("where 1=1  ");
		if(null!=mstate)sql.append(" ").append("and minfo.mstate=").append(mstate);
		sql.append(" And minfo.is_sgdf=1");
		if(!Ryt.empty(mid))sql.append(" ").append("and tlog.mid=").append(Ryt.addQuotes(mid));
		if(!Ryt.empty(tseq))sql.append(" ").append("and tlog.tseq=").append(Ryt.addQuotes(tseq));
		if(!Ryt.empty(batchNo))sql.append(" ").append("and tlog.p8=").append(Ryt.addQuotes(batchNo));
		sql.append(" ").append("and tlog.sys_date>=").append(bdate);
		sql.append(" ").append("and tlog.sys_date<=").append(edate);
		if(null!=againPayStatus){
			sql.append(" ").append("and tlog.againPay_status=").append(againPayStatus);
		}else{
			sql.append(" ").append("and (tlog.againPay_status<>0");
			sql.append(" ").append("and tlog.againPay_status<>1 )");
		}
		if(!Ryt.empty(ptype)){
			sql.append(" ").append(" and tlog.type=").append(ptype);
		}else{
			sql.append(" ").append("and (tlog.type=").append(Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
			sql.append(" or ").append(" tlog.type=").append(Constant.TlogTransType.PAYMENT_FOR_PUBLIC);
			sql.append(")");
		}
		String hlogSql=sql.toString().replace(Constant.TLOG, Constant.HLOG);
		sql.append(" union all ").append(hlogSql);
		
		StringBuffer sqlCount=new StringBuffer("select count(1) ");
		sqlCount.append(" from (").append(sql).append(") as orderInfo");
		
		StringBuffer amtSumSql=new StringBuffer();
		amtSumSql.append(" SELECT -sum(ABS(orderInfo.amount)) from (");
		amtSumSql.append(sql).append(") as orderInfo ");
		StringBuffer sysAtmFeeSumSql=new StringBuffer(" SELECT sum(orderInfo.fee_amt) from (");
		sysAtmFeeSumSql.append(sql).append(") as orderInfo");
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql.toString());
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql.toString());

		Map<String, String> params = new HashMap<String, String>();
		String querySql="select * from ("+sql.toString()+") all_log order by all_log.sys_date desc,all_log.sys_time desc";
		params.put("sql", querySql);
		params.put("sqlCount", sqlCount.toString());
		params.put("amtSumSql", amtSumSql.toString());
		params.put("sysAtmFeeSumSql", sysAtmFeeSumSql.toString());
		LogUtil.printInfoLog("SgdfQueryDao", "queryTlogInfo", "query sqls ",
				params);
		return queryForPage(sqlCount.toString(), sql.toString(), pageNo,
				pageSize, OrderInfo.class, sumSQLMap);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<OrderInfo> queryDownData(String mid,String tseq,String batchNo,String ptype,Integer mstate,Integer againPayStatus,Integer bdate,Integer edate){
		StringBuffer sql=new StringBuffer("select tlog.*,minfo.name from ");
		sql.append(" ").append(Constant.TLOG).append(" tlog");
		sql.append(" left join ").append("minfo minfo");
		sql.append(" on tlog.mid= minfo.id");
		sql.append(" ").append("where 1=1 ");
		if(null!=mstate)sql.append(" ").append("and minfo.mstate=").append(mstate);
		sql.append(" and minfo.is_sgdf=1");
		if(!Ryt.empty(mid))sql.append(" ").append("and tlog.mid=").append(Ryt.addQuotes(mid));
		if(!Ryt.empty(tseq))sql.append(" ").append("and tlog.tseq=").append(Ryt.addQuotes(tseq));
		if(!Ryt.empty(batchNo))sql.append(" ").append("and tlog.p8=").append(Ryt.addQuotes(batchNo));
		sql.append(" ").append("and tlog.sys_date>=").append(bdate);
		sql.append(" ").append("and tlog.sys_date<=").append(edate);
		if(null!=againPayStatus){
			sql.append(" ").append("and tlog.againPay_status=").append(againPayStatus);
		}else{
			sql.append(" ").append("and (tlog.againPay_status<>").append(Constant.SgDfTstat.TSTAT_DEFAULT);
			sql.append(" ").append("and tlog.againPay_status<>").append(Constant.SgDfTstat.TSTAT_INIT).append(")");
		}
		if(!Ryt.empty(ptype)){
			sql.append(" ").append(" and tlog.type=").append(ptype);
		}else{
			sql.append(" ").append("and (tlog.type=").append(Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
			sql.append(" ").append("or tlog.type=").append(Constant.TlogTransType.PAYMENT_FOR_PUBLIC);
			sql.append(")");
		}
		String hlogSql=sql.toString().replace(Constant.TLOG, Constant.HLOG);
		sql.append(" union all ").append(hlogSql);
		return query(sql.toString(), OrderInfo.class);
	}
}
