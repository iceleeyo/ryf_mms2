package com.rongyifu.mms.refund.bank;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cert.CertUtil;

import com.Ostermiller.util.MD5;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.unionpaywap.*;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 银联wap联机退货
 * 
 */
@SuppressWarnings("restriction")
public class UnionPayRefund2 implements IOnlineRefund {

	private String merchantName = "上海电银信息技术";
	private String merchantId = "872310045110201";
	private String merchantOrderId;
	private String merchantOrderTime;
	private String merchantOrderAmt;
	private String merchantOrderCurrency = "156";
	private String cupsQid;
	private String keyPwd = "123456";
	private String sendSeqId;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//	SimpleDateFormat sendSeqFormatter = new SimpleDateFormat("yyMMddHHmmss");

	@Override
	public OnlineRefundBean executeRefund(Object obj) {
		OnlineRefundBean refund = (OnlineRefundBean) obj;
		this.merchantOrderId = refund.getOrgTseq();
		this.merchantOrderTime = getDateAndTime(refund.getSysDate(),refund.getSysTime());
		this.merchantOrderAmt = getOrderAmt(refund.getRefAmt());
		this.cupsQid = refund.getBkTseq();
		this.sendSeqId = handleSendSeqId(String.valueOf(refund.getId()));
		refund.setRefBatch(sendSeqId);
		String content = getSendContent();
		LogUtil.printInfoLog("UnionPayRefund2", "executeRefund","response: tseq=" + merchantOrderId + " cupsQid=" + cupsQid + "\n" + content);
		try {
			Document document = DocumentHelper.parseText(content);
			Element upbp = document.getRootElement();
			if ("0000".equals(upbp.elementText("respCode"))) {
				refund.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
			} else {
				String failmsg = upbp.elementText("respDesc");
				refund.setRefundFailureReason(failmsg);
				refund.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);
				LogUtil.printInfoLog("UnionPayRefund2", "executeRefund", "联机退款交易失败：" + failmsg);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("UninPayRefund2", "executeRefund", "tseq=" + merchantOrderId + " cupsQid=" + cupsQid, e);
		}
		return refund;
	}

