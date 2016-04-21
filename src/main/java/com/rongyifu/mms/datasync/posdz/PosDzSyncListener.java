package com.rongyifu.mms.datasync.posdz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.datasync.SyncFileInfo;
import com.rongyifu.mms.modules.liqmanage.service.PosSyncConfigService;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/***
 * Pos 清算对账文件同步 服务类
 * @author 
 *
 */
public class PosDzSyncListener implements Runnable  {
	private static Integer waitTime=300*1000; //等待5分钟继续执行300*1000
	private String fileSuffix;
	
	
	public PosDzSyncListener() {
		super();
	}

	public PosDzSyncListener(String fileSuffix) {
		super();
		this.fileSuffix = fileSuffix;
	}

	/***
	 * 定时任务 自动运行
	 */
	public void process(){
		try{
			
			if(!Ryt.isStartService("检查清算对账数据同步服务")){
				return;
			}
			
			if(isExec()){
				return;
			}
			
			loopReadFile(getSyncFile());
		}catch (Exception e) {
			LogUtil.printErrorLog("PosDzSyncListener", "run", "exception:"+e.getMessage(), e);
		}
	}
	
	/****
	 * 手工上传 起线程调用
	 */
	public void run(){
		try{
			List<SyncFileInfo> nls=null;
			for (SyncFileInfo syncFile : getSyncFile()) {
				if(fileSuffix.equals(syncFile.getFileSuffix())){
					LogUtil.printInfoLog("PosDzSyncListenre", "run", "fileSuffix:"+fileSuffix);
					nls=new ArrayList<SyncFileInfo>();
					nls.add(syncFile);
					loopReadFile(nls);
				}else{
					continue;
				}
			}
			
		}catch (Exception e) {
			LogUtil.printErrorLog("PosDzSyncListener", "run", "exception:"+e.getMessage(), e);
		}
		
	}
	
