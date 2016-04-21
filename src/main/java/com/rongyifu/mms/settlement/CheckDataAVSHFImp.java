package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 恒丰银行-vas
 * @author Administrator
 *
 */
public class CheckDataAVSHFImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
//		交易流水号;商户号;订单号;交易类型;交易状态;订单付款卡号;交易金额;交易日期;订单日期;订单时间;订单状态;订单内容;订单金额;币种;订单提交时间;已退款次数;已退款金额;已退款手续费
//		MB100000000000048059;000000000013662;PH140611232950232471;支付;成功;6230 **** **** 9637;100.00;2014-06-1123:28:44;2014-06-11;23:29:50;已支付;;100.00;人民币;2014-06-1123:30:14;0;0.00;0.00
//		MB100000000000048058;000000000013662;PH140611232124694852;支付;成功;6223 **** **** 6788;50.00;2014-06-1123:20:18;2014-06-11;23:21:25;已支付;;50.00;人民币;2014-06-1123:21:48;0;0.00;0.00
//		MB100000000000048057;000000000013662;PH140611225429456576;支付;成功;6223 **** **** 0884;50.00;2014-06-1122:53:55;2014-06-11;22:54:30;已支付;;50.00;人民币;2014-06-1122:55:26;0;0.00;0.00
		
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int row = 0;
		for (String line : datas) {
			row++;
			
			if(row == 1)
				continue;
			
			if (Ryt.empty(line))
				continue;

			String[] dataArr = line.split(";");
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setMerOid(dataArr[2]);
			bean.setAmt(dataArr[6].replaceAll(",", ""));
			bean.setDate(dataArr[8].replaceAll("-", ""));
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