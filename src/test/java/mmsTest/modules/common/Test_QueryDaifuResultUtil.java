package mmsTest.modules.common;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.modules.common.QueryDaifuResultUtil;
import com.rongyifu.mms.modules.liqmanage.dao.QuerytransferDao;
import com.rongyifu.mms.utils.DateUtil;

public class Test_QueryDaifuResultUtil {
	private QueryDaifuResultUtil queryDaifuResultUtil=null;
	private QuerytransferDao dao=null;
	private OrderInfo o=null;
	private static Logger log=Logger.getLogger(Test_QueryDaifuResultUtil.class);
	
	@Before
	public void setUp() throws Exception {
		dao=new QuerytransferDao();
		o=new OrderInfo();
		o.setSysDate(DateUtil.today());
		o.setOid("9292029001");
		o.setTseq("920291921");
		o.setMid("764");
		queryDaifuResultUtil=new QueryDaifuResultUtil(dao);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReqSGSyncRes() {
		TlogBean order=new TlogBean();
		order.setTseq(Long.parseLong(o.getTseq()));
		order.setOid(o.getOid());
		order.setMid(o.getMid());
		order.setMdate(o.getMdate());
//		String[] result=CallPayPocessor.queryDfResult(order);
		String [] result=new String[]{"success","1.00"};
		log.info("className:Test_QueryDaifuResultUtil       "+"method:testReqSGSyncRes    result[0]:"+result[0]+"       result[1]:"+result[1]);
		
	}

}
