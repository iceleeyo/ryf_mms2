package com.rongyifu.mms.ewp;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.liqmanage.dao.SettlementVerifyDao;
import com.rongyifu.mms.utils.MD5;

public class Test_AutoDf {
	
	private SettlementVerifyDao dao=new SettlementVerifyDao();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test_AutoDf autoDf=new Test_AutoDf();
		try {
			System.out.println(autoDf.run("102290068892"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String run(String bkNo) throws Exception{
		String mid="764";
		String payAmt="0.10";
		String RecAccNo="6222021001098550814";
		String RecAccName="zhong";
		String bankNo=bkNo;
		String liqBatch="111111";
		Integer gate=71001;
		String dfType = "A";
		String data_source = "7";
		String md5key = "iFv5x6Cu";
		String transType = "auto_df";
		String provId="310";
		String tmsIp=dao.queryBytmsIp(mid);
		String bankName = "bankName";
		String bankBranch = "bankBranch";
		// 保存订单到tlog;
		Map<String, String> map = dao
				.insertliqtlog(mid, payAmt, gate,
						RecAccNo, RecAccName,bankName,bankBranch,
						bankNo, dfType, data_source,
						liqBatch, transType, provId);
		String sql = map.get("sql");
		int[] ret=dao.batchSqlTransaction(new String[]{sql});
		if(ret[0]==1){
			System.out.println("insert suc");
		}else{
			System.out.println("insert fail");
		}
		String oid2 = map.get("oid");
		String date = map.get("date");
		String tseq = dao.getTlogTseq(mid, oid2,
				date, gate, RecAccNo,
				RecAccName, bankNo);
		System.out.println("tseq:"+tseq);
		String data = mid + payAmt + transType + data_source+ tmsIp + md5key;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("transAmt", payAmt);
		paramMap.put("data_source", data_source);
		paramMap.put("mid", mid);
		paramMap.put("chkValue", MD5.getMD5(data.getBytes()));
		paramMap.put("transType", transType);
		paramMap.put("tseq", tseq);
		String url = Ryt.getEwpPath();
		try {
			System.out.println("url:"+url+"df/auto_df");
			Ryt.requestWithPost(paramMap, url+ "df/auto_df");
			return "suc";
		} catch (Exception e) {
			// TODO: handle exception
			return "error:"+e.getMessage();
		}
		
	}

}
