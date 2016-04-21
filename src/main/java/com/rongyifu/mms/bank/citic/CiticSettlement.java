package com.rongyifu.mms.bank.citic;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.axis.client.Call;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.rongyifu.mms.bean.Tlog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 深圳中信定时结算
 * @author yang.yaofeng
 *
 */
public class CiticSettlement {
	private static final String CITIC_GID="12201";//中信网关号
	static SettlementDao settlementDao=null;
	static SystemDao sysdao=null;
	private static String batchNo="";
	private static String sumAmt="";
	private static int sumCount=0;
	private static String merId="";//商户号
	private static Call call=null;
	private static Map<String, String> errMsgMap=null; 
	private static final String TERMINALID="00000000";
	/**
	 * 通过查询接口获得同步订单数据
	 */
	public void synOrder(){
		sysdao=new SystemDao();
		settlementDao=new SettlementDao();
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		batchNo=(DateUtil.getDateyestoday(Integer.parseInt(sdf.format(date)))+"").substring(2);//要结算的批次号【前一天的年（年取后两位）月日】
		Map<String, String> citicMap=sysdao.getMerNoByGid(CITIC_GID);
		merId=citicMap.get("mer_no");
		CiticLoginService loginService=CiticLoginService.getInstance();
		call=loginService.getCall();
		call.setOperationName("getQuestResult");
		String xml=getSynOrderXml();
		String result = "";
		try {
			result = (String) call.invoke(new Object[] { xml });
			handleSynOrder(result);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步订单
	 * @param records 订单信息
	 */
	public static void handleSynOrder(String resultXml){
		Document doc = null;
		Element root =null;
		Map<String, String> orderIdMap=new HashMap<String, String>();//所有银行成功的订单,key为订单号，值为“授权号##银行流水”（不包括请款成功且跑批成功）
		try {
			doc = DocumentHelper.parseText(resultXml);
			root = doc.getRootElement();
			String retCode=root.element("retCode").getText();
			if(!"0000000".equals(retCode)){
				LogUtil.printErrorLog("CiticSettlement", "handleSynOrder", "同步订单失败,失败原因:"+root.element("commentRes").getText());
				settlement();//同步订单失败，进行结算
				return;
			}
			Element dataSet = root.element("dataSet");
			List<Element> elems=dataSet.elements();
			//将所有银行成功的订单加入map
			for (int i = 0; i < elems.size(); i++) {
				String orderID=elems.get(i).getText().substring(45,64).trim();//订单号
				String authorizeCode=elems.get(i).getText().substring(98,104).trim();//授权号
				String systemRefCode=elems.get(i).getText().substring(80,92).trim();//银行流水
				orderIdMap.put(orderID, authorizeCode+"##"+systemRefCode);
			}
			List<Tlog>  tlogs=settlementDao.getCiticSucOrder(batchNo);
			//去掉所有我们系统已经成功的订单号，剩下的即银行成功，我们未成功的
			for (Tlog tlog : tlogs) {
				orderIdMap.remove(tlog.getTseq());
			}
			Set<String> keySet=orderIdMap.keySet();
			for (String key : keySet) {
				int result=settlementDao.updatetCiticOrderState(key,orderIdMap.get(key),batchNo);
				if(result!=1)
					LogUtil.printInfoLog("CiticSettlement", "handleSynOrder", "订单状态更新失败,订单号:"+key);
				else
				LogUtil.printInfoLog("CiticSettlement", "handleSynOrder", "订单状态更新成功,订单号:"+key);	
			}
			settlement();//同步订单成功，进行结算
			return;
		} catch (DocumentException e) {
			LogUtil.printErrorLog("CiticSettlement", "handleSynOrder", "同步订单出错,错误原因:"+(root==null?"解析XML失败!":e.getMessage()));
			settlement();//同步订单失败，进行结算
//			e.printStackTrace();
		}
	}
	
	public String getSynOrderXml(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		DateUtil.getIntDateTime();
		StringBuffer xmlStr=new StringBuffer();
		xmlStr.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		xmlStr.append("<message method=\"getQuestResult\" type=\"request\">");
		xmlStr.append("<infoType>0200</infoType>");
		xmlStr.append("<terminalID>"+TERMINALID+"</terminalID>");
		xmlStr.append("<posTime>"+posTime+"</posTime>");
		xmlStr.append("<posID>"+posId+"</posID>");
		xmlStr.append("<merchantID>"+merId+"</merchantID>");
		xmlStr.append("<merchantName></merchantName>");
		xmlStr.append("<batchNo>"+batchNo+"</batchNo>");
		xmlStr.append("<transBeginDate>20"+batchNo+"</transBeginDate>");
		xmlStr.append("<transBeginTime>00000001</transBeginTime>");
		xmlStr.append("<transEndDate>20"+batchNo+"</transEndDate>");
		xmlStr.append("<transEndTime>23595959</transEndTime>");
		xmlStr.append("<sequelFlag></sequelFlag>");
		xmlStr.append("<offsetDate></offsetDate>");
		xmlStr.append("<offsetTime></offsetTime>");
		xmlStr.append("<retCode></retCode>");
		xmlStr.append("<dataSet></dataSet>");
		xmlStr.append("<commentRes></commentRes>");
		xmlStr.append("<reserved></reserved>");
		xmlStr.append("</message>");
		return xmlStr.toString();
	}
	
	
	
	
	/**
	 * 结算请款
	 */
	public static void settlement(){
		sumAmt=String.format("%0"+13+"d", settlementDao.getCiticSumAmt(batchNo));//总金额(不足13位左补零)
		sumCount=settlementDao.getCiticSumCount(batchNo);//总条数
		String xml=getSettlementXml();//结算报文
		call.setOperationName("settltment");
		try {
			String  result = (String) call.invoke(new Object[] { xml });
			handleSettlementRet(result);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 结算报文
	 * @return
	 */
	public static String getSettlementXml(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		StringBuffer xmlStr=new StringBuffer();
		xmlStr.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		xmlStr.append("<message method=\"settltment\" type=\"request\">");
		xmlStr.append("<infoType>0200</infoType>");
		xmlStr.append("<posTime>");
		xmlStr.append(posTime);
		xmlStr.append("</posTime>");
		xmlStr.append("<posID>");
		xmlStr.append(posId);
		xmlStr.append("</posID>");
		xmlStr.append(" <retCode></retCode>");
		xmlStr.append("<terminalID>"+TERMINALID+"</terminalID>");
		xmlStr.append("<merchantID>");
		xmlStr.append(merId);
		xmlStr.append("</merchantID>");
		xmlStr.append("<merchantName></merchantName>");
		xmlStr.append("<batchNo>");
		xmlStr.append(batchNo);
		xmlStr.append("</batchNo>");
		xmlStr.append("<totalTrsCnt>");
		xmlStr.append(sumCount);
		xmlStr.append("</totalTrsCnt>");
		xmlStr.append("<sign>+</sign>");
		xmlStr.append("<traSumAmt>");
		xmlStr.append(sumAmt);
		xmlStr.append("</traSumAmt>");
		xmlStr.append("<successFlag></successFlag>");
		xmlStr.append("<commentRes></commentRes>");
		xmlStr.append("<reserved></reserved>");
		xmlStr.append("</message>");
		return xmlStr.toString();
	}
	
	/**
	 * 对账明细
	 */
	public static void checkAccount(){
		String [] checkAcountXml=getCheckAccountXml();
		call.setOperationName("checkAccount ");
		for (int i = 0; i < checkAcountXml.length; i++) {
			try {
				String  result = (String) call.invoke(new Object[] { checkAcountXml[i] });
				LogUtil.printInfoLog("CiticSettlement", "checkAccount","中信无磁无密明细对账完成！");
			} catch (RemoteException e) {
				LogUtil.printInfoLog("CiticSettlement", "checkAccount", "中信无磁无密对账明细出错。错误原因："+e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 对明细数据报文
	 * @return
	 */
	public static String[] getCheckAccountXml(){
		int eachNum=1;//循环组织报文次数（请求次数）
		StringBuffer xmlStr=null;//最终返回的数据
		List<Tlog> tempList=new ArrayList<Tlog>();
		Date date=new Date();
		if(settlementDao==null)
			settlementDao=new SettlementDao();
		List<Tlog> data=settlementDao.getCiticDataSet(batchNo);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		int dataCount=data.size();
		if(dataCount<=0)return null;
		if(dataCount/20>0&&dataCount%20>0)
			eachNum=(dataCount/20)+1;
		String [] xmls=new String[eachNum];//装入请求报文的数组
		for (int i = 0; i < eachNum; i++) {
			tempList=data.subList(i*20, (i*20)+19>=dataCount?dataCount:(i*20)+19);
			xmlStr=new StringBuffer();
			xmlStr.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
			xmlStr.append("<message method=\"checkAccount\" type=\"request\">");
			xmlStr.append("<infoType>0200</infoType>");
			xmlStr.append("<posTime>");
			xmlStr.append(posTime);
			xmlStr.append("</posTime>");
			xmlStr.append("<posID>");
			xmlStr.append(posId);
			xmlStr.append("</posID>");
			xmlStr.append(" <retCode></retCode>");
			xmlStr.append("<terminalID>"+TERMINALID+"</terminalID>");
			xmlStr.append("<merchantID>");
			xmlStr.append(merId);
			xmlStr.append("</merchantID>");
			xmlStr.append("<merchantName></merchantName>");
			xmlStr.append("<batchNo>");
			xmlStr.append(batchNo);
			xmlStr.append("</batchNo>");
			xmlStr.append(getDataSet(tempList));
			xmlStr.append("<terminalFlag>"+(i==(eachNum-1)?"Y":"N")+"</terminalFlag>");
			xmlStr.append("<commentRes></commentRes>");
			xmlStr.append("<reserved></reserved>");
			xmlStr.append("</message>");
			xmls[i]=xmlStr.toString();
		}
		return xmls;
	}
	
	/**
	 * 明细对账明细记录
	 * @param data
	 * @return
	 */
	public static String getDataSet(List<Tlog> data){
		StringBuffer xmlStr=new StringBuffer();
		xmlStr.append("<dataSet  count=\""+data.size()+"\" >");
		for (Tlog tlog : data) {
			xmlStr.append("<record>");
			xmlStr.append(merId);
			xmlStr.append(TERMINALID);
			xmlStr.append(batchNo);
			xmlStr.append(Ryt.creatSpecifyValue(16," "));
			xmlStr.append(tlog.getTseq()+Ryt.creatSpecifyValue(19-tlog.getTseq().length(), " "));//订单号右补空格
			xmlStr.append(String.format("%0"+13+"d", tlog.getPayAmt()));
			xmlStr.append(156);
			xmlStr.append(Ryt.creatSpecifyValue(12," "));
			xmlStr.append(tlog.getP1());
			xmlStr.append(tlog.getP7());
			xmlStr.append("000000");
			xmlStr.append("00");
			xmlStr.append("  ");
			xmlStr.append(Ryt.creatSpecifyValue(36," "));
			xmlStr.append("</record>");
		}
		xmlStr.append("</dataSet>");
		return xmlStr.toString();
	}
	
	/**
	 * 处理结算返回报文  如不成功则进行明细对账
	 * @param retXml
	 */
	public static void handleSettlementRet(String retXml){
		if(Ryt.empty(retXml)){
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款返回结果为空!");
			checkAccount();//对账明细
			return ;
		}
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(retXml);
		} catch (DocumentException e) {
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款返回报文处理异常! "); 
			checkAccount();//对账明细
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		Element successFlag = root.element("successFlag");
		if(successFlag==null){
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款返回报文解析异常!");
			checkAccount();//对账明细
			return ;
		}
		if(successFlag.getText().equals("A")){
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款成功!");
			return;
		}else if(successFlag.getText().equals("B")){
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "该批次已请款!");
			return;
		}
		else{
			LogUtil.printInfoLog("CiticSettlement", "handleSettlementRet", "中信无磁无密请款失败,已调用明细对账处理。错误原因："+(Ryt.empty(successFlag.getText())?root.element("commentRes").getText():errMsgMap.get(successFlag.getText())));
			checkAccount();//对账明细
			return;
		}
	}
	
	static {
		errMsgMap=new HashMap<String, String>();
		errMsgMap.put("B", "已核对");
		errMsgMap.put("C", "核对失败");
		errMsgMap.put("D", "交易不存在");
		errMsgMap.put("E", "主机多出交易");
		errMsgMap.put("M", "金额不符");
		errMsgMap.put("T", "POS流水号不符");
		errMsgMap.put("R", "主机不成功商户成功");
	}
	/**
	 * 撤销
	 */
	public static void cancelXml(){
		String xml=getCancelXml();
		call.setOperationName("dividedPaymentReversal");
		try {
			String result = (String) call.invoke(new Object[] { xml });
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 撤销xml
	 * @return
	 */
	public static String getCancelXml(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<message method=\"dividedPaymentReversal\" type=\"request\">");
		sb.append("<infoType>0420</infoType>");
		sb.append("<pan>4041570000048343</pan>");//卡号
		sb.append("<orderID>9100004024</orderID>");
		sb.append("<processCode>002000</processCode>");
		sb.append("<transAmt>"+String.format("%0"+13+"d", 10)+"</transAmt>");
		sb.append("<posTime>"+posTime+"</posTime>");
		sb.append("<posID>"+posId+"</posID>");
		sb.append("<inputType>021</inputType>");
		sb.append("<cardSerialNo></cardSerialNo>");
		sb.append("<posConditionCode>02</posConditionCode>");
		sb.append("<secondTrack></secondTrack>");
		sb.append("<thirdTrack></thirdTrack>");
		sb.append("<systemRefCode>161438502012</systemRefCode>");
		sb.append("<authorizeCode>160285</authorizeCode>");
		sb.append("<orgPosID>004024</orgPosID>");
		sb.append("<retCode></retCode>");
		sb.append("<terminalID>"+TERMINALID+"</terminalID>");
		sb.append("<merchantID>"+merId+"</merchantID>");
		sb.append("<merchantName>测试商户</merchantName>");
		sb.append("<commentRes></commentRes>");
		sb.append("<currCode>156</currCode>");
		sb.append("<passwdMac></passwdMac>");
		sb.append("<securityInfo></securityInfo>");
		sb.append("<termAbilities></termAbilities>");
		sb.append("<personalMsg></personalMsg>");
		sb.append("<icDataField></icDataField>");
		sb.append("<chIdNum>430281198506154548</chIdNum>");
		sb.append("<chName>谢大脚</chName>");
		sb.append("<chMobile>13632649296</chMobile>");
		sb.append("<cvv2>803</cvv2>");
		sb.append("<expiredDate>2108</expiredDate>");
		sb.append("<dynamicPwd></dynamicPwd>");
		sb.append("<dividedNum>00</dividedNum>");
		sb.append("<productType>000000</productType>");
		sb.append("<batchNo>141239</batchNo>");
		sb.append("<reserved1></reserved1>");
		sb.append("<reserved2></reserved2>");
		sb.append("</message>");
		return sb.toString();
	}
	
	/**
	 * 下载流水文件
	 * @param downLoadDate 8位数字
	 */
	public static void downloadBill(String downLoadDate){
		call.setOperationName("download");
		String xml=getDownloadXml(downLoadDate);
		try {
			String result = (String) call.invoke(new Object[] { xml });
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
	} 
	
	/**
	 * 下载流水文件XML
	 * @param downLoadDate 8位数字
	 * @return
	 */
	public static String getDownloadXml(String downLoadDate){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
	    sb.append("<message method=\"download\" type=\"request\">");
	    sb.append("<infoType>0200</infoType>");
	    sb.append("<posTime>"+posTime+"</posTime>");
		sb.append("<posID>"+posId+"</posID>");
		sb.append("<retCode></retCode>");
		sb.append("<terminalID>"+TERMINALID+"</terminalID>");
		sb.append("<merchantID>"+merId+"</merchantID>");
		sb.append("<fileType>A</fileType>");
		sb.append("<dataSet></dataSet>");
		sb.append("<date>"+downLoadDate+"</date>");
		sb.append("<commentRes></commentRes>");
		sb.append("<reserved></reserved>");
		sb.append("</message>");
		return sb.toString();
	}
	
	/**
	 * 退款
	 */
	public static void refundAmt(){
		call.setOperationName("hirePurchaseReturn");
		String xml=getRefundAmtXml();
		try {
			String result = (String) call.invoke(new Object[] { xml });
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 退款XML
	 * @return
	 */
	public static String getRefundAmtXml(){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String posTime=sdf.format(date);
		String posId=posTime.substring(posTime.length()-6);
		StringBuffer sb=new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<message method=\"hirePurchaseReturn\" type=\"request\">");
		sb.append("<infoType>0200</infoType>");
		sb.append("<Pan>4041570000048343</Pan>");
		sb.append("<orderID>9100004025</orderID>");
		sb.append("<processCode>002000</processCode>");
		sb.append("<orgTransAmt>"+String.format("%0"+13+"d", 10)+"</orgTransAmt>");
		sb.append("<transAmt>"+String.format("%0"+13+"d", 10)+"</transAmt>");
		sb.append("<posTime>"+posTime+"</posTime>");
		sb.append("<posID>"+posId+"</posID>");
		sb.append("<transTime></transTime>");
		sb.append("<transDate></transDate>");
		sb.append("<inputType>021</inputType>");
		sb.append("<cardSerialNo></cardSerialNo>");
		sb.append("<posConditionCode>02</posConditionCode>");
		sb.append("<secondTrack></secondTrack>");
		sb.append("<thirdTrack></thirdTrack>");
		sb.append("<systemRefCode>161456042030</systemRefCode>");
		sb.append("<authorizeCode>160292</authorizeCode>");
		sb.append("<retCode></retCode>");
		sb.append("<terminalID>"+TERMINALID+"</terminalID>");
		sb.append("<merchantID>"+merId+"</merchantID>");
		sb.append("<merchantName>测试商户</merchantName>");
		sb.append("<commentRes></commentRes>");
		sb.append("<currCode>156</currCode>");
		sb.append("<orgPosID>004025</orgPosID>");
		sb.append("<orgBatchNo>141239</orgBatchNo>");
		sb.append("<passwdMac></passwdMac>");
		sb.append("<securityInfo></securityInfo>");
		sb.append("<icDataField></icDataField>");
		sb.append("<termAbilities></termAbilities>");
		sb.append("<personalMsg></personalMsg>");
		sb.append("<chIdNum>430281198506154548</chIdNum>");
		sb.append("<chName>谢大脚</chName>");
		sb.append("<chMobile>13632649296</chMobile>");
		sb.append("<cvv2>803</cvv2>");
		sb.append("<expiredDate>2108</expiredDate>");
		sb.append("<dynamicPwd></dynamicPwd>");
		sb.append("<dividedNum>00</dividedNum>");
		sb.append("<productType>000000</productType>");
		sb.append("<batchNo>111225</batchNo>");
		sb.append("<reserved1></reserved1>");
		sb.append("<reserved2></reserved2>");
		sb.append("</message>");
		return sb.toString();
	}
}
