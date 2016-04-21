package com.rongyifu.mms.modules.myAccount.dao;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.exception.B2EException;

public class DfB2BSingleDao extends MyAccountDao{
	
	/**
	 * 查询商户的客户企业名称
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> queryMerCustomerCom() throws Exception{
		String masterId=getLoginUserMid();
		String sql="select cid,cname from ass_cus as a ,cus_infos as c where a.cus_id=c.cid and a.cus_type=0 and a.master_id="+masterId;
		return queryToMap(sql);
	}
	
	/**
	 * 保存订单，冻结账户余额
	 * @param order
	 * @throws Exception
	 */
	public void insertOrder(TlogBean order) throws Exception{
		String orderSql = SqlGenerator.generateInsertSql(order);		
		String updateAccountSql = "update acc_infos set balance = balance - " +  order.getPay_amt() +",freeze_amt=freeze_amt +" + order.getPay_amt() + " where aid=uid and aid = " +Ryt.addQuotes(order.getMid());
		int[] ret = batchSqlTransaction(new String[] { orderSql, updateAccountSql });
		if(ret == null)
			throw new B2EException("batch sql error");
	}
	
	/**
	 * 根据客户企业查询账号
	 * 
	 * @param uid
	 * @return
	 */
	public Map<String, String> queryBkAccByCusId(String uid) {
		String sql = "select acc_no,bk_name from user_bk_acc where uid=" + Ryt.addQuotes(uid);
		Map<String, String> bkAccMap = queryToMap(sql);
		Map<String, String> accNameMap = new HashMap<String, String>();
		for (String bkAcc : bkAccMap.keySet()) {
			accNameMap.put(bkAcc, bkAccMap.get(bkAcc) + bkAcc);
		}
		return accNameMap;
	}
}
