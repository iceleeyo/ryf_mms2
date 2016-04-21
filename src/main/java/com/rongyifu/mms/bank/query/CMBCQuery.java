package com.rongyifu.mms.bank.query;

import java.io.IOException;
import java.io.InputStream;
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

import com.rongyifu.mms.bank.CMBC;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;

/**
 * 民生银行订单查询
 * 
 * @author zhang.chaochao
 * 
 */
public class CMBCQuery extends ABankQuery {
	//民生银行查询url地址
	private static final String URL_BANK_CMBC =
			"https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/B2C/servlet/orderQueryServlet";
	
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		BankQueryBean bankquerybean = new BankQueryBean();
		String oid = order.getTseq();// 订单号(测试：9200099389)
		String merNO = gate.getMerNo(); // 商户号(测试：00005)
		//民生订单号=商户号（5位） + 订单号（融易付）（15位）
		String oidCMBC = getOidOfCMBC(merNO, oid);
		//获取加密后的报文（明文=商户号码|订单号码）
		String oidCMBCencryption = "";
		String cmbcParams = merNO + "|" + oidCMBC;
		try {
			oidCMBCencryption = CMBC.wapSign(cmbcParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = URL_BANK_CMBC;
		Map<String, String> requestParaMap = new HashMap<String, String>();
		requestParaMap.put("cryptograph", oidCMBCencryption);
		try {
			String repose = requestByPostwithURL(requestParaMap, url).trim();
			Document doc = DocumentHelper.parseText(repose);
			Element root = doc.getRootElement();
			
			//读取返回加密报文数据
			String encryptData = root.element("body").element("div").getTextTrim();
			//若银行返回数据为异常信息
			if (encryptData.contains("验签失败")) {
				String error = root.elementText("验签失败");
				bankquerybean.setErrorMsg(error);
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			} else if (encryptData.contains("报文数据解密错误")) {
				String error = root.elementText("报文数据解密错误");
				bankquerybean.setErrorMsg(error);
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			} else if (encryptData.contains("报文数据项数目不对")) {
				String error = root.elementText("报文数据项数目不对");
				bankquerybean.setErrorMsg(error);
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			}
			//urldecode解码
			encryptData = java.net.URLDecoder.decode(encryptData, "utf-8");
			//获取解密后数据（订单号码|订单金额|订单状态|订单说明）
			String decryptData = CMBC.wapDecryptData(encryptData);
			String[] decryptDataArray = decryptData.split("\\|");
			if (decryptDataArray.length == 4) { //解析后数组长度为4
				if ("1".equals(decryptDataArray[2])) { //成功
					bankquerybean
							.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					return bankquerybean;

				} else if ("0".equals(decryptDataArray[2])) { //失败
					String error = root.elementText(decryptDataArray[3]);
					bankquerybean.setErrorMsg(error);
					bankquerybean
							.setOrderStatus(QueryCommon.ORDER_STATUS_FAILURE);
					return bankquerybean;
		
				} else { //其他
					String error = root.elementText(decryptDataArray[3]);
					bankquerybean.setErrorMsg(error);
					bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
					return bankquerybean;
				}
			} else {
				String error = root.elementText("银行返回结果异常");
				bankquerybean.setErrorMsg(error);
				bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				return bankquerybean;
			}
		} catch (Exception e) {
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
					//读取返回数据，此处需要根据银行返回数据情况做修改===========================
					InputStream input = method.getResponseBodyAsStream();
//					String abc = method.getResponseBodyAsString();
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
	 * 获取民生订单号
	 * @author zhang.chaochao
	 * @date 2014-5-22
	 * @modify
	 * @param merNO 商户号码
	 * @param oid 融易付订单号
	 * @return String 民生订单号
	 */
	private String getOidOfCMBC(String merNO, String oid) {
		StringBuffer oidCMBC = new StringBuffer("");
		//民生订单号码=商户号码（5位）+融易付订单号（15位）
		if (oid.length() < 15) {
			//少于15位，以0补充
			StringBuffer zeroSB = new StringBuffer("");
			while(zeroSB.length() + oid.length() < 15) {
				zeroSB.append("0");
			}
			//组装民生订单号码
			oidCMBC.append(merNO).append(zeroSB).append(oid);
		} else {
			//组装民生订单号码
			oidCMBC.append(merNO).append(oid);
		}
		return oidCMBC.toString();
	}
	
}
