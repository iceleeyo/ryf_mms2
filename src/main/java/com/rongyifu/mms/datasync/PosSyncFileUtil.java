package com.rongyifu.mms.datasync;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.rongyifu.mms.bean.AdjustAccount;
import com.rongyifu.mms.bean.DataSyncLog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PosSyncFileUtil {
	
	private static final String PRO_TYPE="DLXT";
	private static String path="/opt/data/current-zf";
	private static AdminZHDao dao=new AdminZHDao();
	/**
	 * 生成消费数据
	 * @param data 消费记录
	 * @return
	 */
	public List<String> createXFDataHlog(List<Object> data){
		List<String> contentList=new ArrayList<String>();
			for (Object ordInfo : data) {
				StringBuffer content=new StringBuffer();
				OrderInfo orderInfo = (OrderInfo) ordInfo;
				String oid=orderInfo.getOid();
				String boid = (oid.length()>6)?(oid.substring(0,oid.length()-6)):oid;
				content.append(Ryt.empty(orderInfo.getP3())?"":orderInfo.getP3().trim()).append(",");//代理商号
				content.append(Ryt.empty(orderInfo.getP4())?"":orderInfo.getP4().trim()).append(",");//刷卡器编号
				content.append(orderInfo.getMid()).append(",");// 商户号
//				content.append(boid).append(",");// 支付流水 （20140224版本更新去掉）
				content.append(boid).append(",");// 交易订单号
				content.append(Ryt.div100(Math.abs(orderInfo.getAmount()))).append(",");// 交易金额
				content.append(orderInfo.getSysDate()).append(",");// 交易日期
				content.append(orderInfo.getBkDate()).append(",");// 支付日期
				content.append(orderInfo.getP1().equals("5")?"2":"1").append(",");//交易类型1:消费、2:消费撤销冲正
				content.append("".equals(orderInfo.getP10().trim())?"3":orderInfo.getP10().trim()).append("\n");//交易类型1:消费、2:消费撤销冲正
				contentList.add(content.toString());
		}
		return contentList;
	}
	
	/**
	 * 生成退款数据
	 * @param data 退款记录
	 * @return
	 */
	public List<String> createTKDataHlog(List<Object> data){
			List<String> contentList=new ArrayList<String>();
			for (Object order : data) {
				 OrderInfo orderInfo=(OrderInfo)order;
				 StringBuffer content=new StringBuffer();
				 Integer  sysDate=orderInfo.getSysDate();
				 String oid=orderInfo.getOid();
				 String boid = (oid.length()>6)?(oid.substring(0,oid.length()-6)):oid;
				 content.append(Ryt.empty(orderInfo.getP3())?"":orderInfo.getP3().trim()).append(",");//代理商号
				 content.append(Ryt.empty(orderInfo.getP4())?"":orderInfo.getP4().trim()).append(",");//刷卡器编号
				 content.append(orderInfo.getMid()).append(",");//商户号
				 content.append(Ryt.div100(Math.abs(orderInfo.getAmount()))).append(",");//本次退款金额
				 content.append(sysDate).append(",");//退款申请日期
				 content.append(sysDate).append(",");//退款确认日期
				 content.append(sysDate).append(",");//退款经办日期
				 content.append(sysDate).append(",");//退款审核日期
				 content.append("").append(",");//退款申请原因
				 content.append(boid).append(",");//原交易订单号
//				 content.append(boid).append(",");//原支付流水（20140224版本更新去掉）
				 content.append(Ryt.div100(Math.abs(orderInfo.getAmount()))).append(",");//原交易金额
				 content.append(sysDate).append(",");//原交易日期
				 content.append(Ryt.div100(Math.abs(orderInfo.getAmount()))).append(",");//申请退款金额总计
				 content.append(orderInfo.getP1().equals("2")?"1":(orderInfo.getP1().equals("3")?"2":"3")).append("\n");//1 撤销  2 冲正
				 contentList.add(content.toString());
			}
		return contentList;
	}
	
	/**
	 * 生成手工调增数据
	 * @param data 数据记录
	 * @return
	 */
	public List<String> createXFDataAccount(List<Object> data){
		List<String> contentList=new ArrayList<String>();
		for (Object orderInfo : data) {
			AdjustAccount order=(AdjustAccount)orderInfo;
			StringBuffer content=new StringBuffer();
			content.append(order.getMid()).append(",");
			content.append(order.getId()).append(",");
			content.append(order.getId()).append(",");
			content.append(Ryt.div100(order.getAccount())).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append("消费").append("\n");
			contentList.add(content.toString());
		}
		return contentList;
	}
	
	
	/**
	 * 生成调减数据
	 * @param data 退调减款记录
	 * @return
	 */
	public List<String> createTKDataAccount(List<Object> data){
		List<String> contentList=new ArrayList<String>();
		for (Object orderInfo : data) {
			AdjustAccount order=(AdjustAccount)orderInfo;
			StringBuffer content=new StringBuffer();
			content.append(order.getMid()).append(",");
			content.append(Ryt.div100(order.getAccount())).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(null==order.getReason()?"":order.getReason().trim().replaceAll(",", "，")).append(",");
			content.append(order.getId()).append(",");
			content.append(order.getId()).append(",");
			content.append(Ryt.div100(order.getAccount())).append(",");
			content.append(order.getSubmitDate()).append(",");
			content.append(Ryt.div100(order.getAccount())).append(",");
			content.append("4").append("\n");//退款类型4 为调减数据
			contentList.add(content.toString());
		}
		return contentList;
	}
	
	
	
	/**
	 * 查询HLOG数据
	 * @param dates 查询日期
	 * @param where
	 * @return
	 */
	public List<OrderInfo> getDateFromHlog(String dates,String where){
		Integer TYPE=Constant.TlogTransType.POS_PAY;
		Integer TSTAT=Constant.PayState.SUCCESS;
		Integer DATA_SOURCE=3;//数据来源  Pos 同步
		Integer BKCHK=DataSyncUtil.BK_CHECK_SUCCESS;//对账标识
		StringBuffer sql=new StringBuffer("select * from hlog where ");
		sql.append("sys_date in (").append(dates).append(") and ");
		sql.append(" type =").append(TYPE).append(" and ");
		sql.append("tstat = ").append(TSTAT).append(" and ");
		sql.append(" data_source= ").append(DATA_SOURCE).append(" and ");
		sql.append(" bk_chk= ").append(BKCHK);
		if(!Ryt.empty(where))
			sql.append(" and ").append(where);
		else{
			LogUtil.printInfoLog("PosSyncFileUtil", "getDateFromHlog","Sql条件语句为空!");
			return new ArrayList<OrderInfo>();
		}
		LogUtil.printInfoLog("PosSyncFileUtil", "getDateFromHlog","执行Sql:"+sql.toString());
		return dao.query(sql.toString(), OrderInfo.class);
	}
	
	/**
	 * 查询调帐表数据
	 * @param dates
	 * @param where
	 * @return
	 */
	public List<AdjustAccount> getDateFromAdjAccount(String dates,String where){
		StringBuffer sql=new StringBuffer("select * from adjust_account where ");
		sql.append(" length(mid)>10 and ").append(" state=1 and ");
		sql.append(" submit_date in(").append(dates).append(") and ");
		if(!Ryt.empty(where))
			sql.append(where);
		else{
			LogUtil.printInfoLog("PosSyncFileUtil", "getDateFromAdjAccount","Sql条件语句为空!");
			return new ArrayList<AdjustAccount>();
			}
		LogUtil.printInfoLog("PosSyncFileUtil", "getDateFromAdjAccount","执行Sql:"+sql.toString());
		return dao.query(sql.toString(), AdjustAccount.class);
	}
	
	/**
	 *  获取数据库记录
	 * @param methodName
	 * @param where
	 * @param date
	 * @return
	 */
	public static List<Object> getData(String methodName,String where,String date){
		List<Object> records=new ArrayList<Object>();
		Class [] paramType =new Class[2];
		paramType[0]=String.class;
		paramType[1]=String.class;
		
		String []args=new String[2];
		args[0]=date;
		args[1]=where;
		
		try {
			Method method= PosSyncFileUtil.class.getMethod(methodName, paramType);
			records=(List<Object>)method.invoke(PosSyncFileUtil.class.newInstance(),args);
		} catch (Exception e) {
			LogUtil.printErrorLog("PosSyncFileUtil", "getData","获取数据库记录出错:"+methodName);
		}
		return records;
	}
	
	/**
	 * 处理数据
	 * @param methodName
	 * @param records
	 * @return
	 */
	public static List<String> handleDate(String methodName,List<Object> records){
		List<String> datas=new ArrayList<String>();
		Class [] paramType =new Class[1];
		paramType[0]=List.class;
		
		List<Object> []arg=new ArrayList[1];
		arg[0]=records;
		try {
			Method method= PosSyncFileUtil.class.getMethod(methodName, paramType);
			datas=(List<String>)method.invoke(PosSyncFileUtil.class.newInstance(),arg);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.printErrorLog("PosSyncFileUtil", "handleDate","处理数据异常:"+methodName);
		}
		return datas;
	}
	
	/**
	 * 查询指定日期是否为双休日
	 * @param day
	 * @return
	 */
	public static boolean isWeekends(String day){
		int dayCount=DateUtil.dayCount(day);
		if(dayCount==6 || dayCount==0)
			return true;
		return false;
	}
	
	/**
	 * 根据两个日期之间的间隔 计算需要数据查询的时间段
	 * @param bdate 开始日期
	 * @param edate 结束日期
	 * @return 返回时间都为需要查询日期的前两天
	 */
	public static String getDays(String bdate,String edate){
		int betweenDays=0;
		try {
			betweenDays=DataSyncUtil.daysBetween(bdate, edate);
		} catch (ParseException e) {
			LogUtil.printInfoLog("PosSyncFileUtil", "getDays", "时间处理异常,错误原因:"+e.getMessage());
		}
		StringBuffer resDates=new StringBuffer();
		if (betweenDays<=1){
				return DataSyncUtil.getIntervalDate(edate,-2);//相隔时间小于等于1天，返回前两天的时间
		}
		else{
		for (int i = 0; i < betweenDays; i++) {
			//日期的前两天
			String resd=DataSyncUtil.getIntervalDate(DataSyncUtil.getIntervalDate(String.valueOf(edate), -i),-2);
			resDates.append(resd).append(",");
		}
		String days=resDates.toString();
		return days.substring(0,days.length()-1);
		}
	}
	
	/**
	 * 写文件
	 * @param fileName 文件名
	 * @param datas 文件内容
	 * @return
	 */
	public static boolean createFile(String filePath,List<String> datas,Integer logDate){
		System.out.println("filePath:"+filePath);
		String fileP=filePath;
		String fileName=filePath.replace(path+"/", "");
		Iterator<String> iterator=null;
		FileOutputStream fos =null;
		OutputStreamWriter osw = null;
		try{
			if(datas.size()>0){
				if (datas.get(0).equals("none") || "".equals(datas.get(0).trim())){
					LogUtil.printInfoLog("PosSyncFileUtil", "createFile", "代理系统数据采集---该文件当天已生成");
					return true;
				}
				fos = new FileOutputStream(fileP);
				osw = new OutputStreamWriter(fos, "UTF-8");
				iterator=datas.iterator();
				int count=0;
				while (iterator.hasNext()) {
					String data = (String) iterator.next();
					osw.write(data);
					count++;
				}
				osw.flush();
				LogUtil.printInfoLog("PosSyncFileUtil", "createFile", "代理系统数据采集---文件生成成功，共 "+count+" 条数据，文件："+fileName);
				DataSyncDb.recordLog(dao, PRO_TYPE, fileName, true,logDate);
			}else{
				DataSyncDb.recordLog(dao, PRO_TYPE, fileName, false,logDate);
				//如果当天为周日或者周一，则不发报警邮件
				int dayCount=DateUtil.dayCount(DateUtil.today()+"");
				if(dayCount !=0 && dayCount !=1)
					DataSyncUtil.sendMail("数据为空", PRO_TYPE, fileName);
				else
					LogUtil.printInfoLog("PosSyncFileUtil", "createFile", "代理系统数据采集---数据为空,当前为周日或者周一");
			}
			return true;
		}catch (Exception e) {
			DataSyncDb.recordLog(dao, PRO_TYPE, fileName, false,logDate);
			DataSyncUtil.sendMail(e.getMessage(), PRO_TYPE, fileName);
			LogUtil.printErrorLog("PosSyncFileUtil", "createFile", "代理系统数据采集---生成文件异常");
			e.printStackTrace();
			return false;
		}finally{
			try{
				if(fos!=null)fos.close();
				if(osw!=null)osw.close();
			}catch (Exception e) {
				LogUtil.printErrorLog("PosSyncFileUtil", "createFile", "代理系统数据采集---生成文件异常");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取最后生成一次时间
	 * @param flag 获取时间类型（1代表支付成功交易数据，2代表退款数据，3代表调账支付交易数据，4代表调账退款交易数据）
	 * @return
	 */
	public static Integer getLastDate(Integer flag){
		StringBuffer sql=new StringBuffer("select * from data_sync_log where type=").append(Ryt.addQuotes(PRO_TYPE));
		sql.append(" and flag='Y' and filename like '2\\_").append(flag).append("\\_%' order by sys_date desc,sys_time desc limit 1");
		List<DataSyncLog> log=dao.query(sql.toString(), DataSyncLog.class);
		int date = DateUtil.today();
		if(log.size()>0){
			if(date==Integer.parseInt(log.get(0).getSysDate()))//如果操作时间等于今天则表示今天已经生成过数据了，不重复执行
				return 0;
			return Integer.parseInt(log.get(0).getSysDate());//生成数据当天的时间
		}
		else{
		return DateUtil.today();}
	}
}
