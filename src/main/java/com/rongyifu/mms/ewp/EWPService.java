package com.rongyifu.mms.ewp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.infosec.util.Base64;

import com.rongyifu.mms.bank.BOCOM;
import com.rongyifu.mms.bank.Ceb;
import com.rongyifu.mms.bank.ICBC;
import com.rongyifu.mms.bank.b2e.DaifuAutoRun;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.RiskControlDao;
import com.rongyifu.mms.datasync.LiqDataProcessor;
import com.rongyifu.mms.datasync.MerInfoAlterSync;
import com.rongyifu.mms.datasync.PosSyncFile;
import com.rongyifu.mms.ewp.ryf_df.CallRyfPayProcessor;
import com.rongyifu.mms.merchant.MerContractExpiresNote;
import com.rongyifu.mms.modules.Mertransaction.service.YCKBillChkService;
import com.rongyifu.mms.modules.liqmanage.service.TrDetailsService;
import com.rongyifu.mms.modules.transaction.service.QueryTradeStatisticService;
import com.rongyifu.mms.quartz.QuartzUtils;
import com.rongyifu.mms.service.DesSign2DBService;
import com.rongyifu.mms.unionpaywap.UnionPayWap;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;

public class EWPService {
	
	private static final String SIGN_STR="46abc7f01f61959bfdf0d24796eefaf1";
	
