package com.rongyifu.mms.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.Region;
import org.directwebremoting.io.FileTransfer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.Account;
import com.rongyifu.mms.bean.AdjustAccount;
import com.rongyifu.mms.bean.DailySheet;
import com.rongyifu.mms.bean.ErrorAnalysis;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.FeeLiqLog;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.InCome;
import com.rongyifu.mms.bean.LostOrder;
import com.rongyifu.mms.bean.MerAccount;
import com.rongyifu.mms.bean.MergeDetail;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.bean.SettleDetail;
import com.rongyifu.mms.bean.SettleMinfo;
import com.rongyifu.mms.bean.TradeStatistics;
import com.rongyifu.mms.bean.TransferMoney;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.BillListDownloadDao;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.settlement.AmountBean;
import com.rongyifu.mms.settlement.SettleTB;
import com.rongyifu.mms.utils.CreateExcelUtil;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.Digit;
import com.rongyifu.mms.utils.EmaySMS;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.RYFMapUtil;

public class SettlementService {

	private SettlementDao dao = new SettlementDao();
//	private JdbcTemplate jt = dao.getJdbcTemplate();
	private final String JSFQSUCC = "结算发起成功";
	private final String JSFQEXP = "[失败]<font color=red>结算发起异常</font>";
	private final String JSFQFAIL1 = "[失败]<font color=red>账户余额不足</font>";
	private final String JSFQFAIL2 = "[失败]<font color=red>未满足结算额度</font>";
	private final String JSFQFAIL3 ="[失败]<font color=red>结算金额为负数</font>";
	private static String VERIFYPHONENO ="15026883132";
	private static String VERIFYCODE ="0000000";
	/**
	 * 所有商户MAP
	 * @return
	 */
	public Map<Integer, String> getHashMer() {
		RYFMapUtil obj = RYFMapUtil.getInstance();
		return obj.getMerMap();
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
	public CurrentPage<AdjustAccount> queryAdjust(int pageIndex, String mid, int type, int btdate, int etdate, int state,Integer mstate) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
		return dao.queryAdjustList(pageIndex,pageSize, mid, type, btdate, etdate, state,mstate);
	}
	/**
	 * 根据商户号，日期，调账类型，调账状态，下载调账表记录
	 * @param mid  商户ID
	 * @param type调账类型
	 * @param state调账状态
	 * @param btdate  下载起始日期
	 * @param etdate下载结束日期
	 * @return
	 */
	  public FileTransfer downAdjust(String mid, int type, int btdate, int etdate, int state,Integer mstate) throws Exception{
		 CurrentPage<AdjustAccount> adjust=dao.queryAdjustList(1,-1, mid, type, btdate, etdate, state,mstate);
		 List<AdjustAccount> adjustAccount=adjust.getPageItems();
			final String[] title={"序号","商户号","商户简称","调账请求操作员ID","调账请求时间","调账金额(元)","调账类型","调账状态","调账审核操作员ID"
					             ,"调账审核时间","调账原因"};
			ArrayList<String[]> strList = new ArrayList<String[]>();
			strList.add(title);
			long adjustAccountAmount = 0;
			int i = 0;
			for (AdjustAccount adjustAccountList : adjustAccount) {
				String[] strArr={
						String.valueOf(i+1),
					    adjustAccountList.getMid(),
					    adjustAccountList.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(adjustAccountList.getMid()),
					    adjustAccountList.getSubmitOperid()==null?"":String.valueOf(adjustAccountList.getSubmitOperid()),
					    adjustAccountList.getSubmitDate()==null?"":(adjustAccountList.getSubmitDate()+" "+Ryt.getStringTime(adjustAccountList.getSubmitTime())),
					    Ryt.div100(adjustAccountList.getAccount()),
					    adjustAccountList.getType()== 1 ? "手工增加":"手工减少",
					    adjustAccountList.getState()== 0 ? "调账提交":adjustAccountList.getState() == 1 ? "审核成功":"审核失败",
					    String.valueOf(adjustAccountList.getAuditOperid()),
					    adjustAccountList.getAuditDate()==null?"":(adjustAccountList.getAuditDate()+"  "+Ryt.getStringTime(adjustAccountList.getAuditTime())),
					    adjustAccountList.getReason()
				};
				strList.add(strArr);
				i += 1;
				adjustAccountAmount+=adjustAccountList.getAccount();
			}
			String[] str = { "总计:" +i + "条记录","","","","",Ryt.div100(adjustAccountAmount),
					        "","","","",""};
			strList.add(str);
			String filename = "AdjustAccount"+ DateUtil.getIntDateTime()+ ".xlsx";
			String name = "手工调账报表";
			return new DownloadFile().downloadXLSXFileBase(strList, filename, name);
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

		
		/**
		 * 用于结算单查询，查询fee_liq_bath表
		 * @param mid  商户号
		 * @param state  结算状态 1-已发起 2-已制表 3-已完成
		 * @param beginDate  查询起始日期
		 * @param endDate  查询结束日期
		 * @return
		 */
	public CurrentPage<FeeLiqBath> searchFeeLiqBathByLiqType(int page,String merType,String mid, int beginDate, int endDate,Integer liqgid) {
		return dao.searchFeeLiqBath(page,merType, mid, beginDate, endDate,liqgid);
	}

	public List<FeeLiqBath> queryPrintTableData(String merType,String mid, int beginDate,int endDate,Integer liqgid) {
		return dao.queryPrintTableData(merType,mid, beginDate,endDate,liqgid);
	}
		
	// 返回Hlog对象LIST
	public List<Hlog> queryHlog(String batch, String gate) {
		// search_settlement.jsp中调用
		return dao.queryHlog(batch, gate);
	}
	/**
	 * 返回Hlog对象LIST 管理后台商户结算单查询查看明细 (20140603改造后的版本)
	 * @param batch 批次号
	 * @return
	 */
	public List<Hlog> queryHlogs(String batch) {
			// search_settlement.jsp中调用
			return dao.queryHlog(batch);
	} 

	// 返回FeeLiqLog对象LIST
	public List<FeeLiqLog> queryLiqFeeLog(String batch) {
		// search_settlement.jsp中调用
		return dao.queryLiqFeeLog(batch);
	}

	/**
	 * 查询对账结果处理---查询
	 * @param page
	 * @param mid
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<ErrorAnalysis> searchSettleResult(int page, int beginDate, int endDate, int errorType,
			int checkDate,int gate) {
		// settle_result.jsp中调用
		return dao.searchSettleResult(page, beginDate, endDate, errorType, checkDate,gate);
	}

	/**
	 * 查询对账结果处理---确认处理
	 * @param loginmid
	 * @param loginuid
	 * @param remark
	 * @param seleteId
	 * @return
	 */
	public String confirmSolve(int loginmid, int loginuid, String remark, String seleteId) {
		// settle_result.jsp中调用
		return dao.confirmSolve(loginmid, loginuid, remark, seleteId);
	}

	/**
	 * 查询fee_liq_bath表并分页
	 * @param mid 商户号
	 * @param state 结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate查询结束日期
	 * @param batch查询批次号
	 * @param pageIndex 页码
	 * @return
	 */
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate,Integer liqGid) {
		return dao.getFeeLiqBath(pageNo, beginDate, endDate, mid, state, batch,mstate, liqGid);
	}
	//获取结算金额的list
	private  List<AmountBean> getAmountBean(int lastLiqDate,String lastBatch,int intExpDate,String mid){
		List<AmountBean> amountList = new ArrayList<AmountBean>();
		Map<Integer, AmountBean> map = new HashMap<Integer, AmountBean>();
		//按网关统计的支付总金额
		List<Map<String, Object>> payM = dao.getPayAmountMap(lastLiqDate,lastBatch, intExpDate, mid);
		//按网关统计的退款总金额
		List<Map<String, Object>> refM = dao.getRefAmountMap(lastLiqDate,lastBatch, intExpDate, mid);
		if (refM.size() != 0) {
			for (Map<String, Object> refm : refM) {
				AmountBean ab1 = new AmountBean();
				//退款网关
				Integer tkgate = Integer.parseInt(refm.get("gate").toString());
				//退款总额
				ab1.setRAmount(getLongValue(refm.get("rsum")));
				//退款次数
				ab1.setRefConut(getIntValue(refm.get("rcount")));
				//电银手续费
				ab1.setRefFee(getIntValue(refm.get("rfee")));
				//银行手续费
				ab1.setBkRefFee(getIntValue(refm.get("bkfee")));
				ab1.setGate(tkgate);
				map.put(tkgate, ab1);
			}
		}
		for (Map<String, Object> paym : payM) {
			Integer gate = Integer.parseInt(paym.get("gate").toString());
			AmountBean ab = null;
			if (map.containsKey(gate)) {
				ab = map.get(gate);
				ab.setPAmount(getLongValue(paym.get("psum")));
				ab.setPurConut(getIntValue(paym.get("pcount")));
				ab.setBankFee(getIntValue(paym.get("fsum")));
			} else {
				ab = new AmountBean();
				ab.setPAmount(getLongValue(paym.get("psum")));
				ab.setPurConut(getIntValue(paym.get("pcount")));
				ab.setBankFee(getIntValue(paym.get("fsum")));
				ab.setRAmount(getLongValue(0));
				ab.setRefConut(getIntValue(0));
				ab.setRefFee(getIntValue(0));
				ab.setBkRefFee(getIntValue(0));
				ab.setGate(gate);
				map.put(gate, ab);
			}
		}
		for (Integer dataKey : map.keySet()) {
			AmountBean ab2 = new AmountBean();
			ab2.setPAmount(map.get(dataKey).getpAmount() == null ? 0 : map.get(dataKey).getpAmount());
			ab2.setPurConut(map.get(dataKey).getPurConut() == null ? 0 : map.get(dataKey).getPurConut());
			ab2.setBankFee(map.get(dataKey).getBankFee() == null ? 0 : map.get(dataKey).getBankFee());
			ab2.setRAmount(map.get(dataKey).getRAmount());
			ab2.setRefConut(map.get(dataKey).getRefConut());
			ab2.setRefFee(map.get(dataKey).getRefFee());
			ab2.setBkRefFee(map.get(dataKey).getBkRefFee());
			ab2.setGate(dataKey);
			amountList.add(ab2);
		}

		return amountList;
	}
	private String getFeeLiqLogSql(String mid,int liqType,String batch,AmountBean amountBean ){
		long zfze = amountBean.getPAmount();// 每个网关的支付总金额
		long tkze = amountBean.getRAmount();// 每个网关的退款总金额
		int sxfze = amountBean.getBankFee();// 每个网关的手续费总额
		int thze = amountBean.getRefFee();//退回手续费总额
		
		/*每个网关的交易金额=支付总金额-退款总金额*/
		long trans_amt = zfze - tkze;
		/*手续费率=手续费总金额/支付总金额*/
		int fee_ratio = zfze > 0 ? (int) (sxfze/ zfze) : 0;
		/*(全额结算)清算金额=交易金额，(净额结算)清算金额= 交易金额-手续费总金额 + 退回的手续费*/
		//long liq_amt = liqType == 1 ? trans_amt : (trans_amt - sxfze + thze);
		
		long liq_amt = (trans_amt - sxfze + thze);
		
		int purCnt = amountBean.getPurConut();
		int refCnt = amountBean.getRefConut();
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("insert into fee_liq_log ");
		sqlBuff.append("(mid,gate,amount,fee_ratio,liq_amt,pur_amt,ref_amt,fee_amt,pur_cnt,ref_cnt,batch,ref_fee)");
		sqlBuff.append("values ( '").append(mid).append("',").append(amountBean.getGate()).append(",");
		sqlBuff.append(trans_amt).append(",").append(fee_ratio).append(",");
		sqlBuff.append(liq_amt).append(",").append(zfze).append(",").append(tkze).append(",");
		sqlBuff.append(sxfze).append(",").append(purCnt).append(",");
		sqlBuff.append(refCnt).append(",'").append(batch).append("',").append(thze).append(")");
		return sqlBuff.toString();
	}
	/**
	 * 结算发起核心代码
	 * @param theMinfo 商户结算信息
	 * @param intExpDate 截至日期
	 * @throws Exception 
	 */
	private void doSettlement(SettleMinfo theMinfo, int intExpDate) throws Exception {
		
		if(theMinfo == null || intExpDate == 0) return;
		// 商户号
		String mid = theMinfo.getMid();
		// 本次结算批次号 
		String batch = theMinfo.getMid() + "" + intExpDate;
		// 结算条件 有条件结算/无条件结算
		int hasCondLiq = theMinfo.getLiqLimit() == 0 ? 0 : 1;
		int liqType = theMinfo.getLiqType();
		// 查询上次结算的日期
		int lastLiqDate = theMinfo.getLastLiqDate();
		String lastBatch = theMinfo.getLastBatch();

		ArrayList<String> arrayList = new ArrayList<String>();
		List<AmountBean> list = getAmountBean(lastLiqDate,lastBatch,intExpDate,mid);

		// 支付总额
		long zfzeSum = 0L;
		// 退款总额
		long tkzeSum = 0L;
		// 手续费总额
		int sxfzeSum = 0;
		// 结算金额
		long liq_amtSum = 0L;
		// 支付总笔数
		int purCntSum = 0;
		// 退款总笔数
		int refCntSum = 0;
		//退回手续费总额
		int thFeeSum = 0;
		//银行退回的手续费
		int bkthFeeSum = 0;
		
		StringBuffer sqlBuff = new StringBuffer();
		for (AmountBean amountBean : list) {
			long zfze = amountBean.getPAmount();// 每个网关的支付总金额
			long tkze = amountBean.getRAmount();// 每个网关的退款总金额
			int sxfze = amountBean.getBankFee();// 每个网关的手续费总额
			int thze = amountBean.getRefFee();//退回手续费总额
			
			/*每个网关的交易金额=支付总金额-退款总金额*/
			long trans_amt = zfze - tkze;

			/*(全额结算)清算金额=交易金额，(净额结算)清算金额= 交易金额-手续费总金额 + 退回的手续费*/
//			long liq_amt = liqType == 1 ? trans_amt : (trans_amt - sxfze + thze);
			/*去掉净额全额之分*/
			long liq_amt = (trans_amt - sxfze + thze);
			

			arrayList.add(getFeeLiqLogSql(mid, liqType, batch, amountBean));
			/*清空sqlBuff，后续在使用*/
			sqlBuff.delete(0, sqlBuff.length());
			
			zfzeSum += zfze; // 所有网关的支付总金额
			tkzeSum += tkze; // 所有网关的退款总金额
			sxfzeSum += sxfze;// 所有网关的手续费总额
			liq_amtSum += liq_amt;// 所有网关的清算金额
			purCntSum += amountBean.getPurConut(); // 支付总笔数
			refCntSum += amountBean.getRefConut(); // 退款总笔数
			thFeeSum += thze;//所有网关的退回总额
			bkthFeeSum += amountBean.getBkRefFee();//银行退回的手续费
		}
		Map<String, Object> addMap =dao.getHandMap(mid,1);// getMapValue(mapSql.toString() + " and a.type = 1 ");
		Map<String, Object> subMap = dao.getHandMap(mid,2);//getMapValue(mapSql.toString() + " and a.type = 2 ");

		long manualAddSum = getLongValue(addMap.get("amt"));
		long manualSubSum = getLongValue(subMap.get("amt"));
		int addCount = getIntValue(addMap.get("cnt"));
		int subCount = getIntValue(subMap.get("cnt"));
		
		liq_amtSum = liq_amtSum + manualAddSum - manualSubSum; // 结算金额
		long balance = dao.get100BalanceById(mid);
		// 判断结算金额是否小于余额是否大于结算满足额度
		if (balance >= liq_amtSum && liq_amtSum >= Integer.parseInt(Ryt.mul100(theMinfo.getLiqLimit()))) {
			sqlBuff.append("insert into fee_liq_bath (mid,trans_amt,ref_amt,fee_amt,liq_amt,liq_date");
			sqlBuff.append(",gen_date,liq_cond,liq_type,batch,last_batch,last_liq_date,manual_add,manual_sub");
			sqlBuff.append(",pur_cnt,ref_cnt,add_cnt,sub_cnt,ref_fee,bk_ref_fee) values ( '");
			sqlBuff.append(mid).append("',").append(zfzeSum).append(",").append(tkzeSum).append(",");
			sqlBuff.append(sxfzeSum).append(",").append(liq_amtSum).append(",");
			sqlBuff.append(intExpDate).append(",").append(DateUtil.today()).append(",");
			sqlBuff.append(hasCondLiq).append(",").append(liqType).append(",'");
			sqlBuff.append(batch).append("',").append(lastBatch).append(",").append(lastLiqDate).append(",");
			sqlBuff.append(manualAddSum).append(",").append(manualSubSum).append(",");
			sqlBuff.append(purCntSum).append(",").append(refCntSum).append(",").append(addCount).append(",");
			sqlBuff.append(subCount).append(",").append(thFeeSum).append(",").append(bkthFeeSum).append(")");
			
			arrayList.add(sqlBuff.toString());
			
			/*清空sqlBuff，后续在使用*/
			sqlBuff = null;
			// 向商户表中插入批次号
			arrayList.add("update minfo set last_batch = '" + batch + "',last_liq_date=" + intExpDate + " where id = "
					+Ryt.addQuotes(mid) );
			
			// 向hlog中插入批次号
			arrayList.add("update hlog set batch = '"+ batch+ "' " +
			              " where mid = "+ Ryt.addQuotes(mid)+ " and tstat = 2 and batch = 0 and is_liq = 0" +
			              "   and ((sys_date >= " + lastLiqDate+ " and sys_date < " + intExpDate+ " and gid <> 55000 and bk_chk=1) " +
			              "    or (sys_date >= " + lastLiqDate+ " and sys_date < " + intExpDate+ " and gid = 55000 ))");
			
			// 向退款表中插入批次号
			arrayList.add("update refund_log set batch='" + batch + "' where batch=0 and stat in(2,3,4) and mid=" + Ryt.addQuotes(mid)
					+ " and sys_date< " + intExpDate + " and pro_date<=" + intExpDate);
			// 向手工调账表中插入批次号
			arrayList.add("update adjust_account set batch='" + batch + "' where state=1 and batch=0 and mid=" + Ryt.addQuotes(mid));

			String[] sqlList = arrayList.toArray(new String[arrayList.size()]);
			int[] r = dao.batchSqlTransaction(sqlList);
			if (r.length == 0) {
				theMinfo.setResule(JSFQEXP);
			} else {
				theMinfo.setResule(JSFQSUCC);
			}

		} else {
			Map<String, String> logParams = LogUtil.createParamsMap();
			logParams.put("mid", mid);
			logParams.put("batch", batch);
			logParams.put("lastLiqDate", String.valueOf(lastLiqDate));
			logParams.put("intExpDate", String.valueOf(intExpDate));
			logParams.put("liq_amtSum", String.valueOf(liq_amtSum));
			logParams.put("balance", String.valueOf(balance));
			logParams.put("liqLimit", String.valueOf(theMinfo.getLiqLimit()));
			logParams.put("zfzeSum", String.valueOf(zfzeSum));
			logParams.put("tkzeSum", String.valueOf(tkzeSum));
			logParams.put("sxfzeSum", String.valueOf(sxfzeSum));
			logParams.put("thFeeSum", String.valueOf(thFeeSum));
			logParams.put("bkthFeeSum", String.valueOf(bkthFeeSum));
			
			if (liq_amtSum < 0){
				theMinfo.setResule(JSFQFAIL3);
				logParams.put("liqFailMsg", JSFQFAIL3);
				LogUtil.printInfoLog("doSettlement", logParams);
			} else if (balance < liq_amtSum) {
				theMinfo.setResule(JSFQFAIL1);
				logParams.put("liqFailMsg", JSFQFAIL1);
				LogUtil.printInfoLog("doSettlement", logParams);
			} else {
				theMinfo.setResule(JSFQFAIL2);
				logParams.put("liqFailMsg", JSFQFAIL2);
				LogUtil.printInfoLog("doSettlement", logParams);
			}
			arrayList = null;
		}

	}

