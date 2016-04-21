package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cert.CertUtil;

import com.bocom.api.b2b.BOCOMB2BMiddlemanClient;
import com.bocom.api.core.BOCOMOPReply;
import com.rongyifu.mms.bank.query.QueryCommon;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.dao.SystemDao;

public class CheckDataBocommB2BImp  implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
		return null;
	}
	
	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> map)
			throws Exception {
		
		BOCOMB2BMiddlemanClient B2BClient = new BOCOMB2BMiddlemanClient();
		boolean ret = B2BClient.initalize(CertUtil.getCertPath("JT_B2B_PATH"));
//		boolean ret = B2BClient.initalize("D:/certs/bocomm/B2BMerchant-new.xml");
		//B2BClient.getElement("")
		if	(!ret)	{ 
			String errmsg = B2BClient.getLastErr();
			throw new Exception(errmsg);
		}
		Map<String,String> gateRoutMap=new SystemDao().getMerNoByGid(bank);
		map.putAll(gateRoutMap);
		List<SBean> sbeanList=new ArrayList<SBean>();
		String beginDateStr = map.get("beginDate");
		String endDateStr = map.get("endDate");
		String merchantNo = map.get("mer_no");
		List<Hlog> orderList = QueryCommon.getDao().queryOrderInfoFromHlog(bank, beginDateStr, endDateStr);
	
		BOCOMOPReply replyResult = null;
		int index = 0;
		StringBuilder merchantOrderNo = new StringBuilder();
		for (Hlog hlog : orderList) {
			index++;
			merchantOrderNo.append("|").append(hlog.getTseq());
			if (index % 20 != 0 && index != orderList.size()) {
				continue;
			}
			replyResult = B2BClient
					.queryOrderDetail(merchantNo, merchantOrderNo.substring(1));
			getDataFromResult(sbeanList, bank, replyResult);
			merchantOrderNo = new StringBuilder();
		}
		return sbeanList;
	}
	/**
	 * 封装成List<SBean>
	 * @param result
	 * @return
	 * @throws Exception 
	 */
	private void getDataFromResult(List<SBean> sbeanList,String bank,BOCOMOPReply result) throws Exception{
		String code = result.getRetCode();
		String msg  = result.getErrorMessage();
	    if (!"0000".equals(code))throw new Exception(code+":"+msg);
		int num = result.getOpResultSize();
			//String total_num  = result.getResponseHead("total_num");
		for (int index = 0 ; index<num ; index++){
				String order_amount = result.getValueByName(index, "order_amount"); //订单金额
				String esatab_date = result.getValueByName(index, "establish_date"); //订单生成日期
				String mer_order_no = result.getValueByName(index, "merchant_order_no");//商户检索号
				
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(mer_order_no);
				bean.setAmt(order_amount);
				bean.setDate(esatab_date);
				sbeanList.add(bean);
		}
	}
}
