package com.rongyifu.mms.unionpaywap;

import java.util.*;
import java.io.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.Ostermiller.util.MD5;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cert.CertUtil;
import com.rongyifu.mms.utils.*;
import com.rongyifu.mms.common.*;

@SuppressWarnings("restriction")
public class UnionPayWap {

	private static String getStringTime(int nowtime) {
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return ""+(hour < 10 ? "0" + hour : hour) + (min < 10 ? "0" + min : min)
				+ (second < 10 ? "0" + second : second);
	}

	private static String radomNu() {
		java.util.Random r = new Random();
		StringBuilder offsetCode = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			offsetCode.append(String.valueOf(r.nextInt(10)));
		}
		return offsetCode.toString();
	}

	public static String unionPayWapSign(Map<String, String> p) {
		String xml = null;
		String judgeRediPage = null;
		String redirectPage = null;
		
		try {
			String sysDate = p.get("sysDate");
			String sysTime = getStringTime(Integer.valueOf(p.get("sysTime")));
			String orderTime = sysDate + sysTime;
			java.text.SimpleDateFormat sendSeqFormatter = new java.text.SimpleDateFormat("yyMMddHHmmss");
			java.util.Date currentTime = new java.util.Date();
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
			String pDate = formatter.format(currentTime);
			String sendSeq = sendSeqFormatter.format(currentTime);
			String merchantOrderId = p.get("merchantOrderId");
			
			// 设置回调地址
			String ewp_path = ParamCache.getStrParamByName("EWP_PATH");
			String backurl = ewp_path + "bk/union_pay_wap_ret";
			String fronturl = ewp_path + "bk/union_pay_wap_page_ret?tseq=" + merchantOrderId;
			
			// 组织请求报文
			StringBuffer data = new StringBuffer();
			data.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			data.append("<upbp application=\"MGw.Req\" version=\"1.0.0\" sendTime=\"" + pDate + "\" sendSeqId=\"" + sendSeq + "\">\n");
			data.append("    <merchantId>872310045110201</merchantId>\n");
			data.append("    <merchantOrderId>" + merchantOrderId + "</merchantOrderId>\n");
			data.append("    <merchantOrderTime>" + orderTime + "</merchantOrderTime>\n");
			data.append("    <merchantOrderAmt>" + p.get("merchantOrderAmt") + "</merchantOrderAmt>\n");
			data.append("    <gwType>01</gwType>\n");
			data.append("    <frontUrl>" + fronturl + "</frontUrl>\n");
			data.append("    <backUrl>" + backurl + "</backUrl>\n");
			data.append("    <merchantOrderCurrency>156</merchantOrderCurrency>\n");
			data.append("    <mobileNum>" + p.get("mobileNum") + "</mobileNum>\n");
			data.append("    <merchantOrderDesc>" + handleOrderDesc(p.get("merchantUserId")) + "</merchantOrderDesc>\n");
			data.append("</upbp>");
			
			LogUtil.printInfoLog("UnionPayWap", "unionPayWapSign","request xml:\n" + data.toString());
			
			BASE64Encoder encoder = new BASE64Encoder();
			BASE64Decoder decoder = new BASE64Decoder();
			String merchantId = encoder.encodeBuffer("872310045110201".getBytes());
			String mm = MD5.getHashString(radomNu());
			String keyPath = CertUtil.getCertPath("UNIONPAYWAP_USER_Sign_CRT");
			String privateKey = EncDecUtil.getCertKey("123456", keyPath);
			String desKey = encoder.encodeBuffer(RSACoder.encryptByPrivateKey(
					mm.getBytes(), decoder.decodeBuffer(privateKey)));

			byte[] bodyb = DesUtil2.encrypt(data.toString().getBytes("utf-8"),
					mm.getBytes());
			String miBody = encoder.encode(bodyb);
			xml = merchantId + "|" + desKey + "|" + miBody;
			RemoteAccessor remoteAccessor = new RemoteAccessor();
			String re = remoteAccessor.getResponseByStream(
					"http://upwap.bypay.cn/gateWay/gate.html", "utf-8", xml);
			
			String[] strArr = re.split("\\|");
			judgeRediPage = strArr[0];
			byte[] de = decoder.decodeBuffer(strArr[1]);
			byte[] b = DesUtil2.decrypt(de, mm.getBytes());
			String content = new String(b, "utf-8");
			
			LogUtil.printInfoLog("UnionPayWap", "unionPayWapSign", "response xml:\n" + content);
			
			Document document = DocumentHelper.parseText(content);
			Element upbp = document.getRootElement();
			if ("1".equals(judgeRediPage)) {
				redirectPage = upbp.elementText("gwInvokeCmd");
			} else {
				redirectPage = "error";
			}
			LogUtil.printInfoLog("UnionPayWap", "unionPayWapSign", "response pay:" + redirectPage);
		} catch (Exception e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			LogUtil.printErrorLog("UnionPayWap", "unionPayWapSign", trace.toString());
		}
		return redirectPage;
	}

	public static String unionPayWapNotify(Map<String, String> p) {
		BASE64Decoder decoder = new BASE64Decoder();
		String req = p.get("notifyBody");
		String[] strArr = req.split("\\|");
		String cerPath = CertUtil.getCertPath("UNIONPAYWAP_USER_Notify_CRT");
		String publicKey = EncDecUtil.getPublicCertKey("000000", cerPath);

		String content = null;
		try {
			String mm = RSACoder.decryptByPublicKey(strArr[1], publicKey);
			byte[] de = decoder.decodeBuffer(strArr[2]);
			byte[] b = DesUtil2.decrypt(de, mm.getBytes());
			content = new String(b, "utf-8");
			LogUtil.printInfoLog("UnionPayWap", "unionPayWapNotify", content);
		} catch (Exception e) {
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			LogUtil.printErrorLog("UnionPayWap", "unionPayWapNotify", trace.toString());
		}
		return content;
	}

	public static String unionPayWapNotifyRet(Map<String, String> p) {
		if (p.get("notifyRetBody").equals("Y")) {
			StringBuffer data = new StringBuffer(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			data.append("<upbp application=\"MTransNotify.Rsp\">");
			data.append("<transType>");
			data.append(p.get("transType"));
			data.append("</transType>");
			data.append("<respCode>0000</respCode>");
			data.append("<respDesc>none</respDesc>");
			data.append("</upbp>");
			BASE64Encoder encoder = new BASE64Encoder();
			String mm = MD5.getHashString(radomNu());
			String miBody = null;
			String riBody = null;
			try {
				byte[] bodyb = DesUtil2.encrypt(
						data.toString().getBytes("utf-8"), mm.getBytes());
				miBody = encoder.encode(bodyb);
				riBody = encoder.encode(MD5.getHash(data.toString()));
			} catch (Exception e) {
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
			}
			String xml = "1" + "|" + miBody + "|" + riBody;
			LogUtil.printInfoLog("UnionPayWap", "unionPayWapNotifyRet", xml);
			return xml;
		} else {
			return "error";
		}
	}

	/**
	 * 处理订单描述 
	 * @param mid
	 * @return
	 */
	private static String handleOrderDesc(String mid){
		if(Ryt.empty(mid))
			return "none";
		else{
			if("295".equals(mid) || "300".equals(mid) || "327".equals(mid))
				return "机票";
			else
				return "none";			
		}
	}
}
