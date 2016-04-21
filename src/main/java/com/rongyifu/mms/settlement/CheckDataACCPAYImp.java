package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
/**
 * 账户支付对账
 * @author wmm
 *
 */
public class CheckDataACCPAYImp implements SettltData {
	//;订单号;	交易号;	订单日期;	商户号;	账户号;交易金额;				支付完成日期;	交易状态
	//;TC151013163000037KW2;20132;	2015-10-13 16:31:09;	56789;	2929393949;85.00;	2015-10-13 16:33:27;	支付成功;
	//;TC151013132600086FO2	;	20131;	2015-10-13 13:27:03;56789;	4949596969;	135.00;	2015-10-13 13:28:22	;	支付成功;	
	
	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> sbeanList = new ArrayList<SBean>();	
		String[] datas = null;
		if(fileContent.indexOf("\n") != -1) datas = fileContent.split("\n");
		for (int i=1;i<datas.length;i++) {// 数据从第2行开始 
			String line = datas[i];
			if(Ryt.empty(line)) continue;
			String[] dataArr = line.trim().split(";");
			SBean bean = new SBean();
			bean.setGate(bank);
			//bean.setBkSeq(dataArr[0].trim());//银行流水号
			bean.setMerOid(dataArr[0].trim());
			bean.setFlag(3);//根据银行网关、商户订单号进行对账
			bean.setAmt(dataArr[5].replaceAll(",", ""));//金额
			String aa =dataArr[6].trim().replace("-", "");
			bean.setDate(aa.substring(0, 9));
			sbeanList.add(bean);
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}
}
