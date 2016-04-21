package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.infosec.util.Base64;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.AccSyncDetail;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.SettleDetail;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.dbutil.SqlGenerator;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@SuppressWarnings("rawtypes")
public class SettlementVerifyDao extends PubDao {

	// 查询结算渠道
	public int queryLiqGid(String batch) {
		String sql = "select liq_gid from fee_liq_bath where batch ='" + batch
				+ "'";
		return queryForInt(sql);
	}
	
	/**
	 * 根据批次号得到FeeLiqBath对象
	 * @return
	 */
	@Override
	public FeeLiqBath getFeeLiqBathByBatch(String batch){
		final String sql = "select * from fee_liq_bath where batch='" + batch+"'";
		return queryForObject(sql,FeeLiqBath.class);
	
	}
	/**
	 * 结算确认的详细信息
	 * @param batch 结算批次号
	 * @return 返回一个list 所选中的记录以及该条记录信息的详细
	 */
	public List<SettleDetail> getSettleDetail(String batch) {
		batch = Ryt.sql(batch);
		String paySql = "select tseq,gate,amount,0 as ref_amt,amount as tradeAmt,fee_amt ,0 as mer_fee from hlog where type!=4 and batch='"
				+ batch+"'";
		String refSql = "select id as tseq,gate,0 as amount,ref_amt,(-1)*ref_amt as tradeAmt,0 as fee_amt,mer_fee from refund_log where batch='"
				+ batch+"'";
		List<SettleDetail> detailList = query(paySql,SettleDetail.class);
		detailList.addAll(query(refSql,SettleDetail.class));
		return detailList;
	}

	/*
	 * 根据商户号查询代付的信息
	 */
	public Minfo queryAccBymid(String mid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select pbk_acc_name as pbkAccName,pbk_acc_no as pbkAccNo,pbk_no as pbkNo, pbk_gate_id as pbkGateId,pbk_prov_id as pbkProvId");
		sql.append(", bank_acct_name as bankAcctName,bank_acct as bankAcct,open_bk_no as openBkNo,gate_id as gateId,bank_prov_id as bankProvId");
		sql.append(" from minfo where id=").append(Ryt.addQuotes(mid));
		return queryForObject(sql.toString(), Minfo.class);
	}

	/*
	 * 根据商户好查询配置允许访问的IP地址
	 */
	public String queryBytmsIp(String mid) {
		String sql = " select par_value from global_params where par_name='TMS_IP'";
		return queryForString(sql);
	}

