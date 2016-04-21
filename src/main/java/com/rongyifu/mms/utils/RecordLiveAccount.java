package com.rongyifu.mms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;

/**
 * 记流水账，算余额
 * 商户总余额 = 未结算余额(all_balance) + 可用余额(balance)
 * @author lv.xiaofeng
 *
 */
public class RecordLiveAccount {
	
	/**
	 * <b>未结算余额</b>流水账SQL，用于生成未结算余额的账户流水
	 * @param AccSeqs
	 * @return
	 */
	private static String recordLiqAccSeqs(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		int recPay = params.getRecPay();
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(recPay + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		
		if(recPay == 0)
			sql.append("all_balance + " + amt);
		else
			sql.append("all_balance - " + amt);
		
		sql.append("  ,balance + freeze_amt");
		sql.append("  from acc_infos ");
		sql.append(" where uid = '" + uid + "' ");
		sql.append("   and aid = '" + aid + "' ");
		
		return sql.toString();
	}
	
	/**
	 * <b>可用余额</b>流水账SQL，用于生成可用余额的账户流水
	 * @param AccSeqs
	 * @return
	 */
	private static String recordUsableAccSeqs(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		int recPay = params.getRecPay();
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(recPay + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		sql.append("all_balance,");
		if(recPay == 0)
			sql.append("balance + freeze_amt + " + amt);
		else
			sql.append("balance + freeze_amt - " + amt);
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	/**
	 * <b>可用余额</b>流水账SQL，用于生成可用余额的账户流水(提现使用)
	 * @param AccSeqs
	 * @return
	 */
	private static String recordUsableAccSeqsForTX(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(Constant.RecPay.REDUCE + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		sql.append(" all_balance, balance + freeze_amt - " + amt);
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		
		return sql.toString();
	}
	
	/**
	 * 记流水账：未结算金额减少；可用余额增加（<b>结算专用</b>）
	 * @param AccSeqs
	 * @return
	 */
	private static String recordAccSeqs(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		int recPay = Constant.RecPay.INCREASE; // 总余额不变，可用余额增加
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(recPay + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		sql.append("all_balance - " + amt + ",");
		sql.append("balance + freeze_amt + " + amt);
		
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		
		return sql.toString();
	}
	
	/**
	 * 记流水账：未结算金额减少；可用余额增加（<b>结算到银行卡专用</b>）
	 * @param AccSeqs
	 * @return
	 */
	private static String recordAccSeqsForLiqCard(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		int recPay = Constant.RecPay.REDUCE; // 总余额减少：未结算金额减少，可用余额不变
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(recPay + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		sql.append("all_balance - " + amt + ",");
		sql.append("balance + freeze_amt");
		
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		
		return sql.toString();
	}
	
	
	/**
	 * 记流水账：未结算金额减少；可用余额增加（<b>结算到银行卡专用</b>）
	 * @param AccSeqs
	 * @return
	 */
	private static String recordAccSeqsForLiqDls(AccSeqs params){
		String uid = params.getUid();
		String aid = params.getAid();
		long trAmt = params.getTrAmt();
		int trFee = params.getTrFee();
		long amt = params.getAmt();
		int recPay = Constant.RecPay.INCREASE; // 总余额减少：未结算金额减少，可用余额不变
		String tbName = params.getTbName();
		String tbId = params.getTbId();
		String remark = params.getRemark();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into acc_seqs(uid,aid,tr_date,tr_time,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark,all_balance,balance) ");
		sql.append("select uid,aid,");
		sql.append("date_format(now(),'%Y%m%d'),");
		sql.append("time_to_sec(curtime()),");
		sql.append(trAmt + ",");
		sql.append(trFee + ",");
		sql.append(amt + ",");
		sql.append(recPay + ",");
		sql.append(Ryt.addQuotes(tbName) + ",");
		sql.append(Ryt.addQuotes(tbId) + ",");
		sql.append(Ryt.addQuotes(remark) + ",");
		sql.append("all_balance +" + amt + ",");
		sql.append("balance + freeze_amt");
		
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		
		return sql.toString();
	}
	
	/**
	 * 计算商户余额：未结算金额减少；可用余额增加（<b>结算专用</b>）
	 * @param uid			商户号
	 * @param aid			账号
	 * @param settleAmt		结算金额
	 * @param recPay		收支标识：0 收入；支出
	 * @return
	 */
	private static String calMerBalance(String uid, String aid, long settleAmt){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		sql.append(" all_balance = all_balance - " + settleAmt + ",");
		sql.append(" balance = balance + " + settleAmt);
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	/**
	 * 只计算未结算余额SQL
	 * @param uid			商户号
	 * @param aid			账号
	 * @param settleAmt		结算金额
	 * @param recPay		收支标识：0 收入；支出
	 * @return
	 */
	private static String calLiqBalance(String uid, String aid, long settleAmt, int recPay){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		
		if(recPay == 0){		
			sql.append(" all_balance = all_balance + " + settleAmt);
		} else { 
			sql.append(" all_balance = all_balance - " + settleAmt);
		}
		
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	/**
	 * 只计算可用余额，用于账户充值
	 * @param uid			商户号
	 * @param aid			账号
	 * @param settleAmt		结算金额
	 * @param recPay		收支标识：0 收入；支出
	 * @return
	 */
	private static String calUsableBalanceForCz(String uid, String aid, long settleAmt, int recPay){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		
		if(recPay == 0){		
			sql.append(" balance = balance + " + settleAmt);
		} else { 
			sql.append(" balance = balance - " + settleAmt);
		}
		
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	/**
	 * 处理提现、代付等下单扣可用余额的情况（下订单、交易失败调用方法）
	 * @param uid			商户号
	 * @param aid			账号
	 * @param settleAmt		结算金额
	 * @param recPay		收支标识：0 收入；支出
	 * @return
	 */
	public static String calUsableBalance(String uid, String aid, long settleAmt, int recPay){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		
		if(recPay == 0){		
			sql.append(" balance = balance + " + settleAmt + ",");
			sql.append(" freeze_amt = freeze_amt - " + settleAmt);
		} else { 
			sql.append(" balance = balance - " + settleAmt + ",");
			sql.append(" freeze_amt = freeze_amt + " + settleAmt);
		}
		
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	/**
	 * 处理冻结金额
	 * @param uid			商户号
	 * @param aid			账户号
	 * @param settleAmt		结算金额
	 * @param recPay		收支标识：0 收入；1 支出
	 * @return
	 */
	private static String calFreezeAmt(String uid, String aid, long settleAmt, int recPay){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		
		if(recPay == 0){
			sql.append(" freeze_amt = freeze_amt + " + settleAmt);
		} else {
			sql.append(" freeze_amt = freeze_amt - " + settleAmt);
		}
		
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}	
	
	
	/**
	 * 只计算未结算余额、记流水账SQL（用于交易、退款）
	 * @param AccSeqs
	 * @return
	 */
	public static List<String> recordAccSeqsAndCalLiqBalance(AccSeqs params){
		List<String> list = new ArrayList<String>();
		list.add(recordLiqAccSeqs(params));
		
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		int recPay = params.getRecPay();
		
		list.add(calLiqBalance(uid, aid, amt, recPay));
		return list;
	}
	
	
	/**
	 * 只计算可用余额、记流水账SQL(用于提现、代付等业务交易成功后调用)
	 * @param AccSeqs
	 * @return
	 */
	public static List<String> recordAccSeqsAndCalUsableBalance(AccSeqs params){
		List<String> list = new ArrayList<String>();
		list.add(recordUsableAccSeqs(params));
		
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		int recPay = params.getRecPay();
		list.add(calUsableBalance(uid, aid, amt, recPay));
		return list;
	}
	
	
	/**
	 * 线下充值
	 * @param AccSeqs
	 * @return
	 */
	public static List<String> recordAccSeqsForOfflineCz(AccSeqs params){
		List<String> list = new ArrayList<String>();
		list.add(recordUsableAccSeqs(params));
		
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		int recPay = params.getRecPay();
		list.add(calUsableBalanceForCz(uid, aid, amt, recPay));
		return list;
	}
	
	/**
	 * 结算到账户
	 * @param params
	 * @return
	 */
	public static List<String> LiqToAccount(AccSeqs params){
		List<String> list = new ArrayList<String>();
		
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		// 总余额不变：未结算余额减少；可用余额增加			
		list.add(recordAccSeqs(params));
		list.add(calMerBalance(uid, aid, amt));	
		
		LogUtil.printInfoLog("RecordLiveAccount", "LiqToAccount", String.valueOf(list));
		
		return list;
	}

    /***
     *结算到账户，自动结算、自动代付专用，
     * @param params
     * @return
     */
    public static List<String> LiqToAccount2(AccSeqs params){
        List<String> list = new ArrayList<String>();

		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		// 总余额不变：未结算余额减少；可用余额增加
		list.add(recordAccSeqs(params));
		list.add(calMerBalance2(uid, aid, amt));

		LogUtil.printInfoLog("RecordLiveAccount", "LiqToAccount", String.valueOf(list));

		return list;
    }
	
	/**
	 * 结算到代理商
	 * @param params
	 * @return
	 */
	public static List<String> LiqToDls(AccSeqs params,AccSeqs params1){
		List<String> list = new ArrayList<String>();
		//商户
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		int recPay = Constant.RecPay.REDUCE;
		//代理商
		String dlsuid=params1.getUid();
		String dlsaid=params1.getAid();
		long dlsamt=params1.getAmt();
		int dlsrecPay = Constant.RecPay.INCREASE;
		// 商户金额减少，代理商金额增加			
		list.add(recordAccSeqsForLiqCard(params));
		list.add(calUsableBalanceForCz(uid, aid, amt,recPay));
		list.add(recordAccSeqsForLiqDls(params1));
		list.add(calUsableBalanceForCz(dlsuid, dlsaid, dlsamt,dlsrecPay));
		LogUtil.printInfoLog("RecordLiveDls", "LiqToDLS", String.valueOf(list));
		
		return list;
	}
	
	/**
	 * 结算到银行卡
	 * @param params
	 * @return
	 */
	public static List<String> LiqToBankCard(AccSeqs params){
		List<String> list = new ArrayList<String>();
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		int recPay = Constant.RecPay.REDUCE;
		String tbName = params.getTbName();
		
		// 账户流水：未结算余额减少，可用余额增加
		params.setTbName(tbName + "(1)");
		list.add(recordAccSeqs(params));
		// 账户流水：未结算余额减少，可用余额不变
		params.setTbName(tbName + "(2)");
		list.add(recordAccSeqsForLiqCard(params));
		// 账号：未结算余额减少，可用余额不变
		list.add(calLiqBalance(uid, aid, amt, recPay));
	    
		LogUtil.printInfoLog("RecordLiveAccount", "LiqToBankCard", String.valueOf(list));
		
		return list;
	}
	
	/**
	 * 处理提现、代付等下单扣可用余额的情况（交易成功调用方法）
	 * @param params
	 * @return
	 */
	public static List<String> handleBalanceForTX(AccSeqs params){
		List<String> list = new ArrayList<String>();
		String uid = params.getUid();
		String aid = params.getAid();
		long amt = params.getAmt();
		
		// 记账户流水
		list.add(recordUsableAccSeqsForTX(params));
		// 冻结金额减少
		list.add(calFreezeAmt(uid, aid, amt, Constant.RecPay.REDUCE));
		
		return list;
	}
	
	
	/**
	 * 生成插入tr_orders和tlog的Sql yang.yaofeng
	 * @param hlogMap hlog参数 键为字段名 值为插入值 
	 * @param trOrdersMap trOrder参数 键为字段名 值为插入值  
	 * @return 
	 */
	public static String[] getInsetSqls(Map<String, String> hlogMap,Map<String, String> trOrdersMap){
		hlogMap.put("is_liq", "1");
		String[] sqls=new String[2];
		StringBuffer hlogSql=new StringBuffer("insert into hlog");
		StringBuffer trOrdersSql=new StringBuffer("insert into tr_orders");
		//hlog插入字段字符串
		String hlogKeyTemp="(";
		//trOrders插入字段字符串
		String trOrderKeyTemp="(";
		String hlogValueTemp=" values (";
		String trOrderValueTemp=" values (";
		Set<String> hlogKeys=hlogMap.keySet();
		Set<String> trOrderKeys=trOrdersMap.keySet();
		for (String hlogKey : hlogKeys) {
			hlogKeyTemp=hlogKeyTemp+hlogKey;
			hlogKeyTemp=hlogKeyTemp+",";
			hlogValueTemp=hlogValueTemp+hlogMap.get(hlogKey);
			hlogValueTemp=hlogValueTemp+",";
		}
		for (String trOrderKey : trOrderKeys) {
			trOrderKeyTemp=trOrderKeyTemp+trOrderKey;
			trOrderKeyTemp=trOrderKeyTemp+",";
			trOrderValueTemp=trOrderValueTemp+trOrdersMap.get(trOrderKey);
			trOrderValueTemp=trOrderValueTemp+",";
		}
		trOrderKeyTemp=trOrderKeyTemp.substring(0, trOrderKeyTemp.lastIndexOf(","))+")";
		trOrderValueTemp=trOrderValueTemp.substring(0, trOrderValueTemp.lastIndexOf(","))+")";
		hlogKeyTemp=hlogKeyTemp.substring(0, hlogKeyTemp.lastIndexOf(","))+")";
		hlogValueTemp=hlogValueTemp.substring(0, hlogValueTemp.lastIndexOf(","))+")";
		hlogSql.append(hlogKeyTemp);
		hlogSql.append(hlogValueTemp);
		trOrdersSql.append(trOrderKeyTemp);
		trOrdersSql.append(trOrderValueTemp);
		sqls[0]=hlogSql.toString();
		sqls[1]=trOrdersSql.toString();
		return sqls;
	}

    	/**
	 * 计算商户余额：未结算金额减少；可用余额不变，冻结金额增加（<b>结算专用</b>）
	 * @param uid			商户号
	 * @param aid			账号
	 * @param settleAmt		结算金额
	 * @return
	 */
	private static String calMerBalance2(String uid, String aid, long settleAmt){
		StringBuffer sql = new StringBuffer();
		sql.append("update acc_infos set ");
		sql.append(" all_balance = all_balance - " + settleAmt + ",");
//		sql.append(" balance = balance + " + settleAmt);
        sql.append("freeze_amt = freeze_amt+").append(settleAmt);
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}

}
