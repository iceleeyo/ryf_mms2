package com.rongyifu.mms.modules.accmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.accmanage.dao.SGSyncDFResultDao;
import com.rongyifu.mms.modules.common.QueryDaifuResultUtil;
import com.rongyifu.mms.modules.liqmanage.dao.QuerytransferDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

/*****
 * 账户管理下手工同步代付结果Service模块
 * @author shdy
 *
 */
public class SGSyncDFResultService {
	private SGSyncDFResultDao dao=new SGSyncDFResultDao();
	private QuerytransferDao querytransferDao=new QuerytransferDao();
	private QueryDaifuResultUtil daifuResultUtil=new QueryDaifuResultUtil(querytransferDao);
	/***
   	 * 查询代付数据  针对接口   手工同步代付结果
   	 * @param pagNo
   	 * @param num
   	 * @param uid
   	 * @param trans_flow
   	 * @param ptype
   	 * @param tseq
   	 * @param mstate
   	 * @param state
   	 * @param gate
   	 * @param bdate
   	 * @param edate
   	 * @return
   	 */
	public CurrentPage<OrderInfo> queryDataForSGSYNC(Integer pagNo,Integer num,String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate){
		return dao.queryDataForSGSYNC(pagNo,num,uid,trans_flow,ptype,tseq,mstate,state,gate,bdate,edate);
	}
	
	/****
	 * 手工同步代付结果   发起查询请求   自动结算发起查询请求
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
				}else if(result.equals("wait_pay")){  //未查询到结果
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
		if(isNormal){//未查询到结果
			msgNormal.replace(msgNormal.length()-1, msgNormal.length(), "");
			msgNormal.append("未获取到最终结果，请稍后发起继续结果同步");
			endRes.append(msgNormal).append("\r\n");
		}
	return endRes.toString();	
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
	public FileTransfer downSGSYNCDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer state,Integer gate,Integer bdate,Integer edate) throws Exception{
		List<OrderInfo> TrList=dao.downSGSYNCDFData(uid, trans_flow, ptype, tseq, mstate,state,gate,bdate,edate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("代付流水号,交易类型,批次号,商户号,账户号,账户名称,支付渠道,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,代付状态".split(","));
		int i = 0;
		try {
            RYFMapUtil obj = RYFMapUtil.getInstance();
			for (OrderInfo t : TrList) {
				String gateRoute = "";
				if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(t.getGid());
				}
				String[] str = { t.getTseq(),(11==t.getType()?"对私代付":"对公代付")+"",t.getP8(), t.getMid(),t.getMid(),obj.getMerMap().get(t.getMid()),gateRoute,
						Ryt.div100(t.getAmount())+"",Ryt.div100(t.getFeeAmt())+"",Ryt.div100(t.getPayAmt())+"",
						gates.get(t.getGate()),t.getP2(),t.getP1(),AppParam.tlog_tstat.get(t.getTstat().intValue())};
				totleTransAmt += t.getAmount();
				totlePayAmt += t.getPayAmt();
				totleTransFee+=t.getFeeAmt();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","", Ryt.div100(totlePayAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totleTransAmt)+"","","","","" };
		list.add(str);
		String filename = "DaiFuSGSYNCDf" + DateUtil.today() + ".xlsx";
		String name = "手工同步代付结果";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
	/****
	 * 手工同步代付结果   发起商户通知  调用pay的商户通知接口
	 * @param lists
	 * @return
	 */
	public String reqNotice_Mer(List<OrderInfo> lists){
		StringBuffer msgSuc=new StringBuffer("代付流水：");
		StringBuffer msgFail=new StringBuffer("代付流水：");
		List<String> sqls=new ArrayList<String>();
		boolean isSuc=false;boolean isFail=false;
		try{
			for (OrderInfo orderInfo : lists) {
				String tseq=orderInfo.getTseq();
				String mid=orderInfo.getMid();
				String url = Ryt.getEwpPath();
				String oid=orderInfo.getOid();
				String p8=orderInfo.getP8(); //代付批次号
				if (!Ryt.empty(p8)) // 批量代付中的订单不单独通知
					continue;
				Map<String, Object> requestMap=new HashMap<String, Object>();
				requestMap.put("tseq", tseq);
				requestMap.put("merId", orderInfo.getMid());
				requestMap.put("transType", convertTransType(orderInfo));
				requestMap.put("oid", oid);
				String ret=Ryt.requestWithPost(requestMap, url+"df/notice_mer");
				LogUtil.printInfoLog("SGSyncDfResultService", "reqNotice_Mer", "df/notice_mer return :"+ret);
				String error_msg=null;
				if (ret.equals("notice_merchant_fail")){
					error_msg="手工代付结果通知商户失败";
					isFail=true;
					msgFail.append(tseq).append("/");
				}else if (ret.equals("notice_merchant_suc")){
					error_msg="手工代付结果通知商户成功";
					String[] info=dao.queryOrderByOid(mid,oid);
					String table=info[0];
					//修改商户通知状态 is_notice
					sqls.add(modifyIsNotice(table,tseq,mid,1));
					isSuc=true;
					msgSuc.append(tseq).append("/");
				}else{
					error_msg=ret;
					LogUtil.printInfoLog("DaifuServer", "reqNotice_Mer", "通知商户返回信息："+error_msg);
					isFail=true;
					msgFail.append(tseq).append("/");
				}
			}
			if(sqls.size()>0){
				String[] sqlList=sqls.toArray(new String[sqls.size()]);
				dao.batchSqlTransaction(sqlList);
			}
			
		}catch (Exception e) {
			LogUtil.printErrorLog("DaifuServer", "reqNotice_Mer", "手工代付结果通知商户-请求ewp失败！",e);
			return "手工代付结果通知商户-请求ewp失败！";
		}
		StringBuffer endRes=new StringBuffer();
		if(isSuc){
			msgSuc.replace(msgSuc.length()-1, msgSuc.length(), "");
			msgSuc.append(" 通知成功");
			endRes.append(msgSuc).append("\r\n");
		}
		if(isFail){
			msgFail.replace(msgFail.length()-1, msgFail.length(), "");
			msgFail.append(" 通知失败");
			endRes.append(msgFail).append("\r\n");
		}
		return endRes.toString();
	
	}
	
