package com.rongyifu.mms.bank.b2e;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.exception.B2EException;

public class CmbXML implements BankXML {
	private final static int dattyp = 2;
	static Map<String, String> cmb_ryt_provmap=null;
	@Override
	public String genSubmitXML(int trCode, B2EGate gate) throws B2EException {
		// TODO Auto-generated method stub
		StringBuffer res = null;
		//查询账户详细信息
		if(trCode==B2ETrCode.QUERY_ACCOUNT||trCode==B2ETrCode.QUERY_ACC_BALANCE){
			res=genSubHead(gate, "GetAccInfo");
			res.append("<SDKACINFX>");
			res.append("<BBKNBR>").append(gate.getProvId()).append("</BBKNBR>");
			res.append("<C_BBKNBR>").append("</C_BBKNBR>");
			res.append("<ACCNBR>").append(gate.getAccNo()).append("</ACCNBR>");
			res.append("</SDKACINFX>");
		}
		//查询代发交易代码
		else if(trCode==B2ETrCode.QUERY_DAIFA_TRADE){
			res=genSubHead(gate, "QueryAgentList");
			res.append("<SDKAGTTSX>");
			res.append("<BUSCOD>").append("N03020").append("</BUSCOD>");
			res.append("</SDKAGTTSX>");	
		}
		//查询代扣的交易代码
		else if(trCode==B2ETrCode.QUERY_DAIKOU_TRADE){
			res=genSubHead(gate, "QueryAgentList");
			res.append("<SDKAGTTSX>");
			res.append("<BUSCOD>").append("N03030").append("</BUSCOD>");
			res.append("</SDKAGTTSX>");		
		}else{
			throw new B2EException("交易码错误");
		}
		res.append("</CMBSDKPGK>");
		return res.toString();
	}
//
	@Override
	public String genSubmitXML(int trCode, TrOrders os, B2EGate gate)
			throws B2EException {
		// TODO Auto-generated method stub
  
		StringBuffer res = null;
		//查询账户交易信息
		if(trCode==B2ETrCode.QUERY_ACCOUNT_TRADE){
		 res=genSubHead(gate, "GetTransInfo");
		 res.append("<SDKTSINFX>");
		 res.append("<BBKNBR>").append(gate.getProvId()).append("</BBKNBR>");
		 res.append("<C_BBKNBR>").append("</C_BBKNBR>");
		 res.append("<ACCNBR>").append(gate.getAccNo()).append("</ACCNBR>");
		 res.append("<BGNDAT>").append(os.getSysDate()).append("</BGNDAT>");
		 res.append("<ENDDAT>").append(os.getSysDate()).append("</ENDDAT>");
		 res.append("<LOWAMT>").append("</LOWAMT>");
		 res.append("<HGHAMT>").append("</HGHAMT>");
		 res.append("<AMTCDR>").append("</AMTCDR>");
		 res.append("</SDKTSINFX>"); 
		}
		 //直接代发代扣
		else if(trCode==B2ETrCode.DAI_FA||trCode==B2ETrCode.PAY_TO_OTHER||trCode==B2ETrCode.DAI_FA_GONGZI||trCode==B2ETrCode.DAI_KOU){
		   String buscod;
		   String busmod=null;
		   if(trCode==B2ETrCode.DAI_FA||trCode==B2ETrCode.PAY_TO_OTHER){
			   buscod="N03020";
			   busmod="00002";
		   }else if(trCode==B2ETrCode.DAI_FA_GONGZI){
			   buscod="N03010";
			   busmod="00002";
		   }else{
			   buscod="N03030";
		   }
		   String toaccno=os.getToAccNo();
		   String topriv=String.valueOf(gate.getProvId());
		   String sum = Ryt.div100(os.getTransAmt());
		   String priv=os.getPriv();
		   String accno=gate.getAccNo();
		   String yurref=os.getOid();
		   String toaccname=os.getToAccName();
		   String ctrstyp=gate.getBusiNo();
		 res=genSubHead(gate, "AgentRequest");
		 res.append("<SDKATSRQX>");
		 res.append("<BUSCOD>").append(buscod).append("</BUSCOD>");
		 res.append("<BUSMOD>").append(busmod).append("</BUSMOD>");
		 res.append("<MODALS>").append("</MODALS>");
		res.append("<C_TRSTYP>").append(ctrstyp).append("</C_TRSTYP>");
		 res.append("<EPTDAT>").append("</EPTDAT>");
		 res.append("<DBTACC>").append(accno).append("</DBTACC>");
		 res.append("<BBKNBR>").append(topriv).append("</BBKNBR>");
		 res.append("<BANKAREA>").append("</BANKAREA>");
		 res.append("<SUM>").append(sum).append("</SUM>");
		 res.append("<TOTAL>").append(1).append("</TOTAL>");
		 res.append("<CCYNBR>").append(10).append("</CCYNBR>");
		 res.append("<CURRENCY>").append("</CURRENCY>");
		 res.append("<YURREF>").append(yurref).append("</YURREF>");
		 res.append("<MEMO>").append(priv).append("</MEMO>");
		 res.append("</SDKATSRQX>");
		 res.append("<SDKATDRQX>");
		 res.append("<ACCNBR>").append(toaccno).append("</ACCNBR>");
		 res.append("<CLTNAM>").append(toaccname).append("</CLTNAM>");
		 res.append("<TRSAMT>").append(sum).append("</TRSAMT>");
		// Y:招行  N：她行
		 String bank_flag = gate.getBkNo().equals(os.getToBkNo()) ? "Y" : "N";
		 res.append("<BNKFLG>").append(bank_flag).append("</BNKFLG>");
		 String eacbnk="";
		 String trsdsp="";
		 if(bank_flag.equals("N")){
			  eacbnk=os.getToBkName();
			  trsdsp=String.valueOf(os.getToProvId());
		 }
		 res.append("<EACBNK>").append(eacbnk).append("</EACBNK>");
		 res.append("<EACCTY>").append(trsdsp).append("</EACCTY>");
		 res.append("<TRSDSP></TRSDSP>");
		 res.append("</SDKATDRQX>");	 
	   }
	  //查询交易概要信息	   
		else if(trCode==B2ETrCode.QUERY_TRADING_PROFILE||trCode==B2ETrCode.QUERY_ORDER_STATE){
		   res=genSubHead(gate, "GetAgentInfo");
		   res.append("<SDKATSQYX>");
		   res.append("<BUSCOD>").append("</BUSCOD>");
		   res.append("<BGNDAT>").append(os.getSysDate()).append("</BGNDAT>");
		   res.append("<ENDDAT>").append(os.getSysDate()).append("</ENDDAT>");
		   res.append("<DATFLG>").append("</DATFLG>");
		   res.append("<YURREF>").append(os.getOid()).append("</YURREF>");
		   res.append("<OPRLGN>").append("</OPRLGN>");
		   res.append("</SDKATSQYX>");
	   }
	 //查询账户的历史余额
		else if (trCode == B2ETrCode.QUERY_ACC_BALANCE) {
	 			res = genSubHead(gate, "SDKNTQABINF");
	 			res.append("<NTQABINFY>");
	 			res.append("<BBKNBR>").append(gate.getProvId()).append("</BBKNBR>");
	 			res.append("<ACCNBR>").append(gate.getAccNo()).append("</ACCNBR>");
	 			res.append("<BGNDAT>").append(os.getSysDate()).append("</BGNDAT>");
	 			res.append("<ENDDAT>").append(os.getSysDate()).append("</ENDDAT>");
	 			res.append("</NTQABINFY>");	
	 				}
	 //查询交易信息明细
		else if(trCode==B2ETrCode.QUERY_HISTORICAL_TRADE){
			res = genSubHead(gate, "GetAgentDetail");
			res.append("<SDKATDQYX>");
			res.append("<REQNBR>").append(os.getTseq()).append("</REQNBR>");
			res.append("</SDKATDQYX>");
		}
	   else{
			throw new B2EException("交易码错误");
		}	
		res.append("</CMBSDKPGK>");
		return res.toString();
	}
	@Override
	public  void parseXML(B2ERet result, String xml) throws B2EException {
		Element root=null;
		// TODO Auto-generated method stub
		if(result==null) result = new B2ERet();
		result.setGid(B2ETrCode.CMB_GID);
		 if(isEmpty(xml)){
			 result.setErr("XML错误");
				return;
		 }
		 try {	
			Document doc=DocumentHelper.parseText(xml);
			 root=doc.getRootElement();
		    Element info=root.element("INFO");
		   String errmsg=info.elementText("ERRMSG");
		   String funnam=info.elementText("FUNNAM");
		   if(null==funnam){
			   result.setErr("解析xml失败，FUNNAM为空");
				return  ;
		   }
		  String dattyp=info.elementText("DATTYP");
		   if(null==dattyp){
			   result.setErr("解析xml失败，DATTYP为空");
				return  ;
		   }
		   result.setTrCode(dattyp);
		   //查询账户的历史余额
		   if("SDKNTQABINF".equals(funnam)){
			   String retcod=info.elementText("RETCOD");
			   if(null==retcod){
				   result.setErr("解析xml失败，RETCOD为空");
					return  ;
			   } 
			   Element ntqabinfz=root.element("NTQABINFZ");
			   result.setSucc(B2ETrCode.CMB_SC_OK.equals(retcod));
			   result.setMsg(errmsg);
			   if(result.isSucc()){
			   result.setResult(ntqabinfz.elementText("BALAMT"));
			   }  
			  
		   }
		 //查看账户的详细信息 
		  if ("GetAccInfo".equals(funnam)) {
			  String retcod=info.elementText("RETCOD");
			   if(null==retcod){
				   result.setErr("解析xml失败，RETCOD为空");
					return  ;
			   }
			   Element ntqaclstz=root.element("NTQACINFZ");
			  if(null==ntqaclstz){
				   result.setErr(errmsg);
					return  ;
			   }
			
			   result.setResult(ntqaclstz.elementText("AVLBLV")); 
			  result.setSucc(B2ETrCode.CMB_SC_OK.equals(retcod));
			  result.setMsg(errmsg);
		  }
		 //查询代发代扣交易代码
			if ("QueryAgentList".equals(funnam)) {
				String retcod = info.elementText("RETCOD");
				if (null == retcod) {
					result.setErr("解析xml失败，RETCOD为空");
					return;
				}
				Element ntqagtttz = root.element("NTQAGTTSZ");
				if (null == ntqagtttz) {
					result.setErr("解析xml失败，没有NTQAGTTSZ节点");
					return;
				}
				// String trstyp=ntqagtttz.elementText("TRSTYP");
				// String trsnam=ntqagtttz.elementText("TRSNAM");
				result.setSucc(B2ETrCode.CMB_SC_OK.equals(retcod));
				result.setMsg(errmsg);
			}
		 //直接代发代扣
			if ("AgentRequest".equals(funnam)) {
				String retcod = info.elementText("RETCOD");
//				int state = 0;
				if (null == retcod) {
					result.setErr("解析xml失败，RETCOD为空");
					result.setSucc(true);
					result.setTransStatus(2);
					result.setStatusInfo("转账申请已受理，正在处理中");
					return;
				} else if ("0".equals(retcod)) {
//					state = DaiFuTransState.PAY_PROCESSING;					
					result.setSucc(true);
					result.setTransStatus(2);
					result.setStatusInfo("转账申请已受理，正在处理中");
				} else {
//					state = DaiFuTransState.PAY_FAILURE;					
					result.setSucc(false);
					result.setTransStatus(1);
					result.setStatusInfo("交易失败");
					result.setErr(errmsg);
				}
				
				return;
			}
		//查询交易概要信息
			if ("GetAgentInfo".equals(funnam)) {
				String errdsp = null;
				String retcod = info.elementText("RETCOD");
				if (null == retcod) {
					result.setErr("解析xml失败，RETCOD为空");
					return;
				}
				int state = 0;
				if ("0".equals(retcod)) {
					Element ntqatsqyz = root.element("NTQATSQYZ");
					if (null == ntqatsqyz) {
						 result.setErr("请求银行失败！");
						 state = DaiFuTransState.PAY_FAILURE;
					} else {
						String reqsta = ntqatsqyz.elementText("REQSTA");
						String rtnflg = ntqatsqyz.elementText("RTNFLG");
						String oprdat = ntqatsqyz.elementText("OPRDAT");
						result.setBank_date(oprdat);
						if (!Ryt.empty(reqsta)) {
							if ("FIN".equals(reqsta)) {
								if ("S".equals(rtnflg)) {
									state = DaiFuTransState.PAY_SUCCESS;
								} else {
								    errdsp = ntqatsqyz.elementText("ERRDSP");
									state = DaiFuTransState.PAY_FAILURE;
								}
							} else {
								state = DaiFuTransState.PAY_PROCESSING;
							}
						} else {
							result.setErr("REQSTA节点为空");
						}}

				} else {
					state = DaiFuTransState.PAY_FAILURE;
				}
				result.setSucc(true);
				B2ETradeResult b2e = new B2ETradeResult("", " ", state, errdsp==null?errmsg:errdsp);
				result.setResult(b2e);
				return;
			}
		 //查询账户的交易信息
		 if("GetTransInfo".equals(funnam)){
			 String retcod=info.elementText("RETCOD");
			   if(null==retcod){
				   result.setErr("解析xml失败，RETCOD为空");
					return  ;
			   }
				 result.setSucc(B2ETrCode.CMB_SC_OK.equals(retcod));
				  result.setMsg(errmsg);
		 } 
		 //查询交易明细
		 if("GetAgentDetail".equals(funnam)){
			 String retcod=info.elementText("RETCOD");
			   if(null==retcod){
				   result.setErr("解析xml失败，RETCOD为空");
					return  ;
			   }
			      result.setSucc(B2ETrCode.CMB_SC_OK.equals(retcod));
				  result.setMsg(errmsg); 
		 }
		    
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
			 
		
	}
	
	private StringBuffer genSubHead(B2EGate gate, String trCode) throws B2EException {
		String lgnnam = gate.getUserNo();
		if(isEmpty(lgnnam))
			throw new B2EException("LGNNAM为空");
		StringBuffer res=new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding = \"GBK\"?>");
		res.append("<CMBSDKPGK>");
		res.append("<INFO>");
		res.append("<FUNNAM>").append(trCode).append("</FUNNAM>");
		res.append("<DATTYP>").append(dattyp).append("</DATTYP>");
		res.append("<LGNNAM>").append(lgnnam).append("</LGNNAM>");
		res.append("</INFO>");
		return res;
		
	}
	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}
	
//	static{
//		cmb_ryt_provmap=new HashMap<String,String>();
//		cmb_ryt_provmap.put("110", "10");
//		cmb_ryt_provmap.put("310", "21");
//		
//	}
	

}
