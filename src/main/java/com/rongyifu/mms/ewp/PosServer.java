package com.rongyifu.mms.ewp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.KeyStoreException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.com.chinaebi.dcsp2.exception.PipeUnreadyException;
import cn.com.chinaebi.xpe.paytrade.PaytradeConfigurator;
import cn.com.chinaebi.xpe.paytrade.PaytradeErrorStatus;
import cn.com.chinaebi.xpe.paytrade.PaytradeNetConfigurator;
import cn.com.chinaebi.xpe.paytrade.PaytradeRequest;
import cn.com.chinaebi.xpe.paytrade.PaytradeStatus;
import cn.com.chinaebi.xpe.paytrade.PaytradeStatusRequest;
import cn.com.chinaebi.xpe.paytrade.ReasonCode;
import cn.com.chinaebi.xpe.paytrade.RequestSender;
import cn.com.chinaebi.xpe.paytrade.exception.LoadNetConfigurationException;
import cn.com.chinaebi.xpe.paytrade.remote.RemoteDataReceive;
import cn.com.chinaebi.xpe.paytrade.socket.PayTradeSocketClient;
import cn.com.chinaebi.xpe.paytrade.util.DebugUtil;

import com.rongyifu.mms.bank.b2e.B2ERet;
import com.rongyifu.mms.bean.GlobalParams;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MailSenderInfo;
import com.rongyifu.mms.utils.RecordLiveAccount;

/** 返回Pos交易状态 如下   
 *ReasonCode.SUCCESS//交易成功
 *ReasonCode.PAYMENT_FAILED//交易失败 
 *ReasonCode.ACCEPT_SUCCESS//处理中  返回Pos受理状态
 *ReasonCode.failure_suspected   //卡号失败三次 RYF订单存交易失败状态   返回Pos 拒绝状态
 */
public class PosServer implements RemoteDataReceive {

	private static Logger logger = Logger.getLogger(PosServer.class);
	public static String tradetype = "0200";
	public static String process = "910000";
	public static String gainrefer = "1234567890ABCDEFGHI";
	public static String gaininfo = "server info";
	public static String acceptcde = "49012901";
	public static Calendar gaintime = Calendar.getInstance();
	public ReasonCode result = ReasonCode.ACCEPT_SUCCESS;
	public static String gainfee = "C00001234";
	private  AdminZHDao zhDao=new AdminZHDao();
	private  MerInfoDao mInfoDao=new MerInfoDao();
	private CallDaiFu callDaiFu=new CallDaiFu();
	public static PaytradeConfigurator ic = PaytradeConfigurator.getInstance();
	public static PaytradeNetConfigurator pnc = PaytradeNetConfigurator
			.getInstance();
	public static PayTradeSocketClient client = null;
	private static int gate=40000;
	
	private static PosServer instance = new PosServer();
	
	private static volatile boolean isInit  = false;
	
	private PosServer(){}
	private Map<String, String[]> failMap=new HashMap<String, String[]>();//连续三次失败 冻结该卡  Key cardNo ;value Integer[date,time,failCount]  
	private static void doInit() throws Exception{
		try {

			ic.loadKeyStore(PosConstants.ksName, PosConstants.alias,
					PosConstants.storePass, PosConstants.keyPass);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
//			pnc.loadConfiguration("192.168.20.177", 19881);
			pnc.loadConfiguration(19881);
		} catch (LoadNetConfigurationException e) {
			// TODO ... socket 参数配置错误
			logger.info("socket 初始化失败,请管理员查看！");
			sendMail("系统报警邮件-pos支付接口", "socket 参数配置错误,初始化失败,请管理员查看");
			throw e;
		}

		client = PayTradeSocketClient.getInstance();
		if (client == null) {
			// TODO ... 获取客户端发送实例失败，确保socket配置是否正确，防火墙端口是否正常
			logger.info("获取客户端实例失败,请管理员查看！");
			sendMail("系统报警邮件-pos支付接口",
					"获取客户端发送实例失败，确保socket配置是否正确，防火墙端口是否正常");
			
			throw new Exception("获取客户端实例失败,请管理员查看！");
			//System.exit(0);
		}
	
	}
	
