package com.rongyifu.mms.datasync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PosSyncFile {
	
	private static String term="2";//终端
	private static String path="/opt/data/current-zf";
	private static final String SQL_WHERE_XF_HLOG=" (p1=1 or p1 =5) ";//HLOG消费数据 条件语句
	private static final String SQL_WHERE_TK_HLOG=" (p1=2 or p1 =3) ";//HLOG退款数据 条件语句
	private static final String SQLWHERE_TZ_ACCOUNT=" type=1 ";//调帐表调增  条件语句
	private static final String SQLWHERE_TJ_ACCOUNT="type=2 ";//调帐表调减  条件语句
	
	/**
	 * 自动执行生成文件
	 */
	public void run(){
		if(!Ryt.isStartService("启动代理系统自动同步服务"))
			return;
		createPosSyncFile();
	}
	
	/**
	 * 生成指定日期的指定类型文件
	 * @param date
	 * @param flag 1.消费  2.退款 3.调增 4.调减
	 * @return
	 */
	public static  String posSyncFile(Integer date,Integer flag){
		if (createFileBySpecifyDate(date, flag))
			return "success";
		return "fail";
	}
	
	/**
	 * 自动生成文件执行方法
	 */
	public void createPosSyncFile(){
		//判断文件目录是否存在
		File file=new File(path);
		if(!file.exists())file.mkdirs();
		
		//初始化参数
		List<Map<String, String>> maps=initParam();
		
		for (Map<String, String> param : maps) {
			//启动线程生成文件
			ThrCreateFile thrc=new ThrCreateFile(param);
			Thread thr=new Thread(thrc);
			thr.start();
		}
	}
	
	/**
	 * 生成指定日期的指定类型的文件
	 * @param date 指定日期
	 * @param flag 指定类型     1代表支付成功交易数据  2代表退款数据  3代表调账支付交易数据  4代表调账退款交易数据
	 * @return 
	 */
	private static boolean createFileBySpecifyDate(Integer date,Integer flag){
		//得到数据库记录
		List<Object> records=new ArrayList<Object>();
		String createMethod="";
		switch (flag) {
		case 1:
			// 得到数据库记录
			records = PosSyncFileUtil.getData("getDateFromHlog", SQL_WHERE_XF_HLOG, date+"");
			createMethod = "createXFDataHlog";
			break;
		case 2:
			// 得到数据库记录
			records = PosSyncFileUtil.getData("getDateFromHlog", SQL_WHERE_TK_HLOG, date+"");
			createMethod = "createTKDataHlog";
			break;
		case 3:
			// 得到数据库记录
			records = PosSyncFileUtil.getData("getDateFromAdjAccount", SQLWHERE_TZ_ACCOUNT, date+"");
			createMethod = "createXFDataAccount";
			break;
		case 4:
			// 得到数据库记录
			records = PosSyncFileUtil.getData("getDateFromAdjAccount", SQLWHERE_TJ_ACCOUNT, date+"");
			createMethod = "createTKDataAccount";
			break;
		default:
			return false;
		}
		// 处理得到的记录
		List<String> datas = PosSyncFileUtil.handleDate(createMethod, records);

		String filePath = path + "/" + term + "_" + flag + "_" + date + ".txt";// 文件名称
		return PosSyncFileUtil.createFile(filePath,datas,date);
		}

	/**
	 * 生成文件线程类
	 * @author yang.yaofeng
	 *
	 */
	class ThrCreateFile implements Runnable{
		
		Map<String, String> param=new HashMap<String, String>();
		
		/**
		 * 构造函数
		 * @param filePath 文件路径
		 * @param where 查询条件语句
		 * @param date 最后操作日期
		 * @param logDate 记录操作日志日期 0为默认系统当前日期
		 */
		public ThrCreateFile(Map<String, String> param) {
			this.param=param;
		}
		
		@Override
		public void run() {
			List<String> contents=new ArrayList<String>();
			
			//获取参数
			String date=param.get("date");//最后操作日期
			String filePath=param.get("filePath");//文件路径
			String sqlMethod=param.get("sqlMethod");//查询SQL的方法
			String createMethod=param.get("createMethod");//创建数据的方法
			String where=param.get("where");//查询SQL的条件语句
			Integer logDate=Integer.parseInt(param.get("logDate"));//记录日志的日期
			
			if("0".equals(date)){
				//表示今天已经生成过该文件了(只用于自动生成有效，调用指定生成方法时一天可重复生成)
				contents.add("none");
				PosSyncFileUtil.createFile(filePath, contents,logDate);
			}else{
				Integer today=DateUtil.today();
				
				//计算需要查询的时间段
				String days=PosSyncFileUtil.getDays(date+"", today+"");
				LogUtil.printInfoLog("ThrCreateFile", "run", "系统同步数据日期段:"+days);
				
				//得到数据库记录
				List<Object> records =PosSyncFileUtil.getData(sqlMethod, where, days);
				
				//处理得到的记录
				List<String> datas=PosSyncFileUtil.handleDate(createMethod, records);
				
				//创建文件
				PosSyncFileUtil.createFile(filePath, datas, logDate);
			}
		}
	}
	
	/**
	 * 初始化参数
	 * @return
	 */
	public List<Map<String, String>> initParam(){
		Integer sys_date=DateUtil.today();
		
		List<Map<String, String>> maps=new ArrayList<Map<String,String>>();
		
		String xf_hlog_path=path+"/"+term+"_"+1+"_"+sys_date+".txt";//交易成功数据 -文件名称
		String tk_hlog_path=path+"/"+term+"_"+2+"_"+sys_date+".txt";//退款数据 -文件名称
		String xf_account_path=path+"/"+term+"_"+3+"_"+sys_date+".txt";//调增数据-文件名称
		String tk_account_path=path+"/"+term+"_"+4+"_"+sys_date+".txt";//调减数据 -文件名称
		
		//实例化 四个文件map
		Map<String, String> xf_hlog=new HashMap<String, String>();
		Map<String, String> tk_hlog=new HashMap<String, String>();
		Map<String, String> xf_account=new HashMap<String, String>();
		Map<String, String> tk_account=new HashMap<String, String>();
		
		
		xf_hlog.put("date", PosSyncFileUtil.getLastDate(1)+"");
		xf_hlog.put("filePath", xf_hlog_path);
		xf_hlog.put("sqlMethod", "getDateFromHlog");
		xf_hlog.put("createMethod", "createXFDataHlog");
		xf_hlog.put("where", SQL_WHERE_XF_HLOG);
		xf_hlog.put("logDate", "0");
		
		
		tk_hlog.put("date", PosSyncFileUtil.getLastDate(2)+"");
		tk_hlog.put("filePath", tk_hlog_path);
		tk_hlog.put("sqlMethod", "getDateFromHlog");
		tk_hlog.put("createMethod", "createTKDataHlog");
		tk_hlog.put("where", SQL_WHERE_TK_HLOG);
		tk_hlog.put("logDate", "0");
		
		xf_account.put("date", PosSyncFileUtil.getLastDate(3)+"");
		xf_account.put("filePath", xf_account_path);
		xf_account.put("sqlMethod", "getDateFromAdjAccount");
		xf_account.put("createMethod", "createXFDataAccount");
		xf_account.put("where", SQLWHERE_TZ_ACCOUNT);
		xf_account.put("logDate", "0");
		
		tk_account.put("date", PosSyncFileUtil.getLastDate(4)+"");
		tk_account.put("filePath", tk_account_path);
		tk_account.put("sqlMethod", "getDateFromAdjAccount");
		tk_account.put("createMethod", "createTKDataAccount");
		tk_account.put("where", SQLWHERE_TJ_ACCOUNT);
		tk_account.put("logDate", "0");
		
		maps.add(xf_hlog);
		maps.add(tk_hlog);
		maps.add(xf_account);
		maps.add(tk_account);
		return maps;
	} 
}
