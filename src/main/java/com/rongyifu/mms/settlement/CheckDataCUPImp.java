package com.rongyifu.mms.settlement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 银联快捷支付
 * @author 
 *
 */
public class CheckDataCUPImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		==============================
//
//                中国银联移动支付直联商户清算交易明细表
//商户编号: 403310048998503                           商户名称: 北京融易通信息技术有限公司上海分公司                         收单机构: 04032900   
//清算日期: 20130731                           生成日期: 20130801013127   
//
//
//
//
//终端编号 交易日期时间   主账号                           发卡行      交易金额             商户费用             结算金额             系统参考号   系统跟踪号 交易渠道 交易类型 订单号
//-------- -------------- -------------------------------- ----------- -------------------- -------------------- -------------------- ------------ ---------- -------- -------- ----------------------------------------------------------------
//
//01080209 0731095524     622588******5448                 03081000                    2.00                 0.01                 1.99 003487497U00 072754     07       消费     3487497U00
//01080209 0731104928     523959******0122                 63040000                   50.00                 0.30                49.70 003487569U00 073467     07       消费     3487569U00
//01080209 0731172035     622631******1063                 03041000                    2.00                 0.01                 1.99 003488134U00 080125     07       消费     3488134U00
//01080209 0731174739     622631******1063                 03041000                    2.00                 0.01                 1.99 003488165U00 080484     07       消费     3488165U00
//01080209 0731224906     523959******2416                 63040000                  142.00                 0.85               141.15 003488526U00 085573     07       消费     3488526U00
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(new Date());
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		//System.out.println(datas[4].replaceAll("\\s{1,}", "\\|"));
		String date = datas[4].replaceAll("\\s{1,}", "\\|");
		String dzDate = date.split("\\|")[2];
		for (int i = 0; i < datas.length; i++) {// 从第一行起i=0
			if (i < 11)
				continue;

			String data = datas[i].replaceAll("\\s{1,}", "\\|");
			String[] rowDatas = data.split("\\|");
			if (rowDatas.length == 12) {
				if (rowDatas[10].contains("退货")) {
					continue;
				} else {
					SBean sbean = new SBean();
					sbean.setGate(bank);
					sbean.setTseq(handleU00(rowDatas[11])); // 商户订单号
					sbean.setBkSeq(handleU00(rowDatas[7])); // 系统参考号
					sbean.setAmt(rowDatas[4].replaceAll(",", "")); // 交易金额
					sbean.setBkFee(rowDatas[5].replaceAll(",", "").replaceAll(
							"-", "")); // 商户费用
					//sbean.setDate(year + rowDatas[1].substring(0, 4)); // 交易日期时间
					sbean.setDate(dzDate);//对账日期
					sbeanList.add(sbean);
				}
			}
		}
		return sbeanList;

	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}
	
	private String handleU00(String str){
		if(Ryt.empty(str))
			return str;
		
		String str2 = str.trim();
		if(str2.endsWith("U00")){
			str2 = str2.substring(0, str2.length() - 3);
		}
		
		return str2;
	}
	
}
