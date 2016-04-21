package com.rongyifu.mms.ewp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import com.hitrust.b2b.trustpay.client.TrxResponse;
import com.hitrust.b2b.trustpay.client.XMLDocument;
import com.hitrust.b2b.trustpay.client.b2b.DownloadTrnxRequest;
import com.hitrust.b2b.trustpay.client.b2b.FundTransferRequest;
import com.hitrust.b2b.trustpay.client.b2b.QueryTrnxRequest;
import com.hitrust.b2b.trustpay.client.b2b.TrnxInfo;
import com.hitrust.b2b.trustpay.client.b2b.TrnxItem;
import com.hitrust.b2b.trustpay.client.b2b.TrnxItems;
import com.hitrust.b2b.trustpay.client.b2b.TrnxRemarks;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.utils.LogUtil;

public class Abc_b2b_pay {

	public static String abc_b2b(Map<String, String> p) throws IOException {

		String tMerchantTrnxNo = p.get("tseq");
		String ordId = p.get("ordId");//商户订单号
		String MerId = p.get("merId");
		String tTrnxAmountStr = p.get("transAmt");
		String tTrnxDate = p.get("orderDate");// "2003/11/12";
		String tTrnxTime = p.get("orderTime");// "23:55:30";
		String tAccountDBNo = p.get("tAccountDBNo"); // "9663396685269";

		String tAccountDBName1 = p.get("tAccountDBName");// "北京宏基";
		String tAccountDBName = new String(
				tAccountDBName1.getBytes("ISO-8859-1"), "utf-8");
		String tAccountDBBank = p.get("tAccountDBBank");// "000000";
		String tResultNotifyURL = p.get("resultNotifyURL"); // "http://172.16.22.64:8080/B2BClient/TrnxResult.jsp";
		String tMerchantRemarks = p.get("tMerchantRemarks");
		double tTrnxAmount = 0;
		boolean isTrnxAmountOK = true;
		String Results = "";
		String returnmessage = null;
		try {
			tTrnxAmount = Double.parseDouble(tTrnxAmountStr);
		} catch (NumberFormatException e) {
			isTrnxAmountOK = false;
		}

		if (isTrnxAmountOK) {// 2、生成TrnxInfo对象

			/*
			 * TrnxItems tTrnxItems = new TrnxItems();
			 * tTrnxItems.addTrnxItem(new TrnxItem("0001", "显示器", 1000.00f, 2));
			 * tTrnxItems.addTrnxItem(new TrnxItem("0002", "硬盘", 600.00f, 5));
			 * tTrnxItems.addTrnxItem(new TrnxItem("IP000001", "中国移动IP卡",
			 * 100.00f,1)); TrnxRemarks tTrnxRemarks = new TrnxRemarks();
			 * tTrnxRemarks.addTrnxRemark(new TrnxRemark("合同号", "555000000"));
			 * tTrnxRemarks.addTrnxRemark(new TrnxRemark("采购时间",
			 * "2003/11/12 14:23:34")); tTrnxRemarks.addTrnxRemark(new
			 * TrnxRemark("交易类型", "买入")); tTrnxRemarks.addTrnxRemark(new
			 * TrnxRemark("其它说明", "能够使买方确信该交易是自己交易的信息")); TrnxInfo tTrnxInfo =
			 * new TrnxInfo(); tTrnxInfo.setTrnxOpr("TrnxOperator0001");
			 * tTrnxInfo.setTrnxRemarks(tTrnxRemarks);
			 * tTrnxInfo.setTrnxItems(tTrnxItems);
			 */

			TrnxInfo tTrnxInfo = new TrnxInfo();
			TrnxRemarks tTrnxRemarks = new TrnxRemarks();
			TrnxItems tTrnxItems = new TrnxItems();

			int trade_type = gettrade_type(MerId);
			String tradeName = gettrade_typeName(trade_type);
			
			
			tTrnxItems.addTrnxItem(new TrnxItem("0001", ordId, Float.parseFloat(tTrnxAmountStr), 1));
			//tTrnxItems.addTrnxItem(new TrnxItem("0001", tradeName, 1.0f, 1));
			
			// tTrnxInfo.setTrnxOpr("TrnxOperator0001");
			tTrnxInfo.setTrnxRemarks(tTrnxRemarks);
			tTrnxInfo.setTrnxItems(tTrnxItems);

			// 3、生成直接支付请求对象
			FundTransferRequest tFundTransferRequest = new FundTransferRequest();

			tFundTransferRequest.setTrnxInfo(tTrnxInfo); // 设定交易细项 （必要信息）
			tFundTransferRequest.setMerchantTrnxNo(tMerchantTrnxNo); // 设定商户交易编号
			tFundTransferRequest.setTrnxAmount(tTrnxAmount); // 设定交易金额 （必要信息）
			tFundTransferRequest.setTrnxDate(tTrnxDate); // 设定交易日期 （必要信息）
			tFundTransferRequest.setTrnxTime(tTrnxTime); // 设定交易时间 （必要信息）
			tFundTransferRequest.setAccountDBNo(tAccountDBNo); // 设定收款方账号 （必要信息）
			tFundTransferRequest.setAccountDBName(tAccountDBName); // 设定收款方账户名//
																	// // （必要信息）
			tFundTransferRequest.setAccountDBBank(tAccountDBBank); // 设定收款方账户开户行联行号（必要信息）
			tFundTransferRequest.setResultNotifyURL(tResultNotifyURL); // 设定交易结果回传网址（必要信息）
			tFundTransferRequest.setMerchantRemarks(tMerchantRemarks); // 设定商户备注信息

			// 4、传送直接支付请求并取得支付网址tTrxResponse.getValue("PaymentURL")
			TrxResponse tTrxResponse = tFundTransferRequest.postRequest();

			if (tTrxResponse.isSuccess()) {
				System.out.println("tTrxResponse.isSuccess()=11====istrue");
				Results = tTrxResponse.getValue("PaymentURL");
				// https://easyabc.95599.cn/b2b/NotCheckStatus/PaymentModeTestAct.ebf?TOKEN=13668595647406094808

			} else {
				// 6、直接支付请求提交失败，商户自定后续动作
				System.out.println("tTrxResponse.isSuccess()=22====isfase");

				Results = tTrxResponse.getReturnCode() + ":"
						+ tTrxResponse.getErrorMessage();
			}
		}
		System.out.println("Results=="+Results);
		return Results.toString();
	}

