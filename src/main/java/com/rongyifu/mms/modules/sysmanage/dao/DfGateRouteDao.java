package com.rongyifu.mms.modules.sysmanage.dao;

import java.sql.Types;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.DfGateRoute;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.modules.sysmanage.service.DfGateRouteService;
import com.rongyifu.mms.utils.CurrentPage;

public class DfGateRouteDao extends PubDao<DfGateRoute> {
	private Logger logger = Logger.getLogger(getClass());
	
	public int delById(Integer id){
		String sql = "delete from df_gate_route where id = ?";
		int count = update(sql, new Object[]{id}, new int[]{Types.INTEGER});
		saveOperLog("删除代付网关", count == 1?"删除成功":"删除失败");
		return count;
	}
	
	public int update(DfGateRoute config){
		String sql = "update df_gate_route set gid = ? where id = ?";
		Object[] args = new Object[]{config.getGid(),config.getId()};
		int count = update(sql, args, new int[]{Types.INTEGER,Types.INTEGER});
		saveOperLog("更新代付网关", count ==1?"更新成功":"更新失败");
		return count;
	}
	
	public int add(DfGateRoute config){
		String sql = "INSERT INTO df_gate_route(type,gate_id,gid,limit_type,config_type) VALUES (?,?,?,?,?)";
		Object[] args = new Object[]{config.getType(),config.getGateId(),config.getGid(),config.getLimitType(),config.getConfigType()};
		int count = update(sql, args, new int[]{Types.TINYINT,Types.INTEGER,Types.INTEGER,Types.TINYINT,Types.TINYINT});
		saveOperLog("新增代付网关", count == 1?"新增成功":"新增失败");
		return count;
	}
	
	public CurrentPage<DfGateRoute> query(DfGateRoute config,Integer pageNo){
		StringBuilder sql = new StringBuilder("SELECT * FROM df_gate_route WHERE 1 = 1");
		if(null != config){
			//配置类型
			if(config.getConfigType() !=null){
				sql.append(" AND config_type = ").append(config.getConfigType());
			}
			//代付类型
			if(config.getType() != null){
				sql.append(" AND type = ").append(config.getType());
			}
			//渠道
			if(config.getGid() != null){
				sql.append(" AND gid = ").append(config.getGid());
			}
		}
		sql.append(" ORDER By id DESC");
		logger.info(sql.toString());
		return queryForCurrPage(sql.toString(), pageNo, DfGateRoute.class);
	}
	
	public DfGateRoute queryById(Integer id){
		String sql = "SELECT * FROM df_gate_route WHERE id=?";
		return queryForObject(sql, new Object[]{id}, DfGateRoute.class);
	}
}
