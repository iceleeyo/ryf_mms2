package com.rongyifu.mms.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings("unchecked")
public class RefundLogRowMapper implements RowMapper {
	

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		RefundLog refundLog = new RefundLog();
		refundLog.setId(rs.getInt("id"));
		refundLog.setTseq(rs.getString("tseq"));
		refundLog.setMdate(rs.getString("mdate"));
		refundLog.setMid(rs.getString("mid"));
		refundLog.setOid(rs.getString("oid"));
		refundLog.setOrg_mdate(rs.getInt("org_mdate"));
		refundLog.setOrg_oid(rs.getString("org_oid"));
		refundLog.setRef_amt(rs.getLong("ref_amt"));
		refundLog.setSys_date(rs.getInt("sys_date"));
		refundLog.setGate(rs.getInt("gate"));
		refundLog.setCard_no(rs.getString("card_no"));
		refundLog.setUser_name(rs.getString("user_name"));
		refundLog.setReq_date(rs.getInt("req_date"));
		refundLog.setPro_date(rs.getInt("pro_date"));
		refundLog.setRef_date(rs.getInt("ref_date"));
		refundLog.setStat(rs.getInt("stat"));
		refundLog.setReason(rs.getString("reason"));
		refundLog.setEtro_reason(rs.getString("etro_reason"));
		
		return refundLog;
	}
	

}
