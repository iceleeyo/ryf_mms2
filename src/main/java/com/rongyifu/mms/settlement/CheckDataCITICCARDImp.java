package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataCITICCARDImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		 Citic Bank Credit Card Center                                                                                                      
//		 Merchant Batch All-Process Report                                                                                                  
//		 Report Date  20130410                                                                          Syetem Date  20130411               
//		 ================================================================================================================================== 
//		 Merchant:  099900000998800                                                                                                         
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 Merchant:  099900000998800     Batch:  000001                                                                                      
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 CardNbr/OrderNbr    TrxType TranDate TranTime AuthNbr TraceNbr RefNbr                    TransAmt Curr PdtCde DvdMth RspCde StlFlg 
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 51821280****0805    RFD     20130410 11192990 537717  949899   101119296259             -2,232.00  156 000000   00     00    A     
//		 51490600****0515    RFD     20130410 11420882 096238  950364   101142087669             -1,060.00  156 000000   00     00    A     
//		 40339200****7221    RFD     20130410 12354293 753945  951400   101235429003               -520.00  156 000000   00     00    A
//		 40339300****5186    RFD     20130410 23170940 202295  962600   102317097954            -14,064.00  156 000000   00     00    A     
//		 ---------------------------------------------------------------------------------------------------------------------------------  
//		 Batch Trx Cnt:               74     Batch Amt Sum:                  -86,341.40                                                     
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 Merchant:  099900000998800     Batch:  025300                                                                                      
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 CardNbr/OrderNbr    TrxType TranDate TranTime AuthNbr TraceNbr RefNbr                    TransAmt Curr PdtCde DvdMth RspCde StlFlg 
//		 ---------------------------------------------------------------------------------------------------------------------------------- 
//		 62268000****6507    PER     20130409 23004790 321954  945387   092300479914              2,140.00  156 000000   00     00    A     
//		 62268900****0664    PER     20130409 23031242 322890  945411   092303121764                950.00  156 000000   00     00    A     
//		 62268900****7510    PER     20130409 23041494 323268  945420   092304149964                157.00  156 000000   00     00    A     
//		 62268900****3973    PER     20130409 23062343 323989  945437   092306231797                240.50  156 000000   00     00    A     
//		 62268900****2575    PER     20130409 23062524 323998  945438   092306251799                360.00  156 000000   00     00    A     
//		 52010880****0142    PER     20130409 23064277 324132  945441   092306421010                520.00  156 000000   00     00    A  
		List<SBean> res = new ArrayList<SBean>();
		String[] records = fileContent.split("\n");
		String batchNo="";
		for (int i = 0; i < records.length; i++) {
			String record=records[i].trim();
			if(record.indexOf("PER")>0){
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setFlag(4);
				bean.setP1(record.substring(54,60));//流水号
				bean.setP2(batchNo);//批次号
				bean.setDate(record.substring(28,36));
				bean.setAmt(record.substring(75,97).replaceAll("-", "").replaceAll(",", "").trim());
				res.add(bean);
			}else if(record.indexOf("Batch:")>0){
				batchNo=record.substring(record.indexOf("Batch:")+6).trim();
			}else
				continue;
		}
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		
		
		return null;
	}
		
}
