package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataBOC_SZ_creditCard implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)throws Exception {
		
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		
//深圳市科技有限公司[104480845111514]
//结算日期[20120201]-[20120206]交易明细
//交易后台:中行总行交易|卡类型：全部卡|发卡行：全部   |终端号：全部终端
//结算货币：人民币
//商户号：104480845111514
//   终端号 	批次号	单据号	          卡号          	                                           结算日期 	交易日期	                        交易金额  	     手续费  	   结算金额 	           授权码	交易码	分期	              卡别 	            参考号   	            订单号            	
//48015846	060131	      	4096-70XX-XXXX-4827     	20120201	20120131	      84.00	       0.34	      83.66	       682528	 PCEP 	0000	 BOCC 	  203111424953	                              	
//48015846	060131	      	4096-70XX-XXXX-4827     	20120201	20120131	     186.50	       0.75	     185.75        812359	 PCEP 	0000	 BOCC 	  203111424112	                              	
//48015846	060131	      	4380-88XX-XXXX-0774     	20120201	20120131	     157.00	       0.63	     156.37	       047766	 PCEP 	0000	 BOCC 	  203109489507	  
		int lineCount = 0;
		for (String line : datas) {
			String[] value = line.split("\t");
			if(lineCount>5&&value.length >=15){
				SBean bean = new SBean();
				bean.setGate(bank);
//				bean.setTseq(value[13].trim());
				bean.setBkSeq(value[13].trim());
				bean.setMerOid(null);
				bean.setTseq(null);
				bean.setAmt(value[6].replaceAll(",","").trim());
				bean.setBkFee(value[7].replaceAll(",","").trim());//手续费
				bean.setDate(value[5].trim());
				res.add(bean);
			}
			lineCount=lineCount+1;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}

}
