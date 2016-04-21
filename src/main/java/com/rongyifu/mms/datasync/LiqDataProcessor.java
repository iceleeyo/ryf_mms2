package com.rongyifu.mms.datasync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class LiqDataProcessor {
	
	public void process(){
		if(!Ryt.isStartService("检查后台数据处理服务"))
			return;
		
		// 处理pos中信银行数据		
		process_Pos_CITIC_Data(null);
	}
	
	private Map<String, OrderInfo> payData = new HashMap<String, OrderInfo>();        // 消费
	private Map<String, OrderInfo> cancelData = new HashMap<String, OrderInfo>();     // 消费撤销
	private Map<String, List<OrderInfo>> rushData = new HashMap<String, List<OrderInfo>>();       // 消费冲正
	private Map<String, OrderInfo> cancelRushData = new HashMap<String, OrderInfo>(); // 消费撤销冲正
	
	/**
	 * 处理pos中信银行数据
	 * @param flowId 批次号
	 */
	@SuppressWarnings("rawtypes")
	public void process_Pos_CITIC_Data(String flowId){
		try{
			if(Ryt.empty(flowId))
				flowId = DataSyncUtil.POS + "_" + DateUtil.systemDate(-1);
			else
				flowId = DataSyncUtil.POS + "_" + flowId;
			
			PubDao dao = new AdminZHDao();
			// 查询数据
			List<OrderInfo> data = get_Pos_CITIC_Data(dao, flowId);
			if(data == null || data.size() == 0){
				LogUtil.printInfoLog("LiqDataProcessor", "process_Pos_CITIC_Data", "没有中信银行数据！");
				return;
			}
			
			// 数据处理，区分各类交易类型
			initMapData(data);
            
			if(payData == null || payData.size() == 0){
				LogUtil.printInfoLog("LiqDataProcessor", "process_Pos_CITIC_Data", "没有消费数据，请检查数据同步是否正常！");
				throw new Exception("没有消费数据，请检查数据同步是否正常！");
			}
			
			// 根据规则处理数据
			processLiqData();
			
			// 数据保存
			saveLiqData(dao, data, flowId);
		} catch(Exception e){
			e.printStackTrace();
			DataSyncUtil.sendMail("pos中信银行同步数据处理发生异常，错误信息：" + e.getMessage(), DataSyncUtil.POS, flowId);
		} finally {
			clear();
		}
	}
	
	/**
	 * 数据处理，区分各类交易类型
	 * @param data
	 */
	private void initMapData(List<OrderInfo> data){
		for(OrderInfo item : data){
			String oid = item.getOid();
			if(Ryt.empty(oid)){
				LogUtil.printErrorLog("LiqDataProcessor", "initMapData", "订单号为空！电银流水号：" + item.getTseq());
				continue;
			} else 
				oid = oid.trim();
			
			if(oid.length() > 20)
				oid = oid.substring(0, 20);
			
			String mid =  item.getMid();
			String key = mid + "-" + oid;
			
			if(DataSyncUtil.POS_TRANS_TYPE_PAY.equals(item.getP1()))
				payData.put(key, item);
			else if(DataSyncUtil.POS_TRANS_TYPE_CANCEL.equals(item.getP1()))
				cancelData.put(key, item);
			else if(DataSyncUtil.POS_TRANS_TYPE_RUSH.equals(item.getP1())){
				if(rushData.containsKey(key)){
					rushData.get(key).add(item);
				} else {
					List<OrderInfo> rushList = new ArrayList<OrderInfo>();
					rushList.add(item);
					rushData.put(key, rushList);
				}
			} else if(DataSyncUtil.POS_TRANS_TYPE_CANCEL_RUSH.equals(item.getP1()))
				cancelRushData.put(key, item);
		}
		
		StringBuffer logContent = new StringBuffer();
		logContent.append("共有" + data.size() + "条数据，");
		logContent.append("其中消费" + payData.size() + "条，");
		logContent.append("冲正" + rushData.size() + "条，");
		logContent.append("撤销" + cancelData.size() + "条，");
		logContent.append("撤销冲正" + cancelRushData.size() + "条");
		LogUtil.printInfoLog("LiqDataProcessor", "initMapData", logContent.toString());
	}
	
	/**
	 * 根据规则处理数据
	 */
	private void processLiqData(){
		// 处理消费、消费冲正
		handleRushData();
		
		// 处理消费、消费撤销、消费撤销冲正
		handleCancelData();
	}
	
	/**
	 * 处理消费、消费冲正
	 */
	private void handleRushData() {
		Iterator<String> rushKeys = rushData.keySet().iterator();
		while (rushKeys.hasNext()) {
			String rKey = rushKeys.next();

			if (!payData.containsKey(rKey)) {
				LogUtil.printErrorLog("LiqDataProcessor", "handleRushData", "冲正数据找不到对应的消费数据：" + rKey);
				continue;
			}
            
			OrderInfo payOrder = payData.get(rKey);
			List<OrderInfo> rushList = rushData.get(rKey);
			for(OrderInfo rushOrder : rushList){
				short rushState = rushOrder.getTstat();
				
				if (rushState == Constant.PayState.SUCCESS) {  // 冲正成功，则消费、冲正状态都改成待支付
					rushOrder.setTstat((short) Constant.PayState.WAIT_PAY);
					payOrder.setTstat((short) Constant.PayState.WAIT_PAY);
				} else if (rushState == Constant.PayState.WAIT_PAY) {  // 冲正待支付，则消费状态改成待支付
					payOrder.setTstat((short) Constant.PayState.WAIT_PAY);
				}
			}
		}
	}
	
	/**
	 * 处理消费、消费撤销、消费撤销冲正
	 */
	private void handleCancelData(){
		Iterator<String> cancelKeys = cancelData.keySet().iterator();
		while(cancelKeys.hasNext()){
			String cKey = cancelKeys.next();
			OrderInfo cancelOrder = cancelData.get(cKey);
			
			if (!payData.containsKey(cKey)) {
				LogUtil.printErrorLog("LiqDataProcessor", "handleCancelData", "撤销数据找不到对应的消费数据：" + cKey);
				continue;
			}
			
			OrderInfo payOrder = payData.get(cKey);			
			if(cancelOrder.getTstat() == Constant.PayState.SUCCESS){  // 消费撤销成功，对消费、消费撤销冲正数据进行处理
				
				payOrder.setTstat((short) Constant.PayState.SUCCESS); // 消费改成成功
				
				if(cancelRushData.containsKey(cKey)){ // 如果做过消费撤销冲正
					
					OrderInfo cancelRushOrder = cancelRushData.get(cKey);
					if(cancelRushOrder.getTstat() == Constant.PayState.SUCCESS){  // 消费撤销冲正成功
						
						cancelOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());  // 消费撤销勾对成功
						cancelRushOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());  // 消费撤销冲正勾对成功
						
					} else if(cancelRushOrder.getTstat() == Constant.PayState.WAIT_PAY){ // 消费撤销冲正待支付，消费、消费撤销都改成待支付
						
						payOrder.setTstat((short) Constant.PayState.WAIT_PAY);
						cancelOrder.setTstat((short) Constant.PayState.WAIT_PAY);
						
					} else if(cancelRushOrder.getTstat() == Constant.PayState.FAILURE){  // 消费撤销冲正失败，消费、消费撤销勾对成功
						
						payOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());
						cancelOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());
					}
					
				} else { // 如果没有做消费撤销冲正，则消费、消费撤销勾对成功
					payOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());
					cancelOrder.setBkChk(DataSyncUtil.BK_CHECK_SUCCESS.shortValue());
				}				
			} else if(cancelOrder.getTstat() == Constant.PayState.WAIT_PAY){  // 消费撤销是待支付，消费、消费撤销冲正都改成待支付
				payOrder.setTstat((short) Constant.PayState.WAIT_PAY);  // 消费改成待支付
				
				if(cancelRushData.containsKey(cKey))  // 消费撤销冲正改成待支付
					cancelRushData.get(cKey).setTstat((short) Constant.PayState.WAIT_PAY);
			}
		}
	}
	
	/**
	 * 数据保存
	 * @param dao
	 * @param data
	 */
	@SuppressWarnings("rawtypes")
	private void saveLiqData(PubDao dao, List<OrderInfo> data, String flowId){
		int errorCount = 0;
		
		for(OrderInfo order : data){
			if(order.getTstat() == Constant.PayState.SUCCESS){
				
				AccSeqs params = new AccSeqs();
				params.setUid(order.getMid());
				params.setAid(order.getMid());
				params.setTrAmt(order.getAmount());
				params.setTrFee(order.getFeeAmt());
				params.setAmt(order.getAmount() - order.getFeeAmt());
				params.setRecPay((short)Constant.RecPay.INCREASE);
				params.setTbName(Constant.HLOG);
				params.setTbId(order.getTseq());
				params.setRemark(DataSyncUtil.POS + "同步");
				
				// 账户流水记未结算金额
				List<String> list = RecordLiveAccount.recordAccSeqsAndCalLiqBalance(params);
				
				short bkChk = order.getBkChk() == null ? 0 : order.getBkChk().shortValue();
				if(bkChk == DataSyncUtil.BK_CHECK_SUCCESS.shortValue()){
					// 计算银行手续费
					String bankFee = ChargeMode.reckon(order.getBkFeeModel(), String.valueOf(order.getPayAmt()), "0");
					// 交易成功、勾对成功、记录账户流水
					String orderSql = "UPDATE hlog SET tstat = " + Constant.PayState.SUCCESS + ", bk_chk = 1, bank_fee = " + bankFee + " WHERE tseq = " + order.getTseq();
					list.add(orderSql);
				} else {
					// 交易成功、记录账户流水
					String orderSql = "UPDATE hlog SET tstat = " + Constant.PayState.SUCCESS + " WHERE tseq = " + order.getTseq();
					list.add(orderSql);
				}
				
				try {
					dao.batchSqlTransaction2(list);
				} catch (Exception e) {
					errorCount++;
					e.printStackTrace();
				}
				
			} else {
				// 修改交易状态
				String sql = "UPDATE hlog SET tstat = " + order.getTstat() + " WHERE tseq = " + order.getTseq();
				dao.update(sql);
			}
		}
		
		if(errorCount != 0)
			DataSyncUtil.sendMail("pos中信银行同步数据有" + errorCount + "条保存发生异常，详情见日志！", DataSyncUtil.POS, flowId);
	}
	
	/**
	 * 清理数据
	 */
	private void clear(){
		if(payData != null)
			payData.clear();
		if(cancelData != null)
			cancelData.clear();
		if(rushData != null)
			rushData.clear();
		if(cancelRushData != null)
			cancelRushData.clear();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OrderInfo> get_Pos_CITIC_Data(PubDao dao, String flowId){
		String sql = "select tseq,mid,oid,amount,pay_amt,fee_amt,p1,bk_fee_model,tstat from hlog where gate = 30019 and type = 10 and data_source = 3 and p9 like '" + flowId + "%' and p10 = '01'";
		LogUtil.printInfoLog("LiqDataProcessor", "get_Pos_CITIC_Data", sql);
		return dao.query(sql, OrderInfo.class);
	}

}
