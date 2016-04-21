package com.rongyifu.mms.bank.b2e;

import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
/****
 * 盛京银行 代付预转存接口
 * @author shdy
 *
 */
public class SjBankXML_DFYC implements BankXML {
	private static Integer query_detail=100000;
	private static String cur_code="01";//币种  01  人民币
	private static String suc_ret="0000";//成功 正常返回 对应报文中的 ret_code
	
	@Override
	public String genSubmitXML(int trCode, B2EGate gate) throws B2EException {
		// TODO Auto-generated method stub
		StringBuffer xml=null;
		if(trCode==B2ETrCode.QUERY_ACC_BALANCE){
			TrOrders os=new TrOrders();
			String today=String.valueOf(DateUtil.today());
			os.setOid(today+Ryt.createRandomStr(4));
			os.setSysDate(DateUtil.today());
			xml=new StringBuffer("00");//报文提数据结构 2字节+报文体  第一个字节表示是否加密传输（0不加密  1加密）
			xml.append("<a>");
			xml.append(getHead(os, gate, "200108"));//余额查询接口 200108
			xml.append("<body>");
			xml.append("<acno>").append(gate.getAccNo()).append("</acno>");
			xml.append("<cur_code>").append(SjBankXML_DFYC.cur_code).append("</cur_code>");
			xml.append("</body>");
			xml.append("</a>");
			return addHead(xml.toString());
		}
		
		return null;
	}
	
	
	

	@Override
	public String genSubmitXML(int trCode, TrOrders os, B2EGate gate)
			throws B2EException {
		StringBuffer xml=null;
		if(trCode==B2ETrCode.PAY_TO_OTHER){ //对外支付
			xml=new StringBuffer("00<a>");
			xml.append(getHead(os, gate, "529301"));//内部转账接口编码529301
			xml.append("<body>");
			xml.append(getXml_529301(os, gate));
			xml.append("</body>");
			xml.append("</a>");
			return addHead(xml.toString());
//		}else if(trCode==529301){//需为内部转账 重新设置trCode  
			
		}else if(trCode==B2ETrCode.QUERY_ORDER_STATE){ //查询转账结果信息
			xml=new StringBuffer("00<a>");
			xml.append(getHead(os, gate, "200205"));//查询流水状态接口编码200205
			xml.append("<body>");
			xml.append(getXml_200205(os,gate));
			xml.append("</body></a>");
			return addHead(xml.toString());
		}
		return null;
	}
	
