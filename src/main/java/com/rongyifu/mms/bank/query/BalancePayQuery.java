package com.rongyifu.mms.bank.query;

import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.JsonUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 账户余额支付-单笔查询
 *
 */
public class BalancePayQuery extends ABankQuery{
	
	private static final String VERSION = "10";
	private static final String TRAN_CODE = "ZH0032";
	private static final String SYS = "RYF";
	private static final String TYPE = "1"; // 1：支付

	@Override
	public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
		// 获取请求参数
		Map<String, Object> requestParaMap = doRequestParams(gate, order);
		// 请求接口
		String retJsonStr = Ryt.requestWithPost(requestParaMap, gate.getRequestUrl());
		// 打印参数
		requestParaMap.put("response", retJsonStr);
		LogUtil.printInfoLog("账户余额支付-单笔查询", requestParaMap);
		// 处理结果
		doResponse(retJsonStr, gate);
		return queryRet;
	}
	
	/**
	 * 处理请求参数
	 * @param gate
	 * @param order
	 * @return
	 */
	private Map<String, Object> doRequestParams(GateRoute gate, Hlog order){
		Map<String, Object> paramsMap = new TreeMap<String, Object>();
		paramsMap.put("version", VERSION);
		paramsMap.put("tranCode", TRAN_CODE);
		paramsMap.put("sys", SYS);
		paramsMap.put("merId", order.getMid());
		paramsMap.put("orderId", order.getOid());
		paramsMap.put("orderDate", order.getMdate());
		paramsMap.put("type", TYPE);
		
		String timestamp = DateUtil.getNowDateTime();
		paramsMap.put("timestamp", timestamp);
		
		// 签名：version+tranCode+sys+merId+orderId+orderDate+type+timestamp
		String md5Key = gate.getMerKey();
		String signStr = VERSION + TRAN_CODE + SYS + order.getMid()
				+ order.getOid() + order.getMdate() + TYPE + timestamp + md5Key;
		String chkValue = QueryCommon.md5Encrypt(signStr);
		paramsMap.put("chkValue", chkValue);
		
		return paramsMap;
	}
	
	private void doResponse(String jsonString, GateRoute gate){
		Map<String, Object> resMap = JsonUtil.getMap4Json(jsonString);
		String resCode = String.valueOf(resMap.get("resCode"));
		if("000".equals(resCode)){ // 返回成功
			// 获取返回参数
			String tranCode = String.valueOf(resMap.get("tranCode"));
			String resMsg = String.valueOf(resMap.get("resMsg"));
			String merId = String.valueOf(resMap.get("merId"));
			String orderId = String.valueOf(resMap.get("orderId"));
			String status = String.valueOf(resMap.get("status"));
			String time = String.valueOf(resMap.get("time"));
			String chkValue = String.valueOf(resMap.get("chkValue"));
			
			// 验签：tranCode+resCode+resMsg+merId+orderId+status+time
			String md5Key = gate.getMerKey();
			String signStr = tranCode + resCode + resMsg + merId + orderId
					+ status + time + md5Key;
			String sign = QueryCommon.md5Encrypt(signStr);
			if(sign.equals(chkValue)){ // 验签通过
				// 判断支付结果
				if("S".equals(status)){
					queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
				} else
					queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
			} else { // 验签失败
				LogUtil.printErrorLog("订单号：" + orderId + "，验签失败");
			}
		} else 
			queryRet.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
	}
}
