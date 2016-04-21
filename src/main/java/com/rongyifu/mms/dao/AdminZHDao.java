package com.rongyifu.mms.dao;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.PerInfos;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.bean.TrOrders_Batch;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class AdminZHDao extends PubDao{
	
	
	/**
	 * 更新tr_orders表中的state状态
	 * @param oid
	 * @param state
	 * @param remark
	 * @return
	 */
	public int updateTrOrdersState(int gid,String oid,Integer state,String tseq,String remark,String bkDate,String bkTime,String resCode,String errorMsg){
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		String sql = "update tr_orders set gate= ? ,oper_date= ? ,state= ?,sys_date=?,sys_time=?,tseq=?,remark=concat(remark,?),bank_date=?,bank_time=?,res_code=?,Error_msg=? where oid=?";
		Object[] args = new Object[]{gid,date,state,date,time,tseq,remark,bkDate,DateUtil.getUTCTime(bkTime),resCode,errorMsg,oid};
		int[] argTypes = new int[]{Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};
        return update(sql, args, argTypes);
	}
	
	/**
	 * 记录银行返回信息，用于中间状态信息的保存
	 * @param oid
	 * @param remark
	 * @return
	 */
	public int updateTrOrdersRetInfo(String oid, String remark){
		String sql = "update tr_orders set remark=concat(remark,?) where oid=?";
		Object[] args = new Object[]{remark,oid};
		int[] argTypes = new int[]{Types.VARCHAR,Types.VARCHAR};
		return update(sql, args, argTypes);
	}
	
	//查询账户信息
	public CurrentPage<AccInfos> queryZHXX(Integer pageNo,String uid,Integer mstate){
		StringBuffer sql=new StringBuffer("select a.* from acc_infos a,minfo m where a.uid=m.id and a.uid=a.aid");
		if(!Ryt.empty(uid))sql.append(" and a.uid ='").append(Ryt.sql(uid)).append("'");
		if(mstate!=null)sql.append(" and m.mstate=").append(mstate);
		sql.append(" order by init_date desc,aid");
		String sqlCount =	sql.toString().replace("a.*","count(*)");
		return queryForPage(sqlCount,sql.toString(), pageNo, AppParam.getPageSize(),AccInfos.class) ;
	}
	
	//查询账户风险配置信息
	public CurrentPage<AccInfos> queryFXPZ(Integer pageNo,String uid,Integer state,Integer mstate){
		StringBuffer sql=new StringBuffer("select  uid, aid,aname,state,minfo.name,acc_infos.dk_month_count,dk_month_amt,acc_month_count ,acc_month_amt" +
				",mer_trade_type,acc_type from acc_infos ,minfo where  id=uid and acc_infos.aid=acc_infos.uid ");
		if(!Ryt.empty(uid))sql.append(" and uid ='").append(Ryt.sql(uid)).append("'");
		if(state!=null)sql.append(" and state =").append(state);
		if(mstate!=null)sql.append(" and mstate=").append(mstate);
		StringBuffer sqlCount=new StringBuffer("select count(*) from acc_infos ,minfo where id=uid  and acc_infos.aid=acc_infos.uid ");
		if(!Ryt.empty(uid))sqlCount.append(" and uid ='").append(Ryt.sql(uid)).append("'");
		if(state!=null)sqlCount.append(" and state =").append(state);
		if(mstate!=null)sqlCount.append(" and mstate=").append(mstate);
		return queryForPage(sqlCount.toString(),sql.toString(), pageNo, AppParam.getPageSize(),AccInfos.class) ;
	}
	
	//查询单个风险信息
	public List<Map<String,Object>> getOneAid(String uid,String aid){
		StringBuffer sql=new StringBuffer("select  uid, aid ,aname,state,minfo.name,acc_infos.dk_month_count,dk_month_amt,acc_month_count ,acc_month_amt" +
		",mer_trade_type from acc_infos ,minfo where  id=uid ");
		if(!Ryt.empty(uid))sql.append(" and id ='").append(Ryt.sql(uid)).append("'");
		sql.append(" and aid='").append(Ryt.sql(aid)).append("'");
		return getJdbcTemplate().queryForList(sql.toString());
	}
	
	public void eidtConfig(String aid,Integer state,Integer accMonthCount,long accMonthAmt,
			Integer dkMonthCount,long dkMonthAmt)throws Exception {
		StringBuffer sql=new StringBuffer("update acc_infos set acc_month_count=?,acc_month_amt=?,");
		sql.append("dk_month_count=?,dk_month_amt=?,state=? where aid=?");
		Object[] obj=new Object[]{accMonthCount,accMonthAmt,dkMonthCount,dkMonthAmt,state,aid};
		update(sql.toString(),obj);
		}
	public void eidtConfigSettlement(String aid,String daifuFeeMode)throws Exception {
		String sql="update acc_infos set daifu_fee_mode=?,state=1 where aid=?";
		Object[] obj=new Object[]{daifuFeeMode,aid};
		update(sql,obj);
		}
	
	//关闭余额支付功能
	public int closeYE(String aid){
		String sql="update acc_infos set pay_flag=0 where aid='"+Ryt.sql(aid)+"'"; 
		return update(sql);
			
	}
	//关闭账户
	public int closeZH(String aid){
		String sql="update acc_infos set state=2 where aid='"+Ryt.sql(aid)+"'"; 		
		return update(sql);
	}
	
	//查询明细
	  public CurrentPage<TrOrders> queryMX(Integer pageNo, String uid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String transFlow,Integer mstate) {
		    StringBuffer sql = new StringBuffer("select t.* from tr_orders t,minfo m where 1=1 and t.uid=m.id ");
		    if (!Ryt.empty(uid)) sql.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
			/*if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");*/
			if (ptype!=null) sql.append(" and t.ptype = ").append(ptype);
			if (state!= null) sql.append(" and t.state = ").append(state);
			if (!Ryt.empty(oid)) sql.append(" and t.oid = ").append(Ryt.addQuotes(oid));
			if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
			sql.append(" and !(t.ptype=0 and t.trans_flow is not null)");
			if(!Ryt.empty(transFlow))sql.append(" and t.trans_flow='").append(transFlow).append("' ");
			sql.append(" and t.sys_date >= ").append(bdate);
			sql.append(" and t.sys_date <= ").append(edate);
			sql.append(" order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
		}
	  
	//查询明细  down
	  public CurrentPage<TrOrders> queryMX_W(Integer pageNo, String uid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,Integer mstate) {
		    StringBuffer sql = new StringBuffer("select t.uid,t.aid,t.oid,t.aname,t.trans_amt,t.trans_fee,t.pay_amt,t.sys_date,t.sys_time,t.init_time,t.ptype,t.state,t.to_uid,t.to_aid,t.to_acc_name,t.to_acc_no from tr_orders t,minfo m where t.uid=m.id ");
		    if (!Ryt.empty(uid)) sql.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
			if (ptype!=null) sql.append(" and t.ptype = ").append(ptype);
			if (state!= null) sql.append(" and t.state = ").append(state);
			if (!Ryt.empty(oid)) sql.append(" and t.oid = ").append(Ryt.addQuotes(oid));
			if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
			sql.append(" and !(t.ptype=0 and t.trans_flow is not null)");
			sql.append(" and t.sys_date >= ").append(bdate);
			sql.append(" and t.sys_date <= ").append(edate);
			sql.append(" order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
		}
	  
	//查询明细 (新修改)
	  public CurrentPage<TrOrders> queryZHJYMX(Integer pageNo, String oid,String trans_flow) {
		  
		    StringBuffer sql = new StringBuffer("select * from tr_orders  where 1=1 ");
		    if(!trans_flow.equals("")){sql.append(" and trans_flow='").append(trans_flow).append("' ");};
			if (!oid.equals("")){ sql.append(" and oid =").append(Ryt.addQuotes(oid));};
			sql.append(" and !(ptype=0 and trans_flow is not null )");
			sql.append(" order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
		}
	  	//查询明细  （新修改）
	  public CurrentPage<TrOrders_Batch> queryMX_X(Integer pageNo, String uid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String transFlow){
//		  	LoginUser u = getLoginUser();
		    StringBuffer sql = new StringBuffer("select uid,aid ,oid,aname ,count(*) as count ,state,sum(trans_amt) as trans_sum_amt,sum(trans_fee) as trans_sum_fee,sum(pay_amt) as trans_sum_payamt,trans_flow,trans_proof ,ptype, (init_time) as sys_Date from tr_orders  where 1=1 ");
			if (ptype!=null) sql.append(" and ptype = ").append(ptype);
			if (state!= null) sql.append(" and state = ").append(state);
			if (!Ryt.empty(oid)) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
			sql.append(" and !(ptype=0 and trans_flow is not null)");
//			sql.append(" and ptype!=0 ");
			if(!Ryt.empty(uid))sql.append(" and uid= ").append(uid);
			sql.append(" and sys_date >= ").append(bdate);
			sql.append(" and sys_date <= ").append(edate);
			sql.append(" group by trans_flow ,ifnull(trans_flow,oid) order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders_Batch.class) ;
	  }
	  
	  
	//查询流水
	  public CurrentPage<AccSeqs> queryLS(Integer pageNo,String uid, String aid,Integer bdate,Integer edate,String tseq,Integer mstate,Integer category) {
	  		    StringBuffer sql = new StringBuffer("select a.*,m.abbrev from minfo m, acc_seqs a  where a.uid=m.id ");
	  		    if (!Ryt.empty(uid)) 
	  		    	sql.append(" and a.aid = '").append(Ryt.sql(uid)).append("'"); // 商户号作为账户号查询
	  		    else if (!Ryt.empty(aid)) 
	  		    	sql.append(" and a.aid = '").append(Ryt.sql(aid)).append("'");
	  			if(!Ryt.empty(tseq)) sql.append(" and a.tb_id = '").append(Ryt.sql(tseq)).append("'");
	  			if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
	  			if(category!=null) sql.append(" and m.category=").append(category);
	  			sql.append(" and a.tr_date >= ").append(bdate);
	  			sql.append(" and a.tr_date <= ").append(edate);
	  			sql.append(" order by id desc");
	   			return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
	   		}
	//查询流水
	  public CurrentPage<AccSeqs> queryJYZHLS(Integer pageNo,String uid, String aid,Integer bdate,Integer edate,Integer mstate) {
	 		    StringBuffer sql = new StringBuffer("select a.* from acc_seqs a,minfo m where a.uid=m.id ");
	  		    if (!Ryt.empty(uid)) sql.append(" and a.uid = '").append(Ryt.sql(uid)).append("'");
				if (!Ryt.empty(aid)) sql.append(" and a.aid = '").append(Ryt.sql(aid)).append("'");
				if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
			sql.append(" and a.tr_date >= ").append(bdate);
	  			sql.append(" and a.tr_date <= ").append(edate).append(" and a.tb_name='tr_orders'");
	  			sql.append(" order by tr_date desc,tr_time desc");
	 		return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
	}
	  
	  /****
	   * 账户收支明细
	   * @param pageNo
	   * @param uid
	   * @param aid
	   * @param bdate
	   * @param edate
	   * @param oid
	   * @return
	   */
	  public CurrentPage<AccSeqs> queryZHSZMX(Integer pageNo,Integer ptype,String uid, String aid,Integer bdate,Integer edate,String oid,Integer mstate) {
		  StringBuffer sqlmstate=new StringBuffer("select a.* from acc_seqs a,minfo m where a.uid=m.id  "); 
		  if(mstate!=null){
			  sqlmstate.append(" and m.mstate=").append(mstate);
		  }
		  StringBuffer sql = new StringBuffer("select  acc_seqs.uid  as uid,acc_seqs.aid as aid,tr_orders.oid as oid,tr_orders.trans_flow as trans_flow,amt,all_balance,tr_date,tr_orders.ptype as ptype,acc_seqs.rec_Pay,tr_orders.init_time as init_time from   tr_orders left join ("+sqlmstate+")as acc_seqs on  acc_seqs.uid=tr_orders.uid and  acc_seqs.tb_id=tr_orders.oid where 1=1 ");
		    if (!Ryt.empty(uid)) sql.append(" and acc_seqs.uid = '").append(Ryt.sql(uid)).append("'");
			if (!Ryt.empty(aid)) sql.append(" and acc_seqs.aid = '").append(Ryt.sql(aid)).append("'");
			if(!Ryt.empty(oid))sql.append(" and tr_orders.oid='").append(oid).append("'");
			sql.append(" and !(tr_orders.ptype=0 and tr_orders.trans_flow is not null)");
			if(ptype!=null)sql.append(" and ptype=").append(ptype).append(" ");
			sql.append(" and tr_orders.state in (" + Constant.DaiFuTransState.PAY_SUCCESS+ "," + Constant.DaiFuTransState.RECHARGE_SUCCESS+ "," + Constant.DaiFuTransState.EXTRACTION_CASH_SUCCESS+ ") ");
			sql.append(" and tr_date >= ").append(bdate);
			sql.append(" and tr_date <= ").append(edate);
			sql.append(" order by tr_date desc,tr_time desc");
			return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
		}
	  //查询提现处理信息
	  public CurrentPage<TrOrders> queryTXCL(Integer pageNo, String uid, int state,Integer mstate) {
		  	StringBuffer sqlBuff=new StringBuffer("from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.ptype=1 ");
		  	sqlBuff.append(" and state = ").append(state);
		  	if (!Ryt.empty(uid)) sqlBuff.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
		  	if(mstate!=null) sqlBuff.append(" and m.mstate=").append(mstate);
		  	sqlBuff.append(" and pstate=" + Constant.DaifuPstate.AUDIT_INIT_STATUS + "  ");
		  	
		    String sql = "select t.*,m.name,m.bank_name,m.bank_acct "+sqlBuff.toString()+" order by init_time desc";
		    String sqlCount = "select count(*)" + sqlBuff.toString();
			return queryForPage(sqlCount,sql, pageNo, AppParam.getPageSize(),TrOrders.class) ;
		}
	//下载提现处理信息
	  
	  public CurrentPage<TrOrders> queryTXCL2(Integer pageNo, String uid,Integer mstate) {
		  	String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		  	StringBuffer sqlBuff=new StringBuffer("from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.ptype=" + Constant.DaiFuTransType.ACCOUNT_EXTRACTION_CASH + " and t.state=" + Constant.DaiFuTransState.EXTRACTION_CASH_SUCCESS + " and sys_date=");
		  	sqlBuff.append(date);
		  	if (!Ryt.empty(uid)) sqlBuff.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
		   if(mstate!=null) sqlBuff.append(" and m.mstate=").append(mstate);
		    String sql = "select t.*,m.name,m.bank_name,m.bank_acct "+sqlBuff.toString();
			return queryForCurrPage(sql, pageNo,TrOrders.class) ;
		}
	 // 更新经办处理信息
	  public int[] updateTXCL(List<TrOrders> list,String option){
		  int date = DateUtil.today();
		  int time=DateUtil.getCurrentUTCSeconds();
		  List<String> sqls=null;
		  int[] result=new int[4];
		  for (int i = 0; i < list.size(); i++) {
			    TrOrders t=list.get(i);
				String oid = t.getOid();
				String uid =t.getUid();
				long transAmt=t.getTransAmt();
				int transFee=t.getTransFee();
//				long payAmt=t.getPayAmt();
				AccSeqs param=new AccSeqs();
				param.setTrAmt(transAmt);
				param.setAid(uid);
				param.setUid(uid);
				param.setTrFee(transFee);
				param.setAmt(transAmt);
				param.setTbId(oid);
				param.setTbName(Constant.HLOG);
				param.setRemark("提现");

				 sqls=RecordLiveAccount.handleBalanceForTX(param);//生成流水sql
				//更新trorders订单状态sql
				StringBuffer sqlBuff=new StringBuffer("update tr_orders set sys_date =");
			    sqlBuff.append(date).append(",sys_time =").append(time).append(",state=" + Constant.DaiFuTransState.EXTRACTION_CASH_SUCCESS + ",");
				sqlBuff.append("pstate=" + Constant.DaifuPstate.AUDIT_SUCCESS + ",");
				sqlBuff.append(" audit_oper =").append(getLoginUser().getOperId()).append(",");
				sqlBuff.append(" audit_remark =").append(Ryt.addQuotes(Ryt.sql(option))).append(",");
				sqlBuff.append(" audit_date =").append(date).append(",");
				sqlBuff.append(" audit_time =").append(time);
				sqlBuff.append(" where oid= '").append(Ryt.sql(oid)).append("'");
				sqls.add(sqlBuff.toString());
				StringBuffer hlogSqlBuff=new StringBuffer("update hlog set tstat =");
				hlogSqlBuff.append(Constant.PayState.SUCCESS);
				hlogSqlBuff.append(" where oid= ");
				hlogSqlBuff.append(Ryt.addQuotes(oid));
				sqls.add(hlogSqlBuff.toString());
				String[] sqls2=sqls.toArray(new String[sqls.size()]);
				 result= batchSqlTransaction(sqls2);
			}
		  return result;
}
	  
	// 更新提现经办处理信息
	  public int[] updateSuccessTXCL(List<TrOrders> list,String option) throws Exception{
		  String []sqls=new String[list.size()];
		  int date = DateUtil.today();
		  LoginUser loginUser = getLoginUser();
		  int time=DateUtil.getCurrentUTCSeconds();
		  for (int i = 0; i < list.size(); i++) {
			TrOrders t=list.get(i);
			String oid = t.getOid();
			StringBuffer sql=new StringBuffer("update tr_orders set oper_date =");
			  sql.append(date).append(",state=" + Constant.DaiFuTransState.EXTRACTION_CASH_PROCESSING + ", ");
			  sql.append(" pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS + ", ");
			  sql.append(" orgn_oper=").append(loginUser.getOperId()).append(",");//审核操作员
			  sql.append("orgn_remark='").append(option).append("', ");
			  sql.append("orgn_date=").append(date).append(", ");
			  sql.append("orgn_time=").append(time).append(" ");
			  sql.append(" where oid='").append(oid).append("'");
//			  StringBuffer hlogSql=new StringBuffer("update hlog set tstat =");
//			  hlogSql.append(Constant.PayState.SUCCESS);
//			  hlogSql.append(" where oid=");
//			  hlogSql.append(Ryt.addQuotes(oid));
			 sqls[i]=sql.toString();
//			 sqls[i*2+1]=hlogSql.toString();		  
			}
		return batchSqlTransaction(sqls);
//		  update(sql.toString());
	  }
	// 更新提现审核失败信息
	  public int[] updateFailTXCL(List<TrOrders> list,String option) throws Exception{
		  String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		  int time=DateUtil.getCurrentUTCSeconds();
		  String []sqls=new String[list.size()*3];
		  for (int i = 0; i < list.size(); i++) {
				TrOrders t = list.get(i);
				String oid = t.getOid();
				String aid = t.getAid();
				long transAmt = t.getTransAmt();
			  StringBuffer sqlTr=new StringBuffer("update tr_orders set sys_date =");
			  sqlTr.append(date).append(",sys_time =").append(time).append(",state=" + Constant.DaiFuTransState.EXTRACTION_CASH_FAILURE + ",");
			  sqlTr.append("pstate=" + Constant.DaifuPstate.AUDIT_FAILURE + ",");
			  sqlTr.append(" audit_oper =").append(getLoginUser().getOperId()).append(",");
			  sqlTr.append(" audit_remark =").append(Ryt.addQuotes(Ryt.sql(option))).append(",");
			  sqlTr.append(" audit_date =").append(date).append(",");
			  sqlTr.append(" audit_time =").append(time);
			  sqlTr.append(" where oid='").append(oid).append("'");
			  StringBuffer sqlAccInfos=new StringBuffer("update acc_infos set balance=balance+");
			  sqlAccInfos.append(transAmt).append(",freeze_amt=freeze_amt-").append(transAmt).append(" where aid='").append(aid).append("'");
			  StringBuffer hlogSql=new StringBuffer("update hlog set tstat=");
			  hlogSql.append(Constant.PayState.FAILURE);
			  hlogSql.append(" where oid=");
			  hlogSql.append(Ryt.addQuotes(oid));
			  sqls[i*3]=sqlTr.toString();
			  sqls[i*3+1]=sqlAccInfos.toString();
			  sqls[i*3+2]=hlogSql.toString();
		  }
		  return batchSqlTransaction(sqls);
	  }
	  
	// 更新提现经办失败信息
		  public int[] updateFailTXCL_JB(List<TrOrders> list,String option) throws Exception{
			  String []sqls=new String[list.size()*3];
			  String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
			  int time=DateUtil.getCurrentUTCSeconds();
			  for (int i = 0; i < list.size(); i++) {
			  TrOrders t=list.get(i);
			  String oid = t.getOid();
			  String aid =t.getUid();
			  long transAmt=t.getTransAmt();
			  StringBuffer sqlTr=new StringBuffer("update tr_orders set sys_date =");
			  sqlTr.append(date).append(",sys_time =").append(time).append(",state=" + Constant.DaiFuTransState.EXTRACTION_CASH_FAILURE + " ,pstate=" + Constant.DaifuPstate.HANDLING_FAILURE + ",");
			  sqlTr.append(" orgn_oper=").append(getLoginUser().getOperId()).append(",");//审核操作员
			  sqlTr.append("orgn_remark='").append(option).append("', ");
			  sqlTr.append("orgn_date=").append(date).append(", ");
			  sqlTr.append("orgn_time=").append(time).append(" ");
			  sqlTr.append(" where oid='").append(oid).append("'");
			  String YeSql=RecordLiveAccount.calUsableBalance(aid, aid, transAmt, Constant.RecPay.INCREASE);
			  StringBuffer hlogSql=new StringBuffer("update hlog set tstat=");
			  hlogSql.append(Constant.PayState.FAILURE);
			  hlogSql.append(" where oid=");
			  hlogSql.append(Ryt.addQuotes(oid));
			  sqls[i*2]=sqlTr.toString();
			  sqls[i*2+1]=YeSql.toString();
			  sqls[i*2+2]=hlogSql.toString();
			}
			  return batchSqlTransaction(sqls);
		  }
	  //查询个人信息
	  public CurrentPage<PerInfos> queryGR(Integer pageNo, String uid,String idNo) {
		    StringBuffer sql = new StringBuffer("select * from per_infos  where 1=1 ");
		    if (!Ryt.empty(uid)) sql.append(" and uid = '").append(Ryt.sql(uid)).append("'");
			if (!Ryt.empty(idNo)) sql.append(" and id_no = '").append(Ryt.sql(idNo)).append("'");
			sql.append(" order by sys_date desc ");
			String sqlCount =	sql.toString().replace("*","count(*)");
			return queryForPage(sqlCount,sql.toString(), pageNo, AppParam.getPageSize(),PerInfos.class) ;
		}
	  //查询个人详细资料
		public PerInfos queryGRByUid(String uid) {
			StringBuffer sqlBuff=new StringBuffer("select * from per_infos where uid='");
			sqlBuff.append(Ryt.sql(uid)).append("'");
			return queryForObject(sqlBuff.toString(), PerInfos.class);
		}
		/**
		 * 根据uid查询账户号（不包含结算账户）
		 * @param uid
		 * @return
		 */
		public Map<String, String> getZHMapByUid(String uid){
			Map<String, String> result = new HashMap<String, String>();
			if(Ryt.empty(uid))
				return result;
			String sql="select aid, aname from acc_infos where uid!=aid and uid = " + Ryt.sql(uid);
			List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
			for(Map<String,Object> m : aList){
				result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
			}
			return result;
		}
		/**
		 * 根据uid查询账户号（包含结算账户）
		 * @param uid
		 * @return
		 */
		public Map<String, String> getZHMapByUid2(String uid){
			Map<String, String> result = new HashMap<String, String>();
			String sql="select aid, aname from acc_infos where uid=aid and uid = " + Ryt.sql(uid);
			List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
			for(Map<String,Object> m : aList){
				result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
			}
			return result;
		}
		
		/**
		 * 根据UID查询结算帐户
		 * @param uid
		 * @return
		 */
		public Map<String, String> getJSZHByUid(String uid){
			Map<String, String> result = new HashMap<String, String>();
			if(Ryt.empty(uid))
				return result;
			String sql="select aid, aname from acc_infos where uid = " + Ryt.sql(uid);
			List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
			for(Map<String,Object> m : aList){
				result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
			}
			return result;
		}
		/**
		 * 查询单笔划款
		 * @param pageNo
		 * @param uid，用户ID 
		 * @param aid，账户ID
		 * @param bdate
		 * @param edate
		 * @param state，状态
		 * @param to_acc_no，收款帐号
		 * @param other_id
		 * @param amount_num
		 * @return
		 */
		public CurrentPage<TrOrders> querySingleTransMoney(Integer pageNo,String uid,String aid,Integer bdate,
				Integer edate,Integer state,String toAccNo,String otherId,long amountNum,String ym,Integer mstate){
			StringBuffer pubCondition=new StringBuffer(" and t.ptype=" + Constant.DaiFuTransType.PAY_TO_PERSON + " and t.uid=t.aid ");
			
			pubCondition.append(" and t.sys_date >= ").append(bdate);
			pubCondition.append(" and t.sys_date <= ").append(edate);
			if (!Ryt.empty(uid)) pubCondition.append(" and t.uid = ").append(Ryt.addQuotes(Ryt.sql(uid)));
			if (!Ryt.empty(aid)) pubCondition.append(" and t.aid = ").append(Ryt.addQuotes(Ryt.sql(aid)));
			if (state!= null) pubCondition.append(" and t.state = ").append(state);
			if (!Ryt.empty(toAccNo)) pubCondition.append(" and t.to_acc_no = '").append(Ryt.sql(toAccNo)).append("' ");
			if(mstate!=null) pubCondition.append(" and m.mstate=").append(mstate);
//			
			StringBuffer sql=new StringBuffer("select t.* from tr_orders t,minfo m where m.id=t.uid ");
			sql.append(pubCondition);
//			&& !ym.equals("")  "sys_date like '%"+ym+"%'"
			if (otherId.equals("acc_month_count") ) {
				sql.append(" and t.to_acc_no in (select to_acc_no from tr_orders where ").append(pubCondition);
				sql.append(" and t.to_acc_no!='' group by to_acc_no having count(tseq)>= ").append(amountNum).append(")");
			}else if(otherId.equals("acc_month_amt")){
				sql.append(" and t.to_acc_no in (select to_acc_no from tr_orders where ").append(" sys_date like '%"+ym+"%'");
				sql.append(" and t.to_acc_no!='' group by to_acc_no having sum(trans_amt)>= ").append(amountNum).append(")");
			}else if(otherId.equals("trans_limit")){
				sql.append(" and t.trans_amt >= ").append(amountNum);
			}/*else{
				sql.append(pubCondition);
				
			}*/
			sql.append(" order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo, TrOrders.class);
		}
		
		/**
		 * 查询银企直连网关维护 信息
		 * @param pageNo
		 * @return
		 */
		public CurrentPage<B2EGate> queryB2EGate(Integer pageNo){
			StringBuffer sql=new StringBuffer("select * from b2e_gate ");
			String sqlCount =	sql.toString().replace("*","count(*)");
			return queryForPage(sqlCount,sql.toString(), pageNo, AppParam.getPageSize(),B2EGate.class) ;
		}
		
		public int addB2EGate(String gid,String name,String ncUrl,Integer provId,String accNo,String accName,
				String termid,String corpNo,String userNo){
			String bkNo=queryForString("select bf_bk_no from gate_route where gid="+String.valueOf(gid));
			StringBuffer sql=new StringBuffer("insert into b2e_gate (gid,name,nc_url,bk_no,prov_id,acc_no,acc_name,termid,corp_no,user_no) values(");
			sql.append("'").append(Ryt.sql(gid)).append("'");
			sql.append(",'").append(Ryt.sql(name)).append("'");
			sql.append(",'").append(Ryt.sql(ncUrl)).append("'");
			sql.append(",'").append(Ryt.sql(bkNo)).append("'");
			sql.append(",").append(provId);
			sql.append(",'").append(Ryt.sql(accNo)).append("'");
			sql.append(",'").append(Ryt.sql(accName)).append("'");
			sql.append(",'").append(Ryt.sql(termid)).append("'");
			sql.append(",'").append(Ryt.sql(corpNo)).append("'");
			sql.append(",'").append(Ryt.sql(userNo)).append("')");
			return update(sql.toString());
		}
		/**
		 * 查询单条银企直连网关信息
		 * @param gid 网关号
		 * @return
		 */
		public B2EGate getOneB2EGate(int gid) {
			StringBuffer sql=new StringBuffer("select * from  b2e_gate where gid =");
			sql.append(gid);
		
			return queryForObject(sql.toString(), B2EGate.class);
		}
		/**
		 * 修改单条银企直连网关信息
		 * @param aGid 网关号
		 * @param aName 网关名称
		 * @param aNcUrl 前置机地址
		 * @param aBkNo 银行行号
		 * @param aProvId 开户省份
		 * @param aAccNo 开户帐号
		 * @param aAccName 开户名称
		 * @param aTermid 企业前置机ip
		 * @param aCorpNo 企业客户编码
		 * @param aUserNo 企业操作员代码
		 * @param aUserPwd 登录密码
		 * @param aCodeType 字符编码
		 * @param aBusiNo 协议编号
		 * @return
		 */
		public int editOneB2EGate(String aGid,String aName,String aNcUrl,String aBkNo,Integer aProvId,String aAccNo,String aAccName,
				String aTermid,String aCorpNo,String aUserNo,String aUserPwd,String aCodeType,String aBusiNo){
			StringBuffer sql=new StringBuffer("update  b2e_gate set ");
			sql.append("prov_id =").append(aProvId);
			if (!Ryt.empty(aName))sql.append(",name='").append(Ryt.sql(aName)).append("'");
			if (!Ryt.empty(aNcUrl))sql.append(",nc_url='").append(Ryt.sql(aNcUrl)).append("'");
			if (!Ryt.empty(aBkNo))sql.append(",bk_no='").append(Ryt.sql(aBkNo)).append("'");
			if (!Ryt.empty(aAccNo))sql.append(",acc_no='").append(Ryt.sql(aAccNo)).append("'");
			if (!Ryt.empty(aAccName))sql.append(",acc_name='").append(Ryt.sql(aAccName)).append("'");
			if (!Ryt.empty(aTermid))sql.append(",termid='").append(Ryt.sql(aTermid)).append("'");
			if (!Ryt.empty(aCorpNo))sql.append(",corp_no='").append(Ryt.sql(aCorpNo)).append("'");
			if (!Ryt.empty(aUserNo))sql.append(",user_no='").append(Ryt.sql(aUserNo)).append("'");
			if (!Ryt.empty(aUserPwd))sql.append(",user_pwd='").append(Ryt.sql(aUserPwd)).append("'");
			if (!Ryt.empty(aCodeType))sql.append(",code_type='").append(Ryt.sql(aCodeType)).append("'");
			if (!Ryt.empty(aBusiNo))sql.append(",busi_no='").append(Ryt.sql(aBusiNo)).append("'");
			sql.append(" where gid='").append(Ryt.sql(aGid)).append("'");
			return update(sql.toString());
			
		}
		/**
		 * 查询代发经办数据
		 * @param uid 用户ID
		 * @param num 条数
		 * @return
		 */
		public List<TrOrders> queryDaiFaJingBan(String uid,Integer num){
			StringBuffer sql=new StringBuffer("select * from tr_orders where ptype in (" + Constant.DaiFuTransType.PAY_TO_PERSON + "," + Constant.DaiFuTransType.PAY_TO_ENTERPRISE + ") and state in (1,8) ");
			if (!Ryt.empty(uid))sql.append("and uid='").append(Ryt.sql(uid)).append("'");
			sql.append(" order by sys_date desc ,sys_time desc limit ").append(num);
			return query(sql.toString(),TrOrders.class);
		}
		
		/**
		 * 查询代发经办数据
		 * @param uid 用户ID
		 * @param num 条数
		 * @param trans_flow 批次号
		 * @return
		 */
		public CurrentPage<TrOrders> queryDaiFaJingBan(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate){
			StringBuffer sql=new StringBuffer("select t.* from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.state=" + Constant.DaiFuTransState.PAY_PROCESSING);
			if(ptype==0)sql.append(" and t.ptype in (" + Constant.DaiFuTransType.PAY_TO_PERSON + "," + Constant.DaiFuTransType.PAY_TO_ENTERPRISE + ")");else sql.append(" and t.ptype=").append(ptype).append(" ");
			if(!Ryt.empty(oid))sql.append(" and  t.oid='").append(oid).append("' ");
			sql.append(" and t.pstate not in (" + Constant.DaifuPstate.HANDLING_SUCCESS + "," + Constant.DaifuPstate.AUDIT_SUCCESS + ") ");
			if (!Ryt.empty(uid))sql.append(" and t.uid='").append(Ryt.sql(uid)).append("' ");
			if (!Ryt.empty(trans_flow))sql.append(" and t.trans_flow='").append(Ryt.sql(trans_flow)).append("'");
			if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
			if(bdate!=null&&edate!=null)	
				sql.append(" and t.sys_date >= ").append(bdate).append(" and t.sys_date <= ").append(edate);
			   sql.append(" order by sys_date desc ,sys_time desc");
			String sqlCount =	sql.toString().replace("t.*","count(*)");
			return queryForPage(sqlCount,sql.toString(), pagNo, pageSize,TrOrders.class) ;
		}
		
		/**
		 * 查询代发经办审核数据
		 * @param uid 用户ID
		 * @param num 条数
		 * @param trans_flow 批次号
		 * @return
		 */
		public CurrentPage<TrOrders> queryDaiFaJingBan_SH(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate ){
			StringBuffer sql=new StringBuffer("select t.* from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.state=" + Constant.DaiFuTransState.PAY_PROCESSING);
			if(ptype==0)sql.append(" and t.ptype in (" + Constant.DaiFuTransType.PAY_TO_PERSON + "," + Constant.DaiFuTransType.PAY_TO_ENTERPRISE + ")");else sql.append(" and t.ptype=").append(ptype).append(" ");
			sql.append(" and t.pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS);
			if(!Ryt.empty(oid))sql.append(" and t.oid='").append(oid).append("' ");
			if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
			if (!Ryt.empty(uid))sql.append(" and t.uid='").append(Ryt.sql(uid)).append("'");
			if (!Ryt.empty(trans_flow))sql.append(" and t.trans_flow='").append(Ryt.sql(trans_flow)).append("'");
			if(bdate!=null&&edate!=null)	
				sql.append(" and t.sys_date >= ").append(bdate).append(" and t.sys_date <= ").append(edate);
			sql.append(" order by sys_date desc ,sys_time desc");
			String sqlCount =	sql.toString().replace("t.*","count(*)");
			return queryForPage(sqlCount,sql.toString(), pagNo, pageSize,TrOrders.class) ;
		}
		
		
		/**
		 * 查询代发经办数据（旧）
		 * @param uid 用户ID
		 * @param num 条数
		 * @return
		 */
		public TrOrders queryTrOrders(String oid){
			if(oid==null||oid.trim().length()==0) return null;
			String sql = "select * from tr_orders where oid  = " + Ryt.addQuotes(oid);
			return queryForObject(sql,TrOrders.class);
		}
		
		public String query4Remark(String oid){
			return queryForString("select remark from tr_orders where oid="+ Ryt.addQuotes(oid));
		}
		/**
		 * 查询该商户是否存在
		 * @param mid 商户号
		 * @return
		 */
	  public int hasMid(String mid){
		 StringBuffer sql=new StringBuffer("select count(*) from minfo where id='");
		 sql.append(mid).append("'");
		return queryForInt(sql.toString());
	  }
	  public int  resetPassWord(String mid,String pwd){
		 StringBuffer sql=new StringBuffer("update minfo set pay_pwd='");
		  sql.append(pwd+"'");
		  sql.append(" where id='");
		  sql.append(mid+"'");
		  return update(sql.toString());
	  }
	  /**
	   * 重置商户密码
	   * @param mid 商户ID
	   * @param pwd 重置密码
	   * @param sqls 需要通知的操作员sql
	   * @return
	   */
	  public int[]  resetPassWord(String mid,String pwd,String []sqls){
		 StringBuffer sql=new StringBuffer("update minfo set pay_pwd='");
		  sql.append(pwd+"'");
		  sql.append(" where id='");
		  sql.append(mid+"'");
		  sqls[sqls.length-1]=sql.toString();
		  return batchSqlTransaction(sqls);
	  }
	  /**
		 * 管理后台查询商户余额
		 * @param pageNo 当前页数
		 * @param mid 查询的商户号
		 * @return
		 * @throws Exception
		 */
	  public CurrentPage<AccInfos> querySHYE(Integer pageNo,String mid,Integer mstate){
		  StringBuffer  sql=new StringBuffer("select a.* from acc_infos a,minfo m where a.acc_type=1 and a.uid=m.id");
		  if(!Ryt.empty(mid)){
			  sql.append(" and uid =").append(Ryt.addQuotes(mid));
		  }
		  if(mstate!=null){
			  sql.append(" and m.mstate=").append(mstate);
		  }
		  String sqlCount =	sql.toString().replace("a.*","count(*)");
		  return queryForPage(sqlCount,sql.toString(), pageNo, AppParam.getPageSize(),AccInfos.class) ;
	  }
	  
	//查询流水收支
	  public CurrentPage<AccSeqs> queryLS_SZ(Integer pageNo,String uid, String aid,Integer bdate,Integer edate,String oid,Integer ptype,Integer mstate) {
		  StringBuffer sql2=new StringBuffer("select t.* from tr_orders t,minfo m where t.uid=t.aid t.uid=m.id");
		if(mstate!=null) sql2.append(" and m.mstate=").append(mstate);  
		  StringBuffer sql = new StringBuffer("select  acc_seqs.tr_amt,acc_seqs.tr_fee,acc_seqs.uid  as uid,acc_seqs.aid as aid,tr_orders.oid as oid,tr_orders.trans_flow as trans_flow,amt,all_balance,tr_date,tr_orders.ptype as ptype,acc_seqs.rec_Pay,tr_orders.init_time as init_time from ("+sql2+") as tr_orders left join   acc_seqs on  acc_seqs.uid=tr_orders.uid and  acc_seqs.tb_id=tr_orders.oid where 1=1 ");
		    if (!Ryt.empty(uid)) sql.append(" and acc_seqs.uid = '").append(Ryt.sql(uid)).append("'");
			if (!Ryt.empty(aid)) sql.append(" and acc_seqs.aid = '").append(Ryt.sql(aid)).append("'");
			if(!Ryt.empty(oid))sql.append(" and tr_orders.oid='").append(oid).append("'");
			if(ptype!=null)sql.append(" and tr_orders.ptype=").append(ptype).append(" ");
			sql.append(" and tr_orders.state in (" + Constant.DaiFuTransState.RECHARGE_SUCCESS + "," + Constant.DaiFuTransState.EXTRACTION_CASH_SUCCESS + "," + Constant.DaiFuTransState.PAY_SUCCESS + ") ");
			sql.append(" and tr_date >= ").append(bdate);
			sql.append(" and tr_date <= ").append(edate);
			sql.append(" order by tr_date desc,tr_time desc");
			return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
		}
	  /**
	   * 判断该商户交易账户是否为开启状态
	   * @param mid 商户号
	   * @return
	   */
	 public boolean adminTransAccIsNomal(String mid){
		  return transAccIsNomals(mid);
	  }
	 /**
	  * 查询提现信息处理(只查询结算帐户的)
	  * @param pageNo
	  * @param uid 商户号
	  * @param state
	  * @param mstate
	  * @author yang.yaofeng (最后修改2013-04-24 14:50:33)
	  * @return
	  */
	  public CurrentPage<TrOrders> queryTXCL_SH(Integer pageNo, String uid, int state,Integer mstate) {
		  	StringBuffer sqlBuff=new StringBuffer(" from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.ptype=" + Constant.DaiFuTransType.ACCOUNT_EXTRACTION_CASH);
		  	sqlBuff.append(" and t.state = ").append(state);
		  	if (!Ryt.empty(uid)) sqlBuff.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
		  	if(mstate!=null) sqlBuff.append(" and m.mstate=").append(mstate);
		  	sqlBuff.append(" and t.pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS);
		  	
		    String sql = "select t.*,m.name,m.bank_name,m.bank_acct "+sqlBuff.toString()+" order by init_time desc";
		    String sqlCount = "select count(*) " + sqlBuff.toString();
		    return queryForPage(sqlCount,sql, pageNo, AppParam.getPageSize(),TrOrders.class) ;
		}
	  //===============================================yyf
		 /**
			 * 查询代发经办数据
			 * @param uid 用户ID
			 * @param num 条数
			 * @param trans_flow 批次号
			 * @return
			 */
			public List<TrOrders> queryXXCZJingBan(String uid,Integer num,Integer mstate){
				StringBuffer sql=new StringBuffer("select t.* from tr_orders t,minfo m where t.uid=m.id and t.uid=t.aid and t.state=" + Constant.DaiFuTransState.OFFLINE_RECHARGE_CERT_AUDIT_PROCESSIING + " and t.pstate=" + Constant.DaifuPstate.CERT_AUDIT_SUCCESS);
				sql.append(" and t.ptype=").append(Constant.DaiFuTransType.ACCOUNT_RECHARGE);
				if (!Ryt.empty(uid))sql.append(" and t.uid='").append(Ryt.sql(uid)).append("'");
				if(mstate!=null)sql.append(" and m.mstate=").append(mstate);
				sql.append(" order by sys_date desc ,sys_time desc limit ").append(num);
				return query(sql.toString(),TrOrders.class);
			}
		 /**
		  * 查询线下充值审核数据
		  * @param pageNo 页数
		  * @param uid 商户id
		  * @param state 状态
		  * @return
		  */
		  public CurrentPage<TrOrders> queryXXCZSH(Integer pageNo, String uid,Integer mstate) {
			  	StringBuffer sqlBuff=new StringBuffer("from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.ptype=" + Constant.DaiFuTransType.ACCOUNT_RECHARGE + " and t.pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS);
			  	sqlBuff.append(" and t.state = ").append(Constant.DaiFuTransState.OFFLINE_RECHARGE_CERT_AUDIT_PROCESSIING);
			  	if (!Ryt.empty(uid)) sqlBuff.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
			  	if(mstate!=null)sqlBuff.append(" and m.mstate=").append(mstate);
			    String sql = "select t.*,m.name,m.bank_name,m.bank_acct "+sqlBuff.toString()+" order by init_time desc";
			    String sqlCount = "select count(*)" + sqlBuff.toString();
				return queryForPage(sqlCount,sql, pageNo, AppParam.getPageSize(),TrOrders.class) ;
			}	
		 /**
		  * 下载线下充值审核数据
		  * @param pageNo
		  * @param uid
		  * @return
		  */
		  public CurrentPage<TrOrders> queryDLXXCZSH(Integer pageNo, String uid,Integer mstate) {
			  	String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
			  	StringBuffer sqlBuff=new StringBuffer("from tr_orders t,minfo m  where t.uid=m.id and t.uid=t.aid and t.ptype=" + Constant.DaiFuTransType.ACCOUNT_RECHARGE + " and t.state=" + Constant.DaiFuTransState.OFFLINE_RECHARGE_CERT_AUDIT_PROCESSIING + " and t.pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS + " and sys_date=");
			  	sqlBuff.append(date);
			  	if (!Ryt.empty(uid)) sqlBuff.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
			    if(mstate!=null)sqlBuff.append(" and m.mstate=").append(mstate);
			    String sql = "select t.*,m.name,m.bank_name,m.bank_acct "+sqlBuff.toString();
				return queryForCurrPage(sql, pageNo,TrOrders.class) ;
			}
		// 更新线下充值审核处理信息
		  public int[] updateXXCZSH(String []sqls){
			  return batchSqlTransaction(sqls);
		  }
		// 更新线下充值审核失败信息
		  public int[] updateFailXXCZSH(List<TrOrders> list,String option) throws Exception{
			  	String [] sqls=new String[list.size()*2];
			  	String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
				int time=DateUtil.getCurrentUTCSeconds();

				for (int i = 0; i < list.size(); i++) {
					TrOrders t=list.get(i);
					String oid = t.getOid();
					StringBuffer hlogSql=new StringBuffer("update hlog set tstat=");
					hlogSql.append(Constant.PayState.FAILURE);
					hlogSql.append(" where oid=");
					hlogSql.append(Ryt.addQuotes(oid));
					StringBuffer sqlTr=new StringBuffer("update tr_orders set sys_date =");
					  sqlTr.append(date).append(",sys_time =").append(time).append(",state=" + Constant.DaiFuTransState.RECHARGE_FAILURE + ",");
					  sqlTr.append("pstate=" + Constant.DaifuPstate.AUDIT_FAILURE + ",");
					  sqlTr.append(" audit_oper=").append(getLoginUser().getOperId()).append(",");//审核操作员
					  sqlTr.append("audit_remark=").append(Ryt.addQuotes(Ryt.sql(option))).append(", ");
					  sqlTr.append("audit_date=").append(date).append(", ");
					  sqlTr.append("audit_time=").append(time).append(" ");
					  sqlTr.append(" where oid='").append(oid).append("'");
					  sqls[i*2]=hlogSql.toString();
					  sqls[i*2+1]=sqlTr.toString();
				}
			  return batchSqlTransaction(sqls);
		  }
		 
		  /**
			 * 更新tr_orders表中的state状态
			 * @param oid
			 * @param state
			 * @param remark
			 * @return
			 */
			public int updateTrOrdersState(int gid,String oid,Integer state,String tseq,String remark){
				int date = DateUtil.today();
				int time= DateUtil.getCurrentUTCSeconds();
				String sql = "update tr_orders set gate= ? ,oper_date= ? ,state= ?,sys_date=?,sys_time=?,tseq=?,remark=concat(remark,?) where oid=?";
				Object[] args = new Object[]{gid,date,state,date,time,tseq,remark,oid};
				int[] argTypes = new int[]{Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};
		        return update(sql, args, argTypes);
			}
			/**
			 * 记录银行返回信息，用于中间状态信息的保存
			 * @param oid
			 * @param remark
			 * @return
			 */
			public String updateTrOrdersRetInfo(String oid, String remark,Integer isPay){
//				String sql = "update tr_orders set remark=concat(remark,?) where oid=?";
//				Object[] args = new Object[]{remark,oid};
//				int[] argTypes = new int[]{Types.VARCHAR,Types.VARCHAR};
				StringBuffer sql=new StringBuffer(); 
				sql.append("update tr_orders set remark=concat(remark,").append(Ryt.addQuotes(remark)).append(") ,").append("is_pay=").append(isPay).append("  where oid=").append(Ryt.addQuotes(oid));
				return sql.toString();
			}
			
			/****
			 * 更新TrOrder订单状态！
			 * @param gid
			 * @param oid
			 * @param state
			 * @param tseq
			 * @param remark
			 * @param bkDate
			 * @param bkTime
			 * @param resCode
			 * @param errorMsg
			 * @param type
			 * @param is_pay
			 * @return
			 */
			public String updateTrOrdersState(String oid,Integer state,String tseq,String remark,String bkDate,String bkTime,String resCode,String errorMsg,String type,Integer is_pay){
				int date = DateUtil.today();
//				int time= DateUtil.getCurrentUTCSeconds();
				StringBuffer sql=new StringBuffer();
				sql.append("update tr_orders set oper_date=").append(date).append(" , state=").append(state);
				sql.append(",tseq=").append(tseq==null?"''":Ryt.addQuotes(tseq)).append(",remark=concat(remark,'").append(remark+"')").append(",bank_date=").append(bkDate==null?"0":bkDate);
				sql.append(",bank_time=").append(DateUtil.getUTCTime(bkTime==null?"0":bkTime)).append(",res_code=").append(Ryt.addQuotes(resCode==null?"''":resCode)).append(",error_msg=").append(Ryt.addQuotes(errorMsg));
				sql.append(" , is_pay=").append(is_pay).append(" where oid=").append(Ryt.addQuotes(oid)).append("");
				return sql.toString();
			}
			public String[] queryOrderByOid(String mid, String oid) {
				String tseq = null;
				String table = Constant.HLOG;
				try {
					tseq = queryForStringThrowException("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid));
				} catch (Exception e) {
					table = Constant.TLOG;
					tseq = queryForStringThrowException("select tseq from " + table + " where mid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid));
				}
				return new String[]{table, tseq};
			}
			
			/**
			 * 查询代发请求银行失败数据 -mms
			 * @param uid 用户ID
			 * @param num 条数
			 * @param trans_flow 批次号
			 * 查询条件 订单状态（tlog/hlog表） 为请求银行失败 
			 * 订单状态为请求银行失败 4    发起端为mms data_source 5
			 * @return
			 */
			public CurrentPage<OrderInfo> queryDataForReqFail(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String tseq,Integer mstate,Integer bdate,Integer edate){
				StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
				if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
				if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
				if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
				tSql.append(" and tlog.tstat =").append(Constant.PayState.REQ_FAILURE);//状态为请求银行失败  4
				tSql.append(" and tlog.data_source=").append("5");
				if(bdate!=null&&edate!=null)	
					tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
				if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
				StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
				StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
				StringBuffer sql=new StringBuffer("select * from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
				if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
				sql.append(" order by a.sys_date desc ,a.sys_time desc");
				StringBuffer sqlCount =new StringBuffer("select count(*) from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
				sqlCount.append(" order by a.sys_date desc ,a.sys_time desc");
				return queryForPage(sqlCount.toString(),sql.toString(), pagNo, pageSize,OrderInfo.class) ;
			}
			
			/***
			 * 下载当日手工代付提交成功的数据
			 * @param uid
			 * @param trans_flow
			 * @param ptype
			 * @param tseq
			 * @param mstate
			 * @return
			 */
			public List<OrderInfo>  downSGDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate){
				Integer sysDate=DateUtil.today();
				StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
				if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
				if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
				if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
				/*tSql.append(" and tlog.tstat =").append(Constant.PayState.REQ_FAILURE);//状态为请求银行失败  4*/		     
				tSql.append(" and tlog.data_source=").append("5").append(" and tlog.p9=1");
				tSql.append("  and tlog.p12=").append(sysDate);
				if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
				StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
				StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
				StringBuffer sql=new StringBuffer("select a.*,m.*,c.cancel_state,c.cancel_reason from (").append(sqlCondition).append(") a left join cancel_log c on a.tseq=c.tseq ,minfo m where a.mid=m.id   ");
				if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
				sql.append(" order by a.sys_date desc ,a.sys_time desc");
				return query(sql.toString(), OrderInfo.class);
			}
			
			/****
			 * 修改订单状态   修改标示 是否多次代付
			 * @param tseq
			 * @param mid
			 * @param table
			 * @return
			 */
			public String modifyOrderState_SGDF(String tseq,String mid,String table){
				Integer sys_date=DateUtil.today();
				StringBuffer sql=new StringBuffer("update  ").append(table).append(" set tstat=3,p9=1, p12=").append(sys_date).append("  where 1=1 and tseq ='").append(tseq).append("'");
				sql.append(" and mid='").append(mid).append("'");
				return sql.toString();
			}
			
			/****
			 * 保存撤销订单
			 * @param cancel_id
			 * @param tseq
			 * @param cancel_type
			 * @param cancel_obj
			 * @param cancel_state
			 * @param operId
			 * @param cancel_reason
			 * @param bk_flag
			 * @param bk_date
			 * @param bk_time
			 * @param bk_code
			 * @param bk_seq
			 * @return
			 */
			public String savecancelLog(String cancel_id,String tseq,String cancel_type,String cancel_obj,String cancel_state,Integer operId,String cancel_reason,
					               Integer bk_flag,Integer bk_date,Integer bk_time,String bk_code,String bk_seq){
				int sys_date = DateUtil.today();
				int sys_time= DateUtil.getCurrentUTCSeconds();
				StringBuffer sql2=new StringBuffer();
				StringBuffer sql=new StringBuffer("insert into cancel_log(cancel_id,tseq,cancel_type,cancel_obj,cancel_state,sys_date,sys_time,cancel_reason");
				if(operId!=null)sql.append(",oper_id");
				if(bk_flag!=null)sql.append(",bk_flag");
				if(bk_date!=null)sql.append(",bk_date");
				if(bk_time!=null)sql.append(",bk_time");
				if(!Ryt.empty(bk_code))sql.append(",bk_code");
				if(!Ryt.empty(bk_seq))sql.append(",bk_seq");
				sql.append(") ");
				sql2.append(" value(").append(cancel_id).append(",'").append(tseq).append("',").append(cancel_type).append(",").append(cancel_obj);
				sql2.append(",").append(cancel_state).append(",").append(sys_date).append(",").append(sys_time).append(",").append(Ryt.addQuotes(cancel_reason));
				if(operId!=null)sql2.append(",").append(operId);
				if(bk_flag!=null)sql2.append(",").append(bk_flag);
				if(bk_date!=null)sql2.append(",").append(bk_date);
				if(bk_time!=null)sql2.append(",").append(bk_time);
				if(!Ryt.empty(bk_code))sql2.append(",").append(bk_code);
				if(!Ryt.empty(bk_seq))sql2.append(",").append(bk_seq);
				sql2.append(")");
				sql.append(sql2);
				return sql.toString();
			}
			
			
			/**
			 * 查询代发数据 -针对接口  手工代付结果同步
			 * @param uid 用户ID
			 * @param num 条数
			 * @param trans_flow 批次号
			 * 查询条件 订单状态（tlog/hlog表）
			 *   发起端为接口 data_source 1
			 * @return
			 */
			public CurrentPage<OrderInfo> queryDataForSGSYNC(Integer pagNo,Integer pageSize,String uid,String trans_flow,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate){
				StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
				if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
				if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
				if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
				if(state!=0){
					tSql.append(" and tlog.tstat =").append(state);//代付状态
				}else{
					tSql.append(" and tlog.tstat =").append(Constant.PayState.WAIT_PAY);//代付状态
				}
				if (gate!=0)tSql.append(" and tlog.gid=").append(gate);
				tSql.append(" and tlog.data_source=1"); //追加数据来源状态  data_source 
				if(bdate!=null&&edate!=null)	
					tSql.append(" and tlog.sys_date >= ").append(bdate).append(" and tlog.sys_date <= ").append(edate);
				if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
				StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
				StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
				StringBuffer sql=new StringBuffer("select * from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
				if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
				sql.append(" order by a.sys_date desc ,a.sys_time desc");
				StringBuffer sqlCount =new StringBuffer("select count(*) from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
				sqlCount.append(" order by a.sys_date desc ,a.sys_time desc");
				return queryForPage(sqlCount.toString(),sql.toString(), pagNo, pageSize,OrderInfo.class) ;
			}
			
			
			/***
			 * 查询 手工同步代付结果的数据
			 * @param uid
			 * @param trans_flow
			 * @param ptype
			 * @param tseq
			 * @param mstate
			 * @return
			 */
			public List<OrderInfo>  downSGSYNCDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate){
				StringBuffer tSql=new StringBuffer("select * from tlog where 1=1 ");
				if (!Ryt.empty(trans_flow))tSql.append(" and tlog.p8='").append(Ryt.sql(trans_flow)).append("'");//电银交易批次号
				if(!Ryt.empty(tseq))tSql.append(" and  tlog.tseq='").append(tseq).append("' ");
				if (!Ryt.empty(uid))tSql.append(" and tlog.mid='").append(Ryt.sql(uid)).append("' ");
				if(state !=0 )tSql.append(" and tlog.tstat =").append(state);//追加订单状态
				if(gate !=0)tSql.append(" and tlog.gid=").append(gate);//追加支付渠道  查询条件
				tSql.append(" and tlog.data_source=1 ");//追加数据来源  data_source 查询条件
				if(ptype==0)tSql.append(" and tlog.type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + "," + Constant.TlogTransType.PAYMENT_FOR_PUBLIC+ ")");else tSql.append(" and tlog.type=").append(ptype).append(" ");
				StringBuffer hlog =new StringBuffer(tSql.toString().replace("tlog", "hlog"));
				StringBuffer sqlCondition=tSql.append(" union all ").append(hlog);
				StringBuffer sql=new StringBuffer("select * from (").append(sqlCondition).append(") a,minfo m where a.mid=m.id  ");
				if(mstate!=null) sql.append(" and m.mstate=").append(mstate);
				sql.append(" order by a.sys_date desc ,a.sys_time desc");
				return query(sql.toString(), OrderInfo.class);
			}
			/**
			 * 通过网关获取手续费
			 * @param mid
			 * @param gate
			 * @return
			 * @throws Exception 
			 */
			public FeeCalcMode getFeeModeByGate(String mid,String gate)throws Exception{
				StringBuffer sql=new StringBuffer("select calc_mode,gid from fee_calc_mode where mid =");
				sql.append(Ryt.addQuotes(mid));
				sql.append(" and gate=");
				sql.append(gate);
				FeeCalcMode mode=queryForObject(sql.toString(),FeeCalcMode.class);
				if(null==mode)
					throw new Exception("该网关尚未配置");
				return mode;
			}
			
			public String getBkFeeModeByGate(String mid,String gate){
				StringBuffer sql=new StringBuffer("select bk_fee_mode from fee_calc_mode where mid =");
				sql.append(Ryt.addQuotes(mid));
				sql.append(" and gate=");
				sql.append(gate);
				String bkFeeMode=queryForString(sql.toString());
				if(Ryt.empty(bkFeeMode))
					return "";
				return bkFeeMode;
			}
			
	/*
	 * 自动代付信息维护查询
	 */
	public CurrentPage<Minfo> queryAutoDf(Integer pageNo, String mid,
			Integer mstate) {

		StringBuffer sql = new StringBuffer("select id,name,mstate,aid,auto_df_state,pbk_prov_id,");
		sql.append("pbk_name,pbk_branch,pbk_no,pbk_acc_no,pbk_acc_name, pbk_gate_id");
		sql.append(" from acc_infos ,minfo where  id=uid and acc_infos.aid=acc_infos.uid ");
		if (!Ryt.empty(mid))sql.append(" and id =").append(Ryt.addQuotes(mid));
		if (mstate != null)sql.append(" and mstate=").append(mstate);
		StringBuffer sqlCount = new StringBuffer("select count(*) from acc_infos ,minfo");
		sqlCount.append(" where id=uid  and acc_infos.aid=acc_infos.uid ");
		if (!Ryt.empty(mid))sqlCount.append(" and id =").append(Ryt.addQuotes(mid));
		if (mstate != null)sqlCount.append(" and mstate=").append(mstate);
		return queryForPage(sqlCount.toString(), sql.toString(), pageNo,AppParam.getPageSize(), Minfo.class);
	}
	
	/*
	 * 根据商户号查询出自动代付的信息
	 */
	
	public Minfo queryByidAutoDf(String mid){
		StringBuffer sql = new StringBuffer("select id,name,mer_trade_type,aid,auto_df_state,pbk_prov_id,");
		sql.append("pbk_name,pbk_branch,pbk_no,pbk_acc_no,pbk_acc_name, pbk_gate_id");
		sql.append(" from acc_infos ,minfo where  id=uid and acc_infos.aid=acc_infos.uid ");
		sql.append(" and id=").append(Ryt.addQuotes(mid));
		return queryForObject(sql.toString(), Minfo.class);
	}
	
	public void updateMerAutoDf(Minfo minfo) throws Exception {
		Object[] obj = {
				minfo.getAutoDfState(),minfo.getPbkProvId(),minfo.getPbkName(),
				minfo.getPbkBranch(),minfo.getPbkAccNo(),minfo.getPbkAccName(),
				minfo.getPbkNo(),minfo.getPbkGateId(),minfo.getId()
				};
		int[] type = { 
				Types.TINYINT,Types.VARCHAR,Types.VARCHAR,
				Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,		
				Types.VARCHAR,Types.TINYINT,Types.VARCHAR
				};
		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set auto_df_state =?,pbk_prov_id =?,pbk_name =?,pbk_branch =?,");
		sb.append("pbk_acc_no =?,pbk_acc_name =?,pbk_no =?, pbk_gate_id=?,");	
		sb.append(" last_update=DATE_FORMAT(NOW(),'%Y%m%d%H%i%s') where id =?");
		update(sb.toString(), obj, type);
}
}

