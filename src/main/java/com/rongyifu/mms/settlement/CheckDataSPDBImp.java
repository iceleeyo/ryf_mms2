package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataSPDBImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//这一行不包含在对账文件		交易缩写｜清算日期｜交易发生时间｜订单号｜网关流水号｜商户号｜终端号｜交易金额｜手续费｜净清算金额｜响应码｜商户保留1｜商户保留2|
//		IPER|20140707|20140707171911|009200098507|000835753414|914408160000201|1a|0.01|0.00|0.01|00|||
//		共有 1        条记录
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (String line : datas) {
				if(line.trim().length()==0)
					break;
				String[] value = line.split("\\|");
				if(value.length != 14 || !"IPER".equals(value[0]))
					continue;
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(Long.parseLong(value[3])+"");
				bean.setAmt(value[7].replaceAll(",",""));
				bean.setDate(value[2].replaceAll("-", "").substring(0,8));
				res.add(bean);				
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
