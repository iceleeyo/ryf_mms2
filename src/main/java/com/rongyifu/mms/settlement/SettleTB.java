package com.rongyifu.mms.settlement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.Digit;

@SuppressWarnings("rawtypes")
public class SettleTB extends PubDao{
	
	private  int TODAY = DateUtil.today();// 系统当前日期
//	private static String SettlementFilePath = null;
	
	public SettleTB() {
		
	}
//	
//	static{
//		SettlementFilePath = Ryt.getParameter("SettlementFilePath");
//	}
	/**
	 * @param batchs 需要结算制表的结算批次号数组
	 * @param operName 结算制表人的名字
	 * @return 制表操作结果
	 * @throws IOException
	 */
	public  String drawSettleTB(String[] batchs) throws IOException {
		try { 
			for (String ibatch : batchs) {
				
				String batch = Ryt.sql(ibatch);
				String mid = batch.substring(0,batch.length()-8);
				
				//更新流水表
				Map m = queryForMap("select liq_date,liq_amt,fee_amt,liq_type from fee_liq_bath where batch='" + batch+"'");
				
				int liq_date = Integer.parseInt(m.get("liq_date").toString());
				List<String> sqlList = new ArrayList<String>();
				//更新商户结算批次表
				SettlementDao dao=new SettlementDao();
				Minfo minfo= dao.quertliqbymid(mid);
				int liqobj = minfo.getLiqObj();
				int liqgid = 0;
				if(liqobj==1){ // 结算对象：电银账户
					int autodfstate=minfo.getAutoDfState();
					if(autodfstate==1){ // 自动代付状态：开启
						liqgid=4; //　自动代付 - 结算到电银账户
					}else{
						liqgid=1; // 手工结算 - 结算到电银账户
					}
				}else if(liqobj == 0){ // 结算对象：银行卡
					int manliq = minfo.getManLiq();
					if(manliq==0){ // 手工结算：关闭
						liqgid=3; // 自动结算 - 结算到银行卡
					}else{
						liqgid=2; // 手工结算 - 结算到银行卡
					}
				}else if (liqobj == 2){
					liqgid=5;
				}
				String updateFLB = "update fee_liq_bath set state=2,liq_gid="+liqgid+",tab_date=date_format(now(),'%Y%m%d') where batch = '" + batch+"'";
				//向日结临时表insert_set_temporary中插入数据 供每天的汇总用
				String insert_set_temporary = "insert into set_temporary (mid,liq_date,batch) values ("+mid+","+liq_date+",'"+batch+"')";
				sqlList.add(updateFLB);
				sqlList.add(insert_set_temporary);
				String[] sqlsList = sqlList.toArray(new String[sqlList.size()]);
				batchSqlTransaction(sqlsList);
				
			}
			return "制表成功!";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "制表失败!";
		}
	}

	public  Map getTableData(String batch,String mid) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select flb.* ,m.name as mName,m.bank_acct_name as accountName,");
		sql.append("bank_name as bankN,m.bank_branch as bankBra ,m.bank_acct as mBankId ,m.abbrev as mAbbrev,m.liq_obj as liqobj");
		sql.append(" from fee_liq_bath flb,minfo m where flb.mid = m.id and flb.batch='").append(batch).append("'");
		
		Map m = queryForMap(sql.toString());

