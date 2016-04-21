package com.rongyifu.mms.bank.b2e;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.service.ProvAreaService;

public class ICBCXML implements BankXML {
	@Override
	public String genSubmitXML(int trCode, B2EGate gate) throws B2EException {
		StringBuffer res = null;
		// 查询余额
		if (trCode == B2ETrCode.QUERY_ACC_BALANCE) {
			String str=String.valueOf(Ryt.crateBatchNumber());
			String fSeqno=new SimpleDateFormat("yyyyMMdd").format(new Date())+Ryt.createRandomStr(7);
			String sysTime = new SimpleDateFormat("HHmmssSSS").format(new Date());
			res = genSubHead("QACCBAL",gate.getCorpNo(),gate.getBkNo(),gate.getUserNo(),sysTime,fSeqno);
			res.append("<in>");
			res.append("<TotalNum>1</TotalNum>");// 总笔数
			res.append("<ReqReserved1></ReqReserved1>");// 请求包备用字段1
			res.append("<ReqReserved2></ReqReserved2>");// 请求包备用字段2
			res.append("<rd>");
			res.append("<iSeqno>").append(str).append("</iSeqno>");// 指令顺序号
			res.append("<AccNo>").append(gate.getAccNo()).append("</AccNo>");// 账号
			res.append("<CurrType>001</CurrType>");// 币种
			res.append("<ReqReserved3></ReqReserved3>");// 请求包备用字段3
			res.append("<ReqReserved4></ReqReserved4>");// 请求包备用字段4
			res.append("</rd>");
			res.append("</in>");
		}
		res.append("</eb>");
		res.append("</CMS>");
		return res.toString();
	}

