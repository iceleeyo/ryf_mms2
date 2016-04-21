package com.rongyifu.mms.modules.sysmanage.service;

import java.util.List;

import com.rongyifu.mms.bean.AreaCode;
import com.rongyifu.mms.modules.sysmanage.dao.AreaCodeDao;

public class AreaCodeService {
	private AreaCodeDao acDao = new AreaCodeDao();
	
	public List<AreaCode> cascadeQuery(Integer type,String provNo) throws Exception{
		return acDao.cascadeQuery(type, provNo);
	}

}
