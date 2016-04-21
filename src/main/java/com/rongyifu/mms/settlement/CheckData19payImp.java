package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//高阳
public class CheckData19payImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {

//		2.00,7805,3231,Y,201104022,0.01,RMB,GWE11040113512138950,20110422,BC00010001
//		COUNT	1		SUMMONEY	0.01

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (String line : datas) {
			if (line.contains("COUNT")) break;
			String[] value = line.split(",");
			if ("Y".equalsIgnoreCase(value[3].trim())){
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[2]);
//				bean.setBkMdate(value[4]);
				bean.setDate(value[8]);
				bean.setAmt(value[5].replaceAll(",",""));
//				bean.setBkFee(null);
				res.add(bean);
			}
			
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
