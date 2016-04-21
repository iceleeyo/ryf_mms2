package com.rongyifu.mms.ewp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.rongyifu.mms.bank.b2e.DaifuAutoRun;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.util.URIUtil;
//
//import com.rongyifu.mms.bank.b2e.B2ETrCode;
//import com.rongyifu.mms.bank.b2e.DaifuAutoRun;
//import com.rongyifu.mms.common.ParamCache;
//import com.rongyifu.mms.exception.B2EException;


//import com.rongyifu.mms.bank.b2e.B2EProcess;
//import com.rongyifu.mms.bean.B2EGate;
//import com.rongyifu.mms.exception.B2EException;

@SuppressWarnings("serial")
public class test extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public test() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		out.write("2.8.9");
		
		
//		String NetpayNotifyData= "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iZ2IyMzEyIiA/PjxCT0NPTT48bm90aWZ5X21vZGU+QjJCPC9ub3RpZnlfbW9kZT48bm90aWZ5X3R5cGU+MTAwPC9ub3RpZnlfdHlwZT48b3JkZXJJbmZvPjxvcmRlcl9ubz5CTzEyMDYxODAwMDE1NTA8L29yZGVyX25vPjxlc3RhYmxpc2hfZGF0ZT4yMDEyMDYxODwvZXN0YWJsaXNoX2RhdGU+PGVzdGFibGlzaF90aW1lPjE0MTU1NDwvZXN0YWJsaXNoX3RpbWU+PG1lcmNoYW50X25vPjMwMTMxMDA0MTMxOTUwMDwvbWVyY2hhbnRfbm8+PG1lcmNoYW50X29yZGVyX25vPjc4ODwvbWVyY2hhbnRfb3JkZXJfbm8+PG9yZGVyX2N1cnJlbmN5PkNOWTwvb3JkZXJfY3VycmVuY3k+PG9yZGVyX2Ftb3VudD40LjE3PC9vcmRlcl9hbW91bnQ+PHBheWVkX2Ftb3VudD40LjE3PC9wYXllZF9hbW91bnQ+PG9yZGVyX3N0YXR1cz4xMDA8L29yZGVyX3N0YXR1cz48L29yZGVySW5mbz48cGF5bWVudD48c2VyaWFsX25vPjUwMDk1MDgyPC9zZXJpYWxfbm8+PHRyYW5fZGF0ZT4yMDEyMDQyNTwvdHJhbl9kYXRlPjx0cmFuX3R5cGU+MTAwPC90cmFuX3R5cGU+PHBheVRyYW5JbmZvPjxwYXlfYWNjX25vPjMxMDA2NjY2MTAxMDEyMzA3OTYxNzwvcGF5X2FjY19ubz48cGF5X2Ftb3VudD40LjE3PC9wYXlfYW1vdW50PjwvcGF5VHJhbkluZm8+PHJlY1RyYW5JbmZvPjxyZWNfYWNjX25vPjMxMDA2NjQ5NjAxODAwMzg0NjU1ODwvcmVjX2FjY19ubz48cmVjX2Ftb3VudD40LjE3PC9yZWNfYW1vdW50PjwvcmVjVHJhbkluZm8+PGZlZV9hbW91bnQ+MS4yMDwvZmVlX2Ftb3VudD48dHJhbl9zdGF0dXM+MjE8L3RyYW5fc3RhdHVzPjxmYWlsX3JlYXNvbj5TQzAwMDA8L2ZhaWxfcmVhc29uPjwvcGF5bWVudD48L0JPQ09NPg==";
//		String NetpayNotifyMsg= "MIIMFgYJKoZIhvcNAQcCoIIMBzCCDAMCAQExCzAJBgUrDgMCGgUAMIIEowYJKoZIhvcNAQcBoIIElASCBJBQRDk0Yld3Z2RtVnljMmx2YmowaU1TNHdJaUJsYm1OdlpHbHVaejBpWjJJeU16RXlJaUEvUGp4Q1QwTlBUVDQ4Ym05MGFXWjVYMjF2WkdVK1FqSkNQQzl1YjNScFpubGZiVzlrWlQ0OGJtOTBhV1o1WDNSNWNHVStNVEF3UEM5dWIzUnBabmxmZEhsd1pUNDhiM0prWlhKSmJtWnZQanh2Y21SbGNsOXViejVDVHpFeU1EWXhPREF3TURFMU5UQThMMjl5WkdWeVgyNXZQanhsYzNSaFlteHBjMmhmWkdGMFpUNHlNREV5TURZeE9Ed3ZaWE4wWVdKc2FYTm9YMlJoZEdVK1BHVnpkR0ZpYkdsemFGOTBhVzFsUGpFME1UVTFORHd2WlhOMFlXSnNhWE5vWDNScGJXVStQRzFsY21Ob1lXNTBYMjV2UGpNd01UTXhNREEwTVRNeE9UVXdNRHd2YldWeVkyaGhiblJmYm04K1BHMWxjbU5vWVc1MFgyOXlaR1Z5WDI1dlBqYzRPRHd2YldWeVkyaGhiblJmYjNKa1pYSmZibTgrUEc5eVpHVnlYMk4xY25KbGJtTjVQa05PV1R3dmIzSmtaWEpmWTNWeWNtVnVZM2srUEc5eVpHVnlYMkZ0YjNWdWRENDBMakUzUEM5dmNtUmxjbDloYlc5MWJuUStQSEJoZVdWa1gyRnRiM1Z1ZEQ0MExqRTNQQzl3WVhsbFpGOWhiVzkxYm5RK1BHOXlaR1Z5WDNOMFlYUjFjejR4TURBOEwyOXlaR1Z5WDNOMFlYUjFjejQ4TDI5eVpHVnlTVzVtYno0OGNHRjViV1Z1ZEQ0OGMyVnlhV0ZzWDI1dlBqVXdNRGsxTURneVBDOXpaWEpwWVd4ZmJtOCtQSFJ5WVc1ZlpHRjBaVDR5TURFeU1EUXlOVHd2ZEhKaGJsOWtZWFJsUGp4MGNtRnVYM1I1Y0dVK01UQXdQQzkwY21GdVgzUjVjR1UrUEhCaGVWUnlZVzVKYm1adlBqeHdZWGxmWVdOalgyNXZQak14TURBMk5qWTJNVEF4TURFeU16QTNPVFl4Tnp3dmNHRjVYMkZqWTE5dWJ6NDhjR0Y1WDJGdGIz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";
//		String NetpayNotifySignData= "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";
//
//		
//		BASE64Decoder decoder = new BASE64Decoder();
//		String notifyMsg = new String(decoder.decodeBuffer(NetpayNotifyData));
//		
//		
//		Map <String,String> m = new HashMap<String,String>();
//		
//		m.put("NetpayNotifyMsg", NetpayNotifyMsg);
//		
//		try {
//			BOCOM.genB2BVerifyString(m);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		
//		String NetpayNotifyData= "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iZ2IyMzEyIiA/PjxCT0NPTT48bm90aWZ5X21vZGU+QjJCPC9ub3RpZnlfbW9kZT48bm90aWZ5X3R5cGU+MTAwPC9ub3RpZnlfdHlwZT48b3JkZXJJbmZvPjxvcmRlcl9ubz5CTzEyMDYxODAwMDE1NTA8L29yZGVyX25vPjxlc3RhYmxpc2hfZGF0ZT4yMDEyMDYxODwvZXN0YWJsaXNoX2RhdGU+PGVzdGFibGlzaF90aW1lPjE0MTU1NDwvZXN0YWJsaXNoX3RpbWU+PG1lcmNoYW50X25vPjMwMTMxMDA0MTMxOTUwMDwvbWVyY2hhbnRfbm8+PG1lcmNoYW50X29yZGVyX25vPjc4ODwvbWVyY2hhbnRfb3JkZXJfbm8+PG9yZGVyX2N1cnJlbmN5PkNOWTwvb3JkZXJfY3VycmVuY3k+PG9yZGVyX2Ftb3VudD40LjE3PC9vcmRlcl9hbW91bnQ+PHBheWVkX2Ftb3VudD40LjE3PC9wYXllZF9hbW91bnQ+PG9yZGVyX3N0YXR1cz4xMDA8L29yZGVyX3N0YXR1cz48L29yZGVySW5mbz48cGF5bWVudD48c2VyaWFsX25vPjUwMDk1MDgyPC9zZXJpYWxfbm8+PHRyYW5fZGF0ZT4yMDEyMDQyNTwvdHJhbl9kYXRlPjx0cmFuX3R5cGU+MTAwPC90cmFuX3R5cGU+PHBheVRyYW5JbmZvPjxwYXlfYWNjX25vPjMxMDA2NjY2MTAxMDEyMzA3OTYxNzwvcGF5X2FjY19ubz48cGF5X2Ftb3VudD40LjE3PC9wYXlfYW1vdW50PjwvcGF5VHJhbkluZm8+PHJlY1RyYW5JbmZvPjxyZWNfYWNjX25vPjMxMDA2NjQ5NjAxODAwMzg0NjU1ODwvcmVjX2FjY19ubz48cmVjX2Ftb3VudD40LjE3PC9yZWNfYW1vdW50PjwvcmVjVHJhbkluZm8+PGZlZV9hbW91bnQ+MS4yMDwvZmVlX2Ftb3VudD48dHJhbl9zdGF0dXM+MjE8L3RyYW5fc3RhdHVzPjxmYWlsX3JlYXNvbj5TQzAwMDA8L2ZhaWxfcmVhc29uPjwvcGF5bWVudD48L0JPQ09NPg==";
//		String NetpayNotifyMsg= "MIIMFgYJKoZIhvcNAQcCoIIMBzCCDAMCAQExCzAJBgUrDgMCGgUAMIIEowYJKoZIhvcNAQcBoIIElASCBJBQRDk0Yld3Z2RtVnljMmx2YmowaU1TNHdJaUJsYm1OdlpHbHVaejBpWjJJeU16RXlJaUEvUGp4Q1QwTlBUVDQ4Ym05MGFXWjVYMjF2WkdVK1FqSkNQQzl1YjNScFpubGZiVzlrWlQ0OGJtOTBhV1o1WDNSNWNHVStNVEF3UEM5dWIzUnBabmxmZEhsd1pUNDhiM0prWlhKSmJtWnZQanh2Y21SbGNsOXViejVDVHpFeU1EWXhPREF3TURFMU5UQThMMjl5WkdWeVgyNXZQanhsYzNSaFlteHBjMmhmWkdGMFpUNHlNREV5TURZeE9Ed3ZaWE4wWVdKc2FYTm9YMlJoZEdVK1BHVnpkR0ZpYkdsemFGOTBhVzFsUGpFME1UVTFORHd2WlhOMFlXSnNhWE5vWDNScGJXVStQRzFsY21Ob1lXNTBYMjV2UGpNd01UTXhNREEwTVRNeE9UVXdNRHd2YldWeVkyaGhiblJmYm04K1BHMWxjbU5vWVc1MFgyOXlaR1Z5WDI1dlBqYzRPRHd2YldWeVkyaGhiblJmYjNKa1pYSmZibTgrUEc5eVpHVnlYMk4xY25KbGJtTjVQa05PV1R3dmIzSmtaWEpmWTNWeWNtVnVZM2srUEc5eVpHVnlYMkZ0YjNWdWRENDBMakUzUEM5dmNtUmxjbDloYlc5MWJuUStQSEJoZVdWa1gyRnRiM1Z1ZEQ0MExqRTNQQzl3WVhsbFpGOWhiVzkxYm5RK1BHOXlaR1Z5WDNOMFlYUjFjejR4TURBOEwyOXlaR1Z5WDNOMFlYUjFjejQ4TDI5eVpHVnlTVzVtYno0OGNHRjViV1Z1ZEQ0OGMyVnlhV0ZzWDI1dlBqVXdNRGsxTURneVBDOXpaWEpwWVd4ZmJtOCtQSFJ5WVc1ZlpHRjBaVDR5TURFeU1EUXlOVHd2ZEhKaGJsOWtZWFJsUGp4MGNtRnVYM1I1Y0dVK01UQXdQQzkwY21GdVgzUjVjR1UrUEhCaGVWUnlZVzVKYm1adlBqeHdZWGxmWVdOalgyNXZQak14TURBMk5qWTJNVEF4TURFeU16QTNPVFl4Tnp3dmNHRjVYMkZqWTE5dWJ6NDhjR0Y1WDJGdGIz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";
//		String NetpayNotifySignData= "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";


		
		
		
//		System.out.println(notifyMsg);
		
