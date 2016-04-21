package com.rongyifu.mms.ewp.ryf_df;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import org.apache.log4j.Logger;

public class CallRyfPayProcessor {
	public static Logger log4j = Logger.getLogger(CallRyfPayProcessor.class);
	/**
	 * 新代付系统 成功返回码
	 */
	private static final String RETSUC="RYF_DF_000";
	/***
	 * ryf_df 交易入口
	 * @param accountID
	 * @param rcvAcname
	 * @param rcvAcno
	 * @param orderId
	 * @param transAmt
	 * @param bkUrl
	 * @param cardFlag
	 * @param bkNo
	 * @param purpose
	 * @param merPriv
	 * @param dfType
	 * @return
	 */
	public static String ryfDfEntry(String accountId,String rcvAcname,String rcvAcno,String orderId,String transAmt,String bkUrl,
														String cardFlag,String bkNo,String purpose,String merPriv,String dfType){
		
		Map<String, Object> params=new HashMap<String, Object>();
		String reqFun="ryf_df/trans_entry";
		String md5key = ParamCache.getStrParamByName("MD5_KEY");
		String transType="C1";
		String version="10";
		String dataSource="1";//支付系统
		String chkStr=version+orderId+transAmt+transType+rcvAcno+md5key;
		String chkValue=MD5.getMD5(chkStr.getBytes()).toUpperCase();
		
		params.put("version", version);
		params.put("accountId", accountId);
		params.put("orderId", orderId);
		params.put("transAmt", transAmt);
		params.put("transType", transType);
		params.put("dfType", dfType);//A or B 
		params.put("cardFlag", cardFlag);
		params.put("rcvAcno", rcvAcno);
		params.put("rcvAcname", rcvAcname);
		params.put("bankNo", bkNo);
		params.put("purpose", purpose);
		params.put("merPriv", merPriv==null?"":merPriv);
		params.put("bgRetUrl", bkUrl);
		params.put("dataSource", dataSource);
		params.put("chkValue", chkValue);
		LogUtil.printInfoLog("CallRyfPayProcessor", "ryfDfEntry", "代付请求参数："+"version:"+version+"  accountId:"+accountId+
				"  orderId:"+orderId+"  transAmt:"+transAmt+"  transType:"+transType+"  dfType:"+dfType+"  cardFlag:"+cardFlag+
				"  rcvAcno:"+rcvAcno+"  rcvAcname:"+rcvAcname+"  bankNo:"+bkNo+"  purpose:"+purpose+"  merPriv:"+merPriv+
				"  bgRetUrl:"+bkUrl+"  dataSource:"+dataSource+"  chkValue:"+chkValue);
		return requestRyfDf(reqFun, params);
		
	}
	
	
	public static boolean RecvSyncResult(String respXml){
		try {
			LogUtil.printInfoLog("CallRyfPayProcessor", "RecvSyncResult", "respXMl:"+respXml);
			Document doc=DocumentHelper.parseText(respXml);
			Element root=doc.getRootElement();
			Element statusEl=root.element("status");
			String value=statusEl.elementText("value");
			if(value==null){
				return false;
			}
			if(!value.equals(RETSUC)){
				return false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.printErrorLog("CallRyfPayProcessor", "RecvSyncResult", "解析同步返回报文失败,RespXml:"+respXml, e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 异步回调通知结果处理
	 * @param responseXml
	 * @return
	 */
	public static String RecvRyfPayResult(Map<String, String> p){
		LogUtil.printInfoLog("CallRyfPayProcessor", "RecvRyfPayResult", "收到异步通知参数：", p);
		String reqIp=p.get("ip");//获取请求IP
		String url=ParamCache.getStrParamByName("IPLIST_NEWDF");//新代付系统访问IP列表
		if(!url.contains(reqIp)){
			LogUtil.printInfoLog("CallRyfPayProcessor", "RecvRyfPayResult", "IP校验失败，请求IP禁止访问！req_ip："+reqIp);
			return "verify_ip_fail";
		}
		String md5key = ParamCache.getStrParamByName("MD5_KEY");
		String accountId=p.get("accountId");
		String  orderId=p.get("orderId");
		String  transAmt=p.get("transAmt");//0.01 格式
		String  sysDate=p.get("sysDate");
		String  sysTime=p.get("sysTime");
		String  transStatus=p.get("transStatus");
		String  tseq=p.get("tseq");
        String errorMsg=p.get("errorMsg");
		String  chkValue=p.get("chkValue");
		String signStr=orderId + transAmt + tseq + transStatus + md5key;
		String sign=MD5.getMD5(signStr.getBytes()).toUpperCase();
		if(!chkValue.equals(sign)){ //验证签名
			LogUtil.printInfoLog("CallRyfPayProcessor", "RecvRyfPayResult", "验证签名失败，sign:"+sign+"|chkValue:"+chkValue);
			return "verify_fail";
		}
		if(transStatus.equals("S")){//成功交易
			MerAccHandleUtil.handleSuc(accountId,orderId, Ryt.mul100(transAmt), sysDate, sysTime, tseq, transStatus, errorMsg);
		}else if(transStatus.equals("F")){//失败交易
			MerAccHandleUtil.handleFail(accountId,orderId, Ryt.mul100(transAmt), sysDate, sysTime, tseq, transStatus, errorMsg);
		}
		
		return "RECV_RYT_ORD_ID_"+orderId;
	}
	
	
	private static String requestRyfDf(String reqFun,Map<String, Object> params){
		Map<String, String> logMap=new HashMap<String, String>();
		if(null==params||params.size()==0){
			logMap.put("reqParam", "");
		}else{
			
			Set<String> keySet=params.keySet();
			for (String key : keySet) {
				String v = (String) params.get(key);
				logMap.put(key, v);
			}
		}
		String url = ParamCache.getStrParamByName("DF_URL");
		String orderId=(String)params.get("orderId");
		if(Ryt.empty(url)){
			LogUtil.printErrorLog("CallRyfPayProcessor", "requestRyfDf", "tseq="+orderId+"  url is null,",logMap);
			return null;
		}
		String reqUrl=url+reqFun;
		String result=Ryt.requestWithPost(params, reqUrl);
		logMap.put("result", result);
		logMap.put("reqFun", reqFun);
		logMap.put("url", url);
		LogUtil.printInfoLog("CallRyfPayProcessor", "requestRyfDf", "params:", logMap);
		return result;
	}
	
	public static String getDfType(Integer gate){
		String gateStr=String.valueOf(gate);
		String dfType="";
		if(gateStr.matches("^71[0-9]{3}")){
			dfType="A";
		}else if(gateStr.matches("^72[0-9]{3}")){
			dfType= "B";
		}
		return dfType;
	}

    /**
     * 调用新代付系统
     * @param tlog
     * @return
     */
     public static String CallRyfPay(TlogBean tlog){
        String accountId=tlog.getMid();
        String rcvAcname=tlog.getP2();
        String rcvAcno=tlog.getP1();
        String orderId=tlog.getTseq().toString();
        String transAmt=Ryt.div100(tlog.getAmount());
        String bkUrl=ParamCache.getStrParamByName("MMS_INTERNAL_URL")+"/mms/go?transCode=RYF_DF_ASYNC_RESP";;
        String cardFlag=tlog.getP6();
        String bkNo=tlog.getP3();
        String purpose="";
        try {
				purpose = new String(Base64.decode(tlog.getP7()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.printErrorLog("CallRyfPayProcessor", "CallRyfPay", "purpose base64.decode is error,tseq:"+tlog.getTseq(), e);
				purpose="";
			}
        String merPriv=tlog.getMer_priv();
        String dfType=CallRyfPayProcessor.getDfType(tlog.getGate());
        String resp= CallRyfPayProcessor.ryfDfEntry(accountId, rcvAcname, rcvAcno, orderId, transAmt, bkUrl, cardFlag, bkNo, purpose, merPriv, dfType);
        return resp;
    }

     /***
      *
      * @param gid
      * @return object[] obj=new object[3]{1,Msg,B2eGate}
      * object[0]-> 0:return suc| 1:return fail
      * object[2]->B2eGate object
      */
 /*    public static Object[] queryBalance(int gid){
         Object[] result=new Object[3];
         try {
            B2EGate b2eGate=new B2EGate();
            String version="10";
            String transType="F1";
            if(gid<=0){
                result[0]=1;
                result[1]="参数[gid]值错误";
                return result;
            }
            String md5key = ParamCache.getStrParamByName("MD5_KEY");
            String chkStr=version+gid+transType+md5key;
            String chkValue=MD5.getMD5(chkStr.getBytes()).toUpperCase();
            String reqFun="ryf_df/trans_entry";
            Map<String,Object> params=new HashMap<String, Object>();
            params.put("version",version);
            params.put("gid",gid);
            params.put("transType",transType);
            params.put("version",version);
            params.put("chkValue",chkValue);
            String resp=requestRyfDf(reqFun, params);
            Document doc=DocumentHelper.parseText(resp);
            Element root=doc.getRootElement();
            Element status=root.element("status");
            String val=status.elementText("value");
//            String msg=status.elementText("msg");
            if(val.equals("RYF_DF_000")){
                Element queryResult=root.element("queryResult");
                String accNo=queryResult.elementText("acc_no");
                b2eGate.setAccNo(accNo);
                String accName=queryResult.elementText("acc_name");
                b2eGate.setAccName(accName);
                String balance=queryResult.elementText("balance");
                b2eGate.setBalance(balance);
                String availBalance=queryResult.elementText("availBalance");
                b2eGate.setAvailBalance(availBalance);
                result[0]=0;
                result[1]="";
                result[2]=b2eGate;
            }else{
                result[0]=1;
                result[1]=getRespMsg(val);
            }
         } catch (Exception e) {
             result[0]=1;
             result[1]="查询失败，处理异常";
             LogUtil.printErrorLog("CallRyfPayProcessor", "queryBalance", "查询余额异常", e);
         }finally{
             return result;
         }
        
     }*/

     public static String getRespMsgFromCode(String Code){
            if("RYF_DF_000".equals(Code)){
                   return  "返回成功";
            }else if("RYF_DF_001".equals(Code)){
                    return  "系统错误";
            }else if("RYF_DF_002".equals(Code)){
                    return  "系统抛出错误";
            }else if("RYF_DF_003".equals(Code)){
                    return  "交易已经成功，请不要重复交易";
            }else if("RYF_DF_004".equals(Code)){
                    return  "交易处理中，请稍后再试";
            }else if("RYF_DF_005".equals(Code)){
                    return  "查询失败，请稍后再试";
            }else if("RYF_DF_006".equals(Code)){
                    return  "不支持的接口类型";
            }else if("RYF_DF_007".equals(Code)){
                    return  "没有找到相应订单记录";
            }else if("RYF_DF_008".equals(Code)){
                    return  "错误的参数（参数名）";
            }else if("RYF_DF_009".equals(Code)){
                    return  "错误的参数，参数（参数名）不能为空";
            }else if("RYF_DF_010".equals(Code)){
                    return  "无权限访问";
            }else if("RYF_DF_011".equals(Code)){
                    return  "签名验签错误";
            }else if("RYF_DF_012".equals(Code)){
                    return  "代付类型错误";
            }else{
                return "其他错误";
            }
     }

     /**
      *
      * @param respXml
      * @return new String["suc",RespMsg]
      */
     public static String[] getRespMsg(String respXml){
         String[] resp=new String[2];
         String respMsg="";
         Document doc=null;
		if(respXml==null||"".equals(respXml)){
            resp[0]="fail";
            resp[1]="返回报文错误";
			return resp;
		}
		try {
			doc=DocumentHelper.parseText(respXml);
            Element root=doc.getRootElement();
            Element status=root.element("status");
            String value=status.elementText("value");
            respMsg=getRespMsgFromCode(value);
            if(value.equals("RYF_DF_000")||value.equals("RYF_DF_003")||value.equals("RYF_DF_004")){
                resp[0]="suc";
            }else{
                resp[0]="fail";
            }
            resp[1]=respMsg;
		} catch (Exception e) {
			LogUtil.printErrorLog("RYF_DF_REQ_SERVER", "parseXml", "xml:"+respXml, e);
			 resp[0]="fail";
             resp[1]="解析返回报文错误";
			return resp;
		}
        
        return resp;

     }

	
}