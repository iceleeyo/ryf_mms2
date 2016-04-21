package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.AccInfos;
import com.rongyifu.mms.bean.AccSeqs;
import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.PerInfos;
import com.rongyifu.mms.bean.TrOrders;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.AdminZHDao;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RecordLiveAccount;

public class AdminZHService {
	private AdminZHDao adminZHDao=new AdminZHDao();
	//查询账户信息
	public CurrentPage<AccInfos> queryZHXX(Integer pageNo,String uid,Integer mstate){
		return adminZHDao.queryZHXX(pageNo,uid,mstate);
		
	}
	

	//查询账户风险配置信息
	public CurrentPage<AccInfos> queryFXPZ(Integer pageNo,String uid,Integer state,Integer mstate){
		
		return adminZHDao.queryFXPZ(pageNo,uid,state,mstate);
	}
	//查询单个风险信息
	public List<Map<String,Object>> getOneAid(String uid,String aid){
		return adminZHDao.getOneAid(uid,aid);
	}
	//修改风险配置信息
	public String eidtConfig(String aid,Integer state,Integer accMonthCount,long accMonthAmt,
			Integer dkMonthCount,long dkMonthAmt) {
		try {
			adminZHDao.eidtConfig(aid,state,accMonthCount,accMonthAmt,
					dkMonthCount,dkMonthAmt);
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		adminZHDao.saveOperLog("修改风险配置", "修改"+aid+"商户风险配置");
		return "ok";
	}
	public String eidtConfigSettlement(String aid,String daifuFeeMode) {
		try {
			adminZHDao.eidtConfigSettlement(aid,daifuFeeMode);
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		return "ok";
	}
	
	
	//关闭余额
	public String closeYE(String aid){
		int row=adminZHDao.closeYE(aid);
		if(row==1){
			adminZHDao.saveOperLog("账户信息维护", "商户"+aid+"余额支付功能修改为关闭状态");
			return "操作成功，余额支付功能修改为关闭状态";
		}else{
			return "操作异常";
		}
	}
	//关闭账户
	public String closeZH(String aid){
		
		int row=adminZHDao.closeZH(aid);
		if(row==1){
			adminZHDao.saveOperLog("账户信息维护", "商户"+aid+"账户为关闭状态");
			return "操作成功，账户为关闭状态";
		}else{
			return "操作异常";
		}
	}
	
	//查询明细
	public CurrentPage<TrOrders> queryMX(Integer pageNo,String uid, String aid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid) {
     return adminZHDao.queryMX(pageNo,uid,ptype,bdate,edate,state,oid,"",null);
	}
	
	//查询明细(新修改)
	public CurrentPage<TrOrders> queryZHJYMX(Integer pageNo,String oid,String trans_flow) {
     return adminZHDao.queryZHJYMX(pageNo,oid,trans_flow);
	}
	
	//查询明细 (新修改。)
	public CurrentPage<TrOrders> queryZHJYMX_(Integer pageNo,String uid, Integer ptype,Integer bdate,Integer edate,Integer state,String oid,String transFlow,Integer mstate) {
     return adminZHDao.queryMX(pageNo,uid,ptype,bdate,edate,state,oid,transFlow,mstate);
	}
	//下载明细
	public FileTransfer downloadMX(String uid,Integer ptype,Integer bdate,Integer edate,Integer state,String oid,Integer mstate) throws Exception {
		
		  CurrentPage<TrOrders> TrListPage=adminZHDao.queryMX_W(-1,uid,ptype,bdate,edate,state,oid,mstate);
		  List<TrOrders> TrList=TrListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleTransAmt = 0;
		long totlePayAmt = 0;
		list.add("序号,用户,账户号,账户名称,系统订单号,结算金额,交易金额,手续费,系统日期,系统时间,交易状态,交易类型,收款方户名,收款方账户,订单时间,融易付流水号".split(","));
		int i = 0;
		try {
			
			for (TrOrders t : TrList) {
				String[] str = { (i + 1) + "",t.getUid(),t.getAid(),t.getAname() , t.getOid(),Ryt.div100(t.getTransAmt())+"",Ryt.div100(t.getPayAmt()),Ryt.div100(t.getTransFee()),t.getSysDate()+"",
						Ryt.getStringTime(t.getSysTime())+"",AppParam.h_acc_state.get(Integer.parseInt(t.getState()+"")),
						AppParam.h_z_ptype.get(Integer.parseInt(t.getPtype()+""))+"",(t.getToAccName()==null||t.getToAccName().equals("null"))?"":(t.getToAccName()),
								(t.getToAccNo()==null||t.getToAccNo().equals("null"))?"":(""+t.getToAccNo()+""),
										Ryt.getNormalTime(t.getInitTime()+"")+"",t.getTseq()==null?"":t.getTseq()+""};
				totleTransAmt += t.getTransAmt();
				totlePayAmt += t.getPayAmt();
				i += 1;
				list.add(str);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String[] str = { "总计:" + i + "条记录", "","","","", Ryt.div100(totleTransAmt)+"", Ryt.div100(totlePayAmt)+"" ,"","","", "", "", "", "" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "明细表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
        }
	//流水查询
	public CurrentPage<AccSeqs> queryLS(Integer pageNo,String uid,String aid,Integer bdate,Integer edate, String tseq,Integer mstate, Integer category) {

 	     return adminZHDao.queryLS(pageNo,uid,aid,bdate,edate,tseq,mstate,category);
	}
	/****
	 * 账户收支明细
	 * @param pageNo
	 * @param uid
	 * @param aid
	 * @param bdate
	 * @param edate
	 * @param oid
	 * @return
	 */
	public CurrentPage<AccSeqs> queryZHSZMX(Integer pageNo,String uid,String aid,Integer ptype,Integer bdate,Integer edate,String oid,Integer mstate) {
	     return adminZHDao.queryZHSZMX(pageNo,ptype,uid,aid,bdate,edate,oid,mstate);
	
	}
	
	//下载流水查询
	public FileTransfer downloadLS(String uid,String aid,Integer bdate,Integer edate,String tseq,Integer mstate, Integer category) throws Exception {
		CurrentPage<AccSeqs> acListPage=adminZHDao.queryLS(-1,uid,aid,bdate,edate,tseq,mstate,category);
		List<AccSeqs> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleAmt = 0;
		long totleTrAmt = 0;
		long totleTrFee = 0;
		list.add("序号,用户,账户号,商户简称,系统日期,系统时间,收支标识,交易金额(元),交易手续费(元),变动金额(元),可用余额(元),余额(元),流水号,流水来源表,简短说明".split(","));
		int i = 0;
		for (AccSeqs a : acList) {
			String[] str = { (i + 1) + "",a.getUid(),a.getAid(), String.valueOf(a.getAbbrev()),a.getTrDate()+"",Ryt.getStringTime(a.getTrTime())+"",
					AppParam.h_rec_pay.get(Integer.parseInt(a.getRecPay()+"")),
					Ryt.div100(a.getTrAmt()),Ryt.div100(a.getTrFee()),
					Ryt.div100(a.getAmt()),Ryt.div100(a.getBalance()),Ryt.div100(a.getAllBalance()+a.getBalance()),a.getTbId()+"",a.getTbName(),a.getRemark()};
			totleTrAmt += a.getTrAmt();
			totleTrFee += a.getTrFee();
			totleAmt += a.getAmt();
			i += 1;
			list.add(str);
			
		}
		String[] str = { "总计:" + i + "条记录","","","","", "","",Ryt.div100(totleTrAmt)+"",Ryt.div100(totleTrFee)+"", Ryt.div100(totleAmt)+"","","","","","" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "流水表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
       }
	//查询提现处理
	public CurrentPage<TrOrders> queryTXCL(Integer pageNo,String uid,int state,Integer mstate) {
	     return adminZHDao.queryTXCL(pageNo,uid,state,mstate);
		}
	
	//下载提现处理
	public FileTransfer downloadTXCL(String uid,Integer mstate) throws Exception {
		try {
		CurrentPage<TrOrders> acListPage=adminZHDao.queryTXCL2(-1,uid,mstate);
		List<TrOrders> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totlePayAmt = 0;
		long totleFee=0;
		list.add("序号,系统订单号,订单时间,用户,账户,账户名称,状态,开户银行名称,开户银行,开户银行卡号,提现金额,手续费".split(","));
		int i = 0;
		for (TrOrders t : acList) {
			String[] str = { (i + 1) + "",t.getOid(),Ryt.getNormalTime(t.getInitTime()+"")+"",t.getUid(),
					t.getAid(),t.getAname(),AppParam.h_acc_state.get(Integer.parseInt(t.getState()+"")),
					t.getName(),t.getBankName(),t.getBankAcct(),Ryt.div100(t.getPayAmt()),Ryt.div100(t.getTransFee())
					};
			totlePayAmt += t.getPayAmt();
			totleFee += t.getTransFee();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录","","","","", "","","", "","", Ryt.div100(totlePayAmt)+"",Ryt.div100(totleFee)+""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name ="提现处理表";
		
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
     }
	//更新经办审核处理
	public String updateTXCL(List<TrOrders> list, String option) {
		int[] i = adminZHDao.updateTXCL(list, option);
		boolean result=false;
		for(int i2:i){
			if(i2>=0)result=true;
		}
		if (result) {
			adminZHDao.saveOperLog("提现审核操作", "提现审核成功操作完成");
			return "提现审核操作完成";
		}
		return "提现审核失败";
	}
	//更新提现审核成功
	public String updateSuccessTXCL(List<TrOrders> list,String option){
		int []flag=null;
		try {
			flag=adminZHDao.updateSuccessTXCL(list, option);
			if(flag!=null&&flag.length==list.size()){
				adminZHDao.saveOperLog("提现经办操作", "提现经办成功操作");
				return "提现审核成功操作完成";
			}
			else{
				return "操作异常";
			}
		} catch (Exception e) {
			return "操作异常";
		}
	}
	//提现审核失败
	public String updateFailTXCL(List<TrOrders> list,String option){
		int []flag=null;
		try {
			flag=adminZHDao.updateFailTXCL(list, option);
			if(flag!=null&&flag.length==list.size()*3){
				adminZHDao.saveOperLog("提现审核操作", "提现审核失败操作完成");
				return "提现审核失败操作完成";
			}
			else{
				return "操作异常";
			}
		} catch (Exception e) {
			return "操作异常";
		}
		 
		 
			
		 
	}
	
	//提现经办失败
		public String updateFailTXCL_JB(List<TrOrders> list,String option){
			   int[] flag = null;
			try {
				flag = adminZHDao.updateFailTXCL_JB(list,option);
			} catch (Exception e) {
				e.printStackTrace();
			}
			   if(flag!=null&&flag.length==list.size()*3){
				   adminZHDao.saveOperLog("提现经办", "提现经办失败操作完成");
				   return "提现审核失败操作完成";
			   }
			   else{
				   return "操作异常";
			   }
		}
	//查询个人信息
	public CurrentPage<PerInfos> queryGR(Integer pageNo,String uid, String idNo) {
	     return adminZHDao.queryGR(pageNo,uid,idNo);
		}
	
	//查询个人详细资料
	public PerInfos queryGRByUid(String uid) {
		return adminZHDao.queryGRByUid(uid);
	}
	public boolean checkButtonAuth(Integer au){
		String auth=adminZHDao.getLoginUser().getAuth();
		return MenuService.hasThisAuth(auth, au);
	}
	/**
	 * 根据uid查询账户号（不包含结算账户）
	 * @param uid
	 * @return
	 */
	public Map<String, String> getZHMapByUid(String uid) {
		return adminZHDao.getZHMapByUid(uid);
	}
	/**
	 * 根据uid查询账户号（包含结算账户）
	 * @param uid
	 * @return
	 */
	public Map<String, String> getZHMapByUid2(String uid) {
		return adminZHDao.getZHMapByUid2(uid);
	}
	
	/**
	 * 根据UID查询结算帐户
	 * @param uid
	 * @return
	 */
	public Map<String, String> getJSZHByUid(String uid){
		return adminZHDao.getJSZHByUid(uid);
	}
	/**
	 * 查询单笔划款
	 * @param pageNo
	 * @param uid
	 * @param aid
	 * @param bdate
	 * @param edate
	 * @param state
	 * @param to_acc_no
	 * @param other_id
	 * @param amount_num
	 * @param ym 月份
	 * @return
	 */
	public CurrentPage<TrOrders> querySingleTransMoney(Integer pageNo,String uid,String aid,Integer bdate,
			Integer edate,Integer state,String toAccNo,String otherId,long amountNum,String ym,Integer mstate){
		return adminZHDao.querySingleTransMoney(pageNo,uid,aid,bdate,edate,state,toAccNo,otherId,amountNum,ym,mstate);
	}
	
	/**
	 * 查询银企直连网关维护 信息
	 * @param pageNo
	 * @return
	 */
	public CurrentPage<B2EGate> queryB2EGate(Integer pageNo){
		return adminZHDao.queryB2EGate(pageNo);
	}
	
	public String addB2EGate(String gid,String name,String ncUrl,Integer provId,String accNo,String accName,
			String termid,String corpNo,String userNo){
			
			if(adminZHDao.addB2EGate(gid,name,ncUrl,provId,accNo,accName,
					termid,corpNo,userNo)==1)return "操作成功";
			else
			return "操作失败";
	}
	/**
	 * 查询单条银企直连网关信息
	 * @param gid 网关号
	 * @return
	 */
	public B2EGate getOneB2EGate(int gid) {
		return adminZHDao.getOneB2EGate(gid);
	}
	/**
	 * 修改单条银企直连网关信息
	 * @param aGid 网关号
	 * @param aName 网关名称
	 * @param aNcUrl 前置机地址
	 * @param aBkNo 银行行号
	 * @param aProvId 开户省份
	 * @param aAccNo 开户帐号
	 * @param aAccName 开户名称
	 * @param aTermid 企业前置机ip
	 * @param aCorpNo 企业客户编码
	 * @param aUserNo 企业操作员代码
	 * @param aUserPwd 登录密码
	 * @param aCodeType 字符编码
	 * @param aBusiNo 协议编号
	 * @return
	 */
	public String editOneB2EGate(String aGid,String aName,String aNcUrl,String aBkNo,Integer aProvId,String aAccNo,String aAccName,
			String aTermid,String aCorpNo,String aUserNo,String aUserPwd,String aCodeType,String aBusiNo){
			adminZHDao.saveOperLog("修改银企直连网关", "修改网关为"+aGid+" 网关名称"+aName);
		if(adminZHDao.editOneB2EGate(aGid,aName,aNcUrl,aBkNo,aProvId,aAccNo,aAccName,
				aTermid,aCorpNo,aUserNo,aUserPwd,aCodeType,aBusiNo)==1)return "修改成功";
		else
		return "修改失败";
	}
	
	public String query4Remark(String oid){
		return adminZHDao.query4Remark(oid);
	}
	public String resetPassWord(String mid,String pwd){
		if("".equals(mid.trim())){
			return "请输入商户号！";
		}
		if("".equals(pwd.trim())){
			return "请输入密码！";
		}
		if(!Ryt.isWordAndNo(pwd)){
			return "密码只能是数字和字母!";
		}
		String []sqls=new String[2];
		int today=DateUtil.today();
		int definedDate=Integer.parseInt(DateUtil.returnDefinedDate(7));  //7天以后的日期
		String title="支付密码修改通知！--"+today;
		String content="尊敬的用户，您好。您的支付密码已重置，请您修改支付密码！";
		//如果选择的是多个操作员
		StringBuffer sql=new StringBuffer("insert into mms_notice(begin_date,end_date,title,content,oper_id,mid) values(");
		sql.append(today).append(","+definedDate).append(",'").append(title+"','").append(content+"',").append(1+",").append(mid).append(")");
		sqls[0]=sql.toString();
		int i=adminZHDao.hasMid(mid);
		//判断商户是否存在
		if(i<=0){
			return "商户不存在!";
		}
		int []result=adminZHDao.resetPassWord(mid, pwd,sqls);
		if(result[0]==1){
			adminZHDao.saveOperLog("支付密码重置", "商户"+mid+"密码重置成功");
			return "ok";
		}
		adminZHDao.saveOperLog("支付密码重置", "商户"+mid+"密码重置失败");
		return "密码重置失败，请重试！";
	}
	
	/***
	 * 生成日期的下拉框
	 * @return
	 */
	public String createMonth(){
		int year=Integer.parseInt(DateUtil.getNowDateTime().substring(0, 4));
		int month=Integer.parseInt(DateUtil.getNowDateTime().substring(4, 6));
		String a="<select id='YM' >";
		for(int i=0;i<12;i++){
			if(month==0){
				month=12;
				year=year-1;
			}
			a+="<option value='"+year+""+month+"'>"+year+(month<10?"0":"")+month+"  "+"</option>";
			month--;
		}
		a=a+"</select>";
		return a;
	}
	/**
	 * 管理后台查询商户余额
	 * @param pageNo 当前页数
	 * @param mid 查询的商户号
	 * @return
	 * @throws Exception
	 */
	public CurrentPage<AccInfos> querySHYE(Integer pageNo,String mid,Integer mstate)throws Exception{
		if(mid.trim().equals("")){
			mid="";
		}
		return adminZHDao.querySHYE(pageNo,mid,mstate);
	}
	
	//下载流水查询
	public FileTransfer downloadLS_SZ(String uid,Integer ptype,Integer bdate,Integer edate,String aid,String oid,Integer mstate) throws Exception {
		  CurrentPage<AccSeqs> acListPage=adminZHDao.queryLS_SZ(-1,uid,aid,bdate,edate,oid,ptype,mstate);
		  List<AccSeqs> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleAmt = 0;
		long totleTrAmt = 0;
		long totleTrFee = 0;
		list.add("序号,用户,账户号,系统订单号,系统批次号,交易时间,交易金额(元),手续费(元),实际金额(元),收入/支出,交易类型,账户余额(元)".split(","));
		int i = 0;
//		
		for (AccSeqs a : acList) {
			String[] str = { (i + 1) + "",a.getUid(),a.getAid(),a.getOid()+"",(a.getTrans_flow()==null||a.getTrans_flow()=="null"||a.getTrans_flow()=="")?"":a.getTrans_flow()+"",
					/*Ryt.getStringTime((int)a.getTrDate())*/Ryt.getNormalTime(String.valueOf(a.getInit_time()))
					+"",
					Ryt.div100(a.getTrAmt()),
							Ryt.div100(a.getTrFee()),Ryt.div100(a.getAmt()),
							a.getRecPay()==0?"收入":"支出",AppParam.h_z_ptype.get(Integer.parseInt(Short.toString(a.getPtype())))+"",Ryt.div100(a.getAllBalance())};
			totleTrAmt += a.getTrAmt();
			totleTrFee += a.getTrFee();
			totleAmt += a.getAmt();
			i += 1;
			list.add(str);
			
		}
		String[] str = { "总计:" + i + "条记录","","","","", "",Ryt.div100(totleTrAmt)+"",Ryt.div100(totleTrFee)+"", Ryt.div100(totleAmt)+"","" };
		list.add(str);
		String filename = "MERSHMXLOG_" + DateUtil.today() + ".xls";
		String name = "收支明细";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
       }
	//交易账户流水查询
	public CurrentPage<AccSeqs> queryJYZHLS(Integer pageNo,String uid,String aid,Integer bdate,Integer edate,Integer mstate ) {
	     return adminZHDao.queryJYZHLS(pageNo,uid,aid,bdate,edate,mstate);
		}
	//下载交易账户流水查询
	public FileTransfer downloadJYZHLS(String uid,String aid,Integer bdate,Integer edate,Integer mstate) throws Exception {
		
		  CurrentPage<AccSeqs> acListPage=adminZHDao.queryJYZHLS(-1,uid,aid,bdate,edate,mstate);
		  List<AccSeqs> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totleAmt = 0;
		long totleTrAmt = 0;
		long totleTrFee = 0;
		list.add("序号,用户,账户号,系统日期,系统时间,收支标识,交易金额,交易手续费,变动金额,余额,电银流水号,流水来源表,简短说明".split(","));
		int i = 0;
		for (AccSeqs a : acList) {
			String[] str = { (i + 1) + "",a.getUid(),a.getAid(),a.getTrDate()+"",Ryt.getStringTime(a.getTrTime())+"",
					AppParam.h_rec_pay.get(Integer.parseInt(a.getRecPay()+"")),
					Ryt.div100(a.getTrAmt()),Ryt.div100(a.getTrFee()),
					Ryt.div100(a.getAmt()),Ryt.div100(a.getAllBalance()),a.getTbId()+"",a.getTbName(),a.getRemark()};
			totleTrAmt += a.getTrAmt();
			totleTrFee += a.getTrFee();
			totleAmt += a.getAmt();
			i += 1;
			list.add(str);
			
		}
		String[] str = { "总计:" + i + "条记录","","","","", "",Ryt.div100(totleTrAmt)+"",Ryt.div100(totleTrFee)+"", Ryt.div100(totleAmt)+"","" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xls";
		String name = "流水表";
		return new DownloadFile().downloadXLSFileBase(list, filename, name);
       }
	
	//查询提现审核
	public CurrentPage<TrOrders> queryTXCL_SH(Integer pageNo,String uid,int state,Integer mstate) {
	     return adminZHDao.queryTXCL_SH(pageNo,uid,state,mstate);
		}
	//=========================================yyf
	 /**
	  * 查询线下充值审核数据
	  * @param pageNo 页数
	  * @param uid 商户id
	  * @param state 状态
	  * @return
	  */
	public CurrentPage<TrOrders> queryXXCZSH(Integer pageNo,String uid,Integer mstate) {
	     return adminZHDao.queryXXCZSH(pageNo,uid,mstate);
		}
	//下载线下充值审核数据
	public FileTransfer downloadXXCZSH(String uid,Integer mstate) throws Exception {
		try {
		CurrentPage<TrOrders> acListPage=adminZHDao.queryDLXXCZSH(-1,uid,mstate);
		List<TrOrders> acList=acListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		long totlePayAmt = 0;
		list.add("序号,系统订单号,订单时间,用户,账户,账户名称,状态,充值金额".split(","));
		int i = 0;
		for (TrOrders t : acList) {
			String[] str = { (i + 1) + "",t.getOid(),Ryt.getNormalTime(t.getInitTime()+"")+"",t.getUid(),
					t.getAid(),t.getAname(),AppParam.h_zz_state.get(Integer.parseInt(t.getState()+"")),
					Ryt.div100(t.getPayAmt())
					};
			totlePayAmt += t.getPayAmt();
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录","","","","", "","", Ryt.div100(totlePayAmt)+""};
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name ="线下充值审核表";
		
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	/**
	 * 更新线下充值审核
	 * @param list
	 * @param option
	 * @return
	 */
		public String updateXXCZSH(List<TrOrders> list,String option){
			option=Ryt.sql(option);
			if(list.size()==0){
				return "请选择一条记录!";
			}
			int operId=adminZHDao.getLoginUser().getOperId();
			int date = DateUtil.today();
			int time=DateUtil.getCurrentUTCSeconds();
			List<String> sqls=new ArrayList<String>();
			try {
				for (int i = 0; i < list.size(); i++) {
					String oid = list.get(i).getOid();
					String uid =list.get(i).getUid();
					String aid =list.get(i).getAid();
					long transAmt=list.get(i).getTransAmt();
					int transFee=list.get(i).getTransFee();
					long payAmt=list.get(i).getPayAmt();
					StringBuffer sqlBuff=new StringBuffer("update tr_orders set sys_date =");
					sqlBuff.append(date).append(",sys_time =").append(time).append(",state=12,");
					sqlBuff.append("pstate=3");
					sqlBuff.append(",audit_oper =").append(operId).append(",");
					sqlBuff.append("audit_remark =").append(Ryt.addQuotes(Ryt.sql(option))).append(",");
					sqlBuff.append("audit_date =").append(date).append(",");
					sqlBuff.append("audit_time =").append(time);
					sqlBuff.append(" where oid= '").append(Ryt.sql(oid)).append("'");
					String[] orderInfo =adminZHDao.queryOrderByOid(uid, oid);
					String tableName = orderInfo[0];	// 表名
					String tseq = orderInfo[1];			// 电银流水号
					StringBuffer hlogSql=new StringBuffer("update "+tableName+" set tstat=");
					hlogSql.append(Constant.PayState.SUCCESS);
					hlogSql.append(" where oid=");
					hlogSql.append(Ryt.addQuotes(oid));
					
					AccSeqs params = new AccSeqs();
					params.setUid(uid);
					params.setAid(aid);
					params.setTrAmt(payAmt);
					params.setTrFee(transFee);
					params.setAmt(transAmt);
					params.setRecPay((short)Constant.RecPay.INCREASE);
					params.setTbName(tableName);
					params.setTbId(tseq);
					params.setRemark("线下充值");
					List<String> lists= RecordLiveAccount.recordAccSeqsForOfflineCz(params);
					
					sqls.add(sqlBuff.toString());
					sqls.add(hlogSql.toString());
					sqls.addAll(lists);
					//sqls[list.size()*2-(i+1)]=sql;
			}
					String[] sqlsList = sqls.toArray(new String[sqls.size()]);
					int[]i= adminZHDao.updateXXCZSH(sqlsList);
					
					// 记录日志
					Map<String, String> logMap = LogUtil.createParamsMap();
					int k = 0;
					for(String item : sqlsList){
						logMap.put("sql[" + k + "]", item);
						k++;
					}
					LogUtil.printInfoLog("AdminZHService", "updateXXCZSH", "线下充值", logMap);
					
					if(i!=null&&i.length==sqlsList.length){
						adminZHDao.saveOperLog("充值审核操作", "线下充值审核成功操作完成");
						return "线下充值审核操作完成！";
					}
					adminZHDao.saveOperLog("充值审核操作", "线下充值审核成功操作异常");
				return "操作失败，请稍后重试！";
			} catch (Exception e) {
				e.printStackTrace();
				return "操作异常！";
			}
		}
		/**
		 * 线下充值审核失败
		 * @param list
		 * @param option
		 * @return
		 */
		public String updateFailXXCZ(List<TrOrders> list,String option){
			 int[] flag=null;
			try {
				flag = adminZHDao.updateFailXXCZSH(list,option);
			} catch (Exception e) {
				LogUtil.printErrorLog("AdminZHService", "updateFailXXCZ", "操作异常"+e.getMessage());
				return "操作异常";
			}
			 if(flag!=null&&flag.length==list.size()*2){
				 
				 adminZHDao.saveOperLog("充值审核操作", "线下充值审核失败操作完成");
				 return "提现审核失败操作完成!";
			 }
			 else{
				 adminZHDao.saveOperLog("充值审核操作", "线下充值审核失败操作异常");				 
				 return "操作异常";
			 }
		}
		

}
