package com.rongyifu.mms.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 *通过bizCode映射对应接口的业务处理类
 */
public final class BizMapper {
	private static Map<String,String> OUT_MAPPER;
	private static Map<String,String> IN_MAPPER;
	private static final String OUT_CONFIG_PATH = "/out.properties";
	private static final String IN_CONFIG_PATH = "/in.properties";
	private static Logger LOGGER = Logger.getLogger(BizMapper.class);
	private static Lock LOCK = new ReentrantLock();
	
	static{
		loadConfig();
	}
	
	private static void loadConfig(){
		try {
			LOCK.lock();
			Map<String,String> outMap = loadProp(OUT_CONFIG_PATH);
			OUT_MAPPER = Collections.unmodifiableMap(outMap);
			Map<String,String> inMap = loadProp(IN_CONFIG_PATH);
			IN_MAPPER = Collections.unmodifiableMap(inMap);
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}finally{
			LOCK.unlock();
		}
	}
	
	private static Map<String,String> loadProp(String path) throws IOException{
		Map<String,String> map = new HashMap<String,String>();
		InputStream is = null;
		try {
			Properties p = new Properties();
			is = BizMapper.class.getResourceAsStream(path);
			p.load(is);
			Set<Entry<Object, Object>> set = p.entrySet();
			for (Entry<Object, Object> entry : set) {
				map.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} finally {
			if(null != is){
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.info(e.getMessage(), e);
				}
			}
		}
		return map;
	}
	
	public static String getBizClassName(String code) throws Exception{
		Map<String,String> mapper = null;
		if(code.startsWith("TCO")){
			mapper = OUT_MAPPER;
		}else if(code.startsWith("TCI")){
			mapper = IN_MAPPER;
		}else{
			throw new Exception("错误的交易码");
		}
		try {
			LOCK.lock();
			return mapper.get(code);
		} finally{
			LOCK.unlock();
		}
	}
}
