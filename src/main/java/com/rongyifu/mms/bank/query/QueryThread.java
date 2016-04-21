package com.rongyifu.mms.bank.query;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.ewp.CallPayPocessor;

/**
 * 银行查询线程
 * @author lv.xiaofeng
 *
 */
public class QueryThread implements Runnable{
	
	// 银行查询处理接口
	private IBankQuery query = null;
	// 订单信息
	private Hlog order = null;
	// 支付渠道信息
	private GateRoute gate = null;
	public QueryThread(IBankQuery bankQuery, Hlog order, GateRoute gateRoute){
		this.query = bankQuery;
		this.order = order;
		this.gate = gateRoute;
	}
	
	@Override
	public void run() {
		BankQueryBean queryRet = query.queryOrderStatusFromBank(gate, order);
		String status = queryRet.getOrderStatus();
		if (status != null
				&& (status.equals(QueryCommon.ORDER_STATUS_SUCCESS) || 
					status.equals(QueryCommon.ORDER_STATUS_FAILURE))) {
			query.updateOrderStatus(order, queryRet);
			CallPayPocessor.callBackMerchants(order.getTseq().toString());
		}

	}
}
