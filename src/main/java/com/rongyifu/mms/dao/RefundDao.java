package com.rongyifu.mms.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.RefundOB;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundProccessor;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RefundDao extends PubDao {
	public final String TK="TKSXFTH"; //退款手续费退回

	private RefundOB queryRefundOB(String queryStr) {
		RefundOB refundOB = null;
		
		try{
			refundOB  = queryForObjectThrowException(queryStr,RefundOB.class);
		} catch(Exception e) {			
		}
		
		if (refundOB == null) {
			queryStr = queryStr.replaceAll("hlog", "tlog");
			refundOB = queryForObject(queryStr, RefundOB.class);
		}
		if (refundOB == null) return null;
		try {
			// 高阳捷迅的
			if (refundOB.getAuthorType()!= null && refundOB.getAuthorType() == Constant.UMP_AUTH_TYPE) {
				refundOB.setRefundCount(0);
			}
			String refundFlagSql = "select refund_flag from minfo  where id = '" +refundOB.getMid()+"'";
			refundOB.setMidRefundFlag(queryForInt(refundFlagSql));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refundOB;
	}

	public RefundOB queryRefundOBByTseq(String tseq, String loginMid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select tseq,mdate,oid,pay_amt,gate,type,mid,sys_date,fee_amt,tstat,refund_amt,");
		sql.append("author_type,gid,trans_period,amount as org_amt,bk_seq1 as org_bk_seq,");
		sql.append("pay_amt as org_pay_amt,pre_amt as pre_amt,pre_amt1 as preAmt1,sys_time");
		sql.append(" from hlog where  tseq = ").append(Ryt.addQuotes(tseq));
		if (!"1".equals(loginMid) ){
			sql.append(" and mid = ").append(Ryt.addQuotes(loginMid));
		}
		return queryRefundOB(sql.toString());

	}

	public RefundOB queryRefundOBByMer(String merOid, String mid, int merDate,String loginMid) {
		merOid=Ryt.sql(merOid);
		String querymid = loginMid.equals("1")? mid : loginMid;
		StringBuilder sql = new StringBuilder();
		sql.append("select tseq,mdate,oid,pay_amt,gate,type,mid,sys_date,fee_amt,tstat,refund_amt,");
		sql.append("author_type,gid,trans_period,amount as org_amt,bk_seq1 as org_bk_seq,");
		sql.append("pay_amt as org_pay_amt,pre_amt as pre_amt,pre_amt1 as preAmt1,sys_time");
		sql.append(" from hlog where  mid =  ").append(Ryt.addQuotes(querymid));
		sql.append(" and oid = '").append(merOid).append("'");
		
		if(merDate>0)
		sql.append(" and mdate = ").append(merDate);
		return queryRefundOB(sql.toString() + " order by tseq desc limit 1 ");
	}
	
	/**
	 * 商户退款申请的数据持久化 向refund_log中插入记录,更新tlog或hlog中的总申请退款金额 refund_amt
	 * @author yuanzy 2010-7-31
	 * @param refundOB 退款交易记录实体
	 * @param refAmt 退款金额(实际金额*100)
	 * @throws Exception
	 */
	public String refundApply(RefundOB refundOB, long refAmt) throws Exception {
		String refundOid = newRefundOid();
		ArrayList<String> sqlsList = new ArrayList<String>();
		StringBuilder refundSQL = new StringBuilder();
		refundSQL.append("insert into refund_log(tseq,author_type,gid,mdate,mid,oid,org_mdate,org_oid,ref_amt,pre_amt,sys_date,sys_time,");
		refundSQL.append("gate,card_no,user_name,req_date,pro_date,ref_date,stat,refund_reason,org_amt,org_bk_seq,org_pay_amt,pre_amt1) values(");
		refundSQL.append(refundOB.getTseq()).append(",");
		refundSQL.append(refundOB.getAuthorType()).append(",");
		refundSQL.append(refundOB.getGid()).append(",");
		refundSQL.append(DateUtil.today()).append(",");
		refundSQL.append(refundOB.getMid()).append(",");
		refundSQL.append(refundOid).append(",");
		refundSQL.append(refundOB.getMdate()).append(",'");
		refundSQL.append(refundOB.getOid()).append("',");
		refundSQL.append(refAmt).append(",");
		refundSQL.append(refundOB.getPreAmt()).append(",");
		refundSQL.append(refundOB.getSysDate()).append(",");
		refundSQL.append(refundOB.getSysTime()).append(",");
		refundSQL.append(refundOB.getGate()).append(",'");
		refundSQL.append( 0+ "',"+ 0+ ","+ 0+ ","+ 0+ ","+ 0+ "," + 5 + ",'" + Ryt.sql(refundOB.getRefundReason()) + "',");
		refundSQL.append(refundOB.getOrgAmt()).append(",");
		refundSQL.append("'"+refundOB.getOrgBkSeq()).append("',");
		refundSQL.append(refundOB.getOrgPayAmt()).append(",");
		refundSQL.append(refundOB.getPreAmt1()).append(")");
		// 更新交易表中的总申请退款金额
		long sumRefund = refAmt + refundOB.getRefundAmt();
		String t = refundOB.getSysDate() == DateUtil.today() ? Constant.TLOG : Constant.HLOG;
		try {
			queryForInt("select fee_amt from "+ t+" where tseq = "+Ryt.addQuotes(refundOB.getTseq()));
		} catch (Exception e) {
			t=t.equals(Constant.TLOG)?Constant.HLOG:Constant.TLOG;
			
		}
		String updateSQL = "update "+t+" h set h.refund_amt= " + sumRefund + " where h.tseq = " + Ryt.addQuotes(refundOB.getTseq());
		sqlsList.add(refundSQL.toString());
		sqlsList.add(updateSQL);
		batchSqlTransaction2(sqlsList);
		sqlsList.clear();
		return refundOid;
	}
	
	public void updateRefund2Verify(int nowdate, String ids) {
		update("update refund_log r set r.req_date=" + nowdate + ", r.stat=1 " + "where r.id in " + Ryt.sql(ids)
			+ " and r.stat= 5 ");
	}

	public List query4ref_amt_sum(int id, String tseq, int sys_date) {
		// 获得同一原始订单已成功退款总额
		String selectSql = "select sum(ref_amt) as ref_amt_sum from refund_log where stat=3 and tseq=" + Ryt.addQuotes(tseq);
	
		int today = DateUtil.today();
		String selectTlogOrHlog = "";
		// 判断原始交易是否当天交易
		if (today == sys_date) {
			selectTlogOrHlog = " select ip,amount,refund_amt,fee_amt from tlog where tseq = " + Ryt.addQuotes(tseq);
		} else {
			selectTlogOrHlog = " select ip,amount,refund_amt,fee_amt from hlog where tseq =" + Ryt.addQuotes(tseq);
		}
		// 获得单笔退款单的详细
		String selectRefundLog = "select * from refund_log where id =" + id;
		List l = new ArrayList();
		Integer ref_amt_sum = 0;
		Hlog hlog = null;
		RefundLog rl = null;		
		ref_amt_sum = queryForInt(selectSql);
		hlog = queryForObject(selectTlogOrHlog, Hlog.class);
		rl = queryForObject(selectRefundLog,RefundLog.class);
		l.add(ref_amt_sum);
		l.add(hlog);
		l.add(rl);
		return l;
	}
	
	public RefundLog getRefundLogById(String refundId) {
		String queryRefundLog = "select mid, tseq ,stat,sys_date,ref_amt from refund_log where  id = " + refundId;
		return queryForObject(queryRefundLog, RefundLog.class);
	}
	
	public void verifyRefund(int nowdate, String ids) {
		String updateRefLog = "update refund_log r set r.ref_date=" + nowdate + ",r.vstate = 1,r.reason = ''"
						+ " where r.id in " + ids;
		update(updateRefLog);
	}

	public void verifySure(int nowdate, int i) {
		String updateRefLog = "update refund_log r set r.req_date=" + nowdate + ", r.stat=1 " + "where r.id = " + i
						+ " and r.stat=5";
		update(updateRefLog);
	}
	
	public long getBalance(String temp) {
//		return  queryForInt("select balance from minfo where id =" + temp);
		return queryBalance(temp);
	}
	
	public int getRefundFee(String temp) {
		return  queryForInt("select refund_fee from minfo where id =" + Ryt.addQuotes(temp));
	}

	public Hlog queryForHlogObject(String sql) {
		return queryForObject(sql, Hlog.class);
	}

	/**
	 * 退款经办的数据库处理   优化后代码
	 * @author yuanzy 
	 * @param mid 商户号
	 * @param refFee 退款退回手续费标识
	 * @param sqlsList sql语句的list
	 * @throws Exception
	 */
	public void refundHandle(RefundLog ref, int refFee ,ArrayList<String> sqlsList) throws Exception {
		
		int d = DateUtil.today();
		  
		Map<String,Object> m = queryForMap("select  fee_model,fee_flag from gates where ryt_gate  = " + ref.getGate() +" and gid = " + ref.getGid());
		String bkmode = m.get("fee_model").toString();
		String ysFee = ChargeMode.reckon(bkmode, Ryt.div100(ref.getRef_amt()));//计算手续费
		String ssFee = "0";
		try {
			if(Integer.parseInt(m.get("fee_flag").toString())==1) ssFee = ysFee;
		} catch (Exception e) {
		}
		//商户手续费
		String  merFee = "0";
		
		int feeAmt=0;

		 		
		if(refFee==1){
			//商户银退回行手续费计算
			String tableName = ref.getSys_date() == d ? Constant.TLOG : Constant.HLOG ;
			try {
				feeAmt = queryForInt("select fee_amt from " + tableName
						+ " where tseq=" + Ryt.addQuotes(ref.getTseq()));
			} catch (Exception e) {
				feeAmt = queryForInt("select fee_amt from "
						+ (tableName.equals(Constant.TLOG) ? Constant.HLOG : Constant.TLOG)
						+ " where tseq=" + Ryt.addQuotes(ref.getTseq()));
			}
			if(feeAmt!=0){
				double doubleNum=(double)ref.getRef_amt()*feeAmt/(ref.getOrgAmt()*100);
				merFee=Ryt.formathHalfUp(doubleNum);
			}
		}
		int dbMerFee = Integer.parseInt(Ryt.mul100(merFee));
		int dbysFee = Integer.parseInt(Ryt.mul100(ysFee));
		int dbssFee = Integer.parseInt(Ryt.mul100(ssFee));
		//商户余额-退款金额+退回的手续费
		// 更新退款记录状态
		if((RefundUtil.ORDER_STATUS_SUCCESS).equals(ref.getOnlineRefundState())){
			sqlsList.add("update refund_log r set  r.stat = 3,r.vstate=1,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state="+ref.getOnlineRefundState()+",r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+ref.getRefundType()+",r.pro_date = " + d + ",r.ref_date="+ d +",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
			
			//sqlsList.add("update refund_log r set  r.stat = 3,r.vstate=1,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state="+ref.getOnlineRefundState()+",r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+ref.getRefundType()+",r.pro_date = " + d + ",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
			
		}else{
			//去掉审核日期的更新
		sqlsList.add("update refund_log r set  r.stat = 2,r.vstate=0,r.online_refund_id=null, r.online_refund_state=0,r.online_refund_reason=null,r.refund_type=0,r.pro_date = " + d + ",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
		}
		// 更新商户余额
		
		//String  accSeqSql = genAddAccSeqSql(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, "refund_log", ref.getId().toString(), "退款");
				//sqlsList.add(accSeqSql);
		List<String> sqls=genAddAccSeqSqls(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, Constant.REFUND_LOG, ref.getId()+"", "退款");
		sqlsList.addAll(sqls);
		batchSqlTransaction2(sqlsList);
		//genAddAccSeqSql(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, "refund_log", ref.getId(), "退款");
//		genAddAccSeqSql(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, "refund_log", ref.getId()+"", "退款");
		/*增加存管数据*/
		//TODO 测试是否正确？
		//createCGData(ref,d,t,dbMerFee);
		sqlsList.clear();
	}
	
	/*
	 * 联机退款
	 */
	public OnlineRefundBean OnlinerefundHandle(RefundLog ref){
		OnlineRefundBean onlinerefundbean =new OnlineRefundBean();
		RefundProccessor refundprocessor=new RefundProccessor();
		String bathno=Ryt.crateBatchNumber();
		String sql="update refund_log r set r.online_refund_id='"+bathno+"' where r.id = " + ref.getId();
		try {
			update(sql.toString());
			onlinerefundbean.setId(ref.getId());
			onlinerefundbean.setRefBatch(bathno);
			onlinerefundbean.setOrgTseq(ref.getTseq());
			onlinerefundbean.setRefundReason(ref.getRefund_reason());
			onlinerefundbean.setRefAmt(ref.getRef_amt());
			onlinerefundbean.setBkTseq(ref.getOrgBkSeq());
			onlinerefundbean.setGid(ref.getGid());
			onlinerefundbean= refundprocessor.proccess(onlinerefundbean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return onlinerefundbean;
		
	}
	/**
	 * dbMerFee 商户退款的手续费
	 * @param ref
	 * @param d
	 * @param t
	 * @param dbMerFee
	 */
	/**
	private void createCGData(RefundLog ref,int d,int t,int dbMerFee) {
		StringBuffer sql1 = new StringBuffer();
		sql1.append("insert into tr_seq (liq_date,obj_id,tr_date,tr_time,tr_amt,tr_type,tr_flag,sys_date)");
		sql1.append(" values (0,");
		sql1.append(ref.getMid()).append(",");
		sql1.append(d).append(",");
		sql1.append(t).append(",");
		sql1.append(ref.getRef_amt()).append(",");
		sql1.append(2).append(",");//2-退款
		sql1.append(ref.getId()).append(",");
		sql1.append(DateUtil.today());
		sql1.append(")");
		
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append("insert into tr_seq (liq_date,Obj_id,Tr_date,Tr_time,Tr_amt,Tr_type,tr_flag,sys_date)");
		sql2.append(" values (0,");
		sql2.append(ref.getMid()).append(",");
		sql2.append(d).append(",");
		sql2.append(t).append(",");
		sql2.append(dbMerFee).append(",");
		sql2.append(3).append(",");//3-退款退回手续费
		sql2.append(ref.getId()).append(",");
		sql2.append(DateUtil.today());
		sql2.append(")");
		
		int amt = (ref.getRef_amt() - dbMerFee);
		
		String bkNo = getBKNOByPayChannel(ref.getGid());
		if(bkNo==null) return;
		
		String sql3 = "update bk_account set bk_bl = bk_bl - " + amt+", bf_bl = bf_bl-"+amt + " where bk_no='"+bkNo+"'";
		
		try {
			// 增加 退款金额流水
			update(sql1.toString());
			//增加退款手续费金额流水
			if(dbMerFee>0)
			update(sql2.toString());
			//修改银行余额数据
			update(sql3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
**/
	/**
	 * 商户退款申请的数据持久化 向refund_log中插入记录,更新tlog或hlog中的总申请退款金额 refund_amt
	 * @author cody 2010-7-13
	 * @param hlog 交易记录实体
	 * @param isToday Y今天的交易 N历史交易
	 * @param refAmt 退款金额(实际金额*100)
	 * @param refundReason 退款原因
	 * @param sqlsList sql语句的list
	 * @throws Exception
	 */
	public void refundApply(Hlog hlog, String isToday, int refAmt, String refundReason, ArrayList<String> sqlsList)
					throws Exception {
		sqlsList.add("insert into refund_log(tseq,author_type,mdate,mid,oid,org_mdate,org_oid,ref_amt,sys_date,gate,"
						+ "card_no,user_name,req_date,pro_date,ref_date,stat,refund_reason) values("
						+ hlog.getTseq()
						+ ","
						+ hlog.getAuthorType()
						+ ","
						+ DateUtil.today()
						+ ","
						+ hlog.getMid()
						+ ",'"
						+ newRefundOid()
						+ "',"
						+ hlog.getMdate()
						+ ",'"
						+ hlog.getOid()
						+ "',"
						+ refAmt
						+ ","
						+ hlog.getSysDate()
						+ ","
						+ hlog.getGate()
						+ ",'"
						+ 0
						+ "',"
						+ 0
						+ ","
						+ 0
						+ ","
						+ 0
						+ ","
						+ 0
						+ "," + 5 + ",'" + refundReason + "')");

		// 更新交易表中的总申请退款金额
		long sumRefund = refAmt + hlog.getRefundAmt();
		String updatgeHlogRefAmount = "update hlog h set h.refund_amt= " + sumRefund + " where h.tseq = " + hlog.getTseq();

		// 判断原始交易是否是当天交易
		if (isToday.equalsIgnoreCase("Y")) {
			updatgeHlogRefAmount = updatgeHlogRefAmount.replace("hlog", "tlog");
		}
		sqlsList.add(updatgeHlogRefAmount);
		batchSqlTransaction2(sqlsList);
		sqlsList.clear();
	}

	/**
	 * 生成退款订单号取当前时间的后12位和4位随机数拼接 不足4位的融易通流水号前面补0
	 * @author cody 2010-7-13
	 * @return 退款订单号
	 */
	public String newRefundOid() {
		String RefundOid = DateUtil.getIntDateTime() + "";// 取当前时间
		String randomStr = String.valueOf(new Random().nextInt(10000));
		// 融易通流水号不足4为 前面补0
		if (randomStr.length() < 4) {
			randomStr = String.format("%04d", Integer.parseInt(randomStr));
		}
		String newRefundOid = RefundOid.substring(2, RefundOid.length()) + randomStr;
		return newRefundOid;
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
//			e.printStackTrace();
		}
		return refundLog;
	}
	/**
	 * 退款申请撤销
	 * @param refLog
	 * @param tableName
	 * @param refundAmt
	 * @return
	 */
	public int[] refundCancel(RefundLog refLog,String tableName,int refundAmt){
		int nowdate = DateUtil.today();
		String updateHlog = "";
		updateHlog = "update " + tableName + " h set h.refund_amt =" + (refundAmt - refLog.getRef_amt()) + " where h.tseq = " + Ryt.addQuotes(refLog.getTseq());
		// 更新退款表中订单的状态 和撤销原因 退款撤销的状态为 6
		String updateRefLog = "update refund_log r set r.online_refund_id=null, r.online_refund_state=0,r.online_refund_reason=null,r.refund_type=0,r.pro_date=" + nowdate + ", r.stat=6,r.etro_reason='" + 
									Ryt.sql(refLog.getEtro_reason()) + "'" + " where r.id = " + refLog.getId();
		
		String[] sqls=new String[]{updateHlog,updateRefLog};
		return batchSqlTransaction(sqls);
	}
	/**
	 * 查询退款金额
	 * @param tableName
	 * @param refLogTseq
	 * @return
	 */
	public int queryRefundAmt(String tableName,String refLogTseq){
		String selectFromHlog = "select refund_amt from " + tableName + " where tseq ="+Ryt.addQuotes(refLogTseq);
	
		return queryForInt(selectFromHlog);
	}
	//退款查询的sql语句
	private StringBuffer getRefundLogsSql(String mid, String stat, String tseq,
			Integer dateState, Integer bdate, Integer edate, Integer gate,
			String orgid, Integer vstate, Integer gid, Integer mstate,
			Integer refundType) {
		
		StringBuffer condition = new StringBuffer();
		if (mstate != null)
			condition.append(" AND mo.mstate=").append(mstate);
		if (!Ryt.empty(mid))
			condition.append(" AND a.mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(stat))
			condition.append(" AND (a.stat = ").append(Ryt.sql(stat)).append(")");
		if (!Ryt.empty(tseq))
			condition.append(" AND a.tseq = ").append(Ryt.sql(tseq));
		if (refundType != null)
			condition.append(" AND a.refund_type=").append(refundType);
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
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.*,m.auth_no");
		sql.append("  FROM (SELECT a.* FROM refund_log a, minfo mo WHERE a.mid = mo.id ");
		sql.append(condition);
		sql.append("  ) AS r");
		sql.append("  LEFT JOIN mlog m");
		sql.append("    ON r.tseq = m.tseq");
		
		return sql;
	}
	
	//退款经办查询的sql语句
	private StringBuffer getRefundJBLogsSql(String mid, String stat, String tseq,Integer dateState, Integer bdate,
			Integer edate, Integer gate, String orgid, Integer vstate,Integer gid,Integer mstate){
		 
		StringBuffer condition = new StringBuffer();
		if (mstate != null)
			condition.append(" AND mo.mstate=").append(mstate);
		if (!Ryt.empty(mid))
			condition.append(" AND a.mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(stat))
			condition.append(" AND (a.stat = ").append(Ryt.sql(stat)).append(")");
		if (!Ryt.empty(tseq))
			condition.append(" AND a.tseq = ").append(Ryt.sql(tseq));
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
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.*,m.auth_no");
		sql.append("  FROM (SELECT a.*");
		sql.append("          FROM refund_log a, minfo mo");
		sql.append("         WHERE a.mid = mo.id");
		sql.append(condition);
		sql.append("           AND a.gid NOT IN (" + RefundUtil.gateList + ")) as r");
		sql.append("  LEFT JOIN mlog m");
		sql.append("    ON r.tseq = m.tseq");
		sql.append(" ORDER BY r.gid desc");
		
		return sql;
	}
	
	/*
	 * 联机退款sql语句查询
	 */
	private StringBuffer getLJRefundJBLogsSql(String mid, String stat,
			Integer bdate, Integer edate, Integer mstate) {
		
		StringBuffer condition = new StringBuffer();
		if (mstate != null)
			condition.append(" AND mo.mstate=").append(mstate);
		if (!Ryt.empty(mid))
			condition.append(" AND r.mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(stat))
			condition.append(" AND (r.online_refund_state = ").append(Ryt.sql(stat)).append(")");
		if (bdate != null)
			condition.append(" AND r.req_date >= ").append(bdate);
		if (edate != null)
			condition.append(" AND r.req_date <= ").append(edate);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.*,m.auth_no");
		sql.append("  FROM (SELECT a.*");
		sql.append("          FROM refund_log a, minfo mo");
		sql.append("         WHERE a.mid = mo.id");
		sql.append(condition);
		sql.append("           AND a.stat not in (2,3,6,5,7)");
		sql.append("           AND a.gid NOT IN (" + RefundUtil.gateList + ")) as r");
		sql.append("  LEFT JOIN mlog m");
		sql.append("    ON r.tseq = m.tseq");
		
		return sql;
	}
	/**
	 * 退款查询
     * @return CurrentPage<RefundLog>
	 */	 
	public CurrentPage<RefundLog> queryRefundLogs(Integer pageNo,Integer pageSize, String mid, String stat, String tseq,Integer dateState, Integer bdate,
					Integer edate, Integer gate, String orgid, Integer vstate,Integer gid,Integer mstate,Integer refundType) {
		StringBuffer sqlBuff=this.getRefundLogsSql(mid,stat,tseq,dateState,bdate,edate,gate,orgid,vstate,gid,mstate,refundType);
		String sqlFetchRows=sqlBuff.toString();
		String sqlCountRows=sqlFetchRows.replace("r.*,m.auth_no", "count(*)");
		String sqlSumAmt=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.ref_amt),0)");
		String sqlMerRefFeeSum=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.mer_fee),0)");
		Map<String, String> sumMap =new HashMap<String, String>();
		sumMap.put(AppParam.REF_FEE_SUM, sqlSumAmt);
		sumMap.put(AppParam.MER_REF_FEE_SUM, sqlMerRefFeeSum);
		return queryForPage(sqlCountRows, sqlFetchRows, pageNo,pageSize, RefundLog.class, sumMap);
	}
	
	/**
	 * 退款经办查询
     * @return CurrentPage<RefundLog>
	 */	 
	public CurrentPage<RefundLog> queryRefundJBLogs(Integer pageNo,Integer pageSize, String mid, String stat, String tseq,Integer dateState, Integer bdate,
					Integer edate, Integer gate, String orgid, Integer vstate,Integer gid,Integer mstate) {
		StringBuffer sqlBuff=this.getRefundJBLogsSql(mid,stat,tseq,dateState,bdate,edate,gate,orgid,vstate,gid,mstate);
		String sqlFetchRows=sqlBuff.toString();
		String sqlCountRows=sqlFetchRows.replace("r.*,m.auth_no", "count(*)");
		String sqlSumAmt=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.ref_amt),0)");
		String sqlMerRefFeeSum=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.mer_fee),0)");
		Map<String, String> sumMap =new HashMap<String, String>();
		sumMap.put(AppParam.REF_FEE_SUM, sqlSumAmt);
		sumMap.put(AppParam.MER_REF_FEE_SUM, sqlMerRefFeeSum);
		return queryForPage(sqlCountRows, sqlFetchRows, pageNo,pageSize, RefundLog.class, sumMap);
	}
	
	
	/*
	 * 联机退款查询
	 */	
	public CurrentPage<RefundLog> queryLJRefundJBLogs(Integer pageNo,Integer pageSize,String mid, String stat,
			Integer bdate, Integer edate, Integer mstate) {
		StringBuffer sqlBuff=this.getLJRefundJBLogsSql(mid,stat,bdate,edate,mstate);
		String sqlFetchRows=sqlBuff.toString();
		String sqlCountRows=sqlFetchRows.replace("r.*,m.auth_no", "count(*)");
		String sqlSumAmt=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.ref_amt),0)");
		String sqlMerRefFeeSum=sqlFetchRows.replace("r.*,m.auth_no", "COALESCE(sum(r.mer_fee),0)");
		Map<String, String> sumMap =new HashMap<String, String>();
		sumMap.put(AppParam.REF_FEE_SUM, sqlSumAmt);
		sumMap.put(AppParam.MER_REF_FEE_SUM, sqlMerRefFeeSum);
		return queryForPage(sqlCountRows, sqlFetchRows, pageNo,pageSize, RefundLog.class, sumMap);
	}
	/**
	 * 查询当天退款记录
	 * @return
	 */
	public List<RefundLog> queryTodayRefundList(String mid) {

		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select r.*,m.auth_no as authNo ");
		sqlBuff.append(" from refund_log r ");
		sqlBuff.append(" left join mlog m ");
		sqlBuff.append(" on r.tseq=m.tseq ");
		sqlBuff.append(" where r.pro_date=DATE_FORMAT(NOW(),'%Y%m%d') ");
		sqlBuff.append(" and r.stat=2 and r.gid not in (" + RefundUtil.gateList+ ")");
		if (!Ryt.empty(mid))
			sqlBuff.append(" and r.mid=").append(Ryt.addQuotes(mid));
		sqlBuff.append(" order by r.gid desc").append(";"); //检索时以原支付渠道排序
		return query(sqlBuff.toString(), RefundLog.class);
	}
	
	/**
	 * 查询当天联机退款记录
	 * @return
	 */
	public List<RefundLog> queryTodayOnlineRefundList(String mid){
		
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select r.*,m.auth_no as authNo");
		sqlBuff.append(" from refund_log r");
		sqlBuff.append(" left join mlog m");
		sqlBuff.append(" on r.tseq = m.tseq");
		sqlBuff.append(" where r.pro_date = DATE_FORMAT(NOW(),'%Y%m%d')");
		sqlBuff.append(" and r.ref_date = DATE_FORMAT(NOW(),'%Y%m%d')");
		sqlBuff.append(" and r.stat = 2");
		sqlBuff.append(" and r.online_refund_state = 2");
		sqlBuff.append(" and r.refund_type = 1");
		sqlBuff.append(" and r.gid in (" + RefundUtil.gateList + ")");
		if (!Ryt.empty(mid))
			sqlBuff.append(" and r.mid=").append(Ryt.addQuotes(mid));
		return query(sqlBuff.toString(), RefundLog.class);
	}
	
	
	// 联机退款订单同步
	public int OnlinerefundStateSynchro(Integer id, String tseq) {
		return queryForInt("select  online_refund_state from refund_log where id  = "+ id + " and tseq = " + Ryt.addQuotes(tseq));
	}
	
	/**
	 * 统计各个订单总退款金额是否超过交易金额
	 * @param tseqList
	 * @return
	 */
	public Map<String, Integer> queryRefundAmtIsExceedTransAmt(String tseqList){
		Map<String, Integer> finalResult = new HashMap<String, Integer>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT tseq,");
		sql.append("       CASE");
		sql.append("         WHEN ref_amt > org_amt THEN 1");
		sql.append("         ELSE 0");
		sql.append("       END ref_flag");
		sql.append("  FROM (SELECT tseq,");
		sql.append("               org_amt,");
		sql.append("               Sum(ref_amt) ref_amt");
		sql.append("        FROM   refund_log");
		sql.append("        WHERE  tseq IN( " + tseqList + " )");
		sql.append("          AND  stat IN( 1, 2, 3)");
		sql.append("        GROUP  BY tseq,");
		sql.append("                  org_amt) a");
		
		List<Map<String, Object>> queryResult = this.queryForList(sql.toString());
		
		for(Map<String, Object> item : queryResult){
			String tseq = String.valueOf(item.get("tseq"));
			Integer refFlag = Integer.parseInt(String.valueOf(item.get("ref_flag")));
			
			finalResult.put(tseq, refFlag);
		}
		return finalResult;
	}
	
	public Map<String, Object> getUpmpMerInfo(){
		String sql="select mer_key,p1,p2,p3 from gate_route where gid= '55001'";
		return queryForMap(sql);
	}
}
