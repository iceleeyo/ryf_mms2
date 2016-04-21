package com.rongyifu.mms.datasync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class DataSyncListener implements ServletContextListener {

	private DataSyncThread myThread;  
	  
    public void contextDestroyed(ServletContextEvent e) { 
        if (myThread != null && myThread.isInterrupted()) {  
            myThread.interrupt();  
        }  
    }  
  
    public void contextInitialized(ServletContextEvent e) { 
    	if (myThread == null) {
            myThread = new DataSyncThread();  
            myThread.start(); 
        }  
    }
    
}  
  
class DataSyncThread extends Thread {
	
	/**
     * <pre>是否需要启动数据同步功能
     * 目的：防止备上也启动此功能
     * 前提：生产服务一直在主的服务器上，因为只有主的上面才有POS/VAS数据文件</pre>
     * @return	是否启动标识
     */
    private boolean isStartService() {
		return Ryt.isStartService("检查数据同步服务");
	}
	
	public void run() {

		List<SyncFileInfo> syncFileList = getSyncFile();

		while (!this.isInterrupted()) {// 线程未中断执行循环
			try {
				if(!isStartService()){ // 每隔30分钟检查服务的有效性
					Thread.sleep(30 * 60 * 1000);
					continue;
				}
				
				LogUtil.printInfoLog("DataSyncThread", "run", "开始执行数据同步！");
				
				for (SyncFileInfo syncFile : syncFileList) {
					File f = new File(syncFile.getFilePath());
					File files[] = f.listFiles(); // 获取指定目录下所有的文件
					
					if (files != null && files.length > 0) {
						for (File item : files) { // 遍历指定目录
							
							if (item.isFile()) { // 判断是否为文件
								Thread.sleep(10 * 1000); // 读取到文件停顿10秒再处理
								
								String fileName = item.getName();
								int len = fileName.length();
								if (!Ryt.empty(fileName) && fileName.endsWith(syncFile.getFileSuffix()) && len == syncFile.getFileNameLen()) {
									LogUtil.printInfoLog("DataSyncThread", "run", "开始解析文件：" + syncFile.getFilePath() + item.getName());
									
									syncFile.setFileName(fileName);
									ParseSyncData psd = new ParseSyncData();
									psd.parseData(syncFile);
									
									LogUtil.printInfoLog("DataSyncThread", "run", "文件解析完成：" + syncFile.getFilePath() + item.getName());
									
									// 删除文件
									if(item.delete())
										LogUtil.printInfoLog("DataSyncThread", "run", "成功删除文件：" + syncFile.getFilePath() + item.getName());
									else
										LogUtil.printInfoLog("DataSyncThread", "run", "文件删除失败：" + syncFile.getFilePath() + item.getName());
								}
							}
						}
					}
					
					// 报警
					handleAlarm(syncFile);
				}
				
				LogUtil.printInfoLog("DataSyncThread", "run", "数据同步执行完成！");
				
				Thread.sleep(10 * 60 * 1000); // 每隔10分钟执行一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
    
    /**
     * 获取文件配置信息
     * @return
     */
    private List<SyncFileInfo> getSyncFile(){
    	List<SyncFileInfo> list = new ArrayList<SyncFileInfo>();
    	
    	//	POS配置信息
//    	SyncFileInfo pos = new SyncFileInfo();
//    	pos.setServicetype(DataSyncUtil.POS);
//    	pos.setFilePath(DataSyncUtil.POS_FILE_PATH);
//    	pos.setFileSuffix(DataSyncUtil.POS_FILE_SUFFIX);
//    	pos.setFileNameLen(16);
//    	pos.setAlarmTime("040"); // 报警时间(hhm)：4点0*分
//    	list.add(pos); //注释旧Pos同步
    	
        // POS配置信息，用于处理冲正数据
//    	SyncFileInfo pos1 = new SyncFileInfo();
//    	pos1.setServicetype(DataSyncUtil.POS);
//    	pos1.setFilePath(DataSyncUtil.POS_FILE_PATH);
//    	pos1.setFileSuffix(DataSyncUtil.POS_C_FILE_SUFFIX);  
//    	pos1.setFileNameLen(18);
//    	pos1.setAlarmTime(null);
//    	list.add(pos1);  //注释旧Pos同步
    	
    	//	VAS配置信息
    	SyncFileInfo vas = new SyncFileInfo();
    	vas.setServicetype(DataSyncUtil.VAS);
    	vas.setFilePath(DataSyncUtil.VAS_FILE_PATH);
    	vas.setFileSuffix(DataSyncUtil.VAS_FILE_SUFFIX);
    	vas.setFileNameLen(16);
    	vas.setAlarmTime("060"); // 报警时间(hhm)：6点0*分
    	list.add(vas);
    	
    	return list;
    }
    
    /**
     * 处理定时报警
     * @param info
     */
	private void handleAlarm(SyncFileInfo info) {
		if(Ryt.empty(info.getAlarmTime()))
			return;
		
		String nowTime = DataSyncUtil.getNowTime("HHmm");
		if (nowTime.startsWith(info.getAlarmTime())) {
			String fileName = DateUtil.systemDate(-1) + info.getFileSuffix();
			String flag = DataSyncDb.getAlarmFlag(info);
			if (Ryt.empty(flag))
				DataSyncUtil.sendMail("数据文件未提供", info.getServicetype(), fileName);
			else if (DataSyncUtil.FILE_READ_FAILURE.equals(flag.trim()))
				DataSyncUtil.sendMail("数据文件处理失败", info.getServicetype(), fileName);
			else if (DataSyncUtil.FILE_READ_SUCCESS.equals(flag.trim()))
				;
			else
				DataSyncUtil.sendMail("数据异常", info.getServicetype(), fileName);
		}
	}
    
}  