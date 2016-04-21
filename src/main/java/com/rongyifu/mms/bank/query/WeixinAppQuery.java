package com.rongyifu.mms.bank.query;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 微信支付APP支付 - 订单结果查询
 * @author Admin
 *
 */
public class WeixinAppQuery extends ABankQuery{
	
	private static final String WEIXIN_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		String appId = gate.getP1();
		String mchId = gate.getMerNo();
		String tseq = order.getTseq();
		String nonceStr = QueryCommon.md5Encrypt(String.valueOf(Math.random()));
		String key = gate.getMerKey();
		
		// 签名
		String signStr = "appid=" + appId 
				       + "&mch_id=" + mchId 
				       + "&nonce_str=" + nonceStr 
				       + "&out_trade_no=" + tseq;
		String sign = QueryCommon.md5Encrypt(signStr + "&key=" + key);
		
		// 组装请求参数
		StringBuffer postData = new StringBuffer();
		postData.append("<xml>");
		postData.append("    <appid>" + appId + "</appid>");
		postData.append("    <mch_id>" + mchId + "</mch_id>");
		postData.append("    <out_trade_no>" + tseq + "</out_trade_no>");
		postData.append("    <nonce_str>" + nonceStr + "</nonce_str>");
		postData.append("    <sign>" + sign + "</sign>");
		postData.append("</xml>");
		
		// 请求查询接口		
		LogUtil.printInfoLog("WeixinAppQuery", "queryOrderStatusFromBank", postData.toString());
		String result = postRequest(WEIXIN_QUERY_URL, postData.toString());
		LogUtil.printInfoLog("WeixinAppQuery", "queryOrderStatusFromBank", "[tseq=" + tseq + "]query result:\n" + result);
		
		BankQueryBean bankquerybean = new BankQueryBean();
		bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		final String SUCCESS = "SUCCESS";
		try {
			// 解析结果
			Document doc = DocumentHelper.parseText(result);
			Element root = doc.getRootElement();
			String returnCode = root.elementText("return_code");
			if(SUCCESS.equals(returnCode)){ // 判断返回码
				String resultCode = root.elementText("result_code");
				if(SUCCESS.equals(resultCode)){ // 判断业务结果
					String tradeState  = root.elementText("trade_state");
					if(SUCCESS.equals(tradeState)){ // 判断交易结果
						String transactionId = root.elementText("transaction_id");
						
						bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
						bankquerybean.setBankSeq(transactionId);
					}
				}
				
			}
		} catch(Exception e){
			LogUtil.printErrorLog("WeixinAppQuery", "queryOrderStatusFromBank", e.getMessage(), e);
		}
		
		return null;
	}
	
	private String postRequest(String url, String postData){
		HttpPost httpPost = new HttpPost(url);
		StringEntity postEntity = new StringEntity(postData, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);
        
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
        	LogUtil.printErrorLog("WeixinAppQuery", "postRequest", e.getMessage(), e);
        } finally {
            httpPost.abort();
        }
        
        return result;
	}
	
}
