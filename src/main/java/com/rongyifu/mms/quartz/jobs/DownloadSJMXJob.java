package com.rongyifu.mms.quartz.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rongyifu.mms.bank.b2e.SjBankMXCX;
import com.rongyifu.mms.bean.SettleResultBean;
import com.rongyifu.mms.bean.TrDetails;
import com.rongyifu.mms.modules.liqmanage.dao.TrDetailsDao;
import com.rongyifu.mms.quartz.jobs.utils.SJTransDetailParser;
import com.rongyifu.mms.service.DoSettlementService;
import com.rongyifu.mms.settlement.SBean;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 定时下载盛京明细
 */
public class DownloadSJMXJob implements Job {
	
	private TrDetailsDao dao = new TrDetailsDao();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			//获取昨天的 yyyyMMdd
			Calendar cld = Calendar.getInstance();
			cld.add(Calendar.DATE, -1);
			Date yesterday = cld.getTime();
			int yest = Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(yesterday));
			//删除重复数据
			int count = dao.deleteByDateAndGid(yest, 40006);
			LogUtil.printInfoLog(DownloadSJMXJob.class.getName(), "execute", "删除重复数据 "+count+" 条, date:"+yest+",gid:"+40006);
			
			//查询昨天的盛京交易明细
			List<String> lst = new SjBankMXCX(yest,yest).querySjBankDetail();
			//解析
			List<TrDetails> list = SJTransDetailParser.parse(lst);
			//存库
			if(CollectionUtils.isNotEmpty(list)){
				dao.saveTrDetails(list);
			}
			
			//自动对账
//			checkBankData("40006", list);
			
			LogUtil.printInfoLog(DownloadSJMXJob.class.getName(), "execute", "批量下载盛京明细成功，日期："+yest);
		} catch (Exception e) {
			LogUtil.printErrorLog(DownloadSJMXJob.class.getName(), "execute", e.getMessage(), e);
		}
	}
	
	private List<SBean> getCheckData(String bank, List<TrDetails> list) throws Exception {
		List<SBean> sbeanList = new ArrayList<SBean>();	
		for (TrDetails trd : list) {
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setBkSeq(trd.getBkSerialNo());//银行流水号
			bean.setAmt(trd.getAmt()/100.0+"");
			bean.setBkFee(trd.getFeeAmt()/100.0+"");//订单手续费
			bean.setDate(trd.getTrDate()+"");
			sbeanList.add(bean);
		}
		return sbeanList;
	}
	
	private void checkBankData(String bank, List<TrDetails> list) throws Exception {
		List<SBean> dataList = getCheckData(bank, list);
		SettleResultBean bean = new SettleResultBean();
		try {
			new DoSettlementService().checkBankData( dataList, bean);
			Map<String,String> params = new HashMap<String,String>();
			params.put("total", dataList.size()+"");
			params.put("suspect", CollectionUtils.isEmpty(bean.getSuspect())?"0":bean.getSuspect().size()+"");
			params.put("success", CollectionUtils.isEmpty(bean.getSuccess())?"0":bean.getSuccess().size()+"");
			params.put("fail", CollectionUtils.isEmpty(bean.getFail())?"0":bean.getFail().size()+"");
			params.put("exception", bean.getException()+"");
			params.put("finish", bean.getFinish()+"");
			params.put("total", dataList.size()+"");
			LogUtil.printInfoLog(this.getClass().getName(), "checkBankData", "自动对账", params);
		} catch (Exception e) {
			LogUtil.printErrorLog(this.getClass().getName(), "checkBankData", e.getMessage(), e);
		}
	}
}
