package com.rongyifu.mms.bank.query;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;

/**
 * 支付宝首航
 * 
 * @author Administrator
 * 
 */
public class AlipayQuerySH extends ABankQuery {
	public static final String BANK_MERID = "2088901677635530";// 支付宝的ID
	public static final String INPUT_CHARSET = "gbk";
	public static final String SIGN_TYPE = "MD5";
	public static final String BANK_SERVICE = "single_trade_query";
	public static final String MD5_KEY = "xkbj43yw0ttldfx8o0qd8o4izu7cvfnw";

	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		BankQueryBean bankquerybean = new BankQueryBean();
		String oid = order.getTseq();// 订单号
		String url = "https://mapi.alipay.com/gateway.do";
		String[] param = { "service=" + BANK_SERVICE + "",
				"partner=" + BANK_MERID + "",
				"_input_charset=" + INPUT_CHARSET + "",
				"out_trade_no=" + oid + "" };
		// 排序
		Arrays.sort(param);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < param.length; i++) {
			sb.append(param[i]).append("&");
		}
		String sig = sb.toString();
		String sign = sig.substring(0, sig.lastIndexOf("&"));
		String sign_md5 = sign + MD5_KEY;
		String md5_str = md5Encrypt(sign_md5);
		Map<String, String> requestParaMap = new HashMap<String, String>();
		for (int i = 0; i < param.length; i++) {
			String[] tmp = param[i].split("=");
			requestParaMap.put(tmp[0], tmp[1]);
		}
		requestParaMap.put("sign", md5_str);
		requestParaMap.put("sign_type", SIGN_TYPE);
		try {
			String repose = requestByPostwithURL(requestParaMap, url);
			Document doc = DocumentHelper.parseText(repose);
			Element root = doc.getRootElement();
			String issuccess = root.elementText("is_success");
			if ("T".equals(issuccess)) {
				Element response = root.element("response");
				Element trade = response.element("trade");
				String trade_status = trade.elementText("trade_status");
				if (trade_status.equals("TRADE_SUCCESS")) {
					bankquerybean
							.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					return bankquerybean;
				}

			} else if ("F".equals(issuccess)) {
				String error = root.elementText("error");
				bankquerybean.setErrorMsg(error);
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			} else {
				bankquerybean.setErrorMsg("没有此状态！！");
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		return bankquerybean;
	}

	public static String requestByPostwithURL(
			Map<String, String> requestParaMap, String url) throws IOException {
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

	private static String md5Encrypt(String str) {
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

}
