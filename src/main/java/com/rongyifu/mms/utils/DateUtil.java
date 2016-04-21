package com.rongyifu.mms.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	private static SimpleDateFormat intSDF = new SimpleDateFormat("yyyyMMdd");
	
	private static SimpleDateFormat stringDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	private static SimpleDateFormat intDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	
	private static SimpleDateFormat bocStringDateTimeFormat = new SimpleDateFormat("yyMMddHHmmss");
	
	/*使用时需用synchronized同步*/ 
	private static final SimpleDateFormat defaultFormater = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat customizedFormater = new SimpleDateFormat("yyyyMMdd");
	
	/**
	* @title: format
	* @description: 按指定个格式格式化日期
	* @author li.zhenxing
	* @date 2014-12-25
	* @param date 日期
	* @param pattern 格式
	*/ 
	public static String format(Date date,String pattern){
		synchronized (customizedFormater) {
			if (null != pattern && !"".equals(pattern.trim())) {
				customizedFormater.applyPattern(pattern);
			}else{
				throw new IllegalArgumentException("pattern can not be empty");
			}
			return customizedFormater.format(date);
		}
	}
	/**
	* @title: format
	* @description: 按yyyyMMdd格式化日期
	*/ 
	public static String format(Date date){
		synchronized (defaultFormater) {
			return defaultFormater.format(date);
		}
	}
	
	/**
	* @title: parse
	* @description: 按指定的格式解析日期字符串
	* @author li.zhenxing
	* @date 2014-12-25
	* @param dateStr 日期字符串
	* @param pattern 格式
	*/  
	public static Date parse(String dateStr,String pattern){
		synchronized (customizedFormater) {
			if (null != pattern && !"".equals(pattern.trim())) {
				customizedFormater.applyPattern(pattern);
			}else{
				throw new IllegalArgumentException("pattern can not be empty");
			}
			try {
				return customizedFormater.parse(dateStr);
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	/**
	* @title: parse
	* @description: 按默认格式yyyyMMdd解析日期字符串
	* @author li.zhenxing
	* @date 2014-12-25
	* @param dateStr
	*/ 
	public static Date parse(String dateStr){
		synchronized (defaultFormater) {
			try {
				return defaultFormater.parse(dateStr);
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	/**
	* @title: validate
	* @description: 用指定的格式校验日期
	* @author li.zhenxing
	* @date 2014-12-25
	* @param dateStr 日期字符串
	* @param pattern 日期字符串应满足的格式
	*/ 
	public static boolean validate(String dateStr,String pattern){
		synchronized (customizedFormater) {
			customizedFormater.setLenient(false);
			if (null != pattern && !"".equals(pattern.trim())) {
				customizedFormater.applyPattern(pattern);
			}else{
				throw new IllegalArgumentException("pattern can not be empty");
			}
			try {
				customizedFormater.parse(dateStr);
				return true;
			} catch (ParseException e) {
				return false;
			}
		}
	}
	/**
	 * 校验日期
	 * 格式：yyyyMMdd
	 * @author li.zhenxing
	 * @date 2014-12-25
	 */ 
	public static boolean validate(String dateStr){
		synchronized (defaultFormater) {
			try {
				defaultFormater.setLenient(false);
				defaultFormater.parse(dateStr);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	/**
	 * 格式化数据库存的整型的日期
	 * @return yyyy-MM-dd
	 */
	public static String formatDate(Integer date){
		try {
			return (date+"").substring(0, 4)+"-"+(date+"").substring(4,6)+"-"+(date+"").substring(6, 8);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 返回当前时间 HHmmss
	 * @return
	 */
	public static int now(){
		return Integer.parseInt(new SimpleDateFormat("HHmmss").format(new Date()));
	}
	
	
	/**
	 * @return 返回当前时间的UTC-seconds
	 */
	@SuppressWarnings("deprecation")
	public static int getCurrentUTCSeconds() {
		Date _current = Calendar.getInstance().getTime();
		String dt = stringDateTimeFormat.format(_current);
		String tempDateTime[] = dt.split("-");
		int yy = Integer.parseInt(tempDateTime[0]);
		int MM = Integer.parseInt(tempDateTime[1]);
		int dd = Integer.parseInt(tempDateTime[2]);
		int HH = Integer.parseInt(tempDateTime[3]);
		int mm = Integer.parseInt(tempDateTime[4]);
		int ss = Integer.parseInt(tempDateTime[5]);
		return (int) (Date.UTC(yy, MM, dd, HH, mm, ss) / 1000 % 86400);
	}
	/**
	 * @return 20090909122124
	 */
	public static long getIntDateTime() {
		Date d = new Date();
		return Long.parseLong(intDateTimeFormat.format(new Date(d.getTime())));
	}
	
	
	/**
	 * @return 20090909122124
	 */
	public static String getBOCB2EDateTime() {
		Date d = new Date();
		return bocStringDateTimeFormat.format(new Date(d.getTime()));
	}
	
	
	
	
	public static String getTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(currentTime);
	}
	
	/**
	 * 将UTC-second类型时间转化为hh:mm:ss格式
	 * @return
	 */
	public static String getStringTime(Integer nowtime) {
		if(null == nowtime){
			return "";
		}
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":" + (second < 10 ? "0" + second : second);
	}
	
	/**
	 * 本月最后一天 
	 * @return 
	 */
	public static int getThisMonthLastDate() {
		Calendar calendar = Calendar.getInstance();
		Calendar cpcalendar = (Calendar) calendar.clone();
		cpcalendar.set(Calendar.DAY_OF_MONTH, 1);
		cpcalendar.add(Calendar.MONTH, 1);
		cpcalendar.add(Calendar.DATE, -1);
		return Integer.parseInt(intSDF.format(new Date(cpcalendar.getTimeInMillis())));
	}
	
	/**
	 * @return 返回整数类型日期，以今天为基准，负date表示今天前date天，正date今天之后date天
	 */
	public static int systemDate(int date) {
		@SuppressWarnings("unused")
		Calendar c = Calendar.getInstance();
		Date d = new Date();
		return Integer.parseInt(intSDF.format(new Date(d.getTime() + (long) date * 24 * 60 * 60 * 1000)));
	}
	/**
	 * @return 返回整数类型日期，以今天为基准，负date表示今天前date天，正date今天之后date天
	 */
	public static String yestodayStr() {
		String date = systemDate(-1)+"";
		return date.substring(0, 4)+ "-"+date.substring(4,6)+"-"+date.substring(6,8);
	}
	/**
	 * 今天
	 * @return
	 */
	public static int today() {
		return systemDate(0);
	}
	/**
	 *判断date是不是不周日
	 * @param date
	 * @return
	 */
	public static boolean isSunday(int date) {
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return week == 0;
	}
	/**
	 * 判断date是不是第一天
	 * @param date
	 * @return
	 */
	public static boolean isFirstDate(int date) {
		String today = String.valueOf(date);
		return "01".equals(today.substring(6));
	}
	/**
	 * 判断date是不是16号
	 * @param date
	 * @return
	 */
	public static boolean is16Date(int date) {
		String today = String.valueOf(date);
		return "16".equals(today.substring(6));
	}
	
	/**
	 * 判断date是不是16号
	 * @param date
	 * @return
	 */
	public static boolean is15Date(int date) {
		String today = String.valueOf(date);
		return "15".equals(today.substring(6));
	}
	
	/**
	 * 得到date所在周的周六
	 * @param date
	 * @return
	 */
	public static int getDateSaturday(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.DATE, 6);
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}
	/**
	 * 得到date所在周的下周周六
	 * @param date
	 * @return
	 */
	public static int getDateNextSaturday(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.DATE, 14-calendar.get(Calendar.DAY_OF_WEEK));
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}

	// 得到本周六日期
	public static int nextSaturday() {
		Calendar calendar = Calendar.getInstance();
		Calendar cpcalendar = (Calendar) calendar.clone();
		cpcalendar = (Calendar) calendar.clone();
		cpcalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		return Integer.parseInt(intSDF.format(new Date(cpcalendar.getTimeInMillis())));
	}
	
	/**
	 * 得到date所在周的上周日
	 * @param date
	 * @return
	 */
	public static int getDatelastSunday(int date) {
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.add(Calendar.DATE, -7);
		return Integer.parseInt(intSDF.format(new Date(cal.getTimeInMillis())));
	}
	/**
	 * 得到date前一天
	 * @param date
	 * @return
	 */
	public static int getDateyestoday(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.DATE, -1);
		return Integer.parseInt(intSDF.format(calendar.getTime()));
	}
	/**
	 * 得到date后一天
	 * @param date
	 * @return
	 */
	public static int getDatetomorrow(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.DATE, 1);
		return Integer.parseInt(intSDF.format(calendar.getTime()));
	}
	
	/**
	 * 得到上月同一天
	 * @param date
	 * @return
	 */
	public static int getLastMonthDate(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.MONTH, -1);
		return Integer.parseInt(intSDF.format(calendar.getTime()));
	}
	// date 所在月的最后一天
	public static int getThisMonthLastDate(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -day);
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}
	
	// date所在月的下月的最后一天
	public static int getNextMonthLastDate(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 2);
		calendar.add(Calendar.DATE, -1);
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}
	
	//下月15号
	public static int getNextMonth15(int date) {
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 15);
		calendar.add(Calendar.MONTH, 1);
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}
	
	/**
	 * 当前日期和时间  yyyyMMddHHmmss
	 */
	public static String getNowDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(new Date());
	}
	
	//两年前的同一天
	public static int getTwoYearsBefore(int date){
		Calendar calendar = Calendar.getInstance();
		int year = Integer.parseInt(String.valueOf(date).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(date).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(date).substring(6, 8));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.add(Calendar.YEAR, -2);
		return Integer.parseInt(intSDF.format(new Date(calendar.getTimeInMillis())));
	}
	/**
	 * 该日期对应的星期
	 * @param date 日期的字符串
	 * @return 该日期对应星期几
	 */
	public static int dayCount(String d) {
		Calendar cal = Calendar.getInstance();
		int year = Integer.parseInt(d.substring(0, 4));
		int month = Integer.parseInt(d.substring(4, 6));
		int day = Integer.parseInt(d.substring(6, 8));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return week;
	}
	/**
	 * 得到指定天数后的天数
	 * @param count 指定几天以后
	 * @return
	 */
	public static String returnDefinedDate(int count) {
		Calendar strDate = Calendar.getInstance();
		strDate.add(Calendar.DATE, count);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(strDate.getTime());
	}
	
	/**
	 * 得到UTC秒
	 * @param time 格式：HHMMSS
	 * @return
	 */
	public static int getUTCTime(String time){
		int len = time.length();
		if(len < 6){
			for(int i = 0; i < 6 - len; i++){
				time = "0" + time;
			}
		}
		
		int HH=Integer.parseInt(time.substring(0, 2));
		int MM=Integer.parseInt(time.substring(2,4));
		int SS=Integer.parseInt(time.substring(4,6));
		return (HH*60+MM)*60+SS;
	}
	
	@SuppressWarnings("deprecation")
	public static int getCurrentUTCSeconds(Date current) {
		String dt = stringDateTimeFormat.format(current);
		String tempDateTime[] = dt.split("-");
		int yy = Integer.parseInt(tempDateTime[0]);
		int MM = Integer.parseInt(tempDateTime[1]);
		int dd = Integer.parseInt(tempDateTime[2]);
		int HH = Integer.parseInt(tempDateTime[3]);
		int mm = Integer.parseInt(tempDateTime[4]);
		int ss = Integer.parseInt(tempDateTime[5]);
		return (int) (Date.UTC(yy, MM, dd, HH, mm, ss) / 1000 % 86400);
	}
	
}
