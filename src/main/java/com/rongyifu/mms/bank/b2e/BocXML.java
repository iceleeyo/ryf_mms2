package com.rongyifu.mms.bank.b2e;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

public class BocXML implements BankXML {

	private final static String furinfo = "EH";
	private final static String pybcur = "001";
	
	@Override
	public String genSubmitXML(int code, B2EGate gate) throws B2EException {

		StringBuffer res = null;
		// 签到
		if (code == B2ETrCode.SIGN) {
			res = genSubHead(gate, "b2e0001");
			res.append("<trans>");
			res.append("<trn-b2e0001-rq>");
			res.append("<b2e0001-rq>");
			res.append("<custdt>").append(DateUtil.getIntDateTime()).append("</custdt>");
			res.append("<oprpwd>").append(gate.getUserPwd()).append("</oprpwd>");
			res.append("</b2e0001-rq>");
			res.append("</trn-b2e0001-rq>");
			res.append("</trans>");
		}
		// 当日余额查询(b2e0005)
		else if (code == B2ETrCode.QUERY_ACC_BALANCE) {
			res = genSubHead(gate, "b2e0005");
			res.append("<trans>");
			res.append("<trn-b2e0005-rq><b2e0005-rq><account>");
			res.append("<ibknum>").append(gate.getBkNo()).append("</ibknum>");
			res.append("<actacn>").append(gate.getAccNo()).append("</actacn>");
			res.append("</account></b2e0005-rq></trn-b2e0005-rq>");
			res.append("</trans>");
		} 
		
		
		
		//...
		else {
			throw new B2EException("交易码错误");
		}

		res.append("</bocb2e>");

		return res.toString();
	}

