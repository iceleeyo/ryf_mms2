package com.rongyifu.mms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.db.datasource.CustomerContextHolder;
import com.rongyifu.mms.utils.LogUtil;

@SuppressWarnings("rawtypes")
public class BillListDownloadDao extends PubDao {
	
	Logger log4j = Logger.getLogger(BillListDownloadDao.class); 
	
	private JdbcTemplate jt = getJdbcTemplate();
	/**
	 * 支付对账单下载
	 * @param map
	 * @return
	 */
	public List<Hlog> queryPayBill(Map<String, String> map){
		String mid=Ryt.sql(map.get("mid"));
		String tstat=Ryt.sql(map.get("tstat"));
		String gate=Ryt.sql(map.get("gate"));
		String date_type=Ryt.sql(map.get("date_type"));
		String date_begin=Ryt.sql(map.get("date_begin"));
		String date_end=Ryt.sql(map.get("date_end"));
		String type=Ryt.sql(map.get("type"));
		
	     StringBuffer hlogSql = new StringBuffer("select h.mid,h.oid,h.mdate,h.amount,h.fee_amt,h.tstat,h.type,h.tseq,h.bk_chk,h.gate,h.sys_date,h.sys_time");
	        hlogSql.append(" from hlog h where h.mid = ").append(Ryt.addQuotes(mid));
	        hlogSql.append(" and h.tstat =  ").append(tstat);
			// 网关
			if(!Ryt.empty(gate)) hlogSql.append(" and h.gate = ").append(gate);
			
			if(!Ryt.empty(date_type)){
				if(!Ryt.empty(date_begin)) hlogSql.append(" and ").append("h."+date_type).append(" >= ").append(date_begin);
				if(!Ryt.empty(date_end)) hlogSql.append(" and ").append("h."+date_type).append(" <= ").append(date_end);
			}
			if(!Ryt.empty(type)) hlogSql.append(" and h.type = ").append(type);
		 StringBuffer hlogSql1 = new StringBuffer("select t.mid,t.oid,t.mdate,t.amount,t.fee_amt,t.tstat,t.type,t.tseq,t.bk_chk,t.gate,t.sys_date,t.sys_time");
		        hlogSql1.append(" from tlog t where t.mid = ").append(Ryt.addQuotes(mid));
		        hlogSql1.append(" and t.tstat =  ").append(tstat);
			 	// 网关
		    if(!Ryt.empty(gate)) hlogSql1.append(" and t.gate = ").append(gate);
				
		    if(!Ryt.empty(date_type)){
			if(!Ryt.empty(date_begin)) hlogSql1.append(" and ").append("t."+date_type).append(" >= ").append(date_begin);
			if(!Ryt.empty(date_end)) hlogSql1.append(" and ").append("t."+date_type).append(" <= ").append(date_end);
				}
			if(!Ryt.empty(type)) hlogSql1.append(" and t.type = ").append(type);
				hlogSql1.append(" order by sys_date desc, sys_time desc");
				hlogSql.append(" union all ").append(hlogSql1);
			List<Hlog> hloglist = jt.query(hlogSql.toString(),new BeanPropertyRowMapper<Hlog>(Hlog.class));
		return hloglist;		
	}
	/**
	 * 退款对账单下载
	 * @param map
	 * @return
	 */
	public List<RefundLog> queryBackBill(Map<String, String> map){
		String mid=Ryt.sql(map.get("mid"));
		String gate=Ryt.sql(map.get("gate"));
		String date_type=Ryt.sql(map.get("date_type"));
		String date_begin=Ryt.sql(map.get("date_begin"));
		String date_end=Ryt.sql(map.get("date_end"));
		StringBuffer refund_logSql = new StringBuffer("select id, mid,ref_amt,mer_fee,tseq,req_date,org_oid,org_mdate,gate,mdate,pro_date,stat ");
		//退款状态成功为2
		refund_logSql.append(" from refund_log  where stat=2 and mid = ").append(Ryt.addQuotes(mid));
        // 网关
		if(!Ryt.empty(gate)) refund_logSql.append(" and gate = ").append(gate);
		//选择的日期字段
		if(!Ryt.empty(date_type)){
			if(!Ryt.empty(date_begin)) refund_logSql.append(" and ").append(date_type).append(" >= ").append(date_begin);
			if(!Ryt.empty(date_end)) refund_logSql.append(" and ").append(date_type).append(" <= ").append(date_end);
		}
		refund_logSql.append(" order by pro_date desc, id desc");
		List<RefundLog> refundLoglist = jt.query(refund_logSql.toString(),new BeanPropertyRowMapper<RefundLog>(RefundLog.class));
		return refundLoglist;
	}
	/**
	 * 支付账单下载 （接口） 本方法默认系统时间（sys_date）
	 * @param mid 商户号
	 * @param tstat 支付状态 0–初始状态 1–待支付 2–成功 3–失败
	 * @param date 指定日期 
	 * @return 返回指定商户支付成功的账单
	 */
	 
