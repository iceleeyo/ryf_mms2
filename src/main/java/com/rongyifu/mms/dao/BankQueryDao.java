package com.rongyifu.mms.dao;

import java.util.Date;
import java.util.List;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * @author lv.xiaofeng
 * 
 */
@SuppressWarnings("rawtypes")
public class BankQueryDao extends PubDao {
	
	/**
	 * 查询支付渠道信息
	 * @param gid
	 * @return
	 */
	public GateRoute queryBankInfoByGid(Integer gid) {
		String sql = "select * from gate_route where gid = " + gid;
		return queryForObject(sql, GateRoute.class);
	}
	
	/**
	 * 查询订单信息
	 * @param gateList
	 * @param maxQueryTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Hlog> queryOrderInfo(String gateList, int maxQueryTime, int startQueryTime){
		int currentUTCSeconds = DateUtil.getCurrentUTCSeconds();
		int today = DateUtil.today();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from tlog ");
		sql.append(" where tstat in (" + Constant.PayState.INIT + ", " + Constant.PayState.WAIT_PAY + ") ");
		sql.append("   and sys_date = " + today);
		sql.append("   and sys_time >= " + (currentUTCSeconds - maxQueryTime));
		sql.append("   and sys_time <= " + (currentUTCSeconds - startQueryTime));
		sql.append("   and gid in(" + gateList + ")");
		return query(sql.toString(), Hlog.class);
	}

	/**
	 * 查询订单信息（日期）
	 * @param gateList
	 * @param maxQueryTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Hlog> queryOrderInfoFromHlog(String gate, String startDate, String endDate){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from hlog ");
		sql.append(" where tstat = " + Constant.PayState.SUCCESS);
		sql.append("   and sys_date >= " + startDate);
		sql.append("   and sys_date <= " + endDate);
		sql.append("   and gid = " + gate);
		return query(sql.toString(), Hlog.class);
	}
	
	/**
	 * 修改订单状态
	 * @param order		订单信息
	 * @param status	订单状态
	 */
	public void updateOrderStatus(Hlog order, BankQueryBean queryRet) {
		String status = queryRet.getOrderStatus();
		String bankSeq = queryRet.getBankSeq();
		String errorMsg = queryRet.getErrorMsg();

		String sql = "update tlog set tstat = " + status;
		if (!Ryt.empty(bankSeq))
			sql += ",bk_seq1=" + Ryt.addQuotes(bankSeq) + ",bk_seq2=" + Ryt.addQuotes(bankSeq);
		if (!Ryt.empty(errorMsg))
			sql += ",error_msg = " + Ryt.addQuotes(errorMsg);
		sql += " where tseq = " + Ryt.addQuotes(order.getTseq());
		sql += " and tstat != " + Constant.PayState.SUCCESS;

		LogUtil.printInfoLog("BankQueryDao", "updateOrderStatus", sql);
		this.update(sql);
	}
	
	/**
	 * 获取修改订单信息sql
	 * @param order		订单信息
	 * @param status	订单状态
	 * @return
	 */
	public String getUpdateOrderSql(Hlog order, BankQueryBean queryRet) {
		String status = queryRet.getOrderStatus();
		String bankSeq = queryRet.getBankSeq();
		String errorMsg = queryRet.getErrorMsg();
		int merFee = queryRet.getMerFee();

		String sql = "update tlog set tstat = " + status;
		sql += ", bk_flag = 1";
		sql += ", bk_date = " + DateUtil.today();
		sql += ", bk_recv = " + DateUtil.getCurrentUTCSeconds(new Date());
		
		if (!Ryt.empty(bankSeq))
			sql += ",bk_seq1=" + Ryt.addQuotes(bankSeq) + ",bk_seq2=" + Ryt.addQuotes(bankSeq);
		if (!Ryt.empty(errorMsg))
			sql += ",error_msg = " + Ryt.addQuotes(errorMsg);
		if(merFee != 0)
			sql += ",fee_amt = " + merFee;
		sql += " where tseq = " + Ryt.addQuotes(order.getTseq());
		sql += " and tstat != " + Constant.PayState.SUCCESS;

		return sql;
	}
	
	/**
	 * 获取商户手续费公式
	 * @param order
	 * @return
	 */
	public String getMerFeeModel(Hlog order){
		String mid = order.getMid();
		int gateId = order.getGate();
		String gid = String.valueOf(order.getGid());
		String sql = "select calc_mode from fee_calc_mode where mid = " + Ryt.addQuotes(mid) + " and gate = " + gateId + " and gid = " + gid;
		return this.queryForString(sql);
	}
}
