package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工商银行
 * @author m
 *
 */
public class CheckDataICBCImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

//		商城代码:0200EC23720705
//		商城公司名称:北京融易通信息技术有限公司
//		从2011-02-01至2011-03-23的交易明细如下：
//		商城账号                       订单号                        交易流水号              订单金额            交易时间            交易状态        退货状态  累计退还转付金额
//		0200012719201015351           987                           HIM000002057601803                0.01 2011-03-21 11:20    支付成功已清算         未经退货      0.00
//		0200012719201015351           654                           HIM000002057616013                0.10 2011-03-21 11:27    支付成功已清算         未经退货      0.00
//		0200012719201015351           652                           HIM000002057805230                0.10 2011-03-21 12:56    支付成功已清算         未经退货      0.00
//		0200012719201015351           650                           HIM000002057914187                0.10 2011-03-21 13:42    支付成功已清算         未经退货      0.00
//		0200012719201015351           640                           HIM000002057933351                0.10 2011-03-21 13:50    支付成功已清算         未经退货      0.00
		
		int lineCount = 0;
		for (String line : datas) {
			
			if(line.trim().length()==0){
				break;
			}
			if(lineCount>3){
				String data = line.replaceAll("\\s{1,}","\\|");
				String[] value = data.split("\\|");
//				商城代码:0200EC23720705
//				商城公司名称:北京融易通信息技术有限公司
//				从2011-02-01至2011-03-23的交易明细如下：
//				商城账号                       订单号                        交易流水号              订单金额            交易时间            交易状态        退货状态  累计退还转付金额
//				0200012719201015351           987                           HIM000002057601803                0.01 2011-03-21 11:20    支付成功已清算         未经退货      0.00
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(value[1]);
				bean.setAmt(value[3].replaceAll(",",""));
//				bean.setBkMdate(value[4].replaceAll("-",""));
				bean.setDate(value[4].replaceAll("-",""));
//				bean.setBkFee(null);
				res.add(bean);
			}
			lineCount=lineCount+1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
