package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 农业银行B2B
 * 
 * @author lyj
 * 
 */
public class CheckDataABC_B2BImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

		for (String line : datas) {
			String[] value = line.split("\\|");

			if (value.length == 15) {
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[1]);
				bean.setDate(value[14].substring(0, 10).replaceAll("-", ""));
				bean.setAmt(value[4]);
				// 得出 setGate，setTseq，setDate，setAmt是必须的四项
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
