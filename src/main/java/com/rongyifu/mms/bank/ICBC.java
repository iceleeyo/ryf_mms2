package com.rongyifu.mms.bank;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import com.rongyifu.mms.utils.LogUtil;


import cert.CertUtil;
import cn.com.infosec.icbc.ReturnValue;

public class ICBC {

	public static String ICBCMERID = "0200EC23720705";// 商户代码
	public static String ICBCMERACC = "0200012719201015351";// 商户帐号

	public static String ICBCKEYPWD = "12345678";

	public static String getData(Map<String, String> p) {
		StringBuffer tranDataText = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>");
		tranDataText.append("<B2CReq>");
		tranDataText.append("<interfaceName>ICBC_WAPB_B2C</interfaceName>");
		tranDataText.append("<interfaceVersion>1.0.0.0</interfaceVersion>");
		tranDataText.append("<orderInfo>");
		tranDataText.append("<orderDate>" + p.get("sysDate") + "</orderDate>");
		tranDataText.append("<orderid>" + p.get("tseq") + "</orderid>");
		tranDataText.append("<amount>" + p.get("transAmt") + "</amount>");// 以分为单位
		tranDataText.append("<curType>001</curType>");
		tranDataText.append("<merID>" + ICBCMERID + "</merID>");
		tranDataText.append("<merAcct>" + ICBCMERACC + "</merAcct>");
		tranDataText.append("</orderInfo>");
		tranDataText.append("<custom>");
		tranDataText.append("<Language>zh_CN</Language>");
		tranDataText.append("</custom>");
		tranDataText.append("<message>");
		tranDataText.append("<goodsID>" + p.get("tseq") + "</goodsID>");
		tranDataText.append("<goodsName></goodsName>");
		tranDataText.append("<goodsNum></goodsNum>");
		tranDataText.append("<carriageAmt></carriageAmt>");
		tranDataText.append("<merHint></merHint>");
		tranDataText.append("<remark1></remark1>");
		tranDataText.append("<remark2></remark2>");
		tranDataText.append("<merURL>" + p.get("retURL") + "</merURL>");
		tranDataText.append("<merVAR>" + p.get("merVAR") + "</merVAR>");
		tranDataText.append("</message>");
		tranDataText.append("</B2CReq>");

		StringBuffer resXml = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		byte[] tranDataByte = ReturnValue.base64enc(tranDataText.toString().getBytes());
		String tranData = new String(tranDataByte).toString();

		resXml.append("<tranData>").append(tranData).append("</tranData>");

		// String password = "12345678";
		byte[] byteSrc = tranDataText.toString().getBytes();
		char[] keyPass = ICBCKEYPWD.toCharArray();

		try {

			FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBC_USER_CRT"));
			byte[] bcert = new byte[in1.available()];
			in1.read(bcert);
			in1.close();

			FileInputStream in2 = new FileInputStream(CertUtil.getCertPath("ICBC_USER_KEY"));
			byte[] bkey = new byte[in2.available()];
			in2.read(bkey);
			in2.close();

			byte[] sign = ReturnValue.sign(byteSrc, byteSrc.length, bkey,
					keyPass);
			byte[] EncSign = ReturnValue.base64enc(sign);
			byte[] EncCert = ReturnValue.base64enc(bcert);

			resXml.append("<encSign>").append(new String(EncSign).toString()).append("</encSign>");
			resXml.append("<encCert>").append(new String(EncCert).toString()).append("</encCert>");

			return resXml.toString();

		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer res = new StringBuffer();
			res
					.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><res><state>");
			res.append(1);
			res.append("</state><result>");
			res.append(e.getMessage());
			res.append("</result></res>");
			return res.toString();
		}
	}

	// public static int veryB2C(String tranData, String sign) throws Exception
	// {
	//
	// byte[] EncSign = sign.getBytes();
	// byte[] DecSign = ReturnValue.base64dec(EncSign);
	// byte[] byteSrc = tranData.getBytes();
	//		
	// FileInputStream in1 = new
	// FileInputStream("D://key/icbcb2c/ebb2cpublic.crt");
	// byte[] bcert = new byte[in1.available()];
	// in1.read(bcert);
	// in1.close();
	// return ReturnValue.verifySign(byteSrc,byteSrc.length,bcert,DecSign);
	//
	// }

