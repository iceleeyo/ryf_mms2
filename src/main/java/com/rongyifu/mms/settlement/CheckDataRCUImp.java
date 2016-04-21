package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataRCUImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		// 交易代码（1-支付 2-退货）|清算日期|商户提交时间|商户流水号（订单号）|交易流水号 |商户号|终端号|交易金额
		// |币种|交易手续费|结算金额|备注1|备注2
		// 第一行： 8
		// 第二行：
		// ZF01|20110611|20111101182025|201111011819|10122527|01020905001000000001||0.11|0|0.11|0000||
		// ZF01|20110611|20111101182025|201111011819||01020905001000000001||0.11|0|0.11|0000||
		// ZF02|20110611|20111102100544|1042000000000201004080000075579|201111011819||01020905001000000001||0.11|0|0.11|0000||
		int lineCount = 0;
		for (String line : datas) {
			String[] value = line.split("\\|");
			if (lineCount > 0 && value.length >= 10) {
				if("ZF02".equals(value[0]))
					continue;
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setMerOid(value[3]);
				bean.setDate(value[2].substring(0, 8));
				bean.setAmt(value[7].replaceAll(",", ""));
				res.add(bean);
			}
			lineCount = lineCount + 1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
