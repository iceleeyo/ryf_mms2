package com.rongyifu.mms.bank.b2e;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.FtpUtil;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 盛京明细查询处理模块
 *
 */
public class SjBankMXCX {
	private B2EGate gate=null;
	private Integer startDate=null;
	private Integer endDate=null;
	private String fileFlag=null;//返回数据方式标识：0为报文返回，1为文件返回
	private String serial_record=null;//返回报文时的数据域
	private Integer record_num=null;//返回记录数
	private Integer field_num=null;//返回字段数
	private String fileName=null;//返回文件名 
	private String respInfo="";//返回中文描述 默认""(空白)
	private String ip=null;//FTP服务器IP
	private Integer port=21;//FTP服务器端口
	private String userName="sjftp";//FTP服务登陆用户名
	private String pwd="sjftp1qaz";//FTP服务登陆密码
	private String remotePath="C:/Program Files/CMS/SJBCTransmitter/Temp"; //远程服务器目录
	private String localPath="D:/opt/data/temp";  //本地目录D:\opt\data\temp
	private List<String> linesList=null;//文件内容列表
	private AdminZHDao dao=new AdminZHDao();
	
	
	
	
	public SjBankMXCX(Integer startDate, Integer endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.gate = dao.getOneB2EGate(40006);;
		this.getPath();//获取本地路径和远程路径
	}


