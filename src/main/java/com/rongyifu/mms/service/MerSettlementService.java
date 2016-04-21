package com.rongyifu.mms.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import com.ibm.icu.text.SimpleDateFormat;
import com.rongyifu.mms.bean.Account;
import com.rongyifu.mms.bean.AdjustAccount;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.FeeLiqLog;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.MidFtp;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.RyfFtp;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.BillListDownloadDao;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.utils.SftpUtil;

public class MerSettlementService {
	private SettlementDao dao = new SettlementDao();
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 商户登录的对账单下载
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadBillTXTData(Map<String, String> p) throws Exception {

		String content = "";
		int downDate = DateUtil.today();
		String mid = p.get("mid");
		String downType = p.get("downType");
		BillListDownloadDao billListDownloadDao = new BillListDownloadDao();

		if ("2".equals(p.get("tstat"))) {

			List<Hlog> hloglist = billListDownloadDao.queryPayBill(p);
			StringBuffer oidBuff = new StringBuffer();
			StringBuffer sheet = new StringBuffer();
			for (Hlog h : hloglist) {// 查询交易记录
				sheet.append(h.getMid() + "," + h.getOid() + ",");
				sheet.append(h.getMdate() + "," + Ryt.div100(h.getAmount()) + "," + Ryt.div100(h.getFeeAmt()) + ",");
				sheet.append(h.getTseq() + "," + h.getSysDate() + "," + h.getType() + ",");
				sheet.append(h.getTstat());
				sheet.append("\r\n");
				oidBuff.append(h.getOid() + "\r\n");
			}
			if (downType.equals("txt")) {// txt 下载

				// 商户号 商户名称 订单号 商户日期 交易类型 交易金额(元) 系统手续费(元) 交易状态 融易通流水号 网关号 系统日期
				String beginTitle = "TRADEDETAIL-START," + mid + "," + downDate + "," + hloglist.size() + ",S\r\n";
				String endTitle = "TRADEDETAIL-END";
				content = beginTitle + sheet.toString() + endTitle;

			}
		} else if ("3".equals(p.get("tstat"))) {
			List<RefundLog> refundLogList = billListDownloadDao.queryBackBill(p);
			// count += hloglist.size();
			StringBuffer oidBuff = new StringBuffer();
			StringBuffer sheet = new StringBuffer();
			for (RefundLog r : refundLogList) {// 查询记录
				// 商户号， 原商户订单号，原商户日期，退款金额，退回手续费，退款流水号，退款确认日期,退款经办日期，退款状态
				// mid,ref_amt,tseq,author_type,org_oid,gate,mdate,pro_date,stat
				
				sheet.append(r.getMid()).append(",");
				sheet.append(r.getOrg_oid()).append(",");
				//sheet.append(r.getMdate()).append(","); // 商户申请退款日期
				sheet.append(r.getOrg_mdate()).append(","); // 原商户交易日期
				sheet.append(Ryt.div100(r.getRef_amt())).append(",");
				sheet.append(Ryt.div100(r.getMerFee())).append(",");
				sheet.append(r.getId()).append(",");
				sheet.append(r.getReq_date()).append(",");
				sheet.append(r.getPro_date()).append(",");
				sheet.append(r.getStat()).append(",");
				sheet.append(r.getTseq());
				sheet.append("\r\n");
				oidBuff.append(r.getOrg_oid() + "\r\n");
			}
			if (downType.equals("txt")) {// txt 下载

				String beginTitle = "TRADEDETAIL-START," + mid + "," + downDate + "," + refundLogList.size() + ",S\r\n";
				String endTitle = "TRADEDETAIL-END";
				content = beginTitle + sheet.toString() + endTitle;

			}
		}
		String filename = "BILLTXT_" + DateUtil.today() + "." + downType;
		return new DownloadFile().downloadTXTFile(content, filename);

	}
	
