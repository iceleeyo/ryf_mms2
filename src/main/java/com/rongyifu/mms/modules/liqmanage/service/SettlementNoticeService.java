package com.rongyifu.mms.modules.liqmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.MinfoNotice;
import com.rongyifu.mms.bean.MinfoNoticeSync;
import com.rongyifu.mms.bean.SettlementDetail;
import com.rongyifu.mms.bean.SettlementSum;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.liqmanage.dao.SettlementNoticeDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class SettlementNoticeService {
	SettlementNoticeDao settlementNoticeDao=new SettlementNoticeDao();
	/**
	 * 商户结算单通知配置列表
	 */
	
	public CurrentPage<MinfoNotice> getMinfoNotices(int pageNo, int beginDate, int endDate, String mid,String name){
		
		return settlementNoticeDao.getMinfoNotices(pageNo,new AppParam().getPageSize(), beginDate, endDate, mid,  name);
		
		
	}
	/**
	 * 新增同步商户信息
	 * @param minfoNotice
	 * @return
	 */
	public int addNotice(MinfoNotice minfoNotice){
		if(StringUtils.isBlank(minfoNotice.getMid())){
			return 0;
		}
		if(StringUtils.isBlank(minfoNotice.getName())){
			return 0;
		}
		if(StringUtils.isBlank(minfoNotice.getIp())){
			return 0;
		}
		
		return settlementNoticeDao.addNotice(minfoNotice);
	}
	
	/**
	 * 删除同步商户信息
	 * @param id
	 * @return
	 */
	public int delById(Integer id){
		if(id == null){
			return 0;
		}
		return settlementNoticeDao.delById(id);
	}
	
	public MinfoNotice queryById(Integer id){
		if(id == null){
			return null;
		}
		return settlementNoticeDao.queryById(id);
	}
	
	
	/**
	 * 更新同步信息
	 * @param minfoNotice
	 * @return
	 */
	public int update(MinfoNotice minfoNotice){
		if(minfoNotice ==null || minfoNotice.getId() == 0 ){
			return 0;
		}
		return settlementNoticeDao.update(minfoNotice);
	}
	
	public String getMinfoName(Integer mid){
		if(mid == null){
			return null;
		}
		return settlementNoticeDao.getMinfoName(mid);
	}
	
	
	
	/**
	 * 商戶結算單通知結果查詢
	 */
	public CurrentPage<MinfoNoticeSync> getNoticeSyncs(int pageNo, int beginDate, int endDate, String mid,String name,String syncState){
		
		return settlementNoticeDao.getNoticeSyncs(pageNo,new AppParam().getPageSize(), beginDate, endDate, mid,  name,syncState);
		
		
	}
/**
 * 结果订单同步
 * @param mNoticeList
 * @return
 */
	public String noticesSync(List<MinfoNoticeSync> mNoticeList) {
		LogUtil.printInfoLog("商户结算单同步开始");
		for (MinfoNoticeSync minfoNoticeSync : mNoticeList) {
			//查詢结算单汇总数据
			SettlementSum settlementSum = settlementNoticeDao.getSettlementSum(minfoNoticeSync.getBatch());
			
			//结算单明细数据
			List<SettlementDetail> list = settlementNoticeDao.getSettlementDetail(minfoNoticeSync.getBatch());
			JSONObject obj = new JSONObject();
			obj.put("settlementSum", settlementSum);
			obj.put("settlementDetail", list);
			JSONObject json=JSONObject.fromObject(obj);
			LogUtil.printInfoLog("报文"+json.toString());
			//获取要推送的url
			String ip = getNoticeUrl(minfoNoticeSync.getMid());
			//推送结算单
			// pushSettlement(json,ip,minfoNoticeSync.getBatch());
			 
			 try {
					 if(StringUtils.isEmpty(ip)){
						 settlementNoticeDao.updateNoticSync(minfoNoticeSync.getBatch(),"1","ip地址不存在");
					 }
					 else{
						 pushSettlementHandle(json,ip,minfoNoticeSync.getBatch());
						
					 }
				}catch (Exception e) {
					e.printStackTrace();
					LogUtil.printInfoLog(e.getMessage());
					
				}
		
		}
	
	return "同步完成";
	
	}
/**
 * 是否需要改変同步狀態  fale不需要更改  true需要更改
 * @param batch
 * @return
 */
	public boolean isCheckState(String batch) {
		MinfoNoticeSync mns = settlementNoticeDao.isCheckState(batch);
		if(mns.getSyncState().equals("0")){
			return false;
		}
		else{
			return true;
		}
	}
	
	//获取需要推送的ip
	public String getNoticeUrl(String mid){
		return settlementNoticeDao.getNoticeUrl(mid);
		
	}
	
	//推送结算单
	public String pushSettlementHandle(JSONObject json,String url,String batch){
		try {
			Map<String, Object> reqPramas = new HashMap<String, Object>();
			reqPramas.put("settlementData", json);
			LogUtil.printInfoLog("商户结算单同步开始");
			String resStr=Ryt.requestWithPost(reqPramas,url);
			if(resStr.equals("SUCCESS")){
				settlementNoticeDao.updateNoticSync(batch,"0","");
				LogUtil.printInfoLog("商户结算单同步成功");
			}
			else{
				//boolean b = isCheckState(batch);
				//if(b){
					settlementNoticeDao.updateNoticSync(batch,"1",resStr);
					LogUtil.printInfoLog("商户结算单同步失败"+resStr);
				//}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			LogUtil.printInfoLog(e.getMessage());
		}
		return null;
		
		
	}
	
	
	//结算确认推送结算单
	public void pushSettlement(List<FeeLiqBath> f){
		for (FeeLiqBath feeLiqBath : f) {
			String mid = feeLiqBath.getMid();
			boolean b = isPush(mid);//是否需要同步
			boolean badd = isAdd(feeLiqBath.getBatch());//是否需要新增计算单通知结果表
			if(b){
				if(badd){
					settlementNoticeDao.addNoticeSync(feeLiqBath);
				}
				//查詢结算单汇总数据
				SettlementSum settlementSum = settlementNoticeDao.getSettlementSum(feeLiqBath.getBatch());
				//结算单明细数据
				List<SettlementDetail> list = settlementNoticeDao.getSettlementDetail(feeLiqBath.getBatch());
				JSONObject obj = new JSONObject();
				obj.put("settlementSum", settlementSum);
				obj.put("settlementDetail", list);
				JSONObject json=JSONObject.fromObject(obj);
				//System.out.println(json.toString());
				LogUtil.printInfoLog("报文"+json.toString());
				//获取要推送的url
				String ip = getNoticeUrl(mid);
				 try {
					 	pushSettlementHandle(json,ip,feeLiqBath.getBatch());
						
					}catch (Exception e) {
					e.printStackTrace();
					LogUtil.printInfoLog(e.getMessage());
					
				}
			}
			
		}
	}
	/**
	 * 是否需要同步 fale不需要  true需要
	 * @param batch
	 * @return
	 */
	public boolean isPush(String mid) {
		MinfoNotice mn= settlementNoticeDao.queryByMid(mid);
		if(mn==null){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * 是否需要新增同步记录  fale不需要  true需要
	 * @param batch
	 * @return
	 */
	public boolean isAdd(String batch) {
		MinfoNoticeSync mns= settlementNoticeDao.isCheckState(batch);
		if(mns!=null){
			return false;
		}
		else{
			return true;
		}
	}
	
}



