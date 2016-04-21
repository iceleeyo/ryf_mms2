package com.rongyifu.mms.modules.accmanage.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.directwebremoting.io.FileTransfer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bank.query.QueryCommon;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.exception.RytException;
import com.rongyifu.mms.modules.accmanage.dao.SgDfShDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
/***
 * 手工代付审核
 *
 */
public class SgDfShService{
	private SgDfShDao sgdfshDao=new SgDfShDao();
	private final String md5Key="iFv5x6Cu"; //支付请求签名用
	private final String transType_ftp="FTP_1"; //支付类型 单笔FTP代付
	private final String transType_auto="auto_df";
	
	/***
	 * 查询申请成功数据
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
		return sgdfshDao.queryDataForReqFail(pagNo,num,uid,trans_flow,ptype,tseq,mstate,bdate,edate);
	}
	
	/****
	 * 手工代付申请 代付提交
	 * 发起再次代付
	 * @param lists
	 * @return
	 */
	public String reqPayDf(List<OrderInfo> lists){
		//Map<String, Integer> batchM=new HashMap<String, Integer>();//batchNo Map 
		//List<TlogBean> ftpDfs=new ArrayList<TlogBean>();//FTP 代付		
		List<TlogBean> normalDfs=new ArrayList<TlogBean>();//常规代付
		StringBuffer normalMsg=new StringBuffer();
		StringBuffer exceptionMsg=new StringBuffer();
		for (OrderInfo orderInfo : lists) {
			try {
                TlogBean tlog=(TlogBean)convertToTlogBean(orderInfo);
                normalDfs.add(tlog);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //修改订单状态为处理中
//			TlogBean tlog=(TlogBean)convertToTlogBean(orderInfo);
//			normalDfs.add(tlog);
			/*if(orderInfo.getData_source()==9){ //FTP 代付
				ftpDfs.add(tlog);
			}else {
				normalDfs.add(tlog);
			}*/
		}
		
		String resp="手工审核成功\n";
		try{
			sgdfshDao.batchSh(normalDfs);//执行审核操作的数据操作
			//MMs重发代付、 因订单重新生产
			if(normalDfs.size()!=0){
				for (TlogBean tlog : normalDfs) {
					String respFlag=begin_otherDf(tlog);
					if(!"suc".equals(respFlag)){
						tlog.setTstat(PayState.FAILURE);
						tlog.setError_msg("请求银行失败");
						tlog.setAgainPay_status(Constant.SgDfTstat.TSTAT_SHFAIL);
						sgdfshDao.updateTstat(tlog);
						exceptionMsg.append(tlog.getOid()).append(",");
					}else{
						normalMsg.append(tlog.getOid()).append(",");
					}
				}
			}
		}catch (RytException e) {
			LogUtil.printErrorLog("SgDfShService", "reqPayDf", "reqPayDf_exception", e);
			return "系统异常-数据异常";
		}catch (Exception e) {
			LogUtil.printErrorLog("SgDfShService", "reqPayDf", "reqPayDf_exception", e);
		}
		
		if(!Ryt.empty(normalMsg.toString())){
			resp+="提交成功:"+normalMsg.substring(0, normalMsg.length()-1)+"\n";
		}
		if(!Ryt.empty(exceptionMsg.toString())){
			resp+="请求银行失败:"+exceptionMsg.substring(0, exceptionMsg.length()-1)+"\n";
		}
		sgdfshDao.saveOperLog("手工代付审核", "审核成功");
		return resp;
		
	}
	
	private String begin_otherDf(TlogBean tlog){
		String url=Ryt.getEwpPath();
		String mid=tlog.getMid();
		String transAmt=tlog.getTstat().toString();
		String transType=transType_auto;
		String data_source=tlog.getData_source().toString();
		String tmsIp=ParamCache.getStrParamByName("TMS_IP");
		String data=mid + transAmt + transType + data_source+ tmsIp + md5Key;
		String tseq=tlog.getTseq().toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("transAmt", transAmt);
		paramMap.put("data_source", data_source);
		paramMap.put("mid", mid);
		paramMap.put("chkValue", QueryCommon.md5Encrypt(data.toString()));
		paramMap.put("transType", transType);
		paramMap.put("tseq", tseq);
		req_ewp req=new req_ewp(paramMap, transType, url+"df/auto_df");
		return req.process();
//		Thread thread=new Thread(req);
//		thread.start();
	}
	
	
	@SuppressWarnings("unused")
	private String begin_ftpDf(List<TlogBean> ftpDfs) {
		StringBuffer batchInfo=new StringBuffer();
		for (TlogBean tlog : ftpDfs) {
			batchInfo.append(tlog.getMid()).append("|");
			batchInfo.append(tlog.getSys_date()).append("|");
			batchInfo.append(tlog.getOid()).append(";");
		}
		if(!Ryt.empty(batchInfo.toString())){
			String url=Ryt.getEwpPath();
			Map<String, Object> map=new HashMap<String, Object>();
			StringBuffer signStr=new StringBuffer();
		    signStr.append(batchInfo);
		    signStr.append(md5Key);
		    String chkValue=QueryCommon.md5Encrypt(signStr.toString());//md5加密merId+orderDate+dybatchNo+md5Key
		    map.put("transType", transType_ftp);
		    map.put("batchInfo", batchInfo);
		    map.put("chkValue", chkValue);
			req_ewp req=new req_ewp(map, transType_ftp, url+"df/ftp_df");
			return req.process();
//			Thread thread=new Thread(req);
//			thread.start();
		}
		
		return "fail";
	}

	/***
	 * FTP批量订单 处理方法
	 * @param sqls
	 * @param batchM
	 */
	public void handleBatchDf(List<String> sqls, Map<String, Integer> batchM) {
		if(!batchM.isEmpty()){
			Iterator<String> keySet=batchM.keySet().iterator();
			while (keySet.hasNext()) {
				String key=keySet.next();
				Integer affectNum=batchM.get(key);
				String updateBatchProcessNum = sgdfshDao.updateBatchProcessNum(key,
						affectNum);
				sqls.add(updateBatchProcessNum);
			}
		}
		return;
	}

	/****
	 * 手工代付申请 代付撤销
	 * @param lists
	 * @param option
	 * @return
	 */
	public String reqRevocation(List<OrderInfo> lists,String option){
		//发起撤销  调用接口   保存撤销意见  
		//撤销 订单保存在撤销表
		if(!sgdfshDao.batchRefuse(lists)){
			sgdfshDao.saveOperLog("审核撤销", "审核撤销数据操作异常");
			return "撤销异常";
		}
		sgdfshDao.saveOperLog("审核撤销", "审核撤销成功");
		return "撤销发起成功";
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
	public FileTransfer downSGDFData(String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate) throws Exception{
		List<OrderInfo> TrList=sgdfshDao.downSGDFData(uid, trans_flow, ptype, tseq, mstate);
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		Map<Integer,String> merMap=obj.getMerMap();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("代付流水号,交易类型,批次号,商户号,商户简称,账户名称,商户订单,支付渠道,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,交易状态,撤销意见".split(","));
		int i = 0;
		try {
			for (OrderInfo t : TrList) {
				String gateRoute = "";
				if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(t.getGid());
				}
				String[] str = {t.getTseq(),(11==t.getType()?"对私代付":"对公代付")+"",t.getP8(), t.getMid(),merMap.get(t.getMid()),t.getName(),t.getOid(),gateRoute,
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
		String filename = "SGDfSH" + DateUtil.today() + ".xlsx";
		String name = "手工代付";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		
	}
	
	/***
	 * orderInfo 转换成tlogBean
	 * @param o
	 * @return
	 */
	public TlogBean convertToTlogBean(OrderInfo o){
		TlogBean tlog=new TlogBean();
		tlog.setTseq(new Long(o.getTseq()));
		tlog.setVersion(o.getVersion());
		tlog.setIp(o.getIp());
		tlog.setMdate(o.getMdate());
		tlog.setOid(o.getOid());
		tlog.setMid(o.getMid());
		tlog.setBid(o.getBid());
		tlog.setAmount(o.getAmount());
		tlog.setPay_amt(o.getPayAmt());
		tlog.setType(Integer.valueOf(o.getType()));
		tlog.setGate(o.getGate());
		tlog.setSys_date(o.getSysDate());
		tlog.setSys_time(o.getSysTime());
		tlog.setInit_sys_date(o.getInitSysDate());
		tlog.setBatch(o.getBatch());
		tlog.setFee_amt(o.getFeeAmt());
		tlog.setBank_fee(o.getBankFee());
		tlog.setTstat(Integer.valueOf(o.getTstat()));
		tlog.setBk_flag(o.getBkFlag());
		tlog.setOrg_seq(o.getOrgSeq());
		tlog.setRef_seq(o.getRefSeq());
		tlog.setRefund_amt(o.getRefundAmt());
        LogUtil.printInfoLog("SgDfShService", "convertToTlog", "o.getMerPriv:"+o.getMerPriv());
		tlog.setMer_priv(o.getMerPriv());
		tlog.setBk_send(o.getBkSend());
		tlog.setBk_recv(o.getBkRecv());
		tlog.setBk_resp(o.getBkResp());
		tlog.setBk_url(o.getBkUrl());
		tlog.setFg_url(o.getFgUrl());
		tlog.setBk_chk(Integer.valueOf(o.getBkChk()));
		tlog.setBk_date(o.getBkDate());
		tlog.setBk_seq1(o.getBk_seq1());
		tlog.setBk_seq2(o.getBk_seq2());
		tlog.setMobile_no(o.getMobileNo());
		tlog.setTrans_period(o.getTransPeriod());
		tlog.setCard_no(o.getCardNo());
		tlog.setError_code(o.getErrorCode());
		tlog.setAuthor_type(o.getAuthorType());
		tlog.setPhone_no(o.getPhoneNo());
		tlog.setOper_id(o.getOperId());
		tlog.setGid(o.getGid());
		tlog.setPre_amt(o.getPreAmt());
		tlog.setPre_amt1(o.getPreAmt1());
		tlog.setBk_fee_model(o.getBkFeeModel());
		tlog.setError_msg(o.getError_msg());
		tlog.setP1(Ryt.desEnc(o.getP1())+"|noDec");
		tlog.setP2(o.getP2());
		tlog.setP3(o.getP3());
		tlog.setP4(o.getP4());
		tlog.setP5(o.getP5());
		tlog.setP6(o.getP6());
		tlog.setP7(o.getP7());
		tlog.setP8(o.getP8());
		tlog.setP9(o.getP9());
		tlog.setP10(o.getP10());
		tlog.setP11(o.getP11());
		tlog.setP12(o.getP12());
/*		tlog.setIs_notice();
		tlog.setIs_liq(0);*/
		tlog.setData_source(o.getData_source());
		tlog.setAgainPay_date(o.getAgainPay_date());
		tlog.setAgainPay_status(o.getAgainPay_status());
		
		return tlog;
		
	}
	

class req_ewp implements Runnable{
	private Map<String, Object> map=null;
	private String transType=null;
	private String url=null;
	
	
	public req_ewp(Map<String, Object> map, String transType, String url) {
		super();
		this.map = map;
		this.transType = transType;
		this.url = url;
	}



	@Override
	public void run() {
		Map<String, String> logMap=new HashMap<String, String>();
		Set<String> keys=map.keySet();
		for (String key : keys) {
			logMap.put(key, String.valueOf(map.get(key)));
		}
		
		//发送支付请求至ewp
		String resp=Ryt.requestWithPost(map, url);
		if(resp.equals("suc") && transType.equals(transType_ftp)){
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求成功",logMap);
		}else if(transType.equals(transType_auto) && !Ryt.empty(resp)){
			parseResultForAutoDf(resp);
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求成功",logMap);
		}else{
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求失败",logMap);
		}
	}
	
	public String process(){
		Map<String, String> logMap=new HashMap<String, String>();
		Set<String> keys=map.keySet();
		for (String key : keys) {
			logMap.put(key, String.valueOf(map.get(key)));
		}
		
		//发送支付请求至ewp
		String resp=Ryt.requestWithPost(map, url);
		if(resp.equals("suc") && transType.equals(transType_ftp)){
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求成功",logMap);
			return "suc";
		}else if(transType.equals(transType_auto) && !Ryt.empty(resp)){
			String returnFlag=parseResultForAutoDf(resp);
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求成功",logMap);
			return returnFlag;
		}else{
			logMap.put("msg", resp);
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "run", "发送支付请求失败",logMap);
			return "fail";
		}
		
	}
	
	/***
	 * 解析自动代付返回信息
	 * @param resp
	 * @return returnFlag
	 * @throws UnsupportedEncodingException 
	 */
	private String parseResultForAutoDf(String resp) {
		String respXml;
		String returnFlag="";
		try {
			respXml = new String(resp.getBytes(), "utf-8");
			resp=respXml;
		} catch (UnsupportedEncodingException e) {
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "parseResultForAutoDf", "exception msg:"+e.getMessage());
		}
		if(Ryt.empty(resp)){
			returnFlag="fail";
			return returnFlag;
		}
		try{
			Document doc=DocumentHelper.parseText(resp);
			Element root=doc.getRootElement();
			Element statusEle=root.element("status");
			String value=statusEle.elementText("value");
			if("0".equals(value)){
				returnFlag="suc";
			}else{
				returnFlag="fail";
			}
			
		}catch (Exception e) {
			LogUtil.printInfoLog("SGDFSHService$req_ewp", "parseResultForAutoDf", "exception msg:"+e.getMessage());
			returnFlag="fail";
		}
		return returnFlag;
	}
	
}
	
}
