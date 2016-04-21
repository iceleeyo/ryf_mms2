package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 支付宝对账
 * @author lv.xiaofeng
 *
 */
public class CheckDataAliPayImp implements SettltData{
	
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		格式 cvs  Excel打开		
//		#支付宝账务明细查询
//		#账号：[20888011365400330156]
//		#起始日期：[2012年12月29日 00:00:00]   终止日期：[2012年12月30日 00:00:00]
//		#-----------------------------------------账务明细列表----------------------------------------
//		账务流水号,业务流水号,商户订单号,商品名称,发生时间,对方账号,收入金额（+元）,支出金额（-元）,账户余额（元）,交易渠道,业务类型,备注
//		54632446537371	,2012122923922029	,3226563	,海航机票	,2012-12-29 21:02:06,陈晶 (kuangzhe@126.com)	,2274.00,0.00,14533.03,支付宝,在线支付,
//		54629090595411	,2012122948927762	,3226533	,海航机票	,2012-12-29 20:31:32,张青 (13335406888)	,393.00,0.00,12261.39,支付宝,在线支付,
//		54628332402591	,2012122926087002	,3226491	,海航机票	,2012-12-29 20:24:45,田爱平 (774680501@qq.com)	,1111.00,0.00,11875.06,支付宝,在线支付,
//		#----------------------------------------账务明细列表结束-------------------------------------
//		#支出合计：0笔，共0.00元
//		#收入合计：18笔，共14989.00元
//		#导出时间：[2012年12月31日 13:19:01]

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			lineCount++;
			if(lineCount < 6 || Ryt.empty(line)){
				continue;
			}
			
			String[] value = line.split(",");
			if(value.length == 12){
				// 过滤收入金额为0的数据
				String amt = value[6];
				if(amtIsZero(amt))
					continue;
				
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[2].trim());
				bean.setAmt(value[6].replaceAll(",","").trim());
				bean.setBkFee(value[7].replaceAll(",","").trim());
				bean.setDate(value[4].replaceAll("-", "").trim().substring(0,8));
				res.add(bean);
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
