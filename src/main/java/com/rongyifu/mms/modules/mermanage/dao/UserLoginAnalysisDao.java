package com.rongyifu.mms.modules.mermanage.dao;

import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class UserLoginAnalysisDao {
	
	private PubDao dao = new PubDao();
	
	public Map<String, Object> statisticsUserLogin(Integer beginDate, Integer endDate){
		StringBuffer sql = new StringBuffer();
		sql.append("select ifnull(sum(case when action_desc like '%登陆成功%' then 1 else 0 end), 0) login_success_num,");
		sql.append("       ifnull(sum(case when action_desc like '%密码错误%' or action_desc = '该操作员已被关闭' then 1 else 0 end), 0) login_fail_num,");
		sql.append("       ifnull(sum(case when action_desc like '%退出系统%' then 1 else 0 end), 0) exit_system_num");
		sql.append("  from oper_log");
		sql.append(" where sys_date between " + beginDate + " and " + endDate);
		sql.append("   and (action_desc like '%登陆成功%' or action_desc like '%密码错误%' or action_desc like '%退出系统%' or action_desc = '该操作员已被关闭')");
	
		return dao.queryForMap(sql.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<OperLog> queryLoginDetail(Integer beginDate, Integer endDate){
		StringBuffer sql = new StringBuffer();
		sql.append("select ol.*, m.name, oi.oper_name");
		sql.append("  from oper_log ol, oper_info oi, minfo m");
		sql.append(" where ol.oper_id = oi.oper_id");
		sql.append("   and ol.mid = m.id");
		sql.append("   and oi.mtype = 0");
		sql.append("   and ol.mtype = 0");
		sql.append("   and ol.mid = oi.mid");
		sql.append("   and ol.sys_date >= " + beginDate);
		sql.append("   and ol.sys_date <= " + endDate);
		sql.append("   and (ol.action = '登录系统' or ol.action = '退出系统')");
		sql.append(" order by ol.sys_date desc, ol.sys_time desc, ol.mid, ol.oper_id");
		
		return dao.query(sql.toString(), OperLog.class);
	}
}
