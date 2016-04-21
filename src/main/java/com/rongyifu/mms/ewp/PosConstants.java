/*********************************************************************
 * 
 * Copyright (C) 2011, Shanghai Chinaebi
 * All rights reserved.
 * http://www.chinaebi.com.cn/
 * 
 *********************************************************************/
package com.rongyifu.mms.ewp;

import cert.CertUtil;

import com.rongyifu.mms.common.RootPath;

public class PosConstants {
	// pos test 
//	public static final String signatureFile = RootPath.getRootpath().replace("%20", " ")+ "test/data/signature.xml";
//	public static final String ksName = RootPath.getRootpath().replace("%20", " ")+ "test/data/keystore.p12";
//	public static final String alias = "test";
//	public static final String storePass = "cardinfo";
//	public static final String keyPass = "cardinfo";
	
	// pos production 
	public static final String signatureFile = CertUtil.getCertPath("POS_XML");
	public static final String ksName = CertUtil.getCertPath("POS_KEYSTORE");
	public static final String alias = "DFSC";
	public static final String storePass = "chinaebi_123";
	public static final String keyPass = "chinaebi_123";
	
	
	public static final String defAppId = "0987654321";
	
	

}
