package com.rongyifu.mms.bank.b2e;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.utils.LogUtil;

public class AbcXML implements BankXML {

	@Override
	public String genSubmitXML(int trCode, B2EGate gate) throws B2EException {
		StringBuffer res=null;
		String DbProv=gate.getAccNo().substring(0, 2);//截取网关中的Acc_No 的前两位
		String DbAccNo=gate.getAccNo().substring(2,gate.getAccNo().length());//截取网关中的Acc_No 的后15位
		if(trCode==B2ETrCode.QUERY_ACC_BALANCE){
			//余额查询    CQRA06
			String str=String.valueOf(Ryt.crateBatchNumber());	
			res = genSubHead(gate, "CQRA06",str);
			res.append("<Cmp>");
			res.append("<DbAccNo>").append(DbAccNo).append("</DbAccNo>");
			res.append("<DbProv>").append(DbProv).append("</DbProv>");
			res.append("<DbCur>01</DbCur>");
			res.append("</Cmp>");
		}
			res.append("</ap>");
		return addhead("0", res.toString());
	}

	@Override
	public String genSubmitXML(int trCode, TrOrders os, B2EGate gate) throws B2EException {
		StringBuffer res=null;
		// 跨行汇兑-落地处理结果查询CQRA61
		String DbAccNo=gate.getAccNo().substring(2,gate.getAccNo().length());//截取网关中的Acc_No 的后15位
		String DbProv=gate.getAccNo().substring(0, 2);//截取网关中的Acc_No 的前两位
		if (trCode==B2ETrCode.ABC_QUERY_STATE_L) {
			String reqNo=String.valueOf(Ryt.crateBatchNumber());	
			res = genSubHead(gate, "CQRA61",reqNo);
			res.append("<Corp>");
			res.append("<BookingDate>").append(os.getOid()).append("</BookingDate>");// 客户端流水号
			res.append("</Corp>");
			res.append("<Cmp>");
			res.append("<DbAccNo>").append(DbAccNo).append("</DbAccNo>");//借方账号
			res.append("<DbCur>01</DbCur>");//借方货币码
			res.append("<DbProv>").append(DbProv).append("</DbProv>");//借方省市代码
			res.append("</Cmp>");	
			
		}else if(trCode==B2ETrCode.PAY_TO_OTHER){
			String CrProv="";
			String CrAccNo="";
			String CrCur="01";
			String OthBankFlag="0";
			String CrBankName=os.getToBkName();
			String CrBankNo=os.getToBkNo();
			/*
			 * 分本行对私  和  它 行 
			 * 本行对私  调用接口
			 * 它行 调用接口
			 */
			String bg=(os.getToBkNo()!=null && os.getToBkNo().length()>3)?os.getToBkNo().substring(0, 3):os.getToBkNo();
			if(bg.equals("103")&&os.getPtype()==Constant.DaiFuTransType.PAY_TO_PERSON){
				//调用CFRT21    本行   单对私
				if(os.getToAccNo().length()==17){
					CrProv=os.getAccNo().substring(0, 2);
					CrAccNo=os.getAccNo().substring(2,gate.getAccNo().length());
				}else{
					CrAccNo=os.getToAccNo();
				}
				
				res=genSubHead(gate, "CFRT21", os.getOid());							//一年内不重复   //汇款-单笔对公
				res.append("<Amt>").append(Ryt.div100(os.getTransAmt())).append("</Amt>");		//交易金额
				res.append("<Cmp>");
				res.append("<DbProv>").append(DbProv).append("</DbProv>");						//借方省市代码
				res.append("<DbAccNo>").append(DbAccNo).append("</DbAccNo>");					//借方账号
				res.append("<DbLogAccNo></DbLogAccNo>");										//借方逻辑账号
				res.append("<DbCur>01</DbCur>");												//借方货币码 01 人民币
				res.append("<CrProv>").append(CrProv).append("</CrProv>");						//贷方省市代码
				res.append("<CrAccNo>").append(CrAccNo).append("</CrAccNo>");					//贷方账号
				res.append("<CrLogAccNo></CrLogAccNo>");										//贷方逻辑账号
				res.append("<CrCur>").append(CrCur).append("</CrCur>");							//贷方货币码
				res.append("<ConFlag>1</ConFlag>");												//校验贷方户名标志[0 不校验  1校验]	
				res.append("</Cmp>");	
				res.append("<Corp>");
				res.append("<PsFlag></PsFlag>");												//送空
				res.append("<BookingFlag>0</BookingFlag>");										//预约标志[0 不预约 1预约]
				res.append("<BookingDate></BookingDate>");
				res.append("<BookingTime></BookingTime>");
				res.append("<UrgencyFlag>N</UrgencyFlag>");										//加急标志[N 不加急 Y 加急]
				res.append("<OthBankFlag>").append(OthBankFlag).append("</OthBankFlag>");		//它行标志[0 本行 1他行]
				res.append("<CrAccName>").append(os.getToAccName()).append("</CrAccName>");		//收款方户名 RecAccName
				res.append("<DbAccName>").append(gate.getAccName()).append("</DbAccName>");		//付款方户名
				res.append("<WhyUse>").append(os.getPriv()).append("</WhyUse>");				//用途
				res.append("<Postscript>").append(os.getPriv()).append("</Postscript>");		//附言
				res.append("</Corp>");
			}else{
				//调用CFRT02    本行它行   单对公
				OthBankFlag="1";
				
				if(os.getToAccNo().length()==17){
					CrProv=os.getToAccNo().substring(0, 2);
					CrAccNo=os.getToAccNo().substring(2, os.getToAccNo().length());
				}else{
					CrAccNo=os.getToAccNo();
				}
				res=genSubHead(gate, "CFRT02", os.getOid());											//汇款-单笔对公
				res.append("<Amt>").append(Ryt.div100(os.getTransAmt())).append("</Amt>");	//交易金额
				res.append("<Cmp>");
				res.append("<DbProv>").append(DbProv).append("</DbProv>");						//借方省市代码
				res.append("<DbAccNo>").append(DbAccNo).append("</DbAccNo>");					//借方账号
				res.append("<DbLogAccNo></DbLogAccNo>");										//借方逻辑账号
				res.append("<DbCur>01</DbCur>");												//借方货币码 01 人民币
				res.append("<CrProv>").append(CrProv).append("</CrProv>");						//贷方省市代码
				res.append("<CrAccNo>").append(CrAccNo).append("</CrAccNo>");					//贷方账号
				res.append("<CrLogAccNo></CrLogAccNo>");										//贷方逻辑账号
				res.append("<CrCur>").append(CrCur).append("</CrCur>");												//贷方货币码
				res.append("<ConFlag>1</ConFlag>");												//校验贷方户名标志[0 不校验  1校验]	
				res.append("</Cmp>");	
				res.append("<Corp>");
				res.append("<PsFlag></PsFlag>");												//送空
				res.append("<BookingFlag>0</BookingFlag>");										//预约标志[0 不预约 1预约]
				res.append("<UrgencyFlag>Y</UrgencyFlag>");										//加急标志[N 不加急 Y 加急]
				res.append("<OthBankFlag>").append(OthBankFlag).append("</OthBankFlag>");		//它行标志[0 本行 1他行]
				res.append("<CrBankType>").append(os.getPtype()).append("</CrBankType>");		//它行行别
				res.append("<CrAccName>").append(os.getToAccName()).append("</CrAccName>");		//收款方户名 RecAccName
				res.append("<CrBankName>").append(CrBankName).append("</CrBankName>");			//收款方开户行名
				res.append("<CrBankNo>").append(CrBankNo).append("</CrBankNo>");				//收款方开户行号CrBankNo
				res.append("<DbAccName>").append(gate.getAccName()).append("</DbAccName>");		//付款方户名
				res.append("<WhyUse>").append(os.getPriv()).append("</WhyUse>");				//用途
				res.append("<Postscript>").append(os.getPriv()).append("</Postscript>");		//附言
				res.append("</Corp>");
			}
			
		}else if(trCode==B2ETrCode.ABC_QUERY_STATE_E){
			//调用异常交易查询接口  CQRT04
			String reqNo=String.valueOf(Ryt.crateBatchNumber());	
			res = genSubHead(gate, "CQRT04",reqNo);
			res.append("<Cmp>");
			res.append("<DbProv>").append(DbProv).append("</DbProv>");
			res.append("<DbAccNo>").append(DbAccNo).append("</DbAccNo>");
			res.append("<DbCur>01</DbCur>");
			res.append("<CmeSeqNo>").append(os.getOid()).append("</CmeSeqNo>");
			res.append("</Cmp>");	
		}
		res.append("</ap>");
		return addhead("0", res.toString());
	}

