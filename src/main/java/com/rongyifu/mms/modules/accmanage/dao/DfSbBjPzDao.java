package com.rongyifu.mms.modules.accmanage.dao;

import java.util.List;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

/****
 * 代付失败报警配置数据库操作类
 * @author shdy
 *
 */
public class DfSbBjPzDao extends PubDao {
	
	/**
	 * 查询
	 * @param gid
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<B2EGate> queryDFBankInfo(String gid,int pageNo){
		StringBuffer sql=new StringBuffer("select * from b2e_gate where 1=1 ");
		if(!Ryt.empty(gid))
		sql.append("and gid="+Ryt.sql(gid));
		String sqlCount =	sql.toString().replace("*","count(*)");
		return queryForPage(sqlCount,sql.toString(), pageNo, new AppParam().getPageSize(),B2EGate.class) ;
	}
	
	/***
	 * 修改B2eGate报警配置
	 * @param gid
	 * @param fcount
	 * @param sucrate
	 * @return
	 */
	public int updateB2eGateConfig(Integer gid,Integer fcount,Integer sucrate){
		StringBuffer sql=new StringBuffer();
		sql.append("update b2e_gate set fail_count=").append(fcount);
		sql.append(" , suc_rate= ").append(sucrate);
		sql.append(" where gid=").append(gid);
		int[] res=this.batchSqlTransaction(new String[]{sql.toString()});
		return res[0];
	}
	
	/**
	 * 初始化
	 * @return
	 */
	public List<B2EGate> initDFYEBJ(){
		StringBuffer sql=new StringBuffer("select * from b2e_gate");
		return query(sql.toString(),B2EGate.class);
	}
}
