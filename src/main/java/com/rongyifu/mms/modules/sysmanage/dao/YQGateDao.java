package com.rongyifu.mms.modules.sysmanage.dao;

import java.util.List;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

/***
 * 
 * @author admin
 *
 */
@SuppressWarnings("rawtypes")
public class YQGateDao extends PubDao {
	
	/**
	 * 查询银企直连网关维护 信息
	 * @param pageNo
	 * @param account
	 * @return
	 */
	public CurrentPage<B2EGate> queryB2EGate(Integer pageNo,String account){
		StringBuffer sql=new StringBuffer("select * from b2e_gate where 1=1");
		if(!Ryt.empty(account)){
			sql.append(" and acc_no=").append(Ryt.addQuotes(account));
		}
		String sqlCount =	sql.toString().replace("*","count(*)");
		return queryForPage(sqlCount,sql.toString(), pageNo, AppParam.getPageSize(),B2EGate.class) ;
	}
	
	
	
	public int addB2eGate(B2EGate b){
		StringBuffer sql=new StringBuffer("insert into b2e_gate (gid,name,nc_url,bk_no,prov_id,acc_no,acc_name,termid,corp_no,user_no,user_pwd,encode,busi_no) values(");
		sql.append("'").append(Ryt.sql(b.getGid().toString())).append("'");
		sql.append(",'").append(Ryt.sql(b.getName())).append("'");
		sql.append(",'").append(Ryt.sql(b.getNcUrl())).append("'");
		sql.append(",'").append(Ryt.sql(b.getBkNo())).append("'");
		sql.append(",").append(b.getProvId());
		sql.append(",'").append(Ryt.sql(b.getAccNo())).append("'");
		sql.append(",'").append(Ryt.sql(b.getAccName())).append("'");
		sql.append(",'").append(Ryt.sql(b.getTermid())).append("'");
		sql.append(",'").append(Ryt.sql(b.getCorpNo())).append("'");
		sql.append(",'").append(Ryt.sql(b.getUserNo())).append("'");
		sql.append(",'").append(Ryt.sql(b.getUserPwd())).append("'");
		sql.append(",'").append(Ryt.sql(b.getEncode())).append("'");
		sql.append(",'").append(Ryt.sql(b.getBusiNo())).append("');");
		return update(sql.toString());
	}
	
	
	/***
	 * 修改银企直连渠道信息
	 * @param b B2eGate
	 * @return
	 */
	public int editB2eGate(B2EGate b){
		StringBuffer sql=new StringBuffer("update  b2e_gate set ");
		sql.append("prov_id =").append(b.getProvId());
		if (!Ryt.empty(b.getName()))sql.append(",name='").append(Ryt.sql(b.getName())).append("'");
		if (!Ryt.empty(b.getNcUrl()))sql.append(",nc_url='").append(Ryt.sql(b.getNcUrl())).append("'");
		if (!Ryt.empty(b.getBkNo()))sql.append(",bk_no='").append(Ryt.sql(b.getBkNo())).append("'");
		if (!Ryt.empty(b.getAccNo()))sql.append(",acc_no='").append(Ryt.sql(b.getAccNo())).append("'");
		if (!Ryt.empty(b.getAccName()))sql.append(",acc_name='").append(Ryt.sql(b.getAccName())).append("'");
		if (!Ryt.empty(b.getTermid()))sql.append(",termid='").append(Ryt.sql(b.getTermid())).append("'");
		if (!Ryt.empty(b.getCorpNo()))sql.append(",corp_no='").append(Ryt.sql(b.getCorpNo())).append("'");
		if (!Ryt.empty(b.getUserNo()))sql.append(",user_no='").append(Ryt.sql(b.getUserNo())).append("'");
		if (!Ryt.empty(b.getUserPwd()))sql.append(",user_pwd='").append(Ryt.sql(b.getUserPwd())).append("'");
		if (!Ryt.empty(b.getCodeType()))sql.append(",code_type='").append(Ryt.sql(b.getCodeType())).append("'");
		if (!Ryt.empty(b.getBusiNo()))sql.append(",busi_no='").append(Ryt.sql(b.getBusiNo())).append("'");
		sql.append(" where gid='").append(Ryt.sql(b.getGid().toString())).append("'");
		return update(sql.toString());
	}
	
	/***
	 * 获取渠道信息
	 * @param gid
	 * @return
	 */
	public B2EGate getOneB2EGate(String gid){
		String sql="select * from b2e_gate where gid="+Ryt.addQuotes(gid)+";";
		List<B2EGate> b=query(sql, B2EGate.class);
		return b.get(0);
	}
	
}
