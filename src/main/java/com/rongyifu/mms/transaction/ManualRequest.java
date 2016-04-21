package com.rongyifu.mms.transaction;

import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import org.springframework.jdbc.core.JdbcTemplate;

import rytong.encrypto.provider.RsaEncrypto;
import cert.CertUtil;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class ManualRequest{
	private static HashMap<Short, String> tstatMap = new HashMap<Short, String>();
	public static final String RECV_CODE = "RECV_RYT_ORD_ID_";
	static{
		tstatMap.put((short)2,"S");
		tstatMap.put((short)3,"F");
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void requestBgRetUrl(String refId, String refundState){
		Map<String, String> logParams = new HashMap<String, String>();
		logParams.put("refId", refId);
		logParams.put("refundState", refundState);
		
		StringBuffer requestUrl = new StringBuffer();   //请求地址
		String pSigna = "";       // 签名字符串
		JdbcTemplate jt = new PubDao().getJdbcTemplate();
		String sql = "select h.mid,h.oid,h.mdate,h.amount,h.refund_amt,r.bgRetUrl,h.tseq,r.ref_amt,r.pro_date,r.stat,r.mer_priv  " +
				"from hlog h,refund_log r " + "where h.mid=r.mid and h.oid=r.org_oid and h.mdate=r.org_mdate and r.id=" + refId;
		//merId ordId merDate transType transAmt refundAmt reffundCount reffundDate tseq refundState pSigna
		Map m = null;
		try {
			m = jt.queryForMap(sql);
		} catch (Exception e) {
			e.printStackTrace();
			m = jt.queryForMap(sql.replaceAll(Constant.HLOG, Constant.TLOG));
		}
		
		logParams.put("bgRetUrl", String.valueOf(m.get("bgRetUrl")));
		
		if(m.get("bgRetUrl") == null || m.get("bgRetUrl").equals("")) {
			LogUtil.printInfoLog("ManualRequest", "requestBgRetUrl(String refId, String refundState)", "", logParams);
			return ;
		}
		
		requestUrl.append(m.get("bgRetUrl"));
		String chkValueText = m.get("mid").toString() + m.get("oid").toString() + m.get("mdate").toString() + 
		    m.get("amount").toString() + m.get("ref_amt").toString() + m.get("pro_date").toString() + m.get("tseq").toString();
		String privateKey = RypCommon.getKey(CertUtil.getCertPath("privatekey"));
		try {
			pSigna = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText.getBytes(), privateKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestUrl.append("?merId=").append(m.get("mid"));
		requestUrl.append("&ordId=").append(m.get("oid"));
		requestUrl.append("&merDate=").append(m.get("mdate"));
		requestUrl.append("&transType=R");
		requestUrl.append("&transAmt=").append(m.get("amount"));
		requestUrl.append("&refundAmt=").append(m.get("ref_amt"));
		requestUrl.append("&reffundCount=").append(m.get("refund_amt"));
		requestUrl.append("&reffundDate=").append(m.get("pro_date"));
		requestUrl.append("&tseq=").append(m.get("tseq"));
		requestUrl.append("&refundState=").append(refundState);
		requestUrl.append("&merPriv=").append(Ryt.empty((String) m.get("mer_priv")) ? "" : m.get("mer_priv"));
		requestUrl.append("&pSigna=").append(pSigna);
		
		logParams.put("requestUrl", requestUrl.toString());
		
		try {
			String merResponse = RypCommon.httpRequest(requestUrl.toString());
			
			logParams.put("merRes", merResponse);
			LogUtil.printInfoLog("ManualRequest", "requestBgRetUrl(String refId, String refundState)", "", logParams);
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("ManualRequest", "requestBgRetUrl(String refId, String refundState)", "", logParams);
		}
	}
	
	
	/**
	 * 获取明细查询和当天交易查询中手工发送请求的地址
	 * @param hlog 在明细查询中为hlog对象  在当天交易查询中为tlog对象
	 * @return URL地址
	 */
	@SuppressWarnings("deprecation")
	public String requestBkUrl(Hlog hlog){
		Map<String, String> logParams = new HashMap<String, String>();		
		
		int now_t = DateUtil.getCurrentUTCSeconds();
		StringBuffer requestUrl = new StringBuffer();
		String pSigna = "";// 签名字符串
		
		String pSigna2 = "";
		
		String msg = "";
		requestUrl.append(hlog.getBkUrl());//请求银行地址
		String chkValueText = hlog.getOid() + hlog.getMid() +Ryt.div100(hlog.getAmount()) + hlog.getMdate() + hlog.getTseq();
		
		String chkValueText2 = hlog.getOid() + hlog.getMid() +Ryt.div100(hlog.getAmount()) + hlog.getMdate() + hlog.getTseq()+ tstatMap.get(hlog.getTstat());
		
		
		// 写在配置文件里
		//String privateKey = RypCommon.getKey(Ryt.getParameter("BKURL_SIGN_PRI"));
		String privateKey = RypCommon.getKey(CertUtil.getCertPath("privatekey"));
		try {
			pSigna = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText.getBytes(), privateKey));
			
			pSigna2 = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText2.getBytes(), privateKey));
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			logParams.put("pSigna", pSigna);
			logParams.put("pSigna2", pSigna2);
			logParams.put("bkUrl", hlog.getBkUrl());
			LogUtil.printErrorLog("ManualRequest", "requestBkUrl(Hlog hlog)", "", logParams);
			
			return "请求失败!";
		}
		requestUrl.append("?ordId=").append(hlog.getOid()); //商户的订单号
		requestUrl.append("&merId=").append(hlog.getMid()); //商户号
		requestUrl.append("&transAmt=").append(Ryt.div100(hlog.getAmount())); //交易金额
		requestUrl.append("&transDate=").append(hlog.getMdate()); //商户日期
		requestUrl.append("&tseq=").append(hlog.getTseq()); //融易通流水号
		requestUrl.append("&transStat=").append(tstatMap.get(hlog.getTstat()));//交易状态  S 成功  F 失败
		requestUrl.append("&merPriv=").append(hlog.getMerPriv().trim());//商户私有域
		requestUrl.append("&bankTime=").append(now_t);// 银行返回时间
		requestUrl.append("&errorMsg=").append(msg); //错误信息  此处为空  是否要填写相应的错误信息
		requestUrl.append("&gateId=").append(hlog.getGate());//网关号
		requestUrl.append("&pSigna=").append(pSigna); //签名项
		requestUrl.append("&pSigna2=").append(pSigna2); //签名项
		
		logParams.put("requestBkUrl", requestUrl.toString());
		
		try {			
			String responseMsg = RypCommon.httpRequest(requestUrl.toString());			
			
			logParams.put("responseMsg", responseMsg);
			LogUtil.printInfoLog("ManualRequest", "requestBkUrl(Hlog hlog)", "", logParams);
		} catch (SocketTimeoutException e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("ManualRequest", "requestBkUrl(Hlog hlog)", "", logParams);
			
			return "连接商户后台超时!";
		} catch (Exception e) {
			logParams.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("ManualRequest", "requestBkUrl(Hlog hlog)", "", logParams);
			
			return "请求失败!";
		}
		return "请求成功!";
	}
}
