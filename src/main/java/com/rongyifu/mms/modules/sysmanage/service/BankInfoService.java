package com.rongyifu.mms.modules.sysmanage.service;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.modules.sysmanage.dao.BankInfoDao;
import com.rongyifu.mms.utils.CurrentPage;

public class BankInfoService {
	
	private BankInfoDao dao = new BankInfoDao();
	
	public BankNoInfo queryById(Integer id){
		if(id == null){
			return null;
		}
		return dao.queryById(id);
	}
	
	public CurrentPage<BankNoInfo> queryForPage(BankNoInfo bni,Integer pageNo){
		bni.setType("01");//只查询type==01的联行号
		return dao.queryForPage(bni, pageNo);
	}
	
	public CurrentPage<BankNoInfo> queryForPage1(BankNoInfo bni,Integer pageNo,Integer pageSize){
		bni.setType("01");//只查询type==01的联行号
		return dao.queryForPage(bni, pageNo, pageSize);
	}
	
	public int update(BankNoInfo bni){
		if(bni ==null || bni.getId() == 0 ||(StringUtils.isBlank(bni.getBkName())&&StringUtils.isBlank(bni.getBkNo())&&StringUtils.isBlank(bni.getGid()))){
			return 0;
		}
		return dao.update(bni);
	}
	
	public int delById(Integer id){
		if(id == null){
			return 0;
		}
		return dao.delById(id);
	}
	
	public int add(BankNoInfo bni){
		if(StringUtils.isBlank(bni.getBkName())){
			return 0;
		}
		if(StringUtils.isBlank(bni.getBkNo())){
			return 0;
		}
		if(StringUtils.isBlank(bni.getGid())){
			return 0;
		}
		bni.setGid(bni.getGid().substring(2));
		bni.setType("01");
		return dao.add(bni);
	}
}
