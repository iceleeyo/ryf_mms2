package com.rongyifu.mms.refund.bank;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.ErrorCodes;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 支付宝联机退款处理类（祥鹏）
 *
 */
public class AlipayXPRefund implements IOnlineRefund {

	public static final String BANK_MERID = "2088801851114955";// 支付宝的ID
	public static final String INPUT_CHARSET = "utf-8";
	public static final String SIGN_TYPE = "MD5";
	public static final String BANK_SERVICE = "refund_fastpay_by_platform_nopwd";
	public static final String MD5_KEY = "yvpe4j463tapz4esndqyq50wu80oi6ye";
	public static final String BATCH_NUM = "1";
	public static final String notify_url=RefundUtil.querywep()+"/ref/alipay_ref";
		
		@Override
		public OnlineRefundBean executeRefund(Object obj) {		
			OnlineRefundBean onlineRefundBean=new OnlineRefundBean();
			OnlineRefundBean refundLog=(OnlineRefundBean)obj;
			String tseq=refundLog.getBkTseq();
			String amount=Ryt.div100(refundLog.getRefAmt()+"");
			java.text.DateFormat format3= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
			String date = format3.format(new Date());
			String Refund_reason="";
			String detail_data=tseq+"^"+amount+"^"+Refund_reason;
			String url = "https://mapi.alipay.com/gateway.do";
			// 批次号生成规则：YYYYMMDD + 退款单号
			String batchNo = DateUtil.today() + "" + refundLog.getId();
			String[] param = { "service=" + BANK_SERVICE + "",
					"partner=" + BANK_MERID + "",
					"_input_charset=" + INPUT_CHARSET + "",
					"notify_url="+notify_url+"",
					"batch_no="+batchNo+"",
					"refund_date="+date+"",
					"batch_num="+BATCH_NUM+"",
					"detail_data="+detail_data+""};
			// 排序
			Arrays.sort(param);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < param.length; i++) {
				sb.append(param[i]).append("&");
			}
			String sig = sb.toString();
			String sign = sig.substring(0, sig.lastIndexOf("&"));
			String sign_md5 = sign + MD5_KEY;
			String md5_str =RefundUtil.md5Encrypt(sign_md5);
			Map<String, String> requestParaMap = new HashMap<String, String>();
			for (int i = 0; i < param.length; i++) {
				String[] tmp = param[i].split("=");
				requestParaMap.put(tmp[0], tmp[1]);
			}
			requestParaMap.put("sign", md5_str);
			requestParaMap.put("sign_type", SIGN_TYPE);
			try {
				String repose =RefundUtil.requestByPostwithURL(requestParaMap, url);
				LogUtil.printInfoLog("AlipayXPRefund", "alipay rerutn result ", repose);
				Document doc = DocumentHelper.parseText(repose);
				Element root = doc.getRootElement();
				String issuccess = root.elementText("is_success");
				String error= root.elementText("error");
				if(null==issuccess){
					onlineRefundBean.setRefundFailureReason("请求银行失败!");				
					onlineRefundBean.setOrderStatus(RefundUtil.QUERT_BANK_FAILURE);				
				}
				if ("T".equals(issuccess)) {					
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_ACCEPT);
				}else if("F".equals(issuccess)){
					String errorMsg = ErrorCodes.Alipay_Refund.get(error);
					onlineRefundBean.setRefundFailureReason(errorMsg);
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_FAILURE);
				}else if("P".equals(issuccess)){
					onlineRefundBean.setOrderStatus(RefundUtil.ORDER_STATUS_PROCESSED);
				}
				else{
					onlineRefundBean.setRefundFailureReason("状态未知!");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			onlineRefundBean.setRefBatch(refundLog.getRefBatch());
			onlineRefundBean.setId(refundLog.getId());
			return onlineRefundBean;
		}
}