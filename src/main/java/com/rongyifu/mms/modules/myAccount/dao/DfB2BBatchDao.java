package com.rongyifu.mms.modules.myAccount.dao;

import java.util.List;

import com.rongyifu.mms.common.Ryt;

public class DfB2BBatchDao extends MyAccountDao {
	
	/**
	 * 保存订单，冻结账户余额
	 * @param orderSqlList
	 * @param payAmt
	 * @param mid
	 * @throws Exception
	 */
	public void insertOrder(List<String> orderSqlList, long payAmt, String mid) throws Exception{
		String updateAccountSql = "update acc_infos set balance = balance - " + payAmt +",freeze_amt=freeze_amt +" + payAmt + " where aid=uid and aid = " + Ryt.addQuotes(mid);
		orderSqlList.add(updateAccountSql);
		batchSqlTransaction2(orderSqlList);
	}
}
