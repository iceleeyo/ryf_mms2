package com.rongyifu.mms.bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;
import cert.CertUtil;

import com.bocom.api.b2b.BOCOMB2BMiddlemanClient;
import com.bocom.api.core.BOCOMSignServer;
import com.bocom.netpay.b2cAPI.BOCOMB2CClient;
import com.bocom.netpay.b2cAPI.BOCOMB2COPReply;
import com.bocom.netpay.b2cAPI.BOCOMSetting;
import com.bocom.netpay.b2cAPI.NetSignServer;
import com.bocom.netpay.b2cAPI.OpResultSet;
import com.bocom.netpay.b2cAPI.PreOrder;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.LogUtil;



// import com.bocom.netpay.infosec.netsigninterface.ServerKeyStore;
// import com.bocom.netpay.infosec.netsigninterface.ServerKeyStoreFactory;

public class BOCOM {

	private static final String JT_B2B_PATH = CertUtil.getCertPath("JT_B2B_PATH");
//	private static final String REC_ACC_NO = Ryt.getParameter("REC_ACC_NO");// 收款账号
	
	
	public static String query(Map<String,String> m) throws Exception{
		String orders = m.get("orders");
		String code,err;
		BOCOMB2CClient client = new BOCOMB2CClient();
		int ret = client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));
		if (ret != 0){  //初始化失败
			throw new Exception("初始化失败,错误信息："+client.getLastErr());
		}else {
			BOCOMB2COPReply rep = client.queryOrder(orders); //批量订单查询
			if (rep == null) {
				err = client.getLastErr();
				throw new Exception("交易错误信息：" + err + "<br>");
			} else {
				code = rep.getRetCode(); //得到交易返回码
				err = rep.getLastErr();
//				msg = rep.getErrorMessage();
//				out.print("交易返回码：" + code + "<br>");
//				out.print("交易错误信息：" + msg + "<br>");
				StringBuffer result = new StringBuffer();
				if("0".equals(code)){//表示交易成功
//					String num;
					int index;
					OpResultSet oprSet = rep.getOpResultSet(); 
					int iNum  = oprSet.getOpresultNum();//总交易记录数
					for (index = 0; index <= iNum - 1; index++) {
						String order 			= oprSet.getResultValueByName(index,"order");			//订单号
//						String orderDate 		= oprSet.getResultValueByName(index, "orderDate");		//订单日期
//						String orderTime 		= oprSet.getResultValueByName(index, "orderTime");		//订单时间
//						String curType 			= oprSet.getResultValueByName(index,"curType");			//币种
						String amount 			= oprSet.getResultValueByName(index,"amount"); 			//金额
//						String tranDate 		= oprSet.getResultValueByName(index,"tranDate"); 		//交易日期
//						String tranTime 		= oprSet.getResultValueByName(index,"tranTime"); 		//交易时间
						String tranState 		= oprSet.getResultValueByName(index,"tranState"); 		//支付交易状态
//						String orderState 		= oprSet.getResultValueByName(index, "orderState"); 	//订单状态
//						String fee 				= oprSet.getResultValueByName(index, "fee"); 			//手续费
						String bankSerialNo 	= oprSet.getResultValueByName(index, "bankSerialNo"); 	//银行流水号
//						String bankBatNo 		= oprSet.getResultValueByName(index, "bankBatNo"); 		//银行批次号
//						String cardType 		= oprSet.getResultValueByName(index, "cardType"); 		//交易卡类型0:借记卡 1：准贷记卡 2:贷记卡
//						String merchantBatNo	= oprSet.getResultValueByName(index, "merchantBatNo"); 	//商户批次号
//						String merchantComment 	= oprSet.getResultValueByName(index, "merchantComment");//商户备注
//						String bankComment 		= oprSet.getResultValueByName(index, "bankComment"); 	//银行备注
						
						if(tranState.trim().equals("1")){
							result.append(order).append(",");
							result.append(amount.replaceAll(",","")).append(",");
//							out.print("支付交易状态[1:成功]：" + tranState);
							result.append("S").append(",");
							result.append(bankSerialNo).append("|");
						}
					}
				}
				
				return result.toString();
			}
		}
		
		