		String purAccount = Digit.percent(m.get("trans_amt").toString()); // 支付金额
		String refAccount = Digit.percent(m.get("ref_amt").toString()); // 退款金额
		String feeAmt = Digit.percent(m.get("fee_amt").toString()); // 系统手续费
		String liqAmt = Digit.percent(m.get("liq_amt").toString()); // 清算金额
		String manualAddAmt = Digit.percent(m.get("manual_add").toString()); // 手工增加
		String manualSubAmt = Digit.percent(m.get("manual_sub").toString()); // 手工减少
		String mBank = m.get("bankN") + "(" + m.get("bankBra") + ")" ;
		String lastLiqDate = m.get("last_liq_date") == null ? "0" : m.get("last_liq_date").toString(); // 上次结算日期
		Map<String, String> dataMap = new HashMap<String, String>();
		// 查询手工调账表
		lastLiqDate = lastLiqDate.equals("0") ? "--/--/--" : lastLiqDate;
		dataMap.put("mid", mid);
		dataMap.put("abbrev", ""+m.get("mAbbrev")); 
		dataMap.put("today", TODAY+"");
		dataMap.put("batch", m.get("batch").toString());
		dataMap.put("mName", "" + m.get("mName"));
		if(m.get("liqobj").toString().equals("1")){
			dataMap.put("accountName", "");
			dataMap.put("mBank", "");
			dataMap.put("mBankId", "");			
		}else{
		dataMap.put("accountName", "" + m.get("accountName"));
		dataMap.put("mBank", "" + mBank);
		dataMap.put("mBankId", "" + m.get("mBankId"));
		}
		dataMap.put("beginDate", "" + lastLiqDate);
		dataMap.put("purAccount", purAccount);
		dataMap.put("refAccount", refAccount);
		dataMap.put("purCount", "" + m.get("pur_cnt").toString());
		dataMap.put("refCount", "" + m.get("ref_cnt").toString());
		dataMap.put("endDate", m.get("liq_date").toString());
//		dataMap.put("liqType", liqType);//去掉净额全额之分
		dataMap.put("liqType", "--");//去掉净额全额之分
		dataMap.put("manualAddAccount",  manualAddAmt);
		dataMap.put("manualSubAccount",  manualSubAmt);
		dataMap.put("manualAddCount", m.get("add_cnt").toString());
		dataMap.put("manualSubCount", m.get("sub_cnt").toString());
		dataMap.put("feeAccount", feeAmt);
		dataMap.put("refFee",Digit.percent(m.get("ref_fee").toString())) ;
		dataMap.put("liqAccount", liqAmt);
		dataMap.put("lister", getLoginUserName());
		
		return dataMap;
	}
}



