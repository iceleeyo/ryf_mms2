package com.rongyifu.mms.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.db.PubDao;
/**
 * 
 * @author cody 2010-5-26
 *
 */
public class ParamCache implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
//	private static JdbcTemplate jt = new JdbcConn().getJdbcTemplate();
	
	private static Map<String, String> m_param_maps;
	private static ParamCache m_obj_ParamCache;
	private static List<Minfo> minfo_list;
	
	public static List<Minfo> getMinfo_list() {
		return minfo_list;
	}

	private ParamCache() {
		try {
			if (m_obj_ParamCache == null){ queryGolbalParams();queryminfo();}
		} catch (Exception e) {
			e.printStackTrace();
			m_obj_ParamCache = null;
		}
	}
	
	
	
	
	/**
	 * 获得缓存的实例
	 * @return 字典缓存的实例
	 */
	public static synchronized ParamCache getInstance() {
		if (m_obj_ParamCache == null) {
			m_obj_ParamCache = new ParamCache();
		}
		return m_obj_ParamCache;
	}
	/**
	 * 查询全局变量表
	 */
	@SuppressWarnings("rawtypes")
	private static synchronized void queryGolbalParams() {
		String sql = "select par_name, par_value from global_params ";
		m_param_maps = new HashMap<String, String>();
		PubDao dao = new PubDao();
		List list = dao.queryForList(sql);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map m = (Map) it.next();
			if (m.get("par_name") != null && m.get("par_value") != null) {
				m_param_maps.put(m.get("par_name").toString(), m.get("par_value").toString().trim());
			}
		}
	}
	
	/**
	 * 根据名字查询全局变量值
	 * @param name  全局变量名
	 * @return 返回变量值
	 */
	public static String getStrParamByName(String name) {
		ParamCache.getInstance();
		String paramValue = ParamCache.m_param_maps.get(name);
		if(Ryt.empty(paramValue)){
			refreshGolbalParams();
			paramValue = ParamCache.m_param_maps.get(name);
		}
		return paramValue;
	}
	
	public static int getIntParamByName(String name) {
		
		try {
		
		return Integer.parseInt(getStrParamByName(name));
		
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 刷新存放全局变量的MAP
	 */
	public synchronized static void refreshGolbalParams() {
		queryGolbalParams();
	}
	
	/****
	 * 查询minfos
	 */
	@SuppressWarnings("rawtypes")
	public synchronized static void queryminfo(){
		String sql="select id,df_config,dk_config,multi_process_day,multi_process_time from minfo ";
		minfo_list = new ArrayList<Minfo>();
		PubDao dao = new PubDao();
		List list = dao.queryForList(sql);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map m = (Map) it.next();
			Minfo minfo=new Minfo();
//			id,exp_date,mstate,trans_limit,mer_chk_flag,public_key,abbrev,refund_flag,acc_succ_count,acc_fail_count,
//            phone_succ_count,phone_fail_count,id_succ_count,id_fail_count,balance,liq_type,bank_acct_name, category,
//            mer_type,liq_obj,cert_path,df_config,dk_config,multi_process_day,multi_process_time
			minfo.setId(m.get("id").toString());
			minfo.setDfConfig(m.get("df_config").toString());
			minfo.setDkConfig(m.get("dk_config").toString());
			minfo.setMultiProcessDay(m.get("multi_process_day").toString());
			minfo.setMultiProcessTime(m.get("multi_process_time").toString());
			minfo_list.add(minfo);
			
		}
	}
	
	/**
	 * 刷新存放全局变量的MAP
	 */
	public synchronized static void refreshMinfo() {
		queryminfo();
	}
	
}
