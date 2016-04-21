package com.rongyifu.mms.bank.query;

import java.util.List;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public abstract class ABankQuery implements IBankQuery {

	protected BankQueryBean queryRet = new BankQueryBean();

	@Override
	public void updateOrderStatus(Hlog order, BankQueryBean queryRet) {
		if(QueryCommon.ORDER_STATUS_SUCCESS.equals(queryRet.getOrderStatus()))
			handleSuccess(order, queryRet);
		else if(QueryCommon.ORDER_STATUS_FAILURE.equals(queryRet.getOrderStatus()))
			handleFail(order, queryRet);
	}
	
	/**
	 * 处理成功交易
	 * @param order
	 * @param queryRet
	 */
	private static synchronized void handleSuccess(Hlog order, BankQueryBean queryRet) {
		// 商户手续费
		String feeModel = QueryCommon.getDao().getMerFeeModel(order);
		int merFee = (int) Double.parseDouble(ChargeMode.reckon(feeModel,
				String.valueOf(order.getAmount()), "0"));
		queryRet.setMerFee(merFee);

		// 修改订单sql
		String updateOrderSql = QueryCommon.getDao().getUpdateOrderSql(order,
				queryRet);

		// 记账户流水sql
		AccSeqs params = new AccSeqs();
		params.setUid(order.getMid());
		params.setAid(order.getMid());
		params.setTrAmt(order.getAmount());
		params.setTrFee(merFee);
		params.setAmt(order.getAmount() - merFee);
		params.setRecPay((short) 0);
		params.setTbName(Constant.TLOG);
		params.setTbId(order.getTseq());
		params.setRemark("支付");
		List<String> sqlList = RecordLiveAccount
				.recordAccSeqsAndCalLiqBalance(params);

		// sql批处理
		sqlList.add(updateOrderSql);
		String[] sqls = (String[]) sqlList.toArray(new String[sqlList.size()]);
		QueryCommon.getDao().batchSqlTransaction(sqls);

		LogUtil.printInfoLog("ABankQuery", "handleSuccess", "订单[" + order.getTseq() + "]支付成功！");
	}
	
	/**
	 * 处理失败交易
	 * @param order
	 * @param queryRet
	 */
	private void handleFail(Hlog order, BankQueryBean queryRet){
		// 修改订单sql
		String updateOrderSql = QueryCommon.getDao().getUpdateOrderSql(order, queryRet);
		QueryCommon.getDao().update(updateOrderSql);
		
		LogUtil.printInfoLog("ABankQuery", "handleFail", "订单[" + order.getTseq() + "]支付失败！");
	}
}
