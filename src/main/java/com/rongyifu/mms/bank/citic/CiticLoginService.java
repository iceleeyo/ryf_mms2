package com.rongyifu.mms.bank.citic;

import java.rmi.RemoteException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;

public class CiticLoginService {
	private static CiticLoginService citicLoginService=null;
	private final static String MERID="002000000000038";
	private final static String MERPWD="citicmch";
	private static Call call =null;
	public static String token="";
	/**
	 * 得到CiticLoginService实例
 	 * @return
	 */
	public static CiticLoginService getInstance() {
	       if (citicLoginService == null) {
	    	   citicLoginService = new CiticLoginService();
	       }
	       return citicLoginService;
	    }
	
	/**
	 * login
	 * @throws Exception 
	 */
	public void login() throws Exception{
		setSSLProperty();
		String resultA ="";
		Document doc=null;
		try {
			//Web服务地址
			String endpoint = "https://pay.bank.ecitic.com/Payment/services/PaymentServices";//"https://pay.test.bank.ecitic.com/Payment/services/PaymentServices";
			getCall().setTargetEndpointAddress(endpoint);
			getCall().setMaintainSession(true);
			getCall().setOperationName("login");
			getCall().addParameter("paraXML", org.apache.axis.Constants.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
			getCall().setReturnType(org.apache.axis.Constants.XSD_STRING);
			getCall().setUseSOAPAction(true);
			getCall().setSOAPActionURI("https://creditcard.ecitic.com/Payment/payerauth.do");//http://service.payment.citiccard.com
			String login = getLogin();
			resultA = (String) getCall().invoke(new Object[] { login });
			doc = DocumentHelper.parseText(resultA);
			}
			catch (Exception e) {
				LogUtil.printErrorLog("CiticSettlement", "login", "中信无磁无密登录失败,失败原因:"+e.getMessage());//
				e.printStackTrace();
				return;
			}
		Element root = doc.getRootElement();
		String infoType = root.element("infoType").getText();
		if(null!=infoType&&infoType.equals("0810")){
			String retCode = root.element("retCode").getText();
			if(retCode.equals("0000000")){
				token=root.element("resParam").elementText("token");
				LogUtil.printInfoLog("CiticSettlement", "login", "登录成功,Token:+"+token+"");
				Thread th=new Thread(new Hdlogin());
				th.start();
			}else{
				LogUtil.printErrorLog("CiticSettlement", "login", "中信无磁无密登录失败,失败原因:"+root.element("commentRes").getText());
			}
				
		}
		
	}
	/**设置https环境
	 * java环境下设置SSL环境
	 * 路径参数需要根据实际情况修改
	 */
	public static void setSSLProperty() {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol");
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		System.setProperty("java.protocol.handler.pkgs",
				"com.sun.net.ssl.internal.www.protocol");
		System.setProperty("javax.net.ssl.keyStore", CITIC.CLIENTPRIKEY);
		System.setProperty("javax.net.ssl.trustStore",CITIC.TRUSTSTORE);
		System.setProperty("javax.net.ssl.trustStorePassword", "654321");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
	} 
	
	private static String getLogin() {
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	    String formatDate=sdf.format(date);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<message method=\"login\" type=\"request\">");
		sb.append("<infoType>0800</infoType>");
		sb.append("<posTime>"+formatDate+"</posTime>");
		sb.append("<posID>"+Ryt.createRandomStr(6)+"</posID>");
		sb.append("<transTime> </transTime>");
		sb.append("<transDate> </transDate>");
		sb.append("<retCode> </retCode>");
		sb.append("<terminalID>88888888</terminalID>");
		sb.append("<merchantID>"+MERID+"</merchantID>");
		sb.append("<merchantName>上海电银信息技术有限公司</merchantName>");
		sb.append("<password>"+MERPWD+"</password>");
		sb.append("<commentRes> </commentRes>");
		sb.append("<resParam> </resParam>");
		sb.append("<reserved> </reserved>");
		sb.append("</message>");
		return sb.toString();
	}
	
	/**
	 * 维持连接
	 */
	public static void holdLogin(){
		String holdLoginXml=getHoldLoginXml();
		call.setOperationName("maintainSession ");
		try {
			String retXml = (String)call.invoke(new Object[] { holdLoginXml });
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(retXml);
			} catch (DocumentException e) {
				LogUtil.printErrorLog("CiticSettlement", "handleSettlementRet", "中信无磁无密维持登录返回报文处理异常!"); 
//				e.printStackTrace();
			}
			Element root = doc.getRootElement();
			Element retCode = root.element("retCode");
			if(retCode==null){
				LogUtil.printErrorLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款返回报文解析异常!"); 
				return ;
			}
			if(retCode.getText().equals("0000000")){
				LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "维持登录成功!"); 
			}else{
				LogUtil.printErrorLog("CiticSettlement", "holdLogin", "中信无磁无密维持登录失败,失败原因:"+root.element("commentRes").getText()); 
			}
		} catch (RemoteException e) {
			LogUtil.printErrorLog("CiticLoginService", "holdLogin","中信无磁无密维持登录连接异常!");
			e.printStackTrace();
		}
		
	} 
	
	public static String getHoldLoginXml(){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<message method=\"maintainSession\" type=\"request\">");
		sb.append("<infoType>0200</infoType>");
		sb.append("<merchantID>"+MERID+"</merchantID>");
		sb.append("<merchantName></merchantName>");
		sb.append("<token>"+token+"</token>");
		sb.append("<retCode></retCode>");
		sb.append("<commentRes></commentRes>");
		sb.append("</message>");
		return sb.toString();
	} 
	
	public Call getCall() {
		return call;
	}

	public static void setCall(Call call) {
		CiticLoginService.call = call;
	}

	static{
		Service service = new Service();
			try {
				setCall((Call) service.createCall()); 
			} catch (ServiceException e) {
				e.printStackTrace();
			}
	}
	class Hdlogin implements Runnable{

		public void run() {
			while(true){
				try {
					Thread.sleep(1000*60*20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CiticLoginService.holdLogin();
			}
		}
	}
}
