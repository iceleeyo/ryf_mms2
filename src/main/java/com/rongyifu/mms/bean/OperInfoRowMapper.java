package com.rongyifu.mms.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings("unchecked")
public class OperInfoRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		OperInfo oper = new OperInfo();
		oper.setMid(rs.getString("mid"));
		oper.setOperId(rs.getInt("oper_id"));
		oper.setOperPass(rs.getString("oper_pass"));
		oper.setOperEmail(rs.getString("oper_email"));
		oper.setOperName(rs.getString("oper_name"));
		oper.setOperTel(rs.getString("oper_tel"));
		oper.setState(rs.getInt("state"));
		oper.setLoginIn(rs.getString("logined"));
		oper.setMtype(rs.getInt("mtype"));
		return oper;
	}

}


