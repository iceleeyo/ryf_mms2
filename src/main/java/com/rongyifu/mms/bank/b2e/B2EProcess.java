package com.rongyifu.mms.bank.b2e;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.unionpaywap.DesUtil2;
import com.rongyifu.mms.utils.LogUtil;

public class B2EProcess {

	private TrOrders orders = null;
	private BankXML bankXml = null;
	private B2EGate gate = null;
	private int trCode = 0;

	public B2EProcess(B2EGate gate, int trCode) {
		super();
		this.gate = gate;
		this.trCode = trCode;
		int gid = gate.getGid();

		if (gid >= 100000)
			gid = gid / 10;

		if (gid == B2ETrCode.BOCOMM_GID)
			this.bankXml = new BankcommXML();
		else if (gid == B2ETrCode.BOC_GID)
			this.bankXml = new BocXML();
		else if (gid == B2ETrCode.BOCOMM_GID_BE)
			this.bankXml = new BankcommBEXML();
		else if (gid == B2ETrCode.CMB_GID)
			this.bankXml = new CmbXML();
		else if (gid == B2ETrCode.ABC_GID)
			this.bankXml = new AbcXML();
		else if (gid == B2ETrCode.ICBC_GID)
			this.bankXml = new ICBCXML();
		else if (gid == B2ETrCode.SJ_GID)
			this.bankXml = new SjBankXML();
		else if (gid == B2ETrCode.SJ_DFYZC_GID)
			this.bankXml = new SjBankXML_DFYC();
		else if(gid==B2ETrCode.PSBC_GID)
			this.bankXml=new PSBCXML();
		else
			this.bankXml = null;

	}

	public void setOrders(TrOrders orders) {
		this.orders = orders;
	}

