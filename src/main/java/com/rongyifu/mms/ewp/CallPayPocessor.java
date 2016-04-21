package com.rongyifu.mms.ewp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;

public class CallPayPocessor {
	
	public static Logger log4j = Logger.getLogger(CallPayPocessor.class);
	
	private static final String SUCCESS = "success";
	private static final String FAIL="fail";
	private static final String WAIT_PAY="wait_pay";//待支付
	private static final String REQ_FAIL="req_fail";//请求失败
	
	public static void callBackMerchants(String tseq) {
		try{
			String url = Ryt.getEwpPath();
			Map<String, Object> requestMap=new HashMap<String, Object>();
			requestMap.put("tseq", tseq);
			requestMap.put("sign", MD5.getMD5(("ryf"+tseq).getBytes()));
			String ret = Ryt.requestWithPost(requestMap, url+"pub/retUrl");
			if(SUCCESS.equals(ret))
				log4j.info("订单[" + tseq + "]调用商户通知接口成功！");
			else
				log4j.info("订单[" + tseq + "]调用商户通知接口失败：" + ret);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 发送邮件
	 * @param content 内容
	 * @param title 标题
	 * @param receive 接收人
	 * @throws Exception 
	 */
	public static boolean sendMail(String content,String title,String receive) throws Exception{
		return EWPService.sendMail(content, title, receive);
	}
	
	/**
	 * 调用ewp代付入口方法
	 * @param order
	 * @param data_source
	 */
	public static boolean ewpDfCommonEntry(TlogBean order){
		String transType = Constant.EWP_DF_FLAG;
		String url = ParamCache.getStrParamByName(Constant.GlobalParams.EWP_INTERNAL_URL);
		String tmsIp = ParamCache.getStrParamByName(Constant.GlobalParams.TMS_IP);
		Integer dataSource = order.getData_source();
		String transAmt = Ryt.div100(order.getAmount());
		String payAmt = Ryt.div100(order.getPay_amt());
		String mid = order.getMid();
		Long tseq = order.getTseq();
		
		String data = mid + transAmt + transType + dataSource + tmsIp + Constant.EWP_DF_MD5_KEY;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("transAmt", transAmt);
		paramMap.put("payAmt", payAmt);
		paramMap.put("data_source", dataSource);
		paramMap.put("mid", mid);
		paramMap.put("chkValue", MD5.getMD5(data.getBytes()));
		paramMap.put("transType", transType);
		paramMap.put("tseq", tseq);
		
		LogUtil.printInfoLog("CallPayPocessor", "ewpDfCommonEntry", mid + "|"
				+ transAmt + "|" + payAmt + "|" + transType + "|" + dataSource
				+ "|" + tmsIp);
		if(Ryt.empty(url)){
			LogUtil.printErrorLog("CallPayProcessor", "ewpDfCommonEntry", "tseq="+tseq+"  url is null");
			return false;
		}
		String resInfo = Ryt.requestWithPost(paramMap, url+ "df/auto_df");
		if(Ryt.empty(resInfo)){
			LogUtil.printErrorLog("CallPayPocessor", "ewpDfCommonEntry", "tseq=" + tseq + " ewp return null");
			return false;
		}
		
		try {
			resInfo = new String(resInfo.getBytes("UTF-8"),"utf-8");
			LogUtil.printInfoLog("CallPayPocessor", "ewpDfCommonEntry", "tseq=" + tseq + " dfRes=" + resInfo);
			
			Document doc = DocumentHelper.parseText(resInfo.trim());
			Element root = doc.getRootElement();
			Element status = root.element("status");
			String statusValue = status.elementTextTrim("value");
			if(Constant.EWP_DF_SUCCESS.equals(statusValue))
				return true;
		} catch (Exception e) {
			LogUtil.printErrorLog("CallPayPocessor", "ewpDfCommonEntry", "解析ewp返回xml异常：tseq=" + tseq, e);
		}
		return false;
	}
	
	
	/***
	 * 查询代付结果
	 * @param order  order.tseq,order.oid,order.mdate,order.mid
	 * @return new String[3]{"success|fail|req_fail(请求失败)|wait_pay(待支付)","Msg or errorMsg"}
	 */
	public static String[] queryDfResult(TlogBean order) throws Exception{
		String [] resp=new String[2];
		String transQueryType="E3";
		String transQueryFun="df/trans_query";
		Map<String, Object> reqParam=new HashMap<String, Object>();
		reqParam.put("tseq", order.getTseq());
		reqParam.put("oid", order.getOid());
		reqParam.put("mid", order.getMid());
		reqParam.put("mdate", order.getMdate());
		
		reqParam.put("transType", transQueryType);
		LogUtil.printInfoLog("CallPayProcessor", "queryDfResult", "tseq:"+order.getTseq()
				+"|oid:"+order.getOid()+"|mid"+order.getMid()+"|mdate:"+order.getMdate()
				+"|transType:"+transQueryType);
		String result=requestEwp(transQueryFun, reqParam);
		
		if(Ryt.empty(result)){
			resp[0]=REQ_FAIL;
			resp[1]="查询失败，Ewp返回异常";
			return resp;
		}
		
		Document doc=getDocument(result);
		if(null==doc){
			resp[0]=REQ_FAIL;
			resp[1]="查询失败，Ewp返回异常";
			return resp;
		}
		
		Element root=doc.getRootElement();
		String state=root.elementText("state");
		String msg=root.elementText("msg");
		msg=new String(msg.getBytes(),"UTF-8");
		if("1".equals(state)){
			resp[0]=WAIT_PAY;
		}else if("2".equals(state)){
			resp[0]=SUCCESS;
		}else if("3".equals(state)){
			resp[0]=FAIL;
		}else{
			resp[0]=REQ_FAIL;
		}
		resp[1]=msg;
		
		return resp;
	}
	
	/***
	 * 
	 * @param gid
	 * @return new String[]{"fail|success","0.01","查询成功Or查询失败，errorMsg"}
	 * @throws Exception 
	 */
	public static String[] queryBkAccountBalance(String gid) throws Exception{
		String[] resp=new String[3];
		String TransQueryType="F2";//余额查询类型
		String TransQueryFun="df/trans_query";//查询function
		String SucState="2";//查询成功
		Map<String, Object> reqParam=new HashMap<String, Object>();
		reqParam.put("gid", gid);
		reqParam.put("transType", TransQueryType);
		LogUtil.printInfoLog("CallPayProcessor", "queryBkAccountBalance", "gid:"+gid+
				"|transType:"+TransQueryType);
		String result=requestEwp(TransQueryFun,reqParam);
		if(Ryt.empty(result)){
			resp[0]=FAIL;
			resp[1]="";
			resp[2]="查询失败，Ewp返回错误";
			return resp;
			
		}
		
		Document doc=getDocument(result);
		if(doc==null){
			resp[0]=FAIL;
			resp[1]="";
			resp[2]="查询失败，Ewp返回错误";
			return resp;
		}
		
		Element root=doc.getRootElement();
		String state=root.elementText("state");
		String msg=root.elementText("msg");
		msg=new String(msg.getBytes(),"UTF-8");
		if(SucState.equals(state)){
			String balance=root.elementText("balance");
			resp[0]=SUCCESS;
			resp[1]=balance;
			resp[2]=msg;
		}else{
			resp[0]=FAIL;
			resp[1]="";
			resp[2]=msg;
		}
		return resp;
	}
	
	private static String requestEwp(String reqFun,Map<String, Object> reqParam){
		Map<String, String> logMap=new HashMap<String, String>();
		if(null==reqParam||reqParam.size()==0){
			logMap.put("reqParam", "");
		}else{
			
			Set<String> keySet=reqParam.keySet();
			for (String key : keySet) {
				String v=reqParam.get(key).toString();
				logMap.put(key, v);
			}
		}
		
		String url=ParamCache.getStrParamByName(Constant.GlobalParams.EWP_INTERNAL_URL);
		if(Ryt.empty(url)){
			LogUtil.printErrorLog("CallPayProcessor", "requestEwp", "params:",logMap);
			return null;
		}
		String result=null;
		if(null!=reqFun){
			String req=url+"/"+reqFun;
			result=Ryt.requestWithPost(reqParam, req);
		}
		
		logMap.put("result", result);
		logMap.put("reqFun", reqFun);
		logMap.put("url", url);
		LogUtil.printInfoLog("CallPayProcessor", "requestEwp", "params:", logMap);
		return result;
	}
	
	
	/***
	 * 解析xml  
	 * @param xml
	 * @return Document
	 */
	private static Document getDocument(String xml){
		Document doc=null;
		if(xml==null||"".equals(xml)){
			return null;
		}
		try {
			doc=DocumentHelper.parseText(xml);
		} catch (Exception e) {
			LogUtil.printErrorLog("EwpReqServer", "parseXml", "xml:"+xml, e);
			doc=null;
		}
		return  doc;
	}
	
}