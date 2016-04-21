package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 青岛银行vas
 * 
 * @author meng.wanying
 * 
 */
public class CheckDataQBA_VASImp implements SettltData {

//	对账文件xls格式	
//	订单号	交易时间	网银流水号	买方商户名称	清算时间	交易金额	手续费	交易状态	交易结果
//	PH150506140148317555	2015年05月06日	10000000000000334196	栾晓莉	2015年05月07日	100.00	0.00	成功	订单支付
//	PH150506140244714924	2015年05月06日	10000000000000334197	李秀美	2015年05月07日	50.00	0.00	成功	订单支付
//	PH150503123000820836	2015年05月03日	10000000000000334199	上海电银信息技术有限公司		50.00	0.00	成功	订单退款
//	PH150503101240303999	2015年05月03日	10000000000000334200	上海电银信息技术有限公司		100.00	0.00	成功	订单退款
//	PH150506141728431922	2015年05月06日	10000000000000334214	吴蒙	2015年05月07日	100.00	0.00	成功	订单支付

//	转换后格式
//	PH150506140148317555|2015年05月06日|10000000000000334196|栾晓莉|2015年05月07日|100.00|0.00
//	PH150506140244714924|2015年05月06日|10000000000000334197|李秀美|2015年05月07日|50.00|0.00
//	PH150503123000820836|2015年05月03日|10000000000000334199|上海电银信息技术有限公司||50.00|0.00
//	PH150503101240303999|2015年05月03日|10000000000000334200|上海电银信息技术有限公司||100.00|0.00
//	PH150506141728431922|2015年05月06日|10000000000000334214|吴蒙|2015年05月07日|100.00|0.00
	
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (String line : datas) {
				String[] value = line.split("\\|");
				// 过滤退款数据
				if(Ryt.empty(value[4]))
					continue;
				
				if (value.length == 7) {
					
					String[] date1 = value[1].split("年");
					String[] date2 = date1[1].split("月");
					String[] date3 = date2[1].split("日");
					String date = date1[0] + date2[0] + date3[0];
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setMerOid(value[0]);
					bean.setDate(date);
					bean.setAmt(value[5]);
					bean.setBkFee(value[6]);
					res.add(bean);
			}
		}
		return res;

	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
