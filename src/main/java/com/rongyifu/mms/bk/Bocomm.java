package com.rongyifu.mms.bk;

import java.io.*;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cert.CertUtil;

import com.bocom.netpay.b2cAPI.BOCOMB2CClient;
import com.bocom.netpay.b2cAPI.BOCOMB2COPReply;
import com.bocom.netpay.b2cAPI.BOCOMSetting;
import com.rongyifu.mms.common.Ryt;

public class Bocomm {
	
	
	public String doJT001(Map<String, String> params) {
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
	
	private String genErrorXml(String errorMsg) {
		return Ryt.buildErrrorXml("1", errorMsg);
	}
	
	/**
	 * 根据下载的交行结算明细文件组装XML返回MMS用于对账
	 * @param path 交行结算明细文件路径
	 * @return 组装好的XML
	 * @throws Exception 对账需要的数据结构 {融易通流水号,银行流水号,银行记录中的商户日期,交易金额,交易日期,交易时间,银行手续费}
	 */
	private String getDownFileData(String path) throws Exception {
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
					System.err.println(line);
					System.err.println(value.length + ">>");
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
}
