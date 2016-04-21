package com.rongyifu.mms.refund.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 银联联机退货处理类
 * 
 */
public class UnionPayRefund implements IOnlineRefund {

	public static final String version = "1.0.0";// 银联
	public static final String charset = "UTF-8";
	public static final String signMethod = "MD5";
	public static final String MD5_KEY = "fBvGQzi6etTZqUZCuwK4AvqhKw1fcCBl";//合作密钥
	public static final String transType = "04"; // 表示退货
	public static final String backEndUrl = RefundUtil.querywep()+ "ref/unionpay_refund_ref"; //UAT测试环境
	public static final String merId = "403310048998503";
	public static final String orderCurrency = "156";
	public static final String url = "https://202.96.255.146/gateway/merchant/trade";
	
	@Override
	public OnlineRefundBean executeRefund(Object obj) {
		OnlineRefundBean onlineRefundBean = (OnlineRefundBean) obj;
		java.text.DateFormat format3 = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String orderTime = format3.format(new Date());		
		String orderNumber = handle_orderNum(String.valueOf(onlineRefundBean.getId()));
		onlineRefundBean.setRefBatch(orderNumber);
		String orderAmount =((int) Math.round(onlineRefundBean.getRefAmt()))+"";
		// 查询流水号: 即银行流水号//21位定长数字
		String qn =onlineRefundBean.getBkTseq();
	  		
		// 关键信息：指报文中除“签名方法”和“签名信息”外的所有字段 按照key值做升序排列
		String param1 ="backEndUrl=" + backEndUrl+ "&charset=" + charset +"&merId=" + merId+					
				      "&orderAmount=" + orderAmount+ "&orderCurrency=" + orderCurrency +
				      "&orderNumber=" +orderNumber+ "&orderTime=" + orderTime+"&qn=" + qn;
		
		String param2= "&transType=" + transType+ "&version=" +version;
		String param =param1 +param2;
		
		//被签名字符串：关键信息和合作密钥信息的拼接结果
		String sign_md5 = param+"&"+ RefundUtil.md5Encrypt(MD5_KEY);        
		String signature = RefundUtil.md5Encrypt(sign_md5);		
		String reqParams=param1+"&signature=" + signature+"&signMethod=MD5" +param2;
		//打印请求参数日志
		LogUtil.printInfoLog("UnionPayRefund", "request", reqParams);
				
		Map<String, String> requestParaMap = new HashMap<String, String>();
		
		requestParaMap.put("backEndUrl", backEndUrl);
		requestParaMap.put("charset", charset);		
		requestParaMap.put("merId", merId);
		requestParaMap.put("orderAmount", orderAmount);
		requestParaMap.put("orderCurrency", orderCurrency);
		requestParaMap.put("orderNumber", orderNumber);
		requestParaMap.put("orderTime", orderTime);
		requestParaMap.put("qn", qn);
		requestParaMap.put("signature", signature);
		requestParaMap.put("signMethod", "MD5");		
		requestParaMap.put("transType", transType);
		requestParaMap.put("version", version);
		
		try {
			
			String res_1 = RefundUtil.requestByPostwithURL_unionpay(requestParaMap, url);
			String res = new String(URLDecoder.decode(res_1, "UTF-8").getBytes(),"UTF-8");
			
			LogUtil.printInfoLog("UnionpayRefund", "response", "qn="
					+ qn + " orderNumber=" + orderNumber + "\n" + res);

			String reponses[] = res.split("&");
			Map<String, String> requestParaMaps = new HashMap<String, String>();
			for (int i = 0; i < reponses.length; i++) {
				String[] tmp = reponses[i].split("=");
				requestParaMaps.put(tmp[0], tmp[1]);
			}
			
			String respCode = requestParaMaps.get("respCode");
			String respMsg = requestParaMaps.get("respMsg");
		
			if ("00".equals(respCode)) {
				//验签 
				if(verify_refund(res_1,MD5_KEY)){
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
				}else{
					LogUtil.printInfoLog("UnionpayRefund", "verify_refund",
							"respMsg[" + qn + "," + orderNumber + "]:验证签名失败");
					
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
					onlineRefundBean.setRefundFailureReason("验证签名失败，请核实银行是否有出款！");
				}
			} else if ("01".equals(respCode)) {
				onlineRefundBean.setRefundFailureReason("请求报文错误!");
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
			} else if ("02".equals(respCode)) {
				onlineRefundBean.setRefundFailureReason("银行验证签名失败!");
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);				
			} else if ("04".equals(respCode)) {
				onlineRefundBean.setRefundFailureReason("回话超时!");
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
			} else if ("11".equals(respCode)) {
				onlineRefundBean.setRefundFailureReason("你要求退款的订单不存在!");
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
			} else if ("42".equals(respCode)) {
				onlineRefundBean.setRefundFailureReason("交易金额超限!");
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
			} else {
			    onlineRefundBean.setRefundFailureReason(respCode+"："+respMsg);
				onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("UnionpayRefund", "exception", "orderNumber=" + orderNumber + " msg=" + e.getMessage());
			e.printStackTrace();
		}
		
		return onlineRefundBean;
	}
	
	/***
	 * 
	 * @param resData
	 *            返回报文
	 * @param secrtKey
	 *            合作密钥
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("deprecation")
	public  boolean verify_refund(String resData, String secrtKey) throws UnsupportedEncodingException {
		boolean res = false;
		StringBuffer signStr = new StringBuffer();
		String secrtKeyMd5 = RefundUtil.md5Encrypt(secrtKey);
		String[] params=resData.split("&");
		List<String> l2=new ArrayList<String>();
		@SuppressWarnings("unused")
		String signMethod="";
		String signature="";
		for (String string : params) {
			if(string.contains("signMethod=")){
				signMethod=string;
			}else if(string.contains("signature=")){
				signature=string;
			}else{
				l2.add(URLDecoder.decode(string));
			}
		}
		Collections.sort(l2);
		for(String str : l2){
			signStr.append(str).append("&");
		}
		String verifyStr = RefundUtil.md5Encrypt(signStr.append(secrtKeyMd5)
				.toString());
		if (signature.contains(verifyStr)) {
			res = true;
		}
		return res;

	}
	public String urlencodes(String var) {
		String urlcode = null;
		try {
			urlcode = URLEncoder.encode(var, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return urlcode;
	}
	
	public static String handle_orderNum(String tseq) {
		String num = "0000000000";
		int len = tseq.length();
		if (len < 10) {
			return num.substring(0, 10 - len) + tseq;
		} else {
			return tseq;
		}
	}

}