	/**
	 * 用于结算单查询，查询fee_liq_bath表
	 * @param mid  商户号
	 * @param state  结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate  查询起始日期
	 * @param endDate  查询结束日期
	 * @return
	 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBath(int page, String mid, int beginDate, int endDate) {
		return dao.searchFeeLiqBath(page, mid, beginDate, endDate);
	}
	// 返回FeeLiqLog对象LIST
	public List<FeeLiqLog> queryLiqFeeLog(String batch) {
		// search_settlement.jsp中调用
		return dao.queryLiqFeeLog(batch);
	}
	// 返回Hlog对象LIST
	public List<Hlog> queryHlog(String batch, String gate) {
		// search_settlement.jsp中调用
		return dao.queryHlog(batch, gate);
	}
	/**
	 * 商户结算单明细查询
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public FileTransfer downloadSettleDetail(Map<String, String> p) throws Exception {
		String action = p.get("a");
		String mid = p.get("mid");
		String batch = p.get("b");
		String gate = p.get("g");
		String name = "明细表";
		String filename = "";
		List datelist = null;
		String[] title = null;
		String downTime = DateUtil.getNowDateTime();// dateFormat.format(new

		if ("qlog".equals(action)) {
			datelist = dao.queryLiqFeeLogList(batch);
			title = new String[] { "商户号", "商户简称", "银行网关", "支付金额", "退款金额", "系统手续费","退回商户手续费" };

			filename = "QLOG";
		}
		if ("qhlog".equals(action)) {
			datelist = dao.queryHlogList(batch, gate);
			title = new String[] { "商户号", "商户简称", "商户交易日期", "订单号", "交易金额", "系统手续费", "交易类型", "系统日期", "交易流水号", "银行" };

			filename = "QHLOG";
		}
		if (("list").equals(action)) {
			String kyOrder = p.get("kyOrder");
			String jbOrder = p.get("jbOrder");
			ArrayList<String[]> list = new ArrayList<String[]>();
			title = new String[] { "订单号", "交易金额", "交易日期", "交易时间", "说明" };
			list.add(title);
			if (kyOrder != "") {
				for (int i = 0; i < kyOrder.split(";").length; i++) {
					list.add(new String[] { kyOrder.split(";")[i].split(",")[0], kyOrder.split(";")[i].split(",")[1],
							kyOrder.split(";")[i].split(",")[2], timeConvert(kyOrder.split(";")[i].split(",")[3]),
							"可疑交易" });
				}
			}
			if (jbOrder != "") {
				for (int i = 0; i < jbOrder.split(";").length; i++) {
					list.add(new String[] { jbOrder.split(";")[i].split(",")[0], jbOrder.split(";")[i].split(",")[1],
							jbOrder.split(";")[i].split(",")[2], timeConvert(jbOrder.split(";")[i].split(",")[3]),
							"交易金额不符合" });
				}
			}
			filename = "SETTLEFAIl" + downTime + ".xlsx";
			return new DownloadFile().downloadXLSXFileBase(list, filename, "对账失败订单报表");
		}

		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(title);

		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<Integer, String> mermap = obj.getMerMap();// (Integer.parseInt(logMid));

		for (int it = 0; it < datelist.size(); it++) {
			Map m = (Map) datelist.get(it);
			if ("qlog".equals(action)) {
				String pa = m.get("pur_amt").toString();
				String ra = m.get("ref_amt").toString();
				String fa = m.get("fee_amt").toString();
				String refFee=m.get("ref_fee").toString();
				String[] str = { mid, mermap.get(mid), gates.get(m.get("gate")), Ryt.div100(pa),
						Ryt.div100(ra), Ryt.div100(fa),Ryt.div100(refFee) };
				list.add(str);
			}
			if ("qhlog".equals(action)) {
				String amount = m.get("amount").toString();
				String fee_amt = m.get("fee_amt").toString();
				Integer type = Integer.parseInt(m.get("type").toString());
				String sysdate = null;
				if (type == 4) {
					sysdate = m.get("mdate").toString();
				} else {
					sysdate = m.get("sys_date").toString();
				}
				String[] str = { mid, mermap.get(mid), m.get("mdate").toString(),
						m.get("oid").toString(), Ryt.div100(amount), Ryt.div100(fee_amt),
						// AppParam.tlog_type.get(type),
						RypCommon.getTlogType().get(type), sysdate, m.get("tseq").toString(),
						m.get("gate") != null ? gates.get(m.get("gate")) : "" };
				list.add(str);
			}
		}
		filename += "_" + downTime + ".xlsx";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
	}
	
	private String timeConvert(String time) {
		StringBuffer temp = new StringBuffer(time);
		if (time.indexOf(":") == -1) {
			temp.insert(2, ":");
			temp.insert(5, ":");
		}
		return temp.toString();
	}

	/**
	 * 查询商户现有金额（实际余额）
	 * 
	 * @param mid 商户ID
	 * @return 商户余额
	 */
	public String getBalanceById(String mid) {

		return dao.getBalanceById(mid);
	}

