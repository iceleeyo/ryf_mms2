package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 盛京银行
 */
public class CheckDataSJYHImp implements SettltData {

//;交易日期;	交易时间;	收入;	支出;	手续费总额;对方帐号;				对方名称;	账户余额;		交易行所;				摘要;	流水号;备注
//;20140822;170407;	0.00;	1704.07;0.00;	0331410102000000023;******;	33967.69;	盛京银行沈阳市华山支行;	电银转账;	706;
//;20140822;163948;	0.00;	1639.48;0.00;	6212440300223293;	潘广春;	33967.79;	盛京银行沈阳市华山支行;	电银转账;	697;

	/** 
	 * 解析上传的数据，剔除前三行（表头）和最后一行（汇总信息）
	 */
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> sbeanList = new ArrayList<SBean>();	
		String[] datas = null;
		if(fileContent.indexOf("\n") != -1) datas = fileContent.split("\n");
		for (int i=2;i<datas.length-1;i++) {// 数据从第3行开始 不处理最后一行
			String line = datas[i];
			if(Ryt.empty(line)) continue;
			String[] dataArr = line.trim().split(";");
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setBkSeq(dataArr[12].trim());//银行流水号
			Double amt = Double.parseDouble(dataArr[4].trim());//金额
			if ( amt != 0.0 ) {
			}else{
				amt = Double.parseDouble(dataArr[5].trim());
			}
			bean.setAmt(amt.toString());
			bean.setBkFee(dataArr[6].trim());//订单手续费
			bean.setDate(dataArr[2].trim().replace("-", ""));
			sbeanList.add(bean);
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
