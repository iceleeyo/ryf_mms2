package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataBOC_B2BImp implements SettltData {
	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

		int lineCount = 0;
		for (String line : datas) {
			
			if(line.trim().length()==0){
				break;
			}
			if(lineCount>0){
				
				String[] value = line.split("\\|");
//				商户订单号|网关交易流水号|CSPA交易流水号|交易类型|币种|金额|付款账户名称|付款账号
//				|付款省行联行号|付款账户开户行机构号|收款账户名称|收款账号|收款省行联行号|收款账户开户行机构号
//				|收款行支付行号|收款行名称|交易状态|提交时间|处理时间	
				
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[0]);
				bean.setAmt(value[5].replaceAll(",",""));
//				System.out.println(value[5].replaceAll(",",""));
				bean.setDate(value[18].substring(0,8));
//				bean.setBkFee(null);
				if("Y".equals(value[16]))
				res.add(bean);
			}
			lineCount=lineCount+1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}
}
