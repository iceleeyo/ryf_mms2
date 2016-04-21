package com.rongyifu.mms.datasync;

import java.util.Map;

import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PosSyncDataProcessor implements ISyncDataProcessor {
	
	FileData data = null;
	@SuppressWarnings("rawtypes")
	PubDao dao = null;
//	String fileName = null;
	
	@SuppressWarnings("rawtypes")
	public PosSyncDataProcessor(FileData result, PubDao dao, String fileSuffix){
		data = result;
		this.dao = dao;
//		this.fileName = fileSuffix;
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
			LogUtil.printErrorLog("PosSyncDataProcessor", "process", "pos同步数据处理发生异常！", params);
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
		if(fields.length < 18){
			data.getErrorDatas().add("row[" + rowNum + "] [error]字段个数错误！");
			data.setSuccess(false);
			return;
		}
		
		PosSyncData posData = new PosSyncData();
		posData.setBid(fields[0]);
		posData.setMerId(fields[1]);
		posData.setOrdId(fields[2]);
		posData.setOrderDate(Integer.parseInt(fields[3]));
		posData.setSysDate(Integer.parseInt(handleSysDate(fields[8], fields[4], fields[5])));
		posData.setSysTime(handleTime(fields[5]));
		posData.setTransAmt(Long.parseLong(Ryt.mul100(fields[6])));
		posData.setPayAmt(Long.parseLong(Ryt.mul100(fields[7])));
		posData.setGateId(fields[8]);
		posData.setTransState(Integer.parseInt(fields[9]));
		posData.setBkSeq(fields[10]);
		posData.setBkResDate(Integer.parseInt(handleBkResDate(fields[11])));
		posData.setBkResTime(handleTime(fields[12]));
		posData.setBkFeeMode(handleBkFeeMode(fields[13]));
		posData.setCardNo(fields[14]);		
		posData.setPaidAmt(Long.parseLong(Ryt.mul100(fields[15])));
		posData.setBusiType(fields[16]);
		posData.setOperId(fields[17]);
		posData.setTransType(handleTransType(fields[2]));
		
		// 终端号
		if(fields.length > 18)
			posData.setTerminal(fields[18]);
		
		// 刷卡器编号
		if(fields.length > 19)
			posData.setPosId(handleStringEmpty(fields[19]));
		
		// 代理商号
		if (fields.length > 20)
			posData.setOrgCode(handleStringEmpty(fields[20]));

		// 处理商户手续费和支付渠道
		handleMerFee(posData, fields[6]);
		
		data.getDatas().add(posData);
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
	 * 处理商户手续费和支付渠道
	 * @param posData
	 * @param orgTransAmt
	 * @throws Exception 
	 */
	private void handleMerFee(PosSyncData posData, String orgTransAmt) throws Exception{
		String busiType = posData.getBusiType();
		long transAmt = posData.getTransAmt();
		long paidAmt = posData.getPaidAmt();
		String mid = posData.getMerId();
		String gateId = posData.getGateId();
		int transState = posData.getTransState();
		
		String merInfo[] = null;
		Map<String, Object> merInfoMap = data.getMerInfo();
		String key = mid + gateId;
		if(merInfoMap.containsKey(key)){
			merInfo = (String[]) merInfoMap.get(key);
		} else {		
			merInfo = DataSyncDb.getMerFeeModel(dao, mid, gateId);
			merInfoMap.put(key, merInfo);
		}
		
		String calcMode = merInfo[0];		// 商户手续费公式
		String gid = merInfo[1];			// 支付渠道
		
		int merFee = 0;
		if(transState == Constant.PayState.SUCCESS){ // 交易成功才计算手续费
			if(DataSyncUtil.POS_SD_BUSI.equals(busiType)){			// 收单业务
				if(orgTransAmt.indexOf("-") == -1)
					merFee = Integer.parseInt(Ryt.mul100(ChargeMode.reckon(calcMode, orgTransAmt, "0")));
				else
					merFee = - Integer.parseInt(Ryt.mul100(ChargeMode.reckon(calcMode, orgTransAmt.replace("-", ""), "0")));
			} else if(DataSyncUtil.POS_ZZ_BUSI.equals(busiType)){	// 转账业务
				merFee = (int)(transAmt - paidAmt);
			}
		}
		
		posData.setGid(handleGid(mid, gateId, gid));
		posData.setMerFee(merFee);
	}
	
	/**
	 * 处理手续费公式
	 * @param bkFeeMode
	 * @return
	 */
	private String handleBkFeeMode(String bkFeeMode){
		return Ryt.empty(bkFeeMode) || "null".equalsIgnoreCase(bkFeeMode.trim()) ? "" : bkFeeMode.trim();
	}
	
	/**
	 * 处理支付渠道
	 * @param mid
	 * @param gateId
	 * @param gid
	 * @return
	 * @throws Exception
	 */
	private String handleGid(String mid, String gateId, String gid) throws Exception{
		if(Ryt.empty(gid) || "null".equalsIgnoreCase(gid.trim()))
			throw new Exception("支付渠道为空，请检查商户[" + mid + "]的银行网关[" + gateId + "]配置！");
		return gid.trim();	
	}
	
	/**
	 * 处理银行应答日期
	 * @param BkResDate
	 * @return
	 */
	private String handleBkResDate(String BkResDate){
		return Ryt.empty(BkResDate) || "null".equalsIgnoreCase(BkResDate.trim()) ? "0" : BkResDate.trim();
	}
	
	/**
	 * 处理系统日期，如果是晚上23点 - 24点，则系统日期往后延一天
	 * @param gateId
	 * @param sysDate
	 * @param sysTime
	 * @return
	 */
	private String handleSysDate(String gateId, String sysDate, String sysTime){
		int time = Ryt.empty(sysTime) || "null".equalsIgnoreCase(sysTime) ? 0 : Integer.parseInt(sysTime);
		if(time >= 230000) // 北京银行、中信银行、银联POS都是23点日切
			return DataSyncUtil.getIntervalDate(sysDate, 1);
		return sysDate;		
	}
	
	/**
	 * 处理POS交易类型
	 * @param transAmt
	 * @return
	 */
	private String handleTransType(String ordId){
		if(Ryt.empty(ordId) || ordId.trim().length() <= 6)
			return null;
		
		String transTypeFlag = ordId.trim().substring(0, ordId.trim().length() - 6); // POS交易类型标识
		String transType = null;		
		if(transTypeFlag.endsWith("SC"))                           // 消费撤销冲正交易
			transType = DataSyncUtil.POS_TRANS_TYPE_CANCEL_RUSH;
		else if(transTypeFlag.endsWith("S"))                       // 消费撤销交易
			transType = DataSyncUtil.POS_TRANS_TYPE_CANCEL;
		else if(transTypeFlag.endsWith("C"))                       // 消费冲正交易
			transType = DataSyncUtil.POS_TRANS_TYPE_RUSH;
		else 
			transType = DataSyncUtil.POS_TRANS_TYPE_PAY;           // 消费交易
				
		return transType;
	}
	
	private String handleStringEmpty(String str){
		if(Ryt.empty(str) || "E".equalsIgnoreCase(str))
			return "";
		else
			return str.trim();
	}
}
