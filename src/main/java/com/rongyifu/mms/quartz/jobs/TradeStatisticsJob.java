package com.rongyifu.mms.quartz.jobs;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rongyifu.mms.modules.transaction.service.QueryTradeStatisticService;
import com.rongyifu.mms.utils.DateUtil;

/**
 *定时生成tlog统计信息定时任务
 */
public class TradeStatisticsJob implements Job {
	private Logger logger = Logger.getLogger(getClass());
	
	QueryTradeStatisticService service = new QueryTradeStatisticService();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Calendar cld = Calendar.getInstance();
		cld.add(Calendar.DATE, -1);
		int count = 0;
		while (true) {
				count++;
				try {
					service.doStatistics(Integer.valueOf(DateUtil.format(cld.getTime())), false);
					logger.info("统计订单信息成功。");
					break;
				} catch (Exception e) {
					if (count<5) {
						logger.error("统计订单信息失败，准备重试第 " + count + " 次重试。");
					}else{
						logger.error("超过最大重试次数，统计订单信息失败。");
						break;
				}
			}
		}
	}

}
