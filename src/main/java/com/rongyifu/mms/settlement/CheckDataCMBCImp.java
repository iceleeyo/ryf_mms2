package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 民生银行对账
 * @author meng.wanying
 *
 */
public class CheckDataCMBCImp implements SettltData{
	
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		中国民生银行网上支付商户对帐文件 ,,,,
//		商户名称,梦龙,对帐日期,20130718,
//		消费笔数,2,消费金额,0.02,
//		撤销笔数,0,撤销金额,0,
//		退单笔数,3,退单金额,3.99,
//		累计笔数,5,汇总金额,-3.97,
//		交易明细 ,,,,
//		订单号,交易金额,交易日期,交易时间,交易类型
//		'99012130710101615,2,20130718,160706,退货,
//		'99012130709100929,1,20130718,160705,退货,
//		'99012130710101615,0.99,20130718,160703,退货,
//		'99012130718141745,0.01,20130718,140709,消费,
//		'99012130718135943,0.01,20130718,130706,消费,
		
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			lineCount++;
			if(lineCount < 9 || Ryt.empty(line)){
				continue;
			}
			
			String[] value = line.split(",");
			if (value.length >= 5) {
				// 过滤收入金额为0的数据
				String amt = value[1];
				if (amtIsZero(amt))
					continue;
				if (value[4].contains("消费")) {
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setMerOid(value[0].replaceAll("'", "").trim());
					bean.setAmt(value[1].replaceAll(",", "").trim());
					bean.setDate(value[2]);
					res.add(bean);
				} else {
					continue;
				}

			}
		}
		return res;
	}

	/**
	 * 判断金额是否为0
	 * 
	 * @param amt
	 * @return
	 */
	private boolean amtIsZero(String amt) {
		if (Ryt.empty(amt))
			return true;
		try {
			double transAmt = Double.parseDouble(amt.replaceAll(",", "").trim());
			if (transAmt == 0D)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
