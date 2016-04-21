package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataUPMPImp implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		交易流水号  |交易时间        |主账号               |交易金额      |交易手续费    |交易消息类型      |交易处理码  |交易类别  |交易类别中文描述                        |清算日期    |电银商户号       |电银终端号  |订单号                 |原笔交易流水号  |原笔交易日期    |银行商户号       |银行终端号  |银联参考号    |授权码  |商户名称                                |收单机构                                |扣款渠道名称        |SAM卡号/终端硬件编号           |转入卡号/转入账号        |代理商  |银行手续费    |扣款渠道编号   |
//		364
//		723777      |20141118231020  |6222021402022686577  |10.00         |0.0           |消费              |910000      |227       |我要收款                                |20141119    |860020030210008  |00000008    |CZ141118230950014238   |                |                |872290049000002  |00005197    |231020723777  |012345  |电银信息（缴费）                        |上海电银                                |银联CUPS            |0DE03100200039F7         |MSWS7713             |000000  |0.03          |11             
//		723781      |20141118232635  |6222520774880278     |3996.00       |0.0           |消费              |910000      |227       |我要收款                                |20141119    |860020030210008  |00000008    |CZ141118232529014239   |                |                |872290049000002  |00005197    |232635723781  |366519  |电银信息（缴费）                        |上海电银                                |银联CUPS            |0DE03100200005C6         |sr111222             |000000  |11.99         |11             

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		
		for (int i = 0; i < datas.length; i++) {
			String []value=datas[i].split("\\|");
			if(i>1 && value.length>=27){
				String gid=value[26].trim();//渠道号
				String ordType=value[7].trim();//订单类型为18
				if("55001".equals(gid) && "18".equals(ordType)){
					String date=value[1].trim().substring(0,8);//交易时间
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(value[0].trim());
					bean.setDate(date.trim());
					bean.setAmt(value[3].replaceAll(",","").trim());
					res.add(bean);
				}
			}
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
