package com.rongyifu.mms.quartz.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bank.b2e.GenB2ETrnid;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 退款经办/审核/联机退款结果同步给清算系统 
 *
 */
public class RefundSyncJob {
	/**
	 * 退款经办
	 */
	public static final String REFUND_TYPE_JB = "1";
	/**
	 * 退款审核
	 */
	public static final String REFUND_TYPE_SH = "2";
	/**
	 * 联机退款
	 */
	public static final String REFUND_TYPE_LJ = "4";
	
	/**
	 * 退款信息格式：商户号 + “_” + 退款单号 + "_" + 退款处理类型
	 * 退款处理类型：1:退款交易经办 2：退款交易审核 3：商户余额查询 4：系统自动退款
	 */
	private static final List<String> refundInfoList = new ArrayList<String>();
	
	private static final Map<String, Integer> syncFailMap = new HashMap<String, Integer>();
	
	public void execute() {
		if(!Ryt.isStartService("启动退款信息同步服务"))
			return;
		
		if(refundInfoList.size() > 0){
			String rand = GenB2ETrnid.getRandOid();
			LogUtil.printInfoLog(getClass().getCanonicalName(), "execute", "[start_" + rand + "] list size:"+refundInfoList.size());
			// 清算系统退款信息同步地址
			String url = ParamCache.getStrParamByName(Constant.GlobalParams.REFUND_SYNC_URL);
			// 清除列表
			List<String> list = new ArrayList<String>();
			for(String item : refundInfoList){
				try {
					String[] args = item.split("_");
					String mid = args[0];
					String refundId = args[1];
					String type = args[2];
				
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("mid", mid);
					paramMap.put("id", refundId);
					paramMap.put("hand_type", type);
					
					// 调用清算系统退款同步接口
					String resInfo = Ryt.requestWithPost(paramMap, url);
					// 解析返回结果
					Document doc = DocumentHelper.parseText(resInfo.trim());
					Element root = doc.getRootElement();
					String ifaceResult = root.elementTextTrim("iface_result");
					// 如果同步成功，则添加到清除列表中
					if("success".equalsIgnoreCase(ifaceResult))
						handleSyncSuccess(item, list);
					else {
						LogUtil.printInfoLog(getClass().getCanonicalName(), "execute", "sync fail: refundInfo="+item+" resInfo：\n"+resInfo);
						handleSyncFail(item, list);
					}
				} catch (DocumentException e) {
					LogUtil.printErrorLog(getClass().getCanonicalName(), "execute", "RefundInfo="+item, e);
				}
			}
			
			LogUtil.printInfoLog(getClass().getCanonicalName(), "execute", "[end_" + rand + "] sync result: "+ list.size() + "/" + refundInfoList.size() + " syncFailMap："+syncFailMap.size());
			
			// 清除已经同步过的退款信息
			clear(list);
		}
	}
	
	/**
	 * 添加同步任务
	 * @param mid
	 * @param refundId
	 * @param type
	 */
	public static void addJob(String mid, String refundId, String type){
		String info = getKey(mid, refundId, type);
		synchronized (refundInfoList) {
			refundInfoList.add(info);
		}
	}
	
	/**
	 * 清除任务
	 * @param list
	 */
	private void clear(List<String> list){
		if(list != null && list.size() > 0){
			synchronized (refundInfoList) {
				refundInfoList.removeAll(list);
				list.clear();
			}
		}
	}
	
	private static String getKey(String mid, String refundId, String type){
		return mid + "_" + refundId + "_" + type;
	}
	
	/**
	 * 处理同步成功
	 * @param refundInfo
	 * @param clearList
	 */
	private void handleSyncSuccess(String refundInfo, List<String> clearList){
		// 清除同步失败记录
		if(syncFailMap.containsKey(refundInfo))
			syncFailMap.remove(refundInfo);
		
		// 添加到任务清除列表中
		clearList.add(refundInfo);
	}
	
	/**
	 * 处理同步失败超过5次，则发报警邮件
	 * @param refundInfo
	 * @param clearList
	 */
	private void handleSyncFail(String refundInfo, List<String> clearList){
		if(syncFailMap.containsKey(refundInfo)){
			Integer failNum = syncFailMap.get(refundInfo) + 1; // 累计失败次数
			if(failNum >= 5){ // 失败超过5次，发报警邮件
				// 清除同步失败记录
				syncFailMap.remove(refundInfo);
				
				// 添加到任务清除列表中
				clearList.add(refundInfo);
				
				// 发报警邮件
				String content = "退款信息【" + refundInfo + "】5次同步失败，请人工干预处理！";
				sendWarnMail(content);
			} else // 未达到5次，则更新失败次数
				syncFailMap.put(refundInfo, failNum);
		} else 
			syncFailMap.put(refundInfo, 1);
	}
	
	/**
	 * 邮件报警
	 * @param content
	 * @return
	 */
	private boolean sendWarnMail(String content){
		String title = "退款信息同步报警-" + DateUtil.today();
    	try {
	    	return EWPService.sendMail(content, title, null);
    	} catch(Exception e){
    		LogUtil.printErrorLog(getClass().getCanonicalName(), "sendWarnMail", content, e);
    	}
    	
    	return false;
	}
}