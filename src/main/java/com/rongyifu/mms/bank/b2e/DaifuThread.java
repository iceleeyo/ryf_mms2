package com.rongyifu.mms.bank.b2e;

import java.util.List;

import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.dao.AdminZHDao;

public class DaifuThread implements Runnable {

	private List<TrOrders> orderList = null;

	private AdminZHDao dao = null;

	public DaifuThread(List<TrOrders> orderList, AdminZHDao dao) {
		this.orderList = orderList;
		this.dao = dao;
	}

	@Override
	public void run() {
		if (orderList == null || orderList.size() == 0)
			return;

		for (int i = 0; i < orderList.size(); i++) {
			DaifuProcessor dfp = new DaifuProcessor(dao, orderList.get(i));
			dfp.process();
		}
	}
}
