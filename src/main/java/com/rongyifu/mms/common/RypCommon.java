package com.rongyifu.mms.common;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import net.rytong.encrypto.provider.RsaEncrypto;

import cert.CertUtil;

import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class RypCommon {
	
	PubDao dao = new PubDao();

	Map<Integer, String> objs = null;

//	/** * 融易通商户所对应的操作员 */
//	public Map<Integer, String> getHashOper(int mid) {
//		String sql = "select oper_id,oper_name from oper_info where mtype=0 and mid =  " + mid;
//		return genMap(sql, "oper_id", "oper_name");
//	}

//	/* account表type 操作类型 */
	public Map<Integer, String> getAccountType() {
		return AppParam.account_type;
	}
	
	/* tlog表type 操作类型 ,包括退款*/
	public static Map<Integer, String> getTlogType() {
		Map<Integer, String> m = new HashMap<Integer, String>();
		m.putAll(AppParam.tlog_type);
		m.put(4, "退款交易");
		return m;
	}
	
	/**
	 * 所有商户MAP
	 * @return
	 */
	public Map<Integer,String> getHashMer(){
		RYFMapUtil obj = RYFMapUtil.getInstance();
		return obj.getMerMap();
	}
	/**
	 * 所有银行MAP
	 * @return
	 */
	public Map<Integer,String> getHashGate(){
		return RYFMapUtil.getGateMap();
	}

//	/** ************************ */
//	/* 文件的写入读取
//	/** ************************ */
//	public String WriteFile(String path, String prikey) { // 私钥文件写入
//
//		File f = new File(path);
//		String parePath = f.getParent();
//		File pf = new File(parePath);
//		if (!pf.exists()) {
//			pf.mkdirs();
//		}
//		String msg = "ok";
//		try {
//			BufferedWriter w = new BufferedWriter(new FileWriter(f));
//			w.write(prikey);
//			w.newLine();
//			w.flush();
//			w.close();
//		} catch (Exception e) {
//			msg = "文件写入失败";
//		}
//		return msg;
//	}
//
//	public String ReadFile(String path) { // 私钥文件的读�?
//		File f = new File(path);
//		String pri = "";
//		if (!f.exists()) {
//			Ryt.print("文件不存在，将创建一个新文件");
//			try {
//				f.createNewFile();
//			} catch (Exception e) {
//				Ryt.err("文件创建失败");
//			}
//		}
//		try {
//			BufferedReader r = new BufferedReader(new FileReader(f));
//			String c = null;
//			while ((c = r.readLine()) != null) {
//				pri += c;
//			}
//		} catch (IOException e) {
//			Ryt.err("读取文件失败");
//		}
//		return pri;
//	}
	/* 请求URL方法 */
	public static String httpRequest(String address) throws IOException {
		String ret = "";
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setDoOutput(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			ret = ret + inputLine;
		}
		in.close();
		in = null;
		url = null;
		ret = ret.trim();
		return ret;
	}
	/* 请求URL方法 */
	public static InputStream httpRequestReturnStream(String address) throws IOException {
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setDoOutput(true);
		return connection.getInputStream();
	}
	/* 请求商户后台 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public void requestMerBGUrl(String tseq, int transDate, String transStat, String msg, int now_t) throws Exception{

		String ordId = "";
		String merId = "";
		String merPriv = "";
		String requestUrl = "";
		String transAmt = "";
		String gateId = "";
		String pSigna = "";// 签名字符串
		String pSigna2 = "";
//		JdbcTemplate jt = new JdbcConn().getJdbcTemplate();
		List list = new ArrayList();
		if (transDate==DateUtil.today()) {
			list = dao.queryForList("select * from tlog where tseq = " +Ryt.addQuotes(tseq));
		} else {
			list = dao.queryForList("select * from hlog where tseq = " +Ryt.addQuotes(tseq));
		}
		if (list.size() > 0) {
			Map m = (Map) list.get(0);
			ordId = m.get("oid").toString();
			merId = m.get("mid").toString();
			transAmt = m.get("amount").toString();
			gateId = m.get("gate").toString();
			merPriv = m.get("mer_priv") == null ? "" : m.get("mer_priv").toString();
			requestUrl = m.get("bk_url") == null ? "" : m.get("bk_url").toString();

		}else{
			return;
		}

		String chkValueText = ordId + merId + transAmt + transDate + tseq;
		String chkValueText2 = chkValueText + transStat;
		String privateKey = getKey(CertUtil.getCertPath("privatekey"));
		try {
			pSigna = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText.getBytes(), privateKey));
			pSigna2 = URLEncoder.encode(RsaEncrypto.RSAsign(chkValueText2.getBytes(), privateKey));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		requestUrl += "?ordId=" + ordId;
		requestUrl += "&merId=" + merId;
		requestUrl += "&transAmt=" + transAmt;
		requestUrl += "&transDate=" + transDate;
		requestUrl += "&tseq=" + tseq;
		requestUrl += "&transStat=" + transStat;
		requestUrl += "&merPriv=" + merPriv.trim();
		requestUrl += "&bankTime=" + now_t;// 银行返回时间
		requestUrl += "&errorMsg=" + msg;
		requestUrl += "&gateId=" + gateId;
		requestUrl += "&pSigna=" + pSigna;
		requestUrl += "&pSigna2=" + pSigna2;
		
		LogUtil.printInfoLog("RypCommon", "requestMerBGUrl", "商户通知： tseq=" + tseq + "\n" + requestUrl);
		
		String res = Ryt.requestWithGet(requestUrl);
		
		LogUtil.printInfoLog("RypCommon", "requestMerBGUrl", "商户响应： tseq=" + tseq + "\n" + res); 
	}

	/**
	 * 通过rsa私钥存取文件名，获取密钥
	 * @param filersa密钥存取文件�?
	 * @return 以字符串的形式返回密�?
	 */
	public static String getKey(String filePath) {
		try {
			byte[] pri = new byte[1024];
			FileInputStream inpri = new FileInputStream(filePath);
			inpri.read(pri);
			inpri.close();
			String key = new String(pri, "UTF-8");
			return key;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Map<Integer, String> genMap(String sql, String k_str, String v_str) {
		objs = new TreeMap<Integer, String>();
		List list = dao.queryForList(sql);
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map m = (Map) it.next();
			if (m.get(k_str) != null && m.get(v_str) != null) {
				objs.put(Integer.parseInt(m.get(k_str).toString()), m.get(v_str).toString());
			}
		}
	
		return objs;
	}

}
