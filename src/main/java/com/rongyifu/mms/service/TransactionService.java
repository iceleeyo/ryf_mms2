package com.rongyifu.mms.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import cmb.netpayment.Settle;

import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.transaction.ManualRequest;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.Digit;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;


@RemoteProxy
public class TransactionService {
	private TransactionDao transactionDao = new TransactionDao();
	private RowMapper<Hlog> rowMapper = new BeanPropertyRowMapper<Hlog>(Hlog.class);

	public RowMapper<Hlog> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<Hlog> rowMapper) {
		this.rowMapper = rowMapper;
	}
	static Map<String, String> orderState = new HashMap<String, String>();
	static {
		orderState.put("0", "已结帐");
		orderState.put("1", "已撤销");
		orderState.put("2", "部分结帐");
		orderState.put("3", "退款");
		orderState.put("4", "未结帐");
		orderState.put("5", "无效状态");
		orderState.put("6", "未知状态");
	}
	/**
	 * 调用招商银行接口查询单笔交易记录
	 * @param tseq RYT订单号
	 * @param sysDate RYT系统日期
	 * @param operId RYT在招商后台的操作员号
	 * @param password RYT在招商后台的操作员密码
	 * @return 查询结果
	 */
	public static String[] search(String tseq, String sysDate, String operId, String password) {
		// searchUmp.jsp中调用
		Settle settle = new Settle();
		String errMsg = "";
		int iRet = settle.SetOptions("netpay.cmbchina.com");
		if (iRet == 0) {// SetOptions ok
			iRet = settle.LoginC("0021", "000096" + operId, password);
			if (iRet == 0) {// LoginC ok;
				StringBuffer strbuf = new StringBuffer();// 接收查询出的数据
				iRet = settle.QuerySingleOrder(sysDate, wapOid(tseq), strbuf);
				if (iRet == 0) {// 查询成功
					if (strbuf.length() != 0) {// 查询的记录不为空再进行组装
						String[] tradeInfo = new String[5];
						tradeInfo[0] = tseq; // RYT流水号
						tradeInfo[1] = sysDate; // RYT系统日期
						String[] srcStr = strbuf.toString().split("\n");
						tradeInfo[2] = srcStr[1]; // 处理日期
						tradeInfo[3] = orderState.get(srcStr[2]); // 订单状态
						tradeInfo[4] = srcStr[3]; // 订单金额
						return tradeInfo;
					} else {
						errMsg = "没有要查询的记录!";
					}
				} else {
					errMsg = settle.GetLastErr(iRet);
				}
			} else {
				errMsg = settle.GetLastErr(iRet);
			}
		} else {
			errMsg = settle.GetLastErr(iRet);
		}
		settle.Logout();
		return new String[] { errMsg };
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
	 * 用于招行订单号格式转换(招行->RYT 去掉前面的'0' RYT->招行 保证10位,不足10位的前面补'0')
	 * @author cody 2010-6-4
	 * @param tseq 需要转换的订单号
	 * @return 转换后的订单号
	 */
	private static String wapOid(String tseq) {
		// 内部中调用
		if (tseq == null) {
			return "";
		}
		if (tseq.startsWith("0")) {
			return Integer.parseInt(tseq) + "";
		}
		return String.format("%010d", Integer.parseInt(tseq));
	}

	// 当天交易下载
	public FileTransfer downloadToday(String mid, Integer gate, Integer tstat, Integer type,
			String tseq, String oid, Integer gateRouteId, String bkseq,Integer mstate)throws Exception {
		 CurrentPage<OrderInfo> tlogListPage= transactionDao.queryMerToday(1,-1,mid,gate,tstat,type, tseq,oid,gateRouteId,bkseq,mstate);
		 List<OrderInfo> tlogList=tlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		long totleAmount = 0;
		long totleFeeAmt = 0;
		long totleBankFee = 0;
		list.add("序号,电银流水号,商户号,商户订单号,商户日期,交易金额(元),交易状态,交易类型,交易银行,支付渠道,系统手续费(元),银行手续费(元),系统时间,银行流水号,失败原因".split(","));
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
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), h.getBk_seq1(), h.getError_msg() };
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
	 * 掉单交易查询
	 * @return
	 */
	public CurrentPage<OrderInfo> queryNotifyFailedOrderRecord(Integer pageNo, String mid, String oid,Integer gate,Integer tstat, Integer type, String tseq, Double amount,Integer bdate, Integer edate){
		return transactionDao.queryNotifyFailedOrderRecord(pageNo,mid,oid,gate,tstat, type,tseq,amount,bdate,edate);
	}
	
	/**
	 * @return
	 * 掉单交易查询 下载xls
	 * @throws Exception 
	 */
	public FileTransfer downloadNotifyFailedOrderRecord(String mid, String oid,Integer gate,Integer tstat, Integer type, String tseq, Double amount,Integer bdate, Integer edate) throws Exception{
		List<OrderInfo> orderList= transactionDao.queryFailedRecordForDownload(mid, oid, gate, tstat, type, tseq, amount, bdate, edate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add("商户号,商户订单号,交易金额(元),交易状态,交易类型,交易银行,系统手续费(元),交易日期".split(","));
		DecimalFormat df = new DecimalFormat("#0.00");
		for (OrderInfo order : orderList) {
			String[] str = {order.getMid(),order.getOid(),df.format(order.getAmount()/100.0)+"",AppParam.tlog_tstat.get(order.getTstat()+0),AppParam.tlog_type.get(order.getType()+0),RYFMapUtil.getGateMap().get(order.getGate()),df.format(order.getFeeAmt()/100.0)+"",order.getSysDate()+"" };
			list.add(str);
		}
		String filename = "NotifyFailedOrder_" + DateUtil.today() + ".xls";
		String name = "掉单交易记录";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
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
		String filename = "MERTLOG_" + DateUtil.today() + ".xls";
		String name = "当天交易表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);

	}
	//历史明细查询下载
	public FileTransfer downloadDetail(String mid, Integer gate, Integer tstat, Integer type,
			String oid, Integer gateRouteId, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,Integer mstate) throws Exception {
	  CurrentPage<OrderInfo> hlogListPage=transactionDao.queryHlogDetail(1,-1, mid, gate, tstat, type, oid, gateRouteId, date, bdate, edate,tseq,bkCheck,mstate);
	  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		//Map<Integer, String> authorType = AppParam.authorType;
		long totleBankFee = 0;
		String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,商户号,商户简称,商户订单号,商户日期,交易金额(元),实际交易金额(元),交易状态,对账状态,交易类型,交易银行,支付渠道,系统手续费(元),银行手续费(元),系统时间,银行流水号,失败原因"
			.split(","));
		int i = 0;
		String gateRoute = "";
		long totlePayAmt = 0;
		for (OrderInfo h : hlogList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq() + "",
					h.getMid() + "",
					h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
					h.getOid(),
					h.getMdate() + "",
					Ryt.div100(h.getAmount()),
					Ryt.div100(h.getPayAmt()),
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					h.getBkChk() == null ? "" : (h.getBkChk() < 0 || h.getBkChk() > 3 ? "" : AppParam.check_state
						.get(Integer.parseInt(h.getBkChk() + ""))),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), gates.get(h.getGate()), gateRoute,
					Ryt.div100(h.getFeeAmt()), Ryt.div100(h.getBankFee()),
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), h.getBk_seq1(), h.getError_msg()};
			totleBankFee += h.getBankFee();
			totlePayAmt += h.getPayAmt();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "","" ,totleAmount, Ryt.div100(totlePayAmt),"", "", "", "", "",
				        totleSysAmtFee, Ryt.div100(totleBankFee) + "", "", "", "" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "交易明细表";
	   return new DownloadFile().downloadXLSXFileBase(list, filename, name);
	}
	/**
	 * 代付历史交易明细下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	
	public FileTransfer downloadpaymentDetail(String mid, Integer gate, Integer tstat,
			String oid, Integer gateRouteId, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,String bkseq,Integer mstate,Integer type,String batchNo)  {
		
	  CurrentPage<OrderInfo> hlogListPage=transactionDao.querypaymentHlogDetail(1,-1, mid, gate, tstat, oid, gateRouteId, date, bdate, edate,tseq,bkCheck,bkseq,mstate,type,batchNo);
	  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		//Map<Integer, String> authorType = AppParam.authorType;
		long totleBankFee = 0;
		String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,商户号,商户简称,账户号,商户订单号,批次号,交易金额(元),系统手续费(元),交易状态,交易类型,交易银行,对方银行行号,对方银行账户名,对方银行账号,支付渠道,订单时间,失败原因".split(","));
		
		int i = 0;
		String gateRoute = "";
		for (OrderInfo h : hlogList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq() + "",
					h.getMid() + "",
					h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
				    h.getMid()+"",
					h.getOid(),
					h.getP8()+"",
					Ryt.div100(-Math.abs(h.getAmount())),
					Ryt.div100(h.getFeeAmt()), 
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), 
					gates.get(h.getGate()), 
					// 对方银行行号p3
					h.getP3(),
					// 对方银行账户名 p2
					h.getP2(),
					// 对方银行账号 p1
					h.getP1(),
					gateRoute,
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), 
					h.getError_msg() };
			totleBankFee += h.getBankFee();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "","" , "",totleAmount, totleSysAmtFee,   "", "", "", "",
				       "","","","",""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "出款交易明细表";
		try {			
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 商户的交易明细
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
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "交易明细表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);

	}

	/**
	 * 手机支付下载
	 */
	public FileTransfer downloadPhonePay(String table,  String mid, Integer tstat, Integer bdate,
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

		String filename = "MERPHONEPAY_" + DateUtil.today() + ".xls";
		String name = "手机支付订单表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);

	}

	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	@RemoteMethod
	public CurrentPage<OrderInfo> queryMerToday(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String tseq, String oid, Integer gid, String bkseq,Integer mstate) {
		return transactionDao.queryMerToday(pageNo,new AppParam().getPageSize(),mid,gate,tstat,type, tseq,oid,gid,bkseq,mstate);
	}

	/**
	 * 根据条件查询交易明细
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,Integer mstate) {

		return transactionDao.queryHlogDetail(pageNo,new AppParam().getPageSize(), 
				mid, gate, tstat, type, oid, gid, date, bdate, edate,tseq,bkCheck,mstate);
		
	
	}
	/**
	 * 根据条件查询代付历史交易明细
	 * @return CurrentPage
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail(Integer pageNo, String mid, Integer gate, Integer tstat,
			String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,String bkseq,Integer mstate,int type,String batchNo){
				return transactionDao.querypaymentHlogDetail(pageNo,new AppParam().getPageSize(), 
						mid, gate, tstat, oid, gid, date, bdate, edate,tseq,bkCheck,bkseq,mstate,type,batchNo);
		
	}
	/**
	 * 根据条件查询失败交易备份，同上
	 * @return CurrentPage
	 */
	public CurrentPage<Hlog> queryBlogs(Integer pageNo, String mid, Integer gate, Integer tstat, Integer type,
					String oid, Integer authtype, String date, Integer bdate, Integer edate,String tseq,Integer mstate) {

		return transactionDao.queryBlogs(pageNo, mid, gate, tstat, type, oid, authtype, date, bdate, edate,tseq,mstate);
	}
	/**
	 * 历史单笔查询 按商户号
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
			Map<String, String> params = new HashMap<String, String>();
			params.put("mid", mid);
			params.put("mdate", String.valueOf(mdate));
			params.put("oid", oid);
			params.put("errorMsg", e.getMessage());
			LogUtil.printErrorLog("TransactionService", "queryHlogByMer(String mid, Integer mdate, String oid)", "", params);
		}
		return hlog;
	}
	/**
	 * 单笔查询 按商户号
	 * @param tseq融易通流水号
	 * @param tableType 1为tlog 2为hlog 3为blog；
	 * @return hlog
	 */
	public OrderInfo queryLogByTseq(String tseq,int tableType) {
		tseq = Ryt.sql(tseq);
		String tableName="";
		if(tableType==1) tableName=Constant.TLOG;
		else if(tableType==2) tableName=Constant.HLOG;
		else tableName=Constant.BLOG;

		return transactionDao.queryHlogByTseq(tableName, tseq,null);
	}
	/**
	 * 代付交易查询
	 * @param tseq融易通流水号
	 * @param tableType 1为tlog 2为hlog 3为blog；
	 * @return hlog
	 */
	public OrderInfo queryLogByTseq2(String tseq) {
		String tableName="tlog";
		OrderInfo list =new OrderInfo();
	   list=transactionDao.queryHlogByTseq(tableName, tseq,null);
	return  list;
	}
	
	public OrderInfo queryLogByBlogTseq(String tseq) {
		
		return transactionDao.queryHlogByBlogTseq("blog", tseq,null);
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
	 * 单笔查询 根据渠道和电银流水号查询
	 * @param tseq银行流水号
	 * @param bankGate
	 * @return Hlog
	 */
	public OrderInfo queryHlogByBkseq(Integer gateRouteId, String bkSeq) {
		return transactionDao.queryHlogByKeys(gateRouteId, bkSeq);
	}

	/**
	 * 商户原始日志
	 * @param mid oid bdate edate
	 * @return
	 */
	public CurrentPage<Hlog> queryMerHlog(Integer pageNo, String mid, String oid, Integer bdate, Integer edate,Integer mstate) {

		return transactionDao.queryMerHlog(pageNo, mid, oid, bdate, edate,mstate);
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
	public String doPhonePay(String mid, String transAmt, String phoneNo, String payPeriod, int operId) {
		String msg = "";
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
		int amt = 0;
		try {
			amt = Integer.parseInt(Ryt.mul100(transAmt));
		} catch (Exception e) {
			return "支付金额格式错误";
		}
		if (limit > 0 && amt > limit) {
			return "支付金额超出单笔交易限额!";
		}
		String ordId = DateUtil.getNowDateTime();
		try {
			msg = transactionDao.doPhonePay(mid, ordId, amt, phoneNo, payPeriod, operId);
		} catch (Exception e) {
			e.printStackTrace();
			return "发送失败";
		}
		if (msg.toLowerCase().contains("success")) {
			return "ok";
		} else {
			return "发送失败";
		}
	}

	/**
	 * 手机支付短息重发
	 * @param tseq
	 * @param mid
	 * @param ordId
	 * @param phoneNumber
	 * @return
	 */
	/*
	public String reSendPayUrl(String tseq, Integer mid, String ordId, String payPeriod, String phoneNo) {
		RYFMapUtil obj = RYFMapUtil.getInstance();
		String minfo = obj.getMerMap().get(mid);
		String chkNo = new Random().nextInt(999999) + "";
		String SMS_CON = Ryt.genPhonePaySMSContent(chkNo, minfo, ordId, payPeriod, tseq + "");
		String sendback = SMSUtil.send(phoneNo, SMS_CON);
		if (sendback.toLowerCase().contains("success")) {
			return "ok," + transactionDao.updateSysTime(tseq, chkNo);
		} else {
			return "发送失败!";
		}
	}
	*/

	/**
	 * 手机支付订单查询
	 * @return CurrentPage<Hlog>
	 */
	public CurrentPage<OrderInfo> queryPhonePay(Integer pageNo, String table, String mid, Integer tstat, Integer bdate,
					Integer edate, String operid) {
		return transactionDao.queryPhonePay(pageNo,new AppParam().getPageSize(), table, mid, tstat, bdate, edate, operid);
	}

	/**
	 * 手机支付 查询（信用卡支付）
	 * @param pageNo
	 * @param mid
	 * @param bdate
	 * @param edate
	 * @param tstat 交易状态
	 * @param tseq 电银流水号
	 * @param cardType 卡/证件的类型
	 * @param cardVal 卡/证的值
	 * @return
	 */
	public CurrentPage<OrderInfo> queryCreditCardResult(int pageNo, String mid, int bdate, int edate, Integer tstat, String tseq,
					int cardType, String cardVal, long payAmount,Integer mstate) {

		return transactionDao.queryMlogList(pageNo,new AppParam().getPageSize(), mid, bdate, edate, tstat, tseq, cardType, cardVal, payAmount,mstate);
	}

	//手机支付（信用卡支付）下载
	public FileTransfer downloadCreditCardPay(String mid, int bdate, int edate, Integer tstat, String tseq,
			int cardType, String cardVal, long payAmount,Integer mstate) throws Exception {
		 CurrentPage<OrderInfo> mlogListPage =transactionDao.queryMlogList(1,-1, mid, bdate, edate, tstat, tseq, cardType, cardVal, payAmount,mstate);
		 List<OrderInfo> mlogList=mlogListPage.getPageItems();
		   ArrayList<String[]> list = new ArrayList<String[]>();
			String[] title = {"序号","电银流水号","系统日期","商户号","商户简称","交易金额","交易状态","卡号","手机号","身份证号"};
			list.add(title);
			long countAmount=0;
			for (int i = 0; i < mlogList.size(); i++) {
				OrderInfo mlog=mlogList.get(i);
				String[] strArr={
						String.valueOf(i+1),
						String.valueOf(mlog.getTseq()),
						String.valueOf(mlog.getSysDate()),
						String.valueOf(mlog.getMid()),
						RYFMapUtil.getInstance().getMerMap().get(mlog.getMid()),
						Ryt.div100(mlog.getPayAmount()),
						AppParam.tlog_tstat.get((int)mlog.getTstat()),
						mlog.getPayCard(),
						mlog.getMobileNo(),
						mlog.getPayId()
					};
				countAmount+=mlog.getPayAmount();
				list.add(strArr);
			}
			String[] str = { "总计:" +mlogList.size() + "条记录", "", "", "", "",Ryt.div100(countAmount),"","",""};
			list.add(str);
			String filename = "CreditCardPay_" + DateUtil.today() + ".xlsx";
			String name = "信用卡支付";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
	}
	/**
	 * 数据分析查询（历史交易）
	 * @param pageNo
	 * @param mid
	 * @param bdate
	 * @param edate
	 * @param tstat
	 * @param merTradeType
	 * @return
	 */
	public CurrentPage<OrderInfo> hlogAnalysis(int pageNo, String mid, int bdate, int edate, Integer tstat, int merTradeType,Integer mstate) {
		return transactionDao.hlogAnalysis(pageNo, mid, bdate, edate, tstat, merTradeType,mstate);
	}

	/**
	 * 数据分析查询（退款）
	 * @param pageNo
	 * @param mid
	 * @param bdate
	 * @param edate
	 * @param tstat
	 * @param merTradeType
	 * @return
	 */
	public CurrentPage<RefundLog> refundAnalysis(int pageNo, String mid, int bdate, int edate, Integer state,
					int merTradeType,Integer mstate) {
		return transactionDao.refundAnalysis(pageNo, mid, bdate, edate, state, merTradeType, mstate);
	}
	
	public boolean checkUpdateChannelAuth(){
		String auth=transactionDao.getLoginUser().getAuth();
		return MenuService.hasThisAuth(auth, 99);
	}
	
	public 	String  updateGid(long tseq,int gid) {
		if(!checkUpdateChannelAuth()){
			return "对不起,你没有权限修改！";
		}
		int i = transactionDao.updateGid(tseq,gid);
		return i==1 ? "修改成功！" : "修改失败！";
	}

	/**
	 * 根据条件查询线下充值交易明细
	 * @return CurrentPage
	 * mid,tstat,date,bdate,edate,tseq,oid,mstate,rg_tstat
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail_cz(Integer pageNo, String mid,Integer tstat,
			String date, Integer bdate, Integer edate,String tseq,String oid,Integer mstate,int pstate){
				return transactionDao.querypaymentHlogDetail_cz(pageNo,new AppParam().getPageSize(), 
						mid,tstat,date,bdate,edate,tseq,oid,mstate,pstate	);
		
	}
	
	/**
	 * 代付线下充值交易明细下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	
	public FileTransfer downloadpaymentDetail_cz(String mid,Integer tstat,
			String date, Integer bdate, Integer edate,String tseq,String oid,Integer mstate,int pstate)  {
		
	  CurrentPage<OrderInfo> hlogListPage=transactionDao.querypaymentHlogDetail_cz(1,-1, mid,tstat,date,bdate,edate,tseq,oid,mstate,pstate);
	  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		//Map<Integer, String> authorType = AppParam.authorType;
		long totleBankFee = 0;
		String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		//String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,商户号,商户简称,账户号,商户订单号,交易金额(元),交易状态,人工操作状态,交易类型,订单时间"
			.split(","));
		int i = 0;
		String gateRoute = "";
		for (OrderInfo h : hlogList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq() + "",
					h.getMid() + "",
					h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
					h.getMid() + "",		
					h.getOid(),
					Ryt.div100(h.getAmount()),
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					AppParam.h_p_state.get(Integer.parseInt(h.getPstate()+"")),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")),					
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime())};
			totleBankFee += h.getBankFee();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "","", totleAmount, 
				         "" ,"","","" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "线下充值明细表";
		try {
			
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据条件查询代付历史交易明细
	 * @return CurrentPage
	 *  pageNo,gate,tstat,oid,gateRouteId,date,bdate,edate,tseq,bkCheck,bkseq,mstate,type,batchNo
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail_mer(Integer pageNo,  Integer gate, Integer tstat,
			String oid, Integer gid, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,String bkseq,Integer mstate,int type,String batchNo){
		String mid=null;
		try {
			mid=transactionDao.getLoginUserMid();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transactionDao.querypaymentHlogDetail(pageNo,new AppParam().getPageSize(), 
						mid, gate, tstat, oid, gid, date, bdate, edate,tseq,bkCheck,bkseq,mstate,type,batchNo);
		
	}
	
	
	/**
	 * 代付历史交易明细下载
	 * @param p
	 * @return
	 * @throws Exception
	 */
	
	public FileTransfer downloadpaymentDetail_mer(Integer gate, Integer tstat,
			String oid, Integer gateRouteId, String date, Integer bdate, Integer edate,String tseq,Integer bkCheck,String bkseq,Integer mstate,Integer type,String batchNo)  {
		String mid=null;
		try {
			mid=transactionDao.getLoginUserMid();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	   CurrentPage<OrderInfo> hlogListPage=transactionDao.querypaymentHlogDetail(1,-1, mid, gate, tstat, oid, gateRouteId, date, bdate, edate,tseq,bkCheck,bkseq,mstate,type,batchNo);
	  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		//Map<Integer, String> authorType = AppParam.authorType;
		long totleBankFee = 0;
		String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,商户号,商户简称,账户号,商户订单号,批次号,交易金额(元),系统手续费(元),交易状态,交易类型,交易银行,支付渠道,订单时间,失败原因"
			.split(","));
		int i = 0;
		String gateRoute = "";
		for (OrderInfo h : hlogList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq() + "",
					h.getMid() + "",
					h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
					h.getMid()+"",		
					h.getOid(),
					h.getP8()+"",
					Ryt.div100(h.getAmount()),
					Ryt.div100(h.getFeeAmt()), 
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), gates.get(h.getGate()), gateRoute,
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), h.getError_msg() };
			totleBankFee += h.getBankFee();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "","" , "",totleAmount, totleSysAmtFee, "", "", "", "",
				       "",""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "出款交易明细表";
		try {
			
			return new DownloadFile().downloadXLSFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据条件查询线下充值交易明细
	 * 
	 * @return CurrentPage mid,tstat,date,bdate,edate,tseq,oid,mstate,rg_tstat
	 */
	public CurrentPage<OrderInfo> querypaymentHlogDetail_cz_mer(Integer pageNo,
			Integer tstat, String date, Integer bdate, Integer edate,
			String tseq, String oid, Integer mstate, int pstate) {
		String mid = null;
		try {
			mid = transactionDao.getLoginUserMid();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transactionDao.querypaymentHlogDetail_cz(pageNo,
				new AppParam().getPageSize(), mid, tstat, date, bdate, edate,
				tseq, oid, mstate, pstate);

	}
	
	/**
	 * 代付线下充值交易明细下载  mer
	 * @param p
	 * @return
	 * @throws Exception
	 */
	
	public FileTransfer downloadpaymentDetail_cz_mer(Integer tstat,
			String date, Integer bdate, Integer edate,String tseq,String oid,Integer mstate,int pstate)  {
		String mid = null;
		try {
			mid = transactionDao.getLoginUserMid();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	  CurrentPage<OrderInfo> hlogListPage=transactionDao.querypaymentHlogDetail_cz(1,-1, mid,tstat,date,bdate,edate,tseq,oid,mstate,pstate);
	  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		//Map<Integer, String> authorType = AppParam.authorType;
		long totleBankFee = 0;
		String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		//String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,商户号,商户简称,账户号,商户订单号,交易金额(元),交易状态,人工操作状态,交易类型,订单时间"
			.split(","));
		int i = 0;
		String gateRoute = "";
		for (OrderInfo h : hlogList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq() + "",
					h.getMid() + "",
					h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
				    h.getMid()+"",
					h.getOid(),
					Ryt.div100(h.getAmount()),
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					AppParam.h_p_state.get(Integer.parseInt(h.getPstate()+"")),
					AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")),					
					h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime())};
			totleBankFee += h.getBankFee();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "","", "", "", totleAmount, 
				       "" ,"","","" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "线下充值明细表";
		try {
			
			return new DownloadFile().downloadXLSFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