	public List<SettleMinfo> beginSettlement2(List<SettleMinfo> objs, int intExpDate) {
		for (SettleMinfo m : objs) {
			try {
				doSettlement(m, intExpDate);
			} catch (Exception e) {
				e.printStackTrace();
				m.setResule(JSFQEXP);
			}
		}
		dao.saveOperLog( "结算发起", "结算截至日期," + intExpDate
				+ ";结算商户数:" + objs.size());
		return objs;
	}

	
	/**
	 * 根据批次号查询结算批次表中的记录
	 * 
	 * @param batch
	 *            结算批次号
	 * @return 结算批次表的实体对象
	 */
	public FeeLiqBath getFeeLiqBathByBatch(String batch) {
		// begin_settlement.jsp中调用
		return dao.getFeeLiqBathByBatch(batch);
	}

	/**
	 * 结算确认的详细信息
	 * 
	 * @param batch
	 *            结算批次号
	 * @return 返回一个list 所选中的记录以及该条记录信息的详细
	 */
	public List<SettleDetail> getSettleDetail(String batch) {
		// admin_begin_settlement.jsp中调用
		return dao.getSettleDetail(batch);
	}

	/**
	 * 检查商户是否为完成对账
	 * 
	 * @param mids
	 *            商户号,商户号,...
	 * @param date
	 *            结算日期
	 * @return 完成对账商户号 + ; + 未完成对账商户号
	 * @throws Exception 
	 */
	public List<SettleMinfo> checkMinfo2(List<SettleMinfo> objs, int date) throws Exception {
		
		for (SettleMinfo m : objs) {
			
			if(m.getExpDate() < DateUtil.today()){
				m.setFlag(false);
				m.setMsg("合同过期!");
				continue;
			}
			// 0--有效的结算商户 1--没有结算订单 2--没有对账
			int flag = isCheck(m.getMid(), date);
			if (flag == 0) {
				m.setFlag(true);
			} else if (flag == 1) {
				m.setFlag(false);
				m.setMsg("没有可结算的订单");
			} else {
				m.setFlag(false);
				m.setMsg("存在没有对账的订单");
			}
		}
		return objs;
	}

