package mmsTest.modules.accmanage.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.io.FileTransfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.modules.accmanage.dao.SgDfShDao;
import com.rongyifu.mms.modules.accmanage.service.SgDfShService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

/****
 * junit test SgDgShService
 * @author wang.bin
 *
 */

public class Test_SgDfShService {
	private SgDfShService dfShService=null;
	private String uid=null;
	private String tseq=null;
	private String batchNo=null;
	private Integer ptype=null;
	private Integer mstate=null;
	private Integer bdate=null;
	private Integer edate=null;
	private List<OrderInfo> os=null;
	@Before
	public void setUp() throws Exception {
		dfShService=new SgDfShService();
		uid="1";
		tseq="000000001";
		batchNo="00000001";
		ptype=11;
		mstate=0;
		bdate=20141111;
		edate=20141111;
		os=initOs();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryDataForReq() {
		Integer pageNo=1;
		Integer pageSize=15;
		CurrentPage<OrderInfo> currentPage=dfShService.queryDataForReq(pageNo, pageSize, uid, batchNo, ptype, tseq, mstate, bdate, edate);
		assertEquals("success", 0, currentPage.getPageTotle());
	}

	@Test
	public void testReqPayDf() {
		List<TlogBean> normalDfs=batchSh(os);
		assertTrue(normalDfs.size()==0);
		boolean actuals=doReq(normalDfs);
		assertTrue(actuals);
		
	}

	@Test
	public void testReqRevocation() {
		String option="junit test sgdfshService";
		String actuals=dfShService.reqRevocation(os, option);
		if(os.size()==0){
			assertEquals("撤销异常", actuals);
		}else{
			assertEquals("success", "撤销发起成功", actuals);
		}
		
	}

	@Test
	public void testDownSGDFData() {
		FileTransfer transfer=null;
		try {
			transfer=dfShService.downSGDFData(uid, batchNo, ptype, tseq, mstate);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			assertNotNull(transfer);
		}
	}
	
	private List<OrderInfo> initOs(){
		Integer pageNo=1;
		Integer pageSize=15;
		CurrentPage<OrderInfo> currentPage= dfShService.queryDataForReq(pageNo, pageSize, uid, batchNo, ptype, tseq, mstate, bdate, edate);
		return currentPage.getPageItems();
	}
	
	
	private List<TlogBean> batchSh(List<OrderInfo> lists){
		List<TlogBean> normalDfs=new ArrayList<TlogBean>();//常规代付
		for (OrderInfo orderInfo : lists) {
			//修改订单状态为处理中 
			TlogBean tlog=(TlogBean)dfShService.convertToTlogBean(orderInfo);
			normalDfs.add(tlog);
		}
		
		try {
			if(normalDfs.size()!=0){
				new SgDfShDao().batchSh(normalDfs);//执行审核操作的数据操作
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return normalDfs;
	}
	
	private boolean doReq(List<TlogBean> normalDfs){
		StringBuffer normalMsg=new StringBuffer();
		StringBuffer exceptionMsg=new StringBuffer();
		String resp="手工审核成功\n";
		try{
			//MMs重发代付、 因订单重新生产
			if(normalDfs.size()!=0){
				for (TlogBean tlog : normalDfs) {
					String respFlag="suc";
					if(!"suc".equals(respFlag)){
						tlog.setTstat(PayState.FAILURE);
						tlog.setError_msg("请求银行失败");
						tlog.setAgainPay_status(Constant.SgDfTstat.TSTAT_SHFAIL);
						new SgDfShDao().updateTstat(tlog);
						exceptionMsg.append(tlog.getOid()).append(",");
					}else{
						normalMsg.append(tlog.getOid()).append(",");
					}
				}
			}
		}catch (Exception e) {
			LogUtil.printErrorLog("SgDfShService", "reqPayDf", "reqPayDf_exception", e);
		}
		
		if(!Ryt.empty(normalMsg.toString())){
			resp+="提交成功:"+normalMsg.substring(0, normalMsg.length()-1)+"\n";
		}
		if(!Ryt.empty(exceptionMsg.toString())){
			resp+="请求银行失败:"+exceptionMsg.substring(0, exceptionMsg.length()-1)+"\n";
		}
		LogUtil.printInfoLog("SgDfShServiceTest", "doReq", "resp:"+resp);
		
		return true;
	}
}
