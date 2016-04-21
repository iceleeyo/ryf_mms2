package com.rongyifu.mms.modules.myAccount.dao;

import java.util.ArrayList;
import java.util.List;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;
import com.rongyifu.mms.dbutil.sqlbean.extens.TlogBean;

@SuppressWarnings("rawtypes")
public class DfqrDao extends PubDao{

	@SuppressWarnings("unchecked")
	public CurrentPage<TlogBean> queryDFQRinfo(Integer pageNo, Integer pageNum,
			String batchNo, Long tseq, Integer type, Integer dateType,
			Integer state, Integer bdate, Integer edate) {
		if (pageNum == 0)
			pageNum = 50;
		LoginUser u = getLoginUser();
		String mid = u.getMid();
		
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append(" where mid=" + Ryt.addQuotes(mid));
		// 交易类型
		if (type == -1)
			sqlWhere.append(" and (type = 11 or type = 12) ");
		else
			sqlWhere.append(" and type=").append(type);
		// 交易批次号
		if (!Ryt.empty(batchNo))
			sqlWhere.append(" and p8 = ").append(Ryt.addQuotes(batchNo));
		// 电银流水号
		if (tseq != null)
			sqlWhere.append(" and tseq=").append(tseq);
		// 日期类型
		if (dateType == 0) { // 申请代付日期
			sqlWhere.append(" and sys_date >= ").append(bdate);
			sqlWhere.append(" and sys_date <= ").append(edate);
		} else if (dateType == 1 || dateType == 2) { // 确认代付日期、撤销代付日期
			sqlWhere.append(" and p4 >= ").append(bdate);
			sqlWhere.append(" and p4 <= ").append(edate);
		}
		// 代付状态
		if (state != -1)
			sqlWhere.append(" and tstat = ").append(state);

		String selectFields = "select tseq,sys_date,sys_time,mid,mdate,oid,amount,pay_amt,type,gate,gid,fee_amt,tstat,p1,p2,p3,p7,p8,p10,data_source,bk_date ";
		StringBuffer selectSql = new StringBuffer();
		selectSql.append(selectFields + " from tlog " + sqlWhere);
		selectSql.append(" union all ");
		selectSql.append(selectFields + " from hlog " + sqlWhere);
		selectSql.append(" order by sys_date desc, sys_time desc");
		
		// 交易总金额为[0],系统手续费总金额[1],支付总金额[2]
		List<String> sumFields = new ArrayList<String>();
		sumFields.add(AppParam.AMT_SUM);
		sumFields.add(AppParam.SYS_AMT_FEE_SUM);
		sumFields.add(AppParam.PAY_AMT_SUM);
		
		StringBuffer sumSql = new StringBuffer();
		sumSql.append("select sum(rowCount) " + AppParam.ROW_COUNT + ",");
		sumSql.append("       sum(amount) " + AppParam.AMT_SUM + ",");
		sumSql.append("       sum(fee_amt) " + AppParam.SYS_AMT_FEE_SUM + ",");
		sumSql.append("       sum(pay_amt) " + AppParam.PAY_AMT_SUM);
		sumSql.append("  from (");
		sumSql.append("  select count(0) rowCount, sum(amount) amount, sum(fee_amt) fee_amt, sum(pay_amt) pay_amt from tlog " + sqlWhere);
		sumSql.append("   union all");
		sumSql.append("  select count(0) rowCount, sum(amount) amount, sum(fee_amt) fee_amt, sum(pay_amt) pay_amt from hlog " + sqlWhere);
		sumSql.append(") a");

		return queryForPageV1(sumSql.toString(), selectSql.toString(), pageNo, pageNum, TlogBean.class, sumFields);
	}
	
	@SuppressWarnings("unchecked")
	public List<TlogBean> downLoadInfo(String batchNo, Long tseq, Integer type,
			Integer dateType, Integer state, Integer bdate, Integer edate) {
		LoginUser u = getLoginUser();
		String mid = u.getMid();
		
		StringBuffer sqlWhere = new StringBuffer();
		sqlWhere.append(" where mid=" + Ryt.addQuotes(mid));
		// 交易类型
		if (type == -1)
			sqlWhere.append(" and (type = 11 or type = 12) ");
		else
			sqlWhere.append(" and type=").append(type);
		// 交易批次号
		if (!Ryt.empty(batchNo))
			sqlWhere.append(" and p8 = ").append(Ryt.addQuotes(batchNo));
		// 电银流水号
		if (tseq != null)
			sqlWhere.append(" and tseq=").append(tseq);
		// 日期类型
		if (dateType == 0) { // 申请代付日期
			sqlWhere.append(" and sys_date >= ").append(bdate);
			sqlWhere.append(" and sys_date <= ").append(edate);
		} else if (dateType == 1 || dateType == 2) { // 确认代付日期、撤销代付日期
			sqlWhere.append(" and p4 >= ").append(bdate);
			sqlWhere.append(" and p4 <= ").append(edate);
		}
		// 代付状态
		if (state != -1)
			sqlWhere.append(" and tstat = ").append(state);

		String selectFields = "select tseq,sys_date,sys_time,mid,mdate,oid,amount,pay_amt,type,gate,gid,fee_amt,tstat,p1,p2,p3,p7,p8,p10,data_source,bk_date ";
		StringBuffer selectSql = new StringBuffer();
		selectSql.append(selectFields + " from tlog " + sqlWhere);
		selectSql.append(" union all ");
		selectSql.append(selectFields + " from hlog " + sqlWhere);
		selectSql.append(" order by sys_date desc, sys_time desc");
		
		return query(selectSql.toString(), TlogBean.class);
	}
	
	
	/**
	 * 根据订单号查询支付渠道
	 * @param mid
	 * @param oid
	 * @return
	 */
	public int getGidByOid(String mid, String oid,Integer ordDate) {
		StringBuffer sql = new StringBuffer("select gid from hlog where mid=");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and oid=");
		sql.append(Ryt.addQuotes(oid));
		sql.append(" and sys_date=");
		sql.append(ordDate);
		return queryForInt(sql.toString());
	}
	
