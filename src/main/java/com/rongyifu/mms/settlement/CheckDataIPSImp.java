package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//环讯
public class CheckDataIPSImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {

		// 商户号012677
		// 总笔数1
		// 说明:订单金额=实际金额 单位:元
		// 总金额0.02
		// 总手续费0.01
		//
		//
		//
		// 文本生成时间:2011-05-04 15:45:04
		// 商户订单号,IPS订单号,银行订单号,币种,订单金额,手续费,余额,支付状态,商户提交日期,银行返回时间,交易类型,支付银行,商户参考信息
		// [3233],NT2011042263225553,3012484639,RMB,0.02,0.01,17869.23,成功,2011-04-22,2011-04-22,消费,招商银行,abcd

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			String[] tStr = line.split(",");
			++lineCount;
			if (lineCount >= 11 && tStr.length >= 5) {
				// 过滤掉退款订单 （手续费为零）
				if (Float.parseFloat(tStr[5]) > 0) {
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(tStr[0].substring(tStr[0].indexOf("[") + 1,tStr[0].indexOf("]")).trim());
//					bean.setBkMdate(tStr[8].replaceAll("-", "").trim());
					bean.setDate(tStr[9].replaceAll("-", "").trim());
					bean.setAmt(tStr[4].replaceAll(",",""));
					bean.setBkFee(tStr[5].replaceAll(",",""));//手续费
//					bean.setBkFee(null);
					res.add(bean);
				}
			}
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
