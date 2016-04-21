package com.rongyifu.mms.bank.query;

import java.util.List;

import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 银行查询接口
 * @author lv.xiaofeng
 *
 */
public class BankQueryProcessor {
	
	// 最大线程数
	private static final int MAX_THREAD_NUM = 1;
	// 1小时内非成功和失败的交易需要请求银行查询交易状态（单位：秒）
	private static final int MAX_QUERY_TIME = 3600;
	// 下单5分钟后开始调用查询接口（单位：秒）
	private static final int START_QUERY_TIME = 300;
	// 启动线程间隔  单位：毫秒
	private static final int START_THREAD_INTERVAL = 1000;
	
	/**
	 * 银行查询接口入口方法
	 */
	public void process(){
		if(!Ryt.isStartService("启动同步订单支付结果服务"))
			return;
		
		List<Hlog> orderList = QueryCommon.getDao().queryOrderInfo(QueryCommon.getGateList(),MAX_QUERY_TIME, START_QUERY_TIME);
		LogUtil.printInfoLog("BankQueryProcessor", "process", "需要同步" + orderList.size() +"笔订单");
		if(orderList == null || orderList.size() == 0)
			return;
		
		int count = 0;
		for(Hlog order : orderList){
			if(QueryCommon.getGateMap().containsKey(String.valueOf(order.getGid()))){
				LogUtil.printInfoLog("BankQueryProcessor", "process", "订单信息["+(count+1)+"]：tseq="+order.getTseq()+", gid="+order.getGid()+", tstat="+order.getTstat());
				startThead(order);
				
				count++;
			}
			
			if(count != 0 && count % MAX_THREAD_NUM == 0){
				try {
					Thread.sleep(START_THREAD_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 启动线程
	 * @param order 订单信息
	 */
	private void startThead(Hlog order){
		String className = QueryCommon.getHandleClass(order.getGid());	// 获取处理类	
		GateRoute gateRoute = QueryCommon.getDao().queryBankInfoByGid(order.getGid()); // 查询支付渠道
		
		// 获取查询对象
		IBankQuery queryObj = null;
		try {
			queryObj = (IBankQuery) Class.forName(className.trim()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// 启动线程
		Thread thread = new Thread(new QueryThread(queryObj, order, gateRoute));
		thread.start();
	}
}
