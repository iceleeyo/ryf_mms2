package com.rongyifu.mms.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import jxl.Sheet;
import jxl.Workbook;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bank.b2e.B2EProcess;
import com.rongyifu.mms.bank.b2e.B2ERet;
import com.rongyifu.mms.bank.b2e.B2ETrCode;
import com.rongyifu.mms.bank.b2e.B2ETradeResult;
import com.rongyifu.mms.bank.b2e.GenB2ETrnid;
import com.rongyifu.mms.bank.b2e.PayEnter;
import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.BatchB2B;
import com.rongyifu.mms.bean.DaiFu;
import com.rongyifu.mms.bean.DaiFuBean;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Constant.PayState;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.dao.MerZHDao;
import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.EmaySMS;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.ParamUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class DaiFuService {
	
	// 一次最多200笔
//	private  int maxSize = ParamCache.getIntParamByName("DAI_FA_FILE_MAX");
	private MerZHDao merZHDao = new MerZHDao();
	private AdminZHDao adminZHDao=new AdminZHDao();
	private TransactionDao transactionDao=new TransactionDao();
	private B2EProcess b2eProcess=null;
	/**
	 * 查询代发经办数据(旧)
	 * @param uid 用户ID
	 * @param num 条数
	 * @param trans_flow
	 * @return
	 */
	public List<TrOrders> queryDaiFaJingBan(String uid ,int num){
		return adminZHDao.queryDaiFaJingBan(uid,num);
	}
	
	/****
	 * 代发经办审核
	 * @param uid
	 * @param num
	 * @param trans_flow
	 * @param ptype
	 * @return
	 */
	
	public CurrentPage<TrOrders> queryDaiFaJingBan_SH(Integer pagNo,Integer num,String uid ,String trans_flow ,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate){
		return adminZHDao.queryDaiFaJingBan_SH(pagNo,num,uid,trans_flow,ptype,oid,mstate,bdate,edate);
	}
	
	/****
	 * 查询代发经办数据(新)
	 * @param uid id
	 * @param num 条数
	 * @param trans_flow  批次号
	 * @return
	 */
	public CurrentPage<TrOrders> queryDaiFaJingBan_(Integer pagNo,Integer num,String uid ,String trans_flow ,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate){
		return adminZHDao.queryDaiFaJingBan(pagNo,num,uid,trans_flow,ptype,oid,mstate,bdate,edate);
	}
	/****
	 * 查询代发经办下载
	 * @param uid id
	 * @param num 条数
	 * @param trans_flow  批次号
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downqueryDaiFaJingBan(String uid ,String trans_flow ,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate) throws Exception{
		  CurrentPage<TrOrders> TrListPage=adminZHDao.queryDaiFaJingBan(1,-1,uid,trans_flow,ptype,oid,mstate,bdate,edate);;
		  List<TrOrders> TrList=TrListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("序号,系统订单号,交易类型,批次号,用户,账户ID,账户名称,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号".split(","));
		int i = 0;
		try {
			for (TrOrders t : TrList) {
				String[] str = { (i + 1) + "",t.getOid(),7==t.getPtype()?"对私代付":"对公代付"+"",t.getTrans_flow(), t.getUid(),t.getAid(),t.getAname(),
						Ryt.div100(t.getTransAmt())+"",Ryt.div100(t.getTransFee())+"",Ryt.div100(t.getPayAmt())+"",
						t.getToBkName(),t.getToAccName(),t.getToAccNo()};
				totleTransAmt += t.getTransAmt();
				totlePayAmt += t.getPayAmt();
				totleTransFee+=t.getTransFee();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("DaiFuService", "downqueryDaiFaJingBan", "uid=" + uid + " oid=" + oid, e);
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","", Ryt.div100(totlePayAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totleTransAmt)+"","","","" };
		list.add(str);
		String filename = "DaiFaJingBan_" + DateUtil.today() + ".xls";
		String name = "代发经办表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
		
	}
	/****
	 * 查询代发经办审核下载
	 * @param uid id
	 * @param num 条数
	 * @param trans_flow  批次号
	 * @return
	 * @throws Exception 
	 */
	public FileTransfer downqueryDaiFaJingBansh(String uid ,String trans_flow ,Integer ptype,String oid,Integer mstate,Integer bdate,Integer edate) throws Exception{
		  CurrentPage<TrOrders> TrListPage=adminZHDao.queryDaiFaJingBan_SH(1,-1,uid,trans_flow,ptype,oid,mstate,bdate,edate);;
		  List<TrOrders> TrList=TrListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		long totleTransFee=0;
		list.add("序号,系统订单号,交易类型,批次号,用户,账户ID,账户名称,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号".split(","));
		int i = 0;
		try {
			for (TrOrders t : TrList) {
				String[] str = { (i + 1) + "",t.getOid(),(7==t.getPtype()?"对私代付":"对公代付")+"",t.getTrans_flow(), t.getUid(),t.getAid(),t.getAname(),
						Ryt.div100(t.getTransAmt())+"",Ryt.div100(t.getTransFee())+"",Ryt.div100(t.getPayAmt())+"",
						t.getToBkName(),t.getToAccName(),t.getToAccNo()};
				totleTransAmt += t.getTransAmt();
				totlePayAmt += t.getPayAmt();
				totleTransFee+=t.getTransFee();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("DaiFuService", "downqueryDaiFaJingBansh", "uid=" + uid + " oid=" + oid, e);
		}
		String[] str = { "总计:" + i + "条记录", "","","","","","", Ryt.div100(totlePayAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totleTransAmt)+"","","","" };
		list.add(str);
		String filename = "DaiFaJingBan_" + DateUtil.today() + ".xls";
		String name = "代发审核表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
		
	}
	
	
	/**
	 * 进行代发经办处理  订单状态改成代发经办处理中
	 * @param l
	 * @param gid
	 * @return
	 */
	public String doB2EAction(List<TrOrders> l,String option){
		if(l==null||l.size()==0) return "请选择要经办的订单";
		option=Ryt.sql(option);
		int operId=dao.getLoginUser().getOperId();
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		int time=DateUtil.getCurrentUTCSeconds();
		/*if(gid==0 || (gid<40000||gid>=50000)) return "选择的付款银行错误";*/
		int i=0;
		for(TrOrders oid : l){
			i=adminZHDao.update("update tr_orders set pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS + ",orgn_oper="+operId+",orgn_remark="+Ryt.addQuotes(option)+",orgn_date="+date+",orgn_time="+time+"  where oid='"+oid.getOid()+"'");
		}
		return i>0 ? "经办成功。" :"经办操作异常";
	}
	
	/**
	 * 进行代发经办审核成功处理
	 * @param l
	 * @param gid
	 * @return
	 * @throws Exception 
	 */
	public String doB2EAction_SH_S(List<TrOrders> l,int gid,String option) throws Exception{
		if(l==null||l.size()==0) return "请选择要审核的订单";
//		if(gid==0 || (gid<40000||gid>=50000)) return "选择的付款银行错误";
		option=Ryt.sql(option);
		Map<String, Integer> tempMap=new HashMap<String, Integer>();
		StringBuffer s = new StringBuffer();
		String oids = "";
		for(TrOrders oid : l){
			String ordid=oid.getOid();
			Integer ordDate=oid.getSysDate();
			Integer gate=oid.getGate();
			String mid=oid.getUid();
			if(null==tempMap.get(mid+gate)){
				gid=merZHDao.getGidByOid(mid,ordid,ordDate);
				tempMap.put(mid+gate, gid);
			}else{
				gid=tempMap.get(mid+gate);
			}
			fukuan(oid.getUid(), ordid,gid,s,option);
			oids += ordid + ",";
		}
		if(!"".equals(oids))
			new PayEnter().exec_pay(oids.substring(0, oids.length()-1).replaceAll(",", "','"));
		if(s.length()==0){
			merZHDao.saveOperLog("代付审核操作", "代付审核成功操作完成");
			return "代付审核成功";
		}
		merZHDao.saveOperLog("代付审核操作", s.toString());
		return   s.toString();
	}
	
	/**
	 * 进行代发经办审核失败处理
	 * @param l
	 * @param gid
	 * @return
	 */
	public String doB2EAction_SH_F(List<TrOrders> list,int gid,String option){
		if(list==null||list.size()==0) return "请选择要审核的订单";
//		if(gid==0 || (gid<40000||gid>=50000)) return "选择的付款银行错误";
		option=Ryt.sql(option);
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		int[] ret=null;
		StringBuffer msg=new StringBuffer();
		for(int i=0;i<list.size();i++){
			TrOrders oid=list.get(i);
			StringBuffer modifyOrderState=new StringBuffer();
			modifyOrderState.append("update tr_orders set state=" + Constant.DaiFuTransState.PAY_FAILURE +  ",pstate=" + Constant.DaifuPstate.AUDIT_FAILURE + ",");
			modifyOrderState.append(" audit_oper=").append(adminZHDao.getLoginUser().getOperId()).append(",");//审核操作员
			modifyOrderState.append(" audit_remark=").append(Ryt.addQuotes(option)).append(", ");
			modifyOrderState.append(" audit_date=").append(date).append(", ");
			modifyOrderState.append(" audit_time=").append(time).append(" ");
			modifyOrderState.append("where oid='"+oid.getOid()+"'");
			//更新tlog中的代付订单状态 为失败 保存网关
			StringBuffer modifyHlogState=new StringBuffer();
			modifyHlogState.append("update hlog set tstat=").append(Constant.PayState.FAILURE).append(",");
			modifyHlogState.append("error_msg=").append("'代发审核失败!'").append(",");
			modifyHlogState.append("error_code=").append("1 where oid=").append(Ryt.addQuotes(oid.getOid()));
			String modifyBalance = "update acc_infos set balance = balance + "+oid.getPayAmt()+", freeze_amt=freeze_amt-"+oid.getPayAmt()+" where aid = '" + oid.getAid()+"'";
			String[] sqls={modifyOrderState.toString(),modifyHlogState.toString(),modifyBalance};
			ret=adminZHDao.batchSqlTransaction(sqls);
			if(ret.length==0)msg.append("代发审核失败操作异常");
		}
		if(Ryt.empty(msg.toString())){
			merZHDao.saveOperLog("代发审核操作", "代发审核失败操作完成");
			return "代发审核失败操作完成。"; 
		}
		else{
			merZHDao.saveOperLog("代发审核操作", "代发审核失败操作异常");
			return"代发审核失败操作异常";
		}
	}
	
	
	/****
	 * 人工触发  银行转账 然后人工触发支付成功
	 * @param l
	 * @return
	 */
	public String doRGAction(List<TrOrders> l){
		String res="人工代发成功";
		 for(TrOrders o : l) {
				try {
						StringBuffer sql2 = new StringBuffer();
						sql2.append("update tr_orders set ");
						sql2.append("sys_date = ").append(DateUtil.today()).append(",");
						sql2.append("sys_time = ").append(DateUtil.getCurrentUTCSeconds()).append(",");
						sql2.append("state = ").append(Constant.DaiFuTransState.PAY_SUCCESS).append(",");
						sql2.append("pstate = ").append(Constant.DaifuPstate.AUDIT_SUCCESS).append(",");
						sql2.append("tseq = '").append("").append("',");
						sql2.append("remark = concat(remark, '-"+res+"') ");
						sql2.append(" where oid = '").append(o.getOid()).append("'");
						String sql3 = transactionDao.updateHlogStat(o.getUid(),
								o.getOid(), Constant.PayState.SUCCESS,Ryt.addQuotes(""),DateUtil.today(),String.valueOf(DateUtil.getCurrentUTCSeconds())).toString();
						AccSeqs param=new AccSeqs();
						param.setTbName(Constant.HLOG);
						param.setTbId(o.getOid());
						param.setUid(o.getUid());
						param.setAid(o.getUid());
						param.setTrAmt((o.getPayAmt()+o.getTransFee()));
						param.setTrFee(o.getTransFee());
						param.setAmt(o.getPayAmt());
						param.setRemark("人工代付");
						param.setRecPay((short)Constant.RecPay.REDUCE);
						List<String> accTseqSql=RecordLiveAccount.handleBalanceForTX(param);
						accTseqSql.add(sql2.toString());
						accTseqSql.add(sql3);
						String[] sql=accTseqSql.toArray(new String[accTseqSql.size()]);
						dao.batchSqlTransaction(sql);
//						String[] sqls3={sql4};
//						dao.batchSqlTransaction(sqls3);
//								dao.update(sql4);
								//交易失败不记流水
				} catch (Exception e) {
					e.printStackTrace();
					String sql4 = "update acc_infos set balance = balance + "+o.getPayAmt()+",freeze_amt=freeze_amt-"+o.getPayAmt()+" where aid = '" + o.getAid()+"'";
					dao.update(sql4);
					return "人工触发异常";
				}
		 };
		 /***
		  * 发送成功短信
		  */
		 for(TrOrders o : l) {
			if(!Ryt.empty(o.getSmsMobiles())){
				Minfo minfo=merZHDao.getMinfoById(o.getUid().toString());
				EmaySMS.sendSMS(new String[]{o.getSmsMobiles()}, minfo.getName()+"企业已将款项付到["+o.getToAccName()+"]"+"账户("+o.getToAccNo()+"),付款金额:["+Ryt.div100(o.getPayAmt())+"]元");
//				SMSUtil.send(o.getSmsMobiles(), minfo.getName()+"企业已将款项付到["+o.getToAccName()+"]"+"账户("+o.getToAccNo()+"),付款金额:["+Ryt.div100(o.getPayAmt())+"]元");
			}
		 }
		return "人工触发成功";
	}
	/**
	 * 进行代发经办处理
	 * @param aOid
	 * @param gid
	 * @param msg
	 */
	void RG_fukun(String aOid,int gid,StringBuffer msg){
		
	}
	
	/**
	 * 进行代发经办处理
	 * @param uid
	 * @param aOid
	 * @param gid
	 * @param msg
	 * @param option
	 */
	void fukuan(String uid, String aOid,int gid,StringBuffer msg,String option){
		option=Ryt.sql(option);
		TrOrders o = adminZHDao.queryForObject("select oid from tr_orders where oid = '" + aOid + "'", TrOrders.class);
		if(null==o) {if(msg.length()==0)msg.append("操作已完成,部分订单异常！");return;}
		B2EGate g = adminZHDao.getOneB2EGate(gid);
		if(null==g) {if(msg.length()==0)msg.append("操作已完成,部分订单异常！");return;}
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		
		StringBuffer sql=new StringBuffer();
		sql.append("update tr_orders set pstate="+Constant.DaifuPstate.AUDIT_SUCCESS+",");
		sql.append("       audit_oper=").append(adminZHDao.getLoginUser().getOperId()).append(",");//审核操作员
		sql.append("       audit_remark=").append(Ryt.addQuotes(option)).append(", ");
		sql.append("       audit_date=").append(date).append(", ");
		sql.append("       audit_time=").append(time).append(",");
		sql.append("	   is_pay=2 ");
		sql.append(" where oid=" + Ryt.addQuotes(aOid));
		sql.append("   and uid = " + Ryt.addQuotes(uid));
		sql.append("   and is_pay = 0"); //代付确认 追加条件 is_pay not in (1,2)  1: 已代付  2：代付发起
		StringBuffer sql3=new StringBuffer();		
		sql3.append("update hlog set bk_send = " + time);
		sql3.append(",tstat=1 ");
		sql3.append(" , p4=").append(date);
		sql3.append(" , p5=").append(time);
		sql3.append(" where mid = " + Ryt.addQuotes(uid));
		sql3.append("   and oid = " + Ryt.addQuotes(aOid));

		String[] sqls = { sql.toString(), sql3.toString() };
		int[] ret = adminZHDao.batchSqlTransaction(sqls);
		if(ret ==null || ret.length != sqls.length ){
			msg.append("操作异常");
		}
	}
	
	

	
	/**
	 * 查询B2E网关余额
	 * @param gid
	 * @return
	 */
	public String queryB2EGateBl(int gid){
		
		B2EGate g = adminZHDao.getOneB2EGate(gid);
		B2EProcess o = new B2EProcess(g,B2ETrCode.QUERY_ACC_BALANCE);
		B2ERet ret = o.submit();
		return ret.isSucc() ? (String)ret.getResult() : ret.getMsg();
	}
	
	public List<Object> getInitData(){
		List<Object> result = new ArrayList<Object>();
		result.add(BankNoUtil.getDaiFuGateMap());
		result.add(dao.getZH());
		return result;
	}
	
	public List<Object> getPubDFInitData(){
		List<Object> result = new ArrayList<Object>();
		result.add(BankNoUtil.getPubDaifuMap());
		result.add(dao.getZH());
		return result;
	}

	/**
	 * 公对私支付 传入批次号（yang.yaofeng）
	 * @param aid
	 * @param payPwd
	 * @param gate
	 * @param data
	 * @param trans_flow
	 * @return
	 * @throws Exception
	 */
	public String doActions(String aid,String payPwd,String gate,List<DaiFu> data,String trans_flow) throws Exception{
		//校验 支付密码
		String oldPass = dao.getPass(); 
		if (!oldPass.equals(payPwd)) {
			throw new Exception("支付密码错误");
		}
		Random r = new Random();
		String random_math=String.valueOf(r.nextInt(1000000));
		int len=random_math.length();
		while(len!=6){
			random_math=String.valueOf(r.nextInt(1000000));
			len=random_math.length();
		}
		String mid=merZHDao.getLoginUserMid();
//		String trans_flow=DateUtil.getNowDateTime()+random_math;
		Minfo m=merZHDao.getMinfoById(mid);
		int transAmt=0;
		int date=DateUtil.today();
		int time=DateUtil.now();
		AccInfos accInfos=dao.queryToAid(aid);
		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态!");
		if(!Ryt.isPay(accInfos.getBalance(), transAmt))throw new Exception("该比交易金额大于单笔限制金额");
		boolean flg=true;
		for(DaiFu o : data){
				Map<String, Object> p = dao.getFeeModeAid(aid, "daifa_fee_mode");
				FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(mid, o.getBkNo());
				int gid=feeCalcMode.getGid();
				String[] bkInfo=BankNoUtil.getBankNo(o.getOpenBkNo(),"", o.getBkNo(), o.getToProvId(), gid);
				String toBkNo=bkInfo[0];
				String toBkName=bkInfo[1];
				String feeMode=feeCalcMode.getCalcMode();
				int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeMode.toString(),o.getTrAmt()));
				long payAmt=Ryt.mul100toInt(o.getTrAmt());
				long trans_amt=payAmt+transFee;
				String oid = Ryt.genOidBySysTime();
				String sql1=dao.getAddSinglePaySql(trans_flow,p.get("aname").toString(),transFee,payAmt,oid, aid, o.getAccNo(), o.getAccName(),toBkName, toBkNo,o.getToProvId(),(int)o.getCardFlag(),trans_amt,33, o.getUse(),m.getBankAcct(),m.getBankAcctName(),Integer.parseInt(o.getBkNo()));
				String sql2=transactionDao.getInsertHlogSql("10191", String.valueOf(date), accInfos.getUid(), oid, String.valueOf(payAmt), "11", date,
						date, time, Integer.parseInt(o.getBkNo()),trans_flow, o.getAccNo(), o.getAccName(),toBkNo,transFee,gid,String.valueOf(trans_amt));
				String sql3=RecordLiveAccount.calUsableBalance(aid, aid, trans_amt, Constant.RecPay.REDUCE);
				int[] flag=transactionDao.batchSqlTransaction(new String[]{sql1,sql2,sql3});	
				if(flag==null||flag.length!=3){
					flg=false;
					break;
				}
			}
			if(flg){
				transactionDao.saveOperLog("对私批量付款", "批次号 "+trans_flow+" 受理成功");
				return "操作成功"+","+trans_flow;
				
			}
			else{
				transactionDao.saveOperLog("对私批量付款", "批次号 "+trans_flow+" 支付失败");
				return "error";
				}
	}
	
	private MerZHDao dao = new MerZHDao();

	public DaiFuBean batchAction(String accId, FileTransfer fileTransfer)throws Exception {
//		if(!merZHDao.merTransAccIsNomal(merZHDao.getLoginUserMid()+"")){
//			throw new Exception("该账户未开启交易账户！");
//		}
		DaiFuBean res = new DaiFuBean();
		String realPath =ParamUtil.getPropertie("upload_bak");
		String filename_Old = fileTransfer.getName();// 文件名
		if (filename_Old.indexOf(".") <= 0) {
			DaiFuBean daifu = new DaiFuBean();
			daifu.setErr("请上传正确的文件！");
			return daifu;
		}
		String extensions = filename_Old.substring(filename_Old
				.lastIndexOf("."));// 后缀名
		if (!extensions.equals(".xls")) {
			DaiFuBean daifu = new DaiFuBean();
			daifu.setErr("文件类型不正确！");
			return daifu;
		}
		AccInfos acc = dao.queryToAid(accId);
		if (acc.getState() != 1) {
			res.setErr("该账户非正常状态！");
			return res;
		}
			res.setAcc(acc);
			String name=adminZHDao.getLoginUserMid()+Ryt.crateBatchNumber();
			String fileName = name + extensions;
			File file = new File(realPath, fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			byte[] bytes = new byte[1024 * 8];
			FileOutputStream foutput = new FileOutputStream(file);

			InputStream fis = fileTransfer.getInputStream();

			int len = 0;
			while ((len = fis.read(bytes)) > 0) {
				foutput.write(bytes, 0, len);
			}
			foutput.flush();
			foutput.close();
			fis.close();
			
			Workbook book = Workbook.getWorkbook(new File(realPath, fileName));
			// 获得第一个工作表对象
			Sheet sheet = book.getSheet(0);
			// 得到列数
			int columns = sheet.getColumns();
			if (columns != 8) {
				res.setErr("列行数不对,请检查文件！");
				return res;
			}
			// 得到行数
			int rows = sheet.getRows();
			if (rows <=1) {
				res.setErr("请检查文件是否为空！");
				return res;

			}
			
		List<DaiFu> sList = new ArrayList<DaiFu>();
		List<DaiFu> fList = new ArrayList<DaiFu>();
		List<String> lines=analysisExcel(sheet,rows);
		res.setSum_lines(lines.size());
		String month = String.valueOf(DateUtil.today()).substring(0,6);
		long useredAmt = dao.queryMonthDaiFuSumAmt(acc.getAid(),month);	
	    long sumAmt = 0l;
	    String mid=merZHDao.getLoginUserMid();
	    List< String> l=new ArrayList<String>();
		for (String aLine : lines) {
			String[] data = aLine.split(",");
			if (data.length != 8){
				res.setErr("文件格式错误");
				return res;
			}else{
				sumAmt += initData(acc,sList,fList,data,l,mid);
			}
			l.add(data[1]);
		}
		double sum_amt=0;//交易金额
		double fee_amt=0;//手续费
		for(int i=0;i<sList.size();i++){
			sum_amt+=Double.parseDouble(sList.get(i).getTrAmt());
			fee_amt+=Double.parseDouble(sList.get(i).getTrFee());
		}
		DecimalFormat formater = new DecimalFormat("#0.##");
		res.setSum_amt(Double.parseDouble(formater.format(sum_amt)));
		res.setFee_amt(Double.parseDouble(formater.format(fee_amt)));
		if((sum_amt-fee_amt)>Double.parseDouble(Ryt.div100(String.valueOf(acc.getBalance())))){
			res.setErr("账户余额不足，请充值！");
			return res;
		}
		if((sumAmt+useredAmt) >= acc.getAccMonthAmt()){
			StringBuffer m = new StringBuffer("超过限额.");
			m.append("本月已支付:").append(Ryt.div100(useredAmt)).append("元，");
			m.append("本次总金额:").append(Ryt.div100(sumAmt)).append("元");
			m.append("月限额为:").append(Ryt.div100(acc.getAccMonthAmt())).append("元");
			res.setErr(m.toString());
			return res;
		}
//		sList_=sList;
		BatchB2B batch = new BatchB2B();
		batch.setBatchNumber(Ryt.crateBatchNumber());
		res.setBatch(batch);
		res.setSobjs(sList);
		res.setFobjs(fList);
		res.setSumAmt(sumAmt);
		res.setFlag(true);
		return res;

	}

	private int initData(AccInfos acc, List<DaiFu> sList, List<DaiFu> fList, String[] data,List l,String mid) throws Exception {
//		String m = acc.getDaifaFeeMode();
		//	收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途
		String accNo = data[1].trim();
		String bkNo=data[2].trim();
		FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(mid, bkNo);
		String feeMode=feeCalcMode.getCalcMode();
		Integer toProvId=Integer.parseInt(data[3].trim());
		short cardFlag=(short)Integer.parseInt(data[4].trim());
		String amt = data[5].trim();
		DaiFu df = new DaiFu();
		df.setAccName(data[0].trim());
		df.setAccNo(accNo);
		df.setBkNo(bkNo);
		df.setToProvId(toProvId);
		df.setCardFlag(cardFlag);
		df.setTrAmt(amt);
		df.setUse(data[6].trim());
		df.setOpenBkNo(data[7].trim());
		if(Ryt.empty(BankNoUtil.getDaiFuGateMap().get(bkNo))){
			df.setErr("银行号错误");
			fList.add(df);
			return 0;
		}
		if(!"0".equals(cardFlag+"")&&!"1".equals(cardFlag+"")){
			df.setErr("卡折标识错误");
			fList.add(df);
			return 0;
		}
		if(RYFMapUtil.getProvMap().get(toProvId)==null){
			df.setErr("省份错误");
			fList.add(df);
			return 0;
		}
		if(!isAccNo(accNo)){
			df.setErr("账户格式错误");
			fList.add(df);
			return 0;
		}
		
		if(!isAmt(amt)){
			df.setErr("金额格式错误");
			fList.add(df);
			return 0;
		}
		
//		if(l.contains(data[1])){
//			df.setErr("银行账户信息重复");
//			fList.add(df);
//			return 0;
//		}
		
		int dbAmt = (int) Ryt.mul100toInt(amt);
		//单笔限额为5W (分开始计位)
//		if(acc.getTransLimit()< dbAmt){
		//System.out.println("单笔限额："+dbAmt);
//		if(5000000<= dbAmt){
//			df.setErr("超过单笔限额");
//			fList.add(df);
//			return 0;
//		}
		//计算代发的手续费
		df.setTrFee(ChargeMode.reckon(feeMode, amt));
		df.setFlag(true);
		sList.add(df);
		return dbAmt;
		
	}
	
	private static boolean isAccNo(String str){
		return str.matches("[0-9]{10,30}");
	}
	
	private static boolean isAmt(String str){
		return str.matches("[0-9]{1,8}(.[0-9]{0,2})?");
	}
	/**
	 * 添加一条，单笔付款到个人银行账户到明细表
	 * @param aid
	 * @param toAccNo
	 * @param toAccName
	 * @param bkNo
	 * @param transAmt
	 * @param remark
	 * @return
	 * @throws Exception
	 */
	public String addSinglePay(String aid,String toAccNo,String toAccName,String toBkName,String toBkNo,Integer toProvId,Integer cardFlag, String payAmt,String priv) throws Exception{
		AccInfos accInfos=dao.queryToAid(aid);
//		Minfo m=merZHDao.getMinfoById(merZHDao.getLoginUserMid());
//		if(!merZHDao.merTransAccIsNomal(merZHDao.getLoginUserMid()+"")){
//			throw new Exception("该账户未开启交易账户！");
//		}
		long payAmt2=Ryt.mul100toInt(payAmt);
		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态"); 
//		if(5000000<= Long.parseLong(Ryt.mul100(payAmt)))throw new Exception("该笔交易金额大于单笔限制金额");
		else if(accInfos.getBalance()< payAmt2 )throw new Exception("该笔交易金额大于该账户可用余额");
		else { 
			Map<String, Object> p = dao.getFeeModeAid(aid, "daifa_fee_mode");
			String gate=BankNoUtil.getDaifuMiddletMap().get(toBkNo);
			FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(merZHDao.getLoginUserMid(), toBkNo);
			Integer gid=feeCalcMode.getGid();
			String feeMode=feeCalcMode.getCalcMode();
			int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeMode,Ryt.div100(payAmt2)));
			long transAmt=payAmt2+transFee;
			String oid = Ryt.genOidBySysTime();
			int row=dao.addSinglePay(p.get("aname").toString(),transFee,payAmt2,oid,aid,toAccNo,toAccName,toBkName,gate,toProvId,cardFlag,transAmt,0,priv,"","",Integer.parseInt(toBkNo.trim()));
			int date=DateUtil.today();
			int time=DateUtil.now();
			//代付 对私 单笔 录入到tlog
			int row2=transactionDao.insertHlog("10191", String.valueOf(date), accInfos.getUid(), oid, String.valueOf(payAmt2), "11", date,
					date, time, Integer.parseInt(toBkNo.trim()),null,toAccNo,toAccName,gate,transFee+"",gid,transAmt+"");
			if(row!=1 && row2 !=1)throw new Exception("操作异常");
			else
				return oid+","+transFee+","+payAmt2 +","+transAmt+","+accInfos.getBalance();
		}
	}
	
	/***
	 * 生成oid 系统订单号  
	 * @return
	 */
	public String createOid(){
		String oid = Ryt.genOidBySysTime();
		return oid;
	}
	
	/**
	 * 添加一条，单笔付款到个人银行账户到明细表
	 * @param aid
	 * @param toAccNo
	 * @param toAccName
	 * @param bkNo
	 * @param transAmt
	 * @param remark
	 * @return
	 * @throws Exception
	 */		
	public String updateSinglePay_N(String oid,String aid,String toAccNo,String toAccName,String toBkName,String toBkNo,Integer toProvId,short cardFlag, String payAmt,String priv) throws Exception{
		AccInfos accInfos=dao.queryToAid(aid);
//		if(!merZHDao.merTransAccIsNomal(merZHDao.getLoginUserMid()+"")){
//			throw new Exception("该账户未开启交易账户！");
//		}
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		long payAmt2=Ryt.mul100toInt(payAmt);
		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态");
		else if(accInfos.getBalance()< payAmt2)throw new Exception("该笔交易金额大于该账户可用余额");
		else { 
				String gate=BankNoUtil.getDaifuMiddletMap().get(toBkNo);
				FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(merZHDao.getLoginUserMid(), toBkNo);
				String feeMode=feeCalcMode.getCalcMode();
				int transFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeMode,Ryt.div100(payAmt2)));
				long transAmt= payAmt2+transFee;
