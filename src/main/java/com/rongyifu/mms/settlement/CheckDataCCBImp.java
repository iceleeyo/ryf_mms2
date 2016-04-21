package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//建设银行
public class CheckDataCCBImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		
		//System.out.println(datas[2].replaceAll("\\s{1,}", "\\|"));
		String date = datas[2].replaceAll("\\s{1,}", "\\|");
		String dzDate = date.split("\\|")[1];
		dzDate = dzDate.substring(5, 13);
		
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
			if(lineCount>3){
				String[] value = line.split("\t");
//					String[] dt = value[1].split(" ");
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(value[1]);
//					bean.setBkMdate(dt[0].replaceAll("-",""));
					bean.setDate(value[10]);
					//System.out.println(dzDate+"aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					bean.setAmt(value[3].replaceAll(",",""));
//					bean.setBkFee(null);
					res.add(bean);
			}
			lineCount=lineCount+1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
