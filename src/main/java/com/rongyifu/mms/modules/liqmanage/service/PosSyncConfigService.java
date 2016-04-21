package com.rongyifu.mms.modules.liqmanage.service;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.datasync.posdz.PosDzSyncListener;
import com.rongyifu.mms.modules.liqmanage.dao.PosSyncConfDao;
import com.rongyifu.mms.utils.LogUtil;

/***
 * PosSync Config Service
 * @author admin
 *
 */
public class PosSyncConfigService {
	private final String par_name_time="TimeConf_ForPos";
	private final String par_name_isOpen="IsOpenConf_ForPos";
	private PosSyncConfDao dao=new PosSyncConfDao();
	
	/***
	 * 更新文件配置
	 * @param time
	 * @param isOpen
	 * @return
	 */
	public String modifyConfig(String time,String isOpen){
		String retMsg="配置修改成功";
		Map<String, String> params=new HashMap<String, String>();
		params.put(par_name_isOpen, isOpen);
		params.put(par_name_time, time);
		dao.modifyConfigParams(params);
		return retMsg;
	}
	
	/***
	 * load 文件内容
	 * @return
	 */
	public String[] loadConfig(){
		Map<String, String> params=dao.queryConfigParams();
		String time=params.get(par_name_time);
		String isOpen=params.get(par_name_isOpen);
		String hh=time.substring(0, 2);
		String MM=time.substring(2, 4);
		LogUtil.printInfoLog("PosSyncConfigService", "loadProperties", "time:"+hh+MM+"	isOpen:"+isOpen);
		String[] result=new String[]{hh,MM,isOpen};
		return result;
	}
	
	/****
	 * 手工上传成功 触发进程  开始读取文件
	 */
	public boolean beginRead(String fileSuffix){
		LogUtil.printInfoLog("PosSyncConfigService", "beginRead", "param_name:fileSuffix;param_value:"+fileSuffix);
		PosDzSyncListener listener=new PosDzSyncListener(fileSuffix);
		Thread thread=new Thread(listener);
		thread.start();
		return true;
	}

}
