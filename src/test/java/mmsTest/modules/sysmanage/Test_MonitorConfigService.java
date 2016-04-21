package mmsTest.modules.sysmanage;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rongyifu.mms.bean.MonitorConfig;
import com.rongyifu.mms.modules.sysmanage.service.MonitorConfigService;
import com.rongyifu.mms.utils.CurrentPage;

/**
 * MonitorConfigService 测试用例
 */
public class Test_MonitorConfigService {
	private MonitorConfigService mcs = new MonitorConfigService();
	private final Log logger = LogFactory.getLog(getClass());

	@Test
	public void testDoPost() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("type", "0");
		params.put("targetId", "1");
		params.put("monitorType", "0");
		String rslt = mcs.doMonitor(params);
		logger.info(rslt);
	}
	

	@Test
	public void testAddConfig() {
		MonitorConfig mcfg = new MonitorConfig();
		String msg = mcs.addConfig(mcfg);
		logger.info(msg);
	}

	@Test
	public void testQueryConfigPage() {
		CurrentPage<MonitorConfig> page = mcs.queryConfigPage(1);
		assertNotNull(page);
		logger.info("currentPage:"+page.getPageNumber()+",totalPage:"+page.getPageTotle());
	}

	@Test
	public void testModifyConfig() {
		MonitorConfig mc = new MonitorConfig();
		String msg = mcs.modifyConfig(mc);
		logger.info(msg);
	}

	@Test
	public void testGetConfigById() {
		MonitorConfig mc = mcs.getConfigById(1);
		logger.info(mc);
	}

	@Test
	public void testIsMerNoExists() {
		boolean flag = mcs.isMerNoExists("764");
		logger.info(flag);
	}

}
