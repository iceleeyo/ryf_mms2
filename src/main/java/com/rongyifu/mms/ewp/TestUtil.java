package com.rongyifu.mms.ewp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

import com.rongyifu.mms.bank.b2e.B2ETrCode;
import com.rongyifu.mms.bank.b2e.GenB2ETrnid;
import com.rongyifu.mms.bank.b2e.SjBankMXCX;
import com.rongyifu.mms.bank.b2e.SjBankXML;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrDetails;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.modules.liqmanage.dao.TrDetailsDao;
import com.rongyifu.mms.quartz.jobs.utils.SJTransDetailParser;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class TestUtil {

	public static String getBocomResult(Map<String, String> p){
		String oid = p.get("oid");
		
		StringBuffer req = new StringBuffer();
		req.append("<ap>");
		req.append("<head>");
		req.append("<tr_code>310204</tr_code>");
		req.append("<corp_no>8000197693</corp_no>");
		req.append("<user_no>00004</user_no>");
		req.append("<req_no>").append(oid).append("</req_no>");
		req.append("<tr_acdt>").append(DateUtil.today()).append("</tr_acdt>");
		req.append("<tr_time>").append(DateUtil.now()).append("</tr_time>");
		req.append("<atom_tr_count>1</atom_tr_count>");
		req.append("<channel>0</channel>");
		req.append("<reserved></reserved>");
		req.append("</head>");
		req.append("<body>");
		req.append("<query_flag>1</query_flag>");
		req.append("<ogl_serial_no>" + oid + "</ogl_serial_no>");
		req.append("</body>");
		req.append("</ap>");
		
		String res = null;
		try {
			res = httpRequestPost(req.toString(), "http://192.168.2.152:8899?", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head></head>");
		html.append("<body>");
		html.append(res.replaceAll("<", "&lt;").replaceAll("<", "&gt;"));
		html.append("</body>");
		html.append("</html>");
		
		return res;
	}
	
	/**
	 * 交行超级网银请求方式
	 * @param reqDate
	 * @param paramsAfterUrl
	 * @return
	 * @throws B2EException
	 * @throws UnsupportedEncodingException 
	 * @throws IllegalArgumentException 
	 */
	private static String httpRequestPost(String reqDate, String ncURL, String encode) throws B2EException, IllegalArgumentException, UnsupportedEncodingException {

//		String ncURL = "http://192.168.2.152:8899?";
		String response = null;
//		String encode= "UTF-8";
		URL url=null;
		HttpURLConnection conn =null;
		OutputStream os=null;
		BufferedReader br=null;
		try {
			url = new URL(ncURL);

			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			os = conn.getOutputStream();
			os.write(reqDate.toString().getBytes(encode));
			String result="";
			br= new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
			response=result;
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "BankcommBEHttpRequest", e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			try {
				if(os !=null ) os.close();
				if(br !=null ) br.close();
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return response;
	}
	
	public static String bocSign(){
		StringBuffer req = new StringBuffer();
		
		req.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		req.append("<bocb2e  version=\"120\" security=\"true\" locale=\"zh_CN\">");
		req.append("<head>");
		req.append("<trnid>" + GenB2ETrnid.getTrace() + "</trnid>");
		req.append("<custid>45772351</custid>");
		req.append("<cusopr>96430468</cusopr>");
		req.append("<trncod>b2e0001</trncod>");
		req.append("</head>");		
		req.append("<trans>");
		req.append("<trn-b2e0001-rq>");
		req.append("<b2e0001-rq>");
		req.append("<custdt>").append(DateUtil.getIntDateTime()).append("</custdt>");
		req.append("<oprpwd>X5VzGXPj</oprpwd>");
		req.append("</b2e0001-rq>");
		req.append("</trn-b2e0001-rq>");
		req.append("</trans>");
		req.append("</bocb2e>");
		
		String res = "A";
		try {
			res = httpRequest(req.toString(), "http://192.168.2.187:8080/B2EC/E2BServlet", "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		LogUtil.printInfoLog("TestUtil", "bocSign", "boc sign report:\n" + res);
		
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head></head>");
		html.append("<body>");
		html.append(res.replaceAll("<", "&lt;").replaceAll("<", "&gt;"));
		html.append("</body>");
		html.append("</html>");
		
		return res;
	}
	
	private static String httpRequest(String reqDate, String ncURL, String encode) throws B2EException {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(ncURL);
		try {
			
			method.setRequestHeader("Content-Type", "application/xmlstream");
			method.setQueryString(URIUtil.encodeQuery(reqDate, encode));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
//				response = method.getResponseBodyAsString();
				InputStream input=method.getResponseBodyAsStream();
				response=readStream(input);
				LogUtil.printInfoLog("TestUtil", "httpRequest", response);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("B2EProcess", "httpRequest", e.getMessage());
			throw new B2EException("请求银行接口异常:" + e.getMessage());

		} finally {
			method.releaseConnection();
		}
		return response;
	}
	
	private static String readStream(InputStream input) throws IOException{
		byte[] b=new byte[1064];
		StringBuffer str=new StringBuffer();
		while((input.read(b)!=-1)){
			str.append(new String(b));
		}
		input.close();
		return str.toString();
	}
	
	
	public static String getBocSupportBank(Map<String, String> p){
		String token = p.get("token");
		String tasktpy = p.get("tasktpy");
		
		StringBuffer req = new StringBuffer();
		
		req.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		req.append("<bocb2e  version=\"120\" security=\"true\" locale=\"zh_CN\">");
		req.append("<head>");
		req.append("<trnid>111726</trnid>");
		req.append("<custid>45772351</custid>");
		req.append("<cusopr>96430468</cusopr>");
		req.append("<trncod>b2e0043</trncod>");
		req.append("<token>" + token + "</token>");
		req.append("</head>");
		req.append("<trans>");
		req.append("<trn-b2e0043-rq>");
		req.append("<b2e0043-rq>");
		req.append("<tasktpy>" + tasktpy + "</tasktpy>");	// 0：CNAPS文件; 1：中行机构号文件
		req.append("</b2e0043-rq>");
		req.append("</trn-b2e0043-rq>");
		req.append("</trans>");
		req.append("</bocb2e>");
		
		String res = "A";
		try {
			res = httpRequest(req.toString(), "http://192.168.2.187:8080/B2EC/E2BServlet", "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return res;
	}
	
	/**
	 * 修改pos成功交易对账状态
	 * @param p
	 * @return
	 */
	public static String updatePosData(Map<String, String> p){
		String tseq = p.get("tseq");
		String bandkFee = p.get("bankFee");
		if(Ryt.empty(tseq) || Ryt.empty(bandkFee))
			return "null";
		
		String sql = "UPDATE hlog SET bk_chk = 1, bank_fee= " + bandkFee + " where tseq = " + tseq + " and tstat = 2 and type = 10";
		int affectedRows = new AdminZHDao().update(sql);
		LogUtil.printInfoLog("TestUtil", "updatePosData", "affected rows: " + affectedRows);
		return "OK";
	}
	
	/**
	 * 开启管理后台admin
	 * @return
	 */
	public static String openAdmin(){
		String sql = "update oper_info set state = 0,err_count = 0 where mid = 1";
		int affectedRows = new AdminZHDao().update(sql);
		LogUtil.printInfoLog("TestUtil", "openAdmin", "affected rows: " + affectedRows);
		return "OK";
	}
	
	/**
	 * 修改代付查询标识
	 * @param p
	 * @return
	 */
	public static String updateDfQueryFlag(Map<String, String> p){
		String mid = p.get("mid");
		String oid = p.get("oid");
		String sql = "update tr_orders set is_pay=1 where uid=" + Ryt.addQuotes(mid) + " and oid=" + Ryt.addQuotes(oid);
		int affectedRows = new AdminZHDao().update(sql);
		LogUtil.printInfoLog("TestUtil", "updateDfQueryFlag", "affected rows: " + affectedRows);
		return "OK";
	}
	
	/****
	 * 银企直连交易 本行对私交易 确认交易状态接口（330002 接口的交易 ）
	 * @param p
	 * @return
	 */
	public static String  getBocommResultForYQ(Map<String, String> p){
		String oid=p.get("oid");
		Integer gid=Integer.parseInt(p.get("gid"));
		B2EGate g = new AdminZHDao().getOneB2EGate(gid);
		StringBuffer req = new StringBuffer();
		req.append("<ap>");
		req.append("<head>");
		req.append("<tr_code>310207</tr_code>");
		req.append("<corp_no>").append(g.getCorpNo()).append("</corp_no>");
		req.append("<user_no>").append(g.getUserNo()).append("</user_no>");
		req.append("<req_no>").append(oid).append("</req_no>");
		req.append("<tr_acdt>").append(DateUtil.today()).append("</tr_acdt>");
		req.append("<tr_time>").append(DateUtil.now()).append("</tr_time>");
		req.append("<atom_tr_count>1</atom_tr_count>");
		req.append("<channel>0</channel>");
		req.append("<reserved></reserved>");
		req.append("</head>");
		req.append("<body>");
		req.append("<query_flag>1</query_flag>");
		req.append("<ogl_serial_no>" + oid + "</ogl_serial_no>");
		req.append("</body>");
		req.append("</ap>");
		
		String res = null;
		try {
			res = httpRequestPost(req.toString(), g.getNcUrl(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		LogUtil.printInfoLog("TestUtil", "getBocommResultForYQ", "tseq:"+oid+"\n"+"res:"+res);
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head></head>");
		html.append("<body>");
		html.append(res.replaceAll("<", "&lt;").replaceAll("<", "&gt;"));
		html.append("</body>");
		html.append("</html>");
		
		return res;
	}
	
	/***
	 * 
	 * @param p
	 * startDate,endDate
	 * @return
	 */
	public static String getSJDetail(Map<String, String> p){
		String res="";
		Integer startDate=Integer.parseInt(p.get("startDate"));
		Integer endDate=Integer.parseInt(p.get("endDate"));
		TrDetailsDao dao=new TrDetailsDao();
		deleteByDateAndGid(startDate,endDate,40006,dao);
		LogUtil.printInfoLog("TestUtil", "deleteByDateAndGid", "drop detail end");
		SjBankMXCX sjMxcx=new SjBankMXCX(startDate, endDate);
		List<String> ls=sjMxcx.querySjBankDetail();
		for (String detail : ls) {
			res+=detail+"\n";
		}
		//解析
		List<TrDetails> list = SJTransDetailParser.parse(ls);
		//存库
		if(CollectionUtils.isNotEmpty(list)){
			try {
				dao.saveTrDetails(list);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.printErrorLog("TestUtil", "getSJDetail", "detail_insert_exception", e);
			}
		}
		return res;
	}
	
	private static int deleteByDateAndGid(Integer startDate,Integer endDate,Integer gid,TrDetailsDao dao){
		
		String sql = "DELETE FROM tr_details WHERE tr_date >= ? and tr_date<=? AND gid = ?";
		Map<String,String> map=new HashMap<String, String>();
		Object[] args = {startDate,endDate,gid};
		map.put("gid", String.valueOf(gid));
		map.put("startDate", String.valueOf(startDate));
		map.put("endDate", String.valueOf(endDate));
		LogUtil.printInfoLog("TestUtil", "deleteByDateAndGid", "drop detail begin", map);
		return dao.update(sql, args);
	}
	
	/***
	 * 
	 * @param p
	 * @param gid,oid,tr_date
	 * @return
	 * @throws Exception
	 */
	public static String testSjQueryState(Map<String, String> p) throws Exception{
		SjBankXML sjb=new SjBankXML();
		AdminZHDao dao=new AdminZHDao();
		Integer gid=Integer.parseInt(p.get("gid"));
		B2EGate g = dao.getOneB2EGate(gid);
		TrOrders os=new TrOrders();
		String oid=p.get("oid");
		Integer sysTime=DateUtil.now();
		Integer today=Integer.parseInt(p.get("tr_date"));
		os.setOid(oid);
		os.setSysDate(today);
		os.setSysTime(sysTime);
		String queryXml=sjb.genSubmitXML(B2ETrCode.QUERY_ORDER_STATE, os, g);
		LogUtil.printInfoLog("TestUtil", "testSjQueryState", "requestXml:"+queryXml);
		String response2=testSJBSocket(queryXml,g);
		LogUtil.printInfoLog("TestUtil", "testSjQueryState", "responseXml:"+response2);
		return response2;
	}
	
	private static String testSJBSocket(String reqData,B2EGate g) throws IOException{
		String[] urls=g.getNcUrl().split(":");
		Integer port=Integer.parseInt(urls[1]);
		Socket socket = new Socket(urls[0], port); // ip,端口
		socket.setSoTimeout(10000);
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		String req=new String(reqData.getBytes(),"GBK");
		pw.write(req);
		pw.flush();
		InputStream read = socket.getInputStream();
		// 接收报文
		byte[] header = new byte[10];
		while ((read.read(header)) > 0) {
			break;
		}
		System.out.println("head :"+new String(header));
		Integer bodyLen = Integer.parseInt(new String(header).trim());
		byte[] body = new byte[1024*4]; // 设置body长度
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
		pw.close();
		read.close();
		socket.close();
		
		return new String(byteOut.toByteArray(), "gbk").trim().substring(2);
	}
	
	/**
	 * 处理没有代付渠道的订单
	 * @param p
	 * @return
	 */
	public static String processDfOrder(Map<String, String> p){
		String tseq = p.get("tseq");
		String gid = p.get("gid");
		String sql = "update df_transaction set tstat = 1, gid = " + gid + " where tseq = " + Ryt.addQuotes(tseq) + " and gid = 0 and tstat != 2";
		int affectedRows = new AdminZHDao().update(sql);
		return "ok|" + affectedRows;
	}
}
	