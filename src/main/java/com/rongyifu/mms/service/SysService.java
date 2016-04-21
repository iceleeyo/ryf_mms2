package com.rongyifu.mms.service;


import com.rongyifu.mms.bank.b2e.B2EProcess;
import com.rongyifu.mms.bank.b2e.B2ERet;
import com.rongyifu.mms.bank.b2e.B2ETrCode;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.TrOrders;
//import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.MerZHDao;
import com.rongyifu.mms.exception.B2EException;

public class SysService {

	private MerZHDao merZHDao = new MerZHDao();
	
	private static final short CHONG_ZHI = 0;
	private static final short FU_KUAN = 5;
	private static final short SHOU_KUAN = 6;


	/**
	 * 订单交易成功
	 * @param oid
	 * @param tseq
	 * @throws Exception
	 */
	public void doOrderWhenSuccess(String oid, String tseq,String transAmt) throws Exception {
		
		
		TrOrders o = merZHDao.queryTrOrderByOid(oid);
		
		if(null==o) throw new Exception("TrOrders is null,oid:" + oid); 

		//可以不要比对金额，金额从表里边取
		//if(Ryt.mul100toInt(transAmt) != o.getTransAmt())  throw new Exception("transAmt error db100:" + o.getPayAmt()); 
		
		short t = o.getPtype();
		
		if (t == CHONG_ZHI) {// 0-充值
		
			merZHDao.accChongZhi(o, tseq);
		
		} else if (t == FU_KUAN || t==SHOU_KUAN) {// 5-付款到银行卡 6-从银行卡收款
		
			doB2EAction(o, tseq);
		
		} else {
			
			throw new Exception("error ptype");
		
		}
	}
	
	/**
	 * @param oid
	 * @param tseq
	 * @throws Exception
	 */
	public void doOrderWhenFail(String oid) throws Exception {
		merZHDao.doUpdateOrderFail(oid);
	}

	private void doB2EAction(TrOrders o, String tseq) throws B2EException {
		
//		网银支付网关号为20000.20001，。。。。银企直连网关号对应为 40000，400001.。。。
//		int gid = o.getGate() + 20000;
		
		int gid = 0;
		
		if ( 20000 <= o.getGate() && o.getGate() < 30000){
			gid = o.getGate() + 20000;
		}
		if ( 40000 <= o.getGate() && o.getGate() < 50000){
			gid = o.getGate();
		}
		if(gid==0)	throw new B2EException("没有该企业网银：gid：" + gid);;
		
		
		
		AdminZHDao dao = new AdminZHDao();
		B2EGate g = dao.getOneB2EGate(gid);
		if(null==g) throw new B2EException("没有该企业网银：gid：" + gid);
		
		B2EProcess obj = new B2EProcess(g,B2ETrCode.PAY_TO_OTHER);
		obj.setOrders(o);
		B2ERet ret = obj.submit();
		
		int state = 0;
		if(ret.isSucc()){//调用银企直连成功
			//每5分钟进行查询交易状态 ，不进行流水处理，为待付款状态
			//5-付款处理中 6-付款失败
			state = 5;
		}else{
			state = 6;
		}
		
		dao.updateTrOrdersState(gid,o.getOid(), state,tseq, ret.getMsg());
		
	}

}
