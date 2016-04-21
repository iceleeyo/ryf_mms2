package com.rongyifu.mms.dao;

import com.rongyifu.mms.db.PubDao;

public class ProvAreaDao extends PubDao {
	//通过省份ID查询省份名称
	public String getProvById(int provId){
		String ProvArea = "";
		try {
			ProvArea= queryForStringThrowException("select prov_name from prov_area where prov_id='"+provId+"'");
		} catch (Exception e) {
			ProvArea="";
		}
	  return ProvArea;
	}
	
	//通过联行号查询联行名称
	public String getBkNameByBkNo(String bkNo){
		String BkName="";
		try {
			BkName= queryForStringThrowException("select bk_name from bank_no_info where bk_no='"+bkNo+"'");
		} catch (Exception e) {
			BkName="";
		}
		return BkName;
	}
}
