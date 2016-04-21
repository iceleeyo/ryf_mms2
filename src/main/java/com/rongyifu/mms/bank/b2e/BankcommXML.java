package com.rongyifu.mms.bank.b2e;

import java.util.List;

import org.apache.log4j.Logger;
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

public class BankcommXML implements BankXML {
	
	Logger log4j = Logger.getLogger(BankcommXML.class);
	
	// <ap>/<body>/<type> 签约类型 C1 Y 参见常用数
	// 据字典
	// S 代付其他款项

	private static final String type = "S";

	private boolean isEmpty(String value) {
		return null == value || value.trim().length() == 0;
	}

	@Override
	public String genSubmitXML(int code, TrOrders o, B2EGate gate) throws B2EException {

//		short t = o.getPtype();
		String tr_code = null;
		// 210201
		// 4、本交易还支持对他行的个人账号进行付款。如果需要对交行的个人账号进行付款，使用
		// 330002 交易或330003 交易。
		// 330002、本交易的个人收款账号只支持交行个人账号。如果需要对他行的个人账号进行付款，使
		// 用210201 交易。
		if(code==B2ETrCode.QUERY_ORDER_STATE){
			
			tr_code = "310204";
			
		}
//		else if (t == 7 && o.getToBkNo().equals(gate.getBkNo())) {// 付款到个人银行卡
//			tr_code = "330002";
//		}
//		else if (t == 5 || t == 6 || t == 8) {
//			tr_code = "210201";
//		}
		else if(code==B2ETrCode.QUERY_PAY_TO_OTHER_RESULT){
			//转账交易查询
			tr_code="310204";
		}else if(code==B2ETrCode.QUERY_ACCOUNT){
			//账户信息查询
			tr_code="310101";
		}else if(code==B2ETrCode.PAY_TO_OTHER){
			//跨行交易
			tr_code="210224";
		}else if(code==B2ETrCode.QUERY_TODAYTRANS){
			//当日交易信息查询
			tr_code="310201";
		}else if(code==B2ETrCode.QUERY_HISTORICAL_TRADE){
			//历史交易信息查询
			tr_code="310301";
		}
		
		else {
			throw new B2EException("交易码错误");
		}
		String corp_no = gate.getCorpNo();
		String user_no = gate.getUserNo();
		String req_no = o.getOid();

		StringBuffer resXml = new StringBuffer("<ap>");
		genSubHead(resXml, tr_code, corp_no, user_no, req_no, "1");
		try {
			genSubBody(resXml, tr_code, gate, o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resXml.append("</ap>");
		return resXml.toString();
	}

	/**
	 * 交易报文采用XML格式，且不带XML 头部（ <?xml version="1.0" encoding="gb2312"?>）声明； XML
	 * 报文最上层节点为‘ap’,样式如下： <ap> <head> 公共报文头 </head> <body> 交易报文体 </body> </ap>
	 * 
	 * @return 提交的完整报文
	 */
	@Override
	public String genSubmitXML(int code, B2EGate gate) throws B2EException {

		String corp_no = gate.getCorpNo();
		String user_no = gate.getUserNo();
		String req_no = GenB2ETrnid.getTrace();

		StringBuffer resXml = new StringBuffer("<ap>");
		// 查询账户余额
		if (code == B2ETrCode.QUERY_ACC_BALANCE) {
			genSubHead(resXml, "310101", corp_no, user_no, req_no, "1");
			resXml.append("<body><acno>");
			resXml.append(gate.getAccNo());
			resXml.append("</acno></body>");
		}

		else
			throw new B2EException("交易码错误");

		resXml.append("</ap>");
		return resXml.toString();
	}

	/**
	 * 公共报文头
	 * @param tr_code 交易码
	 * @param corp_no 企业代码
	 * @param user_no企业用户号
	 * @param req_no  发起方序号
	 * @param tr_acdt 交易日期
	 * @param tr_time时间
	 * @param atom_tr_count 原子交易数
	 * @param channel渠道标志
	 * @param reserved 保留字段
	 * @return 公共报文头
	 */
	private void genSubHead(StringBuffer res, String tr_code, String corp_no,
			String user_no, String req_no, String atom_tr_count) {

		res.append("<head>");
		res.append("<tr_code>").append(tr_code).append("</tr_code>");
		res.append("<corp_no>").append(corp_no).append("</corp_no>");
		res.append("<user_no>").append(user_no).append("</user_no>");
		res.append("<req_no>").append(req_no).append("</req_no>");
		res.append("<tr_acdt>").append(DateUtil.today()).append("</tr_acdt>");
		res.append("<tr_time>").append(DateUtil.now()).append("</tr_time>");
		res.append("<atom_tr_count>").append(isEmpty(atom_tr_count) ? 1 : atom_tr_count).append("</atom_tr_count>");
		res.append("<channel>0</channel>");
		res.append("<reserved></reserved>");
		res.append("</head>");
	}



	/**
	 * // 对外转账交易（210201）(目前仅支持人民币)
	 * @param res
	 * @param tr_code
	 * @param g
	 * @param o
	 * @throws B2EException
	 */
	private void genSubBody(StringBuffer res, String tr_code, B2EGate g,TrOrders o) throws Exception,B2EException {
		res.append("<body>");
		if (tr_code.equals("210201")) {
			res.append("<pay_acno>").append(g.getAccNo()).append("</pay_acno>");
			res.append("<pay_acname>").append(g.getAccName()).append("</pay_acname>");
			res.append("<rcv_bank_name>").append(o.getToBkName()).append("</rcv_bank_name>");
			res.append("<rcv_acno>").append(o.getToAccNo()).append("</rcv_acno>");
			res.append("<rcv_acname>").append(o.getToAccName()).append("</rcv_acname>");
			// 收款方交换号 C10 同城交换
			res.append("<rcv_exg_code>").append("").append("</rcv_exg_code>");
			res.append("<rcv_bank_no>").append(o.getToBkNo()).append("</rcv_bank_no>");
			res.append("<cur_code>").append("CNY").append("</cur_code>");
			res.append("<amt>").append(Ryt.div100(o.getTransAmt())).append("</amt>");
			res.append("<cert_no>").append(o.getOid()).append("</cert_no>");
			res.append("<summary>").append(o.getPriv()).append("</summary>");
			// 指收款方账户的开户行所在地0：交行1：他行
			int bank_flag = g.getBkNo().equals(o.getToBkNo()) ? 0 : 1;
			res.append("<bank_flag>").append(bank_flag).append("</bank_flag>");
			// 0：同城1：异地
			String s1 = String.valueOf(g.getProvId());
			String s2 = String.valueOf(o.getToProvId());
			
			int area_flag = (s1.equals(s2)) ? 0 : 1;
			res.append("<area_flag>").append(area_flag).append("</area_flag>");

		}
		//跨行支付交易
		else if(tr_code.equals("210224")){
			res.append("<pay_acno>").append(g.getAccNo()).append("</pay_acno> ");
			res.append("<pay_acname>").append(g.getAccName()).append("</pay_acname>");
			res.append("<rcv_acno>").append(o.getToAccNo()).append("</rcv_acno>");
			res.append("<rcv_acname>").append(o.getToAccName()).append("</rcv_acname>");
			res.append("<partyid>").append(o.getToBkNo()).append("</partyid>");
			res.append("<amt>").append(Ryt.div100(o.getTransAmt())).append("</amt>");
			res.append("<cert_no>").append(o.getOid()).append("</cert_no>");
			res.append("<purpose>").append("转账付款").append("</purpose>");
			res.append("<summary>").append(o.getPriv()==null?"":o.getPriv()).append("</summary>");
		}
		//账号信息查询
		else if(tr_code.equals("310101")){
			res.append("<acno>").append(g.getAccNo()).append("</acno>");
		}
		//当日交易明细查询
		else if(tr_code.equals("310201")){
			res.append("<acno>").append(g.getAccNo()).append("</acno>");
		}
//		//历史交易明细查询
//		else if(tr_code.equals("310301")){
//			res.append("<acno>").append(o.getAccNo()).append("</acno>");
//			res.append("<start_date>").append(o.getInitTime()).append("</start_date>");
//			res.append("<end_date>").append(o.getPriv()).append("</end_date>");
//		}
		else if (tr_code.equals("330002")) {
			// <ap>/<body>/<cert_no> 企业凭证号 C20 Y
			res.append("<cert_no>").append(o.getOid()).append("</cert_no>");
			// <ap>/<body>/<pay_acno> 付款账号 C21 Y
			res.append("<pay_acno>").append(g.getAccNo()).append("</pay_acno>");
			// <ap>/<body>/<type> 签约类型 C1 Y 参见常用数据字典
			res.append("<type>").append(type).append("</type>");
			// <ap>/<body>/<sum> 总笔数 Int Y 总笔数和明细数要一致
			res.append("<sum>").append(1).append("</sum>");
			// <ap>/<body>/<sum_amt> 总金额 N14.2 Y总金额与明细金额的和需要由发起方进行校验控制
			res.append("<sum_amt>").append(Ryt.div100(o.getTransAmt())).append("</sum_amt>");
			// <ap>/<body>/<pay_month> 月份 C6 Y
			String pay_month = String.valueOf(DateUtil.today()).substring(0, 6);
			res.append("<pay_month>").append(pay_month).append("</pay_month>");
			// <ap>/<body>/<summary> 附言 C60
			res.append("<summary>").append(o.getPriv()).append("</summary>");
			// <ap>/<body>/<busi_no> 协议编号 C10 Y
			res.append("<busi_no>").append(g.getBusiNo()).append("</busi_no>");
			// <ap>/<body>/<mailflg> 传票汇总标志 C1 Y： 汇总，N：不汇总默认为Y：汇总
			res.append("<mailflg>").append("Y").append("</mailflg>");
			// <ap>/<body>/<tran>/<rcd> 扣款明细信息 笔数限制见下面的说明
			res.append("<tran>");
			// 多个rcd
			res.append("<rcd>");
			// <ap>/<body>/<tran>/<rcd>/<card_no> 卡号 C21 Y
			res.append("<card_no>").append(o.getToAccNo()).append("</card_no>");
			// <ap>/<body>/<tran>/<rcd>/<acname> 户名 Y
			res.append("<acname>").append(o.getToAccName()).append("</acname>");
			// <ap>/<body>/<tran>/<rcd>/<card_flag > 卡/折标志 C1 Y 卡/折标志 0：卡 1：存折
			res.append("<card_flag>").append(o.getCardFlag()).append("</card_flag>");
			// <ap>/<body>/<tran>/<rcd>/<amt> 金额 N14 .2 Y
			res.append("<amt>").append(Ryt.div100(o.getTransAmt())).append("</amt>");
			// <ap>/<body>/<tran>/<rcd>/<busino> 业务编号 C20
			res.append("<busino></busino>");
			res.append("</rcd>");
			res.append("</tran>");
			
			
			
		}else if (tr_code.equals("310204")){
			
			res.append("<query_flag>1</query_flag>");
			res.append("<ogl_serial_no>").append(o.getOid()).append("</ogl_serial_no>");
			
		} else {
			throw new B2EException("错误的交易码");
		}
		
		res.append("</body>");
	}
	
	public static void main(String args[]){
		BankcommXML bank  = new BankcommXML();
		String xmlString = "<ap><head><tr_code>210224</tr_code><corp_no> </corp_no><req_no>1353376432613143562</req_no><serial_no>98269067</serial_no><ans_no> </ans_no><next_no> </next_no><tr_acdt>20121120</tr_acdt><tr_time>095648</tr_time><ans_code>0</ans_code><ans_info> </ans_info><particular_code>0000</particular_code><particular_info> </particular_info><atom_tr_count> </atom_tr_count><reserved> </reserved></head><body><tranStatus>21</tranStatus></body></ap>";
		B2ERet result = new B2ERet();
		try {
			bank.parseXML(result, xmlString);
		} catch (B2EException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * err_code == 0 表示解析成功，否则解析失败 err_msg 解析错误信息
	 * @param xmlString
	 * @return B2ERet
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void parseXML(B2ERet result,String xmlString) throws B2EException {

		if(null==result) result = new B2ERet();
		result.setGid(B2ETrCode.BOCOMM_GID);
		
		if(isEmpty(xmlString)){
			result.setErr("XML错误");
			return;
		} 
		
		try {
			Document doc = DocumentHelper.parseText(xmlString.trim());
			Element root = doc.getRootElement();
			Element head = root.element("head");
			
			if (head == null) {
				result.setErr("XML没有head节点");
				return  ;
			}

			Element trCode = head.element("tr_code");
			if (trCode == null) {
				result.setErr("XML没有tr_code节点");
				return  ;
			}

			String tr_code = trCode.getText();
			if (tr_code == null) {
				result.setErr("tr_code值错误");
				return  ;
			}

			result.setTrCode(tr_code);
			// particular_code 0000：交易  成功  其他：4 位  返回码
			Element particularCode = head.element("particular_code");
			String particular_code = particularCode.getText();
			Element particularInfo = head.element("particular_info");
			String particular_info = particularInfo.getText();
			result.setMsg(particular_info);
			Element body = root.element("body");
			result.setSucc(B2ETrCode.BOCOMM_SC_OK.equals(particular_code));
			Element bankDate=head.element("tr_acdt");//银行响应日期
			Element bankTime=head.element("tr_time");//银行响应时间
			result.setBank_date(bankDate.getText());
			result.setBank_time(bankTime.getText());
			result.setRes_code(particular_code);
			result.setErrorMsg(particularInfo.getText());
			
			if (body == null){
				LogUtil.printErrorLog("BankcommXML", "parseXML", "返回报文没有Body");
				String errorMsg=head.element("ans_info").getTextTrim();
				result.setErr(errorMsg);
				return  ;
			}

			// 查询余额
			if ("310101".equals(tr_code)) {

				Element serialRecord = body.element("serial_record");

				if (serialRecord == null) {
					result.setErr("body没有serial_record节点");
					return  ;
				}
				String serial_record = serialRecord.getText();
				if (isEmpty(serial_record)) {
					result.setErr("serial_record节点没有值");
					return  ;
				}
				// //户名(C60)|账 号(C32)|币种 (C3)| 余额 (N14.2)|可用 余额(N14.2)| 开户日期
				// (C8)| 账户类 型(C1)| 开户 行(C60)|错误 信息(C60)| 成功标志 (C1)|
				String[] r = serial_record.split("\\|");
				if (r.length < 20) {
					result.setErr("serial_record节点值有错");
					return  ;
				}

				result.setSucc(r[19].equals("0"));
				result.setMsg(r[18]);
				// 可用 余额
				result.setResult(r[14]);

				return  ;
			}
			// 转账交易结果查询（310204）
			if (tr_code.equals("310204")) {

				String ogl_seria = body.elementText("ogl_serial_no");
				String stat = body.elementText("stat");

				if (isEmpty(stat)) {
					result.setErr("没有stat节点");
					return  ;
				}
				String errMsg = body.elementText("err_msg");
				result.setMsg(errMsg);

				int state = 0;
				// 0 银行处理 中1 成功2 失败3 可疑4 记录不存 在
				//4记录不存在 为失败交易！
				if (stat.equals("1")) {
					state = DaiFuTransState.PAY_SUCCESS;
				} else if (stat.equals("2")) {
					state=DaiFuTransState.PAY_FAILURE;
				}else if(stat.equals("4")){
					state=DaiFuTransState.PAY_FAILURE;
				} else if(stat.equals("3")) {
//					result.setErr("状态未知");
					state=DaiFuTransState.PAY_PROCESSING;
				}else {
					result.setErr("状态未知");
					return;
				}
				result.setSucc(true);

				B2ETradeResult b2e = new B2ETradeResult("",ogl_seria, state,errMsg);
				b2e.setAmt(body.elementText("amt"));

				result.setResult(b2e);
				return  ;
			}

			// 对公转对私（330002）
			if (tr_code.equals("330002")) {

				Element tran = body.element("tran");
				if (null == tran) {
					result.setErr("xml没有tran节点");
					return  ;
				}
				List<Element> rcdList = tran.elements();
				if (null == rcdList) {
					result.setErr("xml没有rcd节点");
					return  ;
				}
				Element rcd = rcdList.get(0);

				if (null == rcd) {
					result.setErr("xml没有rcd节点");
					return  ;
				}
				// for(Element rcd : rcdList){
				// sucFlg F 成功 E 失败 3 可疑
				String sucFlg = rcd.elementText("sucFlg");
				
				log4j.info("sucFlag:"+sucFlg+"<<<<");
				
				result.setSucc("F".equals(sucFlg));
				if(!"F".equals(sucFlg))
					result.setTransStatus(1);
				// flw
				String flw = rcd.elementText("flw");
				result.setResult(flw);
				return  ;
			}
			// 210224 跨行转账
			if(tr_code.equals("210224")){ 
				String tranStatus = body.element("tranStatus").getTextTrim();
				String reqNo = head.element("req_no").getTextTrim();
				if(tranStatus==null || tranStatus.equals("")){
//					result.setSucc(false);
//					result.setTransStatus(1); 
//					result.setStatusInfo("交易失败");
					String ansInfo=head.element("ans_info").getTextTrim();
					String errorMsg="";
					if(ansInfo.equals("")){
						errorMsg=particular_info;
					}else if(particular_info.equals("")){
						errorMsg=ansInfo;
					}
					result.setErr(errorMsg);
				}else if("20".equals(tranStatus)){
					LogUtil.printInfoLog("BankcommXML", "parseXML", "订单["+reqNo+"]付款结果：交易可疑");
					Element serialNo=head.element("serial_no");//银行流水
					result.setResult(serialNo.getText());
					result.setSucc(true);
					result.setTransStatus(2); // 交易结果: 0 成功；1 失败；2 中间状态（需要通过查询接口来确认交易结果）
					result.setStatusInfo("交易可疑");
				} else if("21".equals(tranStatus)){
					LogUtil.printInfoLog("BankcommXML", "parseXML", "订单["+reqNo+"]付款结果：交易成功");
					result.setSucc(true);
					Element serialNo=head.element("serial_no");//银行流水
					result.setResult(serialNo.getText());
					result.setTransStatus(0);
					result.setStatusInfo("交易成功");
				} else if("22".equals(tranStatus)){
					LogUtil.printInfoLog("BankcommXML", "parseXML", "订单["+reqNo+"]付款结果：交易失败");
					result.setSucc(false);
					result.setTransStatus(1);
					result.setStatusInfo("交易失败");
				} else if("34".equals(tranStatus)){
					LogUtil.printInfoLog("BankcommXML", "parseXML", "订单["+reqNo+"]付款结果：转账申请已受理，正在处理中");
					Element serialNo=head.element("serial_no");//银行流水
					result.setResult(serialNo.getText());
					result.setSucc(true);
					result.setTransStatus(2);
					result.setStatusInfo("转账申请已受理，正在处理中");
				} else {
					LogUtil.printInfoLog("BankcommXML", "parseXML", "订单["+reqNo+"]付款结果：未知状态");
					Element serialNo=head.element("serial_no");//银行流水
					result.setResult(serialNo.getText());
					result.setSucc(true);
					result.setTransStatus(2);
					result.setStatusInfo("未知状态");
				}
				
				return;
				
			} 
			//其他情况
			result.setErr("错的交易码");			
			return  ;
			
		} catch (Exception e) {
			result.setErr("XML解析失败");
			e.printStackTrace();
			return  ;
		}

	}

}
