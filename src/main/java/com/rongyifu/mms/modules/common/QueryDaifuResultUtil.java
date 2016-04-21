package com.rongyifu.mms.modules.common;

import com.rongyifu.mms.bank.b2e.B2EProcess;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.ewp.CallPayPocessor;
import com.rongyifu.mms.modules.liqmanage.dao.QuerytransferDao;
import com.rongyifu.mms.utils.LogUtil;

/******
 * 调用代付查询（常规代付，自动代付，自动结算，接口代付结果）
 * @author shdy
 *
 */
public class QueryDaifuResultUtil {
	private QuerytransferDao dao=null;
	private B2EProcess b2eProcess=null;
	
	public QueryDaifuResultUtil(QuerytransferDao dao){
		this.dao=dao;
	}
	
	/****
	 * 发起代付查询交易
	 * @param o
	 * @return
	 */
	public String[]  reqSGSyncRes(OrderInfo o){
		try {
			TlogBean order=new TlogBean();
			order.setTseq(Long.parseLong(o.getTseq()));
			order.setOid(o.getOid());
			order.setMid(o.getMid());
			order.setMdate(o.getMdate());
			String[] result=CallPayPocessor.queryDfResult(order);
			
			return new String[]{result[1],result[0]};
		} catch (Exception e) {
			LogUtil.printErrorLog("QueryDaifuResultUtil", "reqSGSyncRes", "tseq:"+o.getTseq(), e);
			return new String[]{"同步结果异常","exception"};
		}
		
	}
	
	
	/****
	 * 	修改（手工同步代付结果或划款结果处理）  订单状态
	 * @param table
	 * @param mid
	 * @param tseq
	 * @param state
	 * @param errorMsg
	 * @param bk_seq
	 * @param bk_date
	 * @param bk_recvTime
	 * @return
	 */
	public String modifyTstatSql(String table,String mid,String tseq,Integer state,String errorMsg,String bk_seq,Integer bk_date,Integer bk_recvTime){
		StringBuffer sql=new StringBuffer("update ").append(table).append(" set tstat=").append(state).append("");
		if (state==PayState.SUCCESS){
			if(!Ryt.empty(bk_seq))sql.append(", bk_seq1=").append(bk_seq);
			if(bk_date !=null || bk_date !=0)sql.append(", bk_date=").append(bk_date);
			if(bk_recvTime != null || bk_recvTime !=0)sql.append(", bk_recv=").append(bk_recvTime);
		}else if(state == PayState.FAILURE || state ==PayState.REQ_FAILURE){
			if(!Ryt.empty(errorMsg))sql.append(" , error_msg=").append(Ryt.addQuotes(errorMsg));
		}
		sql.append(" where mid=").append(Ryt.addQuotes(mid)).append(" and tseq=").append(Ryt.addQuotes(tseq)).append(" and tstat !=").append(PayState.SUCCESS);
		return sql.toString();
	}
}