	/****
	 * 生成查询明细报文
	 * @param trCode
	 * @param os
	 * @param gate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String genSubmitXmlForQueryDetail(int trCode, TrOrders os, B2EGate gate,Integer startDate,Integer endDate){
		StringBuffer xml=null;
		
		if(trCode==query_detail){ //查询交易明细  200110
			String today=String.valueOf(DateUtil.today());
			os.setOid(today+Ryt.createRandomStr(4));
			os.setSysDate(DateUtil.today());
			xml=new StringBuffer("00");//报文提数据结构 2字节+报文体  第一个字节表示是否加密传输（0不加密  1加密）
			xml.append("<a>");
			xml.append(getHead(os, gate, "200110")); // 交易明细查询接口编码（200110）
			xml.append("<body>");
			xml.append("<acno>").append(gate.getAccNo()).append("</acno>");
			xml.append("<cur_code>").append(SjBankXML_DFYC.cur_code).append("</cur_code>");
			xml.append("<start_date>").append(startDate).append("</start_date>");
			xml.append("<end_date>").append(endDate).append("</end_date>");
			xml.append("</body>");
			xml.append("</a>");
			return addHead(xml.toString());
		}
		
		return null;
	}
	
	@Override
	public void parseXML(B2ERet ret, String xml) throws B2EException {
		if(xml==null || xml.length()==0){
			LogUtil.printErrorLog("SjBankXml", "parseXml", "返回报文错误："+xml);
			ret.setErr("返回报文错误！");
			return;
		}
		
		Document document=null;
		String trCode=null;
		String retCode=null;//是否成功返回标识
		String respMsg=null;
		String succFlag=null;
		try {
			document=DocumentHelper.parseText(xml);
			Element sjBankRoot=document.getRootElement();
			Element head=sjBankRoot.element("head");
			Element body=sjBankRoot.element("body");
			if(head==null){
				LogUtil.printErrorLog("SjBankXml", "parseXml", "返回报文错误："+xml);
				ret.setErr("返回报文错误,head节点不存在！");
				return;
			}
			
		/*	if(body==null){
				LogUtil.printErrorLog("SjBankXml", "parseXml", "返回报文错误："+xml);
				ret.setErr("返回报文错误,body节点不存在！");
				return;
			}*/
			retCode=head.elementText("ret_code");
			trCode=head.elementText("tr_code");//交易码
			respMsg=head.elementText("ret_info");//返回信息
			succFlag=head.elementText("succ_flag");//成功标识
			if("200108".equals(trCode)){ //余额查询接口
				if(SjBankXML_DFYC.suc_ret.equals(retCode)){
					parseXml_200108(ret,head,body);
				}else{
					ret.setSucc(false);
					ret.setMsg(respMsg);
					// 可用 余额
					ret.setResult(null);
				}
			}else if("200110".equals(trCode)){//明细查询接口
				//暂时不用
			}else if("300001".equals(trCode)){//对外支付接口
				if(SjBankXML_DFYC.suc_ret.equals(retCode)){
					parseXml_300001(ret,head,body);
				}else if("9999".equals(retCode) && "1".equals(succFlag)){ // 通讯机超时
					ret.setSucc(true);
					ret.setTransStatus(2); // 银行处理中
				}else if("8".equals(succFlag)){ // 主机结果未知
					ret.setSucc(true);
					ret.setTransStatus(2); // 银行处理中
				}else{
					ret.setTransStatus(1);// 失败
					ret.setSucc(false);
					ret.setMsg(respMsg);
					ret.setErrorMsg(respMsg);
				}
				
			}else if("200205".equals(trCode)){//查询流水状态接口
				parseXml_200205(ret, retCode, respMsg, head, body);
				
			}else if("529301".equals(trCode)){//转内部帐
				if(SjBankXML_DFYC.suc_ret.equals(retCode)){
					parseXml_529301(ret,head,body);
				}else if("9999".equals(retCode) && "1".equals(succFlag)){ // 通讯机超时
					ret.setSucc(true);
					ret.setTransStatus(2); // 银行处理中
				}else if("8".equals(succFlag)){ // 主机结果未知
					ret.setSucc(true);
					ret.setTransStatus(2); // 银行处理中
				}else{
					ret.setTransStatus(1);// 失败
					ret.setSucc(false);
					ret.setMsg(respMsg);
					ret.setErrorMsg(respMsg);
				}
			}
			
		} catch (Exception e) {
			LogUtil.printErrorLog("SjBankXML_DFYC", "parseXML", xml, e);
		}
		
		return;
		
	}

	
	/***
	 * 生成公用报文头
	 * @param os
	 * @param gate
	 * @param trCode
	 * @return
	 */
	private String getHead(TrOrders os ,B2EGate gate,String trCode){
		StringBuffer head=new StringBuffer();
		String tseq=os.getOid();
		Integer sysTime=DateUtil.now();
		head.append("<head>");
		head.append("<tr_code>").append(trCode).append("</tr_code>");
		head.append("<cms_corp_no>").append("</cms_corp_no>");
		head.append("<user_no>").append("</user_no>");
		head.append("<org_code>").append("</org_code>");
		head.append("<serial_no>").append("</serial_no>");
		head.append("<req_no>").append(tseq).append("</req_no>");
		head.append("<tr_acdt>").append(os.getSysDate()).append("</tr_acdt>");
		head.append("<tr_time>").append(sysTime).append("</tr_time>");
		head.append("<channel>").append("5").append("</channel>");//ERP 送5  固定未5
		head.append("<sign>").append("</sign>");
		head.append("<file_flag>").append("0").append("</file_flag>");//0 报文  1 文件
		head.append("<reserved>").append("</reserved>");
		head.append("</head>");
		return head.toString();
		
	}
	
	/***
	 * 内部转账报文体
	 * @param os
	 * @param gate
	 * @return
	 */
	private String getXml_529301(TrOrders os,B2EGate gate){
		StringBuffer xml=new StringBuffer();
		String remark=os.getPriv();
		if(Ryt.empty(remark)||remark.equals("null")){
			remark="电银内部转账";
		}
		xml.append("<pay_acno>").append(gate.getAccNo()).append("</pay_acno>");
		xml.append("<pay_cur_code>").append(SjBankXML_DFYC.cur_code).append("</pay_cur_code>");
		xml.append("<pay_acname>").append(gate.getAccName()).append("</pay_acname>");
		xml.append("<item_no>").append("").append("</item_no>");//暂时传空值
		xml.append("<rcv_acno>").append(os.getToAccNo()).append("</rcv_acno>");//银行内部账户
		xml.append("<as_flag>").append("").append("</as_flag>");//暂时传空
		xml.append("<as_acno>").append("").append("</as_acno>");//暂时传空
		xml.append("<as_acname>").append("").append("</as_acname>");//暂时传空
		xml.append("<amt>").append(Ryt.div100(os.getTransAmt())).append("</amt>");
		xml.append("<purpose>").append(remark).append("</purpose>");
		xml.append("<postscript>").append(remark).append("</postscript>");
		
		return xml.toString();
	}
	
