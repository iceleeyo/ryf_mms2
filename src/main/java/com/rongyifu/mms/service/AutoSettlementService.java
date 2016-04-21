package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
//import org.directwebremoting.io.FileTransfer;
import org.dom4j.Element;
import org.springframework.dao.DuplicateKeyException;

import com.rongyifu.mms.bank.BOCOMSettleCore;
import com.rongyifu.mms.bank.b2e.B2EProcess;
import com.rongyifu.mms.bank.b2e.B2ERet;
import com.rongyifu.mms.bank.b2e.B2ETrCode;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.BkAccount;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.PaymentBean;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.AutoSettlementDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class AutoSettlementService {
	private Logger logger  =  Logger.getLogger(AutoSettlementService. class );
	
	private AutoSettlementDao autoSettlementDao = new AutoSettlementDao();
	private static Map<Integer, String> payStateMap = new HashMap<Integer, String>();
	private static Map<Integer, String> bank_no_info=new HashMap<Integer, String>();
	static {
		payStateMap.put(9, "初始状态");
		payStateMap.put(0, "银行处理中");
		payStateMap.put(1, "成功");
		payStateMap.put(2, "失败");
		payStateMap.put(3, "可疑");
		payStateMap.put(4, "记录不存在"); 

	}
	// 调用银行接口,查询用户信息
	public List<BkAccount> getAccInfo(String accNo) throws Exception {
		String responseXML = BOCOMSettleCore.do310101(accNo);
		Element body=BOCOMSettleCore.getBody(responseXML);
		
		String serial_record=body.element("serial_record").getText();
		String field_num=body.element("field_num").getText();
		int fieldNum=Integer.parseInt(field_num);
		
		String[] info = serial_record.split("\\|");
		BkAccount bkAccount = new BkAccount();
		String errorMsg=info[8 +fieldNum].trim();
		if(!errorMsg.equals("")){
			bkAccount.setErrorMsg(errorMsg);
		}else{
			bkAccount.setAccName(info[fieldNum]);
			bkAccount.setAccNo(info[1 +fieldNum]);
			bkAccount.setCurrency(info[2 +fieldNum]);
			bkAccount.setBkBl(Long.parseLong(Ryt.mul100(info[3 + fieldNum])));
			bkAccount.setBfBl(Long.parseLong(Ryt.mul100(info[4 +fieldNum])));
			bkAccount.setOperDate(Integer.parseInt(info[5 +fieldNum]));
			bkAccount.setAccType(Integer.parseInt(info[6 +fieldNum]));
			bkAccount.setBkName(info[7 +fieldNum]);
		}
		List<BkAccount> bkAccountList=new ArrayList<BkAccount>(); 
		bkAccountList.add(bkAccount);
		return bkAccountList;
	}
	//插入存管银行信息
	public String insertInfo(BkAccount bkAccount) {
		try {
			int countBkNo=autoSettlementDao.bkNoIsExist(bkAccount.getAccNo());
			if(countBkNo>0){
				return "该银行卡号已经录入,不能重复!";
			}
			bkAccount.setBkType(1);//1为存管银行
			bkAccount.setBkAbbv("交通银行");
			int effectRow = autoSettlementDao.addHoldBk(bkAccount);
			if(effectRow==1){
				return AppParam.SUCCESS_FLAG;
			}else{
				return "录入失败！";
			}
		} catch (Exception e) {
			return "操作异常，请重新再试或与管理员联系！";
		}
	}

	// 修改银行卡号
	public String alterAccNo(String oldNo, String newNo, String bkType) {

		try {
			autoSettlementDao.alterAccNo(oldNo, newNo, bkType);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "操作异常，请重新再试或与管理员联系！";
		}
		return "ok";
	}
	// 获得账号和户名的列表
	public Map<String, String> AccNoMatchName() {

		return autoSettlementDao.getAcc();
	}

	// 查询数据库获得账号信息
	public BkAccount getLocalAccInfo(String acc_no) {

		return autoSettlementDao.getLocalAccInfo(acc_no);
	}

	/**
	 * 划款录入
	 * @param batchs  批次号以,分割
	 * @return
	 * @throws Exception 
	 */
	public String transferMoneyInput(String[] batchs,int gid) throws Exception {
		
		if (batchs == null || batchs.length==0) return "请选择你要录入的记录！";
		String errorMsg = "";
		for (String batch : batchs) {
			FeeLiqBath feeLiqBath = autoSettlementDao.getFeeLiqBathByBatch(batch);
			if(feeLiqBath == null ) continue;
			Minfo minfo = autoSettlementDao.getMinfoById(feeLiqBath.getMid().toString());
			if(minfo==null) continue;
			
			TrOrders o = new TrOrders();
			o.setOid(Ryt.genOidBySysTime());
			o.setUid(minfo.getId()+"");
			o.setAid(minfo.getId()+"");
			o.setAname("结算账户");
			o.setInitTime(DateUtil.getIntDateTime());
			o.setSysDate(DateUtil.today());
			o.setSysTime(DateUtil.getCurrentUTCSeconds());
			o.setOperDate(DateUtil.today());
			
			o.setGate(gid);
			o.setPtype((short)8);
			o.setOrgOid(String.valueOf(batch));//原始交易订单号
			long payAmt =feeLiqBath.getLiqAmt();
			o.setTransAmt(payAmt);
			o.setTransFee(0);
			o.setPayAmt(payAmt);
			o.setToAccName(minfo.getBankAcctName());
			o.setToAccNo(minfo.getBankAcct());
			o.setToBkName(minfo.getBankName());
			o.setToBkNo(minfo.getOpenBkNo());
			o.setToProvId(minfo.getBankProvId());
			o.setState((short)0);
			o.setPriv("结算划款");
			o.setRemark(autoSettlementDao.getLoginUserName() + ",划款录入");
			
			int effectRows = autoSettlementDao.saveObject(o);
			
			if(effectRows>0){
				
				// TODO 测试线不修改
				autoSettlementDao.updateFeeLiqBathSlogFlagTo1(batch);
			}else{
				errorMsg = "流水号为：" + batch + ",";
			}
		}
		return errorMsg.equals("") ? "ok" : errorMsg + "划款录入失败，请重试！";
		
	
	}
		
//	public String transferMoneyInput(long[] batchs) throws Exception {
//		if (batchs == null || batchs.length==0) return "请选择你要录入的记录！";
//		BkAccount bkAccount = autoSettlementDao.getCGBank();
//		if(bkAccount==null) return "存管银行账号不存在，请先录入后再进行此步操作！";
//		String errorMsg = "";
//		for (long batch : batchs) {
//			FeeLiqBath feeLiqBath = autoSettlementDao.getFeeLiqBathByBatch(batch);
//			if(feeLiqBath == null ) continue;
//			Minfo minfo = autoSettlementDao.getMinfoById(feeLiqBath.getMid());
//			if(minfo==null) continue;
//			
//			Slog slog = new Slog();
//			slog.setBatch(batch);
//			slog.setPayAcname(bkAccount.getAccName());
//			slog.setPayAcno(bkAccount.getAccNo());
//			slog.setMid(minfo.getId());
//			slog.setMname(minfo.getName());
//			slog.setRcvBankName(minfo.getBankBranch());
//			slog.setRcvAcno(minfo.getBankAcct());
//			slog.setRcvAcname(minfo.getBankAcctName());
//			slog.setRcvAddr(minfo.getAddr());
//			slog.setRcvBank(minfo.getBankName());
//			slog.setRcvLhh(minfo.getOpenBkNo());
//			slog.setLiqAmt(feeLiqBath.getLiqAmt());
//			slog.setAddDate(DateUtil.today());
//			slog.setAddOperId(autoSettlementDao.getLoginUser().getOperId());
//			slog.setTransType(1);
//			slog.setLiqState(2);
//			slog.setPayState(9);
//			slog.setFlag1(0);
//			slog.setIsSameProv(minfo.getBankProvId() == 310 ? 0 : 1);
//			
//			int effectRows = autoSettlementDao.addSlog(slog);
//			if(effectRows>0){
//				autoSettlementDao.updateFeeLiqBathSlogFlagTo1(batch);
//			}else{
//				errorMsg = "流水号为：" + batch + ",";
//			}
//		}
//		return errorMsg.equals("") ? "ok" : errorMsg + "划款录入失败，请重试！";
//	}

	/*
	 * 查询录入信息
	 */
	public CurrentPage<FeeLiqBath> getTransferData(int pageNo, int bdate,int edate,String mid, String batch) {
		return autoSettlementDao.getFeeLiqBath(pageNo,bdate,edate,mid,batch);
	}

	/***
	 * 划款确认查询
	 * 
	 * @param pageNo
	 * @param bdate
	 * @param edate
	 * @param mid
	 * @param batch
	 * @return
	 */
	public CurrentPage<TrOrders> getTransMoneyInputPage(int pageNo, int bdate,
			int edate, String mid, String batch) {
		return autoSettlementDao.getSlogPage(pageNo, bdate,edate,mid,batch);
	}

	/**
	 * 划款确认
	 * @param mlogIds
	 * @return
	 * @throws Exception 
	 */
	public String transferMoneySure(String[] oids)throws Exception {
		if (oids == null || oids.length==0)
			return "选择为空，请选择你要划款的记录！";
		
		String errorMsg = "";

		AdminZHDao dao = new AdminZHDao();
		
		for (String oid : oids) {
			
			TrOrders o = dao.queryTrOrders(oid);
			if(null==o) continue;
			
			//对于已经失败的订单的处理6-付款失败
			if(o.getState()==6){
				o.setOid(Ryt.genOidBySysTime());
				o.setState((short)0);
				int i = dao.saveObject(o);
				if(i==1){
					//4-订单取消,重新生产订单重新执行付款操作
					dao.update("update tr_orders set state=4 where oid = " + oid);
				}else{
					continue;
				}
			}
			
			B2EGate g = dao.getOneB2EGate(o.getGate());
			if(null==g) continue;
			
			B2EProcess p = new B2EProcess(g,B2ETrCode.PAY_TO_OTHER);
			p.setOrders(o);
			
			B2ERet ret = p.submit();
			if(ret.isSucc()){
				String bkSeq =	(String)ret.getResult();
				dao.updateTrOrdersState(g.getGid(),oid, 5, bkSeq, ","+dao.getLoginUserName() + ",划款确认");//5-付款处理中
			}else{
				//dao.updateTrOrdersState(oid, 6, "",ret.getMsg());//6-付款失败
				errorMsg+= " 电银流水号为["+oid+"]的记录，银行返回："+ret.getMsg()+"；\n";
			}
		}
		
		if (errorMsg.equals("")) {
			return "ok";
		} else {
			return errorMsg;
		}
		
		
	}

		/**
		 * 划款结果处理查询
		 * 
		 * @param pageNo
		 * @param mid
		 * @param payState
		 * @param bdate
		 * @param edate
		 * @param batch
		 * @return
		 */
		public CurrentPage<TrOrders> queryResultHandle(int pageNo, String mid,int payState, int bdate, int edate, String batch) {
			return autoSettlementDao.getTrOrders(pageNo,mid,payState, bdate, edate, batch);
		}

//	/**
//	 * 下载划款结果处理
//	 * 
//	 * @param mid
//	 * @param payState
//	 * @param bdate
//	 * @param edate
//	 * @param batch
//	 * @return
//	 * @throws Exception
//	 */
//	public FileTransfer downLoadResultHandle(String mid, Integer payState,int bdate, int edate, long batch) throws Exception {
//		
//		String[] head = { "序号", "电银流水号", "	付款人户名", "付款人账号", "商户号", "商户简称",
//				"	收款所在地", "收款方开户行名", "收款人账号", "收款方交换号", "收款方联行号", "收款方银行",
//				"清算金额", "结算批次号", "结算状态", "划款确认日期", "划款状态" };
//		List<Slog> slogList = autoSettlementDao.getSlog4DownLoad(mid, payState, bdate, edate, batch);
//		ArrayList<String[]> list = new ArrayList<String[]>();
//		list.add(head);
//		int i = 0;
//		int totleLiqAmt = 0;
//		for (Slog slog : slogList) {
//			i++;
//			String[] slogStr = { i + "", slog.getId() + "",
//					slog.getPayAcname(), slog.getPayAcno(), slog.getMid() + "",
//					slog.getMname(), slog.getIsSameProv() == 0 ? "异地" : "同城",
//					slog.getRcvBankName(), slog.getRcvAcno(), slog.getRcvJhh(),
//					slog.getRcvLhh(), slog.getRcvBank(),
//					Ryt.div100(slog.getLiqAmt()), slog.getBatch() + "",
//					slog.getLiqState() == 2 ? "已制表" : "已确认",
//					slog.getSureDate() + "",
//					payStateMap.get(slog.getPayState()) };
//			list.add(slogStr);
//			totleLiqAmt += slog.getLiqAmt();
//
//		}
//		String[] strArr = { "总计:" + i + "条记录", "", "", "", "", "", "", "", "",
//				"", "", "", Ryt.div100(totleLiqAmt), "", "", "", "" };
//		list.add(strArr);
//		String filename = "resultHandle_" + DateUtil.today() + ".xls";
//		String name = "划款结果处理表";
//		return new DownloadFile().downloadXLSFileBase(list, filename, name);
//	}

	/**
	 * 合作银行账号新增
	 * @param accNo
	 * @param bkNo
	 * @param bkAbbv
	 * @return
	 */
	public String addCooperateBkNo(String bkNo,String bkAbbv){
		try {
			int effectRow = autoSettlementDao.addCooperateBkNo(bkNo, bkAbbv);
			if(effectRow==1){
				return AppParam.SUCCESS_FLAG;
			}else{
				return "合作银行行号新增失败！";
			}
		}catch (DuplicateKeyException e) {
			    return "录入失败！该银行行号已存在，请不要重复录入。";
		}catch (Exception e) {
			   return "合作银行行号新增失败！";
		}
	}
	/******************************/


	/**
	 *  单笔划款查询
	 * @param seqType
	 * @param webSerialNo
	 * @return
	 * @throws Exception 
	 */
	public PaymentBean querySinglePay(String seqType,String webSerialNo) throws Exception {
		String responseXML = BOCOMSettleCore.do310204(seqType, webSerialNo);
		PaymentBean paymentBean=wrapPaymentBean(responseXML);
		String state=paymentBean.getState();
		if ( state!= null &&!state.trim().equals("")&&!state.equals("4")){//修改slog的状态
			    autoSettlementDao.alterPayState(seqType,webSerialNo,state);
			if(state.trim().equals("1")){//修改结算状态为 "已完成"
				String batch=autoSettlementDao.getBatchById(seqType, webSerialNo);
				int row= autoSettlementDao.updateLiqBath(batch);
				if(row==1){//结算状态修改成功
					//new SettlementDao().doCreateCGData(batch);//记录存管银行数据
				}else{
					paymentBean.setErrMsg(paymentBean.getErrMsg()+"；但结算状态修改失败，请去结算确认手工确认。 ");
				}
			}
		}
		return paymentBean;
	}
	/**
	 * 封装  单笔划款查询的bean
	 * @param responseXML
	 * @return
	 */
	private PaymentBean wrapPaymentBean(String responseXML){
		PaymentBean payment = new PaymentBean();
		Element body = BOCOMSettleCore.getBody(responseXML);
		payment.setOldSerialNo(body.elementText("ogl_serial_no"));
		payment.setReceAccNo(body.elementText("rcv_acno"));
		payment.setState(body.elementText("stat"));
		payment.setReceBankNo(body.elementText("rcv_bank_no"));
		payment.setErrMsg(body.elementText("err_msg"));
		payment.setMoney(body.elementText("amt"));
		payment.setPayAccNo(body.elementText("pay_acno"));
		payment.setRypSerialNo(body.elementText("cert_no"));
		payment.setPayBankNo(body.elementText("pay_bank_no"));
		return payment;
	}
	
	
	/***
	 * 自动结算划款结果处理
	 * @param pagNo
	 * @param num
	 * @param uid
	 * @param liqBatch
	 * @param state
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public CurrentPage<OrderInfo> queryDataForAutoSettlement(Integer pagNo,Integer num,String uid ,String liqBatch ,Integer state,Integer bdate,Integer edate){
		return autoSettlementDao.queryDataForAutoSettleAmt(pagNo,num,uid,liqBatch,state,bdate,edate);
	}
	
	/*****
	 * 下周当日成功手工代付数据
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param oid
	 * @param mstate
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downAutoSettlementData(String uid ,String trans_flow ,Integer state,Integer bdate,Integer edate) throws Exception{
		List<OrderInfo> TrList=autoSettlementDao.downAutoSettlementData(uid, trans_flow,state,bdate,edate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
//		long totleTransAmt = 0;
//		long totlePayAmt = 0;
//		long totleTransFee=0;
		list.add("电银流水号,商户号,结算批次号,结算确认日期,商户名称,开户银行名称,开户账户名称,开户账户号,划款状态,划款金额,交易银行,支付渠道".split(","));
		int i = 0;
		try {
			for (OrderInfo t : TrList) {
				String gateRoute = "";
				if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(t.getGid());
				}
				String[] str = { t.getOid(),t.getMid(),t.getP9(),String.valueOf(t.getSysDate()),t.getName(),t.getBank_branch(),t.getP1(),t.getP2(),
						AppParam.tlog_tstat.get(t.getTstat().intValue()),Ryt.div100(t.getAmount())+"",
						gates.get(t.getGate()),gateRoute};
//				totleTransAmt += t.getAmount();
//				totlePayAmt += t.getPayAmt();
//				totleTransFee+=t.getFeeAmt();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","", "", "" ,"","","" };
		list.add(str);
		String filename = "AutoSettlementData" + DateUtil.today() + ".xlsx";
		String name = "自动代付划款结果处理";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
	
	
}
