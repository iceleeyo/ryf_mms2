package com.rongyifu.mms.datasync;

import java.util.Map;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class VasSyncDataProcessor implements ISyncDataProcessor{
	FileData data = null;
	PubDao dao = null;
	
	public VasSyncDataProcessor(FileData result, PubDao dao){
		data = result;
		this.dao = dao;
	}
	
	public void process(int rowNum, String rowData) {
		try {
			if (rowNum == 1) {
				handleFirstRow(rowNum, rowData);
			} else {
				parseColumns(rowNum, rowData);
			}
			
		} catch (Exception e) {
			data.getErrorDatas().add("row["+rowNum+"]"+rowData+"[error]"+e.getMessage());
			data.setSuccess(false);
			
			Map<String, String> params = LogUtil.createParamsMap();
			params.put("rowNum", String.valueOf(rowNum));
			params.put("rowData", rowData);
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("VasSyncDataProcessor", "process", "vas同步数据处理发生异常！", params);
		}
		
		data.setDataRows(data.getDataRows() + 1);
	}
	
	/**
	 * 处理首行
	 * @param rowData
	 */
	private void handleFirstRow(int rowNum, String rowData){
		if(rowData == null){
			data.getErrorDatas().add("row[" + rowNum + "] [error]首行不能为空！");
			data.setSuccess(false);
		}
		
		int dataRows = 0;
		try{
			dataRows = Integer.parseInt(rowData.trim());
		} catch(Exception e){
			data.getErrorDatas().add("row[" + rowNum + "]" + rowData + " [error]首行为数据总行数，必须是数字！");
			data.setSuccess(false);
			e.printStackTrace();
		}
		
		data.setDataSum(dataRows);
	}
	
	/**
	 * 处理字段
	 * @param rowNum
	 * @param rowData
	 * @throws Exception 
	 */
	private void parseColumns(int rowNum, String rowData) throws Exception{
		if(Ryt.empty(rowData)){
			data.getErrorDatas().add("row[" + rowNum + "] [error]数据行不能为空！");
			data.setSuccess(false);
			return;
		}
		
		String fields[] = rowData.split("\t");
		if(fields.length != 12){
			data.getErrorDatas().add("row[" + rowNum + "] [error]字段个数错误！");
			data.setSuccess(false);
			return;
		}
		
		VasSyncData vasData = new VasSyncData();
		vasData.setMerId(fields[0]);
		vasData.setOrdId(fields[1]);
		vasData.setOrderDate(fields[2]);		
		vasData.setTransAmt(Long.parseLong(Ryt.mul100(fields[3])));
		vasData.setPreAmt(Long.parseLong(Ryt.mul100(fields[4])));
		vasData.setPreAmt1(Long.parseLong(Ryt.mul100(fields[5])));		
		vasData.setPayAmt(Long.parseLong(Ryt.mul100(fields[6])));
		handleGateId(vasData, fields[7]);
		handleTransState(vasData, fields[8]);
		vasData.setBkSeq(fields[9]);
		vasData.setSysDate(Integer.parseInt(handleSysDate(fields[7], fields[10], fields[11])));
		vasData.setSysTime(handleTime(fields[11]));
		
		// 处理商户手续费和支付渠道
		handleMerFee(vasData, fields[6]);
		
		data.getDatas().add(vasData);
	}
	
	/**
	 * 将时间（HHmmss）转成UTC秒
	 * @param time
	 * @return
	 */
	private int handleTime(String time){
		return DateUtil.getUTCTime(time);
	}
	
	/**
	 * 处理交易状态
	 * @param vasData
	 * @param transState
	 * @throws Exception
	 */
	private void handleTransState(VasSyncData vasData, String transState) throws Exception{
		if(Ryt.empty(transState))
			throw new Exception("交易状态码为空！");
		
		if("S".equals(transState.trim()))		//	交易成功
			vasData.setTransState(2);
		else if("F".equals(transState.trim()))	//	交易失败
			vasData.setTransState(3);
		else
			throw new Exception("交易状态码错误：" + transState);
	}
	
	/**
	 * 处理网关
	 * @param vasData
	 * @param gateId
	 * @throws Exception
	 */
	private void handleGateId(VasSyncData vasData, String gateId) throws Exception{
		int gate = Integer.parseInt(gateId);
		if(gate >= 30000 && gate < 40000)
			vasData.setGateId(gateId);
		else
			throw new Exception("银行网关错误：" + gateId);
	}
	
	/**
	 * 处理商户手续费和支付渠道
	 * @param vasData
	 * @param orgTransAmt
	 */
	private void handleMerFee(VasSyncData vasData, String transAmt){
		String mid = vasData.getMerId();
		String gateId = vasData.getGateId();
		String mapKey = mid + gateId;
		int transState = vasData.getTransState();
		
		String merInfo[] = null;
		Map<String, Object> merInfoMap = data.getMerInfo();
		if(merInfoMap.containsKey(mapKey)){
			merInfo = (String[]) merInfoMap.get(mapKey);
		} else {		
			merInfo = DataSyncDb.getMerFeeModel(dao, mid, gateId);
			merInfoMap.put(mapKey, merInfo);
		}
		
		String calcMode = merInfo[0];		// 商户手续费公式
		String gid = merInfo[1];			// 支付渠道
		String bkFeeMode = merInfo[2];		// 银行手续费公式
		String transMode = merInfo[3];		// 交易类型
		
		int merFee = 0;
		if(transState == Constant.PayState.SUCCESS){ // 交易成功才计算手续费
			merFee = Integer.parseInt(Ryt.mul100(ChargeMode.reckon(calcMode, transAmt, "0")));
		}
		
		vasData.setBkFeeMode(bkFeeMode);
		vasData.setGid(gid);
		vasData.setMerFee(merFee);
		vasData.setTransMode(Integer.parseInt(transMode));
	}
	
	/**
	 * 处理POS数据系统日期，如果是晚上23点 - 24点，则系统日期往后延一天
	 * @param sysDate
	 * @param sysTime
	 * @return
	 */
	private String handleSysDate(String gateId, String sysDate, String sysTime){
		int time = Ryt.empty(sysTime) || "null".equalsIgnoreCase(sysTime) ? 0 : Integer.parseInt(sysTime);
		if("30013".equals(gateId) && time >= 230000)
			return DataSyncUtil.getIntervalDate(sysDate, 1);
		return sysDate;		
	}
}