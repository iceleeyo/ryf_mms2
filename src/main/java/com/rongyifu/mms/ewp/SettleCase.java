package com.rongyifu.mms.ewp;

import java.util.List;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.utils.RecordLiveAccount;

/****
 * 结算
 * @author shdy
 *
 */
public  class SettleCase {
	public static TransactionDao dao=new TransactionDao();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public static void handleSettle() {		
		String[] info = new String[19];
		info[0] = "872205057220001,872205057220001,51740,0,51740,0,adjust_account,2014061101,手工调增";
		info[1] = "999222055330001,999222055330001,54725,0,54725,0,adjust_account,2014061102,手工调增";
		info[2] = "872331054110001,872331054110001,87604,0,87604,0,adjust_account,2014061103,手工调增";
		info[3] = "999222080990003,999222080990003,89450,0,89450,0,adjust_account,2014061104,手工调增";
		info[4] = "872491057220006,872491057220006,99550,0,99550,0,adjust_account,2014061105,手工调增";
		info[5] = "872731070110002,872731070110002,107638,0,107638,0,adjust_account,2014061106,手工调增";
		info[6] = "872491057220003,872491057220003,113430,0,113430,0,adjust_account,2014061107,手工调增";
		info[7] = "872821159980002,872821159980002,198539,0,198539,0,adjust_account,2014061108,手工调增";
		info[8] = "872305557220002,872305557220002,199100,0,199100,0,adjust_account,2014061109,手工调增";
		info[9] = "872581054110002,872581054110002,230257,0,230257,0,adjust_account,2014061110,手工调增";
		info[10] = "999191056990004,999191056990004,390535,0,390535,0,adjust_account,2014061111,手工调增";
		info[11] = "872498058120001,872498058120001,478937,0,478937,0,adjust_account,2014061112,手工调增";
		info[12] = "872821159980011,872821159980011,497400,0,497400,0,adjust_account,2014061113,手工调增";
		info[13] = "999290050720002,999290050720002,505000,0,505000,0,adjust_account,2014061114,手工调增";
		info[14] = "872498079970001,872498079970001,543125,0,543125,0,adjust_account,2014061115,手工调增";
		info[15] = "872491054110010,872491054110010,597300,0,597300,0,adjust_account,2014061116,手工调增";
		info[16] = "872871051370001,872871051370001,680100,0,680100,0,adjust_account,2014061117,手工调增";
		info[17] = "872651059980002,872651059980002,1964800,0,1964800,0,adjust_account,2014061118,手工调增";
		info[18] = "999220059480001,999220059480001,2490982,0,2490982,0,adjust_account,2014061119,手工调增";
		
		for(String item : info){
			String[] itemInfo = item.split(",");
			
			AccSeqs params = new AccSeqs();
			params.setAid(itemInfo[0]);
			params.setUid(itemInfo[0]);
			params.setTrAmt(Long.parseLong(itemInfo[2]));
			params.setTrFee(Integer.parseInt(itemInfo[3]));
			params.setAmt(Long.parseLong(itemInfo[4]));
			params.setRecPay((short)Integer.parseInt(itemInfo[5]));
			params.setTbName("adjust_account");
			params.setTbId(itemInfo[7]);
			params.setRemark(itemInfo[8]);
			
			handleUsableBalance(params);
		}
	}
	
	/**
	 * 调整商户可用金额
	 * @return
	 */
	public static void handleUsableBalance(AccSeqs params){
		List<String> sqlList = RecordLiveAccount.recordAccSeqsForOfflineCz(params);
		dao.batchSqlTransaction(sqlList.toArray(new String[sqlList.size()]));
	}
	
	/**
	 * 调整商户未结算金额
	 * @return
	 */
	public static void handleMerBalance(AccSeqs params){
		List<String> sqlList = RecordLiveAccount.recordAccSeqsAndCalLiqBalance(params);
		dao.batchSqlTransaction(sqlList.toArray(new String[sqlList.size()]));
	}
	
	/***
	 * 减少可用余额
	 */
	public static  String reduceBalance(String mid,String amt,String tseq){
		long settleAmt=Ryt.mul100toInt(amt);
		AccSeqs accSeqs=new AccSeqs();
		accSeqs.setUid(mid);
		accSeqs.setAid(mid);
		accSeqs.setTrAmt(settleAmt);
		accSeqs.setAmt(settleAmt);
		accSeqs.setTrFee(0);
		accSeqs.setTbId(tseq);
		accSeqs.setTbName("adjust_account");
		accSeqs.setRemark("手工调减");
		String sql1=useableAccseqs(accSeqs);
		String sql2="update  acc_infos set balance=balance - "+settleAmt+"  where  uid="+Ryt.addQuotes(mid)+" and  aid="+Ryt.addQuotes(mid)+";";
		String[] sqls=new String[2];
		sqls[0]=sql1;sqls[1]=sql2;
		int[] res=dao.batchSqlTransaction(sqls);
		String res2=""+res[0]+res[1];
		if(res2.equals("11")){
			System.out.println("ok");
			return "ok";
		}else{
			return "error";
		}
	}

	public static  String reduceBalance2(String mid,String amt,String tseq){
		long settleAmt=Ryt.mul100toInt(amt);
		AccSeqs accSeqs=new AccSeqs();
		accSeqs.setUid(mid);
		accSeqs.setAid(mid);
		accSeqs.setTrAmt(settleAmt);
		accSeqs.setAmt(settleAmt);
		accSeqs.setTrFee(0);
		accSeqs.setTbId(tseq);
		accSeqs.setTbName("adjust_account");
		accSeqs.setRemark("手工调减");
		String sql1=useableAccseqs(accSeqs);
		String sql2="update  acc_infos set freeze_amt=freeze_amt - "+settleAmt+"  where  uid="+Ryt.addQuotes(mid)+" and  aid="+Ryt.addQuotes(mid)+";";
		String[] sqls=new String[2];
		sqls[0]=sql1;sqls[1]=sql2;
		int[] res=dao.batchSqlTransaction(sqls);
		String res2=""+res[0]+res[1];
		if(res2.equals("11")){
			System.out.println("ok");
			return "ok";
		}else{
			return "error";
		}
	}
	
	
	/****
	 * 手工调减账户可用余额
	 * @param params
	 * @return
	 */
	public static  String useableAccseqs(AccSeqs params){
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
		sql.append(" all_balance, balance  + freeze_amt - " + amt);
		sql.append("  from acc_infos ");
		sql.append(" where uid = " + Ryt.addQuotes(uid));
		sql.append("   and aid = " + Ryt.addQuotes(aid));
		return sql.toString();
	}
	
	

}
