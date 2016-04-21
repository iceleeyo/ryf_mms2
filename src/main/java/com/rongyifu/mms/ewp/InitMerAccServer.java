package com.rongyifu.mms.ewp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.rongyifu.mms.bean.MerAccount;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/*****
 * 初始化商户账务信息表
 * @author shdy
 *
 */
public class InitMerAccServer{
	private	Logger log=Logger.getLogger(InitMerAccServer.class);
	public static void main(String[] args){
		InitMerAccServer accServer=new InitMerAccServer();
		accServer.initMerAcc();
	}

	public  void initMerAcc(){
		
		LogUtil.printInfoLog("InitMerAccServer", "initMerAcc", "init success");
		String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
		int previousEDate=DateUtil.getThisMonthLastDate(DateUtil.getLastMonthDate(Integer.parseInt(date)));
		int previousBDate=Integer.parseInt(String.valueOf(previousEDate).substring(0, 6).concat("01"));
		saveMerAccounts(previousEDate, previousBDate);
		
	}	
	
	/***
	 * 外部接口调用方法
	 * @param previousBDate
	 * @param previousEDate
	 */
	public String initMerAcc(Integer previousBDate,Integer previousEDate ){
		return saveMerAccounts(previousEDate, previousBDate);
	}
		
	protected String saveMerAccounts(int previousEDate, int previousBDate) {
		SettlementDao dao=new SettlementDao();
		String liqMonth=String.valueOf(previousEDate).substring(0, 6);
		StringBuffer delSql= new StringBuffer("delete from mer_accounts ");
		delSql.append(" where liqMonth=").append(liqMonth).append(";");
		int delRes=0;
		String delMsg="";
		try{
			//删除当月数据
			delRes=dao.update(delSql.toString()); 
			delRes+=1;
		}catch(Exception e){
			delMsg=e.getMessage();
		}
		if(delRes>0){
			LogUtil.printInfoLog("InitMerAccServer", "initMerAcc", "delete data  success");
		}else{
			LogUtil.printInfoLog("InitMerAccServer", "initMerAcc", "delete data  fail ,error msg:"+delMsg);
			return "delete data fail  ,errormsg:"+delMsg;
		}
		
		
		List<MerAccount> m=dao.queryaccounts(previousBDate,previousEDate);
		StringBuffer sql=new StringBuffer("insert into mer_accounts (mid,category,previousBalance,currentBalance,beginTrantDate," +
										"endTrantDate,transAmt,feeAmt,refAmt,refFeeAmt,liqAmt,manualAdd,manualSub,rec_pay,tbName,abbrev,liqMonth) values ");
		for (MerAccount ma : m) {
			sql.append("(").append(ma.getMid()).append(",");
			sql.append(ma.getCategory()).append(",");
			sql.append(ma.getPreviousBalance()).append(",");
			sql.append(ma.getCurrentBalance()).append(",");
			sql.append(ma.getBeginTrantDate()==null?previousBDate:ma.getBeginTrantDate()).append(",");
			sql.append(ma.getEndTrantDate()==null?previousEDate:ma.getEndTrantDate()).append(",");
			sql.append(ma.getTransAmt()).append(",");
			sql.append(ma.getFeeAmt()).append(",");
			sql.append(ma.getRefAmt()).append(",");
			sql.append(ma.getRefFeeAmt()).append(",");
			sql.append(ma.getLiqAmt()).append(",");
			sql.append(ma.getManualAdd()).append(",");
			sql.append(ma.getManualSub()).append(",");
			sql.append(ma.getRec_pay()==null?0:ma.getRec_pay()).append(",");
			sql.append(Ryt.addQuotes(ma.getTbName()==null?"":ma.getTbName())).append(",");
			sql.append(Ryt.addQuotes(ma.getAbbrev())).append(",");
			sql.append(liqMonth);
			sql.append("),");
		}
		String sql2=sql.toString().substring(0, sql.length()-1);
		int result=0;
		String msg="";
		
		try {
			result=dao.update(sql2);
		} catch (Exception e) {
			msg=e.getMessage();
		}
		if(result>0){
			LogUtil.printInfoLog("InitMerAccServer", "initMerAcc", "save success");
			return "success";
		}else{
			LogUtil.printErrorLog("InitMerAccServer", "initMerAcc", "save fail :"+msg);
			return "save mer_accounts fail ,errorsmg:"+msg;
		}
	}

}