	// 自动结算、自动代付订单录入tlog
    //切换新代付渠道，Tstat 默认为1
	public Map<String, String> insertliqtlog(String mid, String payAmt,
			Integer gate, String AccNo, String AccName,String bankName, String bankBranch, String OpenBkNo,
			String dfType, String data_source, String liqBatch,
			String transType, String pbkProvId) throws Exception {
		FeeCalcMode feeCalcMode = getFeeModeByGate(mid, gate + "");
		int transFee = (int) Ryt.mul100toInt(ChargeMode.reckon(feeCalcMode.getCalcMode(), payAmt));
		long trantAmt = Ryt.mul100toInt(payAmt) - transFee;
		java.text.DateFormat format3 = new java.text.SimpleDateFormat(
				"yyyyMMdd");
		String date = format3.format(new Date());
		int time = Ryt.getCurrentUTCSeconds();
		String oid = Ryt.crateBatchNumber();
		Integer dfType1 = null;
		if (dfType.equals("A")) {
			dfType1 = 11;
		} else if (dfType.equals("B")) {
			dfType1 = 12;
		}
		String msg = null;
		if (data_source.equals("7")) {
			msg = "自动代付";
		} else if (data_source.equals("8")) {
			msg = "自动结算";
		}
		String bk_url = "";
		String fg_url = "";
		TlogBean tlog = new TlogBean();
		tlog.setVersion(10);
		tlog.setIp(new Long(0));
		tlog.setMdate(Integer.parseInt(date));
		tlog.setMid(mid);
		tlog.setBid(mid);
		tlog.setOid(oid);
		tlog.setAmount(trantAmt);
		tlog.setType(dfType1);
		tlog.setGate(gate);
		tlog.setSys_date(Integer.parseInt(date));
		tlog.setInit_sys_date(Integer.parseInt(date));
		tlog.setSys_time(time);
		tlog.setTstat(1);
		tlog.setBk_flag(0);
		tlog.setMer_priv(Base64.encode(msg));
		tlog.setBk_url(bk_url);
		tlog.setFg_url(fg_url);
		tlog.setTrans_period(30);
		tlog.setPay_amt(Ryt.mul100toInt(payAmt));
		tlog.setCard_no(Ryt.desEnc(AccNo)+"|noDec");
		tlog.setBk_send(time);
		tlog.setP1(Ryt.desEnc(AccNo)+"|noDec");
		tlog.setP2(AccName);
		tlog.setP3(OpenBkNo);
		tlog.setP4(bankName); //开户银行名称
		tlog.setP5(bankBranch); //开户支行名称
		tlog.setP6("0");
		tlog.setP7(Base64.encode(msg));
		tlog.setP9(liqBatch);
		tlog.setP10(pbkProvId);
		tlog.setIs_liq(1);
		tlog.setFee_amt(transFee);
		tlog.setGid(feeCalcMode.getGid());
		tlog.setData_source(Integer.parseInt(data_source));
		SqlGenerator sqlGenerator = new SqlGenerator();
		String sql = sqlGenerator.generateInsertSql(tlog);
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql", sql);
		map.put("oid", oid);
		map.put("date", date.toString());
		return map;

	}

	/**
	 * 通过网关获取手续费
	 * 
	 * @param mid
	 * @param gate
	 * @return
	 * @throws Exception
	 */
	public FeeCalcMode getFeeModeByGate(String mid, String gate)
			throws Exception {
		StringBuffer sql = new StringBuffer("select calc_mode,gid from fee_calc_mode where mid =");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and gate=");
		sql.append(gate);
		FeeCalcMode mode = queryForObject(sql.toString(), FeeCalcMode.class);
		if (null == mode)
			throw new Exception("该网关尚未配置");
		return mode;
	}

	/**
	 * 结算对象为账户时
	 * 
	 * @param bathNo
	 * @return
	 */
	public int[] liqaccount(FeeLiqBath mid, String remark, String tlogsql) {
		AccSeqs acc = new AccSeqs();
		acc.setUid(mid.getMid());
		acc.setAid(mid.getMid());
		acc.setAmt(mid.getLiqAmt());
		acc.setTrAmt(mid.getLiqAmt());
		acc.setTrFee(0);
		acc.setTbName(Constant.FEE_LIQ_BATH);
		acc.setTbId(mid.getBatch());
		acc.setRemark(remark);
		List<String> sqlsList = RecordLiveAccount.LiqToAccount(acc);
		String sql = "update fee_liq_bath set state=3 where batch='"+ Ryt.sql(mid.getBatch()) + "'";
		sqlsList.add(sql);
		sqlsList.add(tlogsql);
		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
		int[] liqcon = batchSqlTransaction(sqls);

		return liqcon;
	}

