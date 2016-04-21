package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//汇付
public class CheckDataPNRImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
//		881223   融易通页面版 20131022    0000003628981 P      9460.00 S N S T1 20131022 679710 000600                                         ryt 
//		881223   融易通页面版 20131021 0020131021117418 R       545.00 S N I Th 20131022 719885 092633                                         ryt 
//		881223   融易通页面版 20131021 0020131021117394 R      3440.00 S N I Th 20131022 719999 092649                                         ryt 
//		881223   融易通页面版 20131021 0020131021117442 R       540.00 S N I Th 20131022 718565 092332                                         ryt 
//		881223   融易通页面版 20131021 0020131021117404 R      1072.00 S N I Th 20131022 719966 092644                                         ryt 
//		881223   融易通页面版 20131021 0020131021117389 R      1471.00 S N I Th 20131022 720681 092819                                         ryt 
//		881223   融易通页面版 20131021 0020131021117422 R      1180.00 S N I Th 20131022 719561 092549                                         ryt 
   
		String[] datas = fileContent.split("\n");
		//int lineCount = 0;
		for (String line : datas) {
			//lineCount++;
			//if (lineCount > 10) {
				line = line.replaceAll("\\s{2,}", " ");
				String[] value = line.split(" ");
				if ("P".equals(value[4].trim().toUpperCase())) {
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(orderIdConverter(value[3]));
//					bean.setBkMdate(value[2]);
					bean.setDate(value[10]);
					bean.setAmt(value[5].replaceAll(",",""));
//					bean.setBkFee(null);
					res.add(bean);
				}
			//}
		}
		return res;
	}

	// 订单号转换
	public static String orderIdConverter(String orderId) {
		if (orderId == null) return "";
		if (orderId.trim().indexOf("000000") == 0) {
			return orderId.substring(6, orderId.length());
		} else {
			return orderId.length() < 6 ? ("000000" + orderId) : orderId;
		}
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
