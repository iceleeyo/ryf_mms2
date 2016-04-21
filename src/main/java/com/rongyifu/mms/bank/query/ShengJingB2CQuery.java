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

import com.rongyifu.mms.bank.SJbank;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 盛京银行订单查询
 * 
 */
public class ShengJingB2CQuery extends ABankQuery {
    private final static String XML_HEARD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    
//    public static void main(String args[]){
//    	GateRoute gate = new GateRoute();
//    	gate.setMerNo("3000010722");
//    	gate.setP1("http://192.168.30.10:1087/netpayReq.do");
//    	
//    	Hlog order = new Hlog();
//    	order.setTseq("9300217682");
//    	
//    	ShengJingB2CQuery test = new ShengJingB2CQuery();
//    	BankQueryBean bean = test.queryOrderStatusFromBank(gate, order);
//    	System.out.println("status="+bean.getOrderStatus());
//    }

    @Override
    public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
        BankQueryBean bankquerybean = new BankQueryBean();
        String tseq = order.getTseq();// 订单号(测试：9200099389)
        String merNO = gate.getMerNo(); // 商户号(测试：00005)
        String reqUrl = gate.getP1(); // 请求路径：http://192.168.30.10:1087/netpayReq.do
        // 请求报文
        String reqStr = XML_HEARD + "<SJBankData><reqData id=\"netPayReq002\"><merNo>" + merNO + "</merNo><orderNo>" + tseq + "</orderNo></reqData></SJBankData>";
        LogUtil.printInfoLog("[request]" + reqStr);

        String signData;
        try {
            // 签名
            signData = SJbank.sign(reqStr);
            Map<String, String> requestParaMap = new HashMap<String, String>();
            requestParaMap.put("sign", signData);
            // 发送请求
            String resData = requestByPostwithURL(requestParaMap, reqUrl).trim();
            // 返回报文验签
            String resXml = SJbank.unSign(resData);
            LogUtil.printInfoLog("[response]" + reqStr);
            
            Document doc = DocumentHelper.parseText(resXml);
            Element root = doc.getRootElement();
            // 读取返回加密报文数据
            String Status = root.element("repData").element("transState").getTextTrim();
            // 数据处理
            if(!Ryt.empty(Status)){
                if ("0".equals(Status)) {
                    bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
                    return bankquerybean;
                }
            }
        } catch (Exception e) {
            LogUtil.printErrorLog("tseq=" + tseq, e);
        }
        bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
        return bankquerybean;
    }

    public static String requestByPostwithURL(Map<String, String> requestParaMap, String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(url);
        String sResponseBody = "";
        NameValuePair[] nameValuePairs = null;
        NameValuePair nameValuePair = null;
        try {
            if (requestParaMap != null && requestParaMap.size() > 0) {
                nameValuePairs = new NameValuePair[requestParaMap.size()];
                int i = 0;
                Iterator<Map.Entry<String, String>> it = requestParaMap.entrySet().iterator();
                while (it.hasNext()) {
                	Map.Entry<String, String> element = (Map.Entry<String, String>) it.next();
                    nameValuePair = new NameValuePair();
                    nameValuePair.setName(String.valueOf(element.getKey()));
                    nameValuePair.setValue(String.valueOf(element.getValue()));
                    nameValuePairs[i++] = nameValuePair;
                }
                method.setRequestBody(nameValuePairs);
                httpClient.executeMethod(method);
                int resCode = method.getStatusCode();
                if (resCode == HttpStatus.SC_OK) {
                    // sResponseBody = method.getResponseBodyAsString();
                    // 读取返回数据，此处需要根据银行返回数据情况做修改===========================
                    InputStream input = method.getResponseBodyAsStream();
                    // String abc = method.getResponseBodyAsString();
                    sResponseBody = Ryt.readStream(input);
                }
            }
        } catch (Exception err) {
        	LogUtil.printErrorLog(url, requestParaMap, err);
        } finally {
            method.releaseConnection();
        }
        return sResponseBody;
    }

}