	public static PosServer getInstance(){
		if(!isInit){
			try {
				doInit();
				isInit = true;
			} catch (Exception e) {
				if(logger.isDebugEnabled())
					logger.error(e);
			}
		}
		ic.setRemoteDataRcv(instance);
		return instance;
	}
	
	
	/*
	 * 初始化配置信息，确保都能成功连接
	 */
	//static {}

	public void send(String key, PaytradeRequest req) {
		PaytradeStatus response = new PaytradeStatus(tradetype, process, key,
				acceptcde, req.getMercode(), req.getTermid(), result,
				gainrefer, gaintime, gainfee, gaininfo);
//		DebugUtil.printPaytradeStatus(response);
		Map<String, String> m=new HashMap<String, String>();
		m.put("tradetype", tradetype);			m.put("gaininfo", gaininfo);
		m.put("gainrefer", gainrefer);			m.put("process", process);
		m.put("key", key);						m.put("acceptcde", acceptcde);
		m.put("merCode", req.getMercode());		m.put("termid", req.getTermid());
		m.put("result", response.getResult().getDescription());
		LogUtil.printInfoLog("PosServer", "send", "return params for Pos", m);

		try {
			RequestSender.send(response);
		} catch (PipeUnreadyException e) {
			if (client.rebuildDcspClient()) {
				send(key, req);
			} else {
				// TODO ... socket 连接失败
			}
		}
	}

	public void send(String key, ReasonCode _result) {
		PaytradeErrorStatus response = new PaytradeErrorStatus(tradetype,
				process, key, _result, gaininfo);

		DebugUtil.printPaytradeErrorStatus(response);

		try {
			RequestSender.send(response);
		} catch (PipeUnreadyException e) {
			if (client.rebuildDcspClient()) {
				send(key, _result);
			} else {
				// TODO ... socket 连接失败
			}
		}
	}

	@Override
	public void onDataError(String unionKey, String errorCode) {
		if ("Invalid XML document or invalid signature".equals(errorCode)) // 签名验证失败，核心系统发起冲正交易
			send(unionKey, ReasonCode.AUTHENTICATION_FAILED);
		else if ("Invalid XML contents".equals(errorCode)) // 数据解析失败，核心系统发起冲正交易
			send(unionKey, ReasonCode.TRADE_PACKAGE_DATA_ERROR);
		else
			send(unionKey, ReasonCode.UNKNOWN_ERROR); // 其它错误，核心系统发起冲正交易
	}
	