	@Override
	public void parseXML(B2ERet ret, String xml) throws B2EException {
		if(ret==null) ret = new B2ERet();
		ret.setGid(B2ETrCode.ABC_GID);
		 if(isEmpty(xml)){
//			 ret.setErr("XML错误");
			     handleAbnormal(ret,"返回XML错误");
				return;
		 }
		 try{
			 Document doc=DocumentHelper.parseText(xml);
			 Element root=doc.getRootElement();//根节点
			 Element TransCode=root.element("TransCode");//内部交易代码
			 if(TransCode==null){
//				 ret.setErr("XML没有TransCode节点");
				 handleAbnormal(ret,"XML没有TransCode节点");
				 return ;
			 }
			 String cctc=TransCode.getText();
			 
			 if(cctc==null || Ryt.empty(cctc)){
//				 ret.setErr("TransCode错误！");
				 handleAbnormal(ret,"TransCode节点错误！");
				 return ;
			 }
			 Element RespCode=root.element("RespCode");//返回码
			 Element RespDate=root.element("RespDate");//返回日期
			 Element RespTime=root.element("RespTime");//返回时间
			 Element RespInfo=root.element("RespInfo");//返回信息
			 Element RSeqNoElement=root.element("RespSeqNo");
			 String RSeqNo=RSeqNoElement.getText();
			 String rpCode=RespCode.getText();
			 String respInfo=RespInfo.getText();
			 ret.setBank_date(RespDate==null?"":RespDate.getText());
			 ret.setBank_time(RespTime==null?"":RespTime.getText());
			 ret.setTrCode(cctc);
			 ret.setRes_code(rpCode);
			 ret.setSucc(B2ETrCode.ABC_SC_OK.equals(rpCode));
			 Element Cmp=root.element("Cmp");
			   if (Cmp == null) {
				   LogUtil.printErrorLog("AbcXML", "parseXML", "返回报文没有Cmp");
//					ret.setErr(respInfo);
				   handleAbnormal(ret,"返回报文没有Cmp节点");
					return  ;
				}
//查询余额    内部交易代码判断
			 if("CQRA06".equals(cctc)){
				 if("0000".equals(rpCode)){//成功
					   Element RespPrvData1=Cmp.element("RespPrvData1");
					   if (RespPrvData1 == null) {
							ret.setErr("没有RespPrvData1节点");
							return  ;
						}
					   String str=RespPrvData1.getText();
					   if(isEmpty(str)){
						   ret.setErr("RespPrvData1节点没有值");
							return  ;
					   }
					   String[] r = str.split("\\|");
					   //Prov    |AccNo|Cur  |AccName |Bal |AvailBal|AccType|AbisRespCode|
					   //省市代码|账号   |货币码|账户名称|余额|可用余额 |账户性质|结果(返回码)|
					   ret.setResult(r[5]);
					   ret.setSucc(true);
					  /* if(r[7].equals("")){
					   ret.setTransStatus(0); // 交易结果: 0 成功；1 失败；2 中间状态（需要通过查询接口来确认交易结果）
					   }*/
					   
					   
				 }else if("9999".equals(rpCode)||"CICS".equals(rpCode)){//前置通讯异常
					 ret.setErrorMsg(respInfo);
					 ret.setTransStatus(1);
				 }else{//失败
					 ret.setErrorMsg(respInfo);
					 ret.setTransStatus(1);
				 }
				 return;
			 }
//单笔对公
			 if("CFRT02".equals(cctc)){
				 Element Corp=root.element("Corp");
				 if(Corp==null){
//					 ret.setErr("xml没有Corp节点");
					 handleAbnormal(ret,"返回报文没有Corp节点");
					 return;
				 }else{
					 Element WaitFlagElement=Corp.element("WaitFlag");
					 String waitFlag=WaitFlagElement.getText();
					 if("0000".equals(rpCode) &&  waitFlag.equals("0") ){//成功
						 ret.setResult(RSeqNo);
						 ret.setTransStatus(0);
					 }else if( "9999".equals(rpCode)||"CICS".equals(rpCode)){//异常交易
						 ret.setResult(B2ETrCode.ABC_QUERY_STATE_E);
						 ret.setTransStatus(2);
					 }else if("1".equals(waitFlag) || "0001".equals(rpCode) ){ //落地处理交易
						 //落地处理
						 ret.setResult(B2ETrCode.ABC_QUERY_STATE_L);
						 ret.setTransStatus(2);
					 }else{//失败
						 ret.setErrorMsg(respInfo);
						 ret.setTransStatus(1);
						 ret.setSucc(false);
						 return;
					 }
					 ret.setSucc(true);
				 }
				 return;
				
			 }
//单对私
			 ////本行只有成功和失败，
			 if("CFRT21".equals(cctc)){
				 if("0000".equals(rpCode)){//成功
					 ret.setSucc(true);
					 ret.setResult(RSeqNo);
					 ret.setTransStatus(0);
				 } else if("9999".equals(rpCode)||"CICS".equals(rpCode)){ //前置通讯异常
					 ret.setSucc(false);
					 ret.setErrorMsg(respInfo);
					 ret.setTransStatus(1);
				 }else{//失败
					 ret.setSucc(false);
					 ret.setErrorMsg(respInfo);
					 ret.setTransStatus(1);
				 }
				 
				 return;
			 }
//异常处理
			 if("CQRT04".equals(cctc)){
				 Integer state=null;
				 Element Corp=root.element("Corp");
				 if(Corp ==null){
					 ret.setErr("xml没有Corp节点");
					 return;
				 }
				 Element RespPrvData=Cmp.element("RespPrvData");
				 Element Postscript=Corp.element("Postscript");
				 String postScriptInfo=Postscript.getText();
				 String RespPrv=RespPrvData.getText();
				 String SzRespCode=RespPrv.substring(18, 22);
				 String SzAbisRespCode=RespPrv.substring(22,26);
				 if(SzRespCode.equals("0000")	||	SzAbisRespCode.equals("0000")){
					 state=DaiFuTransState.PAY_SUCCESS;
					 ret.setTransStatus(0);
				 }else if(SzRespCode.equals("0389")	||	SzAbisRespCode.equals("0389")){
					 state=DaiFuTransState.PAY_FAILURE;
					 ret.setTransStatus(1);
				 }else if(SzRespCode.equals("0388")	||	SzAbisRespCode.equals("0388")){
					 state=DaiFuTransState.PAY_PROCESSING;
					 ret.setTransStatus(2);
				 }else if("9999".equals(rpCode)||"CICS".equals(rpCode)){ //通讯异常  发起查询
					 state=DaiFuTransState.PAY_PROCESSING;
					 ret.setTransStatus(2);
				 }else{
					 state=DaiFuTransState.PAY_FAILURE;
					 ret.setTransStatus(1);
				 }
				 B2ETradeResult b2eResult = new B2ETradeResult("",RSeqNo, state,postScriptInfo);
				 ret.setResult(b2eResult);
				 return;
			 }
//落地处理
			 if("CQRA61".equals(cctc)){
				 Integer state =null;
				 if("0000".equals(rpCode)){//成功
					 Element Corp=root.element("Corp");
					 Element UseState=Corp.element("UseState");
					 String useState=UseState.getText();
					 if("1".equals(useState)){
						 //成功交易
						 state=DaiFuTransState.PAY_SUCCESS;
						 ret.setTransStatus(0);
					 }else if("0".equals(useState)){
						 state=DaiFuTransState.PAY_PROCESSING;
						 //处理中或失败交易
						 ret.setTransStatus(2);
					 }
				 }else if("9999".equals(rpCode)||"CICS".equals(rpCode)){//异常
					 state=DaiFuTransState.PAY_PROCESSING;
					 ret.setTransStatus(2);
				 }else{//失败
					 state=DaiFuTransState.PAY_FAILURE;
					 ret.setErrorMsg(respInfo);
					 ret.setTransStatus(1);
				 } 
				 B2ETradeResult b2eResult = new B2ETradeResult("",RSeqNo, state,respInfo);
				 ret.setResult(b2eResult);
				 return;
			 }
		 }catch (DocumentException e) {
			 e.printStackTrace();
			 handleAbnormal(ret,e.getMessage());
			 return;
		}
	}

