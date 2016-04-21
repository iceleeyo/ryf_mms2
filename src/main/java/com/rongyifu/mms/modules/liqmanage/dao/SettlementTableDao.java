package com.rongyifu.mms.modules.liqmanage.dao;

import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("rawtypes")
public class SettlementTableDao extends PubDao {
	
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
	@SuppressWarnings("unchecked")
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate, Integer liqGid) {
		String selSql = "select f.mid,f.trans_amt,f.ref_amt,(f.trans_amt-ref_amt)as tradeAmt,f.fee_amt,f.manual_add,f.manual_sub,f.liq_amt,";
		selSql += " f.batch,f.liq_date,f.state,f.ref_fee from fee_liq_bath f,minfo m";

		StringBuffer condSql = new StringBuffer(" where f.id > 0 and f.mid=m.id ");
		if (beginDate != 0)
			condSql.append(" and f.liq_date >= ").append(beginDate);
		if (endDate != 0)
			condSql.append(" and f.liq_date <= ").append(endDate);
		if (state == 0) {// 结算制表中 0 表示全部 结算确认中 -1表示全部
			condSql.append(" and f.state in (1,2)");
		} else if (state == -1) {
			condSql.append(" and f.state in (2,3)");
		} else {
			condSql.append(" and f.state =" + state);
		}
		if (!mid.trim().equals(""))
			condSql.append(" and f.mid='"+ mid+"'");
		if (!batch.trim().equals(""))
			condSql.append(" and f.batch='" + batch+"'");
		
		if(mstate!=null)condSql.append(" and m.mstate=").append(mstate);
		// 结算对象
//		if(liqGid == 0) condSql.append(" and (f.liq_gid=2 or f.liq_gid=3 )"); //银行卡
//		else if(liqGid == 1) condSql.append(" and (f.liq_gid=1 or f.liq_gid=4 )");//电银账户
//		else condSql.append(" and f.liq_gid=5");//代理商
		
		String querySql = selSql + condSql.toString();
		String countSql = "select count(f.id) from fee_liq_bath f,minfo m " + condSql.toString();
		return queryForPage(countSql, querySql, pageNo, 50, FeeLiqBath.class);
	}
	

}