	public B2ERet submit() {
		B2ERet ret = new B2ERet();
		if (this.trCode == 0) {
			ret.setErr("无效的交易类型");
			return ret;
		}
		if (this.gate == null || this.bankXml == null) {
			ret.setErr("无效的银行网关");
			return ret;
		}
		ret.setGid(this.gate.getGid());

		if (isEmpty(this.gate.getNcUrl())) {
			ret.setErr("前置机地址为空");
			return ret;
		}
		
		String orderId = "";
		try {
			String reqData = null;
			
			if (this.orders == null)
				reqData = this.bankXml.genSubmitXML(this.trCode, this.gate);
			else {
				reqData = this.bankXml.genSubmitXML(this.trCode, this.orders,
						this.gate);
				orderId = "[" + orders.getOid() + "]";
			}

			if (isEmpty(reqData)) {
				ret.setErr("组装报文错误");
				return ret;
			}
			LogUtil.printInfoLog("B2EProcess", "submit", orderId + "request:"
					+ reqData);
			String resData = null;
			if (gate.getGid() == B2ETrCode.BOCOMM_GID)
				// resData = BankcommHttpRequest(reqData);
				resData = BankcommHttpRequestPost(reqData);
			else if (gate.getGid() == B2ETrCode.BOCOMM_GID_BE)
				resData = BankcommBEHttpRequest(reqData);
			else if (gate.getGid() == B2ETrCode.CMB_GID)
				resData = CMBHttpRequest(reqData);
			else if (gate.getGid() == B2ETrCode.ABC_GID)
				resData = AbcSocket(reqData);
			else if (gate.getGid() == B2ETrCode.ICBC_GID)
				resData = ICBCHttpRequest(reqData);
			else if (gate.getGid() == B2ETrCode.SJ_GID)
				resData=sjBankSocket(reqData);
			else if (gate.getGid() == B2ETrCode.SJ_DFYZC_GID)
				resData = sjBankSocket(reqData);
			else if(gate.getGid()==B2ETrCode.PSBC_GID)
				resData=psbcScoket(reqData);
			else
				resData = httpRequest(reqData);
			LogUtil.printInfoLog("B2EPorcess", "submit", orderId + "response:"
					+ resData);
			this.bankXml.parseXML(ret, resData.trim());
			return ret;

		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "submit", orderId + e.getMessage(), e);
			ret.setErr(e.getMessage());
			return ret;
		}

	}

	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}

	// 招行请求方式
	private String CMBHttpRequest(String data) throws B2EException {
		String ipurl = this.gate.getNcUrl();
		String result = "";
		try {
			URL url;
			url = new URL(ipurl);

			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os;
			os = conn.getOutputStream();
			os.write(data.toString().getBytes("gbk"));
			os.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
			br.close();
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "httpRequest", e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		}
		return result;

	}

	private String httpRequest(String reqDate) throws B2EException {

		String ncURL = this.gate.getNcUrl();
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(ncURL);

		String encode = this.gate.getCodeType();

		try {

			method.setRequestHeader("Content-Type", "application/xmlstream");
			method.setQueryString(URIUtil.encodeQuery(reqDate, encode));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				// response = method.getResponseBodyAsString();
				InputStream input = method.getResponseBodyAsStream();
				response = readStream(input);
				response = transCharCode(response, encode);// 对响应xml转码
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "httpRequest", e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 交行超级网银请求方式
	 * 
	 * @param reqDate
	 * @param paramsAfterUrl
	 * @return
	 * @throws B2EException
	 */
	private String BankcommHttpRequest(String reqDate) throws B2EException {

		String ncURL = this.gate.getNcUrl();
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(ncURL + "?"
				+ URLEncoder.encode(reqDate));
		// String encode = this.gate.getCodeType();

		try {

			// method.setRequestHeader("Content-Type", "application/xmlstream");
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				// response = method.getResponseBodyAsString();
				InputStream input = method.getResponseBodyAsStream();
				response = readStream(input);
				// response = transCharCode(response, encode);// 对响应xml转码
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "BankcommHttpRequest",
					e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 交行银企直连请求方式
	 * 
	 * @param reqDate
	 * @param paramsAfterUrl
	 * @return
	 * @throws B2EException
	 * @throws UnsupportedEncodingException
	 */
	private String BankcommBEHttpRequest(String reqDate) throws B2EException,
			UnsupportedEncodingException {

		String ncURL = this.gate.getNcUrl();
		String response = null;
		HttpClient client = new HttpClient();
		// HttpMethod method = new GetMethod(ncURL + "?" +
		// URLEncoder.encode(reqDate,gate.getCodeType()));
		// HttpMethod method = new GetMethod(ncURL + "?" +
		// URLEncoder.encode(reqDate));
		PostMethod postMethod = new PostMethod(ncURL + "?"
				+ URLEncoder.encode(reqDate, gate.getCodeType()));
		String encode = this.gate.getCodeType();
		// postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// encode);
		// postMethod.addParameter("ReqData", reqDate);
		HttpMethod method = postMethod;

		try {

			// method.setRequestHeader("Content-Type", "application/xmlstream");
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
				response = transCharCode(response, encode);// 对响应xml转码
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "BankcommBEHttpRequest",
					e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			method.releaseConnection();
		}
		return response;
	}

	// public B2EProcess() {
	// }

	private String transCharCode(String responseXml, String encode) {
		if (this.gate.getGid() == B2ETrCode.BOC_GID)
			return responseXml;
		if (this.gate.getGid() == B2ETrCode.BOCOMM_GID
				|| this.gate.getGid() == B2ETrCode.BOCOMM_GID_BE) {
			try {
				responseXml = new String(responseXml.getBytes("ISO8859-1"),
						encode);
			} catch (UnsupportedEncodingException e) {
				LogUtil.printErrorLog("B2EProcess", "transCharCode",
						e.getMessage());
			}

		} else if (this.gate.getGid() == B2ETrCode.ICBC_GID) {
			try {
				responseXml = new String(responseXml.getBytes(), encode);
			} catch (UnsupportedEncodingException e) {
				LogUtil.printErrorLog("B2EProcess", "transCharCode",
						e.getMessage());
			}
		}
		return responseXml;
	}

	private String readStream(InputStream input) throws IOException {
		byte[] b = new byte[1064];
		StringBuffer str = new StringBuffer();
		while ((input.read(b) != -1)) {
			str.append(new String(b));
		}
		input.close();
		return str.toString();
	}

	/**
	 * 交行超级网银请求方式
	 * 
	 * @param reqDate
	 * @param paramsAfterUrl
	 * @return
	 * @throws B2EException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalArgumentException
	 */
	private String BankcommHttpRequestPost(String reqDate) throws B2EException,
			IllegalArgumentException, UnsupportedEncodingException {
		String ncURL = this.gate.getNcUrl();
		String response = null;
		String encode = this.gate.getCodeType();
		URL url = null;
		HttpURLConnection conn = null;
		OutputStreamWriter wr = null;
		BufferedReader br = null;
		try {
			url = new URL(ncURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			if (!Ryt.empty(reqDate)) {
				wr = new OutputStreamWriter(conn.getOutputStream(), encode);
				;
				wr.write(reqDate);
				wr.flush();
			}

			String result = "";
			br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
			response = result;
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "BankcommBEHttpRequest", "ncURL="+ncURL, e);
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			try {
				if (wr != null)
					wr.close();
				if (br != null)
					br.close();

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return response;
	}

	/***
	 * 农行银企直连 socket请求方式
	 * 
	 * @param reqData
	 * @return
	 * @throws SocketException
	 */
	public String AbcSocket(String reqData) throws Exception {
		String ncUrl = this.gate.getNcUrl();
		int last = ncUrl.lastIndexOf(".");
		String host = ncUrl.substring(0, last);
		Integer port = Integer.parseInt(ncUrl.substring(last + 1));
		Socket socket = new Socket();
		InetSocketAddress endpoint = new InetSocketAddress(host, port);
		socket.setSoTimeout(6000);
		socket.connect(endpoint);
		socket.setSendBufferSize(1024 * 102);
		socket.setReceiveBufferSize(1024 * 102);
		FileOutputStream out = (FileOutputStream) socket.getOutputStream();
		String encode = "GBK";
		byte[] b2 = reqData.getBytes(encode);
		for (byte c : b2) {
			out.write(c);
		}
		out.flush();
		FileInputStream read = (FileInputStream) socket.getInputStream();
		byte[] header = new byte[7];
		while ((read.read(header)) > 0) {
			break;
		}
		Integer bodyLen = Integer.parseInt(new String(header).trim());
		byte[] body = new byte[bodyLen]; // 设置body长度
		Integer size = 0;
		int i = 0;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		while ((i = read.read(body)) > 0) {
			if (size >= bodyLen) {
				break;
			}
			size += i;
			byteOut.write(body, 0, i);
		}
		out.close();
		read.close();
		socket.close();
		return new String(byteOut.toByteArray(), "gbk").trim();
	}

	/**
	 * 工行银企直连请求方式
	 * 
	 * @param reqDate
	 * @param paramsAfterUrl
	 * @return
	 * @throws B2EException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("restriction")
	private String ICBCHttpRequest(String reqDate) throws B2EException,
			IllegalArgumentException, UnsupportedEncodingException {
		String ncURL = this.gate.getNcUrl();
		String result = "";
		String signResponse = null;
		String encode = this.gate.getEncode();
		String ID = this.gate.getUserNo();
		String Version = "0.0.0.1";
		String BankCode = this.gate.getAccName();
		String GroupCIS = this.gate.getCorpNo();
		String PackageID = reqDate.substring(reqDate.indexOf("<fSeqno>") + 8,
				reqDate.indexOf("</fSeqno>"));
		String SendTime = new SimpleDateFormat("HHmmssSSS").format(new Date());
		String TransCode = reqDate.substring(
				reqDate.indexOf("<TransCode>") + 11,
				reqDate.indexOf("</TransCode>"));
		String data;
		if (TransCode.equals("PAYPER") || TransCode.equals("PAYENT")) {
			signResponse = ICBCsign(ncURL, reqDate);// 签名
		}
		if (null != signResponse) {
			data = "userID=" + ID + "&PackageID=" + PackageID + "&SendTime="
					+ SendTime + "&Version=" + Version + "&TransCode="
					+ TransCode + "&BankCode=" + BankCode + "&GroupCIS="
					+ GroupCIS + "&ID=" + ID + "&PackageID=" + PackageID
					+ "&Cert=" + "&reqData=" + signResponse;
		} else {
			data = "userID=" + ID + "&PackageID=" + PackageID + "&SendTime="
					+ SendTime + "&Version=" + Version + "&TransCode="
					+ TransCode + "&BankCode=" + BankCode + "&GroupCIS="
					+ GroupCIS + "&ID=" + ID + "&PackageID=" + PackageID
					+ "&Cert=" + "&reqData=" + reqDate;
		}
		try {
			URL url;
			url = new URL(ncURL + "/servlet/ICBCCMPAPIReqServlet");
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			os.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
			br.close();
			if ("".equals(result))
				return null;

			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			try {
				byte[] b = decoder.decodeBuffer(result.substring(8));// 解码
				result = new String(b, encode);
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "httpRequest", e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());
		}
		return result;
	}

	// /工行签名（结算类）
	private String ICBCsign(String ncURL, String reqData) throws B2EException {
		HttpClient client = new HttpClient();
		String encode = this.gate.getCodeType();
		String port = getPort("sign");
		String ncurl = ncURL + ":" + port + "/servlet/ICBCCMPAPIReqServlet";
		PostMethod postMethod = new PostMethod(ncurl);
		try {
			String length = String.valueOf(reqData.getBytes("gb2312").length);
			postMethod.setRequestHeader("Content-Type", "INFOSEC_SIGN/1.0");
			postMethod.setRequestHeader("Content-Length", length);
			postMethod.setRequestEntity(new StringRequestEntity(reqData,
					"text/html", "gb2312"));
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				String responseStr = transCharCode(
						postMethod.getResponseBodyAsString(), "gb2312");// xml转码
				String result = responseStr.substring(
						responseStr.indexOf("<result>") + 8,
						responseStr.indexOf("</result>"));
				if ("0".equals(result)) {
					return responseStr.substring(
							responseStr.indexOf("<sign>") + 6,
							responseStr.indexOf("</sign>"));
				}
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "ICBCHttpRequest",
					e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());
		} finally {
			postMethod.releaseConnection();
		}
		return null;
	}

	/***
	 * 工行选择端口
	 * 
	 * @param type
	 * @return
	 */
	public String getPort(String type) {
		if ("sign".equals(type)) {
			return "443";
		}
		return "80";
	}

	/***
	 * 盛京银行交易请求 -单笔支付接口
	 * 
	 * @param url
	 * @param reqData
	 * @return 返回transCode+"\r\n"+"reqXml"
	 */
	public String sjBankReq(String reqData) {
		String result = null;
		HttpClient client = new HttpClient();
		String[] reqParams = reqData.split("\\|");
		String transCode = reqParams[0];
		String port = getPortForSj("trans");
		String url = gate.getNcUrl();
		PostMethod postMethod = new PostMethod(url + ":" + port
				+ "/corporLink/httpAccess");
		try {
			String sign = sjBankSign(url, reqData);
			postMethod.setRequestHeader("Content-Type",
					"text/xml;charset=gb2312");
			postMethod.setRequestEntity(new StringRequestEntity(sign,
					"text/xml", "gb2312"));
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				String respData = postMethod.getResponseBodyAsString();
				result = transCode + "\r\n"
						+ new String(respData.getBytes(), "utf-8");
			} else {
				LogUtil.printErrorLog("B2eProcess", "sjBankReq", "交易请求异常！");
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// /签名结果
		return result;
	}

	/***
	 * 盛京银行交易请求 -代付预转存接口
	 * 
	 * @param url
	 * @param reqData
	 * @return 返回transCode+"\r\n"+"reqXml"
	 */
	public String sjBankYZCReq(String reqData) {
		String result = null;
		HttpClient client = new HttpClient();
		String[] reqParams = reqData.split("\\|");
		String transCode = reqParams[0];
		String port = getPortForSj("trans");
		String url = gate.getNcUrl();
		PostMethod postMethod = new PostMethod(url + ":" + port
				+ "/corporLink/httpAccess");
		try {
			String sign = sjBankSign(url, reqData);
			postMethod.setRequestHeader("Content-Type",
					"text/xml;charset=gb2312");
			postMethod.setRequestEntity(new StringRequestEntity(sign,
					"text/xml", "gb2312"));
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				String respData = postMethod.getResponseBodyAsString();
				result = transCode + "\r\n"
						+ new String(respData.getBytes(), "utf-8");
			} else {
				LogUtil.printErrorLog("B2eProcess", "sjBankYZCReq", "交易请求异常！");
				return transCode + "\r\n" + "交易请求异常！";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// /签名结果
		return result;
	}

	/****
	 * 盛京签名方法
	 * 
	 * @param url
	 * @param reqData
	 *            code|xml
	 * @return
	 * @throws B2EException
	 */
	public String sjBankSign(String url, String reqData) throws B2EException {
		String result = null;
		String[] reqParams = reqData.split("\\|");
		String transCode = reqParams[0];
		String reqXml = reqParams[1];
		String port = getPortForSj("sign");
		HttpClient client = new HttpClient();
		LogUtil.printInfoLog("B2eProcess", "sjbank_sign", "url:	" + url + ":"
				+ port);
		PostMethod postMethod = new PostMethod(url + ":" + port);
		try {
			postMethod.setRequestHeader("Content-Type", "INFOSEC_SIGN/1.0");
			postMethod.setRequestEntity(new StringRequestEntity(reqXml,
					"text/xml", "gb2312"));
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				String responseStr = new String(postMethod
						.getResponseBodyAsString().getBytes(), "utf-8");// xml转码
				System.out.println("sign resp:"
						+ new String(responseStr.getBytes()));
				Document document = DocumentHelper.parseText(responseStr);
				Element root = document.getRootElement();
				Element head = root.element("head");
				Element resultE = head.element("result");
				String isSignSuc = resultE.getText();
				if (isSignSuc.equals("0")) {
					Element body = root.element("body");
					Element sign = body.element("sign");
					String signString = sign.getText().replace("\r\n", "");
					result = transCode + "|#" + signString;
				}
			} else {
				LogUtil.printErrorLog("B2eProcess", "sjBankSign", "签名请求异常！");
				return transCode + "|#签名请求异常";
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "ICBCHttpRequest",
					e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());
		} finally {
			postMethod.releaseConnection();
		}
		return result;
	}

	/****
	 * 获取盛京前置端口号
	 * 
	 * @param type
	 * @return
	 */
	public String getPortForSj(String type) {
		if (type.equals("sign")) {
			return "4437";
		} else {
			return "4455";
		}
	}

	/***
	 * 邮储银企直连请求
	 * @param reqDate
	 * @return
	 * @throws Exception
	 */
	private String psbcScoket(String reqData) throws Exception {
		String result="";
		Socket socket=null;
		PrintWriter pw=null;
		InputStream is=null;
		try {
			String ncUrl = this.gate.getNcUrl();
			int n = ncUrl.indexOf(":");
			String ip = ncUrl.substring(0, n);
			int port = Integer.parseInt(ncUrl.substring(n + 1));
			socket = new Socket(ip,port);
			socket.setSoTimeout(10000);
			pw = new PrintWriter(socket.getOutputStream());
			byte[] by = new byte[4096];
			String req=new String(reqData.getBytes("GBK"));
			pw.write(req);
			pw.flush();
			// 接收报文
			is = socket.getInputStream();
			try {
				is.read(by);
			} catch (Exception e) {
				e.printStackTrace();
			}
			result=new String(by,gate.getEncode()).trim();
			LogUtil.printErrorLog("B2EProcess", "psbcScoket","返回报文的长度判断是否为0："+result.length());
			if (result.startsWith("770020"))
			{
				// 更新密钥
				String hexString = "0123456789ABCDEF";
				byte[] bytes = getSubbyte(by, 87, 95);
				String key=this.gate.getToken();
				// 获取中平返回密钥
				StringBuilder sb = new StringBuilder(bytes.length * 2);
				for (int i = 0; i < bytes.length; i++)
				{
					sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
					sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
				}
				// 解密
				LogUtil.printErrorLog("B2EProcess", "psbcScoket","********************"+sb.toString());
				String resultMac =new String(DesUtil2.decrypt(sb.toString().getBytes(), key.getBytes()));
				LogUtil.printErrorLog("B2EProcess", "psbcScoket","解密后的MAC为："+resultMac);
				return resultMac;
			}
		} catch (IOException e) {
			LogUtil.printErrorLog("B2EProcess", "psbcScoket",e.getMessage());
		}finally{
			if(pw!=null){
				pw.close();				
			}
			if(is!=null){
			is.close();
			}
			if(socket!=null&&!socket.isClosed()){
			socket.close();
			}
		}
		return result;
	}

	//截取需要解密的报文
	private byte[] getSubbyte(byte[] value, int begin, int end)
	{
		byte[] byte1 = value;
		byte[] byte2 = new byte[end - begin];
		for (int i = 0; i < byte2.length; i++)
			byte2[i] = byte1[begin + i];
		return byte2;
	}
	
	/***
	 * 盛京银企直连 新系统 Socket请求
	 * @param reqData
	 * @return  <a>......</a>
	 * @throws Exception
	 */
	private String sjBankSocket(String reqData) throws Exception{
		String ncUrl = this.gate.getNcUrl();
		String[] url=ncUrl.split(":");
		String host=url[0];
		Integer port=Integer.parseInt(url[1]);
		Socket socket = new Socket();
		InetSocketAddress endpoint = new InetSocketAddress(host, port);
		socket.setSoTimeout(60000);
		socket.connect(endpoint);
		socket.setSendBufferSize(1024 * 4);
		socket.setReceiveBufferSize(1024 *4);
		FileOutputStream out = (FileOutputStream) socket.getOutputStream();
		String encode = "GBK";
		byte[] b2 = reqData.getBytes(encode);
		for (byte c : b2) {
			out.write(c);
		}
		out.flush();
		FileInputStream read = (FileInputStream) socket.getInputStream();
		byte[] header = new byte[10];
		while ((read.read(header)) > 0) {
			break;
		}
		Integer bodyLen = Integer.parseInt(new String(header).trim());
		byte[] body = new byte[bodyLen]; // 设置body长度
		Integer size = 0;
		int i = 0;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		while ((i = read.read(body)) > 0) {
			if (size >= bodyLen) {
				break;
			}
			size += i;
			byteOut.write(body, 0, i);
		}
		out.close();
		read.close();
		socket.close();
		return new String(byteOut.toByteArray(), "gbk").trim().substring(2);
	}
	
}