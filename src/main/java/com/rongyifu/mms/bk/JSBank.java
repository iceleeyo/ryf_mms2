package com.rongyifu.mms.bk;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;

public class JSBank {
	
	
	String url = Ryt.getJSSignServerUrl();
	String JS_SUBMIT_HOST = "";//TODO
	int JS_SUBMIT_PORT = 0;//TODO
	String JS_SUBMIT_METHOD = "";//TODO
	
	static final String signType = "INFOSEC_SIGN/1.0";
	
	static final String VerifyType = "INFOSEC_VERIFY_SIGN/1.0";
	
	@SuppressWarnings("deprecation")
	private String genSignVerify(String data ,String contentType)throws Exception{
		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Content-Type", contentType);
		method.setRequestHeader("Content-Length", data.length() + "");
		method.setRequestBody(data);
			// 执行getMethod
		int statusCode = httpClient.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			return null;
		}
		byte[] responseBody = method.getResponseBody();
		return new String(responseBody, "GB2312");
	}
	
	
	
	@SuppressWarnings("deprecation")
	public String queryHistoryOrders(String userID ,String userPWD,String MER_NO,String BEG_TIME,String END_TIME)throws Exception{
		
		String sessionId = loginJSBServer(userID,userPWD);
		if(sessionId==null) return "登录失败";
		
		String serialNo = DateUtil.getIntDateTime()+"";
		System.err.println("时间:" + serialNo);
		
		StringBuffer str = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		str.append("<JSBEBankData>");
		str.append("<opReq>");
		str.append("<serialNo>").append(serialNo).append("</serialNo>");// <!-- 交易序列号(客户端交易唯一标识) -->
		str.append("<userId>").append(userID).append("</userId>");  // <!-- 客户Id标识--> 
		str.append("<reqTime>").append(serialNo).append("</reqTime>"); // <!-- 客户端请求时间（yyyymmddhhmmss） -->
		str.append("<ReqParam>");
		str.append("<VERSION>").append("1.0.0").append("</VERSION>");//版本编号
		str.append("<MER_NO>").append(MER_NO).append("</MER_NO>");//<MER_NO>商户编号</MER_NO>
		str.append("<PAGE_NO>").append("1").append("<PAGE_NO>");//>页索引号
		str.append("<ORDER_STT>").append("1").append("<ORDER_STT>");//订单交易状态
//		0：未支付；1：已支付；2：支付处理中；3：已撤销；4：退款处理中；5：已全额退款；9：支付失败无输入则查询全部
		str.append("<BEG_TIME>").append(BEG_TIME).append("<BEG_TIME>");//订单交易状态
		str.append("<END_TIME>").append(END_TIME).append("<END_TIME>");//起始日期
		str.append("<BEG_ORDERNO>").append("").append("<BEG_ORDERNO>");//起始订单号
		str.append("<END_ORDERNO>").append("").append("<END_ORDERNO>");//截止订单号
		str.append("</ReqParam>");
		str.append("</opReq>");
		str.append("</JSBEBankData>");
		
		
		HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(JS_SUBMIT_HOST, JS_SUBMIT_PORT);
        PostMethod post = new PostMethod(JS_SUBMIT_METHOD);
//        post.setParameter("k", k);
//        post.setParameter("p", encryptStr);
        
//        String strSign = genSignVerify(str.toString(),signType);
        
        String body = sessionId + "|srv102_HistoryQuery|";
        
        post.setRequestBody(body);
        client.executeMethod(post);
        String res = post.getResponseBodyAsString();
        post.releaseConnection();
		return res;
	}

//	发送方式：POST
//	请求URL：https://ebank.jsbank.cn:[Port]/cbdirectlink/APISessionReqServlet
//			请求报文：sessionId|serviceId|#交易请求XML包
//					 -------------------- ---------------
//					      请求报文头         请求报文体
//	银行方响应HTTP数据：
//		响应报文：sessionId |retCode| errorMsg|#交易返回XML包
//					 ----------------------------- --------------
//					           响应报文头             响应报文体
//	说明：
//	sessionId为会话编号，每次登录之后会产生一个唯一的编号，用于标识这次会话。用户每次进行交易时都必须上送该Id。如果是登录交易，则请求报文头中的sessionId传-1即可。
//	serviceId为服务编号，用以区分不同的操作，见具体交易格式说明。
	
	@SuppressWarnings("deprecation")
	private String loginJSBServer(String userID,String userPWD) {
		String serialNo = DateUtil.getIntDateTime()+"";
		System.err.println("时间:" + serialNo);
		StringBuffer str = new StringBuffer();
		str.append("<?xml version='1.0' encoding='UTF-8'?>");
		str.append("<JSBEBankData>");
		str.append("<opReq>");
		str.append("<serialNo>").append(serialNo).append("</serialNo>");//<!-- 交易序列号(客户端唯一标识) -->
		str.append("<reqTime>").append(serialNo).append("</reqTime>");          //<!--  客户端请求时间 -->
		str.append("<ReqParam>");
		str.append("<userID>").append(userID).append("</userID>");// <!--  用户代码 -->
		str.append("<userPWD>").append(userPWD).append("</userPWD>");//  <!--  用户银企直联密码  -->
		str.append("</ReqParam>");
		str.append("</opReq>");
		str.append("</JSBEBankData>");
		String signStr = null;
		try {
			signStr = genSignVerify(str.toString(),signType);
		} catch (Exception e) {
			// 签名失败
			System.err.println("JSB error : 签名失败:" + e.getMessage());
			e.printStackTrace();
			return null;
		}
		HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost(JS_SUBMIT_HOST, JS_SUBMIT_PORT);
        PostMethod post = new PostMethod(JS_SUBMIT_METHOD);
        String body =  "-1|srv001_SignIn|" + signStr;
        post.setRequestBody(body);
        try {
			client.executeMethod(post);
		} catch (HttpException e) {
			System.err.println("JSB error : 登录失败:" + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("JSB error : 登录失败:" + e.getMessage());
			e.printStackTrace();
			return null;
		}
        String res = null;
		try {
			res = post.getResponseBodyAsString();
		} catch (IOException e) {
			System.err.println("JSB error : 登录失败:" + e.getMessage());
			e.printStackTrace();
			return null;
		}
        post.releaseConnection();
        if(res.length()==0){
        	System.err.println("JSB error : 登录失败:银行返回报文为空");
        	return null;
        } 
        String resText[] = res.split("\\|");
        if(resText.length>=3 && resText[1].equals("0000")){
        	return resText[0];//sessionId 
        }else{
        	System.err.println("JSB error : 登录失败:" + resText[2]);
        	return null;
        }
	}

}
