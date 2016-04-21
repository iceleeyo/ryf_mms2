package com.rongyifu.mms.modules.Mertransaction.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.rongyifu.mms.service.http.ITransactionSynRYFService;
import com.rongyifu.mms.service.http.TradeLog;
import com.rongyifu.mms.utils.ApplicationContextHolder;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class QueryPosMerTodayService {
	
    private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public CurrentPage<TradeLog> queryPosMerToday(Integer pageNo,Integer pageSize,String mid,String oid,String innerTermId,Integer  type, Integer tstat,String terminalNumber,long begintrantAmt,long endAmount){
		if(StringUtils.isBlank(mid)||null == pageNo||null == pageSize||pageNo<=0||pageSize<=0){
			return null;
		}
		ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
		ITransactionSynRYFService service = (ITransactionSynRYFService) ctx.getBean("ServiceUrlProxy");
		Map<String,String> params = new HashMap<String,String>();
		params.put("pageNo", pageNo.toString());
		params.put("pageSize", pageSize.toString());
		params.put("mid", mid);
		params.put("oid", oid);
		params.put("innerTermId", innerTermId);
		params.put("type", String.valueOf(type));
		params.put("tstat", String.valueOf(tstat));
		params.put("terminalNumber", terminalNumber);
		params.put("begintrantAmt", String.valueOf(begintrantAmt));
		params.put("endAmount", String.valueOf(endAmount));
		params.put("tstat", tstat==null?null:tstat.toString());
		LogUtil.printInfoLog(getClass().getCanonicalName(), "queryPosMerToday", "调用POS接口查询POS当天交易数据", params);
		CurrentPage<TradeLog> page = null;
		try {
			//Integer pageNo,Integer size,Integer mid,String tseq, String bkseq, String oid, Integer tstat, String bankCardNo
			//page = service.queryMerToday(pageNo, pageSize, "999292981680001", StringUtils.isBlank(oid)?null:oid, innerTermId,type, tstat,terminalNumber,begintrantAmt,endAmount );
			page = service.queryMerToday(pageNo, pageSize, mid, StringUtils.isBlank(oid)?null:oid, innerTermId,type, tstat,terminalNumber,begintrantAmt,endAmount );
			
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return page;
	}
	
	public CurrentPage<TradeLog> queryMerHlogs(Integer pageNo,Integer pageSize,String mid,String oid,String innerTermId,Integer  type, Integer tstat,String terminalNumber,long begintrantAmt,long endAmount,Integer beginDate,Integer endDate){
		if(StringUtils.isBlank(mid)||null == pageNo||null == pageSize||pageNo<=0||pageSize<=0){
			return null;
		}
		ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
		ITransactionSynRYFService service = (ITransactionSynRYFService) ctx.getBean("ServiceUrlProxy");
		Map<String,String> params = new HashMap<String,String>();
		params.put("pageNo", pageNo.toString());
		params.put("pageSize", pageSize.toString());
		params.put("mid", mid);
		params.put("oid", oid);
		params.put("innerTermId", innerTermId);
		params.put("type", String.valueOf(type));
		params.put("tstat", String.valueOf(tstat));
		params.put("terminalNumber", terminalNumber);
		params.put("begintrantAmt", String.valueOf(begintrantAmt));
		params.put("endAmount", String.valueOf(endAmount));
		params.put("tstat", tstat==null?null:tstat.toString());
		LogUtil.printInfoLog(getClass().getCanonicalName(), "queryMerHlogs", "调用POS接口查询POS历史交易数据", params);
		CurrentPage<TradeLog> page = null;
		try {
			
			//page = service.queryMerHlogs(pageNo, pageSize, "999292948120058", oid, innerTermId, type, tstat, terminalNumber, begintrantAmt, begintrantAmt,beginDate,endDate);
			page = service.queryMerHlogs(pageNo, pageSize, mid, oid, innerTermId, type, tstat, terminalNumber, begintrantAmt, begintrantAmt,beginDate,endDate);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return page;
	}
	
}
