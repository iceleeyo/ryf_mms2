package com.rongyifu.mms.bank.b2e;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.common.Constant.DaifuPstate;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 付款入口
 *
 */
public class PayEnter {
	
	private AdminZHDao dao = new AdminZHDao();

	@SuppressWarnings("unchecked")
	public void exec_pay(String oid){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.oid,a.uid,a.aid,a.aname,a.sys_date,b.gid gate,a.ptype,a.trans_fee,a.pay_amt, ");
		sql.append("       a.to_uid,a.to_aid,a.acc_name,a.acc_no,a.to_acc_name,a.to_acc_no,a.to_bk_name,a.to_bk_no, ");
		sql.append("       a.to_prov_id,a.card_flag,a.sms_mobiles,a.bk_no,a.trans_amt,a.priv ");
		sql.append("  from tr_orders a,fee_calc_mode b ");
		sql.append(" where a.uid = b.mid  ");
		sql.append("   and a.gate = b.gate ");
		sql.append("   and a.state =").append(DaiFuTransState.PAY_PROCESSING);
		sql.append("   and a.pstate=").append(DaifuPstate.AUDIT_SUCCESS);
		sql.append("   and a.oid in ('" + oid + "')");
		sql.append("   and a.is_pay = ").append(2); //代付发起  追加条件 is_pay 为 2（已代付确认）
		List<TrOrders> orderList = dao.query(sql.toString(), TrOrders.class);
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("daifu sql", sql.toString());
		map.put("daifu num", "开始发起" + orderList.size() + "笔付款");
		LogUtil.printInfoLog("PayEnter", "exec_pay", "代付日志", map);
		
		if (orderList == null || orderList.size() == 0)
			return;
		
		Thread thread = new Thread(new DaifuThread(orderList, dao));
		thread.start();
	}
	
	@SuppressWarnings("unchecked")
	public void exec_pay_reqFail(String oid){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.oid,a.uid,a.aid,a.aname,a.sys_date,b.gid gate,a.ptype,a.trans_fee,a.pay_amt, ");
		sql.append("       a.to_uid,a.to_aid,a.acc_name,a.acc_no,a.to_acc_name,a.to_acc_no,a.to_bk_name,a.to_bk_no, ");
		sql.append("       a.to_prov_id,a.card_flag,a.sms_mobiles,a.bk_no,a.trans_amt,a.priv ");
		sql.append("  from tr_orders a,fee_calc_mode b ");
		sql.append(" where a.uid = b.mid  ");
		sql.append("   and a.gate = b.gate ");
	/*	sql.append("   and a.state =").append(DaiFuTransState.PAY_PROCESSING);
		sql.append("   and a.pstate=").append(DaifuPstate.AUDIT_SUCCESS);*/
		sql.append("   and a.oid in ('" + oid + "')");
		List<TrOrders> orderList = dao.query(sql.toString(), TrOrders.class);
		
		if (orderList == null || orderList.size() == 0)
			return;
		
		Map<String, String> map=new HashMap<String, String>();
		map.put("daifu sql", sql.toString());
		map.put("daifu num", "开始发起" + orderList.size() + "笔付款");
		LogUtil.printInfoLog("PayEnter", "exec_pay", "代付日志", map);
		
		Thread thread = new Thread(new DaifuThread(orderList, dao));
		thread.start();
	}
}