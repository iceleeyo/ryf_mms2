package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交通银行网银
 * @author m
 *
 */
public class CheckDataBOCOMImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			lineCount++;
			if (lineCount > 10) {
				if (line.contains("==============")) break;
				line = line.replaceAll("\\s{2,}", " ");
				String[] value = line.split(" ");
				// 订单号       订单日期                 交易日期-时间                               支付流水号                         卡类型           交易金额            商户手续费 实际结算金额 商户批次号
				// 54779 20110503  20110503-215006  55293832                  借记卡           1,100.00 5.50 1,094.50 -
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[1]);
//				bean.setBkMdate(value[2]);
				bean.setDate(value[3].split("-")[0]);
				bean.setAmt(value[6].replaceAll(",",""));
				bean.setBkFee(value[7].replaceAll(",",""));//商户手续费 
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