	/**
	 * 	
/**
	 * 
	 * @param orderDate
	 *            14 20100901162142
	 * @param
	 * @return
	 */
	public static String genB2CWebForm(Map<String, String> p) throws Exception {

		StringBuffer tranDataText = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>");
		tranDataText.append("<B2CReq>");
		tranDataText.append("<interfaceName>").append(p.get("interfaceName")).append("</interfaceName>");
		tranDataText.append("<interfaceVersion>").append(p.get("interfaceVersion")).append("</interfaceVersion>");
		tranDataText.append("<orderInfo>");
		// 交易日期时间 orderDate =14 必输， 格式为：YYYYMMDDHHmmss
		// 目前要求在银行系统当前时间的前后十分钟范围内，否则判定交易时间非法。
		tranDataText.append("<orderDate>").append(p.get("orderDate")).append("</orderDate>");
		// 支付币种 curType = 3 必输， 用来区分一笔支付的币种，目前工行只支持使用人民币（001）支付。 取值： “001”
		tranDataText.append("<curType>001</curType>");
		// 必输， 唯一确定一个商户的代码，由商户在工行开户时，由工行告知商户。
		tranDataText.append("<merID>").append(p.get("merID")).append("</merID>");
		tranDataText.append("<subOrderInfoList>");
		// 多个订单有多个
		tranDataText.append("<subOrderInfo>");
		// 必输，每笔订单都需要有不同的订单号； 客户支付后商户网站产生的一个唯一的定单号，
		tranDataText.append("<orderid>").append(p.get("orderid")).append("</orderid>");
		// 必输，每笔订单一个； 客户支付订单的总金额，一笔订单一个，以分为单位。不可以为零，必需符合金额标准。
		tranDataText.append("<amount>").append(p.get("amount")).append("</amount>");
		// 分期付款期数 installmentTimes MAX（2） 必输，每笔订单一个；
		// 取值：1、3、6、9、12、18、24；1代表全额付款，必须为以上数值，否则订单校验不通过。
		tranDataText.append("<installmentTimes>1</installmentTimes>");
		// 商户账号 merAcct MAX(19) 必输，每笔订单一个，可以相同；
		// 商户入账账号，只能交易时指定。（商户付给银行手续费的账户，可以在开户的时候指定，也可以用交易指定方式；用交易指定方式则使用此商户账号）
		tranDataText.append("<merAcct>").append(p.get("merAcct")).append("</merAcct>");
		tranDataText.append("<goodsID></goodsID>");
		tranDataText.append("<goodsName>ryt</goodsName>");
		tranDataText.append("<goodsNum>1</goodsNum>");
		tranDataText.append("<carriageAmt></carriageAmt>");
		tranDataText.append("</subOrderInfo>");
		tranDataText.append("</subOrderInfoList>");
		tranDataText.append("</orderInfo>");
		tranDataText.append("<custom>");
		tranDataText.append("<verifyJoinFlag>0</verifyJoinFlag>");
		tranDataText.append("<Language>ZH_CN</Language>");
		tranDataText.append("</custom>");
		tranDataText.append("<message>");
		tranDataText.append("<creditType>2</creditType>");
		// 取值“0”：无论支付成功或者失败，银行都向商户发送交易通知信息；
		// 取值“1”，银行只向商户发送交易成功的通知信息。
		tranDataText.append("<notifyType>HS</notifyType>");
		tranDataText.append("<resultType>1</resultType>");
		tranDataText.append("<merReference></merReference>");
		tranDataText.append("<merCustomIp>").append(p.get("ewpHost"));
		tranDataText.append("</merCustomIp>");
		tranDataText.append("<goodsType>1</goodsType>");
		tranDataText.append("<merCustomID></merCustomID><merCustomPhone></merCustomPhone>");
		tranDataText.append("<goodsAddress></goodsAddress>");
		tranDataText.append("<merOrderRemark></merOrderRemark>");
		tranDataText.append("<merHint></merHint>");
		tranDataText.append("<remark1></remark1><remark2></remark2>");
		tranDataText.append("<merURL>").append(p.get("merURL")).append("</merURL>");
		tranDataText.append("<merVAR>").append("").append("</merVAR>");
		tranDataText.append("</message>");
		tranDataText.append("</B2CReq>");
		
		byte[] byteSrc = tranDataText.toString().getBytes();
		char[] keyPass = p.get("key").toCharArray();

		FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBCB2C_USER_CRT"));
		byte[] bcert = new byte[in1.available()];
		in1.read(bcert);
		in1.close();

		FileInputStream in2 = new FileInputStream(CertUtil.getCertPath("ICBCB2C_USER_KEY"));
		byte[] bkey = new byte[in2.available()];
		in2.read(bkey);
		in2.close();

		byte[] sign = ReturnValue.sign(byteSrc, byteSrc.length, bkey, keyPass);
		byte[] EncSign = ReturnValue.base64enc(sign);
		byte[] EncCert = ReturnValue.base64enc(bcert);
		byte[] EncSrc = ReturnValue.base64enc(byteSrc);

		StringBuffer ret = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		ret.append("<icbcb2c>");
		ret.append("<src>").append(new String(EncSrc)).append("</src>");
		ret.append("<sign>").append(new String(EncSign)).append("</sign>");
		ret.append("<cert>").append(new String(EncCert)).append("</cert>");
		ret.append("</icbcb2c>");
		return ret.toString();
	}
	
