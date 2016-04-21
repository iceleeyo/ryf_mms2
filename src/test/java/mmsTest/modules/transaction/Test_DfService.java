package mmsTest.modules.transaction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.junit.Test;

import com.rongyifu.mms.bean.DfTransaction;
import com.rongyifu.mms.modules.transaction.service.DfService;
import com.rongyifu.mms.service.LoginService;

public class Test_DfService {
	
	private DfService svc = new DfService();
	private final Logger logger = Logger.getLogger(getClass());
	
	@Test
	public void testSyncDfResult(){
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		svc.syncDfResult(ids);
	}
	
	@Test
	public void testNotifyMerchant(){
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		svc.notifyMerchant(ids);
	}
	
//	@Test
//	public void testQueryByTseq() throws Exception{
//		svc.queryByTseq("1");
//	}
//	
//	@Test
//	public void testQueryForPage() throws Exception{
//		DfTransaction dfTr = new DfTransaction();
//		dfTr.setBdate(20150101);
//		dfTr.setEdate(20150131);
//		svc.queryForPage(dfTr, 1);
//	}
//	
//	@Test
//	public void testDownloadXls() throws Exception{
////		LoginService lsvc = new LoginService();
////		lsvc.adminLogin(130, "lzx.00", role, ckpwd)
//		DfTransaction dfTr = new DfTransaction();
//		dfTr.setBdate(20150101);
//		dfTr.setEdate(20150131);
//		svc.downloadXls(dfTr, 1);
//	}
	
}
