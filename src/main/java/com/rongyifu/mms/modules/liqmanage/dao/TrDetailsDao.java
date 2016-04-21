package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.TrDetails;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class TrDetailsDao extends PubDao<TrDetails> {
	
	public int deleteByDateAndGid(int date,int gid){
		String sql = "DELETE FROM tr_details WHERE tr_date = ? AND gid = ?";
		Object[] args = {date,gid};
		return update(sql, args);
	}
	
	public CurrentPage<TrDetails> query(Integer pageNo,Integer pageSize,Integer gid,Integer jd,Integer bdate,Integer edate){
		if(bdate == null || edate == null || pageNo == null){
			return null;
		}
		String condition = queryCondition(gid, jd, bdate, edate);
		String fetchSql = "SELECT t.cur_code, t.tr_date,t.tr_time,t.rcvamt,t.payamt,t.fee_amt,t.opp_acno,t.acname,t.acno,t.opp_acname,t.balance,t.bank_name,t.summary,t.bk_serial_no,t.postscript FROM tr_details t"+condition;
		String countSql = "SELECT COUNT(*) FROM tr_details t" + condition;//,hlog h
		String rcvamtSql = "SELECT IFNULL(SUM(t.rcvamt),0) FROM tr_details t" + condition;
		String payamtSql = "SELECT IFNULL(SUM(t.payamt),0) FROM tr_details t" + condition;
		String feeAmtSql = "SELECT IFNULL(SUM(t.fee_amt),0) FROM tr_details t" + condition;
		Map<String,String>  sumSQLMap = new HashMap<String,String>();
		sumSQLMap.put("rcvamt", rcvamtSql);
		sumSQLMap.put("payamt", payamtSql);
		sumSQLMap.put("feeAmt", feeAmtSql);
		return queryForPage(countSql, fetchSql, pageNo, pageSize, TrDetails.class, sumSQLMap);
	}
	
	private String queryCondition(Integer gid,Integer jd,Integer bdate,Integer edate){
		StringBuilder condition = new StringBuilder();
		condition.append(" WHERE t.tr_date>=").append(bdate).append(" AND t.tr_date<=").append(edate);
		if(gid != null){
			condition.append(" AND t.gid = ").append(gid);
		}
		if(jd != null){
				condition.append(" AND t.jd_flag = ").append(jd);
		}
		condition.append(" ORDER BY t.tr_date DESC,t.tr_time DESC");
		return condition.toString();
	}
	
	public int[] saveTrDetails(List<TrDetails> list) throws Exception{
		List<Object[]> paramsList = new ArrayList<Object[]>();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO tr_details (gid,bk_serial_no,acno,acname,");
		sql.append("tr_bk_no,cur_code,tr_date,tr_time,tr_timestamp,old_tr_date,");
		sql.append("opp_acno,opp_acname,opp_bk_no,opp_bk_name,opp_cur_code,");
		sql.append("jd_flag,rcvamt,payamt,amt,fee_amt,balance,last_balance,");
		sql.append("freeze_amt,summary,postscript,cert_type,cert_batch_no,");
		sql.append("cert_no,old_serial_no,host_serial_no,tr_type,ch_flag,");
		sql.append("bk_flag,area_flag,tr_from,tr_flag,cash_flag,tr_code,user_no,");
		sql.append("sub_no,reserved1,reserved2,tr_bank_name,bank_no,bank_name,print_count");
		sql.append(")values(");
		sql.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
		sql.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		for (TrDetails td : list) {
			paramsList.add(new Object[]{td.getGid(),td.getBkSerialNo(),td.getAcno(),td.getAcname(),//4
										td.getTrBkNo(),td.getCurCode(),td.getTrDate(),td.getTrTime(),td.getTrTimestamp(),td.getOldTrDate(),//6
										td.getOppAcno(),td.getOppAcname(),td.getOppBkNo(),td.getOppBkName(),td.getOppCurCode(),//5
										td.getJdFlag(),td.getRcvamt(),td.getPayamt(),td.getAmt(),td.getFeeAmt(),td.getBalance(),td.getLastBalance(),//7
										td.getFreezeAmt(),td.getSummary(),td.getPostscript(),td.getCertType(),td.getCertBatchNo(),//5
										td.getCertNo(),td.getOldSerialNo(),td.getHostSerialNo(),td.getTrType(),td.getChFlag(),//5
										td.getBkFlag(),td.getAreaFlag(),td.getTrFrom(),td.getTrFlag(),td.getCashFlag(),td.getTrCode(),td.getUserNo(),//7
										td.getSubNo(),td.getReserved1(),td.getReserved2(),td.getBankName(),td.getBankNo(),td.getBankName(),td.getPrintCount()//7
										});
		}
		return batchSqlTransaction(sql.toString(), paramsList);
	}
}
