package com.rongyifu.mms.modules.transaction.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.OnlineRefund;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundProccessor;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.refund.bank.UnionPayRefund;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("rawtypes")
public class OnlineRefundDao extends PubDao {
	
	/*
	 * 联机退款查询
	 */	
	@SuppressWarnings("unchecked")
	public CurrentPage<RefundLog> queryLJRefundJBLogs(Integer pageNo,Integer pageSize,String mid, String stat,
			Integer bdate, Integer edate, Integer mstate,String tseq,String gid) {
		StringBuffer sqlBuff=this.getLJRefundJBLogsSql(mid,stat,bdate,edate,mstate,tseq,gid);
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
	 * 联机退款sql语句查询
	 */
	private StringBuffer getLJRefundJBLogsSql(String mid, String stat,
			Integer bdate, Integer edate, Integer mstate,String tseq,String gid) {
		
		StringBuffer condition = new StringBuffer();
		if (mstate != null)
			condition.append(" AND mo.mstate=").append(mstate);
//		if(!Ryt.empty(gid)) //新增查询条件 -原支付渠道	
//			condition.append(" AND a.gid=").append(gid);
//		else 
		condition.append(" AND a.gid in (" + RefundUtil.gateList + ")");		
		if (!Ryt.empty(mid))
			condition.append(" AND a.mid = ").append(Ryt.addQuotes(mid));
		if (!Ryt.empty(stat))
			if(stat.equals("0")){
			condition.append(" AND a.refund_type =0 AND a.stat=1 AND (a.online_refund_state = ").append(Ryt.sql(stat)).append(")");	
			}else{
			condition.append(" AND a.refund_type =1 AND (a.online_refund_state = ").append(Ryt.sql(stat)).append(")");
			}
		if (bdate != null)
			condition.append(" AND a.req_date >= ").append(bdate);
		if (edate != null)
			condition.append(" AND a.req_date <= ").append(edate);
		if(!Ryt.empty(tseq))
			condition.append(" AND a.tseq = ").append(Ryt.sql(tseq));
		
		StringBuffer sql = new StringBuffer();
		sql.append("select r.*,m.auth_no");
		sql.append("  from (select a.* from refund_log a, minfo mo where a.mid = mo.id ");
		sql.append(condition);
		sql.append("  ) as r");
		sql.append("  left join mlog m");
		sql.append("    on r.tseq = m.tseq");
		if(!Ryt.empty(gid)) //新增查询条件 -原支付渠道	
			sql.append(" where r.gid=").append(gid);
		
		return sql;
	}
	
	/**
	 * 查询当天联机退款记录
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RefundLog> queryTodayOnlineRefundList(String mid){
		
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select r.*,m.auth_no as authNo");
		sqlBuff.append(" from refund_log r");
		sqlBuff.append(" left join mlog m");
		sqlBuff.append(" on r.tseq = m.tseq");
		sqlBuff.append(" where r.pro_date = DATE_FORMAT(NOW(),'%Y%m%d')");
		sqlBuff.append(" and r.ref_date = DATE_FORMAT(NOW(),'%Y%m%d')");
		sqlBuff.append(" and r.online_refund_state = 2");
		sqlBuff.append(" and r.refund_type = 1");
		sqlBuff.append(" and r.gid in (" + RefundUtil.gateList + ")");
		if (!Ryt.empty(mid))
			sqlBuff.append(" and r.mid=").append(Ryt.addQuotes(mid));
		return query(sqlBuff.toString(), RefundLog.class);
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
	
	
	public int getRefundFee(String temp) {
		return  queryForInt("select refund_fee from minfo where id =" + Ryt.addQuotes(temp));
	}
	
	public long getBalance(String temp) {
		return queryBalance(temp);
	}
	/**
	 * 退款经办的数据库处理   优化后代码
	 * @author yuanzy 
	 * @param mid 商户号
	 * @param refFee 退款退回手续费标识
	 * @param sqlsList sql语句的list
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
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
		//改廉艳举		2014年4月14日 11:27:29
		if((RefundUtil.ORDER_STATUS_SUCCESS).equals(String.valueOf(ref.getOnlineRefundState()))){
			int refundType = ref.getRefundType().intValue() == 2 ? ref.getRefundType().intValue() : 1;
			
			sqlsList.add("update refund_log r set  r.stat = 3,r.vstate=1,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state=2,r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+refundType+",r.pro_date = " + d + ",r.ref_date="+ d +",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
			//sqlsList.add("update refund_log r set  r.stat = 3,r.vstate=1,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state=2,r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+refundType+",r.pro_date = " + d + ",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
		}else{
			//不更新退款审核日期
			int refundType = ref.getRefundType().intValue() == 2 ? ref.getRefundType().intValue() : 1;
			sqlsList.add("update refund_log r set  r.stat = 2,r.vstate=0,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state=3,r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+refundType+",r.pro_date = " + d + ",r.mer_fee ="+ dbMerFee + ",r.bk_fee ="+dbysFee+",bk_fee_real = "+dbssFee+" where r.id = " + ref.getId());
		}
		
		//如下是对商户的金额进行修改
		List<String> sqls=genAddAccSeqSqls(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, Constant.REFUND_LOG, ref.getId()+"", "退款");
		sqlsList.addAll(sqls);
		batchSqlTransaction2(sqlsList);
		sqlsList.clear();
	}
	
	//联机退款-人工经办 从refundHandle方法中分离
	@SuppressWarnings("unchecked")
	public void manualHandlingRefund(RefundLog ref, int refFee ,ArrayList<String> sqlsList) throws Exception {
		int d = DateUtil.today();
		
		//退回给商户的手续费,银行退回的手续费,实收银行退回的手续费的计算
		RefundLog rl= RefundMoneyCal(ref,refFee);
		//商户余额-退款金额+退回的手续费
		// 更新退款记录状态不更新退款审核日期	
		int refundType = ref.getRefundType().intValue() == 2 ? ref.getRefundType().intValue() : 1;
		
		sqlsList.add("update refund_log r set  r.stat = 2,r.vstate=0,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state=3,r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+refundType+",r.pro_date = " + d + ",r.mer_fee ="+ rl.getMerFee() + ",r.bk_fee ="+rl.getBkFee()+",bk_fee_real = "+rl.getBkFeeReal()+" where r.id = " + ref.getId());

		//如下是对商户的金额进行修改
		changeBalance(ref,sqlsList,rl.getMerFee());
		
	}
	
	//联机退款-联机退款     从refundHandle方法中分离
		@SuppressWarnings("unchecked")
		public void onlinerefundHandle(RefundLog ref, int refFee ,ArrayList<String> sqlsList) throws Exception {
			int d = DateUtil.today();
			
			//退回给商户的手续费,银行退回的手续费,实收银行退回的手续费的计算
			RefundLog rl= RefundMoneyCal(ref,refFee);
			//商户余额-退款金额+退回的手续费
			// 更新退款记录状态不更新退款审核日期	
			int refundType = ref.getRefundType().intValue() == 2 ? ref.getRefundType().intValue() : 1;
			
			sqlsList.add("update refund_log r set  r.stat = 3,r.vstate=1,r.online_refund_id='"+ref.getOnlineRefundId()+"', r.online_refund_state=2,r.online_refund_reason='"+ref.getOnlineRefundReason()+"',r.refund_type="+refundType+",r.pro_date = " + d + ",r.ref_date="+ d +",r.mer_fee ="+ rl.getMerFee() + ",r.bk_fee ="+rl.getBkFee()+",bk_fee_real = "+rl.getBkFeeReal()+" where r.id = " + ref.getId());

			//如下是对商户的金额进行修改
			changeBalance(ref,sqlsList,rl.getMerFee());
			
		}
		
	
	//退回给商户的手续费,银行退回的手续费,实收银行退回的手续费的计算
	public RefundLog RefundMoneyCal(RefundLog ref,int refFee){
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
		RefundLog rl=new RefundLog();
		rl.setBkFee(dbysFee);
		rl.setBkFeeReal(dbssFee);
		rl.setMerFee(dbMerFee);
		return rl;
	}
	
	//退款功能商户金额变动
	public void changeBalance(RefundLog ref,ArrayList<String> sqlsList,int dbMerFee) throws Exception{
		List<String> sqls=genAddAccSeqSqls(ref.getMid(), ref.getMid(), ref.getRef_amt(), dbMerFee, (ref.getRef_amt()-dbMerFee),1, Constant.REFUND_LOG, ref.getId()+"", "退款");
		sqlsList.addAll(sqls);
		batchSqlTransaction2(sqlsList);
		sqlsList.clear();
	}
	// 联机退款订单同步
		public int OnlinerefundStateSynchro(Integer id, String tseq) {
			return queryForInt("select  online_refund_state from refund_log where id  = "+ id + " and tseq = " + Ryt.addQuotes(tseq));
		}
		
		/*
		 * 联机退款
		 */
		public synchronized OnlineRefundBean OnlinerefundHandle(RefundLog ref) throws Exception  {

			OnlineRefundBean onlinerefundbean = new OnlineRefundBean();
			RefundProccessor refundprocessor = new RefundProccessor();
			String bathno = Ryt.crateBatchNumber();			
			Integer id=ref.getId();
			//先从数据库查询。如果online_refund_id存在值说明已经提交退款了		
			String online_refund_id = queryForString("select  online_refund_id  from refund_log where id  = "+ id);
			
			if(!Ryt.empty(online_refund_id)){
				throw new Exception("重复退款!");
			}  
			
			try {				
				String tseq=ref.getTseq();
				onlinerefundbean.setId(ref.getId());
				onlinerefundbean.setRefBatch(bathno);
				onlinerefundbean.setOrgTseq(tseq);
				onlinerefundbean.setRefundReason(ref.getRefund_reason());
				onlinerefundbean.setRefAmt(ref.getRef_amt());
				onlinerefundbean.setBkTseq(ref.getOrgBkSeq());
				onlinerefundbean.setGid(ref.getGid());
				// lyj 提交给银行的订单号就是电银的流水号 补充到10位
				onlinerefundbean.setOrderNumber(UnionPayRefund.handle_orderNum(ref.getTseq()));
				onlinerefundbean.setMerId(ref.getMid());
				onlinerefundbean.setOrgOrderDate(ref.getOrg_mdate());
				//lyh提交银联订单时间为hlog 或 tlog的sys_date与systime处理后的连接字符串
				onlinerefundbean.setSysTime(ref.getSys_time());
				onlinerefundbean.setSysDate(ref.getSys_date());
				// 退款执行
				onlinerefundbean = refundprocessor.proccess(onlinerefundbean);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return onlinerefundbean;

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

		public CurrentPage<OnlineRefund> queryOnlineRefunds(Integer pageNo,Integer pageSize,Map<String, String> param) {
			//select a.ref_bk_seq,a.tseq,a.mid,a.org_oid,a.gid,a.gate,a.org_amt,a.ref_amt,a.req_bk_date,a.ref_status from online_refund
			StringBuffer condition=getConditionSql(param);
			String sqlCount = " SELECT COUNT(a.tseq) " + condition.toString();
			String sql = "select a.id,a.ref_bk_seq,a.tseq,a.mid,a.org_oid,a.gid,a.gate,a.org_amt,a.ref_amt,a.req_bk_date,a.ref_status " +
							 condition.toString() + " order by id desc";
			// 交易总金额为[0],退款总金额[1]
//			String amtSumSql = " SELECT sum(a.org_amt)  " + condition.toString();
			String sysAtmFeeSumSql = " SELECT sum(a.ref_amt)" + condition.toString();
				Map<String,String> sumSQLMap=new HashMap<String,String>();
//				sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
				sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
				return queryForPage(sqlCount,sql, pageNo,pageSize,OnlineRefund.class,sumSQLMap);
			
			
		}
		private StringBuffer getConditionSql(Map<String, String> param){
			StringBuffer condition = new StringBuffer();
			
			condition.append(" FROM ").append(" online_refund"+" as a, minfo m ").append(" WHERE  a.mid=m.id ");
			if (!Ryt.empty(param.get("mid"))) condition.append(" AND a.mid = "+ Ryt.addQuotes(param.get("mid")));
			if (!Ryt.empty(param.get("gate"))) condition.append(" AND a.gate = " + param.get("gate"));
			if (!Ryt.empty(param.get("stat")) ) condition.append(" AND a.ref_status = " + param.get("stat"));
			
			if (!Ryt.empty(param.get("tseq"))) condition.append(" AND a.tseq = " + Ryt.addQuotes(param.get("tseq")));
			
			if (!Ryt.empty(param.get("gateRouteId")) ) condition.append(" AND a.gid = " + param.get("gateRouteId"));
			if(!Ryt.empty(param.get("mstate"))){
				condition.append(" AND m.mstate= " + param.get("mstate"));
			}
			if(!Ryt.empty(param.get("rId"))){
				condition.append(" AND a.id= " + param.get("rId"));
			}
			if(!Ryt.empty(param.get("refundType"))){
				condition.append(" AND a.ref_status= " + param.get("refundType"));
			}
			if (!Ryt.empty(param.get("bdate"))) {
				condition.append(" AND a.req_bk_date >= " + param.get("bdate"));
			}
			if (!Ryt.empty(param.get("edate"))){
				condition.append(" AND a.req_bk_date <= " + param.get("edate"));
			} 
			
			if(!Ryt.empty(param.get("begintrantAmt"))){
				condition.append(" AND a.ref_amt>= " + Ryt.mul100toInt(param.get("begintrantAmt")));
			}
			if(!Ryt.empty(param.get("endtrantAmt"))){
				condition.append(" AND a.ref_amt<=" + Ryt.mul100toInt(param.get("endtrantAmt")));
			}
			return condition;
			}

		public OnlineRefund getOnlineRefund(long id) {
			// TODO Auto-generated method stub
			String sql ="select * from online_refund where id="+id;
			
			return queryForObject(sql.toString(), OnlineRefund.class);
		}
		
	}
