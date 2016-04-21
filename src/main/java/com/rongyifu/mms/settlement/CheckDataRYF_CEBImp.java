package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 光大银行：通过电银流水号对账
 *
 */
public class CheckDataRYF_CEBImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		ZF01|20140529|20140529135225|4628261|000499136946|365010000101||1630|0|1630|AAAAAAA|||
//		共有 1        条记录

		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 0; i < datas.length; i++) {//从第一行起i=0
			String[] rowDatas=datas[i].split("\\|");
			if(rowDatas.length==14){
				SBean sbean=new SBean();
				sbean.setGate(bank);
				sbean.setTseq(rowDatas[3]);
				sbean.setAmt(rowDatas[7].replaceAll(",",""));
				sbean.setDate(rowDatas[1]);
				sbeanList.add(sbean);
			}
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}
}