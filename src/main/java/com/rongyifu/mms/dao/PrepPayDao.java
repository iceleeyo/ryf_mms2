package com.rongyifu.mms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;


import com.rongyifu.mms.bean.*;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("unchecked")
public class PrepPayDao extends PubDao{
	
	/**
	 * 银行行号和 银行简称的map
	 * @return
	 */
	public Map<String,String> getBkNoMap(){
		String sql="select bk_no,bk_abbv from bk_account ";
		return queryToMap(sql);
	}
 
	public String getBkNoByChannelNo(String channelId){
		String sql="select bf_bk_no from gate_route where gid=? ";
		return queryForString(sql,new Object[]{ channelId});
	}
	 
	/**
	 * 查询银行余额
	 * @param pageNo
	 * @param bkId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<DateBlLog> queryBankBalance(Integer pageNo,String bkId,Integer beginDate,Integer endDate){
		StringBuffer sqlBuff=new StringBuffer();
		sqlBuff.append(" from date_bl_log where obj_type=1 and liq_date>=? and liq_date<=?");
		if(!Ryt.empty(bkId)){
			sqlBuff.append(" and obj_id=").append(Ryt.addQuotes(bkId));
		}
		String sqlCount="select count(*) "+sqlBuff.toString();
		String sqlRows="select id,name,bf_bl,balance,liq_date"+sqlBuff.toString();
		Object[] objArr=new Object[]{ beginDate, endDate};
		return queryForPage(sqlCount, sqlRows,objArr,pageNo, new AppParam().getPageSize(), DateBlLog.class);
	}
	/**
	 * 商户流水查询
	 * @param pageNo
	 * @param minfoId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<TrSeq> queryAccountDetail(Integer pageNo,Integer pageSize,Integer minfoId,Integer beginDate,Integer endDate){
	     StringBuffer sqlbuff=new StringBuffer("select * from tr_seq where 1=1 ");
	    if(minfoId!=null&&minfoId!=0){
	    	sqlbuff.append(" and obj_id=").append(minfoId);
	    }
	    if(beginDate!=0&&endDate!=0){
	    	sqlbuff.append(" and tr_date>=").append(beginDate);
	    	sqlbuff.append(" and tr_date<=").append(endDate);
	    }
	    return queryForPage(sqlbuff.toString().replace("*", "count(*)"),sqlbuff.toString(), pageNo,pageSize, TrSeq.class);
	}
	/**
	 *  交易查询
	 * @param pageNo
	 * @param minfoId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<CgOrder> queryTransPage(Integer pageNo,Integer dateType,Integer beginDate,
											Integer endDate,String bkNo,Integer transType,Integer transState){
		 StringBuffer sqlbuff=new StringBuffer("select * from cg_order where 1=1 ");
		 if(dateType==1){
			 if(beginDate!=null)sqlbuff.append(" and add_date>=").append(beginDate);
			 if(endDate!=null)sqlbuff.append(" and add_date<=").append(endDate);
		 }else{
			 if(beginDate!=null)sqlbuff.append(" and oper_date>=").append(beginDate);
			 if(endDate!=null)sqlbuff.append(" and oper_date<=").append(endDate);
		 }
		 if(transType!=null){
			 if(transType==-1){
				 sqlbuff.append(" and tr_type in (2,3)");//调增、调减
			 }else{
				 sqlbuff.append(" and tr_type=").append(transType);
			 }
		 }
		 if(!Ryt.empty(bkNo)) sqlbuff.append(" and bk_no=").append(Ryt.addQuotes(bkNo));
		 if(transState!=null)sqlbuff.append(" and tr_state=").append(transState);
		 return queryForPage(sqlbuff.toString().replace("*", "count(*)"),sqlbuff.toString(), pageNo, CgOrder.class);
	}
	/**
	 * 查询系统中这笔交易是否存在
	 * @param mid
	 * @param oldTseq
	 * @return
	 */
	public Hlog queryHlogIsExist(String mid,Integer oldTseq){
		String sql="select tseq,oid,bk_seq1,amount,author_type,gate,sys_date from hlog where mid=? and tseq=?";
		return queryForObject(sql, new Object[]{mid,oldTseq}, Hlog.class);
	}
	/**
	 * 差错退款申请
	 * @param cgOrder
	 * @return
	 */
	public int submitErrorRefund(CgOrder cgOrder)throws Exception{
		String sql="insert into cg_order (bk_no,tr_date,tr_amt,tseq,mid,bk_seq,auth_type,gate_id,add_date," +
				"tr_type,tr_state,add_oper_id,remark,bk_ref_fee)values (?,?,?,?,?,?,?,?,?,0,0,?,?,?)";
		Object[] objArr=new Object[]{
				cgOrder.getBkNo(),cgOrder.getTrDate(),cgOrder.getTrAmt(),
				cgOrder.getTseq(),cgOrder.getMid(),cgOrder.getBkSeq(),
				cgOrder.getAuthType(),cgOrder.getGateId(),DateUtil.today(),
				getLoginUser().getOperId(),cgOrder.getRemark(),cgOrder.getBkRefFee() 
				};
		return update(sql, objArr);
	} 
	
	//================================PrepPayDao==========================================	
	public long getMerDateSuccessSumByTrType(String string,String mid,int t) {
		StringBuffer sql = new StringBuffer("select SUM(tr_amt) from tr_seq ");
		sql.append("where obj_id = '").append(mid).append("' and ");
		sql.append("sys_date=").append(t).append(" and ");
		sql.append("tr_type in (").append(string).append(")");
		return queryForLong(sql.toString());
	}
	
