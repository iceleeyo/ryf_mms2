package mmsTest.modules.transaction;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.rongyifu.mms.modules.transaction.service.QueryTradeStatisticService;

public class Test_QueryTradeStatisticService {
	private QueryTradeStatisticService svc = new QueryTradeStatisticService();
	private Logger logger = Logger.getLogger(getClass());
	
	@Test
	public void testDoStatistics(){
		boolean flag = svc.doStatistics(20150210, false);
		logger.info("doStatistics result:"+flag);
	}
	
	@Test
	public void doExportTransExcel(){
		svc.exportTransExcel(20150101, 20150131, QueryTradeStatisticService.JYFB);
	}
	
	@Test
	public void testQueryTradeStatistics(){
		svc.queryTradeStatistics(20150101, 20150131, QueryTradeStatisticService.JYFB, 1);
	}
}