	/****
	 * 拼接修改订单通知状态的sql
	 * @param table
	 * @param tseq
	 * @param mid
	 * @param is_notice    0   1   2  
	 * @return
	 */
	public String modifyIsNotice(String table,String tseq,String mid,Integer is_notice){
		StringBuffer sql=new StringBuffer();
		sql.append("update  ").append(table).append(" set is_notice=").append(is_notice);
		sql.append(" where tseq=").append(Ryt.addQuotes(tseq)).append(" and mid=").append(Ryt.addQuotes(mid));
		return sql.toString();
	}
	
	/***
	 * 转换交易类型 
	 * @param order
	 * @return  C1 C2 B1 D1 D2
	 */
	public String convertTransType(OrderInfo order){
		String convertType="other";
		if( order.getP8()==null|| order.getP8().equals("")){//单笔
			if(order.getType()==Constant.TlogTransType.PAYMENT_FOR_PRIVATE  || order.getType()==Constant.TlogTransType.PAYMENT_FOR_PUBLIC ){
				convertType="C1";
			}else if(order.getType()==Constant.TlogTransType.WITHDRAW_DEPOSIT_PRIVATE  || order.getType()==Constant.TlogTransType.WITHDRAW_DEPOSIT_PUBLIC ){
				convertType="B1";
			}else if(order.getType()==Constant.TlogTransType.PROXY_PAYABLE_PRIVATE ){
				convertType="D1";
			}
		}else{
			if(order.getType()==Constant.TlogTransType.PAYMENT_FOR_PRIVATE  || order.getType()==Constant.TlogTransType.PAYMENT_FOR_PUBLIC ){
				convertType="C2";
			}else if(order.getType()==Constant.TlogTransType.PROXY_PAYABLE_PRIVATE ){
				convertType="D2";
			}
		}
		return convertType;
	}
	
	
}
