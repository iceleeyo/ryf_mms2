package com.rongyifu.mms.dao;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import net.rypay.util.page.PaginationHelper;

//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;

import com.rongyifu.mms.bean.*;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("unchecked")
public class RiskControlDao extends PubDao{
	
	/**
	 * @param month
	 * @return
	 * 批量导入白名单
	 */
	public int batchAddWhiteList(int month){
//		1、卡号 2、身份证号 3、手机号
		StringBuilder sql = new StringBuilder("select mobile_no field,3 fieldType from mlog where trans_type=3 and tstat = 2 and date_format(sys_date,'%Y%m')=").append(month);
		sql.append(" and mobile_no <> '' and mobile_no is not null");
		sql.append(" UNION ALL ");
		sql.append("select pay_card field,1 fieldType from mlog where trans_type=3 and tstat = 2 and date_format(sys_date,'%Y%m')=").append(month);
		sql.append(" and pay_card <> '' and pay_card is not null");
		sql.append(" UNION ALL ");
		sql.append("select pay_id field,2 fieldType from mlog where trans_type=3 and tstat = 2 and date_format(sys_date,'%Y%m')=").append(month);
		sql.append(" and pay_id <> '' and pay_id is not null");
		List<Mlog> list = query(sql.toString(), Mlog.class);
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		StringBuilder insertSql = new StringBuilder("INSERT IGNORE card_auth (field,field_type,date,auth_type) values ");
		int size = list.size();
		for (int i = 0; i<size; i++) {
			Mlog item = list.get(i);
			if(i==0){
				insertSql.append("(");
			}else{
				insertSql.append(",(");
			}
			insertSql.append(Ryt.addQuotes(Ryt.desDec(item.getField(0)))).append(",").append(item.getFieldType());
			insertSql.append(",").append(date).append(",").append(1);//1白名单
			insertSql.append(")");
		}
		return update(insertSql.toString());
	}
	
	/**
	 * @param list
	 * @param authType
	 * @return
	 * 批量导入黑名单
	 */
	public int batchAddBlackList(List<CardAuth> list){
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		StringBuilder sql = new StringBuilder("INSERT IGNORE card_auth (field,field_type,date,auth_type) values ");
		int size = list.size();
		for (int i = 0; i<size; i++) {
			CardAuth item = list.get(i);
			if(i==0){
				sql.append("(");
			}else{
				sql.append(",(");
			}
			sql.append(Ryt.addQuotes(Ryt.desEnc(item.getField(0)))).append(",").append(item.getFieldType());
			sql.append(",").append(date).append(",").append(0);
			sql.append(")");
			
		}
		return update(sql.toString());
	}

	public int addRiskLog(RiskLog log) throws Exception {
		 return saveObject(log);
	}
	
	public int checkAuthtype(String field, String field_type) {
		String checksql = "select auth_type from card_auth where (field=? or field=? ) and field_type=?";
		return  queryForInt(checksql, new Object[] { field,Ryt.desEnc(field), field_type }, new int[] { Types.VARCHAR,Types.VARCHAR, Types.TINYINT });
	}
	
	
	
	public int checkAuthtype(String field) {
		String checksql = "select auth_type from card_auth where field=? or field=? LIMIT 1";
		return  queryForInt(checksql, new Object[] {field,Ryt.desEnc(field)}, new int[] { Types.VARCHAR,Types.VARCHAR});
	}

	public void addList(String field, int field_type, int auth_type) {
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		String sql = "insert into card_auth (field,field_type,date,auth_type) values ('" + Ryt.sql(Ryt.desEnc(field)) + "'," +field_type
						+ "," + Ryt.sql(date)+ "," +auth_type+")";
		 update(sql);
	}
	
	
	public void addList(String field,String auth_type) {
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		String sql = "insert into card_auth (field,date,auth_type) values ('" + Ryt.sql(Ryt.desEnc(field)) + "'," + date + "," + Ryt.sql(auth_type) + ")";
		update(sql);
	}

	public void deleteList(Integer id) {
		String sql = "delete from card_auth where id=" + id;
		update(sql);
	}

