package com.rongyifu.mms.modules.transaction.dao;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.CurrentPage;

@SuppressWarnings("rawtypes")
public class QueryRefundDao extends PubDao{
	
	/**
	 * 退款查询
     * @return CurrentPage<RefundLog>
	 */	 
	@SuppressWarnings("unchecked")
	public CurrentPage<RefundLog> queryRefundLogs(Integer pageNo,Integer pageSize, String mid, String stat, String tseq,Integer dateState, Integer bdate,
					Integer edate, Integer gate, String orgid, Integer vstate,Integer gid,Integer mstate,Integer refundType,
					String begintrantAmt,String endtrantAmt) {
		StringBuffer sqlBuff=this.getRefundLogsSql(mid,stat,tseq,dateState,bdate,edate,gate,orgid,vstate,gid,mstate,refundType, begintrantAmt,endtrantAmt);
		String sqlFetchRows=sqlBuff.toString();
		String sqlCountRows=sqlFetchRows.replace("r.*, m.auth_no", "count(*)");
		String sqlSumAmt=sqlFetchRows.replace("r.*, m.auth_no", "COALESCE(sum(r.ref_amt),0)");
		String sqlMerRefFeeSum=sqlFetchRows.replace("r.*, m.auth_no", "COALESCE(sum(r.mer_fee),0)");
		Map<String, String> sumMap =new HashMap<String, String>();
		sumMap.put(AppParam.REF_FEE_SUM, sqlSumAmt);
		sumMap.put(AppParam.MER_REF_FEE_SUM, sqlMerRefFeeSum);
		
		return queryForPage(sqlCountRows, sqlFetchRows, pageNo,pageSize, RefundLog.class, sumMap);
	}
	
	// 退款查询的sql语句
	private StringBuffer getRefundLogsSql(String mid, String stat, String tseq,
			Integer dateState, Integer bdate, Integer edate, Integer gate,
			String orgid, Integer vstate, Integer gid, Integer mstate,
			Integer refundType, String begintrantAmt, String endtrantAmt) {
		StringBuffer condition = new StringBuffer();
		if(mstate != null)
			condition.append(" AND mo.mstate=").append(mstate);
		if (!Ryt.empty(mid))
			condition.append(" AND a.mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(stat))
			condition.append(" AND (a.stat = ").append(Ryt.sql(stat)).append(")");
		if (!Ryt.empty(tseq))
			condition.append(" AND a.tseq = ").append(Ryt.sql(tseq));
		if (refundType != null) {
			if (refundType.intValue() == Constant.RefundType.ONLINE_TO_MANUAL) // 联机退款 -人工经办
				condition.append(" AND a.gid in (" + RefundUtil.gateList+ ") AND a.refund_type = " + Constant.RefundType.ONLINE_TO_MANUAL);
			else if (refundType.intValue() == Constant.RefundType.ONLINE) { // 联机退款
				condition.append(" AND a.gid in (" + RefundUtil.gateList+ ") AND a.refund_type in ("+Constant.RefundType.MANUAL+","+Constant.RefundType.ONLINE+")");
			}else if(refundType.intValue() == Constant.RefundType.MANUAL){ // 人工经办
				condition.append(" AND a.gid not in (" + RefundUtil.gateList+ ") AND a.refund_type =").append(Constant.RefundType.MANUAL);
			} else {
				condition.append(" AND a.refund_type=").append(refundType);
			}
		}
		if (dateState == 1) {// 1为申请日期2为确认日期3为经办日期4为审核日期
			if (bdate != null)
				condition.append(" AND a.mdate >= ").append(bdate);
			if (edate != null)
				condition.append(" AND a.mdate <= ").append(edate);
		} else if (dateState == 2) {
			if (bdate != null)
				condition.append(" AND a.req_date >= ").append(bdate);
			if (edate != null)
				condition.append(" AND a.req_date <= ").append(edate);
		} else if (dateState == 3) {
			if (bdate != null)
				condition.append(" AND a.pro_date >= ").append(bdate);
			if (edate != null)
				condition.append(" AND a.pro_date <= ").append(edate);
		} else if (dateState == 4) {
			if (bdate != null)
				condition.append(" AND a.ref_date >= ").append(bdate);
			if (edate != null)
				condition.append(" AND a.ref_date <= ").append(edate);
		}
		if (gate != null)
			condition.append(" AND a.gate = ").append(gate);
		if (!Ryt.empty(orgid))
			condition.append(" AND a.org_oid = ").append(Ryt.addQuotes(orgid));
		if (vstate != null)
			condition.append(" AND a.vstate = ").append(vstate);
		if (gid != null)
			condition.append(" AND a.gid = ").append(gid);
		if (!Ryt.empty(begintrantAmt))
			condition.append(" AND a.ref_amt >= ").append(Ryt.mul100toInt(begintrantAmt));
		if (!Ryt.empty(endtrantAmt))
			condition.append(" AND a.ref_amt <= ").append(Ryt.mul100toInt(endtrantAmt));
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.*, m.auth_no");
		sql.append("  FROM (SELECT a.*");
		sql.append("          FROM refund_log a, minfo mo");
		sql.append("         WHERE a.mid = mo.id ");
		sql.append(condition.toString() + ") AS r");
		sql.append("  LEFT JOIN mlog m");
		sql.append("    ON r.tseq = m.tseq");
		
		return sql;
	}
		/**
		 * 根据Id查询RefundLog
		 * @param id
		 * @return RefundLog
		 */
		public RefundLog queryRefundLogById(Integer id) {
			RefundLog refundLog=null;
			try {
				refundLog=queryForObject("select r.*,m.auth_no from refund_log r left join mlog m on  r.tseq=m.tseq where r.id = ?"  , new Object[]{id},RefundLog.class);
			} catch (Exception e) {
//				e.printStackTrace();
			}
			return refundLog;
		}

}
