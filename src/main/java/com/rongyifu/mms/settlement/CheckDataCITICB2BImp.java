package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 /**
  * 中信B2B
  * @author meng.wanying
  *
  */
public class CheckDataCITICB2BImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {		
//		商户编号:10000068
//		结算账户:7342610182400003610
//		清算日期:2011-03-01
//		支付总笔数:3
//		支付总金额:112.01
//		退款总笔数:10
//		退款总金额:1.00
//		======================================================
//		交易时间|交易类型|交易流水号|对方账号|对方账户名称|发生额|订单号
//		------------------------------------------------------
//		20110301135030|ZF|00100000000000252858|7341010182600033841|CNCB10212947|0.01|1234567890
//		20110301135349|ZF|00100000000000252859|7331710182200038962|中信10343973|100.00|1234567891
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int count=0;
		for (int i = 0; i < datas.length; i++) {
			count++;
			if(count<=10)
			continue;
			String[] values = datas[i].split("\\|");
			if(values.length==7){
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setTseq(values[2]);
			bean.setDate(values[0].substring(0, 8));
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