	/**
	 * lyj 根据mid查询商户所属行业
	 */
	public static int gettrade_type(String mid) {
		MerInfoDao merInfoDao = new MerInfoDao();
		return merInfoDao.gettrade_typeByMid(mid);
	}

	/**
	 * lyj
	 * 
	 * @param trade
	 *            (int)
	 * @return tradename(string)
	 */
	public static String gettrade_typeName(int trade) {
		String name = "";
		if (trade == 100) {
			name = "航空机票";
		} else if (trade == 101) {
			name = "酒店/旅游";
		} else if (trade == 101) {
			name = "酒店/旅游";
		} else if (trade == 102) {
			name = "服务/缴费";
		} else if (trade == 103) {
			name = "综合商城";
		} else if (trade == 104) {
			name = "金融/保险";
		} else if (trade == 105) {
			name = "虚拟/游戏";
		} else if (trade == 106) {
			name = "医药/保健";
		} else if (trade == 107) {
			name = "教育/招生";
		} else if (trade == 108) {
			name = "交友/咨询";
		} else {
			name = "其他";
		}

		return name;
	}


	
	// 下载
	public static String abc_b2b_load(Map<String, String> p) throws IOException {

		String tMerchantTrnxDate = p.get("orderDate");
		String tMerchantRemarks = p.get("tMerchantRemarks");
		String Results = "";

		// 2、生成下载交易记录请求对象
		DownloadTrnxRequest tDownloadTrnxRequest = new DownloadTrnxRequest();
		tDownloadTrnxRequest.setMerchantTrnxDate(tMerchantTrnxDate); // 设定商户交易编号
																		// （必要信息）
		tDownloadTrnxRequest.setMerchantRemarks(tMerchantRemarks); // 设定商户备注信息

		// 3、传送下载交易记录请求并取得支付网址
		TrxResponse tTrxResponse = tDownloadTrnxRequest.postRequest();
		if (tTrxResponse.isSuccess()) {
			System.out.print("tTrxResponse.isSuccess==true");
			// 4、下载交易记录请求提交成功
			XMLDocument tDetailRecords = new XMLDocument(
					tTrxResponse.getValue("TrnxDetail"));
			ArrayList tRecords = tDetailRecords.getDocuments("TrnxRecord");
			String[] iRecord = new String[tRecords.size()];
			if (tRecords.size() > 0) {
				for (int i = 0; i < tRecords.size(); i++) {
					iRecord[i] = ((XMLDocument) tRecords.get(i)).toString();
					String j = "Record-" + i + " = [" + iRecord[i] + "]<br>";

					Results = iRecord[i] + Results;
				}
			} else {

				Results = "指定的日期里没有交易记录";
			}
		} else {
			System.out.print("tTrxResponse.isSuccess==false");
			// 5、下载交易记录请求提交失败，商户自定后续动作
			Results = tTrxResponse.getReturnCode() + ":"
					+ tTrxResponse.getResponseMessage();
		}

		return "<h2>" + Results.toString() + "</h2>";
	}

