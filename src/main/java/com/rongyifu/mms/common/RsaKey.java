package com.rongyifu.mms.common;

import java.security.Key;
//import sun.misc.BASE64Encoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;


public class RsaKey {
	
	//public static BASE64Encoder enc = new BASE64Encoder();

	public  String getRSAPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get("RSAPrivateKey");
		return net.rytong.encrypto.provider.Base64.encode(key.getEncoded());
		//return enc.encode(key.getEncoded());
	}

	public  String getRSAPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get("RSAPublicKey");
		return net.rytong.encrypto.provider.Base64.encode(key.getEncoded());
		//return enc.encode((key.getEncoded()));
	}

	public  Map<String, Object> initRSAKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 获取公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 获取私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put("RSAPublicKey", publicKey);
		keyMap.put("RSAPrivateKey", privateKey);
		return keyMap;
	}

}
