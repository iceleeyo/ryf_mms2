package com.rongyifu.mms.dao;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

//import net.rypay.util.page.PaginationHelper;

//import org.apache.poi.hssf.record.formula.functions.T;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("unchecked")
public class MerOperDao extends PubDao{

//	private JdbcTemplate jt = getJdbcTemplate();

	public List getOpers(int merid, String operName, int t) {
		String sql = "select * from  oper_info where ";
		if (merid > -1) {
			sql += " mid = " + merid + " and ";
		}
		if (operName != null && operName != "") {
			sql += " oper_name = '" + Ryt.sql(operName) + "' and ";
		}
		if (t > -1) {
			sql += " state = " + t + " and ";
		}
		sql += " mtype=0  order by mid,oper_id";
		return queryForList(sql);
	}

	// 当前页面的操作员
	public CurrentPage<OperInfo> getOpers4Object(String mid, String operName,
			int pageNo) {
		String sql = "";
		if (mid.trim()!=null) {
			sql += "mid='"+mid+"'"+" and";
		}
		if (operName != null && !operName.equals("")) {
			sql += " oper_name like '%" + Ryt.sql(operName) + "%'  and ";
		}

		// List<OperInfo> l = jt.query(sql, new
		// BeanPropertyRowMapper(OperInfo.class));
//		PaginationHelper<OperInfo> ph = new PaginationHelper<OperInfo>();
		// 页面对象
		String countSql = "select count(*) from oper_info "
				+ (sql.equals("") ? "" : " where " + sql) + " mtype=0";
		sql = "select * from  oper_info where " + sql
				+ " mtype=0  order by mid,oper_id";
//		CurrentPage<OperInfo> p = ph.fetchPage(jt, countSql, sql,
//				new Object[] {}, pageNo, new AppParam().getPageSize(),
//				new BeanPropertyRowMapper(OperInfo.class));
//		return p;
		
		return queryForPage(countSql, sql, pageNo, new AppParam().getPageSize(),
			OperInfo.class);
	}

	public List<OperInfo> getOneOpersObject(String mid, Integer operId) {
		StringBuffer buf=new StringBuffer();
		buf.append("select * from  oper_info where mid='"+mid+"'");
		if(operId!=null&&operId!=0){buf.append(" and oper_id =" + operId);  };
		buf.append(" and  mtype=0");
		List<OperInfo> l = query(buf.toString(),OperInfo.class);
		return l;
	}

	public List getListOper() {
		String sql = "select distinct oper_name from oper_info where mtype=0";
		return queryForStringList(sql);
	}
	/**
	 * 融易付操作员日志查询
	 * @param mid
	 * @param name
	 * @param sdate
	 * @param edate
	 * @param pageIndex
	 * @return
	 */
	public CurrentPage<OperLog> queryForDownload(String mid, String name, int sdate, int edate) {
		name = Ryt.sql(name);
		String sql = "select ol.*, m.name, oi.oper_name from  oper_log ol, oper_info oi, minfo m "
						+ "where ol.oper_id = oi.oper_id and ol.mid = m.id and oi.mtype=0 and ol.mtype=0 and ol.mid=oi.mid   and";

		if (mid.trim()!=null) {
			sql += " ol.mid = '"+mid+"'"+" and";
		}
		if (name != null && !name.equals("")) {
			sql += " oi.oper_name like '%" + name + "%'   and";
		}
		if (sdate > 0) {
			sql += " ol.sys_date >= " + sdate + "   and";
		}
		if (edate > 0) {
			sql += " ol.sys_date <= " + edate + "   and";
		}
		sql = sql.substring(0, sql.length() - 5);
		sql += " order by ol.sys_date desc,ol.sys_time desc,ol.mid,ol.oper_id";
		String countSql = "select count(*) from ( " + sql + " ) as s";
		return queryForPage(countSql, sql, 1, -1,OperLog.class);
	}
	/**
	 * 融易付操作员日志查询
	 * @param mid
	 * @param name
	 * @param sdate
	 * @param edate
	 * @param pageIndex
	 * @return
	 */
	public CurrentPage<OperLog> getOperLog4DWR(String mid, String name, int sdate, int edate, int pageIndex) {
		name = Ryt.sql(name);
		String sql = "select ol.*, m.name, oi.oper_name from  oper_log ol, oper_info oi, minfo m "
						+ "where ol.oper_id = oi.oper_id and ol.mid = m.id and oi.mtype=0 and ol.mtype=0 and ol.mid=oi.mid   and";

		if (mid.trim()!=null) {
			sql += " ol.mid = '"+mid+"'"+" and";
		}
		if (name != null && !name.equals("")) {
			sql += " oi.oper_name like '%" + name + "%'   and";
		}
		if (sdate > 0) {
			sql += " ol.sys_date >= " + sdate + "   and";
		}
		if (edate > 0) {
			sql += " ol.sys_date <= " + edate + "   and";
		}
		sql = sql.substring(0, sql.length() - 5);
		sql += " order by ol.sys_date desc,ol.sys_time desc,ol.mid,ol.oper_id";
		String countSql = "select count(*) from ( " + sql + " ) as s";
		return queryForPage(countSql, sql, pageIndex, new AppParam().getPageSize(),OperLog.class);
	}