	public static String call(Map<String, String> p) {

		String transCode = p.get("transCode");
		if (null == transCode || transCode.isEmpty()) {
			return "<h1> This is MMS </h1>";
		}
		try {

			if (transCode.equals("RYF001")) {
				String signText = p.get("signText");
				return buildEWPXml("0", BOCOM.genSignString(signText));
			} else if (transCode.equals("RYF002")) {
				// 校验参数
				if (p.get("phoneNo") == null || p.get("tseq") == null
						|| p.get("transAmt") == null
						|| p.get("sysDate") == null || p.get("retURL") == null)
					return buildEWPXml("1", "参数传递错误");
				return buildEWPXml("0", BOCOM.createBCMPreOrder(p));
			}

			else if (transCode.equals("RYF003")) {
				// 校验参数
				if (p.get("tseq") == null || p.get("transAmt") == null
						|| p.get("sysDate") == null || p.get("retURL") == null)
					return buildEWPXml("1", "参数传递错误");
				return buildEWPXml("0", ICBC.getData(p));
			} else
			// ArgList =
			// [{"transCode","RYF004"},{},{"merchantNo",?B2B_MERID},{"transAmt",Amount},{"tseq",Tseq},{"payAcc",Account},{"vPeriod",ValidatePeriod}],
			if (transCode.equals("RYF004")) {
				// 校验参数
				if (p.get("merchantNo") == null || p.get("transAmt") == null
						|| p.get("tseq") == null || p.get("payAcc") == null
						|| p.get("vPeriod") == null || p.get("recAccNo") == null)
					return buildEWPXml("1", "参数传递错误");
				return buildEWPXml("0", BOCOM.genB2BWebSignString(p));
			} else

			if (transCode.equals("RYF005")) {
				// 校验参数
				if (p.get("NotifyMsg") == null)
					return "F";
				// return buildEWPXml("1", "参数传递错误);
				try {
					// return buildEWPXml("0", BOCOM.verifyB2C(p));

					return BOCOM.verifyB2C(p);

				} catch (Exception e) {
					e.printStackTrace();
					//String s = Ryt.getParameter("JHV");
					//if (s != null && s.equals("1"))
						//return "S";
					return "F";
				}

			} else if (transCode.equals("RYF006")) {
				// 订单号列�- �'|' 分割* 有成�返回 订单号，金额，成功状态S，银行流水号|.... 否则返回 ""
				// 校验参数
				if (p.get("orders") == null || p.get("orders").length() == 0)
					return "";
				// return buildEWPXml("1", "参数传递错误);
				try {
					// return buildEWPXml("0", BOCOM.verifyB2C(p));
					return BOCOM.query(p);

				} catch (Exception e) {

					e.printStackTrace();
					return "";
				}

			}else if (transCode.equals("RYF007")) {
				// 校验参数
				if (p.get("NetpayNotifyMsg") == null || p.get("NetpayNotifyMsg").length() == 0)
					return "";
				try {
					// return buildEWPXml("0", BOCOM.genB2BVerifyString(p));
					return BOCOM.genB2BVerifyString(p);
				} catch (Exception e) {
					e.printStackTrace();
					return "";
				}

			} else if (transCode.equals("icbcb2c")) {

				try {
					// return buildEWPXml("0", BOCOM.verifyB2C(p));
					return ICBC.genB2CWebForm(p);

				} catch (Exception e) {

					e.printStackTrace();
					return "";
				}

			}

			else if (transCode.equals("icbcb2cveri")) {

				return ICBC.icbcb2cveri(p);
			}
			else if (transCode.equals("icbcwapsign")) {

				return ICBC.icbcwapsign(p);
			}
			else if (transCode.equals("icbcwapveri")) {

				return ICBC.Wapicbcveri(p);
			}
			else if (transCode.equals("icbcb2b")) {

				return ICBC.sign(p);
			}else if(transCode.equals("icbcb2bveri")){
				return ICBC.B2bVerify(p);
			}
			else if(transCode.equals("unionpaywapsign")){
				return UnionPayWap.unionPayWapSign(p);
			}
			else if(transCode.equals("unionpaywapnotify")){
				return UnionPayWap.unionPayWapNotify(p);
			}
			else if(transCode.equals("unionpaywapnotifyret")){
				return UnionPayWap.unionPayWapNotifyRet(p);
			}

			
			else if (transCode.equals("RYF100")){
				
				return DaifuPay.queryOrderState(transCode, p.get("tseq")) ;
			}
			else if (transCode.equals("abc_b2b")) //农业银行把b2b
			{
				return Abc_b2b_pay.abc_b2b(p);
			}
			else if (transCode.equals("abc_b2b_query")) //农业银行把b2b
			{
				
				return Abc_b2b_pay.abc_b2b_query(p);
			}
			else if (transCode.equals("abc_b2b_load")) //农业银行把b2b�
			{
				return Abc_b2b_pay.abc_b2b_load(p);
			}else if(transCode.equals("ceb_b2c"))//光大银行单笔查询
			{
				return Ceb.query(p);
			} else if(transCode.equals("DF01")) {
				return TestUtil.getBocomResult(p);
			} else if(transCode.equals("DF02")) {
				return TestUtil.bocSign();
			} else if(transCode.equals("DF03")) {
				return TestUtil.getBocSupportBank(p);
			} else if(transCode.equals("DF04")) {
				return TestUtil.updateDfQueryFlag(p);
			} else if(transCode.equals("updatePosData")) {
				return TestUtil.updatePosData(p);
			} else if(transCode.equals("openAdmin")) {
				return TestUtil.openAdmin();
			} else if(transCode.equals("createPosFile")){//手工创建代理系统文件
				Integer date=Integer.parseInt(p.get("date"));
				Integer flag=Integer.parseInt(p.get("flag"));
				return PosSyncFile.posSyncFile(date,flag);
			} else if(transCode.equals("POS_CITIC")){
				String flowId = p.get("flowId");
				new LiqDataProcessor().process_Pos_CITIC_Data(flowId);
				return "OK";
			}else if(transCode.equals("boc_b2e_sign")){
				return new DaifuAutoRun().sign();
			}else if(transCode.equals("settleCase")){ //手工调整账户余额
				SettleCase.handleSettle();
				return "ok";
			}else if(transCode.equals("queryFee")){//代理商结算查询手续费
				return QueryFee.queryFee(p);
			}else if(transCode.equals("init_mer_accounts")){//商户账务初始化数�
				Integer previousBDate=Integer.parseInt(p.get("bDate"));
				Integer previousEDate=Integer.parseInt(p.get("eDate"));
				return new InitMerAccServer().initMerAcc(previousBDate, previousEDate);
			}else if(transCode.equals("autoDf")){
				String bkNo=p.get("bkNo");
				return new Test_AutoDf().run(bkNo);
			}else if(transCode.equals("yckBillChk")){
				String bdate=p.get("bdate");
				String edate=p.get("edate");
				return new YCKBillChkService().getBillChkData(bdate, edate) ;
			}else if("QuartzUtilsStatus".equals(transCode)){//查询定时任务状态
				return QuartzUtils.showStatus();
			}else if("QuartzUtilsRestart".equals(transCode)){//重启定时任务
				return QuartzUtils.restart()?"重启成功":"重启失败";
			}else if("QuartzUtilsShutDown".equals(transCode)){//关闭定时任务
				return QuartzUtils.shutDown()?"关闭失败":"关闭成功";
			}else if("importWhiteList".equals(transCode)){//手工导入白名单
				String month =p.get("month");
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					//验证month格式
					Date d = sdf.parse(month);
					month = sdf.format(d);
					int count = new RiskControlDao().batchAddWhiteList(Integer.valueOf(month));
					return "导入成功，共导入 "+count+" 条记录。";
				} catch (Exception e) {
					return "导入失败";
				}
			}else if("SJMX".equals(transCode)){
				try {
					String dateStr = p.get("date");
					TrDetailsService service = new TrDetailsService();
					int count = service.deleteByDateAndGid(Integer.valueOf(dateStr), 40006);
					LogUtil.printInfoLog(EWPService.class.getName(), "execute", "删除重复数据 "+count+" 条, date:"+dateStr+",gid:"+40006);
					return new TrDetailsService().downLoadSJMX(dateStr);
				} catch (Exception e) {
					return "下载失败";
				}
			}else if("DF05".equals(transCode)){ //盛京明细下载
				return TestUtil.getSJDetail(p);
			}else if("DF06".equals(transCode)){ //盛京流水结果查询
				return TestUtil.testSjQueryState(p);
			}else if("DF07".equals(transCode)){//交行银企直连流水查询
				return TestUtil.getBocommResultForYQ(p);
			}else if("DF08".equals(transCode)){//处理新代付订单没有渠道的问题
				return TestUtil.processDfOrder(p);
			}else if("GENMMSNOTICE".equals(transCode)){
				String dateStr=p.get("beginDate");
				if(Ryt.empty(dateStr)||"null".equals(dateStr)||dateStr==null)return "日期错误";
				Integer beginDate=Integer.parseInt(dateStr);
				return MerContractExpiresNote.doAddNote(beginDate);
			}else if("ZHXTXXTB".equals(transCode)){//手工同步账户系统商户信息
				String sign=p.get("sign");
				String mid=p.get("mid");
				return new MerInfoAlterSync().syncSg(sign, mid);
			}else if("JYTJ".equals(transCode)){//交易统计
				try {
					Integer date=Integer.valueOf(p.get("date").trim());
					boolean isTlog=Boolean.valueOf(p.get("isTlog").trim());
					new QueryTradeStatisticService().doStatistics(date, isTlog);
					return "交易统计成功";
				} catch (Exception e) {
					return "交易统计失败";
				}
			}else if("DES_ENC_MINFO".equals(transCode)){//商户信息存储加密
				String sign=p.get("sign");
				if(!SIGN_STR.equals(sign)){
					return "验证签名失败!";
				}
				else{
					if(new DesSign2DBService().encMinfo2DB())
						return "商户信息存储加密成功";
					else
						return "商户信息存储加密失败,请查看操作日志！";
				}
			}else if("DES_DEC_MINFO".equals(transCode)){//商户信息存储解密
				String sign=p.get("sign");
				if(!SIGN_STR.equals(sign)){
					return "验证签名失败!";
				}
				else{
					if(new DesSign2DBService().decMinfo2DB())
						return "商户信息存储解密成功";
					else
						return "商户信息存储解密失败,请查看操作日志！";
				}
				
			}else if("RYF_DF_ASYNC_RESP".equals(transCode)){ //接收ryf_df 异步结果
				
				return CallRyfPayProcessor.RecvRyfPayResult(p);
			}
			
			// return buildEWPXml("1", "非法请求");
			return "<h1> This is MMS </h1>";

		} catch (Exception e) {
			e.printStackTrace();
			return buildEWPXml("1", e.getMessage());
		}

	}

	/**
	 * 生成在业务方法中业务处理后返回XML文件内容
	 * 
	 * @return XML文件内容 <res><state></state><result></result></res> state 0成功1失败
	 */
	private static String buildEWPXml(String state, String resultString) {
		StringBuffer res = new StringBuffer();
		res.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><res><state>");
		res.append(state);
		res.append("</state><result>");
		res.append(resultString);
		res.append("</result></res>");
		return res.toString();
	}

	/**
	 * 刷新ewp ETS表记 
	 * ryf 4.6 根据表名刷新是否刷新ryf_df
	 * @return
	 */
	public static boolean refreshEwpETS(Map<String, Object> p) {
		boolean flag = true;
		String tableName = p.get("t").toString();
		//visit_ip_config	b2e_gate	global_params	df_gate_route需要刷新ryf_df
		if("route_rule".equals(tableName)){
			return refreshRyfDfETS(tableName);//不需要刷新pay 直接返回
		}else if("visit_ip_config".equals(tableName) || "b2e_gate".equals(tableName) || "global_params".equals(tableName)){
			flag = refreshRyfDfETS(tableName);
		} 
		
		String url = Ryt.getEwpPath();
		if (url.contains(",")) {
			String u1 = url.split(",")[0];
			String u2 = url.split(",")[1];
			String res1 = Ryt.requestWithPost(p, u1 + "pub/rets");
			String res2 = Ryt.requestWithPost(p, u2 + "pub/rets");
			return flag && (res1.equals("ok") || res2.equals("ok"));
		} else {
			String res = Ryt.requestWithPost(p, url + "pub/rets");
			LogUtil.printInfoLog("EWPService", "refreshEwpETS", "result:"+res);
			return flag && res.equals("ok");
		}
		// String url = Ryt.getEwpPath()+ "pub/rets";
		// String res = Ryt.requestWithPost(p,url);
		// return res.equals("ok");
	}
	
	private static boolean refreshRyfDfETS(String tableName){
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("tableName", tableName);
		
		String md5Key = ParamCache.getStrParamByName("MD5_KEY");
		String dfUrl = ParamCache.getStrParamByName("DF_URL");
		
//		chkValue	数字签名	MD5(tableName + md5Key). toUpperCase,其中md5Key从global_params表中获取
		String reqUrl = dfUrl+"refresh/refresh_entry";
		String chkValue = MD5.getMD5((tableName+md5Key).getBytes()).toUpperCase();
		params.put("chkValue", chkValue);
		
		String response =  Ryt.requestWithPost(params, reqUrl);
		LogUtil.printInfoLog("EWPService", "refreshRyfDfETS", "tableName:"+tableName+", chkValue:"+chkValue+", url:"+reqUrl+", result:"+response);
		return "ok".equals(response);
	}
	
	/**
	 * 调用ewp发送邮�
	 * @param content 内容
	 * @param title 标题
	 * @param receive 接收�
	 * @throws Exception 
	 */
	public static boolean sendMail(String content,String title,String receive) throws Exception{
		if(Ryt.empty(title) || Ryt.empty(content))
			return false;
		
		if(Ryt.empty(receive))
			receive = "";
		
		String url = Ryt.getEwpPath();
		String recAddr=Base64.encode(receive.trim());
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("title", Base64.encode(title.trim()));
		paramMap.put("content", Base64.encode(content.trim()));
		paramMap.put("receive",recAddr );
		paramMap.put("sign",MD5.getMD5(recAddr.getBytes()) );
		String res = Ryt.requestWithPost(paramMap, url + "pub/sendMail");
		return res.equals("success");
	}
}
