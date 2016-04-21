package com.rongyifu.mms.refund.bank;

import java.util.HashMap;
import java.util.Map;

import cmb.netpayment.Settle;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.refund.IOnlineRefund;
import com.rongyifu.mms.refund.OnlineRefundBean;
import com.rongyifu.mms.refund.RefundUtil;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 招商银行联机退款类-直连退款
 * @author shdy
 *
 */
public class CMBRefund implements IOnlineRefund {
	private String gate_id="0021";/**渠道***/
	private String security="RongYiTong201000"; /**合作密钥***/
	private String mer_no="000400";/**商户号***/
	private String pwd="222222";/**密码***/
	@Override
	public OnlineRefundBean executeRefund(Object obj) {
		OnlineRefundBean refund=(OnlineRefundBean)obj;
		Object[] res=this.reqCMB(refund);
		String respMsg=String.valueOf(res[0]);
		String refundStat=String.valueOf(res[1]);
		refund.setRefundFailureReason(respMsg);
		refund.setOrderStatus(refundStat);
		return refund;
	}

	public Object[] reqCMB(OnlineRefundBean bean){
		Object[] res=new Object[2];
		String orderDate=String.valueOf(bean.getOrgOrderDate());/***原始订单日期***/
		String tseq=handle_orderNum(bean.getOrgTseq());//原始流水号
		String refundAmt=Ryt.div100(String.valueOf(bean.getRefAmt()));
		String remark=bean.getRefundReason();
		Settle settle=new Settle();
		settle.SetOptions("payment.ebank.cmbchina.com");
    	int iRet = settle.LoginC(gate_id,mer_no,pwd);
    	if (iRet == 0)
		{
    		//登陆成功
			LogUtil.printInfoLog("CMBRefund", "reqCMB", "LoginC ok, trans tseq:"+tseq);
	    } 
    	else
		{
    		//登陆失败
			res[0]=settle.GetLastErr(iRet);
	        res[1]=RefundUtil.ORDER_STATUS_FAILURE;
	        LogUtil.printErrorLog("CMBRefund", "reqCMB", "LoginC fail;"+res[0]);
			return res;
		}
    	Map<String, String> params=new HashMap<String, String>();
    	params.put("orderDate", orderDate);
    	params.put("tseq", tseq);
    	params.put("refundAmt", refundAmt);
    	params.put("remark", remark);
        iRet = settle.RefundOrder(orderDate,tseq,refundAmt,remark,security);
        if (iRet == 0) {
        	//成功
        	LogUtil.printInfoLog("CMBRefund", "reqCMB", "orgTseq:"+tseq+"refund success_msg:"+settle.GetLastErr(iRet),params);
        	res[0]="退款成功";
	        res[1]=RefundUtil.ORDER_STATUS_SUCCESS;
        }else {
        	//退款失败
        	LogUtil.printInfoLog("CMBRefund", "reqCMB", "orgTseq:"+tseq+"ErrorMsg:"+settle.GetLastErr(iRet),params);
        	res[0]=settle.GetLastErr(iRet);
	        res[1]=RefundUtil.ORDER_STATUS_FAILURE;
        }
        settle.Logout();
        return res;
	}
	
	
	public  String handle_orderNum(String tseq) {
		String num = "0000000000";
		int len = tseq.length();
		if (len < 10) {
			return num.substring(0, 10 - len) + tseq;
		} else {
			return tseq;
		}
	}
}