//package net.rypay.mms.settlement;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URLDecoder;
//import java.sql.Connection;
//import java.text.SimpleDateFormat;
//import java.util.HashMap;
//import java.util.Map;
//
//import net.rypay.mms.common.Ryt;
//import net.sf.jasperreports.engine.JRExporterParameter;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.export.JRXlsExporter;
//import net.sf.jasperreports.engine.util.JRLoader;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import com.chinaebi.pay.mms.db.PubDao;
//import com.rongyifu.mms.utils.DateUtil;
//import com.rongyifu.mms.utils.Digit;
//
//public class SettleTB {
//
//	/**
//	 * Constructor of the object.
//	 */
//	public SettleTB() {
//	}
//	/**
//	 * 
//	 * @param batchs 需要结算制表的结算批次号数组
//	 * @param operName 结算制表人的名字
//	 * @return 制表操作结果
//	 * @throws IOException
//	 */
//	@SuppressWarnings({ "unchecked", "deprecation" })
//	public static String drawSettleTB(String[] batchs) throws IOException {
//		PubDao dao = new PubDao() ;
//		JdbcTemplate jt = dao.getJdbcTemplate();
//		String msg = "";
//		Connection con = dao.getConnection();
//		int now_d = DateUtil.today();// 系统当前日期
//		int now_t = DateUtil.getCurrentUTCSeconds();// 系统当前时间
//		String SettlementFilePath = Ryt.getParameter("SettlementFilePath");
//		try { 
//			for (String batch : batchs) {
//				String mid = batch.substring(0,batch.length()-8);
//				JasperFillManager.fillReportToFile(URLDecoder.decode(SettleTB.class
//					.getResource("../../../../../../settleTB.jasper").getPath()), getTableData(jt, dao.getLoginUserName(), batch), con);
//				JasperPrint jp = (JasperPrint) JRLoader.loadObject(URLDecoder.decode(SettleTB.class
//					.getResource("../../../../../../settleTB.jrprint").getPath()));
//				File dir = new File(SettlementFilePath);
//				if(!dir.exists()) {
//					dir.mkdir();
//				}
//				String fileName = SettlementFilePath + batch + "settleTB.xls";
//				JRXlsExporter exporter = new JRXlsExporter();
//				// 指定要导出的jrprit数据
//				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
//				// 指定导出文件的文件名
//				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
//				// 实现报表的导出
//				exporter.exportReport();
//				
//				//更新流水表
//				Map m = jt.queryForMap("select liq_date,liq_amt,fee_amt,liq_type from fee_liq_bath where batch=" +Ryt.sql( batch));
//				
//				int liq_date = Integer.parseInt(m.get("liq_date").toString());
//				int balance = jt.queryForInt("select balance from minfo where id=" + mid);
//				
//				int temp = Integer.parseInt(m.get("liq_amt").toString());
////				int fee = Integer.parseInt(m.get("fee_amt").toString());
////				int tradeAmt = (Integer)m.get("liq_type") == 1 ? temp : temp + fee;
//				//去掉净额全额之分
//				int fee = 0;
//				int tradeAmt = temp;
//				
//				String accountSql = "insert into account (mid,tseq,type,date,time,account,balance,fee,amount) values (" + mid + "," + Ryt.sql( batch)
//				+ "," + 3 + "," + now_d + "," + now_t + "," + temp + "," + (balance - temp) + "," + fee + "," + tradeAmt +")";
//				//更新商户表
//				String updateMinfo = "update minfo set balance=" + (balance - temp) + " where id=" + mid;
//				//更新商户结算批次表
//				String updateFLB = "update fee_liq_bath set state=2 where batch="+Ryt.sql( batch);
//				//向日结临时表insert_set_temporary中插入数据 供每天的汇总用
//				String insert_set_temporary = "insert into set_temporary (mid,liq_date,batch) values ("+mid+","+liq_date+","+Ryt.sql( batch)+")";
//				String[] sqls = {accountSql,updateMinfo,updateFLB,insert_set_temporary};
//				dao.batchSqlTransaction(sqls);
//			}
//			msg = "制表成功!";
//		} catch (Exception e) {
//			//e.printStackTrace();
//			msg = "制表失败!";
//		}
//		return msg;
//	}
//
//	@SuppressWarnings("unchecked")
//	/**
//	 * 返回一个填充资金结算表数据的Map
//	 * 
//	 * @param jt
//	 *            数据库连接
//	 * @param lister
//	 *            制表人
//	 * @param batch
//	 *            批次号
//	 * @return Map
//	 */
//	public static Map getTableData(JdbcTemplate jt, String lister, String batch) {
//		
//		Map m = jt
//				.queryForMap("select flb.* ,m.name as mName,m.bank_acct_name as accountName," 
//					+ "bank_name as bankN,m.bank_branch as bankBra ,m.bank_acct as mBankId"
//				    + " from fee_liq_bath flb,minfo m where flb.mid = m.id and flb.batch=" + batch);
//
//		String purAccount = Digit.percent(m.get("trans_amt").toString()); // 支付金额
//		String refAccount = Digit.percent(m.get("ref_amt").toString()); // 退款金额
//		String feeAmt = Digit.percent(m.get("fee_amt").toString()); // 系统手续费
//		
//		feeAmt = feeAmt + ",退回商户手续费:" + Digit.percent(m.get("ref_fee").toString());
//		
//		String liqAmt = Digit.percent(m.get("liq_amt").toString()); // 清算金额
//		String manualAddAmt = Digit.percent(m.get("manual_add").toString()); // 手工增加
//		String manualSubAmt = Digit.percent(m.get("manual_sub").toString()); // 手工减少
//		String mBank = m.get("bankN") + "(" + m.get("bankBra") + ")" ;
//		String lastLiqDate = m.get("last_liq_date") == null ? "0" : m.get(
//				"last_liq_date").toString(); // 上次结算日期
//		String liqType = (Integer) m.get("liq_type") == 1 ? "全额结算" : "净额结算"; // 结算类型
//		Map<String, String> dataMap = new HashMap<String, String>();
//		/** 表中数据已经进行过处理 。yuanzy */
//		// 查询手工调账表
//		// liqAmt = Digit.add(liqAmt, Digit.sub(manualAddAmt, manualSubAmt));
//		String today = new SimpleDateFormat("yyyyMMdd")
//				.format(new java.util.Date());
//		lastLiqDate = lastLiqDate.equals("0") ? "--/--/--" : lastLiqDate;
//		dataMap.put("mid", batch.substring(0, batch.length() - 8));
//		dataMap.put("today", today);
//		dataMap.put("batch", m.get("batch").toString());
//		dataMap.put("mName", "" + m.get("mName"));
//		dataMap.put("accountName", "" + m.get("accountName"));
//		dataMap.put("mBank", "" + mBank);
//		dataMap.put("mBankId", "" + m.get("mBankId"));
//		dataMap.put("beginDate", "" + lastLiqDate);
//		dataMap.put("purAccount", purAccount);
//		dataMap.put("refAccount", refAccount);
//		dataMap.put("purCount", "" + m.get("pur_cnt").toString());
//		dataMap.put("refCount", "" + m.get("ref_cnt").toString());
//		dataMap.put("endDate", m.get("liq_date").toString());
//		dataMap.put("liqType", liqType);
//		dataMap.put("manualAddAccount",  manualAddAmt);
//		dataMap.put("manualSubAccount",  manualSubAmt);
//		dataMap.put("manualAddCount", m.get("add_cnt").toString());
//		dataMap.put("manualSubCount", m.get("sub_cnt").toString());
//		dataMap.put("feeAccount", feeAmt);
//		dataMap.put("liqAccount", liqAmt);
//		dataMap.put("lister", lister);
//		return dataMap;
//	}
//}
