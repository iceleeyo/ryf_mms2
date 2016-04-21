package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 中国银行
 * @author m
 *
 */
public class CheckDataBOCImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		//第一行：消费笔数|退货笔数|总计笔数|消费总金额|退货总金额|金额合计|
		//第二行：商户号|订单号|协议号|终端号|交易日期时间|交易币种|交易金额|卡别|交易码|保留域|
//		31|4|35|4566.48|1356.97|3209.51|
//		104330153000054|6429765|1155122140|33015689|20150407000000|001|514.00|BOCD|REFP|0000000000000000IP00|
//		104330153000054|6429765|1155122140|33015689|20150407000000|001|514.00|BOCD|REFP|0000000000000000IP00|
//		104330153000054|6793882|1154890079|33015683|20150512055959|001|104.00|BOCQ|PCEP|5131551845840000IP00|
//		104330153000054|6794166|1154914153|33015677|20150512080657|001|99.00|BOCD|PCEP|5132766932110000IP00|
		for (String line : datas) {
			// 过滤非交易数据：PCEP 交易；REFP 退款
			if (line.indexOf("PCEP") == -1)
				continue;

			String[] value = line.split("\\|");
			
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setTseq(value[1]);
			// bean.setBkMdate(value[4].substring(0,8));
			bean.setDate(value[4].substring(0, 8));
			bean.setAmt(value[6].replaceAll(",", ""));
			// bean.setBkFee(null);
			res.add(bean);
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