	// 查询hlog流水号
	public String getTlogTseq(String mid, String oid, String date,
			Integer gate, String AccNo, String AccName, String OpenBkNo) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select tseq from tlog where mid=").append(Ryt.addQuotes(mid));
		sql.append(" and oid=").append(Ryt.addQuotes(oid));
		sql.append(" and mdate=").append(date);
		sql.append(" and gate=").append(gate);
		sql.append(" and p1=").append(Ryt.addQuotes(AccNo));
		sql.append(" and p2=").append(Ryt.addQuotes(AccName));
		sql.append(" and p3=").append(Ryt.addQuotes(OpenBkNo));
		LogUtil.printInfoLog("SettlementVerifyDao", "getTlogTseq", "Sql:"+sql.toString());
		String tseq=new String(this.getJdbcTemplate().queryForObject(sql.toString(),String.class));
		return tseq;
	}

	/**
	 * 查询商户类型
	 * 
	 * @param bathNo
	 * @return
	 */

	public int querymertype(String mid) {
		String sql = "select mer_type from minfo where id ='" + mid + "'";
		return queryForInt(sql);
	}

	public int[] liqbankcard(FeeLiqBath mid) {
		AccSeqs acc = new AccSeqs();
		acc.setUid(mid.getMid());
		acc.setAid(mid.getMid());
		acc.setAmt(mid.getLiqAmt());
		acc.setTrAmt(mid.getLiqAmt());
		acc.setTrFee(0);
		acc.setTbName(Constant.FEE_LIQ_BATH);
		acc.setTbId(mid.getBatch());
		acc.setRemark("结算到银行卡");
		List<String> sqlsList = RecordLiveAccount.LiqToBankCard(acc);
		String sql = "update fee_liq_bath set state=3 where batch='"+ Ryt.sql(mid.getBatch()) + "'";
		sqlsList.add(sql);
		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
		int[] liqcon = batchSqlTransaction(sqls);

		return liqcon;
	}

	//通过代理商号查询代理商  %%(暂时没用到)
//	public String querydlsmid(String dlsCode) {
//		String sql = "select id from minfo where dls_code="+Ryt.addQuotes(dlsCode)+" and category=3 limit 1";
//	 		return queryForString(sql);
//	 
//	 	}

	/**
	 * 结算对象为代理商时
	 * 
	 * @param bathNo
	 * @return
	 */
