package com.rongyifu.mms.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.AuthInfo;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;

@SuppressWarnings("unchecked")
public class OperAuthDao extends PubDao{
	
	/**
	 * @param mid
	 * @param operId
	 * 使权限修改生效
	 */
	public int effectAuthChange(String mid, Integer operId){
		String authstr = getAuthStr(mid, operId);
		String sql = "update oper_info set auth = ?,auth_change = ?,auth_change_status=? where mtype=0 and mid = ? and oper_id = ? ";
		return update(sql, new Object[] { authstr,"",0,mid,operId}, new int[] {
				Types.VARCHAR, Types.VARCHAR,Types.TINYINT,Types.VARCHAR, Types.INTEGER });
		
	}
	
	public String getAuthStr(String mid, Integer operId){
		String authSql = "SELECT auth_change FROM oper_info WHERE mtype=0 and mid = ? and oper_id = ?";
		Object[] args = new Object[]{mid,operId};
		String authstr = queryForString(authSql, args);
		return authstr;
	}
	
	public Map<String,Object> getAuthApply(String mid, Integer operId){
		String sql = "SELECT auth_change,auth FROM oper_info WHERE mtype=0 AND auth_change_status = 1 AND mid = ? AND oper_id = ? LIMIT 1";
		Object[] args = new Object[]{mid,operId};
		return queryForMap(sql, args);
	}
	
	public int addMenuApply(String authstr, String mid,Integer operId){
		int rowcount = 0 ;
		String sql = "UPDATE oper_info SET auth_change = ?, auth_change_status = 1 WHERE mtype=0 AND mid = ? AND oper_id = ?";
		Object [] args = new Object[]{authstr,mid,operId};
		int [] argTypes = new int[]{Types.VARCHAR,Types.VARCHAR,Types.INTEGER};
		rowcount = update(sql, args, argTypes);
		return rowcount;
	}
	
	/**
	 * @param mid
	 * @param operId
	 * @return
	 * 检查是否已经存在申请
	 */
	public boolean isApplyExist(String mid,Integer operId){
		boolean flag = false;
		String sql = "SELECT COUNT(*) FROM oper_info WHERE mtype=0 AND auth_change_status = 1 AND mid = ? AND oper_id=?";
		Object [] args = new Object[]{mid,operId};
		int rowcount = queryForInt(sql, args, new int[]{Types.VARCHAR,Types.TINYINT});
		if(rowcount != 0 ){
			flag = true;
		}
		return flag;
	}

//	private JdbcTemplate jt = getJdbcTemplate();
	
	public CurrentPage<OperInfo> showOperApply(String mid,Integer operId,Integer pageNo){
		StringBuilder sqlCountRows = new StringBuilder("SELECT COUNT(*) FROM oper_info WHERE mtype=0 AND auth_change_status= 1");
		StringBuilder sqlFetchRows = new StringBuilder("SELECT o.oper_id,o.oper_name,o.mid,m.name mName FROM oper_info o, minfo m WHERE o.mid = m.id AND mtype=0 AND o.auth_change_status = 1");
		StringBuilder condition = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		if(StringUtils.isNotBlank(mid)){
			condition.append(" AND mid = ?");
			args.add(mid);
		}
		if(null != operId){
			condition.append(" AND oper_id = ?");
			args.add(operId);
		}
		sqlCountRows.append(condition);
		sqlFetchRows.append(condition).append(" ORDER BY o.oper_id DESC");
		return queryForPage(sqlCountRows.toString(), sqlFetchRows.toString(), args.toArray(), pageNo, new AppParam().getPageSize(),  OperInfo.class);
	}

	public List<AuthInfo> getMenu(int pid, int level, LoginUser user) {

		String sql = "select a.* from auth_info a ,oper_auth_info o where  a.parent_id = ? and a.auth_level = ? "
				+ "and o.mid= ? and o.oper_id=? and o.mtype= ? and o.auth_id = a.auth_id order by a.auth_id ";
		Object[] args = { pid, level, user.getMid(), user.getOperId(),
				user.getMtype() };

		int[] argsType = { Types.SMALLINT, Types.SMALLINT, Types.VARCHAR,
				Types.SMALLINT, Types.TINYINT };
		return query(sql, args, argsType,AuthInfo.class);
	}

	public List<AuthInfo> getMenu(int authId) {
		return query(
				"select * from auth_info where auth_id=?",
				new Object[] { authId }, new int[] { Types.SMALLINT },AuthInfo.class);
	}

	public List<AuthInfo> getAllMenu(int pid, int level, String mid,
			int authType) {
		return query(
				"select * from auth_info where parent_id = ? and auth_level = ? "
						+ " and state=" + authType + " order by auth_id",
				new Object[] { pid, level }, new int[] { Types.SMALLINT,
						Types.SMALLINT }, AuthInfo.class);
	}

