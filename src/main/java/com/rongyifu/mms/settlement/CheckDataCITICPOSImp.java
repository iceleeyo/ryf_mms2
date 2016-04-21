package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 /**
  * 中信银行 pos
  * @author meng.wanying
  *
  */
public class CheckDataCITICPOSImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
///清算日期|交易时间  |处理码|交易流水号|卡号               |交易金额       |手续费         |结算金额       |参考扣率|授权码|参考号      |原参考号    |终端号  |商户号         |商户名称                                
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//20120216|0216204102|003000|000311    |6226900503052801   |0.08           |0.00           |0.08           |0.72%   |498792|010200010496|            |50010001|010290048161024|快钱支付清算信息有限公司                
//20120216|0216204424|003000|000318    |6226900503052801   |0.02           |0.00           |0.02           |0.72%   |500607|010200010506|            |50010001|010290048161024|快钱支付清算信息有限公司                
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//                                                     总计|217.44         |1.75           |215.69         |

		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int count = 0;
		for (int i = 0; i < datas.length; i++) {
			count++;
			if (count <= 2)
				continue;
			String[] values = datas[i].split("\\|");
			if (values.length == 15) {
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setDate(values[0]);
				bean.setAmt(values[5]);
				bean.setBkSeq(values[10]);
				bean.setFlag(5);

				res.add(bean);
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
