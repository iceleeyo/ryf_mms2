package com.rongyifu.mms.bank.query;

import cert.CertUtil;

import com.bocom.netpay.b2cAPI.BOCOMB2CClient;
import com.bocom.netpay.b2cAPI.BOCOMB2COPReply;
import com.bocom.netpay.b2cAPI.OpResultSet;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;

/**
 * 交行单笔查询
 *    对应渠道：jh 
 *    业务：B2C/WAP
 */
public class BocomB2CQuery extends ABankQuery{
	
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		return query(order.getTseq());
	}
	
	public static BankQueryBean query(String order){
		BankQueryBean bankQuery = new BankQueryBean();
		bankQuery.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		
		BOCOMB2CClient client = new BOCOMB2CClient();
		int ret = client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));
		if (ret != 0)  //初始化失败
			return bankQuery;
		
		BOCOMB2COPReply rep = client.queryOrder(order); //订单查询
		if (rep == null)
			return bankQuery;
		
		String code = rep.getRetCode(); //得到交易返回码
		if("0".equals(code)){
			OpResultSet oprSet = rep.getOpResultSet(); 
			int iNum  = oprSet.getOpresultNum();//总交易记录数
			for (int index = 0; index <= iNum - 1; index++) {
				String tranState 		= oprSet.getResultValueByName(index,"tranState"); 		//支付交易状态1[成功]
				String orderState 		= oprSet.getResultValueByName(index, "orderState"); 	//订单状态[0 未支付、1 已支付、2 已撤销、3 已部分退货、4退货处理中、5 已全额退货]
				String bankSerialNo 	= oprSet.getResultValueByName(index, "bankSerialNo"); 	//银行流水号
				
				if("1".equals(tranState.trim()) && "1".equals(orderState.trim())){
					bankQuery.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
					bankQuery.setBankSeq(bankSerialNo);
				}
			}
		}
		return bankQuery;
	}
}
