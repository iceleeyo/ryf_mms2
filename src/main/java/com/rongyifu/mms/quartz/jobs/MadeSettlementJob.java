package com.rongyifu.mms.quartz.jobs;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rongyifu.mms.modules.transaction.service.QueryTradeStatisticService;
import com.rongyifu.mms.service.MerSettlementService;
import com.rongyifu.mms.utils.DateUtil;

/**
 *定时生成对账单上传到ftp定时任务
 */
public class MadeSettlementJob implements Job {
	private Logger logger = Logger.getLogger(getClass());
	
	MerSettlementService service = new MerSettlementService();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Calendar cld = Calendar.getInstance();
		cld.add(Calendar.DATE, -1);
				try {
					service.madeSettlement(Integer.valueOf(DateUtil.format(cld.getTime())));
					logger.info("上传ftp完成。");

				} catch (Exception e) {
						logger.error("自动上传对账单失败，请使用手工方式 ");
					
				}
			}
}
