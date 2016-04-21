package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TransferMoney;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class QuerytransferDao extends PubDao {
	private PubDao dao = new PubDao();

	/*
	 * 划款结果查询
	 */

	@SuppressWarnings("unchecked")
	public CurrentPage<TransferMoney> querytransfer(Integer pageNo,
			Integer pageSize, String mid, Integer bdate, Integer edate,
			String batchNo, Integer tstat,Integer type,Integer gateRouteId) {

		StringBuffer condition = getquerytransferSql(mid, bdate, edate,batchNo, tstat,type,gateRouteId);
		String sqlCount = " SELECT  COUNT(b.tseq) from  ("+ condition.toString() + ") as b";
		String amtSumSql = " SELECT sum(ABS(b.amount)) from ("+ condition.toString() + ") as b";
		String sysAtmFeeSumSql = " SELECT sum(b.feeAmt) from ("+ condition.toString() + ") as b";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount, condition.toString(), pageNo, pageSize,TransferMoney.class, sumSQLMap);
	}

	private StringBuffer getquerytransferSql(String mid, Integer bdate,
			Integer edate, String batchNo, Integer tstat,Integer type,Integer gateRouteId) {
		StringBuffer querytransferSql = new StringBuffer();
		querytransferSql.append("select a.tseq as tseq,a.mid as mid,a.p3 as bankNo,a.p9 as batch ,");
		querytransferSql.append("a.sys_date as liqDate ,a.p4 as bankName,a.p5 as bankBranch ,");
		querytransferSql.append("a.p2 as bankAcctName,a.p1 as bankAcct,");
		querytransferSql.append("a.tstat as tstat ,a.amount as amount,a.gate as gate ,a.gid as gid,");
		querytransferSql.append("a.error_msg as errorMsg,a.error_code as errorCode,a.fee_amt as feeAmt,");
		querytransferSql.append("a.type as type,m.abbrev as name");//abbrev还是从minfo表取的
		querytransferSql.append(" from hlog a ,minfo m ");
		querytransferSql.append(" where a.mid=m.id  and a.data_source=8 ");
		querytransferSql.append(" and a.sys_date>=").append(bdate);
		querytransferSql.append(" and a.sys_date<=").append(edate);
		if (!Ryt.empty(mid)) {
			querytransferSql.append(" and a.mid=").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(batchNo)) {
			querytransferSql.append(" and a.p9=").append(
					Ryt.addQuotes(batchNo));
		}
		if (tstat != null) {
			querytransferSql.append(" and a.tstat=").append(tstat);
		}
		if(type!=null){
			querytransferSql.append(" and a.type=").append(type);
		}
		if(gateRouteId!=null){
			querytransferSql.append(" and a.gid=").append(gateRouteId);
		}

		StringBuffer querytransferSql2 = new StringBuffer();
		querytransferSql2.append("select a.tseq as tseq,a.mid as mid,a.p3 as bankNo,a.p9 as batch ,");
		querytransferSql2.append("a.sys_date as liqDate ,m.abbrev as name ,m.bank_name as bankName ,");
		querytransferSql2.append("m.bank_acct_name as bankAcctName,m.bank_acct as bankAcct,");
		querytransferSql2.append("a.tstat as tstat ,a.amount as amount,a.gate as gate ,a.gid as gid,");
		querytransferSql2.append("a.error_msg as errorMsg,a.error_code as errorCode,a.fee_amt as feeAmt,");
		querytransferSql2.append("a.type as type,m.bank_branch as bankBranch");
		querytransferSql2.append(" from tlog a ,minfo m ");
		querytransferSql2.append(" where a.mid=m.id and a.data_source=8");
		querytransferSql2.append(" and a.sys_date>=").append(bdate);
		querytransferSql2.append(" and a.sys_date<=").append(edate);
		if (!Ryt.empty(mid)) {
			querytransferSql2.append(" and a.mid=").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(batchNo)) {
			querytransferSql2.append(" and a.p9=").append(
					Ryt.addQuotes(batchNo));
		}
		if (tstat != null) {
			querytransferSql2.append(" and a.tstat=").append(tstat);
		}
		if(type!=null){
			querytransferSql2.append(" and a.type=").append(type);
		}
		if(gateRouteId!=null){
			querytransferSql2.append(" and a.gid=").append(gateRouteId);
		}
		querytransferSql.append(" union all ").append(querytransferSql2);
		return querytransferSql;

	}
	
	/**
	 * 查询单条银企直连网关信息
	 * @param gid 网关号
	 * @return
	 */
	public B2EGate getOneB2EGate(int gid) {
		StringBuffer sql=new StringBuffer("select * from  b2e_gate where gid =");
		sql.append(gid);
	
		return queryForObject(sql.toString(), B2EGate.class);
	}
	
	/****
	 * 查询orderInfo 
	 * @param mid
	 * @param oid
	 * @param orgOid
	 * @param tseq
	 * @return
	 * @throws Exception 
	 */
	public List<Object>  queryOrder(String mid,String oid,String orgOid,String tseq) throws Exception{
		List<Object> ols=new ArrayList<Object>();
		String table=Constant.TLOG;
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ").append(table).append(" where 1=1 ");
		if(!Ryt.empty(tseq))sql.append(" and  tseq=").append(Ryt.addQuotes(tseq));
		if(!Ryt.empty(mid))sql.append(" and mid=").append(Ryt.addQuotes(mid));
		if(!Ryt.empty(oid) && !Ryt.empty(orgOid))sql.append(" and (oid=").append(Ryt.addQuotes(oid)).append(" or oid=").append(Ryt.addQuotes(orgOid)).append(") ");
		if(!Ryt.empty(oid))sql.append(" and oid=").append(Ryt.addQuotes(oid));
		if(!Ryt.empty(orgOid))sql.append(" and oid =").append(Ryt.addQuotes(orgOid));
		List<OrderInfo> ol=dao.query(sql.toString(), OrderInfo.class);
		if(ol.size()==1){
			LogUtil.printInfoLog("QuerytransferDao", "queryOrder", "tlogSql:"+sql.toString());
			ols.add(ol.get(0));
			ols.add(table);
		}else{
			String hsql=sql.toString().replace("tlog", "hlog");
			List<OrderInfo> ol2=dao.query(hsql.toString(), OrderInfo.class);
			if(ol2.size()==1){
				LogUtil.printInfoLog("QuerytransferDao", "queryOrder", "hlogSql:"+hsql.toString());
				ols.add(ol2.get(0));
				ols.add(Constant.HLOG);
			}else{
				throw new Exception("查不到记录！");
			}
		}
		return ols;
	}
	
}
