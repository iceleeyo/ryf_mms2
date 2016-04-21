package com.rongyifu.mms.modules.transaction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class UploadHlogDao extends PubDao<Hlog> {
	
	private Map<String, String> calcModeMap = new HashMap<String, String>();
	
	int i = 0;
	public int batchAdd(List<Hlog> list) {
		for (Hlog hlog : list) {
			int merFee = calcMerFee(hlog.getMid(), hlog.getGate(), hlog.getAmount());
			String tseq = Ryt.genOidBySysTime();
			
			StringBuilder sql = new StringBuilder();
			sql.append("insert into hlog (tseq,oid,type,mid,amount,gid,gate,sys_date,sys_time,tstat,version,ip,mdate,init_sys_date,pay_amt,fee_amt,bk_flag,bk_date,bk_recv) values(");
			sql.append(tseq + ",");
			sql.append(Ryt.addQuotes(hlog.getOid()) + ",");
			sql.append(hlog.getType() + ",");
			sql.append(hlog.getMid() + ",");
			sql.append(hlog.getAmount() + ",");
			sql.append(hlog.getGid() + ",");
			sql.append(hlog.getGate() + ",");
			sql.append(hlog.getSysDate() + ",");
			sql.append(hlog.getSysTime() + ",");
			sql.append(hlog.getTstat() + ",10,0,");
			sql.append(hlog.getSysDate() + ",");
			sql.append(hlog.getSysDate() + ",");
			sql.append(hlog.getAmount() + ",");
			sql.append(merFee + ",1,");
			sql.append(hlog.getSysDate() + ",");
			sql.append(hlog.getSysTime() + ")");
			
			// 记账户流水sql
			AccSeqs params = new AccSeqs();
			params.setUid(hlog.getMid());
			params.setAid(hlog.getMid());
			params.setTrAmt(hlog.getAmount());
			params.setTrFee(merFee);
			params.setAmt(hlog.getAmount() - merFee);
			params.setRecPay((short) 0);
			params.setTbName(Constant.HLOG);
			params.setTbId(tseq);
			params.setRemark("手工导入数据");
			List<String> sqlList = RecordLiveAccount.recordAccSeqsAndCalLiqBalance(params);
			
			sqlList.add(sql.toString());
			
			String[] sqls = (String[]) sqlList.toArray(new String[sqlList.size()]);
			int[] result = batchSqlTransaction(sqls);
			if(result != null && result.length != 0)
				i++;
		}
		
		return i;
	}
	
	/**
	 * 计算商户手续费
	 * @param mid
	 * @param gateId
	 * @param Amount
	 * @return
	 */
	private int calcMerFee(String mid, Integer gateId, Long Amount){
		try {
			String calcMode = null;
			String key = mid + "_" + gateId;
			if(calcModeMap.containsKey(key))
				calcMode = calcModeMap.get(key);
			else {
				FeeCalcMode mode = getFeeModeByGate(mid, String.valueOf(gateId));
				calcMode = mode.getCalcMode();
				calcModeMap.put(key, calcMode);
			}
			
			return (int) Double.parseDouble(ChargeMode.reckon(calcMode, String.valueOf(Amount), "0"));
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "calcMerFee", "", e);
		}
		return 0;
	}
}
