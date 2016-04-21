package com.rongyifu.mms.modules.accmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.accmanage.dao.SgDfSqDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

/**
 * 手工代付申请处理server
 *
 */
public class SgDfSqService {
	
	private SgDfSqDao dao=new SgDfSqDao();
   	/***
   	 * 查询代付支付失败数据  mms 代付
   	 * @param pagNo
   	 * @param num
   	 * @param uid
   	 * @param trans_flow
   	 * @param ptype
   	 * @param tseq
   	 * @param mstate
   	 * @param bdate
   	 * @param edate
   	 * @return
   	 */
	public CurrentPage<OrderInfo> queryDataForReq(Integer pagNo,Integer num,String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer bdate,Integer edate){
		return dao.queryDataForReqFail(pagNo,num,uid,trans_flow,ptype,tseq,mstate,bdate,edate);
	}
	
	/***
	 * 申请代付请求处理
	 * @param os
	 * @return
	 */
	public boolean sgdf(List<OrderInfo> os){
		dao.batchSq(os);
		return true;
	}

	/***
	 * 批量拒绝请求处理
	 * @param os
	 * @return
	 */
	public boolean sgdfRefuse(List<OrderInfo> os){
		dao.batchRefuse(os);
		return true;
	}
	
	
	/****
	 * 
	 * @param uid
	 * @param trans_flow
	 * @param ptype
	 * @param tseq
	 * @param mstate
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downSGDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate) throws Exception{
		List<OrderInfo> TrList=dao.downSGDFData(uid, trans_flow, ptype, tseq, mstate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("代付流水号,交易类型,批次号,商户号,账户号,账户名称,支付渠道,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,代付状态,撤销意见".split(","));
		int i = 0;
		try {
			for (OrderInfo t : TrList) {
				String gateRoute = "";
				if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(t.getGid());
				}
				String[] str = {t.getOid(),(11==t.getType()?"对私代付":"对公代付")+"",t.getP8(), t.getMid(),t.getMid(),t.getName(),gateRoute,
						Ryt.div100(t.getAmount())+"",Ryt.div100(t.getFeeAmt())+"",Ryt.div100(t.getPayAmt())+"",
						gates.get(t.getGate()),t.getP2(),t.getP1(),AppParam.tlog_tstat.get(t.getTstat().intValue()),t.getCancel_reason()};
				totleTransAmt += t.getAmount();
				totlePayAmt += t.getPayAmt();
				totleTransFee+=t.getFeeAmt();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totlePayAmt)+"","","","" };
		list.add(str);
		String filename = "DaiFuSGDf" + DateUtil.today() + ".xlsx";
		String name = "手工代付";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
}