//		return "";
	}

	public static String verifyB2C(Map<String, String> m) throws Exception {
		
		BOCOMB2CClient client = new BOCOMB2CClient();
		client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));
		
		
		String notifyMsg = m.get("NotifyMsg"); // 获取银行通知结果
		int lastIndex = notifyMsg.lastIndexOf("|");
		String signMsg = notifyMsg.substring(lastIndex + 1, notifyMsg.length()); // 获取签名信息
		String srcMsg = notifyMsg.substring(0, lastIndex + 1);
		int veriyCode = -1;
		com.bocom.netpay.b2cAPI.NetSignServer nss = new com.bocom.netpay.b2cAPI.NetSignServer();
		nss.NSDetachedVerify(signMsg.getBytes("GBK"), srcMsg.getBytes("GBK"));
		veriyCode = nss.getLastErrnum();
		if (veriyCode < 0) {
			LogUtil.printErrorLog("BOCOM", "verifyB2C", "notifyMsg=" + notifyMsg);
			throw new Exception("Error");
		}
		
		return "S";
	}

	public static String genB2BWebSignString(Map<String, String> m) throws Exception {

		BOCOMB2BMiddlemanClient B2BClient = new BOCOMB2BMiddlemanClient();
		boolean ret = B2BClient.initalize(JT_B2B_PATH);
		String corporNo=B2BClient.getElement("CorporNo");//企业客户号
		// 交行B2BWEB支付
		StringBuffer sign = new StringBuffer("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		/**中间商户-商户保证金模式**/
		/*sign.append("<BOCOMB2B>");
		sign.append("<merchant_no>").append(m.get("merchantNo")).append("</merchant_no>");
		sign.append("<merchant_order_no>").append(m.get("tseq")).append("</merchant_order_no>");
		sign.append("<order_mode>21</order_mode>");
		sign.append("<pay_mode>02</pay_mode>");
		sign.append("<validate_period>").append(m.get("vPeriod")).append("</validate_period>");
		sign.append("<order_name>B2BWEBOrder</order_name>");
		sign.append("<order_currency>CNY</order_currency>");
		sign.append("<order_amount>").append(m.get("transAmt")).append("</order_amount>");
		sign.append("<order_comment>order_comment</order_comment>");
		sign.append("<bargainor_ebank_no>").append(corporNo).append("</bargainor_ebank_no>");
		sign.append("<bargainor_name></bargainor_name>");
		sign.append("<purchaser_fee>").append("0.01").append("</purchaser_fee>");
		sign.append("<mld_comment></mld_comment>");
		sign.append("<bargainor_comment></bargainor_comment>");
		sign.append("<purchaser_comment></purchaser_comment>");
		sign.append("<purchaser_ebank_no>").append("8000121719").append("</purchaser_ebank_no>");
		sign.append("<pay_acc_no>").append(m.get("payAcc")).append("</pay_acc_no>");
		sign.append("<rec_acc_no>").append(m.get("recAccNo")).append("</rec_acc_no>");
		sign.append("<rec_bank_type>01</rec_bank_type>");
		sign.append("<erp_serial_no>erp</erp_serial_no>");
		sign.append("</BOCOMB2B>");*/
		/***直接商户-一次性协议支付接口***/
		sign.append("<BOCOMB2B>");
		sign.append("<merchant_no>").append(m.get("merchantNo")).append("</merchant_no>");
		sign.append("<merchant_order_no>").append(m.get("tseq")).append("</merchant_order_no>");
		sign.append("<order_mode>10</order_mode>");
		sign.append("<pay_mode>02</pay_mode>");
		sign.append("<validate_period>").append(m.get("vPeriod")).append("</validate_period>");
		sign.append("<order_name>ryf</order_name>");
		sign.append("<order_currency>CNY</order_currency>");
		sign.append("<order_amount>").append(m.get("transAmt")).append("</order_amount>");
		sign.append("<order_comment></order_comment>");
		sign.append("<bargainor_comment></bargainor_comment>");
		sign.append("<purchaser_comment></purchaser_comment>");
		sign.append("<pay_acc_no>").append(m.get("payAcc")).append("</pay_acc_no>");
		sign.append("<rec_acc_no>").append(m.get("recAccNo")).append("</rec_acc_no>");
		sign.append("<rec_bank_type>01</rec_bank_type>");
		sign.append("<erp_serial_no>erp</erp_serial_no>");
		sign.append("</BOCOMB2B>");
		
		/***中间商户-一次性协议支付接口***/
		/*sign.append("<BOCOMB2B>");
		sign.append("<merchant_no>").append(m.get("merchantNo")).append("</merchant_no>");
		sign.append("<merchant_order_no>").append(m.get("tseq")).append("</merchant_order_no>");
		sign.append("<order_mode>31</order_mode>");
		sign.append("<pay_mode>02</pay_mode>");
		sign.append("<validate_period>").append(m.get("vPeriod")).append("</validate_period>");
		sign.append("<order_name>B2BWEBOrder</order_name>");
		sign.append("<order_currency>CNY</order_currency>");
		sign.append("<order_amount>").append(m.get("transAmt")).append("</order_amount>");
		sign.append("<order_comment></order_comment>");
		sign.append("<bargainor_ebank_no>").append(corporNo).append("</bargainor_ebank_no>");
		sign.append("<bargainor_name></bargainor_name>");
		sign.append("<mld_comment></mld_comment>");
		sign.append("<bargainor_comment></bargainor_comment>");
		sign.append("<purchaser_comment></purchaser_comment>");
		sign.append("<purchaser_ebank_no>").append("8000171219").append("</purchaser_ebank_no>");
		sign.append("<pay_acc_no>").append(m.get("payAcc")).append("</pay_acc_no>");
		sign.append("<rec_acc_no>").append(m.get("recAccNo")).append("</rec_acc_no>");
		sign.append("<rec_bank_type>01</rec_bank_type>");
		sign.append("<erp_serial_no>erp</erp_serial_no>");
		sign.append("</BOCOMB2B>");*/
	
		String reqData = com.rongyifu.mms.utils.Base64.encode(sign.toString().getBytes());
		reqData = reqData.replaceAll("\r", "");
		reqData = reqData.replaceAll("\n", "");
		LogUtil.printInfoLog("BOCOm", "genB2BWebSignString", "req:"+reqData);
		if (!ret) {
			LogUtil.printErrorLog("BOCOM", "genB2BWebSignString", "jt b2b err:" + B2BClient.getLastErr());
			throw new Exception("初始化函数出错");
		}
		BOCOMSignServer nss = B2BClient.getSignServer();

		try {
			nss.setPlainText(reqData.getBytes("GBK"));
			byte[] signMes = nss.attachedSign();
			String B2BSignData = (new String(signMes, "GBK"));

			return B2BSignData;
		} catch (Exception e) {
			LogUtil.printErrorLog("BOCOM", "genB2BWebSignString", "jt b2b occur exception:" + e.getMessage());
			throw new Exception("签名出现错误");
		}

	}
	public static String genB2BVerifyString(Map<String, String> m) throws Exception {
		String NetpayNotifyMsg = m.get("NetpayNotifyMsg");
		BOCOMB2BMiddlemanClient B2BClient = new BOCOMB2BMiddlemanClient();
		boolean ret = B2BClient.initalize(JT_B2B_PATH);
		if (!ret) {
			String errmsg = B2BClient.getLastErr();
			LogUtil.printErrorLog("BOCOM", "genB2BVerifyString", "bocom b2b err:" + errmsg);
			//throw new Exception("初始化函数出错:"+errmsg);
			return "";
		}
		try{
			BOCOMSignServer nss = B2BClient.getSignServer();
			String	srcData = nss.attachedVerify(NetpayNotifyMsg);
			
			BASE64Decoder decoder = new BASE64Decoder();
			String notifyMsg = new String(decoder.decodeBuffer(srcData));
			
			LogUtil.printInfoLog("BOCOM", "genB2BVerifyString", "jt notifyMsg:" + notifyMsg);
			
//			<?xml version="1.0" encoding="gb2312" ?><BOCOM><notify_mode>B2B</notify_mode><notify_type>100</notify_type><orderInfo><order_no>BO1206180001550</order_no><establish_date>20120618</establish_date><establish_time>141554</establish_time><merchant_no>301310041319500</merchant_no><merchant_order_no>788</merchant_order_no><order_currency>CNY</order_currency><order_amount>4.17</order_amount><payed_amount>4.17</payed_amount><order_status>100</order_status></orderInfo><payment><serial_no>50095082</serial_no><tran_date>20120425</tran_date><tran_type>100</tran_type><payTranInfo><pay_acc_no>310066661010123079617</pay_acc_no><pay_amount>4.17</pay_amount></payTranInfo><recTranInfo><rec_acc_no>310066496018003846558</rec_acc_no><rec_amount>4.17</rec_amount></recTranInfo><fee_amount>1.20</fee_amount><tran_status>21</tran_status><fail_reason>SC0000</fail_reason></payment></BOCOM>
			Document doc = DocumentHelper.parseText(notifyMsg);
			Element root = doc.getRootElement();
//			notify_type
//			000：订单生成
//			001：中间商保证金交割
//			002：中间商保证金退款
//			010：卖家退款
//			100：买家付款
//			200：协议付款
			String nt = root.elementText("notify_type").trim();
			if(!nt.equals("100")){
				return "";
			}
			
			Element orderInfo = root.element("orderInfo");
			
			String bkNo = orderInfo.elementText("order_no");
			String tseq = orderInfo.elementText("merchant_order_no");
			String payed_amount = orderInfo.elementText("payed_amount");
			String order_status = orderInfo.elementText("order_status");
			
			
//			<?xml version="1.0" encoding="gb2312" ?>
//			<BOCOM><notify_mode>B2B</notify_mode>
//			<notify_type>100</notify_type>
//			<orderInfo>
//			<order_no>BO1206180001550</order_no>
//			<establish_date>20120618</establish_date>
//			<establish_time>141554</establish_time>
//			<merchant_no>301310041319500</merchant_no>
//			<merchant_order_no>788</merchant_order_no>
//			<order_currency>CNY</order_currency>
//			<order_amount>4.17</order_amount>
//			<payed_amount>4.17</payed_amount>
//			<order_status>100</order_status>
//			</orderInfo>
			
			Element payment = root.element("payment");
			String serial_no = payment.elementText("serial_no");
			String fee_amount = payment.elementText("fee_amount");
            fee_amount=Ryt.empty(fee_amount) ? "0" : fee_amount;//fee_amount 为"" 时指定默认为0
			
//			tran_status
//			0未审核
//			1未授权
//			3审核被拒绝
//			4审核完成，等待继续授权
//			5授权被拒绝
//			6授权成功，等待继续授权
//			8授权完成，等待发送主机
//			20主机处理中
//			21交易成功
//			22交易失败
			String tran_status = payment.elementText("tran_status").trim();
			
			if(!tran_status.equals("21")){
				return "";
			}
			
			String fail_reason = payment.elementText("fail_reason");
			String tran_date = payment.elementText("tran_date");
//			<payment>
//			<serial_no>50095082</serial_no>
//			<tran_date>20120425</tran_date>
//			<tran_type>100</tran_type>
//			<payTranInfo>
//			<pay_acc_no>310066661010123079617</pay_acc_no>
//			<pay_amount>4.17</pay_amount>
//			</payTranInfo>
//			<recTranInfo>
//			<rec_acc_no>310066496018003846558</rec_acc_no>
//			<rec_amount>4.17</rec_amount>
//			</recTranInfo>
//			<fee_amount>1.20</fee_amount>
//			<tran_status>21</tran_status>
//			<fail_reason>SC0000</fail_reason>
//			</payment>
//			</BOCOM>
			StringBuffer res = new StringBuffer();
			res.append(tseq).append(",").append(bkNo).append(",");
			res.append(serial_no).append(",");
			res.append(payed_amount).append(",");
			res.append(fee_amount).append(",");
			res.append(tran_date);
			
			return res.toString();
			//转码
			
		}catch(Exception err){
			err.printStackTrace();
//			throw new Exception("签名出现错误，"+err.getMessage());
			
			return "";
		}
	}
	/**
	 * B2C sign
	 * @throws Exception
	 * @throws Exception
	 */
	public static String genSignString(String signText) throws Exception {
		// String merchantDN = "C=CN,O=BANKCOMM CA,OU=BANKCOMM,OU=Merchants,CN=040@00746718748@[301110073999596]@000";
		// FileInputStream fpfx = null;
		// fpfx = new FileInputStream("F://jiaohang.pfx");
		// String pwdpfx = "82825933";
		// ServerKeyStore pfxstore = ServerKeyStoreFactory.generatePKCS12ServerKeyStore(fpfx, pwdpfx.toCharArray());
		// fpfx.close();
		//
		// byte bSignMsg[] = NSDetachedSign(signText.getBytes("GBK"), pfxstore);
		//
		// if (bSignMsg == null) {
		// throw new Exception("Sign Error");
		// }
		// String signMsg = new String(bSignMsg, "GBK");
		// Ryt.print("---jt sign-ok---");
		// return signMsg;

		BOCOMB2CClient client = new BOCOMB2CClient();//解析xml
		int ret = client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));
		if (ret != 0) { // init error
			throw new Exception(client.getLastErr());
		}

		NetSignServer nss = new NetSignServer();//setApi()
		String merchantDN = BOCOMSetting.MerchantCertDN;
		nss.NSSetPlainText(signText.getBytes("GBK"));

		byte bSignMsg[] = nss.NSDetachedSign(merchantDN);
		if (nss.getLastErrnum() < 0) {
			throw new Exception("Sign Error");
		}
		String signMsg = new String(bSignMsg, "GBK");
