package com.rongyifu.mms.utils;

import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cert.CertUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoUtils {
	
	  public static Map<String,String> rytEncrypt(String checkBody,String checkValue,
			  			String merPriKey,String rytPubKey)throws Exception{
		  String desKey=generateDesKey();
		  String param=	checkBody+"&chkValue="+rytRsaSign(checkValue, merPriKey);
		  String p=desEncrypt(param, desKey);
		  String k=rytRsaEncryptByPublicKey(desKey, rytPubKey);
		  Map<String,String> resMap=new HashMap<String,String>();
		  //String p=URLEncoder.encode(enParam,"UTF-8");
		 // String k=URLEncoder.encode(enKey,"UTF-8");
		  resMap.put("p", p);
		  resMap.put("k", k);
		  return resMap;
	  }
    /**
     * 获得8位的随机des密码
     * @return
     */
	  public static String generateDesKey()
	  {
	    try
	    {
	      String radStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234!@#$%^&*()56789qwertyuioplkjhgfdsazxcvbnm";
	      StringBuffer generateRandStr = new StringBuffer();
	      Random rand = new Random();
	      int length = 8;
	      int radLen = radStr.length();
	      for (int i = 0; i < length; i++)
	      {
	        int randNum = rand.nextInt(radLen - 2);
	        generateRandStr.append(radStr.substring(randNum, randNum + 1));
	      }
	      return generateRandStr.toString();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }return null;
	  }
    /**
     * ECB模式的des加密，以base64的编码输出
     * @param message
     * @param key
     * @return
     * @throws Exception
     */
	  public static String desEncrypt(String message, String key)throws Exception{
		// DES/ECB CBC CFB OFB /PKCS5Padding  NoPadding  加密/模式/填充
	    Cipher cipher = Cipher.getInstance("DES");//默认就是 DES/ECB/PKCS5Padding
	    DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	    cipher.init(1, secretKey);
	    return new BASE64Encoder().encode(cipher.doFinal(message.getBytes("UTF-8")));
	  }
	  /**
	   * ECB模式的des解密
	   * @param message
	   * @param key
	   * @return
	   * @throws Exception
	   */
	  public static String desDecrypt(String message, String key)throws Exception{
	    Cipher cipher = Cipher.getInstance("DES");
	    DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	    cipher.init(2, secretKey);
	    return new String(cipher.doFinal(Base64.decode(message)), "UTF-8");
	  }
	  
	  /**********************rsa加解密、签名验签**************************************/
	  /**
	   * 通过rsa私钥签名
	   * @param plainTxt 签名前普通字符串
	   * @param privateKey 私钥
	   * @return
	   * @throws Exception 
	   */
	  public static String  rytRsaSign(String plainTxt,String privateKey) throws Exception{
	      try {
	    	  String chk=RSAsign(plainTxt.getBytes(), privateKey);
	          return URLEncoder.encode(chk,"UTF-8");
	      } catch (Exception e) {
	    	  throw new Exception("rsa签名异常！");
	      }
	  }
	  /**
	   * 通过rsa公钥验签
	   * @param plainTxt 签名前普通字符串
	   * @param signStr  签名后的字符串
	   * @param publicKey 公钥
	   * @return
	 * @throws Exception 
	   */
	  public static void rytRsaVerify(String plainTxt, String signStr) throws Exception{
    	 // signStr = URLDecoder.decode(signStr,"UTF-8");
    	  boolean flag = RSAverify(plainTxt.getBytes(), CertUtil.getRyfPublicKey(), signStr); 
          if(!flag)  throw new Exception("验签失败！");
	  }
	  /**
	   * 通过rsa公钥加密
	   * @param plainTxt
	   * @param publicKey
	   * @return
	   * @throws Exception 
	   */
	  public static String rytRsaEncryptByPublicKey(String plainTxt,String publicKey)throws Exception{
		  try {
			  byte[] encryptData=RSAencryptByPublicKey(plainTxt.getBytes(), publicKey);
			return encryptBASE64(encryptData);
		} catch (Exception e) {
			throw new Exception("rsa加密异常！");
		}
	  }
	  /**
	   *  通过rsa私钥解密
	   * @param encryptData
	   * @param privateKey
	   * @return
	   * @throws Exception
	   */
	  public static String rytRsaDecryptByPrivateKey(String encryptData,String privateKey) throws Exception{
		  try {
			byte[] data=decryptBASE64(encryptData);
			byte[] decryptData=RSAdecryptByPrivateKey(data, privateKey);
			return new String(decryptData);
		} catch (Exception e) {
			throw new Exception("rsa解密异常！");
		}
	  }
	  
	  /*********************rsa加解密、签名验签   *******************************/
	  
	  public static String encryptBASE64(byte[] key) throws Exception {
		    return new BASE64Encoder().encodeBuffer(key);
		  }

	  public static byte[] decryptBASE64(String key) throws Exception {
		    return new BASE64Decoder().decodeBuffer(key);
	  }
		  /**
		   * rsa私钥的签名
		   * @param data
		   * @param privateKey
		   * @return
		   * @throws Exception
		   */
		  public static String RSAsign(byte[] data, String privateKey) throws Exception{
		    byte[] keyBytes = decryptBASE64(privateKey);

		    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		    PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		    Signature signature = Signature.getInstance("MD5withRSA");
		    signature.initSign(priKey);
		    signature.update(data);

		    return encryptBASE64(signature.sign());
		  }

		  /**
		   * rsa公钥的验签
		   * @param data
		   * @param publicKey
		   * @param sign
		   * @return
		   * @throws Exception
		   */
		  public static boolean RSAverify(byte[] data, String publicKey, String sign)throws Exception {
		    byte[] keyBytes = decryptBASE64(publicKey);

		    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		    PublicKey pubKey = keyFactory.generatePublic(keySpec);

		    Signature signature = Signature.getInstance("MD5withRSA");
		    signature.initVerify(pubKey);
		    signature.update(data);

		    return signature.verify(decryptBASE64(sign));
		  }
		  
		  /**
		   * rsa的用私钥解密
		   * @param data
		   * @param key
		   * @return
		   * @throws Exception
		   */
		  public static byte[] RSAdecryptByPrivateKey(byte[] data, String key)throws Exception{
		    byte[] keyBytes = decryptBASE64(key);

		    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		    Cipher cipher = Cipher.getInstance("RSA");
		    cipher.init(Cipher.DECRYPT_MODE, privateKey);

		    return cipher.doFinal(data);
		  }
		  /**
		   * rsa公钥解密
		   * @param data
		   * @param key
		   * @return
		   * @throws Exception
		   */
		  public static byte[] RSAdecryptByPublicKey(byte[] data, String key)throws Exception{
		    byte[] keyBytes = decryptBASE64(key);

		    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		    Cipher cipher = Cipher.getInstance("RSA");
		    cipher.init(2, publicKey);

		    return cipher.doFinal(data);
		  }

		  /**
		   * rsa公钥加密
		   * @param data
		   * @param key
		   * @return
		   * @throws Exception
		   */
		  public static byte[] RSAencryptByPublicKey(byte[] data, String key)throws Exception{
		    byte[] keyBytes = decryptBASE64(key);

		    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    Key publicKey = keyFactory.generatePublic(x509KeySpec);

		    Cipher cipher = Cipher.getInstance("RSA");
		    cipher.init(1, publicKey);

		    return cipher.doFinal(data);
		  }
		  /**
		   * rsa私钥加密
		   * @param data
		   * @param key
		   * @return
		   * @throws Exception
		   */
		  public static byte[] RSAencryptByPrivateKey(byte[] data, String key) throws Exception{
		    byte[] keyBytes = decryptBASE64(key);

		    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		    Cipher cipher = Cipher.getInstance("RSA");
		    cipher.init(1, privateKey);

		    return cipher.doFinal(data);
		  }
}
