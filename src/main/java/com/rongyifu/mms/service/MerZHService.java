package com.rongyifu.mms.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.directwebremoting.io.FileTransfer;
import org.springframework.dao.DuplicateKeyException;

import uk.ltd.getahead.dwr.WebContext;
import uk.ltd.getahead.dwr.WebContextFactory;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.CusContactInfos;
import com.rongyifu.mms.bean.CusInfos;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.bean.TrOrders_Batch;
import com.rongyifu.mms.bean.UserBkAcc;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.BankNoUtil;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.RootPath;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerZHDao;
import com.rongyifu.mms.dao.TransactionDao;
import com.rongyifu.mms.utils.ChargeMode;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.EmaySMS;
import com.rongyifu.mms.utils.GenRequestParamUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.ParamUtil;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.bean.OrderInfos;

public class MerZHService {
	private MerZHDao merZHDao= new MerZHDao();
	private TransactionDao transactionDao=new TransactionDao();
	/**
	 * 增加账户
	 * @param info 账户对象
	 * @throws Exception 
	 * @throws DuplicateKeyException
	 */
	public String add(String aid,String aname,String mid) throws Exception{
		if(Ryt.empty(aid)) return "操作失败，账号名称不能为空"; 
		if(Ryt.empty(aname)) return "操作失败，账户名称不能为空";
		if (merZHDao.getAname1(mid)>3) return "操作失败，账户不能多于4个";
		try {
			AccInfos a = new AccInfos(aid,""+mid,aname,DateUtil.today());
			merZHDao.add(a);
			return "ok";
		} catch (DuplicateKeyException e) {
			return "操作失败，该账号名称已经存在";
		}
	}
	/**开启余额
	 * @param mid 商户号（用户ID）
	 * @param aid 账户号
	 * @param state 状态 0-生成，1-正常，2-关闭
	 */
	public String openPF(String mid,String aid,String pwd){
     	String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		int row=merZHDao.editPF(mid,aid,1);
		if(row==1){
			return "操作成功,余额支付功能修改为正常状态";
		}else{
			return "操作异常";
		}
	}
	
