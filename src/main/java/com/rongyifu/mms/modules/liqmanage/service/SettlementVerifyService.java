package com.rongyifu.mms.modules.liqmanage.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.rytong.encrypto.provider.RsaEncrypto;
import net.sf.json.JSONObject;

import org.directwebremoting.io.FileTransfer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.AccSyncDetail;
import com.rongyifu.mms.bean.FeeLiqBath;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.QkCardInfo;
import com.rongyifu.mms.bean.SettleDetail;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.ewp.SendEmailServer;
import com.rongyifu.mms.ewp.ryf_df.CallRyfPayProcessor;
import com.rongyifu.mms.ewp.ryf_df.MerAccHandleUtil;
import com.rongyifu.mms.modules.liqmanage.dao.SettlementVerifyDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class SettlementVerifyService {

	SettlementVerifyDao settlementverifyDao = new SettlementVerifyDao();
	SystemDao systemDao = new SystemDao();

	/**
	 * 对已制表的结算记录进行确认
	 * 
	 * @param batchs需要结算记录的批次号数组
	 * @param loginmid
	 *            执行确认操作的操作员的商户号
	 * @param loginuid
	 *            执行确认操作的操作员的操作员号
	 * @return 确认的结果
	 */
	public String verifySettle(List<FeeLiqBath> f) {
		String msg = null;
		StringBuffer exceptionBuff = new StringBuffer(); // 因异常不能完成结算的商户
		if (f == null || f.size() == 0)
			return "请选择要结算确认的订单";

		for (FeeLiqBath mid : f) {
			try {
				String midq = mid.getMid();
				String liqBatch = mid.getBatch();
				int liqgid = settlementverifyDao.queryLiqGid(liqBatch);
				if (liqgid == 4) {// 自动代付 结算到电银账户；
					String remark = "结算到账户";
					Minfo minfo = settlementverifyDao.queryAccBymid(midq);
					String payAmt = Ryt.div100(mid.getLiqAmt());
					String dfType = "A";
					String data_source = "7";
					String transType = "auto_df";
					// 保存订单到tlog; 
					// ryf4.8 保存开户银行名称(minfo.pbk_name)到tlog.p4,保存开户银行简称(minfo.pbk_branch)到tlog.p5
					Map<String, String> map = settlementverifyDao
							.insertliqtlog(midq, payAmt, minfo.getPbkGateId(),
									minfo.getPbkAccNo(), minfo.getPbkAccName(),
									minfo.getPbkName(),minfo.getPbkBranch(),
									minfo.getPbkNo(), dfType, data_source,
									liqBatch, transType, minfo.getPbkProvId());
					String sql = map.get("sql");
					settlementverifyDao.liqaccount2(mid, remark, sql);
					String oid = map.get("oid");
					String date = map.get("date");
					TlogBean tlog = settlementverifyDao.getTlog(midq, oid,date);
				    //自动代付 调用新代付系统
                    msg=CallRyfPayProcessor.CallRyfPay(tlog);
                    String[] ret=CallRyfPayProcessor.getRespMsg(msg);
                    if(ret[0].equals("suc")){
                        msg="结算成功";
                        settlementverifyDao.saveOperLog("结算确认", "结算确认成功");
                    }else{
                        msg="结算确认失败，"+ret[1];
                        tlog.setError_msg(ret[1]);
                        doReqRyfDfFail(tlog);
                        settlementverifyDao.saveOperLog("结算确认", "结算确认失败："+msg);
                    }
				} else if (liqgid == 1) {// 手工结算 - 结算到电银账户
					String remark = "结算到账户";
					int[] account = settlementverifyDao.liqaccount(mid, remark,
							null);
					if (account.length == 3)
						msg = "结算成功！";
					settlementverifyDao.saveOperLog("结算确认", "结算确认成功");
				} else if (liqgid == 3) {// 自动结算 - 结算到银行卡
					String remark = "结算到银行卡";
					Minfo minfo = settlementverifyDao.queryAccBymid(midq);
					String payAmt = Ryt.div100(mid.getLiqAmt());
					int mertype = settlementverifyDao.querymertype(midq);
					String dfType;
					Integer GateId;
					String GateId1 = minfo.getGateId().toString();
					if (mertype == 1) {
						dfType = "A";
						GateId = Integer.parseInt("71" + GateId1.substring(2));
					} else {
						dfType = "B";
						GateId = Integer.parseInt("72" + GateId1.substring(2));
					}
					String data_source = "8";
					String transType = "auto_df";
					// 保存订单到tlog;
					// ryf4.8 保存开户银行名称(minfo.bank_name)到tlog.p4,保存开户银行简称(minfo.bank_branch)到tlog.p5
					Map<String, String> map = settlementverifyDao
							.insertliqtlog(midq, payAmt, GateId, minfo
									.getBankAcct(), minfo.getBankAcctName(),
									minfo.getBankName(),minfo.getBankBranch(),
									minfo.getOpenBkNo(), dfType, data_source,
									liqBatch, transType, minfo.getBankProvId()
											.toString());
					String sql = map.get("sql");
					settlementverifyDao.liqaccount2(mid, remark, sql);// 结算到账户
					String oid = map.get("oid");
					String date = map.get("date");
					TlogBean tlog = settlementverifyDao.getTlog(midq, oid,date);
					//自动结算 调用新代付系统
					msg=CallRyfPayProcessor.CallRyfPay(tlog);
                    String[] ret=CallRyfPayProcessor.getRespMsg(msg);
                    if(ret[0].equals("suc")){
                        msg="结算成功";
                        settlementverifyDao.saveOperLog("结算确认", "结算确认成功");
                    }else{
                        msg="结算确认失败，"+ret[1];
                        tlog.setError_msg(ret[1]);
                        doReqRyfDfFail(tlog);
                        settlementverifyDao.saveOperLog("结算确认", "结算确认失败："+msg);
                    }
				} else if (liqgid == 2) {// 手工结算 - 结算到银行卡
					int[] bankcard = settlementverifyDao.liqbankcard(mid);
					if (bankcard.length == 4)
						msg = "结算成功！";
					settlementverifyDao.saveOperLog("结算确认", "结算确认成功");
				} else if (liqgid == 5) {
					String midDlsCode=mid.getDlsCode();//POS商户代理商号
					if(Ryt.empty(midDlsCode)) 
						throw new Exception("结算异常:POS商户代理商号为空,且结算对象为'结算到代理商'");
					else if(!Ryt.empty(midDlsCode) && mid.getIsTodayLiq()==1)
						throw new Exception("结算异常:POS商户代理商号不为空,且结算对象为'T+1结算'商户");
	
					//记录商户流水
					AccSeqs params=new AccSeqs();
					params.setAmt(mid.getLiqAmt());
					params.setUid(mid.getMid());
					params.setAid(mid.getMid());
					params.setTbId(liqBatch);
					params.setTbName(Constant.FEE_LIQ_BATH);
					params.setRemark("结算到代理商");
					List<String> sqlsList=RecordLiveAccount.LiqToBankCard(params);
					//更新该批次结算状态
					String sql = "update fee_liq_bath set state=3 where batch='"+ Ryt.sql(mid.getBatch()) + "'";
					sqlsList.add(sql);
					String[] sqls = sqlsList.toArray(new String[sqlsList.size()]);
					int [] isSuc=settlementverifyDao.batchSqlTransaction(sqls);
					if (isSuc == null || isSuc.length != 4) {
						msg = "结算失败,数据库错误!";
					} else {
						// 启动线程调用YCK额度恢复接口
						StringBuffer signStr = new StringBuffer();// 待签名
																	// tranCode+date+posMid+amount+batchNo
						signStr.append("EN0020");
						signStr.append(DateUtil.today());
						signStr.append(mid.getMid());
						signStr.append(mid.getLiqAmt());
						signStr.append(mid.getBatch());
						String signedStr = URLEncoder.encode(RsaEncrypto.RSAsign(signStr.toString().getBytes(),Constant.YCK_CZ_PRIVATE_KEY).replaceAll("\r\n", ""), "UTF-8");

						Map<String, Object> reqPramas = new HashMap<String, Object>();
						reqPramas.put("tranCode", "EN0020");
						reqPramas.put("date", DateUtil.today() + "");
						reqPramas.put("dlsCode", midDlsCode);
						reqPramas.put("posMid", mid.getMid());
						reqPramas.put("amount", mid.getLiqAmt());
						reqPramas.put("batchNo", mid.getBatch());
						reqPramas.put("sign", signedStr);
						LogUtil.printInfoLog("SettlementVerifyService", "verifySettle", "YCK_req param:"+reqPramas+"  Url:"+Constant.YCKURL);
						// 调用YCK额度恢复
						Thread th = new Thread(new YCKrecharge(reqPramas,Constant.YCKURL));
						th.start();

						msg = "结算成功！";
						settlementverifyDao.saveOperLog("结算确认", "结算确认成功");
					}

				} else {
					msg = "结算对象异常！";
					settlementverifyDao.saveOperLog("结算确认", "结算确认异常，结算对象异常！");
				}

			} catch (Exception e) {
				exceptionBuff.append(mid.getMid()+ "—"+ RYFMapUtil.getInstance().getMerMap().get(mid.getMid()) + ",").append("\n");
				msg = "结算异常!";
				e.printStackTrace();
				settlementverifyDao.saveOperLog("结算确认", "结算确认失败");
			}
		}
		if (exceptionBuff.length() != 0) {
			exceptionBuff.deleteCharAt(exceptionBuff.lastIndexOf(",")).append("\n").append("的记录因异常结算失败!");
			msg = "商户为:" + "\n" + exceptionBuff.toString();
		}

		return msg;

	}
	
	/**
	 * 查询fee_liq_bath表并分页
	 * @param mid 商户号
	 * @param state 结算状态 1-已发起 2-已制表 3-已完成
	 * @param beginDate 查询起始日期
	 * @param endDate查询结束日期
	 * @param batch查询批次号
	 * @param pageIndex 页码
	 * @return
	 */
	public CurrentPage<FeeLiqBath> getFeeLiqBath(int pageNo, int beginDate, int endDate, String mid, int state, String batch,Integer mstate,Integer liqGid,String gid) {
		return settlementverifyDao.getFeeLiqBath(pageNo, beginDate, endDate, mid, state, batch,mstate, liqGid,gid);
	}

	/**
	 * 根据批次号查询结算批次表中的记录
	 * 
	 * @param batch
	 *            结算批次号
	 * @return 结算批次表的实体对象
	 */
	public FeeLiqBath getFeeLiqBathByBatch(String batch) {
		// begin_settlement.jsp中调用
		return settlementverifyDao.getFeeLiqBathByBatch(batch);
	}
	/**
	 * 结算确认的详细信息
	 * 
	 * @param batch
	 *            结算批次号
	 * @return 返回一个list 所选中的记录以及该条记录信息的详细
	 */
	public List<SettleDetail> getSettleDetail(String batch) {
		// admin_begin_settlement.jsp中调用
		return settlementverifyDao.getSettleDetail(batch);
	}
	/**
	 * 商户交易同步查询
	 * @param pageNo 页码
	 * @param beginDate  结算开始时间
	 * @param endDate   结算结束时间
	 * @param mid   商户id
	 * @param batch  批次号
	 * @param mstate  商户状态
	 * @return
	 */
	public CurrentPage<FeeLiqBath> getAccFeeLiqBath(int pageNo, int beginDate, int endDate, String mid,  String batch,Integer mstate,Integer liqState) {
		return settlementverifyDao.getAccFeeLiqBath(pageNo, beginDate, endDate, mid,  batch,mstate,liqState);
	}
	/**
	 * 商户结算单同步
	 * @param batch  批次号
	 * @return
	 */
	public String accSync(String batch,int beginDate, int endDate,String mid) {
		LogUtil.printInfoLog("商户结算单同步开始");
		StringBuffer items = new StringBuffer();
        //String url="http://192.168.18.68:8080/accounting-service-preposition/service";
		//String key="EAAoGBAMkYfh7MeT";
		GateRoute gateRoute =systemDao.queryGateRoteByGid("55002");
		String url=gateRoute.getRequestUrl();
		String key=gateRoute.getMerKey();
		Map<String, Object> reqPramas = new HashMap<String, Object>();
		//协议参数
		String checking="Y";
		reqPramas.put("tranCode", "ZH0036");
		reqPramas.put("version", "10");
		reqPramas.put("sys", "RYF");
		reqPramas.put("checking", checking);
		List<AccSyncDetail> accSyncDetails =settlementverifyDao.queryAccSyncDetails(batch);
		long sumAmount = settlementverifyDao.getSumAmount(batch);
		for (AccSyncDetail accSyncDetail : accSyncDetails) {
			 items.append(accSyncDetail.getInUserId());
			 items.append(",");
			 items.append(accSyncDetail.getType());
			 items.append(",");
			 items.append(accSyncDetail.getSeqNo());
			 items.append(",");
			 items.append(accSyncDetail.getOrderId());
			 items.append(",");
			 items.append(accSyncDetail.getOrderDate());
			 items.append(",");
			 items.append(Ryt.div100(accSyncDetail.getAmount()));
			 items.append(",");
			 items.append(Ryt.div100(accSyncDetail.getFee()));
			 items.append("|");
			
		}
		String date= DateUtil.getNowDateTime();
		//业务参数
		//reqPramas.put("merId", "111111111111111");//平台商户号
		reqPramas.put("merId", mid);//平台商户号
		reqPramas.put("batchNo", batch);//批次号
		reqPramas.put("items", items);//订单详情
		reqPramas.put("beginDate", beginDate);//开始时间
		reqPramas.put("endDate", endDate);//结束时间
		reqPramas.put("totalItems", accSyncDetails.size());//总笔数
		reqPramas.put("timestamp",date);//时间戳 即当前时间
		reqPramas.put("totalAmt", Ryt.div100(sumAmount));//总金额
		
		String aa ="10"+"ZH0036"+"RYF"+mid+batch+beginDate+endDate+accSyncDetails.size()+Ryt.div100(sumAmount)+checking+items+date+key;
		//String aa ="10"+"ZH0036"+"RYF"+"111111111111111"+batch+beginDate+endDate+accSyncDetails.size()+Ryt.div100(sumAmount)+checking+items+date+key;

		reqPramas.put("chkValue", MD5.getMD5(aa.getBytes()));
		try {
			String resStr=Ryt.requestWithPost(reqPramas,url);
			if (resStr.equals("Connection refused: connect")){
				//失败
				settlementverifyDao.addAccSyncResult(batch, 1, resStr);
				return "失败";
			}
			else if(resStr !=null && resStr!=""){
				//解析
				JSONObject json=JSONObject.fromObject(resStr);
			    String resCode=json.getString("resCode");
			  
			     if(resCode.equals("000")){
					//成功
					settlementverifyDao.addAccSyncResult(batch, 0," ");
					LogUtil.printInfoLog("SettlementVerifyService", "accSync", "推送成功,count:"+accSyncDetails.size());
					return "成功";
				}
				else{
					//失败
					  String resMsg=json.getString("resMsg");
					settlementverifyDao.addAccSyncResult(batch, 1, resMsg);
					LogUtil.printErrorLog("SettlementVerifyService", "accSync", "失败,原因:"+resMsg);
					return "失败";
				}
			}
			else{
				LogUtil.printInfoLog("连接失败");
				return "连接失败";
			}
			
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.printInfoLog(e.getMessage());
				return "系统异常";
			}
		
		
		
	}
	
	public FileTransfer downloadSyncDetail(String batch,String minfoName,String userId)throws Exception{
		List<SettleDetail> tlogList = settlementverifyDao.getSyncDetail(batch);
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add("序号,商户号,商户简称,账户号,用户平台ID,订单号,交易流水号,交易时间,支付金额,退款金额,交易净额,系统手续费,退回商户手续费".split(","));
		int i = 0;
		long totlePayAmt = 0;//支付总金额
		long totleTradeFee = 0;//支付总净额
		long totleRefAmt = 0;//退款总金额
		long totleMerFee = 0;//系统总手续费
		long totleFeeAmt = 0;//退回商户总手续费
		for (SettleDetail h : tlogList) {
			String[] str = { (i + 1) + "",h.getMid(),minfoName,h.getOutUserId(),userId,
					h.getOid(),h.getTseq(),h.getSysDate(),Ryt.div100(h.getAmount()),Ryt.div100(h.getRefAmt()),
					Ryt.div100(h.getTradeAmt()),Ryt.div100(h.getMerFee()),Ryt.div100(h.getFeeAmt())};
			totlePayAmt += h.getAmount();
			totleTradeFee += h.getTradeAmt();
			totleRefAmt += h.getRefAmt();
			totleMerFee += h.getMerFee();
			totleFeeAmt += h.getFeeAmt();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录","","","","","","","",Ryt.div100(totlePayAmt),Ryt.div100(totleRefAmt),Ryt.div100(totleTradeFee),Ryt.div100(totleMerFee),Ryt.div100(totleFeeAmt)};
		list.add(str);
		String filename = "LiqBath_" + DateUtil.today() + ".xlsx";
		String name = "结算单明细";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		//return null;
	}
	
	/**
	 * 处理YCK充值失败 发送报警邮件 (结算专用) 
	 * @param errMsg 错误信息
	 * @param params 参数
	 * @param reqUrl 请求地址
	 */
	public static void handleYCKCZFail(String errMsg,Map<String, Object> params,String reqUrl){
		
		LogUtil.printInfoLog("SettlementVerifyService.YCKrecharge", "run", errMsg);
		
		String title="[YCK充值]充值失败报警-"+DateUtil.today();
		
		//邮件内容
		StringBuffer content=new StringBuffer(errMsg+":   <br/>"+reqUrl);
		Iterator it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			content.append(String.valueOf(element.getKey()));
			content.append("=");
			content.append(String.valueOf(element.getValue()));
			content.append("&");
		}
		//发送报警邮件
		Thread th=new Thread(new SendEmailServer(content.toString(),title,"lv.xiaofeng@chinaebi.com"));
		th.start();
	
	}
	
	/**
	 * YCK充值  结算给代理商专用
	 * @author yang.yaofeng
	 *
	 */
	class YCKrecharge implements Runnable{
		
		private Map<String, Object> params;//请求参数
		private String reqUrl;//请求地址
		
		public YCKrecharge(Map<String, Object> params,String reqUrl){
			this.params=params;
			this.reqUrl=reqUrl;
		}

		@Override
		public void run() {
				String headStr="商户号:"+params.get("posMid")+"<br/>";
			try {
				String resStr=Ryt.requestWithPost(params,reqUrl);
				LogUtil.printInfoLog("SettlementVerifyService.YCKrecharge", "run", "YCK_recharge response:"+resStr);
				//解析返回数据
				Document doc = DocumentHelper.parseText(resStr);
				Element root = doc.getRootElement();
				String val=root.element("status").element("value").getText();
				if(val.equals("0"))
					LogUtil.printInfoLog("SettlementVerifyService.YCKrecharge", "run", "YCK系统充值成功:"+params.get("posMid"));
				else{
					String errMsg=root.element("status").element("msg").getText();
					handleYCKCZFail(headStr+"YCK系统充值失败，失败原因--"+errMsg+"",params,reqUrl);
					}
			} catch (Exception e) {
				handleYCKCZFail(headStr+"YCK系统充值异常--"+e.getMessage(),params,reqUrl);
			}
			
		}
	}

    /***
     *结算确认 代付发起失败
     * 自动结算交易
     * 自动代付
     * @param tlog
     */
    private void doReqRyfDfFail(TlogBean tlog){
        LogUtil.printInfoLog("SettleVerifyService", "doReqRyfFail", "tseq:"+tlog.getTseq());
        int bkDate=DateUtil.today();
        int bkTime=DateUtil.getCurrentUTCSeconds();
        String bkSeq="";
        MerAccHandleUtil.doModifyForFail(tlog.getTseq().toString(), tlog.getMid(), tlog.getError_msg(), tlog.getGate(), bkDate, bkTime,  tlog.getAmount().toString(), bkSeq, tlog.getGid(), tlog);
    }
}
