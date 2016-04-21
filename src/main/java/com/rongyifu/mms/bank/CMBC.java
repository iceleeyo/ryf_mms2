package com.rongyifu.mms.bank;

import cert.CertUtil;
import Union.JnkyServer;
/**
 * 民生银行
 * 
 * @author zhang.chaochao
 * 
 */
public class CMBC {
	/**
	 * wap加密
	 * @author zhang.chaochao
	 * @param signText
	 * @return String
	 * @throws Exception
	 */
	public static String wapSign(String signText) throws Exception {
		JnkyServer my = new JnkyServer(CertUtil.getCertPath("CMBC_WAP_BANK_CRT"), 
				CertUtil.getCertPath("CMBC_WAP_MER_CRT"), CertUtil.getCertPath("CMBC_WAP_MER_KEY"));
		String envelopData = my.EnvelopData(signText, "GBK");
		return envelopData;
	}
	
	/**
	 * wap解密
	 * @author zhang.chaochao
	 * @param signature
	 * @return String
	 * @throws Exception
	 */
	public static String wapDecryptData(String signature) throws Exception {
		JnkyServer my = new JnkyServer(CertUtil.getCertPath("CMBC_WAP_BANK_CRT"), 
				CertUtil.getCertPath("CMBC_WAP_MER_CRT"), CertUtil.getCertPath("CMBC_WAP_MER_KEY"));
		String decodeStr = my.DecryptData(signature, "GBK");
		return decodeStr;
	}
}
