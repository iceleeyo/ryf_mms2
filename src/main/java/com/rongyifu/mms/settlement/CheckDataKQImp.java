package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//快钱
public class CheckDataKQImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		//交易编号	商家名称	终端票据编号	终端名称	缩略卡号	发卡机构编号	交易金额	手续费	交易时间	终端交易时间	外部跟踪编号	卡类型	交易状态	交易类型	输入模式
		//000038084478,北京融易通信息技术有限公司CNP/IVR,000002,北京融易通信息技术有限公司,6225884444,招商银行,1.00,,2011-05-23 17:39:42,2011-05-23 17:39:41,20110523173941,借记卡,交易转发,消费,手输卡号+无PIN,
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i=1;i<datas.length;i++) {
			String[] tStr = datas[i].split(",");
			if (tStr.length== 15) {
				SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(String.valueOf(Long.parseLong(tStr[10])));
//					bean.setBkMdate(tStr[8].split(" ")[0].replaceAll("-", "").trim());
					bean.setDate(tStr[9].split(" ")[0].replaceAll("-", "").trim());
					bean.setAmt(tStr[6].replaceAll(",",""));
					bean.setBkFee(tStr[7].replaceAll(",",""));//手续费金额
//					bean.setBkFee(null);
					res.add(bean);
			}
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}

}
