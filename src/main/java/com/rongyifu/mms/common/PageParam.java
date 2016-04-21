package com.rongyifu.mms.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;


import org.directwebremoting.WebContextFactory;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.utils.RYFMapUtil;
import com.rongyifu.mms.web.WebConstants;

public class PageParam {

	public static List<Map<Integer, String>> getMerDetailQuery(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());
		aList.add(AppParam.tlog_tstat);
		aList.add(AppParam.HLOG_MER_TYPE);
		return aList;
	}
	
	//普通商户单笔查询
	public static List<Map<Integer, String>> getMerSpeciaQuery(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(AppParam.tlog_bk_chk);//银行对帐标志
		//银行应答标志
		return aList;
	}
	//手机支付页面
	public static List<Map<Integer, String>> getMerPhonePay(String mid){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(AppParam.tlog_tstat);//交易状态
		aList.add(new MerInfoDao().getAllopersMap(mid));//操作员
		return aList;
	}
	
//	//退款查询页面
//	public static List<Map<Integer, String>> getRefundQuery(int loginmid){
//		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
//		aList.add(RYFMap.getGateMap());//银行
//		if(loginmid == 1 ){
//			aList.add(AppParam.refund_log_stat_query);//退款状态(管理员)
//			aList.add(CacheUtil.getMinfoMap());//商户
//		}else{
//			aList.add(AppParam.refund_log_stat);//退款状态(商户)
//		}
//		return aList;
//	}
	//调单手工提交
	//当天交易查询
	public static List<Map<Integer, String>> getLostOrde(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		aList.add(obj.getMerMap());//商户
		aList.add(RYFMapUtil.getGateMap());//银行
		aList.add(AppParam.authorType);//发起类型     auth_type
		return aList;
	}
	
	//当天交易、明细查询管理员页面初始化
	public static List<Map<Integer, String>> initAdminQueryPage(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());//银行
		aList.add(AppParam.tlog_tstat);//交易状态
		aList.add(AppParam.tlog_type);//交易类型
		RYFMapUtil obj = RYFMapUtil.getInstance();
		aList.add(obj.getMerMap());//商户
		aList.add(AppParam.authorType);//发起类型     auth_type
		aList.add(AppParam.bkFlagType);
		aList.add(AppParam.tlog_bk_chk);//m_bk_chks;
		return aList;
	}
	//当天交易、明细查询商户页面初始化
	public static List<Map<Integer, String>> initMerQueryPage(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());//银行
		aList.add(AppParam.tlog_tstat);//交易状态
		aList.add(AppParam.HLOG_MER_TYPE);//交易类型(商户)
		return aList;
	}
	//当天交易、明细查询商户页面初始化
	public static List<String> initLoginInfo(){
		LoginUser user = getLoginUser();
		List<String> aList = new ArrayList<String>();
		aList.add(user.getMid()+"");//商户号
		aList.add(user.getAbbrev());//商户简称
		aList.add(user.getOperId()+"");//商户简称
		return aList;
	}
	
	private static LoginUser getLoginUser() {
		HttpSession session = WebContextFactory.get().getSession(false);
		LoginUser user = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
		return user;
	}
	
	//单笔查询页面初始化
	public List<Map<Integer, String>> initAdminQueryAHlogPage(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		aList.add(obj.getMerMap());//商户
		aList.add(RYFMapUtil.getGateMap());//银行
		aList.add(AppParam.tlog_tstat);//交易状态
		aList.add(AppParam.tlog_type);//交易类型
		aList.add(AppParam.tlog_bk_chk);//m_bk_chks;
		aList.add(AppParam.bkFlagType);
		return aList;
	}
	
	//原始日志查询页面初始化
	public List<Map<Integer, String>> initQueryOrigPage(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		aList.add(obj.getMerMap());//商户
		aList.add(AppParam.tlog_type);//交易类型
		return aList;
	}
	
	//汇总查询页面初始化
	public List<Map<Integer, String>> initQueryCollect(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		RYFMapUtil obj = RYFMapUtil.getInstance();
		aList.add(obj.getMerMap());//商户
		aList.add(RYFMapUtil.getGateMap());//银行
		return aList;
	}
	
	
	//退款页面初始化
	public List<Map<Integer, String>> initRefund(String role){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());//银行
		if(role.equals("admin") ){
			aList.add(AppParam.refund_log_stat);//退款状态(管理员)
			RYFMapUtil obj = RYFMapUtil.getInstance();
			aList.add(obj.getMerMap());//商户
		}else{
			aList.add(AppParam.refund_log_stat_query);//退款状态(商户)
		}
		return aList;
	}
	
	public List<Map<Integer, String>> initSel(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());//银行
		Map<Integer, String> mType = RypCommon.getTlogType();
		mType.remove(0);
		aList.add(mType);//交易类型包括退款
		
//		Map<Integer, String> mTstat = AppParam.tlog_tstat; //交易状态
		Map<Integer, String> mTstat = new HashMap<Integer,String>(); //交易状态
		mTstat.putAll(AppParam.tlog_tstat); //交易状态
		mTstat.remove(0);
		mTstat.remove(1);
		aList.add(mTstat);
		return aList;
	}
	
	
	//商户信息增加页面
	public List<Map<Integer, String>> initMerInfo(String action){
		
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		if(Ryt.empty(action)){
			return aList;
		}
		if(action.equals("add")){
			aList.add(RYFMapUtil.getProvMap());//地区
			aList.add(AppParam.minfo_liq_period);//结算周期
			aList.add(RYFMapUtil.getMerTradeType());// 所属行业
			//aList.add(AppParam.category);//商户类别
		}
		
		return aList;
	}
	
	public List<Map<Integer, String>> initEditGate(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(AppParam.trans_mode_String);//交易类型
		aList.add(AppParam.authorType);//发起类型     auth_type
		return aList;
	}
	
	public List<Map<Integer, String>> initAdminStatePtype(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(AppParam.h_zz_state);
		aList.add(AppParam.h_z_ptype);
		aList.add(AppParam.h_acc_state);
		aList.add(AppParam.h_p_state);
		return aList;
	}
	
	
	
	public List<Map<?, String>> initQueryMinfo(){
		List<Map<?, String>> aList = new ArrayList<Map<?, String>>();
		aList.add(RYFMapUtil.getProvMap());
		aList.add(AppParam.minfo_liq_period);
		aList.add(AppParam.liqType);
		aList.add(RYFMapUtil.getMerTradeType());
		aList.add(RYFMapUtil.getIdType());
		return aList;
	}
	
	//单笔查询页面初始化
	public List<Map<Integer, String>> initAdminIncomeSheet(){
		List<Map<Integer, String>> aList = new ArrayList<Map<Integer, String>>();
		aList.add(RYFMapUtil.getGateMap());//银行
		aList.add(AppParam.tlog_type);//交易类型
		return aList;
	}

}
