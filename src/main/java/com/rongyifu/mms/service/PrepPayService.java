package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.*;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AccountDao;
import com.rongyifu.mms.dao.PrepPayDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;


public class PrepPayService {
	private PrepPayDao prepPayDao = new PrepPayDao();

	/**
	 * 银行行号和 银行简称的map
	 * @return
	 */
	public Map<String,String> getBkNoMap(){
		return prepPayDao.getBkNoMap();
	}
	 
	/**
	 * 查询银行余额
	 * @param pageNo
	 * @param bkId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<DateBlLog> queryBankBalance(Integer pageNo,String bkId,Integer beginDate,Integer endDate){
		return prepPayDao.queryBankBalance(pageNo, bkId, beginDate, endDate);
	}
	/**
	 * 商户流水查询
	 * @param pageNo
	 * @param minfoId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<TrSeq> queryAccountDetail(Integer pageNo,Integer minfoId,Integer beginDate,Integer endDate){
		return prepPayDao.queryAccountDetail(pageNo,new AppParam().getPageSize(), minfoId, beginDate, endDate);
	}
	/**
	 * 商户流水下载
	 * @param pageNo
	 * @param minfoId
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downloadAccountDetail(Integer minfoId,Integer beginDate,Integer endDate) throws Exception{
		
		 CurrentPage<TrSeq> trSeqListPage=  prepPayDao.queryAccountDetail(1,-1, minfoId, beginDate, endDate);
		 List<TrSeq> trSeqList=trSeqListPage.getPageItems();
		 ArrayList<String[]> rowList = new ArrayList<String[]>();
		 rowList.add("电银流水号,商户号,商户简称,商户日期,操作类型,交易金额".split(","));
		 for (TrSeq trSeq:trSeqList) {
			String[] strArr={
					trSeq.getId().toString(),
					trSeq.getObjId(),
					RYFMapUtil.getInstance().getMerMap().get(Integer.parseInt(trSeq.getObjId())),//trSeq.get
					trSeq.getTrDate()+" "+DateUtil.getStringTime(trSeq.getTrTime()),
					AppParam.trans_type.get(trSeq.getTrType()),
					Ryt.div100(trSeq.getTrAmt())
			};
			rowList.add(strArr);
		}
			String[] str = { "总计:" + trSeqList.size()+ "条记录", "", "", "", "", ""};
			rowList.add(str);
			String filename = "ACOUNT_" + DateUtil.today() + ".xls";
			String name = "交易流水表";
			return new DownloadFile().downloadXLSFileBase(rowList, filename, name); 
		 
	}
	/**
	 *  交易查询
	 * @param pageNo
	 * @param minfoId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public CurrentPage<CgOrder> queryTransPage(Integer pageNo,Integer dateType,
							Integer beginDate,Integer endDate,String bkNo,Integer transType,Integer transState){
		
		return prepPayDao.queryTransPage(pageNo, dateType, beginDate, endDate, bkNo, transType, transState);
	}
	/**
	 * 差错退款查询
	 * @param mid
	 * @param oldTseq
	 * @return
	 */
	public Hlog queryHlogIsExist(String mid,Integer oldTseq){
		
		return prepPayDao.queryHlogIsExist(mid,oldTseq);
	}
	/**
	 * 差错退款申请
	 * @param cgOrder
	 * @return
	 */
	public String submitErrorRefund(CgOrder cgOrder){
		try {
			if(cgOrder.getAuthType()==null||cgOrder.getGateId()==null){
				return "输入的数据错误！";
			}
			String bankFeeModel=new AccountDao().getBkFeeModel(cgOrder.getGateId(),cgOrder.getGid());
			String bkRefFee= Ryt.mul100(ChargeMode.reckon(bankFeeModel,Ryt.div100(cgOrder.getTrAmt())));
			cgOrder.setBkRefFee(Integer.parseInt(bkRefFee));
			//get channelId
			String channelId=cgOrder.getAuthType()==0?cgOrder.getGateId()+"":cgOrder.getAuthType()+"";
			String bankNo=prepPayDao.getBkNoByChannelNo(channelId);
			cgOrder.setBkNo(bankNo);
			int effectRow = prepPayDao.submitErrorRefund(cgOrder);
			if(effectRow==1){
				return AppParam.SUCCESS_FLAG;
			}else{
				return "差错退款申请失败，请重试！";
			}
		} catch (Exception e) {
			return "差错退款申请异常！";
		}
	}

