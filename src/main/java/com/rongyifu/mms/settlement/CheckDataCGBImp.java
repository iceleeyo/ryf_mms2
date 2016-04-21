package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 广发银行对账
 * @author m
 *
 */
public class CheckDataCGBImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
//		银行流水号|订单号|支付时间|支付金额|支付状态|商户处理状态|付款人名称
//		200010910758|FT121026102444272546|2012-10-26 10:24:42|1,450.00|成功||
//		200010786842|FT121025193955985570|2012-10-25 19:39:54|920.00|成功||
		
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = rmBlankLine(fileContent.split("\n"));
		for (int i = 1; i < datas.length; i++) {//从第二行起i=1
//			System.out.println("["+i+"]"+datas[i]);
			String[] rowDatas=datas[i].split("\\|");

			SBean sbean = new SBean();
			sbean.setGate(bank);
			sbean.setMerOid(rowDatas[1]);
			//过滤金额中的 "和,
			sbean.setAmt(rowDatas[3].replaceAll(",", "").replaceAll("\"", ""));
			sbean.setDate(rowDatas[2].replaceAll("-", "").substring(0, 8));
			sbeanList.add(sbean);
		}
		
		
		return sbeanList;
	}
	
	/**
	* 删除所有空行
	*/ 
	private String[] rmBlankLine(String[] lines){
		List<String> rslt = new ArrayList<String>();
		for (String line : lines) {
			if(StringUtils.isBlank(line))continue;
			rslt.add(line);
		}
		return rslt.toArray(new String[0]);
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
