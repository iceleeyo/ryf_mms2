package com.rongyifu.mms.ewp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;

public class QueryFee {
	static PubDao pubdao = new PubDao();

	public static String queryFee(Map<String, String> p) {
		String mid = p.get("mid");
		String gid = p.get("gate");
		String value = "";
		String queryresult = null;
		List<Map<String, Object>> mode = getFeeModeByGate(mid, gid);
		for (Map<String, Object> m : mode) {
			String mid1 = m.get("mid").toString();
			String gate = m.get("gid").toString();
			String calcMode = m.get("calcMode").toString();
			value += mid1 + "|" + gate + "|" + calcMode + ";";
		}
		if (!Ryt.empty(value)) {
			queryresult = "<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status> <value>0</value><msg>查询手续费成功！</msg><result>"+ value +"</result></status></res>";
		} else {
			queryresult = "<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status><value>1</value><msg>查询手续费为空！</msg></status></res>";
		}
		return queryresult;

	}

	public static List<Map<String, Object>> getFeeModeByGate(String mid,
			String gid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select calc_mode as calcMode ,gid as gid ,mid as mid from fee_calc_mode where trans_mode=10 ");
		if (!Ryt.empty(mid)) {
			sql.append(" and mid=").append(Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(gid)) {
			sql.append(" and gate=").append(gid);
		}
		List<Map<String, Object>> aList = pubdao.queryForList(sql.toString());
		return aList;

	}

	public static void main(String[] args) {
		Map<String, String> p = new HashMap<String, String>();
		String a = queryFee(p);
		System.out.println(a);
	}
}
