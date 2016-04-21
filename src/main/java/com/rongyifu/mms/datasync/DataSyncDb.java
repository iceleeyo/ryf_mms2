package com.rongyifu.mms.datasync;

import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class DataSyncDb {

	public static String[] getMerFeeModel(PubDao dao, String mid, String gateId){
		String sql = "select calc_mode,gid,bk_fee_mode,trans_mode from fee_calc_mode where mid = " + Ryt.addQuotes(mid) + " and gate = " + gateId;
		Map<String, Object> map = dao.queryForMap(sql);
		String calcMode = (String) map.get("calc_mode");
		String gid = String.valueOf(map.get("gid"));
		String bkFeeMode = (String) map.get("bk_fee_mode");
		String transMode = String.valueOf(map.get("trans_mode"));
		
		return new String[]{calcMode, gid, bkFeeMode, transMode};
	}
	
	public static void recordLog(PubDao dao, String type, String fileName, boolean flag,Integer date){
		Integer dat=0==date?DateUtil.today():date;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into data_sync_log(type,filename,flag,sys_date,sys_time)");
		sql.append(" values(");
		sql.append(Ryt.addQuotes(type) + ",");
		sql.append(Ryt.addQuotes(fileName) + ",");
		sql.append(Ryt.addQuotes(flag ? "Y" : "N") + ",");
		sql.append(dat + ",");
		sql.append(DataSyncUtil.getNowTime());
		sql.append(")");
		dao.update(sql.toString());
	}
	
	public static void recordLog(PubDao dao, String type, String fileName, boolean flag){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into data_sync_log(type,filename,flag,sys_date,sys_time)");
		sql.append(" values(");
		sql.append(Ryt.addQuotes(type) + ",");
		sql.append(Ryt.addQuotes(fileName) + ",");
		sql.append(Ryt.addQuotes(flag ? "Y" : "N") + ",");
		sql.append(DateUtil.today() + ",");
		sql.append(DataSyncUtil.getNowTime());
		sql.append(")");
		
		dao.update(sql.toString());
	}
	
	public static String getAlarmFlag(SyncFileInfo info){
		PubDao dao =  new AdminZHDao();
		String type = info.getServicetype();
		String fileName = DateUtil.systemDate(-1) + info.getFileSuffix();
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
