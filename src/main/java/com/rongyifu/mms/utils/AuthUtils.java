package com.rongyifu.mms.utils;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 *根据当前登录用户的权限 对敏感账户信息进行处理
 */
public class AuthUtils {
	
	@SuppressWarnings("unchecked")
	public static <E> E handle(Object obj,Class<E> clz,Map<String,Strategy<?>> targetAndStrategy) throws Exception{
		E bean = null;
		if(clz.isAssignableFrom(obj.getClass())){
			bean = (E)obj;
		}else{
			throw new Exception("Object obj is not an instance of Class clz");
		}
		BeanWrapper bw = new BeanWrapperImpl(bean);
		Set<String> keySet = targetAndStrategy.keySet();
		for (String key : keySet) {
			@SuppressWarnings("rawtypes")
			Strategy s = targetAndStrategy.get(key);
			Object oldVal = bw.getPropertyValue(key);
			Object handledValue = s.handle(oldVal);
			bw.setPropertyValue(key, handledValue);
		}
		return bean;
	}
	
	public static interface Strategy<T>{
		public T handle(T t) throws Exception;
	}

}
