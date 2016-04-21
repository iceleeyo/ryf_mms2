package com.rongyifu.mms.modules.merchant.dao;

import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.bean.DynamicToken;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

@SuppressWarnings("rawtypes")
public class DynamicTokenDao extends PubDao {
	
	public boolean isTokenActive(String mid,Integer operId){
		boolean flag = false;
		if(StringUtils.isBlank(mid) || null == operId) return flag;
		DynamicToken token = queryForObject("SELECT token_sn,system,status,mid,oper_id FROM token_config WHERE mid = " + Ryt.addQuotes(mid) + " AND oper_id = " + operId + " limit 1", DynamicToken.class);
		if(null != token && token.getStatus() == DynamicToken.STATUS_ACTIVE) flag = true;
		return flag;
	}
	
	public int setStatus(int toStatus,int id){
		String sql = "UPDATE token_config SET status = " + toStatus;
		if(toStatus == DynamicToken.STATUS_NOT_ACTIVE)
			sql += ", disable_time = NOW() ";
		else if(toStatus == DynamicToken.STATUS_ACTIVE)
			sql += ", enable_time = NOW() ";
		sql += ", update_time = NOW() WHERE id = "+ id;
		return update(sql);
	}
	
	@SuppressWarnings("unchecked")
	public CurrentPage<DynamicToken> queryConfigForPage(DynamicToken token,Integer pageNo){
		String fetchSql = "SELECT tc.token_sn,tc.mid,tc.id,tc.oper_id,tc.system,tc.bind_time,tc.disable_time,tc.status,oi.oper_name"+
						  " FROM token_config tc"+
						  " LEFT JOIN oper_info oi ON (tc.mid =oi.mid AND tc.oper_id = oi.oper_id)";
		String countSql = "SELECT COUNT(*) FROM token_config tc";
		String condition = getCondition(token);
		return queryForPage(countSql+condition, fetchSql+condition, pageNo, DynamicToken.class);
	}
	
	private String getCondition(DynamicToken token){
		StringBuilder sbr = new StringBuilder(" WHERE 1=1");
		if(StringUtils.isNotBlank(token.getTokenSn())){
			sbr.append(" AND tc.token_sn = ").append(Ryt.addQuotes(token.getTokenSn()));
		}
		if(null != token.getOperId()){
			sbr.append(" AND tc.oper_id = ").append(token.getOperId());
		}
		if(null != token.getStatus()){
			sbr.append(" AND tc.status = ").append(token.getStatus());
		}
		return sbr.toString();
	}

	public DynamicToken queryConfigById(Integer id){
		if(null == id) return null;
		String sql = "select * from token_config where id = "+id;
		return queryForObject(sql, DynamicToken.class);
	}

	public DynamicToken getConfigByUser(String mid,Integer operId){
		if(StringUtils.isBlank(mid) || null == operId) return null;
		return queryForObject("SELECT token_sn,system,status,mid,oper_id FROM token_config WHERE mid = " + Ryt.addQuotes(mid) + " AND oper_id = " + operId + " limit 1", DynamicToken.class);
	}
	
	public int modifyTokenConfig(DynamicToken token){
		if(StringUtils.isBlank(token.getTokenSn())||StringUtils.isBlank(token.getMid()) || null == token.getOperId() || null == token.getSystem()|| null == token.getId()) return 0;
		StringBuilder sql = new StringBuilder("update token_config set");
		sql.append(" token_sn=?,mid=?,oper_id=?,system=?,update_time=NOW()");
		sql.append(" where id = ?");
		return update(sql.toString(), new Object[]{token.getTokenSn(),token.getMid(),token.getOperId(),token.getSystem(),token.getId()});
	}
	
	public boolean isRegistered(String tokenSn){
		if(StringUtils.isBlank(tokenSn)) return false;
		String sql ="select count(*) from tokens where token_sn = ?";
		int count = queryForInt(sql, new Object[]{tokenSn});
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	public int addTokenConfig(DynamicToken token){
		token.setTokenSn(Ryt.sql(token.getTokenSn()));
		token.setMid(Ryt.sql(token.getMid()));
		if(StringUtils.isBlank(token.getTokenSn())||StringUtils.isBlank(token.getMid()) || null == token.getOperId() || null == token.getSystem()) return 0;
		StringBuilder sbr = new StringBuilder("insert into token_config (token_sn,mid,oper_id,system,bind_time,update_time) values ("); 
		sbr.append(Ryt.addQuotes(token.getTokenSn())).append(",");
		sbr.append(Ryt.addQuotes(token.getMid())).append(",");
		sbr.append(token.getOperId()).append(",");
		sbr.append(token.getSystem()).append(",NOW(),NOW())");
		return update(sbr.toString());
	}
	
	public int addToken(DynamicToken token){
		StringBuilder sbr = new StringBuilder("insert into tokens (token_sn,user_name,system,bind_time) values(");
		sbr.append(Ryt.sql(token.getTokenSn())).append(",");
		sbr.append(Ryt.addQuotes(token.getUserName())).append(",");
		sbr.append(token.getSystem()).append(",NOW())"); 
		return update(sbr.toString());
	}

}
