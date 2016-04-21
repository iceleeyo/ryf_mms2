package com.rongyifu.mms.modules.merchant;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统类型
 */
public enum SystemType{
	RYF_ADMIN(0,"融易付管理平台"),
	RYF_MER(1,"融易付商户后台"),
	POS(2,"POS系统"),
	VAS(3,"VAS系统");
	String desc;
	int code;
	SystemType(int code, String desc){
		this.desc = desc;
		this.code = code;
	}
	
	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public static SystemType getByCode(int code) throws Exception{
		SystemType[] types = values();
		for (SystemType systemType : types) {
			if(systemType.code == code) return systemType;
		}
		throw new Exception("未知的系统标识");
	}
	public static Map<Integer,String> getTypeMap(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		SystemType[] types = values();
		for (SystemType systemType : types) {
			map.put(systemType.code, systemType.desc);
		}
		return map;
	}
}