	/*
	 * 工行wap签名  
	 */
	public static String icbcwapsign(Map<String, String> p) throws Exception {

		StringBuffer tranDataText = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?>");
		tranDataText.append("<B2CReq>");
		tranDataText.append("<interfaceName>").append(p.get("interfaceName")).append("</interfaceName>");
		tranDataText.append("<interfaceVersion>").append(p.get("interfaceVersion")).append("</interfaceVersion>");
		tranDataText.append("<orderInfo>");
		tranDataText.append("<orderDate>").append(p.get("orderDate")).append("</orderDate>");
		tranDataText.append("<orderid>").append(p.get("orderid")).append("</orderid>");
		tranDataText.append("<amount>").append(p.get("amount")).append("</amount>");
		tranDataText.append("<installmentTimes>1</installmentTimes>");
		tranDataText.append("<curType>001</curType>");
		tranDataText.append("<merID>").append(p.get("merID")).append("</merID>");
		tranDataText.append("<merAcct>").append(p.get("merAcct")).append("</merAcct>");
		tranDataText.append("</orderInfo>");
		tranDataText.append("<custom>");
		tranDataText.append("<verifyJoinFlag>0</verifyJoinFlag>");
		tranDataText.append("<Language>ZH_CN</Language>");
		tranDataText.append("</custom>");
		tranDataText.append("<message>");	
		tranDataText.append("<goodsID></goodsID>");
		tranDataText.append("<goodsName>ryt</goodsName>");
		tranDataText.append("<goodsNum></goodsNum>");
		tranDataText.append("<carriageAmt></carriageAmt>");
		tranDataText.append("<merHint></merHint>");
		tranDataText.append("<remark1></remark1><remark2></remark2>");
		tranDataText.append("<merURL>").append(p.get("merURL")).append("</merURL>");
		tranDataText.append("<merVAR>").append("</merVAR>");
		tranDataText.append("<notifyType>HS</notifyType>");
		tranDataText.append("<resultType>0</resultType>");
		tranDataText.append("<backup1></backup1>");
		tranDataText.append("<backup2></backup2>");
		tranDataText.append("<backup3></backup3>");
		tranDataText.append("<backup4></backup4>");	
		tranDataText.append("</message>");
		tranDataText.append("</B2CReq>");	
		LogUtil.printInfoLog("icbcwap", "sign", tranDataText.toString());
		byte[] byteSrc = tranDataText.toString().getBytes();
		char[] keyPass = p.get("key").toCharArray();

		FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBCB2C_USER_CRT"));
		byte[] bcert = new byte[in1.available()];
		in1.read(bcert);
		in1.close();

		FileInputStream in2 = new FileInputStream(CertUtil.getCertPath("ICBCB2C_USER_KEY"));
		byte[] bkey = new byte[in2.available()];
		in2.read(bkey);
		in2.close();

		byte[] sign = ReturnValue.sign(byteSrc, byteSrc.length, bkey, keyPass);
		byte[] EncSign = ReturnValue.base64enc(sign);
		byte[] EncCert = ReturnValue.base64enc(bcert);
		byte[] EncSrc = ReturnValue.base64enc(byteSrc);

		StringBuffer ret = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		ret.append("<icbcb2c>");
		ret.append("<src>").append(new String(EncSrc).replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "")).append("</src>");
		ret.append("<sign>").append(new String(EncSign).replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "")).append("</sign>");
		ret.append("<cert>").append(new String(EncCert).replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "")).append("</cert>");
		ret.append("</icbcb2c>");
		return ret.toString();

	}
	
	public static String Wapicbcveri(Map<String, String> p) throws Exception {
		String notifyData = p.get("notifyData");
		String signMsg = p.get("signMsg");
		try {
			byte[] byteSrc =ReturnValue.base64dec(notifyData.getBytes());
			FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBCWAP_PUB_KEY"));
			byte[] bcert = new byte[in1.available()];
			in1.read(bcert);
			in1.close();
			byte[] DecSign = ReturnValue.base64dec(signMsg.getBytes());
			int a = ReturnValue.verifySign(byteSrc, byteSrc.length,bcert,DecSign);
			return a == 0 ? "S" : "F";

		} catch (Exception e) {
			e.printStackTrace();
			return "F";
		}

	}

	/**
	 * B2B签名
	 * @param p  srcStr key
	 * @return  
	 * 成功：
	 * <?xml version=\"1.0\" encoding=\"utf-8\"?><icbcb2b><sign></sign><cert></cert></icbcb2b>
	 * 失败： ""
	 */
	public static String sign(Map<String, String> p) {
		
		FileInputStream in1 = null;
		FileInputStream in2 = null;
		try {
			
			String srcBase64 = p.get("srcStr");
			
			System.out.println("srcBase64:"+ srcBase64);
//			srcBase64 = ReturnValue.base64dec(srcBase64.getBytes());
			
			byte[] byteSrc = ReturnValue.base64dec(srcBase64.getBytes());//p.get("srcStr").getBytes();
			
			System.out.println("srcBase64:========src");
			System.out.println(new String(byteSrc));
			
			char[] keyPass = p.get("pwd").toCharArray();

			in1 = new FileInputStream(CertUtil.getCertPath("ICBCB2B_USER_CRT"));
			byte[] bcert = new byte[in1.available()];
			in1.read(bcert);
			in1.close();

			in2 = new FileInputStream(CertUtil.getCertPath("ICBCB2B_USER_KEY"));
			byte[] bkey = new byte[in2.available()];
			in2.read(bkey);
			in2.close();

			byte[] sign = ReturnValue.sign(byteSrc, byteSrc.length, bkey, keyPass);
			byte[] EncSign = ReturnValue.base64enc(sign);
			byte[] EncCert = ReturnValue.base64enc(bcert);

			StringBuffer ret = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			ret.append("<icbcb2b>");
			ret.append("<sign>").append(new String(EncSign)).append("</sign>");
			ret.append("<cert>").append(new String(EncCert)).append("</cert>");
			ret.append("</icbcb2b>");
			return ret.toString();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if (in1 != null) try {in1.close();} catch (IOException e) {}
			if (in2 != null) try {in2.close();} catch (IOException e) {}
			return "";
		}

	}

	public static String icbcb2cveri(Map<String, String> p) {
        
		String notifyData = p.get("notifyData");
		String signMsg = p.get("signMsg");
		try {
			// byte[] xml = ReturnValue.base64dec(notifyData.getBytes());
			// System.out.println(new String(xml,"GBK"));
			byte[] byteSrc = ReturnValue.base64dec(notifyData.getBytes());

			FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBCB2C_PUB_KEY"));
			byte[] bcert = new byte[in1.available()];
			in1.read(bcert);
			in1.close();

			byte[] DecSign = ReturnValue.base64dec(signMsg.getBytes());
			int a = ReturnValue.verifySign(byteSrc, byteSrc.length, bcert,DecSign);

			System.out.println("(0,success)icbcb2cveri:" + a);
			return a == 0 ? "S" : "F";

		} catch (Exception e) {
			System.err.println("icbcb2cveri:error" + e.getMessage());
			e.printStackTrace();
			return "F";
		}

	}
	
	public static String B2bVerify(Map<String, String> p) throws Exception {
		String notifyData = p.get("notifyData");
		String signMsg = p.get("signMsg");
		try {
			byte[] byteSrc =ReturnValue.base64dec(notifyData.getBytes());
			FileInputStream in1 = new FileInputStream(CertUtil.getCertPath("ICBCB2B_PUBLIC_CRT"));
			byte[] bcert = new byte[in1.available()];
			in1.read(bcert);
			in1.close();
			byte[] DecSign = ReturnValue.base64dec(signMsg.getBytes());
			int a = ReturnValue.verifySign(byteSrc, byteSrc.length,bcert,DecSign);
			return a == 0 ? "S" : "F";

		} catch (Exception e) {
			System.err.println("icbcb2cveri:error" + e.getMessage());
			e.printStackTrace();
			return "F";
		}

	}
	
	
	

}
