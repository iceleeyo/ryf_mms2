package mmsTest.modules.sysmanage;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.modules.sysmanage.service.BalanceQueryService;
import com.rongyifu.mms.utils.LogUtil;

public class Test_BalanceQueryService {

	private BalanceQueryService balance = new BalanceQueryService();
	private String gid=null;
	private Logger log=Logger.getLogger(Test_BalanceQueryService.class);

	@Before
	public void setUp() throws Exception {
		gid="40000";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryBalance() {
		// 创建EwpReqModule
		String[] respInfo;
		try {
//			respInfo = CallPayPocessor.queryBkAccountBalance(gid);
			respInfo=new String[]{"success","1.00","msg"};
			LogUtil.printInfoLog("BalanceQuserService", "queryBalance",
					"respinfo[0]:" + respInfo[0] + " respinfo[1]:"
							+ respInfo[1] + " respinfo[2]:" + respInfo[2]);
			if (respInfo[0].equals("success")) {
				String balance = respInfo[1];
				if (!balance.contains(".")) {
					balance = Ryt.div100(balance);
				}
//				return balance;
				log.info("testQueryBalance:"+balance);
			} else {
//				return respInfo[2];
				log.info("testQueryBalance:"+respInfo[2]);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("BalanceQueryService", "queryBalance", "gid:"
					+ gid + "   exception", e);
//			return "查询失败，系统异常";
			log.info("testQueryBalance:查询失败，系统异常");
		}
	}

}