	public List<Hlog> iqueryPayBill(String mid,String tstat,String date,String dateField){
		 StringBuffer sql = new StringBuffer("select tseq,mid,oid,mdate,amount,tstat,type,gate,fee_Amt,sys_date");
		 sql.append(" from hlog  where mid = ").append(Ryt.addQuotes(mid));
		 sql.append(" and tstat =  ").append(tstat);
		 sql.append(" and ").append(dateField).append(" = ").append(date);
		 sql.append(" order by tseq desc");
		 
		 LogUtil.printInfoLog("iqueryPayBill: " +sql.toString());
		 
		 CustomerContextHolder.setSlave();
	     List<Hlog> list= jt.query(sql.toString(),new BeanPropertyRowMapper<Hlog>(Hlog.class));	
	     LogUtil.printInfoLog("iqueryPayBill: orderNum=" + list.size());
	     return list;
	}
	/**
	 * 退款账单下载（接口）  本方法默认系统时间（sys_date）
	 * @param mid 商户号
	 * @param date 指定日期 
	 * @return 返回指定商户退款成功的账单
	 * 退款流水号	原电银流水号	原商户订单号	原银行流水号	原交易日期	           原交易金额（元）  	原交易银行	  退款金额（元）	  退还商户手续费(元)	经办日期	    退款确认日期	退款状态 
	 * id       tseq       org_oid     org_bk_seq  org_mdate   org_amt     gid      ref_amt     mer_fee        pro_date  req_date   stat
	 */
	public List<RefundLog> iqueryBackBill(String mid ,String date, String dateField){
		StringBuffer sql = new StringBuffer("select id,oid,tseq,org_oid,org_bk_seq,org_mdate,org_amt,gid,ref_amt,mer_fee,pro_date,req_date,mid,stat,mdate");
		//退款状态成功为2
		sql.append(" from refund_log  where stat in(2,3) and mid = ").append(Ryt.addQuotes(mid));
		sql.append(" and ").append(dateField).append(" = ").append(date);
		sql.append(" order by pro_date desc, id desc");
		
		LogUtil.printInfoLog("iqueryBackBill: " + sql.toString());
		
		CustomerContextHolder.setSlave();
		List<RefundLog> refundLoglist = jt.query(sql.toString(),new BeanPropertyRowMapper<RefundLog>(RefundLog.class));
		LogUtil.printInfoLog("iqueryBackBill: refundNum=" + refundLoglist.size());
		return refundLoglist;
	}
	/**
	 * 得到所有银行
	 * @return 该方法返回所有银行 
	 */
	public Map<String, String> getAllGate(){
		Map<String, String> map=new HashMap<String, String>();
		String sql="select gate,gate_name from ryt_gate";
		List<RYTGate> list=jt.query(sql,new BeanPropertyRowMapper<RYTGate>(RYTGate.class));
		for (RYTGate rytGate : list) {
			map.put(rytGate.getGate()+"", rytGate.getGateName());
		}
		return map;
	}
}
