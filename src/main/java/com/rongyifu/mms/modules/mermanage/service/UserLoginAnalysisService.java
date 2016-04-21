package com.rongyifu.mms.modules.mermanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.mermanage.dao.UserLoginAnalysisDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 系统用户登录分析 
 *
 */
public class UserLoginAnalysisService {
	
	public Map<String, Object> statisticsUserLogin(Integer beginDate, Integer endDate){
		Map<String, Object> queryResult = new UserLoginAnalysisDao().statisticsUserLogin(beginDate, endDate);
		
		String bDate = DateUtil.formatDate(beginDate);
		String eDate = DateUtil.formatDate(endDate);
		queryResult.put("statisticsDate", bDate + " 至 " + eDate);
		return queryResult;
	}
	
	public FileTransfer doDownload(Integer beginDate, Integer endDate) {
		List<OperLog> logList = new UserLoginAnalysisDao().queryLoginDetail(beginDate, endDate);
		
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] title = {"商户号","商户简称","操作员号","操作员名","系统日期","系统时间","操作员IP","操作","操作结果"};
		list.add(title);
		
		for (int j = 0; j < logList.size(); j++) {
			OperLog log = logList.get(j);
			String[] strArr = { log.getMid(), 
					            log.getName(),
					            log.getOperId() + "", 
					            log.getOper_name(),
					            log.getSysDate() + "",
					            Ryt.getStringTime(log.getSysTime()), 
					            log.getOperIp(),
					            log.getAction(), 
					            log.getActionDesc()};
			list.add(strArr);
		}
		
		String filename = "login_log_" + DateUtil.getIntDateTime() + ".xlsx";
		try {
			return new DownloadFile().downloadXLSXFileBase(list, filename, "系统登录日志");
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "doDownload", "", e);
		}
		
		return null;
	}
}
