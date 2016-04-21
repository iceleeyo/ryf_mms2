package com.rongyifu.mms.merchant;

import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.service.SysManageService;
import com.rongyifu.mms.utils.DateUtil;

public class MerContractExpiresNote {

	/**
	 * 合同到期续约报警，在到期前一个月以通知的方式提示
	 */
	@SuppressWarnings("unchecked")
	public static void doAddNote() {
		if(!Ryt.isStartService("检查商户通知信息服务")){
			return;
		}
		
		int today = DateUtil.today();
		
		int A = 0;
		int B = 0;
		// 1号
		if (DateUtil.isFirstDate(today)) {
			// >本月最后一天 <= 下月15号
			A = DateUtil.getThisMonthLastDate();
			B = DateUtil.getNextMonth15(today);
			// 15号
		} else if (DateUtil.is15Date(today)) {
			// >下月15号 <= 下月最后一天
			A = DateUtil.getNextMonth15(today);
			B = DateUtil.getNextMonthLastDate(today);

		} else {
			return;
		}
		
		String sql = " select id,name,exp_date from minfo where exp_date > " + A + " and exp_date<= " + B;
		sql+=" and mstate <> 1;";

		PubDao dbdao = new PubDao();
//		JdbcTemplate jt = db.getJdbcTemplate();
		SysManageService dao = new SysManageService();

		List<Map<String, Object>> midmaps = dbdao.queryForList(sql);
		if (!midmaps.isEmpty()) {
			StringBuilder mids = new StringBuilder("(|");
			for (Map<String, Object> m : midmaps) {
				mids.append(m.get("id") + "-" + m.get("exp_date") + "|");
				MMSNotice n = new MMSNotice();
				n.setBeginDate(today);
				n.setEndDate(B);
				n.setTitle("商户合约到期通知--" + today+"--" + m.get("id"));
				n.setContent("尊敬的商户:" + m.get("name") + ",您的合同将于" + m.get("exp_date") + "到期,为了不影响您的使用，请续约!");
				n.setMid(m.get("id").toString());
				n.setOperId(1);
				n.setNoticeType(1);
				n.setExpiration_date((Integer)m.get("exp_date"));
				dao.addOrEditMessage(n, "add");
			}
			/*MMSNotice nn = new MMSNotice();
			nn.setBeginDate(today);
			nn.setEndDate(B);
			nn.setTitle("商户合约到期通知--" + today);
			nn.setMid("1");
			nn.setOperId(1);
			nn.setContent("");
			dao.addOrEditMessage(nn, "add");*/
		}

		List<Map<String, Object>> codesmaps = dbdao.queryForList(sql.replaceAll("exp_date", "code_exp_date"));

		if (codesmaps.isEmpty()) return;

		StringBuilder mids = new StringBuilder("(|");
		for (Map<String, Object> m : codesmaps) {
			mids.append(m.get("id") + "-" + m.get("code_exp_date") + "|");
			MMSNotice n = new MMSNotice();
			n.setBeginDate(today);
			n.setEndDate(B);
			n.setTitle("商户组织机构代码有效期到期通知--" + today + "--" + m.get("id"));
			n.setContent("尊敬的商户:" + m.get("name") + ",您的组织机构代码将于" + m.get("code_exp_date") + "到期,请联系我们!");
			n.setMid(m.get("id").toString());
			n.setOperId(1);
			n.setExpiration_date((Integer)m.get("code_exp_date"));
			n.setNoticeType(2);
			dao.addOrEditMessage(n, "add");
		}
		/*MMSNotice nn = new MMSNotice();
		nn.setBeginDate(today);
		nn.setEndDate(B);
		nn.setTitle("商户组织机构代码有效期 到期通知--" + today);
		nn.setMid("1");
		nn.setOperId(1);
		nn.setContent("以下商户：" + mids.toString() + "|),组织机构代码即将到期！");
		dao.addOrEditMessage(nn, "add");*/

	}
	
	
	
	public static String doAddNote(Integer beginDate){
		if(!Ryt.isStartService("检查商户通知信息服务")){
			return "检查商户通知信息服务";
		}

		
		int A = 0;
		int B = 0;
		// 1号
		if (DateUtil.isFirstDate(beginDate)) {
			// >本月最后一天 <= 下月15号
			A = DateUtil.getThisMonthLastDate(beginDate);
			B = DateUtil.getNextMonth15(beginDate);
			// 15号
		} else if (DateUtil.is15Date(beginDate)) {
			// >下月15号 <= 下月最后一天
			A = DateUtil.getNextMonth15(beginDate);
			B = DateUtil.getNextMonthLastDate(beginDate);

		} else {
			return "日期错误";
		} 
		
		@SuppressWarnings("rawtypes")
		PubDao dbdao = new PubDao();
		SysManageService dao = new SysManageService();
		String delSql="delete from mms_notice where begin_date="+beginDate+" and end_date="+B+";";
		
		dbdao.update(delSql);
		
		String sql = " select id,name,exp_date from minfo where exp_date > " + A + " and exp_date<= " + B;
		sql+=" and mstate <> 1;";


		List<Map<String, Object>> midmaps = dbdao.queryForList(sql);
		if (!midmaps.isEmpty()) {
			StringBuilder mids = new StringBuilder("(|");
			for (Map<String, Object> m : midmaps) {
				mids.append(m.get("id") + "-" + m.get("exp_date") + "|");
				MMSNotice n = new MMSNotice();
				n.setBeginDate(beginDate);
				n.setEndDate(B);
				n.setTitle("商户合约到期通知--" + beginDate+"--" + m.get("id"));
				n.setContent("尊敬的商户:" + m.get("name") + ",您的合同将于" + m.get("exp_date") + "到期,为了不影响您的使用，请续约!");
				n.setMid(m.get("id").toString());
				n.setOperId(1);
				n.setNoticeType(1);
				n.setExpiration_date((Integer)m.get("exp_date"));
				dao.addOrEditMessage(n, "add");
			}
		}

		List<Map<String, Object>> codesmaps = dbdao.queryForList(sql.replaceAll("exp_date", "code_exp_date"));

		if (!codesmaps.isEmpty()){
			StringBuilder mids = new StringBuilder("(|");
			for (Map<String, Object> m : codesmaps) {
				mids.append(m.get("id") + "-" + m.get("code_exp_date") + "|");
				MMSNotice n = new MMSNotice();
				n.setBeginDate(beginDate);
				n.setEndDate(B);
				n.setTitle("商户组织机构代码有效期到期通知--" + beginDate + "--" + m.get("id"));
				n.setContent("尊敬的商户:" + m.get("name") + ",您的组织机构代码将于" + m.get("code_exp_date") + "到期,请联系我们!");
				n.setMid(m.get("id").toString());
				n.setOperId(1);
				n.setExpiration_date((Integer)m.get("code_exp_date"));
				n.setNoticeType(2);
				dao.addOrEditMessage(n, "add");
			}
		}
		
		return "执行成功";
		
	}

}
