package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataYeePayImp implements SettltData{
	
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		格式 cvs  Excel打开		
//		商户订单号,       银行订单号,易宝订单号,          订单金额,支付时间,                商品名称,扩展信息,支付银行  ,分账明细
//		1296108419885,40191399,112413324066801H,150.00,2011-01-27 14:28:09,"TESTPID",""  ,ABC-NET,
//		1296108843713,00000040191408,319700412375857G,150.00,2011-01-27 14:28:10,"TESTPID","",GDB-NET,
//		1296111142757,01027000000040191429,719280322619805G,200.00,2011-01-27 15:07:11,"TESTPID","",CMBC-NET,
//		1296104207818,01121000000040191398,767129691521514E,150.00,2011-01-27 14:28:09,"TESTPID","",CMBC-NET,

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			if(line.trim().length()==0){
				break;
			}
			if(lineCount>0){
				String[] value = line.split(",");
//				商户订单号,银行订单号,易宝订单号,订单金额,支付时间,商品名称,扩展信息,支付银行,分账明细
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[0]);
				bean.setAmt(value[3].replaceAll(",",""));
				bean.setDate(value[4].replaceAll("-", "").substring(0,8));
				res.add(bean);				
			}
			lineCount=lineCount+1;
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
