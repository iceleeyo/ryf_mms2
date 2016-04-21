package com.rongyifu.mms.modules.mermanage.dao;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccSyncDetail;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.QkRisk;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;

public class MerRiskConDao extends PubDao {
	/**
	 * 获得单个商户对象
	 * @param mid
	 * @return
	 */
	public Minfo getOneMinfo(String mid) throws Exception {
		String sql = "select * from minfo where id ='"+mid+"'";
		return queryForObject(sql,Minfo.class);
	}
	
	
	/**
	 * 商户风险信息增加
	 * 
	 * @param minfo
	 * @return
	 * @throws Exception
	 */
	public int editMinfoFX(Minfo minfo) throws Exception {
		Object[] obj = { minfo.getAccSuccCount(), minfo.getAccFailCount(),minfo.getPhoneSuccCount(),
				minfo.getPhoneFailCount(),minfo.getIdSuccCount(), minfo.getIdFailCount(),
				minfo.getTransLimit()*100,minfo.getMerChkFlag(),minfo.getRefundFlag(),minfo.getAmtLimitD()*100,
				minfo.getAmtLimitM()*100,minfo.getCardLimit()*100,
				minfo.getWhiteAccSuccCount(),minfo.getWhiteIdSuccCount(),minfo.getWhitePhoneSuccCount(),
				minfo.getWhiteAccFailCount(),minfo.getWhiteIdFailCount(),minfo.getWhitePhoneFailCount(),
				minfo.getId() };
		int[] type = { 
				Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.INTEGER, Types.INTEGER, Types.INTEGER,
				Types.INTEGER, Types.TINYINT, Types.TINYINT,
				Types.VARCHAR,Types.INTEGER,Types.INTEGER,
				Types.INTEGER,Types.INTEGER,Types.INTEGER,
				Types.INTEGER,Types.INTEGER,Types.INTEGER,
				Types.VARCHAR};
		StringBuffer sb = new StringBuffer();
		sb.append("update minfo set ");
		sb.append("acc_succ_count=?,acc_fail_count=?,phone_succ_count=?,");
		sb.append("phone_fail_count=?,id_succ_count=?,id_fail_count=?, ");
		sb.append("trans_limit=?,mer_chk_flag=?,refund_flag=?,amt_limit_d=?,amt_limit_m=?,card_limit=?, ");
		sb.append("white_acc_succ_count=?,white_id_succ_count=?,white_phone_succ_count=?,");
		sb.append("white_acc_fail_count=?,white_id_fail_count=?,white_phone_fail_count=? ");
		sb.append(" where  id = ?");
		
		
	/*	QkRisk qk = getOneMinfoQkRisk(minfo.getId());
		if(qk!=null){
			Object[] obj1 = { qkrisk.getCardType(), qkrisk.getSingleLimit()*100,qkrisk.getDayLimit()*100,
					qkrisk.getDayFailCount(),qkrisk.getDaySuccCount(),
					qkrisk.getMid() };
			int[] type1 = { 
					Types.VARCHAR, Types.INTEGER, Types.INTEGER,
					Types.INTEGER, Types.INTEGER, Types.VARCHAR};
			StringBuffer sb1 = new StringBuffer();
			sb1.append("update qk_risk set ");
			sb1.append("card_type=?,single_limit=?,day_limit=?,");
			sb1.append("day_fail_count=?,day_succ_count=? ");
			sb1.append(" where  mid = ?");
			 update(sb1.toString(), obj1, type1);

		}
		else{
			StringBuilder sql1 = new StringBuilder();
			sql1.append("insert into qk_risk(card_type,single_limit,day_limit,day_fail_count,");
			sql1.append("day_succ_count,mid) values(");
			sql1.append(Ryt.addQuotes(qkrisk.getCardType())).append(",");
			sql1.append(qkrisk.getSingleLimit()).append(",");
			sql1.append(qkrisk.getDayLimit()).append(",");
			sql1.append(qkrisk.getDayFailCount()).append(",");
			sql1.append(qkrisk.getDaySuccCount()).append(",");
			sql1.append(qkrisk.getMid()).append(")");
			update(sql1.toString());
			
		}*/
		
		return update(sb.toString(), obj, type);
	}
	
	public  List<QkRisk> getOneMinfoQkRisk(String mid) throws Exception {
		String sql = "select * from qk_risk where mid ='"+mid+"'";
		return	query(sql, QkRisk.class);
	}
	
	public  QkRisk getQkRisk(String mid,String cardType) throws Exception {
		String sql = "select * from qk_risk where mid ='"+mid+"' and card_type='"+cardType+"'";
		return	queryForObject(sql, QkRisk.class);
	}
	
	
	public int editQkRisk(QkRisk qkrisk) throws Exception{
		Object[] obj1 = { qkrisk.getSingleLimit()*100,qkrisk.getDayLimit()*100,
				qkrisk.getDayFailCount(),qkrisk.getDaySuccCount(),
				qkrisk.getMid(),qkrisk.getCardType() };
		int[] type1 = { 
				Types.VARCHAR, Types.INTEGER, Types.INTEGER,
				Types.INTEGER, Types.VARCHAR, Types.VARCHAR};
		StringBuffer sb1 = new StringBuffer();
		sb1.append("update qk_risk set ");
		sb1.append("single_limit=?,day_limit=?,");
		sb1.append("day_fail_count=?,day_succ_count=? ");
		sb1.append(" where  mid = ?");
		sb1.append(" and  card_type=?");
		return update(sb1.toString(), obj1, type1);
		  
	}
	
	public int addQkRisk(QkRisk qkrisk) throws Exception{
		StringBuilder sql1 = new StringBuilder();
		sql1.append("insert into qk_risk(card_type,single_limit,day_limit,day_fail_count,");
		sql1.append("day_succ_count,mid) values(");
		sql1.append(Ryt.addQuotes(qkrisk.getCardType())).append(",");
		sql1.append(qkrisk.getSingleLimit()*100).append(",");
		sql1.append(qkrisk.getDayLimit()*100).append(",");
		sql1.append(qkrisk.getDayFailCount()).append(",");
		sql1.append(qkrisk.getDaySuccCount()).append(",");
		sql1.append(qkrisk.getMid()).append(")");
		return update(sql1.toString());
	}
	
}