//		DaifuAutoRun o = new DaifuAutoRun();
//		o.run();
		
//		System.out.println(ParamCache.getIntParamByName("DAI_FA_FILE_MAX"));
		
//		BocXML xml = new BocXML();
//		B2ERet result = new B2ERet();
//		
//		xml.parseXML(result, resData);
		
		
//		String b2eStr=request.getParameter("b2e");
//		B2EProcess b2e=null;
//		if(b2eStr.equals("sign")){
//			b2e=signTest();
//		}else if(b2eStr.equals("trans")){
//			b2e=transTest();
//		}else if(b2eStr.equals("balance")){
//			b2e=queryBalanceTest();
//		}else if(b2eStr.equals("pay")){
//			b2e=daiFa();
//		}else if(b2eStr.equals("pay2")){
//			b2e=daiFa2();
//		}else{
//			b2e=queryTransStateTest();
//		}
//		
//		try {
//			Map<String,String> p = b2e.submit().getP();
////			String xmlStr=b2e.submit();
//			for(String name : p.keySet())
//			{	out.print(name);
//			out.print(":");
//			out.print(p.get(name));
//		}
//			
//		} catch (B2EException e) {
//			e.printStackTrace();
//			out.println(e.getMessage());
//		}
		out.flush();
		out.close();
	}