	private String getSendContent() {
		java.util.Date currentTime = new java.util.Date();
		String pDate = formatter.format(currentTime);
//		String sendSeq = sendSeqFormatter.format(currentTime);
		String retXML = null;
		StringBuffer data = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		data.append("<upbp application=\"MTransRefund.Req\" version =\"1.0.0\" sendTime =\"")
				.append(pDate).append("\" sendSeqId =\"").append(sendSeqId).append("\">\n");
		data.append("<merchantName>").append(merchantName).append("</merchantName>\n");
		data.append("<merchantId>").append(merchantId).append("</merchantId>\n");
		data.append("<merchantOrderId>").append(merchantOrderId).append("</merchantOrderId>\n");
		data.append("<merchantOrderTime>").append(merchantOrderTime).append("</merchantOrderTime>\n");
		data.append("<merchantOrderAmt>").append(merchantOrderAmt).append("</merchantOrderAmt>\n");
		data.append("<merchantOrderCurrency>").append(merchantOrderCurrency).append("</merchantOrderCurrency>\n");
		data.append("<cupsQid>").append(cupsQid).append("</cupsQid>\n");
		data.append("</upbp>");
		
		LogUtil.printInfoLog("UnionPayRefund2", "getSendContent","Request: tseq=" + merchantOrderId + " cupsQid=" + cupsQid + "\n" + data.toString());
		
		BASE64Encoder encoder = new BASE64Encoder();
		BASE64Decoder decoder = new BASE64Decoder();
		String merchantId2 = encoder.encodeBuffer(merchantId.getBytes());
		String mm = MD5.getHashString(radomNu());
		String keyPath = CertUtil.getCertPath("UNIONPAYWAP_USER_Sign_CRT");
		String privateKey = EncDecUtil.getCertKey(keyPwd, keyPath);
		try {
			String desKey = encoder.encodeBuffer(RSACoder.encryptByPrivateKey(
					mm.getBytes(), decoder.decodeBuffer(privateKey)));
			byte[] bodyb = DesUtil2.encrypt(data.toString().getBytes("utf-8"),
					mm.getBytes());
			String miBody = encoder.encode(bodyb);
			String sendXML = merchantId2 + "|" + desKey + "|" + miBody;
			RemoteAccessor remoteAccessor = new RemoteAccessor();
			String re = remoteAccessor.getResponseByStream("http://upwap.bypay.cn/gateWay/gate.html", "utf-8",sendXML);
			
			LogUtil.printInfoLog("UnionPayRefund2", "getSendContent","Response: tseq=" + merchantOrderId + " cupsQid=" + cupsQid + "\n" + re);
			String[] strArr = re.split("\\|");
			String isSucRes=strArr[0];
			/***
			 * 正确的返回格式：1|90eFDGOoutaOxuOocgZI4KkpBZvQSJULCm2TiLuxfyz4aUJ+WRjmOrw4kw5DbuiW2mvdJ8Q8IjE46BeUX2b124S/kXWCMZKJ6TimuilvhGvadjK3/ceko+ckH2P7oPBiUeqqml71R/F+PwUf/QDYRDQnoeFNcFHyoWnzC3IovgxvzBGEmm9f1KHsH0s/U2YKBPXc3k22Q8Atov+T6vGdl2P87mMie9O+l25KfuVJjr9b6kOgvbMWIaKb+87e9GV7B+ol4DhAMnkiLv7SAF5XqWWvd71RMjayEeDl27RVAL8XL4fVmNMYE+bBnrW4p0odxb3m8bLP+1wEIPzlYFM8BDzNb/DBq1CoKB7tSwyZYhllb4bc4HPUezJNFYNWzuXUCGABTjsNE7vFvebxss/7XK+R6l/rS3Q//ZB7Rmb5eIzFvebxss/7XLG0IzwQtlzgdiCfVrX8UqPir39ytwtw6C/oJjuyJSKY/ZB7Rmb5eIzFvebxss/7XABmqD7OFKsw0lSz33YuymRob5DkPXUxw1vx080zEzTjBhQBt2V5T5TTOSg292pZyH2RIV4DUy+7haT18+5MiJI43IPDpNkG5aii2ns38FEl2ZbJcgYl+g49TmKsmZznZ0Re4RRTg57QaUTYIpslvXmEOq4MZ0t148uhiTTjklHuI0E0EE8oB9XQ84CBSlMt8dRT2qKKE5hq+TOfCNemJ3E=|MWNkNGU0YTliMWM4ZTMyZjJhZDNmM2ZhZDgzM2M3NGM=
			 * 错误的返回格式：0|0003|5Y+C5pWw6ZSZ6K+v
			 */
			byte[] b=null;
			if("1".equals(isSucRes)){ 
				byte[] de = decoder.decodeBuffer(strArr[1]);
				b = DesUtil2.decrypt(de, mm.getBytes());
				retXML = new String(b, "utf-8");
			}else{
				String errorMsg=Base64.decodeToString(strArr[2]);
				Map<String, String> map=new HashMap<String, String>();
				map.put("isSucRes", isSucRes);
				map.put("errorCode", strArr[1]);
				map.put("errorMsg", errorMsg);
				LogUtil.printInfoLog("UninPayRefund2", "getSendContent", "", map);
				StringBuffer ret=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
				ret.append("<upbp>");
				ret.append("<respCode>").append(strArr[1]).append("</respCode>");
				ret.append("<respDesc>").append(errorMsg).append("</respDesc>");
				ret.append("</upbp>");
				retXML=ret.toString();
			}

		} catch (Exception e) {
			LogUtil.printErrorLog("UninPayRefund2", "getSendContent", "tseq=" + merchantOrderId + " cupsQid=" + cupsQid, e);
		}
		return retXML;
	}

	private static String getDateAndTime(Integer nowDate, Integer nowTime) {
		String time = getStringTime(nowTime);
		return nowDate.toString() + time;
	}

	private static String getStringTime(int nowtime) {
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return ""+(hour < 10 ? "0" + hour : hour) + (min < 10 ? "0" + min : min)
				+ (second < 10 ? "0" + second : second);
	}

	private String getOrderAmt(Double amt) {
		Integer orderAmt = amt.intValue();
		return orderAmt.toString();
	}

	private static String radomNu() {
		java.util.Random r = new Random();
		StringBuilder offsetCode = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			offsetCode.append(String.valueOf(r.nextInt(10)));
		}
		return offsetCode.toString();
	}
	
	/**
	 * 固定12位：不足前面补0；过长截取后12位
	 * @param refundId
	 * @return
	 */
	private String handleSendSeqId(String refundId){
		int len = refundId.length();
		String sendSeqId = null;
		if(len <= 12){
			sendSeqId = refundId;
			for(int i = 0; i < 12 - len; i++){
				sendSeqId = "0" + sendSeqId;
			}
		} else if (len > 12){
			sendSeqId = refundId.substring(len - 12);
		}
		return sendSeqId;
	}
}
