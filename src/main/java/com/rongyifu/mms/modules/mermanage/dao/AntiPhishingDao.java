package com.rongyifu.mms.modules.mermanage.dao;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class AntiPhishingDao extends PubDao{
	
	public void doSave(String mid, Integer operId, String antiPhishingStr) {
		String sql = "update oper_info set anti_phishing_str = " + Ryt.addQuotes(antiPhishingStr) 
				   + " where mid = " + Ryt.sql(mid)
				   + "   and oper_id = " + operId;
		
		this.update(sql);
	}
}
