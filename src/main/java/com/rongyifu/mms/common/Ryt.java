package com.rongyifu.mms.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.rytong.encrypto.provider.DesEncrypto;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;

import cert.CertUtil;

import com.rongyifu.mms.bank.b2e.GenB2ETrnid;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.OSUtil;
import com.rongyifu.mms.utils.ParamUtil;

@SuppressWarnings("deprecation")
public class Ryt {

	private static SimpleDateFormat stringDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public static final String BILL_JKS_FILE_PATH = CertUtil.getCertPath("BILL_JKS");

	public static final String ACTION_OK = "ok";
	
	private static int count=0;
	
	/**
	 * 产生6位随机数，不足六位的左边补0
	 * @return 
	 */
	public static String genOidBySysTime(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss");
	    String formatDate=sdf.format(date);
	    return formatDate+GenB2ETrnid.getRandOid();
//		return System.currentTimeMillis() + GenB2ETrnid.getRandOid();
//		String r = String.valueOf(new Random().nextInt(1000000));
//		DecimalFormat ft=new DecimalFormat("000000"); 
//		return String.valueOf(System.currentTimeMillis())+((r.length()==6)?r:ft.format(Integer.parseInt(r)));
	}
	/**
	 * 用于生成商户订单号的尾6位数
	 * @return
	 */
	public synchronized static int getCount(){
		if(count==999999){
			count=0;
		}
		count=count+1;
		return count;
	}
	/**
	 * 生成批次号
	 * @return
	 */
	public static String crateBatchNumber(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	    String formatDate=sdf.format(date);
	    return formatDate+createRandomStr(6);
	}
	public static void print(String msg) {
		LogUtil.printInfoLog("Ryt", "print", msg);
	}

	public static void err(String err) {
		LogUtil.printErrorLog("Ryt", "err", err);
	}

	/**
	 * 将UTC-second类型时间转化为hh:mm:ss格式
	 * @return
	 */
	public static String getStringTime(int nowtime) {
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return hour + ":" + (min < 10 ? "0" + min : min) + ":" + (second < 10 ? "0" + second : second);
	}
	
	public static String getNormalTime(String longTime){
		if(longTime=="null"||longTime=="")return "";
		String normalDate=longTime.substring(0,8);
		String normalTime=longTime.substring(8,10)+":"+longTime.substring(10,12)+":"+longTime.substring(12,14);
		return normalDate+" "+normalTime;
	}

	public static String getStringExportOid(String oid) {
		return oid.indexOf("r") == -1 ? oid : oid.substring(1);
	}

	/**
	 * 把参数缩小一百倍，保留两位小数
	 */
	public static String div100(String amount) {
		if (Ryt.empty(amount)) return "0.00";
		return div100Base(amount);
	}

	public static String div100(Integer amount) {

		return div100Base(String.valueOf(amount));
	}

	public static String div100(Long amount) {
		return div100Base(String.valueOf(amount));
	}

	/**
	 * 把参数扩大一百倍
	 */
	public static String mul100(String amount) {
		return mul100Base(amount);
	}
	
	public static long mul100toInt(String amount) {
		return  Long.parseLong(mul100(amount));
	}

	public static String mul100(Integer amount) {
		return mul100Base(String.valueOf(amount));
	}

