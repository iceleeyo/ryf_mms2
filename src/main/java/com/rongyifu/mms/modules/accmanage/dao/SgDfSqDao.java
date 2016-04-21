package com.rongyifu.mms.modules.accmanage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
/****
 * 手工代付申请数据库操作类
 * @author shdy
 *
 */
@SuppressWarnings("rawtypes")
public class SgDfSqDao extends PubDao {

	/**
	 * 查询代发请求银行失败数据 -管理平台 ewp接口
	 * 
	 * @param uid 用户ID
	 * @param num 条数
	 * @param trans_flow 批次号 查询条件 订单状态（tlog/hlog表） 交易失败的数据|重复失败的数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CurrentPage<OrderInfo> queryDataForReqFail(Integer pageNo,
			Integer pageSize, String uid, String trans_flow, Integer ptype,
			String tseq, Integer mstate, Integer bdate, Integer edate) {
		String dataSources = StringUtils.join(
				new Integer[] { Constant.DataSource.TYPE_DFDKINTERFACE,
						Constant.DataSource.TYPE_DFDKMMS,
						Constant.DataSource.TYPE_FTPDF,
						Constant.DataSource.TYPE_AUTODF }, ",");

		StringBuffer tSql = new StringBuffer("select * from tlog tlog left join minfo minfo on tlog.mid=minfo.id where minfo.is_sgdf=1 ");
		if (mstate != null)
			tSql.append(" and minfo.mstate=").append(mstate);
		if (!Ryt.empty(trans_flow))
			tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");// 电银交易批次号
		if (!Ryt.empty(tseq))
			tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
		if (!Ryt.empty(uid))
			tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
		tSql.append(" and tlog.tstat =").append(Constant.PayState.FAILURE);// 状态为代付交易失败
		tSql.append(" and tlog.data_source in (").append(dataSources).append(")"); // 数据来源标识 1,5,9，7
		tSql.append(" and tlog.againPay_status in(").append(Constant.SgDfTstat.TSTAT_DEFAULT).append(",").append(Constant.SgDfTstat.TSTAT_INIT).append(")");
		if (bdate != null && edate != null)
			tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
		
		if (ptype == 0)
			tSql.append(" and tlog.type in ("+ Constant.TlogTransType.PAYMENT_FOR_PRIVATE + ","+ Constant.TlogTransType.PAYMENT_FOR_PUBLIC + ")");
		else 
			tSql.append(" and tlog.type=").append(ptype).append(" ");
		StringBuffer hlog = new StringBuffer(tSql.toString().replace("tlog","hlog"));
		StringBuffer sqlCondition = tSql.append(" union all ").append(hlog);
		StringBuffer sql = new StringBuffer("select * from (").append(sqlCondition).append(") a ");
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		StringBuffer sqlCount = new StringBuffer("select count(*) from (").append(sqlCondition).append(") a ");
		sqlCount.append(" order by a.sys_date desc ,a.sys_time desc");
		String amtSumSql = " SELECT -sum(ABS(a.amount)) from ("+ sqlCondition.toString() + ") as a";
		String sysAtmFeeSumSql = " SELECT sum(a.fee_amt) from (" + sqlCondition.toString() + ") as a";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);

		Map<String, String> params = new HashMap<String, String>();
		params.put("sql", sql.toString());
		params.put("sqlCount", sqlCount.toString());
		params.put("amtSumSql", amtSumSql.toString());
		params.put("sysAtmFeeSumSql", sysAtmFeeSumSql.toString());
		LogUtil.printInfoLog("SGDFSHDao", "queryDataForReqFail", "query sqls ",
				params);
		return queryForPage(sqlCount.toString(), sql.toString(), pageNo,
				pageSize, OrderInfo.class, sumSQLMap);
	}
	
	/***
	 * 下载当日手工代付提交成功的数据
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param tseq
	 * @param mstate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrderInfo>  downSGDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate){
		Integer sysDate=DateUtil.today();
		StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
		if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
		if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
		if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
//		tSql.append(" and tlog.tstat =").append(Constant.PayState.FAILURE);//状态为交易失败  4		     
		tSql.append(" and tlog.data_source in(").append("1,5,9,7)");
		tSql.append(" and tlog.againPay_status in (3,4) ");//再次代付处理标识：3.申请通过（已发起代付）4.撤销再次代付
		tSql.append("  and tlog.againPay_date=").append(sysDate);//再次代付发起日期
		if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
		StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
		StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
		StringBuffer sql=new StringBuffer("select a.*,m.*,c.cancel_state,c.cancel_reason from (");
		sql.append(sqlCondition);
		sql.append(") a left join cancel_log c on a.tseq=c.tseq ,minfo m where a.mid=m.id   ");
		if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		return query(sql.toString(), OrderInfo.class);
	}
	
	/***
	 * 拼接修改再次支付申请标识
	 * @param table
	 * @param orderInfo
	 * @return
	 */
	public String genModifySql(OrderInfo orderInfo,String table,Integer againPayStatus){
		StringBuffer sql=new StringBuffer("update  ").append(table);
		sql.append(" set againPay_status=").append(againPayStatus);
		sql.append(" where  tseq =").append(Ryt.addQuotes(orderInfo.getTseq())).append("");
		sql.append(" and mid=").append(Ryt.addQuotes(orderInfo.getMid()));
		sql.append(" and mdate=").append(orderInfo.getMdate());
		sql.append(";");
		return sql.toString();
	}
	
