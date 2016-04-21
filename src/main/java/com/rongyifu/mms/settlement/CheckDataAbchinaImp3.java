package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 农业银行-vas 
 * 通过银行流水号对账
 * @author yang.yaofeng 
 *
 */
public class CheckDataAbchinaImp3 implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)throws Exception {
//		交易类型^账单编号^交易金额^票据号^交易时间^交易批号^批内序号^参考号
//		REFUND^GM150428170904181687^49.9^750592^2015-5-6 14:09:39^000775^90150506140939SS375^360506068162
//		SALE^GM150506001350941654^29.97^750460^2015-5-6 0:14:02^000775^9015050600140276065^360506001974
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 1; i < datas.length; i++) {//从第二行起i=1
			// 过滤非交易数据
			if (datas[i].indexOf("SALE") == -1)
				continue;
			String[] rowDatas=datas[i].split("\\^");
			SBean sbean=new SBean();
			sbean.setGate(bank);
			sbean.setBkSeq(rowDatas[7]);
			sbean.setAmt(rowDatas[2].replaceAll(",",""));
			sbean.setDate(handleDate(rowDatas[4]));
			sbean.setFlag(2);
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