	/**关闭余额
	 * @param mid 商户号（用户ID）
	 * @param aid 账户号
	 * @param pwd 密码
	 * @param state 状态 0-生成，1-正常，2-关闭
	 */
	public String closePF(String mid,String aid,String pwd){
		String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		int row=merZHDao.editPF(mid,aid,0);
		if(row==1){
			return "操作成功，余额支付功能修改为关闭状态";
		}else{
			return "操作异常";
		}
	}
	/**
	 * 关闭账户
	 * @param mid，商户号（用户ID）
	 * @param aid，账户号
	 * @param pwd 密码
	 * @param state 状态 0-生成，1-正常，2-关闭 
	 * @return
	 */
	public String closeZH(String mid,String aid,String pwd){
		String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		
		int row=merZHDao.closeZH(mid,aid,2);
		if(row==1){
			return "操作成功，账户为关闭状态";
		}else{
			return "操作异常";
		}
	}
	/**
	 * 查询账户余额
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public List<AccInfos> queryZHYE(String mid){
		return merZHDao.queryZHYE(mid);
		
	}
	/**
	 * 查询账户下的所有用户名称
	 * @return
	 */
	public Map<String, String> getZH() {
		return merZHDao.getZH();
	}
	public Map<String, String> getZH2() {
		return merZHDao.getZH2();
	}
	/**
	   * 查询账户下的状态为正常的用户名称
	   * 查询账户名称
	   * @return
	   */
	public Map<String, String> getZHUid() {
		return merZHDao.getZHUid();
	}
	/**
	 * 查询账户下的状态为正常的用户名称，并且不可以自己转给自己
	 * 查询对方账户名称
	 * @param uid，用户ID
	 * @return
	 */
	public Map<String, String> getZHByUid(String uid) {
		return merZHDao.getZHByUid(uid);
	}
	
	
	/**
	 * 查询明细
	 * @param pageNo
	 * @param aid,账户ID
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate,
	 * @param edate,
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @param oid,
	 * @return
	 */
	public CurrentPage<TrOrders> queryMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow) {
     return merZHDao.queryMX(pageNo,ptype,bdate,edate,state,oid, trans_flow);
	}
	
	/**
	 * 上传凭证查看该批次号明细信息
	 * @param pageNo
	 * @param aid,账户ID
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate,
	 * @param edate,
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @param oid,
	 * @param trans_flow
	 * @return
	 */
	public CurrentPage<TrOrders> queryMX_showDetail(Integer pageNo, String aid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow) {
    
		return merZHDao.queryMX_detail(pageNo,aid,ptype,bdate,edate,state,oid,trans_flow);
	}
	
	/****
	 * 账户交易信息明细  （新修改。。）
	 * @param pageNo
	 * @param uid
	 * @param ptype
	 * @param bdate
	 * @param edate
	 * @param state
	 * @param oid
	 * @return
	 */
	public CurrentPage<TrOrders> queryZHJYMX_X(Integer pageNo ,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String trans_flow) {
		return merZHDao.queryZHJYMX(pageNo,ptype,bdate,edate,state,oid, trans_flow);
	}
	
	/**
	 * 上传凭证查询批量明细
	 * @param pageNo
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate,
	 * @param edate,
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @return
	 */
	public CurrentPage<TrOrders_Batch> queryMX_(Integer pageNo,Integer ptype,Integer bdate,Integer edate,Integer state) {
		return merZHDao.queryMX(pageNo,ptype,bdate,edate,state);
	}
	
	/**
	 * 审批凭证查询批量明细
	 * @param pageNo
	 * @param aid,账户ID
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate,
	 * @param edate,
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @return
	 */
	public CurrentPage<TrOrders_Batch> queryMX_A_SPPZ(Integer pageNo, String uid,Integer ptype,Integer bdate,Integer edate,Integer state,Integer mstate) {
		return merZHDao.queryMX_A_SPPZ(pageNo,uid,ptype,bdate,edate,state,mstate);
	}
	
	/****
	 * 上传付款凭证
	 * @param uid
	 * @param trans_flow
	 * @param fileTransfer
	 * @return
	 */
	public String uploadBatchPic(String uid,String trans_flow,String oid,FileTransfer fileTransfer,String recharge_amt ){
		try {
			WebContext webContext = WebContextFactory.get();
			String realPath="";
			if(trans_flow.equals("")){
				 realPath = ParamUtil.getPropertie("Batch_Path_DB");
			}else{
				 realPath = ParamUtil.getPropertie("Batch_Path_PL");
			}
			String filename_Old = fileTransfer.getName();// 文件名
			if (filename_Old.indexOf(".") <= 0) {
				
				return "请上传正确的文件";
			}
			String extensions = filename_Old.substring(filename_Old
					.lastIndexOf("."));// 后缀名
			if (!Ryt.isPicture(filename_Old)) {
				return "文件格式不对";
			}
			String fileName = uid + "-" + (trans_flow.equals("")?oid:trans_flow) + extensions;
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
			// 复制图片到工程目录
			getPICPath_(realPath + "/" + fileName, fileName);
			String sql = "update tr_orders set trans_proof='" + fileName
					+ "',state=32 ,recharge_amt=" + Ryt.mul100(recharge_amt)
					+ " where 1=1  ";
			if (trans_flow.equals("null") || trans_flow.equals("")) {
				sql += " and oid='" + oid + "'";
			} else {
				sql += " and trans_flow='" + trans_flow + "'";
			}
			int res = merZHDao.update(sql);
			if (res == 0) {
				merZHDao.saveOperLog("充值凭证上传", "充值凭证上传失败");
				return "上传失败";
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("MerZHService", "uploadBatchPic", "上传充值凭证失败-"+e.getMessage());
			return "上传失败";
		}
		merZHDao.saveOperLog("充值凭证上传", "充值凭证上传成功");
		return "上传成功";
	}
	
	/****
	 * 审核
	 * @param oid
	 * @param trans_flow
	 * @return
	 */
	public String do_SHPZ(String oid,String trans_flow,int ptype,String option){
		option=Ryt.sql(option);
		StringBuffer sql=new StringBuffer();
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		sql.append("update tr_orders set ");
		sql.append(" cert_oper=").append(merZHDao.getLoginUser().getOperId()).append(",");//审核操作员
		sql.append("cert_remark=").append(Ryt.addQuotes(Ryt.sql(option))).append(", ");
		sql.append("cert_date=").append(date).append(", ");
		sql.append("cert_time=").append(time).append(", ");
		if(ptype==0){
			sql.append(" state=32,pstate=5  where 1=1 ");
			
		}else{
			sql.append(" state=33,pstate=5 where 1=1 ");
		}
		if(trans_flow.equals("")){
			sql.append(" and oid='").append(oid).append("' ");
		}else {
			sql.append(" and trans_flow='").append(trans_flow).append("'");
		}
		int res = merZHDao.update(sql.toString());
		if (res == 0) {
			merZHDao.saveOperLog("凭证上传审核", "凭证上传审核成功操作失败");
			return "系统异常";
		}
		/*if(ptype==0){
			
			
//			插入流水表	
				String sql1="insert into acc_seqs (uid,aid,tr_amt,tr_fee,amt,rec_pay,tb_name,tb_id,remark) select uid,aid,trans_amt,trans_fee,pay_amt,0,'tr_orders',"+oid+",'充值' from tr_orders where tr_orders.oid='"+oid+"'";
				int r= merZHDao.update(sql1.toString());
				if(r==0)return "系统异常";
		}*/
		merZHDao.saveOperLog("凭证上传审核", "凭证上传审核成功操作完成");
		return "审核成功";
	}
	
	/****
	 * 审核失败
	 * @param oid
	 * @param trans_flow
	 * @param ptype
	 * @param option
	 * @return
	 */
	public String do_SHPZ_F(String oid,String trans_flow,int ptype,String option){
		option=Ryt.sql(option);
		StringBuffer sql=new StringBuffer();
		int date = DateUtil.today();
		int time= DateUtil.getCurrentUTCSeconds();
		sql.append("update tr_orders set");
		sql.append(" audit_oper=").append(merZHDao.getLoginUser().getOperId()).append(",");//审核操作员
		sql.append("audit_remark=").append(Ryt.addQuotes(option)).append(", ");
		sql.append("audit_date=").append(date).append(", ");
		sql.append("audit_time=").append(time).append(", ");
		sql.append("cert_oper=").append(merZHDao.getLoginUser().getOperId()).append(",");
		sql.append("cert_remark=").append(Ryt.addQuotes(option)).append(",");
		sql.append(" state=13,pstate=6  where 1=1 ");
			
		StringBuffer hlogSql=new StringBuffer("update hlog set tstat=");
		hlogSql.append(Constant.PayState.FAILURE);
		hlogSql.append(" where 1=1 ");
		if(trans_flow.equals("")){
			sql.append(" and oid='").append(oid).append("' ");
			hlogSql.append(" and oid=");
			hlogSql.append(Ryt.addQuotes(oid));
		}else {
			sql.append(" and trans_flow='").append(trans_flow).append("'");
			hlogSql.append(" and trans_flow=");
			hlogSql.append(Ryt.addQuotes(trans_flow));
		}
		int[]  flag=merZHDao.batchSqlTransaction(new String[]{sql.toString(),hlogSql.toString()});
		if(flag!=null&&flag.length==2){
			merZHDao.saveOperLog("凭证上传审核", "凭证上传审核失败操作完成");			
			return "审核操作已完成";
		}
		else {
			merZHDao.saveOperLog("凭证上传审核", "凭证上传审核失败操作完成");
			return "系统异常";
		}
	}
	
	/***
	 * 获取上传凭证在服务器的保存路径
	 * @return
	 */
	public String getPICPath(){
		
		return ParamUtil.getPropertie("Batch_Path_DB")+","+ParamUtil.getPropertie("Batch_Path_PL");
	}
	
	public String getPICPath_(String file,String fileName){
		try{
			FileInputStream inputStream=new FileInputStream(new File(file));
			String rootpath=RootPath.class.getResource("/").getPath().substring(1,RootPath.class.getResource("/").getPath().indexOf("/WEB-INF")).replace("%20", " ");
			File file1=new File("/"+rootpath+"/public/cert/"+fileName);
			if (!file1.exists()) {
//				file1.createNewFile();
			}
			FileOutputStream outputStream=new FileOutputStream(file1,false);
			
			byte tmp[] = new byte[1024*1024];
			int i=0;
			while ((i = inputStream.read(tmp)) != -1) {
				outputStream.write(tmp, 0, i);
			}
			inputStream.close();
			outputStream.flush(); //强制清出缓冲区                
			outputStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "/public/cert/"+fileName;
	}
	
	/**
	 * 下载明细
	 * @param ptype,交易类型,0-充值,1-提现,2-充值撤销,3-付款到融易付账户,4-收款,5-付款到银行卡
	 * @param bdate
	 * @param edate
	 * @param state,状态,0-订单生成,1-交易处理中,,2-交易成功,3-交易失败,4-订单取消,5-付款处理中,6-付款失败
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadMX(Integer ptype,Integer bdate,Integer edate,Integer state,String oid) throws Exception {
		  CurrentPage<TrOrders> TrListPage=merZHDao.queryMX(-1,ptype,bdate,edate,state,oid,"");
		  List<TrOrders> TrList=TrListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		list.add("序号,账户号,账户名称,系统订单号,交易金额,手续费,系统日期,系统时间,交易状态,交易类型,交易对方,订单时间,融易付流水号".split(","));
		int i = 0;
		try {
			for (TrOrders t : TrList) {
				String[] str = { (i + 1) + "",t.getAid(),t.getAname() , t.getOid(),Ryt.div100(t.getPayAmt()),Ryt.div100(t.getTransFee()),t.getSysDate()+"",
						Ryt.getStringTime(t.getSysTime())+"",AppParam.h_acc_state.get(Integer.parseInt(t.getState()+"")),
						AppParam.h_z_ptype.get(Integer.parseInt(t.getPtype()+""))+"",(t.getToUid()==null||t.getToUid().equals("null"))?"":t.getToUid()+((t.getToAid()==null||t.getToAid().equals("null"))?"":"["+t.getToAid()+"]"),
								Ryt.getNormalTime(t.getInitTime()+"")+"",t.getTseq()+""};
				totleTransAmt += t.getPayAmt();
				totlePayAmt += t.getTransFee();
				i += 1;
				list.add(str);
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totlePayAmt)+"" ,"","","", "", "", "", "" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "明细表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
        }
	
	 /**
	   * 查询流水
	   * @param pageNo
	   * @param aid,账户ID
	   * @param bdate
	   * @param edate
	   * @return
	 * @throws ParseException 
	   */
	
	public CurrentPage<AccSeqs> queryLS(Integer pageNo,String aid,Integer bdate,Integer edate){
	     return merZHDao.queryLS(pageNo,aid,bdate,edate);
		}
	/**
	 * 下载流水查询
	 * @param aid,账户ID
	 * @param bdate
	 * @param edate
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadLS(String aid,Integer bdate,Integer edate) throws Exception {
		
		  CurrentPage<AccSeqs> acListPage=merZHDao.queryLS(-1,aid,bdate,edate);
		  List<AccSeqs> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTrAmt = 0;
		long totleTrFee = 0;
		long totleAmt = 0;
		list.add("序号,账户号,系统日期,系统时间,收支标识,交易金额,交易手续费,变动金额,可用余额,余额,简短说明".split(","));
		int i = 0;
		for (AccSeqs a : acList) {
			String[] str = { (i + 1) + "",a.getAid(),a.getTrDate()+"",Ryt.getStringTime(a.getTrTime())+"",
					AppParam.h_rec_pay.get(Integer.parseInt(a.getRecPay()+"")),
					Ryt.div100(a.getTrAmt()),Ryt.div100(a.getTrFee()),
					Ryt.div100(a.getAmt()),Ryt.div100(a.getBalance()),Ryt.div100(a.getAllBalance()+a.getBalance()),a.getRemark()};
			totleTrAmt += a.getTrAmt();
			totleTrFee += a.getTrFee();
			totleAmt += a.getAmt();
			i += 1;
			list.add(str);
			
		}
		String[] str = { "总计:" + i + "条记录","","","", "", Ryt.div100(totleTrAmt)+"",Ryt.div100(totleTrFee)+"",Ryt.div100(totleAmt)+"", "" ,"" ,""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "账户流水查询明细表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
        }
	
	
	/**
	   * 查询我要付款
	   * 查询本账户可用金额，和对方账户
	   * @param aid,账户ID
	   * @return
	   */
	@SuppressWarnings("unchecked")
	public List queryFK(String aid,String toUid ){
		List a = new ArrayList();
		a.add(merZHDao.queryFK(aid));
		
		Map<String,String> m = getZHByUid(toUid);
		
		if(m.size()==0)a.add(null);
		a.add(m);
		return   a;
	}
	
	/**
	 * 对方账户信息
	 * @param toAid，对方账户
	 * @return
	 */
	public AccInfos queryToAid(String toAid){
		
		return merZHDao.queryToAid(toAid);
	}
	
	/**
	 * 确定我要付款
	 * @param aid，账户ID
	 * @param mid ，商户号（用户ID）
	 * @param toAid，交易对方账户ID
	 * @param toUid ，交易对方用户ID
	 * @param payAmt ，结算金额
	 * @throws Exception  
	 *
	 */
	public  String qdzf(String aid,String mid,String toAid,String toUid,String payAmt,String pwd,String oid,String oid2){
		String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		try{
			merZHDao.qdzf(aid,mid,toAid,toUid,payAmt,oid,oid2);
		}catch(Exception e){
			e.printStackTrace();
			return "操作失败";
		}
		return "操作成功";
	}
	/**
	 * 查询商户可用的银行网关
	 * @return
	 */
	public Map<Integer,String> getPayGates(){
		
		return merZHDao.getPayGates();
	}
	/**
	 * 账户充值--得到支付加密数据
	 * @param aid
	 * @param chargeAmount
	 * @param gate
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getEncryptPayParam(String payAmt,Integer gate) throws Exception{
		if(!Ryt.isMoney(payAmt)){
			throw new Exception("金额格式错误！");
		};
//		if(!merZHDao.merTransAccIsNomal(merZHDao.getLoginUserMid()+"")){
//			throw new Exception("暂无交易账户，无法交易！");
//		}
		//计算手续费
		AccInfos accInfos = merZHDao.queryFKUID(merZHDao.getLoginUserMid()+"");// 付款方
		if(accInfos.getState()!=1)throw new Exception("该账户非正常状态!");
		// 计算手续费
		String feeMode = accInfos.getCzFeeMode();
		String transFee = ChargeMode.reckon(feeMode, payAmt);
		String aname=accInfos.getAname();//账户名
		//		Map<String, Object> modeMap=merZHDao.getFeeModeAid(aid,"cz_fee_mode");
//		String aname=(String)modeMap.get("aname");//账户名
//		String feeMode=(String)modeMap.get("cz_fee_mode");//手续费公式
//		String transFee=ChargeMode.reckon(feeMode, payAmt);
		long saveTransAmt=Ryt.mul100toInt(payAmt)+Ryt.mul100toInt(transFee);//实际交易金额（乘100后的）
		Integer saveTransFee=(int) Ryt.mul100toInt(transFee);
		long savePayAmt=Ryt.mul100toInt(payAmt);
		
		String ordId=Ryt.genOidBySysTime();
		Map<String,String> paramMap=GenRequestParamUtil.getParamKeyMap(Ryt.mul100toInt(payAmt),0, gate, ordId);
			int []row=merZHDao.insertTrOrders(ordId,aname,Ryt.mul100toInt(payAmt),0,Ryt.mul100toInt(payAmt),"",gate);
			if(row.length!=2){
				throw new Exception("提交的订单异常！请重试，或联系客服人员");
			}
			return paramMap;
	}
	/**
	 * 
	 * @param oid
	 * @return
	 */
	public String submitPayOrder(String ordId)throws Exception{
//		if(!merZHDao.merTransAccIsNomal(merZHDao.getLoginUserMid()+"")){
//			throw new Exception("暂无交易账户,无法交易！");
//		}
		if(Ryt.empty(ordId))return "订单号不能为空！";
		int row=merZHDao.updateTrOrder(ordId);
		if(row==1)
		return AppParam.SUCCESS_FLAG;
		else
			return "更新订单状态失败！";
		
	}
	
	/**
	 * 查询订单状态
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public String queryOrderState(String orderId) throws Exception{
		if(orderId.equals("")){throw new Exception("orderId error");}
		Map<Integer,String> stateMap=AppParam.h_zz_state;
		int state= merZHDao.queryOrderState(orderId);
		return stateMap.get(state);
	}
	/**
	 * 查询账户下的状态为正常的用户名称（结算账户）
	 * @param uid，用户ID
	 * @return
	 */
	public Map<String, String> getZHUid2(String uid) {
		return merZHDao.getZHUid(uid);
	}
	/**
	   * 查询我要付款
	   * @param aid,账户ID
	   * @return
	   */
	public AccInfos queryZH(String aid){
		return merZHDao.queryFK(aid);
	}
	/**
	 * 确定提现 （2013-04-26 结算帐户 yang.yaofeng）
	 * @param mid，商户号（用户ID）
	 * @param aid,账户ID
	 * @param transAmt,交易金额
	 * @return
	 */
	public String editTX_N(String mid,String aid,String payAmt,String pwd)throws Exception{
		String oldPass = merZHDao.getPass(mid);
		 AccInfos accInfos=merZHDao.queryToAid(mid);
		 if(accInfos.getState()!=1)throw new Exception("该账户非正常状态!");
		if(!Ryt.isPay(accInfos.getBalance(), Long.parseLong(Ryt.mul100(payAmt))))throw new Exception("该比交易金额大于单笔限制金额");
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		if(merZHDao.editTX(mid,mid, payAmt).length==3){
			merZHDao.saveOperLog("账户提现", "提现成功-提现金额为"+payAmt);
			return "操作成功";
		}
		else{
			merZHDao.saveOperLog("账户提现", "提现失败-提现金额为"+payAmt);
			return "操作失败";
		}
	}
	/**
	 * 用户银行账户新增
	 * @param uid，用户ID
	 * @param accName，开户帐号名称
	 * @param bkName，开户银行名称
	 * @param provId，开户省份
	 * @param accNo，开户银行账号
	 * @return
	 */
	public String addUserBkAcc(String uid,String accName,String bkName,Integer gate,Integer provId,String accNo){
		
		if(merZHDao.addUserBkAcc(uid,accName,bkName,gate,provId,accNo)==1)return "操作成功";
		else
		return "操作失败";
	}
	/**
	 * 获取全部银行账户信息
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<UserBkAcc> getUserBkAcc(Integer pageNo){
		return merZHDao.getUserBkAcc(pageNo);
	}
	/**
	 * 获取单个银行账户信息
	 * uid+accNo ,Unique Index
	 * @param uid，用户ID
	 * @param accNo，开户银行账号
	 * @return
	 */
	public List<UserBkAcc> getOneUserBkAcc(String uid,String accNo) {
		return merZHDao.getOneUserBkAcc(uid,accNo);
	}
	/**
	 * 删除银行账户信息
	 * @param uid，用户ID
	 * @param accNo，开户银行账号
	 * @return
	 */
	public String deleteOneUserBkAcc(String uid,String accNo){
		if(merZHDao.deleteOneUserBkAcc(uid,accNo)==1)return "删除成功";
		else
		return "删除失败";
	}
	/**
	 * 修改单个银行账户信息
	 * @param uid，用户ID
	 * @param aBkName，新开户银行名称
	 * @param aProvId，新开户省份
	 * @param aAccNo，原开户银行账号
	 * @param bAccNo，修改后的新开户银行账号
	 * @return
	 */
	public String editOneUserBkAcc(String uid,String aBkName,Integer aGate,Integer aProvId,String aAccNo,String bAccNo){
		if(merZHDao.editOneUserBkAcc(uid,aBkName,aGate,aProvId,aAccNo,bAccNo)==1)return "修改成功";
		else
		return "修改失败";
	}
	/**
	 * 查询是否已经进行支付密码维护
	 * @return
	 */
	public String getMinfoPayPwd(){
		if(merZHDao.getMinfoPayPwd()>0){
			return "ok";
		}else{
			return "false";
		}
	}
	/**
	 * 添加支付密码
	 * @param mid
	 * @param pass
	 * @param vpass
	 * @return
	 */
	public String addMinfoPayPwd(String mid,String pass) {
		try {
			if (Ryt.empty(pass)) return "请输入支付密码。";
			merZHDao.saveOperLog("添加支付密码", "添加支付密码");
			if (merZHDao.editMinfoPayPwd(mid, pass) ){
				merZHDao.saveOperLog("添加支付密码", "添加支付密码操作成功");
				return "添加成功";
			}
			merZHDao.saveOperLog("添加支付密码", "添加支付密码操作失败");
			return "添加失败";
		} catch (Exception e) {
			return "添加异常";
		}
	}
	/**
	 * 修改支付密码
	 * @param mid_
	 * @param opass
	 * @param npass
	 * @return
	 */
	public String editMinfoPayPwd(String mid_,String opass, String npass) {
		try {
			if (Ryt.empty(npass)) return "请输入新支付密码。";
			
			String oldPass = merZHDao.getPass( mid_);
			if (!oldPass.equals(opass)) {
				return "原支付密码错误！";
			}
			if(merZHDao.editMinfoPayPwd(mid_,npass)){
				merZHDao.saveOperLog("修改支付密码", "修改成功");
				return "修改成功";
			}
			merZHDao.saveOperLog("修改支付密码", "修改失败");
			return  "修改失败";
		} catch (Exception e) {
			return "修改异常";
		}
	}
	/**
	 * 得到关联客户信息,客户信息维护的信息查询
	 * @param c_cid,客户ID
	 * @return
	 * @throws Exception 
	 */
	public List<CusInfos> getCusInfos(String c_cid) throws Exception{
		return merZHDao.getCusInfos(c_cid);
	}
	/**
	 * 	获取单个联系人信息
	 * @param id，cus_contact_infos表，自增长唯一标识
	 * @return
	 */
	public List<CusContactInfos> getOneCusContactInfos(Integer id) {
		return merZHDao.getOneCusContactInfos(id);
	}
	
	/**
	 * 修改单个联系人信息
	 * @param id，cus_contact_infos表，自增长唯一标识
	 * @param contact，姓名
	 * @param position，职位
	 * @param cell，手机
	 * @return
	 */
	public String editOneCusContactInfos(Integer id,String contact,String position,String cell){
		if(merZHDao.editOneCusContactInfos(id,contact,position,cell)==1)return "修改成功";
		else
		return "修改失败";
	}
	/**
	 * 查询开户账号名称
	 * @param cid
	 * @return
	 */
	public String getAccName(String cid){
		return merZHDao.queryForString("select acc_name from user_bk_acc where uid='"+cid+"' limit 1");
	}
	/**
	 * 单个联系人信息添加	
	 * @param iiUid，用户ID
	 * @param iContact，姓名
	 * @param iPosition，职位
	 * @param iCell，手机
	 * @return
	 */
	public String addOneCusContactInfos(String iiUid,String iContact,String iPosition,String iCell){
		
		if(merZHDao.addOneCusContactInfos(iiUid,iContact,iPosition,iCell)==1)return "操作成功";
		else
		return "操作失败";
	}
	/**
	 * 添加客户信息（未完成编码，只实现了单个添加，没有实现多个添加）
	 * @param cid，客户ID
	 * @param ctype，类型，0-企业，1-个人
	 * @param tradeType，所属行业
	 * @param cname，名称
	 * @param bkName，开户银行名称
	 * @param provId，开户省份
	 * @param accNo，开户银行账号
	 * @param contact，姓名
	 * @param position，职位
	 * @param cell，手机
	 * @throws Exception
	 */
	public String addKeHu(String cid,Integer ctype,Integer tradeType,String cname,
			String bkName,Integer gate,Integer provId,String accNo){
		try{
			merZHDao.addKeHu(cid,ctype,tradeType,cname,bkName,gate,provId,accNo);
		}catch(Exception e){
			e.printStackTrace();
			return "增加失败";
		}
		return "增加成功";
		
	}
	/**
	 * 查询待支付订单
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<TrOrders> queryWaitPay(Integer pageNo) {
	     return merZHDao.queryWaitPay(pageNo);
		}
	/**
	 * 确定支付时，产生交易明细表
	 * @param aid，账户ID
	 * @param mid，用户ID
	 * @param toAid，交易对方账户ID
	 * @param toUid，交易对方用户ID
	 * @param payAmt，结算金额
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List genTrOrders(String aid,String mid,String toAid,String toUid,String payAmt) throws Exception{
		return merZHDao.genTrOrders(aid,mid,toAid,toUid,payAmt);
	}

	/**
	 * 付款
	 * @param mid，用户ID
	 * @param oid，系统订单号
	 * @param oid2，对方系统订单号
	 * @param pwd，支付密码
	 * @param state，状态4-订单取消
	 * @return
	 */
	
	
	public String pay4One(String mid,String oid,String pwd){
		String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";}
		try{
		    merZHDao.pay4One(oid);
		}catch(Exception e){
			e.printStackTrace();
			return "取消成功";
		}
		return "操作成功";
	}
	/**
	 * 取消订单
	 * @param mid，用户ID
	 * @param oid，系统订单号
	 * @param oid2，对方系统订单号
	 * @param pwd，支付密码
	 * @param state，状态4-订单取消
	 * @return
	 */
	public String cancel4One(String mid,String oid,String oid2,String pwd){
		String oldPass = merZHDao.getPass(mid);
		if (!oldPass.equals(pwd)) {
			return "操作失败，密码错误！";
		}
		int[] row=merZHDao.cancel4One(oid,oid2);
		if(row!=null&&row.length==2){
			return "取消成功";
		}else{
			return "操作异常";
		}
	}
	/**
	 * 查询商户的客户企业名称
	 * @return
	 * @throws Exception
	 */
	public List<Object> queryMerCustomerInfoList() throws Exception{
		List<Object> infoList=new ArrayList<Object>();
		infoList.add(merZHDao.queryMerCustomerCom());
		//infoList.add(merZHDao.getB2bPayGates());
		infoList.add(RYFMapUtil.getProvMap());
		return infoList;
	}
	/**
	 * 根据客户企业查询账号
	 * @param uid
	 * @return
	 */
	public Map<String, String> queryBkAccByCusId(String uid){
		return merZHDao.queryBkAccByCusId(uid);
	}
	/**
	 * 修改订单状态为银行处理中
	 * @param ordId
	 * @param phoneNo
	 * @return
	 */
	public String submitB2bOrder(String ordId,String phoneNo){
		if(Ryt.empty(ordId))return "订单号不能为空！";
		int row=merZHDao.updateTrOrder(ordId);
		String flag=AppParam.SUCCESS_FLAG;
		if(!Ryt.empty(phoneNo)){
			EmaySMS.sendSMS(new String[]{phoneNo}, "尊敬的企业用户，您的订单已经生成，订单号为："+ordId+"，请您尽快去企业网银后台支付!");
//			SMSUtil.send(phoneNo,"尊敬的企业用户，您的订单已经生成，订单号为："+ordId+"，请您尽快去企业网银后台支付!");
		}
		if(row!=1){
			return "更新订单状态失败！";
		}else{
			return flag;
		}
	}
	/**
	 * 付款到客户的银行账户
	 * 
	 * @param cusId  对方企业ID
	 * @param accNo 对方银行帐号
	 * @param payAmt 支付金额
	 * @param phoneNo 手机号
	 * @param remark 备注
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ?> getB2bPayParam(String cusId, String accNo,
			String payAmt, String phoneNo, String remark,String cusName,String yh,Integer sf,String bkname, String... ordIds)
			throws Exception {
		String gate=BankNoUtil.getPubDFMiddletMap().get(yh);
		Map paramMap=new HashMap();
		String ordId = Ryt.genOidBySysTime();
		boolean flag = false;
		if (ordIds.length > 0) {
			ordId = ordIds[0];
			flag = true;
		}
		paramMap.put("remark", remark);
		paramMap.put("phoneNo", phoneNo);
		paramMap.put("payAmt", payAmt);
		paramMap.put("accNo", accNo);
//		UserBkAcc userBkAcc = merZHDao.getUserBkAccObj(cusId, accNo);// 收款方
		String aid = String.valueOf(merZHDao.getLoginUserMid());
		AccInfos accInfos = merZHDao.queryFKUID(aid);// 付款方
		if(accInfos.getState()!=1) 
			throw new Exception("该账户非正常状态！");
		
		// 计算手续费
//		String feeMode = accInfos.getDaifuFeeMode();
		FeeCalcMode feeCalMode=merZHDao.getFeeModeByGate(aid,yh);
		Integer gid=feeCalMode.getGid();
		String feeMode=feeCalMode.getCalcMode();
		String transFee = ChargeMode.reckon(feeMode, payAmt);
		paramMap.put("transFee", transFee);
		paramMap.put("transAmt", Double.parseDouble(transFee)+Double.parseDouble(payAmt));
		long saveTransAmt = Ryt.mul100toInt(payAmt)
				+ Ryt.mul100toInt(transFee);// 实际交易金额（乘100后的）
		Integer saveTransFee = (int) Ryt.mul100toInt(transFee);
		long savePayAmt = Ryt.mul100toInt(payAmt);
		int date = DateUtil.today();
		int time = DateUtil.getCurrentUTCSeconds();
		int t=DateUtil.now();//hhmmss
//		Integer gate = userBkAcc.getGate();
//		Map paramMap = GenRequestParamUtil.getParamKeyMap(saveTransAmt,
//				saveTransFee, gate, ordId);
//		paramMap.put("userBkAcc", userBkAcc);
		paramMap.put("ordId", ordId);
		TrOrders trOrders = new TrOrders();
		trOrders.setSysDate(date);
		trOrders.setSysTime(time);
		trOrders.setOid(ordId);
		trOrders.setUid(aid);// 使用结算账户付款
		trOrders.setAid(accInfos.getAid());
		trOrders.setAname(accInfos.getAname());
		trOrders.setInitTime(DateUtil.getIntDateTime());
		trOrders.setGate(Integer.parseInt(yh.trim()));
		trOrders.setPtype((short) 5);
		trOrders.setTransAmt(savePayAmt);
		trOrders.setTransFee(saveTransFee);
		trOrders.setPayAmt(saveTransAmt);
//		trOrders.setToUid(userBkAcc.getUid());
		trOrders.setToUid(cusId);
//		trOrders.setToAid(userBkAcc.getId() + "");
		trOrders.setAccName(accInfos.getAname());
		trOrders.setAccNo(accInfos.getAid());
//		trOrders.setToAccName(userBkAcc.getAccName());
		trOrders.setToAccName(cusName);
		trOrders.setToAccNo(accNo);
//		trOrders.setToBkName(userBkAcc.getBkName());
//		trOrders.setToBkNo(userBkAcc.getBkNo());
		trOrders.setToBkName(bkname);
		trOrders.setToBkNo(gate);
		trOrders.setToProvId(sf);
		trOrders.setState((short) 0);
//		trOrders.setRemark(remark);
		trOrders.setPriv(remark);
		trOrders.setSmsMobiles(phoneNo);
		int row;
		int row2 = 0;
		if (flag) {
			row = merZHDao.updateOrder(trOrders);
			row2=transactionDao.updateHlog(trOrders.getOid(),accInfos.getAid(),String.valueOf(trOrders.getPayAmt()),Long.parseLong(""+trOrders.getTransAmt()),trOrders.getTransFee(), accNo,cusName,gate,yh,gid);
		} else {
			row = merZHDao.payToOtherCom(trOrders);
			 row2=transactionDao.insertHlog("10191", String.valueOf(date), accInfos.getUid(), trOrders.getOid(), ""+String.valueOf(trOrders.getTransAmt()), "12", date,
					date, t, Integer.parseInt(yh),null,accNo,cusName,gate,saveTransFee+"",gid,String.valueOf(trOrders.getPayAmt()));// 12 交易类型  对公代付
		}

		if (row != 1 && row2 !=1) {
			throw new Exception("记录提交的订单异常！请重试，或联系客服人员");
		}
		return paramMap;

	}
	/**
	 * 生成帐号
	 * @param mid
	 * @return 生成的帐号 账户ID生产策略（行业（3位数字）+区号（3位)+4位随机数）
	 */
	public String createNumber(String mid){
		List<Minfo>list= merZHDao.createZHINFO(mid);
		if(list.size()<=0){
			return "系统错误";
		}
		String provId=list.get(0).getProvId()==null?"":list.get(0).getProvId()+"";
		
		String randStr=createRandomStr();
		String strNo="";
		strNo=provId+randStr;
		Map<String, String> map=new HashMap<String, String>();
		List<AccInfos> lists=merZHDao.selectAllAid();
		for (int i = 0; i < lists.size(); i++) {
			map.put(lists.get(i).getAid(),lists.get(i).getAid());
		}while (true) {
			if(map.get(strNo.trim())!=null){
			strNo="";
			randStr=createRandomStr();
			strNo=provId+randStr;
			}else{
				break;
			}
		}
		return strNo;
	}
	/**
	 * 产生6位随机数
	 * @return 4位随机数
	 */
	private String createRandomStr(){
		String randStr="";
		Random random=new Random();
		for (int i = 0; i < 4; i++) {
			int r=random.nextInt(10);
			randStr=randStr+r;
		}
		return randStr;
	}
	/**
	 * 检查用户余额支付条件是否通过
	 * @param transAmt 应付金额
	 * @return
	 * @throws Exception 
	 */
	public String checkBalance(double transAmt,String payPwd) throws Exception{
		if(Ryt.empty(payPwd)){
			return "支付密码不能为空！";
		}
		 String mid=merZHDao.getLoginUserMid();
		 List<AccInfos> accinf=queryJSZHYE(mid);
		 String paypass=merZHDao.getPass();//得到用户支付密码
		 if(accinf.size()<=0)
			 return "账户信息异常或账户不存在,无法进行支付！";
		 if(!paypass.equals(payPwd)){
			 return "支付密码错误，请重试！";
		 }
		 if(accinf.get(0).getState()!=1)
			 return "该账户非正常状态，无法使用支付功能！";
		 if(accinf.get(0).getBalance()<Ryt.mul100toInt(String.valueOf(transAmt)))
			 return "账户余额不足，请充值后重试！";
		 else
			 return "ok";
	}
	/**
	 * 使用余额支付
	 * @param payAmt 支付金额
	 * @param oid 订单号
	 * @param payPwd 支付密码
	 * @return
	 * @throws Exception 
	 */
	public String payForBalances(String[] data,String payPwd) throws Exception{
		String payAmt=data[7];
		String oid=data[1];
		if (!checkBalance(Double.parseDouble(payAmt), payPwd).equals("ok")) {
			return "支付异常，请勿非法操作！";
		}
		String uid = merZHDao.getLoginUserMid();
		List<AccInfos> accinfo=queryJSZHYE(uid);
	  	    if(accinfo.size()<=0){
	  	    	return "没有结算账户！";
	  	    }
	  	if(!Ryt.isPay(accinfo.get(0).getBalance(),Ryt.mul100toInt(String.valueOf(payAmt))))throw new Exception("该比交易金额大于单笔限制金额");
		boolean res=saveSinglePayOrder(data);
		int[] i = merZHDao.payForBalance(oid, String.valueOf(payAmt), uid);
		if ( res && i != null ) {
			merZHDao.saveOperLog("对公单笔付款", "订单号 "+oid+" 余额支付 受理成功");
			return "ok";
		}
		merZHDao.saveOperLog("对公单笔付款", "订单号 "+oid+" 支付失败");
		return "支付失败！";
	}
	/**
	 * 线下支付
	 * @param oderid 订单号
	 * @return 
	 */
	public String payForOffline(String oderid,String feeamt){
		if(merZHDao.stateNomal(oderid)==0){
			int flag=merZHDao.payForOffline(oderid,Ryt.mul100(feeamt));
			if(flag!=1){
				merZHDao.saveOperLog("对公单笔付款", "订单号 "+oderid+"  支付失败");
				return "操作失败，请重试！";
			}
			merZHDao.saveOperLog("对公单笔付款", "订单号 "+oderid+" 线下支付 受理成功");
			return "ok";
		}
		merZHDao.saveOperLog("对公单笔付款", "订单号 "+oderid+"  支付失败");
		return "操作失败，请检查订单状态！";
		
	}
	/**
	 * 账户充值（线下支付  提交信息） yang.yaofeng
	 * @param aid 
	 * @param payAmt
	 * @param gate
	 * @return
	 * @throws Exception
	 */
	public String payForAccount(String aid,String payAmt,String ordId) throws Exception{
		if (!Ryt.isMoney(payAmt)) {
			throw new Exception("金额格式错误！");
		}
		AccInfos accInfos = merZHDao.queryFKUID(merZHDao.getLoginUserMid()+"");// 付款方
		if(accInfos.getState()!=1) 
			return "该账户非正常状态！";
//		if(accInfos==null){
//			throw new Exception("该商户没有交易账户！");
//		};
		// 计算手续费
	//		String feeMode = accInfos.getCzFeeMode();
//		String transFee = ChargeMode.reckon(feeMode, payAmt);
		String transFee ="0";
		long saveTransAmt = Ryt.mul100toInt(payAmt)
				+ Ryt.mul100toInt(transFee);// 实际交易金额（乘100后的）
		Integer saveTransFee = (int) Ryt.mul100toInt(transFee);
		long savePayAmt = Ryt.mul100toInt(payAmt);
		if (merZHDao.stateNomals(ordId) <= 0) {
			int []row = merZHDao.payForAccount(ordId, aid, accInfos.getAname(), saveTransAmt,
					saveTransFee, savePayAmt, "");
			if (row.length != 2) {
				throw new Exception("提交的订单异常！请重试，或联系客服人员");
			} else {
				merZHDao.saveOperLog("账户充值", "订单号"+ordId+" 充值金额"+payAmt+" 手续费"+transFee+"-订单受理成功");
				return "ok";
			}
		} else {
			merZHDao.saveOperLog("账户充值", "订单号"+ordId+" 充值金额"+payAmt+" 手续费"+transFee+"-订单受理失败");
			return "支付失败，请检查订单状态！";
		}
	}
	/**
	 * 账户充值返回手续费等信息（线下）
	 * @param payAmt
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> offLineForAccount(String payAmt)throws Exception{
		if (!Ryt.isMoney(payAmt)) {
			throw new Exception("金额格式错误！");
		}
		Map<String, String> map = new HashMap<String, String>();
		// 计算手续费
//		String feeMode = accinfos.get(0).getCzFeeMode();// 手续费公式
//		String transFee = ChargeMode.reckon(feeMode, payAmt);
		String transFee ="0";
		String ordId = Ryt.genOidBySysTime();
		map.put("ordId", ordId);// 订单号
		map.put("transFee", transFee);// 手续费
		map.put("tranPayAmt",
				(Double.parseDouble(payAmt) + Double.parseDouble(transFee))
						+ "");// 应付金额
		map.put("payAmt", payAmt);// 应付金额
		return map;
	}
		/**
	 * 查询指定商户的结算账户
	 * @param uid 指定商户ID
	 * @return
	 */
	public String getSettlementAcc(String uid){
		return merZHDao.getSettlementAcc(uid);
	}
	
	/**
	 * 查询指定商户的交易账户
	 * @param uid
	 * @return
	 */
	public String getTransAcc(String uid){
		return merZHDao.getTransAcc(uid);
	}
	
	/**
	 * 根据订单号查询订单信息
	 * @param oid
	 * @return
	 */
	public  List<OrderInfos> getOrderInfoByOid(String oid,int flag)throws Exception{
		String name="";
		String last="";
		if(flag==1){
			name="trans_flow";
			last=" and ptype=0";
		}else if(flag==2){
			name="oid";
		}else{
			return null;
		}
		List<OrderInfos> list=merZHDao.getOrderInfoByOid(oid,name,last);
		return list;
	}
	
	/****
	 * 账户收支明细 yang.yaofeng 2013-04-25
	 * @param pageNo
	 * @param uid
	 * @param bdate
	 * @param edate
	 * @param oid
	 * @return
	 */
	public CurrentPage<AccSeqs> queryZHSZMX(Integer pageNo,Integer ptype,Integer bdate,Integer edate,String oid) {
	     return merZHDao.queryZHSZMX(pageNo,ptype,bdate,edate,oid);
	
	}
	
	/***
	 * 计算手续费  查询银行账户
	 * @param aid
	 * @param transAmt
	 * @return
	 */
	public String queryTXFee(String aid,double transAmt){
		AccInfos accInfos = merZHDao.queryFK(aid);// 付款方
		// 计算手续费
		String feeMode = accInfos.getTixianFeeMode();
		String transFee = ChargeMode.reckon(feeMode,String.valueOf(transAmt));
		Minfo m=merZHDao.getMinfoById(accInfos.getUid());
		return transFee+","+m.getBankName()+","+m.getBankAcct();
	}
	/**
	 * 查询结算账户余额
	 * @param mid 商户号（用户ID）
	 * @return
	 */
	public List<AccInfos> queryJSZHYE(String mid){
		return merZHDao.queryJSZHYE(mid);
		
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
	 * 商户端查看凭证
	 * @param file
	 * @param fileName
	 * @return map(key,value) 凭证的路径 审核意见  实际的充值金额  实际的交易金额
	 */
	public Map<String, String> getPICPath_New(String file,String oid,String fileName){
		TrOrders tr=(TrOrders)merZHDao.query("select  * from tr_orders where oid='"+oid+"' limit 0, 1", TrOrders.class).get(0);
		Map<String, String> map=new HashMap<String, String>();
		map.put("filePath","/public/cert/"+fileName);
		map.put("option", tr.getAuditRemark());
		map.put("rechargeAmt", String.valueOf(tr.getRechargeAmt()));
		return map;
	}
	public String queryRYTBankNo(){
		return merZHDao.queryRYTBankNo()==null?"":merZHDao.queryRYTBankNo();
	}
	
	//下载流水查询（新增）yang.yaofeng 2013-04-25
		public FileTransfer downloadLS_SZ(Integer ptype,Integer bdate,Integer edate,String oid) throws Exception {
//			  CurrentPage<AccSeqs> acListPage=adminZHDao.queryLS_SZ(-1,uid,aid,bdate,edate,oid,ptype,mstate);
			CurrentPage<AccSeqs> acListPage=merZHDao.queryZHSZMX(-1,ptype,bdate,edate,oid);
			List<AccSeqs> acList=acListPage.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			long totleAmt = 0;
			long totleTrAmt = 0;
			long totleTrFee = 0;
			list.add("序号,用户,账户号,系统订单号,系统批次号,交易时间,交易金额(元),手续费(元),实际金额(元),收入/支出,交易类型,账户余额(元)".split(","));
			int i = 0;
//			
			for (AccSeqs a : acList) {
			String[] str = {
					(i + 1) + "",
					a.getUid(),
					a.getAid(),
					a.getOid() + "",
					(Ryt.empty(a.getTrans_flow()) ? "" : a.getTrans_flow())
							+ "",
					Ryt.getNormalTime(String.valueOf(a.getInit_time())) + "",
					Ryt.empty(a.getTrAmt()+"") ? "" : Ryt.div100(a.getTrAmt()),
					Ryt.empty(a.getTrFee()+"") ? "" : Ryt.div100(a.getTrFee()),
					Ryt.empty(a.getAmt() + "") ? "" : Ryt.div100(a.getAmt()),
					a.getRecPay() == 0 ? "收入" : "支出",
					AppParam.h_z_ptype.get(Integer.parseInt(Short.toString(a
							.getPtype()))) + "", Ryt.div100(a.getAllBalance()) };
				totleTrAmt += Ryt.empty(a.getTrAmt()+"")? 0 : a.getTrAmt();
				totleTrFee += Ryt.empty(a.getTrFee()+"")? 0 : a.getTrFee();
				totleAmt += Ryt.empty(a.getAmt()+"")? 0 : a.getAmt();
				i += 1;
				list.add(str);
			}
			String[] str = { "总计:" + i + "条记录","","","","", "",Ryt.div100(totleTrAmt)+"",Ryt.div100(totleTrFee)+"", Ryt.div100(totleAmt)+"","" };
			list.add(str);
			String filename = "MERSHMXLOG_" + DateUtil.today() + ".xls";
			String name = "收支明细";
			return new DownloadFile().downloadXLSFileBase(list, filename, name);
	       }
		
		/*****
		 *  单笔对公付款 参数验证  计算手续费 
		 * @param aid
		 * @param oid
		 * @param transAmt
		 * @param toBkNo
		 * @return map {oid,transAmt,transFee,payAmt,gid}  其中  金额单位为元 0.01
		 * @throws Exception
		 */
		public Map<String, String> checkSinglePay(String aid,String oid,String transAmt,String toBkNo)throws Exception{
			Map<String, String> map=new HashMap<String, String>();
			AccInfos accInfos=merZHDao.queryToAid(aid);
			long transAmt2=Ryt.mul100toInt(transAmt);
			if(accInfos.getState()!=1)throw new Exception("该账户非正常状态"); 
			/*if(5000000<= Long.parseLong(Ryt.mul100(payAmt)))throw new Exception("该笔交易金额大于单笔限制金额");*/
			//计算手续费  实际支付金额
			FeeCalcMode feeCalcMode=merZHDao.getFeeModeByGate(merZHDao.getLoginUserMid(), toBkNo);
			//交易手续费  格式 (以分为单位)
			long transFee=(long) Ryt.mul100toInt(ChargeMode.reckon(feeCalcMode.getCalcMode(),Ryt.div100(transAmt2)));
			long payAmt=transAmt2+transFee;//实际支付金额 格式 (以分为单位)
			long balance=accInfos.getBalance();//账户可用余额 格式 (以分为单位)
			if(oid == null ||oid.equals("")) oid=Ryt.genOidBySysTime();/*订单号---上送订单号 该订单号不变返回， 未上送订单号 生成订单号 返回*/
			if(accInfos.getBalance()< payAmt )throw new Exception("该笔交易金额大于该账户可用余额");
			String toBkNo2=BankNoUtil.getPubDFMiddletMap().get(toBkNo);
			map.put("oid", oid);
			map.put("transAmt", Ryt.div100(transAmt2));
			map.put("transFee", Ryt.div100(transFee));
			map.put("payAmt", Ryt.div100(payAmt));
			map.put("gid", String.valueOf(feeCalcMode.getGid()));
			map.put("toBkNo", toBkNo2);
			return map;
		}
		/****
		 * 保存单笔支付订单
		 * @param data[]
		 * @return
		 */
		public boolean saveSinglePayOrder(String[] data)throws Exception{
			String aid=data[0];
			String ordId=data[1];
			String toAccNo=data[2];
			String toAccName=data[3];
			String toBkName=data[4];
			String toBkNo=data[5];
			String toProvId=data[6];
			String payAmt=data[7];
			String transAmt=data[8];
			String transFee=data[9];
			String priv=data[10];
			String gid=data[11];
			String gate=data[12];
			String smsMobile=data[13];
			String cusAid=data[14];//收款方账户号
			String[] bkInfo=BankNoUtil.getBankNo(toBkNo, toBkName, gate, Integer.parseInt(toProvId), Integer.parseInt(gid));
			toBkNo=bkInfo[0];
			toBkName=bkInfo[1];
			AccInfos accInfos = merZHDao.queryFKUID(aid);// 付款方
			TrOrders trOrders = new TrOrders();
			int date = DateUtil.today();
			int time= DateUtil.getCurrentUTCSeconds();
			trOrders.setSysDate(date);
			trOrders.setSysTime(time);
			trOrders.setOid(ordId);
			trOrders.setUid(aid);// 使用结算账户付款
			trOrders.setAid(accInfos.getAid());
			trOrders.setAname(accInfos.getAname());
			trOrders.setInitTime(DateUtil.getIntDateTime());
			trOrders.setGate(Integer.parseInt(gate));
			trOrders.setPtype((short) 5);
			trOrders.setTransAmt(Ryt.mul100toInt(transAmt));
			trOrders.setTransFee(Integer.parseInt(Ryt.mul100(transFee)));
			trOrders.setPayAmt(Ryt.mul100toInt(payAmt));
			trOrders.setToUid(cusAid);
			trOrders.setAccName(accInfos.getAname());
			trOrders.setAccNo(accInfos.getAid());
			trOrders.setToAccName(toAccName);
			trOrders.setToAccNo(toAccNo);
			trOrders.setToBkName(toBkName);
			trOrders.setToBkNo(toBkNo);
			trOrders.setToProvId(Integer.parseInt(toProvId));
			trOrders.setState((short) 0);
			trOrders.setPriv(priv);
			trOrders.setSmsMobiles(smsMobile);
			
			int row;
			int row2 = 0;
			row = merZHDao.payToOtherCom(trOrders);
			row2=transactionDao.insertHlog("10191", String.valueOf(date), accInfos.getUid(), trOrders.getOid(), ""+String.valueOf(trOrders.getTransAmt()), "12", date,
					date, DateUtil.now(), Integer.parseInt(gate),null,toAccNo,toAccName,toBkNo,Ryt.mul100(transFee),Integer.parseInt(gid),String.valueOf(trOrders.getPayAmt()));// 12 交易类型  对公代付

			if (row != 1 && row2 !=1) {
				throw new Exception("记录提交的订单异常！请重试，或联系客服人员");
			}
			return true;
		}
		
}
