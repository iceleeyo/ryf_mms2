package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

@SuppressWarnings("rawtypes")
public class SearchFeeLiqBathDao extends PubDao{

	/**
	 * 用于根据结算类型查询 结算单
	 * @param mid           商户号
	 * @param state         结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate     查询起始日期
	 * @param endDate       查询结束日期
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int pageNo, String merType,
			String mid, int beginDate, int endDate, Integer liqgid,String gid) {
		StringBuffer feildSql = new StringBuffer("select flb.*,m.liq_obj,m.category,m.abbrev,m.gate_id,m.pbk_gate_id ");
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb,minfo m,acc_infos acc where flb.mid=m.id and m.id=acc.uid and acc.uid =acc.aid and flb.liq_date>= ");
		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
		pubSql.append(" and flb.state in(2,3) and flb.liq_gid=").append(liqgid);
		if (!Ryt.empty(merType))
			pubSql.append(" and m.category= ").append(Integer.parseInt(merType));
		if (!Ryt.empty(mid))
			pubSql.append(" and m.id= ").append(Ryt.addQuotes(mid));
		if (liqgid == 2 || liqgid == 3) {
			feildSql.append(",m.bank_acct_name,m.bank_acct");// 银行账户
		} else if (liqgid == 1 || liqgid == 4) {
			feildSql.append(",acc.aid,acc.aname");// 电银账户
		}
		else if(liqgid == 0){
			feildSql.append(",m.bank_acct_name,m.bank_acct,acc.aid,acc.aname");
		}
		StringBuffer finalsql=new StringBuffer(" select * from (select a.*,b.gid from ("+feildSql.toString() + pubSql.toString()+") as a");
		finalsql.append(" left join fee_calc_mode b on  a.mid=b.mid ");
		if(liqgid==3){
			finalsql.append(" and a.gate_id=b.gate ) as c ");
		}else{
			finalsql.append(" and a.pbk_gate_id=b.gate ) as c");
		}
		if(!Ryt.empty(gid)){
			finalsql.append(" where c.gid=").append(gid);
		}
		String sqlCountTotle = " select sum(liq_amt) from (" + finalsql.toString() + ") d";
		Map<String, String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, sqlCountTotle);
		return queryForPage("select count(*) from (" + finalsql.toString() + ") d",finalsql.toString(), pageNo,new AppParam().getPageSize(), FeeLiqBath.class,sumSQLMap);
	}

	
	/**
	 * 用于结算单查询，查询fee_liq_bath表
	 * @param mid  商户号
	 * @param state  结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate  查询起始日期
	 * @param endDate  查询结束日期
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int pageNo, String mid,int beginDate, int endDate) {
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb where flb.liq_date>= ");
		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
		pubSql.append(" and flb.state=3 and flb.mid=").append(Ryt.addQuotes(mid));
		return queryForPage("select count(*) " + pubSql.toString(),"select flb.* " + pubSql.toString(), pageNo,new AppParam().getPageSize(), FeeLiqBath.class);
	}
	/*
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public List<FeeLiqBath> queryPrintTableData(String merType, String mid,int beginDate, int endDate, Integer liqgid,String gid) {
		StringBuffer feildSql = new StringBuffer("select flb.*,m.liq_obj,m.category,m.abbrev, m.gate_id,m.pbk_gate_id");
		StringBuilder pubSql = new StringBuilder(" from fee_liq_bath flb,minfo m,acc_infos acc where flb.mid=m.id and m.id=acc.uid and acc.uid =acc.aid and flb.liq_date>= ");
		pubSql.append(beginDate).append(" and flb.liq_date<=").append(endDate);
		pubSql.append(" and flb.state in(2,3) and flb.liq_gid=").append(liqgid);
		if (!Ryt.empty(merType))
			pubSql.append(" and m.category= ").append(Integer.parseInt(merType));
		if (!Ryt.empty(mid))
			pubSql.append(" and m.id= ").append(Ryt.addQuotes(mid));
		if (liqgid == 0||liqgid == 2 || liqgid == 3) {
			feildSql.append(",m.bank_acct_name,m.bank_acct,m.bank_name,m.bank_branch ");// 银行账户
		} else if (liqgid == 1 || liqgid == 4) {
			feildSql.append(",acc.aid,acc.aname");// 电银账户
		}
		StringBuffer finalsql=new StringBuffer(" select * from (select a.*,b.gid from ("+feildSql.toString() + pubSql.toString()+") as a");
		finalsql.append(" left join fee_calc_mode b on  a.mid=b.mid ");
		if(liqgid==3){
			finalsql.append(" and a.gate_id=b.gate ) as c ");
		}else{
			finalsql.append(" and a.pbk_gate_id=b.gate ) as c");
		}
		if(!Ryt.empty(gid)){
			finalsql.append(" where c.gid=").append(gid);
		}
		return query(finalsql.toString(), FeeLiqBath.class);
	}

}