	@Override
	public String genSubmitXML(int trCode, TrOrders os, B2EGate gate)
			throws B2EException {
		int index=os.getOid().length();
		String fSeqno=os.getSysDate()+os.getOid().substring(index-7,index);
		StringBuffer res = null;
		/**
		 *  查询交易信息指令（对私和对公）
		 */
		if (trCode == B2ETrCode.QUERY_ORDER_STATE) {
				String sysTime = new SimpleDateFormat("HHmmssSSS").format(new Date())+"000";
				res = genSubHead("QPAYENT",gate.getCorpNo(),gate.getBkNo(),gate.getUserNo(),sysTime,fSeqno);
				res.append("<in>");
				res.append("<QryfSeqno>").append(fSeqno).append("</QryfSeqno>");// 待查指令包序列号
				res.append("<QrySerialNo>").append(os.getTseq()).append("</QrySerialNo>");// 待查平台交易序列号
				res.append("<ReqReserved1></ReqReserved1>");// 请求备用字段1
				res.append("<ReqReserved2></ReqReserved2>");// 请求备用字段2
				res.append("<rd>");
				res.append("<iSeqno>").append(os.getOid()).append("</iSeqno>");// 指令包顺序号
				res.append("<QryiSeqno>").append(os.getOid()).append("</QryiSeqno>");// 待查指令包顺序号
				res.append("<QryOrderNo>1</QryOrderNo>");// 待查平台交易顺序号
				res.append("<ReqReserved3></ReqReserved3>");// 请求备用字段3
				res.append("<ReqReserved4></ReqReserved4>");// 请求备用字段4
				res.append("</rd>");
				res.append("</in>");
		}

		/**
		 * 支付指令提交
		 */
		if (trCode == B2ETrCode.PAY_TO_OTHER) {
			int isSamebank;		//本行跨行
			int isSameCity;		//同城异地
			int enterpriseOrPerson=1;//对私
			String DbProv="";	//省份城市
			if(os.getToProvId()==310){
				isSameCity=1;	//同城
			}else{				
				isSameCity=2;	//异地
			}
				if(os.getPtype()==Constant.DaiFuTransType.PAY_TO_ENTERPRISE){
					enterpriseOrPerson=1;
				}else{
				}
				String bg=(os.getToBkNo()!=null && os.getToBkNo().length()>3)?os.getToBkNo().substring(0, 3):os.getToBkNo();
				if(bg.equals("102")){//本行
					isSamebank=1;
				}else{//跨行
					isSamebank=2;
					ProvAreaService service=new ProvAreaService();
					DbProv=service.queryProv(os.getToProvId());
				}
				String sysTime = new SimpleDateFormat("HHmmssSSS").format(new Date());
				res = genSubHead("PAYENT",gate.getCorpNo(),gate.getBkNo(),gate.getUserNo(),sysTime,fSeqno);
				res.append("<in>");
				res.append("<OnlBatF>1</OnlBatF>");// 联机批量标志
				res.append("<SettleMode>0</SettleMode>");// 入账方式
				res.append("<TotalNum>1</TotalNum>");// 总笔数
				res.append("<TotalAmt>").append(os.getPayAmt()).append("</TotalAmt>");// 总金额
				res.append("<SignTime>")
						.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())).append("</SignTime>");// 签名时间
				res.append("<ReqReserved1></ReqReserved1>");// 请求备用字段1
				res.append("<ReqReserved2></ReqReserved2>");// 请求备用字段2
				res.append("<rd>");
				res.append("<iSeqno>").append(os.getOid()).append("</iSeqno>");// 指令顺序号
				res.append("<ReimburseNo></ReimburseNo>");// 自定义序号
				res.append("<ReimburseNum></ReimburseNum>");// 单据张数
				res.append("<StartDate></StartDate>");// 定时启动日期
				res.append("<StartTime></StartTime>");// 定时启动时间
				res.append("<PayType>").append("1").append("</PayType>");// 记账处理方式(1加急2普通)
				res.append("<PayAccNo>").append(gate.getAccNo()).append("</PayAccNo>");// 本方账号
				res.append("<PayAccNameCN>").append(gate.getAccName()).append("</PayAccNameCN>");// 本方账户名称
				res.append("<PayAccNameEN></PayAccNameEN>");// 本方账户英文名称
				res.append("<RecAccNo>").append(os.getToAccNo()).append("</RecAccNo>");// 对方账号
				res.append("<RecAccNameCN>").append(os.getToAccName()).append("</RecAccNameCN>");// 对方账户名称
				res.append("<RecAccNameEN></RecAccNameEN>");// 对方账户英文名称
				res.append("<SysIOFlg>").append(isSamebank).append("</SysIOFlg>");// 系统内外标志(1：系统内 2：系统外)
				res.append("<IsSameCity>").append(isSameCity).append("</IsSameCity>");// 同城异地标志(1:同城 2:异地)
				res.append("<Prop>").append(enterpriseOrPerson).append("</Prop>");//对公对私标志(跨行必输   0：对公1：个人)
				res.append("<RecICBCCode></RecICBCCode>");// 交易对方工行地区号
				res.append("<RecCityName>").append(DbProv).append("</RecCityName>");// 收款方所在城市名称(跨行必输)
				res.append("<RecBankNo>").append(os.getToBkNo()).append("</RecBankNo>");// 对方行行号
				res.append("<RecBankName>").append(os.getToBkName()).append("</RecBankName>");// 交易对方银行名称
				res.append("<CurrType>001</CurrType>");// 币种
				res.append("<PayAmt>").append(os.getPayAmt()).append("</PayAmt>");// 金额(以分为单位，不带小数点)
				res.append("<UseCode></UseCode>");// 用途代码
				res.append("<UseCN>").append(os.getPriv()).append("</UseCN>");// 用途中文描述
				res.append("<EnSummary></EnSummary>");// 英文备注
				res.append("<PostScript></PostScript>");// 附言
				res.append("<Summary></Summary>");// 摘要
				res.append("<Ref></Ref>");// 业务编号（业务参考号）
				res.append("<Oref></Oref>");// 相关业务编号
				res.append("<ERPSqn></ERPSqn>");// ERP流水号
				res.append("<BusCode></BusCode>");// 业务代码
				res.append("<ERPcheckno></ERPcheckno>");// ERP支票号
				res.append("<CrvouhType></CrvouhType>");// 原始凭证种类
				res.append("<CrvouhName></CrvouhName>");// 原始凭证名称
				res.append("<CrvouhNo></CrvouhNo>");// 原始凭证号
				res.append("<ReqReserved3></ReqReserved3>");// 请求备用字段3
				res.append("<ReqReserved4></ReqReserved4>");// 请求备用字段4
				res.append("</rd>");
				res.append("</in>");
		}
			//String TransAmt=String.valueOf(os.getTransAmt()*100);
			/*if(os.getPtype()==Constant.DaiFuTransType.PAY_TO_PERSON){ 
				// 对私
				String bg=(os.getToBkNo()!=null && os.getToBkNo().length()>3)?os.getToBkNo().substring(0, 3):os.getToBkNo();
				if(bg.equals("102")){//本行
					isSamebank=1;
				}else{//跨行
					isSamebank=2;
					ProvAreaService service=new ProvAreaService();
					DbProv=service.queryProv(os.getToProvId());
				}
				String sysTime = new SimpleDateFormat("HHmmssSSS").format(new Date());
				res = genSubHead("PAYPER",sysTime);
				res.append("<in>");
				res.append("<OnlBatF>1</OnlBatF>");// 联机批量标志
				res.append("<SettleMode>0</SettleMode>");// 入账方式
				res.append("<TotalNum>1</TotalNum>");// 总笔数
				res.append("<TotalAmt>").append(os.getPayAmt()).append("</TotalAmt>");// 总金额
				res.append("<SignTime>")
						.append(new SimpleDateFormat("yyyyMMddHHmmssSSS")
								.format(new Date())).append("</SignTime>");// 签名时间
				res.append("<ReqReserved1></ReqReserved1>");// 请求备用字段1
				res.append("<ReqReserved2></ReqReserved2>");// 请求备用字段2
				res.append("<rd>");
				res.append("<iSeqno>").append(os.getOid()).append("</iSeqno>");// 指令顺序号
				res.append("<ReimburseNo></ReimburseNo>");// 自定义序号
				res.append("<ReimburseNum></ReimburseNum>");// 单据张数
				res.append("<StartDate></StartDate>");// 定时启动日期
				res.append("<StartTime></StartTime>");// 定时启动时间
				res.append("<PayType>").append("1").append("</PayType>");// 记账处理方式(1加急2普通)
				res.append("<PayAccNo>").append(gate.getAccNo())
						.append("</PayAccNo>");// 本方账号
				res.append("<PayAccNameCN></PayAccNameCN>");// 本方账户名称
				res.append("<PayAccNameEN></PayAccNameEN>");// 本方账户英文名称
				res.append("<RecAccNo>").append(os.getToAccNo()).append("</RecAccNo>");// 对方账号
				res.append("<RecAccNameCN></RecAccNameCN>");// 对方账户名称
				res.append("<RecAccNameEN></RecAccNameEN>");// 对方账户英文名称
				res.append("<SysIOFlg>").append(isSamebank).append("</SysIOFlg>");// 系统内外标志(1：系统内 2：系统外)
				res.append("<IsSameCity>").append(isSameCity).append("</IsSameCity>");// 同城异地标志(1:同城 2:异地)
				res.append("<RecICBCCode></RecICBCCode>");// 交易对方工行地区号
				res.append("<RecCityName>").append(DbProv).append("</RecCityName>");// 收款方所在城市名称(跨行必输)
				res.append("<RecBankNo></RecBankNo>");// 对方行行号
				res.append("<RecBankName></RecBankName>");// 交易对方银行名称
				res.append("<CurrType>001</CurrType>");// 币种
				res.append("<PayAmt>").append(os.getPayAmt()).append("</PayAmt>");// 金额(以分为单位，不带小数点)
				res.append("<UseCode></UseCode>");// 用途代码
				res.append("<UseCN>").append(os.getPriv()).append("</UseCN>");// 用途中文描述
				res.append("<EnSummary></EnSummary>");// 英文备注
				res.append("<PostScript></PostScript>");// 附言
				res.append("<Summary></Summary>");// 摘要
				res.append("<Ref></Ref>");// 业务编号（业务参考号）
				res.append("<Oref></Oref>");// 相关业务编号
				res.append("<ERPSqn></ERPSqn>");// ERP流水号
				res.append("<BusCode></BusCode>");// 业务代码
				res.append("<ERPcheckno></ERPcheckno>");// ERP支票号
				res.append("<CrvouhType></CrvouhType>");// 原始凭证种类
				res.append("<CrvouhName></CrvouhName>");// 原始凭证名称
				res.append("<CrvouhNo></CrvouhNo>");// 原始凭证号
				res.append("<ReqReserved3></ReqReserved3>");// 请求备用字段3
				res.append("<ReqReserved4></ReqReserved4>");// 请求备用字段4
				res.append("</rd>");
				res.append("</in>");
			}*/
		res.append("</eb>");
		res.append("</CMS>");
		return res.toString();
	}
	

	@Override
	public void parseXML(B2ERet ret, String xml) throws B2EException {
		if (ret == null)
			ret = new B2ERet();
		ret.setGid(B2ETrCode.ICBC_GID);// 银企直连网关号
		if (isEmpty(xml)) {
			ret.setErr("XML错误");// 错误信息
			return;
		}
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			Element eb = root.element("eb");
			if(eb==null){
				 ret.setErr("XML没有eb节点");
				 return ;
			}
			Element pub = eb.element("pub");
			if(pub==null){
				 ret.setErr("XML没有pub节点");
				 return ;
			}
			Element out = eb.element("out");
			if(out==null){
				 ret.setErr("XML没有out节点");
				 return ;
			}
			Element TransCode = pub.element("TransCode");//交易码
			if (TransCode == null) {
				ret.setErr("XML没有TransCode节点");
				return;
			}
			Element fSeqno=pub==null ? null:pub.element("fSeqno");//银行流水
			Element RetCode =pub==null ? null: pub.element("RetCode");// 交易返回码(0表示成功 非0表示失败)
			Element SerialNo=pub==null ? null:pub.element("SerialNo");//银行返回流水
			Element tranDate =pub==null ? null: pub.element("TranDate");// 银行返回的日期
			Element tranTime =pub==null ? null:pub.element("TranTime");// 银行返回的时间
			Element RetMsg=pub==null?null:pub.element("RetMsg");
			Element rd =out==null ? null: out.element("rd");
			/*if(rd==null){
				 ret.setErr("XML没有rd节点");
				 return ;
			}*/
			Element instrRetMsg = rd==null ? null : rd.element("instrRetMsg");//查询返回错误信息
			Element iRetMsg=rd==null ? null :rd.element("iRetMsg");//交易返回错误信息
			Element Oid=rd==null ? null :rd.element("QryiSeqno");//订单号
			Element Result = rd==null ? null : rd.element("Result");// 指令状态
			
			ret.setBank_date(tranDate.getText());
			ret.setBank_time(tranTime.getText());
			Element UsableBalance=rd==null ? null :rd.element("UsableBalance");//可用余额
			
			String transCode = TransCode.getText();
			if (transCode == null) {
				ret.setErr("TransCode错误！");
				return;
			}
			ret.setTrCode(transCode);// 银行返回的交易码
			/*if ("PAYPER".equals(transCode)) {//对私交易
				if ("0".equals(RetCode.getText())) {
					ret.setTransStatus(0);// 成功
				} else {
					ret.setErrorMsg(iRetMsg.getText());
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(state.getText());// 状态信息
					ret.setResult(iRetMsg.getText());
				}
			}*/	
			//ret.setSucc("0".equals(RetCode.getText()) ? true : false);// 交易成功标识
			if ("PAYENT".equals(transCode)) {// 交易
				if ("0".equals(RetCode.getText())) {
					if("7".equals(Result.getText().trim())){//处理成功
						ret.setTransStatus(0);// 成功
						ret.setSucc(true);
						ret.setResult(SerialNo.getText());
					}else if("6".equals(Result.getText().trim())||"8".equals(Result.getText().trim())){
						ret.setTransStatus(1);// 失败
						ret.setSucc(false);
						ret.setMsg(iRetMsg.getText());
						ret.setStatusInfo(SerialNo.getText());// 状态信息
					}else{
						ret.setTransStatus(2);//中间状态
						ret.setResult(SerialNo.getText());
						ret.setMsg(iRetMsg.getText());
					}
				} else {
					ret.setTransStatus(1);// 失败
					ret.setSucc(false);
					ret.setMsg(iRetMsg.getText());
					ret.setStatusInfo(SerialNo.getText());// 状态信息
				}
				return;
			}

			/*if ("QPAYPER".equals(transCode)) {//对私交易查询
				if ("0".equals(RetCode.getText())) {
					ret.setTransStatus(0);// 成功
				} else {
					ret.setErrorMsg(iRetMsg.getText());
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(state.getText());// 状态信息
					ret.setResult(iRetMsg.getText());
				}
			}*/
			
			if ("QPAYENT".equals(transCode)) {//交易查询
				 Integer state=null;
				if ("0".equals(RetCode.getText())) {
					if("7".equals(Result.getText().trim())){
						state=DaiFuTransState.PAY_SUCCESS;
						ret.setTransStatus(0);// 成功
					}else if("6".equals(Result.getText().trim())||"8".equals(Result.getText().trim())){
						state=DaiFuTransState.PAY_FAILURE;
						ret.setMsg(instrRetMsg==null?"":instrRetMsg.getText());
						ret.setTransStatus(1);// 失败
						ret.setStatusInfo(Result.getText());// 状态信息
					}else{
						state=DaiFuTransState.PAY_PROCESSING;
						ret.setMsg(instrRetMsg==null?"":instrRetMsg.getText());
						ret.setTransStatus(2); 
					}
				}else{
					state=DaiFuTransState.PAY_FAILURE;
					ret.setSucc(true);
					ret.setMsg(RetMsg==null?"":RetMsg.getText());
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(Result==null?null:Result.getText());// 状态信息
				}
				B2ETradeResult b2eResult = new B2ETradeResult(Oid==null?"":Oid.getText(),fSeqno.getText(), state,instrRetMsg==null?null:instrRetMsg.getText());
				ret.setResult(b2eResult);
				 return;
			}
			
			if ("QACCBAL".equals(transCode)) {	// 查询余额
				if ("0".equals(RetCode.getText())) {
					ret.setSucc(true);
					ret.setTransStatus(0);// 成功
					String tmpBalance=UsableBalance.getText();
					ret.setResult(Ryt.div100(tmpBalance));
				} else {
					ret.setSucc(false);
					ret.setErrorMsg(iRetMsg.getText());
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(Result.getText());// 状态信息
				}
				return;
			}
			

		} catch (DocumentException e) {
			e.printStackTrace();
			return;
		}

	}

	// 组装报文公共部分
	private StringBuffer genSubHead(String transCode,String cis,String bkNo,String Id,String sysTime,String fSeqno) {
		String sysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		StringBuffer res = new StringBuffer();
		res.append("<?xml version='1.0' encoding = 'GBK'?>");
		res.append("<CMS>");
		res.append("<eb>");
		res.append("<pub>");
		res.append("<TransCode>").append(transCode).append("</TransCode>");// 交易代码
		res.append("<CIS>").append(cis).append("</CIS>");// 集团CIS号
		res.append("<BankCode>").append(bkNo).append("</BankCode>");// 归属银行编号
		res.append("<ID>").append(Id).append("</ID>");// 证书ID
		res.append("<TranDate>").append(sysDate).append("</TranDate>");// 交易日期
		res.append("<TranTime>").append(sysTime).append("</TranTime>");// 交易时间
		res.append("<fSeqno>").append(fSeqno).append("</fSeqno>");// 指令包序列号
		res.append("</pub>");
		return res;
	}

	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}
	
}

