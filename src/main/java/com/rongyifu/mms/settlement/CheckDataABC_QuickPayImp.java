package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 农行贷记卡
 * 
 * @author meng.wanying
 * 
 */
public class CheckDataABC_QuickPayImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		103290070110010|20130804|20130804|6211111111111111   |320.00|43138004|000001001948|140000010276|贷记卡消费|
//		103290070110010|20130804|20130804|6211111111111111   |1000.00|43138004|000001001961|140000010414|贷记卡消费|
//		103290070110010|20130804|20130804|6211111111111111   |344.00|43138004|000001001968|140000010516|贷记卡消费|
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

		for (String line : datas) {
			String[] value = line.split("\\|");

			if (value.length == 10) {
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setDate(value[2]);
				bean.setAmt(value[4]);
				bean.setBkSeq(value[7]);
				bean.setFlag(5);
				res.add(bean);
			}
		}
		return res;

	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
