package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 建行B2C
 *
 */
public class CheckDataCCB_B2C_Imp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		
//		 交易流水明细
//		 结算单生成时间：2012-08-08 11:04:22
//		 记账日期[2012-08-07]
//		 交易类型 交易时间 定单号 付款方账号 支付金额 退款金额 柜台号 备注1 备注2 付款方式 订单状态 记账日期
//		 支付	2012-08-07 11:09:52	7979	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 09:56:15	7910	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 10:00:43	7914	***************7974	0.20	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 10:16:31	7936	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 10:18:19	7937	***************7974	0.01	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 10:51:31	7966	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 11:36:55	8010	***************7974	0.21	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 14:53:45	8082	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//		 支付	2012-08-07 15:35:26	8102	***************7974	0.10	0.00	552799318	*	*	本地	成功	2012-08-07	
//
//		 支付总金额/总笔数：1.02/9
//		 退款总金额/总笔数：0.00/0
		int lineCount = 0;
		for (String line : datas) {
			if(lineCount>3){
				String[] value = line.split("\t");
				if(value.length == 12){
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(value[2]);
					bean.setDate(value[11]);
					bean.setAmt(value[4].replaceAll(",",""));
					res.add(bean);
				}
			}
			lineCount=lineCount+1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		return null;
	}
}
