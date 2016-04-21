package com.rongyifu.mms.bank.citic;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;


/**
* @author GuoDong Ni
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class CertUtils {

	private static final String JKS = "JKS";
	private static final String P12 = "P12";
	private static final String PKCS12 = "PKCS12";
	private static final String JCEKS = "JCEKS";
	private static final String JCK = "JCK";
	private static final String PFX = "PFX";

	/**
	 *  获取信息摘要
	 * @param textBytes 原信息
	 * @param algorithm 算法
	 * @return 返回该算法的信息摘要
	 * @throws Exception
	 */
	public static byte[] msgDigest(byte[] textBytes, String algorithm)
		throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
		messageDigest.update(textBytes);
		return messageDigest.digest();
	}

	/**
	 * 通过证书获取公钥
	 * @param certPath 证书的路径
	 * @return 返回公钥
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String certPath) throws Exception {
		Certificate cert = getCert(certPath);
		return cert.getPublicKey();
	}

	public static Certificate getCert(String certPath) throws Exception {
		InputStream streamCert = new FileInputStream(certPath);
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		Certificate cert = factory.generateCertificate(streamCert);
		return cert;
	}

	public static byte[] getCertBase64(String certPath) throws Exception {
		Certificate cert = getCert(certPath);
		byte[] encoded = Base64.encode(cert.getEncoded());
		return encoded;
	}
	/**
	 * 通过密钥文件或者证书文件获取私钥
	 * @param keyPath  密钥文件或者证书的路径
	 * @param passwd   密钥文件或者证书的密码
	 * @return 返回私钥
	 * @throws Exception
	 */ 
	public static PrivateKey getPrivateKey(String keyPath, String passwd)
		throws Exception {

		String keySuffix = keyPath.substring(keyPath.indexOf(".") + 1);
		String keyType = JKS;
		if (keySuffix == null || keySuffix.trim().equals(""))
			keyType = JKS;
		else
			keySuffix = keySuffix.trim().toUpperCase();

		if (keySuffix.equals(P12)) {
			keyType = PKCS12;
		} else if (keySuffix.equals(PFX)) {
			keyType = PKCS12;
		} else if (keySuffix.equals(JCK)) {
			keyType = JCEKS;
		} else
			keyType = JKS;

		return getPrivateKey(keyPath, passwd, keyType);

	}

	/**
	 * 通过证书或者密钥文件获取私钥
	 * @param keyPath  证书或者密钥文件
	 * @param passwd   密钥保存密码
	 * @param keyType  密钥保存类型
	 * @return    返回私钥
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(
		String keyPath,
		String passwd,
		String keyType)
		throws Exception {

		KeyStore ks = KeyStore.getInstance(keyType);
		char[] cPasswd = passwd.toCharArray();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(keyPath);
			ks.load(fis, cPasswd);
			fis.close();
		} finally {
			if (fis != null) {
				fis.close();
				fis = null;
			}
		}
		Enumeration aliasenum = ks.aliases();
		String keyAlias = null;
		PrivateKey key = null;
		while (aliasenum.hasMoreElements()) {
			keyAlias = (String) aliasenum.nextElement();
			key = (PrivateKey) ks.getKey(keyAlias, cPasswd);
			if (key != null)
				break;
		}
		return key;
	}
	/**
	 * 	使用私钥签名 
	 * @param key 私钥
	 * @param b 需要签名的byte 数组
	 * @return 返回签名后的byte
	 * @throws Exception
	 */
	public static byte[] sign(PrivateKey priKey, byte[] b) throws Exception {
		//try{
		
		 System.out.println("算法："+priKey.getAlgorithm());
		 Signature sig = Signature.getInstance("SHA1withRSA");
		//}catch(NoSuchAlgorithmException e){
			//System.out.println("错误："+e.getMessage());
		//}
		sig.initSign(priKey);
		sig.update(b);
		return sig.sign();
	}
	/**
	 * 	使用公钥验证 
	 * @param pubKey 公钥
	 * @param orgByte 原始数据byte 数组
	 * @param signaByte 签名后的数据byte 数组
	 * @return 是否验证结果
	 * @throws Exception
	 */
	public static boolean verify(
		PublicKey pubKey,
		byte[] orgByte,
		byte[] signaByte)
		throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(pubKey);
		sig.update(orgByte);
		return sig.verify(signaByte);
	}

	public static void main(String[] args) throws Exception {
		String clientPriKey = "D:/epos/newkey/server.pfx";
		String clientPubCert = "D:/epos/newkey/server.cer";
		String cliPasswd = "123456";
		String serPasswd = "123456";
		String orgStr = "1000000000000045   1211068000216226890000025824  000000001030020121106115028609";
		//用客户端私钥签名
		PrivateKey priKey = getPrivateKey(clientPriKey, cliPasswd);

		byte[] testByte = sign(priKey, orgStr.getBytes("UTF8"));
		String testBase64 = new String(Base64.encode(testByte), "UTF8");	
		System.out.println("测试签名结果           : " + testBase64);	
		
		byte[] signtestBase64 = Base64.decode(testBase64.getBytes("UTF8"));	
		//用客户端的公钥验证信息
		PublicKey pubKey = getPublicKey(clientPubCert);
		boolean isv = verify(pubKey, orgStr.getBytes("UTF8"), signtestBase64);
		System.out.println("测试签名验证结果 a         : " + isv);		
	}
}
