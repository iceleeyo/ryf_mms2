package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.common.Ryt;

/**
 * 北京银行（增值）
 * @author m
 *
 */
public class CheckDataBJImp implements SettltData {

	@SuppressWarnings("unchecked")
	@Override
	public List<SBean> getCheckData(String bank, String fileContent) throws Exception {
		/* 解析XML */
//		<?xml  version="1.0" encoding="GB2312" standalone="no" ?>
//		<KKFile>
//		<!--===================注意：本文件中的金额均以分为单位！================-->
//		<Header HeaderStr="#商城代码|订单号|客户付款帐号|交易类型（01支付 02支付不成功 03退款 04部分退款）|交易金额|交易日期|"/>
//		<RecordList>
//		<Record MerCode="000000000061" OrderNum="CH110916144000717" PayAcctNo="6210300005539348" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916190212648" PayAcctNo="6029695050032102" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916204831822" PayAcctNo="6029693080000181" PayType="01" Amt="000000020000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110915224224355" PayAcctNo="6210300004333529" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110915234307909" PayAcctNo="6210300220016650" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916070209488" PayAcctNo="4221613800173861" PayType="01" Amt="000000010000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916152140851" PayAcctNo="4213173000305888" PayType="01" Amt="000000010000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916165747212" PayAcctNo="4221603600262972" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916184717502" PayAcctNo="6210300007632638" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916184747308" PayAcctNo="6210300007632638" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916164436217" PayAcctNo="6210300006419193" PayType="01" Amt="000000005000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916142410339" PayAcctNo="6029692015719998" PayType="01" Amt="000000020000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916145103403" PayAcctNo="6029693024266914" PayType="01" Amt="000000010000" TranTime="20110916" />
//		<Record MerCode="000000000061" OrderNum="CH110916085826195" PayAcctNo="4213173000546069" PayType="01" Amt="000000010000" TranTime="20110916" />
//		</RecordList>
//		</KKFile>
		
		
		Document doc = DocumentHelper.parseText(fileContent);
		Element root = doc.getRootElement();//KKFile
		Element recordList = root.element("RecordList");
		Iterator iterator = recordList.elementIterator();
		
		List<SBean> res = new ArrayList<SBean>();
		while(iterator.hasNext()){
			Element el = (Element)iterator.next();
			String record = el.getName();
			if(!record.equalsIgnoreCase("Record")) continue;
			String PayType = el.attributeValue("PayType");
//			交易类型（01支付 02支付不成功 03退款 04部分退款）
			if(null == PayType || !PayType.equals("01"))  continue;
//			String MerCode = el.attributeValue("MerCode");
//			String PayAcctNo = el.attributeValue("PayAcctNo");
//			注意：本文件中的金额均以分为单位！
			String Amt = Ryt.div100(Integer.parseInt(el.attributeValue("Amt").trim()));
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setMerOid(el.attributeValue("OrderNum"));
			bean.setDate(el.attributeValue("TranTime"));
			bean.setAmt(Amt);
			res.add(bean);
		}
		
		return res;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m) throws Exception {
		return null;
	}
	

}
