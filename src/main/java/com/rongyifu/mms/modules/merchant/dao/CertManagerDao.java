package com.rongyifu.mms.modules.merchant.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.CertManager;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.CurrentPage;

public class CertManagerDao {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CurrentPage<CertManager> queryCertInfo(Map<String, String> params,Integer pageNo){
		String fetchSql = "SELECT mid, algorithm, subject, not_before, not_after, import_time FROM cert_manager";
		String countSql = "SELECT COUNT(0) FROM cert_manager ";
		
		String condition = getCondition(params);
		String orderBy = " ORDER BY import_time DESC";
		return new PubDao().queryForPage(countSql+condition, fetchSql+condition+orderBy, pageNo, CertManager.class);
	}
	
	private String getCondition(Map<String, String> params){
		String certName = params.get("certName");
		String mid = params.get("mid");
		String certValidPeriod = params.get("certValidPeriod");
		
		String condition = " WHERE 1 = 1 ";
		if(!Ryt.empty(certName))
			condition += " AND subject LIKE '%" + Ryt.sql(certName.trim()) + "%'";
		if(!Ryt.empty(mid))
			condition += " AND mid =" + Ryt.addQuotes(mid.trim());
		if(!Ryt.empty(certValidPeriod)){
			condition += " AND not_before <= " + Ryt.sql(certValidPeriod.trim());
			condition += " AND not_after >= " + Ryt.sql(certValidPeriod.trim());
		}
		
		return condition;
	}
	
	@SuppressWarnings("rawtypes")
	public void saveCertInfo(Map<String, String> params) throws Exception{
		String mid = params.get("mid");
		String subjectDN = params.get("subjectDN");
		String publicKey = params.get("publicKey");
		String algorithm = params.get("algorithm");
		String notBefore = params.get("notBefore");
		String notAfter = params.get("notAfter");
		
		// 保存证书信息
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("INSERT INTO cert_manager(mid, algorithm, subject, not_before, not_after)");
		insertSql.append(" VALUES(");
		insertSql.append(Ryt.addQuotes(mid) + ",");
		insertSql.append(Ryt.addQuotes(algorithm) + ",");
		insertSql.append(Ryt.addQuotes(subjectDN) + ",");
		insertSql.append(Ryt.sql(notBefore) + ",");
		insertSql.append(Ryt.sql(notAfter) + ")");
		
		// 修改商户公钥
		String updateSql = "UPDATE minfo set public_key = " + Ryt.addQuotes(publicKey) + " WHERE id = " + Ryt.addQuotes(mid);
		
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(insertSql.toString());
		sqlList.add(updateSql);
		new PubDao().batchSqlTransaction2(sqlList);
	}
	
	@SuppressWarnings("rawtypes")
	public void deleteCert(String mid){
		// 删除证书信息
		String deleteSql = "DELETE FROM cert_manager WHERE mid = " + Ryt.addQuotes(mid);
		// 商户公钥清空
		String updateSql = "UPDATE minfo set public_key = '' WHERE id = " + Ryt.addQuotes(mid);
		
		new PubDao().batchSqlTransaction(new String[]{deleteSql, updateSql});
	}
}
