package com.rongyifu.mms.dao;


import java.util.List;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.bean.UserBkAcc;
import com.rongyifu.mms.db.PubDao;
@SuppressWarnings("unchecked")
public class CompanyDao extends PubDao {
	/**
	 * 
	 * @param accName 付款账户名
	 * @param oid系统订单号
	 * @return
	 * @throws Exception 
	 */
	public TrOrders queryOrderDetials(String accName,String oid)  {
		StringBuffer sql= new StringBuffer("select * from tr_orders where acc_name=");
		sql.append("'").append(accName).append("' and oid=");
		sql.append("'").append(oid).append("' and ptype=6 limit 1");
		return queryForObject(sql.toString(), TrOrders.class);
	}
	/**
	 * 
	 * @param tseq流水号
	 * @param table查询的表
	 * @return
	 */
	public Hlog queryMX(String tseq,String table){
		StringBuffer sql= new StringBuffer("select * from ");
		sql.append(table).append(" where tseq='").append(tseq).append("'");
		return queryForObject(sql.toString(), Hlog.class);
	}
	/**
	 * 根据商户号 查询 <账户号,银行>的map
	 * @return
	 */
	public List<UserBkAcc> getBkAccMap(String mid){
//		String sql="select id,bk_name,acc_no from user_bk_acc where uid="+mid;
		String sql="select id,bk_name,acc_no,acc_name from user_bk_acc where uid=(select uid from acc_infos where aid='"+mid+"')";
		return query(sql,UserBkAcc.class);
	}
	/**
	 * 根据id查询银行账户
	 * @param id
	 * @return
	 */
	public UserBkAcc getUserBkAccById(Integer id){
		String sql="select * from user_bk_acc where id="+id;
		return queryForObject(sql,UserBkAcc.class);
	}
	/**
	 * 付款到
	 * @return
	 * @throws Exception 
	 */
	public int payToMerchant(TrOrders trOrders) throws Exception{
		
		return saveObject(trOrders);
	}
}
