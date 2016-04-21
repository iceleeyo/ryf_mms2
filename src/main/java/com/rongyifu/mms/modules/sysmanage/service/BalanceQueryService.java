package com.rongyifu.mms.modules.sysmanage.service;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.utils.LogUtil;

/***
 * B2eGate网关余额查询Service
 * @author admin
 *
 */
public class BalanceQueryService {
	
	/**
	 * 余额查询
	 * 查询成功返回余额，否知返回失败原因
	 * @param gid
	 * @return
	 */
	public String queryBalance(String gid){
		//创建EwpReqModule
		String[] respInfo;
		try {
			respInfo = CallPayPocessor.queryBkAccountBalance(gid);
			LogUtil.printInfoLog("BalanceQuserService", "queryBalance", "respinfo[0]:"+respInfo[0]+" respinfo[1]:"+respInfo[1]+" respinfo[2]:"+respInfo[2]);
			if(respInfo[0].equals("success")){
				String balance=respInfo[1];
				if(!balance.contains(".")){
					balance=Ryt.div100(balance);
				}	
				return balance;
			}else{
				return respInfo[2];
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("BalanceQueryService", "queryBalance","gid:"+gid+"   exception", e);
			return "查询失败，系统异常";
		}
		
	}
}
