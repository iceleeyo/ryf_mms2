package com.rongyifu.mms.datasync.posdz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.datasync.DataSyncDb;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.datasync.FileData;
import com.rongyifu.mms.datasync.IDataSync;
import com.rongyifu.mms.datasync.ISyncDataProcessor;
import com.rongyifu.mms.datasync.PosSyncDataProcessor;
import com.rongyifu.mms.datasync.SyncFileInfo;
import com.rongyifu.mms.datasync.VasSyncDataProcessor;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.utils.LogUtil;

public class ParseSyncDataForPosDz {
	private List<String> os=null;
	/**
	 * 解析数据文件
	 * @param filePath
	 * @param fileName
	 * @param type
	 */
	@SuppressWarnings("rawtypes")
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
			int validRows = rowNum - emptyRow - 2;
			if(data.isSuccess() && data.getDataSum() != validRows){
				data.setSuccess(false);
				data.getErrorDatas().add("数据总行数跟实际的数据条数不一致！");
			}
			
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
	
	@SuppressWarnings("rawtypes")
	private ISyncDataProcessor getProcessObject(FileData data, String type, PubDao dao, String fileSuffix){
		ISyncDataProcessor processor = null;
		if (DataSyncUtil.POS.equals(type))
			processor =  new PosSyncDataProcessor(data, dao, fileSuffix);
		else if (DataSyncUtil.VAS.equals(type))
			processor =  new VasSyncDataProcessor(data, dao);
		else if(DataSyncUtil.PosDz.equals(type))
			processor = new PosDzSyncProcess(data, dao, fileSuffix); 

		return processor;
	}
	
	/**
	 * 文件成功读取后续处理
	 * @param data
	 * @param dao
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private boolean processSuccess(FileData data, String fileName, String type, PubDao dao) throws Exception{
		String flowNo = type + "_" + fileName;
		List<IDataSync> dataList = data.getDatas();
		if(dataList == null || dataList.size() == 0){
			LogUtil.printErrorLog("ParseSyncData", "processSuccess", type + "文件中数据行为空！");
			return false;
		}
		os=PosDzSyncDb.getTheDaysRecord(type, flowNo, dao);//获取当前批次数据库已经存在的订单号
		Integer existsCount=0;
		String errMsg = "";
		int dataRow = setStartLine(fileName,type);	// 判断有效数据开始行号 清算数据开始行号为第三行 其他为第二行
		int errCount = 0;	// 统计错误笔数
		StringBuffer alarmForAlreadyExists=null; //已存在的数据 
		for(IDataSync itemData : dataList){
			
			try{
				PosDzSyncData posDz=(PosDzSyncData)itemData;
				if(os != null&& !os.contains(posDz.getOrdId())){
					dao.batchSqlTransaction2(itemData.getSql(flowNo, type));
				}else{
					if(alarmForAlreadyExists==null){
						alarmForAlreadyExists=new StringBuffer();
					}
					existsCount++;
					alarmForAlreadyExists.append("["+dataRow+"]行数据已存在，该["+posDz.getMerId()+"]商户已存在订单号为："+posDz.getOrdId()+"的交易，\n");
					LogUtil.printInfoLog("ParseSyncDataForPosDz", "processSuccess", type+"同步数据已存在，exists_oid:"+posDz.getOrdId()+",该数据行:["+dataRow+"];");
				}
				
			}catch(Exception e){
				errCount++;
				errMsg += "数据行[" + dataRow + "]保存失败: " + e.getMessage() +"\r\n";
			}
			
			dataRow++;
		}
		
		if(alarmForAlreadyExists != null){
			//发送同步数据重复记录报警邮件
			sendMailForAlreadyExists(alarmForAlreadyExists.toString(),type,fileName);
		}
		
		if(Ryt.empty(errMsg)){
			LogUtil.printInfoLog("ParseSyncDataForPosDz", "processSuccess", type + "同步数据处理完成，共保存" + dataList.size() + "条记录！重复记录行数["+existsCount+"]!");
			return true;
		} else {
			String logInfo = type + "同步数据处理完成，其中成功保存" + (dataList.size() - errCount) + "条，异常数据" + errCount + "条！";
			LogUtil.printInfoLog("ParseSyncDataForPosDz", "processSuccess", logInfo + "\n" + errMsg);
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
	
	/***
	 * 数据开始行号
	 * 注意：PosDZ Pos对账清算数据 开始行号为第三行 （第一行为标题，第二为总记录数，第三行为正文数据行）
	 * @param fileName
	 * @param type
	 * @return
	 */
	private Integer setStartLine(String fileName, String type){

		if(fileName.contains(DataSyncUtil.PosDz_FILE_SUFFIX)||fileName.contains(DataSyncUtil.PosDz_cc_FILE_SUFFIX)){
			return 3;
		}
		return 2;
	}
	
	/**
	 * 发送报警邮件 
	 * 内容为同步数据时部分重复的数据  要以邮件报警发出来 
	 * @param content	邮件内容
	 * @param type		业务类型
	 * @param fileName  文件名
	 */
	private void sendMailForAlreadyExists(String content, String type, String fileName){
		String title = "[" + type + "]数据同步报警-" + DataSyncUtil.getNowTime("yyyyMMdd");
		try {
			String mailContent = type + "数据同步出现重复，请联系相关技术人员进行处理！\r\n";
			mailContent += "数据文件：" + fileName + "\r\n";
			mailContent += "错误原因如下：\r\n" + (Ryt.empty(content) ? "" : content.trim());
			LogUtil.printInfoLog("DataSyncUtil", "sendMail", title + "：\r\n" + mailContent);
			CallPayPocessor.sendMail(mailContent, title, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
