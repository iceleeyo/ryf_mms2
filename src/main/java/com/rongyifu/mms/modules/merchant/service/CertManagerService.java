package com.rongyifu.mms.modules.merchant.service;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.CertManager;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.modules.merchant.dao.CertManagerDao;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;

public class CertManagerService {
	
	public CurrentPage<CertManager> queryCertInfo(Map<String, String> params, int pageNo){
		CertManagerDao dao =  new CertManagerDao();
		return dao.queryCertInfo(params, pageNo);
	}
	
	public String uploadCert(FileTransfer file, String mid){
		try {
			
			Map<String, String> map = parseCert(file);
			map.put("mid", mid);
			
			// 保存证书信息 
			new CertManagerDao().saveCertInfo(map);
			
			// 刷新ewp
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "minfo");
			p.put("k", mid);
			EWPService.refreshEwpETS(p);
//			EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "刷新ewp失败!";
			
			return "导入成功！";
		} catch(Exception e){
			LogUtil.printErrorLog("CertManagerService", "uploadCert", "filename=" + file.getName(), e);
			return "导入失败！";
		}
	}
	
	private Map<String, String> parseCert(FileTransfer file) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		X509Certificate c = (X509Certificate) cf.generateCertificate(file.getInputStream());

		Map<String, String> map = new HashMap<String, String>();
		// 获取标题
		String subjectDN = c.getSubjectDN().getName();
		map.put("subjectDN", subjectDN);
		// 获取公钥
		String publicKey = Base64.encode(c.getPublicKey().getEncoded()); 
		map.put("publicKey", publicKey);
		// 获取公钥算法
		String algorithm =c.getPublicKey().getAlgorithm();//得到公钥算法 
		map.put("algorithm", algorithm);		
		// 获取证书有效期
		SimpleDateFormat intSDF = new SimpleDateFormat("yyyyMMdd");
		String notBefore = intSDF.format(c.getNotBefore());
		String notAfter = intSDF.format(c.getNotAfter());
		map.put("notBefore", notBefore);
		map.put("notAfter", notAfter);
		
		return map;
	}
	
}
