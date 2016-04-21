package mmsTest.modules.accmanage.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.io.FileTransfer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.modules.accmanage.service.SgDfSqService;
import com.rongyifu.mms.utils.CurrentPage;

/****
 * junit test SgDfSqService 
 * @author wang.bin
 *
 */
public class Test_SgDfSqService {
	
	private SgDfSqService dfSqService=null;
	private String uid=null;
	private String tseq=null;
	private String batchNo=null;
	private Integer ptype=null;
	private Integer mstate=null;
	private Integer bdate=null;
	private Integer edate=null;
	

	@Before
	public void setUp() throws Exception {
		dfSqService=new SgDfSqService();
		uid="1";
		tseq="000000001";
		batchNo="00000001";
		ptype=11;
		mstate=0;
		bdate=20141111;
		edate=20141111;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryDataForReq() {
		Integer pageNo=1;
		Integer pageSize=15;
		CurrentPage<OrderInfo> currentPage=dfSqService.queryDataForReq(pageNo, pageSize, uid, batchNo, ptype, tseq, mstate, bdate, edate);
		Assert.assertEquals("success", 0, currentPage.getPageTotle());
	}

	@Test
	public void testSgdf() {
		List<OrderInfo> os=new ArrayList<OrderInfo>();
		OrderInfo o=new OrderInfo();
		o.setTseq("141104132256154950");
		o.setOid("141104132255154949"); 
		o.setMid("764");
		o.setAgainPay_status(2);
		o.setMdate(20141104);
		os.add(o);
		OrderInfo o2=new OrderInfo();
		o2.setTseq("1411041322154950");
		o2.setOid("141104132255154949"); 
		o2.setMid("764");
		o2.setAgainPay_status(2);
		o2.setMdate(20141104);
		os.add(o2);
		boolean result=dfSqService.sgdf(os);
		assertTrue(result);
	}

	@Test
	public void testdownSGDFData() {
		FileTransfer transfer=null;
		try {
			transfer=dfSqService.downSGDFData(uid, batchNo, ptype, tseq, mstate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("success", transfer);
	}

}
