package mmsTest.modules.accmanage.service;

import org.directwebremoting.io.FileTransfer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.modules.accmanage.service.SgDfQueryService;
import com.rongyifu.mms.utils.CurrentPage;

/***
 * junit test SgDfQueryService 
 * @author wang.bin
 *
 */
public class Test_SgDfQueryService {
	
	private SgDfQueryService queryService=null;
	private String mid=null;
	private String tseq=null;
	private String batchNo=null;
	private String ptype=null;
	private Integer mstate=null;
	private Integer againPayStatus=null;
	private Integer bdate=null;
	private Integer edate=null;
	
	@Before
	public void setUp() throws Exception {
		queryService=new SgDfQueryService();
		mid="1";
		tseq="00000000001";
		batchNo="0000000001";
		ptype="11";
		mstate=0;
		againPayStatus=2;
		bdate=20141111;
		edate=20141111;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryTlogInfo() {
		Integer pageNo=1;
		Integer pageSize=15;
		CurrentPage<OrderInfo> currentPage=queryService.queryTlogInfo(pageNo, pageSize, mid, tseq, batchNo, ptype, mstate, againPayStatus, bdate, edate);
		Assert.assertEquals("success", 0, currentPage.getPageTotle());
	}

	@Test
	public void testDownSGDFData() {
		FileTransfer transfer=queryService.downSGDFData(mid, tseq, batchNo, ptype, mstate, againPayStatus, bdate, edate);
		Assert.assertNotNull("success", transfer);
	}

}
