package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataBOSImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		// 订单号|卡标志|币种|交易金额|交易类型|交易状态|交易日期|交易时间|清算日期|清算结果|备注
		// 20130222143430837|信用卡|人民币|0.01|支付|成功|2013-02-22|14:35:24|2013-02-22|已清算|
		// 20130222112214747|信用卡|人民币|0.01|支付|成功|2013-02-22|11:23:24|2013-02-22|已清算|
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 1; i < datas.length; i++) {
			String[] values = datas[i].split("\\|");
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setTseq(values[0]);
			bean.setDate(values[6].replaceAll("-", ""));
			bean.setAmt(values[3].replaceAll(",", ""));
			res.add(bean);
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
