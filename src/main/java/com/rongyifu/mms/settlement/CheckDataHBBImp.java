package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 河北银行对账
 * @author m
 *
 */
public class CheckDataHBBImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
//		流水号,商户编码,订单号,币种,订单金额,姓名,交易日期,交易时间,交易状态,退款状态,对账结果
//		"64120927102583	","2000020180	","GC120927000634559453	","人民币	","1.00	","张卓博	","20120927	","000419	","支付成功	","	","已对账	"
//		"64120927102584	","2000020180	","GC120927001250954640	","人民币	","1.00	","张卓博	","20120927	","001036	","支付成功	","	","已对账	"
//		"64120927102585	","2000020180	","GC120927001923704865	","人民币	","1.00	","张卓博	","20120927	","001704	","支付成功	","	","已对账	"
//		"64120927102600	","2000020180	","GC120927150153240542	","人民币	","1.00	","张卓博	","20120927	","145934	","支付失败	","	","已对账	"
//		"64120927102620	","2000020180	","OF120927182344439228	","人民币	","50.00	","汪登勇	","20120927	","182139	","支付成功	","	","已对账	"
		
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 1; i < datas.length; i++) {//从第二行起i=1
			// 过滤支付失败的对账数据
			if(datas[i] != null && datas[i].indexOf("支付失败") != -1)
				continue;
			
			String[] rowDatas=datas[i].split("\",\"");
			if(rowDatas.length==11){
				SBean sbean=new SBean();
				sbean.setGate(bank);
				sbean.setMerOid(rowDatas[2].trim());
				sbean.setAmt(rowDatas[4].replaceAll(",","").trim());
				sbean.setDate(rowDatas[6].trim());
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
