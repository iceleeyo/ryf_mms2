package com.rongyifu.mms.modules.accmanage.dao;

import java.util.List;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
/*****
 * 账户管理下 手工同步代付结果Dao模块
 * @author shdy
 *
 */
@SuppressWarnings("rawtypes")
public class SGSyncDFResultDao extends PubDao {
	
	/**
	 * 手工代付结果同步 - 查询
	 * @param pagNo
	 * @param pageSize
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param tseq
	 * @param mstate
	 * @param state
	 * @param gate
	 * @param bdate
	 * @param edate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CurrentPage<OrderInfo> queryDataForSGSYNC(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate){
		// 查询字段
		String fields = "tseq, type, mid, amount, fee_amt, pay_amt, tstat, gate, gid, p1, p2, p8, sys_date, sys_time, oid, mdate"; 
		// 查询条件
		StringBuffer where = new StringBuffer();
		where.append(" where sys_date >= ").append(bdate).append(" and sys_date <= ").append(edate);
		where.append("   and data_source in (1,5,7,9,10)"); //追加数据来源状态  data_source
		
		if (!Ryt.empty(trans_flow))
			where.append(" and p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
		if(!Ryt.empty(tseq))
			where.append(" and tseq='").append(Ryt.sql(tseq)).append("' ");
		if (!Ryt.empty(uid))
			where.append(" and mid='").append(Ryt.sql(uid)).append("' ");
		if(state!=0){
			where.append(" and tstat =").append(state);//代付状态
		}else{
			where.append(" and tstat =").append(Constant.PayState.WAIT_PAY);//代付状态
		}
		if (gate!=0)
			where.append(" and gid=").append(gate);
		if(ptype==0)
			where.append(" and type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");
		else 
			where.append(" and type=").append(ptype).append(" ");
		
		StringBuffer sqlPart = new StringBuffer();
		sqlPart.append(" from (");
		sqlPart.append(" select " + fields + " from tlog " + where);
		sqlPart.append(" union all ");
		sqlPart.append(" select " + fields + " from hlog " + where);
		sqlPart.append(" ) a,minfo m where a.mid=m.id");
		if (mstate != null)
			sqlPart.append(" and m.mstate=").append(mstate);
		
		// 查询sql
		String querySql = "select " + fields + sqlPart + " order by a.sys_date desc ,a.sys_time desc";
		// 统计sql
		String countSql = "select count(0) " + sqlPart;
		
		return queryForPage(countSql, querySql, pagNo, pageSize,OrderInfo.class);
	}
	
	
	/***
	 * 手工同步代付结果 - 下载
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param tseq
	 * @param mstate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrderInfo>  downSGSYNCDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate){
		// 查询字段
		String fields = "tseq, type, mid, amount, fee_amt, pay_amt, tstat, gate, gid, p1, p2, p8, sys_date, sys_time"; 
		// 查询条件
		StringBuffer where = new StringBuffer();
		where.append(" where sys_date >= ").append(bdate).append(" and sys_date <= ").append(edate);
		where.append("   and data_source in (1,5,7,9,10)"); //追加数据来源状态  data_source
		
		if (!Ryt.empty(trans_flow))
			where.append(" and p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
		if(!Ryt.empty(tseq))
			where.append(" and tseq='").append(Ryt.sql(tseq)).append("' ");
		if (!Ryt.empty(uid))
			where.append(" and mid='").append(Ryt.sql(uid)).append("' ");
		if(state!=0){
			where.append(" and tstat =").append(state);//代付状态
		}else{
			where.append(" and tstat =").append(Constant.PayState.WAIT_PAY);//代付状态
		}
		if (gate!=0)
			where.append(" and gid=").append(gate);
		if(ptype==0)
			where.append(" and type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");
		else 
			where.append(" and type=").append(ptype).append(" ");
		
		StringBuffer querySql = new StringBuffer();
		querySql.append("select " + fields + " from (");
		querySql.append(" select " + fields + " from tlog " + where);
		querySql.append(" union all ");
		querySql.append(" select " + fields + " from hlog " + where);
		querySql.append(" ) a,minfo m where a.mid=m.id");
		if (mstate != null)
			querySql.append(" and m.mstate=").append(mstate);
		querySql.append(" order by a.sys_date desc ,a.sys_time desc");

		
		return query(querySql.toString(), OrderInfo.class);
	}
	
	public String[] queryOrderByOid(String mid, String oid) {
		String tseq = null;
		String table = Constant.HLOG;
		try {
			tseq = queryForStringThrowException("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid));
		} catch (Exception e) {
			table = Constant.TLOG;
			tseq = queryForStringThrowException("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid));
		}
		return new String[]{table, tseq};
	}
}