	public List<AuthInfo> getCreditMenu(int pid, int level) {
		return query(
				"select * from auth_info where parent_id = ? and auth_level = ? "
						+ " and state=3 order by auth_id", new Object[] { pid,
						level }, new int[] { Types.SMALLINT, Types.SMALLINT },AuthInfo.class);
	}

	public String checkMenu(String mid, int oid) {
		String sql = "select auth from oper_info where mtype=0 and mid =? and oper_id = ?";
		Object[] args = new Object[]{mid,oid};
		return  queryForString(sql, args);
	}

	public List getOperRightsUrls(String mid, String uid, int mtype) {
		return queryForList(
						"select a.action from auth_info a ,oper_auth_info o where  o.mid= ? and o.oper_id=? and o.mtype = ? and o.auth_id=a.auth_id",
						new Object[] { mid, uid, mtype }, new int[] {
								Types.VARCHAR, Types.INTEGER, Types.INTEGER });
	}

	public List<String> findCreditAuth(String mid, String oid) {
		String sql = "select a.auth_desc from auth_info a, oper_auth_info oa ";
		sql += "where a.auth_id = oa.auth_id and oa.mtype=1 and";

		if (mid != null && !mid.equals("")) {
			sql += " oa.mid = " + Ryt.addQuotes(mid) + "   and";
		}
		if (oid != null && !oid.equals("")) {
			sql += " oa.oper_id = " + Integer.parseInt(oid) + "   and";
		}
		sql = sql.substring(0, sql.length() - 5);
		List list = queryForStringList(sql);
		return list;
	}

	public String findAuth(String mid, String oid) {
		String sql = "select auth from oper_info where mtype=0 and ";

		if (mid != null && !mid.equals("")) {
			sql += " mid = " + Ryt.addQuotes(mid) + "   and";
		}
		if (oid != null && !oid.equals("")) {
			sql += " oper_id = " + Integer.parseInt(oid) + "   and";
		}
		sql = sql.substring(0, sql.length() - 5);
		
		return queryForString(sql);
	}

	public int getRole(String mid,int operId) {
		return queryForInt("select role from oper_info where mtype=0 and mid='"+mid+"'"
				+ " and oper_id = " + operId);
	}
	
	public int cancelAddMenu(String mid,int operId){
		String sql = "UPDATE oper_info SET auth_change = ?,auth_change_status=? WHERE mtype=0 and mid = ? and oper_id = ? ";
		return update(sql, new Object[] { "", 0 ,mid ,operId}, new int[] {
				Types.VARCHAR,Types.TINYINT,Types.VARCHAR, Types.INTEGER });
	}
	
	public void addMenu(String authstr, String mid,int operId) {
		String sql = "update oper_info set auth = ? where mtype=0 and mid = ? and oper_id = ? ";
		update(sql, new Object[] { authstr,mid,operId}, new int[] {
				Types.VARCHAR, Types.VARCHAR, Types.INTEGER });
	}
	 /**
	  * 查询商户是否有一个以上的操作员
	  * @param mid
	  * @return
	  */
	public boolean isExistOper(String mid){
		   String sql="select count(*) from oper_info where mid='"+mid+"'";
			try {
				return  getJdbcTemplate().queryForInt(sql) > 0 ;
			}catch (Exception e) {
				return false;
			}
	}
	/**
	 * 是否存在相同的操作员
	 * @param operid
	 * @param minfo_id
	 * @param mtype
	 * @return
	 */
	public int hasOper(int operid, String minfo_id, int mtype) {
		//内部中调用
		return queryForInt("select count(*) from oper_info where mid=? and oper_id=? and mtype=?",
			new Object[] { minfo_id, operid, mtype }, new int[] { Types.VARCHAR, Types.INTEGER, Types.TINYINT });
	}
	/**
	 * 查询商户的操作员数
	 * @param mid
	 * @return
	 */
   public int queryOperNum(String mid){
	   String sql="select count(*) from oper_info where mid=?";
	   return queryForInt(sql,new Object[]{mid});
   }
	/**
	 * 操作员修改密码
	 * @param md5
	 * @param mid
	 * @param operId
	 * @param mtype
	 * @return
	 */
	public boolean updateOperPwd(String md5, String mid, Integer operId, Integer mtype) {
		//内部中调用(editPass)
		String sql = "update oper_info set oper_pass = ?,last_updat=? where mid = ? and oper_id = ? and mtype= ?";
		try {
			return getJdbcTemplate().update(sql, new Object[] { md5, DateUtil.today(), mid, operId, mtype },
				new int[] { Types.CHAR, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.TINYINT }) == 1;
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 开启交易账户
	 * @param mid 商户号
	 * @return
	 */
	public int openJYZH(String mid){
		StringBuffer sql=new StringBuffer("update acc_infos set state=1 where acc_type=1 and uid='"); 
		sql.append(mid+"'");
		return update(sql.toString());
	}
}
