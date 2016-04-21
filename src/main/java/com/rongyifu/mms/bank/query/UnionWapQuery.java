package com.rongyifu.mms.bank.query;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.unionpaywap.DesUtil2;
import com.rongyifu.mms.unionpaywap.EncDecUtil;
import com.rongyifu.mms.unionpaywap.RSACoder;
import com.rongyifu.mms.unionpaywap.RemoteAccessor;
import com.rongyifu.mms.utils.LogUtil;

import java.util.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.Ostermiller.util.MD5;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cert.CertUtil;

@SuppressWarnings("restriction")
public class UnionWapQuery extends ABankQuery {
	private String merchantOrderId;
	private String merchantOrderTime;
	

	private static String getStringTime(int nowtime) {
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return String.valueOf(hour) + (min < 10 ? "0" + min : min)
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

	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		BankQueryBean bankQueryBean = new BankQueryBean();
		LogUtil.printInfoLog("UnionWapQuery", "queryOrderStatusFromBank",
				"触发成功");
		merchantOrderId = order.getTseq();
		merchantOrderTime = String.valueOf(order.getSysDate())
				+ String.valueOf(getStringTime(order.getSysTime()));

		LogUtil.printInfoLog("UnionWapQuery", "queryOrderStatusFromBank",
				"merchantOrderId is :" + merchantOrderId
						+ ". merchantOrderTime is :" + merchantOrderTime);
		String content = getQueryXML();
		LogUtil.printInfoLog("UnionWapQuery", "queryOrderStatusFromBank",
				"content is :" + content);
		try {
			Document document = DocumentHelper.parseText(content);
			Element upbp = document.getRootElement();

			if ("0000".equals(upbp.elementText("respCode"))) {
				String queryResult = upbp.elementText("queryResult");
				if ("0".equals(queryResult)) {
					bankQueryBean
							.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					bankQueryBean.setBankSeq(upbp.elementText("cupsQid"));
				} else if ("1".equals(queryResult)) {
					bankQueryBean.setErrorMsg("交易失败");
					bankQueryBean
							.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				} else if ("2".equals(queryResult)) {
					bankQueryBean.setErrorMsg("交易处理中");
					bankQueryBean
							.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				} else if ("3".equals(queryResult)) {
					bankQueryBean.setErrorMsg("无此交易");
					bankQueryBean
							.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				}
			} else {
				bankQueryBean.setErrorMsg("提交查询失败： "+upbp.elementText("respDesc"));
				bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bankQueryBean;
	}

	public String getQueryXML() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyyMMddHHmmss");
		java.text.SimpleDateFormat sendSeqFormatter = new java.text.SimpleDateFormat(
				"yyMMddHHmmss");

		java.util.Date currentTime = new java.util.Date();
		String pDate = formatter.format(currentTime);
		String sendSeq = sendSeqFormatter.format(currentTime);
		String queryXML = null;
		StringBuffer data = new StringBuffer(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		data.append(
				"<upbp application=\"MTransInfo.Req\" version =\"1.0.0\" sendTime =\"")
				.append(pDate).append("\" sendSeqId =\"").append(sendSeq)
				.append("\">");
		data.append("<transType>01</transType>");
		data.append("<merchantId>872310045110201</merchantId>");
		data.append("<merchantOrderId>").append(merchantOrderId)
				.append("</merchantOrderId>");
		data.append("<merchantOrderTime>").append(merchantOrderTime)
				.append("</merchantOrderTime>");
		data.append("</upbp>");
		BASE64Encoder encoder = new BASE64Encoder();
		BASE64Decoder decoder = new BASE64Decoder();
		String merchantId = encoder.encodeBuffer("872310045110201".getBytes());
		String mm = MD5.getHashString(radomNu());
		String keyPath = CertUtil.getCertPath("UNIONPAYWAP_USER_Sign_CRT");
		String privateKey = EncDecUtil.getCertKey("123456", keyPath);
		try {
			String desKey = encoder.encodeBuffer(RSACoder.encryptByPrivateKey(
					mm.getBytes(), decoder.decodeBuffer(privateKey)));
			byte[] bodyb = DesUtil2.encrypt(data.toString().getBytes("utf-8"),
					mm.getBytes());
			String miBody = encoder.encode(bodyb);
			String sendXML = merchantId + "|" + desKey + "|" + miBody;
			RemoteAccessor remoteAccessor = new RemoteAccessor();
			String re = remoteAccessor.getResponseByStream(
					"http://upwap.bypay.cn/gateWay/gate.html",
					"utf-8", sendXML);
			LogUtil.printInfoLog("UnionWapQuery", "UnionWapQuery", "" + re);
			String[] strArr = re.split("\\|");
			byte[] de = decoder.decodeBuffer(strArr[1]);
			byte[] b = DesUtil2.decrypt(de, mm.getBytes());
			queryXML = new String(b, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queryXML;
	}

	public static void main(String args[]) {

	}
}
