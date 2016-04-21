package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工行B2B
 * @author yang.yaofeng
 *
 */
public class CheckDataICBC_B2B_Imp  implements SettltData{

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		商城代码:1001EC13459193
//		商城公司名称:电银信息
//		从2013-05-08至2013-05-08的交易明细如下：
//		商城账号                  		      订单号                      	  交易序号         	 	   订单金额                   支付状态                              交易时间        			 累计退还金额
//		1001281219006888425   9100006379     HFK303690967      10                     指令处理完成，转账成功     2013-05-08 16:56     0
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			if(lineCount>3){
					String data = line.replaceAll("\\s{1,}","\\|");
					String[] value = data.split("\\|");
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setTseq(value[1]);
					bean.setDate(value[5].replaceAll("-", "").trim());
					bean.setAmt(value[3]);
					res.add(bean);
			}
			lineCount++;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	                    
	  public static void main(String[] args) {
			String Str="1001281219006888425           9100006379                    HFK303690967      10                     指令处理完成，转账成功     2013-05-08 16:56    0                      ";
			System.out.println(Str.replaceAll("\\s{1,}","\\|"));
		}
}
