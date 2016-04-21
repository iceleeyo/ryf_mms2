package com.rongyifu.mms.modules.sysmanage.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.AreaCode;
import com.rongyifu.mms.db.PubDao;

public class AreaCodeDao extends PubDao<AreaCode> {
	
	@SuppressWarnings("unchecked")
	public List<AreaCode> cascadeQuery(Integer type,String provNo) throws Exception{
		if(null == type ||(type != 0 && type != 1)){
			throw new Exception("错误的type值:"+type);
		}else if(type == 1 && StringUtils.isBlank(provNo)){
			throw new Exception("当type=1时,provNo不能为空");
		}
		String sql = genSql(type,provNo);
		return query(sql, AreaCode.class);
	}
	
	private String genSql(Integer type,String provNo){
		StringBuilder sql = new StringBuilder();
		if(type == 0){//查省份
			sql.append("SELECT DISTINCT(attr_prov_no),attr_prov_name FROM area_code ORDER BY attr_prov_no");
		}else{//根据省份编码查询查询区域编码信息
			sql.append("SELECT area_no,area_name,area_level,attr_prov_no,attr_prov_name FROM area_code WHERE area_level <= 2");
			sql.append(" AND attr_prov_no = ").append(provNo);
		}
		return sql.toString();
	}
}
