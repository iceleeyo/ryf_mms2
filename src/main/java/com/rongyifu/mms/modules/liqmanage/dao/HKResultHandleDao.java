package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.ArrayList;
import java.util.List;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class HKResultHandleDao  extends PubDao{
	
	public CurrentPage<OrderInfo> queryDataForAutoSettleAmt(Integer pagNo,Integer num, String uid, String liqBatch, Integer state,Integer bdate, Integer edate) {
		StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
		if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
		if(state!=0){
			tSql.append(" and tlog.tstat =").append(state);//代付状态
		}else{
			tSql.append(" and tlog.tstat =").append(Constant.PayState.WAIT_PAY);//代付状态
		}
		tSql.append(" and tlog.data_source in (8) "); //追加数据来源状态  data_source   只处理自动结算订单
		if(!Ryt.empty(liqBatch)){ //追加结算批次号
			tSql.append(" and tlog.p9=").append(liqBatch);
		}
		if(bdate!=null&&edate!=null)	
			tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
		StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
		StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
		StringBuffer sql=new StringBuffer("select * from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		StringBuffer sqlCount =new StringBuffer("select count(*) from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
		sqlCount.append(" order by a.sys_date desc ,a.sys_time desc");
		LogUtil.printInfoLog("HKResultHandleDao", "queryDataForAutoSettleAmt", sql.toString());
		return queryForPage(sqlCount.toString(),sql.toString(), pagNo, num,OrderInfo.class) ;
	}
	
	/****
	 * 自动结算划款结果处理 下载方法
	 * @param uid
	 * @param trans_flow
	 * @param state
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public List<OrderInfo> downAutoSettlementData(String uid,
			String liqBatch, Integer state, Integer bdate, Integer edate) {
		StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
		if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
		if(state !=0 )tSql.append(" and tlog.tstat =").append(state);//追加订单状态
		if(!Ryt.empty(liqBatch))tSql.append(" and tlog.p9=").append(liqBatch);
		if(bdate!=null&&edate!=null)	
			tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
		tSql.append(" and tlog.data_source in (8)");//追加数据来源  data_source 查询条件
		StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
		StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
		StringBuffer sql=new StringBuffer("select *  from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		LogUtil.printInfoLog("HKResultHandleDao", "downAutoSettlementData", sql.toString());
		return query(sql.toString(), OrderInfo.class);
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
		List<OrderInfo> ol=query(sql.toString(), OrderInfo.class);
		if(ol.size()==1){
			LogUtil.printInfoLog("QuerytransferDao", "queryOrder", "tlogSql:"+sql.toString());
			ols.add(ol.get(0));
			ols.add(table);
		}else{
			String hsql=sql.toString().replace("tlog", "hlog");
			List<OrderInfo> ol2=query(sql.toString(), OrderInfo.class);
			if(ol2.size()==1){
				LogUtil.printInfoLog("QuerytransferDao", "queryOrder", "hlogSql:"+sql.toString());
				ols.add(ol2.get(0));
				ols.add(Constant.HLOG);
			}else{
				throw new Exception("查不到记录！");
			}
		}
		return ols;
	}
}
