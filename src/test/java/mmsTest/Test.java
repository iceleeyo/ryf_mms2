package mmsTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.FileUtil;

public class Test {
	
	private static final Object lock = new Object();

	public static void main(String[] args) {
//		StringBuilder sbr = new StringBuilder();
//		sbr.append("\n\r");
//		String str = sbr.toString().trim();
//		System.out.println();
		
//		Test t = new Test();
//		System.out.println(t.handleSendSeqId("1234567890121as"));
//		t.parseCert();
		try {
//			getPrivateKey();
			
			geneneratePublicKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseCert(){
		try {
		      FileInputStream fis = new FileInputStream("e:\\rongyifu.der");
		      CertificateFactory cf=CertificateFactory.getInstance("X509");
		      X509Certificate c=(X509Certificate) cf.generateCertificate(fis);
		      
		      System.out.println("Certficate for " + c.getSubjectDN().getName());
		      System.out.println("Generated with "+c.getSigAlgName());
		      System.out.println("== "+ c.getSubjectDN().toString());
		      String publicKey = Base64.encode(c.getPublicKey().getEncoded());
		      System.out.println("publicKey=" + publicKey);
		      
//		      Map<String, String> map = parseSubjectDN(c.getSubjectDN().toString());
//		      System.out.println("map: "+map);
		      
//		      String notBefore =c.getNotBefore().toString();//得到开始有效日期
//		      String notAfter = c.getNotAfter().toString();//得到截止日期
		      String serialNumber = c.getSerialNumber().toString(16);//得到序列号
		      String dn =c.getIssuerDN().getName();//得到发行者名
		      String sigAlgName =c.getSigAlgName();//得到签名算法
		      String algorithm =c.getPublicKey().getAlgorithm();//得到公钥算法 
		      
		      SimpleDateFormat intSDF = new SimpleDateFormat("yyyyMMdd");
		      System.out.println("notBefore=" + intSDF.format(c.getNotBefore()));
		      System.out.println("notAfter=" + intSDF.format(c.getNotAfter()));
		      System.out.println("serialNumber=" + serialNumber);
		      System.out.println("dn=" + dn);
		      System.out.println("sigAlgName=" + sigAlgName);
		      System.out.println("algorithm=" + algorithm);		      
		      
		      fis.close();
		    }
		    catch (Exception ex) {
		    	ex.printStackTrace();
		    }
	}
	
	public static void getPrivateKey() throws Exception{
		String privateKey = getPrivateKey("C:\\ca\\client\\test02_pkcs8.der");
//	    byte[] keyBytes = Base64.decode(privateKey);  
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey.getBytes());
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    System.out.println(keyFactory.generatePrivate(spec));
	    
//	    RSAPrivateKey pk = (RSAPrivateKey) keyFactory.generatePrivate(spec);
//	    String privateKeyString = Base64.encode(pk.getEncoded());
//	    System.out.println("privateKeyString="+privateKeyString);
	}  
	

    public static void geneneratePublicKey() throws Exception{
    	String file = "C:\\ca\\client\\test02.der";
//    	String key = getPrivateKey("C:\\ca\\client\\test02.der");
    	FileInputStream inpri = new FileInputStream(file);
    	CertificateFactory cf = CertificateFactory.getInstance("X509");
		X509Certificate c = (X509Certificate) cf.generateCertificate(inpri);
    	System.out.println(c.getPublicKey());
    	
//        KeySpec keySpec = new X509EncodedKeySpec(key.getBytes());  
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
//        PublicKey pk = keyFactory.generatePublic(keySpec);  
//        System.out.println(pk);
    }  
	
	public static String getPrivateKey(String file) throws IOException {
		byte[] pri = new byte[1024];
		FileInputStream inpri = new FileInputStream(file);
		inpri.read(pri);
		inpri.close();
		String privateKey = new String(pri, "UTF-8");
		return privateKey;
}
	
	private Map<String, String> parseSubjectDN(String subjectDN){
		Map<String, String> map = new HashMap<String, String>();
		String fields[] = subjectDN.split(",");
		for(String item : fields){
			String kv[] = item.split("=");
			if(Ryt.empty(kv[0]))
				continue;
			
			map.put(kv[0].trim(), Ryt.empty(kv[1]) ? "" : kv[1].trim());
		}
		
		return map;
	}
	
    public static void exportCert(X509Certificate cert, String name, String path)  
            throws Exception {  
        File dir = new File(path);  
        if (!dir.exists()) {  
            dir.mkdir();  
        }  
        File certFile = new File(path + File.separator + name + ".cer");  
        FileOutputStream fos = new FileOutputStream(certFile);  
        fos.write(cert.getEncoded());  
        fos.close();  
    }  
	
	private String handleSendSeqId(String refundId){
		int len = refundId.length();
		String sendSeqId = null;
		if(len <= 12){
			sendSeqId = refundId;
			for(int i = 0; i < 12 - len; i++){
				sendSeqId = "0" + sendSeqId;
			}
		} else if (len > 12){
			sendSeqId = refundId.substring(len - 12);
		}
		return sendSeqId;
	}
	
	private static String mul100Base(String amount) {
		BigDecimal b1 = new BigDecimal(amount.trim());
		BigDecimal b2 = new BigDecimal(100);
		return b1.multiply(b2).setScale(0, BigDecimal.ROUND_UP).toString();
	}
		
	static class Thred implements Runnable{
		Thred(int x){
			this.x = x;
		}
		private int x;

		@Override
		public void run() {
			Random r = new Random();
			int rInt = r.nextInt(1000);
			try {
				Thread.sleep(rInt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (lock) {
				while (true) {
					System.out.println(Thread.currentThread().getId()+":"+x);
				}
			}
		}
		
	}
}
