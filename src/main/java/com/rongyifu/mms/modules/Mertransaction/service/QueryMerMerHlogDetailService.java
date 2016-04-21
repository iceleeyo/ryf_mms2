package com.rongyifu.mms.modules.Mertransaction.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.Mertransaction.dao.MerQueryHlogDetailDao;
import com.rongyifu.mms.modules.Mertransaction.dao.QueryMerMerTodayDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.transaction.ManualRequest;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryMerMerHlogDetailService {
	private MerQueryHlogDetailDao merQueryHlogDetailDao=new MerQueryHlogDetailDao();
	
	/**
	 * 根据条件查询交易明细
	 * @return CurrentPage
	 * @throws ParseException 
	 */
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer gid, String date, Integer bdate, Integer edate,Integer bkCheck,String begintrantAmt,String endtrantAmt){

	    return  merQueryHlogDetailDao.queryHlogDetail(pageNo, AppParam.getPageSize(), 
				mid, gate, tstat, type, oid, gid, date, bdate, edate,null,bkCheck,null, begintrantAmt, endtrantAmt);
		
		
	}
	
	/**
	 * 商户的交易明细下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadDetail_MER(String mid, Integer gate, Integer tstat, Integer type,
			String oid, Integer gateRouteId, String date, Integer bdate, Integer edate,String begintrantAmt,String endtrantAmt) throws Exception {
		  CurrentPage<OrderInfo> hlogListPage=merQueryHlogDetailDao.queryHlogDetail(1,-1, mid, gate, tstat, type, oid, gateRouteId, date, bdate, edate,null,null,null,
				  begintrantAmt, endtrantAmt);
		  List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		long totleAmount = 0;
		long totleFeeAmt = 0;
		list.add("序号,电银流水号,商户号,商户订单号,商户日期,交易金额(元),交易状态,交易类型,交易银行,系统手续费(元),系统日期".split(","));
		int i = 0;

		for (OrderInfo h : hlogList) {
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
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "交易明细表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}
	
	/**
	 * @return批量通知
	 */
	public String batchNotifyMerBkUrl(List<String> tseqList,String table){
		StringBuilder msg = new StringBuilder();
		try {
			for (String tseq : tseqList) {
				String rslt = notifyMerBkUrl(tseq, table);
				msg.append(tseq).append(":").append(rslt).append(",");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "请求失败!"+e.getMessage();
		}
		return msg.toString();
	}
	
	/**
	 * 获取明细查询和当天交易查询中手工发送请求的地址 通知商户后台
	 */
	public String notifyMerBkUrl(String tseq, String table) {
		QueryMerMerTodayDao dao = new QueryMerMerTodayDao();
		Hlog hlog = dao.queryHlogById(tseq, table);
		if (hlog.getBkUrl() == null || hlog.getBkUrl().trim().equals("")) return "请求失败!";
		
		 String msg = new ManualRequest().requestBkUrl(hlog);
		 if("请求成功!".equals(msg)){
			 dao.updateNotifyStatus("(" + tseq + ")",table,1);//1 已通知
		 }
		 return msg;
	}
	

}
