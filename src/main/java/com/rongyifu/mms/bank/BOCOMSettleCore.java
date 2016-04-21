package com.rongyifu.mms.bank;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;

public class BOCOMSettleCore {
	private static Logger logger  =  Logger.getLogger(BOCOMSettleCore.class);
	
	private static final String corpNo = Ryt.getParameter("JH_CORP_NO");//"8000500118";
	private static final String userNo = Ryt.getParameter("JH_USER_NO");//"22222";
	private static final String channel = "0";
	private static final String requestUrl = Ryt.getParameter("JH_NC_URL");//"http://192.168.64.77:8899";

	/**
	 * 账户信息查询 310101
	 * @return
	 * @throws Exception 
	 */
	public static String do310101(String accNo) throws Exception {
		// 说明：
		// 1、账户类型：1-基本户；2-一般户；3-专用户；4-临时户。
		StringBuffer sb = new StringBuffer();
		sb.append("<ap>");
		sb.append(getRequestHead("310101", "", DateUtil.today(),  DateUtil.now(), 1, ""));
		sb.append("<body><acno>");
		sb.append(accNo).append("</acno></body>");
		sb.append("</ap>");
		String res = interactive(requestUrl, sb.toString());
		return res;
	}

	/**
	 * 当日交易明细查询（310201）
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static String do310201(String accNo) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<ap>").append(getRequestHead("310201", "", DateUtil.today(),DateUtil.now(), 1, ""));
		sb.append("<body><acno>").append(accNo).append("</acno></body>");
		sb.append("</ap>");
		String res = interactive(requestUrl, sb.toString());
		return res;

	}

	/**
	 *历史交易明细查询（310301）
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static String do310301(String accNo, String beginDate,String endDate) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<ap>");
		sb.append(getRequestHead("310301", "", DateUtil.today(),DateUtil.now(), 1, ""));
		sb.append("<body><acno>").append(accNo).append("</acno>");
		sb.append("<start_date>").append(beginDate).append("</start_date>");
		sb.append("<end_date>").append(endDate).append("</end_date></body>");
		sb.append("</ap>");
		String res = interactive(requestUrl, sb.toString());
		return res;
	}

	/**
	 * 转账交易结果查询（310204） 流水号类型 queryFlag ‘1’ ：原流水号为企业凭证号 ‘2’ ：原流水号为网银流水号
	 * ‘3’:代发工资转账结果查询企业流水号 ‘4’:代发工资转账结果查询网银批次号
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static String do310204(String queryFlag, String oglSerialNo) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<ap>");
		sb.append(getRequestHead("310204", "",  DateUtil.today(),DateUtil.now(), 1, ""));
		sb.append("<body><query_flag>").append(queryFlag).append("</query_flag>");
		sb.append("<ogl_serial_no>").append(oglSerialNo).append("</ogl_serial_no>");
		sb.append("</body></ap>");

		String res = interactive(requestUrl, sb.toString());
		return res;
	}


	// 与交行接口交互
	public static String interactive(String url, String queryString) throws Exception {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			// if (StringUtils.isNotBlank(queryString))
			if (queryString != null && !queryString.equals(""))
				method.setQueryString(URIUtil.encodeQuery(queryString,"GBK"));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
//				response = method.getResponseBodyAsString();
				InputStream input = method.getResponseBodyAsStream();
				response = Ryt.readStream(input);
				response=transCharCode(response);//对响应xml转码
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("请求银行接口异常！");
		} finally {
			method.releaseConnection();
		}
		throwRequestException(response);
		return response;
	}

	public static StringBuilder getRequestHead(String trCode, String reqNo,
			int date, int time, int atomTrCount, String reserved) {
		StringBuilder xmlHead = new StringBuilder();
		xmlHead.append("<head> ");
		xmlHead.append("<tr_code>").append(trCode).append("</tr_code>");
		xmlHead.append("<corp_no>").append(corpNo).append("</corp_no>");
		xmlHead.append("<user_no>").append(userNo).append("</user_no>");
		xmlHead.append("<req_no>").append(reqNo).append("</req_no>");
		xmlHead.append("<tr_acdt>").append(date).append("</tr_acdt>");
		xmlHead.append("<tr_time>").append(time).append("</tr_time>");
		xmlHead.append("<atom_tr_count>").append(atomTrCount).append("</atom_tr_count>");
		xmlHead.append("<channel>").append(channel).append("</channel>");
		xmlHead.append("<reserved>").append(reserved).append("</reserved>");
		xmlHead.append("</head>");
		return xmlHead;
	}
    private static String transCharCode(String responseXml){
		try {
			responseXml=new String(responseXml.getBytes("ISO8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		return responseXml;
    }
    /**
     * 抛出请求异常
     * @param responseXML
     * @throws Exception
     */
    private static  void throwRequestException(String responseXML) throws Exception{
		if(responseXML==null||responseXML.equals("")){ throw new Exception("请求银行接口异常！");}
		Element headElement=getHead(responseXML);
		String ans_info=headElement.element("ans_info").getTextTrim();
		if(!ans_info.equals("")){
			 logger.error("银行返回错误信息："+ans_info);
			 throw new Exception(ans_info);
		}
	}
	/**
	 * 获得响应xml的body节点
	 * @param responseXML
	 * @return
	 */
	public static Element getBody(String responseXML){
		try {
			Document document = DocumentHelper.parseText(responseXML);
			Element responseBody=document.getRootElement().element("body");
			 return responseBody;
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	/**
	 * 获得响应xml的head节点
	 * @param responseXML
	 * @return
	 */
	public static Element getHead(String responseXML){
		try {
			Document document = DocumentHelper.parseText(responseXML);
			Element responseBody=document.getRootElement().element("head");
			 return responseBody;
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
