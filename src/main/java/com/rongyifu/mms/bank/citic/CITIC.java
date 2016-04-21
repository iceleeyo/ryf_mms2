package com.rongyifu.mms.bank.citic;

import java.security.PrivateKey;
import java.util.Map;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.Base64;

public class CITIC {
	protected static String CLIENTPRIKEY = "/usr/pay/cert/citic/citic_card.pfx";// 私钥
	protected static String CLIENTPUBCERT = "/usr/pay/cert/citic/citic_card_ca.cer";// 公钥
	protected static String TRUSTSTORE = "/usr/pay/cert/citic/citic_card_truststore";// 公钥
//	protected static String CLIENTPRIKEY = "D:/citicsc.pfx";// 私钥
//	protected static String CLIENTPUBCERT = "D:/citicsc.cer";// 公钥
//	protected static String TRUSTSTORE = "D:/truststore";// 公钥
	protected static String CLIPASSWD = "123456";// 私钥密码
	protected static String SERPASSWD = "123456";// 公钥密码

	public static String pay(Map<String, String> p) {
		String payXml = getPay(p);
		return payXml;
	}

	private static String getPay(Map<String, String> p) {
		String merID = p.get("merID");
		String terminalID = p.get("terminalID");
		String posID = p.get("posID");
		String batchNo = p.get("batchNo");
		String orderID = p.get("orderID");
		String transDate = p.get("transDate");
		String transTime = p.get("transTime");
		String purchAmount = p.get("purchAmount");
		String timeStamp = p.get("timeStamp");
		String posTime = p.get("posTime");
		String merURLs = p.get("merURL");// 需要做base64解码 回调地址
		String merURL = new String(Base64.decode(merURLs));// 回调地址
		String orderIDs=orderID;
		int gap=19-orderID.length();
		if(gap>0){
			for (int i = 0; i < gap; i++) {
				orderID+=" ";
			}
		}
		// 订单号＋批次号＋流水号＋金额＋时间戳
		String signatureStr = orderID + batchNo + posID + purchAmount
				+ timeStamp;
		// 用客户端私钥签名
		PrivateKey priKey;
		String signature = "";
		try {
			priKey = CertUtils.getPrivateKey(CLIENTPRIKEY, CLIPASSWD);
			byte[] byteStr = CertUtils.sign(priKey,
					signatureStr.getBytes("UTF8"));
			signature = new String(Base64.encode(byteStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		xml.append("<message method=\"pareq\" type=\"request\">");
		xml.append("<merchant>");
		xml.append("<merURL>");
		xml.append(Ryt.empty(merURL)?"":merURL);
		xml.append("</merURL>");
		xml.append("<merID>");
		xml.append(Ryt.empty(merID)?"":merID);
		xml.append("</merID>");
		xml.append("<token></token>");
		xml.append("<terminalID>");
		xml.append(terminalID);
		xml.append("</terminalID>");
		xml.append("</merchant>");
		xml.append("<purchase>");
		xml.append("<posID>");
		xml.append(posID);
		xml.append("</posID>");
		xml.append("<batchNo>");
		xml.append(batchNo);
		xml.append("</batchNo>");
		xml.append("<orderID>");
		xml.append(orderIDs);
		xml.append("</orderID>");
		xml.append("<transDate>");
		xml.append(transDate);
		xml.append("</transDate>");
		xml.append("<transTime>");
		xml.append(transTime);
		xml.append("</transTime>");
		xml.append("<purchAmount>");
		xml.append(purchAmount);
		xml.append("</purchAmount>");
		xml.append("<productType>000000</productType>");
		xml.append("<dividedNum>00</dividedNum>");
		xml.append("<currency>156</currency>");
		xml.append("<timeStamp>");
		xml.append(timeStamp);
		xml.append("</timeStamp>");
		xml.append("<signature>");
		xml.append(signature);
		xml.append("</signature>");
		xml.append("<posTime>");
		xml.append(posTime);
		xml.append("</posTime>");
		xml.append("<exponent>2</exponent>");
		xml.append("</purchase>");
		xml.append("</message>");
		return new String(Base64.encode(xml.toString().getBytes()));
	}
}