	private StringBuffer genSubHead(B2EGate gate, String trCode, String os)
			throws B2EException {
		String cropNo = gate.getCorpNo();
		Date date = new Date();
		String SysDate = new SimpleDateFormat("yyyyMMdd").format(date);
		String SysTime = new SimpleDateFormat("HHmmss").format(date);

		if (isEmpty(cropNo))
			throw new B2EException("CropNo为空");

		StringBuffer res = new StringBuffer();
		res.append("<ap>");
		res.append("<CCTransCode>").append(trCode).append("</CCTransCode>");// 内部交易代码
		res.append("<ProductID>").append("ICC").append("</ProductID>");
		res.append("<ChannelType>").append("ERP").append("</ChannelType>");
		res.append("<CorpNo>").append(cropNo).append("</CorpNo>");// 企业技监局号码/客户号
		res.append("<OpNo></OpNo>");// 企业操作员编号
		res.append("<AuthNo></AuthNo>");// 认证码
		res.append("<ReqSeqNo>").append(os).append("</ReqSeqNo>");// 请求方流水号
		res.append("<ReqDate>").append(SysDate).append("</ReqDate>");// 请求日期
		res.append("<ReqTime>").append(SysTime).append("</ReqTime>");// 请求时间
		res.append("<Sign></Sign>");// 数字签名
		res.append("<FileFlag>0</FileFlag>");// 数据文件标识
		return res;
	}

	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}
	
	/***
	 * Socket方式报文结构为：包头＋数据包，报头为1个字节的是否为加密包标志，
	 * 加上6个字节的字符表示数据包的长度，如果长度不足6位则右边用空格补足
	 * @param yorn 是否加密,1或0
	 * @param str  XML
	 * @return
	 * @throws UnsupportedEncodingException 
	 */

	private String addhead(String yorn,String str){
			String len = null;
			try {
				len = yorn+(str.getBytes("GBK").length);
			} catch (UnsupportedEncodingException e) {
				len = yorn+(str.getBytes().length);
				e.printStackTrace();
			}
			while(len.length()<=6){
				len=len+" ";
				}
			return len+str;
	}
	
	/****
	 * 处理异常情况
	 * @param ret
	 * @param msg
	 */
	public void handleAbnormal(B2ERet ret,String msg){
		ret.setResult(B2ETrCode.ABC_QUERY_STATE_E);
		 ret.setTransStatus(2);
		 ret.setSucc(false);
		 ret.setErrorMsg(msg);
		 return;
	}
	
}
