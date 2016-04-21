package com.rongyifu.mms.modules.accmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.modules.accmanage.dao.DfSbBjPzDao;
import com.rongyifu.mms.utils.CurrentPage;

/****
 * 代付报警配置service
 * @author shdy
 *
 */
public class DfSbBjPzService {
	private DfSbBjPzDao dao=new DfSbBjPzDao();
	/**
	 * 查询代付风险管理信息
	 * @param gid
	 * @return
	 */
	public CurrentPage<B2EGate> queryDFBankInfo(String gid,int pageNo){
		return dao.queryDFBankInfo(gid,pageNo);
	}

	/****
	 * 修改B2eGate配置
	 * @param gid
	 * @param fCount
	 * @param sucRate
	 * @return
	 */
	public boolean updateB2eGateConfig(Integer gid,Integer fCount,Integer sucRate){
		boolean result=false;
		if(dao.updateB2eGateConfig(gid,fCount,sucRate)!=0){
			result=true;
		}
		return result;
	}
	
	/**
	 * 代付银行余额报警初始化
	 * @return
	 */
	public Map<String, String> initDFYEBJ(){
		Map<String, String> map=new HashMap<String, String>();
		List<B2EGate> date= dao.initDFYEBJ();
		for (B2EGate b2eGate : date) {
			map.put(b2eGate.getGid()+"", b2eGate.getName());
		}
		return map;
	}
}
