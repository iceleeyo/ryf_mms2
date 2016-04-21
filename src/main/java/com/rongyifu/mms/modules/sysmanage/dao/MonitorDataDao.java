package com.rongyifu.mms.modules.sysmanage.dao;

import java.util.Map;

import com.rongyifu.mms.bean.MonitorData;
import com.rongyifu.mms.db.PubDao;

/**
* @ClassName: MonitorConfigDao
* @Description: 监控数据dao
* @author li.zhenxing
* @date 2014-9-22 下午2:57:06
*/ 
public class MonitorDataDao extends PubDao<MonitorData> {
	
	public Map<String,Object> queryMaxAndMin(String cfgId,int monitorType,String sysDate){
		String sql = "SELECT IFNULL(MAX(x),0) as 'MAX',IFNULL(MIN(x),0) as 'MIN' FROM monitor_data WHERE 1=1 AND config_id = ? AND monitor_type = ? AND sys_date = ?";
		return queryForMap(sql, new Object[]{cfgId,monitorType,sysDate});
	}
	
	public int add(MonitorData md){
		StringBuilder sql = new StringBuilder("INSERT INTO monitor_data (");
		sql.append("config_id,sys_date,sys_time,status");
		sql.append(",monitor_type,x,max,min");
		sql.append(")values(");
		sql.append("?,?,?,?");
		sql.append(",?,?,?,?)");
		Object[] args = new Object[]{md.getConfigId(),md.getSysDate(),md.getSysTime(),md.getStatus()
									 ,md.getMonitorType(),md.getX(),md.getMax(),md.getMin()};
		return update(sql.toString(), args);
	}
	
	public String getRYtGateById(int gateId){
		String Sql = "select gate_name from ryt_gate where gate="+gateId;
		return queryForString(Sql) ;
	}
}
