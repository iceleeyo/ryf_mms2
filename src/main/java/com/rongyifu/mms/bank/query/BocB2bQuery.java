package com.rongyifu.mms.bank.query;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.PKCS7Tool;
import com.rongyifu.mms.common.TrustSSL;
import com.rongyifu.mms.utils.LogUtil;

public class BocB2bQuery  extends ABankQuery{
	
	private final static String URL="https://ebspay.boc.cn/PGWPortal/B2BQueryOrder.do?";
	private final static String BOCNO="21922";
	private final static String PFXPATH="/usr/pay/cert/boc/boc_b2b_pfx.pfx";
	private final static String PFXPWD="000000";
	
	
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		BankQueryBean queryBean=new BankQueryBean();
		
		String oid=order.getTseq().trim();//订单号
		
		String signText=BOCNO+"|"+oid;//待签名字符串
		PKCS7Tool tool=null;
		String str_return = "";
		try {
			tool = PKCS7Tool.getSigner(PFXPATH, PFXPWD, PFXPWD);
			String signData = tool.sign(signText.getBytes());//签名
			signData = signData.replaceAll("\n", "").replaceAll("\r","");
			signData = new String(signData.getBytes(), "UTF-8");
			
			//最终拼接的请求地址
			String reqUrl=URL+"bocNo="+BOCNO+"&orderNos="+oid+"&signData="+URLEncoder.encode(signData);
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustSSL.TrustAnyTrustManager() }, new java.security.SecureRandom());
			URL console = new URL(reqUrl);
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();

			//请求
			conn.connect();
			InputStream is = conn.getInputStream();
			DataInputStream indata = new DataInputStream(is);
			
			String ret = "";
			while (ret != null) {
				ret = indata.readLine();
				if (ret != null && !ret.trim().equals("")) {
					str_return = str_return + new String(ret.getBytes("ISO-8859-1"), "UTF-8");
				}
			}
			conn.disconnect();
			
			LogUtil.printInfoLog("BocB2bQuery", "queryOrderStatusFromBank", str_return);
			
			return handleData(str_return);
		} catch (Exception e) {
			e.printStackTrace();
			queryBean.setErrorMsg(e.getMessage());
			queryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		}
		queryBean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		return queryBean;
	}
	
	
	private BankQueryBean handleData(String data) throws Exception{
		BankQueryBean bqb=new BankQueryBean();
		
		Document doc = DocumentHelper.parseText(data);
		Element root = doc.getRootElement();
		String errCode = root.element("header").element("rtnCode").getText();
		Element ordTrans=root.element("body").element("orderTrans");
		
		if(ordTrans != null && "00".equals(errCode)){//返回正常状态
			
			String orderStatus=ordTrans.element("orderStatus").getText();
			
			if("T1".equals(orderStatus)){//成功 记录银行流水
				
				String orderSeq=ordTrans.element("orderSeq").getText();
				bqb.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
				bqb.setBankSeq(orderSeq);
				
			}else if("T2".equals(orderStatus)){//失败
			
				bqb.setOrderStatus(QueryCommon.ORDER_STATUS_FAILURE);
				
			}else//其他状态
				
				bqb.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		}else{//返回异常状态
			
			String rtnMsg = root.element("header").element("rtnMsg").getText();
			bqb.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
			bqb.setErrorMsg(rtnMsg);
		}
		return bqb;
	}
}
