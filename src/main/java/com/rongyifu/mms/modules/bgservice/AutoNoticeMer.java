package com.rongyifu.mms.modules.bgservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.modules.bgdao.AutoNoticeMerDao;
import com.rongyifu.mms.utils.LogUtil;

/****
 * 商户通知   自动服务
 * @author
 *
 */
public class AutoNoticeMer {
	// 最大线程数
	private static final int MAX_THREAD_NUM = 1;
	
	// 下单15分钟后开始调用查询接口（单位：秒）
	private static final int START_QUERY_TIME = 900;
	// 启动线程间隔  单位：毫秒
	private static final int START_THREAD_INTERVAL = 500;
	
	private static final AutoNoticeMerDao dao=new AutoNoticeMerDao();
	
	public void run(){
		LogUtil.printInfoLog("AutoNoticeMer", "run", "start");
		List<TlogBean> tls=dao.queryOrderInfo(START_QUERY_TIME);
		LogUtil.printInfoLog("AutoNoticeMer", "run", "需发起商户通知的交易笔数:"+tls.size());
		Integer count=0;
		for (TlogBean tlogBean : tls) {
			NoticeThread notice=new NoticeThread(tlogBean);
			Thread thread=new Thread(notice);
			thread.start();
			count++;
			
			if(count != 0 && count % MAX_THREAD_NUM == 0){
				try {
					Thread.sleep(START_THREAD_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private class NoticeThread implements Runnable{
		private TlogBean t=null;
		
		
		public NoticeThread(TlogBean t) {
			super();
			this.t = t;
		}

		@Override
		public void run() {
			String ewp=Ryt.getEwpPath()+"ret/ret_url";
			
			LogUtil.printInfoLog("AutoNoticeMer", "thread_start", "tseq:"+t.getTseq());
			Map<String, String> requestParaMap=new HashMap<String, String>();
			requestParaMap.put("tseq", t.getTseq().toString());
			
			String result="";
			try {
				result=Ryt.requestByPostwithURL(requestParaMap, ewp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				requestParaMap.put("exceptin_msg", e.getMessage());
			}
			requestParaMap.put("ewp_url", ewp);
			requestParaMap.put("result", result);
			LogUtil.printInfoLog("AutoNoticeMer", "thread_end", "tseq:"+t.getTseq(),requestParaMap);
		}
		
	}
}


