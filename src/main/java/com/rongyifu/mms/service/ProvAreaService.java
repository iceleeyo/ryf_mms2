package com.rongyifu.mms.service;


import com.rongyifu.mms.dao.ProvAreaDao;

public class ProvAreaService {
	private ProvAreaDao dao=new ProvAreaDao();
	
	public String queryProv(int provId) {
		return dao.getProvById(provId);
	}
	public String queryBkName(String bkNo){
		return dao.getBkNameByBkNo(bkNo);
	}
}