	@Override
	public void parseXML(B2ERet result,String resData) {
		if(result==null) result = new B2ERet();
		result.setGid(B2ETrCode.BOC_GID);
		
		try {

			Document doc = DocumentHelper.parseText(resData);
			Element root = doc.getRootElement();
			Element head = root.element("head");
			
			if(null==head){
				result.setErr("解析xml失败，没有head节点");
				return  ;
			}
//			res.put("termid", head.elementText("termid"));
//			res.put("trnid",  head.elementText("trnid"));
//			res.put("custid", head.elementText("custid"));
//			res.put("cusopr", head.elementText("cusopr"));
			String trncod = head.elementText("trncod");
			if (trncod == null){
				result.setErr("解析xml失败，trncod为空");
				return  ;
			}
			
			result.setTrCode(trncod);

			Element trans = root.element("trans");
			
			if (trans == null){
				result.setErr("解析xml失败，没有trans节点");
				return  ;
			}
			String eName = "trn-" + trncod + "-rs";
			Element rs = trans.element(eName);
			
			if (rs == null){
				result.setErr("解析xml失败，没有"+eName+"节点");
				return  ;
			}
			Element status = rs.element("status");
			String rspcod = status.elementText("rspcod");
			//未签到，调用签到
			if ("1033".equals(rspcod) || "B107".equals(rspcod)){
				LogUtil.printInfoLog("BocXML", "parseXML", "前置机未签到,自动发起签到");
				result.setSucc(false);
				result.setTransStatus(1);//支付失败
				result.setMsg("前置未向服务器签到");
				new DaifuAutoRun().sign();
				return;
			}
			// 4.1签到交易(b2e0001)
			if ("b2e0001".equals(trncod)) {
				String msg = status.elementText("rspmsg");
				String token = rs.elementText("token");
				String serverdt = rs.elementText("serverdt");
				result.setSucc(B2ETrCode.BOC_SC_OK.equals(rspcod));
				result.setResult(new B2ESignResult(token,serverdt.substring(0,8)));
				result.setMsg(msg);
				return  ;
			}
			// 4.5当日余额查询(b2e0005)
			if ("b2e0005".equals(trncod)) {
				if (!B2ETrCode.BOC_SC_OK.equals(rspcod)) {
					result.setErr(status.elementText("rspmsg"));
					return  ;
				}

				Element b2e0005 = rs.element("b2e0005-rs");
				Element status2 = b2e0005.element("status");

				String code = status2.elementText("rspcod");
				
				result.setSucc(B2ETrCode.BOC_SC_OK.equals(code));
				result.setMsg(status2.elementText("rspmsg"));
				Element balance = b2e0005.element("balance");
				
				//可用余额
				result.setResult(balance.elementText("avabal"));
				return  ;

			}
			// 交易状态查询(b2e0007)
			if ("b2e0007".equals(trncod)) {
				if (!B2ETrCode.BOC_SC_OK.equals(rspcod)) {
					result.setErr(status.elementText("rspmsg"));
					return  ;
				}else
					result.setSucc(true);
				
				Element b2e0007 = rs.element("b2e0007-rs");
				Element status2 = b2e0007.element("status");
				
				String code = status2.elementText("rspcod");
				String msg = status2.elementText("rspmsg");
				
				result.setMsg(msg);
				String obssid = b2e0007.elementText("obssid");
				String insid = b2e0007.elementText("insid");
				int ordState=DaiFuTransState.PAY_PROCESSING;
				if ("B059".equals(code)||"B054".equals(code)||"B266".equals(code))
					ordState=DaiFuTransState.PAY_PROCESSING;
				else if(B2ETrCode.BOC_SC_OK.equals(code)){
					ordState=B2ETrCode.PAY_SUCC;
				}else if(!B2ETrCode.BOC_SC_OK.equals(code)){
					ordState=B2ETrCode.PAY_Fail;
				}
				 result.setResult(new B2ETradeResult(insid,obssid,ordState,msg));
				
				return  ;
			}

			// 4.9转账汇款(b2e0009)
			if ("b2e0009".equals(trncod)) {
				if (!B2ETrCode.BOC_SC_OK.equals(rspcod)) {
					result.setErr(status.elementText("rspmsg"));
					LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+rs.element("b2e0009-rs").elementText("obssid")+"转账汇款失败！");
					return  ;
				}
				Element b2e0009 = rs.element("b2e0009-rs");
				result.setTransStatus(2);
				result.setSucc(true);
				result.setMsg("");
				result.setResult(b2e0009.elementText("obssid"));
				LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+b2e0009.elementText("obssid")+"提交成功！");
				return  ;

			}
			// 对私转账汇款(b2e0061)
			if ("b2e0061".equals(trncod)) {
				if (!B2ETrCode.BOC_SC_OK.equals(rspcod)) {
					result.setErr(status.elementText("rspmsg"));
					LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+rs.element("b2e0061-rs").elementText("obssid")+"对私转账汇款失败！");
					return  ;
				}
				Element b2e0061 = rs.element("b2e0061-rs");
				Element status2 = b2e0061.element("status");