	public long findMerBalance(String mid) {
		// 原来的余额
		String sql = "select balance from date_bl_log where id = (select max(id) from date_bl_log where obj_id = '"+ mid+"')";
		try {
			return queryForLong(sql);
					//select max(id) from date_bl_log where obj_id = '"+ mid+"' 如果之前不存在会抛EmptyResultDataAccessException
			} catch (EmptyResultDataAccessException e) {
					return 0l;
			}
	}
	
	
	/**
	 * 往date_bl_log里边增加一条记录 
	 * @param o
	 */
	public void insertDateBlLog(DateBlLog o) {
		StringBuffer sql = new StringBuffer("insert into date_bl_log ");
		sql.append("(obj_id,obj_type,name,bf_bl,mer_fee,balance,liq_date,sys_date)");
		sql.append("values (?,?,?,?,?,?,?,?)");
		Object[] objs = {o.getObjId(),o.getObjType(),o.getName(),o.getBfBl(),o.getMerFee(),o.getBalance(),o.getLiqDate(),DateUtil.today()};
		update(sql.toString(),objs);
	}
	
	public void updateTrSeqLiqDate(int liqDate, int t) {
		update("update tr_seq set liq_date = " + liqDate + " where sys_date = " + t);
	}
	
	
	public long getSumMerFee(int t) {
		//应收商户手续费,存管银行才有
		return queryForLong("select SUM(tr_amt) from tr_seq  where tr_type=1 sys_date = " + t);
	}
	
	/**
	 * 所有银行账户
	 * @return
	 */
	public List<BkAccount> getAllBKAccount() {
		return query("select * from bk_account",BkAccount.class);
	}
	
	
	public long getDateSuccessSumByTrType(String string,int t) {
		StringBuffer sql = new StringBuffer("select SUM(tr_amt) from cg_order ");
		sql.append("where tr_state = 2 and ");
		sql.append("tr_type in (").append(string).append(") and ");
		sql.append("vali_date=").append(t);
		return queryForLong(sql.toString());
	}
	
	/**
	 * 差错退款经办查询
	 * @param pageNo
	 * @param authType
	 * @param transState
	 * @param bdate
	 * @param edate
	 * @return
	 */
	/**
	 * 差错退款经办查询
	 * @param pageNo
	 * @param authType
	 * @param transState
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public CurrentPage<CgOrder> queryErrorRefundPay(Integer pageNo,Integer pageSize,Integer authType,Integer transState,
			Integer bdate,Integer edate,String oldTseq){
		StringBuffer sqlBuff=new StringBuffer("select * from cg_order where tr_type=0 ");
		if(authType!=null)sqlBuff.append(" and auth_type=").append(authType);
		if(transState!=null)sqlBuff.append(" and tr_state=").append(transState);
		if(bdate!=null)sqlBuff.append(" and add_date>=").append(bdate);
		if(bdate!=null)sqlBuff.append(" and add_date<=").append(edate);
		if(!Ryt.empty(oldTseq))sqlBuff.append(" and tseq=").append(Ryt.addQuotes(oldTseq));
		String sql=sqlBuff.toString();
		String countSql=sql.replace("*", "count(*)");
		String amtSumSql = sql.replace("*", "sum(tr_amt)");
		String refFeeSumSql = sql.replace("*", "sum(bk_ref_fee)");
		
		Map sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.BK_REF_FEE_SUM, refFeeSumSql);
		return queryForPage(countSql,sql, pageNo,CgOrder.class,sumSQLMap) ;
	}
	public List<CgOrder> queryTodayRefund(){
		String sql="select * from cg_order where tr_type=0 and tr_state=1 and oper_date=date_format(now(),'%Y%m%d')";
		List<CgOrder> cgOrderList= query(sql, CgOrder.class);
		return  cgOrderList;
	}
	/**
	 * 差错退款经办/撤销
	 * @param ids
	 * @return
	 */
	public int handRefundPay(Integer handlType,Integer id){
		String sql="update cg_order set tr_state=? , oper_oper_id=? , oper_date=? where id=?";
		Object[] objArr=new Object[]{handlType, getLoginUser().getOperId(), DateUtil.today(), id };
		return update(sql,objArr);
	}
	/**
	 * 差错退款审核
	 * @param ids
	 * @return
	 */
	public int verifyRefundPay(Integer handlType,Integer id){
		String sql="update cg_order set tr_state=? , vali_oper_id=? , vali_date=? where id=?";
		Object[] objArr=new Object[]{handlType, getLoginUser().getOperId(), DateUtil.today(), id };
		return update(sql,objArr);
	}
	/**
	 * 商户、银行余额查询（商户资金归集申请）
	 * @param channelId
	 * @return
	 */
	public BkAccount queryBkBalance(String bkNo){
		String sql="select acc_no,bk_bl,bf_bl from bk_account where bk_no=?";
		return queryForObject(sql,new Object[]{ bkNo }, BkAccount.class);
	}
	/**
	 * 商户资金归集、调帐申请
	 * @param cgOrder
	 * @return
	 */
	public int fundChangeReq(CgOrder cgOrder){
		String sql="insert into cg_order (bk_no,tr_date,tr_time,tr_amt,add_date,tr_type,tr_state,add_oper_id,remark) values (?,?,?,?,?,?,0,?,?)";
		Object[] objArr=new Object[]{
				cgOrder.getBkNo(),cgOrder.getTrDate(),DateUtil.getCurrentUTCSeconds(),
				cgOrder.getTrAmt(),DateUtil.today(),cgOrder.getTrType(),
				getLoginUser().getOperId(),cgOrder.getRemark()  };
		return update(sql, objArr);
	}
	
	public int findTodayDateBlLogCount() {
			return queryForInt("select count(*) from date_bl_log where sys_date = " + DateUtil.today());
	}
	public List<Map<String ,Object>> queryAllRelationBank(){
		String sql="select * from pay_chl_bk";
		return queryForList(sql);
	}
}
