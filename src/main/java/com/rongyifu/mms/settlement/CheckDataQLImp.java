package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rongyifu.mms.common.Ryt;
/**
 * 齐鲁银行对账
 *
 */
public class CheckDataQLImp implements SettltData{
	Logger log=Logger.getLogger(CheckDataQLImp.class);
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		
//		订单号,商户流水号,网银流水号,订单日期,订单金额,手续费金额,已退款金额,交易类型,入账日期,订单状态
//		"MV120928102545236148	","20120928102628556485	","30000000019790	","20120928	","42.00	","0.00	","	","消费	","20120929	","成功	"
//		"FT120928103430401933	","20120928103512512453	","30000000019791	","20120928	","2220.00	","0.00	","	","消费	","20120929	","成功	"
//		"FT120928083218473988	","20120928083304380406	","30000000019766	","20120928	","2220.00	","0.00	","	","消费	","20120929	","成功	"
				
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
		for (int i = 1; i < datas.length; i++) {//从第二行起i=1
			if(datas[i] == null)
				continue;
			
			String[] rowDatas=datas[i].replaceAll("\"", "").split(",");	
			if(rowDatas.length==10){
				//增加过滤代码 过滤（失败交易）入账日期为空的数据行
				if(Ryt.empty(handleNull(rowDatas[8]))){
					log.info("fun=CheckDataQLImp  method=getCheckData"+"失败交易信息（入账日期为空）："+datas[i]);
					continue;
				}
				SBean sbean=new SBean();
				sbean.setGate(bank);
				sbean.setMerOid(handleNull(rowDatas[0]));
				sbean.setAmt(handleNull(rowDatas[4]));
				sbean.setBkFee(handleNull(rowDatas[5]));
				sbean.setDate(handleNull(rowDatas[8]));
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

	private String handleNull(String str){
		return str == null ? "" : str.trim();
	}
}
