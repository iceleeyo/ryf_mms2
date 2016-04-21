package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 农业银行
 * @author m
 *
 */
public class CheckDataAbchinaImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)throws Exception {
//		交易类型^账单编号^交易金额^票据号^交易时间^交易批号^批内序号^参考号
//		SALE^6217^0.1^3^2011-10-18 14:18:36^000001^9011101814183641825^361018066886
//		SALE^6220^0.1^5^2011-10-18 14:22:51^000001^9011101814225161075^361018067545
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 1; i < datas.length; i++) {//从第二行起i=1
			String[] rowDatas=datas[i].split("\\^");
			SBean sbean=new SBean();
			sbean.setGate(bank);
			sbean.setTseq(rowDatas[1]);
			sbean.setAmt(rowDatas[2].replaceAll(",",""));
			sbean.setDate(handleDate(rowDatas[4]));
			sbeanList.add(sbean);
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/***
	 * 农行Vas 对账文件  日期处理
	 * @param date
	 * @return
	 */
	private String handleDate(String date){
		String[] dt=date.split(" ")[0].split("-");
		String year=dt[0];
		Integer month=Integer.parseInt(dt[1]);
		Integer day=Integer.parseInt(dt[2]);
		String monthStr=""+month;
		String dayStr=""+day;
		if(month<10){
			monthStr="0"+month;
		}
		if(day<10){
			dayStr="0"+day;
		}
		return year+monthStr+dayStr;
	}
}
