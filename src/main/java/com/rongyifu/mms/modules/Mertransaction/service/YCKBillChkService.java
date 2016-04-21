package com.rongyifu.mms.modules.Mertransaction.service;

import java.text.ParseException;
import java.util.List;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.modules.Mertransaction.dao.YCKBillChkDao;
import com.rongyifu.mms.utils.LogUtil;

public class YCKBillChkService {
		
	private YCKBillChkDao yckDao=new YCKBillChkDao();
	
	/**
	 * 获取基础平台对账数据
	 * @param bdate 开始日期
	 * @param edate 结束日期
	 * @return 
	 */
	public String getBillChkData(String bdate, String edate){
		int btwDays;
		try {
			btwDays = Ryt.daysBetween(bdate, edate);
		} catch (ParseException e) {
			LogUtil.printErrorLog("YCKBillChkService", "getBillChkData", "基础对账平台传输日期处理异常:"+e.getMessage());
			return handleXmlData(null,1,"日期传输错误");
		}
		if(btwDays>=7)return handleXmlData(null,1,"相隔日期不能大于7天");
		else return handleXmlData(yckDao.getBillChkData(bdate, edate,DataSyncUtil.getIntervalDate(bdate,1)),0,"查询成功");
	}
	
	/**
	 * 处理基础平台对账数据
	 * @param datas 数据
	 * @param flag 0表示成功，1表示失败
	 * @param msg 信息
	 * @return
	 */
	private String handleXmlData(List<Hlog> datas,int flag,String msg){
		String splitStr="";
		StringBuffer xml=new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("<res>");
		xml.append("<status>");
		xml.append("<value>");
		xml.append(flag);
		xml.append("</value>");
		xml.append("<msg>");
		xml.append(msg);
		xml.append("</msg>");
		xml.append("</status>");
		if(flag == 0){
			xml.append("<data>");
			xml.append("<record_num>");
			xml.append(datas.size());
			xml.append("</record_num>");
			xml.append("<record>");
			for (Hlog hlog : datas) {
				xml.append(splitStr);
				Integer sysTime=hlog.getSysTime();
				Integer sysDate=hlog.getSysDate();
				//记录处理
				xml.append(hlog.getP3()).append("|");
				xml.append(hlog.getMid()).append("|");
				xml.append(hlog.getOid().substring(0, 20)).append("|");
				xml.append(hlog.getAmount()).append("|");
				xml.append(hlog.getFeeAmt()).append("|");
				xml.append(sysTime>82800?DataSyncUtil.getIntervalDate(sysDate+"",-1):sysDate).append("|");
				xml.append(hlog.getType()).append("|");
				xml.append(hlog.getP10());
				splitStr=",";
			}
			xml.append("</record>");
			xml.append("</data>");
		}
		xml.append("</res>");
		return xml.toString();
	}
	
}
