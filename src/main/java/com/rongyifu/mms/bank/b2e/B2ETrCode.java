package com.rongyifu.mms.bank.b2e;

public class B2ETrCode {
	
	public final static String BOC_BK_NO = "104100000004";
	
	public final static String BOC_SC_OK = "B001";
	
	public final static String BOCOMM_SC_OK = "0000";
	
	public final static String CMB_SC_OK="0";
	
	public final static String ABC_SC_OK="0000";
	
	public final static String ICBC_SC_OK="0";
	
	public final static String PSBC_SC_OK="00";
	
//	2-交易成功
//	6-付款失败
	public static final int PAY_SUCC = 34;
	public static final int PAY_Fail = 35;

	public static final int BOC_GID = 40001;
	public static final int BOCOMM_GID = 40000;
	public static final int BOCOMM_GID_BE=40002;
	public static final int CMB_GID=40003;
	public static final int ABC_GID=40004;
	public static final int ICBC_GID=40005;
	public static final int SJ_GID=40006;
	public static final int SJ_DFYZC_GID=40007; //盛京代付预转存接口
	public static final int PSBC_GID=40008;//邮储
	
	
	
	//不要赋值 0
	// 签到
	public static final int SIGN = 1;

	// 查询账户余额
	public static final int QUERY_ACC_BALANCE = 2;

	// 查询订单状态
	public static final int QUERY_ORDER_STATE = 3;

	// 查询多个订单信息
	public static final int QUERY_ORDERS_INFO = 4;

	// 转账汇款
	public static final int PAY_TO_OTHER = 5;

	// 代发业务
	public static final int DAI_FA = 6;
	
	//转账交易结果查询
	public static final int QUERY_PAY_TO_OTHER_RESULT=7;
	
	//账户信息查询
	public static final int QUERY_ACCOUNT=8;
	
	//跨行支付交易
	public static final int PAY_TO_OTHERBK=9;
	
	//当日交易明细查询
	public static final int QUERY_TODAYTRANS=10;
	
	//历史交易信息明细
	public static final int QUERY_HISTORICAL_TRADE=11;
	//查询代发交易代码
	public static final int QUERY_DAIFA_TRADE=12;
	
	//查询代扣交易代码
	public static final int QUERY_DAIKOU_TRADE=13;
	//代发工资
	public static final int DAI_FA_GONGZI=14;
	//代扣
	public static final int DAI_KOU=15;
	//查询交易概要信息
	public static final int QUERY_TRADING_PROFILE=16;
	//查询账户的交易信息
	public static final int QUERY_ACCOUNT_TRADE=17;
	//落地处理
	public static final int  ABC_QUERY_STATE_L=18;
	//异常交易
	public static final int ABC_QUERY_STATE_E=19;
	//更新密钥
	public static final int PSBC_UPDATE_KEY=20;

}
