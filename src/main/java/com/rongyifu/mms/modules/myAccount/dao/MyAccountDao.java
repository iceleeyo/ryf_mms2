package com.rongyifu.mms.modules.myAccount.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("rawtypes")
public class MyAccountDao extends PubDao{

	/**
	 * 查询账户信息
	 * 
	 * @param aid 账户id
	 * @return
	 */
	public  AccInfos queryAccount(String aid){
		String sql="select * from acc_infos where  aid=uid and aid = "+Ryt.addQuotes(aid);
		return queryForObject(sql,AccInfos.class);
	}
	
	/**
	 * 查询密码
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public String getPass(String mid) {
		return queryForString("select pay_pwd from minfo where id='"+ mid+"'");
	}
	
	/**
	 * 查询手续费计算公式
	 * @param aid
	 * @param FeeModeName
	 * @return
	 */
	public Map<String, Object> getFeeModeAid(String aid,String FeeModeName){
		String sql = "select uid,aname," + Ryt.sql(FeeModeName) + " from acc_infos where aid=?";
		return queryForMap(sql, new Object[]{aid});
	}
	
	/**
	 * 查询账户下的状态为正常的用户名称（结算账户）
	 * @param uid，用户ID
	 * @return
	 */
	public Map<String, String> getZHUid(String uid) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		LoginUser u = getLoginUser();
		if(u==null) 
			return result;
		 
		String sql="select aid, CONCAT(aid,'【',aname,'】') as aname from acc_infos where state=1 and uid=aid ";
			sql+=" and  uid= "+Ryt.addQuotes(uid)+"";
		return queryToMap(sql);
	}
	
	/**
	 * 查询商户网关
	 * @param mid
	 * @param transType
	 * @return
	 */
	public  Map<String, String> getMerGate(String mid, int transType){
		StringBuffer sql = new StringBuffer();
		sql.append("select b.gate, b.gate_name");
		sql.append("  from fee_calc_mode a, ryt_gate b");
		sql.append(" where a.gate = b.gate");
		sql.append("   and a.mid = " + Ryt.addQuotes(mid));
		sql.append("   and b.trans_mode = " + transType);
		sql.append("   and b.gate not like '4%'");
		sql.append(" order by b.gate");
		
		List<Map<String, Object>> gateList = queryForList(sql.toString());
		Map<String, String> gateMap = new TreeMap<String, String>();
		for(Map<String, Object> gate : gateList){
			String gateId = String.valueOf(gate.get("gate"));
			String gateName = String.valueOf(gate.get("gate_name"));
			
			gateMap.put(gateId, gateName);
		}
		return gateMap;
	}
	/**
	 * 统计月累计代付金额
	 * @param aid
	 * @param month
	 * @return
	 */
	public long queryMonthDaiFuSumAmt(String aid, String month) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(amount)");
		sql.append("  from (select sum(amount) amount");
		sql.append("          from tlog");
		sql.append("         where mid = " + Ryt.addQuotes(aid));
		sql.append("           and tstat in (" + Constant.PayState.INIT + ", " + Constant.PayState.WAIT_PAY + ", " + Constant.PayState.SUCCESS + ")");
		sql.append("           and type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + ", " + Constant.TlogTransType.PAYMENT_FOR_PUBLIC + ")");
		sql.append("           and sys_date > " + month + "00");
		sql.append("           and sys_date < " + month + "32");
		sql.append("        union all");
		sql.append("        select sum(amount) amount");
		sql.append("          from hlog");
		sql.append("         where mid = " + Ryt.addQuotes(aid));
		sql.append("           and tstat in (" + Constant.PayState.INIT + ", " + Constant.PayState.WAIT_PAY + ", " + Constant.PayState.SUCCESS + ")");
		sql.append("           and type in (" + Constant.TlogTransType.PAYMENT_FOR_PRIVATE + ", " + Constant.TlogTransType.PAYMENT_FOR_PUBLIC + ")");
		sql.append("           and sys_date > " + month + "00");
		sql.append("           and sys_date < " + month + "32) a");
		return queryForLong(sql.toString());
	}
}