	private static String div100Base(String amount) {
		BigDecimal b1 = new BigDecimal(amount);
		BigDecimal b2 = new BigDecimal(100);
		return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).toString();
	}

	private static String mul100Base(String amount) {
		BigDecimal b1 = new BigDecimal(amount.trim());
		BigDecimal b2 = new BigDecimal(100);
		return b1.multiply(b2).setScale(0, BigDecimal.ROUND_UP).toString();
	}

	/**
	 * 参数转换器，对于参数为null或者为“”的参数返回null
	 * @param parameter 参数名称
	 * @return 返回参数的值
	 */
	public static String conversionParameter(String parameter) {
		if (null == parameter || parameter.trim().length() == 0) {
			return null;
		}
		return parameter.trim();
	}

	/** 返回当前时间的UTC-seconds */
	public static int getCurrentUTCSeconds() {
		Date _current = Calendar.getInstance().getTime();
		String dt = stringDateTimeFormat.format(_current);// yyyy-MM-dd-HH-mm-ss
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
	 * 读取配置文件
	 * @param paramName 参数名称
	 * @return 返回参数的值
	 */
	public static String getParameter(String paramName) {
		return ParamUtil.getPropertie(paramName);
	}
	
	/**
	 * 读取配置文件
	 * @param paramName 参数名称
	 * @return 返回参数的值
	 */
	public static int getIntParameter(String paramName) {
		try {
			return  Integer.parseInt(getParameter(paramName));
		} catch (Exception e) {
			return -100;
		}
		
	}
	
	public static String getEwpPath(){
		
		return ParamCache.getStrParamByName("EWP_PATH");
	
		
		//return getParameter("EWP_PATH");
	}
	
	public static String getJSSignServerUrl(){
		
		return ParamCache.getStrParamByName("JS_SIGN_SERVER_URL");
	
		
		//return getParameter("EWP_PATH");
	}

	/**
	 * 向指定URL发送POST方式的请求
	 * @param requestParaMap http请求参数
	 * @param url 请求地址
	 * @return 返回字符串
	 */
	public static String requestWithPost(Map<String, Object> requestParaMap, String url) {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		String sResponseBody = "";
		NameValuePair[] nameValuePairs = null;
		NameValuePair nameValuePair = null;
		try {
			if (requestParaMap != null && requestParaMap.size() > 0) {
				nameValuePairs = new NameValuePair[requestParaMap.size()];
				int i = 0;
				Iterator it = requestParaMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry element = (Map.Entry) it.next();
					nameValuePair = new NameValuePair();
					nameValuePair.setName(String.valueOf(element.getKey()));
					nameValuePair.setValue(String.valueOf(element.getValue()));
					nameValuePairs[i++] = nameValuePair;
				}
				method.setRequestBody(nameValuePairs);
				httpClient.executeMethod(method);
				int resCode = method.getStatusCode();
				if (resCode == HttpStatus.SC_OK) {
//					sResponseBody = method.getResponseBodyAsString();
					InputStream input = method.getResponseBodyAsStream();
					sResponseBody = Ryt.readStream(input);
				}
			}
		} catch (Exception err) {
			sResponseBody=err.getMessage();
			LogUtil.printErrorLog("Ryt", "requestWithPost", "url=" + url, err);
		} finally {
			method.releaseConnection();
		}
		return sResponseBody;
	}

	/**
	 * 向指定URL发送GET方式的请求
	 * @param requestParaMap http请求参数
	 * @param url 请求地址
	 * @return 返回字符串
	 */
	public static String requestWithGet(String htmlurl) throws Exception {
	
			URL url = new URL(htmlurl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String html = "";
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				html = html + readLine;
			}
			br.close();
			return html;


	}

	/**
	 * 判断字符串是否为空或者null
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean empty(String str) {
		if (str == null) return true;
		if (str.trim().equals("")) return true;
		return false;
	}

	/**
	 * 为一个字符串两端加上单引号
	 * @param value 源字符串
	 * @return String
	 */
	public static String addQuotes(String value) {
		if (empty(value)) return "''";
		return "'" + sql(value.trim()) + "'";
	}

	/**
	 * 去掉一个字符串中的空格，换行符
	 * @param value 源字符串
	 * @return String
	 */
	public static String cleanString(String value) {
		if (empty(value)) return "";
		return value.replaceAll("\\r\\n|\\n|\\r", "");
	}

	/**
	 * 检查字符串中的单引号，如果发现，则再追加一个单引号 此函数功能主要是防止存入数据库的字符串内包含单引号会引起的错误， 如"Tom's home" 在入库时会写成这样: Update Tom set home = 'tom's
	 * home' 会出错 Update Tom set home = 'tom''s home' 这才是正确的
	 * @param strSrc 源字符串
	 * @return String
	 */
	public static String checkSingleQuotes(String strSrc) {
		return strSrc.replaceAll("'", "\''");
	}

	public static boolean isNumber(String str) {
		if (str != null && str.length() > 0) {
			int len = str.length();
			for (int i = 0; i < len; i++) {
				char c = str.charAt(i);
				if (c < '0' || c > '9') {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 生成在业务方法中业务处理错误的返回XML文件内容
	 * @param errorCode 错误代码
	 * @param errorMsg 错误提示信息
	 * @return XML文件内容
	 */
	public static String buildErrrorXml(String errorCode, String errorMsg) {
		StringBuffer res = new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status><value>");
		res.append(errorCode);
		res.append("</value><msg>");
		res.append(errorMsg);
		res.append("</msg></status></res>");
		return res.toString();
	}

	// 采用正则表达式将包含有 单引号(')，分号(;) 和 注释符号(--)的语句给替换掉来防止SQL注入
	public static String sql(String str) {
		String s = "exec,insert,select,delete,update,drop,join,union,alter,rename,',--,\"";
		 String [] results=new String[]{"exec~","insert~","select~","delete~","update~","drop~","join~","union~","alter~","rename~","’","——","”"};
		 
		return StringUtils.replaceEach(str, s.split(","),results);
		//return str.replaceAll(".*([';]+|(--)+).*", " ");
	}

	public static String gen6Rand() {
		Random random = new Random();
		String randStr = "1234567890";
		String sRand = "";
		int len = randStr.length();
		for (int i = 0; i < 6; i++) {
			int num = random.nextInt(len);
			String rand = String.valueOf(randStr.charAt(num));
			sRand += rand;
		}
		return sRand;

	}
	
	/**
	 * 6-15位数字字母的组合
	 * @param str
	 * @return
	 */
	public static boolean isWordAndNo(String str) {   
		return str.matches("[A-Za-z0-9]{1,}");
    }
	
	public static boolean isWordOrNumberAllownEmpty(String str) {  
		if(str==null) return false;
		if(str.trim().length()==0) return true;
		return str.trim().matches("[A-Za-z0-9-]{1,}");
    }
	
	public static boolean isDateAllownEmpty(String str) {  
		if(str==null) return false;
		if(str.trim().length()==0) return true;
		return str.trim().matches("[0-9-]{8}");
    }
	public static boolean isIntAllownEmpty(String str) {  
		if(str==null) return false;
		if(str.trim().length()==0) return true;
		try {
			Integer.parseInt(str.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
    }
	/**
	 * 金额格式，0-2位小数
	 * @return
	 */
	public static boolean isMoney(String moneyStr){
		if(moneyStr==null) return false;
		if(moneyStr.trim().length()==0) return true;
		return moneyStr.trim().matches("^(([1-9][0-9]*)|0)(\\.[0-9]{1,2})?$");
	}
	/**
	 * 过滤特殊字符
	 * $% @ ,/\<>"'%;() & +
	 * @param str
	 * @return
	 */
	public static String cleanStr(String str){
		return str.replaceAll("[<>\"'%;()&+@$/\\\\]", "");
	}
	
	
	/**
	 * 手机短信内容（）手机支付
	 * @param str
	 * @return
	 */
	public static String genPhonePaySMSContent(String chkNo,String mname,String ordId,String payTime,String tseq){
		String SMS_CON = ParamCache.getStrParamByName("SMS_CON");
		return SMS_CON.replace("%0",chkNo).replace("%1",mname).replace("%2", ordId).replace("%3", payTime).replace("%4",tseq);
	}
	
	/**
	 * 从字符串中区数字
	 * 2011-11-12 - 20111112
	 * 2011年11月12日->21011112
	 * 1,990->1990
	 * @param str
	 * @return
	 */
	public static String getDigitFromString(String str){
		if(str==null) return "0";
		return str.replaceAll("[^0-9]","").trim();
	}
	
	/**
	 * 是数字
	 * @param str
	 * @return
	 */
	public static boolean isDigit(String str) {
		if(str==null) return false;
		if(str.trim().length()==0) return false;
		return str.matches("[0-9-]{1,}");
	}
	/**
	 * 数字的四舍五入,保留两位小数
	 * @param number
	 * @return
	 */
	public static String formathHalfUp(double number){
		   BigDecimal   bd=new   BigDecimal(number);     
		   double newNum=bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
//		 DecimalFormat df = new DecimalFormat("#.00"); 
//		 df.format(number);
	     return String.valueOf(newNum);
	}
	/**
	 * 产生随机数
	 * @param maxNumber
	 * @return 产生的随机数 
	 */
	public static String createRandomStr(int maxNumber){
		String randStr="";
		Random random=new Random();
		for (int i = 0; i < maxNumber; i++) {
			int r=random.nextInt(10);
			randStr=randStr+r;
		}
		return randStr;
	}
	
	/**
	 * 判断是否是图片，支持图片格式：jpg、jpeg、gif、png、bmp
	 * @param fileName
	 * @return
	 */
	public static boolean isPicture(String fileName){		
		if(empty(fileName) || fileName.indexOf(".") == -1 || fileName.endsWith("."))
			return false;
		
		String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
		if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg")
				|| suffix.equalsIgnoreCase("gif")
				|| suffix.equalsIgnoreCase("png")
				|| suffix.equalsIgnoreCase("bmp"))
			return true;
		
		return false;
	}
	/***
	 * 判断余额是否可以进行支付或体现
	 * @return
	 */
	public static boolean isPay(long balance,long transAmt) {
			if(balance<transAmt){
				return false;
			}
			return true;
	}
	
	public static String creatSpecifyValue(int Num,String value){
		StringBuffer sbStr=new StringBuffer();
		for (int i = 0; i < Num; i++) {
			sbStr.append(value);
		}
		return sbStr.toString();
	}
	
	/**
	 * 是否启动服务
	 * @return
	 */
	public static boolean isStartService(String msg){
		boolean flag = false;

		String dbConfig = ParamCache.getStrParamByName("TMS_IP");
		InetAddress addr = OSUtil.getLocalIP();
		String address = addr.getHostName();// 获得本机名称
		if (!Ryt.empty(address) && address.equalsIgnoreCase(dbConfig.trim()))
			flag = true;
		
		Map<String, String> logParams = LogUtil.createParamsMap();
		logParams.put("hostname", address);
		logParams.put("dbConfig", dbConfig);
		logParams.put("isStartService", String.valueOf(flag));
		LogUtil.printInfoLog("Ryt", "isStartService", (Ryt.empty(msg) ? "" : msg.trim()), logParams);
		
		return flag;
	}
	
	public static String readStream(InputStream input) throws IOException{
		byte[] b=new byte[1024];
		StringBuffer str=new StringBuffer();
		int len = 0;
		while((len = input.read(b))!=-1){
			str.append(new String(b, 0, len));
		}
		input.close();
		return str.toString().trim();
	}
	
	// 得到前一天的时间
	public static String getSpecifiedDayBefore(String specifiedDay) throws Exception {
		String curdate = specifiedDay.substring(0, 4) + "-"+ specifiedDay.substring(4, 6) + "-"+ specifiedDay.substring(6, specifiedDay.length());
		Calendar c = Calendar.getInstance();
		Date date = null;
		date = new SimpleDateFormat("yyyy-MM-dd").parse(curdate);
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
		return dayBefore;
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
	
	/**
	 * post请求
	 * @param requestParaMap
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String requestByPostwithURL(
			Map<String, String> requestParaMap, String url) throws IOException {
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		String sResponseBody = "";
		NameValuePair[] nameValuePairs = null;
		NameValuePair nameValuePair = null;
		try {
			if (requestParaMap != null && requestParaMap.size() > 0) {
				nameValuePairs = new NameValuePair[requestParaMap.size()];
				int i = 0;
				Iterator it = requestParaMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry element = (Map.Entry) it.next();
					nameValuePair = new NameValuePair();
					nameValuePair.setName(String.valueOf(element.getKey()));
					nameValuePair.setValue(String.valueOf(element.getValue()));
					nameValuePairs[i++] = nameValuePair;
				}
				method.setRequestBody(nameValuePairs);
				httpClient.executeMethod(method);
				int resCode = method.getStatusCode();
				if (resCode == HttpStatus.SC_OK) {
//					sResponseBody = method.getResponseBodyAsString();
					InputStream input = method.getResponseBodyAsStream();
					sResponseBody = Ryt.readStream(input);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return sResponseBody;
	}
	
	public static String desEnc(String body){
		String result="";
		 try {
			 result=DesEncrypto.encrypt(body, Constant.EWP_DF_MD5_KEY);
		} catch (Exception e) {
			return body;
		}
		 return result;
	}
	
	public static String desDec(String deStr){
		String result="";
		 try {
			 result=DesEncrypto.decrypt(deStr, Constant.EWP_DF_MD5_KEY);
		} catch (Exception e) {
			return deStr;
		}
		 return result;
	}
	
	public static String minfoSetHandle(String str){
		if(str==null ) {
			return null;
		}
		if (!str.equals(Ryt.desDec(str))) {
			return str;
		} else {
			String tn=Ryt.desEnc(str);
			return tn;
		}
	}
	
	public static String minfoGetHandle(String str){
		if(str==null) {
			return null;
		}
		if (!str.equals(Ryt.desDec(str))) {
			return Ryt.desDec(str);
		}else{
			return str;
		}
	}
    

    /***
     * 返回加*的账户号
     * @param acc
     * @return
     */
    public static String shadowAcc(String acc){
        int len=acc.length();
        if(6>len&&0<len){
           return acc+"******"+acc; 
        }else if("".equals(acc)){
            return "";
        }
        String head=acc.substring(0,6);
        String end=acc.substring(len - 4);
        return head+"******"+end;
    }
    /**
     * 返回加*号的手机号
     * @param phone
     * @return
     */
    public static String shadowPhone(String phone){
        int len=phone.length();
        if(4>len&&0<len){
           return phone+"****"+phone;
        }else if("".equals(phone)){
            return "";
        }
        String head=phone.substring(0,3);
        String end=phone.substring(len - 4);
        return head+"****"+end;
    }


    /***
     * Bean 获取添加权限控制后的属性值
     * @param user
     * @param param
     * @return
     */
    public static String getProperty(LoginUser user,String param){
        if(user == null){
            LogUtil.printInfoLog("Ryt", "getProperty", "user is null");
			param = "";
			return param;
		}

		String auth = user.getAuth();
        String mid=user.getMid();
        param = desDec(param);
		if (auth == null) {
			param = "";
        }else if(!"1".equals(mid)){
            param=param;
		} else if (auth.length() > 105 && auth.charAt(105) == '1') {
			param = param;
		} else {
			if (param == null) {
				param = "";

			} else if (param.length() > 10) {
                if(param.length()==11){
                       param = param.substring(0, 3)+ "****"+ param.substring(param.length() - 4,
								param.length());
                }else{
                       param = param.substring(0, 6)+ "****"+ param.substring(param.length() - 4,
								param.length());
                }
				
			} else {
				param = param;
			}
		}

        return param;
    }
}
