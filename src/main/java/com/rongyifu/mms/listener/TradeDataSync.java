package com.rongyifu.mms.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.db.datasource.CustomerContextHolder;
import com.rongyifu.mms.utils.JsonUtil;
import com.rongyifu.mms.utils.LogUtil;

public class TradeDataSync implements ServletContextListener{
	
	private TradeDataSyncThread myThread;  
	  
    public void contextDestroyed(ServletContextEvent e) {
        if (myThread != null) {  
            myThread.interrupt();
        }  
    }
  
    public void contextInitialized(ServletContextEvent e) { 
    	if (myThread == null) {
            myThread = new TradeDataSyncThread();
            myThread.start();
        }
    }
    
}  
  
class TradeDataSyncThread extends Thread {
	
	@SuppressWarnings("rawtypes")
	private static PubDao dao = new PubDao();
	
	private static final String SYNC_TYPE_ORDER = "1";
	private static final String SYNC_TYPE_RESULT = "2";
	private static final String RET_SUCCESS = "000";
	
	public void run() {
		long time = 0L;
		long interval = 5 * 60 * 1000; // 5分钟：检查服务/延迟同步
		
		while (!this.isInterrupted()) {// 线程未中断执行循环
			try {
				Date date =  new Date();
				if(time == 0 || date.getTime() - time >= interval){ // 每隔5分钟检查服务的有效性
					time = date.getTime();
					if(!Ryt.isStartService("检查交易数据实时同步服务")){
						Thread.sleep(interval);
						continue;
					}
				}
				
				if(Ryt.empty(getSyncUrl())){ // 同步地址为空，则延迟5分钟再同步
					Thread.sleep(interval);
					continue;
				}
				
				List<Map<String, Object>> list = getOrderInfo();
				if(list != null && list.size() > 0){
					String ids = null;
					for(Map<String, Object> item : list){
						String syncId = String.valueOf(item.get("sync_id"));
						String syncType = String.valueOf(item.get("sync_type"));
						
						boolean retFlag = false;
						if(SYNC_TYPE_ORDER.equals(syncType)){ // 同步订单
							retFlag = syncOrder(item);
						} else if (SYNC_TYPE_RESULT.equals(syncType)){ // 同步支付结果
							retFlag = syncPayResult(item);
						}
						
						// 同步成功，则记录syncId
						if(retFlag)
							ids = setIds(ids, syncId);
					}
					
					// 清除数据
					clearData(ids);
					list.clear();
				} else 
					Thread.sleep(10000); // 如果没数据，等待10秒钟再轮循
				
			} catch (Exception e) {
				LogUtil.printErrorLog(e.getMessage(), e);
			}

		}
	}
    
	/**
	 * 同步订单信息
	 * @param orderInfo
	 * @return
	 */
    private boolean syncOrder(Map<String, Object> orderInfo){
    	// 清除不需要的信息
    	orderInfo.remove("sync_id");
    	orderInfo.remove("sync_type");
    	orderInfo.remove("bk_flag");
    	orderInfo.remove("bk_date");
    	orderInfo.remove("bk_seq1");
    	orderInfo.remove("bk_seq2");
    	orderInfo.remove("bk_resp");
    	orderInfo.remove("error_msg");
    	
    	// 添加协议参数
    	orderInfo.put("tranCode", "ZH0018");
    	orderInfo.put("sys_type", "1");
    	
    	String url = getSyncUrl() + "backstagemamage/reciveTradeDataDzfService";
    	LogUtil.printInfoLog("[request] syncOrder: " + url, orderInfo);
    	String resInfo = Ryt.requestWithPost(orderInfo, url);
    	LogUtil.printInfoLog("[response] syncOrder: " + resInfo);
    	
    	Map<String, Object> retParams = JsonUtil.getMap4Json(resInfo);
    	String resCode = String.valueOf(retParams.get("resCode"));
    	return RET_SUCCESS.equals(resCode);
    }
    
