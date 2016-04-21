package com.rongyifu.mms.common;

public class Constant {

	public static final int UMP_AUTH_TYPE = 3;//联动优势发起类型
	public static final int UMP_ALLOW_REFUND_COUNT = 1;//联动优势允许退款的次数
	public static final int ADMIN_MID = 1;//管理员商户号
	public static final int TRANS_SUCCES = 2;//交易成功的状态
	
	public static final int NOT_ALLOW_REFUND_FLAG = 0;//不允许退款的标示
	
	public static final int FEE_RATIO = 100 ;
	
	//YCK系统充值的地址(T+0额度恢复用到)
	public static final String YCKURL="http://192.168.20.118:4002/zhxt/en_trans";
	
	//YCK系统充值时用到的私钥
	public static final String YCK_CZ_PRIVATE_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIAeOznBNdBeo3KrxJSm3MAZu1C22g2gAIUGS+vuusMqJ28x3mTaFNobZp+5SMQJIFHve3OhStxNrLeNNv/f4W7gUvYwGzszoC/BrQi6FIuEPy4guGMlk+fpM7AXLCfy+roo1BRM4PFYTUmqC4pcRY0D8IREMIMR6nqdyew4BA+hAgMBAAECgYAKb2vvhlc1w5+YhXkbioPMecwSBPK5zWB9dUfVzboUj2hq3Cr7F/A+26B+Pf85SG0Df3gUsPyB+SmqQaxLJxcbbIfRwrcipVTM+EcuZL3h0R1J7suX8URT7JF/pwFJGjo5DTgKMoGAxIzTLq3qI0mD9XXrhLDGohs6g6XtNd1HCQJBAOSXY2Dum03HdCrSDHbXbaVlbVa15K2sk/xx7KFtW89KNtbPqe5yeH9rjdZJH7fMJcvDlbX83uUeY3vO0qWRqcMCQQCPetDv70TZ4jbKKcvUJ5rNJiXhb5xy5yy/gx6m57+ZBvfKDUSNQMFC43kX5zWLEraXVH+NIe3kYyDfxxJ7+qbLAkBGtxaFPAtLSnKDdKtqcEZ5LiFe+7IckBDPvaaIYb5PKBRC68cT/tGMCYe9TK3FPCBNItpDjFCf7IC2IktD8QXlAkAJOlXryJC+Dq2FbRC98VokbZqsGBiQz0tyecVj6K3K8sbHCPlquEp1udmU7vrjme2CaB3X/uGk+bcojU4kCSfnAkApl0K5C45dMDunNn1i27adLWavv+JShDZxr7d89o9fG6pl3hB9msoTNXQI3NltH4mSoCy+1h3+5Z2kTqblZHSW";
	
	//YCK系统充值时用到的公钥
	public static final String YCK_CZ_PUB_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAHjs5wTXQXqNyq8SUptzAGbtQttoNoACFBkvr7rrDKidvMd5k2hTaG2afuUjECSBR73tzoUrcTay3jTb/3+Fu4FL2MBs7M6Avwa0IuhSLhD8uILhjJZPn6TOwFywn8vq6KNQUTODxWE1JqguKXEWNA/CERDCDEep6ncnsOAQPoQIDAQAB";
	/**
	 * 当天交易表
	 */
	public static final String TLOG = "tlog";
	/**
	 * 历史交易表
	 */
	public static final String HLOG = "hlog";
	/**
	 * 交易失败备份表
	 */
	public static final String BLOG = "blog";
	/**
	 * tr_orders表
	 */
	public static final String TR_ORDERS = "tr_orders";
	/**
	 * 结算表
	 */
	public static final String FEE_LIQ_BATH = "fee_liq_bath";
	/**
	 * 退款表
	 */
	public static final String REFUND_LOG = "refund_log";
	/**
	 * 调账表
	 */
	public static final String ADJUST_ACCOUNT = "adjust_account";
	/**
	 * 权限字段长度
	 */
	public static final int AUTHLEN=200;	
	/**
	 * ewp代付MD5加密key
	 */
	public static final String EWP_DF_MD5_KEY = ParamCache.getStrParamByName("CRYPTO_KEY");
	/**
	 * ewp代付标识
	 */
	public static final String EWP_DF_FLAG = "auto_df";
	/**
	 * ewp代付受理成功
	 */
	public static final String EWP_DF_SUCCESS = "0";
	/**
	 * 版本号
	 */
	public static final int VERSION = 10;
	
	/**
	 * 订单状态
	 *
	 */
	public class PayState{
		/**
		 * 订单初始化状态
		 */
		public static final int INIT = 0;
		/**
		 * 代支付
		 */
		public static final int WAIT_PAY = 1;
		/**
		 * 支付成功
		 */
		public static final int SUCCESS = 2;
		/**
		 * 支付失败
		 */
		public static final int FAILURE = 3;
		
