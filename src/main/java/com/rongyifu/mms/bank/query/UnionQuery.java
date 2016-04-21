package com.rongyifu.mms.bank.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

public class UnionQuery extends ABankQuery  {
	
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		BankQueryBean bankQueryBean=new BankQueryBean();
		String MERCHANID = gate.getMerNo();
		String secrtKey = gate.getP1(); // 合作密钥
		String url = gate.getP2();
		String tseq = order.getTseq();
		String orderNum = handle_orderNum(tseq);// 上送银行订单号
		String md5SecrtKey = QueryCommon.md5Encrypt(secrtKey);// 合作密钥 md5加密
																	// 32位
		String orderDate = "" + order.getSysDate();
		String params = "charset=UTF-8&merId=" + MERCHANID + "&orderNumber="
				+ orderNum + "&orderTime=" + orderDate
				+ "&transType=31&version=1.0.0";
		String md5Sign = QueryCommon.md5Encrypt(params + "&" + md5SecrtKey);
		String reqParams = params + "&signMethod=MD5&signature=" + md5Sign;
		try {
			Map<String, Object> requestParaMap=new HashMap<String, Object>();
			requestParaMap.put("charset", "UTF-8");
			requestParaMap.put("merId", MERCHANID);
			requestParaMap.put("orderNumber", orderNum);
			requestParaMap.put("orderTime", orderDate);
			requestParaMap.put("transType", "31");
			requestParaMap.put("version", "1.0.0");
			requestParaMap.put("signMethod", "MD5");
			requestParaMap.put("signature", md5Sign);
			LogUtil.printInfoLog("UnionQuery", "queryOrderStatusFromBank",
					"reqData:" + reqParams);
			String res_1=Ryt.requestWithPost(requestParaMap, url);
			String res = new String(URLDecoder.decode(res_1).getBytes(),"utf-8");
			LogUtil.printInfoLog("UnionQuery", "queryOrderStatusFromBank",
					"resData:" + res+"\n");
			String respCode = get_params("(?<=respCode=)(\\w)*(?=&)", res);
			String respMsg="";
			if (respCode.equals("00")) {
				// 验证签名
				String transStatus = get_params("(?<=transStatus=)(\\w)*(?=&)",
						res);
				respMsg=get_params("(?<=respMsg=)(.*?\\[?\\]?)*(?=&)",res);
				String qn = "";
				if (transStatus.equals("00")) {// 支付成功
					qn = get_params("(?<=qn=)(\\w)*(?=&)", res);
					if(verify_query(res_1,secrtKey)){
						bankQueryBean.setBankSeq(qn);
						bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					}else{
						LogUtil.printInfoLog("UnionQuery", "queryOrderStatusFromBank", "respMsg:验证签名失败");
						bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
					}
				} else if (transStatus.equals("01")) {// 交易处理中
					bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				} else if (transStatus.equals("02")) {// 交易失败
					bankQueryBean.setErrorMsg(respMsg.equals("")?"	":respMsg);
					bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_FAILURE);
				}
			}else {
				respMsg=get_params("(?<=respMsg=)(.*?\\[?\\]?)*(?=&)",res);
				LogUtil.printInfoLog("UnionQuery", "queryOrderStatusFromBank", "respMsg:"+respMsg);
				bankQueryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
				bankQueryBean.setErrorMsg(respMsg.equals("")?"	":respMsg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankQueryBean;
	}


	private String handle_orderNum(String tseq) {
		String num = "0000000000";
		int len = tseq.length();
		if (len < 10) {
			return num.substring(0, 10 - len) + tseq;
		} else {
			return tseq;
		}
	}

	/***
	 * 正则截取字符
	 * 
	 * @param regex
	 * @param respStr
	 * @return
	 */
	private String get_params(String regex, String respStr) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(respStr);
		String res = "";
		if (matcher.find()) {
			res = matcher.group();
		}
		return res;
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
	public  boolean verify_query(String resData, String secrtKey) throws UnsupportedEncodingException {
		boolean res = false;
		StringBuffer signStr = new StringBuffer();
		String secrtKeyMd5 = QueryCommon.md5Encrypt(secrtKey);
		String[] params=resData.split("&");
		List<String> l2=new ArrayList<String>();
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
		String verifyStr = QueryCommon.md5Encrypt(signStr.append(secrtKeyMd5)
				.toString());
		if (signature.contains(verifyStr)) {
			res = true;
		}
		return res;

	}
}
