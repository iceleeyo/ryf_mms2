package com.rongyifu.mms.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.rongyifu.mms.common.Ryt;

/**
 * 如果你是SP，上行就是从你往运营商出发送，下行就是运营商给你返回的。 如果你是普通用户，那么上行就是你往运营商发送的，下行则是运营商发给你的
 * @author Administrator
 */
public class SMSUtil {

	/**
	 * 下面是command
	 */
	public final static String MT_REQUEST = "MT_REQUEST";// 下行请求消息
	public final static String MT_RESPONSE = "MT_RESPONSE";// 下行响应消息
	public final static String MULTI_MT_REQUEST = "MULTI_MT_REQUEST";// 批量下行请求消息(相同内容)
	public final static String MULTI_MT_RESPONSE = "MULTI_MT_RESPONSE";// 批量下行响应消息(相同内容)
	public final static String MULTIX_MT_REQUEST = "MULTIX_MT_REQUEST";// 批量下行请求消息(不同内容)
	public final static String MULTIX_MT_RESPONSE = "MULTIX_MT_RESPONSE";// 批量下行响应消息(不同内容)
	public final static String BATCH_MT_REQUEST = "BATCH_MT_REQUEST";// 文件群发下行请求消息
	public final static String BATCH_MT_RESPONSE = "BATCH_MT_RESPONSE";// 文件群发下行响应消息
	public final static String MO_REQUEST = "MO_REQUEST";// 上行请求消息
	public final static String MO_RESPONSE = "MO_RESPONSE";// 上行响应消息
	public final static String RT_REQUEST = "RT_REQUEST";// 状态报告请求消息
	public final static String RT_RESPONSE = "RT_RESPONSE";// 状态报告响应消息
	public final static String ERROR_RESPONSE = "ERROR_RESPONSE";// 错误响应消息
	/**
	 * 用户名
	 */
	public final static String SPID = "3417";
	/**
	 * 密码
	 */
	public final static String SPPASSWORD = "iopjkl";
	/**
	 * 请求url
	 */
	public final static String MTURL = "http://esms.etonenet.com/sms/mt";
	/**
	 * sp服务代码，可选参数，默认为 00
	 */
	public final static String SPSC = "00";
	/**
	 * 下行内容以及编码格式，必填参数,此处dc设为15，即使用GBK编码格式
	 */
	public static final int DC = 15;
	/**
	 * 电话号码前缀
	 */
	public static final String BEFORE_TEL = "86";
	public static final String CODE = "/";

	// public static void main(String[] args) throws Exception {
	//    	
	// System.out.println("sssssssssssssssssss");
	//        
	// sendDownLineSingleMt("13436612929", "testAbcd123");
	//        
	// // sendUpLineMultiMt(new String[]{"13681593158", "13261330690"}, "你好");
	// // sendUpLineMultiXMt(new String[][]{{"13681593158","华仔"},{"13261330690","我自己"}});
	// }

	public static String send(String dest, String content) {

		return sendSMS(dest, content);
	}

	/**
	 * 短信发送
	 * @param da 目标地址,多个地址 用，隔开
	 * @param content 短信内容
	 * @return command=MT_RESPONSE&spid=7770&mtmsgid=12827158660066085&mtstat=ACCEPTD&mterrcode=000
	 */
	public static String sendSMS(String da, String content) {

		String ret = "";
		if (Ryt.empty(da)) {
			return "";
		}
		// 多个手机号码
		if (da.contains(",")) {

			String[] das = da.split(",");
			ret = sendDownLineMultiMt(das, content);

		} else {// 单条短信
			ret = sendDownLineSingleMt(da, content);
		}

		if (ret.contains("mterrcode=")) {

			if (ret.split("mterrcode=")[1].equals("000")) {
				return "success";
			}
		}
		Ryt.print(ret);
		return ret;

	}

	/**
	 * 单条下行短信发送
	 * @param da 目标地址
	 * @param content 短信内容
	 */
	public static String sendDownLineSingleMt(String da, String content) {
		String sm = encodeHexStr(DC, content);// 下行内容进行Hex编码
		String smsUrl = MTURL + "?command=" + MT_REQUEST + "&spid=" + SPID + "&sppassword=" + SPPASSWORD + "&spsc="
						+ SPSC + "&sa=" + "" + "&da=" + BEFORE_TEL + da + "&sm=" + sm + "&dc=" + DC;// 组成url字符串
		return doGet(smsUrl.toString());// 发送http请求，并接收http响应
	}

