package com.rongyifu.mms.modules.transaction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.modules.transaction.dao.QueryMerTodayDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryMerTodayService {
	private QueryMerTodayDao queryMerTodayDao = new QueryMerTodayDao();
	private PubDao dao = new PubDao();
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	@RemoteMethod
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String tseq, String oid, Integer gid, String bkseq,Integer mstate,String begintrantAmt,String endtrantAmt,String p15) {
		return queryMerTodayDao.queryMerToday(pageNo,new AppParam().getPageSize(),mid,gate,tstat,type, tseq,oid,gid,bkseq,mstate,begintrantAmt,endtrantAmt,p15);
	}
	
	
	// 当天交易下载
		public FileTransfer downloadToday(String mid, Integer gate, Integer tstat, Integer type,
				String tseq, String oid, Integer gateRouteId, String bkseq,Integer mstate,String begintrantAmt,String endtrantAmt,String p15)throws Exception {
			 CurrentPage<OrderInfo> tlogListPage= queryMerTodayDao.queryMerToday(1,-1,mid,gate,tstat,type, tseq,oid,gateRouteId,bkseq,mstate,begintrantAmt,endtrantAmt,p15);
			 List<OrderInfo> tlogList=tlogListPage.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			long totleAmount = 0;
			long totleFeeAmt = 0;
			long totleBankFee = 0;
			list.add("序号,电银流水号,商户号,商户订单号,商户日期,交易金额(元),交易状态,交易类型,交易银行,支付渠道,系统手续费(元),银行手续费(元),系统时间,银行流水号,失败原因,接入方式".split(","));
			int i = 0;
			for (OrderInfo h : tlogList) {
				String gateRoute = "";
				if (h.getGid() != null && !String.valueOf(h.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(h.getGid());
				}
				String[] str = { (i + 1) + "", h.getTseq() + "", h.getMid() + "", h.getOid(), h.getMdate() + "",
						Ryt.div100(h.getAmount()), AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
						AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), gates.get(h.getGate()), gateRoute,
						Ryt.div100(h.getFeeAmt()), Ryt.div100(h.getBankFee()),
						h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), h.getBk_seq1(), h.getError_msg(),h.getP15() };
				totleAmount += h.getAmount();
				totleFeeAmt += h.getFeeAmt();
				totleBankFee += h.getBankFee();
				i += 1;
				list.add(str);
			}
			String[] str = { "总计:" + i + "条记录", "", "", "", "", Ryt.div100(totleAmount) + "", "", "", "", "",
					Ryt.div100(totleFeeAmt) + "", Ryt.div100(totleBankFee) + "", "", "", "" };
			list.add(str);
			String filename = "MERTLOG_" + DateUtil.today() + ".xlsx";
			String name = "当天交易表";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
			
		}
		
		/**
		 * 当天交易详情查询
		 * @param tseq融易通流水号
		 * @param tableType 1为tlog 2为hlog 3为blog；
		 * @return hlog
		 */
		public OrderInfo queryLogByTseq(String tseq,int tableType) {
			String tableName="";
			if(tableType==1) tableName=Constant.TLOG;
			else if(tableType==2) tableName=Constant.HLOG;
			else tableName=Constant.BLOG;

			return queryMerTodayDao.queryHlogByTseq(tableName, tseq,null);
		}
		
		/*public Map<Integer,String> getGRouteByName(String name){
			Map<Integer, String> gateRoutMap = new HashMap<Integer, String>();
			gateRoutMap = dao.queryGRouteByName(name);
		return gateRoutMap;
	}*/


}
