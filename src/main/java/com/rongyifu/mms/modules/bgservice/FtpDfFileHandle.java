package com.rongyifu.mms.modules.bgservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rongyifu.mms.utils.ParamUtil;

/***
 * FTP 代付  文件处理类。
 * @author shdy
 *
 */
public class FtpDfFileHandle {
	private String fileResultPath=ParamUtil.getProperties("ftpResultPath", "df_param.properties");
	private Logger log=Logger.getLogger(FtpDfFileHandle.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		FtpDfFileHandle fileHandle=new FtpDfFileHandle();
		fileHandle.readFile("D:/file/00000076420140328002.txt");
		
	}
	
	/***
	 * FTP代付 读取交易文件
	 * @param filePath
	 * @return
	 */
	public List<String> readFile(String filePath){
		FileReader fileReader=null;
		BufferedReader bufferedReader=null;
		List<String> result=null;
		File file =null;
		try {
			file=new File(filePath);
			fileReader=new FileReader(filePath);
			bufferedReader =new BufferedReader(fileReader);
			String temp=null;
			result=new ArrayList<String>();
			while((temp=bufferedReader.readLine())!=null){
				result.add(temp);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
				try {
					if(bufferedReader!=null)bufferedReader.close();
					if(fileReader!=null)fileReader.close();
					file.delete();
					log.info("交易文件已删除["+filePath+"]");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
		}
	}
	
	/***
	 * 生产FTP代付结果文件
	 * @param fileName
	 * @param result
	 */
	public void createFile(String fileName,List<String> result){
		File tmpFile=null;
		File realFile=null;
		FileWriter fileWriter=null;
		BufferedWriter bufferedWriter=null;
		String tmpName=fileName+".txt.d";
		String realName=fileName+".txt";
		try {
			if(fileResultPath==null){
				log.error("结果文件生产路径未配置！");
				return;
			}
			tmpFile=new File(fileResultPath+tmpName);
			tmpFile.createNewFile();
			realFile=new File(fileResultPath+realName);
			fileWriter=new FileWriter(tmpFile);
			bufferedWriter=new BufferedWriter(fileWriter);
			for (String string : result) {
				bufferedWriter.write(string+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(bufferedWriter!=null)bufferedWriter.close();
				if(fileWriter!=null)fileWriter.close();
				tmpFile.renameTo(realFile);
				tmpFile.deleteOnExit();
				log.info("临时文件已删除，生产正式文件["+fileResultPath+realName+"]!");
			} catch (Exception e2) {
			e2.printStackTrace();
			}
		}
	}
	
}