	/**
	 * 查询订单信息
	 * @param tseqList
	 * @param payState
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TlogBean> getOrders(String tseqList, int payState){
		StringBuffer sql = new StringBuffer();
		/*sql.append("select tseq,mid,amount,pay_amt,data_source from tlog where tseq in(" + tseqList + ") and tstat = " + payState);
		sql.append(" union all ");
		sql.append("select tseq,mid,amount,pay_amt,data_source from hlog where tseq in(" + tseqList + ") and tstat = " + payState);*/
		sql.append("select tseq,sys_date,sys_time,mid,mdate,oid,amount,pay_amt,type,gate,gid,fee_amt,tstat,p1,p2,p3,p7,p8,p10,data_source,bk_date,p6,mer_priv  from tlog where tseq in(" + tseqList + ") and tstat = " + payState);
		sql.append(" union all ");
		sql.append("select tseq,sys_date,sys_time,mid,mdate,oid,amount,pay_amt,type,gate,gid,fee_amt,tstat,p1,p2,p3,p7,p8,p10,data_source,bk_date,p6,mer_priv  from hlog where tseq in(" + tseqList + ") and tstat = " + payState);
		return query(sql.toString(), TlogBean.class);
	}
	/**
	 * 确认代付，修改订单状态
	 * @param tseqList
	 * @throws B2EException
	 */
	public void batchUpdateOrder(String tseqList) throws B2EException{
		List<String> lists=new ArrayList<String>();
		int date = DateUtil.today();
		int time = DateUtil.getCurrentUTCSeconds();
		
		StringBuffer sql1 = new StringBuffer();
		sql1.append("update tlog set bk_send = " + time);
		sql1.append(" , tstat=" + Constant.PayState.WAIT_PAY);
		sql1.append(" , bk_date=").append(date);
		sql1.append(" where tseq in(" + tseqList + ")");

		StringBuffer sql2 = new StringBuffer();
		sql2.append("update hlog set bk_send = " + time);
		sql2.append(" , tstat=" + Constant.PayState.WAIT_PAY);
		sql2.append(" , bk_date=").append(date);
		sql2.append(" where tseq in(" + tseqList + ")");

		lists.add(sql1.toString());
		lists.add(sql2.toString());

		String[] sqls = lists.toArray(new String[lists.size()]);
		int[] affectLines = batchSqlTransaction(sqls);
		if (affectLines == null) 
			throw new B2EException("订单处理异常!");
	}
	/**
	 * 代付撤销
	 * @param dfOrderList
	 * @throws Exception
	 */
	public void DfCancel(List<TlogBean> dfOrderList) throws Exception{
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		long payAmtSum = 0;
		// 修改订单状态
		List<String> sqlList = new ArrayList<String>();
		for (TlogBean item : dfOrderList) {
			StringBuffer tlog = new StringBuffer();
			tlog.append("update tlog set tstat=").append(Constant.PayState.CANCEL);
			tlog.append(" ,p4=").append(date);
			tlog.append(" ,p5=").append(time);
			tlog.append(" where tseq=").append(item.getTseq());
			sqlList.add(tlog.toString());
			
			StringBuffer hlog = new StringBuffer();
			hlog.append("update hlog set tstat=").append(Constant.PayState.CANCEL);
			hlog.append(" ,p4=").append(date);
			hlog.append(" ,p5=").append(time);
			hlog.append(" where tseq=").append(item.getTseq());			
			sqlList.add(hlog.toString());
			
			payAmtSum += item.getPay_amt();
			
		}
		// 退回冻结的金额
		String mid = getLoginUserMid();
		String balancesql = RecordLiveAccount.calUsableBalance(mid, mid, payAmtSum, Constant.RecPay.INCREASE);
		sqlList.add(balancesql);
		
		batchSqlTransaction2(sqlList);
	}
}
