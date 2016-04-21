package com.rongyifu.mms.api.bizobjs;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.utils.RYFMapUtil;

public class GateInfoBizObj implements BizObj  {

	@Override
	public Object doBiz(Map<String, String> map) throws Exception {
		String gateName = map.get("gateName");
		Map<Integer,String> gates=null;
		if(gateName == null || "".equals(gateName)){
			gates = RYFMapUtil.getGateMapByType3(new Integer[]{11});
		}
		else{
			 gates = RYFMapUtil.getGateMapByType3(new Integer[]{11},gateName);
		}
		
		JSONArray arr = new JSONArray();
		for (Entry<Integer,String> entry : gates.entrySet()) {
			JSONObject ele = new JSONObject();
			String key = String.valueOf(entry.getKey());
			ele.put(key.substring(key.length()-3, key.length()), entry.getValue());
			arr.add(ele);
		}
		return arr;
	}

}
