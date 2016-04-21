package com.rongyifu.mms.modules.mermanage.dao;

import java.util.List;
import java.util.Map;

import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class SystemModuleStatisticsDao {
	
	private PubDao dao = new PubDao();
	
	public List<Map<String, Object>> doQuery(Integer beginDate, Integer endDate, Integer moduleId){
		StringBuffer sql = new StringBuffer();
		sql.append("select action, cast(max(concat(sys_date, '_', sys_time)) as char) last_oper_time, count(0) oper_num");
		sql.append("  from oper_log");
		sql.append(" where sys_date between " + beginDate + " and " + endDate);
		sql.append("   and action in ('" + getModuleName(moduleId) + "')");
		sql.append(" group by action");
		sql.append(" order by action desc");
		
		return dao.queryForList(sql.toString());
	}
	
	private String getModuleName(Integer moduleId){
		StringBuffer moduleName = new StringBuffer();
		if(moduleId <= 0 || moduleId > 8){
			int i = 0;
			for(String item : moduleNames){
				if(i == 0)
					moduleName.append(item);
				else
					moduleName.append("','").append(item);
				
				i++;
			}
		} else 
			moduleName.append(moduleNames[moduleId - 1]);
		
		return moduleName.toString();
	}
	
	public static final String moduleNames[] = new String[8];
	
	static{
		moduleNames[0] = "支付渠道维护";
		moduleNames[1] = "银行网关维护";
		moduleNames[2] = "数据库维护";
		moduleNames[3] = "系统参数配置";
		moduleNames[4] = "商户重要信息修改申请";
		moduleNames[5] = "手工调账请求";
		moduleNames[6] = "联机退款";
		moduleNames[7] = "退款经办";
	}
}
