package com.rongyifu.mms.service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.RYFMapUtil;

public class CommonService {
	
	/**
	 * @return 省份Map
	 */
	public TreeMap<Integer, String> getProvMap() {
		return RYFMapUtil.getProvMap();
	}

	/**
	 * @return 融易通所支持的网关Map 8008 广发银行
	 */
	public static TreeMap<Integer, String> getGateMap() {
		return RYFMapUtil.getGateMap();
	}
	
	/**
	 * 刷新商户的网关列表
	 * @param mid
	 */
	public boolean refreshFeeCalcModel(String mid) {
		try {
			RYFMapUtil util = RYFMapUtil.getInstance();
			util.refreshFeeCalcModel(mid);
			Map<String, Object> p = new HashMap<String, Object>();
	 		p.put("t", "fee_calc_mode");
	 		p.put("k", "");
	 		p.put("f", "");
	 		return EWPService.refreshEwpETS(p) ? true : false;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 刷新card_auth黑白名单表
	 * @param mid
	 */
	public boolean refreshCardAuth() {
		try {
			Map<String, Object> p = new HashMap<String, Object>();
	 		p.put("t", "card_auth");
	 		p.put("k", "");
	 		p.put("f", "");
	 		return EWPService.refreshEwpETS(p) ? true : false;
			
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 刷新商户Map  id-> abbrev 
	 * @param mid
	 */
	public boolean refreshMinfoMap(String mid) {
		try {
			RYFMapUtil util = RYFMapUtil.getInstance();
			util.refreshMinfoMap(mid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 得到商户已经开通的网关号
	 */
	public Map<Integer, String> getMerGatesMap(String mid) {
		RYFMapUtil util = RYFMapUtil.getInstance();
		return util.getMerGatesMap(mid);
	}
	/**
	 * 得到商户Map
	 */
	public Map<Integer, String> getMerMap() {
		RYFMapUtil util = RYFMapUtil.getInstance();
		return util.getMerMap() ;
	}
	

}
