package com.rongyifu.mms.modules.transaction.dao;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.DfTransaction;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class DfTransactionDao extends PubDao<DfTransaction> {
	
	private Logger logger = Logger.getLogger(getClass());
	
	public DfTransaction queryByTseq(String tseq){
		String sql = "select * from df_transaction where tseq = ?";
		return queryForObject(sql, new Object[]{tseq}, DfTransaction.class);
	}
	
	public CurrentPage<DfTransaction> queryForPage(DfTransaction dfTr,Integer pageNo){
		StringBuilder sql = new StringBuilder("select * from df_transaction where 1=1");
		StringBuilder condition = new StringBuilder();
		condition.append(" AND sys_date>= ").append(dfTr.getBdate());
		condition.append(" AND sys_date<= ").append(dfTr.getEdate());
		if(dfTr.getGate() != null){
			String gate = formatInt(dfTr.getGate());
			condition.append(" AND gate like '%").append(gate).append("'");
		}
		if(dfTr.getGid() != null){
			condition.append(" AND gid = ").append(dfTr.getGid());
		}
		if(dfTr.getTstat() != null){
			condition.append(" AND tstat = ").append(dfTr.getTstat());
		}
		if(dfTr.getType() != null){
			condition.append(" AND type = ").append(dfTr.getType());
		}
		if(StringUtils.isNotBlank(dfTr.getTseq())){
			condition.append(" AND tseq = ").append(Ryt.addQuotes(dfTr.getTseq()));
		}
		if(StringUtils.isNotBlank(dfTr.getOrderId())){
			condition.append(" AND order_id = ").append(Ryt.addQuotes(dfTr.getOrderId()));
		}
		if(StringUtils.isNotBlank(dfTr.getAccountId())){
			condition.append(" AND account_id = ").append(Ryt.addQuotes(dfTr.getAccountId()));
		}
		if(dfTr.getDataSource() != null){
			condition.append(" AND data_source = ").append(dfTr.getDataSource());
		}
		sql.append(condition);
		sql.append(" ORDER BY tseq DESC");
		logger.info(sql.toString());
		String amtSumSql = "select sum(trans_amt) from df_transaction where 1=1" + condition.toString();
 		Map<String,String> sumSQLMap=new HashMap<String,String>();
 		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		return queryForCurrPage(sql.toString(), pageNo, DfTransaction.class,sumSQLMap);
	}
	
	private String formatInt(Integer num){
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(3);
		nf.setGroupingUsed(false);
		return nf.format(num);
	}
	
}
