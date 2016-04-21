package com.rongyifu.mms.datasync.posdz;

import java.util.List;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.datasync.IDataSync;
import com.rongyifu.mms.datasync.SyncFileInfo;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PosDzSyncDb {
	
	/***
	 * 获取当天记录，判断是否已存在
	 * @param fileSuffix
	 * @param dao
	 * @return
	 */
	public static List<String> getTheDaysRecord(String type,String flowNo,PubDao dao){
		String mdate=DataSyncUtil.getIntervalDate(flowNo.split("_")[1].replace("-", ""), -1);
		StringBuffer sql=new StringBuffer("select oid from hlog where mdate=");
		sql.append(mdate);
		sql.append(" and p9=").append(Ryt.addQuotes(flowNo));
		return dao.queryForStringList(sql.toString());
	}
	
	public static String getAlarmFlag(SyncFileInfo info){
		PubDao dao =  new AdminZHDao();
		String type = info.getServicetype();
		String fileName = DataSyncUtil.formatDate(DateUtil.systemDate(-1)+"") + info.getFileSuffix();
		String sql = "select flag from data_sync_log where type= '" + type + "' and filename='" + fileName + "' order by sys_time desc limit 1";
		String flag = null;
		try{
			flag = dao.queryForStringThrowException(sql);
		}catch(Exception e){
			flag = null;
			LogUtil.printErrorLog("DataSyncDb", "getAlarmFlag", "查询数据同步日志发生异常：" + e.getMessage());
		}
		
		return flag;
	}
	
}
