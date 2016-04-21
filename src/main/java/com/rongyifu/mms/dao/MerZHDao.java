package com.rongyifu.mms.dao;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.BatchB2B;
import com.rongyifu.mms.bean.CusContactInfos;
import com.rongyifu.mms.bean.CusInfos;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.OrderInfos;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.bean.TrOrders_Batch;
import com.rongyifu.mms.bean.UserBkAcc;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

@SuppressWarnings("unchecked")
public class MerZHDao extends PubDao{
	
	/**
	 * 增加账户
	 * @param info 账户对象
	 * @throws Exception 
	 */
	public void add(AccInfos info) throws Exception{
		
		
		Object[] obj = new Object[] {info.getAid(),info.getUid(),info.getInitDate(),info.getAname()};
		String sql="insert into acc_infos (aid,uid,init_date,aname) values (?,?,?,?)";
		update(sql, obj);
		
	}
	/**
	 * 查询所有账户号
	 * @return 所有账户号
	 */
	public List<AccInfos> selectAllAid(){
		String sql="select aid from acc_infos";
		return query(sql, AccInfos.class);
	}
	//增加账户
	public void add(String aid,String aname,String mid) throws DuplicateKeyException{
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		Object[] obj = new Object[] {aid,mid,date,aname};
		String sql="insert into acc_infos (aid,uid,init_date,aname) values (?,?,?,?)";
		update(sql, obj);
	}
	/**
	 * 得到账户个数
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public int getAname1(String mid){
		String sql="select count(*) from acc_infos where uid ='"+mid+"'";
		return queryForInt(sql);
	}
	/**开启,关闭余额
	 * @param mid 商户号（用户ID）
	 * @param aid 账户号
	 * @param state 状态 0-生成，1-正常，2-关闭
	 */
	 
	public int editPF(String mid,String aid,int payFlag){
		String sql="update acc_infos set pay_flag=? where uid=? and aid=?";
		Object[] obj=new Object[]{payFlag,mid,aid};
		return update(sql,obj);
	}
	
	/**关闭账户
	 * 
	 * @param mid  商户号（用户ID）
	 * @param aid 账户号
	 * @param state 状态，0-生成，1-正常，2-关闭
	 * @return
	 */
	public int closeZH(String mid,String aid,int state){
		String sql="update acc_infos set state=? where uid=? and aid=?";
		Object[] obj=new Object[]{state,mid,aid};
		return update(sql,obj);
	}
	/**查询密码
	 * 
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public String getPass(String mid) {
		return queryForString("select pay_pwd from minfo where id='"+ mid+"'");
	}
	/**查询密码
	 * 
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public String getPass() {
		LoginUser loginUser = getLoginUser();
		return queryForString("select pay_pwd from minfo where id=" + loginUser.getMid());
	}
	/**
	 * 查询账户信息
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public List<AccInfos> queryZHYE(String mid){
		String sql="select * from acc_infos where uid=aid and uid ="+Ryt.addQuotes(mid);
		return query(sql,AccInfos.class);
	} 
	public List<Minfo> createZHINFO(String mid){
		String sql="select * from minfo where id='"+mid+"'";
		return query(sql, Minfo.class);
	}
	/**
	 * 查询账户下的所有用户名称
	 * @return
	 */
	public Map<String, String> getZH(){
		Map<String, String> result = new HashMap<String, String>();
		LoginUser u = getLoginUser();
		if(u==null) return result;
		
		//结算账户不可 用来付款，故不显示  zhi
		String sql="select aid, aname from acc_infos where uid!=aid and uid = '" + u.getMid() +"' and acc_type=1";
		List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
		for(Map<String,Object> m : aList){
			result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
		}
		return result;
	}
	public Map<String, String> getZH2(){
		Map<String, String> result = new HashMap<String, String>();
		LoginUser u = getLoginUser();
		if(u==null) return result;
		//只查询交易账号 即为 acc_type=1 
		String sql="select aid, aname from acc_infos where  uid = '" + u.getMid() +"' and acc_type=1";
		List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
		for(Map<String,Object> m : aList){
			result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
		}
		return result;
	}
	