	private int getIntValue(Object o) {
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	private long getLongValue(Object o) {
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
			return 0l;
		}
	}

	/**
	 * 根据日期和商户号数组查询可以发起结算的商户信息
	 * 
	 * @param date  结算发起截止日期
	 * @param mids 不在结算周期内的商户号数组
	 * @return  商户信息的map={id=?, name=?}
	 */
	public List<SettleMinfo> getSettleMinfos2(int date) {

		List<SettleMinfo> list = new ArrayList<SettleMinfo>();
		List<SettleMinfo> list1 = dao.getInSettleMinfo(date);
		
		StringBuffer mids = new StringBuffer("(0");
		for (SettleMinfo minfo : list1) {
			minfo.setPerioded(true);
			mids.append(",").append(minfo.getMid());
		}
		mids.append(")");
		list.addAll(list1);
		list.addAll(dao.getNoInSettleMinfo(mids.toString()));
		return list;
	}

	/**
	 * 结算制表
	 * 
	 * @param batchs
	 *            需要结算制表的批次号数组
	 * @return 制表结果
	 * @throws IOException
	 */
	public String drawSettleTB(String[] batchs) throws IOException {
		SettleTB o = new SettleTB();
		return o.drawSettleTB(batchs);
	}

	/**
	 * 对已制表的结算记录进行确认
	 * 
	 * @param batchs需要结算记录的批次号数组
	 * @param loginmid 执行确认操作的操作员的商户号
	 * @param loginuid  执行确认操作的操作员的操作员号
	 * @return 确认的结果
	 */
	public String verifySettle(List<FeeLiqBath> f) {
		String msg =null;
		boolean isException = false;
		StringBuffer exceptionBuff = new StringBuffer(); // 因异常不能完成结算的商户
		if(f==null||f.size()==0) return "请选择要结算确认的订单";
	
			for (FeeLiqBath mid : f) {
				try{
				String midq = mid.getMid();
				String liqBatch = mid.getBatch();
				int liqgid = dao.queryLiqGid(liqBatch);
				if (liqgid == 4) {
					String remark = "结算到账户";
					String url = Ryt.getEwpPath();
					Minfo minfo = dao.queryAccBymid(midq);
					String payAmt = Ryt.div100(mid.getLiqAmt());
					String tmsIp = dao.queryBytmsIp(midq);
					String dfType = "A";
					String data_source = "7";
					String md5key = "iFv5x6Cu";
					String transType = "auto_df";
					// 保存订单到tlog;
					Map<String, String> map = dao.insertliqtlog(midq, payAmt,
							minfo.getPbkGateId(), minfo.getPbkAccNo(),
							minfo.getPbkAccName(), minfo.getPbkNo(), dfType,
							data_source, liqBatch, transType,
							minfo.getPbkProvId(), minfo.getPbkName(),
							minfo.getPbkBranch());
					String sql = map.get("sql");
					dao.liqaccount(mid, remark, sql);// 结算到账户
					String oid = map.get("oid");
					String date = map.get("date");
					String tseq = dao.getTlogTseq(midq, oid, date,
							minfo.getPbkGateId(), minfo.getPbkAccNo(),
							minfo.getPbkAccName(), minfo.getPbkNo());
					String data = midq + payAmt + transType + data_source
							+ tmsIp + md5key;
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("transAmt", payAmt);
					paramMap.put("data_source", data_source);
					paramMap.put("mid", midq);
					paramMap.put("chkValue", MD5.getMD5(data.getBytes()));
					paramMap.put("transType", transType);
					paramMap.put("tseq", tseq);
					
					String sResponseBody = Ryt.requestWithPost(paramMap, url+ "df/auto_df");
					paramMap.put("response", sResponseBody);
					LogUtil.printInfoLog("Auto_df", paramMap);
					msg = "自动代付订单录入成功";
				} else if (liqgid == 1) {
					String remark = "结算到账户";
					int[] account = dao.liqaccount(mid, remark, null);// 结算到账户
					if (account.length == 3)
						msg = "结算成功！";
				} else if (liqgid == 3) {
					String remark = "结算到银行卡";
					String url = Ryt.getEwpPath();
					Minfo minfo = dao.queryAccBymid(midq);
					String payAmt = Ryt.div100(mid.getLiqAmt());
					String tmsIp = dao.queryBytmsIp(midq);
					int mertype = dao.querymertype(midq);
					String dfType;
					Integer GateId;
					String GateId1 = minfo.getGateId().toString();
					if (mertype == 1) {
						dfType = "A";
						GateId = Integer.parseInt("71" + GateId1.substring(2));
					} else {
						dfType = "B";
						GateId = Integer.parseInt("72" + GateId1.substring(2));
					}
					String data_source = "8";
					String md5key = "iFv5x6Cu";
					String transType = "auto_df";
					// 保存订单到tlog;
					Map<String, String> map = dao.insertliqtlog(midq, payAmt,
							GateId, minfo.getBankAcct(), minfo.getBankAcctName(), 
							minfo.getOpenBkNo(),dfType, data_source, liqBatch, transType, 
							minfo.getBankProvId().toString(), minfo.getBankName(), minfo.getBankBranch());
					String sql = map.get("sql");
					dao.liqaccount(mid, remark, sql);// 结算到账户
					String oid = map.get("oid");
					String date = map.get("date");
					String tseq = dao.getTlogTseq(midq, oid, date, GateId,
							minfo.getBankAcct(), minfo.getBankAcctName(),
							minfo.getOpenBkNo());
					String data = midq + payAmt + transType + data_source+ tmsIp + md5key;
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("transAmt", payAmt);
					paramMap.put("data_source", data_source);
					paramMap.put("mid", midq);
					paramMap.put("chkValue", MD5.getMD5(data.getBytes()));
					paramMap.put("transType", "auto_df");
					paramMap.put("tseq", tseq);
					
					String sResponseBody = Ryt.requestWithPost(paramMap, url+ "df/auto_df");
					paramMap.put("response", sResponseBody);
					LogUtil.printInfoLog("Auto_Liq", paramMap);
					msg = "自动结算订单录入成功";
				} else if (liqgid == 2) {
					int[] bankcard = dao.liqbankcard(mid);// 结算到银行卡
					if (bankcard.length == 4)
						msg = "结算成功！";
				}
				dao.saveOperLog("结算确认", "结算确认成功");

			}
		 catch (Exception e) {
			exceptionBuff.append(mid.getMid()+"—"+RYFMapUtil.getInstance().getMerMap().get(mid.getMid())+ ",").append("\n");
			msg = "结算异常!";
			e.printStackTrace();	
			isException = true;
			dao.saveOperLog("结算确认", "结算确认失败");
			}
		}
		if (exceptionBuff.length() != 0) {
			exceptionBuff.deleteCharAt(exceptionBuff.lastIndexOf(",")).append("\n").append("的记录因异常结算失败!");
			msg = "商户为:"+"\n" + exceptionBuff.toString();
		}

		return msg;

	}

	/**
	 * 根据商户名称（商户号），查询minfo表中余额
	 * @param mid商户ID
	 * @return 商户余额LIST
	 */
	public long searchMinfoBalance(String mid) {
		return dao.queryBalance(mid);
	}

	/**
	 * 根据商户名称（商户号）,日期，查询account表中明细
	 * @param mid 商户ID
	 * @param begin_date  查询起始日期
	 * @param end_date 查询结束日期
	 * @param pageIndex 查询页码
	 * @return
	 */
	public CurrentPage<Account> searchAccount(int pageNo, String mid, int begin_date, int end_date,Integer mstate) {
		
		return dao.searchAccount(pageNo, AppParam.getPageSize(),mid, begin_date, end_date,mstate);
	}
	/**
	 * 下载  account表中明细
	 * @param pageNo
	 * @param mid
	 * @param begin_date
	 * @param end_date
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downLoadAccount( String mid, int begin_date, int end_date,Integer mstate) throws Exception {
		CurrentPage<Account> accountPage=dao.searchAccount(1,-1,mid, begin_date, end_date,mstate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] title ="序号,商户号,商户简称,结算方式,交易时间,操作类型,操作标识符,交易金额(元),系统手续费(元),变动金额(元),可用余额(元),余额(元)".split(",");
		list.add(title);
		List<Account> accountList = accountPage.getPageItems();
		long totleAmount = 0;
		long totleSysFee = 0;
		for (int i = 0; i < accountList.size(); i++) {
			Account account=accountList.get(i);
			  String[] str = {
					String.valueOf(i+1),
					String.valueOf(account.getMid()), 
					String.valueOf(account.getMid()), 
					account.getLiqType()==1?"全额结算":"净额结算", 
					account.getDate()+" "+DateUtil.getStringTime(account.getTime()), 
					AppParam.account_type.get(account.getType()),
					String.valueOf(account.getTseq()),
					Ryt.div100(account.getAmount()),
					Ryt.div100(account.getFee()),
					Ryt.div100(account.getAccount()),
					Ryt.div100(account.getBalance())
				};
			  totleAmount+=account.getAmount();
			  totleSysFee+=account.getFee();
				list.add(str);
		}
		String[] nullstr = { "", "", "", "", "", "",  "", "", "", "", "" };
		String[] totle = {"总计："+accountList.size()+"条", "", "", "", "", "",
				"",Ryt.div100(totleAmount),Ryt.div100(totleSysFee), "", "" };

		list.add(nullstr);
		list.add(totle);
		String filename = "ACCOUNTLOG_" + DateUtil.getIntDateTime() + ".xls";
		String name = "账户流水表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
	}
	/**
	 * 根据商户号，订单号，融易通流水号，订单日期，查询tlog或hlog表中待支付状态订单(掉单手共提交 查询)
	 * @param mid  商户号
	 * @param oId 订单号
	 * @param tseq 融易通流水号
	 * @param btdate 订单日期
	 * @param pageIndex  查询页码
	 * @return 符合条件订单list
	 */
	public CurrentPage<Hlog> queryLostOrder(int page, String mid, String oId, String tseq, int btdate) {

		return dao.queryLostOrder(page, mid, oId, tseq, btdate);
	}

