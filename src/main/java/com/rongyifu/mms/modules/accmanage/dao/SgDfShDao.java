package com.rongyifu.mms.modules.accmanage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SgDfShDao extends PubDao{
	/**
	 * 查询代发请求银行失败数据----手工付付审核
	 * @param pagNo
	 * @param pageSize
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param tseq
	 * @param mstate
	 * @param bdate
	 * @param edate
	 * @return
	 */
	
	public CurrentPage<OrderInfo> queryDataForReqFail(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String tseq,Integer mstate,Integer bdate,Integer edate){
		String dataSources=StringUtils.join(new Integer[]{Constant.DataSource.TYPE_DFDKINTERFACE,
				Constant.DataSource.TYPE_DFDKMMS,
				Constant.DataSource.TYPE_AUTODF,
				Constant.DataSource.TYPE_FTPDF
		}, ",");
		StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
		if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
		if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
		if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
		tSql.append(" and tlog.tstat =").append(Constant.PayState.FAILURE);//状态为支付失败  3
		tSql.append(" and tlog.data_source in (").append(dataSources).append(")"); //数据来源标识  1,5,9
		tSql.append(" and tlog.againPay_status=").append(Constant.SgDfTstat.TSTAT_SQSUC);
		if(bdate!=null&&edate!=null)	
			tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
		if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
		StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
		StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
		System.out.println(sqlCondition);
		StringBuffer sql=new StringBuffer("select * from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
		if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		StringBuffer sqlCount =new StringBuffer("select count(*) from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
		sqlCount.append(" order by a.sys_date desc ,a.sys_time desc");
		String amtSumSql = " SELECT -sum(ABS(a.amount)) from ("
				+ sqlCondition.toString() + ") as a";
		String sysAtmFeeSumSql = " SELECT sum(a.fee_amt) from ("
				+ sqlCondition.toString() + ") as a";
		Map<String, String> sumSQLMap = new HashMap<String, String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		return queryForPage(sqlCount.toString(),sql.toString(), pagNo, pageSize,OrderInfo.class,sumSQLMap) ;
	}
	
	/**
	 * 通过oid查询tlog hlog    流水
	 * @param mid
	 * @param oid
	 * @return
	 */
	public Object[] queryOrderByOid(String mid, String oid) {
		String table = Constant.HLOG;
		TlogBean order=null;
		try {
			order = queryForObjectThrowException("select * from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid),TlogBean.class);
		} catch (Exception e) {
			table = Constant.TLOG;
			order = queryForObjectThrowException("select * from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid),TlogBean.class);
		}
		return new Object[]{table, order};
	}
	
	/**
	 * 原订单移到备份表（ blog表）
	 * @param mid
	 * @param oid
	 * @return
	 */
	public String insertOrderToBlog(TlogBean t){
		StringBuffer sql=new StringBuffer("insert into "+Constant.BLOG+" select * from "+Constant.HLOG+" where mid="+Ryt.addQuotes(t.getMid())+" and tseq="+Ryt.addQuotes(t.getTseq().toString()));
		sql.append(" union all ");
		sql.append("select * from ");
		sql.append(Constant.TLOG);
		sql.append("  where mid=").append(Ryt.addQuotes(t.getMid()));
		sql.append("  and tseq=").append(Ryt.addQuotes(t.getTseq().toString()));
		return sql.toString();
	}
	
	
	/***
	 * 生产新的订单
	 * @param ord
	 * @return
	 */
	public String insertOrderToTlog(TlogBean tb){
		String sql="";
		try {
			tb.setTstat(Constant.PayState.INIT);//生产新的流水，订单状态为待支付
			Integer sys_time=DateUtil.getCurrentUTCSeconds();
			Integer dataSource=tb.getData_source();
			Integer today=DateUtil.today();
			if(dataSource==5){//原交易为管理平台代付
				tb.setData_source(1); //需设置数据源为接口代付
			}
			tb.setSys_date(today);
			tb.setSys_time(sys_time);
			tb.setAgainPay_date(today);//设置审核日期
			tb.setAgainPay_status(Constant.SgDfTstat.TSTAT_SHSUC);//设置审核状态
			tb.setBk_recv(null);
			tb.setBk_resp(null);
			tb.setBk_seq1(null);
			tb.setBk_seq2(null);
			tb.setError_code(null);
			tb.setError_msg(null);
			sql = new SqlGenerator().generateInsertSql(tb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	
	
	/**
	 * 删除hlog，hlog表的订单
	 * @param tesq
	 * @param table
	 * @return
	 */
	public String deleteOrder(String tseq,String table){
		StringBuffer sql=new StringBuffer("delete from "+table+" where tseq="+Ryt.addQuotes(tseq));
		return sql.toString();
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
	public List<OrderInfo>  downSGDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate){
		String dataSources=StringUtils.join(new Integer[]{Constant.DataSource.TYPE_DFDKINTERFACE,
				Constant.DataSource.TYPE_DFDKMMS,
				Constant.DataSource.TYPE_AUTODF,
				Constant.DataSource.TYPE_FTPDF
		}, ",");
		Integer sysDate=DateUtil.today();
		StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
		if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
		if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
		if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
		tSql.append(" and tlog.data_source in(").append(dataSources).append(")");
		tSql.append(" and tlog.againPay_status in (").append(Constant.SgDfTstat.TSTAT_SHSUC).append(",");
		tSql.append(Constant.SgDfTstat.TSTAT_SHFAIL).append(")");//再再次代付处理标识：3.审核成功4.撤销
		tSql.append("  and tlog.againPay_date=").append(sysDate);//再次代付发起日期
		if(ptype==0)
			tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");
		else 
			tSql.append(" and tlog.type=").append(ptype).append(" ");
		StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
		StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
		StringBuffer sql=new StringBuffer("select a.*,m.*,c.cancel_state,c.cancel_reason from (").append(sqlCondition).append(") a");
		sql.append("  left join cancel_log c on a.tseq=c.tseq ,minfo m where a.mid=m.id   ");
		if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
		sql.append(" order by a.sys_date desc ,a.sys_time desc");
		return query(sql.toString(), OrderInfo.class);
	}

	
	public String updateBatchProcessNum(String key, Integer affectNum) {
		String updateBatchProcessNum="update batch_log set process_num=process_num-"+affectNum+" where batch_id="+Ryt.addQuotes(key);
		return updateBatchProcessNum;
	}
	
	/***
	 * 执行撤销更新数据操作
	 * @param os
	 * @return
	 */
	public boolean batchRefuse(List<OrderInfo> os) {
		List<String> sqlList=new ArrayList<String>();
		int today=DateUtil.today();
		if(os.size()==0)return false;
		for (OrderInfo orderInfo : os) {
			StringBuffer modifyTstat=new StringBuffer("update ");
			modifyTstat.append(Constant.HLOG);
			modifyTstat.append(" set againPay_status=");
			modifyTstat.append(Constant.SgDfTstat.TSTAT_SHFAIL);
			modifyTstat.append(" , againPay_date=").append(today);
			modifyTstat.append("  where mid=").append(Ryt.addQuotes(orderInfo.getMid()));
			modifyTstat.append(" and tseq=").append(Ryt.addQuotes(orderInfo.getTseq()));
			sqlList.add(modifyTstat.toString());
		}
		String[] sqls=sqlList.toArray(new String[sqlList.size()]);
		boolean result=batchUpdate(sqls,sqls.length,0);
		return result;
	}
	
	/***
	 * 修改订单状态
	 * modify fields tstat,error_msg
	 * @param tlog
	 * @return
	 */
	public boolean updateTstat(TlogBean tlog){
		boolean resp=false;
		String oid=tlog.getOid();
		String mid=tlog.getMid();
		String sysDate=tlog.getSys_date().toString();
		StringBuffer sql=new StringBuffer("update ").append(Constant.HLOG).append(" set tstat=").append(tlog.getTstat());
		if(tlog.getTstat()==PayState.FAILURE){
			sql.append(",").append(" error_msg=").append(Ryt.addQuotes(tlog.getError_msg()));
		}
		sql.append(" where  mid=").append(Ryt.addQuotes(mid));
		sql.append(" and oid=").append(Ryt.addQuotes(oid));
		sql.append(" and sys_date=").append(sysDate).append(";");
		if(update(sql.toString())==1){
			resp=true;
		}
		return resp;
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
			LogUtil.printErrorLog("SgDfShDao", "batchUpdate", "batchTransaction return null...,\nsqls:\n"+printlnSql);
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
				LogUtil.printErrorLog("SgDfShDao", "batchUpdate", "actualAffect > exceptAffect...",m);
				return false;
			}else{
				return batchUpdate(l.toArray(new String[l.size()]),exceptAffect,actualAffect);
			}
		}
		return true ;
	}
	
	/***
	 * 执行审核操作的Sql
	 * 录入blogSql
	 * 删除tlogSql
	 * 录入tlogSql.
	 * @param os
	 * @return
	 */
	public void batchSh(List<TlogBean> os)throws Exception{
		List<String> sqlList=new ArrayList<String>();
		for (TlogBean tlog : os) {
			String sql1=insertOrderToBlog(tlog);
			String sql2=deleteOrder(tlog.getTseq().toString(),Constant.HLOG);
			String sql3=sql2.replace(Constant.HLOG, Constant.TLOG);
			String tseq=Ryt.genOidBySysTime();
			tlog.setTseq(new Long(tseq));//生成新的流水号
			String sql4=insertOrderToTlog(tlog).replace(Constant.TLOG, Constant.HLOG);
			sqlList.add(sql1);
			sqlList.add(sql2);
			sqlList.add(sql3);	
			sqlList.add(sql4);
		}
		
		batchSqlTransaction2(sqlList);
	}
}