	/***
	 * 返回所有的商户账号
	 * @return
	 */
	public Map<String, String> getZH_All(){
		Map<String, String> result = new HashMap<String, String>();
		//只查询交易账号 即为 acc_type=1 
		String sql="select aid, aname from acc_infos where  and acc_type=1";
		List<Map<String,Object>> aList = getJdbcTemplate().queryForList(sql);
		for(Map<String,Object> m : aList){
			result.put(m.get("aid").toString(),m.get("aid").toString()+"【"+ m.get("aname").toString()+"】");
		}
		return result;
	}
	/**
	 * 查询明细
	 * @param pageNo
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate,
	 * @param edate,
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @param oid,
	 * @return
	 */
	  public CurrentPage<TrOrders> queryMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow) {
		    LoginUser u = getLoginUser();
		    StringBuffer sql = new StringBuffer("select * from tr_orders  where 1=1 and aid=uid ");
//			if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");
			if (ptype!=null) sql.append(" and ptype = ").append(ptype);
			if (state!= null) sql.append(" and state = ").append(state);
			if (!Ryt.empty(oid)) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
			if(!Ryt.empty(trans_flow))sql.append(" and trans_flow='").append(trans_flow).append("' ");
			sql.append(" and !(ptype=0 and trans_flow is not null)");
			sql.append(" and uid= ").append(Ryt.addQuotes(u.getMid()));
			sql.append(" and sys_date >= ").append(bdate);
			sql.append(" and sys_date <= ").append(edate);
			sql.append(" order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
		}
	  
	  /****
	   * 账户收支明细查询
	   * @param pageNo
	   * @param ptype
	   * @param uid
	   * @param aid
	   * @param bdate
	   * @param edate
	   * @param oid
	   * @return
	   */
	  public CurrentPage<AccSeqs> queryZHSZMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,String oid) {
		    StringBuffer sql = new StringBuffer("select  acc_seqs.uid  as uid,acc_seqs.aid as aid,tr_orders.oid as oid,tr_orders.trans_flow as trans_flow,amt,all_balance,tr_date,tr_orders.ptype as ptype,acc_seqs.rec_Pay,tr_orders.init_time as init_time from   tr_orders left join   acc_seqs on  acc_seqs.uid=tr_orders.uid and  acc_seqs.tb_id=tr_orders.oid where 1=1 ");
		    LoginUser u = getLoginUser();
		    sql.append("and acc_seqs.uid=acc_seqs.aid and acc_seqs.uid = '").append(u.getMid()).append("'");
			if(!Ryt.empty(oid))sql.append(" and tr_orders.oid='").append(oid).append("'");
			sql.append(" and !(tr_orders.ptype=0 and tr_orders.trans_flow is not null)");
			if(ptype!=null)sql.append(" and tr_orders.ptype=").append(ptype).append(" ");
			sql.append(" and tr_orders.state in (12,22,34) ");
			sql.append(" and tr_date >= ").append(bdate);
			sql.append(" and tr_date <= ").append(edate);
			sql.append(" order by tr_date desc,tr_time desc");
			return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
		}
	  
	  /****
	   * 账户交易信息明细查询 (新修改)
	   * @param pageNo
	   * @param uid
	   * @param ptype
	   * @param bdate
	   * @param edate
	   * @param state
	   * @param oid
	   * @return
	   */
	  public CurrentPage<TrOrders>  queryZHJYMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow){
		  LoginUser u = getLoginUser();
		    StringBuffer sql = new StringBuffer("select * from tr_orders  where 1=1 and uid=aid ");
//			if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");
			if (ptype!=null) sql.append(" and ptype = ").append(ptype);
			if (state!= null) sql.append(" and state = ").append(state);
			if (!Ryt.empty(oid)) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
			sql.append(" and !(ptype=0 and trans_flow is not null)");
//			sql.append(" and ptype!=0 ");
			sql.append(" and uid= ").append(u.getMid());
			if(!Ryt.empty(trans_flow))sql.append(" and trans_flow='").append(trans_flow).append("' ");
			sql.append(" and sys_date >= ").append(bdate);
			sql.append(" and sys_date <= ").append(edate);
			sql.append("  order by sys_date desc ,sys_time desc");
			return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
	  }
	  
	  
	  /**
		 * 上传凭证点击查看信息触发该方法
		 * @param pageNo
		 * @param aid,账户ID
		 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
		 * @param bdate,
		 * @param edate,
		 * @param state,固定1
		 * @param oid,
		 * @param trans_flow
		 * @param offline_flag 1 线下支付
		 * @return
		 */
		  public CurrentPage<TrOrders> queryMX_detail(Integer pageNo, String aid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow) {
			    LoginUser u = getLoginUser();
			    StringBuffer sql = new StringBuffer("select * from tr_orders  where 1=1 ");
				if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");
				if (ptype!=null) sql.append(" and ptype = ").append(ptype);
//				if (state!= null) sql.append(" and state = ").append(1);
				sql.append(" and !(ptype=0 and trans_flow is not null) ");
				sql.append(" and Offline_flag=").append(1).append(" ");
				if (!Ryt.empty(oid)&&trans_flow.equals("null")) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
				if(!trans_flow.equals("null"))sql.append(" and trans_flow='").append(trans_flow).append("'");
				sql.append(" and uid= ").append(Ryt.addQuotes(u.getMid()));
				sql.append(" and sys_date >= ").append(bdate);
				sql.append(" and sys_date <= ").append(edate);
				
				sql.append(" order by sys_date desc ,sys_time desc");
				return queryForCurrPage(sql.toString(), pageNo,TrOrders.class) ;
			}
	  
	  
	  /**
		 * 上传凭证--查询明细
		 * @param pageNo
		 * @param ptype,
		 * @param bdate,
		 * @param edate,
		 * @param state,
		 * @param oid,
		 * @return
		 */
		  public CurrentPage<TrOrders_Batch> queryMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,Integer state) {
			    LoginUser u = getLoginUser();
			    StringBuffer sql = new StringBuffer("select uid,aid ,oid,aname ,count(*) as count ,state,sum(trans_amt) as trans_sum_amt,sum(trans_fee) as trans_sum_fee,sum(pay_amt) as trans_sum_payamt,trans_flow,trans_proof ,ptype,init_time as sysDate,pstate from tr_orders  where 1=1 and uid=aid");
				//if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");
//				if (ptype!=null) sql.append(" and ptype = ").append(ptype);
				if (state!= null) sql.append(" and state in ( ").append(31).append(" , ").append(32).append(" , ").append(33).append(")");
//				if (!Ryt.empty(oid)) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
				sql.append(" and !(ptype=0 and trans_flow is not null)");
//				sql.append(" and pstate=0");
				sql.append(" and Offline_flag=").append(1).append(" ");
				sql.append(" and uid= ").append(u.getMid());
				sql.append(" and sys_date >= ").append(bdate);
				sql.append(" and sys_date <= ").append(edate);
				sql.append(" group by trans_flow ,ifnull(trans_flow,oid) order by sys_date desc ,sys_time desc");
				
				return queryForCurrPage(sql.toString(), pageNo,TrOrders_Batch.class);
			}
		  
		  
		  /**
			 * 审批凭证查询明细
			 * @param pageNo
			 * @param aid,账户ID
			 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
			 * @param bdate,
			 * @param edate,
			 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
			 * @param oid,
			 * @return
			 */
			  public CurrentPage<TrOrders_Batch> queryMX_A_SPPZ(Integer pageNo, String uid,Integer ptype,Integer bdate,Integer edate,Integer state,Integer mstate) {
//				    LoginUser u = getLoginUser();
				    StringBuffer sql = new StringBuffer("select t.uid,t.aid ,t.oid,t.aname ,count(*) as count ,ifnull(sum(trans_amt),0) as trans_sum_amt,ifnull(sum(trans_fee),0) as trans_sum_fee,ifnull(sum(pay_amt),0) as trans_sum_payamt,t.trans_flow,t.trans_proof ,t.ptype,t.sys_Date,t.recharge_amt from tr_orders t,minfo m where t.uid=m.id and t.uid=t.aid");
					if (!Ryt.empty(uid)) sql.append(" and t.uid = '").append(Ryt.sql(uid)).append("'");
					/*if (ptype!=null) sql.append(" and ptype = ").append(ptype);*/
					if (state!= null) sql.append(" and t.state = ").append(32);
					if(mstate!=null)sql.append(" and m.mstate=").append(mstate);
					sql.append(" and t.pstate=0 ");
					sql.append(" and t.Offline_flag=").append(1).append(" ");
//					if (!Ryt.empty(oid)) sql.append(" and oid = ").append(Ryt.addQuotes(oid));
					sql.append(" and !(t.ptype=0 and t.trans_flow is not  null) ");
					sql.append(" and t.sys_date >= ").append(bdate);
					sql.append(" and t.sys_date <= ").append(edate);
					sql.append(" group by trans_flow ,ifnull(trans_flow,oid) order by sys_date desc ,sys_time desc");
					
					return queryForCurrPage(sql.toString(), pageNo,TrOrders_Batch.class) ;
				}
	  /**
	   * 查询流水
	   * @param pageNo
	   * @param aid,账户ID
	   * @param bdate
	   * @param edate
	   * @return
 	   */
	  public CurrentPage<AccSeqs> queryLS(Integer pageNo, String aid,Integer bdate,Integer edate) {
		  
		    LoginUser u = getLoginUser();
		  
		    StringBuffer sql = new StringBuffer("select * from acc_seqs  where 1=1 ");
			if (!Ryt.empty(aid)) sql.append(" and aid = '").append(Ryt.sql(aid)).append("'");
			sql.append(" and uid= ").append(u.getMid());
			sql.append(" and tr_date >= ").append(bdate);
			sql.append(" and tr_date <= ").append(edate);
			sql.append(" order by id desc");
 			return queryForCurrPage(sql.toString(), pageNo,AccSeqs.class) ;
 		}
 	  
	  /**
	   * 查询我要付款
	   * @param aid,账户ID
	   * @return
	   */
	  public AccInfos queryFK(String aid){
		  String sql="select * from acc_infos where aid ="+Ryt.addQuotes(Ryt.sql(aid))+" and aid=uid";
		  return queryForObject(sql, AccInfos.class) ;
	  }
	  /**
	   * 查询账户下的状态为正常的用户名称
	   * @return
	   */
		public Map<String, String> getZHUid() {
			LoginUser loginUser = getLoginUser();
			if(loginUser==null) return  new HashMap<String, String>();
			String sql="select aid,aname from acc_infos where state=1 and uid = '" + loginUser.getMid()+"'";
			return queryToMap(sql);
		}
		/**
		 * 查询账户下的状态为正常的用户名称，并且不可以自己转给自己
		 * @param uid，用户ID
		 * @return
		 */
	public Map<String, String> getZHByUid(String uid) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		LoginUser u = getLoginUser();
		if(u==null) return result;
		 
		String sql="select * from acc_infos where state=1 and uid!=aid and uid = '" + Ryt.sql(uid) + "'  and uid != '" + u.getMid()+"'";
		List<Map<String,Object>> aList = queryForList(sql);
		for(Map<String,Object> m : aList){
			result.put(m.get("aid").toString(), m.get("aid").toString()+"【" +m.get("aname").toString()+"】");
		}
		return result;
	}
	/**
	 * 对方账户信息
	 * @param toAid，对方账户
	 * @return
	 */
	public  AccInfos queryToAid(String toAid){
		String sql="select * from acc_infos where  aid=uid and aid = "+Ryt.addQuotes(toAid);
		return queryForObject(sql,AccInfos.class);
	}
	
	/**
	 * 确定我要付款
	 * @param aid，账户ID
	 * @param mid ，商户号（用户ID）
	 * @param toAid，交易对方账户ID
	 * @param toUid ，交易对方用户ID
	 * @param payAmt ，结算金额
	 * @throws Exception  
	 *
	 */
	public void qdzf(String aid,String mid,String toAid,String toUid,String payAmt,String oid,String oid2) throws Exception{
		int pay_amt=Integer.parseInt(Ryt.mul100(payAmt));
		
		//int date = DateUtil.today();//new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		//long initTime = DateUtil.getIntDateTime();//new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		//int time= DateUtil.getCurrentUTCSeconds();
		
		//		String oid = genOidBySysTime();
		//		String oid2 = genOidBySysTime();
		//		if(oid.equals(oid2)) oid2 = oid + "0";
		
		//String aname1= queryForString("select aname from acc_infos where aid ='"+aid+"'");
		//String aname2= queryForString("select aname from acc_infos where aid ='"+toAid+"'");
		
		//明细表tr_orders
		//String sqlTr1=genAddTrOrdersSql(oid, mid.toString(), aid, 3,aname1,initTime,date,time,toUid,toAid, pay_amt, 2);
		//String sqlTr2=genAddTrOrdersSql(oid2, toUid, toAid, 4,aname2, initTime, date,time, mid.toString(),aid, pay_amt, 2);
		String sqlTr1=updateTrOrdersStateSql(oid,2,"交易成功");
		String sqlTr2=updateTrOrdersStateSql(oid2,2,"交易成功");
		
		//		String sqlAccSeqs1 = genAddAccSeqSql(mid,aid,pay_amt,0,pay_amt,1,"tr_orders",oid,"付款到融易付账户");
		//		String sqlAccSeqs2 = genAddAccSeqSql(toUid,toAid,pay_amt,0,pay_amt,0,"tr_orders",oid2,"从融易付账户收款");
			List<String> fkSql=genAddAccSeqSqls(mid,aid,pay_amt,0,pay_amt,1,"tr_orders",oid,"付款到融易付账户");
			List<String> skSql=genAddAccSeqSqls(toUid,toAid,pay_amt,0,pay_amt,0,"tr_orders",oid2,"从融易付账户收款");
		 		
		List<String>  sqlsList = new ArrayList<String>();// 批处理
		sqlsList.add(sqlTr1.toString());
		sqlsList.add(sqlTr2.toString());
		sqlsList.addAll(fkSql);
		sqlsList.addAll(skSql);
		//sqlsList.add(sqlAccSeqs1);
		//sqlsList.add(sqlAccSeqs2);
		
		batchSqlTransaction2(sqlsList);
		
		sqlsList.clear();
	}
	/**
	 * 查询网关号，在商户手续费计算公式表
	 * 查询商户可用的银行网关
	 *  trans_mode,0– 网上支付
	 *  state,0-关闭，1-正常
	 * @return
	 */
	public Map<Integer,String> getPayGates(){
		String sql="select gate,calc_mode from fee_calc_mode where mid=1 and trans_mode=0 and state=1 ";
		return queryToMap2(sql);
	}
	 /**
	  * 充值 yang.yaofeng
	  * @param oid,系统订单号
	  * @param mid,商户号（用户ID）
	  * @param trans_amt,交易金额
	  * @param priv,订单信息
	  * @return
	 * @throws Exception 
	  */
	public int[] insertTrOrders(String oid,String aname,
			long transAmt,Integer transFee,long payAmt, String priv,Integer gate) throws Exception{
//		Object[] objArr=new Object[]{oid,mid,mid,aname,DateUtil.getIntDateTime(),0,transAmt,transFee,payAmt,0,"账户充值",priv,DateUtil.today()};
//		String trOrderSql="insert into tr_orders(oid,uid,aid,aname,init_time,ptype,trans_amt,trans_fee,pay_amt,state,remark,priv,sys_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		return update(sql,objArr);
		String mid=getLoginUserMid();
		Map<String, String> hlogMap=new HashMap<String, String>();
		Map<String, String> trOrdersMap =new HashMap<String, String>();
		hlogMap.put("tseq", Ryt.genOidBySysTime());
		hlogMap.put("version", "10");
		hlogMap.put("ip", "3232251650");
		hlogMap.put("mdate", DateUtil.today()+"");
		hlogMap.put("oid", Ryt.addQuotes(oid));
		hlogMap.put("mid", Ryt.addQuotes(mid));
		hlogMap.put("amount", payAmt+"");
		hlogMap.put("type", String.valueOf(Constant.TlogTransType.B2B_RECHARGE));
		hlogMap.put("sys_date", DateUtil.today()+"");
		hlogMap.put("init_sys_date", DateUtil.today()+"");
		hlogMap.put("sys_time", DateUtil.getCurrentUTCSeconds()+"");
		hlogMap.put("gate", gate+"");
		hlogMap.put("tstat", "1");
		hlogMap.put("gid", gate+"");
		hlogMap.put("pay_amt", transAmt+"");
		trOrdersMap.put("oid", Ryt.addQuotes(oid));
		trOrdersMap.put("uid", Ryt.addQuotes(mid));
		trOrdersMap.put("aid", Ryt.addQuotes(mid));
		trOrdersMap.put("aname", Ryt.addQuotes(aname));
		trOrdersMap.put("init_time", DateUtil.getIntDateTime()+"");
		trOrdersMap.put("ptype", "0");
		trOrdersMap.put("trans_amt", transAmt+"");
		trOrdersMap.put("trans_fee", transFee+"");
		trOrdersMap.put("pay_amt", payAmt+"");
		trOrdersMap.put("state", "0");
		trOrdersMap.put("remark", Ryt.addQuotes("账户充值"));
		trOrdersMap.put("priv",  Ryt.addQuotes(priv));
		trOrdersMap.put("sys_date", DateUtil.today()+"");
		String []sqls=RecordLiveAccount.getInsetSqls(hlogMap, trOrdersMap);
		return batchSqlTransaction(sqls);
	}
	/**
	  * 付款到银行账户
	  * @param oid,系统订单号
	  * @param mid,商户号（用户ID）
	  * @param aid,账户ID
	  * @param trans_amt,交易金额
	  * @param priv,订单信息
	  * @return
	 * @throws Exception 
	 * @throws Exception 
	  */
	public int payToOtherCom(TrOrders trOrders) throws Exception{
		return saveObject(trOrders);
	}
	/**
	 * 查询订单的状态
	 * @param orderId,系统订单号
	 * @return
	 */
	public int queryOrderState(String orderId){
		String sql="select state from tr_orders where oid=?";
		return queryForInt(sql, new Object[]{orderId});
	}
	/**
	 * 确定提现 yang.yaofeng
	 * @param mid，商户号（用户ID）
	 * @param aid,账户ID
	 * @param transAmt,交易金额
	 * @return
	 */
	public int[] editTX(String mid,String aid,String payAmt){
				
				String oid = Ryt.genOidBySysTime();
				int date = DateUtil.today();
				long initTime = DateUtil.getIntDateTime();
				int time=DateUtil.getCurrentUTCSeconds();
				
				long payAmt_=Ryt.mul100toInt(payAmt);
				Map<String, Object> p = getFeeModeAid(aid, "tixian_fee_mode");
				int trFee=(int) Ryt.mul100toInt(ChargeMode.reckon(p.get("tixian_fee_mode").toString(),payAmt));
				long transAmt=payAmt_+trFee;
//				StringBuffer sql=new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,sys_time,");
//				sql.append("ptype,trans_amt,trans_fee,pay_amt,state,remark) values ('");
//				sql.append(Ryt.sql(oid)).append("','").append(Ryt.sql(mid)).append("','").append(Ryt.sql(aid)).append("','").append(p.get("aname").toString()).append("',");
//				sql.append(initTime).append(",").append(date).append(",").append(time).append(",1,");
//			    sql.append(transAmt).append(",").append(trFee).append(",").append(payAmt_).append(",21,'')");
//			    String sql2 = "update acc_infos set balance = balance - " +  transAmt + " where aid = " +Ryt.addQuotes(Ryt.sql(aid));
			    Map<String, String> hlogMap=new HashMap<String, String>();
				Map<String, String> trOrdersMap =new HashMap<String, String>();
				trOrdersMap.put("oid", Ryt.addQuotes(oid));
				trOrdersMap.put("uid", Ryt.addQuotes(mid));
				trOrdersMap.put("aid", Ryt.addQuotes(mid));
				trOrdersMap.put("aname", Ryt.addQuotes(p.get("aname").toString()));
				trOrdersMap.put("init_time", initTime+"");
				trOrdersMap.put("sys_date", date+"");
				trOrdersMap.put("sys_time",time+"");
				trOrdersMap.put("ptype", "1");
				trOrdersMap.put("trans_amt", transAmt+"");
				trOrdersMap.put("trans_fee", trFee+"");
				trOrdersMap.put("pay_amt", payAmt_+"");
				trOrdersMap.put("state", "21");
				trOrdersMap.put("remark", "''");
				hlogMap.put("tseq", Ryt.genOidBySysTime());
				hlogMap.put("version", "10");
				hlogMap.put("ip", "3232251650");
				hlogMap.put("mdate", date+"");
				hlogMap.put("oid", Ryt.addQuotes(oid));
				hlogMap.put("mid", Ryt.addQuotes(mid));
				hlogMap.put("amount", payAmt_+"");
				hlogMap.put("type", String.valueOf(Constant.TlogTransType.WITHDRAW_DEPOSIT_PUBLIC));
				hlogMap.put("sys_date", date+"");
				hlogMap.put("init_sys_date", date+"");
				hlogMap.put("sys_time", time+"");
				hlogMap.put("tstat", "1");
				hlogMap.put("gate", "0");
				hlogMap.put("gid", "0");
				hlogMap.put("pay_amt", transAmt+"");
				String []insertSqls=RecordLiveAccount.getInsetSqls(hlogMap, trOrdersMap);
				String sql=RecordLiveAccount.calUsableBalance(mid, mid, transAmt, 1);//减少用户余额
			    return batchSqlTransaction(new String[]{insertSqls[0],insertSqls[1],sql});
	       }

	/**
	 * 查询账户下的状态为正常的用户名称（结算账户）
	 * @param uid，用户ID
	 * @return
	 */
	public Map<String, String> getZHUid(String uid) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		LoginUser u = getLoginUser();
		if(u==null) return result;
		 
		String sql="select aid, CONCAT(aid,'【',aname,'】') as aname from acc_infos where state=1 and uid=aid ";
			sql+=" and  uid= "+Ryt.addQuotes(uid)+"";
		return queryToMap(sql);
	}
	
		/**
		 * 获取商户的公钥
		 * @return
		 */
		public String getMerPublicKey(String mid){
			String sql="select public_key from minfo where id='"+mid+"'";
			return queryForString(sql);
		}
	/**
	 * 用户银行账户新增
	 * @param uid，用户ID
	 * @param accName，开户帐号名称
	 * @param bkName，开户银行名称
	 * @param provId，开户省份
	 * @param accNo，开户银行账号
	 * @return
	 */
	public int addUserBkAcc(String uid,String accName,String bkName,Integer gate,Integer provId,String accNo){
		String bkNo=queryForString("select bf_bk_no from gate_route where gid="+gate);
		String sql="insert into user_bk_acc (uid,bk_no,acc_name,bk_name,gate,prov_id,acc_no) values(?,?,?,?,?,?,?)";
		Object[] obj=new Object[]{uid,bkNo,accName,bkName,gate,provId,accNo};
		return update(sql,obj);
	}
	/**
	 * 获取全部银行账户信息
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<UserBkAcc> getUserBkAcc(Integer pageNo){
		LoginUser u = getLoginUser();
		StringBuffer sql=new StringBuffer("select * from user_bk_acc where uid='");
		sql.append(u.getMid()).append("'");
		String sqlCount =	sql.toString().replace("*","count(*)");
		return queryForPage(sqlCount,sql.toString(), pageNo, new AppParam().getPageSize(),UserBkAcc.class) ;
	}
	/**
	 * 获取单个银行账户信息
	 * uid+accNo ,Unique Index
	 * @param uid，用户ID
	 * @param accNo，开户银行账号
	 * @return
	 */
	public List<UserBkAcc> getOneUserBkAcc(String uid,String accNo) {
		StringBuffer sql=new StringBuffer("select * from  user_bk_acc where uid ='");
		sql.append(Ryt.sql(uid)).append("' and acc_no='").append(Ryt.sql(accNo)).append("'");
		List<UserBkAcc> l = query(sql.toString(), UserBkAcc.class);
		return l;
	}
	/**
	 * 删除银行账户信息
	 * @param uid，用户ID
	 * @param accNo，开户银行账号
	 * @return
	 */
	public int deleteOneUserBkAcc(String uid,String accNo){
		String sql="delete from user_bk_acc where uid=? and acc_no=?";
		Object[] obj=new Object[]{uid,accNo};
		return update(sql,obj);
		
	}
	/**
	 * 修改单个银行账户信息
	 * @param uid，用户ID
	 * @param aBkName，新开户银行名称
	 * @param aProvId，新开户省份
	 * @param aAccNo，原开户银行账号
	 * @param bAccNo，修改后的新开户银行账号
	 * @return
	 */
	public int editOneUserBkAcc(String uid,String aBkName,Integer aGate,Integer aProvId,String aAccNo,String bAccNo){
		String sql="update  user_bk_acc set bk_name=?,gate=?, prov_id =?, acc_no=? where uid=? and acc_no=?";
		Object[] obj=new Object[]{aBkName,aGate,aProvId,aAccNo,uid,bAccNo};
		return update(sql,obj);
		
	}
	/**
	 * 查询是否已经进行支付密码维护
	 * @return
	 */
	public int getMinfoPayPwd(){
		LoginUser u=getLoginUser();
		StringBuffer sql=new StringBuffer("select count(*) from minfo where id='");
		sql.append(u.getMid()).append("' and pay_pwd!=''");
		return queryForInt(sql.toString());
	}
	
	
	/**
	 * 添加,修改支付密码
	 * @param mid,商户号（用户ID）
	 * @param md5,密码
	 * @return
	 */
	public boolean editMinfoPayPwd( String mid,String md5) {
		String sql = "update minfo set pay_pwd = ? where id=";
		sql+=mid;
		try {
			return getJdbcTemplate().update(sql, new Object[] { md5 },
				new int[] { Types.VARCHAR}) == 1;
		} catch (Exception e) {
		}
		return false;
	}
	


	/*	
	  得到关联客户信息
	 public List<CusInfos> getCusInfos(){
	StringBuffer sql = new StringBuffer("select cid,ctype,cname,trade_type from cus_infos ");
	sql.append(" where cid in ");
	sql.append(" ( select cus_id from ass_cus where master_id =  ").append(getLoginUserMid()).append(")");
	 List<CusInfos> rList = new ArrayList<CusInfos>();
	List<Map<String,Object>> aList = queryForList(sql.toString());
	if(aList==null) return null;
	for(Map<String,Object> m : aList){
		CusInfos cusInfos = new CusInfos();
		cusInfos.setCid(m.get("cid").toString());
		cusInfos.setCname(m.get("cname").toString());
		cusInfos.setCtype(Integer.parseInt(m.get("ctype").toString()));
		cusInfos.setTradeType(Integer.parseInt(m.get("tradeType").toString()));
		//cusInfos.setUserBkAccSet(queryForList("select * from user_bk_acc  where uid = '" + cusInfos.getCid()+"'",UserBkAcc.class));
		 String sql2 = "select uid, bk_name, prov_id, acc_no from user_bk_acc  where uid = '" + cusInfos.getCid() +"'" ;
		 List<UserBkAcc> rList2 = new ArrayList<UserBkAcc>();
		 List<Map<String,Object>> aList2 = queryForList(sql2.toString());
		 for(Map<String,Object> m2 : aList2){
			UserBkAcc userBkAcc = new UserBkAcc();
			userBkAcc.setUid(m2.get("uid").toString());
			userBkAcc.setBkName(m2.get("bk_name").toString());
			userBkAcc.setAccNo(m2.get("acc_no").toString());
			userBkAcc.setProvId(Integer.parseInt(m2.get("prov_id").toString()));
			rList2.add(userBkAcc);
		 }
		 cusInfos.setUserBkAccSet(rList2);
		 
		 String sql3 = "select uid, contact, cell, position from cus_contact_infos  where uid = '" + cusInfos.getCid() +"'" ;
		 List<CusContactInfos> rList3 = new ArrayList<CusContactInfos>();
		 List<Map<String,Object>> aList3 = queryForList(sql3.toString());
		 for(Map<String,Object> m3 : aList3){
			CusContactInfos cusContactInfos = new CusContactInfos();
			cusContactInfos.setUid(m3.get("uid").toString());
			cusContactInfos.setContact(m3.get("contact").toString());
			cusContactInfos.setCell(m3.get("cell").toString());
			cusContactInfos.setPosition(m3.get("position").toString());
			rList3.add(cusContactInfos);
		 }
		 cusInfos.setCusContactInfosSet(rList3);
		//cus.setCusContactInfosSet(queryForList("select * from cus_contact_infos where uid =  " + cus.getCid() ,CusContactInfos.class));
		//cusInfos.setUserBkAccSet(queryForList("select * from user_bk_acc  where uid = '" + cusInfos.getCid()+"'",UserBkAcc.class));
		rList.add(cusInfos);
	}
	return rList;
}*/
	/**
	 * 得到关联客户信息,客户信息维护的信息查询
	 * @param c_cid,客户ID
	 * @return
	 * @throws Exception 
	 */
	public List<CusInfos> getCusInfos(String c_cid) throws Exception{
	StringBuffer sql = new StringBuffer("select cid,ctype,cname,trade_type from cus_infos ");
	if(Ryt.empty(c_cid)){
	sql.append(" where cid in ( select cus_id from ass_cus where master_id = ");
	sql.append(getLoginUserMid()).append(")");
	}else{
		sql.append(" where cid= '").append(c_cid).append("'");
	}
	 List<CusInfos> aList = new ArrayList<CusInfos>();
	aList=query(sql.toString(), CusInfos.class);
	for( CusInfos cus : aList){
		cus.setCusContactInfosSet(query("select * from cus_contact_infos where uid = '" + cus.getCid()+"'" ,CusContactInfos.class));
		cus.setUserBkAccSet(query("select * from user_bk_acc  where uid = '" + cus.getCid()+"'",UserBkAcc.class));
		}
		return aList;
}
	/**
	 * 	获取单个联系人信息
	 * @param id，cus_contact_infos表，自增长唯一标识
	 * @return
	 */
	public List<CusContactInfos> getOneCusContactInfos(Integer id) {
		StringBuffer sql=new StringBuffer("select * from  cus_contact_infos where id =");
		sql.append(id);
		List<CusContactInfos> l = query(sql.toString(), CusContactInfos.class);
		return l;
	}
	/**
	 * 修改单个联系人信息
	 * @param id，cus_contact_infos表，自增长唯一标识
	 * @param contact，姓名
	 * @param position，职位
	 * @param cell，手机
	 * @return
	 */
	public int editOneCusContactInfos(Integer id,String contact,String position,String cell){
		StringBuffer sql=new StringBuffer("update  cus_contact_infos set contact='");
		sql.append(contact).append("' , position ='").append(position).append("' , cell='");
		sql.append(cell).append("' where id=").append(id);
		return update(sql.toString());
		
	}
	/**
	 * 单个联系人信息添加	
	 * @param iiUid，用户ID
	 * @param iContact，姓名
	 * @param iPosition，职位
	 * @param iCell，手机
	 * @return
	 */
	public int  addOneCusContactInfos(String iiUid,String iContact,String iPosition,String iCell){
		StringBuffer sql=new StringBuffer("insert into cus_contact_infos (uid,contact,position,cell) values('");
		sql.append(iiUid).append("','").append(iContact).append("','").append(iPosition).append("','");
		sql.append(iCell).append("')");
		return update(sql.toString());
	}
	/**
	 * 添加客户信息（未完成编码，只实现了单个添加，没有实现多个添加）
	 * @param cid，客户ID
	 * @param ctype，类型，0-企业，1-个人
	 * @param tradeType，所属行业
	 * @param cname，名称
	 * @param bkName，开户银行名称
	 * @param gate，开户银行gate
	 * @param provId，开户省份
	 * @param accNo，开户银行账号
	 * @param contact，姓名
	 * @param position，职位
	 * @param cell，手机
	 * @throws Exception
	 */
	public void  addKeHu(String cid,Integer ctype,Integer tradeType,String cname,
			String bkName,Integer gate,Integer provId,String accNo)throws Exception{
		StringBuffer sql1=new StringBuffer("insert into cus_infos (cid,ctype,trade_type,cname) values('");
		sql1.append(Ryt.sql(cid)).append("',").append(ctype).append(",").append(tradeType).append(",'");
		sql1.append(Ryt.sql(cname)).append("')");
		
		StringBuffer sql2=new StringBuffer("insert into ass_cus (master_id,cus_type,cus_id) values('");
		sql2.append(getLoginUserMid()).append("',").append(ctype).append(",'").append(Ryt.sql(cid)).append("')");
		
//		StringBuffer sql3=new StringBuffer("insert into cus_contact_infos (uid,contact,position,cell) values('");
//		sql3.append(cid).append("','").append(contact).append("','").append(position).append("','");
//		sql3.append(cell).append("')");
		
		String bkNo=getJdbcTemplate().queryForObject("select bf_bk_no from gate_route where gid="+gate,String.class);
		StringBuffer sql4=new StringBuffer("insert into user_bk_acc (uid,bk_no,acc_name,bk_name,gate,prov_id,acc_no) values('");
		sql4.append(Ryt.sql(cid)).append("','").append(Ryt.sql(bkNo)).append("','").append(Ryt.sql(cname)).append("','").append(Ryt.sql(bkName));
		sql4.append("',").append(gate).append(",").append(provId).append(",'").append(Ryt.sql(accNo)).append("')");
		List<String>  sqlsList = new ArrayList<String>();// 批处理
		sqlsList.add(sql1.toString());
		sqlsList.add(sql2.toString());
		//sqlsList.add(sql3.toString());
		sqlsList.add(sql4.toString());

		batchSqlTransaction2(sqlsList);
		
		sqlsList.clear();
		
	}
	
	 				
	public TrOrders queryTrOrderByOid(String oid) {
		return queryForObject("select * from tr_orders where oid = ? ",new Object[]{oid},TrOrders.class);
	}
	/**
	 * 交易成功的操作（回调）
	 * @param ord
	 * @param tseq
	 */
	public void accChongZhi(TrOrders ord, String tseq) {
		int today=DateUtil.today();
		int currtime=DateUtil.getCurrentUTCSeconds();
		String trOrdersSql = "update tr_orders set state=2,sys_date="+today+",sys_time="+currtime+" where oid = "+Ryt.addQuotes(ord.getOid());
	//		String accSeqsSql  =   genAddAccSeqSql(ord.getUid(), ord.getAid(),ord.getTransAmt(),ord.getTransFee(),(ord.getTransAmt()-ord.getTransFee()),0, "tr_orders",ord.getOid(), "账户充值");		
		String tlogSql = "update tlog set batch=1 where tseq="+Ryt.sql(tseq);//设置batch=1不进结算
		List<String> sqlList=genAddAccSeqSqls(ord.getUid(), ord.getAid(),ord.getTransAmt(),ord.getTransFee(),(ord.getTransAmt()-ord.getTransFee()),0, "tr_orders",ord.getOid(), "账户充值");
		sqlList.add(trOrdersSql);
		sqlList.add(tlogSql);
		String[] strArr=sqlList.toArray(new String[sqlList.size()]);
		batchSqlTransaction(strArr);
//		genAddAccSeqSql(ord.getUid(), ord.getAid(),ord.getTransAmt(),ord.getTransFee(),(ord.getTransAmt()-ord.getTransFee()),0, "tr_orders",ord.getOid(), "账户充值");
	}
	/**
	 * 交易失败时的操作（回调）
	 * @param oid
	 */
	public void doUpdateOrderFail(String oid) {
		int today=DateUtil.today();
		int currtime=DateUtil.getCurrentUTCSeconds();
		String trOrdersSql = "update tr_orders set state=3,sys_date="+today+",sys_time="+currtime+" where oid = "+Ryt.addQuotes(oid);
		update(trOrdersSql);
	}		
	/**
	 * 查询待支付订单
	 * @param pageNo
	 * @return
	 */
	 public CurrentPage<TrOrders> queryWaitPay(Integer pageNo) {
		    LoginUser u = getLoginUser();
		    StringBuffer sql = new StringBuffer("select * from tr_orders  where state=0 and ptype=3");
			sql.append(" and uid= ").append(u.getMid());
			sql.append(" order by sys_date desc ,sys_time desc");
			String sqlCount =	sql.toString().replace("*","count(*)");
			return queryForPage(sqlCount,sql.toString(), pageNo, new AppParam().getPageSize(),TrOrders.class) ;
		}
	 /**
	  * 确定支付时，产生交易明细表
	  * @param aid，账户ID
	  * @param mid，用户ID
	  * @param toAid，交易对方账户ID
	  * @param toUid，交易对方用户ID
	  * @param payAmt，结算金额
	  * @throws Exception
	  */
	 public List genTrOrders(String aid,String mid,String toAid,String toUid,String payAmt) throws Exception{
		    List a=new ArrayList();
		    long pay_amt=Ryt.mul100toInt(payAmt);
		    int date = DateUtil.today();
			long initTime = DateUtil.getIntDateTime();
			int time= DateUtil.getCurrentUTCSeconds();
			String oid = Ryt.genOidBySysTime();
			String oid2 = Ryt.genOidBySysTime();
			//if(oid.equals(oid2)) oid2 = oid + "0";
			String aname1= queryForString("select aname from acc_infos where aid ='"+Ryt.sql(aid)+"'");
			String aname2= queryForString("select aname from acc_infos where aid ='"+Ryt.sql(toAid)+"'");
			
		    String sqlTr1=genAddTrOrdersSql(oid, mid, aid, 3,oid2,aname1,initTime,date,time,toUid,toAid, pay_amt, 0);
			String sqlTr2=genAddTrOrdersSql(oid2, toUid, toAid, 4,oid,aname2, initTime, date,time, mid.toString(),aid, pay_amt, 0);
			List<String>  sqlsList = new ArrayList<String>();// 批处理
			sqlsList.add(sqlTr1);
			sqlsList.add(sqlTr2);
			batchSqlTransaction2(sqlsList);
			sqlsList.clear();
			a.add(oid);
			a.add(oid2);
			return a;
	 }
	 /**
		 * 付款
		 * @param oid，系统订单号
		 * @param oid2，对方系统订单号
		 * @param state，状态，4-订单取消
		 * @return
		 */
		 public void pay4One(String oid)throws Exception{
			 	if(null==oid||oid.trim().length()==0) return;
			 	TrOrders a = queryForObject("select * from tr_orders where state=0 and oid='"+Ryt.sql(oid)+"'", TrOrders.class);
			 	if(null==a) return;
			 	String sqlTr1 = updateTrOrdersStateSql(oid,2,"交易成功");
				String sqlTr2 = updateTrOrdersStateSql(a.getOrgOid(),2,"交易成功");
				//String sqlAccSeqs1 = genAddAccSeqSql(a.getUid(),a.getAid(),a.getPayAmt(),0,a.getPayAmt(),1,"tr_orders",oid,"付款到融易付账户");
		    	//String sqlAccSeqs2 = genAddAccSeqSql(a.getToUid(),a.getToAid(),a.getPayAmt(),0,a.getPayAmt(),0,"tr_orders",a.getOrgOid(),"从融易付账户收款");
				List<String> fkSql=genAddAccSeqSqls(a.getUid(),a.getAid(),a.getPayAmt(),0,a.getPayAmt(),1,"tr_orders",oid,"付款到融易付账户");
				List<String> skSql=genAddAccSeqSqls(a.getToUid(),a.getToAid(),a.getPayAmt(),0,a.getPayAmt(),0,"tr_orders",a.getOrgOid(),"从融易付账户收款");
				List<String> sqlList=new ArrayList<String>();
				sqlList.addAll(skSql);
				sqlList.addAll(fkSql);
				String[] sqlsList = sqlList.toArray(new String[sqlList.size()]);
				batchSqlTransaction(sqlsList);
				
				 			}
	/**
	 * 取消订单 
	 * @param oid，系统订单号
	 * @param oid2，对方系统订单号
	 * @param state，状态，4-订单取消
	 * @return
	 */
	 public int[] cancel4One(String oid,String oid2){
		 	String sqlTr1=updateTrOrdersStateSql(oid,4,"订单取消");
			String sqlTr2=updateTrOrdersStateSql(oid2,4,"订单取消");
			return batchSqlTransaction(new String[]{sqlTr1,sqlTr2} );
			
		}
	 /**
		 * 查询商户的客户企业名称
		 * @return
		 * @throws Exception
		 */
		public Map<String,String> queryMerCustomerCom() throws Exception{
			String masterId=getLoginUserMid();
			String sql="select cid,cname from ass_cus as a ,cus_infos as c where a.cus_id=c.cid and a.cus_type=0 and a.master_id="+masterId;
			return queryToMap(sql);
		} 
		/**
		 * 根据客户企业查询账号
		 * @param uid
		 * @return
		 */
		public Map<String, String> queryBkAccByCusId(String uid){
			String sql="select acc_no,bk_name from user_bk_acc where uid="+Ryt.addQuotes(uid);
			Map<String, String> bkAccMap=  queryToMap(sql);
			Map<String,String> accNameMap=new HashMap<String,String>();
			for (String bkAcc:bkAccMap.keySet()) {
				accNameMap.put(bkAcc, bkAccMap.get(bkAcc)+bkAcc);
			}
			return accNameMap;
		}
		/**
		 * 根据  用户uid+开户银行账号acc_no 查询 UserBkAcc
		 * @param cusId
		 * @param accNo
		 * @return
		 */
		public UserBkAcc getUserBkAccObj(String cusId,String accNo){
			String sql="select * from  user_bk_acc where uid =? and acc_no=?";
			return queryForObject(sql, new Object[]{cusId,accNo}, UserBkAcc.class);
		}
		public int updateTrOrder(String oid){
			int today=DateUtil.today();
			int currSecond=DateUtil.getCurrentUTCSeconds();
			String sql="update tr_orders set state=11,sys_date=?,sys_time=?,oper_date=? where oid=?";
			return update(sql,new Object[]{today,currSecond,today,oid});
		}
		/**
		 * 查询手续费计算公式
		 * @param aid
		 * @param FeeModeName
		 * @return
		 */
		public Map<String, Object> getFeeModeAid(String aid,String FeeModeName){
			String sql="select uid,aname,"+Ryt.sql(FeeModeName)+" from acc_infos where aid=?";
			return queryForMap(sql,new Object[]{aid});
		}

		/**
		 * 查询网关号，在商户手续费计算公式表
		 * @return
		 */
		public List<Integer> getB2bPayGates(){
			LoginUser loginUser = getLoginUser();
			if(loginUser==null) return null;
			String sql="select gate from fee_calc_mode where mid="+loginUser.getMid()
			+" and trans_mode=0 and state=1 and gate>=20000 and gate<30000";
			return queryForIntegerList(sql);
		}

		public long queryMonthDaiFuSumAmt(String aid, String month) {
			StringBuffer sql = new StringBuffer("select sum(trans_amt) from tr_orders ");
			sql.append(" where aid = '").append(Ryt.sql(aid)).append("'");
			sql.append(" and ptype = 5 ");
			sql.append(" and state= 2 ");
			sql.append(" and (sys_date > ").append(month).append("00");
			sql.append(" and sys_date <").append(month).append("32)");
			return queryForLong(sql.toString());
		}
		
		
		
		
		
		
		/**
		 * 添加一条，单笔付款到个人银行账户到明细表
		 * @param oid
		 * @param aid
		 * @param toAccNo
		 * @param toAccName
		 * @param bkNo
		 * @param transAmt
		 * @param remark 
		 * @return
		 */
		public int addSinglePay(String aname,int transFee,long transAmt ,String oid,String aid,String toAccNo,String toAccName,String toBkName,String toBkNo,Integer toProvId,Integer cardFlag,long payAmt,int state,String priv,String accNo,String accName,int gate){
			LoginUser loginUser = getLoginUser();
			
			int date = DateUtil.today();
			long initTime = DateUtil.getIntDateTime();
			int time= DateUtil.getCurrentUTCSeconds();
		
			StringBuffer sql=new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,");
			sql.append("sys_time,ptype,trans_amt,trans_fee,pay_amt,to_acc_no,to_acc_name,to_bk_name,to_bk_no,to_prov_id,card_flag,state,priv,acc_no,acc_name,gate) values (");
			sql.append("'").append(oid).append("',");
			sql.append("'").append(loginUser.getMid().toString()).append("',");
			sql.append("'").append(Ryt.sql(aid)).append("',");
			sql.append("'").append(Ryt.sql(aname)).append("',");
			sql.append(initTime).append(",");
			sql.append(date).append(",");
			sql.append(time).append(",");
			sql.append("7").append(",");
			sql.append(transAmt).append(",");
			sql.append(transFee).append(",");
			sql.append(payAmt).append(",");
			sql.append("'").append(Ryt.sql(toAccNo)).append("',");
			sql.append("'").append(Ryt.sql(toAccName)).append("',");
			sql.append("'").append(Ryt.sql(toBkName)).append("',");
			sql.append("'").append(Ryt.sql(toBkNo)).append("',");
			sql.append(toProvId).append(",");
			sql.append(cardFlag).append(",");
			sql.append(state).append(",");
			sql.append("'").append(Ryt.sql(priv)).append("',");
			sql.append("'"+accNo+"',").append("'"+accName+"',"+gate+")");
			return update(sql.toString());
		}
		
		public int addSinglePay(String trans_flow,String aname,int transFee,long transAmt ,String oid,String aid,String toAccNo,String toAccName,String toBkName,String toBkNo,Integer toProvId,Integer cardFlag,long payAmt,int state,String priv,String accNo,String accName){
			LoginUser loginUser = getLoginUser();
			
			int date = DateUtil.today();
			long initTime = DateUtil.getIntDateTime();
			int time= DateUtil.getCurrentUTCSeconds();
		
			StringBuffer sql=new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,");
			sql.append("sys_time,ptype,trans_amt,trans_fee,pay_amt,to_acc_no,to_acc_name,to_bk_name,to_bk_no,to_prov_id,card_flag,state,priv,trans_flow,acc_no,acc_name) values (");
			sql.append("'").append(oid).append("',");
			sql.append("'").append(loginUser.getMid().toString()).append("',");
			sql.append("'").append(Ryt.sql(aid)).append("',");
			sql.append("'").append(Ryt.sql(aname)).append("',");
			sql.append(initTime).append(",");
			sql.append(date).append(",");
			sql.append(time).append(",");
			sql.append("7").append(",");
			sql.append(transAmt).append(",");
			sql.append(transFee).append(",");
			sql.append(payAmt).append(",");
			sql.append("'").append(Ryt.sql(toAccNo)).append("',");
			sql.append("'").append(Ryt.sql(toAccName)).append("',");
			sql.append("'").append(Ryt.sql(toBkName)).append("',");
			sql.append("'").append(Ryt.sql(toBkNo)).append("',");
			sql.append(toProvId).append(",");
			sql.append(cardFlag).append(",");
			sql.append(state).append(",");
			sql.append("'").append(Ryt.sql(priv)).append("',");
			sql.append("'").append(Ryt.sql(trans_flow)).append("',");
			sql.append("'"+accNo+"',").append("'"+accName+"')");
			return update(sql.toString());
		}
		
		public String getAddSinglePaySql(String trans_flow,String aname,int transFee,long transAmt ,String oid,String aid,String toAccNo,String toAccName,String toBkName,String toBkNo,Integer toProvId,Integer cardFlag,long payAmt,int state,String priv,String accNo,String accName,int gate){
			LoginUser loginUser = getLoginUser();
			
			int date = DateUtil.today();
			long initTime = DateUtil.getIntDateTime();
			int time= DateUtil.getCurrentUTCSeconds();
		
			StringBuffer sql=new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,");
			sql.append("sys_time,ptype,trans_amt,trans_fee,pay_amt,to_acc_no,to_acc_name,to_bk_name,to_bk_no,to_prov_id,card_flag,state,priv,trans_flow,acc_no,acc_name,gate) values (");
			sql.append("'").append(oid).append("',");
			sql.append("'").append(loginUser.getMid().toString()).append("',");
			sql.append("'").append(Ryt.sql(aid)).append("',");
			sql.append("'").append(Ryt.sql(aname)).append("',");
			sql.append(initTime).append(",");
			sql.append(date).append(",");
			sql.append(time).append(",");
			sql.append("7").append(",");
			sql.append(transAmt).append(",");
			sql.append(transFee).append(",");
			sql.append(payAmt).append(",");
			sql.append("'").append(Ryt.sql(toAccNo)).append("',");
			sql.append("'").append(Ryt.sql(toAccName)).append("',");
			sql.append("'").append(Ryt.sql(toBkName)).append("',");
			sql.append("'").append(Ryt.sql(toBkNo)).append("',");
			sql.append(toProvId).append(",");
			sql.append(cardFlag).append(",");
			sql.append(state).append(",");
			sql.append("'").append(Ryt.sql(priv)).append("',");
			sql.append("'").append(Ryt.sql(trans_flow)).append("',");
			sql.append("'"+accNo+"',").append("'"+accName+"',"+gate+")");
			return sql.toString();
		}
		
		/**
		 * 确定支付一笔，单笔付款到个人银行账户。
		 * @param oid
		 */
		public void updateSinglePay(String oid,String aid,String payAmt){
			String sql1=updateTrOrdersStateSql(oid,33);
//			int transAmt=getTransAmt(aid, Ryt.div100(payAmt));
			String sql2 = "update acc_infos set balance = balance - " +  Long.parseLong(payAmt) +",freeze_amt=freeze_amt +"+Long.parseLong(payAmt)+" where aid=uid and aid = '" +Ryt.sql(aid)+ "'";
			batchSqlTransaction(new String[]{sql1,sql2} );
		}
		/**
		 * 进行代发手续费的计算后，返回交易金额
		 * @param aid
		 * @param transAmt
		 * @return
		 */
		public long getTransAmt(String aid,String payAmt){
			String daifaFeeMode=queryForString("select daifa_fee_mode from acc_infos where aid='"+Ryt.sql(aid)+"'");
			int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(daifaFeeMode,payAmt));
			long payAmt_=Ryt.mul100toInt(payAmt);
			int trans_amt=(int) (payAmt_+transFee);
			return trans_amt;
		}
		/**
		 * 账户充值（线下支付）
		 * 
		 * @param oid
		 *            订单号
		 * @param aid
		 *            账户ID
		 * @param aname
		 *            账户名称
		 * @param transAmt
		 *            应付金额
		 * @param transFee
		 *            手续费
		 * @param payAmt
		 *            充值金额
		 * @param priv
		 *            充值信息
		 * @return
		 * @throws Exception
		 */
		public int[] payForAccount(String oid, String aid, String aname,
				long transAmt, Integer transFee, long payAmt, String priv)
				throws Exception {
			String mid = getLoginUserMid();
//			Object[] objArr = new Object[] { oid, mid, aid, aname,
//					DateUtil.getIntDateTime(), 0, transAmt, transFee, payAmt, 31,
//					"账户充值", priv, DateUtil.today(), 1 ,DateUtil.getTime().replaceAll(":", "")};
//			String sql = "insert into tr_orders(oid,uid,aid,aname,init_time,ptype,trans_amt,trans_fee,pay_amt,state,remark,
//			priv,sys_date,offline_flag,sys_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			return update(sql, objArr);
			Map<String, String> hlogMap=new HashMap<String, String>();
			Map<String, String> trOrdersMap =new HashMap<String, String>();
			trOrdersMap.put("oid", Ryt.addQuotes(oid));
			trOrdersMap.put("uid", Ryt.addQuotes(mid));
			trOrdersMap.put("aid", Ryt.addQuotes(mid));
			trOrdersMap.put("aname", Ryt.addQuotes(aname));
			trOrdersMap.put("init_time", DateUtil.getIntDateTime()+"");
			trOrdersMap.put("ptype", "0");
			trOrdersMap.put("trans_amt", transAmt+"");
			trOrdersMap.put("trans_fee", transFee+"");
			trOrdersMap.put("pay_amt", payAmt+"");
			trOrdersMap.put("state", "31");
			trOrdersMap.put("remark", Ryt.addQuotes("账户充值"));
			trOrdersMap.put("priv", Ryt.addQuotes(priv));
			trOrdersMap.put("sys_date", DateUtil.today()+"");
			trOrdersMap.put("offline_flag", "1");
			trOrdersMap.put("gate", "0");
			trOrdersMap.put("sys_time",DateUtil.getTime().replaceAll(":", ""));
			hlogMap.put("tseq", Ryt.genOidBySysTime());
			hlogMap.put("version", "10");
			hlogMap.put("ip", "3232251650");
			hlogMap.put("mdate", DateUtil.today()+"");
			hlogMap.put("oid", Ryt.addQuotes(oid));
			hlogMap.put("mid", Ryt.addQuotes(mid));
			hlogMap.put("amount", payAmt+"");
			hlogMap.put("type", String.valueOf(Constant.TlogTransType.OFFLINE_RECHARGE));
			hlogMap.put("sys_date", DateUtil.today()+"");
			hlogMap.put("init_sys_date", DateUtil.today()+"");
			hlogMap.put("sys_time", DateUtil.getCurrentUTCSeconds()+"");
			hlogMap.put("tstat", "1");
			hlogMap.put("gate", "0");
			hlogMap.put("gid", "0");
			hlogMap.put("pay_amt", transAmt+"");
			String []sqls=RecordLiveAccount.getInsetSqls(hlogMap, trOrdersMap);
			return batchSqlTransaction(sqls);
		}

		/**
		 * 检查订单状态是否正常
		 * 
		 * @param oid
		 *            订单号
		 * @return
		 */
		public int stateNomal(String oid) {
			StringBuffer sql = new StringBuffer(
					"select state from tr_orders where oid='");
			sql.append(oid+"'");
			return queryForInt(sql.toString());
		}
		/**
		 * 检查订单状态是否正常(账户充值检查)
		 * 
		 * @param oid
		 *            订单号
		 * @return
		 */
		public int stateNomals(String oid) {
			StringBuffer sql = new StringBuffer(
					"select count(*) from tr_orders where oid='");
			sql.append(oid+"'");
			return queryForInt(sql.toString());
		}
		/**
		 * 检查B2B批量付款订单状态是否正常
		 * 
		 * @param batchNo
		 *            订单号
		 * @return 没付款记录条数 >0则表示可以付款
		 */
		public int batchStateNomal(String batchNo) {
			StringBuffer sql = new StringBuffer(
					"select count(*) from tr_orders where  trans_flow ='");
			sql.append(batchNo+"'");
			return queryForInt(sql.toString());
		}
		/**
		 * B2B批量余额支付
		 * 
		 * @param oid
		 *            订单号
		 * @param transAmt
		 *            应付金额
		 * @param uid
		 * @param aid
		 * @return
		 */
		public int[] batchPayForBalance(String oid, double transAmt, String uid,
				String aid, String[] sqls) {
			String balanceSql=RecordLiveAccount.calUsableBalance(uid, uid, Long.parseLong(Ryt.mul100(transAmt+"")), 1);//减少用户余额
			sqls[sqls.length - 1] = balanceSql.toString();
			return batchSqlTransaction(sqls);
		}
		/**
		 * 线下支付 修改订单状态为处理中，订单线下标识符1
		 * 
		 * @param oderid
		 *            订单号
		 * @return
		 */
		public int payForOffline(String oderid,String fee) {
			StringBuffer sql = new StringBuffer(
					"update tr_orders set trans_fee="+fee+",trans_amt=pay_amt+"+fee+",state=31,offline_flag=1 where oid='");
			sql.append(oderid);
			sql.append("' ");
			return update(sql.toString());
		}
		/**
		 * 余额支付
		 * 
		 * @param oid
		 *            订单号
		 * @param transAmt
		 *            应付金额
		 * @param uid
		 * @return
		 */
		public int[] payForBalance(String oid, String transAmt, String uid) {
			String[] sqls = new String[2];
			StringBuffer balanceSql = new StringBuffer(
					"update acc_infos set balance=balance-");// 减少余额
			StringBuffer oidSql = new StringBuffer(
					"update tr_orders set state=33 where oid=");// 更新订单状态
			oidSql.append(Ryt.addQuotes(oid));
			oidSql.append(" and state!=1;");
			balanceSql.append(Ryt.mul100(transAmt + ""));
			balanceSql.append(",freeze_amt=freeze_amt+"+Ryt.mul100(transAmt + ""));//增加冻结金额
			balanceSql.append(" where uid=");
			balanceSql.append(Ryt.addQuotes(uid));
			balanceSql.append(" and aid=uid;");
			sqls[0] = oidSql.toString();
			sqls[1] = balanceSql.toString();
			return batchSqlTransaction(sqls);
		}
		/**
		 * 线下支付批量 修改订单状态为处理中，订单、线下标识符1
		 * 
		 * @param batchNo
		 *            批次号
		 * @return
		 */
		public int[] batchPayForOffline(String batchNo, String[] sqls,BatchB2B batchB2B,String uid,String oid,AccInfos accInfos,String priv) {
			StringBuffer sql=new StringBuffer("insert into tr_orders(oid,uid,aid,aname,init_time,ptype,trans_amt,trans_fee,pay_amt,state,remark,offline_flag,trans_flow,sys_date,sys_time,priv,tseq ) values('");
			sql.append(oid+"','").append(accInfos.getUid()+"','").append(uid+"','").append(accInfos.getAname()+"',");
			sql.append(DateUtil.getIntDateTime()+",").append(0).append(",").append(Ryt.mul100(batchB2B.getAllrAmt()+"")+",");
			sql.append(Ryt.mul100(batchB2B.getFeeAmt()+"")+",").append(Ryt.mul100(batchB2B.getOrderAmt()+"")+",");
			sql.append(0).append(",'"+batchB2B.getOrderDescribe()+"").append("',").append(1+",'").append(batchNo).append("',").append(DateUtil.today()+",").append(DateUtil.getCurrentUTCSeconds()+",'");
			sql.append(""+priv+"'").append(", '");
			sql.append(Ryt.genOidBySysTime()+"')");
			String []addSqls=new String[sqls.length+1];
			System.arraycopy(sqls, 0, addSqls, 0, sqls.length);
			addSqls[sqls.length]=sql.toString();
			return batchSqlTransaction(addSqls);
		}

		/**
		 * 通过商户查找出支付的账户名
		 * 
		 * @param uid
		 *            商户ID
		 * @return
		 */
		public String queryZHAID(String uid) {
			StringBuffer sql = new StringBuffer(
					"select aid  from acc_infos where uid=");
			sql.append(uid);
			sql.append(" and acc_type=1");
			return queryForString(sql.toString());
		}
		/**
		 * 单笔付款到企业银行
		 * @param trOrders
		 * @return
		 */
		public int updateOrder(TrOrders trOrders){
			StringBuffer sql=new StringBuffer("update tr_orders set to_uid='");
			sql.append(trOrders.getToUid()+"',").append("to_acc_no='").append(trOrders.getAccNo()+"',");
			sql.append("pay_amt=").append(trOrders.getPayAmt()).append(",");
			sql.append("sys_time=").append(trOrders.getSysTime()).append(",init_time='");
			sql.append(trOrders.getInitTime()).append("',").append("gate=");
			sql.append(trOrders.getGate()).append(",trans_amt=").append(trOrders.getTransAmt());
			sql.append(",trans_fee=").append(trOrders.getTransFee()).append(",pay_amt=");
			sql.append(trOrders.getPayAmt()).append(",to_acc_name='").append(trOrders.getToAccName());
			sql.append("',to_acc_no='").append(trOrders.getToAccNo()).append("',");
			sql.append("to_bk_name='").append(trOrders.getToBkName()).append("',to_bk_no='");
			sql.append(trOrders.getToBkNo()).append("',priv='").append(trOrders.getPriv()).append("',");
			sql.append("sms_mobiles='").append(trOrders.getSmsMobiles()+"'").append(", to_prov_id=").append(trOrders.getToProvId()).append(",gate=").append(trOrders.getGate()).append(",to_bk_name='").append(trOrders.getToBkName()+"'").append(" where oid=").append(Ryt.addQuotes(trOrders.getOid())+"");
			return update(sql.toString());
		}
		/**
		 * 查询指定商户的结算账户(正常状态)
		 * @param uid 商户id
		 * @return
		 */
		public String getSettlementAcc(String uid){
			StringBuffer sql=new StringBuffer("select concat(uid,'[',aname,']') from acc_infos where aid=uid and state =1 ");
			sql.append(" and uid=").append(Ryt.addQuotes(uid));
			return queryForString(sql.toString());
		}
		
	/**
	 * 根据商户号查询交易账户	
	 * @param uid
	 * @return
	 */
	public String getTransAcc(String uid) {
		String sql = "select concat(aid,'[',aname,']') from acc_infos where uid='" + uid + "' and acc_type=1 and state=1";
		return queryForString(sql);
	}
		
		/**
		 * 根据订单号查询订单信息
		 * @param oid
		 * @return
		 */
		public  List<OrderInfos> getOrderInfoByOid(String oid,String name,String last)throws Exception{
			StringBuffer sql=new StringBuffer("select t.uid,m.name,t.pay_amt,t.trans_fee,t.trans_amt,t.tseq,t.remark,t.priv from tr_orders t left join minfo m on t.uid=m.id where t.uid=");
			sql.append(getLoginUserMid()).append(" and t."+name+"='").append(oid).append("' "+last+"");
			return getJdbcTemplate().query(sql.toString(),new BeanPropertyRowMapper<OrderInfos>(OrderInfos.class));
		}
		//查询的是结算帐户 yang.yaofeng
		 public AccInfos queryFKUID(String uid){
			  String sql="select * from acc_infos where uid ="+Ryt.addQuotes(Ryt.sql(uid))+" and aid=uid";
			  return queryForObject(sql, AccInfos.class) ;
		  }
		 	/**
			 * 查询结算账户信息
			 * @param mid 商户号（用户ID）
			 * @return
			 */
			public List<AccInfos> queryJSZHYE(String mid){
				StringBuffer sql=new StringBuffer("select * from acc_infos where uid=aid and uid=");
				sql.append(Ryt.addQuotes(Ryt.sql(mid)));
				return query(sql.toString(),AccInfos.class);
			}
			/**
			 * 查询交易账户信息
			 * @param mid 商户号（用户ID）
			 * @return
			 */
			public List<AccInfos> queryJYZHYE(String mid){
				StringBuffer sql=new StringBuffer("select * from acc_infos where uid ='");
				sql.append(Ryt.sql(mid+"")).append("' and acc_type=1");
				return query(sql.toString(),AccInfos.class);
			}
			 /**
			  * 查询融易通中国银行帐号
			  * @return
			  */
			 public String queryRYTBankNo(){
				 String sql="select concat(acc_no,'(',name,')') from b2e_gate where  gid=40001";
				 return queryForString(sql);
			 }
			 /**
			   * 判断该商户交易账户是否为开启状态
			   * @param mid 商户号
			   * @return
			   */
			 public boolean merTransAccIsNomal(String mid){
				  return transAccIsNomals(mid);
			  }
				/**
				 * 查询账户信息
				 * @param mid 商户号（用户ID）
				 * @return
				 */
				public List<AccInfos> checkJYZH(String mid){
					String sql="select * from acc_infos where uid ='"+mid+"'"+"and acc_type=1";
					return query(sql,AccInfos.class);
				}

	public int addSinglePay_N(String aname, int transFee, long transAmt,
			String oid, String uid, String toAccNo, String toAccName,
			String toBkName, String toBkNo, Integer toProvId, Integer cardFlag,
			long payAmt, int state, String priv, String accNo, String accName,
			int gid, String orgOid, String aid, short pstate) {
		// LoginUser loginUser = getLoginUser();

		int date = DateUtil.today();
		long initTime = DateUtil.getIntDateTime();
		int time = DateUtil.getCurrentUTCSeconds();

		StringBuffer sql = new StringBuffer("insert into tr_orders (oid,uid,aid,aname,init_time,sys_date,");
		sql.append("sys_time,ptype,trans_amt,trans_fee,pay_amt,to_acc_no,to_acc_name,to_bk_name,to_bk_no,to_prov_id,card_flag,state,priv,acc_no,acc_name,gate,org_oid,pstate) values (");
		sql.append("'").append(Ryt.sql(oid)).append("',");
		sql.append("'").append(Ryt.sql(uid)).append("',");
		sql.append("'").append(Ryt.sql(aid)).append("',");
		sql.append("'").append(Ryt.sql(aname)).append("',");
		sql.append(initTime).append(",");
		sql.append(date).append(",");
		sql.append(time).append(",");
		sql.append("7").append(",");
		sql.append(transAmt).append(",");
		sql.append(transFee).append(",");
		sql.append(payAmt).append(",");
		sql.append("'").append(Ryt.sql(toAccNo)).append("',");
		sql.append("'").append(Ryt.sql(toAccName)).append("',");
		sql.append("'").append(Ryt.sql(toBkName)).append("',");
		sql.append("'").append(Ryt.sql(toBkNo)).append("',");
		sql.append(toProvId).append(",");
		sql.append(cardFlag).append(",");
		sql.append(state).append(",");
		sql.append("'").append(Ryt.sql(priv)).append("',");
		sql.append("'" + accNo + "',").append("'" + accName + "',");
		sql.append(gid).append(",").append(Ryt.addQuotes(orgOid) + ",");
		sql.append(pstate).append(" )");
		return update(sql.toString());
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
	
	public CurrentPage<TrOrders> queryDFQRinfo(Integer pageNo,Integer pageNum,String batchNo,String tseq,Integer type,Integer dateType,Integer state,Integer bdate,Integer edate){
		if(pageNum==0)
			pageNum=50;
		LoginUser u = getLoginUser();
		String mid=u.getMid();
		StringBuffer sql=new StringBuffer(" from tr_orders t,hlog h where t.oid=h.oid and t.uid=h.mid and t.aid=t.uid and t.uid=");
		sql.append(Ryt.addQuotes(mid));
		if(type==0)
			sql.append(" and (t.ptype=5 or t.ptype =7) ");
		else
			sql.append(" and t.ptype=").append(type);
		if(!Ryt.empty(batchNo))
			sql.append(" and t.trans_flow = ").append(Ryt.addQuotes(batchNo));
		if(!Ryt.empty(tseq))
			sql.append(" and h.tseq=").append(Ryt.addQuotes(tseq));
		//申请代付日期
		if(dateType==0){
			sql.append(" and t.sys_date >= ").append(bdate);
			sql.append(" and t.sys_date <= ").append(edate);
		}
		//确认代付日期、撤销代付日期
		else if(dateType==1 || dateType==2){
			sql.append(" and h.p4 >= ").append(bdate);
			sql.append(" and h.p4 <= ").append(edate);
		}
		
		if(state==2)
			sql.append(" and h.tstat = ").append(1);
		else if(state==3)
			sql.append(" and h.tstat = ").append(2);
		else if(state==4)
			sql.append(" and h.tstat = ").append(3);
		else if(state==5)
			sql.append(" and h.tstat = ").append(5);
		else{
			sql.append(" and h.tstat = ").append(0);/***默认查询待确认数据***/
			sql.append(" and t.is_pay = ").append(0);//追加查询条件 is_pay=0 :查询tr_orders is_pay=0(未发起的交易)
		}
		
		
		sql.append(" order by t.sys_date desc ,t.sys_time desc");
		String sqlCount = " SELECT  COUNT(h.tseq) " + sql.toString();
		// 交易总金额为[0],系统手续费总金额[1]
		String amtSumSql = " SELECT sum(h.amount)  " + sql.toString();
		String sysAtmFeeSumSql = " SELECT sum(h.fee_amt)" + sql.toString();
		Map<String,String> sumSQLMap=new HashMap<String,String>();
		sumSQLMap.put(AppParam.AMT_SUM, amtSumSql);
		sumSQLMap.put(AppParam.SYS_AMT_FEE_SUM, sysAtmFeeSumSql);
		StringBuffer allSql=new StringBuffer("select * ");
		allSql.append(sql.toString());
		return queryForPage(sqlCount,allSql.toString(), pageNo,pageNum,TrOrders.class,sumSQLMap) ;
//		return queryForCurrPage(sql.toString(), pageNo, TrOrders.class);
	}

}