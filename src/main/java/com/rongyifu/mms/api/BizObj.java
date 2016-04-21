package com.rongyifu.mms.api;

import java.util.Map;

/**
 * 校验请求参数 调用业务代码 并把结果封装成json对象 
 * json数据格式要求key必须为字符串 所以当转换key不为字符串的map需要做额外处理
 */
public interface BizObj {
	
	Object doBiz(Map<String,String> params) throws Exception;
}
