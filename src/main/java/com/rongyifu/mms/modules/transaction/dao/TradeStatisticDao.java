package com.rongyifu.mms.modules.transaction.dao;

import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.TradeStatistics;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.modules.transaction.service.QueryTradeStatisticService;
import com.rongyifu.mms.utils.CurrentPage;

public class TradeStatisticDao extends PubDao {
	
	private Logger logger = Logger.getLogger(getClass());
	
	public CurrentPage<TradeStatistics> queryForPage(Integer bdate,Integer edate,Integer type,Integer pageNo){
		StringBuilder sql = new StringBuilder();
		StringBuilder condition = new StringBuilder(" AND t.sys_date >= ").append(bdate);
		condition.append( " AND t.sys_date <= ").append(edate);
		if(type == QueryTradeStatisticService.JYZB){//交易类型  总笔数  成功笔数  成功总金额  成功率（成功笔数/总笔数）包括(1,3,5,6,7,8,10,18)
			sql.append("SELECT d.trans_mode,c.successCount,c.successAmt,c.totalCount FROM (select distinct(trans_mode) FROM ryt_gate WHERE trans_mode IN (1,3,5,6,7,8,10,18)) d");
			sql.append(" LEFT JOIN"); 
			sql.append(" (SELECT a.trans_mode,b.successCount,b.successAmt,a.totalCount FROM");
			sql.append(" (SELECT t.trans_mode, SUM(t.count) totalCount FROM trade_statistics t WHERE t.trans_mode IN (1,3,5,6,7,8,10,18)");
			sql.append(condition);
			sql.append(" GROUP BY t.trans_mode) a");
			sql.append(" LEFT JOIN");
			sql.append(" (SELECT t.trans_mode,SUM(t.count) successCount,SUM(t.amount) successAmt FROM trade_statistics t WHERE t.tstat =2 AND t.trans_mode IN (1,3,5,6,7,8,10,18)");
			sql.append(condition);
			sql.append(" GROUP BY t.trans_mode) b");
			sql.append(" ON a.trans_mode = b.trans_mode ) c");
			sql.append(" ON d.trans_mode = c.trans_mode");
			sql.append( " ORDER BY trans_mode");
		}else if(type == QueryTradeStatisticService.JYFB){
			sql.append("SELECT g.trans_mode,g.gate_name,g.gate,c.successCount,c.successAmt,c.totalCount FROM ryt_gate g");
			sql.append(" LEFT JOIN");
			//临时表a 按 trans_mode 和 gate 分组的成功订单数据
			sql.append(" (SELECT a.gate, a.trans_mode,b.successCount,b.successAmt,a.totalCount FROM ");
				sql.append("(SELECT t.gate, t.trans_mode, SUM(t.count) totalCount FROM trade_statistics t WHERE 1=1 AND t.trans_mode IN (1,3,5,6,7,8,10,18)").append(condition).append(" GROUP BY t.trans_mode,t.gate) a");
				sql.append(" LEFT JOIN");
				//临时表b 按 trans_mode 分组的所有订单数据
				sql.append(" (SELECT t.trans_mode,t.gate,SUM(t.count) successCount,SUM(t.amount) successAmt FROM trade_statistics t WHERE t.tstat =2 AND t.trans_mode IN (1,3,5,6,7,8,10,18)").append(condition).append(" GROUP BY t.trans_mode,t.gate) b");
			//临时表a和临时表b left join 生成临时表c
			sql.append(" ON a.trans_mode = b.trans_mode AND a.gate = b.gate) c");
			//gates表左外连接临时表c和ryt_gate 生成最终结果表 因为要展示交易类型所有开通银行的订单情况 即时没有订单
			sql.append(" ON g.trans_mode=c.trans_mode AND g.gate = c.gate");
			sql.append(" WHERE g.trans_mode IN (1,3,5,6,7,8,10,18)");
			sql.append(" ORDER BY trans_mode, gate");
		}
		logger.info("SQL: "+sql.toString());
		return queryForCurrPage(sql.toString(), pageNo, TradeStatistics.class);
	}
	
	public int deleteStatisticsByDate(int date) {
		String sql = "DELETE FROM trade_statistics WHERE sys_date = ?";
		return update(sql,date);
	}
	
	public int doStatistics(int date, boolean isTlog){
		String table = "tlog";
		if(!isTlog){
			table = "hlog";
		}
		String sql = "INSERT INTO trade_statistics (sys_date,mid,trans_mode,tstat,gate,gid,count,amount,pay_amt,fee_amt,bank_fee) SELECT sys_date, mid, type, tstat, gate, gid, COUNT(*) AS count, SUM(amount) AS amount, SUM(pay_amt) AS pay_amt, SUM(fee_amt) AS fee_amt, SUM(bank_fee) AS bank_fee FROM "+table+" WHERE sys_date = ? GROUP BY sys_date, type, gate, tstat, gid, mid";
		return update(sql,date);
	}
}
