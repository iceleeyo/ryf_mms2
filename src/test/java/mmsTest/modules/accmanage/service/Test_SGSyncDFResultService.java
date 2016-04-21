package mmsTest.modules.accmanage.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.modules.accmanage.service.SGSyncDFResultService;

public class Test_SGSyncDFResultService {
	private static Logger log=Logger.getLogger(Test_SGSyncDFResultService.class);
    SGSyncDFResultService thisObject=null;
    List<OrderInfo> lists=null;
    Integer pagNo=1;
    Integer num=15;
    String uid ="777";
    String trans_flow ="777";
    Integer ptype=11;
    String tseq="777777";
    Integer mstate=1;
    Integer state=3;
    Integer gate=71001;
    Integer bdate=20150301;
    Integer edate=20150301;
	
	
	@Before
	public void setUp() throws Exception {
		thisObject=new SGSyncDFResultService();
		lists=new ArrayList<OrderInfo>();
		OrderInfo o=new OrderInfo();
		o.setTseq("77777777777");
		lists.add(o);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryDataForSGSYNC() {
		thisObject.queryDataForSGSYNC(pagNo, num, uid, trans_flow, ptype, tseq, mstate, state, gate, bdate, edate);
	}

	@Test
	public void testReqQuery_Bank() {
		StringBuffer msgSuc=new StringBuffer("代付流水：");
		StringBuffer msgFail=new StringBuffer("代付流水：");
		StringBuffer msgException=new StringBuffer("代付流水：");
		StringBuffer msgNormal=new StringBuffer("代付流水：");
		boolean isSuc=false;boolean isFail=false; boolean isException=false;boolean isNormal=false;
		for (OrderInfo orderInfo : lists) {
				String tseq=orderInfo.getTseq();
//				String[] res=daifuResultUtil.reqSGSyncRes(orderInfo);
				String[] res=new String[]{"成功交易","success"};
				String result=res[1];
				if(result.equals("success")){
					msgSuc.append(tseq).append("/");
					isSuc=true;
				}else if(result.equals("fail")){
					msgFail.append(tseq).append("/");
					isFail=true;
				}else if(result.equals("req_fail")){
					msgException.append(tseq).append("/");
					isException=true;
				}else if(result.equals("wait_pay")){  //未查询到结果
						msgNormal.append(tseq).append("/");
						isNormal=true;
				}
			
		}
		StringBuffer endRes=new StringBuffer();
		if(isSuc){
			msgSuc.replace(msgSuc.length()-1, msgSuc.length(), "");
			msgSuc.append(" 同步成功, 交易结果为成功");
			endRes.append(msgSuc).append("\r\n");
		}
		if(isFail){
			msgFail.replace(msgFail.length()-1, msgFail.length(), "");
			msgFail.append(" 同步成功，交易结果为失败");
			endRes.append(msgFail).append("\r\n");
		}
		if(isException){
			msgException.replace(msgException.length()-1, msgException.length(), "");
			msgException.append(" 同步异常");
			endRes.append(msgException).append("\r\n");
		}
		if(isNormal){//未查询到结果
			msgNormal.replace(msgNormal.length()-1, msgNormal.length(), "");
			msgNormal.append("未获取到最终结果，请稍后发起继续结果同步");
			endRes.append(msgNormal).append("\r\n");
		}
		log.info("testReqQuery_Bank:"+endRes.toString());
	}

	@Test
	@Ignore
	public void testReqNotice_Mer() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testModifyIsNotice() {
		fail("Not yet implemented");
	}


}
