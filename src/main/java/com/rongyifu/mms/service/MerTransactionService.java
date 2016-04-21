package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.transaction.ManualRequest;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.Digit;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MerTransactionService {
	
	private TransactionDao transactionDao = new TransactionDao();

	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	@RemoteMethod
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String tseq, String oid, Integer gid, String bkseq) {
		return transactionDao.queryMerToday(pageNo,new AppParam().getPageSize(),mid,gate,tstat,type, tseq,oid,gid,bkseq,null);
	}
	
	/**
	 * 商户当天交易下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadToday_MER(String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gateRouteId, String bkseq) throws Exception {
		 CurrentPage<OrderInfo> tlogListPage= transactionDao.queryMerToday(1,-1,mid,gate,tstat,type, tseq,oid,gateRouteId,bkseq,null);
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
	
	/**
	 * 获取明细查询和当天交易查询中手工发送请求的地址 通知商户后台
	 */
	public String notifyMerBkUrl(String tseq, String table) {
		Hlog hlog = new TransactionDao().queryHlogById(tseq, table);
		if (hlog.getBkUrl() == null || hlog.getBkUrl().trim().equals("")) return "请求失败!";
		
		 return new ManualRequest().requestBkUrl(hlog);
		
		
	}
	
	
	/**
	 * 根据条件查询交易明细
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer gid, String date, Integer bdate, Integer edate,Integer bkCheck) {

	    return  transactionDao.queryHlogDetail(pageNo,new AppParam().getPageSize(), 
				mid, gate, tstat, type, oid, gid, date, bdate, edate,null,bkCheck,null);
		
		
	}
	
	/**
	 * 商户的交易明细下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadDetail_MER(String mid, Integer gate, Integer tstat, Integer type,
			String oid, Integer gateRouteId, String date, Integer bdate, Integer edate) throws Exception {
		  CurrentPage<OrderInfo> hlogListPage=transactionDao.queryHlogDetail(1,-1, mid, gate, tstat, type, oid, gateRouteId, date, bdate, edate,null,null,null);
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
	 * 单笔查询
	 * @param mid
	 * @param mdate
	 * @param oid商户订单号
	 * @return Hlog
	 */
	public OrderInfo queryHlogByMer(String mid, Integer mdate, String oid) {
		OrderInfo hlog = null;
		try {
			hlog = transactionDao.queryHlogByMer(mid, mdate, oid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hlog;
	}
	
	
	/**
	 * 查询历史单笔
	 * @param tseq电银流水
	 * @param mid 商户号
	 * @return
	 */
	public OrderInfo queryMerHlogByTseq(String tseq,String mid){
		return transactionDao.queryHlogByTseq("hlog",tseq,mid);
	}
	
	
	/**
	 * 商户原始日志
	 * @param mid oid bdate edate
	 * @return
	 */
	public CurrentPage<Hlog> queryMerHlog(Integer pageNo, String mid, String oid, Integer bdate, Integer edate) {

		return transactionDao.queryMerHlog(pageNo, mid, oid, bdate, edate,null);
	}
	
	/**
	 * 手机支付订单生成 mms 2.0
	 * @param merId
	 * @param ordId
	 * @param transAmt
	 * @param phoneNumber
	 * @param payTimePeriod
	 * @return
	 */
	public String doPhonePay(String mid, String transAmt, String phoneNo, String payPeriod, int operId,String userOid) {
		String msg = "";
		if(Ryt.empty(userOid)){
			return "订单号不能为空";
		}
		if (Ryt.empty(phoneNo)) {
			return "手机号不能为空";
		}
		if (Ryt.empty(phoneNo) || phoneNo.trim().length() != 11) {
			return "手机号格式不正确";
		}
		if (!(phoneNo.startsWith("13") || phoneNo.startsWith("15") || phoneNo.startsWith("18"))) {
			return "手机号格式不正确";
		}
		int limit = transactionDao.queryTransLimit(mid);
		long amt = 0;
		try {
			amt =Ryt.mul100toInt(transAmt);
		} catch (Exception e) {
			return "支付金额格式错误";
		}
		if (limit > 0 && amt > limit) {
			return "支付金额超出单笔交易限额!";
		}
//		String ordId = DateUtil.getNowDateTime();
		try {
			msg = transactionDao.doPhonePay(mid, userOid, amt, phoneNo, payPeriod, operId);
		} catch (Exception e) {
			e.printStackTrace();
			return "发送失败";
		}
		if (msg.toLowerCase().contains("success")) {
			return "ok";
		} else if(msg.equals("entryErr"))
			return "商户订单号重复，请核实";
		else {
			return "发送失败";
		}
	}
	
	/**
	 * 手机支付订单查询
	 * @return CurrentPage<Hlog>
	 */
	public CurrentPage<OrderInfo> queryPhonePay(Integer pageNo, String table, String mid, Integer tstat, Integer bdate,
					Integer edate, String operid) {
		return transactionDao.queryPhonePay(pageNo,new AppParam().getPageSize(), table, mid, tstat, bdate, edate, operid);
	}
	
	/**
	 * 手机支付下载
	 */
	public FileTransfer downloadPhonePay(String table, String mid, Integer tstat, Integer bdate,
			Integer edate, String operid) throws Exception {
		CurrentPage<OrderInfo> resultListPage=transactionDao.queryPhonePay(1,-1,table, mid, tstat, bdate, edate, operid);
		List<OrderInfo> mlogList=resultListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		Map<Integer, String> operMap = new MerInfoDao().getAllopersMap(mid);
		Map<Integer, String> tMap = AppParam.tlog_tstat;
		String[] title = { "序号 ", "电银流水号 ", "商户订单号", "交易金额(元) ", "系统日期", " 系统时间", " 交易状态", " 手机号", "支付时效(分钟)", "操作员" };
		list.add(title);

		int i = 0;
		String countAmount = "0";
		for (OrderInfo hlog : mlogList) {
			String pAccount = Ryt.div100(hlog.getAmount());
			String[] str = { String.valueOf(i + 1), String.valueOf(hlog.getTseq()), hlog.getOid(), pAccount,
					String.valueOf(hlog.getSysDate()), Ryt.getStringTime(hlog.getSysTime()),
					tMap.get(Integer.parseInt("" + hlog.getTstat())), hlog.getMobileNo(),
					String.valueOf(hlog.getTransPeriod()),
					hlog.getOperId() == null ? "" : operMap.get(hlog.getOperId()) };
			countAmount = Digit.add(countAmount, pAccount);
			i++;
			list.add(str);
		}
		String[] nullstr = { "", "", "", "", "", "", "", "", "", "" };
		list.add(nullstr);
		String[] str = { "总计:" + i + "条记录", "", "", countAmount, "", "", "", "", "", "" };
		list.add(str);

		String filename = "MERPHONEPAY_" + DateUtil.today() + ".xlsx";
		String name = "手机支付订单表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}
}
