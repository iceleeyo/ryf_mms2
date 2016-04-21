package com.rongyifu.mms.bank.b2e;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.common.Constant.DaiFuTransState;
import com.rongyifu.mms.exception.B2EException;
import com.rongyifu.mms.unionpaywap.EncDecUtil;
import com.rongyifu.mms.utils.LogUtil;

public class PSBCXML implements BankXML {

	@Override
	public String genSubmitXML(int trCode, B2EGate gate) throws B2EException {
		String datetime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		StringBuffer res = new StringBuffer();
		int accNo=gate.getAccNo().length();
		int accNameLen=0;
		String accName=gate.getAccName();
		try {
			accNameLen= gate.getAccName().getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String rand=Ryt.createRandomStr(8);
		/**
		 * 查询余额
		 */
		if (trCode == B2ETrCode.QUERY_ACC_BALANCE) {
			// 交易处理码 N6
			res.append("772000");
			// 主帐号/卡号 AN..32(LLVAR)
			res.append(accNo < 10 ? "0" + accNo : String.valueOf(accNo)).append(gate.getAccNo()); 
			// 渠道标识码 AN2
			res.append("29"); 
			// 卡有效期 N8 (信用卡交易时有效,其他交易填“00000000”)
			res.append("00000000"); 
			// 流水号N8
			res.append(rand); 
			// 本地时间 N6
			res.append(datetime.substring(8)); 
			// 本地日期N8
			res.append(datetime.substring(0, 8)); 
			// 清算日期 N8
			res.append(datetime.substring(0, 8)); 
			// 交易标志 AN16  (第一位：1：折交易，2：卡交易3：公司结算账号、4：信用卡)
			res.append("3000000000000000"); 
			// 委托单位代码 AN12 （310132610647）
			res.append(gate.getCorpNo()); 
			// 第二磁道数据 Z..37(LLVAR) 卡交易时有效 00
			res.append("00"); 
			// 第三磁道数据 Z...104(LLLVAR) 卡交易时有效 000
			res.append("000"); 
			// 户名 ANS..60(LLVAR)
			res.append(accNameLen < 10 ? "0" + accNameLen : String.valueOf(accNameLen)).append(accName); 
			//res.append("05abcde");
			// 密码（PIN） B64 个人结算账户和信用卡交易时有效，其他填写8个空格
			res.append("        "); 
			// 安全控制信息 N16
			res.append("1120000000000000"); 
			// 身份信息个人结算账户和信用卡交易时有效，其他填写25个空格
			res.append("                         "); 
			// 业务代码 AN6
			res.append(gate.getBusiNo()); 
			// 报文鉴别码 B64
			
			 //* 交易处理码 主账号 流水号 本地时间 本地日期 委托单位代码
			 
			String arrmac ="772000"+" "
							+ (accNo < 10 ? "0" + accNo : String.valueOf(accNo))+gate.getAccNo()+" "
							+ rand+" " 
							+datetime.substring(8)+ " " 
							+datetime.substring(0, 8)+ " " 
							+gate.getCorpNo()+ " ";
			//res.append(this.returnmac(arrmac));
			String Macdata=this.returnmac(arrmac);
			String key=gate.getToken();
			try {
				String data=EncDecUtil.encXml(key.getBytes(), Macdata);
				res.append(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		/**
		 * 更新密钥
		 */
		//request:0000660310132610647770020293860054510384820140626201406263101326106471110000000000000
		//trCode == B2ETrCode.PSBC_UPDATE_KEY
		//trCode == B2ETrCode.QUERY_ACC_BALANCE
		if (trCode == B2ETrCode.PSBC_UPDATE_KEY) {
			// 交易处理码(6位数字)
			res.append("770020"); 
			// 渠道标识码(2位数字)
			res.append("29"); 
			// 流水号(8位数字)
			res.append(rand); 
			// 本地时间(6位数字 时分秒)
			res.append(datetime.substring(8));
			// 本地日期(8位数字 年月日)
			res.append(datetime.substring(0, 8)); 
			// 清算日期(8位数字)
			res.append(datetime.substring(0, 8)); 
			// 委托单位代码(12位数字或字符)310323600143
			res.append(gate.getCorpNo()); 
			// 安全控制信息(16位数字)
			res.append("1000000000000000"); 
		}
		return this.contentHead(res.toString(), gate.getCorpNo()) + res.toString();
	}

	@Override
	public String genSubmitXML(int trCode, TrOrders os, B2EGate gate)
			throws B2EException {
		String payAmt=String.valueOf(os.getPayAmt()==0||os.getPayAmt()==null?"":os.getPayAmt());
		String payAmt1=this.StringLength(payAmt, 12);
		String rand=os.getOid().substring(os.getOid().length()-8, os.getOid().length());
		StringBuffer res = new StringBuffer();
		String toAccNo=os.getToAccNo()==null?"":os.getToAccNo();
		String toAccName=os.getToAccName()==null?"":os.getToAccName().trim();
		String orderInfo=os.getPriv()==null?"":os.getPriv();
		Integer sysTime=os.getSysTime();
		String time=this.StringLength(this.getStringTime(sysTime==null||sysTime==0?0:sysTime), 6);
		String orderId=os.getOid()==null?"":os.getOid();
		int orderIdLen=0;
		int toAccNoLen=0;
		int toAccNameLen=0;
		int orderInfoLen=0;
		try {
			orderIdLen = orderId.getBytes("GBK").length;
			toAccNoLen= toAccNo.getBytes("GBK").length;
			toAccNameLen= toAccName.getBytes("GBK").length;
			orderInfoLen= orderInfo.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		/**
		 * 存款
		 */
		if (trCode == B2ETrCode.PAY_TO_OTHER) {
			// 交易处理码 (6位数字)
			res.append("774000"); 
			// 主账户/卡号（字母或数字最大32位）
			res.append(toAccNoLen < 10 ? "0" + toAccNoLen : String.valueOf(toAccNoLen)).append(toAccNo); 
			// 交易金额(12位数字)
			res.append(payAmt1);
			// 渠道标识码(2位字母或数字)
			res.append("29"); 
			// 卡有效期(8位数字) 信用卡交易时有效，其他交易填“00000000”。
			res.append("00000000"); 
			// 流水号(8位数字)
			res.append(rand); 
			// 本地时间(6位数字)
			res.append(time); 
			// 本地日期（8位数字）
			res.append(null==os.getSysDate()||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 清算日期（8位数字）
			res.append(null==os.getSysDate()||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 交易标志(16位数字或字母)//1：折交易，2：卡交易3：公司结算账号、4：信用卡
			if(os.getPtype()==Constant.DaiFuTransType.PAY_TO_PERSON){//对私
				if(os.getCardFlag()==0){
					res.append("2000000000000000"); //卡
				}else{
					res.append("1000000000000000"); //折
				}
			}else{//对公
				res.append("3000000000000000");//公司结算账号
			}
			// 委托单位代码(12 数字或字母)
			res.append(gate.getCorpNo()); 
			// 第二磁道数据 卡交易时有效 其它填00
			res.append("00"); 
			// 第三磁道数据 卡交易时有效 其它填000
			res.append("000"); 
			// 户名(最长60位的字母或数字或特殊符号)
			res.append(toAccNameLen < 10 ? "0" + toAccNameLen : String.valueOf(toAccNameLen)).append(toAccName); 
			// 身份信息   个人账户有效，对公司结算账户填25个空格。
				res.append("                         ");
			// 用户号码（最大40位字母数字特殊字符）缴费号或合同号....................
			res.append(orderIdLen < 10 ? "0" + orderIdLen : String.valueOf(orderIdLen)).append(orderId);
			// 业务代码(6位数字字母)
			res.append(gate.getBusiNo()); 
			// 地区代码(6位数字)
			res.append("000"+gate.getProvId()); 
			// 发起自定义数据元(最大999位字母数字特殊字符)
			 if (orderInfoLen < 10) {
					 res.append("00").append(orderInfoLen).append(orderInfo);
			} else if (orderInfoLen < 100) {
				res.append("0").append(orderInfoLen).append(orderInfo);
			} else {
				res.append(orderInfoLen).append(orderInfo);
			}

			// 报文鉴别码(64位二进制)
			/**
			 * 交易处理码 主账号 交易金额 流水号 本地时间 本地日期 委托单位代码  用户号码
			 */
			String arrmac ="774000"+" "
					+ (toAccNoLen < 10 ? "0" + toAccNoLen : String.valueOf(toAccNoLen))+toAccNo+" "
					+payAmt1+" "
					+ rand+" " 
					+time+ " " 
					+(os.getSysDate()==null||os.getSysDate()==0?"00000000":os.getSysDate())+ " " 
					+gate.getCorpNo()+ " "
					+"00";
			//res.append(this.returnmac(arrmac));
			String Macdata=this.returnmac(arrmac);
			String key=gate.getToken();
			try {
				String data=EncDecUtil.encXml(key.getBytes(), Macdata);
				res.append(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		/**
		 * 交易结果查询
		 */
		if (trCode == B2ETrCode.QUERY_ORDER_STATE) {
			// 交易处理码 N6
			res.append("772030"); 
			// 主帐号/卡号 AN..32(LLVAR)
			res.append(toAccNoLen < 10 ? "0" + toAccNoLen : String.valueOf(toAccNoLen)).append(toAccNo); 
			// 渠道标识码 AN2
			res.append("29"); 
			// 流水号 N8
			res.append(rand); 
			// 本地时间 N6
			res.append(time); 
			// 本地日期 N8
			res.append(os.getSysDate()==null||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 清算日期 N8
			res.append(os.getSysDate()==null||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 交易标志(16位数字或字母)//1：折交易，2：卡交易3：公司结算账号、4：信用卡
			if(os.getPtype()==Constant.DaiFuTransType.PAY_TO_PERSON){//对私
				if(os.getCardFlag()==0){
					res.append("2000000000000000"); //卡
				}else{
					res.append("1000000000000000"); //折
				}
			}else{//对公
				res.append("3000000000000000");//公司结算账号
			}
			// 委托单位代码 AN12
			res.append(gate.getCorpNo()); 
			// 安全控制信息 N16
			res.append("1120000000000000"); 
			// 原交易处理码(原交易信息) N6..........................
			res.append("774000"); 
			// 原本地日期(原交易信息) N8(YYYYMMDD)
			res.append(null==os.getSysDate()||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 原本地时间(原交易信息) N6(hhmmss)
			res.append(time); 
			// 原交易清算日期(原交易信息) N8(YYYYMMDD)
			res.append(null==os.getSysDate()||os.getSysDate()==0?"00000000":os.getSysDate()); 
			// 原交易委托方代码(原交易信息) AN12
			res.append(gate.getCorpNo()); 
			// 原流水号 N8(原交易信息)
			res.append(rand); 
			// 原交易金额(原交易信息) N12
			res.append(payAmt1);
			// 报文鉴别码 B64
			/**
			 * 交易处理码 主账号 流水号 本地时间 本地日期 委托单位代码 
			 */
			String arrmac ="772030"+" "
					+ (toAccNoLen < 10 ? "0" + toAccNoLen : String.valueOf(toAccNoLen))+toAccNo+" "
					+ rand+" " 
					+time+ " " 
					+(os.getSysDate()==null||os.getSysDate()==0?"00000000":os.getSysDate())+ " " 
					+gate.getCorpNo();
			//res.append(this.returnmac(arrmac));
			String Macdata=this.returnmac(arrmac);
			String key=gate.getToken();
			try {
				String data=EncDecUtil.encXml(key.getBytes(), Macdata);
				res.append(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		LogUtil.printErrorLog("PSBCXML", "genSubmitXML","发送报文："+this.contentHead(res.toString(), gate.getCorpNo())+ res.toString());
		return this.contentHead(res.toString(), gate.getCorpNo())
				+ res.toString();
	}

	@Override
	public void parseXML(B2ERet ret, String xml) throws B2EException {
		if (ret == null)
			ret = new B2ERet();
		ret.setGid(B2ETrCode.PSBC_GID);// 银企直连网关号
		if (isEmpty(xml)) {
			ret.setErr("XML错误");// 错误信息
			return;
		}
		
		try {
			xml = xml.substring(19).trim();
			String transCode = xml.substring(0, 6);// 交易处理码
			ret.setTrCode(transCode);
			// 查询余额
			//0							-			6交易处理码
			//6						-				8卡号长度
			//8						-				8+n 卡号
			//8+n					-				8+n+2渠道标识码
			//8+n+2								-	8+n+2+8  流水号
			//8+n+2+8							-	8+n+2+8+6时间
			//8+n+2+8+6							-	8+n+2+8+6+8日期	
			//8+n+2+8+6+8						-	8+n+2+8+6+8+8清算日期
			//8+n+2+8+6+8+8						-	8+n+2+8+6+8+8+16交易标识
			//8+n+2+8+6+8+8+16					-	8+n+2+8+6+8+8+16+12委托单位代码
			//8+n+2+8+6+8+8+16+12				-	8+n+2+8+6+8+8+16+12+2响应码
			//8+n+2+8+6+8+8+16+12+2				-	8+n+2+8+6+8+8+16+12+2+16安全控制信息
			//8+n+2+8+6+8+8+16+12+2+16			-	8+n+2+8+6+8+8+16+12+2+16+12存款余额
			//8+n+2+8+6+8+8+16+12+2+16+12		-	8+n+2+8+6+8+8+16+12+2+16+12+12存折余额
			//8+n+2+8+6+8+8+16+12+2+16+12+12	-	8+n+2+8+6+8+8+16+12+2+16+12+12+12可用余额
			if ("772000".equals(transCode)) {
				int n=Integer.valueOf(xml.substring(6, 8));
				String RetCode=xml.substring(n+68, n+70);
				if("00".equals(RetCode)){
					//成功
					ret.setSucc(true);
					ret.setTransStatus(0);// 成功
					String tmpBalance=xml.substring(n+110, n+122);
					ret.setResult(Ryt.div100(tmpBalance));
				}else{
					//失败
					ret.setSucc(false);
					ret.setErrorMsg(RetCode);
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(RetCode);// 状态信息
				}
				return;
			}
			//0000841310 1326106477 7002015201 4050510000 0201405052 0140505310 1326106470 0100000000 0000000荲妲!楗p�铲櫜{�
			// 更新密钥
			// 接收报文样例：770020 0-6
			// 15渠道标识(15电话银行16个人网银17公司网银) 6-8
			// 00000026流水号 8-16
			// 142333本地时间 16-22
			// 20120222本地日期 22-30
			// 20120222清算日期 30-38
			// 310000710151委托单位代码 38-50
			// 00响应码(成功)50-52
			// 1000000000000000安全控制信息Key Type 52- 68
					// N1 密钥类型。
					// 加密算法标志（ENCRYPTION-METHOD-USED） N2（00表示DES加密算法）。
					// 置成全“0”（RESERVED） N13
			// K篊K30瞔臗[钅}镃 报文安全码68
			if ("770020".equals(transCode)) {
				String key=xml.substring(68).trim();
				String RetCode = xml.substring(50, 52);// 交易状态00成功
				String fSeqno = xml.substring(8, 16);// 流水号
				String tranTime = xml.substring(16, 22);// 交易时间
				String tranDate = xml.substring(22, 30);// 交易日期
				ret.setBank_date(tranDate);
				ret.setBank_time(tranTime);

				if ("00".equals(RetCode)) {// 成功
					ret.setTransStatus(0);// 成功
					ret.setSucc(true);
					ret.setResult(fSeqno);
					ret.setMsg(key);
				} else {//失败
					ret.setTransStatus(1);// 失败
					ret.setSucc(false);
					ret.setMsg(RetCode);//.......
					ret.setStatusInfo(RetCode);// 状态信息
				}
				return;
			}
// 存款
//774000  				0										-	6交易处理码
//18 					6										-	8卡号长度
//602918400201426911 	8 										- 	8+num    卡号
//120000000900    		8+num 									-  	8+num+12交易金额
//15   					8+num+12 								 - 	8+num+12+2渠道标识码
//55044847   			8+num+12+2  							-   8+num+12+2+8流水
//100000  				8+num+12+2+8 							 -  8+num+12+2+8+6时间
//20140606				8+num+12+2+8+6 							 -  8+num+12+2+8+6+8日期
//20140606				8+num+12+2+8+6+8	  					-  	8+num+12+2+8+6+8+8清算日期
//2000000000000000 		8+num+12+2+8+6+8+8 						 -	8+num+12+2+8+6+8+8+16交易标识
//310132610647			8+num+12+2+8+6+8+8+16					-	8+num+12+2+8+6+8+8+16+12委托单代码
//ZZ					8+num+12+2+8+6+8+8+16+12				-	8+num+12+2+8+6+8+8+16+12+2响应码
//042020用户号码			8+num+12+2+8+6+8+8+16+12+2				-	8+num+12+2+8+6+8+8+16+12+2+2用户号码长度
//						8+num+12+2+8+6+8+8+16+12+2+2			-	8+num+12+2+8+6+8+8+16+12+2+2+n用户号码
//208030业务代码			8+num+12+2+8+6+8+8+16+12+2+2+n			-	8+num+12+2+8+6+8+8+16+12+2+2+n+6业务代码
//021012地区代码			8+num+12+2+8+6+8+8+16+12+2+2+n+6		-	8+num+12+2+8+6+8+8+16+12+2+2+n+6+6地区代码
//000自定义				8+num+12+2+8+6+8+8+16+12+2+2+n+6+6		-	8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3发起自定义数据长度
//						8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3 	-	8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3+nn发起自定义数据
//						8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3+nn	-	8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3+nn+3应答自定义数据
//应答					8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3+nn+3 -	8+num+12+2+8+6+8+8+16+12+2+2+n+6+6+3+nn+3+ss
//妲岊喛鏌
			if (xml.startsWith("774000")) {
				int n=Integer.valueOf(xml.substring(6,8));//卡号长度
				int n1=Integer.valueOf(xml.substring(n+82, n+84));//用户号码长度
				int n2=Integer.valueOf(xml.substring(n+n1+96, n+n1+99));//自定义发起数据长度
				int n3=Integer.valueOf(xml.substring(n+n1+n2+99, n+n1+n2+102));//自定义应答数据长度
				String respon=xml.substring(n+n1+n2+102, n+n1+n2+n3+102);//自定义应答数据
				String RetCode = xml.substring(n+80, n+82);// 交易状态00成功
				String fSeqno = xml.substring(n+22, n+30);// 流水号
				String tranTime = xml.substring(n+30, n+36);// 交易时间
				String tranDate = xml.substring(n+36, n+44);// 交易日期
				ret.setBank_date(tranDate);
				ret.setBank_time(tranTime);
				if("00".equals(RetCode)){
					ret.setTransStatus(0);// 成功
					ret.setSucc(true);
					ret.setResult(fSeqno);
				}else{
					ret.setTransStatus(1);// 失败
					ret.setSucc(false);
					ret.setMsg(respon);
					ret.setStatusInfo(RetCode);// 状态信息
				}
				return;
			}
			// 交易结果查询
			//0-										6交易处理码
			//6-										8卡号长度
			//8-										8+n卡号
			//8+n	-									8+n+2 渠道标识码
			//8+n+2	-									8+n+2+8流水号
			//8+n+2+8	-								8+n+2+8+6本地时间
			//8+n+2+8+6	-								8+n+2+8+6+8本地日期
			//8+n+2+8+6+8	-							8+n+2+8+6+8+8清算日期
			//8+n+2+8+6+8+8	-							8+n+2+8+6+8+8+16交易标识
			//8+n+2+8+6+8+8+16	-						8+n+2+8+6+8+8+16+12委托单位代码
			//8+n+2+8+6+8+8+16+12	-					8+n+2+8+6+8+8+16+12+2响应码
			//8+n+2+8+6+8+8+16+12+2	-					8+n+2+8+6+8+8+16+12+2+16安全控制信息
			//8+n+2+8+6+8+8+16+12+2+16	-				8+n+2+8+6+8+8+16+12+2+16+6原交易码
			//8+n+2+8+6+8+8+16+12+2+16+6	-			8+n+2+8+6+8+8+16+12+2+16+6+8原日期
			//8+n+2+8+6+8+8+16+12+2+16+6+8	-			8+n+2+8+6+8+8+16+12+2+16+6+8+6原时间
			//8+n+2+8+6+8+8+16+12+2+16+6+8+6	-		8+n+2+8+6+8+8+16+12+2+16+6+8+8+8原清算日期
			//8+n+2+8+6+8+8+16+12+2+16+6+8+6+8	-		8+n+2+8+6+8+8+16+12+2+16+6+8+8+8+12委托单代码
			//8+n+2+8+6+8+8+16+12+2+16+6+8+6+8+12		8+n+2+8+6+8+8+16+12+2+16+6+8+8+8+12+8原流水
			//8+n+2+8+6+8+8+16+12+2+16+6+8+6+8+12+8		8+n+2+8+6+8+8+16+12+2+16+6+8+8+8+12+8+12原交易金额
			//8+n+2+8+6+8+8+16+12+2+16+6+8+6+8+12+8+12	8+n+2+8+6+8+8+16+12+2+16+6+8+8+8+12+8+12+3自定义数据长度
			if (xml.startsWith("772030")) {
				Integer state=null;
				int n=Integer.valueOf(xml.substring(6,8));
				String RetCode = xml.substring(n+68, n+70);// 交易状态00成功
				String fSeqno = xml.substring(n+10, n+18);// 流水号
				String tranTime = xml.substring(n+16, n+24);// 交易时间
				String tranDate = xml.substring(n+24, n+32);// 交易日期
				//int autoinfo=Integer.valueOf(xml.substring(n+146, n+149));//自定义数据长度
				
				String status=xml.substring(n+157, n+159);//状态信息
				String statusInfo="";
				if(null!=status&&"".equals(status)){
					int num=Integer.valueOf(status);
					switch (num) {
					case 0:
						statusInfo="交易成功";
						break;
					case 1:
						statusInfo="交易失败";
						break;
					case 2:
						statusInfo="交易被恢复";
						break;
					case 3:
						statusInfo="交易被取消";
						break;
					default:
						statusInfo="报文出错";
						break;
					}
				}
				String desstatus=xml.substring(n+159, n+199);//状态信息描述
				ret.setBank_date(tranDate);
				ret.setBank_time(tranTime);
				if("00".equals(RetCode)){
					state=DaiFuTransState.PAY_SUCCESS;
					ret.setTransStatus(0);// 成功
				}else{
					state=DaiFuTransState.PAY_FAILURE;
					ret.setMsg(desstatus);
					ret.setTransStatus(1);// 失败
					ret.setStatusInfo(statusInfo);// 状态信息
				}
				B2ETradeResult b2eResult = new B2ETradeResult("",fSeqno, state,desstatus);
				ret.setResult(b2eResult);
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * 报文头
	 * 
	 * @param contentBody
	 * @param systemID
	 * @return
	 */
	private String contentHead(String contentBody, String systemID) {
			String contentLength="";
				try {
					contentLength = this.StringLength(
									String.valueOf(contentBody.getBytes("GBK").length), 6);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		String contentDirection = "0";
		return contentLength + contentDirection+systemID;
	}

	/**
	 * 位数不足则补0
	 * 
	 * @param content
	 * @param length
	 * @return
	 */
	private String StringLength(String content, int length) {
		if (content.length() >= length) {
			// 如果长度够了就直接返回出去.
			return content;
		} else {
			// 长度不够前面补0
			content = "0" + content;
			// 递归调用本身.
			return StringLength(content, length);
		}
	}

	private boolean isEmpty(String str) {
		return null == str || str.trim().length() == 0;
	}

	public String returnmac(String destStr) {
		destStr = destStr.trim();

		char[] tmp = new char[destStr.length()];

		// 拷贝目标字符串到char[]
		destStr.getChars(0, destStr.length(), tmp, 0);

		StringBuffer sb = new StringBuffer();

		char preChar = (char) 0;

		int bitCount = 0;

		for (int i = 0; i < tmp.length; i++) {
			// 如果是重复空格
			if (preChar == ' ' && (tmp[i] == ' ')) {
				continue;
			}

			if ((tmp[i] >= '0' && tmp[i] <= '9')
					|| (tmp[i] >= 'A' && tmp[i] <= 'Z')
					|| (tmp[i] >= 'a' && tmp[i] <= 'z') || tmp[i] == ','
					|| tmp[i] == '.' || tmp[i] == ' ') {
				sb.append(tmp[i]);
				bitCount += 8;
			}
			preChar = tmp[i];
		}

		int yx = bitCount % 64;
		if (yx > 0) {
			for (int i = 0; i < ((64 - yx) / 8); i++) {
				sb.append((char) 0x00);
			}
		}

		return sb.toString();
	}
	public String getStringTime(int nowtime) {
		int hour = (nowtime % 86400) / 3600;
		int min = (nowtime % 3600) / 60;
		int second = nowtime % 60;
		return String.valueOf(hour)+(min < 10 ? "0" + min : min)+(second < 10 ? "0" + second : second);
	}
	
}
