package com.rongyifu.mms.modules.transaction.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.DfTransaction;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.transaction.dao.DfTransactionDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.AuthUtils;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.utils.XmlUtils;

public class DfService {
	private DfTransactionDao dao = new DfTransactionDao();
	private Logger logger = Logger.getLogger(getClass());
	private static final String[] DF_TSTAT= new String[]{"初始状态","银行处理中","成功","失败"};
	private static final String[] DATA_SOURCE= new String[]{"支付系统","账户系统","清算系统","资金托管系统","POS系統","新账户系统"};
	private static final String TRANS_TYPE = "E3";
	private static final String VERSION = "10";
	/**
	 * @description: 同步代付结果
	*/ 
	public Map<String,String> syncDfResult(List<String> ids){
		String url = ParamCache.getStrParamByName("DF_URL")+"ryf_df/trans_entry";
		String md5key = ParamCache.getStrParamByName("MD5_KEY");
		logger.info("mdTkey:"+md5key+", url:"+url);
		Map<String,String> resultMap = new HashMap<String,String>();
		for (String tseq : ids) {
			//version +tseq + transType +md5Key
			String chkValue = MD5.getMD5((VERSION+tseq+TRANS_TYPE+md5key).getBytes()).toUpperCase();
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("tseq", tseq);
			params.put("version", VERSION);
			params.put("transType", TRANS_TYPE);
			params.put("chkValue", chkValue);
			logger.info("tseq:"+tseq+", chkValue:"+chkValue);
			String respXml = Ryt.requestWithPost(params, url);
			LogUtil.printInfoLog("DfService", "syncDfResult", "response from ryf_df:"+respXml);
			if (StringUtils.isNotBlank(respXml)) {
				XmlUtils xmlUtils = new XmlUtils(respXml);
				String status = xmlUtils.getNodeTextByPath("//res/status/value").trim();
				if ("RYF_DF_000".equals(status)) {
					String transStatusFlag = xmlUtils.getNodeTextByPath("//res/transResult/transStatus").trim();
					int tstat = 1;
					if ("F".equals(transStatusFlag)) {
						tstat = 3;
					} else if ("S".equals(transStatusFlag)) {
						tstat = 2;
					}
					resultMap.put(tseq, "同步成功,订单状态:" + DF_TSTAT[tstat]);
				} else {
					resultMap.put(tseq, "同步失败");
				}
			}else{
				resultMap.put(tseq, "同步失败");
			}
		}
		return resultMap;
	}

	/**
	* @description: 代付结果通知商户
	请求地址：	[DF_URL]notice/notice_entry,DF_URL：从global_params表中获取
	请求参数：
		tseq	电银流水号	
		async	异步调用标识	值1，可不传，默认同步调用
		chkValue	数字签名	MD5(tseq + md5Key). toUpperCase
		其中md5Key从global_params表中获取
	返回信息：
		成功返回：ok
		失败返回：error
	*/ 
	public Map<String,String> notifyMerchant(List<String> ids){
		String url = ParamCache.getStrParamByName("DF_URL")+"notice/notice_entry";
		String mdTkey = ParamCache.getStrParamByName("MD5_KEY");
		logger.info("mdTkey:"+mdTkey+", url:"+url);
		Map<String,String> resultMap = new HashMap<String,String>();
		
		for (String tseq : ids) {
			//tseq +md5Key
			String chkValue = MD5.getMD5((tseq+mdTkey).getBytes()).toUpperCase();
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("tseq", tseq);
//			params.put("async", 1);
			params.put("chkValue", chkValue);
			logger.info("tseq:"+tseq+", chkValue:"+chkValue);
			
			String respStr = Ryt.requestWithPost(params, url);
			LogUtil.printInfoLog("DfService", "syncDfResult", "response from ryf_df:"+respStr);
			resultMap.put(tseq, "ok".equals(respStr)?"成功":"失败");
		}
		return resultMap;
	}
	
	public DfTransaction queryByTseq(String tseq) throws Exception{
		DfTransaction tr = dao.queryByTseq(tseq);
		//权限控制
//		boolean hasAuth = new MerchantService().hasButtonAuth(new int []{192})[0];  权限控制由192 改成105
        boolean hasAuth = new MerchantService().hasButtonAuth(new int []{105})[0];
		if(!hasAuth){
			doAuthControl(tr);
		}
		return tr;
	}

