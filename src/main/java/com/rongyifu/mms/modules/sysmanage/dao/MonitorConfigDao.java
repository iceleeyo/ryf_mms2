package com.rongyifu.mms.modules.sysmanage.dao;

import com.rongyifu.mms.bean.MonitorConfig;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

/**
* @ClassName: MonitorConfigDao
* @Description: 监控配置dao
* @author li.zhenxing
* @date 2014-9-22 下午2:57:06
*/ 
public class MonitorConfigDao extends PubDao<MonitorConfig> {
	
	public MonitorConfig getConfigById(int id){
		String sql = "SELECT * FROM monitor_config WHERE id = ?";
		return queryForObject(sql, new Object[]{id}, MonitorConfig.class);
	}
	
	public MonitorConfig queryByTypeAndTarget(String type,String targetId,String gateId) {
		String joinTable = "";
		String column = "";
		if("1".equals(type)){
			joinTable = " LEFT JOIN minfo m ON mc.target_id = m.id";
			column = ",m.id as targetName";
		}
		String sql = "SELECT mc.*"+column+" FROM monitor_config mc "+joinTable+" WHERE mc.type = ? AND mc.target_id=? AND mc.gate_id=? LIMIT 1";
		return queryForObject(sql, new Object[]{type,targetId,gateId}, MonitorConfig.class);
	}
	
	public int update(MonitorConfig mc){
		StringBuilder sql = new StringBuilder("UPDATE monitor_config SET");
		sql.append(" busy_interval_s=?,idle_interval_s=?,judger_index_s=?,success_n=?,success_m=?");
		sql.append(",busy_interval_f=?,idle_interval_f=?,judger_index_f=?,fail_n=?,fail_m=?");
		sql.append(",busy_interval_c=?,idle_interval_c=?,judger_index_c=?,continual_fail_n=?,continual_fail_m=?");
		//sql.append(",wait_n=?,wait_m=?,judger_index_w=?,judger_index_sf=?");
		sql.append(" WHERE id=?");
		Object[] args = new Object[]{
				 mc.getBusyIntervalS(),mc.getIdleIntervalS(),mc.getJudgerIndexS(),mc.getSuccessN(),mc.getSuccessM()
				 ,mc.getBusyIntervalF(),mc.getIdleIntervalF(),mc.getJudgerIndexF(),mc.getFailN(),mc.getFailM()
				 ,mc.getBusyIntervalC(),mc.getIdleIntervalC(),mc.getJudgerIndexC(),mc.getContinualFailN(),mc.getContinualFailM()
				 ,mc.getId()};
		int count = update(sql.toString(), args);
		saveOperLog("更新监控配置", count==1?"更新成功":"更新失败");
		return count;
	}
	
	public int addConfig(MonitorConfig mc){
		
		StringBuilder sql = new StringBuilder("INSERT INTO monitor_config (");
		sql.append("type,target_id");
		sql.append(",busy_interval_s,idle_interval_s,judger_index_s,success_n,success_m");
		sql.append(",busy_interval_f,idle_interval_f,judger_index_f,fail_n,fail_m");
		sql.append(",busy_interval_c,idle_interval_c,judger_index_c,continual_fail_n,continual_fail_m,gate_id");
		//sql.append(",wait_n,wait_m,judger_index_w,judger_index_sf");
		sql.append(")values(");
		sql.append("?,?");
		sql.append(",?,?,?,?,?");
		sql.append(",?,?,?,?,?");
		//sql.append(",?,?,?,?,?");
		sql.append(",?,?,?,?,?,?)");
		Object[] args = new Object[]{mc.getType(),mc.getTargetId()
									 ,mc.getBusyIntervalS(),mc.getIdleIntervalS(),mc.getJudgerIndexS(),mc.getSuccessN(),mc.getSuccessM()
									 ,mc.getBusyIntervalF(),mc.getIdleIntervalF(),mc.getJudgerIndexF(),mc.getFailN(),mc.getFailM()
									 ,mc.getBusyIntervalC(),mc.getIdleIntervalC(),mc.getJudgerIndexC(),mc.getContinualFailN(),mc.getContinualFailM(),mc.getGateId()
									 };
		int count = update(sql.toString(), args);
		saveOperLog("新增监控配置", count==1?"新增成功":"新增失败");
		return count;
	}
	
	public int isMerNoExists(String mid){
		String sql = "SELECT COUNT(*) FROM minfo WHERE id = ?";
		return queryForInt(sql, new Object[]{mid});
	}

	public String delConfig(Integer id) {
		// TODO Auto-generated method stub
		String msg = "ok";
		String deleteSql = "delete from monitor_config where id = " + Ryt.sql(String.valueOf(id));
		try {
			update(deleteSql);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "error";
		}
		return msg;
	}
}