	public void deleteOper(String mid, int oper_id) throws Exception {
		String operInfo = "delete from oper_info where mid='"+ mid+"'"
				+ " and oper_id=" + oper_id; // 删除操作员的信息
		String authInfo = "delete from oper_auth_info where mid='"+ mid+"'"
				+ " and oper_id=" + oper_id;// 删除操作员的权限信息
		batchSqlTransaction(new String[] { operInfo, authInfo });
	}

	// 更新操作员登录时间
	public void updateUserLoginTime(String mid, String uid, String loginIp) {
		int mtype=0;
		String dateTime = DateUtil.today()+" " +DateUtil.getStringTime(DateUtil.getCurrentUTCSeconds());
		String usql = "update oper_info set err_count=0 ,logined =?, last_login_ip=? where mid=? and oper_id =? and mtype = ?";
		Object[] uobj = new Object[] { dateTime, loginIp, mid, uid, mtype };
		int[] uint = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
				Types.INTEGER };
		 update(usql, uobj, uint);
	}

	public void setPass(String mid, int oper_id, String npass) throws Exception {
		 update(
						"update oper_info set oper_pass = ?,last_updat=0 where mtype=0 and oper_id = ? and mid= ?",
						new Object[] { npass, oper_id, mid }, new int[] {
								Types.VARCHAR, Types.INTEGER, Types.VARCHAR });
	}

	public void setCreditPass(String mid, String oper_id, String npass)
			throws Exception {
		 update(
						"update oper_info set oper_pass = ? where mtype=1 and oper_id = ? and mid= ?",
						new Object[] { npass, oper_id, mid }, new int[] {
								Types.VARCHAR, Types.INTEGER, Types.VARCHAR });
	}
	
	public void edit(String ostate, String oper_email, String oper_tel,
			String oper_name, String operid, String mid, int mtype)
			throws Exception {

		Object[] obj = new Object[] { ostate, oper_email, oper_tel, oper_name,
				                      operid, mid, mtype};
		int[] type = new int[] { Types.TINYINT, Types.VARCHAR, Types.VARCHAR,
				                 Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.INTEGER};
	   update("update oper_info set state =?,oper_email=?,oper_tel=?,oper_name=?,err_count=0 where oper_id=? and mid=? and mtype=?",
						obj, type);
	}
	
	public LoginUser getLoginedUser(String mid, String uid) throws Exception {
		int mtype=0;//融易付商户操作员
		StringBuilder sb = new StringBuilder();		
		sb.append("select m.abbrev     as abbrev, ");
		sb.append("       m.exp_date   as exp_date, ");
		sb.append("       m.mstate     as state, ");
		sb.append("       o.mid        as mid, ");
		sb.append("       o.oper_name  as oper_name, ");
		sb.append("       o.oper_id    as oper_id, ");
		sb.append("       o.mtype      as mtype, ");
		sb.append("       o.logined    as logined, ");
		sb.append("       o.oper_pass  as md5pwd, ");
		sb.append("       o.err_count  as err_count, ");
		sb.append("       o.last_updat as last_updat, ");
		sb.append("       o.role       as role, ");
		sb.append("       o.auth       as auth, ");
		sb.append("       o.state      as oper_state, ");
		sb.append("       o.err_time   as err_time, ");
		sb.append("       o.anti_phishing_str as antiPhishingStr, ");
		sb.append("       o.last_login_ip   as lastLoginIp");
		sb.append("  from oper_info o, minfo m ");
		sb.append(" where m.id = o.mid ");
		sb.append("   and o.mid = ? ");
		sb.append("   and o.oper_id = ? ");
		sb.append("   and o.mtype = ? ");
		
		Object[] uobj = new Object[] { mid, uid, mtype };
		int[] uint = new int[] { Types.VARCHAR, Types.INTEGER, Types.INTEGER };
		return  queryForObject(sb.toString(), uobj, uint,LoginUser.class);
	}

	public int hasOper(String operid, String minfo_id, int mtype) {
		return queryForInt(
						"select count(*) from oper_info where mid=? and oper_id=? and mtype=?",
						new Object[] { minfo_id, operid, mtype }, new int[] {
								Types.VARCHAR, Types.INTEGER, Types.TINYINT });
	}

	public void add(String action, String ostate, String oper_email,
			String oper_tel, String oper_name, String operpass, int operid,
			String minfo_id, int mtype, int role,String auth) throws Exception {
		int regDate = DateUtil.today();
		Object[] obj = new Object[] { ostate, oper_email, oper_tel, oper_name,
				operpass, operid, minfo_id, regDate, mtype, role,auth};
		int[] type = new int[] { Types.TINYINT, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR,
				Types.INTEGER, Types.TINYINT, Types.TINYINT, Types.VARCHAR};

		String sql = "insert into oper_info (state,oper_email,oper_tel,oper_name,oper_pass,oper_id,mid,reg_date,mtype,role,auth) values (?,?,?,?,?,?,?,?,?,?,?)";
		update(sql, obj, type);
	}

	public String getOldPass(int mtype, int id, String mid) {
		return queryForString(
				"select oper_pass from oper_info where mtype=" + mtype
						+ " and oper_id = " + id + " and mid='"+ mid+"'");
	}

	public void editPass(String opass, String npass, int mtype, int id, String mid) {
		update("update oper_info set oper_pass = '" + Ryt.sql(npass)
				+ "' where mtype=" + mtype + " and oper_id = " + id
				+ " and mid='"+mid+"'");
	}

	public void updateUserLoginLog(String sql, Object[] object, int[] types) {
		update(sql, object, types);
	}
	//商户重要信息修改
	public int updateImportantMsg(Minfo minfo) {
		Object[] obj=new Object[] {
				minfo.getName(),minfo.getAbbrev(),minfo.getCategory(),minfo.getBeginDate(),
				minfo.getExpDate(),minfo.getCorpCode(),minfo.getCodeExpDate(),minfo.getRegCode(),
				minfo.getOpenDate(),minfo.getBankName(),minfo.getBankAcct(),minfo.getBankProvId(),
				minfo.getBankBranch(),minfo.getBankAcctName(),minfo.getCorpName(),minfo.getIdType(),
				minfo.getIdNo(),minfo.getOpenBkNo(),minfo.getLiqObj(),minfo.getLiqState(),minfo.getManLiq(),
				minfo.getGateId(),minfo.getId()};
		int [] types=new int[] {
				Types.VARCHAR,Types.VARCHAR,Types.TINYINT, Types.INTEGER,
				Types.INTEGER,Types.VARCHAR,Types.INTEGER,Types.VARCHAR,
				Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.SMALLINT,
				Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.CHAR,
				Types.CHAR,Types.CHAR,Types.TINYINT,Types.TINYINT,
				Types.TINYINT,Types.TINYINT,Types.VARCHAR};
	   String sql="update minfo set name=?, abbrev=?, category=?,begin_date=?,exp_date=?,corp_code=?,Code_exp_date=?, " +
			"reg_code=?,open_date=?,bank_name=?,bank_acct=?,bank_prov_id=?,bank_branch=?,bank_acct_name=?,corp_name=?," +
			"id_type=?,id_no=?,open_bk_no=?,liq_obj=?,liq_state=?,man_liq=?,gate_id=?,last_update=DATE_FORMAT(NOW(),'%Y%m%d%H%i%s') where id=?";

       return update(sql,obj,types);
	}
	
	//查询商户类型
	public int queryMerType(String mid ){
		String sql="select mer_type from minfo where id=?";
		Object[] args = new Object[]{mid};
		int[] argTypes = new int[]{Types.VARCHAR};
		return queryForInt(sql,args,argTypes);
		
	}
	//向cg_task中插入一条数据
   public int addCgTask(String operType,String operand ){
	   String cgTaskSql="insert into cg_task (tdate,tcode,operand) values(DATE_FORMAT(NOW(),'%Y%m%d'),?,?)";
	   return update(cgTaskSql,new Object[]{operType,operand});
   }

	/**
	 * 商户关闭
	 * @param mid
	 * @param loginMid
	 * @param loginId
	 * @return
	 */
	public String closeMer(String mid, String loginMid,int loginId){
		
		//这里先不做判断
//		 String queryHlogSql="select count(*) from hlog where batch=0 and tstat=2 and mid="+mid;
//		 int mun=jt.queryForInt("select ("+queryHlogSql+")+("+queryHlogSql.replace("hlog", "tlog")+")");
//		 if(mun!=0){
//			 return "商户还存在未结算的订单，请结算完后再关闭！";
//		 }
		int row= update("update minfo set mstate=1 where id='"+mid+"'");
		if(row==1){
			saveOperLog("商户关闭","关闭了商户号为：["+mid+"]的商户");
			return "ok";
		}else{
			return "商户关闭失败，请重试！";
		}
	}
	/**
	 * 商户开启
	 * @param mid
	 * @return
	 */
	public int merOpen(String mid) {
		Object[] obj=new Object[] { 0, mid };
		return update("update minfo set mstate=? where id=?",obj , 
				new int[] {Types.TINYINT, Types.VARCHAR });
	}
	/**
	 * 商户验证
	 */
	public String merValid(String mid) {

		String pubKey = "";
		int countMinfo = 0;
		
		// 判断商户是否存在
		countMinfo = queryForInt("select count(*) from minfo where id= '"+mid+"'");

		if (countMinfo <= 0) {
			return  "开启失败:商户不存在!";
		}

		// 判断商户是否开启
		countMinfo = queryForInt("select count(*) from minfo where id=? and mstate=?", new Object[] { mid, 0 },
			new int[] { Types.VARCHAR, Types.TINYINT });

		if (countMinfo > 0) {
			return  "商户已开启,不能重复开启!";
		}

		// 判断商户是否导入公钥
		pubKey = queryForString("select public_key from minfo where id=?", new Object[] { mid });
		if (pubKey == null || pubKey.equals("")) {
			return  "开启失败:商户未导入公钥!";
		}

		// 判断商户是否开通网关
		countMinfo = queryForInt("select count(*) from fee_calc_mode where mid=? and state=?",
			new Object[] { mid, 1 }, new int[] { Types.VARCHAR, Types.TINYINT });

		if (countMinfo <= 0) {
			return  "开启失败:商户未开通任何网关!";
		}

		return  AppParam.SUCCESS_FLAG;
	}
	
	/**
	 * 查询商户信息
	 * @param mid
	 * @return
	 */
	public Map<String, Object> queryMerInfo(String mid){
		String sql = "select * from minfo where id = '"+ mid+"'" + " limit 1";
		Map<String, Object> merInfo = queryForMap(sql);
		return merInfo;
	}
	
	/**
	 * 保存账户
	 * @param uid
	 * @param aid
	 * @param aname
	 * @param state
	 * @param accType
	 * @return
	 */
	public int[] saveAccInfo(String uid, String aid,String aname, String state, String accType,String []sqls){
		StringBuffer sql=new StringBuffer("insert into acc_infos (uid,aid,init_date,aname,state,acc_type,pay_flag) values(");
		sql.append("'").append(uid).append("',");
		sql.append("'").append(aid).append("',");
		sql.append(DateUtil.today()).append(",");
		sql.append("'").append(aname).append("',");
		sql.append(state).append(",");
		sql.append(accType).append(",");
		sql.append("1");
		sql.append(")");
		sqls[1]=sql.toString();
		return batchSqlTransaction(sqls);
	}
	
	/**
	 * 判断是否有交易账户
	 * @param mid
	 * @return
	 */
	@Override
	public boolean hasTransAcc(String mid){
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and acc_type = 1";
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
	
	/**
	 * 根据商户号统计账户数
	 * @param mid
	 * @return
	 */
	public int countAccNum(String mid){
		String sql = "select count(0) from acc_infos where uid = '" + mid + "'";		
		return queryForInt(sql);
	}
	
	/**
	 * 判断账户是否存在
	 * @param aid
	 * @return
	 */
	public boolean hasAccByAid(String aid){
		String sql = "select count(0) from acc_infos where aid = " + aid;
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
	
	/**
	 * 判断商户是否开启
	 * @param mid
	 * @return
	 */
	public boolean merIsOpen(String mid) {
		boolean flag = false;
		
		// 判断商户是否开启
		int countMinfo = queryForInt("select count(*) from minfo where id='"+ mid+"' and mstate=0");
		if (countMinfo > 0) {
			flag= true;
		}

		return flag;
	}
	
	/**
	 * 关闭操作员号
	 * @param mid
	 * @param operId
	 * @param mtype
	 * @param newErrCount
	 */
	public int closeOper(int newErrCount, String mid, Integer operId, Integer mtype) {
		String sql="update oper_info set state = 1,err_count=? where mid =? and oper_id =? and mtype=? ";
		Object[] objArr=new Object[]{newErrCount ,mid ,operId ,mtype };
		return update(sql,objArr);
	}
	/**
	 * 修改错误次数
	 * @param newErrCount
	 * @param mid
	 * @param operId
	 * @param mtype
	 */
	public int updatErrorCount(int newErrCount, String mid, Integer operId, Integer mtype) {
		String sql="update oper_info set err_count =?,err_time=? where mid =? and oper_id =? and mtype=? ";
		Object[] objArr=new Object[]{newErrCount ,DateUtil.today(),mid ,operId ,mtype };
		return update(sql,objArr);
	}
	/**
	 * 修改密码
	 * @param md5
	 * @param mid
	 * @param operId
	 * @param mtype
	 * @return
	 */
	public int updateOperPwd(String md5Pwd, String mid, Integer operId, String antiPhishingStr) {
		int mtype=0;
		String sql = "update oper_info set oper_pass = ?,last_updat=?,anti_phishing_str=? where mid = ? and oper_id = ? and mtype= ?";
		Object[] objArr=new Object[]{md5Pwd,DateUtil.today(),antiPhishingStr,mid,operId,mtype};
		return update(sql,objArr);
	}
	/**
	 * 根据mid查询操作员
	 * @param mid
	 * @return
	 */
	public Map<Integer, String> getHashOper(String mid) {
		String sql = "select oper_id,oper_name from oper_info where mtype=0 and mid ='"+ mid+"'";
		return queryToMap2(sql);
	}
	/**
	 * 是否存在该商户
	 * @param mid 商户ID
 	 * @return true:存在   false:不存在
	 */
	public boolean hasMid(String mid){
		StringBuffer sql=new StringBuffer("select * from oper_info where mid=");
		if(mid!=null){
			sql.append(Ryt.addQuotes(mid)+" and role=1");
			int size=getJdbcTemplate().query(sql.toString(),new BeanPropertyRowMapper<OperInfo>(OperInfo.class)).size();
			if(size>0)
				return true;
			else
				return false;
		}
		return false;
	}
	
	/**
	 * 判断商户是否存在该操作员
	 * @param mid 商户号 
	 * @param operId 操作员ID
	 * @return
	 */
	public List<OperInfo> hasOperInMid(String mid,String operId){
//		select * from oper_info where mid=1  and oper_id=12306 and oper_pass='eabd8ce9404507aa8c22714d3f5eada9' and role=1
		StringBuffer sql=new StringBuffer("select * from oper_info where mid=");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and role=1 and oper_id=").append(operId);
		List<OperInfo> list= getJdbcTemplate().query(sql.toString(),new BeanPropertyRowMapper<OperInfo>(OperInfo.class));
		return list;
	}
	
	/**
	 * 判断操作员密码是否正确
	 * @param mid 商户号
	 * @param operId 操作员号
	 * @param operPass 操作员密码
	 * @return
	 */
	public List<OperInfo> checkOperPass(String mid,String operId,String operPass){
//		select * from oper_info where mid=1  and oper_id=12306 and oper_pass='eabd8ce9404507aa8c22714d3f5eada9' and role=1
		StringBuffer sql=new StringBuffer("select * from oper_info where mid=");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and role=1 and oper_id=").append(operId);
		sql.append(" and oper_pass='").append(operPass).append("'");
		List<OperInfo> list= getJdbcTemplate().query(sql.toString(),new BeanPropertyRowMapper<OperInfo>(OperInfo.class));
		return list;
	}
	
	/**
	 * 查询用户权限
	 * @param mid 商户ID
	 * @param operId 操作员ID
	 * @return
	 */
	public String getUserAuth(String mid,int operId){
		StringBuffer sql=new StringBuffer("select auth from oper_info where mid=");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and oper_id=");
		sql.append(operId);
		String reslt=queryForString(sql.toString());
		return reslt;
	}
	/**
	 * 判断是否fou
	 * @param mid
	 * @return
	 */
	public boolean hasTransAcc_State(String mid){
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and acc_type = 1 and state=1";
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
	/**
	 * 判断账户信息表中是否有这个结算账户
	 * @param mid
	 * @return
	 */
	public boolean hasTransliqAcc(String mid){
		String sql = "select count(0) from acc_infos where uid = '" + mid + "' and aid = '" + mid + "'";
		if(queryForInt(sql) > 0)
			return true;
		return false;
	}
/*
 *查看商户的状态
 */
	
	public int querymstate(String mid){
		return queryForInt("select  mstate from minfo where id  = " + Ryt.addQuotes(mid));
		
	}
	
	public CurrentPage<OperLog> getMidOperLog(String mid, String operId, int sdate, int edate, int pageIndex,int pageSize) {
		
		String sql = "select ol.*, m.name, oi.oper_name from  oper_log ol, oper_info oi, minfo m "
						+ "where ol.oper_id = oi.oper_id and ol.mid = m.id and oi.mtype=0 and ol.mtype=0 and ol.mid=oi.mid   and ol.mid = '"+mid.trim()+"' and ol.oper_id = '"+operId.trim()+"' and ";

		if (sdate > 0) {
			sql += " ol.sys_date >= " + sdate + "   and";
		}
		if (edate > 0) {
			sql += " ol.sys_date <= " + edate + "   and";
		}
		sql = sql.substring(0, sql.length() - 5);
		sql += " order by ol.sys_date desc,ol.sys_time desc,ol.mid,ol.oper_id";
		String countSql = "select count(*) from ( " + sql + " ) as s";
		return queryForPage(countSql, sql, pageIndex, pageSize,OperLog.class);
	}
	
	public int saveMinfoLog(String mid,Integer operId,String action,String actionDesc,String
			operIp){
		int sys_date = DateUtil.today();
		int sys_time= DateUtil.getCurrentUTCSeconds();
		StringBuffer sql=new StringBuffer("insert into oper_log(mid,sys_date,sys_time,oper_id,action,action_desc,oper_ip)");
		
		sql.append(" value(").append(Ryt.addQuotes(mid)).append(",").append(sys_date).append(",").append(sys_time).append(",").append(operId).append(",").append(Ryt.addQuotes(action)).append(",").append(Ryt.addQuotes(actionDesc));
		sql.append(",").append(Ryt.addQuotes(operIp));
		sql.append(")");
		System.out.println("sql="+sql.toString());
		return update(sql.toString());
		
		
	}
}
