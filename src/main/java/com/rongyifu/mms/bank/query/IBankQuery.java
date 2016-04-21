package com.rongyifu.mms.bank.query;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;

/**
 * 银行查询接口
 * @author lv.xiaofeng
 *
 */
public interface IBankQuery {
	
	/**
	 * 调用银行接口查询单笔订单状态
	 * @param gate 支付渠道信息
	 * @param order 订单信息
	 * @return 订单查询结果
	 */
	BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order);
	
	/**
	 * 更新订单状态
	 * @param order 订单信息
	 * @param queryRet 订单查询结果
	 */
	void updateOrderStatus(Hlog order, BankQueryBean queryRet);
}
