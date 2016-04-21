package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 杭州web广发银行对账
 * @author m
 *
 */
public class CheckDataCGBHZImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {

//		#交易日期|网上支付交易订单号|交易金额
//		2011-09-15|LT110915181318627|20.00
//		2011-09-15|CH110915181402662|99.50
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 2; i < datas.length; i++) {//从第三行起i=2
			String[] rowDatas=datas[i].split("\\|");
			if(rowDatas.length==3){
				SBean sbean=new SBean();
				sbean.setGate(bank);
//				sbean.setMerOid(rowDatas[1]);
				Long oid = Long.parseLong(rowDatas[1]);
				sbean.setTseq(String.valueOf(oid));
				sbean.setMerOid(null);
				sbean.setAmt(rowDatas[2].replaceAll(",",""));
				sbean.setDate(rowDatas[0].replaceAll("-", "").substring(0,8));
				
				sbeanList.add(sbean);
			}
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
