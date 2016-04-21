package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataZHESHANGImp  implements SettltData {
//	浙商银行-vas
//	交易类型：1代表支付，2代表退款 处理状态：Y代表成功 N代表失败	
//	清算日期,流水号,商户号,订单号,交易日期,交易类型,交易金额(元),币种,原订单号,原订单日期,处理状态,失败原因
//	2011-11-12,PAY000000006325,570766761,CH111112035350360,2011-11-12 3:54,1,"3,450.00",01,,,Y,
//	2011-11-12,PAY000000006326,570766761,CH111112040018128,2011-11-12 4:00,1,"50.00",01,,,Y,
//	2015-05-12,REJ000000002880	,570766761	,PH150509205559117242	,2015-05-12 15:54:04,2	,"100.00",01	,PH150509205559117242	,2015-05-09,Y	,
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 0; i < datas.length; i++) {
			if(i<=10) continue;
			String[] value = datas[i].replaceAll("\".*\"", " ").split(","); 
			if(value.length==12&&value[10].trim().equalsIgnoreCase("Y")&&value[5].trim().equals("1")){
				String amt=datas[i].split("\"")[1].replaceAll(",", "");//取交易金额  "1,000.00"
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setMerOid(value[3]);
				bean.setDate(getDate(value[4]));
				bean.setAmt(amt);
				res.add(bean);
			}
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}
	private String getDate(String dateTime){
		String dateStr=dateTime.replaceAll("-", "");
		if(dateStr.length()>=6){
			return dateStr.substring(0,6);
		}else{
			return "0";
		}
		
	}

}
