package com.rongyifu.mms.modules.transaction.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.modules.transaction.dao.UploadHlogDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
	 商户日期为商户订单好的字母之后的6位,系统时间为订单号的字母后面的1-12
	交易金额为应付金额(北京VAS机票业务)、交易金额为支付价格
	交易状态为交易成功
	交易类型为增值业务
	交易银行/渠道:
		1.团购和现金业务:交通银行-vas
		2.机票业务:对应的银行加上-vas（如北京银行-vas），支付渠道为对应的银行加上-vas（如北京银行-vas）。其中广发银行-vas分为大小额，广发银行的机票业务的支付渠道为（广发银行（大）-vas)
	其他字段都为空
 *	sys_date,sys_time,pay_amt,type(5,增值业务),gate(支付网关网关),gid(支付渠道),tstat(成功)
 */
public class UploadHlogService {
	private static final Map<Integer,String[]> DATA_MAPPING;//csv文件和hlog属性的映射关系 文件类型->hlog属性名数组(cvs中不对应hlog属性的字段填空字符串)
	private static final Map<Integer,Strategy[]> PARSE_STRATEGIES;//字符串解析机制
	private static final Map<String,int[]> GATE_INFOS;
	private UploadHlogDao dao = new UploadHlogDao();
	private Logger logger = Logger.getLogger(getClass());
	static{
		GATE_INFOS = readGatesInfo();
		Map<Integer,String[]> mapping = new HashMap<Integer,String[]>();
		mapping.put(0, new String[]{null,"sysDate|sysTime|oid",null,null,"gate|gid",null,null,null,"amount"});//团购业务
		mapping.put(1, new String[]{null,"sysDate|sysTime|oid",null,null,"gate|gid",null,null,null,"amount"});//现金券业务
		mapping.put(2, new String[]{null,null,null,"gate|gid","sysDate|sysTime|oid",null,null,null,null,"amount"});//融易通机票业务
		DATA_MAPPING = Collections.unmodifiableMap(mapping);
		Map<Integer,Strategy[]> strategy = new HashMap<Integer,Strategy[]>();
		//从订单号截取订单日期 团购
		Strategy retrieveSysDateTimeOid = new Strategy(){
			@Override
			public void setValue(String fieldName, String input, BeanWrapper bw) throws Exception {
				String[] fields = fieldName.split("\\|");
				String dateStr = "20"+input.substring(2,8);
				bw.setPropertyValue(fields[0], Integer.valueOf(dateStr));//sys_date
				String timeStr = input.substring(8,14);
				Integer utcSecs = DateUtil.getUTCTime(timeStr);
				bw.setPropertyValue(fields[1], utcSecs);//sys_time
				bw.setPropertyValue(fields[2], input);//oid
			}
		};
		//设置融易付机票业务的网关和渠道
		Strategy setGidAndGate = new Strategy(){
			@Override
			public void setValue(String fieldName, String input, BeanWrapper bw) throws Exception {
				int[] gateAndGid = GATE_INFOS.get(input);
				bw.setPropertyValue("gate", gateAndGid[0]);//如果没有匹配的会抛出空指针异常 当前订单记录不会存库
				bw.setPropertyValue("gid", gateAndGid[1]);
			}
		};
		//金额转换
		Strategy yuanToFen = new Strategy(){
			@Override
			public void setValue(String fieldName, String input, BeanWrapper bw) throws Exception {
				bw.setPropertyValue(fieldName, Ryt.mul100(input));
			}
		};
		strategy.put(0, new Strategy[]{null,retrieveSysDateTimeOid,null,null,setGidAndGate,null,null,null,yuanToFen});//团购业务
		strategy.put(1, new Strategy[]{null,retrieveSysDateTimeOid,null,null,setGidAndGate,null,null,null,yuanToFen});//现金券业务
		strategy.put(2, new Strategy[]{null,null,null,setGidAndGate,retrieveSysDateTimeOid,null,null,null,null,yuanToFen});//融易通机票业务
		PARSE_STRATEGIES = Collections.unmodifiableMap(strategy);
	}
	
	
	public String uploadHlog(FileTransfer file,String fileName, Integer type){
		if(type == 3 ){//上传数据同步文件
			return uploadDataSyncFile(file,fileName);
		}
		String msg = "";
		BufferedReader br = null;//该buffreredReader是对ByteArrayInputStream的封装 不需要关闭
		ByteArrayOutputStream byteOut = null;
		try {
			String extensions = fileName.substring(fileName.lastIndexOf("."));// 后缀名
			if (fileName.indexOf(".") <= 0||!extensions.equals(".csv")) {
				msg = "文件类型不正确！";
			}
			int length = 0;
			byte[] buff = new byte[1024];
			byteOut = new ByteArrayOutputStream();
			InputStream in = file.getInputStream();
			while((length = in.read(buff)) != -1){
				byteOut.write(buff,0,length);
			}
			byte[] fileBytes = byteOut.toByteArray();
			byteOut = null;
			
//			//判断上传文件的编码
			CharsetDetector detec = new CharsetDetector();
			detec.setText(fileBytes);
		    CharsetMatch match = detec.detect();
		    if(match == null){
		    	throw new Exception("未知的文件编码");
		    }
		    String encoding = match.getName();
		    logger.info("-------- file["+fileName+"] encoding:"+encoding+" --------");
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileBytes),encoding));
			String[] mapping = DATA_MAPPING.get(type);
			Strategy[] strategies = PARSE_STRATEGIES.get(type);
			List<Hlog> hlogList = new ArrayList<Hlog>();
			br.readLine();//过滤表头
			if (null != mapping) {
				String line = "";
				while ((line = br.readLine())!=null) {
					String[] values = line.split(",");
					Hlog hlog= new Hlog();
					hlog.setTstat((short)2);//交易状态为成功
					hlog.setType((short)5);//增值业务
					hlog.setMid("325");//商户号 325
					BeanWrapper bw = new BeanWrapperImpl(hlog);
					for (int i = 0;i<mapping.length;i++) {
						String fieldName = mapping[i];
						if(StringUtils.isNotBlank(fieldName)){
							Strategy stra = strategies[i];
							String value = values[i].trim();
							if(stra != null){
								stra.setValue(fieldName,value,bw);//需要自定义转换
							}else if(StringUtils.isNotBlank(value)){
								bw.setPropertyValue(fieldName, value);//直接set
							}else{
								throw new Exception("["+fieldName+"]不能为空");
							}
						}
					}
					hlogList.add(hlog);
				}
				fileBytes = null;
				if (!hlogList.isEmpty()) {
					int count = dao.batchAdd(hlogList);
					if (count != 0) {
						msg = "上传成功,共上传 " + count + " 条记录";
					}else{
						msg = "上传失败";
					}
				}else{
					msg = "上传失败,上传文件中没有订单数据";
				}
			}else{
				msg = "上传失败,未知的业务类型";
			}
		} catch (Exception e) {
			msg = "上传失败";
			LogUtil.printErrorLog(getClass().getCanonicalName(), "uploadHlog", "fileName="+fileName+" type="+type, e);
		}
		return msg;
	}
	
	private String uploadDataSyncFile(FileTransfer file,String fileName){
		//处理文件名
		int lastIndex = fileName.lastIndexOf("\\");
		if(fileName.lastIndexOf("\\") != -1){
			fileName = fileName.substring(lastIndex+1);
		}
		String msg = "";
		String destDir = DataSyncUtil.VAS_FILE_PATH;
//		String destDir = "E:/";
		File f = new File(destDir+fileName);
		FileOutputStream out =null;
		InputStream in = null;
		byte[] buff = new byte[1024];
		if(f.exists()){
			msg = "名为["+fileName+"]的文件已存在";
		}else{
			try {
				out = new FileOutputStream(f);
				in = file.getInputStream();
				int length = 0;
				while((length=in.read(buff)) != -1){
					out.write(buff,0,length);
				}
				out.flush();
				msg = "名为["+fileName+"]的文件已成功上传";
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				msg = "上传失败";
			}finally{
				buff = null;
				if(out != null){
					try {
						out.flush();
						out.close();
						out = null;
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
				}
				if(in != null){
					try {
						in.close();
						in = null;
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
		return msg;
	}
	
/**	------------------------------------------- **/	
	
	private interface Strategy{
		void setValue(String fieldName,String input,BeanWrapper bw) throws Exception;
	}
	
	private static Map<String,int[]> readGatesInfo(){
		Map<String,int[]> map = new HashMap<String,int[]>();
		BufferedReader br = null;
		try {
			//class和classloader的getResourceAsStream()方法的区别			
			//classloader读取资源的路径为相对于当前项目的classpath根目录
			//class读取资源根据参数是否以‘/’开头区分为相对路径和绝对路径,相对路径为相对于当前class所在目录的路径
			br = new BufferedReader(new InputStreamReader(UploadHlogService.class.getClassLoader().getResourceAsStream("importOrderGates.txt"),"UTF-8"));
			String buff = null;
			while((buff = br.readLine())!=null){
				if(StringUtils.isEmpty(buff)||buff.indexOf("#")==0){//忽略空行和注释
					continue;
				}
				String[] values = buff.split(",");
				int[] ins = new int[]{Integer.valueOf(values[0].trim()),Integer.valueOf(values[1].trim())};
				String key = values[2].trim();
				map.put(key, ins);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		return Collections.unmodifiableMap(map);
	}

}
