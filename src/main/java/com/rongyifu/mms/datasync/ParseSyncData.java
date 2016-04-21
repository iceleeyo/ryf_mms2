package com.rongyifu.mms.datasync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.LogUtil;

public class ParseSyncData {
	
	/**
	 * 解析数据文件
	 * @param filePath
	 * @param fileName
	 * @param type
	 */
	public void parseData(SyncFileInfo syncFile) {
		String filePath = syncFile.getFilePath();
		String fileName = syncFile.getFileName();
		String type = syncFile.getServicetype();
		String fileSuffix = syncFile.getFileSuffix();
		
		File f = null;
		BufferedReader input = null;

		try {
			f = new File(filePath + fileName);
			if(f.length() == 0){
				LogUtil.printErrorLog("ParseSyncData", "parseData", "[" + filePath + fileName + "]空文件！");
				return;
			}
			
			input = new BufferedReader(new FileReader(f));
			
			FileData data = new FileData();
			PubDao dao =  new AdminZHDao();
			ISyncDataProcessor processor = getProcessObject(data, type, dao, fileSuffix);
			if(processor == null){
				LogUtil.printErrorLog("ParseSyncData", "parseData", "初始化处理类失败，请检查配置是否正确！");
				return;
			}
			
			int rowNum = 0;
			int emptyRow = 0;
			String rowData = null;
			while ((rowData = input.readLine()) != null) {
				rowNum++;
				
				if(!Ryt.empty(rowData) )
					processor.process(rowNum, rowData);
				else					
					emptyRow++;
			}
			
			// 数据条数校验：有效数据 = 统计的总行数 - 空行数 - 首行
//			int validRows = rowNum - emptyRow - 1;
//			if(data.isSuccess() && data.getDataSum() != validRows){
//				data.setSuccess(false);
//				data.getErrorDatas().add("首行数据总数跟实际的数据条数不一致！");
//			}
			
			boolean dataSaveFlag = false;
			if(data.isSuccess()){
				dataSaveFlag = processSuccess(data, fileName, type, dao);
			} else {
				processFail(data, type, fileName);
			}
			
			// 记录日志
			DataSyncDb.recordLog(dao, type, fileName, data.isSuccess() && dataSaveFlag);
			
			LogUtil.printInfoLog("ParseSyncData", "parseData", type + "数据[" + fileName + "]同步完成！");
		} catch (Exception e) {
			DataSyncUtil.sendMail("文件处理发生异常：" + e.getMessage(), type, fileName);
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取处理类
	 * @param data
	 * @param type
	 * @param dao
	 * @return
	 */
	private ISyncDataProcessor getProcessObject(FileData data, String type, PubDao dao, String fileSuffix){
		ISyncDataProcessor processor = null;
		if (DataSyncUtil.POS.equals(type))
			processor =  new PosSyncDataProcessor(data, dao, fileSuffix);
		else if (DataSyncUtil.VAS.equals(type))
			processor =  new VasSyncDataProcessor(data, dao);

		return processor;
	}
	
	/**
	 * 文件成功读取后续处理
	 * @param data
	 * @param dao
	 * @throws Exception 
	 */
	private boolean processSuccess(FileData data, String fileName, String type, PubDao dao) throws Exception{
		String flowNo = type + "_" + fileName;
		List<IDataSync> dataList = data.getDatas();
		if(dataList == null || dataList.size() == 0){
			LogUtil.printErrorLog("ParseSyncData", "processSuccess", type + "文件中数据行为空！");
			return false;
		}
		
		String errMsg = "";
		int dataRow = 2;	// 有效数据从第二行开始
		int errCount = 0;	// 统计错误笔数
		for(IDataSync itemData : dataList){
			
			try{
				dao.batchSqlTransaction2(itemData.getSql(flowNo, type));
			}catch(Exception e){
				errCount++;
				errMsg += "数据行[" + dataRow + "]保存失败: " + e.getMessage() +"\r\n";
				
//				LogUtil.printErrorLog("ParseSyncData", "processSuccess", type + "数据同步发生异常：" + errMsg);
			}
			
			dataRow++;
		}
		
		if(Ryt.empty(errMsg)){
			LogUtil.printInfoLog("ParseSyncData", "processSuccess", type + "同步数据处理完成，共保存" + dataList.size() + "条记录！");
			return true;
		} else {
			String logInfo = type + "同步数据处理完成，其中成功保存" + (dataList.size() - errCount) + "条，异常数据" + errCount + "条！";
			LogUtil.printInfoLog("ParseSyncData", "processSuccess", logInfo+"\n"+errMsg);
			DataSyncUtil.sendMail(logInfo, type, fileName);
			return false;
		}
	}
	
	/**
	 * 文件读取失败后续处理
	 * @param data
	 * @throws Exception
	 */
	private void processFail(FileData data, String type, String fileName) throws Exception{
		StringBuffer content = new StringBuffer();
		for(String error : data.getErrorDatas())
			content.append(error + "\r\n");
		
		DataSyncUtil.sendMail(content.toString(), type, fileName);
	}
	
}
