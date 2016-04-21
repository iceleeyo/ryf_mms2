package com.rongyifu.mms.settlement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 银联
 *
 */
public class CheckDataUnionpayImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		
//		04032900    00092900    315183 0608185456 6222021001098550814 000000000002 000000000000  00000000000 0200 000000 4816 06000001 403310048168501 000001000004 00 012345 01020000    000000 00 021 000000000000 000000000000  00000000000 1 000 2 0 0000000000 01020000    0 03     00000000000     101		  
//		04032900    00092900    003471 0506124024 4041170049****56    000000040000 000000000000  00000000000 0200 000000 5398 30116001 4033100******08 000001724725 00 028658 01030000    000000 00 021 000000000008 000000000000  00000000000 1 000 2 0 0000000000 01030001    0 03     00000000000     102       
//		04032900    00092900    010830 0506124314 622892002003*****0  000000029800 000000000000  00000000000 0200 000000 5398 30116001 4033100******08 000001724755 00 000000 04012900    000000 00 021 000000000006 000000000000  00000000000 1 000 2 0 0000000000 04012900    0 03     00000000000     101       
//		04032900    00092900    022675 0506124905 622262011000****448 000000029700 000000000000  00000000000 0200 000000 5398 30116001 4033100******08 000001724817 00 648258 03010000    000000 00 021 000000000006 000000000000  00000000000 1 000 2 0 0000000000 03012900    0 03     00000000000     101       
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(new Date());
		for (String line : datas) {
			
			String data = line.trim().replaceAll("\\s{1,}", "\\|");
			String[] value = data.split("\\|");
			
			if (value != null && value.length == 33) {
				// 银联参考号
				String cupRef = value[2];
				// 扣款日期
				String date = year + value[3].substring(0, 4);
				// 交易金额: 以分为单位
				String amt = String.format("%.2f",Double.parseDouble(value[5]) / 100);
				
				/*-- 过滤余额查询交易 --*/ 
				String reportType = value[8];	// 报文类型
				String transType = value[9].substring(0,2);	// 交易类型码
				// 只要是报文类型为0200且交易类型码为300000的交易记录定义为无效交易记录，不参与对账结算；POS交易对账结果体现有效交易对账结果数据
				if("0200".equals(reportType) && "30".equals(transType)){
					continue;
				}
				/*-- 过滤消费撤销交易 ,改交易金额为负--*/

				// 只要是报文类型为0200且交易类型码为200000的交易记录为撤销交易，需要将交易金额设置成负数
				if (("0200".equals(reportType) && "20".equals(transType))||("0420".equals(reportType) && "00".equals(transType))) {
					
					SBean bean = new SBean();
					bean.setGate(bank);
					bean.setDate(date);
					bean.setAmt("-"+amt);//改交易金额为负
					bean.setBkSeq(cupRef);
					bean.setFlag(5);
					res.add(bean);
					continue;
				}
				
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setDate(date);
				bean.setAmt(amt);
				bean.setBkSeq(cupRef);
				bean.setFlag(5);
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
