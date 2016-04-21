package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

/**
 * 银联wap支付
 * 
 */
//===================================================================
//
//               中国银联wap支付商户清算交易明细表
//商户编号：872310045110201	商户名称：上海电银信息技术有限公司
//清算日期：20140618     生成日期：20140618
//
//===================================================================
//S22 48722900    00049999    120150 0618145643 622262*********4809 000000000001 4511 07 201406181456431201502 06 2362182756578697                 01                    000000000000 C000000000001 0301                 01                                                                            872310045110201   000000000000000                                   000000000000 C000000000001 01080209                                   000000000000  000000000000                                                                                                                                                      
//S22 48722900    00049999    349854 0618153900 622202*********0814 000000000001 4511 07 201406181539003498542 06 2362182756579250                 01                    000000000000 C000000000001 0301                 01                                                                            872310045110201   000000000000000                                   000000000000 C000000000001 01080209                                   000000000000  000000000000                                                                                                                                                      
//S22 48722900    00049999    874392 0618143623 439225******1388    000000000001 4511 07 201406181436238743922 06 2362182756578457                 02                    000000000000 C000000000001 0301                 01                                                                            872310045110201   000000000000000                                   000000000000 C000000000001 01080209                                   000000000000  000000000000                                                                                                                                                      
//S22 48722900    00049999    889493 0618140909 439225******1388    000000000001 4511 07 201406181409098894932 06 2362182756578118                 02                    000000000000 C000000000001 0301                 01                                                                            872310045110201   000000000000000                                   000000000000 C000000000001 01080209                                   000000000000  000000000000                                                                                                                                                      
//S22 48722900    00049999    695494 0618193146 622262*********4809 000000000001 4511 07 201406181931466954942 06 2362182756582198                 01                    000000000000 C000000000001 0301                 01                                                                            872310045110201   000000000000000                                   000000000000 C000000000001 01080209                                   000000000000  000000000000                                                                                                                                                      

public class CheckDataCUPImp2 implements SettltData {

	private static HashMap<Integer, String> getFields(String rec) {
		ArrayList<Integer> volumes = new ArrayList<Integer>(Arrays.asList(3,
				11, 11, 6, 10, 19, 12, 4, 2, 21, 2, 32, 2, 6, 10, 13, 13, 4,
				15, 2, 2, 6, 2, 4, 32, 1, 21, 15, 1, 15, 32, 13, 13, 8, 32, 13,
				13, 120));
		char[] record = rec.toCharArray();
		HashMap<Integer, String> fields = new HashMap<Integer, String>();
		int j = 0;
		for (int i = 0; i < 38; i++) {
			int fieldBoundary = j + volumes.get(i);
			StringBuilder strbField = new StringBuilder();
			while (j < fieldBoundary) {
				char c = record[j];
				strbField.append(c);
				j++;
			}
			j++;
			fields.put(i, strbField.toString());
		}
		return fields;
	}

	private static String getExDate(String time) throws Exception {
		Pattern pattern = Pattern.compile("^\\d{10}");
		Matcher matcher = pattern.matcher(time);
		String exDate = null;

		if (matcher.find()) {
			exDate = matcher.group();
			return exDate;
		} else {
			// 抛出无法匹配交易时间异常
			throw new Exception() {
				private static final long serialVersionUID = 1L;
				private String errMessage = "Settlement Exception: The regex can't match the exchange time string";

				public String getMessage() {
					return errMessage;
				}

				public String toString() {
					return errMessage;
				}
			};
		}

	}

	private static String getFee(String feeStr) {

		Pattern pForData = Pattern.compile("\\d+");
		Matcher mForData = pForData.matcher(feeStr);

		String data = null;
		if (mForData.find()) {
			data = mForData.group();
		}

		Double dou=Double.parseDouble(data);
		if(dou==0){
			return "0.00";
		}else{
			double d=dou/100;
			return String.valueOf(d);
		}
		
	}

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {

		List<SBean> sbeanList = new ArrayList<SBean>();

		String[] datas = fileContent.split("\r\n");
		for (int i = 0; i < datas.length; i++) {
			HashMap<Integer, String> fields = CheckDataCUPImp2
					.getFields(datas[i]);
			
			// 过滤非交易数据：01 消费；04 退款
			if(!"01".equals(fields.get(19)))
				continue;
			
			String exDate = null;
			try {
				
				exDate = getExDate(fields.get(9));
                exDate = exDate.substring(0,8);
//				if(Long.parseLong(exDate.substring(8, 10))>=23){
//
//					int dt =DateUtil.getDatetomorrow(Integer.parseInt(exDate.substring(0,8)));
//					exDate=String.valueOf(dt);
//				}
//				else{
//					exDate =exDate.substring(0,8);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String bankSeq = fields.get(9);
			String amt = getFee(fields.get(6));
			String fee = getFee(fields.get(15));
			SBean sbean = new SBean();
			sbean.setGate(bank);
			sbean.setBkSeq(bankSeq); // 银行流水号
			sbean.setAmt(amt); // 交易金额
			sbean.setBkFee(fee);//商户手续费
			sbean.setDate(exDate);//交易日期
			sbean.setFlag(2);
			sbeanList.add(sbean);
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		
		return null;
	}

}