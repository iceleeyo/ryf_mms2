package com.rongyifu.mms.filter;

import java.lang.reflect.Method;


import org.apache.log4j.Logger;
import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;


import com.rongyifu.mms.db.PubDao;

public class DwrAjaxFilter implements AjaxFilter  {
	@SuppressWarnings("unchecked")
	PubDao pubDao=new PubDao();
	private Logger logger  =  Logger.getLogger(DwrAjaxFilter. class );
	
	@Override
	public Object doFilter(Object obj, Method method, Object[] params,
			AjaxFilterChain chain) throws Exception {
		String paramsStr="";
		for (int i = 0; i < params.length; i++) {
			paramsStr+=params[i]+",";
		}
		String logMassage="["+pubDao.getLoginUserName()+"]调用了"+obj.getClass().getName()+"的"
							+method.getName()+"方法，参数：("+paramsStr+")";
		logger.info(logMassage);
		return chain.doFilter(obj, method, params);
	}
}
