package com.rongyifu.mms.modules.Mertransaction.dao;

import java.util.List;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.LogUtil;

public class YCKBillChkDao extends PubDao {
	
	/**
	 * 开始日期23点(包括23点)后   到  结束日期23点(不包括23点)前
 	 * @param bdate
	 * @param edate
	 * @param bdate_one
	 * @return
	 */
	public List<Hlog> getBillChkData(String bdate, String edate,String bdate_one){
		StringBuffer sql=new StringBuffer("select h.p3,h.mid,h.oid,h.amount,h.fee_amt,h.sys_date,h.type,h.p10,h.sys_time from hlog h,minfo m  ");
		sql.append("where m.id=h.mid  and h.tstat = 2 and length(h.mid)>10 and m.liq_obj=2 and h.bk_chk=1 ");
		sql.append(" and  ((h.sys_date>"+bdate+" and h.sys_time>=82800 and h.sys_date<="+bdate_one+")");//开始日期十一点后的数据
		sql.append(" or  (h.sys_date>="+bdate_one+"  and h.sys_time<82800 and h.sys_date<="+edate+"))");//结束日期十一点前的数据
		LogUtil.printInfoLog("YCKBillChkDao", "getBillChkData", "Sql:"+sql.toString());
		return query(sql.toString(), Hlog.class);
	}
}
