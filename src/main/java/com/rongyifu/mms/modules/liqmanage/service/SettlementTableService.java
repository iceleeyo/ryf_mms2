package com.rongyifu.mms.modules.liqmanage.service;

import java.io.IOException;
import java.util.Map;

import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.modules.liqmanage.dao.SettlementTableDao;
import com.rongyifu.mms.settlement.SettleTB;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.RYFMapUtil;

public class SettlementTableService {
	
	SettlementTableDao settlementtabledao=new SettlementTableDao();
	
	/**
	 * 所有商户MAP
	 * @return
	 */
	public Map<Integer, String> getHashMer() {
		RYFMapUtil obj = RYFMapUtil.getInstance();
		return obj.getMerMap();
	}

	
	/**
	 * 查询fee_liq_bath表并分页
	 * @param mid 商户号
	 * @param state 结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate查询结束日期
	 * @param batch查询批次号
	 * @param pageIndex 页码
	 * @return
	 */
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate,Integer liqGid) {
		return settlementtabledao.getFeeLiqBath(pageNo, beginDate, endDate, mid, state, batch,mstate, liqGid);
	}
	
	/**
	 * 结算制表
	 * 
	 * @param batchs
	 *            需要结算制表的批次号数组
	 * @return 制表结果
	 * @throws IOException
	 */
	public String drawSettleTB(String[] batchs) throws IOException {
		SettleTB o = new SettleTB();
		return o.drawSettleTB(batchs);
	}
	

}