	public CurrentPage<CardAuth> getBlackWhiteList(int pageNo,int fieldType,String field, int date, String auth_type) {
		StringBuilder sqlBuff=new StringBuilder("select * from card_auth where 1=1 ");
		StringBuilder sqlCountRows = new StringBuilder("select count(*) from card_auth where 1=1");
		StringBuilder condition = new StringBuilder();
		if(fieldType!=0)condition.append(" and field_type = ").append(fieldType);
		if (!Ryt.empty(field)) condition.append(" and (field = '").append(Ryt.sql(field )).append("' or field='").append(Ryt.desEnc(field)).append("')");
		if(date >0)condition.append(" and date =").append(date);
		condition.append(" and auth_type = ").append(auth_type).append(" order by id DESC");
		return  queryForPage(sqlCountRows.append(condition).toString(), sqlBuff.append(condition).toString(), pageNo, CardAuth.class);
	}

	public ArrayList<Hlog> queryFailTransList() {
		String sql = "select tseq, mid,oid ,amount,sys_date,sys_time,gate,gid,error_code,author_type from tlog ";
		sql += " where tstat =3 order by tseq DESC ";
		return (ArrayList<Hlog>)  query(sql,Hlog.class);
	}

	public ArrayList<Hlog> queryLastSuccTransList(int count) {
		String sql = " select tseq, mid,oid ,amount,sys_date,sys_time from tlog where tstat = 2 ";
		sql += " order by tseq DESC limit  " + count;
		return (ArrayList<Hlog>)  query(sql, Hlog.class);
	}

	public String QueyXYKWar() {
		String v = null;
		try {
			v = queryForString("select par_value from global_params where par_name = 'WY_XYK_WAR'");
		} catch (Exception e) {
		}
		return v ;
	}

	public int querySuccesCount() {
		return  queryForInt(" select count(tseq) from tlog where tstat = 2 ");
	}

	public int cleanWar() {
		return  update("update global_params set par_value = '' where par_name = 'WY_XYK_WAR' ");
	}
	
	
	public int cleanWarCardPayTemp() {
		return  update("delete from card_pay_temp");
	}

