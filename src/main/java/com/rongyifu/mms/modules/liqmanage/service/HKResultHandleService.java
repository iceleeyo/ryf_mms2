package com.rongyifu.mms.modules.liqmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.common.QueryDaifuResultUtil;
import com.rongyifu.mms.modules.liqmanage.dao.HKResultHandleDao;
import com.rongyifu.mms.modules.liqmanage.dao.QuerytransferDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

/*****
 * 划款结果处理 service
 * @author wang.bin
 *
 */
public class HKResultHandleService {
	private HKResultHandleDao handleDao=new HKResultHandleDao();
	private QuerytransferDao qtDao=new QuerytransferDao();
	private QueryDaifuResultUtil daifuResultUtil=new QueryDaifuResultUtil(qtDao);
	
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
		return handleDao.queryDataForAutoSettleAmt(pagNo,num,uid,liqBatch,state,bdate,edate);
	}
	
	/****
	 * 划款结果处理下载
	 * @param uid
	 * @param trans_flow
	 * @param state
	 * @param bdate
	 * @param edate
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downAutoSettlementData(String uid ,String trans_flow ,Integer state,Integer bdate,Integer edate) throws Exception{
		List<OrderInfo> TrList=handleDao.downAutoSettlementData(uid, trans_flow,state,bdate,edate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
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
	
	/****
	 *  自动结算发起查询请求
	 * 银行有返回最终结果  更新结果  提示订单XXX状态同步成功
	 * @param lists
	 * @return
	 */
	public String reqQuery_Bank(List<OrderInfo> lists){
		StringBuffer msgSuc=new StringBuffer("代付流水：");
		StringBuffer msgFail=new StringBuffer("代付流水：");
		StringBuffer msgException=new StringBuffer("代付流水：");
		StringBuffer msgNormal=new StringBuffer("代付流水：");
		boolean isSuc=false;boolean isFail=false; boolean isException=false;boolean isNormal=false;
		for (OrderInfo orderInfo : lists) {
			String tseq=orderInfo.getTseq();
			String[] res=daifuResultUtil.reqSGSyncRes(orderInfo);
			String result=res[1];
			if(result.equals("success")){
				msgSuc.append(tseq).append("/");
				isSuc=true;
			}else if(result.equals("fail")){
				msgFail.append(tseq).append("/");
				isFail=true;
			}else if(result.equals("req_fail")){
				msgException.append(tseq).append("/");
				isException=true;
			}else if(result.equals("wait_pay")){ 
				msgNormal.append(tseq).append("/");
				isNormal=true;
			}
			
		}
		StringBuffer endRes=new StringBuffer();
		if(isSuc){
			msgSuc.replace(msgSuc.length()-1, msgSuc.length(), "");
			msgSuc.append(" 同步成功, 交易结果为成功");
			endRes.append(msgSuc).append("\r\n");
		}
		if(isFail){
			msgFail.replace(msgFail.length()-1, msgFail.length(), "");
			msgFail.append(" 同步成功，交易结果为失败");
			endRes.append(msgFail).append("\r\n");
		}
		if(isException){
			msgException.replace(msgException.length()-1, msgException.length(), "");
			msgException.append(" 同步异常");
			endRes.append(msgException).append("\r\n");
		}
		if(isNormal){
			msgNormal.replace(msgNormal.length()-1, msgNormal.length(), "");
			msgNormal.append("未获取到最终结果，请稍后发起继续结果同步");
			endRes.append(msgNormal).append("\r\n");
		}
	return endRes.toString();	
	}
	
}
