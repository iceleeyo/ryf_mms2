package com.rongyifu.mms.modules.sysmanage.service;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.DfGateRoute;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.modules.sysmanage.dao.DfGateRouteDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.RYFMapUtil;

public class DfGateRouteService {
	private DfGateRouteDao dao = new DfGateRouteDao();
	public static final short CUSTOM_CONFIG = 1;//自定义配置
	public static final short DEFAULT_CONFIG = 0;//默认配置
	
	// 刷新ets
	public String refresh(){
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "route_rule");
		return EWPService.refreshEwpETS(p) ? "刷新成功":"刷新失败";
	
	}
	
	/**
	* @title: query
	* @description: 查询
	* @author li.zhenxing
	* @date 2015-1-6
	* @param config
	* @return
	*/ 
	public CurrentPage<DfGateRoute> query(DfGateRoute config,Integer pageNo){
		return dao.query(config,pageNo);
	}
	
	public DfGateRoute queryById(Integer id){
		if(id == null){
			return null;
		}
		return dao.queryById(id);
	}
	
	public int add(DfGateRoute config){
		config.setConfigType(CUSTOM_CONFIG);
		return dao.add(config);
	}
	
	public int doUpdate(DfGateRoute config){
		return dao.update(config);
	}
	
	public int delById(Integer id){
		return dao.delById(id);
	}
	
}
