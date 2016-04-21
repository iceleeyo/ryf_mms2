package com.rongyifu.mms.settlement;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.rongyifu.mms.bank.SJbank;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.BankQueryDao;

/**
 * 盛京银行对账
 * 
 * @author m
 * 
 */
public class CheckDataSJImp implements SettltData {
    BankQueryDao bkquery = new BankQueryDao();

    private final static String XML_HEARD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Override
    public List<SBean> getCheckData(String bank, String fileContent) throws Exception {
        List<SBean> res = new ArrayList<SBean>();
        String[] datas = fileContent.split("\n");
        for (int i = 1; i < datas.length; i++) {
            String[] values = datas[i].split(",");
            // 过滤非支付数据
            if(!"0".equals(values[3]))
            	continue;
            
            SBean bean = new SBean();
            bean.setGate(bank);
            bean.setTseq(values[0]);
            bean.setDate(values[7].substring(0, 8));
            String amt=values[4];
            if(amt.contains("\"")){
    			amt = amt.substring(1,amt.length()-1);
    		}
            bean.setAmt(amt.replaceAll(",", ""));
            res.add(bean);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.rongyifu.mms.settlement.SettltData#getCheckData(java.lang.String, java.util.Map)
     * 接口对账
     */
    @Override
    public List<SBean> getCheckData(String bank, Map<String, String> m) throws Exception {
        String merNo = m.get("merNo");
        String transTime = m.get("transTime");
        // 组成请求报文
        String reqXml = XML_HEARD + "<SJBankData><reqData id=\"netPayReq003\"><merNo>" + merNo + "</merNo><transTime>" + transTime + "</transTime></reqData></SJBankData>";

        System.out.println("reqStr=" + reqXml);
        // 报文签名
        String signData = SJbank.sign(reqXml);

        System.out.println("signData=" + signData);
        // 获取请求地址
        GateRoute gr = bkquery.queryBankInfoByGid(Integer.parseInt(bank));
        String reqUrl = gr.getP1();

        Map<String, String> requestParaMap = new HashMap<String, String>();
        requestParaMap.put("sign", signData);
        requestParaMap.put("merNo", merNo);
        requestParaMap.put("transTime", transTime);

        // 发送请求
        String resData = requestByPostwithURL(requestParaMap, reqUrl).trim();

        System.out.println("resData=" + resData);
        if (resData.indexOf("errCode") >= 0 || resData.indexOf("errDesc") >= 0)
            return null;

        if (!resData.equals("")) {
            return packageData(bank, resData);
        }

        return null;
    }

    /*
     * 解析数据
     */
    private List<SBean> packageData(String bank, String fileData) {
        List<SBean> sbeanList = new ArrayList<SBean>();

        String[] datas = fileData.split("\\|");
        for (int i = 0; i < datas.length; i++) {
            String[] dataArr = datas[i].split(",");
            if (dataArr.length == 10) {
                SBean bean = new SBean();
                bean.setGate(bank.trim());
                bean.setTseq(dataArr[0].trim());
                bean.setAmt(dataArr[5].trim());
                bean.setDate(dataArr[8].substring(0, 8));
                sbeanList.add(bean);
            }
        }
        return sbeanList;
    }

    /*
     * 请求URL
     */
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
                    // sResponseBody = method.getResponseBodyAsString();
                    // 读取返回数据，此处需要根据银行返回数据情况做修改===========================
                    InputStream input = method.getResponseBodyAsStream();
                    // String abc = method.getResponseBodyAsString();
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

}
