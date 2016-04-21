package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.bean.UserBkAcc;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.CompanyDao;
import com.rongyifu.mms.dao.MerZHDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.GenRequestParamUtil;

@SuppressWarnings("unchecked")
public class CompanyService {
	
	
	private CompanyDao companyDao = new CompanyDao();
	private MerZHDao merZHDao = new MerZHDao();
	/**
	 * 订单明细查询
	 */
	public List<Object> queryOrderDetails(String accName,String oid)  {
		try {
		List a=new ArrayList();
		TrOrders b=companyDao.queryOrderDetials(accName,oid);
		a.add(b);
		String tseq=b.getTseq();
		if(tseq!=""){
			String orderDate=(b.getInitTime()+"").substring(0,8);
			String date=DateUtil.getNowDateTime().substring(0,8);
			if(orderDate.equals(date)){
				Hlog h=companyDao.queryMX(tseq,"tlog");
				if(h!=null)a.add(h);
			}else{
				
				Hlog h2=companyDao.queryMX(tseq,"hlog");
				if(h2!=null)a.add(h2);
			}
		}
		
		return a;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据商户号 查询 <账户号,银行>的map
	 * @return
	 */
	public List<UserBkAcc> getBkAccList(String mid){
		List<UserBkAcc> bkAccList= companyDao.getBkAccMap(mid);
		if(bkAccList.size()<=0){
			return null;
		}
		List<UserBkAcc> newAccList=new ArrayList<UserBkAcc>();
		for (int i = 0; i < bkAccList.size(); i++) {
			UserBkAcc userBkAcc=bkAccList.get(i);
			String accNo=userBkAcc.getAccNo();
			if(!accNo.equals("")&&accNo.length()>6){
				accNo=accNo.substring(accNo.length()-6,accNo.length());//取银行卡的后六位
			}
			userBkAcc.setAccNo(accNo);
			newAccList.add(userBkAcc);
		}
		return newAccList;
	}
	/**
	 * 修改订单状态为银行处理中
	 * @param ordId
	 * @param phoneNo
	 * @return
	 */
	public String submitB2bOrder(String ordId,String phoneNo){
		return new MerZHService().submitB2bOrder(ordId, phoneNo);
	}
	/**
	 * 查询订单状态
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public String queryOrderState(String orderId) throws Exception{
		return new MerZHService().queryOrderState(orderId);
	}
	/**
	 * 获得付款到商户的加密参数
	 * @param comName
	 * @param mid
	 * @param bkAccId
	 * @param transAmt
	 * @param phoneNo
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	public Map<String,?> getPayToMerParam(String comName,String mid,Integer bkAccId,
									String transAmt,String phoneNo,String remark) throws Exception{
		UserBkAcc userBkAcc=companyDao.getUserBkAccById(bkAccId);//收款方银行账号信息
		AccInfos accInfos=merZHDao.queryFK(mid);//收款方电银账户信息
		//计算手续费
		String feeMode=accInfos.getDaifuFeeMode();
		String transFee=ChargeMode.reckon(feeMode, transAmt);
		
		long savePayAmt=Ryt.mul100toInt(transAmt)-Ryt.mul100toInt(transFee);//实际交易金额（乘100后的）
		Integer saveTransFee=(int)Ryt.mul100toInt(transFee);
		long savetTransAmt=Ryt.mul100toInt(transAmt);
		
		String ordId=Ryt.genOidBySysTime();
		Integer gate=userBkAcc.getGate();
		Map paramMap=GenRequestParamUtil.getParamKeyMap(savetTransAmt, saveTransFee, gate, ordId)	;
		paramMap.put("userBkAcc", userBkAcc);
		
		 TrOrders trOrders=new TrOrders();
		 	trOrders.setOid(ordId);
			trOrders.setUid(mid);//使用结算账户付款
			trOrders.setAid(mid);
			trOrders.setAname(accInfos.getAname());
			trOrders.setInitTime(DateUtil.getIntDateTime());
			trOrders.setGate(gate);
			trOrders.setPtype((short)6);//商户从银行收款
			trOrders.setTransAmt(savetTransAmt);
			trOrders.setTransFee(saveTransFee);
			trOrders.setPayAmt(savePayAmt);
			trOrders.setToUid(userBkAcc.getUid());
			trOrders.setToAid(userBkAcc.getId()+"");
			trOrders.setAccName(comName);
			trOrders.setToAccName(userBkAcc.getAccName());
			trOrders.setToAccNo(userBkAcc.getAccNo());
			trOrders.setToBkName(userBkAcc.getBkName());
			trOrders.setToBkNo(userBkAcc.getBkNo());
			trOrders.setState((short)0);
			trOrders.setRemark(remark);
			trOrders.setSmsMobiles(phoneNo);
		int row=companyDao.payToMerchant(trOrders);//保存订单
		if(row!=1){
			throw new Exception("提交的订单异常！请重试，或联系客服人员"); 
		}
		return paramMap;
	}
}
