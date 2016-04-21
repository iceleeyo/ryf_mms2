package com.rongyifu.mms.bank.query;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;
/**
 * 易宝单笔订单查询
 * @author yang.yaofeng
 *
 */
public class YeePayQuery  extends ABankQuery{
	// 取文本流时的缓存大小
	private static final int tempLength = 1024; 
	private static String KEYVALUE="69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl";
	private static String EPOSREQURL ="https://www.yeepay.com/app-merchant-proxy/command";
	private static String hmac_CONNECT_Flag = "";
	// url中每组参数间的分隔符
	private static final String PARAM_CONNECT_FLAG = "&";
	// url中地址与参数间的分隔符
	private static final String URL_PARAM_CONNECT_FLAG = "?";
	// url中每组参数中键与值间的分隔符
	private static final String KEY_VALUE_CONNECT_FLAG = "=";
	private static final String get = "get";
	private static final String post = "post";
	private static String encodingCharset = "UTF-8";
	private static String MERCHANTID = "10001126856";
	private static String ServerCharsetName = "iso-8859-1";
	private static String YeepayCharsetName = "gbk";
	public static final String CHECKHMAC = "checkHmac";
	public static final String ResultString = "resultString";
	public static final String Parameter = "parameter";
	public static final String HTTPConnection = "httpConnection";
	public static final String GroupFlag = "\n";
	public static final String KeyValueFlag = "=";
	private static final String reqEncodeCode = "gbk";
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		Map parameterMap = new HashMap();
		parameterMap.put("p2_Order", order.getTseq()+"");
		parameterMap.put("p3_ServiceType",0);
		Map map = getEposQueryBackMap(parameterMap);
		Boolean checkHmac = (Boolean) map.get("checkHmac");
		Map parameter = (Map) map.get("parameter");
		if (Boolean.TRUE.equals(checkHmac)) {
			LogUtil.printInfoLog("YeePayQuery", "queryOrderStatusFromBank","",parameter);
			String status=(String)parameter.get("rb_PayStatus");
			if(!Ryt.empty(status)){
				if(status.trim().equals("SUCCESS")){
					queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					return queryRet;
				}else{
					queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
					return queryRet;
				}
			}
		}
		queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		return queryRet;
	}
	public static Map getEposQueryBackMap(Map parameterMap){
		String keyValue =KEYVALUE;//密钥
		String url = EPOSREQURL;//请求路径
		Map fixParameter = getEposQueryFixParameter();
		String[] hmacOrder = getEposQueryHmacOrder();
		String[] backHmacOrder = getEposQueryBackHmacOrder();
		return getRequestBackMap(parameterMap, keyValue, url, fixParameter, hmacOrder, backHmacOrder);
	}
	public static Map getEposQueryFixParameter(){
		Map returnMap = new HashMap();
		returnMap.put("p0_Cmd", "QueryOrdDetail");
		returnMap.put("p1_MerId", MERCHANTID);
		returnMap.put("pv_Ver", "3.0");
		return returnMap;
	}
	// 查询请求参数
		public static String[] getEposQueryHmacOrder(){
			return new String[] {"p0_Cmd","p1_MerId","p2_Order","pv_Ver","p3_ServiceType"};
		}
		public static String[] getEposQueryBackHmacOrder(){
			return new String[] {"r0_Cmd","r1_Code","r2_TrxId","r3_Amt","r4_Cur","r5_Pid","r6_Order","r8_MP",
					     "rw_RefundRequestID","rx_CreateTime","ry_FinshTime","rz_RefundAmount","rb_PayStatus",
					     "rc_RefundCount","rd_RefundAmt"};
		}
		public static Map getRequestBackMap(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder, String[] backHmacOrder){
			//将参数转换
			parameterMap = changeMapCharset(parameterMap, ServerCharsetName, YeepayCharsetName);
			String reqResult = getRequestBackString(parameterMap, keyValue, url, fixParameter, hmacOrder);
			return getRequestBackMap(reqResult, keyValue, url, fixParameter, hmacOrder, backHmacOrder);
		}
		public static Map getRequestBackMap(String reqResult, String keyValue, String url, Map fixParameter, String[] hmacOrder, String[] backHmacOrder){
			Map returnMap = new HashMap();
			if(reqResult == null){
				returnMap.put(HTTPConnection, Boolean.valueOf(false));
			}else{
				returnMap.put(HTTPConnection, Boolean.valueOf(true));
				Map parameterMap = formatReqReturnString(reqResult, GroupFlag, KeyValueFlag);
				parameterMap = urlDecodeMap(parameterMap);
				returnMap.put(CHECKHMAC, Boolean.valueOf(checkHmac(backHmacOrder, parameterMap,  keyValue)));
				returnMap.put(Parameter, parameterMap);
			}
			return returnMap;
		}
		// 检查map中的hmac与在map中的以HmacOrder为键的值所组成的hmac是否一致
		public static boolean checkHmac(String[] HmacOrder,Map map, String keyValue){
			boolean returnBoolean = false;
			Object hmacObj = map.get("hmac");
			String hmac = (hmacObj == null) ? "" : (String)hmacObj ;
			String sbold = getHmacSBOld(HmacOrder, map);
			String newHmac = "";
			newHmac = hmacSign(sbold, keyValue);
			if(hmac.equals(newHmac)){
				returnBoolean = true;
			}
			return returnBoolean; 
		}
		// 格式化http通讯返回文本流的方法
		public static Map formatReqReturnString(String str, String groupFlag, String keyValueFlag ){
			Map returnMap = new HashMap();
			String[] groups = str.split(groupFlag);
			String[] group = new String[2];
			String key = "";
			String value = "";
			int index = groups.length;

			for(int i = 0; i < index; i++){
				group = groups[i].split(keyValueFlag);
				if(group.length >= 1 ){
					key = group[0];
				}
				if(group.length >= 2 ){
					value = group[1];
				}else{
					value = "";
				}
				returnMap.put(key, value);
			}
			return returnMap; 
		}
		public static Map changeMapCharset(Map map, String beforeChangeCharsetName, String afterChangeCharsetName){
			if(beforeChangeCharsetName.equals(afterChangeCharsetName)){
				return map;
			}
			Set keySet = map.keySet();
			Object[] objs = keySet.toArray();
			for(int i = 0; i < objs.length; i++){
				String key = objs[i].toString();
				String value =map.get(key)+"";
				if(value == null){
					value = "";
				}else{
					try {
						value = new String(value.getBytes(beforeChangeCharsetName), afterChangeCharsetName);
					} catch (UnsupportedEncodingException uee) {
						uee.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				map.put(key, value);
			}
			return map;
		}
		public static String getRequestBackString(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder){
			parameterMap = setFixParameterValue(parameterMap, fixParameter);
			parameterMap = addHmac(hmacOrder, parameterMap, keyValue);
			String returnString = sendRequest(url, parameterMap,"get");
			return returnString;
			
		}
		// 发送http通讯请求
		public static String sendRequest(String url, Map parameter, String method){
			String returnString = "";
			String content = getContentURL(parameter);
			returnString = sendRequest(url, content, method);
			return returnString;
		}
		// 发送http通讯请求
		public static String sendRequest(String url, String content, String method){
			String returnString = "";
			try{
				if(post.equalsIgnoreCase(method)){
					returnString = URLPost(url, content).toString();
				}else{
					returnString = URLGet(url, content).toString();
				}
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			return returnString;
		}
		public static String getContentURL(Map parameterMap){
			if (null == parameterMap || parameterMap.keySet().size() == 0) {
				return ("");
			}
			StringBuffer url = new StringBuffer();
			Set keys = parameterMap.keySet();
			for (Iterator i = keys.iterator(); i.hasNext(); ) {
				String key = String.valueOf(i.next());
				if (parameterMap.containsKey(key)) {
					Object val = parameterMap.get(key);
					String str = val!=null?val.toString():"";
					try {
						str = URLEncoder.encode(str, reqEncodeCode);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					url.append(key).append(KEY_VALUE_CONNECT_FLAG).append(str).append(PARAM_CONNECT_FLAG);
				}
			}
			String strURL = "";
			strURL = url.toString();
			if (PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
				strURL = strURL.substring(0, strURL.length() - 1);
			}
			return strURL;
		}
		public static StringBuffer URLGet(String strUrl, String content) throws IOException {
			String strTotalURL = "";
			strTotalURL = getTotalURL( strUrl, content);
	        		//URL url = new URL(strTotalURL);
	        URL url=new URL(null, strTotalURL,new sun.net.www.protocol.https.Handler());

			if(strUrl.trim().startsWith("https")){
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setUseCaches(false);
				HttpURLConnection.setFollowRedirects(true);
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			}else{
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setUseCaches(false);
				HttpURLConnection.setFollowRedirects(true);
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			}
		}
		public static String getTotalURL(String strUrl, String content){
			String totalURL = strUrl;
			if(totalURL.indexOf(URL_PARAM_CONNECT_FLAG) == -1) {
				totalURL += URL_PARAM_CONNECT_FLAG;
			} else {
				totalURL += PARAM_CONNECT_FLAG;
			}
			totalURL += content;
			return totalURL;
		}
		// 设置固定值的方法
		public static Map setFixParameterValue(Map parameterMap, Map fixParameterMap){
			Object[] keys = fixParameterMap.keySet().toArray();
			int index = keys.length;
			String key = "";
			String value = "";
			for(int i = 0; i < index; i++){
				key = keys[i].toString();
				value = fixParameterMap.get(key).toString();
				parameterMap.put(key, value);
			}
			return parameterMap;
		}
		public static Map addHmac(String[] HmacOrder, Map map, String keyValue){
			map = formatMap(HmacOrder, map);
			//		String sbold ="EposTransaction100011268562012113011284870.00CNYhttp://192.168.66.112:4002/bk/yeepay_RetIDCARD12312412423142320131213888888888yyf13131313441";
			String sbold = getHmacSBOld(HmacOrder, map);
//			System.out.println("sbold:"+sbold);
			String hmac = "";
			String str = sbold; 
			hmac = hmacSign(sbold, keyValue);
//			System.out.println("hmac:"+hmac);
			map.put("hmac", hmac);
			return map;
		}
		// 格式化参数Map，如HmacOrder中有的参数名在Map中没有此键值对的话，在Map中添加键值对
		public static Map formatMap(String[] HmacOrder, Map map){
			String key = "";
			String value = "";
			for(int i = 0; i < HmacOrder.length; i++){
				key = HmacOrder[i];
				value = (String)map.get(key);
				if(value == null){
					map.put(key, "");
				}
			}
			return map;
		}
		// 获得生成hmac时需要的sbold
		public static String getHmacSBOld(String[] HmacOrder,Map map){
			return getHmacSBOld(HmacOrder, map, hmac_CONNECT_Flag  );
		}
		// 获得生成hmac时需要的sbOld
		public static String getHmacSBOld(String[] HmacOrder,Map map, String connectFlag){
			int index = HmacOrder.length;
			String[] args = new String[index];
			String key = "";
			String value = "";
			for(int i = 0; i < index; i++){
				key = HmacOrder[i];
					value = (String)map.get(key);
					if(value == null){
						value = "";
					}
					args[i] = value;
			}
			return getHmacSBOld(args, connectFlag);
		}
		// 获得生成hmac时需要的sbOld
		public static String getHmacSBOld(String[] args, String connectFlag){
			StringBuffer returnString = new StringBuffer();
			int index = args.length;
			for(int i = 0; i < index; i++)
			{
				returnString.append(args[i]).append(connectFlag);
			}
			return returnString.substring(0, returnString.length() - hmac_CONNECT_Flag.length());
		}
		public static String hmacSign(String aValue, String aKey) {
			byte k_ipad[] = new byte[64];
			byte k_opad[] = new byte[64];
			byte keyb[];
			byte value[];
			try {
				String str = aValue; 
				keyb = aKey.getBytes(encodingCharset);
				value = aValue.getBytes(encodingCharset);
			} catch (UnsupportedEncodingException e) {
				keyb = aKey.getBytes();
				value = aValue.getBytes();
			}
			Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
			Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
			for (int i = 0; i < keyb.length; i++) {
				k_ipad[i] = (byte) (keyb[i] ^ 0x36);
				k_opad[i] = (byte) (keyb[i] ^ 0x5c);
			}
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			md.update(k_ipad);
			md.update(value);
			byte dg[] = md.digest();
			md.reset();
			md.update(k_opad);
			md.update(dg, 0, 16);
			dg = md.digest();
			return toHex(dg);
		}
		public static String toHex(byte input[]) {
			if (input == null)
				return null;
			StringBuffer output = new StringBuffer(input.length * 2);
			for (int i = 0; i < input.length; i++) {
				int current = input[i] & 0xff;
				if (current < 16)
					output.append("0");
				output.append(Integer.toString(current, 16));
			}
			return output.toString();
		}
		public static StringBuffer URLPost(String strUrl, Map map) throws IOException {
			String content = getContentURL(map);
			//URL url = new URL(strUrl);
	        URL url=new URL(null, strUrl,new sun.net.www.protocol.https.Handler());
			if(strUrl.trim().startsWith("https")){
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				int length = con.getContentLength();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setAllowUserInteraction(false);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
				BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
				bout.write(content);
				bout.flush();
				bout.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			}else{
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int length = con.getContentLength();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setAllowUserInteraction(false);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
				BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
				bout.write(content);
				bout.flush();
				bout.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			}
		}
		public static StringBuffer URLPost(String strUrl, String content) throws IOException {

	        		//URL url = new URL(strUrl);
	        URL url=new URL(null, strUrl,new sun.net.www.protocol.https.Handler());
			if(strUrl.trim().startsWith("https")){
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				int length = con.getContentLength();
			con.setDoInput(true);
//				con.setDoOutput(true);
				con.setAllowUserInteraction(false);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
				BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
				bout.write(content);
				bout.flush();
				bout.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			} else {
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int length = con.getContentLength();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setAllowUserInteraction(false);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
				BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
				bout.write(content);
				bout.flush();
				bout.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				return getStringBufferFormBufferedReader(in);
			}
		}
		private static StringBuffer getStringBufferFormBufferedReader(BufferedReader in) throws IOException{
			StringBuffer returnStringBuffer = new StringBuffer();
			char[] tmpbuf= new char[tempLength];
			int num = in.read(tmpbuf);
			while(num != -1){
				returnStringBuffer.append(tmpbuf, 0, num);
				num = in.read(tmpbuf);
			}
			in.close();
			return returnStringBuffer;
		}
		public static Map urlDecodeMap(Map parameterMap){
			Map returnMap = new HashMap();
			Set set = parameterMap.keySet();
			Iterator iterator  = set.iterator();
			String key = "";
			String value = "";
			while(iterator.hasNext()){
				key = (String)iterator.next();
				value = (String)parameterMap.get(key);
				value = URLDecoder.decode(value);
				returnMap.put(key, value);
			}
			return returnMap;
			
		}
		private static void trustAllHttpsCertificates() {
			try {
				// Create a trust manager that does not validate certificate chains:
				javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
				javax.net.ssl.TrustManager tm = new MyTrustManager();
				trustAllCerts[0] = tm;
				javax.net.ssl.SSLContext sc;
				sc = javax.net.ssl.SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, null);
				javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		private static HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
//				System.out.println("Warning: URL Host: " + arg0 + " vs. " + arg1.getPeerHost());
				return true;
			}
		};
}
class MyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}
	public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
		return true;
	}
	public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
		return true;
	}
	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
		return;
	}
	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
		return;
	}
}