//			    String oid = Ryt.genOidBySysTime();
				TrOrders tr_orders=new TrOrders();
				tr_orders.setToAccName(toAccName);
				tr_orders.setToAccNo(toAccNo);
				tr_orders.setToBkName(toBkName);
				tr_orders.setToBkNo(gate);
				tr_orders.setSysDate(date);
				tr_orders.setSysTime(time);
				tr_orders.setInitTime(DateUtil.getIntDateTime());
				tr_orders .setAid(aid);
				tr_orders.setOid(oid);
				tr_orders.setToProvId(toProvId);
				tr_orders.setCardFlag(cardFlag);
				tr_orders.setPayAmt(payAmt2);
				tr_orders.setTransFee(transFee);
				tr_orders.setTransAmt(transAmt);
				tr_orders.setPriv(priv);
				tr_orders.setSmsMobiles("");
				tr_orders.setToOid("");
			int row=dao.updateOrder(tr_orders);
			int row2=transactionDao.updateHlog(oid,aid,String.valueOf(payAmt2),Long.parseLong(""+transAmt),transFee,toAccNo,toAccName,gate);
			if(row!=1 && row2!=1){
				throw new Exception("操作异常");
			}
			else{
				transactionDao.saveOperLog("对私单笔付款", "订单号 "+oid+" 受理成功");
				return oid+","+transFee+","+payAmt2+","+transAmt+","+accInfos.getBalance();
			}
		}
	}
	
	
	/**
	 * 确定支付一笔，单笔付款到个人银行账户。
	 * @param data  array [aid,oid,toAccNo,toAccName,toBkName,toBkNo,toProvId,cardFlag,payAmt,transAmt,transFee,priv,gid,gate]
	 * @param pwd
	 * @return
	 */
	public String updateSinglePay(String[] data,String pwd)throws Exception{
		String mid=data[0];//商户号
		String aid=data[0];//账户号
		String oid=data[1];//订单号
		String toAccNo=data[2];//收款账号
		String toAccName=data[3];//收款账户名
		String toBkName=data[4];//收款银行名称
		String toBkNo=data[5];//收款银行行号
		String toProvId=data[6];//收款人所在省份
		String cardFlag=data[7];//卡折标志
		String payAmt=data[8];//实际支付金额
		String transAmt=data[9];//交易金额
		String transFee=data[10];//交易手续费
		String priv=data[11];//priv
		String gid=data[12];//支付渠道
		String gate=data[13];//支付网关号
		String oldPass = dao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		String[] bkInfo=BankNoUtil.getBankNo(toBkNo,toBkName, gate, Integer.parseInt(toProvId), Integer.parseInt(gid));
		toBkNo=bkInfo[0];
		toBkName=bkInfo[1];
		AccInfos accInfos=dao.queryToAid(aid);
		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态");
		if(!Ryt.isPay(accInfos.getBalance(),Ryt.mul100toInt(payAmt)))throw new Exception("该比交易金额大于账户余额");
		try{
			Map<String, Object> p = dao.getFeeModeAid(aid, "daifa_fee_mode");
			int row=dao.addSinglePay(p.get("aname").toString(),Integer.parseInt(Ryt.mul100(transFee)),Ryt.mul100toInt(transAmt),oid,aid,toAccNo,toAccName,toBkName,toBkNo,Integer.parseInt(toProvId),Integer.parseInt(cardFlag),Ryt.mul100toInt(payAmt),0,priv,"","",Integer.parseInt(gate.trim()));
			int date=DateUtil.today();
			int time=DateUtil.now();
			//代付 对私 单笔 录入到tlog
			int row2=transactionDao.insertHlog("10191", String.valueOf(date), accInfos.getUid(), oid, Ryt.mul100(transAmt), "11", date,
					date, time, Integer.parseInt(gate.trim()),null,toAccNo,toAccName,toBkNo,Ryt.mul100(transFee),Integer.parseInt(gid),Ryt.mul100(payAmt));
			if(row!=1 && row2 !=1)throw new Exception("操作异常");
			dao.updateSinglePay(oid,aid,Ryt.mul100(payAmt));
		}catch(Exception e){
			e.printStackTrace();
			dao.saveOperLog("单笔付款到个人银行账户", "支付失败");
			return "单笔付款到个人银行账户，操作失败";
		}
		dao.saveOperLog("单笔付款到个人银行账户", "订单号 "+oid+"支付成功");
		return "单笔付款到个人银行账户，操作成功";
	}
	/**
	 * 银行号，银行名称下载
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadBkNo() throws Exception {
			Map<String,String> map=BankNoUtil.getDaiFuGateMap();
			StringBuffer sheet = new StringBuffer("银行号,银行名\r\n");
			for(Map.Entry<String, String> entry:map.entrySet()){ 
				sheet.append(entry.getKey()).append(",").append(entry.getValue());
				sheet.append("\r\n");
			}
			String filename = "BkNo.txt";
			return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
      }
	
	public FileTransfer downloadPubDFBkNo() throws Exception {
		Map<String,String> map=BankNoUtil.getPubDaifuMap();
		StringBuffer sheet = new StringBuffer("银行号,银行名\r\n");
		for(Map.Entry<String, String> entry:map.entrySet()){ 
			sheet.append(entry.getKey()).append(",").append(entry.getValue());
			sheet.append("\r\n");
		}
		String filename = "BkNo.txt";
		return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
  }
      /**
       * 省份号，省份名称下载
       * @return
       * @throws Exception
       */
      public FileTransfer downloadProvId() throws Exception {
			Map<Integer,String> map=RYFMapUtil.getProvMap();
			StringBuffer sheet = new StringBuffer("省份号,省份名\r\n");
			for(Map.Entry<Integer, String> entry:map.entrySet()){ 
				sheet.append(entry.getKey()).append(",").append(entry.getValue());
				sheet.append("\r\n");
			}
			String filename = "Prov.txt";
			return new DownloadFile().downloadTXTFile(sheet.toString(), filename);
    }
      /**
       * 下载批量格式
       * @return
       * @throws Exception
       */
      public FileTransfer downloadBatch() throws Exception {
//			String content = "张三,6222600810065058595,313227000012,110,0,10000,发工资\r\n";
//			content +="李四,6222600810065058456,402641000014,120,0,10000,发工资\r\n";
//			content +="王五,6333600810065058342,301290000007,130,0,10000,发工资\r\n";
//			content +="赵六,6234605465065058432,308584000013,140,0,10000,发工资\r\n";
			List<String[]> list=new ArrayList<String[]>();
			String[] a5={"收款人户名","收款人账号","收款银行（银行对应表编号）","开户所在省份（省份对应表编号）","卡折标志（添代号：0代表卡,1代表存折）","交易金额","用途","收款方联行行号"};
			String[] a1={"张三","6222600810065058595","71001","110","0","10000","发工资","102100099996"};
			String[] a2={"李四","6222600810065058456","71002","120","0","10000","发工资","104163708432"};
			String[] a3={"王五","6333600810065058342","71003","130","0","10000","发工资","301290000007"};
			String[] a4={"赵六","6234605465065058432","71004","140","0","10000","发工资","301361000013"};
			list.add(a5);list.add(a1);list.add(a2);list.add(a3);list.add(a4);
			//String beginTitle = "收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途\r\n";
			String filename = "example.xls";
			return new DownloadFile().downloadXLS(list, filename);
  }
      public FileTransfer downloadXLSFileBase(){
  		List<String[]> list=new ArrayList<String[]>();
  		String filename="example.xls";//收款方开户账号名、收款方银行编号、收款方银行账号、订单金额、收款方开户银行省份编号，收款方联行行号
  		String head[]={"收款方开户账号名","收款方银行编号","收款方银行账号","订单金额","收款方开户银行省份编号","收款方联行行号"};
  		String a[]={"张三","72001","6222600810065058594","10000","430","104163708432"};
  		String b[]={"张四","72002","6222600810065058595","20000","430","102100099996"};
  		list.add(head);
  		list.add(a);
  		list.add(b);
  		FileTransfer ft=null;
  		try {
  			ft=new DownloadFile().downloadXLS(list, filename);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return ft;
  	}

  	/**
  	 * 批量付款到企业
  	 * @throws Exception 
  	 */
  	public DaiFuBean batchForExcel(String describe, FileTransfer fileTransfer) throws Exception {
//  		if(!adminZHDao.adminTransAccIsNomal(adminZHDao.getLoginUserMid()+"")){
//			DaiFuBean daifu = new DaiFuBean();
//  			daifu.setErr("该账户未开启交易账户！");
//			return daifu;
//		}
  		AccInfos accInfos=dao.queryToAid(adminZHDao.getLoginUserMid()+"");
  		if(accInfos.getState()!=1){
  			DaiFuBean daifu = new DaiFuBean();
			daifu.setErr("该账户非正常状态!");
		return daifu;
  		}
  		try {
  			String realPath =ParamUtil.getPropertie("upload_bak");
  			String filename_Old = fileTransfer.getName();// 文件名
  			if (filename_Old.indexOf(".") <= 0) {
  				DaiFuBean daifu = new DaiFuBean();
  				daifu.setErr("请上传正确的文件！");
  				return daifu;
  			}
  			String extensions = filename_Old.substring(filename_Old
  					.lastIndexOf("."));// 后缀名
  			if (!extensions.equals(".xls")) {
  				DaiFuBean daifu = new DaiFuBean();
  				daifu.setErr("文件类型不正确！");
  				return daifu;
  			}
  			String name=adminZHDao.getLoginUserMid()+Ryt.crateBatchNumber();
  			String fileName = name + extensions;
  			File file = new File(realPath, fileName);
  			if (!file.getParentFile().exists()) {
  				file.getParentFile().mkdirs();
  			}
  			byte[] bytes = new byte[1024 * 8];
  			FileOutputStream foutput = new FileOutputStream(file);

  			InputStream fis = fileTransfer.getInputStream();
  			int len = 0;
  			while ((len = fis.read(bytes)) > 0) {
  				foutput.write(bytes, 0, len);
  			}
  			foutput.flush();
  			foutput.close();
  			fis.close();
  			// 文件上传成功 开始解析
  			return analysisExcel(realPath + "/" + fileName, describe);
  		} catch (Exception e) {
  			DaiFuBean daifu = new DaiFuBean();
  			daifu.setErr("上传失败!");
  			return daifu;
  		}
  	}

  	/**
  	 * 解析Excel文件
  	 * 
  	 * @param filepath
  	 *            文件路径
  	 */
  	public DaiFuBean analysisExcel(String filepath, String describe)throws Exception{
  		DaiFuBean daiFuBean = new DaiFuBean();
  		List<DaiFu> flist = new ArrayList<DaiFu>();
  		List<DaiFu> slist = new ArrayList<DaiFu>();
//  		Map<String,String> map=new HashMap<String,String>();
  		DaiFu daifu = null;
  		double orderAmt = 0;// 订单金额
  		double feeAmt = 0;// 手续费
  		double allrAmt = 0;// 付款总金额
  		String batchNumber = Ryt.crateBatchNumber();// 批次号
  		try {
  			Workbook book = Workbook.getWorkbook(new File(filepath));
  			// 获得第一个工作表对象
  			Sheet sheet = book.getSheet(0);
  			// 得到行数
  			int rows = sheet.getRows();
  			if (rows <= 1) {
  				daiFuBean.setErr("请检查文件是否为空！");
  				return daiFuBean;
  			}
  			// 得到列数
  			int columns = sheet.getColumns();
  			if (columns != 6) {
  				daiFuBean.setErr("列数不对,请检查文件！");
  				return daiFuBean;
  			}
  			// 手续费计算公式
				LoginUser loginUser = merZHDao.getLoginUser();
				String uid = loginUser.getMid();// 商户ID
//				String aid = queryZHAID(uid);// 操作员
				AccInfos accInfos=merZHDao.queryFK(uid);//付款方账户信息
				if(accInfos.getState()!=1){
					throw new Exception("该账户非正常状态！");
				}
//				if(accInfos==null){
//  					throw new Exception("该账户没有交易账户！");
//  				}
  			//计算手续费
//			String fee=accInfos.getDaifuFeeMode();
  			boolean flag = false;
  			int counts=0;//有效数据总条数
  			for (int i = 1; i < rows; i++) {
  				// 实例化一个Bean
  				daifu = new DaiFu();
  				// 以下进行一系列取值以及判断
  				String province = sheet.getCell(4, i).getContents().trim();// 收款方开户银行省份
  				String bankName = sheet.getCell(1, i).getContents().trim();// 收款方银行名
  				String orderMoney = sheet.getCell(3, i).getContents().trim();// 订单金额
  				String bankNo = sheet.getCell(2, i).getContents().trim();// 收款方银行账号
  				String accName = sheet.getCell(0, i).getContents().trim();// 得到第一列第一行的数据
  				String openbankno = sheet.getCell(5, i).getContents().trim();// 收款方联行行号
  				if(Ryt.empty(province)&&Ryt.empty(bankName)&&Ryt.empty(orderMoney)&&Ryt.empty(bankNo)&&
  						Ryt.empty(accName)&&Ryt.empty(openbankno))															
  						continue;
  				counts++;
  				String feeMode="";
  				if(isInBankNo(bankName)){
  					FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(uid, bankName);
  					feeMode=feeCalcMode.getCalcMode();
  					}
  				else{
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("收款银行填写错误");
  				}
  					
  				// 收款方开户账号名
  				if (!Ryt.isNumber(province)
  						|| !provInMap(Integer.parseInt(province))) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("省份ID填写错误");
  				}
  			// 收款方联行行号
  				if (!Ryt.isNumber(openbankno)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("联行行号填写错误");
  				}
  				if (!isAmt(orderMoney)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("订单金额错误");
  					
  				}else{
  					feeAmt = feeAmt
  	  						+ Double.parseDouble(ChargeMode.reckon(feeMode, orderMoney));// 手续费
  				}
  				if (!isAccNo(bankNo)) {
  					daiFuBean.setFlag(flag);
  					daifu.setErrMsg("银行帐号格式错误");
  				}else{
//  					if(map.get(bankNo)!=null){
//  						daiFuBean.setFlag(flag);
//  	  					daifu.setErrMsg("银行帐号有重复");
//  	  					//代付对私批量 重复显示全部重复订单
//  	  	  	  			for (DaiFu daiFu2 : slist) {
//  	  	  					if(daiFu2.getAccNo().equals(bankNo.trim())){
//  	  	  					    orderAmt = orderAmt - Double.parseDouble(daiFu2.getTrAmt());// 金额累加
//  	  	  						DaiFu Fdaifu=new DaiFu();
//								Fdaifu.setErrMsg("银行帐号有重复");
//								Fdaifu.setErrProvId(String.valueOf(daiFu2.getToProvId()));
//								Fdaifu.setAccName(daiFu2.getAccName());// 账户名
//								Fdaifu.setAccNo(daiFu2.getAccNo());// 账户银行卡号
//								Fdaifu.setTrAmt(daiFu2.getTrAmt());// 订单金额
//								Fdaifu.setBkNo(daiFu2.getBkNo());// 银行名
////  	  	  	  					slist.remove(daiFu2);
//  	  	  	  					flist.add(Fdaifu);
//  	  	  					}
//  	  	  				}
//  					}else{
//  						map.put(bankNo, bankNo);
//  					}
  				}
  				
  				if (!bankName.equals("")) {
  					if (!Ryt.isNumber(bankName) || !isInBankNo(bankName)) {
  						daiFuBean.setFlag(flag);
  						daifu.setErrMsg("收款银行填写错误");
  					}
  				}
  				if (accName.equals("")) {
  					daifu.setErrMsg("账户名错误");
  				}
  				
  				// 判断该Bean为失败还是成功
  				if (daifu.getErrMsg() == null) {
  					orderAmt = orderAmt + Double.parseDouble(orderMoney);// 金额累加
  					daifu.setAccName(Ryt.sql(accName));// 账户名
  					daifu.setAccNo(Ryt.sql(bankNo));// 账户银行卡号
  					daifu.setTrAmt(orderMoney);// 订单金额
  					daifu.setBkNo(Ryt.sql(bankName));// 银行名
  					daifu.setOpenBkNo(openbankno);
  					daifu.setToProvId(Integer.parseInt(province));// 省份ID
  					daifu.setUse(Ryt.sql(describe));//设置备注
  					slist.add(daifu);
  				} else {
  					daifu.setErrProvId(province);
  					daifu.setAccName(Ryt.sql(accName));// 账户名
  					daifu.setAccNo(Ryt.sql(bankNo));// 账户银行卡号
  					daifu.setTrAmt(orderMoney);// 订单金额
  					daifu.setBkNo(Ryt.sql(bankName));// 银行名
  					daifu.setOpenBkNo(openbankno);
  					flist.add(daifu);
  				}
  			}
  			daiFuBean.setSum_lines(counts);
  			// 如果有失败数据则不做操作
  			if (flist.size() != 0) {
  				daiFuBean.setFobjs(flist);// 设置flist中的DaifBean
  			} else if (slist.size() != 0) {
  				allrAmt = orderAmt + feeAmt;
  				int count = slist.size();// 交易笔数
  				daiFuBean.setBkName("ok");
  				BatchB2B batch = new BatchB2B();
  				batch.setAllrAmt(allrAmt);
  				batch.setBatchNumber(batchNumber);
  				batch.setCount(count);
  				batch.setFeeAmt(feeAmt);
  				batch.setOrderAmt(orderAmt);
  				batch.setOrderDescribe(describe);
  				daiFuBean.setBatch(batch);
  				daiFuBean.setSobjs(slist);
  			}
  			book.close();
  		} catch (Exception e) {
  			LogUtil.printErrorLog("DaiFuService", "analysisExcel", "批量处理异常："+e.getMessage());
  			daiFuBean.setErr("服务器异常，请稍后重试！");
  			return daiFuBean;
  		}
  		return daiFuBean;

  	}
  	

  	/**
  	 * 判断填写省份是否存在
  	 * 
  	 * @param prov
  	 *            省份ID
  	 * @return
  	 */
  	public boolean provInMap(int prov) {
  		// 得到省份Map
  		TreeMap<Integer, String> map = RYFMapUtil.getProvMap();
  		if (map.get(prov) == null) {
  			return false;
  		}
  		return true;
  	}

  	public boolean isInBankNo(String key) {
  		Map<String, String> map = BankNoUtil.getPubDFMiddletMap();
  		if (Ryt.empty(map.get(key)))
  			return false;
  		return true;
  	}

  	/**
  	 * 检查用户余额支付条件是否通过
  	 * 
  	 * @param transAmt
  	 *            应付金额
  	 * @return
  	 */
  	public String checkBalance(double transAmt, String payPwd) {
  		OperAuthDao operAuthDao = new OperAuthDao();
  		LoginUser loginUser = operAuthDao.getLoginUser();
  		String mid = loginUser.getMid();
  		List<AccInfos> accinf = merZHDao.queryZHYE(mid);
  		String paypass = merZHDao.getPass();// 得到用户支付密码
  		if (accinf.size() <= 0)
  			return "商户信息异常或商户不存在,无法进行支付！";
  		if (!paypass.equals(payPwd)) {
  			return "支付密码错误，请重试！";
  		}
  		if (accinf.get(0).getState() != 1)
  			return "账户状态非正常，无法使用支付功能！";
  		if (Double.parseDouble(Ryt.div100(accinf.get(0).getBalance())) < transAmt)
  			return "账户余额不足，请充值后重试！";
  		else
  			return "ok";
  	}

  	/**
  	 * 公对公余额支付
  	 * @param trfee 手续费
  	 * @param transAmt 应付金额
  	 * @param batchNo 批次号
  	 * @param payPwd 支付密码
  	 * @param sucOrder 成功订单
  	 * @return
  	 * @throws Exception
  	 */
  	public String payForBalances(double trfee,double transAmt, String batchNo,
  			String payPwd, List<DaiFu> sucOrder) throws Exception {
  		String uid = merZHDao.getLoginUserMid();// 商户ID
  		List<AccInfos> accinfo=queryZHYE(uid);//结算帐户
  		if(accinfo.size()>0 && accinfo.get(0).getState()!=1)
  			return "该账户非正常状态!";
	  	String aid = accinfo.get(0).getAid();// 操作员
  		// 余额支付检查通过
  		if (!checkBalance(transAmt, payPwd).equals("ok")) {
  			return "支付异常，请勿非法操作！";
  		}
  		//批量sql语句
  		String []sqls=creatBatchSql(sucOrder,batchNo,0);
  		if (merZHDao.batchStateNomal(batchNo)<=0) {
  			int[] i = merZHDao.batchPayForBalance(batchNo, transAmt, uid, aid,sqls);
  			if (i.length == sqls.length ) {
  				merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 余额支付 受理成功");
  				return "ok";
  			}
  		}else{
  			merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 支付失败");
  			return "支付失败,请检查是否重复提交订单！";
  		}
  		merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 支付失败");
  		return "支付失败！";
  	}
  	/**
  	 * 通过商户查找出支付的账户名
  	 * @param uid 商户ID
  	 * @return
  	 */
  	public String queryZHAID(String uid){
  	  return merZHDao.queryZHAID(uid);
  	}
  	/**
  	 * 生成批量付款订单录入Sql语句
  	 * @param sucOrder 订单Bean
  	 * @param batchNo 批次号
  	 * @param offLineFlag 线下支付标识符
  	 * @return
  	 */
  	public String [] creatBatchSql(List<DaiFu> sucOrder,String batchNo,int offLineFlag)throws Exception{
  		LoginUser loginUser = adminZHDao.getLoginUser();
  		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
  		String uid = loginUser.getMid();// 商户ID
  		List<AccInfos> accinfo=queryZHYE(uid);//结算帐户
  		AccInfos accInfos=accinfo.get(0);//付款方账户信息(改结算帐户)
  		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态!");
  		//计算手续费公式
//  	String feeMode=accInfos.getDaifuFeeMode();
  		// 批量sql
  		String[] sqls = new String[sucOrder.size()+1];
  		String[] sqls2=new String[sucOrder.size()];
  		// 余额相应减少，将订单批量写入数据
  		for (int i = 0; i < sucOrder.size(); i++) {
//  			String transFee=ChargeMode.reckon(feeMode, sucOrder.get(i).getTrAmt());//手续费
  			FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(uid, sucOrder.get(i).getBkNo());
  			int gid=feeCalcMode.getGid();  			
			String feeMode=feeCalcMode.getCalcMode();
  			String oid=System.currentTimeMillis() + Ryt.createRandomStr(6);
  			//联行号特殊处理
  			String[] bkInfo=BankNoUtil.getBankNo(sucOrder.get(i).getOpenBkNo(), "", sucOrder.get(i).getBkNo(), sucOrder.get(i).getToProvId(), gid);
//  			sucOrder.get(i).setAccName(bkInfo[1]);
//  			sucOrder.get(i).setAccNo(bkInfo[0]);
  			String toBkName=bkInfo[1];
  			String toBkNo=bkInfo[0];
			//计算手续费
			Integer saveTransFee=(int) Ryt.mul100toInt(ChargeMode.reckon(feeMode, sucOrder.get(i).getTrAmt()));//手续费
			long saveTransAmt=Ryt.mul100toInt(sucOrder.get(i).getTrAmt())+saveTransFee;//实际交易金额（乘100后的）
  			long savePayAmt=Ryt.mul100toInt(sucOrder.get(i).getTrAmt());//付款金额
  			StringBuffer sql = new StringBuffer(
  					"insert into tr_orders(oid,uid,aid,aname,init_time,ptype,trans_amt,trans_fee,pay_amt," +
  					"acc_name,acc_no,to_acc_name,to_acc_no,to_bk_name,to_bk_no,to_prov_id,state,remark,offline_flag,trans_flow,sys_date,sys_time,priv,gate) values('");
  			sql.append(oid+"',");// 订单号（System.currentTimeMillis()+6位随机）
  			sql.append(uid+",'");
  			sql.append(uid+"','");
  			sql.append(accInfos.getAname()+"','");//付款方账户名
  			sql.append(DateUtil.getIntDateTime()+"',");
  			sql.append(Constant.DaiFuTransType.PAY_TO_ENTERPRISE+",");//支付类型
  			sql.append(savePayAmt+",");//支付总金额
  			sql.append(saveTransFee+",");
  			sql.append(saveTransAmt+",'");
  			sql.append(accInfos.getAname()+"','");//付款银行卡账户名
  			sql.append(accInfos.getAid()+"','");//付款银行号
  			sql.append(sucOrder.get(i).getAccName()+"','");//收款方账户名
  			sql.append(sucOrder.get(i).getAccNo()+"',");//收款银行帐号
  			sql.append(Ryt.addQuotes(toBkName)).append(",");//收款银行名称
  			sql.append(toBkNo).append(","); //收款银行号
  			sql.append(sucOrder.get(i).getToProvId()+",");
  			sql.append((offLineFlag==1?Constant.DaiFuTransState.OFFLINE_RECHARGE_PROCESSING : Constant.DaiFuTransState.PAY_PROCESSING)+",'");
  			sql.append(sucOrder.get(i).getUse()+"',");//备注
  			sql.append(offLineFlag+",");//是否为线下
  			sql.append("'"+batchNo+"',");//批次号
  			sql.append(""+date+",");//系统日期
  			sql.append(""+time+",");//系统时间
  			sql.append("'"+sucOrder.get(i).getUse()+"',"+sucOrder.get(i).getBkNo()+")");
  			StringBuffer sql2=new StringBuffer();
  			sql2.append(" insert into hlog (version,ip,mdate,mid,oid,amount,type,sys_date,init_sys_date,sys_time,gate,tstat,gid,pay_amt,is_liq,tseq,p8,p1,p2,p3,data_source,fee_amt)");
  			sql2.append(" values(10,").append("10191,").append("").append(date).append(",");
  			sql2.append(uid).append(",").append(Ryt.addQuotes(oid)).append(",").append(""+savePayAmt).append(",").append("12").append(",");//12 交易类型：对公代付
  			sql2.append(date).append(",").append(date).append(",").append(String.valueOf(time)).append(",");
  			sql2.append(sucOrder.get(i).getBkNo()).append(",").append("0").append(",").append(gid).append(",").append(""+saveTransAmt).append(",").append(" 1").append(",").append(Ryt.addQuotes(Ryt.genOidBySysTime())).append(",").append("'"+batchNo+"'");
  			sql2.append(","+sucOrder.get(i).getAccNo()).append(",'"+sucOrder.get(i).getAccName()).append("','"+toBkNo+"'");
  			sql2.append(",5,"+saveTransFee+")");
//  			LogUtil.printInfoLog("DaifuService", "creatBatchSql", "sql:"+sql.toString());
  			sqls[i]=sql.toString();
  			sqls2[i]=sql2.toString();
  				}
		String[] sqls3=new String[sqls.length+sqls2.length];
		System.arraycopy(sqls2, 0, sqls3, 0, sqls2.length);
		System.arraycopy(sqls, 0, sqls3, sqls2.length, sqls.length);
  		return sqls3;
  	}
  	/**
  	 * 线下支付
  	 * @param batchNo 批次号
  	 * @return 
  	 * @throws Exception 
  	 */
  	public String payForOffline(List<DaiFu> sucOrder,String batchNo,BatchB2B batchB2B) throws Exception{
  		//是否正常订单
  		if(merZHDao.batchStateNomal(batchNo)<=0){
  			//批量Sql
  			String []sqls=creatBatchSql(sucOrder,batchNo,1);
  	  		String uid = adminZHDao.getLoginUser().getMid();// 商户ID
  	  	    List<AccInfos> accinfo=queryZHYE(uid);
  	  	    if(accinfo.size()>0 && accinfo.get(0).getState()!=1)
  	  	    	return "该账户为非正常状态!";
  			String oid=System.currentTimeMillis() + Ryt.createRandomStr(6);//订单号
  			String priv=sucOrder.get(0).getUse();
  			/*batchB2B.setAllrAmt(batchB2B.getOrderAmt());*/
  			int []i=merZHDao.batchPayForOffline(batchNo,sqls,batchB2B,uid,oid,accinfo.get(0),priv);
  			if (i != null &&i.length!=0 ) {
  				merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 线下支付 受理成功");
  				return "ok";
  			}
  		}else{
  			merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 支付失败");
  			return "支付失败,请检查是否重复提交订单！";
  		}
  		merZHDao.saveOperLog("对公批量付款", "批次号 "+batchNo+" 支付失败");
  		return "操作失败，请重试！";
  	}
  	/**
	 * 查询账户余额
	 * 
	 * @param mid
	 *            商户号（用户ID）
	 * @return
	 */
	public List<AccInfos> queryZHYE(String mid) {
		return merZHDao.queryZHYE(mid);
	}
	
	
	public List<String> analysisExcel(Sheet sheet,int rows)throws Exception{
		List<String> lines=new ArrayList<String>();
			for(int i=1;i<rows ;i++){
//				收款人户名,收款人账号，收款银行，开户所在省份，卡折标志，交易金额，用途\r\n";
				String to_accName=sheet.getCell(0, i).getContents().trim();
				String to_accNo=sheet.getCell(1, i).getContents().trim();
				String to_bk_no=sheet.getCell(2, i).getContents().trim();
				String to_prov_id=sheet.getCell(3, i).getContents().trim();
				String to_cardFlag=sheet.getCell(4, i).getContents().trim();
				String to_payAmt=sheet.getCell(5, i).getContents().trim();
				String to_priv=sheet.getCell(6, i).getContents().trim();
				String to_openbk_no=sheet.getCell(7, i).getContents().trim();
				if(Ryt.empty(to_accName)&&Ryt.empty(to_accNo)&&Ryt.empty(to_bk_no)&&Ryt.empty(to_prov_id)
						&&Ryt.empty(to_cardFlag)&&Ryt.empty(to_payAmt)&&Ryt.empty(to_priv)&&Ryt.empty(to_openbk_no))
					continue;
				lines.add(to_accName+","+to_accNo+","+to_bk_no+","+to_prov_id+","+to_cardFlag+","+to_payAmt+","+to_priv+","+to_openbk_no);
			}
			return lines;
	}
	
	/****
	 * 管理员查看所有的可用账户
	 * @return
	 */
	public Map<String, String> getAllHZ(){
		return merZHDao.getZH_All();
	}
	/**
	 * 查询交易账户余额
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public List<AccInfos> queryJYZHYE(String mid){
		return merZHDao.queryJYZHYE(mid);
		
	}
	/***
	 * 代发经办失败
	 * @param l
	 * @param gid
	 * @return
	 */
	public String doB2EActionFail(List<TrOrders> list,String option){
		option=Ryt.sql(option);
		int operId=dao.getLoginUser().getOperId();
		String date = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		int time=DateUtil.getCurrentUTCSeconds();
		if(list==null||list.size()==0) return "请选择要经办的订单";
		StringBuffer msg=new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			TrOrders ord=list.get(i);
			String s1="update tr_orders set state="+Constant.DaiFuTransState.PAY_FAILURE+",pstate="+Constant.DaifuPstate.HANDLING_FAILURE+" ,orgn_oper="+operId+",orgn_remark="+Ryt.addQuotes(option)+",orgn_date="+date+",orgn_time="+time+"  where oid='"+ord.getOid()+"'";
			StringBuffer sql=new StringBuffer();
			sql.append(" update hlog set tstat=").append(Constant.PayState.FAILURE).append(",error_msg=").append(Ryt.addQuotes("付款失败,代发经办失败操作处理")).append(" ,bk_flag=1 ").append(" where ");
			sql.append(" mid=").append(Ryt.addQuotes(ord.getUid())).append(" and ").append(" oid=").append(Ryt.addQuotes(ord.getOid()));
			String sql4 = "update acc_infos set balance = balance + "+ord.getPayAmt()+",freeze_amt=freeze_amt-"+ord.getPayAmt()+" where aid = '" + ord.getAid()+"'";
			String[] sqls={s1,sql.toString(),sql4};
			int[] ret=adminZHDao.batchSqlTransaction(sqls);
			if (ret.length==0) msg.append("经办失败操作异常");
		}
		if(Ryt.empty(msg.toString())){
			adminZHDao.saveOperLog("经办失败操作", "经办失败操作完成");
			return "经办失败操作成功。";
		}
		else{
			return "经办操作异常";
		}
	}
	//============================================yyf
		/****
		 * 查询线下充值经办数据 
		 * @param uid id
		 * @param num 条数
		 * @return
		 */
		public List<TrOrders> queryXXCZJingBan(String uid ,int num,Integer mstate ){
			return adminZHDao.queryXXCZJingBan(uid,num,mstate);
		}
		/**
		 * 线下充值经办处理  订单状态改成经办处理中
		 * @param l
		 * @param gid
		 * @return
		 */
		public String doB2EAction_XXCZ(List<TrOrders> l,String option){
			option=Ryt.sql(option);
			Integer operId=dao.getLoginUser().getOperId();
			int date = DateUtil.today();
		    int time=DateUtil.getCurrentUTCSeconds();
			if(l==null||l.size()==0) return "请选择要经办的订单";
			int i=0;
			for(TrOrders oid : l){
				String Sql="update tr_orders set pstate=" + Constant.DaifuPstate.HANDLING_SUCCESS + " ,orgn_oper="+operId+",orgn_remark="+Ryt.addQuotes(option)+"," +
			"orgn_date="+date+",orgn_time="+time+"  where oid='"+oid.getOid()+"'";
				i=adminZHDao.update(Sql);
			}
			if(i>0){
				adminZHDao.saveOperLog("充值经办操作", "经办成功操作完成");
				return "经办成功";
			}
			adminZHDao.saveOperLog("充值经办操作", "经办成功操作异常");
			return "经办操作异常!";
		}
		/***
		 * 线下充值经办不通过
		 * @param l
		 * @param gid
		 * @return
		 */
		public String doB2EActionFail_XXCZ(List<TrOrders> list,String option){
			if(list==null||list.size()==0) return "请选择要经办的订单";
			option=Ryt.sql(option);
			Integer operId=dao.getLoginUser().getOperId();
			int date = DateUtil.today();
		    int time=DateUtil.getCurrentUTCSeconds();
			String []sqls=new String[list.size()*2];
			for (int j = 0; j < list.size(); j++) {
				TrOrders oid=list.get(j);
				String trOrdersSql="update tr_orders set state=" + Constant.DaiFuTransState.RECHARGE_FAILURE + ",pstate=" + Constant.DaifuPstate.HANDLING_FAILURE + 
				",orgn_oper="+operId+
				",orgn_remark="+Ryt.addQuotes(option)+",orgn_date="+date+",orgn_time="+time+" where oid='"+oid.getOid()+"'";
				StringBuffer hlogSql=new StringBuffer("update hlog set tstat=");
				hlogSql.append(Constant.PayState.FAILURE);
				hlogSql.append(" where oid=");
				hlogSql.append(Ryt.addQuotes(oid.getOid()));
				sqls[j*2]=trOrdersSql;
				sqls[j*2+1]=hlogSql.toString();
			}
			int []flag=adminZHDao.batchSqlTransaction(sqls);
			if(null!=flag&&flag.length==list.size()*2){
				adminZHDao.saveOperLog("充值经办操作", "经办失败操作完成");
				return "经办失败操作成功!";
			}
			else{
				adminZHDao.saveOperLog("充值经办操作", "经办失败操作失败");
				return "经办操作异常!";
			}
		}
		
		/**
		 * 根据商户号查找结算帐户
		 * @param uid 商户号
		 * @return
		 */
	   public Map<String, String> getJSZHByUid(String uid) {
			return merZHDao.getZHUid(uid);
		}
	    
	   	/***
	   	 * 查询代付请求银行失败数据  mms 代付
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
		public CurrentPage<OrderInfo> queryDataForReqF(Integer pagNo,Integer num,String uid ,String trans_flow ,Integer ptype,String tseq,Integer mstate,Integer bdate,Integer edate){
			return adminZHDao.queryDataForReqFail(pagNo,num,uid,trans_flow,ptype,tseq,mstate,bdate,edate);
		}
		
		/****
		 * 手工代付申请 代付提交
		 * 发起代付查询
		 * @param lists
		 * @return
		 */
		public String reqPayDf(List<OrderInfo> lists){
			//修改订单状态  发起代付
			String oids="";
			List<String> sqls=new ArrayList<String>();
			for (OrderInfo orderInfo : lists) {
				//修改订单状态为处理中  并修改P9 为1
				String[] info=adminZHDao.queryOrderByOid(orderInfo.getMid(), orderInfo.getOid());
				String table=info[0];
				String sql=adminZHDao.modifyOrderState_SGDF(orderInfo.getTseq(),orderInfo.getMid(),table);
				sqls.add(sql);
				oids+=orderInfo.getOid()+",";
			}
			String[] batchSql=sqls.toArray(new String[sqls.size()]);
			int[] ret = adminZHDao.batchSqlTransaction(batchSql);
			if (ret.length==0)return "手工提交异常！";
			//调用接口
			if(!"".equals(oids))
				new PayEnter().exec_pay_reqFail(oids.substring(0, oids.length()-1).replaceAll(",", "','"));
			return "手工提交成功";
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
			List<String> sqlList=new ArrayList<String>();
			for (OrderInfo orderInfo : lists) {
				String end=GenB2ETrnid.getRandOid();
				String head=DateUtil.getNowDateTime();
				String mid=orderInfo.getMid();String tseq=orderInfo.getTseq();String oid=orderInfo.getOid();
				String[] info=adminZHDao.queryOrderByOid(mid, oid);
				String table=info[0];
				StringBuffer cancel_id=new StringBuffer(head).append(end);
				String sql=adminZHDao.savecancelLog(cancel_id.toString(),tseq, "1", "1", "1", adminZHDao.getLoginUser().getOperId(), option,null,null,null,"","");
				StringBuffer modifyTstat=new StringBuffer("update ").append(table).append(" set tstat=").append(Constant.PayState.FAILURE);
				modifyTstat.append(",").append(" p9=1").append(",").append("p12=").append(DateUtil.today()).append(" where mid=").append(Ryt.addQuotes(mid)).append(" and tseq=").append(Ryt.addQuotes(tseq));
				String sql3 =  RecordLiveAccount.calUsableBalance(mid, mid, orderInfo.getPayAmt(), Constant.RecPay.INCREASE);
				sqlList.add(sql);sqlList.add(modifyTstat.toString());sqlList.add(sql3);
			}
			String[] sqls=sqlList.toArray(new String[sqlList.size()]);
			int[] ret=adminZHDao.batchSqlTransaction(sqls);
			if(ret.length<=0)return "撤销异常！";
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
			List<OrderInfo> TrList=adminZHDao.downSGDFData(uid, trans_flow, ptype, tseq, mstate);
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
			return adminZHDao.queryDataForSGSYNC(pagNo,num,uid,trans_flow,ptype,tseq,mstate,state,gate,bdate,edate);
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
			List<OrderInfo> TrList=adminZHDao.downSGSYNCDFData(uid, trans_flow, ptype, tseq, mstate,state,gate,bdate,edate);
			ArrayList<String[]> list = new ArrayList<String[]>();
			Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			long totleTransAmt = 0;
			long totlePayAmt = 0;
			long totleTransFee=0;
			list.add("代付流水号,交易类型,批次号,商户号,账户号,账户名称,支付渠道,交易金额（元）,交易手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,代付状态".split(","));
			int i = 0;
			try {
				for (OrderInfo t : TrList) {
					String gateRoute = "";
					if (t.getGid() != null && !String.valueOf(t.getGid()).equals("")) {
						gateRoute = gateRouteMap.get(t.getGid());
					}
					String[] str = { t.getOid(),(11==t.getType()?"对私代付":"对公代付")+"",t.getP8(), t.getMid(),t.getMid(),t.getName(),gateRoute,
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
				Integer gid=orderInfo.getGid();
				TrOrders o=new TrOrders();
				o.setTseq(tseq);o.setOrgOid(orderInfo.getOid());o.setSysDate(orderInfo.getSysDate());
				o.setOid(tseq);o.setGate(gid);o.setAid(orderInfo.getMid());o.setUid(orderInfo.getMid());
				o.setTransAmt(Long.parseLong(String.valueOf(orderInfo.getAmount())));
				o.setTransFee(orderInfo.getFeeAmt());o.setPayAmt(Long.parseLong(String.valueOf(orderInfo.getPayAmt())));
				if(orderInfo.getData_source()==8){ //自动结算
					o.setPtype(new Short("8")); 
				}else{ //接口代付 
					o.setPtype(null);
				}
				String[] res=reqSGSyncRes(o);
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
 				}else if(result.equals("normal")){
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
		/****
		 * 手工同步代付结果   发起商户通知  调用pay的商户通知接口
		 * @param lists
		 * @return
		 */
		public String reqNotice_Mer(List<OrderInfo> lists){
			StringBuffer msgSuc=new StringBuffer("代付流水：");
			StringBuffer msgFail=new StringBuffer("代付流水：");
			List<String> sqls=new ArrayList<String>();
			String isSuc="";String isFail="";
			try{
				for (OrderInfo orderInfo : lists) {
					String tseq=orderInfo.getTseq();
					String mid=orderInfo.getMid();
					String url = Ryt.getEwpPath();
					String oid=orderInfo.getOid();
					String p8=orderInfo.getP8();
					if(p8.equals(""))continue;
					Map<String, Object> requestMap=new HashMap<String, Object>();
					requestMap.put("tseq", tseq);
					requestMap.put("merId", orderInfo.getMid());
					requestMap.put("transType", orderInfo.getType());
					requestMap.put("oid", oid);
					String ret=Ryt.requestWithPost(requestMap, url+"df/notice_mer");
					String error_msg=null;
					if (ret.equals("notice_merchant_fail")){
						error_msg="手工代付结果通知商户失败";
						isFail+="12";
						msgFail.append(tseq).append("/");
					}else if (ret.equals("notice_merchant_suc")){
						error_msg="手工代付结果通知商户成功";
						String[] info=adminZHDao.queryOrderByOid(mid,oid);
						String table=info[0];
						//修改商户通知状态 is_notice
						sqls.add(modifyIsNotice(table,tseq,mid,1));
						isSuc+="12";
						msgSuc.append(tseq).append("/");
					}else{
						error_msg=ret;
						LogUtil.printInfoLog("DaifuServer", "reqNotice_Mer", "通知商户返回信息："+error_msg);
						isFail+="12";
						msgFail.append(tseq).append("/");
					}
				}
				if(sqls.size()>0){
					String[] sqlList=sqls.toArray(new String[sqls.size()]);
					adminZHDao.batchSqlTransaction(sqlList);
				}
				
			}catch (Exception e) {
				LogUtil.printInfoLog("DaifuServer", "reqNotice_Mer", "手工代付结果通知商户-请求ewp失败！");
				return "手工代付结果通知商户-请求ewp失败！";
			}
			msgSuc.replace(msgSuc.length()-1, msgSuc.length(), "");
			msgFail.replace(msgFail.length()-1, msgFail.length(), "");
			msgSuc.append(" 通知成功");
			msgFail.append(" 通知失败");
			if (!isSuc.equals("") && isFail.equals("")){
				return msgSuc.toString();
			}else if(isSuc.equals("") && !isFail.equals("")){
				return msgFail.toString();
			}else{
				return msgSuc.toString()+"\r\n"+msgFail.toString();
			}
		}
		
		/***
		 * 根据交易类型获取对应的网关  支付渠道
		 * @return
		 */
		public Map<Integer, String> getGateChannelMapByType(){
			Map<Integer,String> mapList=new HashMap<Integer, String>();
		    mapList=RYFMapUtil.getGateRouteMap();
			Iterator<Integer> iterator=mapList.keySet().iterator();
			List<String> list=new ArrayList<String>();
			while (iterator.hasNext()) {
				String integer = String.valueOf(iterator.next());
				if (!integer.matches("[4][0-46-9]+")){
					list.add(integer);
				}
			}
			for (String string : list) {
				mapList.remove(Integer.parseInt(string));
			}
			return mapList;
		}	
		
		/****
		 * 发起代付查询交易
		 * @param o
		 * @return
		 */
		public String[]  reqSGSyncRes(TrOrders o){
			try {
				B2EGate g = adminZHDao.getOneB2EGate(o.getGate());
				System.out.println("gate："+o.getGate());
				if (null == g)
					return new String[]{"支付渠道为空！","exception"};
				// 查询订单状态
				int code = B2ETrCode.QUERY_ORDER_STATE;
				b2eProcess = new B2EProcess(g, code);
				b2eProcess.setOrders(o);
				B2ERet ret=b2eProcess.submit();
				B2ETradeResult result=(B2ETradeResult) ret.getResult();
				Integer payState=null;
				if(result!=null){
					if(result.getState()==Constant.DaiFuTransState.PAY_SUCCESS){//成功
						payState=Constant.PayState.SUCCESS;
					}else if(result.getState()==Constant.DaiFuTransState.PAY_PROCESSING){//待支付
						payState=Constant.PayState.WAIT_PAY;
					}else if(result.getState()==Constant.DaiFuTransState.PAY_FAILURE){//失败
						payState=Constant.PayState.FAILURE;
					}
				}else{
					return new String[]{"请求银行失败！","req_fail"};
				}
				String[] info=adminZHDao.queryOrderByOid(o.getUid(), o.getOrgOid());
				String table=info[0];
				if (ret.isSucc()&&payState==PayState.WAIT_PAY)return new String[]{"该交易正在处理中","wait_pay"};
				
				if(!ret.isSucc()){
					String errorMsg=ret.getMsg();
					return new String[]{"请求银行失败！"+errorMsg,"req_fail"};
				}
				Short ptype=o.getPtype()==null ? 0 : o.getPtype(); //ptype == null 指定为0   ！=null 时 判断是否为8 自动结算
				
				if(ret.isSucc()&&payState==PayState.SUCCESS){
					//交易成功 1.修改订单状态  减少可用余额 
					String errorMsg=ret.getErrorMsg();String tseq=o.getTseq();
					String bk_seq=result.getBankSeq();Integer bk_date=Integer.parseInt(ret.getBank_date()== null ? "0" :ret.getBank_date());
					Integer bk_recvTime=DateUtil.getUTCTime(ret.getBank_time()==null?"0":ret.getBank_time());
					String modifyTstatSql=modifyTstatSql(table,o.getUid(),tseq,PayState.SUCCESS,errorMsg,bk_seq,bk_date,bk_recvTime);
					// ptype==7 修改tlog状态 插入流水
					String[] msg = ptype ==8 ?new String[] {"fee_liq_bath","自动结算"} :new String[]{table,"手工同步代付结果"};
					AccSeqs param=new AccSeqs();
					param.setTrAmt(o.getPayAmt());param.setAid(o.getAid());
					param.setUid(o.getUid());param.setTrFee(o.getTransFee());
					param.setAmt(o.getTransAmt());param.setTbId(tseq);
					param.setTbName(msg[0]);param.setRemark(msg[1]);
					//修改账户余额  录入流水
					List<String> sqlList = RecordLiveAccount.handleBalanceForTX(param);
					sqlList.add(modifyTstatSql);
					String[] sqls = sqlList.toArray(new String[sqlList.size()]);
					adminZHDao.batchSqlTransaction(sqls);
					return new String[]{ret.getErrorMsg()+ret.getMsg(),"success"};
				}
				//正常的接口代付  失败 情况 余额回滚
				if(ret.isSucc()&&payState==PayState.FAILURE  && ptype !=8){
					//交易失败 1.修改订单状态  
					String errorMsg=ret.getErrorMsg();String tseq=o.getTseq();
					String bk_seq=result.getBankSeq();Integer bk_date=Integer.parseInt(ret.getBank_date()== null ? "0" :ret.getBank_date());
					Integer bk_recvTime=DateUtil.getUTCTime(ret.getBank_time()==null?"0":ret.getBank_time());
					String modifyTstatSql=modifyTstatSql(table,o.getUid(),tseq,PayState.FAILURE,errorMsg,bk_seq,bk_date,bk_recvTime);
					String modifyBalanceSql=RecordLiveAccount.calUsableBalance(o.getAid(), o.getAid(), o.getPayAmt(), Constant.RecPay.INCREASE);
					String[] sqls={modifyTstatSql,modifyBalanceSql};
					adminZHDao.batchSqlTransaction(sqls);
					return new String[]{ret.getErrorMsg()+ret.getMsg(),"fail"};
				}
				
				//ptype ==8  自动结算   交易失败情况也录入流水
				if(ret.isSucc()&&payState==PayState.FAILURE  && ptype ==8){
					//交易失败 1.修改订单状态  
					String errorMsg=ret.getErrorMsg();String tseq=o.getTseq();
					String bk_seq=result.getBankSeq();Integer bk_date=Integer.parseInt(ret.getBank_date()== null ? "0" :ret.getBank_date());
					Integer bk_recvTime=DateUtil.getUTCTime(ret.getBank_time()==null?"0":ret.getBank_time());
					String modifyTstatSql=modifyTstatSql(table,o.getUid(),tseq,PayState.FAILURE,errorMsg,bk_seq,bk_date,bk_recvTime);
					String[] msg = ptype ==8 ?new String[] {"fee_liq_bath(2)","(结算到银行卡)自动结算"} :new String[]{table,"手工同步代付结果"};
					AccSeqs param=new AccSeqs();
					param.setTrAmt(o.getPayAmt());param.setAid(o.getAid());
					param.setUid(o.getUid());param.setTrFee(o.getTransFee());
					param.setAmt(o.getTransAmt());param.setTbId(tseq);
					param.setTbName(msg[0]);param.setRemark(msg[1]);
					//修改账户余额  录入流水
					List<String> sqlList = RecordLiveAccount.handleBalanceForTX(param);
					sqlList.add(modifyTstatSql);
					String[] sqls = sqlList.toArray(new String[sqlList.size()]);
					adminZHDao.batchSqlTransaction(sqls);
					return new String[]{ret.getErrorMsg()+ret.getMsg(),"fail"};
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return new String[]{"同步结果异常","exception"};
			}
			
			return new String[]{"同步结果成功","normal"};
		}
		
		/****
		 * 修改手工同步代付结果 订单状态
		 * @param table
		 * @param mid
		 * @param tseq
		 * @param state
		 * @param errorMsg
		 * @param bk_seq
		 * @param bk_date
		 * @param bk_recvTime
		 * @return
		 */
		public String modifyTstatSql(String table,String mid,String tseq,Integer state,String errorMsg,String bk_seq,Integer bk_date,Integer bk_recvTime){
			StringBuffer sql=new StringBuffer("update ").append(table).append(" set tstat=").append(state).append("");
			if (state==PayState.SUCCESS){
				if(!Ryt.empty(bk_seq))sql.append(", bk_seq1=").append(bk_seq);
				if(bk_date !=null)sql.append(" , bk_date=").append(bk_date);
				if(bk_recvTime != null)sql.append(", bk_recv=").append(bk_recvTime);
			}else if(state == PayState.FAILURE || state ==PayState.REQ_FAILURE){
				if(!Ryt.empty(errorMsg))sql.append(" , error_msg=").append(Ryt.addQuotes(errorMsg));
			}
			sql.append(" where mid=").append(Ryt.addQuotes(mid)).append(" and tseq=").append(Ryt.addQuotes(tseq)).append(" and tstat !=").append(PayState.SUCCESS);
			return sql.toString();
		}
		
		/****
		 * 
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
		
		/*
		 * 自动代付信息维护查询
		 */
		
		public CurrentPage<Minfo> queryAutoDf(Integer PageNo,String mid,Integer mstate){
			return adminZHDao.queryAutoDf(PageNo,mid,mstate);
			
		}
		/*
		 * 根据商户号查询出商户自动代付信息
		 */
		public Minfo queryByidAutoDf(String mid){
			return adminZHDao.queryByidAutoDf(mid);
			
		}
		
		/*
		 * 修改自动代付信息
		 * 
		 * 
		 */
		public String updateMerAutoDf(Minfo minfo,String mid){
			//search_edit_info.jsp中调用
			
			try {
				adminZHDao.saveOperLog("修改自动代付信息", "修改商户"+mid);
				minfo.setId(mid);
				adminZHDao.updateMerAutoDf(minfo);
			} catch (Exception e) {
				e.printStackTrace();
				return "修改失败！";
			}
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "minfo");
			p.put("k", mid);
			return EWPService.refreshEwpETS(p) ? "修改成功!" : "资料修改成功，刷新ewp失败!";
			
		}
		
		
		public CurrentPage<TrOrders> queryDFQRinfo(Integer pageNo,String batchNo,String tseq,Integer type,Integer dateType,Integer state,Integer bdate,Integer edate) throws Exception{
			if(!Ryt.isNumber(pageNo.toString()) || !Ryt.isNumber(type.toString())||!Ryt.isNumber(type.toString())
				||!Ryt.isNumber(dateType.toString())||!Ryt.isNumber(state.toString())||!Ryt.isNumber(bdate.toString())
				||!Ryt.isNumber(edate.toString()))
				throw new Exception("数据类型错误");
			if((type!=0 && type!=5 && type!=7) || (dateType!=0 && dateType!=1 && dateType!=2)
					||(state!=0 && state!=1 && state!=2 && state!=3 && state!=4 && state!=5 ))
				throw new Exception("数据类型错误");
			return merZHDao.queryDFQRinfo(pageNo,0,Ryt.sql(batchNo),Ryt.sql(tseq),type,dateType,state,bdate,edate);
		}
		
		/**
		 * 代付确认
		 * @param l
		 * @return
		 */
		public String DFconfirm(List<TrOrders> l){
			if(l==null||l.size()==0) return "请选择要确认的订单";
 			int gid=0;
			Map<String, Integer> tempMap=new HashMap<String, Integer>();
			StringBuffer Result = new StringBuffer();
			String oids = "";
			String oids2=new String();
			for(TrOrders o:l){
				oids2+=Ryt.addQuotes(o.getOid())+",";
			}
			oids2=oids2.substring(0, oids2.length()-1);
			
			for(TrOrders oid : l){
				String ordid=oid.getOid();
				Integer ordDate=oid.getSysDate();
				Integer gate=oid.getGate();
				String mid=oid.getUid();
				if(null==tempMap.get(mid+gate)){
					gid=merZHDao.getGidByOid(mid,ordid,ordDate);
					tempMap.put(mid+gate, gid);
				}else{
					gid=tempMap.get(mid+gate);
				}
				oids += ordid + ",";
			}
			
			batchUpdateOrder(Result,l); /***   批量修改订单数据  **/
			
			if(!"".equals(oids))
				new PayEnter().exec_pay(oids.substring(0, oids.length()-1).replaceAll(",", "','"));
			if(Result.length()==0){
				merZHDao.saveOperLog("代付确认操作", "代付确认成功操作完成");
				return "代付确认成功";
			}
			merZHDao.saveOperLog("代付确认操作", Result.toString());
			return   Result.toString();
		}
		
		/**
		 * 代付撤销
		 * @param oids
		 * @return
		 * @throws Exception
		 */
		public String DFcancel(List<TrOrders> oids) throws Exception{
			String mid=dao.getLoginUserMid();
			int date = DateUtil.today();
			int time= DateUtil.getCurrentUTCSeconds();
			for (int i = 0; i < oids.size(); i++) {
				String []sqls=new String[3];
				StringBuffer tlog=new StringBuffer();
				StringBuffer trOrders=new StringBuffer();
				StringBuffer balancesql=new StringBuffer();
				tlog.append("update hlog set tstat=").append(Constant.PayState.CANCEL);
				tlog.append(" ,p4=").append(date);
				tlog.append(" ,p5=").append(time);
				tlog.append(" where tseq=").append(Ryt.addQuotes(oids.get(i).getTseq()));
				trOrders.append("update tr_orders set pstate=").append(Constant.DaifuPstate.AUDIT_FAILURE);
				trOrders.append(" where oid=").append(Ryt.addQuotes(oids.get(i).getOid()));
				balancesql.append(RecordLiveAccount.calUsableBalance(mid, mid, oids.get(i).getPayAmt(), 0));
				sqls[0]=tlog.toString();
				sqls[1]=trOrders.toString();
				sqls[2]=balancesql.toString();
				int[]	flag=dao.batchSqlTransaction(sqls);
				if (null==flag || flag.length!=sqls.length)
					return "fail";
			}
			return "success";
		}
		
		public FileTransfer downqueryDaiFuQueRen(Integer pageNo,String batchNo,String tseq,Integer type,Integer dateType,Integer state,Integer bdate,Integer edate) throws Exception{
			CurrentPage<TrOrders> TrListPage=merZHDao.queryDFQRinfo(pageNo,-1,Ryt.sql(batchNo),Ryt.sql(tseq),type,dateType,state,bdate,edate);
			String [] tstats={"申请代付","代付确认","代付成功","代付失败","请求银行失败","代付撤销"};
			List<TrOrders> TrList=TrListPage.getPageItems();
			Map<Integer, String> provMap=  new PageService().getProvMap();
			ArrayList<String[]> list = new ArrayList<String[]>();
			long totleTransAmt = 0;
			long totlePayAmt = 0;
			long totleTransFee=0;
			list.add("序号,商户号,电银流水号,账户号,商户订单号,批次号,交易金额（元）,系统手续费（元）,付款金额（元）,收款银行,收款账户名,收款账号,开户省份,代付状态,交易类型,代付日期,用途".split(","));
			int i = 0;
			try {
				for (TrOrders t : TrList) {
					String[] str = { (i + 1) + "",t.getUid(),t.getTseq(),t.getAname(),t.getOid(),t.getTrans_flow(),Ryt.div100(t.getTransAmt())+"",Ryt.div100(t.getTransFee())+"",Ryt.div100(t.getPayAmt())+"",
							t.getToBkName(),t.getToAccName(),t.getToAccNo(),provMap.get(t.getToProvId()),(tstats[Integer.parseInt(t.getTstat())]),7==t.getPtype()?"对私代付":"对公代付"+"",
							t.getSysDate()+"",t.getPriv()};
					totleTransAmt += t.getTransAmt();
					totlePayAmt += t.getPayAmt();
					totleTransFee+=t.getTransFee();
					i += 1;
					list.add(str);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String[] str = { "总计:" + i + "条记录", "","","","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totleTransFee)+"" , Ryt.div100(totlePayAmt)+"","","","","","","","","" };
			list.add(str);
			String filename = "DaiFUQueRen_" + DateUtil.today() + ".xlsx";
			String name = "代付确认表";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
			
		}
		
		/***
		 * 代付单笔录入  验证账户是否可以代付 验证余额是否足够代付  计算手续费
		 * @param aid 账号号
		 * @param transAmt 交易金额  格式以分为单位
		 * @param toBkNo 收款行号
		 * @param oid 订单号  
		 * @return array[oid,transAmt,transFee,payAmt,balance,gid]  其中 金额格式为元  0.01
		 */
		public String[] checkSinglePay(String aid,String oid,String transAmt,String toBkNo)throws Exception{
			String[] result=null;
			AccInfos accInfos=dao.queryToAid(aid);
			long transAmt2=Ryt.mul100toInt(transAmt);
			if(accInfos.getState()!=1)throw new Exception("该账户非正常状态"); 
			/*if(5000000<= Long.parseLong(Ryt.mul100(payAmt)))throw new Exception("该笔交易金额大于单笔限制金额");*/
			//计算手续费  实际支付金额
			FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(merZHDao.getLoginUserMid(), toBkNo);
			//交易手续费  格式 (以分为单位)
			long transFee=(long) Ryt.mul100toInt(ChargeMode.reckon(feeCalcMode.getCalcMode(),Ryt.div100(transAmt2)));
			long payAmt=transAmt2+transFee;//实际支付金额 格式 (以分为单位)
			long balance=accInfos.getBalance();//账户可用余额 格式 (以分为单位)
			if( oid == null|| oid.equals("")) oid=Ryt.genOidBySysTime();/*订单号---上送订单号 该订单号不变返回， 未上送订单号 生成订单号 返回*/
			if(accInfos.getBalance()< payAmt )throw new Exception("该笔交易金额大于该账户可用余额");
			String toBkNo2=BankNoUtil.getDaifuMiddletMap().get(toBkNo);
			result=new String[]{oid,Ryt.div100(transAmt2),Ryt.div100(transFee),Ryt.div100(payAmt),Ryt.div100(balance),String.valueOf(feeCalcMode.getGid()),toBkNo2};
			return result;
		}
		
		/****
		 * 代付确认处理操作
		 * @param result
		 * @param orders
		 * @param auditMsg
		 */
		public void batchUpdateOrder(StringBuffer result,List<TrOrders> orders){
			List<String> lists=new ArrayList<String>();
			int date = DateUtil.today();
			int time= DateUtil.getCurrentUTCSeconds();
			for (TrOrders trOrders : orders) {
				StringBuffer sql=new StringBuffer();
				sql.append("update tr_orders set pstate="+Constant.DaifuPstate.AUDIT_SUCCESS+",");
				sql.append("       audit_oper=").append(adminZHDao.getLoginUser().getOperId()).append(",");//审核操作员
//				sql.append("       audit_remark=").append(Ryt.addQuotes(option)).append(", ");
				sql.append("       audit_date=").append(date).append(", ");
				sql.append("       audit_time=").append(time).append(",");
				sql.append("	   is_pay=2 ");
				sql.append(" where oid=" + Ryt.addQuotes(trOrders.getOid()));
				sql.append("   and uid = " + Ryt.addQuotes(trOrders.getAid()));
				sql.append("   and is_pay = 0"); //代付确认 追加条件 is_pay not in (1,2)  1: 已代付  2：代付发起
				StringBuffer sql3=new StringBuffer();		
				sql3.append("update hlog set bk_send = " + time);
				sql3.append(",tstat=1 ");
				sql3.append(" , p4=").append(date);
				sql3.append(" , p5=").append(time);
				sql3.append(" where mid = " + Ryt.addQuotes(trOrders.getUid()));
				sql3.append("   and oid = " + Ryt.addQuotes(trOrders.getOid()));
				sql3.append("  and tstat <> ").append(Constant.PayState.SUCCESS);
				lists.add(sql.toString());
				lists.add(sql3.toString());
			}
			
			String[] sqls=lists.toArray(new String[lists.size()]);
			try {
				int[] affectLines=dao.batchSqlTransaction(sqls);
				if(affectLines==null){
					result.append("订单处理异常，请联系管理员");
					return ;
				}
				for (int i : affectLines) {
					if(i==0){
						result.append("订单处理异常，请联系管理员");
						return ;
					}
				}
			} catch (Exception e) {
				LogUtil.printErrorLog("DaiFUService", "batchUpdateOrder", "Exception msg:"+e.getMessage(), e);
				result.append("订单处理异常，请联系管理员");
				return ;	
			}
			
		}
}