	/**
	 * 根据融易通流水号查询掉单详情
	 * 
	 * @param tseq
	 *            融易通流水号
	 * @return 该条订单对象Hlog
	 */
	public Hlog queryLostOrderByTseq(String tseq) {
		try {
			Hlog hg = dao.queryLostOrderByTseq(tseq);
			return hg;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 掉单手工确认处理
	 * 
	 * @param action
	 * @param tseq
	 * @param merDate
	 * @param bank_seq
	 * @param loginmid
	 * @param loginuid
	 * @return
	 */
	public String confirmLostOrder(String action, String tseq, int merDate, String bank_seq, String gateID, String mid,
			String amount, int gid) {

		int time = DateUtil.getCurrentUTCSeconds();
		if (Ryt.empty(tseq) || null == bank_seq)
			return "操作失败，请重试！";
		String actionMsg = "";
		String backResult = "";
		LostOrder bean = dao.queryLostOrderById(mid, gateID, gid);
		// 手工确认支付成功
		if (action.equals("success")) {
			actionMsg = "掉单确认成功";
			backResult = "S";
			int[] effectRow=dao.confirmLostOrderSuccess(tseq, merDate, bank_seq, amount, bean,mid);
		    if (effectRow.length !=3)
				return "处理出现异常，请重试！";
		}
		// 手工确认支付失败
		if (action.equals("failure")) {
			actionMsg = "掉单确认失败";
			backResult = "F";
			int effectRow= dao.confirmListOrderFailure(bean.getGid(), bank_seq, tseq, merDate);
			if (effectRow < 1)
				 return "处理出现异常，请重试！";
		}
		try {
			// 异步通知商户处理结果
			new RypCommon().requestMerBGUrl(tseq, merDate, backResult, actionMsg, time);
		} catch (ConnectException e) {
			 return "操作成功 ，但通知商户后台连接超时!";
		} catch (IOException e) {
			 return "操作成功，但通知商户时io异常!";
		} catch (Exception e) {
		    return "操作成功，但"+e.getMessage();
		}
			dao.saveOperLog(actionMsg, "操作成功(" + tseq + ")");
		return AppParam.SUCCESS_FLAG;
	}

	// 调单手工提交
	// 当天交易查询
	public static List<Map<Integer, String>> getLostOrde() {
		// submit_lost_order.jsp 中调用
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();

		RYFMapUtil obj = RYFMapUtil.getInstance();

		aList.add(obj.getMerMap());// 商户
		aList.add(RYFMapUtil.getGateMap());// 银行
		aList.add(AppParam.authorType);// 发起类型 auth_type
		return aList;
	}

	/**
	 * 手工调账请求
	 * 
	 * @param mid
	 * @param operType
	 * @param origAcc
	 * @param editAcc
	 * @param reason
	 * @param loginmid
	 * @param loginuid
	 * @return
	 */
	public String adjustAccount(String mid, int operType, String origAcc, String editAcc, String reason, int loginmid,
			int loginuid) {
		// adjust_account_submit.jsp中调用
		String reason2 = !Ryt.empty(reason) ? reason.replaceAll("\\r\\n|\\n|\\r", "") : "";
		if ("".trim().equals(mid) || 0 == operType || null == origAcc || null == editAcc || 0 == loginmid)
			return "调账失败";
		if (loginmid != 1)
			return "该商户不能进行调账";
		int type = operType;
		long oAcc = Ryt.mul100toInt(origAcc);
		long balance = 0;
		long account =Ryt.mul100toInt(editAcc);
		String actinDesc = "手工调账:为商户(" + mid + ")";

		if (type == 1) {
			actinDesc += "增加" + editAcc + "元";
		}
		if (type == 2) {
			balance = oAcc - account;
			actinDesc += "减少" + editAcc + "元";
			if (balance < 0)
				return "调账请求失败，手工减少金额不得超过现有余额!";
		}
		int date = DateUtil.today();
		int time = DateUtil.getCurrentUTCSeconds();
		try {
			dao.adjustAccount(mid, type, loginuid, date, time, account, reason2);
			dao.saveOperLog( "手工调账请求", actinDesc += "请求成功");
		} catch (Exception e) {
			dao.saveOperLog("手工调账请求", actinDesc += "请求失败");
			return "调账请求失败,请重试。";
		}
		return "ok";
	}

	/**
	 * 手工调账审核成功
	 * 
	 * @param action
	 * @param loginmid
	 * @param loginuid
	 * @param auditId
	 * @return
	 */
	public String auditSuccess(String loginmid, int loginuid, long[] audit_Id) {
		String msg = "";
		for (int i = 0; i < audit_Id.length; i++) {
			// 根据调账表ID得到调账详细信息
			AdjustAccount adjust = getAdjustAccountById(audit_Id[i]);
			String actinDesc = "手工调账:为商户(" + adjust.getMid() + ")";
			// 查询商户当前余额
			//String org_f_balance = getBalanceById(adjust.getMid());
			//int org_balance = Integer.parseInt(Ryt.mul100(org_f_balance));
//			int balance = 0;
			
			int isAdd = 0;
			// 调账方式为增加或是减少时，求调账后余额
			if (adjust.getType() == 1) {
				actinDesc += "增加" + Digit.percent(String.valueOf(adjust.getAccount())) + "元";
//				balance = org_balance + adjust.getAccount();
			}
			if (adjust.getType() == 2) {
				actinDesc += "减少" + Digit.percent(String.valueOf(adjust.getAccount())) + "元";
//				balance = org_balance - adjust.getAccount();
				
				long balance = dao.queryBalance(adjust.getMid()) - adjust.getAccount();
				isAdd = 1;
				if (balance < 0)
					return "操作失败，手工减少金额不得超过现有余额!";
			}
			try {
				int[] effectCount = dao.auditSuccess(adjust, audit_Id[i], isAdd, loginuid);
				if (effectCount==null||effectCount.length == 0) {
					throw new Exception("流水号为" + adjust.getId() + "的记录处理异常！");
				}
				
				/*增加存管数据*/
				//TODO 测试是否正确？
				//dao.createCGData(adjust);
				
				
				dao.saveOperLog("调账审核成功", actinDesc + "审核成功");
			} catch (Exception e) {
				dao.saveOperLog("调账审核成功", actinDesc + "审核失败");
				return "操作失败，请重试！" + e.getMessage();
			}
			msg = "操作已成功";
		}
		return msg;
	}

	/**
	 * 手工调账审核失败
	 * 
	 * @param action
	 * @param loginmid
	 * @param loginuid
	 * @param auditId
	 * @return
	 */
	private String auditFailure(String loginmid, int loginuid, long[] audit_Id) {
		String msg = "";
		for (int i = 0; i < audit_Id.length; i++) {
			// 根据调账表ID得到调账详细信息
			AdjustAccount adjust = getAdjustAccountById(audit_Id[i]);
			String actinDesc = "手工调账:为商户(" + adjust.getMid() + ")";
			// 调账方式为增加或是减少
			if (adjust.getType() == 1) {
				actinDesc += "增加" + Digit.percent(String.valueOf(adjust.getAccount())) + "元";
			}
			if (adjust.getType() == 2) {
				actinDesc += "减少" + Digit.percent(String.valueOf(adjust.getAccount())) + "元";
			}
			try {
				int effectRow=dao.auditFailure(loginmid, loginuid, audit_Id[i]);
				if(effectRow==1)
				    dao.saveOperLog("调账审核失败", actinDesc + "审核成功");
			} catch (Exception e) {
				dao.saveOperLog("调账审核失败", actinDesc + "审核失败");
				return "操作失败,请重试！";
			}
			msg = "操作已成功";
		}
		return msg;

	}

	/**
	 * 手工调账审核
	 * 
	 * @param action
	 * @param loginmid
	 * @param loginuid
	 * @param auditId
	 * @return
	 */
	public String auditAdjust(String action, String loginmid, int loginuid, String auditId) {
		// adjust_account_audit.jsp中调用
		String returnStr = "";
		if (!"1".equals(loginmid))
			return "该商户不能进行调账";
		String[] audit_Id = auditId.split("&");
		if (auditId == "" & audit_Id.length < 1)
			return "输入参数有误";

		long[] audit_Id_ArrInt = new long[audit_Id.length];
		for (int i = 0; i < audit_Id.length; i++) {
			audit_Id_ArrInt[i] = Long.parseLong(audit_Id[i]);
		}

		// 审核成功时的处理
		if (action.equals("success")) {
			returnStr = auditSuccess(loginmid, loginuid, audit_Id_ArrInt);
		} else if (action.equals("failure")) {
			returnStr = auditFailure(loginmid, loginuid, audit_Id_ArrInt);
		}
		return returnStr;
	}

	/**
	 * 收益表查询
	 * @param pageIdex
	 * @param mid
	 * @param beginDate
	 * @param endDate
	 * @param gate
	 * @param type
	 * @return
	 */
	public List<InCome> searchIncome(String mid, int beginDate, int endDate,Integer mstate) {
		List<InCome> aList = dao.searchIncome(mid, beginDate, endDate,mstate);
		for(InCome o : aList){
			o.setBankFee(dao.getBankFeeByBatch(o.getBatch()));//统计银行手续费
			o.setIncome(o.getFeeAmt()-o.getBankFee()+o.getBkRefFee()-o.getRefFee());
		}
		return aList;
	
	}

	/**
	 * 查询商户日结表(分页)
	 * @param mid 商户ID
	 * @param beginDate 查询起始时间
	 * @param endDate 查询结束时间
	 * @return 返回 封装日结表结果bean的list
	 */
	public CurrentPage<DailySheet> getDailySheet(int page, String mid, int beginDate, int endDate,Integer mstate) {
		
		return dao.getDailySheet(page, mid, beginDate, endDate,mstate);
	}

	/**
	 * 检查结算商户信息
	 * 
	 * @param mid
	 *            商户号
	 * @param date
	 *            结算日期
	 * @return //验证结算用户是否已经对账 0--有效的结算商户 1--没有结算订单 2--没有对账
	 * @throws Exception 
	 */
	private int isCheck(String mid, int date) throws Exception {
		int flag = 0;
		//m.getLiq_limit(),该商户的结算额度。只有jt.queryForInt(queryAllAmount)大于该额度才能结算
		Map m = dao.getMap(mid, date);
		if (m.get("allBK").toString().equals("0")) { // 商户没有可以结算的订单
			flag = 1;
		} else if (m.get("minBK").toString().equals("0")) { // 结算的订单没有对账
			flag = 2;
		}
		return flag;
	}





	/**
	 * 根据调账表id查询调账详细信息
	 * 
	 * @param id
	 *            调账表ID
	 * @return 该条调账记录对象AdjustAccount
	 */
	private AdjustAccount getAdjustAccountById(long id) {
		if (id == 0)
			return null;

		try {
			AdjustAccount ad = dao.getAdjustAccountById(id);
			return ad;
		} catch (Exception e) {
			return null;
		}
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

	// 内部中调用-end----------

	/**
	 * 对账结果处理
	 * 
	 */
	public FileTransfer downloadResult(Map<String, String> p) throws Exception {

		String date_begin = p.get("date_begin");
		String date_end = p.get("date_end");
		String error_type = p.get("error_type");
		String check_date = p.get("check_date");

		if (!Ryt.isDateAllownEmpty(date_begin) || !Ryt.isDateAllownEmpty(date_end)) {
			return null;
		}

		String[] title = new String[] { "序号", "商户号", "商户简称", "电银流水号", "交易日期", "交易金额(元)", "交易银行", "银行订单号", "银行金额",
				"错误类型", "处理状态	", "对账日期", "处理说明","支付渠道" };
		List<ErrorAnalysis> datalist = dao.searchSettleResultList(date_begin, date_end, error_type, check_date);
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(title);

		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<Integer, String> mermap = obj.getMerMap();

		double totleMoney = 0;
		int i = 0;
		for (ErrorAnalysis ea : datalist) {
			i++;
			double money = ea.getMid() == null ? 0 : Digit.div(ea.getAmount(), 100, 2);
			totleMoney = Digit.add(totleMoney, money);
			String[] str = { String.valueOf(i), ea.getMid() == null ? "" : String.valueOf(ea.getMid()),
					ea.getMid() == null ? "" : mermap.get(ea.getMid()),
					ea.getTseq() == null ? "" : String.valueOf(ea.getTseq()), String.valueOf(ea.getPayDate()),
					ea.getMid() == null ? "" : String.valueOf(money),
					ea.getGate() == null ? "" : gates.get(ea.getGate()),
					ea.getBkMerOid() == null ? "" : ea.getBkMerOid(),
					ea.getBkAmount() == null ? "" : String.valueOf(Ryt.div100(ea.getBkAmount())),
					ea.getErrorType() == null ? "" : (ea.getErrorType() == 0 ? "失败交易" : "可疑交易"),
					ea.getState() == null ? "" : (ea.getState() == 0 ? "未处理" : "已处理"),
					ea.getCheckDate() == null ? "" : String.valueOf(ea.getCheckDate()),
					ea.getSolveRemark() == null ? "" : ea.getSolveRemark(), RYFMapUtil.getGateRouteMap().get(ea.getGate())};
			list.add(str);
		}
		String[] totle = new String[] { "总计", "", "", "", "", String.valueOf(totleMoney) + "元", "", "", "", "", "", "",
				"","" };
		list.add(totle);
		String[] print1 = new String[] { "", "", "", "", "", "", "", "" };
		String[] print2 = new String[] { "运行制表：", "", "制表日期：", "", "结算核实：", "", "日期：", "" };
		String[] print3 = new String[] { "运行复核：", "", "日期：", "", "结算处理：", "", "日期：", "" };
		String[] print4 = new String[] { "运行主管：", "", "日期：", "", "结算主管：", "", "日期：", "" };
		list.add(print1);
		list.add(print2);
		list.add(print3);
		list.add(print4);
		String filename = "RESULTHLOG_" + DateUtil.today() + ".xlsx";
		String name = "对账结果处理表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}

	/**
	 * 应收银行交易款
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadBankData(int[] gateRoutArr, Integer bdate, Integer edate) throws Exception {
		
		ArrayList<String[]> list = new ArrayList<String[]>();

		// String fname = "银行划款总额统计表";
		String[] title = { "银行", "支付金额", "退款金额", "银行手续费", "应收银行退回手续费", "实收银行退回手续费", "银行划款额" };
		list.add(title);

		List<MergeDetail> dataList = getMergeAmountList(gateRoutArr,  bdate,  edate);

		long totleAmount = 0;
		long totleRefAmount = 0;
		int totleBankFee = 0;
		int totlePayBank = 0;
		int totleBkFee = 0 ;
		long totleBkFeeReal = 0;
		for (MergeDetail md : dataList) {
			long payBank = md.getPayAmt() - md.getRefAmt() - md.getBankFee() + md.getBkFeeReal();
			String[] str = {
							md.getBkName(),
							Ryt.div100(md.getPayAmt()), 
							Ryt.div100(md.getRefAmt()),
							Ryt.div100(md.getBankFee()), 
							Ryt.div100(md.getBkFee()), 
							Ryt.div100(md.getBkFeeReal()),
							Ryt.div100(payBank)

			};
			list.add(str);
			totleAmount += md.getPayAmt();
			totleRefAmount += md.getRefAmt();
			totleBankFee += md.getBankFee();
			totlePayBank += payBank;
			totleBkFee += md.getBkFee();
			totleBkFeeReal += md.getBkFeeReal();
		}
		String[] nullstr = { "", "", "", "", "", "", "" };

		String[] totle = { "总计：", Ryt.div100(totleAmount), Ryt.div100(totleRefAmount), Ryt.div100(totleBankFee),
				Ryt.div100(totleBkFee), Ryt.div100(totleBkFeeReal), Ryt.div100(totlePayBank) };

		list.add(nullstr);
		list.add(totle);
		String filename = "TRANHLOG_" + DateUtil.today() + ".xlsx";
		String name = "应收银行交易款表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}

	/**
	 * 网关对应商户交易款
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadBankDataToMer(Integer gateRouteId, String bkName,Integer bdate, Integer edate) throws Exception {
		
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] title = { "银行", "商户号", "商户简称", "支付金额", "退款金额", "银行手续费", "银行退回的手续费", "银行划款额" };
		list.add(title);

		List<MergeDetail> dataList = getMergeAmountListToMer(gateRouteId,bkName,bdate,edate);

		long totleAmount = 0l;
		long totleRefAmount = 0l;
		long totleBankFee = 0l;
		long totlePayBank = 0l;
		long totleBkFeeReal = 0l;

		for (MergeDetail md : dataList) {
			long payBank = md.getPayAmt() - md.getRefAmt() - md.getBankFee() + md.getBkFeeReal();
			String[] str = { md.getBkName(), md.getMid().toString(), getHashMer().get(md.getMid()),
					Ryt.div100(md.getPayAmt()), Ryt.div100(md.getRefAmt()), Ryt.div100(md.getBankFee()),
					Ryt.div100(md.getBkFeeReal()), Ryt.div100(payBank)

			};
			list.add(str);
			totleAmount += md.getPayAmt();
			totleRefAmount += md.getRefAmt();
			totleBankFee += md.getBankFee();
			totlePayBank += payBank;
			totleBkFeeReal += md.getBkFeeReal();

		}
		String[] nullstr = { "", "", "", "", "", "", "", "" };
		String[] totle = { "总计：", "", "", Ryt.div100(totleAmount), Ryt.div100(totleRefAmount),
				Ryt.div100(totleBankFee), Ryt.div100(totleBkFeeReal), Ryt.div100(totlePayBank) };

		list.add(nullstr);
		list.add(totle);
		String filename = "TRANHLOG_" + DateUtil.today() + ".xlsx";
		String name = "网关对应商户交易款";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}

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
//				sheet.append(r.getMdate()).append(","); // 商户申请退款日期
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
	 * 商户结算单明细查询
	 * 
	 */
	@SuppressWarnings("unchecked")
	public FileTransfer downloadSettleDetail(Map<String, String> p) throws Exception {
      
		String action = p.get("a");
		String mid = p.get("mid");
		String batch = p.get("b");
//		String gate = p.get("g");
		String name = "明细表";
		String filename = "";
		List datelist = null;
		String[] title = null;
		String downTime = DateUtil.getNowDateTime();// dateFormat.format(new

		String logMid = mid == null || mid.equals("") ? "1" : mid;
		if (logMid == null)
			return null;
//		if ("qlog".equals(action)) {
//			datelist = dao.queryLiqFeeLogList(batch);
//			title = new String[] { "商户号", "商户简称", "银行网关", "支付金额", "退款金额", "系统手续费","退回商户手续费" };
//
//			filename = "QLOG";
//		}
		if ("qhlog".equals(action)) {
			datelist = dao.queryHlogList(batch);
			title = new String[] { "商户号", "商户简称", "商户交易日期", "订单号", "交易金额", "系统手续费", "交易类型", "系统日期", "交易流水号", "银行" };

			filename = "QHLOG";
		}
//		if (("list").equals(action)) {
//			String kyOrder = p.get("kyOrder");
//			String jbOrder = p.get("jbOrder");
//			ArrayList<String[]> list = new ArrayList<String[]>();
//			title = new String[] { "订单号", "交易金额", "交易日期", "交易时间", "说明" };
//			list.add(title);
//			if (kyOrder != "") {
//				for (int i = 0; i < kyOrder.split(";").length; i++) {
//					list.add(new String[] { kyOrder.split(";")[i].split(",")[0], kyOrder.split(";")[i].split(",")[1],
//							kyOrder.split(";")[i].split(",")[2], timeConvert(kyOrder.split(";")[i].split(",")[3]),
//							"可疑交易" });
//				}
//			}
//			if (jbOrder != "") {
//				for (int i = 0; i < jbOrder.split(";").length; i++) {
//					list.add(new String[] { jbOrder.split(";")[i].split(",")[0], jbOrder.split(";")[i].split(",")[1],
//							jbOrder.split(";")[i].split(",")[2], timeConvert(jbOrder.split(";")[i].split(",")[3]),
//							"交易金额不符合" });
//				}
//			}
//			filename = "SETTLEFAIl" + downTime + ".xls";
//			return new DownloadFile().downloadXLSFileBase(list, filename, "对账失败订单报表");
//		}

		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(title);

		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<Integer, String> mermap = obj.getMerMap();// (Integer.parseInt(logMid));

		for (int it = 0; it < datelist.size(); it++) {
			Map m = (Map) datelist.get(it);
//			if ("qlog".equals(action)) {
//				String pa = m.get("pur_amt").toString();
//				String ra = m.get("ref_amt").toString();
//				String fa = m.get("fee_amt").toString();
//				String refFee=m.get("ref_fee").toString();
//				String[] str = { mid, mermap.get(mid), gates.get(m.get("gate")), Ryt.div100(pa),
//						Ryt.div100(ra), Ryt.div100(fa),Ryt.div100(refFee) };
//				list.add(str);
//			}
			if ("qhlog".equals(action)) {
				String amount = m.get("amount").toString();
				String fee_amt = m.get("fee_amt").toString();
				Integer type = Integer.parseInt(m.get("type").toString());
				String merId=m.get("mid").toString();
				String sysdate = null;
				if (type == 4) {
					sysdate = m.get("mdate").toString();
				} else {
					sysdate = m.get("sys_date").toString();
				}
				String[] str = { merId, mermap.get(merId), m.get("mdate").toString(),
						m.get("oid").toString(), Ryt.div100(amount), Ryt.div100(fee_amt),
						// AppParam.tlog_type.get(type),
						RypCommon.getTlogType().get(type), sysdate, m.get("tseq").toString(),
						m.get("gate") != null ? gates.get(m.get("gate")) : "" };
				list.add(str);
			}
		}
		filename += "_" + downTime + ".xls";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
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
	 * 商户日结算
	 * 
	 */

	public FileTransfer downloadSettleDaily(Map<String, String> p) throws Exception {

		ArrayList<String[]> list = new ArrayList<String[]>();
		String mid = p.get("mid");
		int beginDate = Integer.parseInt(p.get("date_begin"));
		int endDate = Integer.parseInt(p.get("date_end"));
		String filename = DateUtil.today() + DateUtil.getCurrentUTCSeconds() + "dailySheet.xls";

		filename = "DAILY_" + DateUtil.today() + ".xls";
		String[] title = null;

		title = new String[] { "序号", "结算日期", "商户号", "商户简称", "网上支付", "WAP支付", "信用卡支付", "消费充值卡支付", "语音/快捷支付", "调增", "退款",
				"调减", "系统手续费","系统退回手续费", "结算金额", "银行手续费","银行退回手续费", "收益金额" };

		list.add(title);

		String sumNetPay = "0";
		String sumDebitCardPay = "0";
		String sumCreditCardPay = "0";
		String sumWapPay = "0";
		String sumBtobPay = "0";
		String sumManualAdd = "0";
		String sumRefund = "0";
		String sumManualSub = "0";
		String sumFeeAmt = "0";
		String sumRefFee="0";
		String sumLiqAmt = "0";
		String sumBankFee = "0";
		String sumBkRefFee="0";
		String sumIncome = "0";
		int i = 0;
		List<DailySheet> dsList = dao.getAllDailySheets(mid, beginDate, endDate);
		for (DailySheet ds : dsList) {

			String netPay = Ryt.div100(ds.getNetPay());
			String debitCardPay = Ryt.div100(ds.getDebitcardPay());
			String creditCardPay = Ryt.div100(ds.getCreditcardPay());
			String wapPay = Ryt.div100(ds.getWapPay());
			String btobPay = Ryt.div100(ds.getBtobPay());
			String manualAdd = Ryt.div100(ds.getManualAdd());
			String refund = Ryt.div100(ds.getRefund());
			String manualSub = Ryt.div100(ds.getManualSub());
			String feeAmt = Ryt.div100(ds.getFeeAmt());
			String refFee=Ryt.div100(ds.getRefFee());
			String liqAmt = "0";
			String income = "0";
			String bankFee = Ryt.div100(ds.getBankFee());
			String bkRefFee=Ryt.div100(ds.getBkRefFee());
			if (ds.getLiqType() == 1) {
				liqAmt = Ryt.div100(ds.getDebitcardPay() + ds.getNetPay() + ds.getWapPay() + ds.getCreditcardPay()
						+ ds.getBtobPay() + ds.getManualAdd() - ds.getRefund() - ds.getManualSub());
				income = "-" + bankFee;
			} else {
				liqAmt = Ryt.div100(ds.getDebitcardPay() + ds.getNetPay() + ds.getWapPay() + ds.getCreditcardPay()
						+ ds.getBtobPay() + ds.getManualAdd() - ds.getRefund() - ds.getManualSub() - ds.getFeeAmt()+ds.getRefFee());
				income = Ryt.div100(ds.getFeeAmt() - ds.getBankFee()-ds.getRefFee()+ds.getBkRefFee());
			}
			String[] str = { String.valueOf(++i), String.valueOf(ds.getLiqDate()), String.valueOf(ds.getMid()),
					ds.getAbbrev(), netPay, wapPay, creditCardPay, debitCardPay, btobPay, manualAdd, refund, manualSub,
					feeAmt,refFee, liqAmt, bankFee,bkRefFee, income };
			list.add(str);
			sumNetPay = Digit.add(sumNetPay, netPay);
			sumDebitCardPay = Digit.add(sumDebitCardPay, debitCardPay);
			sumCreditCardPay = Digit.add(sumCreditCardPay, creditCardPay);
			sumWapPay = Digit.add(sumWapPay, wapPay);
			sumBtobPay = Digit.add(sumBtobPay, btobPay);
			sumManualAdd = Digit.add(sumManualAdd, manualAdd);
			sumRefund = Digit.add(sumRefund, refund);
			sumManualSub = Digit.add(sumManualSub, manualSub);
			sumFeeAmt = Digit.add(sumFeeAmt, feeAmt);
			sumRefFee=Digit.add(sumRefFee, refFee);
			sumLiqAmt = Digit.add(sumLiqAmt, liqAmt);
			sumBankFee = Digit.add(sumBankFee, bankFee);
			sumBkRefFee=Digit.add(sumBkRefFee, bkRefFee);
			sumIncome = Digit.add(sumIncome, income);

		}

		String[] totle = { "总计：" + String.valueOf(dsList.size()) + "条记录", "", "", "", sumNetPay, sumWapPay,
				sumCreditCardPay, sumDebitCardPay, sumBtobPay, sumManualAdd, sumRefund, sumManualSub, sumFeeAmt,sumRefFee,
				sumLiqAmt, sumBankFee,sumBkRefFee, sumIncome };
		list.add(totle);
		String name = "商户日结算表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
	}

	/**
	 * 根据系统时间，和选择的银行查找hlog资金归并方法
	 */
	@SuppressWarnings("unchecked")
	public List<MergeDetail> getMergeAmountList(int[] gateRoutArr,Integer bdate,Integer edate) {
		if (gateRoutArr.length <1 )
			return null;
		List<MergeDetail> mdList = new ArrayList(gateRoutArr.length);
		for (int i = 0; i < gateRoutArr.length; i++) {
			mdList.add(doMergeAmount(gateRoutArr[i],bdate,edate));
		}
		return mdList;
	}
		

	/**
	 * 应收银行交易款  归集
	 * @param gateRouteId
	 * @param bdate
	 * @param edate
	 * @return
	 */
	private MergeDetail doMergeAmount(int gateRouteId, Integer bdate, Integer edate) {
		MergeDetail md=new MergeDetail();
		Map<String,Object> aMap1 = dao.payMerge(gateRouteId, bdate, edate);
		Map<String,Object> aMap = dao.refMerge(gateRouteId, bdate, edate);
		
		if(aMap1.get("bank_type")!=null){
			String bkName=RYFMapUtil.getGateRouteMap().get(Integer.parseInt(aMap1.get("bank_type").toString()));
			md.setBkName(bkName);
		} 
		if(aMap1.get("bank_fee")!=null) md.setBankFee(Integer.parseInt(aMap1.get("bank_fee").toString()));
		if(aMap1.get("pay_amt")!=null) md.setPayAmt(Long.parseLong(aMap1.get("pay_amt").toString()));
		
		if(aMap.get("bk_fee")!=null) md.setBkFee(Integer.parseInt(aMap.get("bk_fee").toString()));
		if(aMap.get("bk_fee_real")!=null) md.setBkFeeReal(Integer.parseInt(aMap.get("bk_fee_real").toString()));
		if(aMap.get("ref_amt") != null ) md.setRefAmt(Long.parseLong(aMap.get("ref_amt").toString()));
		
		return md;
	}

	@SuppressWarnings("unchecked")
	public List<MergeDetail> getMergeAmountListToMer(Integer gateRouteId, String bkName,Integer bdate, Integer edate) {
		
		if(gateRouteId==null ) return null;
		
		StringBuffer refSqlCon = new StringBuffer();
		refSqlCon.append(" from refund_log r where r.vstate=1 and r.ref_date BETWEEN ").append(bdate).append(" AND ");
		refSqlCon.append(edate);
		refSqlCon.append(" and r.gid= ").append(gateRouteId);
		
		//System.out.println(paySql+"\n"+refSqlCon);
		List<MergeDetail> retList = dao.payMergeToMer(gateRouteId, bkName, bdate, edate);
		StringBuilder mids = new StringBuilder("(0");
		for(MergeDetail md : retList){
			mids.append(",").append(md.getMid());
			String refSql2 = "select sum(r.ref_amt) as rm,sum(bk_fee_real) as bkf " + refSqlCon.toString()+ " and mid = "+ md.getMid();
			Map<String,Object> aMap = dao.getJdbcTemplate().queryForMap(refSql2);
			//refSql.append(" select sum(r.ref_amt) as ref_amt,sum(bk_fee_real) as bk_fee_real ");
			if(aMap.get("rm")!=null) md.setRefAmt(Long.parseLong(aMap.get("rm").toString()));
			if(aMap.get("bkf")!=null) md.setBkFeeReal(Integer.parseInt(aMap.get("bkf").toString()));
		}
		mids.append(")");
		
		StringBuffer refSql = new StringBuffer(" select '"+bkName+"' as bk_name, r.mid as mid , sum(r.ref_amt) as ref_amt,sum(bk_fee_real) as bk_fee_real ");
		refSql.append(refSqlCon);
		refSql.append(" and r.mid not in ").append(mids).append(" group by r.mid ");
		
		retList.addAll(dao.getJdbcTemplate().query(refSql.toString(), new BeanPropertyRowMapper(MergeDetail.class)));
		
		return retList;
		
	}

	/**
	 * 收益表下载xls
	 * @throws Exception
	 */
	public FileTransfer downloadIncomeSheet(String mid ,int bdate,int edate,Integer mstate) throws Exception {
		RYFMapUtil util = RYFMapUtil.getInstance();
		Map<Integer, String> minfoMap = util.getMerMap();
//		Map<Integer, String> gatesMap = util.getGateMap();
//		Map<Integer, String> typeMap = AppParam.tlog_type;
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] incomeTitle = { "结算日期", "商户号", "商户简称","交易金额", "退款金额", "系统手续费", "系统退回手续费", "银行手续费", "银行退回手续费","收益金额" };
		list.add(incomeTitle);
		List<InCome> incomeSheet = searchIncome(mid, bdate, edate,mstate);
		long all_amount =  0 ;
		long all_fee_amt = 0 ;
		int all_bank_fee = 0 ;
		int all_income = 0 ;
		int all_ref_fee =  0 ;
		int all_bk_ref_fee =  0 ;
		int all_ref_amt = 0 ;
		
		for (InCome o : incomeSheet) {
			all_amount += o.getAmount();
			all_ref_amt += o.getRefAmt();
			all_fee_amt += o.getFeeAmt();
			all_ref_fee += o.getRefFee();
			all_bk_ref_fee += o.getBkRefFee();
			all_bank_fee += o.getBankFee();
			all_income += o.getIncome();
			String[] incomeString = { 
							o.getLiqDate()+"",
							o.getMid()+"",
							minfoMap.get(o.getMid()),
					        //"-1".equals(gate) ? "全部" : gatesMap.get(Integer.parseInt(gate)),
							//typeMap.get(Integer.parseInt(m.get("type").toString())), 
							Ryt.div100(o.getAmount()),
							Ryt.div100(o.getRefAmt()), 
							Ryt.div100(o.getFeeAmt()), 
							Ryt.div100(o.getRefFee()), 
							Ryt.div100(o.getBankFee()),
							Ryt.div100(o.getBkRefFee()), 
							Ryt.div100(o.getIncome()) };
				list.add(incomeString);
			}
			String[] st1 = { "", "", "", "", "", "", "", "", "", "" };
			list.add(st1);
			String[] st2 = { "总计:", "", "共" + incomeSheet.size() + "条记录",  
							Ryt.div100(all_amount),
							Ryt.div100(all_ref_amt), 
							Ryt.div100(all_fee_amt),
							Ryt.div100(all_ref_fee), 
							Ryt.div100(all_bank_fee), 
							Ryt.div100(all_bk_ref_fee), 
							Ryt.div100(all_income) };
			list.add(st2);

		return new DownloadFile().downloadXLSXFileBase(list, DateUtil.getNowDateTime() + "incomeSheet.xlsx", "收益表");

	}

	/**
	 * 结算制表下载
	 * @param batch 结算批次号
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadSettleTB(String batch) throws Exception {
		String fileName = batch + "settleTB.xls";
		File settlementFile = new File(Ryt.getParameter("SettlementFilePath") + fileName);
		if (settlementFile.exists()) {
			BufferedInputStream bis = null;
			try {
				InputStream is = new FileInputStream(settlementFile);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				buffer.write(is);
				return new FileTransfer(fileName, "application/x-xls", buffer.toByteArray());
			} catch (IOException e) {
				throw e;
			} finally {
				if (bis != null)
					bis.close();
			}
		} else {
			return null;
		}
	}
	public List<AccInfos> searchBalance(String mid){
		return dao.searchBalance(mid);
		
	}
	
	/**
	 * 获取手机验证码
	 */
	public void getVfCode(){
		Random rdhandle=new Random();
		VERIFYCODE=rdhandle.nextInt(999999)+1+"";
		EmaySMS.sendSMS(new String[]{VERIFYPHONENO},"您的验证码为："+VERIFYCODE+"。此验证码仅操作手工调帐有效。");
	}
	
	/**
	 * 验证手机验证码
	 * @param code 输入的验证码
	 * @return 0表示通过 1表示不通过
	 */
	public int verifyCode(String code){
		if(VERIFYCODE.equals("000000")){
			return 1;
		}
		if(null!=code.trim()&&!"".equals(code.trim())&&VERIFYCODE.equals(code.trim())){
			VERIFYCODE="000000";
			return 0;
			}
		return 1;
	}
	
	/**
	 * 手工调帐
	 * @param vfcode 短信验证码 
	 * @param mid 商户号 
	 * @param type 操作类型
	 * @param amt 操作金额
	 * @param fee 手续费
	 * @param tbName 表名
	 * @param tseq 流水号
	 * @param remark 备注说明
	 * @return 0表示成功，1表示失败
	 */
	public int accountSGDZ(String vfcode,String mid,String type,String amt,String fee,String tbName,String tseq,String remark){
		if(verifyCode(vfcode)==1){
			return 1;
		}
		//======插入
		
		//=======
		return 0;
	}
	
	public int accountAmtChange(String vfcode,String mid,String type,String amt){
		if(verifyCode(vfcode)==1){
			return 1;
		}
		return 0;
	}
	
	
/*
 * 划款结果查询
 */
	
	public CurrentPage<TransferMoney> querytransfer(Integer pageNo,String mid,Integer bdate,Integer edate,String batchNo,Integer tstat){
		return dao.querytransfer(pageNo,new AppParam().getPageSize(),mid,bdate, edate,batchNo,tstat);
		
	}
/*
 * 划款结果下载
 */
	
  public FileTransfer downloadtransfer(String mid,Integer bdate,Integer edate,String batchNo,Integer tstat){
	  CurrentPage<TransferMoney> TransferMoneyListPage=dao.querytransfer(1,-1,mid,bdate, edate,batchNo,tstat);
	  	List<TransferMoney> TransferMoneyList=TransferMoneyListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		String totleAmount=Ryt.div100(TransferMoneyListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		String totleSysAmtFee=Ryt.div100(TransferMoneyListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,联行行号,商户号,结算批次号,结算确认日期,商户名称,开户银行名称,开户账户名称,开户账户号,划款状态,划款金额,交易银行,支付渠道,失败原因".split(","));
		
		int i = 0;
		String gateRoute = "";
		for (TransferMoney h : TransferMoneyList) {
			if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			}else{
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq(),
					h.getBankNo().toString(),
					h.getMid(),
					h.getBatch(),
					h.getLiqDate() + "",
					h.getName(),
					h.getBankName(),
					h.getBankAcctName(),
					h.getBankAcct(),
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					h.getAmount() + "",
					gates.get(h.getGate()),
					gateRoute,
					h.getErrorMsg() == null ? h.getErrorCode() : h
							.getErrorMsg() };
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "","", "","" , "","","","",totleAmount,   "", "", ""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "划款结果查询";
		try {			
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
  
	/*
	 * 商户账务查询
	 */
	public CurrentPage<MerAccount> queryaccounts(Integer pageNo,String mid, Integer category, Integer bdate, Integer edate){
	  int pageSize = ParamCache.getIntParamByName("pageSize");
	  return dao.queryMerAccounts(pageNo,pageSize,mid,category,bdate,edate);
	  
  }
	
	/*
	 * 商户账务查询下载
	 */
	
	public FileTransfer downaccounts(String mid, Integer category, Integer bdate, Integer edate) throws Exception{
		 CurrentPage<MerAccount> AccountsListPage =dao.queryMerAccounts(1,-1, mid, category,bdate, edate);
		 List<MerAccount> AccountList=AccountsListPage.getPageItems();
		   ArrayList<String[]> list = new ArrayList<String[]>();
			String[] title = {"序号","商户号","商户名称","商户简称","商户类别","上期账户余额（元）","起始交易日期","交易金额（元）","系统手续费（元）","退款金额（元）",
					"退回商户手续费（元）","结算金额（元）","调增（元）","调减（元）","结束交易日期","本期账户余额（元）"};
			list.add(title);
			long PreviousBalance=0; 
			long TrAmt=0;
			long FeeAmt=0;
			long RefAmt=0;
			long RefFeeAmt=0;
			long LiqAmt=0;
			long ManualAdd=0;
			long ManualSub=0;
			long CurrentBalance=0;
			int i=0;
			for (MerAccount Account:AccountList) {
				String[] strArr={
						String.valueOf(i+1),
						String.valueOf(Account.getMid()),
						Account.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(Account.getMid()),
					    String.valueOf(Account.getAbbrev()),
						String.valueOf(Account.getCategory()==0?"RYF商户":Account.getCategory()==2?"POS商户":"VAS商户"),
						Ryt.div100(null==Account.getPreviousBalance()?0:Account.getPreviousBalance()),
						String.valueOf(bdate),/*交易金额计算 不区分商户类型 */
//						Ryt.div100(Account.getCategory()==1?Account.getTransAmt()-Account.getRefAmt():Account.getTransAmt()),
//						Ryt.div100(Account.getCategory()==1?Account.getFeeAmt()-Account.getRefFeeAmt():Account.getFeeAmt()),
						Ryt.div100(null==Account.getTransAmt()?0:Account.getTransAmt()),
						Ryt.div100(null==Account.getFeeAmt()?0:Account.getFeeAmt()),
						Ryt.div100(null==Account.getRefAmt()?0:Account.getRefAmt()),
						Ryt.div100(null==Account.getRefFeeAmt()?0:Account.getRefFeeAmt()),
						Ryt.div100(null==Account.getLiqAmt()?0:Account.getLiqAmt()),
						Ryt.div100(null==Account.getManualAdd()?0:Account.getManualAdd()),
						Ryt.div100(null==Account.getManualSub()?0:Account.getManualSub()),
						String.valueOf(edate),
						Ryt.div100(null==Account.getCurrentBalance()?0:Account.getCurrentBalance()),
						
					};
				PreviousBalance+=Account.getPreviousBalance();
//				TrAmt+=Account.getCategory()==1?Account.getTransAmt()-Account.getRefAmt():Account.getTransAmt();
//				FeeAmt+=Account.getCategory()==1?Account.getFeeAmt()-Account.getRefFeeAmt():Account.getFeeAmt();
				TrAmt+=Account.getTransAmt();
				FeeAmt+=Account.getFeeAmt();
				RefAmt+=Account.getRefAmt();
				RefFeeAmt+=Account.getRefFeeAmt();
				LiqAmt+=Account.getLiqAmt();
				ManualAdd+=Account.getManualAdd();
				ManualSub+=Account.getManualSub();
				CurrentBalance+=Account.getCurrentBalance();
				i += 1;
				list.add(strArr);
			}
			/*String[] str = {"","","总计:" +i + "条记录","",Ryt.div100(PreviousBalance),"",Ryt.div100(TrAmt),Ryt.div100(FeeAmt),Ryt.div100(RefAmt),
					Ryt.div100(RefFeeAmt),Ryt.div100(LiqAmt),Ryt.div100(ManualAdd),Ryt.div100(ManualSub),"",Ryt.div100(CurrentBalance)};
			list.add(str);*/
			String filename = "MerAccounts_" + DateUtil.today() + ".xlsx";
			String name = "商户账务报表";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
	/**
	 * 
	 * @param bdate
	 * @param edate
	 * @param gid
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<TradeStatistics> queryTradeStatisticByGid(Integer bdate,Integer edate,String gid,Integer pageNo){
		
		return dao.queryTradeStatisticByGid(bdate, edate, gid, pageNo,new AppParam().getPageSize());
	}
	
	public CurrentPage<TradeStatistics> queryTradeStatisticByMid(Integer bdate,Integer edate,String mid,Integer pageNo){
		
		return dao.queryTradeStatisticByMid(bdate, edate, mid, pageNo,new AppParam().getPageSize());
	}
	
	public Map<String, List<TradeStatistics>> queryDataLstByInstIdForExcel(Integer bdate,Integer edate,String gid) {
		Map<String, List<TradeStatistics>> map_data = null;
		try {
			List<TradeStatistics>  list = dao.getStatisticsDetailGid(bdate, edate, gid);
			if(list != null && list.size() > 0){
				map_data = new HashMap<String, List<TradeStatistics>>();
				for (TradeStatistics statistics : list) {
					String name_ = statistics.getGateName();
					List<TradeStatistics> value_list = map_data.get(name_);
					List<TradeStatistics> list_data = null;
					if(value_list == null){
						list_data = new ArrayList<TradeStatistics>();
						list_data.add(statistics);
						map_data.put(name_,list_data);
					}else{
						list_data = value_list;
						list_data.add(statistics);
						map_data.put(name_, list_data);
					}
				}
			}
		} catch (Exception e) {
			//log.error(e.getMessage());
		}
		return map_data;
	}
	
	public FileTransfer downStatisticsGid11(Integer bdate,Integer edate,String gid,HttpServletResponse response) throws Exception{
		//Excel表头
		String[] header = {"网关表-按扣款渠道"};
		String[] headerTop = {"扣款渠道", "商户号", "商户简称", "支付金额", "支付汇总金额"};
		int date = DateUtil.today();
		// 创建新的Excel 工作簿
	    HSSFWorkbook workbook = new HSSFWorkbook();
	    // 在Excel 工作簿中建一工作表
	    HSSFSheet sheet = workbook.createSheet("网关表(按渠道)");
	    // 单元格合并      
        // 四个参数分别是：起始行，起始列，结束行，结束列      
        sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 5));
        
        sheet.setColumnWidth(0, 6500);
        sheet.setColumnWidth(1, 6500);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 5500);
        sheet.setColumnWidth(4, 5500);
	    
	    // 设置单元格格式(文本)
	 	HSSFCellStyle cellStyle = workbook.createCellStyle();
	 	cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	 	cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	 	
	 	// 设置表格底部边框
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	   // 设置表格左边边框
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	   // 设置表格右边边框
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	   // 设置表格顶部边框
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	   // 设置表格中间横线
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	   // 设置表格中间竖线
	    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    
	    //创建抬头(标题)
	    CreateExcelUtil.createHeader(workbook,sheet,header);
	    CreateExcelUtil.createTop(workbook,sheet,headerTop, cellStyle);
	   
	    Map<String, List<TradeStatistics>> mapList = queryDataLstByInstIdForExcel( bdate, edate, gid);
		if (mapList != null && mapList.keySet().size() > 0) {
			String[] data = null;
			int row = 2; // 已经定义了两行抬头内容
		    int row_size = 0; // 记录每个list大小
			String totalAmount = "0";
			int totalCount = 0;
			for (String key : mapList.keySet()) {
				List<TradeStatistics> list_data = mapList.get(key);
				row_size = list_data.size();
				String tradeAmount = "0";
				//int tradeCount = 0;
				for (TradeStatistics merchantSettleStatistics : list_data) {
					tradeAmount =  CreateExcelUtil.add(merchantSettleStatistics.getPayAmt().toString(),tradeAmount);
					
					data = new String[] {
							"",
							merchantSettleStatistics.getMid(),
							merchantSettleStatistics.getName(),
							Ryt.div100(null==merchantSettleStatistics.getPayAmt()?0:merchantSettleStatistics.getPayAmt()),
							"",
							
					};
					CreateExcelUtil.output(cellStyle,sheet,row,data);
					data = null;
					row++;
				}
				
				totalAmount = CreateExcelUtil.add(totalAmount,tradeAmount);
				//totalCount += tradeCount;
				
				HSSFRow hssfRow = sheet.getRow(row-row_size);
				//赋值渠道名称
				hssfRow.getCell(0).setCellValue(key);
				sheet.addMergedRegion(new Region(row-row_size, (short) 0, row - 1, (short) 0));
				//赋值支付汇总金额
				hssfRow.getCell(4).setCellValue(Ryt.div100(tradeAmount));
				sheet.addMergedRegion(new Region(row-row_size, (short) 4, row - 1, (short) 4));
//				//赋值支付支付笔数
				//hssfRow.getCell(5).setCellValue(tradeCount);
				//sheet.addMergedRegion(new Region(row-row_size, (short) 5, row - 1, (short) 5));
			}
			
			//写入数据统计行
			data = new String[]{"总计:" + (row-2) + "条记录", "", "", "",Ryt.div100(totalAmount)+""};
			CreateExcelUtil.output(cellStyle,sheet,row,data);
			
			FileTransfer f= CreateExcelUtil.createExcel(response, workbook,"TRANHLOG_bank_"+ date +".xls");
			return f;
			
		}
		return null;
	}
	
	public Map<String, List<TradeStatistics>> queryDataLstByMidForExcel(Integer bdate,Integer edate,String mid) {
		Map<String, List<TradeStatistics>> map_data = null;
		try {
			List<TradeStatistics>  list = dao.getStatisticsDetailMid(bdate, edate, mid);
			if(list != null && list.size() > 0){
				map_data = new HashMap<String, List<TradeStatistics>>();
				for (TradeStatistics statistics : list) {
					String name_ = statistics.getMid();
					List<TradeStatistics> value_list = map_data.get(name_);
					List<TradeStatistics> list_data = null;
					if(value_list == null){
						list_data = new ArrayList<TradeStatistics>();
						list_data.add(statistics);
						map_data.put(name_,list_data);
					}else{
						list_data = value_list;
						list_data.add(statistics);
						map_data.put(name_, list_data);
					}
				}
			}
		} catch (Exception e) {
			//log.error(e.getMessage());
		}
		return map_data;
	}
	
	public FileTransfer downStatisticsmid11(Integer bdate,Integer edate,String mid,HttpServletResponse response) throws Exception{
		//Excel表头
		String[] header = {"商户对应渠道交易明细"};
		String[] headerTop = {"商户号", "商户名称","渠道名称", "支付金额", "汇总"};
		int date = DateUtil.today();
		// 创建新的Excel 工作簿
	    HSSFWorkbook workbook = new HSSFWorkbook();
	    // 在Excel 工作簿中建一工作表
	    HSSFSheet sheet = workbook.createSheet("商户对应渠道交易明细");
	    // 单元格合并      
        // 四个参数分别是：起始行，起始列，结束行，结束列      
        sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 5));
        
        sheet.setColumnWidth(0, 6500);
        sheet.setColumnWidth(1, 6500);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 5500);
        sheet.setColumnWidth(4, 5500);
	    
	    // 设置单元格格式(文本)
	 	HSSFCellStyle cellStyle = workbook.createCellStyle();
	 	cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	 	cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	 	
	 	// 设置表格底部边框
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	   // 设置表格左边边框
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	   // 设置表格右边边框
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	   // 设置表格顶部边框
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	   // 设置表格中间横线
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	   // 设置表格中间竖线
	    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	    
	    //创建抬头(标题)
	    CreateExcelUtil.createHeader(workbook,sheet,header);
	    CreateExcelUtil.createTop(workbook,sheet,headerTop, cellStyle);
	   
	    Map<String, List<TradeStatistics>> mapList = queryDataLstByMidForExcel( bdate, edate, mid);
		if (mapList != null && mapList.keySet().size() > 0) {
			String[] data = null;
			int row = 2; // 已经定义了两行抬头内容
		    int row_size = 0; // 记录每个list大小
			String totalAmount = "0";
			int totalCount = 0;
			for (String key : mapList.keySet()) {
				List<TradeStatistics> list_data = mapList.get(key);
				row_size = list_data.size();
				String tradeAmount = "0";
				String name=list_data.get(0).getName();
				//int tradeCount = 0;
				for (TradeStatistics merchantSettleStatistics : list_data) {
					tradeAmount =  CreateExcelUtil.add(merchantSettleStatistics.getPayAmt().toString(),tradeAmount);
					
					data = new String[] {
							"",
							"",
							merchantSettleStatistics.getGateName(),
							Ryt.div100(null==merchantSettleStatistics.getPayAmt()?0:merchantSettleStatistics.getPayAmt()),
							"",
							
					};
					CreateExcelUtil.output(cellStyle,sheet,row,data);
					data = null;
					row++;
				}
				
				totalAmount = CreateExcelUtil.add(totalAmount,tradeAmount);
				//totalCount += tradeCount;
				
				HSSFRow hssfRow = sheet.getRow(row-row_size);
				//赋值商户号
				hssfRow.getCell(0).setCellValue(key);
				sheet.addMergedRegion(new Region(row-row_size, (short) 0, row - 1, (short) 0));
				//赋值商户名
				hssfRow.getCell(1).setCellValue(name);
				sheet.addMergedRegion(new Region(row-row_size, (short) 1, row - 1, (short) 1));
				//赋值支付汇总金额
				hssfRow.getCell(4).setCellValue(Ryt.div100(tradeAmount));
				sheet.addMergedRegion(new Region(row-row_size, (short) 4, row - 1, (short) 4));
//				//赋值支付支付笔数
				//hssfRow.getCell(5).setCellValue(tradeCount);
				//sheet.addMergedRegion(new Region(row-row_size, (short) 5, row - 1, (short) 5));
			}
			
			//写入数据统计行
			data = new String[]{"总计:" + (row-2) + "条记录", "", "", "",Ryt.div100(totalAmount)+""};
			CreateExcelUtil.output(cellStyle,sheet,row,data);
			
			FileTransfer f= CreateExcelUtil.createExcel(response, workbook,"TRANHLOG_bank_"+ date +".xls");
			return f;
			
		}
		return null;
	}
	
	
}