				String rmsg = status2.elementText("rspmsg");
//				result.setSucc(B2ETrCode.BOC_SC_OK.equals(code));
				result.setSucc(true);
				result.setMsg(rmsg);
				result.setTransStatus(2);
				result.setResult(b2e0061.elementText("obssid"));
				LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+b2e0061.elementText("obssid")+"对私转账汇款提交成功！");
				return  ;

			} 
			// 快捷代发业务(b2e0078)
			if ("b2e0078".equals(trncod)) {
				if (!B2ETrCode.BOC_SC_OK.equals(rspcod)) {
					result.setErr(status.elementText("rspmsg"));
					LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+rs.element("b2e0078-rs").elementText("obssid")+"快捷代发失败！");
					return  ;
				}
				Element b2e0078 = rs.element("b2e0078-rs");
				Element status2 = b2e0078.element("status");

				String code = status2.elementText("rspcod");
				String rmsg = status2.elementText("rspmsg");
				result.setSucc(true);
				result.setMsg(rmsg);
				result.setTransStatus(2);
				result.setResult(b2e0078.elementText("obssid"));
				LogUtil.printInfoLog("BocXml", "parseXML", "订单号:"+b2e0078.elementText("obssid")+"快捷代发成功！");
				return  ;

			} 
			//没有的交易代码
			result.setErr("错误的交易码");
			return  ;
		
		} catch (Exception e) {
			
			result.setErr(e.getMessage());
			return  ;
		
		}
	}

	@Override
	public String genSubmitXML(int code, TrOrders os, B2EGate gate) throws B2EException {
		
		if (os == null)
			throw new B2EException("订单为空");
		
		StringBuffer res = null;
//		if (code==B2ETrCode.DAI_FA) {
		if(code==B2ETrCode.QUERY_ORDER_STATE){
			
			res = genSubHead(gate, "b2e0007");
			res.append("<trans>");
			res.append("<trn-b2e0007-rq><b2e0007-rq>");
			res.append("<insid>").append(os.getOid()).append("</insid>");
			res.append("<obssid></obssid>");
			res.append("</b2e0007-rq></trn-b2e0007-rq>");
			
		}
//		else if(code==B2ETrCode.PAY_TO_OTHER){
		else if(os.getPtype()==5){
			res = genSubHead(gate, "b2e0009");
			res.append("<trans>");
			b2e0009body(res, gate, os);
			
		}
		else if (os.getPtype()==7) {	
			res = genSubHead(gate, "b2e0078");
			res.append("<trans>");
			b2e0078body(res, gate, os);
		
		} 
		else {
			throw new B2EException("交易码(tr_code)为空");
		}
		res.append("</trans>");
		res.append("</bocb2e>");
		return res.toString();
	}

	private StringBuffer genSubHead(B2EGate gate, String trCode) throws B2EException {

		String custid = gate.getCorpNo();
		String cusopr = gate.getUserNo();
		String token = gate.getToken();

		if (isEmpty(custid))
			throw new B2EException("custid为空");
		if (isEmpty(cusopr))
			throw new B2EException("cusopr为空");
		if (isEmpty(trCode))
			throw new B2EException("trCode为空");

		StringBuffer res = new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		res.append("<bocb2e  version=\"120\" security=\"true\" locale=\"zh_CN\">");
		// BOCB2E消息包含消息头(head)和消息体(trans)。
		res.append("<head>");

		res.append("<trnid>").append(GenB2ETrnid.getTrace()).append("</trnid>");
		res.append("<custid>").append(custid).append("</custid>");
		res.append("<cusopr>").append(cusopr).append("</cusopr>");
		res.append("<trncod>").append(trCode).append("</trncod>");
		// <token> 交易验证标识，签到时生成、签退时注销Base64字符串0-64位 检查令牌是否正确
		if (trCode.equals("b2e0001")) {// 签到为空
			// res.append("<token></token>");
		} else if (isEmpty(token)) {
			throw new B2EException("token为空");
		} else {
			res.append("<token>").append(token).append("</token>");
		}
		res.append("</head>");

		return res;

	}

	private void b2e0009body(StringBuffer res, B2EGate gate, TrOrders o)
			throws B2EException {

		res.append("<trn-b2e0009-rq>");
		res.append("<transtype></transtype>");
		res.append("<b2e0009-rq>");
		String insid = o.getOid();
		res.append("<insid>").append(insid).append("</insid>");
		String obssid = "";
		res.append("<obssid>").append(obssid).append("</obssid>");

		// 付款人账户<fractn>
		res.append("<fractn>");
		// <fribkn> 联行号 非空数码5位 1、非空数码5位 2、联行号有对应的省行联行号
		String fribkn = gate.getBkNo();
		res.append("<fribkn>").append(fribkn).append("</fribkn>");
		String actacn = gate.getAccNo();
		res.append("<actacn>").append(actacn).append("</actacn>");
		// <actnam> 付款人名称 字符串0-70位
		String actnam = gate.getAccName();
		res.append("<actnam>").append(actnam).append("</actnam>");
		res.append("</fractn>");

		// <toactn> 收款人账户信息
		res.append("<toactn>");
		// <toibkn> 收款行联行号 非空数码5位 1、非空数码5位 2、联行号有对应的省行联行号
		String toibkn = o.getToBkNo();
		res.append("<toibkn>").append(toibkn).append("</toibkn>");
		// <actacn> 收款账号 非空数码1-35位 1、非空数码12-18位2、检查操作员有无转账权限, 判断账户是否存在
		
		String actacn2 = o.getToAccNo();
		res.append("<actacn>").append(actacn2).append("</actacn>");

		// <toname> 收款人名称 非空字符串 1-70位
		String toname = o.getToAccName();
		res.append("<toname>").append(toname).append("</toname>");

		// <toaddr> 收款人地址 字符串 0-70位
		String toaddr = "";
		res.append("<toaddr>").append(toaddr).append("</toaddr>");
		// <tobknm> 收款人开户行名称 字符串0-70位
		String tobknm = o.getToBkName();
		res.append("<tobknm>").append(tobknm).append("</tobknm>");
		res.append("</toactn>");

		// <trnamt> 转账金额 非空正数 22.2位，最多两位小数 1、非空正数
		// 15.2位，最多两位小数2、根据交易币种的辅币位数检查金额的格式；不超过转账限额
		String trnamt = Ryt.div100(o.getTransAmt());
		res.append("<trnamt>").append(trnamt).append("</trnamt>");

		// <trncur> 转账货币 非空3位大写字母、数字 只支持001和CNY
		String trncur = "CNY";
		res.append("<trncur>").append(trncur).append("</trncur>");

		// <priolv> 报文发送优先级 非空字母数字1-2位 非空枚举，0-普通；1-加急
		res.append("<priolv>1</priolv>");

		// <furinfo> 用途 字符串 长度0-80
		String furinfo = o.getPriv();
		res.append("<furinfo>").append(furinfo).append("</furinfo>");

		// <trfdate> 要求的转账日期 格式YYYYMMDD 系统当前日期（含当日）之后的一个月内
		String trfdate = getBOCdate(gate.getBusiNo());
		//String trfdate = DateUtil.today() + "";
		res.append("<trfdate>").append(trfdate).append("</trfdate>");

		// <comacn> 手续费账号 数码0或者1-35位 1、如果不为空则数码12-18位
		// 2、账户已在网银维护；操作员有权限；如果为空则使用付款账户代替3、与付款账号同省
		String comacn = gate.getAccNo();
		res.append("<comacn>").append(comacn).append("</comacn>");
		res.append("</b2e0009-rq>");
		res.append("</trn-b2e0009-rq>");

	}
 
	/**
	 * 快捷代发业务(b2e0078)
	 * 
	 * @param res
	 * @param gate
	 * @param p
	 * @throws B2EException
	 */
	private void b2e0078body(StringBuffer res, B2EGate gate, TrOrders o)
			throws B2EException {
		String insid = o.getOid();

		String fribkn = gate.getBkNo();// map.get("fribkn"); 联行号 非空数码5位 1、非空数码5位
		// 2、联行号有对应的省行联行号
		String actacn = gate.getAccNo();// map.get("actacn");//付款账号 非空数码1-35位
		// 1、非空数码12-18位2、账户已在网银维护，操作员有权限
		String actnam = gate.getAccName();// 付款人名称 可空字符串 长度0-70

		String pybamt = Ryt.div100(o.getTransAmt());
		String pybnum = "1";
		String crdtyp = o.getToBkNo().trim().length()<=5 ? "7" : "6";// <crdtyp>
		String useinf = o.getPriv();
		String trfdate =DateUtil.today() + ""; 
		String toibkn = "";
		if (crdtyp.equals("7")) {
			toibkn = o.getToProvId().toString().substring(0,2).trim();//本行使用2位省份号
		} else {
			toibkn = o.getToBkNo();//他行使用12位联行号
		}
		String toname = o.getToAccName();
		String toactn = o.getToAccNo();
		String pydamt = pybamt;

		res.append("<trn-b2e0078-rq>");
		res.append("<b2e0078-rq>");
		res.append("<insid>").append(insid).append("</insid>");
		res.append("<fractn>");
		res.append("<fribkn>").append(fribkn).append("</fribkn>");
		res.append("<actacn>").append(actacn).append("</actacn>");
		res.append("<actnam>").append(actnam).append("</actnam>");
		res.append("</fractn>");

		res.append("<pybcur>").append("CNY").append("</pybcur>");
		res.append("<pybamt>").append(pybamt).append("</pybamt>");
		res.append("<pybnum>").append(pybnum).append("</pybnum>");
		res.append("<crdtyp>").append(crdtyp).append("</crdtyp>");
		res.append("<furinfo>").append(furinfo).append("</furinfo>");
		res.append("<useinf>").append(useinf).append("</useinf>");
		res.append("<trfdate>").append(trfdate).append("</trfdate>");

		res.append("<detail>");
		res.append("<toibkn>").append(toibkn).append("</toibkn>");
		res.append("<tobank></tobank>");
		res.append("<toactn>").append(toactn).append("</toactn>");
		res.append("<pydcur>").append("CNY").append("</pydcur>");
		res.append("<pydamt>").append(pydamt).append("</pydamt>");
		res.append("<toname>").append(toname).append("</toname>");
		res.append("<toidtp></toidtp>");
		res.append("<toidet></toidet>");
		res.append("<furinfo>").append(useinf).append("</furinfo>");
		res.append("</detail>");

		res.append("</b2e0078-rq>");
		res.append("</trn-b2e0078-rq>");
	}

	private void b2e0061body(StringBuffer res, B2EGate gate, TrOrders o)
			throws B2EException {
		String insid = o.getOid();
		String bocflag="1";
		String fribkn = gate.getBkNo();//联行号 非空数码5位 1、非空数码5位
		String actacn = gate.getAccNo();//付款账号 
		String actnam = gate.getAccName();// 付款人名称 
		String cnaps=o.getToBkNo();//收款联行号
		String pybamt = Ryt.div100(o.getTransAmt());
		String useinf = o.getPriv();
		String toname = o.getToAccName();
		String toactn = o.getToAccNo();
		Integer skBkNo=o.getGate();//收款银行
		if (skBkNo!=40001)
			bocflag="0";
		res.append("<trn-b2e0061-rq>");
		res.append("<b2e0061-rq>");
		res.append("<insid>").append(insid).append("</insid>");
		res.append("<fractn>");
		res.append("<fribkn>").append(fribkn).append("</fribkn>");
		res.append("<actacn>").append(actacn).append("</actacn>");
		res.append("<actnam>").append(actnam).append("</actnam>");
		res.append("</fractn>");
		
		res.append("<toactn>");
		res.append("<toibkn>").append(cnaps).append("</toibkn>");//收款联行号
		res.append("<acttyp>").append("119").append("</acttyp>");//119:借记卡 101:普活活期 188:活一本
		res.append("<actacn>").append(toactn).append("</actacn>");//收款人帐号
		res.append("<toname>").append(toname).append("</toname>");//收款人名
		res.append("<tobknm>").append("</tobknm>");//收款人开户行名称
		res.append("<toaddr>").append("中国").append("</toaddr>");
		res.append("</toactn>");

		res.append("<trnamt>").append(pybamt).append("</trnamt>");
		res.append("<trncur>").append("CNY").append("</trncur>");
		res.append("<priolv>").append("1").append("</priolv>");
		res.append("<cuspriolv>").append("1").append("</cuspriolv>");
		res.append("<furinfo>").append(useinf).append("</furinfo>");
		res.append("<trfdate>").append(DateUtil.today()).append("</trfdate>");
		res.append("<comacn>").append(actacn).append("</comacn>");
		res.append("<bocflag>").append(bocflag).append("</bocflag>");
	 
		res.append("</b2e0061-rq>");
		res.append("</trn-b2e0061-rq>");
	}

	
	
	private String getToibkn(String priv) {
		int i = Integer.parseInt(priv);
		return String.valueOf(i / 10);
	}


	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}
	
	private String getBOCdate(String busino){
		//生产环境
//		if(ParamCache.getIntParamByName("B2EBOCDATE")==0){
			return DateUtil.today() + "";
//		}
		//测试环境
//		else{
//			return busino;
//		}
	}
}
