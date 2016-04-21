package com.rongyifu.mms.modules.bgdao;

import java.util.List;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class AutoNoticeMerDao extends PubDao {

	public List<TlogBean> queryOrderInfo(int startQuerySce){
		Integer today=DateUtil.today();
		Integer curSec=DateUtil.getCurrentUTCSeconds();
		StringBuffer sql=new StringBuffer("select tseq from tlog where mdate=");
		sql.append(today);
		sql.append(" and tstat in(").append(Constant.PayState.SUCCESS).append(",").append(Constant.PayState.FAILURE).append(")");
		sql.append(" and is_notice in (0,2)");// 0  未通知              2  五次通知未响应
		sql.append(" and data_source=0");//常规交易
		sql.append(" and sys_time <").append(curSec-startQuerySce).append(";");
		LogUtil.printInfoLog("AutoNoticeMerDao", "queryOrderInfo", "sql:"+sql.toString());
		return query(sql.toString(), TlogBean.class);
	}
}
