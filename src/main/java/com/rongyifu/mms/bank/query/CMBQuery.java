package com.rongyifu.mms.bank.query;

import cmb.netpayment.Settle;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 招商银行
 * 
 * @author lv.xiaofeng
 * 
 */
public class CMBQuery extends ABankQuery {
	public static final String BANK_GATE_ID = "0021";// 行号
	public static final String OPER_ID = "0001";// 操作员ID
	public static final String OPER_PWD = "123456";// 操作员密码

	public static void main(String args[]){
		GateRoute gate = new GateRoute();
		gate.setMerNo("000400");
		Hlog order = new Hlog();
		order.setTseq("3556225");
		order.setMdate(20130912);
		
		CMBQuery q = new CMBQuery();
		q.queryOrderStatusFromBank(gate, order);
	}
	
	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		Settle settle = new Settle();
		int iRet = settle.SetOptions("payment.ebank.cmbchina.com");
		if (iRet == 0) {
			iRet = settle.LoginC(BANK_GATE_ID, gate.getMerNo() + OPER_ID,
					OPER_PWD);
			if (iRet == 0) {
				StringBuffer strbuf = new StringBuffer();// 接收查询出的数据
				String orderId = order.getTseq();
				String tmp = "";
				if (orderId.length() < 10) {
					for (int i = 0; i < (10 - orderId.length()); i++) {
						tmp += "0";
					}
					orderId = tmp + orderId;
				}
				int qrStr = settle.QuerySingleOrder(order.getMdate() + "",
						orderId, strbuf);
				if (qrStr == 0) {
					// 查询成功 解析数据。格式:交易日期\n处理日期\n定单状态\n定单金额\n【结帐金额\n】
					if (strbuf.length() != 0) {// 查询的记录不为空再进行组装
						String[] data = strbuf.toString().split("\n");
						if (data[2].equals("0")) {
							queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
							LogUtil.printInfoLog("CMBQuery", "queryOrderStatusFromBank", strbuf.toString().replaceAll("\n", "\\|")+order.getTseq());
							settle.Logout();
							return queryRet;
						} else {
							queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
							settle.Logout();
							return queryRet;
						}
					}
				} else {
					LogUtil.printInfoLog("CMBQuery", "queryOrderStatusFromBank", "tseq=" + order.getTseq() + " error:[" + settle.GetLastErr(iRet) + "]");
				}
			} else {
				LogUtil.printInfoLog("CMBQuery", "queryOrderStatusFromBank", "tseq=" + order.getTseq() + " error:[" + settle.GetLastErr(iRet) + "]");
			}
		} else {
			LogUtil.printInfoLog("CMBQuery", "queryOrderStatusFromBank", "tseq=" + order.getTseq() + " error:[" + settle.GetLastErr(iRet) + "]");
		}
		
		queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
		settle.Logout();
		return queryRet;
	}
}