	private void loopReadFile(List<SyncFileInfo> sfL){
		List<String> fl=new ArrayList<String>();
		String path=DataSyncUtil.PosDz_FILE_PATH;
		try {
			//检索文件
			isExistsFile(1,fl,path);
			
			//文件不存在 报警
			if(fl.size()==0){
				LogUtil.printInfoLog("PosDzSyncListener", "run", "三次遍历目录，文件仍然不存在，请手工上传");
				//每隔5分钟遍历目录仍无同步文件，跳出循环。。
				// 报警 
				handleAlarmForPosDz(sfL);
				return;
			}
			for (String fileName : fl) {
				SyncFileInfo sync=new SyncFileInfo();
				sync.setFilePath(DataSyncUtil.PosDz_FILE_PATH);
				sync.setAlarmTime("now");
				sync.setServicetype(DataSyncUtil.PosDz);
				if(fileName.contains(DataSyncUtil.PosDz_FILE_SUFFIX)){
					sync.setFileSuffix(DataSyncUtil.PosDz_FILE_SUFFIX);
					sync.setFileNameLen(22);
					sync.setFileName(fileName);
				}else if(fileName.contains(DataSyncUtil.PosDz_cc_FILE_SUFFIX)){
					sync.setFileSuffix(DataSyncUtil.PosDz_cc_FILE_SUFFIX);
					sync.setFileNameLen(22);
					sync.setFileName(fileName);
				}
				keyCode(sync);
			}
			handleAlarmForPosDz(sfL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/***
	 * 调用解析文件模块
	 * @param sync
	 */
	private void keyCode(SyncFileInfo sync){
		String fileName=sync.getFileName();
		File file=new File(sync.getFilePath()+"/"+fileName);
		LogUtil.printInfoLog("PosDzSyncListener", "keyCode", "开始解析文件：" + DataSyncUtil.PosDz_FILE_PATH + fileName);
		
		ParseSyncDataForPosDz psd = new ParseSyncDataForPosDz();
		psd.parseData(sync);
		
		LogUtil.printInfoLog("PosDzSyncListener", "keyCode", "文件解析完成：" + DataSyncUtil.PosDz_FILE_PATH + fileName);
		
		// 删除文件
		if(file.delete())
			LogUtil.printInfoLog("PosDzSyncListener", "keyCode", "成功删除文件：" + DataSyncUtil.PosDz_FILE_PATH + fileName);
		else
			LogUtil.printInfoLog("PosDzSyncListener", "keyCode", "文件删除失败：" + DataSyncUtil.PosDz_FILE_PATH + fileName);
	}
	
	private List<SyncFileInfo> getSyncFile(){
		List<SyncFileInfo> syncFiles=new ArrayList<SyncFileInfo>();
		SyncFileInfo pos_dz=new SyncFileInfo();
		pos_dz.setFilePath(DataSyncUtil.PosDz_FILE_PATH);
		pos_dz.setFileSuffix(DataSyncUtil.PosDz_FILE_SUFFIX);
		pos_dz.setFileNameLen(22);
		pos_dz.setAlarmTime("now");
		pos_dz.setServicetype(DataSyncUtil.PosDz);
		syncFiles.add(pos_dz);
		
		SyncFileInfo pos_cc=new SyncFileInfo();
		pos_cc.setFilePath(DataSyncUtil.PosDz_FILE_PATH);
		pos_cc.setFileSuffix(DataSyncUtil.PosDz_cc_FILE_SUFFIX);
		pos_cc.setFileNameLen(22);
		pos_cc.setAlarmTime("now");
		pos_cc.setServicetype(DataSyncUtil.PosDz);
		syncFiles.add(pos_cc);
		return syncFiles;
	}
	
	/***
	 * 判断是否发送邮件
	 * @param info
	 */
	private void handleAlarmForPosDz(List<SyncFileInfo> info){
		for (SyncFileInfo syncFileInfo : info) {
			handleAlarmForPosDz(syncFileInfo);
		}
	}
	
	/**
	 * 判断是否发送邮件
	 * @param info
	 * @param isAll
	 */
	private void handleAlarmForPosDz(SyncFileInfo info){
		if(Ryt.empty(info.getAlarmTime())){
			return;
		}

		//报警邮件
		String fileName = DataSyncUtil.formatDate(DateUtil.systemDate(-1)+"") + info.getFileSuffix();
		String flag = PosDzSyncDb.getAlarmFlag(info);
		if (Ryt.empty(flag))
			DataSyncUtil.sendMail("数据文件未提供", info.getServicetype(), fileName);
		else if (DataSyncUtil.FILE_READ_FAILURE.equals(flag.trim()))
			DataSyncUtil.sendMail("数据文件处理失败", info.getServicetype(), fileName);
		else if (DataSyncUtil.FILE_READ_SUCCESS.equals(flag.trim()))
			;
		else
			DataSyncUtil.sendMail("数据异常", info.getServicetype(), fileName);
	}

	
	
	
	
	/***
	 * 判断是否开启 是否到了预设时间
	 * preTime 预设执行时间
	 * isOpen 预设开启状态 ：0 开启 、1 关闭
	 * @return
	 */
	protected  boolean isExec(){
		PosSyncConfigService configService=new PosSyncConfigService();
		String[] result=configService.loadConfig();
		String preTime=result[0]+result[1];
		String isOpen=result[2];
		String nowTime=DateUtil.getTime().replace(":", "").substring(0, 4);
		LogUtil.printInfoLog("PosDzSyncListener", "isExec", "预设执行时间："+preTime+"	预设执行状态："+isOpen);
		//开启状态为1 不执行
		if("1".equals(isOpen)){
			LogUtil.printInfoLog("PosDzSyncListener", "isExec", "执行状态未开启");
			return true;
		}
		
		//预设执行时间不符 不执行
		if(!nowTime.equals(preTime)){
			LogUtil.printInfoLog("PosDzSyncListener", "isExec", "未到预设的执行时间");
			return true;
		}
		return false;
		
	}
	
	/***
	 * 判断文件是否存在，存在则把文件名填充到fl中
	 * @param i
	 * @param fl
	 * @param path
	 * @throws InterruptedException
	 */
	private  void isExistsFile(int i,List<String> fl,String path) throws InterruptedException{
		if(i>3)return;
		LogUtil.printInfoLog("PosDzSyncListener", "run", "开始执行清算对账数据同步_"+i);
		File f=new File(path);
		if(f.exists()&&f.isDirectory()){
			File[] fs=f.listFiles();
			if(fs!=null&&fs.length>0){
				for (File file : fs) {
					if(file.isFile()&&checkFileName(file.getName(),DataSyncUtil.PosDz_FILE_SUFFIX))fl.add(file.getName());
					else if(file.isFile()&&checkFileName(file.getName(),DataSyncUtil.PosDz_cc_FILE_SUFFIX))fl.add(file.getName());
				}
				if(fl.size()!=0)return;
			}
		}
		Thread.sleep(waitTime);
		isExistsFile(i+1,fl,path);
	}
	
	/**
	 * 校验fileName 格式：yyyy-MM-dd_POS_DZ.txt | yyyy-MM-dd_POS_CC.txt
	 * @param fileName
	 * @return
	 */
	private boolean checkFileName(String fileName,String fileSuffix){
		if(!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}"+fileSuffix, fileName)){
			return false;
		}
		return true;
	}
	
}
