package com.rongyifu.mms.bank.query;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rongyifu.mms.dao.BankQueryDao;

public class QueryCommon {

	private static Map<String, String> gateMap = new HashMap<String, String>();
	
	private static BankQueryDao dao = new BankQueryDao();
	
	// 成功状态
	public static final String ORDER_STATUS_SUCCESS = "2";
	// 失败状态
	public static final String ORDER_STATUS_FAILURE = "3";
	// 其他状态
	public static final String ORDER_STATUS_OTHER = "9";
	
	// 初始化网关处理类
	static {
		// 招商银行-B2C
		gateMap.put("10300", "com.rongyifu.mms.bank.query.CMBQuery");
		// 招商银行-wap
		gateMap.put("90000", "com.rongyifu.mms.bank.query.CMBQuery");
		// 招商银行-wap(借)
		gateMap.put("90001", "com.rongyifu.mms.bank.query.CMBQuery");
		// 支付宝海航
		gateMap.put("56000", "com.rongyifu.mms.bank.query.AlipayQuery");
		gateMap.put("90009", "com.rongyifu.mms.bank.query.AlipayQuery");
		// 支付宝首航
		gateMap.put("56001", "com.rongyifu.mms.bank.query.AlipayQuerySH");
		gateMap.put("90010", "com.rongyifu.mms.bank.query.AlipayQuerySH");
		// 支付宝祥鹏
		gateMap.put("56002", "com.rongyifu.mms.bank.query.AlipayQueryXP");
		gateMap.put("90011", "com.rongyifu.mms.bank.query.AlipayQueryXP");
		// 银联手机支付
		gateMap.put("55000", "com.rongyifu.mms.bank.query.UnionQuery");
		// 银联wap支付
		gateMap.put("90016", "com.rongyifu.mms.bank.query.UnionWapQuery");
		// 民生银行wap
		gateMap.put("90018", "com.rongyifu.mms.bank.query.CMBCQuery");
		// 交行B2C/WAP（jh）
		gateMap.put("5", "com.rongyifu.mms.bank.query.BocomB2CQuery");
	    // 盛京B2C
		gateMap.put("10901", "com.rongyifu.mms.bank.query.ShengJingB2CQuery");
		// 易宝无卡支付
		gateMap.put("56113", "com.rongyifu.mms.bank.query.EposQuery");
		// 账户余额支付查询
		gateMap.put("55002", "com.rongyifu.mms.bank.query.BalancePayQuery");
	}
	
	public static Map<String, String> getGateMap(){
		return gateMap;
	}
	
	public static BankQueryDao getDao(){
		return dao;
	}
	
	/**
	 * 获取支付渠道ID，以“,”分隔
	 * @return
	 */
	public static String getGateList(){
		String gateList = "";
		if(gateMap.isEmpty())
			return gateList;
		
		Iterator<String> iterator = gateMap.keySet().iterator();
		while(iterator.hasNext()){
			gateList += iterator.next() + ",";
		}
		
		return gateList.substring(0, gateList.length() - 1);
	}
	
	/**
	 * 根据网关获取处理类
	 * @param gate
	 * @return
	 */
	public static String getHandleClass(Integer gate){
		if(gate == null)
			return null;
		
		String gid = String.valueOf(gate);
		return gateMap.get(gid);
	}
	
	public static String md5Encrypt(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}
}