	/***
	 * 返回明细信息
	 * @return
	 */
	public List<String>  querySjBankDetail(){
		Map<String, String> map=new HashMap<String, String>();
		linesList=new ArrayList<String>();
		try {
			SjBankXML sjb=new SjBankXML();
			TrOrders os=new TrOrders();
			String reqData=sjb.genSubmitXmlForQueryDetail(100000, os, gate, startDate, endDate);
			LogUtil.printInfoLog("SjBankMXCX", "querySjBankDetail", "reqData:"+reqData);
			String respData=sjBankSocket(reqData);
			LogUtil.printInfoLog("SjBankMXCX", "querySjBankDetail", "respData:"+respData);
			parseXml(respData);
			
			map.put("fileName", fileName);
			map.put("fileFlag", fileFlag);
			map.put("serial_record", serial_record);
			map.put("record_num", String.valueOf(record_num));
			map.put("field_num", String.valueOf(field_num));
			
			if(record_num!=null && record_num > 0){
				if("1".equals(fileFlag)){ //文件返回方式
					Thread.sleep(60*1000);//等待1分钟 等待文件生成
					if(!downFileFromHost()){
						LogUtil.printErrorLog("SjBankMXCX", "querySjBankDetail", "FTP下载文件失败！从前置拉取文件失败");
						respInfo="FTP下载文件失败！从前置拉取文件失败";
						map.put("respInfo", respInfo);
						return linesList;
					}
					linesList=readFile(localPath+"/"+fileName);
				}else if(record_num != null && record_num>0){
					String data[] = serial_record.split("\\|");
					for(int row = 0; row <= record_num; row++){
						String rowStr = "";
						for(int col = 0; col < field_num; col++){
							rowStr += data[row * field_num + col] + "|";
						}
						
						linesList.add(rowStr);
					}
				}
				linesList.remove(0);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("SjBankMXCX", "querySjBankDetail", e.getMessage(), map, e);
		}
		LogUtil.printInfoLog("SjBankMXCX", "querySjBankDetail", "params", map);
		return linesList;
	}
	
	
	/***
	 * Socket 请求
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	private String sjBankSocket(String reqData) throws Exception{
		String ncUrl = this.gate.getNcUrl();
		String[] url=ncUrl.split(":");
		String host=url[0];
		ip=host;
		Integer port=Integer.parseInt(url[1]);
		Socket socket = new Socket();
		InetSocketAddress endpoint = new InetSocketAddress(host, port);
		socket.setSoTimeout(60000);
		socket.connect(endpoint);
		socket.setSendBufferSize(1024 * 4);
		socket.setReceiveBufferSize(1024 *4);
		FileOutputStream out = (FileOutputStream) socket.getOutputStream();
		String encode = "GBK";
		byte[] b2 = reqData.getBytes(encode);
		for (byte c : b2) {
			out.write(c);
		}
		out.flush();
		FileInputStream read = (FileInputStream) socket.getInputStream();
		byte[] header = new byte[10];
		while ((read.read(header)) > 0) {
			break;
		}
		Integer bodyLen = Integer.parseInt(new String(header).trim());
		byte[] body = new byte[bodyLen]; // 设置body长度
		Integer size = 0;
		int i = 0;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		while ((i = read.read(body)) > 0) {
			if (size >= bodyLen) {
				break;
			}
			size += i;
			byteOut.write(body, 0, i);
		}
		out.close();
		read.close();
		socket.close();
		return new String(byteOut.toByteArray(), "gbk").trim().substring(2);
	}
	
	private void parseXml(String xml){
		if(xml==null||xml.length()==0){
			return;
		}
		Document document=null;
		String trCode=null;
		String retCode=null;//是否成功返回标识
		try {
			document=DocumentHelper.parseText(xml);
			Element sjBankRoot=document.getRootElement();
			Element head=sjBankRoot.element("head");
			Element body=sjBankRoot.element("body");
			if(head==null || body==null){
				LogUtil.printErrorLog("SjBankXml", "parseXml", "返回报文错误："+xml);
				return;
			}
			trCode=head.elementText("tr_code");
			retCode=head.elementText("ret_code");
			if("200110".equals(trCode)){
				String sucFlag=head.elementText("succ_flag");
				if ("0000".equals(retCode) && "0".equals(sucFlag)) {
					// 成功查询
					fileFlag = head.elementText("file_flag");
					record_num = Integer.parseInt(body.elementText("record_num"));
					field_num = Integer.parseInt(body.elementText("field_num"));
					if ("1".equals(fileFlag)) {// 返回文件
						fileName = body.elementText("file_name");
					} else {
						serial_record = body.elementText("serial_record");
					}
				}				
			}
			
		} catch (DocumentException e) {
			LogUtil.printErrorLog("SjBankMXCX", "parseXML", xml, e);
		}
		
		return;
	}
	
	/***
	 * FTP下载文件
	 * @throws Exception
	 */
	private boolean  downFileFromHost() throws Exception{
		File file=new File(localPath);
		if(!file.exists()){
			file.mkdirs();
		}
		FtpUtil ftpUtil=new FtpUtil(ip, port, userName, pwd);
		return ftpUtil.downFileFromHost(remotePath, localPath,fileName);
	}
	
	/***
	 * 读取文件
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private List<String> readFile(String fileName) throws Exception{
		List<String> ls=new ArrayList<String>();
		String line=null;
		File file=new File(fileName);
		InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file),"GB2312");
		BufferedReader reader=new BufferedReader(inputStreamReader);
		while ((line=reader.readLine())!=null) {
			ls.add(line);
		}
		
		inputStreamReader.close();
		reader.close();
		if(file.delete()){
			LogUtil.printInfoLog("SjBankMXCX", "readFile", "成功删除文件！"+fileName);
		}
		return ls;
	}
	
	/***
	 * 获取配置的本地路径和远程路径
	 * @return
	 */
	private void getPath(){
		String path=SjBankMXCX.class.getResource("/").getPath().replace("%20", " ")+"sjParam.properties";
		try {
			// 定义一个properties文件的名字
			// 定义一个properties对象
			Properties properties = new Properties();
			// 读取properties
			InputStream file = new FileInputStream(new File(path));
			// 加载properties文件
			properties.load(file);
			// 读取properties中的某一项
			localPath =properties.getProperty("sjb_localPath");
			remotePath =properties.getProperty("sjb_remotePath");
			ip=properties.getProperty("ip");
			userName=properties.getProperty("userName");
			pwd=properties.getProperty("pwd");
			port=Integer.parseInt(properties.getProperty("port"));
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return;
	}
	
}