//    private B2EGate getGate(){
//		B2EGate gate = new B2EGate();
//		gate.setGid(40001);
//		gate.setNcUrl("http://192.168.64.135:8080/B2EC/E2BServlet");
//		gate.setEncode("UTF-8");
//		gate.setCorpNo("25404157");
//		gate.setUserNo("25933681");
//		gate.setUserPwd("tM2dinjM");
//		gate.setToken("CKeqruGrbW6ARZCsXANqmng");
//		return gate;
//    } 
//	private B2EProcess signTest(){
//		B2EGate gate = new B2EGate();
//		gate.setGid(40001);
//		gate.setNcUrl("http://192.168.64.135:8080/B2EC/E2BServlet");
//		gate.setEncode("UTF-8");
//		gate.setCorpNo("25404157");
//		gate.setUserNo("25933681");
//		gate.setBkNo("47669");
//		gate.setUserPwd("tM2dinjM");
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		b2e.putParam("tr_code", "b2e0001");
//		b2e.putParam("trnid", (System.currentTimeMillis()+"").substring(5));
//		b2e.putParam("oprpwd", "tM2dinjM");
//		return b2e;
//	}
//	/**
//	 * 交易
//	 * @return
//	 */
//	public B2EProcess transTest(){
//		String trnid=(System.currentTimeMillis()+"").substring(4);
//		B2EGate gate=getGate();
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		//head部分
//		b2e.putParam("tr_code","b2e0009");
//		b2e.putParam("trnid",trnid );
//		
//		b2e.putParam("insid",trnid );
//		b2e.putParam("fribkn","47669");
//		b2e.putParam("actacn","747158003782");
//		b2e.putParam("actnam","宝安西乡股份有限公司BB");
////		<toactn>
//		b2e.putParam("toibkn","47669");
//		b2e.putParam("actacn2","765357970828");
//		b2e.putParam("toname","市民中心基金公司金融机构");
//		b2e.putParam("toaddr","test");
//		b2e.putParam("tobknm","test支行");
//		
//		b2e.putParam("trnamt","100.00");
//		b2e.putParam("trncur","CNY");
//		b2e.putParam("priolv","0");
//		b2e.putParam("furinfo","企业划款");
//		b2e.putParam("trfdate","20120809");
//		b2e.putParam("comacn","");
//		return b2e;
//	}
///**
// * 查询交易状态
// * @return
// */
//	public B2EProcess queryTransStateTest(){
//		String trnid=(System.currentTimeMillis()+"").substring(4);
//		B2EGate gate=getGate();
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		//head部分
//		b2e.putParam("tr_code","b2e0007");
//		b2e.putParam("trnid",trnid );
//		
//		b2e.putParam("insid","372147403" );
//		b2e.putParam("obssid","346527663" );
//		return b2e;
//	}
//	/**
//	 * 查询余额
//	 * @return
//	 */
//	public B2EProcess queryBalanceTest(){
//		String trnid=(System.currentTimeMillis()+"").substring(4);
//		B2EGate gate=getGate();
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		//head部分
//		b2e.putParam("tr_code","b2e0005");
//		b2e.putParam("trnid",trnid );
//		
//		b2e.putParam("ibknum","47669" );//联行号
//		b2e.putParam("actacn","747158003782" );//账号
//		return b2e;
//	}
//	/**
//	 * 快捷代发
//	 * @return
//	 */
//	public B2EProcess daiFa(){
//		String trnid=(System.currentTimeMillis()+"").substring(4);
//		B2EGate gate=getGate();
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		//head部分
//		b2e.putParam("tr_code","b2e0078");
//		b2e.putParam("trnid",trnid );
//		
//		b2e.putParam("insid",trnid );
//		b2e.putParam("fribkn","47669");
//		b2e.putParam("actacn","770557970899");
//		b2e.putParam("actnam","市民中心基金公司金融机构");
////		<toactn>
//		b2e.putParam("pybcur","CNY");
//		b2e.putParam("pybamt","100.00");
//		b2e.putParam("pybnum","1"); //批总笔数
//		b2e.putParam("crdtyp","7"); //6：他行 7：我行
//		b2e.putParam("furinfo","EV");
//		b2e.putParam("useinf","工资");
//		b2e.putParam("trfdate","20120809");
//		
//		b2e.putParam("toibkn","47669");
//		b2e.putParam("tobank","");
//		b2e.putParam("toactn","765357970828");
//		b2e.putParam("pybcur2","CNY");
//		b2e.putParam("pydamt","100.00");
//		b2e.putParam("toname","市民中心基金公司金融机构");
//		b2e.putParam("toidtp","");
//		b2e.putParam("toidet","");
//		//furinfo
//		return b2e;
//	}
//	/**
//	 * 普通代发
//	 * @return
//	 */
//	public B2EProcess daiFa2(){
//		String trnid=(System.currentTimeMillis()+"").substring(4);
//		B2EGate gate=getGate();
//		
//		B2EProcess b2e = new B2EProcess(gate);
//		//head部分
//		b2e.putParam("tr_code","b2e0014");
//		b2e.putParam("trnid",trnid );
//		
//		b2e.putParam("insid",trnid );
//		b2e.putParam("fribkn","47669");
//		b2e.putParam("actacn","770557970899");
//		b2e.putParam("actnam","市民中心基金公司金融机构");
////		<toactn>
//		b2e.putParam("pybcur","CNY");
//		b2e.putParam("pybamt","100");
//		b2e.putParam("pybnum","1"); //批总笔数
//		b2e.putParam("crdtyp","7"); //6：他行 7：我行
//		b2e.putParam("furinfo","EV");
//		b2e.putParam("useinf","工资");
//		b2e.putParam("trfdate","20120809");
//		
//		b2e.putParam("toibkn","47669");
//		b2e.putParam("tobank","");
//		b2e.putParam("toactn","765357970828");
//		b2e.putParam("pybcur2","CNY");
//		b2e.putParam("pydamt","100");
//		b2e.putParam("toname","市民中心基金公司金融机构");
//		b2e.putParam("toidtp","");
//		b2e.putParam("toidet","");
//		//furinfo
//		return b2e;
//	}
	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		// Put your code here
	}

}