	@Override
	public void onDataReceive(String unionKey, PaytradeRequest req) {
		String toaccName = req.getAccountname();// 转入卡账户名
		String amt = req.getAmount();// 交易金额
		String outCard = req.getOutcard();// 转出卡号
		String inCard = req.getIncard();// 转入卡号
		String tobankId = req.getBankid();// 转入银行行号
		String merId = req.getMercode();
		String reference=req.getReference();//系统参考号
		String trace = req.getTrace();// 交易流水
		process = req.getProcess();// 交易处理码 网关吗 。。。
		tradetype = req.getTradetype();// 交易类型
		String termid=req.getTermid();//终端号
		String hints=req.getHints();
//		String userCode="00000000000";//用户号
		String[] hintArray=hints.split("\\|");
		String bill=hintArray[0];
		String psam="0700000000000000";//刷卡设备号 save
		String rand4=Ryt.createRandomStr(4);//新增4位随机数
		String ordId=bill + "-R"+rand4;//pos订单号  新增4位随机数
		int date=DateUtil.today();
		int time=DateUtil.now();
		Map<String, String> params=new HashMap<String, String>();
		params.put("accName", toaccName); params.put("amt", amt);
		params.put("outCard", outCard); params.put("inCard", inCard);
		params.put("bankId", tobankId);   params.put("merId", merId);
		params.put("trace", trace);     params.put("process", process);
		params.put("tradetype", tradetype); params.put("hints", hints);
		LogUtil.printInfoLog("PosServer", "onDataReceive", "pos转账参数", params);
		if(zhDao.hasMid(merId)==0){
			logger.info("商户号不存在！merId not exists "+merId);
			return;
		}else{
			try{
				Minfo minfo=mInfoDao.getMinfoById(merId);
				/*** 组装订单信息 **/
				String fee="0";String toBankName="";
				String cardFlag="1";/**卡折标志**/  String providId="0";//省份
				String pState=String.valueOf(Constant.DaifuPstate.AUDIT_SUCCESS);
				Integer[] gateInfo=getGateForBkNo(tobankId);
				Integer payGate=gateInfo[0];
				Integer gate=gateInfo[1];
				TrOrders trOrders = callDaiFu.getOrders(minfo.getName(), ordId, amt, fee, merId,
						inCard, toaccName, toBankName, tobankId, providId, cardFlag, amt, String.valueOf(Constant.DaiFuTransState.PAY_PROCESSING), outCard, "",
						(short)Constant.DaiFuTransType.PAY_TO_PERSON, gate, pState);
				if (failMap.containsKey(inCard)){
					/**卡 -交易失败三次 处理函数**/
					boolean res=handleFailCard3(inCard,trOrders,unionKey,req,termid,trace,reference,psam);
					if(!res){ 	return; }
				}
				
				callDaiFu.saveTrOrder(trOrders);
				String ip="0";
				String type=String.valueOf(Constant.TlogTransType.PAYMENT_FOR_PRIVATE);
				String errorMsg="";
				callDaiFu.saveTlog(ip, String.valueOf(date), merId, ordId, amt, type, date, date, time,payGate,inCard,toaccName,tobankId,termid,trace,reference,psam,gate,PayState.WAIT_PAY,errorMsg);
				// 发起代付先扣可用余额
				String reduceBalance = RecordLiveAccount.calUsableBalance(merId, merId, Long.parseLong(amt), Constant.RecPay.REDUCE);
				zhDao.update(reduceBalance);
				String[] str=callDaiFu.queryTseq(ordId);
				String tseq=null;
				B2ERet ret=null;
				ret = callDaiFu.callBank(trOrders, trOrders.getGate(), 5,str[0]);
				tseq=callDaiFu.retTseq(str[0]);
				acceptcde = String.valueOf(gate);
				if (ret.getTransStatus() == 2) {
					result = ReasonCode.ACCEPT_SUCCESS;
				} else if(ret.getTransStatus()==1 ) {
					result = ReasonCode.PAYMENT_FAILED;
					/**交易失败卡号 失败次数累计操作 handleFailCardSum**/
					handleFailCardSum(inCard, date, time, str);
				}else if(ret.getTransStatus()==3){
						result = ReasonCode.PAYMENT_FAILED;
				}else if(ret.getTransStatus()==0){
					result=ReasonCode.SUCCESS;
					if(failMap.containsKey(inCard)){
						failMap.remove(inCard);
					}
				}else{
					 logger.info("返回状态异常,返回状态为："+ret.getTransStatus());
					 return;
				}
				gaininfo = ret.getMsg();
				gainfee="0";
				gainrefer=tseq;
				
			}catch(Exception e){
				LogUtil.printErrorLog("PosServer", "onDataReceive", "转账汇款异常", params,e);
				/***按超时处理****/
				return ;
			}
			
			send(unionKey, req);
		}
	}

	

	@Override
	public void onDataReceive(String unionKey, PaytradeStatusRequest req) {
		DebugUtil.printPaytradeStatusRequest(req);

	}

	/*public static void main(String[] args) throws InterruptedException {
		ic.setRemoteDataRcv(new PosServer());
	}*/

