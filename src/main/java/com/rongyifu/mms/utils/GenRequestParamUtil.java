package com.rongyifu.mms.utils;

//import java.text.DecimalFormat;
import java.util.Map;

import cert.CertUtil;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;

public class GenRequestParamUtil {
	/**
	 * 返回签名和加密后的map
	 * @param transAmt  交易金额（乘了100的）
	 * @param transFee  手续费（乘了100的）
	 * @param gate
	 * @param ordId
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> getParamKeyMap(long transAmt,Integer transFee,Integer gate,String ordId) throws Exception{
		String realTransAmt=Ryt.div100(transAmt);//实际交易金额/100
		String realTransFee=Ryt.div100(transFee);//交易手续费/100
		
		String mid="1";//
		String version="10";
		Integer orderDate=DateUtil.today();
		String transType="P";
		String merPriv="";
		String retUrl="";
		String bgRetUrl=ParamCache.getStrParamByName("PAY_RET_URL");
		String body="";
		body +="&version="+version;
		body += "&merId=" + mid;
		body += "&ordId=" + ordId;
		body += "&orderDate=" + orderDate;
		body += "&transType=" + transType;
		body += "&transAmt=" + realTransAmt;
		body += "&gateId=" + gate;
		body += "&merPriv=" + merPriv;
		body += "&retUrl=" + retUrl;
		body += "&bgRetUrl=" + bgRetUrl;
		body += "&transPeriod=1";
		String chkValue = version + mid + ordId + realTransAmt
				+ orderDate + transType + gate + merPriv + retUrl
				+ bgRetUrl; // 商家签名
		
		String privateKey= CertUtil.getRyfPrivateKey();
		String publicKey=CertUtil.getRyfPublicKey();
		Map<String,String> paramMap= CryptoUtils.rytEncrypt(body,chkValue, privateKey, publicKey);
		String actionUrl=ParamCache.getStrParamByName("EWP_PATH")+"pay/trans_entry";//跳转地址
		paramMap.put("actionUrl",actionUrl);
		paramMap.put("ordId",ordId);
		paramMap.put("transFee",realTransFee);
		paramMap.put("transAmt",realTransAmt);
		return paramMap;
	}
}
