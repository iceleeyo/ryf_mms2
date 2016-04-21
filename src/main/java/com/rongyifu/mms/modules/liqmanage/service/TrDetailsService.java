package com.rongyifu.mms.modules.liqmanage.service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bank.b2e.SjBankMXCX;
import com.rongyifu.mms.bean.SettleResultBean;
import com.rongyifu.mms.bean.TrDetails;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.liqmanage.dao.TrDetailsDao;
import com.rongyifu.mms.quartz.jobs.DownloadSJMXJob;
import com.rongyifu.mms.quartz.jobs.utils.SJTransDetailParser;
import com.rongyifu.mms.service.DoSettlementService;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.settlement.SBean;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class TrDetailsService {

	private TrDetailsDao dao = new TrDetailsDao();
	
	
	public int deleteByDateAndGid(Integer date,Integer gid){
		return dao.deleteByDateAndGid(date, gid);
	}
	
	public String downLoadSJMX(String dateStr){
		String msg = "下载失败";
		try {
			//验证日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse(dateStr);
			int dateInt = Integer.valueOf(sdf.format(date));
			//查询昨天的盛京交易明细
			List<String> lst = new SjBankMXCX(dateInt,dateInt).querySjBankDetail();
			//解析
			List<TrDetails> list = SJTransDetailParser.parse(lst);
			//存库
			if(CollectionUtils.isNotEmpty(list)){
				dao.saveTrDetails(list);
			}
			
			//自动对账
//			checkBankData("40006", list);
			
			msg = "批量下载盛京明细成功，日期："+dateStr+",下载明细 "+(list==null?"0":list.size())+" 条。";
		} catch (Exception e) {
			msg = "批量下载盛京明细失败,日期："+dateStr+",原因："+e.getMessage();
		}
		LogUtil.printInfoLog(DownloadSJMXJob.class.getName(), "execute", msg);
		return msg;
	}
	
	private List<SBean> getCheckData(String bank, List<TrDetails> list) throws Exception {
		List<SBean> sbeanList = new ArrayList<SBean>();	
		for (TrDetails trd : list) {
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setBkSeq(trd.getBkSerialNo());//银行流水号
			bean.setAmt(trd.getAmt()/100.0+"");
			bean.setBkFee(trd.getFeeAmt()/100.0+"");//订单手续费
			bean.setDate(trd.getTrDate()+"");
			sbeanList.add(bean);
		}
		return sbeanList;
	}
	
	private void checkBankData(String bank, List<TrDetails> list) throws Exception {
		List<SBean> dataList = getCheckData(bank, list);
		SettleResultBean bean = new SettleResultBean();
		try {
			new DoSettlementService().checkBankData( dataList, bean);
			Map<String,String> params = new HashMap<String,String>();
			params.put("total", dataList.size()+"");
			params.put("suspect", CollectionUtils.isEmpty(bean.getSuspect())?"0":bean.getSuspect().size()+"");
			params.put("success", CollectionUtils.isEmpty(bean.getSuccess())?"0":bean.getSuccess().size()+"");
			params.put("fail", CollectionUtils.isEmpty(bean.getFail())?"0":bean.getFail().size()+"");
			params.put("exception", bean.getException()+"");
			params.put("finish", bean.getFinish()+"");
			params.put("total", dataList.size()+"");
			LogUtil.printInfoLog(this.getClass().getName(), "checkBankData", "自动对账", params);
		} catch (Exception e) {
			LogUtil.printErrorLog(this.getClass().getName(), "checkBankData", e.getMessage(), e);
		}
	}
	
	public CurrentPage<TrDetails> query(Integer pageNo,Integer gid,Integer jd,Integer bdate,Integer edate){
		if(bdate == null || edate == null){
			return null;
		}else{
			return dao.query(pageNo,new AppParam().getPageSize(), gid, jd, bdate, edate);
		}
	}
	
	public FileTransfer download(Integer gid,Integer jd,Integer bdate,Integer edate) throws Exception{
			CurrentPage<TrDetails> page = dao.query(1,-1, gid, jd, bdate, edate);
			List<TrDetails> trList = page.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			list.add("序号,交易日期,交易时间,收入,支出,手续费总额,对方帐号,对方名称,账户余额,交易行所,摘要,流水号,备注".split(","));
			int i = 1;
			try {
				for (TrDetails t : trList) {
					list.add(new String[]{(i++)+"",formatDate(t.getTrDate().toString()),formatTime(t.getTrTime()),Ryt.div100(t.getRcvamt()),
										  Ryt.div100(t.getPayamt()),Ryt.div100(t.getFeeAmt()),t.getOppAcno(),t.getOppAcname(),
										  Ryt.div100(t.getBalance()),t.getBankName(),t.getSummary(),t.getBkSerialNo(),t.getPostscript()
										});
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			String rcvamt = Ryt.div100(Long.valueOf(page.getSumResult().get("rcvamt").toString()));
			String payamt = Ryt.div100(Long.valueOf(page.getSumResult().get("payamt").toString()));
			String feeamt = Ryt.div100(Long.valueOf(page.getSumResult().get("feeAmt").toString()));
			String[] str = { "总计:"+(i-1)+"条记录", "","",rcvamt,payamt,feeamt,"","","","","","",""};
			list.add(str);
			String filename = "TRDETAILS_" + DateUtil.today() + ".xls";
			String name = "资金明细表";
			return new DownloadFile().downloadXLSFileBase(list, filename, name);
	}
	private String formatDate(String date) throws ParseException{
		if(StringUtils.isEmpty(date)||date.length() != 8){
			return "";
		}
		StringBuilder sbr = new StringBuilder(date);
		sbr.insert(4, '-');
		sbr.insert(7, '-');
		return sbr.toString();
	}
	private String formatTime(Integer time){
		if(null == time){
			return "";
		}
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(6);
		nf.setGroupingUsed(false);
		String s = nf.format(time);
		StringBuilder sbr = new StringBuilder(s);
		sbr.insert(2, ':');
		sbr.insert(5, ':');
		return sbr.toString();
	}
}
