package com.rongyifu.mms.modules.liqmanage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class PosSyncConfDao extends PubDao {
	
	public Map<String, String> queryConfigParams(){
		StringBuffer sql=new StringBuffer("select group_concat(concat(par_name,'|',par_value)) from global_params where par_name in ('TimeConf_ForPos','IsOpenConf_ForPos') and par_edit=9 ;");
		//result = "IsOpenConf_ForPos|1,TimeConf_ForPos|111100"
		String result=this.queryForString(sql.toString());
		String[] params=result.split(",");
		Map<String, String> m=new HashMap<String, String>();
		String[] param1=params[0].split("\\|");
		m.put(param1[0], param1[1]);
		String[] param2=params[1].split("\\|");
		m.put(param2[0], param2[1]);
		return m;
	}
	
	public void modifyConfigParams(Map<String, String> params){
		Set<String> keys=params.keySet();
		List<String> sqls=new ArrayList<String>();
		for (String key : keys) {
			sqls.add("update global_params set par_value="+Ryt.addQuotes(params.get(key))+" where par_name="+Ryt.addQuotes(key)+";");
		}
		String[] sqlBatch=sqls.toArray(new String[sqls.size()]);
		this.batchSqlTransaction(sqlBatch);
	}
	
}
