package com.rongyifu.mms.modules.sysmanage.service;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.modules.sysmanage.dao.YQGateDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

/***
 * 银企直连网关维护模块
 * @author admin
 *
 */
public class YQGateService {
	private YQGateDao dao=new YQGateDao();
	/***
	 * 银企直连网关维护-网关列表查询
	 * @param pageNo
	 * @param account
	 * @return currentPage<B2eGate>
	 */
	public CurrentPage<B2EGate> queryB2EGate(Integer pageNo,String account){
		CurrentPage<B2EGate> retPage=dao.queryB2EGate(pageNo, account);
		return retPage;
	}
	
	/***
	 * 银企直连网关维护-新增银企直连渠道
	 * pay和ryf_df项目有维护b2e_gate表的缓存 所以b2e_gate新增或修改数据时需刷新ets缓存
	 * @param B2eGate B
	 * @return
	 */
	public String addB2EGate(B2EGate b){
		LogUtil.printInfoLog("YQGateService", "addB2EGate", "新增gid:"+b.getGid());
		int affectLine=dao.addB2eGate(b);
		if(affectLine<=0){
			return "新增银企直连渠道失败";
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("t", "b2e_gate");
		String refreshStatus = EWPService.refreshEwpETS(params) ? "刷新缓存成功":"刷新缓存失败";
		return "新增银企直连渠道成功,"+refreshStatus;
	}
	
	/***
	 * 银企直连网关维护-修改渠道信息
	 * pay和ryf_df项目有维护b2e_gate表的缓存 所以b2e_gate新增或修改数据时需刷新ets缓存
	 * @param b
	 * @return
	 */
	public String editOneB2EGate(B2EGate b ){
		int affectLine=dao.editB2eGate(b);
		if(affectLine<=0){
			return "修改渠道信息失败";
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("t", "b2e_gate");
		String refreshStatus = EWPService.refreshEwpETS(params) ? "刷新缓存成功":"刷新缓存失败";
		return "修改渠道信息成功,"+refreshStatus;
	}
	
	/***
	 * 获取渠道信息
	 * @param gid
	 * @return
	 */
	public B2EGate getOneB2EGate(String gid){
		return dao.getOneB2EGate(gid);
	}
}
