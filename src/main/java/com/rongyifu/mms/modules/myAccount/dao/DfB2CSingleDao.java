package com.rongyifu.mms.modules.myAccount.dao;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.exception.B2EException;

public class DfB2CSingleDao extends MyAccountDao{
	
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
}