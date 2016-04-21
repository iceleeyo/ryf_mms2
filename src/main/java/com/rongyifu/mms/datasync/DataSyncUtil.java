package com.rongyifu.mms.datasync;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.utils.LogUtil;


public class DataSyncUtil {
	
	/*---------------------- POS配置信息 --------------------*/
	public static final String POS = "POS";                                 // POS
	public static final String POS_FILE_SUFFIX = "_RYF.txt";                // POS数据文件后缀
	public static final String POS_C_FILE_SUFFIX = "_C_RYF.txt";            // POS冲正数据文件后缀
	public static final String POS_FILE_PATH = "/opt/data/current/";        // POS文件存放路径 /opt/data/current/
	public static final String POS_SD_BUSI = "01";                          // 收单业务
	public static final String POS_ZZ_BUSI = "02";                          // 转账业务
	
	// POS交易类型
	public static final String POS_TRANS_TYPE_PAY = "1";                   // 消费
	public static final String POS_TRANS_TYPE_CANCEL = "2";                // 消费撤销
	public static final String POS_TRANS_TYPE_RUSH = "3";                  // 冲正交易
	public static final String POS_TRANS_TYPE_REFUND = "4";                // 退货
	public static final String POS_TRANS_TYPE_CANCEL_RUSH = "5";           // 消费撤销冲正
	
	/*---------------------- VAS配置信息 --------------------*/
	public static final String VAS = "VAS";                                 // VAS
	public static final String VAS_FILE_SUFFIX = "_RYF.txt";                // VAS数据文件后缀
	public static final String VAS_FILE_PATH = "/opt/data/current-vas/";    // VAS文件存放路径 /opt/data/current-vas/
	
	/*---------------------- 文件读取状态 --------------------*/
	public static final String FILE_READ_SUCCESS = "Y";                     // 文件读取成功
	public static final String FILE_READ_FAILURE = "N";                     // 文件读取失败
	
	/*---------------------- 对账标识  ----------------------*/
	public static  final Integer BK_CHECK_INIT = 0;                         //初始状态（未对帐）
	public static  final Integer BK_CHECK_SUCCESS = 1;                      //勾对成功，双方均成功
	public static  final Integer BK_CHECK_FAIL_AMT = 2;                     //勾对失败，双方金额不符
	public static  final Integer BK_CHECK_FAIL_BANK = 3;                    //勾对失败，银行为可疑交易
	
	/*--------------------Pos对账数据，差错文件同步---------------------------*/
	public static final String PosDz="PosDz";								//PosDz Pos对账文件
	public static final String PosDz_FILE_SUFFIX="_POS_DZ.txt";				//Pos对账文件后缀
	public static final String PosDz_cc_FILE_SUFFIX="_POS_CC.txt";			//Pos差错文件
	public static final String PosDz_FILE_PATH="/opt/data/current-pos/";	//Pos对账文件存放路径
	
	/**
	 * 获取时间（格式：HHmmss）
	 * @return
	 */
	public static String getNowTime(){
		return new SimpleDateFormat("HHmmss").format(new Date());
	}
	
	/**
	 * 获取日期时间
	 * @param formatStr	日期时间格式
	 * @return
	 */
	public static String getNowTime(String formatStr){
		return new SimpleDateFormat(formatStr).format(new Date());
	}
	
	/**
	 * 发送报警邮件
	 * @param content	邮件内容
	 * @param type		业务类型
	 * @param fileName  文件名
	 */
	public static void sendMail(String content, String type, String fileName){
		String title = "[" + type + "]数据同步报警-" + getNowTime("yyyyMMdd");
		try {
			String mailContent = type + "数据同步失败，请联系相关技术人员进行处理！\r\n";
			mailContent += "数据文件：" + fileName + "\r\n";
			mailContent += "错误原因如下：\r\n" + (Ryt.empty(content) ? "" : content.trim());
			LogUtil.printInfoLog("DataSyncUtil", "sendMail", title + "：\r\n" + mailContent);
			CallPayPocessor.sendMail(mailContent, title, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * 根据原日期和间隔天数来计算出间隔N天的日期
	 *     如果间隔天数是正数，则是往后延的日期；
	 *     如果间隔天数是负数，则是往前的日期</pre>
	 * @param date
	 * @param intervalDays
	 * @return
	 */
	public static synchronized String getIntervalDate(String date, int intervalDays){
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.add(Calendar.DATE, intervalDays);
		
		return dateFormat.format(new Date(cal.getTimeInMillis()));
	}
	
	/**
	 * 计算两个日期相差的天数
	 */
	public static int daysBetween(String bdate, String edate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(bdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(edate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
	
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	
	/***
	 * @param date  日期
	 * @return  格式：yyyy-MM-dd
	 */
	public static String formatDate(String date){
		String y=date.substring(0, 4);
		String m=date.substring(4, 6);
		String d=date.substring(6,8);
		return y+"-"+m+"-"+d;
	}
}