	/**
	 * 下行相同内容群发示例
	 * @param da 目标地址
	 * @param content 短信内容
	 */
	public static String sendDownLineMultiMt(String[] da, String content) {
		StringBuffer das = new StringBuffer();
		for (int i = 0; i < da.length; i++) {
			if (i != da.length - 1) {
				das.append(BEFORE_TEL).append(da[i]).append(",");
			} else {
				das.append(BEFORE_TEL).append(da[i]);
			}
		}

		String sm = encodeHexStr(DC, content);
		// 组成url字符串
		String smsUrl = MTURL + "?command=" + MULTI_MT_REQUEST + "&spid=" + SPID + "&sppassword=" + SPPASSWORD
						+ "&spsc=" + SPSC + "&sa=" + "" + "&das=" + das + "&sm=" + sm + "&dc=" + DC;
		return doGet(smsUrl.toString());
	}

	/**
	 * 下行不同内容群发示例
	 * @param daAndContent 目标地址和短信内容 格式[a][b] a代表目标电话号码 b代表内容 并且b的长度是2
	 */
	public static void sendDownLineMultiXMt(String[][] daAndContent) {
		StringBuilder dasms = new StringBuilder();
		for (int i = 0; i < daAndContent.length; i++) {
			if (i == 0) {
				dasms.append(BEFORE_TEL).append(daAndContent[i][0]).append(CODE);
				dasms.append(encodeHexStr(DC, daAndContent[i][1]));
			} else {
				dasms.append(",").append(BEFORE_TEL).append(daAndContent[i][0]).append(CODE);
				dasms.append(encodeHexStr(DC, daAndContent[i][1]));
			}
		}
		String smsUrl = MTURL + "?command=" + MULTIX_MT_REQUEST + "&spid=" + SPID + "&sppassword=" + SPPASSWORD
						+ "&spsc=" + SPSC + "&sa=" + "" + "&dasm=" + dasms.toString() + "&dc=" + DC;
		// String resStr = doPost(smsUrl.toString());
		doPost(smsUrl.toString());
	}

	/**
	 * 单条上行实例
	 * @param da 目标地址
	 * @param content 短信内容
	 * @throws Exception
	 */
	public static String sendUpLineSingleMt(String da, String content) {
		String sm = encodeHexStr(DC, content);// 下行内容进行Hex编码
		StringBuilder smsUrl = new StringBuilder();
		smsUrl.append(MTURL);
		smsUrl.append("?command=" + MT_REQUEST);
		smsUrl.append("&spid=" + SPID);
		smsUrl.append("&sppassword=" + SPPASSWORD);
		smsUrl.append("&spsc=" + SPSC);
		smsUrl.append("&sa=" + "");
		smsUrl.append("&da=").append(BEFORE_TEL).append(da);
		smsUrl.append("&sm=").append(sm);
		smsUrl.append("&dc=").append(DC);
		// 发送http请求，并接收http响应
		String resStr = doGet(smsUrl.toString());
		return resStr;
	}

	/**
	 * 上行相同内容群发实例
	 * @param da 目标地址
	 * @param content 短信内容
	 * @throws Exception
	 */
	public static String sendUpLineMultiMt(String[] da, String content) {
		StringBuffer das = new StringBuffer();
		for (int i = 0; i < da.length; i++) {
			if (i != da.length - 1) {
				das.append(BEFORE_TEL).append(da[i]).append(",");
			} else {
				das.append(BEFORE_TEL).append(da[i]);
			}
		}
		String sm = encodeHexStr(DC, content);
		StringBuilder smsUrl = new StringBuilder();
		smsUrl.append(MTURL);
		smsUrl.append("?command=" + MULTI_MT_REQUEST);
		smsUrl.append("&spid=" + SPID);
		smsUrl.append("&sppassword=" + SPPASSWORD);
		smsUrl.append("&spsc=" + SPSC);
		smsUrl.append("&sa=" + "");
		smsUrl.append("&das=").append(das);
		smsUrl.append("&sm=").append(sm);
		smsUrl.append("&dc=").append(DC);
		String resStr = doGet(smsUrl.toString());
		return resStr;
	}

