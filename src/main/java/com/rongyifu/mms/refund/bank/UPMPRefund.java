package com.rongyifu.mms.refund.bank;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.RefundDao;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class UPMPRefund implements IOnlineRefund{
	
	final static String UPMP_VER="1.0.1";
	final static String UPMP_CHAR_SET="UTF-8";
	final static String UPMP_SIGN_METHOD="MD5";
	private static final String TRANSTYPE ="04"; // 表示退货
	private static final String BACK_RET_URL = RefundUtil.querywep()+ "ref/upmp_refund_ret"; //UAT测试环境
	
	@Override
	public OnlineRefundBean executeRefund(Object obj) {
		OnlineRefundBean onlineRefundBean = (OnlineRefundBean) obj;
		
		String mid=onlineRefundBean.getMerId();//电银商户号
		
		String merchantid=queryUpmpMerId(mid);//银联商户号
		String merType=merchantid.substring(7, 11);//银联商户类型 MCC
		
		String orderNumber = handle_orderNum(onlineRefundBean.getId()+"");
		onlineRefundBean.setRefBatch(orderNumber);
		String orderAmount =((int) Math.round(onlineRefundBean.getRefAmt()))+"";
		// 查询流水号: 即银行流水号//21位定长数字		
		String qn =onlineRefundBean.getBkTseq();
		
		 Map<String, Object> map=new RefundDao().getUpmpMerInfo();
		 String acqCode=(String) map.get("p2");//机构号
		 String merKey=(String) map.get("mer_key");//密钥
		 String name=(String) map.get("p1");
		 String queryReqUrl=(String) map.get("p3");//查询请求地址
		 
		String []params={"version=" +UPMP_VER,"charset="+UPMP_CHAR_SET,
					"transType="+TRANSTYPE,"merName="+name,
					"merId="+merchantid,"merType="+merType,"backEndUrl="+BACK_RET_URL,
					"acqCode="+acqCode,"orderTime="+DateUtil.getIntDateTime(),
					"orderNumber="+orderNumber,"orderCurrency=156","orderAmount="+orderAmount,
					"qn="+qn};
		
		//排序
		Arrays.sort(params);
		//请求参数map
		Map<String, String> requestParaMap = new HashMap<String, String>();
		requestParaMap.put("signMethod", "MD5");
		//待排序字符串
		StringBuffer signParams =new StringBuffer();
		
		for (String param : params) {
			signParams.append(param).append("&");//拼接排序后的字符串字符串
			
			String[] tempStr=param.split("=");
			requestParaMap.put(tempStr[0], tempStr[1]);
		}
		//待签名字符串
		String signParamsStr=signParams.toString()+ RefundUtil.md5Encrypt(merKey); 
		String signature = RefundUtil.md5Encrypt(signParamsStr);	
		requestParaMap.put("signature", signature);
		
		//打印请求参数日志
		LogUtil.printInfoLog("UPMPRefund", "request", requestParaMap.toString());
		 
		try {
			
			String res_1 = RefundUtil.requestByPostwithURL_unionpay(requestParaMap, queryReqUrl);
			String res = new String(URLDecoder.decode(res_1, "UTF-8").getBytes(),"UTF-8");
			
			LogUtil.printInfoLog("UPMPRefund", "response", "qn="
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
				if(verify_refund(res_1,merKey)){
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_SUCCESS);
				}else{
					LogUtil.printInfoLog("UPMPRefund", "verify_refund",
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
			LogUtil.printErrorLog("UPMPRefund", "exception", "orderNumber=" + orderNumber + " msg=" + e.getMessage(), e);
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
		for (String param : params) {
			if(param.contains("signMethod=")){
				signMethod=param;
			}else if(param.contains("signature=")){
				signature=param;
			}else{
				l2.add(URLDecoder.decode(param));
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
	
	/**
	 * 处理订单号
	 * @param tseq 订单号
	 * @return 处理过后的订单号
	 */
	public static String handle_orderNum(String tseq) {
		String num = "0000000000";
		int len = tseq.length();
		if (len < 10) {
			return num.substring(0, 10 - len) + tseq;
		} else {
			return tseq;
		}
	}
	
	/**
	 * 根据电银商户号查询该商户使用哪个 银联商户id
	 * @param mid 电银商户号
	 * @return
	 */
	public String queryUpmpMerId(String mid){
		return new  MerInfoDao().getUpmpMerId(mid);
	}
	
	public static void main(String[] args) {
		 Map<String, Object> map=new RefundDao().getUpmpMerInfo();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			System.out.println("key:"+key+"   value:"+map.get(key));
		}
		
	}
}
