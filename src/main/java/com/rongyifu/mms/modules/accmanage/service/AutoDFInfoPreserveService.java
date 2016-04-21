package com.rongyifu.mms.modules.accmanage.service;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.modules.accmanage.dao.AutoDFInfoPreserveDao;
import com.rongyifu.mms.service.MerchantService;
import com.rongyifu.mms.utils.CurrentPage;

public class AutoDFInfoPreserveService {
	
	
	private AutoDFInfoPreserveDao autodfInfo=new AutoDFInfoPreserveDao();
	
	/*
	 * 自动代付信息维护查询
	 */
	
	public CurrentPage<Minfo> queryAutoDf(Integer PageNo,String mid,Integer mstate){
		return autodfInfo.queryAutoDf(PageNo,mid,mstate);
		
	}
	
	/*
	 * 根据商户号查询出商户自动代付信息
	 */
	public Minfo queryByidAutoDf(String mid){
		return autodfInfo.queryByidAutoDf(mid);
		
	}
	
	/*
	 * 修改自动代付信息
	 * 
	 * 
	 */
	public String updateMerAutoDf(Minfo minfo,String mid){
		//search_edit_info.jsp中调用
		
		try {
			autodfInfo.saveOperLog("修改自动代付信息", "修改商户"+mid);
			minfo.setId(mid);
			autodfInfo.updateMerAutoDf(minfo);
		} catch (Exception e) {
			e.printStackTrace();
			return "修改失败！";
		}
		
		//商户信息同步至账户系统线程		
		MerchantService.syncMinfo(mid);
		
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "minfo");
		p.put("k", mid);
		return EWPService.refreshEwpETS(p) ? "修改成功!" : "资料修改成功，刷新ewp失败!";
		
	}
	
	//查询联行号
	
	public CurrentPage<BankNoInfo> queryBKNo(Integer PageNo,String gate,String bkname ){
		return autodfInfo.queryBKNo(PageNo,gate,bkname);
	}

}