	/**
	* @description: 下载
	*/ 
	public CurrentPage<DfTransaction> queryForPage(DfTransaction dfTr,Integer pageNo) throws Exception{
		if (!validate(dfTr)) {
			return null;
		}
		CurrentPage<DfTransaction> page = dao.queryForPage(dfTr, pageNo);
		//权限控制
		boolean hasAuth = new MerchantService().hasButtonAuth(new int []{105})[0];//权限控制由192 改成105
		if(!hasAuth){
			doAuthControl(page.getPageItems().toArray(new DfTransaction[0]));
		}
		return page;
	}
	
	/**
	* @param type 0代付交易查询 1代付结果同步
	* @throws Exception
	*/ 
	public FileTransfer downloadXls(DfTransaction dfTr,Integer type) throws Exception{
		if (!validate(dfTr)) {
			return null;
		}
		List<String[]> data = new ArrayList<String[]>();
		String[] title = new String[]{"电银流水号","账户号","订单号","交易金额","交易状态","交易类型","交易银行","代付渠道","数据来源","系统时间","银行流水号","失败原因"};
		if(type == 1){
			title[9] = "收款人账号";
			title[10] = "收款人户名";
		}
		data.add(title);
		String fileName = "DF_TRANSACTION_"+DateUtil.format(new Date(), "yyyyMMddHHmmss")+".xlsx";
		
		CurrentPage<DfTransaction> page = dao.queryForPage(dfTr,-1);
		
		//权限控制
		boolean hasAuth = new MerchantService().hasButtonAuth(new int []{105})[0];//权限控制由192 改成105
		if(!hasAuth){
			doAuthControl(page.getPageItems().toArray(new DfTransaction[0]));
		}
		
		Map<Integer, String> gateMap =  RYFMapUtil.getGateMapByType3(new Integer[]{11,12});
		Map<Integer, String> routeMap =  RYFMapUtil.getDFGateRouteMap();
		
		List<DfTransaction> items = page.getPageItems();
		for (DfTransaction df : items) {
			if(type == 0){
				String[] row = new String[]{df.getTseq(),df.getAccountId(),df.getOrderId(),Ryt.div100(df.getTransAmt()),DF_TSTAT[df.getTstat()],df.getType()==11?"对私代付":"对公代付",gateMap.get(df.getGate()),routeMap.get(df.getGid()),getDataSource(df.getDataSource()),df.getSysDateStr(),df.getBkSeq(),StringUtils.isBlank(df.getErrorMsg())?df.getErrorCode():df.getErrorMsg()};
				data.add(row);
			}else{
				String[] row = new String[]{df.getTseq(),df.getAccountId(),df.getOrderId(),Ryt.div100(df.getTransAmt()),DF_TSTAT[df.getTstat()],df.getType()==11?"对私代付":"对公代付",gateMap.get(df.getGate()),routeMap.get(df.getGid()),getDataSource(df.getDataSource()),df.getAccNo(),df.getAccName(),StringUtils.isBlank(df.getErrorMsg())?df.getErrorCode():df.getErrorMsg()};
				data.add(row);
			}
		}
		try {
			return new DownloadFile().downloadXLSXFileBase(data, fileName, "代付交易明细");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private String getDataSource(Short code){
		String dataSource = "未知";
		if(null != code){
			try {
				dataSource = DATA_SOURCE[code-1];
			} catch (Exception e) {
			}
		}
		return dataSource;
	}
	
	private boolean validate(DfTransaction dfTr){
		if(dfTr == null){
			return false;
		}
		if(dfTr.getBdate() == null || dfTr.getEdate() == null || !DateUtil.validate(dfTr.getBdate()+"", "yyyyMMdd")|| !DateUtil.validate(dfTr.getEdate()+"", "yyyyMMdd")){
			return false;
		}
		return true;
	}

	private void doAuthControl(DfTransaction ... trs) throws Exception{
		Map<String,AuthUtils.Strategy<?>> targetAndStrategy = new HashMap<String,AuthUtils.Strategy<?>>();
		targetAndStrategy.put("accNo", new AuthUtils.Strategy<String>(){
			@Override
			public String handle(String str) throws Exception {
				int length = str.length();
				StringBuilder stars = new StringBuilder();
				stars.append(str.substring(0,6));
				for(int i = 0;i<length-10;i++){
					stars.append("*");
				}
				return stars.append(str.substring(length-4)).toString();
			}
		});
		for (DfTransaction tr : trs) {
			AuthUtils.handle(tr, DfTransaction.class, targetAndStrategy);
		}
	}
	
}
