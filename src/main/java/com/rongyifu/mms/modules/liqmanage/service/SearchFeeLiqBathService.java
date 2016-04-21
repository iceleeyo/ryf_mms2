package com.rongyifu.mms.modules.liqmanage.service;

import java.util.List;

import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.modules.liqmanage.dao.SearchFeeLiqBathDao;
import com.rongyifu.mms.utils.CurrentPage;

public class SearchFeeLiqBathService {
	private SearchFeeLiqBathDao searchfeeliqbathdao = new SearchFeeLiqBathDao();

	/**
	 * 用于结算单查询，查询fee_liq_bath表
	 * @param mid       商户号
	 * @param state     结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate   查询结束日期
	 * @return
	 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBathByLiqType(int page,String merType, String mid, int beginDate, int endDate,Integer liqgid,String gid) {
		return searchfeeliqbathdao.searchFeeLiqBath(page, merType, mid,beginDate, endDate, liqgid,gid);
	}

	
	/**
	 * 用于结算单查询，查询fee_liq_bath表
	 * @param mid  商户号
	 * @param state  结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate  查询起始日期
	 * @param endDate  查询结束日期
	 * @return
	 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int page, String mid, int beginDate, int endDate) {
		return searchfeeliqbathdao.searchFeeLiqBath(page, mid, beginDate, endDate);
	}
	/*
	 * 打印
	 */
	public List<FeeLiqBath> queryPrintTableData(String merType,String mid, int beginDate,int endDate,Integer liqgid,String gid) {
		return searchfeeliqbathdao.queryPrintTableData(merType,mid, beginDate,endDate,liqgid,gid);
	}
}
