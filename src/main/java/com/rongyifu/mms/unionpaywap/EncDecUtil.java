package com.rongyifu.mms.unionpaywap;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



/**
 * 3DES加密
 * 
 * @version 1.0
 * @author
 * 
 */
public abstract class EncDecUtil {

	private static DesUtil2 desUtil;

	public static BASE64Decoder dec = new BASE64Decoder();
	public static BASE64Encoder enc = new BASE64Encoder();

	/**
	 * 解密
	 * 
	 * @return byte[] 二进制密钥
	 */
	public static String decXml(byte[] xmlKey, String encryptionContent)
			throws Exception {

		String xmlOut = "";
		// 解密报文体
		try {

			byte[] xmlStyle = dec.decodeBuffer(encryptionContent);
			byte[] xmlContentDataOut = null;

			xmlContentDataOut = desUtil.decrypt(xmlStyle, xmlKey);
			xmlOut = new String(xmlContentDataOut, "utf-8");
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		return xmlOut;
	}

	/**
	 * 加密
	 * 
	 * @return byte[] 二进制密钥
	 */
	public static String encXml(byte[] xmlKey, String xmlContent)
			throws Exception {

		// 生成加密体
		byte[] xmlContentData = xmlContent.getBytes("utf-8");
		// 生成密钥
		// System.out.println("密钥：\t" + Base64.encodeBase64String(key));

		xmlContentData = desUtil.encrypt(xmlContentData, xmlKey);
		//		
		xmlContent = enc.encode(xmlContentData);

		return xmlContent;
	}

	/**
	 * des密钥rsa加密
	 * 
	 * @return 完整解密报文
	 */
//	public static String decRsaCode(String xmlOut) throws Exception {
//
//		// rsa私钥
//		byte[] xmlPrivateKey = dec.decodeBuffer(IvrValue.privateKey);
//		// 拆开报文
//		String data1 = xmlOut.substring(0, xmlOut.indexOf("&"));
//		System.out.println("报文体:" + data1);
//		String data2 = xmlOut.substring(xmlOut.indexOf("&") + 1);
//		System.out.println("des加密密钥:" + data2);
//		// 解出密钥
//		try {
//			byte[] decodedData1 = RSACoder.decryptByPrivateKey(dec
//					.decodeBuffer(data2), xmlPrivateKey);
//			DesUtil.printbytes("decodedData1", decodedData1);
//
//			byte[] keyDes = new byte[32];
//			System.arraycopy(decodedData1, 0, keyDes, 0, 32);
//			DesUtil.printbytes("keyDes", keyDes);
//			// String data =
//			// Converts.bytesToHexString(decodedData1).substring(0,32);
//
//			// 报文体
//			xmlOut = decXml(keyDes, data1);
//			System.out.println("解报文:" + xmlOut);
//
//		} catch (InvalidKeySpecException e) {
//			System.out.println("密钥不合法");
//			e.printStackTrace();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
//
//		return xmlOut;
//	}

	/**
	 * des密钥rsa加密
	 * 
	 * @return 完整加密报文
	 */
//	public static String encRsaCode(String xmlContent) throws Exception {
//
//		// 生成随机数并MD5加密
//		String rsaCode = Md5Util.getMD5(StringHandler.getRandomKeys(6));
//		System.out.println("rsaCode:" + rsaCode);
//		// des加密报文体
//		xmlContent = encXml(rsaCode.getBytes(), xmlContent);
//		System.out.println("xmlContent:" + xmlContent);
//		// rsa私钥
//		byte[] xmlPrivateKey = dec.decodeBuffer(IvrValue.privateKey);
//
//		// rsa私钥加密
//		byte[] encodedData1 = RSACoder.encryptByPrivateKey(rsaCode.getBytes(),
//				xmlPrivateKey);
//
//		rsaCode = enc.encode(encodedData1);
//		System.out.println("rsaCode:" + rsaCode);
//
//		// 拼接完整报文
//		xmlContent = xmlContent + "&" + rsaCode;
//		System.out.println("xmlContent:" + xmlContent);
//
//		return xmlContent;
//	}

	/**
	 * rsa私钥解密
	 * 
	 * @return pin密码
	 */
//	public static String decPin(String pin) throws Exception {
//
//		// rsa私钥
//		byte[] pinPrivateKey = dec.decodeBuffer(IvrValue.pinPrivateKey);
//		// 解密
//		byte[] decodedData1 = RSACoder.decryptByPrivateKey(dec
//				.decodeBuffer(pin), pinPrivateKey);
//		System.out.println("解密文:" + new String(decodedData1));
//
//		pin = new String(decodedData1);
//
//		return pin;
//	}

	/**
	 * 将InputStream转化成String
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	/**
	 * 获取证书私钥
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getCertKey(String mi,String path) {

		String pwd = mi;//"";//"898000000000001";
		String certPath = path;//"D://dev2.pfx";
		String billRsaKey = "";
		try {

			KeyStore ks = KeyStore.getInstance("PKCS12");
			FileInputStream is = new FileInputStream(certPath);
			ks.load(is, pwd.toCharArray());
			is.close();
//			System.out.println("keystore type=" + ks.getType());
			Enumeration enuma = ks.aliases();
			String keyAlias = null;
			if (enuma.hasMoreElements()) {
				keyAlias = (String) enuma.nextElement();
//				System.out.println("alias=[" + keyAlias + "]");
			}

//			System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
			BASE64Encoder enc = new BASE64Encoder();
			PrivateKey privatekey = (PrivateKey) ks.getKey(keyAlias, pwd
					.toCharArray());
			billRsaKey = enc.encode(privatekey.getEncoded());
//			System.out.println("private key = " + billRsaKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billRsaKey;
	}

	/**
	 * 获取证书公钥
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getPublicCertKey(String mi,String path) {

		String pwd = mi;//"898000000000001";
		String publicCertPath = path;//"D://dev2.cer";
		String billRsaKey = "";
		try {

			// 签名 公钥解密
			CertificateFactory cff = CertificateFactory.getInstance("X.509");
			FileInputStream fis1 = new FileInputStream(publicCertPath); // 证书文件
			Certificate cf = cff.generateCertificate(fis1);
			PublicKey publicKey = cf.getPublicKey();

			byte[] pk = publicKey.getEncoded();
			billRsaKey = enc.encode(pk);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return billRsaKey;
	}


	public static void main(String[] args) {
//		System.out.println(EncDecUtil.getPublicCertKey());

//		String pwd = "898000000000001";
//		String certPath = "D://config/898000000000001.pfx";
//		String publicCertPath = "D://config/898000000000001.cer";
//		try {
//
//			// 签名 公钥解密
//			CertificateFactory cff = CertificateFactory.getInstance("X.509");
//			FileInputStream fis1 = new FileInputStream(publicCertPath); // 证书文件
//			Certificate cf = cff.generateCertificate(fis1);
//			PublicKey publicKey = cf.getPublicKey();
//			System.out.println("publicKey::::::::::::"
//					+ enc.encode(publicKey.getEncoded()));
//
//			byte[] pk = publicKey.getEncoded();
//			System.out.println(pk.length);
//
//			// 私钥解密
//
//			String privateKeyData = EncDecUtil.getCertKey();
//			System.out.println("privateKey::::::::::::" + privateKeyData);
//
//		} catch (Exception e) {
//
//		}

	}

}
