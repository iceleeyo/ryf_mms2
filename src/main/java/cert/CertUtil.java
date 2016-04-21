package cert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.FileUtil;
import com.rongyifu.mms.utils.ParamUtil;


/**
 * 该类用于取到第三方支付公司或银行的的证书文件
 * @author yzy
 *
 */

public class CertUtil {

	private static Map<String, String> certMap = new HashMap<String, String>();
	
	static {
		String rootPath = ParamUtil.getPropertie("cert_path");
		
		Ryt.print("rootPath:"+rootPath);
		
		// 商户1私钥
		certMap.put("rytPriKey", rootPath + "rypay/1_pri.txt");
		// 融易付私钥
		certMap.put("privatekey", rootPath + "rypay/pri.txt");
		// 融易付公钥
		certMap.put("rytPubKey", rootPath + "rypay/pub.txt");
		
		// 汇付 网银
		certMap.put("PNR_MerKeyFile", rootPath + "pnr/MerPrK880963.key");
		certMap.put("PNR_PUKEYFILE", rootPath + "pnr/PgPubk.key");
		// 汇付  信用卡
		certMap.put("PNRT_MerKeyFile", rootPath + "pnrt/MerPrK881223.key");
		certMap.put("PNRT_PUKEYFILE", rootPath + "pnrt/PgPubk.key");

		// 环讯 
		certMap.put("IPSpublickey", rootPath + "ips/publickey.txt");
		certMap.put("IPSpublickey_i", rootPath + "ips/publickey_i.txt");

		// 交行 配置文件
		certMap.put("JT_B2B_PATH", rootPath + "bocomm/B2BMerchant.xml");
		certMap.put("JT_B2C_PATH", rootPath + "bocomm/B2CMerchant.xml");
		
		//招行证书公钥
		certMap.put("CMBPubKey", rootPath + "cmb/public.key");
		
		
		//工行证书公钥
		certMap.put("ICBC_USER_CRT", rootPath + "icbc/user.crt");
		
		//工行证书公钥
		certMap.put("ICBC_USER_KEY", rootPath + "icbc/user.key");
		
		//工行b2c测试
		certMap.put("ICBCB2C_USER_CRT", rootPath + "icbc/chinaebi002.crt");
		certMap.put("ICBCB2C_USER_KEY", rootPath + "icbc/chinaebi002.key");
		certMap.put("ICBCB2C_PUB_KEY", rootPath + "icbc/ebb2cpublic.crt");
		//工行B2B
		certMap.put("ICBCB2B_USER_CRT", rootPath + "icbc/Chinaebi001.crt");
		certMap.put("ICBCB2B_USER_KEY", rootPath + "icbc/Chinaebi001.key");
		certMap.put("ICBCB2B_PUBLIC_CRT", rootPath + "icbc/ebb2cpublic.crt");
		
		//工行wap测试
		certMap.put("ICBCWAP_USER_CRT", rootPath + "icbc/dyTestWap.crt");
		certMap.put("ICBCWAP_USER_KEY", rootPath + "icbc/dyTestWap.key");
		certMap.put("ICBCWAP_PUB_KEY",  rootPath + "icbc/ebb2cpublic.crt");
		
		//bill99
		certMap.put("BILL_JKS", rootPath + "bill/99931004816005890.jks");
		
		//CEAir Union Pay
		certMap.put("UNIONPAYWAP_USER_Sign_CRT", rootPath+"unionpaywap/872310045110201.pfx");
		certMap.put("UNIONPAYWAP_USER_Notify_CRT", rootPath+"unionpaywap/898510148990028.cer");
		
		//民生wap
		certMap.put("CMBC_WAP_BANK_CRT", rootPath + "cmbc/cmbc_wap.cer");
		certMap.put("CMBC_WAP_MER_CRT", rootPath + "cmbc/cmbc_wap.pfx");
		certMap.put("CMBC_WAP_MER_KEY", "123123");
		
		// pos证书
		certMap.put("POS_XML", rootPath + "pos/signature.xml");
		certMap.put("POS_KEYSTORE", rootPath + "pos/xpe_ryf_df_keystore.p12");
	}

	/**
	 * 根据名字等到相对应的文件路径，包含改文件
	 * @param paramName
	 * @return
	 */
	public static String getCertPath(String paramName) {
		return certMap.get(paramName);
	}
	/**
	 * 融易付私钥
	 * @return
	 * @throws IOException
	 */
	public static String getRyfPrivateKey() throws IOException{
		return FileUtil.getPrivateKey(getCertPath("privatekey"));
	}
	/**
	 * 融易付公钥
	 * @return
	 * @throws IOException
	 */
	public static String getRyfPublicKey() throws IOException{
		return FileUtil.getPrivateKey(getCertPath("rytPubKey"));
	}
}
