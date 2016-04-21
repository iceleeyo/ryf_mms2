package com.rongyifu.mms.modules.Mertransaction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.Mertransaction.dao.QueryMerMerTodayDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.transaction.ManualRequest;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryMerMerTodayService {
	private QueryMerMerTodayDao querymermerdao=new QueryMerMerTodayDao();
	
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	@RemoteMethod
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String tseq, String oid, Integer gid, String bkseq,String begintrantAmt,String endtrantAmt) {
		return querymermerdao.queryMerToday(pageNo,new AppParam().getPageSize(),mid,gate,tstat,type, tseq,oid,gid,bkseq,null,begintrantAmt,endtrantAmt);
	}
	
	/**
	 * @param tseqList
	 * @param table
	 * @return
	 * 批量通知商户
	 */
	public String batchNotifyMerBkUrl(List<String> tseqList,String table){
		StringBuilder msg = new StringBuilder();
		List<String> successTesqs = new ArrayList<String>();
		try {
			for (String tseq : tseqList) {
				String rslt = notifyMerBkUrl(tseq, table);
				msg.append(rslt).append(",");
				if("请求成功!".equals(rslt)){
					successTesqs.add(tseq);
				 }
			}
			int size = successTesqs.size();
			if(size>0){
				StringBuilder inStr = new StringBuilder("(");
				for (int i=0;i<size;i++) {
					String tseq = successTesqs.get(i);
					inStr.append(Ryt.sql(tseq)).append(i==size-1?")":",");
				}
				querymermerdao.updateNotifyStatus(inStr.toString(),table,1);//已通知
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "请求失败";
		}
		return msg.toString();
	}
	/**
	 * 获取明细查询和当天交易查询中手工发送请求的地址 通知商户后台
	 */
	public String notifyMerBkUrl(String tseq, String table) {
		String msg="";
		Hlog hlog= null;
		try {
			hlog = querymermerdao.queryHlogById(tseq, table);
			if (hlog.getBkUrl() == null || hlog.getBkUrl().trim().equals("")) return hlog.getOid()+" 请求失败!";
			
			msg= hlog.getOid()+" "+new ManualRequest().requestBkUrl(hlog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg= hlog.getOid()+ " 请求失败";
		}
		return msg;
	}
	
	/**
	 * 商户当天交易下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadToday_MER(String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gateRouteId, String bkseq,String begintrantAmt,String endtrantAmt) throws Exception {
		 CurrentPage<OrderInfo> tlogListPage= querymermerdao.queryMerToday(1,-1,mid,gate,tstat,type, tseq,oid,gateRouteId,bkseq,null,begintrantAmt,endtrantAmt);
		 List<OrderInfo> tlogList=tlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		long totleAmount = 0;
		long totleFeeAmt = 0;
		list.add("序号,电银流水号,商户号,商户订单号,商户日期,交易金额(元),交易状态,交易类型,交易银行,系统手续费(元),系统日期".split(","));
		int i = 0;
		for (OrderInfo h : tlogList) {
			String[] str = { (i + 1) + "", h.getTseq() + "", h.getMid() + "", h.getOid(), h.getMdate() + "",
					Ryt.div100(h.getAmount()), AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), gates.get(h.getGate()),
					Ryt.div100(h.getFeeAmt()), h.getSysDate() + "" };
			totleAmount += h.getAmount();
			totleFeeAmt += h.getFeeAmt();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "", Ryt.div100(totleAmount) + "", "", "", "",
				Ryt.div100(totleFeeAmt) + "", "" };
		list.add(str);
		String filename = "MERTLOG_" + DateUtil.today() + ".xlsx";
		String name = "当天交易表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}

}
