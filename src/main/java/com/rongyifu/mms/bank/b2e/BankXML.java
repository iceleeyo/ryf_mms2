package com.rongyifu.mms.bank.b2e;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.exception.B2EException;

public interface BankXML {
	
	String genSubmitXML(int trCode,B2EGate gate) throws B2EException ;
	
	String genSubmitXML(int trCode,TrOrders os,B2EGate gate) throws B2EException ;
	
	void parseXML(B2ERet ret,String xml)throws B2EException ;
	
	
	
}