/*	*//***
	 * 对外支付
	 * @param os
	 * @param gate
	 * @return
	 *//*
	private String getXml_300001(TrOrders os,B2EGate gate){
		StringBuffer xml=new StringBuffer();
		String remark=os.getRemark();
		if(Ryt.empty(remark)||remark.equals("null")){
			remark="电银转账";
		}
		String urgencyFlag=handleRmtType("2", os.getTransAmt());
		String bk_no=os.getToBkNo();
		String bg=bk_no.substring(0, 3);
		String bankFlag="1";
		if(bg.equals("313")){
			if(BankNoUtil.isSjBank(bk_no)==0){
				bankFlag="0";
			}
		}
		xml.append("<pay_acno>").append(gate.getAccNo()).append("</pay_acno>");
		xml.append("<pay_cur_code>").append(SjBankXML_DFYC.cur_code).append("</pay_cur_code>");//币种
		xml.append("<pay_acname>").append(gate.getAccName()).append("</pay_acname>");
		xml.append("<as_flag>").append("</as_flag>");
		xml.append("<as_acno>").append("</as_acno>");
		xml.append("<as_acname>").append("</as_acname>");
		xml.append("<cert_type>").append("</cert_type>");
		xml.append("<cert_no>").append(os.getOid()).append("</cert_no>");
		xml.append("<rcv_acno>").append(os.getToAccNo()).append("</rcv_acno>");
		xml.append("<rcv_cur_code>").append(SjBankXML_DFYC.cur_code).append("</rcv_cur_code>");//币种
		xml.append("<rcv_acname>").append(os.getToAccName()).append("</rcv_acname>");
		xml.append("<rcv_bank_no>").append(os.getToBkNo()).append("</rcv_bank_no>");
		xml.append("<rcv_bank_name>").append(os.getToBkName()).append("</rcv_bank_name>");
		xml.append("<amt>").append(Ryt.div100(os.getTransAmt())).append("</amt>");
		xml.append("<bank_flag>").append(bankFlag).append("</bank_flag>");
		xml.append("<urgency_flag>").append(urgencyFlag).append("</urgency_flag>");
		xml.append("<purpose>").append(remark).append("</purpose>");
		xml.append("<postscript>").append(remark).append("</postscript>");

		return xml.toString();
	}
	*/
	/***
	 * 查询流水状态接口 Body
	 * @param os
	 * @param gate
	 * @return
	 */
	private String getXml_200205(TrOrders os,B2EGate gate){
		StringBuffer xml=new StringBuffer();
		String tseq=os.getOid();
		xml.append("<cert_no>").append("</cert_no>");//暂时传空值
		xml.append("<req_no>").append(tseq).append("</req_no>");//订单号
		xml.append("<tr_acdt>").append(os.getSysDate()).append("</tr_acdt>");//交易时间
		xml.append("<cms_corp_no>").append("</cms_corp_no>");
		xml.append("<acno>").append(gate.getAccNo()).append("</acno>");
		xml.append("<cur_code>").append(SjBankXML_DFYC.cur_code).append("</cur_code>");
		return xml.toString();
	}
	
	/***
	 * 解析200108 余额查询返回报文
	 * @param ret
	 * @param head
	 * @param body
	 */
	private void parseXml_200108(B2ERet ret,Element head,Element body){
		String succFlag=head.elementText("succ_flag");
		String respMsg=head.elementText("ret_info");
		if("0".equals(succFlag)){//成功
			String balance=body.elementText("use_balance");
			ret.setSucc(true);
			ret.setMsg("查询成功");
			// 可用 余额
			ret.setResult(balance);
		}else{//其他  失败
			ret.setSucc(false);
			ret.setMsg(respMsg);
			// 可用 余额
			ret.setResult(null);
		}
	}
	
	/***
	 * 解析300001 对外支付报文
	 * @param ret
	 * @param head
	 * @param body
	 */
	private void parseXml_300001(B2ERet ret,Element head,Element body){
		String succFlag=head.elementText("succ_flag");
		String respMsg=head.elementText("ret_info");
		if("0".equals(succFlag)){//交易成功
			ret.setTransStatus(0);// 成功
			ret.setSucc(true);
			ret.setMsg(respMsg);
			ret.setErrorMsg(respMsg);
		}else{// succFlag 0 成功，1 表示通讯机超时，8 表示主机结果未知，其他 表示失败   。。 上述状态希望通过查询获取最终结果
			ret.setTransStatus(2);//中间状态
			ret.setSucc(true);
		}
	}
	
	
	/***
	 * 解析529301 对外支付报文
	 * @param ret
	 * @param head
	 * @param body
	 */
	private void parseXml_529301(B2ERet ret,Element head,Element body){
		String succFlag=head.elementText("succ_flag");
		String respMsg=head.elementText("ret_info");
		if("0".equals(succFlag)){//交易成功
			ret.setTransStatus(0);// 成功
			ret.setSucc(true);
			ret.setMsg(respMsg);
			ret.setErrorMsg(respMsg);
		}else{// succFlag 0 成功，1 表示通讯机超时，8 表示主机结果未知，其他 表示失败   。。 上述状态希望通过查询获取最终结果
			ret.setTransStatus(2);//中间状态
			ret.setSucc(true);
		}
	}
	/***
	 * 解析200205查询流水状态接口返回报文
	 * @param ret
	 * @param retCode
	 * @param respMsg
	 * @param head
	 * @param body
	 */
	private void parseXml_200205(B2ERet ret, String retCode, String respMsg,
			Element head, Element body) {
		B2ETradeResult b2e=new B2ETradeResult();
		Integer transState=DaiFuTransState.PAY_PROCESSING;
		String oid=head.elementText("req_no");//电银订单号
		String serial_no=head.elementText("serial_no");//行方流水号
		
		if(SjBankXML_DFYC.suc_ret.equals(retCode)){
			String stat=body.elementText("stat");//流水状态
			String error_info=body.elementText("error_info");
			if("9".equals(stat)){//成功交易
				transState=DaiFuTransState.PAY_SUCCESS;
			}else if("6".equals(stat)){//交易失败
				transState=DaiFuTransState.PAY_FAILURE;
			}else{//其他   参照处理中
				//处理中
			}
			
			b2e.setErrMsg(error_info);
			
		}else if("2100".equals(retCode)){ //ret_code 2100 succ_flag 2 为失败交易（银行查不到该交易）
			String succFlag=head.elementText("succ_flag");
			if("2".equals(succFlag)){
				transState=DaiFuTransState.PAY_FAILURE;
				b2e.setErrMsg(respMsg);
			}
		}else{
			b2e.setErrMsg(respMsg);
			//状态参照处理中
		}
		
		b2e.setBankSeq(serial_no);
		b2e.setOid(oid);
		b2e.setState(transState);
		ret.setStatusInfo(respMsg);
		ret.setErrorMsg(respMsg);
		ret.setErr(respMsg);
		ret.setResult(b2e);
		ret.setSucc(true);
	}
	
	/***
	 * 报文头处理-> 报文体长度（不足10位时补空格至10位）
	 * @param reqXml
	 * @return
	 */
	private String addHead(String reqXml){
		String headStr = null;
		try {
			headStr =String.valueOf(reqXml.getBytes("GBK").length);
		} catch (UnsupportedEncodingException e) {
			headStr = String.valueOf(reqXml.getBytes().length);
			e.printStackTrace();
		}
		Integer len=10-headStr.length();
		for (int i = 0; i <len; i++) {
			headStr+=" ";
		}
		
		return headStr+reqXml;
	}
	
	/****
	 *  在17:00以后 走普通小额系统 
	 *  大额系统每天（0830,1630）其他时间关闭
	 * 其他走大额加急系统
	 * @param rmtType  1 加急  2 普通   
	 * @param amount
	 * @return
	 */
	public String handleRmtType(String rmtType,Long amount){
		Integer secTime=Ryt.getCurrentUTCSeconds();
		if(secTime>59400 && secTime < 30600){
			rmtType="1";
		}
		return rmtType;
	}
	
}