	/**
	 * 根据商户名称（商户号）,日期，查询account表中明细
	 * @param mid 商户ID
	 * @param begin_date  查询起始日期
	 * @param end_date 查询结束日期
	 * @param pageIndex 查询页码
	 * @return
	 */
	public CurrentPage<Account> searchAccount(int pageNo, String mid, int begin_date, int end_date) {
		
		return dao.searchAccount(pageNo,new AppParam().getPageSize(),mid, begin_date, end_date,null);
	}
	
	/**
	 * 根据商户号，日期，调账类型，调账状态，查询调账表记录
	 * @param mid  商户ID
	 * @param type调账类型
	 * @param state调账状态
	 * @param btdate  查询起始日期
	 * @param etdate查询结束日期
	 * @return
	 */
	public CurrentPage<AdjustAccount> queryAdjust(int pageIndex, String mid, int type, int btdate, int etdate, int state) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
		return dao.queryAdjustList(pageIndex,pageSize, mid, type, btdate, etdate, state,null);
	}
	
	/**
	 * 生成结算单（获取交易状态为成功的前一天的订单）
	 * @param mid 商户id
	 * @param date 前一天时间
	 * @return
	 */
	public boolean  madeSettlement(int date) throws Exception{
		
		//获取需要生成的商户信息
		
		List<MidFtp> MidFtps = dao.getMidFtpList();
		//获取登录ftp的信息
		RyfFtp ryfFtp = dao.getRyfFtpById("RYF_DZ");
		
		for (MidFtp midFtp : MidFtps) {
			String mid = midFtp.getMid();//商户id
			String url=midFtp.getFtpUrl();//商户上传密钥url
			//获取商户的订单信息
			List<Hlog> hlogs = dao.madeSettlement(mid,date);
			//创建txt的对账单
			File file = createSettlementFile(hlogs,mid,date);
			//
			SftpUtil util = null;
			try {
					util = new SftpUtil(ryfFtp.getFtpIp(), ryfFtp.getFtpName(), null, ryfFtp.getFtpPort(), ryfFtp.getFtpPrivateKey(),null);
					util.connect();
					util.cd(url);//商户上传到的目录
					String path = file.getPath();
					String name = file.getName();
					util.uploadFile(name, path);
					if(file.exists())
						file.delete();
					}catch(Exception e){
						logger.error(mid+"上传失败请用手动方式上传"+e.getMessage(), e);
					} finally {
						if (util != null) {
							util.close();
						}
						if(file.exists())
							file.delete();
					}
		}
		
		return true;
		
	}
	
	public File createSettlementFile(List<Hlog> hlogs,String mid,int date) throws FileNotFoundException {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式

		//String filename = "BILLTXT_"+df.format(new Date())+".txt";
		String filename = "BILLTXT_"+date+".txt";
		
		//String path = "d:/"+filename;//生成对账单的地址
		String path ="/opt/data/temp/" +filename; //生产上生成对账单的地址
		File file = new File(path);
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(fos); 
		StringBuffer sb = new StringBuffer();
		sb.append("TRADEDETAIL-START");
		sb.append(",");
		sb.append(mid);
		sb.append(",");
		sb.append(df.format(new Date()));
		sb.append(",");
		sb.append(hlogs.size());
		sb.append(",");
		sb.append("S");
		sb.append("\r\n");
		for (Hlog hlog : hlogs) {
			sb.append(hlog.getMid()).append(",").append(hlog.getOid());
			sb.append(",").append(hlog.getMdate()).append(",").append(Ryt.div100(hlog.getAmount())).append(",");
			sb.append(Ryt.div100(hlog.getFeeAmt())).append(",").append(hlog.getTseq()).append(",");
			sb.append(hlog.getSysDate()).append(",").append(hlog.getType()).append(",");
			sb.append(hlog.getTstat()).append("\r\n");
		}
		sb.append("TRADEDETAIL-END");
		pw.write(sb.toString());
		pw.flush(); 
		pw.close(); 
		return file;
		
	}

}
