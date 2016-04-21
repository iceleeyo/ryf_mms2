package com.rongyifu.mms.ewp;

import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.LogUtil;


public class DaifuPay {
	public static PubDao pubDao=new PubDao();
	
	/***
	 * 查询接口  
	 * @param merId 商户号
	 * @param transType 交易类型
	 * @param tseq 流水号
	 * @return
	 */
	public static String queryOrderState(String transType,String tseq){
		StringBuffer result=new StringBuffer();
		String sql="select tstat from tlog where  tseq="+tseq;
		String state=pubDao.queryForString(sql);
		String sta="";
		String message=null;
		/**判断查询 tlog不存在  查hlog**/
		if(state==null || state.equals("")){
			sql="select tstat from hlog where  tseq="+tseq;
			state=pubDao.queryForString(sql);
		}
		if(state==null || state.equals("")){
			LogUtil.printInfoLog("DaifuPay", "queryOrderState", " oid:"+tseq+"  该笔订单不存在！");
			sta="F";
			message="该笔订单不存在";
		}else if(state.equals("1")){
			sta="W";
			message="该笔订单状态：待支付";
		}else if(state.equals("2")){
			sta="S";
			message="该笔订单状态：支付成功";
		}else if(state.equals("3")){
			sta="F";
			message="该笔订单状态：支付失败";
		}
		result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status>").append("<value>").append(sta).append("</value>");
		result.append("<msg>").append(message).append("</msg></status></res>");
		return result.toString();
	}
	
	
}
