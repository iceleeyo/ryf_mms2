package com.rongyifu.mms.refund;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.LogUtil;

public class RefundUtil {
	
	/**
	 * 支持联机退款的银行网关
	 */
	public static final String gateList = "4,10,11,55000,55001,56000,56001,56002,90000,90001,90009,90010,90011,90016,56113,10901,10902";
	
	// 退款申请被受理
	public static final String ORDER_STATUS_ACCEPT  = "5";
	// 失败状态
	public static final String ORDER_STATUS_FAILURE = "3";
	// 处理中
	public static final String ORDER_STATUS_PROCESSED = "1";
	// 请求银行失败
	public static final String QUERT_BANK_FAILURE = "4";
	//退款成功
	public static final String ORDER_STATUS_SUCCESS = "2";
	
	private static Map<String, String> reFundGateList = null;
	
	public static Map<String, String> getRefundGateList(){
		return reFundGateList;
	}
	static{
		reFundGateList = new HashMap<String, String>();
		// 支付宝控件支付 - 海航
		reFundGateList.put("56000", "com.rongyifu.mms.refund.bank.AlipayHHRefund");
		// 支付宝控件支付 - 首航
		reFundGateList.put("56001", "com.rongyifu.mms.refund.bank.AlipaySHRefund");
		// 支付宝控件支付 - 祥鹏
		reFundGateList.put("56002", "com.rongyifu.mms.refund.bank.AlipayXPRefund");
		// 支付宝wap - 海航
		reFundGateList.put("90009", "com.rongyifu.mms.refund.bank.AlipayHHRefund");
		// 支付宝wap - 首航
		reFundGateList.put("90010", "com.rongyifu.mms.refund.bank.AlipaySHRefund");
		// 支付宝wap - 祥鹏
		reFundGateList.put("90011", "com.rongyifu.mms.refund.bank.AlipayXPRefund");
		// 银联手机支付
		reFundGateList.put("55000", "com.rongyifu.mms.refund.bank.UnionPayRefund");
		// 银联UPMP
		reFundGateList.put("55001", "com.rongyifu.mms.refund.bank.UPMPRefund");
		// 招行wap
		reFundGateList.put("90000", "com.rongyifu.mms.refund.bank.CMBRefund");
		// 招行wap(借)
		reFundGateList.put("90001", "com.rongyifu.mms.refund.bank.CMBRefund");
		// 快钱 - 东航
		reFundGateList.put("4", "com.rongyifu.mms.refund.bank.KqRefund");
		// 银联wap
		reFundGateList.put("90016", "com.rongyifu.mms.refund.bank.UnionPayRefund2");
		// 快钱 - 国航
		reFundGateList.put("10", "com.rongyifu.mms.refund.bank.KqRefund");
		// 快钱 - 海航
		reFundGateList.put("11", "com.rongyifu.mms.refund.bank.KqRefund");
		// 易宝 - 信用卡支付
		reFundGateList.put("56113", "com.rongyifu.mms.refund.bank.EposRefund");
		// 盛京B2C支付 - 综合通道
		reFundGateList.put("10901", "com.rongyifu.mms.refund.bank.SJRefund");
		// 盛京B2C支付 - 借记卡支付
		reFundGateList.put("10902", "com.rongyifu.mms.refund.bank.SJRefund");
	}
	//md5加密
	public static String md5Encrypt(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}
	//请求银行
	@SuppressWarnings("rawtypes")
	public static String requestByPostwithURL(
			Map<String, String> requestParaMap, String url) throws IOException {
		LogUtil.printInfoLog("alipay refunb", requestParaMap);
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
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
	
	/*如下是银联退款请求处理代码，
	 * 和requestByPostwithURL代码一样,
	 * 建议进行优化
	   请求银行*/
	@SuppressWarnings("rawtypes")
	public static String requestByPostwithURL_unionpay(
			Map<String, String> requestParaMap, String url) throws IOException {
		LogUtil.printInfoLog("unionpay refund", requestParaMap);
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
//						sResponseBody = method.getResponseBodyAsString();
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
		
	/**
	 * 查询ewp地址
	 */
	public static String querywep() {
		SystemDao dao = new SystemDao();
		return dao.queryewp();
	}
}