		/**
		 * 请求银行失败
		 */
		public static final int REQ_FAILURE=4;
		/**
		 * 撤销
		 */
		public static final int CANCEL=5;
	}
	
	
	/**
	 * 代付交易状态
	 *
	 */
	public class DaiFuTransState {
		/**
		 * 订单生成
		 */
		public static final int ORDER_FORM  = 0;
		
		/**
		 * 网银充值-充值中
		 */
		public static final int RECHARGE_PROCESSING  = 11;
		/**
		 * 充值成功
		 */
		public static final int RECHARGE_SUCCESS = 12;
		/**
		 * 充值失败
		 */
		public static final int RECHARGE_FAILURE = 13;
		
		/**
		 * 提现处理中
		 */
		public static final int EXTRACTION_CASH_PROCESSING = 21;
		/**
		 * 提现成功
		 */
		public static final int EXTRACTION_CASH_SUCCESS = 22;
		/**
		 * 提现失败
		 */
		public static final int EXTRACTION_CASH_FAILURE = 23;
		
		/**
		 * 线下充值支付-充值中
		 */
		public static final int OFFLINE_RECHARGE_PROCESSING = 31;
		/**
		 * 线下充值支付-凭证审核中
		 */
		public static final int OFFLINE_RECHARGE_CERT_AUDIT_PROCESSIING = 32;
		/**
		 * 付款处理中
		 */
		public static final int PAY_PROCESSING = 33;
		/**
		 * 付款成功
		 */
		public static final int PAY_SUCCESS = 34;
		/**
		 * 付款失败
		 */
		public static final int PAY_FAILURE = 35;
		/***
		 * 请求银行失败
		 */
		public static final int REQ_FAILURE=36;
	}
	
	/**
	 * 代付经办、审核状态 pstate
	 */
	public class DaifuPstate{
		/**
		 * 经办审核初始状态
		 */
		public static final int AUDIT_INIT_STATUS = 0;
		/**
		 * 经办成功
		 */
		public static final int HANDLING_SUCCESS = 1;
		/**
		 * 经办失败
		 */
		public static final int HANDLING_FAILURE = 2;
		/**
		 * 审核成功
		 */
		public static final int AUDIT_SUCCESS = 3;
		/**
		 * 审核失败
		 */
		public static final int AUDIT_FAILURE = 4;
		/**
		 * 凭证审核成功
		 */
		public static final int CERT_AUDIT_SUCCESS = 5;
		/**
		 * 凭证审核失败
		 */
		public static final int CERT_AUDIT_FAILURE = 6;
	}
	/**
	 * TLOG交易类型
	 * @author yang.yaofeng
	 *
	 */
	public class TlogTransType{
		public static final int INIT = 0;//初始化
		
		public static final int WEB_PAY = 1;//网银支付
		
		public static final int CONSUME_CARD_PAY = 2;//消费充值卡支付
		
		public static final int CARD_PAY = 3;//信用卡支付
		
		public static final int REFUND_TRADE = 4;//退款业务
		
		public static final int VALUE_ADDED_SERVICe= 5;//增值业务
		
		public static final int VOICE_PAY= 6;//语音支付
		
		public static final int B2B_WEB_PAY= 7;//B2B企业网银支付
		
		public static final int WAP_PAY= 8;//WAP支付
		
		public static final int POS_PAY= 10;//POS支付
		
		public static final int PAYMENT_FOR_PRIVATE= 11;//对私代付
		
		public static final int PAYMENT_FOR_PUBLIC= 12;//对公代付
		
		public static final int B2B_RECHARGE= 13;//B2B充值
		
		public static final int OFFLINE_RECHARGE= 14;//线下充值
		
		public static final int PROXY_PAYABLE_PRIVATE= 15;//对私代扣
				
		public static final int WITHDRAW_DEPOSIT_PUBLIC= 16;//对公提现
		
		public static final int WITHDRAW_DEPOSIT_PRIVATE= 17;//对私提现
		
		public static final int QUICK_PAY= 18;//快捷支付
		
    }
	/**
	 * 代付交易类型
	 *
	 */
	public class DaiFuTransType{
		/**
		 * 账户充值
		 */
		public static final int ACCOUNT_RECHARGE = 0;
		/**
		 * 账户提现
		 */
		public static final int ACCOUNT_EXTRACTION_CASH = 1;
		/**
		 * 充值撤销
		 */
		public static final int RECHARGE_CANCEL = 2;
		/**
		 * 付款到电银账户
		 */
		public static final int PAY_TO_CHINAEBI = 3;
		/**
		 * 从电银账户收款
		 */
		public static final int FROM_CHINAEBI_COLLECTION = 4;
		/**
		 * 付款到企业银行账户
		 */
		public static final int PAY_TO_ENTERPRISE = 5;
		/**
		 * 从银行账户收款
		 */
		public static final int FROM_BANK_COLLECTION = 6;
		/**
		 * 付款到个人银行账户
		 */
		public static final int PAY_TO_PERSON = 7;
		/**
		 * 自动结算划款
		 */
		public static final int AUTO_SETTLEMENT_PAY = 8;	
		/**
		 * 对私代扣
		 */
		public static final int PROXY_PAYABLE_PRIVATE = 9;
	}
	
