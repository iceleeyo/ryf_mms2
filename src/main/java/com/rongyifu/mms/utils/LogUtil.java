package com.rongyifu.mms.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class LogUtil {

	protected static Logger log4j = Logger.getLogger(LogUtil.class);
	
	/**
	 * 打印info级别日志
	 * @param className
	 * @param method
	 * @param info
	 * @deprecated
	 */
	public static void printInfoLog(String className, String method, String info){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className1 = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.info("[" + className1 + ":" + lineNumber + "] " + info + "\n");
	}
	
	/**
	 * 打印info级别日志
	 * @param info
	 */
	public static void printInfoLog(String info){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.info("[" + className + ":" + lineNumber + "] " + info + "\n");
	}
	
	/**
	 * 打印error级别日志
	 * @param className
	 * @param method
	 * @param info
	 * @deprecated
	 */
	public static void printErrorLog(String className, String method, String info){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className1 = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.error("[" + className1 + ":" + lineNumber + "] " + info + "\n");
	}
	
	/**
	 * 打印error级别日志
	 * @param info
	 */
	public static void printErrorLog(String info){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.error("[" + className + ":" + lineNumber + "] " + info + "\n");
	}
	
	/**
	 * 打印error级别日志，并打印堆栈日志
	 * @param className
	 * @param method
	 * @param info
	 * @deprecated
	 */
	public static void printErrorLog(String className, String method, String info, Throwable e){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className1 = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.error("[" + className1 + ":" + lineNumber + "] " + info, e);
	}
	
	/**
	 * 打印error级别日志，并打印堆栈日志
	 * @param info
	 */
	public static void printErrorLog(String info, Throwable e){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = (StackTraceElement) stackTrace[2];
		String className = caller.getClassName() + ".java";
		int lineNumber = caller.getLineNumber();
		log4j.error("[" + className + ":" + lineNumber + "] " + info, e);
	}
	
	/**
	 * 打印info级别日志
	 * @param className
	 * @param method
	 * @param info
	 * @param params
	 */
	@Deprecated
	public static void printInfoLog(String className, String method, String info, Map<String, String> params){
		try{
			log4j.info(organizateLogContent(className, method, info, params));
		} catch(Exception e){
			log4j.error("日志打印异常！", e);
		}
	}
	
	/**
	 * 打印info级别日志
	 * @param info
	 * @param params
	 */
	public static void printInfoLog(Map<String, ?> params){
		try{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StackTraceElement caller = (StackTraceElement) stackTrace[2];
			String className = caller.getClassName() + ".java";
			int lineNumber = caller.getLineNumber();
			log4j.info(organizateLogContent(className, lineNumber, "", params));
		} catch(Exception e){
			log4j.error("日志打印异常！", e);
		}
	}
	
	/**
	 * 打印info级别日志
	 * @param info
	 * @param params
	 */
	public static void printInfoLog(String info, Map<String, ?> params){
		try{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StackTraceElement caller = (StackTraceElement) stackTrace[2];
			String className = caller.getClassName() + ".java";
			int lineNumber = caller.getLineNumber();
			log4j.info(organizateLogContent(className, lineNumber, info, params));
		} catch(Exception e){
			log4j.error("日志打印异常！", e);
		}
	}
	
	/**
	 * 打印error级别日志
	 * @param className
	 * @param method
	 * @param info
	 * @param params
	 */
	@Deprecated
	public static void printErrorLog(String className, String method, String info, Map<String, String> params){
		try{
			log4j.error(organizateLogContent(className, method, info, params));
		} catch(Exception e){
			log4j.error("日志打印异常！", e);
		}
	}
	
	/**
	 * 打印error级别日志
	 * @param info
	 * @param params
	 */
	public static void printErrorLog(String info, Map<String, ?> params){
		try{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StackTraceElement caller = (StackTraceElement) stackTrace[2];
			String className = caller.getClassName() + ".java";
			int lineNumber = caller.getLineNumber();
			log4j.error(organizateLogContent(className, lineNumber, info, params));
		} catch(Exception e){
			log4j.error("日志打印异常！", e);
		}
	}
	
	/**
	 * 打印error级别日志，并打印堆栈日志
	 * @param className
	 * @param method
	 * @param info
	 * @param params
	 */
	@Deprecated
	public static void printErrorLog(String className, String method, String info, Map<String, String> params, Throwable e){
		try{
			log4j.error(organizateLogContent(className, method, info, params), e);
		} catch(Exception ex){
			log4j.error("日志打印异常！", ex);
		}
	}
	
	/**
	 * 打印error级别日志
	 * @param info
	 * @param params
	 */
	public static void printErrorLog(String info, Map<String, ?> params, Throwable e){
		try{
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StackTraceElement caller = (StackTraceElement) stackTrace[2];
			String className = caller.getClassName() + ".java";
			int lineNumber = caller.getLineNumber();
			log4j.error(organizateLogContent(className, lineNumber, info, params), e);
		} catch(Exception ex){
			log4j.error("日志打印异常！", ex);
		}
	}
	
	/**
	 * 组织日志内容
	 * @param className
	 * @param method
	 * @param info
	 * @param params
	 * @return
	 */
	private static String organizateLogContent(String className, String method, String info, Map<String, String> params){
		StringBuffer logContent = new StringBuffer();
		logContent.append("[" + className + "." + method + "] " + info);
		if(!params.isEmpty()){
			Iterator<String> iterator = params.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				String value = params.get(key);
				logContent.append("\n  " + fillString(key) + ": " + value);
			}
		}
		
		return logContent.toString();
	}
	
	/**
	 * 组织日志内容
	 * @param className
	 * @param lineNumber
	 * @param info
	 * @param params
	 * @return
	 */
	private static String organizateLogContent(String className, int lineNumber, String info, Map<String, ?> params){
		StringBuffer logContent = new StringBuffer();
		logContent.append("[" + className + ":" + lineNumber + "] " + info);
		if(!params.isEmpty()){
			Iterator<String> iterator = params.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				String value = String.valueOf(params.get(key));
				logContent.append("\n  " + fillString(key) + ": " + value);
			}
		}
		
		return logContent.toString();
	}
	
	/**
	 * 填充字符：不足30位的，在后面补充空格
	 * @param str
	 * @return
	 */
	private static String fillString(String str){
		if(str == null)
			str = "";
		
		int strLength = str.length();
		int fixedLength = 30; 
		for(int i = 0; i < fixedLength - strLength; i++){
			str += " ";
		}
		return str;
	}
	
	/**
	 * 创建Map
	 * @return
	 */
	public static Map<String, String> createParamsMap(){
		return new TreeMap<String, String>();
	}
	
	/**
	 * 创建Map
	 * @return
	 */
	public static Map<String, Object> createTreeMap(){
		return new TreeMap<String, Object>();
	}
}
