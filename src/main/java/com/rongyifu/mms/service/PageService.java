package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.RYFMapUtil;

public class PageService {
	
	private PubDao dao = new PubDao();

	/**
	 * 所有商户Map
	 * @return
	 */
	public Map<Integer, String> getMinfosMap() {
		RYFMapUtil obj = RYFMapUtil.getInstance();
		return obj.getMerMap();
	}
	/**
	 * 获得单个商户对象的操作员map
	 * @param mid
	 * @return
	 */
	public Map<Integer, String> getMerOpersMap(String mid){
		return new MerInfoDao().getAllopersMap(mid);
	}
	
	/**
	 * 获取省份的map
	 * @return
	 */
	public Map<Integer, String> getProvMap(){
		return RYFMapUtil.getProvMap();
	}
	
	/**
	 * 所有银行Map
	 * @return
	 */
	public TreeMap<Integer, String> getGatesMap() {
		return RYFMapUtil.getGateMap();
	}

	/**
	 * 获得所属行业的map
	 * @return
	 */
	public Map<Integer, String> getTradeTypeMap(){
		return RYFMapUtil.getMerTradeType();
	}
	/**
	 * 证件类型map
	 * @return
	 */
	public Map<String, String> getIdType(){
		return RYFMapUtil.getIdType();
	}
	
	/**
	 * 获取支付渠道map
	 */
	public Map<Integer,String> getGateRouteMap(){
		
		return RYFMapUtil.getGateRouteMap();
	}
	
	/**
	 * 同时获得GateMap、GateRoute的map
	 * @return list
	 */
	public List<Map<Integer,String>> getGateChannelMap(){
		List<Map<Integer,String>> mapList=new ArrayList<Map<Integer,String>>();
		mapList.add(RYFMapUtil.getGateMap());
		mapList.add(RYFMapUtil.getGateRouteMap());
		return mapList;
	}
	/**
	 * 同时获得出款银行的GateMap、GateRoute的map
	 * @return list
	 */
	public List<Map<Integer,String>> getGateChannelMap1(){
		List<Map<Integer,String>> mapList=new ArrayList<Map<Integer,String>>();
		mapList.add(RYFMapUtil.getGateMap1());
		mapList.add(RYFMapUtil.getGateRouteMap());
		return mapList;
	}
	/**
	 * 获得企业银行的map
	 * @return
	 */
	public Map<Integer,String> getCompanyBkMap(){
		return RYFMapUtil.getCompanyBkMap();
	}
	/***
	 * 根据交易类型获取对应的网关  支付渠道
	 * @return
	 */
	public List<Map<Integer, String>> getGateChannelMapByType(int type){
		List<Map<Integer,String>> mapList=new ArrayList<Map<Integer,String>>();
		mapList.add(RYFMapUtil.getGateMapByType(type));
		mapList.add(RYFMapUtil.getGateRouteMap());
		return mapList;
	}
	public List<Map<Integer, String>> getGateChannelMapByType1(Integer[] type){
		List<Map<Integer,String>> mapList=new ArrayList<Map<Integer,String>>();
		mapList.add(RYFMapUtil.getGateMapByType1(type));
		mapList.add(RYFMapUtil.getGateRouteMap());
		return mapList;
	}
	
	public List<Map<Integer, String>> getDFGateChannelMapByType(Integer[] type){
		List<Map<Integer,String>> mapList=new ArrayList<Map<Integer,String>>();
		mapList.add(RYFMapUtil.getGateMapByType1(type));
		mapList.add(RYFMapUtil.getDFGateRouteMap());
		return mapList;
	}
	public List<Map<Integer, String>> getDFGateChannelMapByType3(Integer[] type){
		List<Map<Integer,String>> mapList3=new ArrayList<Map<Integer,String>>();
		mapList3.add(RYFMapUtil.getGateMapByType3(type));
		mapList3.add(RYFMapUtil.getDFGateRouteMap());
		return mapList3;
	}
	
	public List<Map<Integer, String>> getGateMapByTypeAndName(Integer[] type,String gateName){
		List<Map<Integer,String>> mapList3=new ArrayList<Map<Integer,String>>();
		mapList3.add(RYFMapUtil.getGateMapByType3(type,gateName));
		mapList3.add(RYFMapUtil.getDFGateRouteMap());
		return mapList3;
	}
	
	public Map<Integer,String> getGRouteByName(String name){
		Map<Integer, String> gateRoutMap = new HashMap<Integer, String>();
		gateRoutMap = dao.queryGRouteByName(name);
	return gateRoutMap;
}
}