    /**
     * 同步支付结果
     * @param orderInfo
     * @return
     */
    private boolean syncPayResult(Map<String, Object> orderInfo){
    	Map<String, Object> requestParams = new HashMap<String, Object>();
    	requestParams.put("version", "10");
    	requestParams.put("tranCode", "ZH0019");
    	requestParams.put("sys_type", "1");
    	requestParams.put("tseq", orderInfo.get("tseq"));
    	requestParams.put("gid", orderInfo.get("gid"));
    	requestParams.put("oid", orderInfo.get("oid"));
    	requestParams.put("mid", orderInfo.get("mid"));
    	requestParams.put("mdate", orderInfo.get("mdate"));
    	requestParams.put("tstat", orderInfo.get("tstat"));
    	requestParams.put("bk_flag", orderInfo.get("bk_flag"));
    	requestParams.put("bk_date", orderInfo.get("bk_date"));
    	requestParams.put("bk_seq1", orderInfo.get("bk_seq1"));
    	requestParams.put("bk_seq2", orderInfo.get("bk_seq2"));
    	requestParams.put("bk_resp", orderInfo.get("bk_resp"));
    	requestParams.put("error_msg", orderInfo.get("error_msg"));
    	
    	String url = getSyncUrl() + "backstagemamage/reciveTradeDataOkService";
    	LogUtil.printInfoLog("[request] syncPayResult: " + url, requestParams);
    	String resInfo = Ryt.requestWithPost(requestParams, url);
    	LogUtil.printInfoLog("[response] syncPayResult: " + resInfo);
    	
    	Map<String, Object> retParams = JsonUtil.getMap4Json(resInfo);
    	String resCode = String.valueOf(retParams.get("resCode"));
    	return RET_SUCCESS.equals(resCode);
    }
    
    /**
     * 记录已同步成功的订单
     * @param ids
     * @param syncId
     */
    private String setIds(String ids, String syncId){
    	if(Ryt.empty(ids))
			ids = syncId;
		else 
			ids += "," + syncId;
    	
    	return ids;
    }
    
    /**
     * 查询订单信息
     * @return
     */
	private List<Map<String, Object>> getOrderInfo() {
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id            sync_id,");
		sql.append("       a.type          sync_type,");
		sql.append("       b.tseq,");
		sql.append("       b.version,");
		sql.append("       b.mdate,");
		sql.append("       b.mid,");
		sql.append("       ifnull(b.bid, '') bid,");
		sql.append("       b.oid,");
		sql.append("       b.amount,");
		sql.append("       b.type,");
		sql.append("       b.gate,");
		sql.append("       ifnull(b.author_type, 0) author_type,");
		sql.append("       b.sys_date,");
		sql.append("       b.init_sys_date,");
		sql.append("       b.sys_time,");
		sql.append("       b.fee_amt,");
		sql.append("       b.bank_fee,");
		sql.append("       b.tstat,");
		sql.append("       ifnull(b.mobile_no, '') mobile_no,");
		sql.append("       ifnull(b.phone_no, '') phone_no,");
		sql.append("       ifnull(b.card_no, '') card_no,");
		sql.append("       b.gid,");
		sql.append("       b.pre_amt,");
		sql.append("       b.pre_amt1,");
		sql.append("       b.bk_fee_model,");
		sql.append("       b.pay_amt,");
		sql.append("       b.currency,");
		sql.append("       b.exchange_rate,");
		sql.append("       b.bk_flag,");
		sql.append("       b.bk_date,");
		sql.append("       ifnull(b.bk_seq1, '') bk_seq1,");
		sql.append("       ifnull(b.bk_seq2, '') bk_seq2,");
		sql.append("       ifnull(b.error_code, '') bk_resp,");
		sql.append("       ifnull(b.error_msg, '') error_msg,");
		sql.append("       ifnull(c.out_user_id, '') out_user_id,");
		sql.append("       ifnull(c.in_user_id, '') in_user_id,");
		sql.append("       ifnull(c.bind_mid, '') bind_mid");
		sql.append("  from transaction_sync a");
		sql.append("  join tlog b");
		sql.append("    on a.tseq = b.tseq");
		sql.append("  left join order_ext c");
		sql.append("    on a.tseq = c.tseq");
		sql.append(" order by a.id");
		
		JdbcTemplate myjt = dao.getJdbcTemplate();
		CustomerContextHolder.setSlave();
		return myjt.queryForList(sql.toString());
	}
	
	/**
	 * 清除已同步成功的数据
	 * @param ids
	 */
	private void clearData(String ids) {
		if (!Ryt.empty(ids)) {
			String sql = "delete from transaction_sync where id in(" + ids + ")";
			dao.update(sql);
		}
	}
	
	/**
	 * 同步接口URL
	 * @return
	 */
	private String getSyncUrl(){
		return ParamCache.getStrParamByName("QSXT_URL");
	}
}