	/**
	 * 发送报警邮件
	 */
	public static void sendMail(String subject, String content) {
		List<GlobalParams> list = new SystemDao().queryAllParams();
		for (GlobalParams globalParams : list) {
			if (globalParams.getParName().equals("MailTos")) {
				if (globalParams.getParValue() != null
						|| !globalParams.getParValue().equals("")) {
					String[] adds = globalParams.getParValue().split(",");
					for (String string : adds) {
						MailSenderInfo.sendmail(subject, content, string);
					}
				}
			}
		}
	}
	/***
	 * 
	 * @param inCard
	 * @param o
	 * @param unionKey
	 * @param req
	 * @param termid
	 * @param trace
	 * @param reference
	 * @param psam
	 */
	public boolean handleFailCard3(String inCard,TrOrders o,String unionKey, PaytradeRequest req,String termid,String trace, String reference,String psam ){
		String[] array=failMap.get(inCard);
		Integer dateF=Integer.parseInt(array[0]);int countF=Integer.parseInt(array[2]);
		String tseqs=array[3];
		int date=DateUtil.today();
		int time=DateUtil.now();
		if (dateF==date){
			if(countF==3){
				String toCardH=inCard.substring(0, 6);String toCardE=inCard.substring(inCard.length()-4, inCard.length());
				String toAccNoNew=toCardH+"******"+toCardE;//处理卡号  前6后4 中间6个*
				logger.info("卡号："+toAccNoNew+"连续失败三次！");
				String content="一天内代付连续失败3笔,交易流水号：\r\n"+tseqs;
				String title="代付交易报警"+DateUtil.today();
				String receive="";
				o.setState(Short.parseShort("35"));
				callDaiFu.saveTrOrder(o);
				callDaiFu.saveTlog("0", String.valueOf(date), o.getUid(), o.getOrgOid(), o.getTransAmt().toString(), "11", date, date, time, AppParam.bocomm_gate.get(o.getToBkNo()),inCard,o.getToAccName(),o.getToBkNo(),termid,trace,reference,psam,gate,PayState.FAILURE,"卡号："+toAccNoNew+"连续失败三次！");
				//异步发送报警邮件   没有返回
				SendEmailServer emailServer=new SendEmailServer(content, title, receive);
				Thread thread=new Thread(emailServer);
				thread.start();
				String[] str=callDaiFu.queryTseq(o.getOrgOid());
				String tseq=callDaiFu.retTseq(str[0]);
				result=ReasonCode.FAILURE_SUSPECTED; //返回Pos拒绝状态
				gaininfo ="卡号："+toAccNoNew+"连续失败三次！";
				gainfee="0";
				gainrefer=tseq;
				send(unionKey, req);
				return false;
			}
		}
		return true;
	}
	
	/****
	 * 失败次数累加   成功交易后 失败次数归0
	 * @param inCard
	 * @param date
	 * @param time
	 * @param str
	 */
	protected void handleFailCardSum(String inCard, int date, int time,
			String[] str) {
		if(failMap.containsKey(inCard)){
			String[] array=failMap.get(inCard);
			String t=array[3];//之前失败的流水
			Integer dateF=Integer.parseInt(array[0]);Integer timeF=Integer.parseInt(array[1]);Integer countF=Integer.parseInt(array[2]);
			if(dateF==date){//当天
				//累加失败次数
			    countF+=1;
				String[] array2={String.valueOf(date),String.valueOf(timeF),String.valueOf(countF),str[0]+","+t};
				failMap.put(inCard, array2);
			}else{//新一天 重置
				String[] array2={String.valueOf(date),String.valueOf(time),"1",str[0]};
				failMap.put(inCard, array2);
			}
		}else{//新纪录 录入  
			String[] array={String.valueOf(date),String.valueOf(time),"1",str[0]};
			failMap.put(inCard, array);
		}
	}
	
	/****
	 * @param bkNo 行号
	 * @return Integer
	 * Integer[]{gid,gate} 渠道，网关号
	 */
	protected Integer[] getGateForBkNo(String bkNo) {
		 Integer gate=AppParam.bocomm_gate.get(bkNo);
			if(gate==null){
				Iterator<String> iterator=AppParam.bocomm_gate.keySet().iterator();
				String top3=bkNo.substring(0, 3);
				while (iterator.hasNext()) {
					String key=iterator.next();
					if(key.matches("(?="+top3+")(\\d)*")){
						gate=AppParam.bocomm_gate.get(key);
						break;
					}
				}
			}
			Integer gid=40000;
			if(gate==71192){
				gid=40006;
			}
		 return new Integer[]{gate,gid};
	}
	
	
	
}