	/**
	 * 差错退款查询
	 * @param pageNo
	 * @param authType
	 * @param transState
	 * @param bdate
	 * @param edate
	 * @return
	 */
	public CurrentPage<CgOrder> queryErrorRefundPay(Integer pageNo,Integer authType,
			Integer transState,Integer bdate,Integer edate,String oldTseq){
		return prepPayDao.queryErrorRefundPay(pageNo,new AppParam().getPageSize(), authType, transState, bdate, edate,oldTseq);
	}
	/**
	 * 下载当天经办成功的记录
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downloadTodayRefund() throws Exception{
		List<CgOrder> cgOrderList=prepPayDao.queryTodayRefund();
		 ArrayList<String[]> rowList = new ArrayList<String[]>();
		 rowList.add("序号,商户号,电银流水号,原银行流水号,原交易日期,原交易金额,原交易银行,退款金额,银行退回的手续费,经办日期,退款人签字栏".split(","));
			 for (int i = 0; i < cgOrderList.size(); i++) {
				 CgOrder cgOrder=cgOrderList.get(i);
					String[] strArr={
							String.valueOf(i+1),
							cgOrder.getMid()+"",
							cgOrder.getTseq()+"",
							cgOrder.getBkSeq(),
							cgOrder.getTrDate().toString(),
							Ryt.div100(cgOrder.getTrAmt()),
							RYFMapUtil.getGateMap().get(cgOrder.getGateId()),
							Ryt.div100(cgOrder.getTrAmt()),
							Ryt.div100(cgOrder.getBkRefFee()),
							cgOrder.getOperDate().toString(),
							cgOrder.getRemark()
					};
					rowList.add(strArr);
			}
			String[] strTotle = { "总计:" + cgOrderList.size()+ "条记录", "", "", "", "", "", "", "", "", "", ""};
			String[] str1 = { "", "制表人:",prepPayDao.getLoginUser().getOperName(), "制表日期:",DateUtil.today()+"", "", "复核人:", "", "日期:", "", "" };
			String[] str2 = { "", "退款录入:", "", "日期:", "", "", "退款复核:", "", "日期:", "", "" };
			String[] str3 = { "", "结算主管:", "", "日期:", "", "", "", "", "", "", "" };
			rowList.add(strTotle);
			rowList.add(str1);
			rowList.add(str2);
			rowList.add(str3);
			String filename = "ACOUNT_" + DateUtil.today() + ".xls";
			String name = "差错退款报表";
			return new DownloadFile().downloadXLSFileBase(rowList, filename, name); 
	}
	/**
	 * 下载差错退款审核的记录
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downloadVerifyRefund(Integer authType,Integer transState,
			Integer bdate,Integer edate,String oldTseq) throws Exception{
		List<CgOrder> cgOrderList=prepPayDao.queryErrorRefundPay(1,-1, authType, transState, bdate, edate,oldTseq).getPageItems();
		 ArrayList<String[]> rowList = new ArrayList<String[]>();
		 rowList.add("序号,商户号,电银流水号,原银行流水号,原交易日期,原交易金额,原交易银行,退款金额,银行退回的手续费,退款申请日期,退款经办日期".split(","));
			 for (int i = 0; i < cgOrderList.size(); i++) {
				 CgOrder cgOrder=cgOrderList.get(i);
					String[] strArr={
							String.valueOf(i+1),
							cgOrder.getMid()+"",
							cgOrder.getTseq()+"",
							cgOrder.getBkSeq(),
							cgOrder.getTrDate().toString(),
							Ryt.div100(cgOrder.getTrAmt()),
							RYFMapUtil.getGateMap().get(cgOrder.getGateId()),
							Ryt.div100(cgOrder.getTrAmt()),
							Ryt.div100(cgOrder.getBkRefFee()),
							cgOrder.getOperDate().toString(),
							cgOrder.getValiDate().toString()
					};
					rowList.add(strArr);
			}
			String[] strTotle = { "总计:" + cgOrderList.size()+ "条记录", "", "", "", "", "", "", "", "", "", ""};
			rowList.add(strTotle);
			String filename = "ACOUNT_" + DateUtil.today() + ".xls";
			String name = "差错退款审核报表";
			return new DownloadFile().downloadXLSFileBase(rowList, filename, name); 
	}
	/**
	 * 差错退款经办/撤销
	 * @param handlType
	 * @param ids
	 * @return
	 */
	public String handRefundPay(Integer handlType,int[] ids){
		if(ids.length<1)return "参数错误！差错退款经办操作失败。";
		String errorMsg="";
		for (int i = 0; i < ids.length; i++) {
			int effectRow=prepPayDao.handRefundPay(handlType,ids[i]);
			if(effectRow!=1){
				errorMsg+="流水号为( "+ids[i]+" )差错退款经办操作失败；";
			}
		}
		if(errorMsg.equals("")){
			return AppParam.SUCCESS_FLAG;
		}else{
			return errorMsg;
		}
	}
	/**
	 * 差错退款 审核
	 * @param handlType
	 * @param ids
	 * @return
	 */
	public String verifyRefundPay(Integer handlType,int[] ids){
		if(ids.length<1)return "参数错误！差错退款审核操作失败。";
		String errorMsg="";
		for (int i = 0; i < ids.length; i++) {
			int effectRow=prepPayDao.verifyRefundPay(handlType,ids[i]);
			if(effectRow!=1){
				errorMsg+="流水号为( "+ids[i]+" )差错退款审核操作失败；";
			}
		}
		if(errorMsg.equals("")){
			return AppParam.SUCCESS_FLAG;
		}else{
			return errorMsg;
		}
	}
	/**
	 * 余额查询（资金归集申请）
	 * @param channelId
	 * @return 
	 */
	public BkAccount queryBkBalance(String bkNo){
		BkAccount bkAccount= prepPayDao.queryBkBalance(bkNo);
		return bkAccount;
	}
	/**
	 * 商户资金归集申请
	 * @param cgOrder
	 * @return
	 */
	public String bunchMerFundReq(CgOrder cgOrder){
		cgOrder.setTrDate(DateUtil.today());
		cgOrder.setTrType(1);
		int effectRow=prepPayDao.fundChangeReq(cgOrder);
		if(effectRow==1){
			return AppParam.SUCCESS_FLAG; 
		}else{
			return "商户资金归集申请失败！请重试。";
		}
	}
	/**
	 * 资金归集审核
	 * @param handlType
	 * @param ids
	 * @return
	 */
	public String verifyMergerFund(Integer handlType,int[] ids){
		if(ids.length<1)return "参数错误！资金归集审核操作失败。";
		String errorMsg="";
		for (int i = 0; i < ids.length; i++) {
			int effectRow=prepPayDao.verifyRefundPay(handlType,ids[i]);
			if(effectRow!=1){
				errorMsg+="流水号为( "+ids[i]+" )资金归集审核操作失败；";
			}
		}
		if(errorMsg.equals("")){
			return AppParam.SUCCESS_FLAG;
		}else{
			return errorMsg;
		}
	}
	/**
	 * 手工调帐申请
	 * @param cgOrder
	 * @return
	 */
	public String adjustAccountReq(CgOrder cgOrder){
		int effectRow=prepPayDao.fundChangeReq(cgOrder);
		if(effectRow==1){
			return AppParam.SUCCESS_FLAG; 
		}else{
			return "手工调帐申请失败！请重试。";
		}
	}
	public List<Map<String ,Object>> queryAllRelationBank(){
		
		return prepPayDao.queryAllRelationBank();
	}
// 	================================PrepPayService==============================================
 	/**
	 * 生成存管数据
	 * @param liqDate 清算日期
	 * @return
	 */
	public String genCGData(int liqDate) {
		
		int i = prepPayDao.findTodayDateBlLogCount();
				
		if(i>0) return "今天的数据已经生成";
		RYFMapUtil util = RYFMapUtil.getInstance();
		Map<Integer, String> merMap = util.getMerMap();
		int t = DateUtil.today();
		for (int mid : merMap.keySet()) {
			//	    0--支付
			//		1-支付手续费
			//		2-退款
			//		3-退款退回手续费
			//		4-调增
			//		5-调减
			//		6-结算
			long addAmt = prepPayDao.getMerDateSuccessSumByTrType("0,3,4", mid+"", t);
			long subAmt = prepPayDao.getMerDateSuccessSumByTrType("1,2,5,6", mid+"", t);
			long initBl = prepPayDao.findMerBalance(mid+"");
			long retBl = (initBl + addAmt - subAmt );
			DateBlLog o = new DateBlLog();
			o.setBalance(retBl);
			o.setBfBl(0l);
			o.setLiqDate(liqDate);
			o.setMerFee(0l);
			o.setName(merMap.get(mid));//
			o.setObjId(mid + "");
			o.setObjType(0);// 0-商户
			prepPayDao.insertDateBlLog(o);
		}

		// 2 商户流水中明细中增加清算日期7.5
		//prepPayDao.updateTrSeqLiqDate(liqDate, t);

		List<BkAccount> bkNos = prepPayDao.getAllBKAccount();
		for (BkAccount obj : bkNos) {
			DateBlLog o = new DateBlLog();
			o.setLiqDate(liqDate);
			// 银行资金变动与商户余额没有关系
			o.setBfBl(obj.getBfBl());
			if (obj.getAccType() == 1) {// 如果是存管银行
				long merFee = prepPayDao.getSumMerFee(t);
				o.setMerFee(merFee);// 存管银行计算手续费
				long balance = getCGBkBlance(obj.getBkBl(), t);
				o.setBalance(balance);
			} else {
				o.setMerFee(0l);
				long balance = getHZBkBlance(obj.getBkBl(), t);
				o.setBalance(balance);// 合作银行的余额变动
			}
			o.setName(obj.getBkAbbv());
			o.setObjId(obj.getBkNo());
			o.setObjType(1);// 1--银行
			prepPayDao.insertDateBlLog(o);
		}

		return AppParam.SUCCESS_FLAG;

	}
	
	
	// 合作银行的余额变动
	private long getHZBkBlance(Long bkBl, int t) {
		long sub = prepPayDao.getDateSuccessSumByTrType("1,3", t);
		long add = prepPayDao.getDateSuccessSumByTrType("2", t);
		
		return bkBl - sub + add;
	}
	//存管银行的余额变动
	private long getCGBkBlance(Long bkBl, int t) {
		long add = prepPayDao.getDateSuccessSumByTrType("1,2", t);
		long sub = prepPayDao.getDateSuccessSumByTrType("3", t);
		return bkBl - sub + add;
	}

}
