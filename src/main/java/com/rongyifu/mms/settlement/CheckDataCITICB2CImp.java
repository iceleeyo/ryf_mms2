package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 /**
  * 中信B2C
  * @author yang.yaofengs
  *
  */
public class CheckDataCITICB2CImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		第三方支付对账文件-充值对账文件
//		第三方支付方：快钱测试有限公司下属快钱北京东城区雍和宫 清算日期:2009-07-20 制表日期:2009-07-21
//		交易总笔数：1 交易总金额:12.0
//		=================================================
//		订单号|订单日期|交易日期-时间|支付流水号|卡类型|结算金额|
//		-----------------------------------------------
//		20090720100000321491|2009-07-20|2009-07-20-14:40:58|TD090720144058000314|借记卡|12.00|
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int count=0;
		for (int i = 0; i < datas.length; i++) {
			count++;
			if(count<=6)
			continue;
			String[] values = datas[i].split("\\|");
			if(values.length==7){
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setTseq(values[0]);
			bean.setDate(values[1].replaceAll("-", ""));
			bean.setAmt(values[5].replaceAll(",", ""));
			res.add(bean);
			}
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