	public static String abc_b2b_query(Map<String, String> p)
			throws IOException {

		String tMerchantTrnxNo = p.get("merId");
		String tMerchantRemarks = p.get("tMerchantRemarks");
		String Results = "";

		// 2、生成交易查询请求对象
		QueryTrnxRequest tQueryTrnxRequest = new QueryTrnxRequest();
		tQueryTrnxRequest.setMerchantTrnxNo(tMerchantTrnxNo); // 设定商户交易编号 （必要信息）
		tQueryTrnxRequest.setMerchantRemarks(tMerchantRemarks); // 设定商户备注信息

		// 3、传送交易查询请求并取得支付网址
		TrxResponse tTrxResponse = tQueryTrnxRequest.postRequest();
		if (tTrxResponse.isSuccess()) {
			// 4、交易查询请求提交成功

			Results = "TrnxType=" + tTrxResponse.getValue("TrnxType")
					+ ";TrnxAMT=" + tTrxResponse.getValue("TrnxAMT")
					+ ";MerchantID=" + tTrxResponse.getValue("MerchantID")
					+ ";MerchantTrnxNo="
					+ tTrxResponse.getValue("MerchantTrnxNo") + ";ReturnCode="
					+ tTrxResponse.getValue("ReturnCode");
		} else {
			// 5、交易查询请求提交失败，商户自定后续动作

			Results = tTrxResponse.getReturnCode() + ":"
					+ tTrxResponse.getErrorMessage();
		}
		return "<h1>" + Results.toString() + "</h1>";
		// return Results.toString() ;
	}

	private String BankabcHttpRequestPost(String reqDate,
			TrxResponse tTrxResponse) throws B2EException,
			IllegalArgumentException, UnsupportedEncodingException {

		String response = null;
		URL url = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;

		try {
			url = new URL(tTrxResponse.getValue("PaymentURL"));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			os = conn.getOutputStream();

			// 打印请求报文
			os.write(reqDate.toString().getBytes("utf-8"));
			String result = "";
			br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}

			response = result;

		} catch (Exception e) {
			LogUtil.printErrorLog("Abc_b2b_pay", "BankabcHttpRequestPost",
					e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			try {
				if (os != null)
					os.close();
				if (br != null)
					br.close();

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		// 返回响应报文
		return response;
	}

}