	/***
	 * 批量申请数据操作
	 * @param os
	 * @param result
	 * @return
	 */
	public boolean batchSq(List<OrderInfo> os) {
		String descStr="批量申请操作";
		List<String> sqls=new ArrayList<String>();
		String table=Constant.HLOG;
		String sql=null;
		for (OrderInfo orderInfo : os) {
			sql=genModifySql(orderInfo,table,Constant.SgDfTstat.TSTAT_SQSUC);
			sqls.add(sql);
		}
		
		String[] batchSql=sqls.toArray(new String[sqls.size()]);
		boolean result=batchUpdate(batchSql,sqls.size(),0);
		saveOperLog("手工代付申请操作", descStr+",操作成功");
		
		return result;
	}
	
	/***
	 * 批量拒绝数据操作
	 * @param os
	 * @param result
	 * @return
	 */
	public boolean batchRefuse(List<OrderInfo> os ){
		String descStr="批量申请操作"; 
		List<String> sqls=new ArrayList<String>();
		String table=Constant.HLOG;
		String sql=null;
		for (OrderInfo orderInfo : os) {
			sql=genModifySql(orderInfo,table,Constant.SgDfTstat.TSTAT_SQFAIL);
			sqls.add(sql);
		}
		String[] batchSqlH=sqls.toArray(new String[sqls.size()]);
		 boolean result=batchUpdate(batchSqlH,sqls.size(),0);
		saveOperLog("手工代付撤销操作", descStr+",操作成功");
		return result;
	}
	
	/***
	 * 批量执行UpdateSql
	 * @param sqls
	 * @param exceptAffect  sql.size
	 * @param actualAffect  default 0
	 * @return
	 */
	public boolean batchUpdate(String[] sqls,int exceptAffect,int actualAffect){
		int [] affectLines=batchSqlTransaction(sqls);
		String printlnSql=StringUtils.join(sqls,"\n");
		if(affectLines==null){
			LogUtil.printErrorLog("SgDfSqDao", "batchUpdate", "batchTransaction return null...,\nsqls:\n"+printlnSql);
			return false;
		}
		int sqlSize=sqls.length;
		
		List<String> l=new ArrayList<String>();
		int i=0;
		for (int affectLine : affectLines) {
			if(affectLine==0){
				String sql=sqls[i];
				if(sql.contains(Constant.TLOG)){
					sql=sql.replace(Constant.TLOG, Constant.HLOG);
				}else{
					sql=sql.replace(Constant.HLOG, Constant.TLOG);
				}
				i++;	
				l.add(sql);
			}else{
				sqlSize--;
				actualAffect++;
			}
		}
		actualAffect+=l.size();
		if(sqlSize!=0){
			if(exceptAffect<actualAffect){
				Map<String, String> m=new HashMap<String, String>();
				m.put("actualAffect", String.valueOf(actualAffect));
				m.put("exceptAffect", String.valueOf(exceptAffect));
				m.put("printlnSql", printlnSql);
				LogUtil.printErrorLog("SgDfSqDao", "batchUpdate", "actualAffect > exceptAffect...",m);
				return false;
			}else{
				return batchUpdate(l.toArray(new String[l.size()]),exceptAffect,actualAffect);
			}
		}
		return true ;
	}
}
