package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 	兴业银行(增值)
 * @author m
 *
 */
public class CheckDataCIBImp implements SettltData {
//	下单日期|网银交易流水号|商户代号|商户订单号|交易帐号|订单金额|退款金额|手续费|订单状态|交易日期|结算日期|银行流水号
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		20111201|2011120106520093|110004|T20111201184254|*8241|0.01||0.00||20111201184348|20111201|9999A8VE
//		20111201|2011120106520279|110004|T20111201184516|*8241|0.05||0.01||20111201184548|20111201|9999A8WC

		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 0; i < datas.length; i++) {//从第一行起i=0
			
			String[] rowDatas=datas[i].split("\\|");
			if(rowDatas.length==12){
				SBean sbean=new SBean();
				sbean.setGate(bank);
				sbean.setMerOid(rowDatas[3]);
				sbean.setAmt(rowDatas[5].replaceAll(",",""));
				sbean.setBkFee(rowDatas[7].replaceAll(",",""));//手续费
				sbean.setDate(rowDatas[9].substring(0,8));
				sbeanList.add(sbean);
			}
		}
		return sbeanList;
		
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