	public Minfo queryMinfoForRisk(String mid) {
		List<Minfo> list = null;
		try {
			list =  query("select * from minfo where id = ? ",new Object[]{mid},new int[]{Types.VARCHAR}, Minfo.class);;
			return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String updateMinfoForRisk(String mid, int accSuccCount, int idSuccCount, int phoneSuccCount, int accFailCount,
					int idFailCount, int phoneFailCount) {
		
		Object[] o = new Object[]{accSuccCount,idSuccCount,phoneSuccCount,accFailCount,idFailCount,phoneFailCount,mid};
		int[] t = new int[]{Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.VARCHAR};
		String sql = "update minfo set acc_succ_count = ? ,id_succ_count = ? , phone_succ_count = ? ,acc_fail_count = ? ,id_fail_count = ? ,phone_fail_count = ? where id = ? ";
		try {
			 update(sql,o,t);;
			return "操作成功" ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "操作失败";
	}
	/**
	 * 可疑交易分页查询(新增)
	 * @param countSql
	 * @param sql
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<Mlog> searchMlogs(int pageNo,String transtate,  int tradetype, int begindate, int enddate, String otherId,
			int otherIdNum, boolean hasAmountNum, int amountNum) {
		String sql=getSearchMlogSql( transtate, tradetype,begindate, enddate, otherId, otherIdNum, hasAmountNum, amountNum);
		String countSql=sql.replace("*", "count(*)");	
		return queryForPage(countSql, sql.toString(), pageNo, new AppParam().getPageSize(), Mlog.class);
	}
	/**
	 * 所有的可疑交易查询
	 * @param countSql
	 * @param sql
	 * @param pageNo
	 * @return List<Mlog> 
	 */
	public List<Mlog> searchMlogList(String transtate, int tradetype,int  begindate, int enddate, String otherId,
	int otherIdNum, boolean hasAmountNum, int amountNum) {
		String sql=getSearchMlogSql(transtate,tradetype,begindate, enddate, otherId, otherIdNum, hasAmountNum, amountNum);
		return query(sql,Mlog.class);
	}
	/**
	 * 可疑交易的sql语句
	 * @param countSql
	 * @param sql
	 * @param pageNo
	 * @return List<Mlog> 
	 */
	private String getSearchMlogSql(String transtate,  int tradetype, int begindate, int enddate, String otherId,
			int otherIdNum, boolean hasAmountNum, int amountNum){
		String amountTrans = Ryt.mul100(amountNum);
		String pubCondition = "( tstat = " +  Ryt.sql(transtate) + ")  and sys_date >= "+begindate +" and sys_date <= " + enddate;
		if(tradetype!=0){
			pubCondition += " and trans_type ="+tradetype;
		}
		//单笔支付金额
		if(hasAmountNum){
			pubCondition += " and pay_amount >= " + amountTrans;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from mlog where ").append(pubCondition);
		if (otherId.equals("card")) {
			sql.append(" and pay_card in (select pay_card from mlog where ").append(pubCondition);
			sql.append(" group by pay_card  ");
			sql.append(" having count(tseq)>= ").append(otherIdNum).append(")");
		}else if(otherId.equals("id")){
			sql.append(" and pay_id in (select pay_id from mlog where ").append(pubCondition);
			sql.append(" group by pay_id  ");
			sql.append(" having count(tseq)>= ").append(otherIdNum).append(")");
		}else if(otherId.equals("phone")){
			sql.append(" and mobile_no in (select mobile_no from mlog where mobile_no !='' and ").append(pubCondition);
			sql.append(" group by mobile_no  ");
			sql.append(" having count(tseq)>= ").append(otherIdNum).append(")");
		}
		return sql.toString();
		
	}
	/**
	 *  查询可疑交易详情
	 * @param queryStr
	 * @param key
	 * @return
	 */
	public List<Hlog> queryALogForRisk(String queryStr,String key){
		key=Ryt.sql(key);
		if(Ryt.empty(queryStr)||Ryt.empty(key)){
			return null;
		}
		String sql = " select * from tlog ";
		Object[] param = null;
		if(queryStr.equals("tseq")){
			sql += " where tseq = ? ";
			param = new Object[]{key,key};
		}
		if(queryStr.equals("oid")){
			sql += " where oid = ? ";
			param = new Object[]{key,key};
		}
		if(queryStr.equals("bkseq")){
			sql += " where bk_seq1 = ? or bk_seq2 = ? ";
			param = new Object[]{key,key,key,key};
		}
		sql=sql + " union "  + sql.replaceAll("tlog", "hlog");
		return query(sql,param,Hlog.class);
	}
	/**
	 * 修改风险交易表
	 * @param tseq
	 * @param remarks
	 * @param amt
	 * @param state
	 * @return
	 */
	public int editRiskLog(int tseq,String remarks,int amt,int state){
		Object[] param = new Object[]{remarks,DateUtil.today(),amt,state,tseq};
		
		StringBuffer sql = new StringBuffer(" update risk_log ");
		if( state==1|| state==2){
			sql.append(" set verify_remarks = ? ,verify_date = ? ,risk_amount=?");
		}else{
			sql.append(" set confirm_remarks = ?,confirm_date = ?, risk_amount=?");
		}
		sql.append(",rstate=? where tseq=? ");
		return update(sql.toString(),param);
	}
	/**
	 * 风险交易撤销
	 * @param tseq
	 * @param remarks
	 * @return
	 */
	public int cancelRiskLog(int tseq,String remarks){
		Object[] param = new Object[]{remarks,DateUtil.today(),tseq};
		StringBuffer sql = new StringBuffer(" update risk_log ");
		sql.append(" set confirm_remarks = ?,confirm_date = ? ");
		sql.append(",rstate=4 where tseq=? ");
		return update(sql.toString(),param);
	}
	/**
	 * 删除RiskLog
	 * @param tseq
	 */
	public int removeRiskLog(String tseq){
		String sql="delete from risk_log where tseq =? ";
		return update(sql,new Object[]{tseq});
	}
	/**
	 * 查询风险交易
	 * @param rstate
	 * @param beginDate
	 * @param endDate
	 * @param tseq
	 * @return
	 */
	public List<RiskLog> queryRiskLog(String rstate,int beginDate,int endDate,String tseq){
		StringBuffer sql = new StringBuffer(" select * from risk_log where add_date >= ");
		sql.append(beginDate).append(" and add_date <= ").append(endDate);
		if(!Ryt.empty(rstate)){
			sql.append(" and ( rstate = ").append(Ryt.sql(rstate)).append(")");
		}
		if(!Ryt.empty(tseq)){
			sql.append(" and tseq = ").append(tseq);
		}
		return query(sql.toString(), RiskLog.class);
	}
}
