package com.rongyifu.mms.dao;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.BkAccount;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("unchecked")
public class AutoSettlementDao extends PubDao {

	// 添加银行
	public int addHoldBk(BkAccount bkAccount)throws Exception {
		
		return saveObject(bkAccount);
	}
	/**
	 * 银行卡号是否存在
	 * @param accNo
	 * @return
	 */
    public int bkNoIsExist(String accNo){
    	String sql_check = "select count(id) from bk_account where acc_no=?";
		return queryForInt(sql_check, new Object[]{accNo});
    }
	// 修改银行卡号
	public void alterAccNo(String oldNo, String newNo, String bkType)
			throws Exception {

		Object[] obj = new Object[] { newNo, bkType, oldNo };

		int[] type = new int[] { Types.VARCHAR, Types.TINYINT, Types.VARCHAR };

		String sql = "update bk_account set Acc_no=?,bk_type=? where Acc_no=?";

	    update(sql, obj, type);
	}
	// 获得账号下拉列表
	public Map<String, String> getAcc() {
		String sql = "select acc_no,acc_no from bk_account where acc_no !='' and acc_no !=0";
		Map<String, String> map =queryToMap(sql);
		return map;
	}

	// 根据账号查询信息
	public BkAccount getLocalAccInfo(String acc_no) {
		String sql = "select id,acc_no,bk_type,acc_name,currency,oper_date,acc_type,bk_name from bk_account where acc_no=?";
		return queryForObject(sql,new Object[]{acc_no},BkAccount.class);
	}
	
	/**
	 * 根据银行流水号或电银流水号查询  结算批次号
	 * @param seqType
	 * @param webSerialNo
	 * @return
	 */
	public String getBatchById(String seqType,String webSerialNo){
		String conditionSql=updateStateCondition(seqType);
		String sql="select batch from slog where "+conditionSql+" limit 1";
		return queryForString(sql,new Object[]{webSerialNo.trim()});
	}
	/**
	 * 修改结算状态为 "已完成"
	 * @param batch
	 * @return
	 */
	public int updateLiqBath(String batch){
		if(batch.equals(""))return 0;
		String sql="update fee_liq_bath set state=3 where batch=?";
		return update(sql,batch);
	}
	//单笔划款状态修改
	public int alterPayState(String seqType,String serialNo,String state){
		String conditionSql=updateStateCondition(seqType);
		if(conditionSql.equals(""))return 0;
		Object[] obj=new Object[]{state,serialNo};
		String sql="update slog set pay_state=? where "+conditionSql;
		return update(sql, obj);
	}
	private String updateStateCondition(String seqType){
		String conditionSql="";
		if(seqType.equals("1")){
			conditionSql=" id=? ";
		}else if(seqType.equals("2")){
			conditionSql=" bank_seq=? ";
		}else {
			return "";
		}
		return conditionSql;
	}
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int bdate,int edate, String mid, String batch) {
		String selSql = "select mid,liq_amt,batch,liq_date,state from fee_liq_bath ";
		StringBuffer condSql = new StringBuffer(" where id > 0 ");
		condSql.append(" and slog_flag =0 and state=2 ");
		if (bdate != 0)
			condSql.append(" and liq_date >= ").append(bdate);
		if (edate != 0)
			condSql.append(" and liq_date <= ").append(edate);
		if (!mid.trim().equals(""))
			condSql.append(" and mid='"+ mid+"'");
		if (!"0".equals(batch))
			condSql.append(" and batch='" + batch+"'");
		String querySql = selSql + condSql.toString();
		String countSql = "select count(id) from fee_liq_bath " + condSql.toString();
		
		return queryForPage(countSql,querySql,pageNo,new AppParam().getPageSize(),FeeLiqBath.class);
	}

	
	public CurrentPage<TrOrders> getSlogPage(int pageNo, int bdate, int edate, String mid, String batch) {
		StringBuffer sqlBuff = new StringBuffer("select * from tr_orders where state=0 and ptype = 8 ");
		if (bdate != 0) sqlBuff.append(" and init_time >= ").append(bdate).append("000000");
		if (edate != 0) sqlBuff.append(" and init_time <= ").append(edate).append("999999");
		if (!mid.trim().equals("")) sqlBuff.append(" and uid=").append(Ryt.addQuotes(mid));
		if (!"0".equals(batch)) sqlBuff.append(" and org_oid='").append(batch).append("'");
		String countSql = sqlBuff.toString().replace("*", "count(*)");
		int pageSize = new AppParam().getPageSize();
		return queryForPage(countSql, sqlBuff.toString(), pageNo, pageSize, TrOrders.class);
	}

	//(int pageNo, int mid,int payState, int bdate, int edate, String batch)
	public CurrentPage<TrOrders> getTrOrders(int pageNo,String mid,int payState, int bdate, int edate, String batch) {
		StringBuffer sqlBuff = new StringBuffer("select * from tr_orders where ptype = 8 ");
		if (payState !=0 ) sqlBuff.append(" and state= ").append(payState);
		if (bdate != 0) sqlBuff.append(" and oper_date >= ").append(bdate);
		if (edate != 0) sqlBuff.append(" and oper_date <= ").append(edate);
		if (!mid.trim().equals("")) sqlBuff.append(" and uid=").append(Ryt.addQuotes(mid));
		if (!"0".equals(batch)) sqlBuff.append(" and org_oid='").append(batch).append("'");
		String countSql = sqlBuff.toString().replace("*", "count(*)");
		int pageSize = new AppParam().getPageSize();
		return queryForPage(countSql, sqlBuff.toString(), pageNo, pageSize, TrOrders.class);
	}

	public boolean updateFeeLiqBathSlogFlagTo1(String batch) {
		final String sql = "update fee_liq_bath set slog_flag =1 where batch='" + batch+"'";
		return update(sql) == 1;
	}


	/**
	 * 合作银行账号新增
	 * @param accNo
	 * @param bkNo
	 * @param bkAbbv
	 * @return
	 */
	public int addCooperateBkNo(String bkNo,String bkAbbv){
		String sql="insert into bk_account (bk_no,bk_abbv,bk_type,acc_type) values(?,?,0,1)";
		return update(sql,bkNo,bkAbbv);
	}
	/**
	 * 得到存管银行账号
	 * @return
	 */
	public String getStoreAccNo(){
	    String sql = "select acc_no from bk_account where bk_type=1 limit 1";
		return  queryForString(sql);
	}
	
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
		LogUtil.printInfoLog("AutoSettlementDao", "queryDataForAutoSettleAmt", sql.toString());
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
		LogUtil.printInfoLog("AutoSettlementDao", "downAutoSettlementData", sql.toString());
		return query(sql.toString(), OrderInfo.class);
	}
	
}
