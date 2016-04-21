package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//建设银行vas
public class CheckDataCCB_VAS_Imp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

//		支付流水明细
//
//		出单日期[20110423] 记账日期[20110422]
//		交易时间 定单号 付款方账号 支付金额 退款金额 柜台号 备注1 备注2 付款方式 订单状态 记账日期
//		2011-04-22 17:47:05	3229	***************7974	0.01	0.00	649167638	--	--	本地	成功	20110422	
//		2011-04-22 17:50:06	3230	***************7974	0.01	0.00	649167638	--	--	本地	成功	20110422	
//		2011-04-22 18:10:03	3238	***************7974	1.00	0.00	649167638	--	--	本地	成功	20110422
//		2011-10-26 14:04:22	157082	***************7974	0.10	0.00	649167638	90002ZMAX(0.1,AMT*0.01)Z2ZAMT*	--	本地	成功	20111026
		int lineCount = 0;
		for (String line : datas) {
			if (lineCount > 3) {
				String[] value = line.split("\t");
				String bkFee = null;
				String counternum = value[5].trim();
				Double amt = Double.parseDouble(value[3].replaceAll(",", ""));
				if (counternum.equals("476574594")) {					
					Double amt1 = amt * 0.004;
					java.math.BigDecimal b = new java.math.BigDecimal(amt1);
					bkFee = b.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).toString();  
				} else if (counternum.equals("400078363")|| counternum.equals("826899809")|| counternum.equals("728463829")) {
					Double amt1 = amt * 0.003;
					java.math.BigDecimal b = new java.math.BigDecimal(amt1);  
					bkFee = b.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).toString();
				} else {
					bkFee = "0.00";
				}
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setMerOid(value[1].trim());
				bean.setDate(value[10].trim());
				bean.setAmt(value[3].replaceAll(",", ""));
				bean.setBkFee(bkFee);
				res.add(bean);
			}
			lineCount = lineCount + 1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
