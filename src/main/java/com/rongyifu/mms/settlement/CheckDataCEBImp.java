package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 	光大银行(增值)：通过商户订单号对账
 * @author m
 *
 */
public class CheckDataCEBImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		ZF01|20110916|20110916003739|CH110916003738810|901262108145|365010000047||50|0|50|AAAAAAA|||
//		ZF01|20110916|20110916004848|CH110916004847664|901262108705|365010000047||200|0|200|AAAAAAA|||
//		共有 2      条记录
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 0; i < datas.length; i++) {//从第一行起i=0
			String[] rowDatas=datas[i].split("\\|");
			if(rowDatas.length==14){
				SBean sbean=new SBean();
				sbean.setGate(bank);
				sbean.setMerOid(rowDatas[3]);
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
		// TODO Auto-generated method stub
		return null;
	}

}