	/**
	 *  账户收支标识
	 *
	 */
	public class RecPay {
		/**
		 * 收支标识: 增加
		 */
		public static final int INCREASE = 0;	
		/**
		 * 收支标识: 减少
		 */
		public static final int REDUCE = 1;
	}
	
	/**
	 * 获取代付交易类型名称
	 * @param transType
	 * @return
	 */
	public static String getDaifuTransTypeName(int transType){
		String remark = "";
		if(transType == DaiFuTransType.PAY_TO_PERSON)
			remark = "对私代付" ;
		else if(transType == DaiFuTransType.PAY_TO_ENTERPRISE)
			remark = "对公代付";
		
		return remark;
	}
	
	/**
	 * 商户类型
	 *
	 */
	public class MerType{
		/**
		 * 商户类型：企业
		 */
		public static final String ENTERPRISE = "0";
		/**
		 * 商户类型：个人
		 */
		public static final String PERSON = "1";
		/**
		 * 商户类型：集团
		 */
		public static final String GROUP = "2";
	}
	
	/**
	 * 退款类型
	 *
	 */
	public class RefundType{
		/**
		 * 人工经办
		 */
		public static final int MANUAL = 0;
		/**
		 * 联机退款
		 */
		public static final int ONLINE = 1;
		/**
		 * 联机退款 转 人工经办
		 */
		public static final int ONLINE_TO_MANUAL = 2;
	}
	
	/****
	 * 数据来源类型
	 *
	 */
	public  class DataSource{
		/***
		 * 常规渠道
		 */
		public static final int TYPE_GENERALROUTE=0;
		/***
		 * 代付代扣接口
		 */
		public static final int TYPE_DFDKINTERFACE=1;
		/***
		 * HTML5
		 */
		public static final int TYPE_HTML5=2;
		/***
		 * POS同步
		 */
		public static final int TYPE_POSSYNC=3;
		/***
		 * VAS同步
		 */
		public static final int TYPE_VASSYNC=4;
		/***
		 * 管理平台发起的代付代扣
		 */
		public static final int TYPE_DFDKMMS=5;
		/***
		 * SK转账
		 */
		public static final int TYPE_ZHSK=6;
		/***
		 * 自动代付（对私代付）
		 */
		public static final int TYPE_AUTODF=7;
		/***
		 * 自动结算（对公代付）
		 */
		public static final int TYPE_AUTOSETTLE=8;
		/***
		 * FTP批量代付
		 */
		public static final int TYPE_FTPDF=9;
		/***
		 * T+0
		 */
		public static final int TYPE_T0DF=10;
	}
	
	/***
	 *手工代付 申请 审核状态
	 */
	public class SgDfTstat{
		/**
		 * 手工代付 默认状态
		 */
		public static final int TSTAT_DEFAULT=0;
		/***
		 * 手工代付 准备状态
		 */
		public static final int TSTAT_INIT=1;
		/***
		 * 手工代付申请成功
		 */
		public static final int TSTAT_SQSUC=2;
		/***
		 * 审核成功
		 */
		public static final int TSTAT_SHSUC=3;
		/***
		 * 审核失败
		 */
		public static final int TSTAT_SHFAIL=4;
		/***
		 * 申请失败
		 */
		public static final int TSTAT_SQFAIL=5;
	}
	
	/**
	 * 订单是否需要结算
	 *
	 */
	public class IsLiq{
		/**
		 * 需要结算
		 */
		public static final int YES = 0;
		/**
		 * 不结算
		 */
		public static final int NO = 1;
	}
	/**
	 * 系统全局常量 
	 *
	 */
	public class GlobalParams{
		/**
		 * 变量名：ewp外网地址
		 */
		public static final String EWP_PATH = "EWP_PATH";
		/**
		 * 变量名：ewp内网地址
		 */
		public static final String EWP_INTERNAL_URL = "EWP_INTERNAL_URL";
		/**
		 * 变量名：管理平台IP
		 */
		public static final String TMS_IP = "TMS_IP";
		/**
		 * 变量名：退款结果通知地址
		 */
		public static final String REFUND_SYNC_URL = "REFUND_SYNC_URL";
		
	}
}