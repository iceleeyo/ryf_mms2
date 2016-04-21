package com.rongyifu.mms.modules.mermanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.QkRisk;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.modules.mermanage.dao.MerRiskConDao;

public class MerRiskConService {
	private MerRiskConDao merriskcom=new MerRiskConDao();
	
	/**
	 * 获得单个商户对象
	 * @param mid
	 * @return
	 */
	public Minfo getOneMinfo(String mid) {
		//search_edit_info.jsp中调用
		Minfo m = null;
		List<QkRisk> qklist = null;
		try {
			m = merriskcom.getOneMinfo(mid);
			qklist = merriskcom.getOneMinfoQkRisk(mid);
			m.setQkrisks(qklist);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return m;
	}
	
	/**
	 * 商户风险信息增加或者修改
	 * @param minfo
	 * @return
	 */
	public String editMinfoFX(Minfo minfo,QkRisk qkrisk,QkRisk qkrisk1) {
		try {
			merriskcom.saveOperLog("商户风险信息修改", "商户为"+minfo.getId());
			merriskcom.editMinfoFX(minfo);
			//if(qkrisk.getCardType().equals("C")){
				QkRisk qk = merriskcom.getQkRisk(minfo.getId(),qkrisk.getCardType());
				if(qk!=null){
					merriskcom.editQkRisk(qkrisk);
				}
				else{
					merriskcom.addQkRisk(qkrisk);
				}
				
			//}
			//if(qkrisk1.getCardType().equals("D")){
				QkRisk qk1 = merriskcom.getQkRisk(minfo.getId(),qkrisk1.getCardType());
				if(qk1!=null){
					merriskcom.editQkRisk(qkrisk1);
				}
				else{
					merriskcom.addQkRisk(qkrisk1);
				}
				
			//}
			Map<String,Object> p = new HashMap<String,Object>();
			p.put("t","minfo");
			p.put("k", minfo.getId());
			return  EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "修改成功,刷新ETS失败";
		} catch (Exception e) {
			return "增加或修改失败";
		}
	}
	
	public QkRisk getQkRisko(String mid) {
		//search_edit_info.jsp中调用
		QkRisk qr = null;
		try {
			//qr = merriskcom.getOneMinfoQkRisk(mid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return qr;
	}
}
