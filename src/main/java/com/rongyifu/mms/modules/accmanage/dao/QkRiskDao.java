package com.rongyifu.mms.modules.accmanage.dao;

import com.rongyifu.mms.bean.QkCardInfo;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

//快捷用户信息
@SuppressWarnings("unchecked")
public class QkRiskDao extends PubDao {
	
	/**
	 * 根据条件查询当天交易
	 * @return CurrentPage
	 */
	public CurrentPage<QkCardInfo> queryQkCardInfo(Integer pageNo,Integer pageSize, String mid, String phoneNo, String cardNo,String bdate,String edate,String abbrev,String type) {
		StringBuffer sql = new StringBuffer();
		if(type.equals("0")){//表示下拉列表框选的是未解绑的
			sql.append("select a.*,u.mid,u.regi_time,m.abbrev from qk_card_info a,qk_users u ,minfo m where a.user_id=u.user_id and u.mid=m.id ");
		}
		else if(type.equals("1")){//表示下拉列表框选的是解绑的
			sql.append("select a.*,u.mid,u.regi_time,m.abbrev from qk_unbind_card a,qk_users u ,minfo m where a.user_id=u.user_id and u.mid=m.id");
		}
		
//		sql.append("select * from (select a.*,u.mid,u.regi_time,m.abbrev from qk_card_info a,qk_users u ,minfo m where a.user_id=u.user_id and u.mid=m.id ) w " +
//				" left join (select user_id,card_no,auth_code code from qk_unbind_card GROUP BY user_id ,card_no)m " +
//				" on w.user_id=m.user_id and w.card_no=m.card_no ");
		if (!Ryt.empty(mid)){
			sql.append(" AND u.mid = "+ Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(phoneNo)){
			sql.append(" AND a.phone_no = "+ Ryt.addQuotes(Ryt.minfoSetHandle(phoneNo)));
		}
		if (!Ryt.empty(cardNo)){
			sql.append(" AND a.card_no = "+ Ryt.addQuotes(Ryt.minfoSetHandle(cardNo)));
		}
		
		if (!Ryt.empty(bdate)){
			sql.append(" AND date_format(u.regi_time,'%Y%m%d')>= "+ Ryt.addQuotes(bdate));
		}
		if (!Ryt.empty(edate)){
			sql.append(" AND date_format(u.regi_time,'%Y%m%d')<= "+ Ryt.addQuotes(edate));
		}
		if (!Ryt.empty(abbrev)){
			sql.append(" AND m.abbrev like '%" +abbrev+ "%'");
		}
		if(type.equals("0")){
			sql.append(" order by u.regi_time desc");
		}
		else if(type.equals("1"))
		sql.append(" GROUP BY user_id ,card_no order by u.regi_time desc");
		
		StringBuffer sql1 = new StringBuffer();
		if(type.equals("0")){
			sql1.append("select count(*) from qk_card_info a,qk_users u ,minfo m where a.user_id=u.user_id and u.mid=m.id ");
		}
		else if(type.equals("1"))
		sql1.append("select count(DISTINCT a.user_id ,a.card_no) from qk_unbind_card a,qk_users u ,minfo m where a.user_id=u.user_id and u.mid=m.id ");
		if (!Ryt.empty(mid)){
			sql1.append(" AND u.mid = "+ Ryt.addQuotes(mid));
		}
		if (!Ryt.empty(phoneNo)){
			sql1.append(" AND a.phone_no = "+ Ryt.addQuotes(Ryt.minfoSetHandle(phoneNo)));
		}
		if (!Ryt.empty(cardNo)){
			sql1.append(" AND a.card_no = "+ Ryt.addQuotes(Ryt.minfoSetHandle(cardNo)));
		}
		if (!Ryt.empty(bdate)){
			sql1.append(" AND date_format(u.regi_time,'%Y%m%d')>= "+ Ryt.addQuotes(bdate));
		}
		if (!Ryt.empty(edate)){
			sql1.append(" AND date_format(u.regi_time,'%Y%m%d')<= "+ Ryt.addQuotes(edate));
		}
		if (!Ryt.empty(abbrev)){
			sql1.append(" AND m.abbrev like '%" +abbrev+ "%'");
		}
		
		return queryForPage(sql1.toString(),sql.toString(), pageNo,pageSize,QkCardInfo.class);


	}
		
		
		/**
		 * 快捷支付用户信息详情查询
		 * @param authCode
		 * @return
		 */
		public QkCardInfo queryQKByTseq(String authCode) {
			String sql="select a.*,u.mid,u.regi_time,m.abbrev from qk_card_info a,qk_users u ,minfo m where a.user_id=u.user_id " +
					"and u.mid=m.id and a.auth_code="+Ryt.addQuotes(authCode);
			
			return queryForObject(sql, QkCardInfo.class);
		}

}