//	public int[] liqDls(FeeLiqBath mid, String remark, String dlsmid) {
//		AccSeqs acc = new AccSeqs();
//		acc.setUid(mid.getMid());
//		acc.setAid(mid.getMid());
//		acc.setAmt(mid.getLiqAmt());
//		acc.setTrAmt(mid.getLiqAmt());
//		acc.setTrFee(0);
//		acc.setTbName(Constant.FEE_LIQ_BATH + "(1)");
//		acc.setTbId(mid.getBatch());
//		acc.setRemark(remark);
//
//		AccSeqs accDls = new AccSeqs();
//		accDls.setUid(dlsmid);
//		accDls.setAid(dlsmid);
//		accDls.setAmt(mid.getLiqAmt());
//		accDls.setTrAmt(mid.getLiqAmt());
//		accDls.setTrFee(0);
//		accDls.setTbName(Constant.FEE_LIQ_BATH + "(2)");
//		accDls.setTbId(mid.getBatch());
//		accDls.setRemark(remark);
//
//		List<String> sqlsList = RecordLiveAccount.LiqToDls(acc, accDls);
//		String sql = "update fee_liq_bath set state=3 where batch='"+ Ryt.sql(mid.getBatch()) + "'";
//		sqlsList.add(sql);
//		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
//		int[] liqcon = batchSqlTransaction(sqls);
//
//		return liqcon;
//	}
	
	/**
	 * 查询fee_liq_bath表并分页
	 * @param mid 商户号
	 * @param state 结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate查询结束日期
	 * @param batch查询批次号
	 * @param pageIndex 页码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate, Integer liqGid,String gid) {
		String selSql = "select f.mid,f.trans_amt,f.ref_amt,(f.trans_amt-ref_amt)as tradeAmt,f.fee_amt,f.manual_add,f.manual_sub,f.liq_amt,";
		selSql += " f.batch,f.liq_date,f.state,f.ref_fee,m.dls_code,m.is_today_liq,m.gate_id,m.pbk_gate_id from fee_liq_bath f,minfo m";

		StringBuffer condSql = new StringBuffer(" where f.id > 0 and f.mid=m.id ");
		if (beginDate != 0)
			condSql.append(" and f.liq_date >= ").append(beginDate);
		if (endDate != 0)
			condSql.append(" and f.liq_date <= ").append(endDate);
		if (state == 0) {// 结算制表中 0 表示全部 结算确认中 -1表示全部
			condSql.append(" and f.state in (1,2)");
		} else if (state == -1) {
			condSql.append(" and f.state in (2,3)");
		} else {
			condSql.append(" and f.state =" + state);
		}
		if (!mid.trim().equals(""))
			condSql.append(" and f.mid='"+ mid+"'");
		if (!batch.trim().equals(""))
			condSql.append(" and f.batch='" + batch+"'");
		
		if(mstate!=null)condSql.append(" and m.mstate=").append(mstate);
		// 结算对象
		if(liqGid != null) condSql.append(" and f.liq_gid=").append(liqGid); 
		String querySql = selSql + condSql.toString();
		StringBuffer finalsql=new StringBuffer(" select * from (select a.*,b.gid from ("+querySql+") as a");
		finalsql.append(" left join fee_calc_mode b on  a.mid=b.mid ");
		if(liqGid==3){
			finalsql.append(" and a.gate_id=b.gate ) as c ");
		}else{
			finalsql.append(" and a.pbk_gate_id=b.gate ) as c");
		}
		if(!Ryt.empty(gid)){
			finalsql.append(" where c.gid=").append(gid);
		}
		String countSql = "select count(*) from ( "+finalsql+") d;";
		return queryForPage(countSql, finalsql.toString(), pageNo,new AppParam().getPageSize(),FeeLiqBath.class);
	}


    // 查询hlog流水号
	public TlogBean getTlog(String mid, String oid, String date)throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from tlog where mid=").append(Ryt.addQuotes(mid));
		sql.append(" and oid=").append(Ryt.addQuotes(oid));
		sql.append(" and mdate=").append(date);
		LogUtil.printInfoLog("SettlementVerifyDao", "getTlogTseq", "Sql:"+sql.toString());
        try {
//            TlogBean data=(TlogBean)query(sql.toString(), TlogBean.class).get(0);
            TlogBean data=(TlogBean)this.getJdbcTemplate().queryForObject(sql.toString(), new BeanPropertyRowMapper(TlogBean.class));
            return data;
        } catch (Exception e) {
            LogUtil.printErrorLog("SettlementVerifyDao", "getTlog", "final_sql:"+sql.toString(), e);
            throw new Exception("查不到交易记录");
        }

    }


    	/**
	 * 结算对象为账户时
	 *
	 * @param bathNo
	 * @return
	 */
	public int[] liqaccount2(FeeLiqBath mid, String remark, String tlogsql) {
		AccSeqs acc = new AccSeqs();
		acc.setUid(mid.getMid());
		acc.setAid(mid.getMid());
		acc.setAmt(mid.getLiqAmt());
		acc.setTrAmt(mid.getLiqAmt());
		acc.setTrFee(0);
		acc.setTbName(Constant.FEE_LIQ_BATH);
		acc.setTbId(mid.getBatch());
		acc.setRemark(remark);
		List<String> sqlsList = RecordLiveAccount.LiqToAccount2(acc);
		String sql = "update fee_liq_bath set state=3 where batch='"+ Ryt.sql(mid.getBatch()) + "'";
		sqlsList.add(sql);
		sqlsList.add(tlogsql);
		String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
		int[] liqcon = batchSqlTransaction(sqls);

		return liqcon;
	}

		public CurrentPage<FeeLiqBath> getAccFeeLiqBath(int pageNo,
				int beginDate, int endDate, String mid, String batch,
				Integer mstate,Integer liqState) {
			// TODO Auto-generated method stub
			String selSql = "select f.mid,m.user_id,f.trans_amt,f.ref_amt,(f.trans_amt-ref_amt)as tradeAmt,f.fee_amt,f.manual_add,f.manual_sub,f.liq_amt,";
			selSql += " f.batch,f.liq_date,f.state,f.ref_fee,m.dls_code,m.is_today_liq,m.gate_id,m.pbk_gate_id from fee_liq_bath f,minfo m";

			StringBuffer condSql = new StringBuffer(" where f.id > 0 and f.mid=m.id ");
			if (beginDate != 0)
				condSql.append(" and f.liq_date >= ").append(beginDate);
			if (endDate != 0)
				condSql.append(" and f.liq_date <= ").append(endDate);
			condSql.append(" and f.state='3'");
			if (!mid.trim().equals(""))
				condSql.append(" and f.mid='"+ mid+"'");
				condSql.append(" and f.liq_gid in (1,4)");
			if (!batch.trim().equals(""))
				condSql.append(" and f.batch='" + batch+"'");
			
			if(mstate!=null)condSql.append(" and m.mstate=").append(mstate);
			
			String querySql = selSql + condSql.toString();
			StringBuffer finalsql=new StringBuffer(" select * from (select a.*,b.acc_mdate,b.acc_state,b.acc_notice from  ("+querySql+") as a ");
			
			finalsql.append(" LEFT JOIN (select * from (select * from acc_sync_result order  BY acc_mdate desc)aa GROUP BY aa.batch   ) b on  a.batch=b.batch");
			finalsql.append(" ) as c ");
			if(liqState!=null)
				finalsql.append( "where c.acc_state="+liqState+"");
			String countSql = "select count(*) from ( "+finalsql+") d;";
			
			//结算总金额
			String sumLiqAmt="select sum(liq_amt) from fee_liq_bath f,minfo m  "+ condSql.toString();
			Map<String, String> sumSQLMap=new HashMap<String,String>();
			sumSQLMap.put(AppParam.AMT_SUM, sumLiqAmt);
			
			return queryForPage(countSql, finalsql.toString(), pageNo,new AppParam().getPageSize(),FeeLiqBath.class,sumSQLMap);
		
		}
		
		//账户同步推送详情
		public List<AccSyncDetail> queryAccSyncDetails(String batch){
			String sql="select h.type,h.tseq as seqNo,h.oid as orderId,h.mdate as orderDate, h.amount ,h.fee_amt as fee,o.in_user_id from hlog h LEFT JOIN 	order_ext o on h.tseq=o.tseq " +
					" where h.batch='"+ Ryt.sql(batch) + "'";
			return query(sql, AccSyncDetail.class);
		}
		
		//账户同步推送详情总金额
		public long getSumAmount(String batch){
				//String sql="select sum(amount)  from hlog h ,minfo m  where h.mid=m.id and h.batch='"+ Ryt.sql(batch) + "'";
			String sql="select liq_amt from fee_liq_bath h  where  h.batch='"+ Ryt.sql(batch) + "'";
			return queryForLong(sql);
				//return queryForInt(sql);
			}
		
		public void addAccSyncResult(String batch,int acc_state,String acc_notice){
			StringBuffer insertSql = new StringBuffer();
			insertSql.append("insert into acc_sync_result(batch,acc_mdate,acc_state,acc_notice)");
			insertSql.append(" values(");
			insertSql.append(batch).append(",");
			insertSql.append(DateUtil.getNowDateTime()).append(",");
			insertSql.append(acc_state).append(",");
			insertSql.append(Ryt.addQuotes(acc_notice)).append(")");
			update(insertSql.toString());
			
		}
		
		public List<SettleDetail> getSyncDetail(String batch) {
			batch = Ryt.sql(batch);
			String paySql = "select a.*,o.out_user_id from  " +
					" (select  h.mid,h.oid,h.sys_date,h.tseq,h.amount,0 as ref_amt,h.amount as tradeAmt,h.fee_amt ,0 as mer_fee  from hlog  h where h.batch='"+batch+"' and  h.type!=4) a " +
					" LEFT JOIN order_ext o on  a.tseq=o.tseq";
			
			String refSql = "select a.*,o.out_user_id from  " +
					" (select  h.mid,h.oid,h.sys_date,h.tseq,0 as amount,h.ref_amt,(-1)*h.ref_amt as tradeAmt,0 as fee_amt ,h.mer_fee  from refund_log  h where h.batch='"+batch+"') a " +
					" LEFT JOIN order_ext o on  a.tseq=o.tseq";
			
			List<SettleDetail> detailList = query(paySql,SettleDetail.class);
			detailList.addAll(query(refSql,SettleDetail.class));
			return detailList;
		}

}