//		Ryt.print("---jt sign-ok---");
		return signMsg;

	}

	// private static byte[] NSDetachedSign(byte[] signByte, ServerKeyStore pfx) {
	// com.bocom.netpay.infosec.netsigninterface.NetSignImpl impl = new
	// com.bocom.netpay.infosec.netsigninterface.NetSignImpl();
	// try {
	// return Base64.encode(impl.GenerateSingleSignedMsg(signByte, pfx, true)).getBytes();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// return null;
	// }
	// }

	/**
	 * 交行预订单生成
	 * @param m
	 * @return 成功 "0" ，失败 失败码
	 * @throws Exception
	 */
	public static String createBCMPreOrder(Map<String, String> m) throws Exception {

		String phoneNo = m.get("phoneNo");
		String orderid = m.get("tseq");
		String amount = m.get("transAmt");
		String orderDate = m.get("sysDate");
		String merURL = m.get("retURL");
		String orderMono = m.get("orderMono");

		BOCOMB2CClient client = new BOCOMB2CClient();
		int ret = client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));

		if (ret != 0) { // 初始化失败
			LogUtil.printErrorLog("BOCOM", "createBCMPreOrder", "ret=" + ret + " lastErr=" + client.getLastErr());
			throw new Exception(client.getLastErr());
		}
		String period = orderDate + "235000";
		String curType = "CNY";
		String netType = "0";
		String phdFlag = "0";// 0 非物流 1 物流配送
		String notifyType = "1";// 0 不通知 1 通知 2 转页面
		String orderContent = "";

		String goodsURL = "";
		String jumpSeconds = "";
		String payBatchNo = "";
		String proxyMerName = "";
		String proxyMerType = "";
		String proxyMerCredentials = "";
		String orderTime = "";

		PreOrder tran = new PreOrder(orderid, orderDate, orderTime, amount, curType, orderContent, orderMono, phoneNo,
			period, phdFlag, notifyType, merURL, goodsURL, jumpSeconds, payBatchNo, proxyMerName, proxyMerType,
			proxyMerCredentials, netType);

		BOCOMB2COPReply rep = client.createPreOrder(tran);

		if (rep == null) {
			LogUtil.printErrorLog("BOCOM", "createBCMPreOrder", "error msg:"+client.getLastErr());
			throw new Exception(client.getLastErr());
		} else {
			Ryt.print("bocomm: jt wap preOrder code(0,Success) : " + rep.getRetCode() + ",msg:" + rep.getErrorMessage());
			return rep.getRetCode(); // 得到交易返回码
		}

	}

	/**
	 * 交行结算单下载
	 * @param params
	 * @return XML
	 */
	public static String doJT001(Map<String, String> params) {
		// 结算日期
		String settleDate = params.get("settleDate");

		BOCOMB2CClient client = new BOCOMB2CClient();
		int ret = client.initialize(CertUtil.getCertPath("JT_B2C_PATH"));// 读取交行B2C的配置文件进行初始化
		if (ret != 0) { // 初始化失败
			return genErrorXml("初始化失败:" + client.getLastErr());
		}
		BOCOMB2COPReply rep = client.downLoadSettlement(settleDate);// 对帐单下载
		String resultXML = genErrorXml("下载失败");
		if (rep == null) {
			return resultXML;
		}
		if (!rep.getRetCode().equals("0")) {
			return genErrorXml(rep.getErrorMessage());
		}
		String fileName = BOCOMSetting.SettlementFilePath; // 读取B2CMerchant.xml中配置的结算明细文件路径
		fileName = fileName + "/BOCOM_B2C_Settlement_" + settleDate;
		try {
			resultXML = getDownFileData(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultXML;
	}

	private static String genErrorXml(String errorMsg) {
		return Ryt.buildErrrorXml("1", errorMsg);
	}

	/**
	 * 根据下载的交行结算明细文件组装XML返回MMS用于对账
	 * @param path 交行结算明细文件路径
	 * @return 组装好的XML
	 * @throws Exception 对账需要的数据结构 {融易通流水号,银行流水号,银行记录中的商户日期,交易金额,交易日期,交易时间,银行手续费}
	 */
	private static String getDownFileData(String path) throws Exception {
		File settleFile = new File(path);
		int lineCount = 0;
		BufferedReader br = null;
		Document doc = DocumentHelper.createDocument();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(settleFile)));
			String line = null;
			Element rootEle = doc.addElement("res");

			Element statusEle = rootEle.addElement("status");
			statusEle.addElement("value").setText("0");
			statusEle.addElement("msg").setText("");

			Element b2cEle = rootEle.addElement("BCMB2C");
			while ((line = br.readLine()) != null) {
				lineCount++;
				if (lineCount > 10) {
					if (line.contains("==============")) break;
					Element orderEle = b2cEle.addElement("order");
					line = line.replaceAll("\\s{2,}", " ");
					String[] value = line.split(" ");
					Ryt.print("jh settle:" + line);
					orderEle.addElement("rytOid").setText(value[1]); // 融易通流水号
					orderEle.addElement("bankOid").setText(value[1]); // 银行流水号
					orderEle.addElement("bankDate").setText(value[2]); // 银行记录中的商户日期
					orderEle.addElement("amount").setText(value[6]); // 交易金额
					orderEle.addElement("date").setText(value[3].split("-")[0]); // 交易日期
					orderEle.addElement("time").setText(value[3].split("-")[1]); // 交易时间
					orderEle.addElement("bankFee").setText(value[7]); // 银行手续费
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) br.close();
		}
		return doc.asXML();
	}
	
	
//	private static void markTlog(String notifyMsg){
//		//解析xml
//		String tseq="778";
//		PubDao pubDao=new PubDao();
//		String sql="update tlog set error_code='b2b_mark' where tseq="+tseq;
//		pubDao.update(sql);
//	}

}