	/**
	 * 上行不同内容群发实例
	 * @param daAndContent 目标地址和短信内容 格式[a][b] a代表目标电话号码 b代表内容 并且b的长度是2
	 * @throws Exception
	 */
	public static void sendUpLineMultiXMt(String[][] daAndContent) {
		StringBuilder dasms = new StringBuilder();
		for (int i = 0; i < daAndContent.length; i++) {
			if (i == 0) {
				dasms.append(BEFORE_TEL).append(daAndContent[i][0]).append(CODE);
				dasms.append(encodeHexStr(DC, daAndContent[i][1]));
			} else {
				dasms.append(",").append(BEFORE_TEL).append(daAndContent[i][0]).append(CODE);
				dasms.append(encodeHexStr(DC, daAndContent[i][1]));
			}

		}
		// 组成url字符串
		StringBuilder smsUrl = new StringBuilder();
		smsUrl.append(MTURL);
		smsUrl.append("?command=" + MULTIX_MT_REQUEST);
		smsUrl.append("&spid=" + SPID);
		smsUrl.append("&sppassword=" + SPPASSWORD);
		smsUrl.append("&spsc=" + SPSC);
		smsUrl.append("&sa=" + "");
		smsUrl.append("&dasm=").append(dasms.toString());
		smsUrl.append("&dc=").append(DC);
		// 发送http请求，并接收http响应
		// String resStr = doPost(smsUrl.toString());
		doPost(smsUrl.toString());
	}

	/**
	 * httpClient 方式 发送http GET请求，并返回http响应字符串
	 * @param urlstr 完整的请求url字符串
	 * @return
	 */
	public static String doGet(String urlstr) {
		String res = null;
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		HttpMethod httpmethod = new GetMethod(urlstr);
		try {
			int statusCode = client.executeMethod(httpmethod);
			if (statusCode == HttpStatus.SC_OK) {
				res = httpmethod.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpmethod.releaseConnection();
		}
		return res;
	}

	/**
	 * httpClient 方式 发送http POST请求，并返回http响应字符串
	 * @param urlstr 完整的请求url字符串
	 * @return
	 */
	public static String doPost(String urlstr) {
		String res = null;
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		HttpMethod httpmethod = new PostMethod(urlstr);
		try {
			int statusCode = client.executeMethod(httpmethod);
			if (statusCode == HttpStatus.SC_OK) {
				res = httpmethod.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpmethod.releaseConnection();
		}
		return res;
	}

	/**
	 * 将普通字符串转换成Hex编码字符串
	 * @param dataCoding 编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
	 * @param realStr 普通字符串
	 * @return Hex编码字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeHexStr(int dataCoding, String realStr) {
		String hexStr = null;
		if (realStr != null) {
			try {
				if (dataCoding == 15) {
					hexStr = new String(Hex.encodeHex(realStr.getBytes("GBK")));
				} else if ((dataCoding & 0x0C) == 0x08) {
					hexStr = new String(Hex.encodeHex(realStr.getBytes("UnicodeBigUnmarked")));
				} else {
					hexStr = new String(Hex.encodeHex(realStr.getBytes("ISO8859-1")));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return hexStr;
	}

	/**
	 * 将Hex编码字符串转换成普通字符串
	 * @param dataCoding 反编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
	 * @param hexStr Hex编码字符串
	 * @return 普通字符串
	 */
	public static String decodeHexStr(int dataCoding, String hexStr) {
		String realStr = null;
		try {
			if (hexStr != null) {
				if (dataCoding == 15) {
					realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "GBK");
				} else if ((dataCoding & 0x0C) == 0x08) {
					realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "UnicodeBigUnmarked");
				} else {
					realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "ISO8859-1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return realStr;
	}

	/**
	 * 将 短信下行 请求响应字符串解析到一个HashMap中
	 * @param resStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap parseResStr(String resStr) {
		HashMap pp = new HashMap();
		try {
			String[] ps = resStr.split("&");
			for (int i = 0; i < ps.length; i++) {
				int ix = ps[i].indexOf("=");
				if (ix != -1) {
					pp.put(ps[i].substring(0, ix), ps[i].substring(ix + 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pp;
	}
}
