package com.rongyifu.mms.common;

import java.util.Map;


import org.apache.poi.ss.formula.functions.T;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.rongyifu.mms.bean.*;

public class GenSql {

	private String t;

	public GenSql() {
		super();
	}

	public GenSql(String t) {
		super();
		this.t = t;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RowMapper<T> getRowMapper() {
		if (this.t.equals("MERTLOG") || this.t.equals("MERHLOG")||this.t.equals("MERPHONEPAY")) {
			return new BeanPropertyRowMapper(Hlog.class);
		} else if (this.t.equals("ELOG")) {
			return new BeanPropertyRowMapper(Elog.class);
		} else if (this.t.equals("MERCOLLECT")) {
			return new BeanPropertyRowMapper(TlogCollect.class);
		}else if(this.t.equals("MERREFUNDQUERY")){
			return new BeanPropertyRowMapper(RefundLog.class);
		}else if(this.t.equals("ACCOUNT")){
			return new BeanPropertyRowMapper(Account.class);
		}else if(this.t.equals("MEROPERINFO")){
			return new BeanPropertyRowMapper(OperInfo.class);
		}
		return null;
	}

	public String[] getSqls(Map<String, String> p) {
		String[] sqls = new String[4];

		StringBuffer condition = new StringBuffer();

		if (this.t.equals("MERTLOG")) {
			condition.append(" FROM tlog WHERE tseq > 0 ");
			if (!Ryt.empty(p.get("mid"))) condition.append(" AND mid = " + p.get("mid"));
			if (!Ryt.empty(p.get("gate"))) condition.append(" AND gate = " + p.get("gate"));
			if (!Ryt.empty(p.get("tstat"))) condition.append(" AND tstat = " + p.get("tstat"));
			if (!Ryt.empty(p.get("type"))) condition.append(" AND type = " + p.get("type"));
			if (!Ryt.empty(p.get("tseq"))) condition.append(" AND tseq = " + p.get("tseq"));
			if (!Ryt.empty(p.get("oid"))) condition.append(" AND oid = " + Ryt.addQuotes(p.get("oid")));
			if (!Ryt.empty(p.get("authtype"))) condition.append(" AND auth_type = " + p.get("authtype"));
			
			if (!Ryt.empty(p.get("bkseq"))) {
				condition.append(" AND (bk_seq1 = " + Ryt.addQuotes(p.get("bkseq")));
				condition.append(" OR bk_seq2 = " + Ryt.addQuotes(p.get("bkseq")));
				condition.append(" )");
			}
			sqls[0] = " SELECT  COUNT(tseq) " + condition.toString();
			String sel =  "SELECT tseq,mid,oid,mdate,amount,type,gate,author_type,sys_date,tstat,bk_chk,fee_amt,bk_seq1,bk_seq2 " + condition.toString() ;
			if (!Ryt.empty(p.get("orderby"))){
				sel += p.get("orderby");
			} else{
				sel += " ORDER BY tseq DESC";
			}
			sqls[1] = sel;
			sqls[2] = " SELECT sum(amount)  " + condition.toString();
			sqls[3] =" SELECT sum(fee_amt)" + condition.toString();
		} else

		if (this.t.equals("MERHLOG")) {
			condition.append(" FROM hlog WHERE tseq > 0 ");
			if (!Ryt.empty(p.get("mid"))) condition.append(" AND mid = " + p.get("mid"));
			if (!Ryt.empty(p.get("gate"))) condition.append(" AND gate = " + p.get("gate"));
			if (!Ryt.empty(p.get("tstat"))) condition.append(" AND tstat = " + p.get("tstat"));
			if (!Ryt.empty(p.get("type"))) condition.append(" AND type = " + p.get("type"));
			if (!Ryt.empty(p.get("authtype"))) condition.append(" AND auth_type = " + p.get("authtype"));
			String qdate = p.get("date");
			if (!Ryt.empty(qdate)) {
				if (!Ryt.empty(p.get("bdate"))) condition.append(" AND " + qdate + " >= " + p.get("bdate"));
				if (!Ryt.empty(p.get("edate"))) condition.append(" AND " + qdate + " <= " + p.get("edate"));
			}
			if (!Ryt.empty(p.get("oid"))) condition.append(" AND oid = " + Ryt.addQuotes(p.get("oid")));
			sqls[0] = " SELECT  COUNT(tseq)  " + condition.toString();
			String sel =  "SELECT tseq,mid,oid,mdate,amount,type,gate,author_type,sys_date,tstat,bk_chk,fee_amt,bk_seq1,bk_seq2 " + condition.toString() ;
			if (!Ryt.empty(p.get("orderby"))){
				sel += p.get("orderby");
			} else{
				sel += " ORDER BY tseq DESC";
			}
			sqls[1] = sel;
			
			sqls[2] = " SELECT sum(amount)  " + condition.toString();
			sqls[3] =" SELECT sum(fee_amt)" + condition.toString();
			
		} else

		if (this.t.equals("ELOG")) {
			condition.append(" from elog where id >0 ");
			if (!Ryt.empty(p.get("mid"))) condition.append(" and mid = " + p.get("mid"));
			// 系统日期
			if (!Ryt.empty(p.get("bdate"))) condition.append(" and sys_date >= " + p.get("bdate"));
			if (!Ryt.empty(p.get("edate"))) condition.append(" and sys_date <= " + p.get("edate"));
			// 订单
			if (!Ryt.empty(p.get("oid"))) condition.append(" and oid = " + Ryt.addQuotes(p.get("oid")));
			sqls[0] = " SELECT  COUNT(*)  " + condition.toString();
			sqls[1] = " select mdate,mid,oid,amount,type,ip,sys_date,sys_time " + condition.toString()
							+ " order by oid desc,sys_date desc,sys_time desc";
		} else

		if (this.t.equals("MERCOLLECT")) {

			StringBuilder selectSqlCollect = new StringBuilder();
			String chosen = "sum(pay_suc_cnt) as pay_suc_cnt, sum(pay_suc_amt) as pay_suc_amt,"
							+ "sum(pay_fai_cnt) as pay_fai_cnt,sum(pay_fai_amt) as pay_fai_amt,sum(cnl_suc_cnt) as cnl_suc_cnt,"
							+ "sum(cnl_suc_amt) as cnl_suc_amt,sum(cnl_fai_cnt) as cnl_fai_cnt, sum(cnl_fai_amt) as cnl_fai_amt ";

			String timeType = p.get("timetype");
			// 从汇总表查数据 拼接SQL语句
			if (timeType.equals("day")) {
				selectSqlCollect.append("select mid,sys_date," + chosen + "  from tlog_collect where");
			} else if (timeType.equals("month")) {
				selectSqlCollect.append("select mid,year(sys_date)*100+month(sys_date) as sys_date," + chosen
								+ "  from tlog_collect where");
			} else if (timeType.equals("season")) {
				selectSqlCollect
					.append("select mid,concat(year(sys_date) ,((MONTH(sys_date)-1) DIV 3) +1 ) as sys_date ," + chosen
									+ "  from tlog_collect where");
			} else {
				selectSqlCollect
					.append("select mid,year(sys_date) as sys_date," + chosen + " from tlog_collect  where");
			}
			// mid
			if (!Ryt.empty(p.get("mid"))) {
				selectSqlCollect.append(" mid= " + p.get("mid") + " and ");
			}
			// 系统日期
			if (!Ryt.empty(p.get("bdate"))) {
				selectSqlCollect.append(" sys_date >= " + p.get("bdate") + " and ");
			}
			if (!Ryt.empty(p.get("edate"))) {
				selectSqlCollect.append(" sys_date <= " + p.get("edate") + " and ");
			}
			// 网关
			if (!Ryt.empty(p.get("gate"))) {
				selectSqlCollect.append(" gate = " + p.get("gate") + " and ");
			}

			String selectList = selectSqlCollect.toString();

			if (timeType.equals("day")) {
				selectList = selectList.substring(0, (selectList.length() - 5))
								+ "  group by sys_date,mid order by sys_date desc ";
			} else if (timeType.equals("month")) {
				selectList = selectList.substring(0, (selectList.length() - 5))
								+ "  group by year(sys_date)*100+month(sys_date),mid order by year(sys_date)*100+month(sys_date) desc ";
			} else if (timeType.equals("season")) {
				selectList = selectList.substring(0, (selectList.length() - 5))
								+ "  group by year(sys_date),((MONTH(sys_date)-1) DIV 3) +1,mid order by year(sys_date) desc ";
			} else {
				selectList = selectList.substring(0, (selectList.length() - 5))
								+ "  group by year(sys_date),mid order by year(sys_date) desc ";
			}

			sqls[0] = " select count(*) from ( " + selectList + " ) temp";
			sqls[1] = selectList;

		}else if(this.t.equals("MERPHONEPAY")){
			
			String t = p.get("table");
			if(t!=null&&t.equals("tlog")){
				condition.append(" From tlog t,mlog m WHERE m.chk_no !='' AND t.tseq=m.tseq AND t.mid = ").append(p.get("mid"));
			}else{
				condition.append(" From hlog t,mlog m WHERE m.chk_no !='' AND t.tseq=m.tseq AND t.mid = ").append(p.get("mid"));
				if (!Ryt.empty(p.get("bdate"))) condition.append(" and t.sys_date >=" + p.get("bdate"));
				if (!Ryt.empty(p.get("edate"))) condition.append(" and t.sys_date <=" + p.get("edate"));
			}
			if (!Ryt.empty(p.get("tstat"))) condition.append(" and t.tstat =" + p.get("tstat"));
			if (!Ryt.empty(p.get("operid"))) condition.append(" and m.oper_id = " + p.get("operid") );
			sqls[0] = " select count(*) " + condition.toString();
			
			StringBuffer selSql = new StringBuffer();
			selSql.append("select t.mid as mid,t.tseq as tseq,t.oid as oid ,t.amount as amount ,");
			selSql.append("t.sys_date as sys_date,t.sys_time as sys_time,t.tstat as tstat,");
			selSql.append("m.mobile_no as mobile_no,m.time_period as trans_period,m.oper_id as oper_id");
			sqls[1] = selSql.toString() + condition.toString() + " order by t.tseq desc" ;
			sqls[2] = " select sum(amount) " + condition.toString() ;
			
		}else if(this.t.equals("MERREFUNDQUERY")){
			//退款流水号  	商户号  	商户简称  	银行  	原融易通流水号  	退款单号  	退款申请日期  	退款金额  	退款状态
			String sel_SQL = " select * ";
			condition.append(" FROM refund_log where id > 0 ");
			if(!Ryt.empty(p.get("mid"))) condition.append(" AND mid = ").append(p.get("mid"));
			if(!Ryt.empty(p.get("tseq"))) condition.append(" AND tseq = ").append(p.get("tseq"));
			if(!Ryt.empty(p.get("bdate"))) condition.append(" AND mdate >= ").append(p.get("bdate"));
			if(!Ryt.empty(p.get("edate"))) condition.append(" AND mdate <= ").append(p.get("edate"));
			if(!Ryt.empty(p.get("gate"))) condition.append(" AND gate = ").append(p.get("gate"));
			if(!Ryt.empty(p.get("stat"))) condition.append(" AND (stat = ").append(p.get("stat")).append(")");
			if(!Ryt.empty(p.get("orgid"))) condition.append(" AND org_oid = ").append(Ryt.addQuotes(p.get("orgid")));
			if(!Ryt.empty(p.get("vstate"))) condition.append(" AND vstate = ").append(p.get("vstate"));
			
			sqls[0] = " select count(id) " + condition.toString();
			sqls[1] = sel_SQL + condition.toString() + " ORDER BY id DESC ";
			sqls[2] = " select sum(ref_amt) " + condition.toString();
			
		}else if(this.t.equals("ACCOUNT")){
			
			condition.append(" from account a,minfo m where a.mid = m.id ");
			if(!Ryt.empty(p.get("mid"))) condition.append(" and a.mid = ").append(p.get("mid"));
			if(!Ryt.empty(p.get("bdate"))) condition.append(" and a.date >= ").append(p.get("bdate"));
			if(!Ryt.empty(p.get("edate"))) condition.append(" and a.date <= ").append(p.get("edate"));
			condition.append(" order by a.mid,a.id DESC");
			sqls[0] = "select count(*) " + condition.toString();
			sqls[1]  = "select a.* ,m.liq_type " + condition.toString();
			
		}else if(this.t.equals("MEROPERINFO")){
			condition.append(" from  oper_info where mtype=0 ");
			if(!Ryt.empty(p.get("mid"))) condition.append(" and mid = ").append(p.get("mid"));
			sqls[0] = "select count(*) " + condition.toString();
			sqls[1]  = "select * " + condition.toString();
		}

		return sqls;
	}

}
