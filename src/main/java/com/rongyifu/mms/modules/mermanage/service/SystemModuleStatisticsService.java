package com.rongyifu.mms.modules.mermanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.mermanage.dao.SystemModuleStatisticsDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 系统重要模块统计 
 *
 */
public class SystemModuleStatisticsService {
	
	/**
	 * 查询 
	 * @param beginDate
	 * @param endDate
	 * @param moduleId
	 * @return
	 */
	public List<Map<String, Object>> doQuery(Integer beginDate, Integer endDate, Integer moduleId){
		return getQueryResult(beginDate, endDate, moduleId);
	}
	
	/**
	 * 下载
	 * @param beginDate
	 * @param endDate
	 * @param moduleId
	 * @return
	 */
	public FileTransfer doDownload(Integer beginDate, Integer endDate, Integer moduleId) {
		List<Map<String, Object>> dataList = getQueryResult(beginDate, endDate, moduleId);
		
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] title = {"序号","模块","最后一次操作时间","操作次数"};
		list.add(title);
		
		for (Map<String, Object> item : dataList) {
			String[] strArr = { String.valueOf(item.get("serialNumber")), 
					            String.valueOf(item.get("action")),
					            String.valueOf(item.get("last_oper_time")),
					            String.valueOf(item.get("oper_num"))};
			list.add(strArr);
		}
		
		String filename = "system_module_statistics_" + DateUtil.getIntDateTime() + ".xlsx";
		try {
			return new DownloadFile().downloadXLSXFileBase(list, filename, "系统重要模块统计");
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doDownload", "", e);
		}
		
		return null;
	}
	
	/**
	 * 获取查询结果
	 * @param beginDate
	 * @param endDate
	 * @param moduleId
	 * @return
	 */
	private List<Map<String, Object>> getQueryResult(Integer beginDate, Integer endDate, Integer moduleId){
		// 查询数据
		List<Map<String, Object>> queryResult = new SystemModuleStatisticsDao().doQuery(beginDate, endDate, moduleId);
		
		// 初始化数据
		List<Map<String, Object>> dataList = initData(moduleId);
		
		// 用查询结果覆盖初始化数据
		for(Map<String, Object> itemData : dataList){
			String moduleName = (String) itemData.get("action");
			
			for(Map<String, Object> item : queryResult){
				String action = (String) item.get("action");
				if(moduleName.equals(action)){
					fillData(itemData, 
							 String.valueOf(itemData.get("serialNumber")),
							 action,
							 String.valueOf(item.get("last_oper_time")),
							 String.valueOf(item.get("oper_num")));
					break;
				}
			}
		}
		return dataList;
	}
	
	/**
	 * 初始化数据集
	 * @param moduleId
	 * @return
	 */
	private List<Map<String, Object>> initData(Integer moduleId){
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if(moduleId <= 0 || moduleId > 8){
			int i = 0;
			for(String moduleName : SystemModuleStatisticsDao.moduleNames){
				dataList.add(fillData(String.valueOf(++i), moduleName));
			}
		} else {
			dataList.add(fillData(String.valueOf(1), SystemModuleStatisticsDao.moduleNames[moduleId - 1]));
		}
		
		return dataList;
	}
	
	/**
	 * 填充数据
	 * @param serialNumber
	 * @param action
	 * @return
	 */
	private Map<String, Object> fillData(String serialNumber, String action){
		return fillData(null, serialNumber, action, "", "0");
	}
	
	/**
	 * 填充数据
	 * @param data
	 * @param serialNumber
	 * @param action
	 * @param lastOperTime
	 * @param operNum
	 * @return
	 */
	private Map<String, Object> fillData(Map<String, Object> data, String serialNumber, String action, String lastOperTime, String operNum){
		String lastOperTime2 = "";
		if(!Ryt.empty(lastOperTime)){
			String dt[] = lastOperTime.split("_");
			String date = DateUtil.formatDate(Integer.parseInt(dt[0]));
			String time = Ryt.getStringTime(Integer.parseInt(dt[1]));
			lastOperTime2 = date + " " + time;
		}
		
		if(data == null)
			data = new HashMap<String, Object>();
		data.put("serialNumber", serialNumber);
		data.put("action", action);
		data.put("last_oper_time", lastOperTime2);
		data.put("oper_num", operNum);
		
		return data;
	}
}
