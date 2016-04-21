package com.rongyifu.mms.modules.transaction.dao;

import java.util.List;
import java.util.Map;

import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class QueryTradeStatisticDao extends PubDao{

	/**
	 * 交易统计
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String, Object>> transactionStatistics(Integer beginDate, Integer endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select type,sum(all_count) all_count,sum(succ_count) succ_count,sum(succ_amount) succ_amount");
		sql.append("  from ( ");
		sql.append("select type,");
		sql.append("       count(0) all_count,");
		sql.append("       sum(case when tstat = 2 then 1 else 0 end) succ_count,");
		sql.append("       sum(case when tstat = 2 then amount else 0 end) succ_amount");
		sql.append("  from tlog");
		sql.append(" where type in (1,3,6,7,8,18)");
		sql.append("   and sys_date>=").append(beginDate);
		sql.append("   and sys_date<=").append(endDate);
		sql.append(" group by type");
		sql.append(" union all ");
		sql.append("select type,");
		sql.append("       count(0) all_count,");
		sql.append("       sum(case when tstat = 2 then 1 else 0 end) succ_count,");
		sql.append("       sum(case when tstat = 2 then amount else 0 end) succ_amount");
		sql.append("  from hlog");
		sql.append(" where type in (1,3,6,7,8,18)");
		sql.append("   and sys_date>=").append(beginDate);
		sql.append("   and sys_date<=").append(endDate);
		sql.append(" group by type) a");
		sql.append(" group by type");
		sql.append(" order by type");
		
		return queryForList(sql.toString());
	}
}
