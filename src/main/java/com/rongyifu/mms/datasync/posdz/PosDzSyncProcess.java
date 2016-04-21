package com.rongyifu.mms.datasync.posdz;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.datasync.FileData;
import com.rongyifu.mms.datasync.ISyncDataProcessor;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.ewp.TestUtil;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PosDzSyncProcess implements ISyncDataProcessor {
	FileData data = null;
	@SuppressWarnings("rawtypes")
	PubDao dao = null;
	String fileSuffix=null; //文件后罪名
	String transType_zz="480000";//转账交易处理码
	String czState="1";//冲正标示符 1为冲正
	Map<String, FeeCalcMode> feeCalcModeMap=null;

	@SuppressWarnings("rawtypes")
	public PosDzSyncProcess(FileData fileData, PubDao dao, String fileSuffix) {
		super();
		this.data = fileData;
		this.dao = dao;
		this.fileSuffix=fileSuffix;
		feeCalcModeMap=new HashMap<String, FeeCalcMode>();
	}
	

	@Override
	public void process(int rowNum, String rowData)  {
		try {
			
			if(rowNum==1){
				//首行不处理
			}else if (rowNum == 2) {
				//第二行开始处理
				handleFirstRow(rowNum, rowData);
			} else {
				if(this.fileSuffix==DataSyncUtil.PosDz_FILE_SUFFIX){
					parseData(rowNum, rowData);
				}else if(this.fileSuffix==DataSyncUtil.PosDz_cc_FILE_SUFFIX){
					parseDataForCc(rowNum, rowData);
				}
			}
			
		} catch (Exception e) {
			data.getErrorDatas().add("row["+rowNum+"]"+rowData+"[error]"+e.getMessage());
			data.setSuccess(false);
			
			Map<String, String> params = LogUtil.createParamsMap();
			params.put("rowNum", String.valueOf(rowNum));
			params.put("rowData", rowData);
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("PosDzSyncDataProcessor", "process", "pos清帐数据同步处理发生异常！", params);

		}
		
	   data.setDataRows(data.getDataRows() + 1);
	}

	/**
	 * 处理首行
	 * 
	 * @param rowData
	 */
	private void handleFirstRow(int rowNum, String rowData) {
		if (rowData == null) {
			data.getErrorDatas().add("row[" + rowNum + "]"+rowData+" [error]第四行不能为空！");
			data.setSuccess(false);
		}

		int dataRows = 0;
		try {
			dataRows = Integer.parseInt(rowData.trim());
		} catch (Exception e) {
			data.getErrorDatas()
					.add("row[" + rowNum + "]" + rowData
							+ " [error]第四行为数据总行数，必须是数字！");
			data.setSuccess(false);
			e.printStackTrace();
		}

		data.setDataSum(dataRows);
	}

	/***
	 * 解析数据   对账数据
	 * 
	 * @param rowNum
	 * @param rowData
	 * @throws Exception 
	 */
	private void parseData(int rowNum, String rowData) throws Exception {

		if (Ryt.empty(rowData)) {
			data.getErrorDatas().add("row[" + rowNum + "]"+rowData+ "[error]数据行不能为空！");
			data.setSuccess(false);
			return;
		}

		String fields[] = handleFields(rowData.split("\\|"));
		if (fields.length < 28) {
			data.getErrorDatas().add("row[" + rowNum + "]"+rowData+" [error]字段个数错误！");
			data.setSuccess(false);
			return;
		}
		PosDzSyncData dzSyncData = new PosDzSyncData();
		dzSyncData.setBkSeq(fields[0]);
		dzSyncData.setOrderDate(handleOrderDate(fields[1]));
		dzSyncData.setCardNo(fields[2]);
		dzSyncData.setTransAmt(Long.parseLong(Ryt.mul100(fields[3])));
		/*dzSyncData.setSysDate(handleDate(fields[9]));*/
		handleSysDateAndTime(fields[9], dzSyncData);
		dzSyncData.setMerId(fields[10]);
		dzSyncData.setBid(fields[10]);
		dzSyncData.setTerminal(fields[11]);
		String oid = handleOid_dz(fields);
		dzSyncData.setOrdId(oid);
		dzSyncData.setOrgCode(fields[24]);
		dzSyncData.setGateId(fields[26]);
		dzSyncData.setTransState(PayState.SUCCESS);
//		dzSyncData.setBkFee(Integer.parseInt(Ryt.mul100(fields[25])));
		handleTransTypeAndAmt(fields[5],dzSyncData);//处理Pos交易类型
        if(30==fields.length){
            handleFeeForNewDzFile(dzSyncData, fields[28], fields[29]);
        }else{
            handleMerFee(dzSyncData,fields[6],fields[4],fields[25]);
        }
        String field9=fields[9];
        if(!Ryt.empty(field9))dzSyncData.setBkDate(Integer.parseInt(field9));
		data.getDatas().add(dzSyncData);
	}

	/****
	 * 解析数据 CC 差错数据
	 * @param rowNum
	 * @param rowData
	 * @throws Exception
	 */
	private void parseDataForCc(int rowNum, String rowData) throws Exception {

		if (Ryt.empty(rowData)) {
			data.getErrorDatas().add("row[" + rowNum + "] "+rowData+"[error]数据行不能为空！");
			data.setSuccess(false);
			return;
		}

		String fields[] = handleFields(rowData.split("\\|"));
		if (fields.length < 29) {
			data.getErrorDatas().add("row[" + rowNum + "]"+rowData+" [error]字段个数错误！");
			data.setSuccess(false);
			return;
		}
		PosDzSyncData dzSyncData = new PosDzSyncData();
		dzSyncData.setBkSeq(fields[0]);
		dzSyncData.setOrderDate(handleOrderDate(fields[1]));
		dzSyncData.setCardNo(fields[2]);
		dzSyncData.setTransAmt(Long.parseLong(Ryt.mul100(fields[3])));
		/*dzSyncData.setSysDate(handleDate(fields[11]));*/
		handleSysDateAndTime(fields[11], dzSyncData);
		dzSyncData.setMerId(fields[12]);
		dzSyncData.setBid(fields[12]);
		dzSyncData.setTerminal(fields[13]);
		String oid=handleOid_cc(fields);
		dzSyncData.setOrdId(oid);
//		dzSyncData.setOrgCode(fields[24]);
		dzSyncData.setGateId(fields[27]);
		handleTransTypeAndAmt(fields[5],dzSyncData);//处理Pos交易类型
		String ccFlag=fields[28];
		if("正常结算".equals(ccFlag)){
			dzSyncData.setTransState(PayState.SUCCESS);
		}else if("退款".equals(ccFlag)){
			dzSyncData.setTransState(PayState.WAIT_PAY);
		}else{
			data.getErrorDatas().add("row[" + rowNum + "]"+rowData+" [error]差错处理方式错误！["+ccFlag+"]");
			data.setSuccess(false);
			return;
		}
		handleMerFee(dzSyncData,fields[6],fields[4],fields[26]);
        String field11=fields[11];
        if(!Ryt.empty(field11))dzSyncData.setBkDate(Integer.parseInt(field11));
		data.getDatas().add(dzSyncData);
	}

	/***
	 * 处理日期格式
	 * Pos文件日期格式：20140726230019
	 * @param orgDate
	 * @return
	 * @throws Exception 
	 */
	private Integer handleOrderDate(String orgDate) throws Exception {
		if(Ryt.empty(orgDate)){
			throw new Exception("订单日期为空");
		}
		if(!isMatch("^\\d{14}$", orgDate)){
			throw new Exception("订单日期格式错误");
		}
		
		String newDate=orgDate.substring(0, 8);
		return Integer.parseInt(newDate);
	}
	
	/***
	 * Pos文件日期格式：20140726
	 * @param orgDate
	 * @param dzSyncData
	 * @throws Exception 
	 */
	private void handleSysDateAndTime(String orgDate,PosDzSyncData dzSyncData) throws Exception{
		if(Ryt.empty(orgDate)){
			throw new Exception("订单系统日期为空");
		}
		if(!isMatch("^\\d{8}$", orgDate)){
			throw new Exception("订单系统日期格式错误");
		}
		
		Integer sysDate=Integer.parseInt(orgDate);
		Date currentTime = new Date();
		dzSyncData.setSysDate(sysDate);
		dzSyncData.setSysTime(DateUtil.getCurrentUTCSeconds(currentTime));
	}

	
	/***
	 * 处理Pos 交易类型and 交易金额
	 * 无需判断交易类型  金额无需根据类型做出正负处理
	 * @param posType
	 * @param busiType
	 * @param dzSyncData
	 * @throws Exception 
	 */
	private void handleTransTypeAndAmt(String posType,PosDzSyncData dzSyncData) throws Exception{
		String transType="1";
		posType=posType.trim();
/*		long transAmt=dzSyncData.getTransAmt();
		if(posType.equals("消费")){
			transType="1";
		}else if(posType.equals("消费撤销")){
			transType="2";
//			transAmt=transAmt*-1;
		}else if(posType.equals("退货")){
			transType="4";
//			transAmt=transAmt*-1;
		}else if(posType.equals("预授权")){
			transType="6";
		}else if(posType.equals("预授权完成")){
			transType="7";
		}else if(posType.equals("冲正")){
			transType="3";
		}else{
			throw new Exception("数据类型异常，请检查商户[" + dzSyncData.getMerId() + "]的交易类型[" + posType + "]配置！");
		}*/
		dzSyncData.setTransType(transType);
	}
	
	/***
	 * 计算商户手续费
	 * @param dzSyncData
	 * @param transType 交易处理码  48000转账业务 ，其他
	 * @param syncMerFee 同步文件中的手续费  元单位
	 * @throws Exception
	 * 10：上海银行(对应ＲＹＦ中信) 11：银联CUPS（对应RYF的银联）    70001：上海银联POSP（对应北京银行）
	 */
	private void handleMerFee(PosDzSyncData dzSyncData,String transType,String syncMerFee,String syncBkFee) throws Exception {

		String mid = dzSyncData.getMerId();
		String gid = dzSyncData.getGateId();
		if(Ryt.empty(mid)){
			throw new Exception("商户号为空，请检查商户号["+mid+"]是否正确！");
		}
		if(Ryt.empty(gid)){
			throw new Exception("支付渠道为空，请检查商户[" + mid + "]的银行网关[" + gid + "]配置！");
		}
		String transAmt = String.valueOf(Ryt.div100(dzSyncData.getTransAmt()));
		boolean plus_minusFlag=false;
		if(transAmt.contains("-")){
			plus_minusFlag=true;
		}
		transAmt=transAmt.replace("-", "");
		syncMerFee=syncMerFee.replace("-", "");
		if(transType.equals(this.transType_zz)){
			long transAmtTemp=Ryt.mul100toInt(transAmt)-Ryt.mul100toInt(syncMerFee);
			transAmt=String.valueOf(Ryt.div100(transAmtTemp));
		}
		FeeCalcMode f=getBkFeeMode(mid, gid);
		String calcMode=f.getCalcMode();
		String bkFeeMode=f.getBkFeeMode();
		/**try{
			if(feeCalcModeMap.containsKey(mid+gid)){
				f=feeCalcModeMap.get(mid+gid);
			}else{
				f = getFeeModeByGate(mid, gid);
				feeCalcModeMap.put(mid+gid, f);
			}
			calcMode = f.getCalcMode();
			bkFeeMode=f.getBkFeeMode();
		}catch (Exception e) {
			throw new Exception(e.getMessage()+",请检查商户["+mid+"]的["+gid+"]银行网关是否配置！");			
		}**/
		Integer merFee=0;
		if(f==null || Ryt.empty(calcMode)){
			//商户手续费未配置的情况下  手续费为0
			LogUtil.printInfoLog("PosDzSyncProcess", "handleMerFee", "商户["+mid+"]的支付渠道商户手续费公式为空");
			
		}else{
			merFee = Integer.parseInt(Ryt.mul100(ChargeMode.reckon(
					calcMode, transAmt, "0")));
		}
		
		Integer bkFee=0;
		if(f==null || Ryt.empty(syncBkFee)){
			//商户手续费未配置的情况下  手续费为0
			LogUtil.printInfoLog("PosDzSyncProcess", "handleMerFee", "商户["+mid+"]的支付渠道银行手续费为空");
			
		}else{
			bkFee = Integer.parseInt(Ryt.mul100(syncBkFee));
		}
		if(plus_minusFlag){
			merFee=merFee*-1;
		}
		
		long payAmt = dzSyncData.getTransAmt();
		dzSyncData.setGid(gid);
		dzSyncData.setGateId(gid);
		dzSyncData.setPayAmt(payAmt);
		dzSyncData.setMerFee(merFee);
		dzSyncData.setBkFee(bkFee);
		dzSyncData.setBkFeeMode(bkFeeMode);
	}
	
	/***
	 * 去除空格
	 * @param fields
	 * @return
	 */
	private String[] handleFields(String[] fields){
		String[] newFs=new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			newFs[i]=fields[i].trim();
		}
		
		return newFs;
	}
	
	private boolean isMatch(String regex,String content){
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(content);
		return matcher.find();
	}
	
	
	private String getRYFGate(String posGate){
		String path=TestUtil.class.getResource("/").getPath().replace("%20", " ")+"posGateList.properties";
		String ryfGate=null;
		try {
			// 定义一个properties文件的名字
			// 定义一个properties对象
			Properties properties = new Properties();
			// 读取properties
			InputStream file = new FileInputStream(new File(path));
			// 加载properties文件
			properties.load(file);
			// 读取properties中的某一项
			 ryfGate =properties.getProperty(posGate);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return ryfGate;
	}
	
	/**
	 * 通过网关获取手续费
	 * @param mid
	 * @param gate
	 * @return
	 * @throws Exception 
	 */
	public FeeCalcMode getFeeModeByGate(String mid,String gate) throws Exception{
		StringBuffer sql=new StringBuffer("select calc_mode,gid,bk_fee_mode from fee_calc_mode where mid =");
		sql.append(Ryt.addQuotes(mid));
		sql.append(" and gate=");
		sql.append(gate);
		FeeCalcMode mode=dao.queryForObject(sql.toString(),FeeCalcMode.class);
		if(null==mode)
			throw new Exception("该网关尚未配置");
		return mode;
	}

	/****
	 * 订单号处理
	 * 订单号取值Pos原订单号+交易流水号（消费或转账类）
	 * 订单号取值Pos原订单号+C+交易流水号（消费或转账类）
	 * @param fields
	 * @return
	 * @throws Exception 
	 */
	private String handleOid_dz(String[] fields) throws Exception {
		String oid=fields[12];
		String seq=fields[0];
		if("".equals(oid)){
			oid+=seq;
		}
		String is_cz=fields[27];//冲正标识
		if(!Ryt.empty(is_cz) && czState.equals(is_cz)){
			oid+="C"+seq;
		}else{
			oid+=seq;
		}
		if(oid.length()>30){
			throw new Exception("订单号长度超过30位");
		}
		return oid;
	}
	
	private String handleOid_cc(String[] fields) {
		String oid=fields[14];
		if("".equals(oid)){
			oid+=fields[0];
		}
		return oid;
	}


    /**
     * handleMerFeeForNewDzFile->支持新的DZ文件：文件新增商户手续费，银行手续费
     * @param data
     * @param merFee
     * @param bkFee
     */
    private void handleFeeForNewDzFile(PosDzSyncData data,String merFee,String bkFee)throws Exception{
        long payamt=data.getTransAmt();
        String gid=data.getGateId();//对账文件中的渠道ID
        String mid=data.getMerId();
        if(!Ryt.empty(merFee))data.setMerFee(Integer.parseInt(Ryt.mul100(merFee)));
        if(!Ryt.empty(bkFee))data.setBkFee(Integer.parseInt(Ryt.mul100(bkFee)));
        FeeCalcMode f=getBkFeeMode(mid,gid);
        if(f!=null)data.setBkFeeMode(f.getBkFeeMode());
        data.setPayAmt(payamt);
        data.setGateId(gid);
        data.setGid(gid);
    }
	
	public FeeCalcMode getBkFeeMode(String mid,String gid) throws Exception{
        FeeCalcMode f=null;
        try{
			if(feeCalcModeMap.containsKey(mid+gid)){
				f=feeCalcModeMap.get(mid+gid);
			}else{
				f = getFeeModeByGate(mid, gid);
				feeCalcModeMap.put(mid+gid, f);
			}
		}catch (Exception e) {
			throw new Exception(e.getMessage()+",请检查商户["+mid+"]的["+gid+"]银行网关是否配置！");
		}
        return f;
    }
}
