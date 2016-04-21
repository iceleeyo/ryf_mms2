package com.rongyifu.mms.common;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.rongyifu.mms.bean.Minfo;


public class AppParam {
	
	//商户的交易类型hlog
	public static Map<Integer, String> HLOG_MER_TYPE = new TreeMap<Integer, String>();
	public static HashMap<Integer,String> bkFlagType = new HashMap<Integer,String>();
	  
	
	// minfo liq_period 结算周期
	public static Map<Integer, String> minfo_liq_period = new TreeMap<Integer, String>();
	// tlog/hlog type 交易类型
	public static Map<Integer, String> tlog_type = new TreeMap<Integer, String>();
	// tlog/hlog tstat交易状态
	public static Map<Integer, String> tlog_tstat = new TreeMap<Integer, String>();
	// tlog/hlog bk_chk 银行对帐标志
	public static Map<Integer, String> tlog_bk_chk = new TreeMap<Integer, String>();
	// account表type 操作类型
	public static Map<Integer, String> account_type = new TreeMap<Integer, String>();
	// refund_log stat 退款状态
	public static Map<Integer, String> refund_log_stat = new TreeMap<Integer, String>();
	// refund_log stat 退款状态 退款申请页面
	public static Map<Integer, String> refund_log_stat_query = new TreeMap<Integer, String>();
	public static Integer[] refund_log_stat_query_sx = { 5, 1, 7, 2, 3, 6 };
	// refund_log stat 退款状态 退款经办页面
	public static Map<Integer, String> refund_log_stat_motion = new TreeMap<Integer, String>();
	// refund_log stat 退款状态 退款审核页面
	public static Map<Integer, String> refund_log_stat_verify = new TreeMap<Integer, String>();
	// 融易贷还款周期refund_type
	public static Map<Integer, String> refund_type = new TreeMap<Integer, String>();
	// punish state 罚金支付状态
	public static Map<Integer, String> punish_state = new TreeMap<Integer, String>();
	//
	public static Map<Integer, String> trans_mode = new TreeMap<Integer, String>();

	public static Map<Integer, String> trans_mode_String = new TreeMap<Integer, String>();
	// 当前正在使用�?
	public static Map<Integer, String> authorType = new TreeMap<Integer, String>();
	//结算方式
	public static Map<Integer, String> liqType = new TreeMap<Integer, String>();
	//状态标志
	public static Map<Integer, String> statFlag = new TreeMap<Integer, String>();
	
	public static Map<Integer, String > auth_type = new TreeMap<Integer, String>();
	//对账状态
	public static Map<Integer,String> check_state=new TreeMap<Integer,String>();
	
	public static Map<Integer, String > h_mer_refund_tstat = new TreeMap<Integer, String>();
	
	
	private static Map<Integer,String> h_merge_bk_1  = new HashMap<Integer, String>();
	//明细交易状态
	public static Map<Integer,String> h_zz_state  = new TreeMap<Integer, String>();
	//账户交易状态
	public static Map<Integer,String> h_acc_state  = new TreeMap<Integer, String>();
	//账户经办审核状态
	public static Map<Integer,String> h_p_state  = new TreeMap<Integer, String>();
	//明细交易类型
	public static Map<Integer,String> h_z_ptype  = new TreeMap<Integer, String>();
	//流水交易状态
    public static Map<Integer,String> h_tr_type = new TreeMap<Integer, String>();
	//流水收支标识
	public static Map<Integer,String> h_rec_pay  = new TreeMap<Integer, String>();
	//cancel_log  cancel_state 撤销状态
	public static Map<Integer,String> cancel_state=new TreeMap<Integer,String>();
	
	public static Minfo DK_PM = null;
	public static final int DK_PM_ID = 1;
	public static final String SUCCESS_FLAG = "ok";
	public static final String AMT_SUM = "amtSum";//系统总交易额
	public static final String SYS_AMT_FEE_SUM = "sysAmtFeeSum";//系统手续费
	public static final String PAY_AMT_SUM = "payAmtSum";//用户实际支付总金额
	public static final String BK_REF_FEE_SUM = "bkRefFee"; //银行退还手续费
	public static final String REF_FEE_SUM = "refFeeSum";//退款总额
	public static final String MER_REF_FEE_SUM = "merRefFeeSum";//退还商户手续费
	public static final String ROW_COUNT = "rowCount";//总行数

	// 银行交易明细银行网关号
	public static final int bank_log_gate = 10002;
	// 存入数据库的手续费倍数
	public static final int ryp_fee_degree = 100;
	// 融易贷服务费存入数据库的手续费倍数
	public static final int fee_degree = 10000;
	// DWR查询每页显示多少条记录
	public static int getPageSize(){
		return ParamCache.getIntParamByName("pageSize");
	}
	// 融易付商户私钥读取地址
	public static final String mid_priky = Ryt.getParameter("MER_PRI_PATH");
	// EWP公钥存储地址
//	public static final String ewp_pubky = Ryt.getParameter("EWP_PUBKEY");
	// BTOB支付接口
	public static final String pay_BTOBurl = Ryt.getParameter("PAT_PATH")+"/pay/trans_entry";
	// 交易状态
	public static final Map<Integer, String> bind_all = new TreeMap<Integer, String>();
	//交易类型
	public static final Map<Integer, String> trans_type = new TreeMap<Integer, String>();
	//Daifu交易类型
	public static final Map<Integer,String> df_transType=new TreeMap<Integer, String>();
	//交行超级网银网关列表
	public static final Map<String,Integer> bocomm_gate=new TreeMap<String, Integer>();
	// 银行手续费
	public static int getBankDegree() {
		return 10000;
	};
	
	
	public static Map<Integer, String> getH_z_ptype() {
		return h_z_ptype;
	}

	static {
		h_zz_state.put(0, "订单生成");
		h_zz_state.put(1, "交易处理中");
		h_zz_state.put(2, "交易成功");
		h_zz_state.put(3, "交易失败");
		h_zz_state.put(4, "订单取消");
		h_zz_state.put(5, "付款处理中");
		h_zz_state.put(6, "付款失败");
		h_zz_state.put(7, "待审批");
		h_zz_state.put(8, "审批不通过");
		h_zz_state.put(9, "代发经办处理中");
		h_zz_state.put(10, "提现失败");
		
		/*网银充值*/
		h_acc_state.put(0, "订单生成");
		h_acc_state.put(11, "充值支付中");
		h_acc_state.put(12, "充值成功");
		h_acc_state.put(13, "充值失败");
		/*提现*/
		h_acc_state.put(21, "提现处理中");
		h_acc_state.put(22, "提现成功");
		h_acc_state.put(23, "提现失败");
		/*线下充值支付*/
		h_acc_state.put(31, "充值支付中");
		h_acc_state.put(32, "凭证审核中");
		h_acc_state.put(33, "付款处理中");
		h_acc_state.put(34, "付款成功");
		h_acc_state.put(35, "付款失败");
		/*线下充值*/
/*		h_acc_state.put(31, "充值支付中");
		h_acc_state.put(32, "凭证审核中");*/
		h_acc_state.put(12, "充值成功");
		h_acc_state.put(13, "充值失败");
		/*经办审核状态*/
		h_p_state.put(1, "经办成功");
		h_p_state.put(2, "经办失败");
		h_p_state.put(3, "审核成功");
		h_p_state.put(4, "审核失败");
		h_p_state.put(5, "凭证审核成功");
		h_p_state.put(6, "凭证审核失败");
		h_z_ptype.put(0, "账户充值");
		h_z_ptype.put(1, "账户提现");
		//h_z_ptype.put(2, "充值撤销");
		h_z_ptype.put(3, "付款到电银账户");
		h_z_ptype.put(4, "从电银账户收款");
		h_z_ptype.put(5, "付款到银行账户");
		h_z_ptype.put(6, "从银行账户收款");
		h_z_ptype.put(7, "付款到个人银行卡");
		h_z_ptype.put(8, "自动结算划款");
		
		h_rec_pay.put(0, "增加");
		h_rec_pay.put(1, "减少");
		
		h_tr_type.put(0, "充值");
		h_tr_type.put(1, "提现");
		h_tr_type.put(2, "提现撤销");
		h_tr_type.put(3, "付款");
		h_tr_type.put(4, "收款");
		
		h_tr_type.put(10, "收支");
		h_tr_type.put(11, "支付手续费");
		h_tr_type.put(12, "退款");
		h_tr_type.put(13, "退款退回手续费");
		h_tr_type.put(14, "调增");
		h_tr_type.put(15, "调减");
		h_tr_type.put(16, "结算");
		
		
		minfo_liq_period.put(1, "每天清算一次");
		minfo_liq_period.put(2, "每周清算一次");
		minfo_liq_period.put(3, "每周清算两次");
		minfo_liq_period.put(4, "每月清算一次");
		minfo_liq_period.put(5, "其它");

	/*	tlog_type.put(0, "初始化");
		tlog_type.put(1, "网银支付");
		tlog_type.put(2, "消费充值卡支付");
		tlog_type.put(3, "信用卡支付");
		//tlog_type.put(4, "退款交易");
		tlog_type.put(5, "增值业务交易");
		tlog_type.put(6, "语音/快捷支付");
		tlog_type.put(7, "BTOB网上");
		tlog_type.put(8, "手机WAP支付");
		tlog_type.put(10, "POS支付");
		tlog_type.put(11, "代付业务");
		*/
		tlog_type.put(0,"初始化");
		tlog_type.put(1,"个人网银支付");
		tlog_type.put(2,"消费充值卡支付");
		tlog_type.put(3,"信用卡支付");
		tlog_type.put(4,"退款交易");
		tlog_type.put(5,"增值业务");
		tlog_type.put(6,"语音支付");
		tlog_type.put(7,"企业网银支付");
		tlog_type.put(8,"手机WAP支付");
		tlog_type.put(10,"POS支付");
		tlog_type.put(11,"对私代付");
		tlog_type.put(12,"对公代付");
		tlog_type.put(13,"B2B充值");
		tlog_type.put(14,"线下充值");
		tlog_type.put(15,"对私代扣");
		tlog_type.put(16,"对公提现");
		tlog_type.put(17,"对私提现");
		tlog_type.put(18,"快捷支付");
		
		
		tlog_tstat.put(0, "初始状态");
		tlog_tstat.put(1, "待支付");
		tlog_tstat.put(2, "交易成功");
		tlog_tstat.put(3, "交易失败");
		tlog_tstat.put(4, "请求银行失败");

		tlog_bk_chk.put(0, "初始状态（未对帐）");
		tlog_bk_chk.put(1, "勾对成功，双方均成功");
		tlog_bk_chk.put(2, "勾对失败，双方金额不符");
		tlog_bk_chk.put(3, "勾对失败，银行为可疑交易");

		account_type.put(1, "支付");
		account_type.put(2, "退款");
		account_type.put(3, "结算");
		account_type.put(4, "手工增加");
		account_type.put(5, "手工减少");

		refund_log_stat.put(1, "商户已提交");
		refund_log_stat.put(2, "退款完成");
		refund_log_stat.put(3, "操作成功");
		refund_log_stat.put(4, "操作失败");
		refund_log_stat.put(5, "商户未确认");
		refund_log_stat.put(6, "撤销退款");
		refund_log_stat.put(7, "商户撤销");

		refund_log_stat_query.put(5, "商户未确认");
		refund_log_stat_query.put(1, "商户已提交");
		refund_log_stat_query.put(7, "商户撤销");
		refund_log_stat_query.put(2, "等待审核");
		refund_log_stat_query.put(3, "退款完成");
		refund_log_stat_query.put(6, "退款失败");
		//refund_log_stat_query.put(4, "退款失败");
		
		
		h_mer_refund_tstat.put(5, "商户申请退款");
		h_mer_refund_tstat.put(1, "商户确认退款");
		h_mer_refund_tstat.put(7, "商户撤销退款");
		h_mer_refund_tstat.put(2, "系统退款成功");
		h_mer_refund_tstat.put(6, "系统退款失败");
		h_mer_refund_tstat.put(3, "系统退款成功");
		h_mer_refund_tstat.put(4, "系统退款失败");
		
//		5:"商户申请退款",1:"商户确认退款",7:"商户撤销退款",2:"系统退款成功",6:"系统退款失败",3:"系统退款成功",4:"系统退款失败"
		
		

		refund_log_stat_motion.put(1, "商户已提交");
		refund_log_stat_motion.put(2, "退款完成");
		refund_log_stat_motion.put(6, "撤销退款");

		refund_log_stat_verify.put(2, "未操作");
		refund_log_stat_verify.put(3, "操作成功");
		refund_log_stat_verify.put(4, "操作失败");

		refund_type.put(0, "周还款");
		refund_type.put(1, "半月还款");
		refund_type.put(2, "月还款");

		punish_state.put(0, "未缴纳");
		punish_state.put(1, "已缴纳未确认");
		punish_state.put(2, "已缴纳已确认");
		punish_state.put(3, "已取消");

		trans_mode.put(0, "");
		trans_mode.put(1, "(M)");
		trans_mode.put(2, "(B2B)");
		trans_mode.put(3, "(B2B)");

		trans_mode_String.put(0, "网上非直连支付");
		trans_mode_String.put(1, "信用卡直连支付");
		trans_mode_String.put(2, "BTOB协议支付");
		trans_mode_String.put(3, "BTOB网上支付");
		trans_mode_String.put(4, "手机WAP支付");
		/*
		authorType.put(0, "直属银行");
		authorType.put(1, "汇付支付");
		authorType.put(2, "环迅支付");
		authorType.put(3, "高阳捷迅");
		authorType.put(4, "快钱支付");
		*/
		authorType.put(0, "yh");
		authorType.put(1, "hf");
		authorType.put(2, "hx");
		authorType.put(3, "gy");
		authorType.put(4, "kq");
		authorType.put(5, "jh");
		
		bind_all.put(0, "初始交易");
		bind_all.put(1, "交易失败");
		bind_all.put(2, "交易成功");
		
		liqType.put(1, "全额");
		liqType.put(2, "净额");
		
		statFlag.put(0, "正常");
		statFlag.put(1, "关闭");
		
		//category.put(0,"融易付商户");
		//category.put(1,"融易贷业务类型商户");
		//category.put(2,"管理员商户");
		
		auth_type.put(0, "融易付管理员");
		auth_type.put(1, "融易付商户");
		auth_type.put(2, "融易贷管理员");
		auth_type.put(3, "融易贷商户");
		
		//HLOG_MER_TYPE.put(0, "初始化");
		HLOG_MER_TYPE.put(1, "网银支付");
		HLOG_MER_TYPE.put(2, "充值卡支付");
		HLOG_MER_TYPE.put(3, "信用卡支付");
		HLOG_MER_TYPE.put(6, "语音/快捷支付");
//		HLOG_MER_TYPE.put(7, "BTOB网上");
		HLOG_MER_TYPE.put(8, "手机WAP支付");
		HLOG_MER_TYPE.put(10, "POS支付");
		
		bkFlagType.put(0, "初始状态");
		bkFlagType.put(1, "正常");
		
		check_state.put(0, "初始状态(未对帐)");
		check_state.put(1, "勾对成功，双方均成功");
		check_state.put(2, "勾对失败,双方金额不符");
		check_state.put(3, "勾对失败,银行为可疑交易");
		
		trans_type.put(0, "支付");
		trans_type.put(1, "支付手续费");
		trans_type.put(2, "退款");
		trans_type.put(3, "退款退回手续费");
		trans_type.put(4, "调增");
		trans_type.put(5, "调减");
		trans_type.put(6, "结算");
		
		
		h_merge_bk_1.put(10001, "交通银行");
		h_merge_bk_1.put(90000, "招商银行(WAP)");
		h_merge_bk_1.put(90001, "交通银行(WAP)");
		h_merge_bk_1.put(90002, "建设银行(WAP)");
		h_merge_bk_1.put(90003, "工商银行(WAP)");
		h_merge_bk_1.put(90004, "中国银行(WAP)");
		h_merge_bk_1.put(1,"hf");
		h_merge_bk_1.put(2,"hx");
		h_merge_bk_1.put(3,"gy");
		h_merge_bk_1.put(4,"kq");
		h_merge_bk_1.put(5,"jh");
		
		//撤销表   撤销状态
		cancel_state.put(1, "撤销处理中");
		cancel_state.put(2, "撤销成功");
		cancel_state.put(3, "撤销失败");
		cancel_state.put(4, "请求银行失败");
		
//		h_merge_bk_1{10001:"交通银行",90000: "招商银行(WAP)",90001:"交通银行(WAP)",90002:"建设银行(WAP)",
//      90003:"工商银行(WAP)",90004:"中国银行(WAP)",1:"hf",2:"hx",3:"gy",4:"kq",5:"jh"}
		
		//daifu交易类型
		df_transType.put(0,"账户充值");
		df_transType.put(1,"账户提现");
		df_transType.put(2,"充值撤销");
		df_transType.put(3,"付款到电银账户");
		df_transType.put(4,"从电银账户收款");
		df_transType.put(5,"对公代付");
		df_transType.put(6,"从银行账户收款");
		df_transType.put(7,"对私代付");
		df_transType.put(8,"自动结算划款");
		df_transType.put(9,"对私代扣");
		
		//交行超级网银网关列表
		bocomm_gate.put("102100099996",71001);
		bocomm_gate.put("103100000026",71002);
		bocomm_gate.put("104100000004",71003);
		bocomm_gate.put("105100000017",71004);
		bocomm_gate.put("301290000007",71005);
		bocomm_gate.put("302100011000",71006);
		bocomm_gate.put("303100000006",71007);
		bocomm_gate.put("304100040000",71008);
		bocomm_gate.put("305100000013",71009);
		bocomm_gate.put("306581000003",71010);
		bocomm_gate.put("307584007998",71011);
		bocomm_gate.put("308584000013",71012);
		bocomm_gate.put("309391000011",71013);
		bocomm_gate.put("310290000013",71014);
		bocomm_gate.put("313100000013",71015);
		bocomm_gate.put("313110000017",71016);
		bocomm_gate.put("313121006888",71017);
		bocomm_gate.put("313127000013",71018);
		bocomm_gate.put("313131000016",71019);
		bocomm_gate.put("313138000019",71020);
		bocomm_gate.put("313141052422",71021);
		bocomm_gate.put("313143005157",71022);
		bocomm_gate.put("313161000017",71023);
		bocomm_gate.put("313168000003",71024);
		bocomm_gate.put("313191000011",71025);
		bocomm_gate.put("313192000013",71026);
		bocomm_gate.put("313205057830",71027);
		bocomm_gate.put("313222080002",71028);
		bocomm_gate.put("313223007007",71029);
		bocomm_gate.put("313227000012",71030);
		bocomm_gate.put("313227600018",71031);
		bocomm_gate.put("313229000008",71032);
		bocomm_gate.put("313241066661",71033);
		bocomm_gate.put("313261000018",71034);
		bocomm_gate.put("313261099913",71035);
		bocomm_gate.put("313290000017",71036);
		bocomm_gate.put("313301008887",71037);
		bocomm_gate.put("313301099999",71038);
		bocomm_gate.put("313331000014",71039);
		bocomm_gate.put("313332082914",71040);
		bocomm_gate.put("313333007331",71041);
		bocomm_gate.put("313336071575",71042);
		bocomm_gate.put("313337009004",71043);
		bocomm_gate.put("313338707013",71044);
		bocomm_gate.put("313345001665",71045);
		bocomm_gate.put("313345010019",71046);
		bocomm_gate.put("313345400010",71047);
		bocomm_gate.put("313391080007",71048);
		bocomm_gate.put("313393080005",71049);
		bocomm_gate.put("313421087506",71050);
		bocomm_gate.put("313428076517",71051);
		bocomm_gate.put("313433076801",71052);
		bocomm_gate.put("313452060150",71053);
		bocomm_gate.put("313453001017",71054);
		bocomm_gate.put("313455000018",71055);
		bocomm_gate.put("313456000108",71056);
		bocomm_gate.put("313458000013",71057);
		bocomm_gate.put("313461000012",71058);
		bocomm_gate.put("313463000993",71059);
		bocomm_gate.put("313463400019",71060);
		bocomm_gate.put("313465000010",71061);
		bocomm_gate.put("313468000015",71062);
		bocomm_gate.put("313473070018",71063);
		bocomm_gate.put("313473200011",71064);
		bocomm_gate.put("313491000232",71065);
		bocomm_gate.put("313492070005",71066);
		bocomm_gate.put("313493080539",71067);
		bocomm_gate.put("313504000010",71068);
		bocomm_gate.put("313506082510",71069);
		bocomm_gate.put("313513080408",71070);
		bocomm_gate.put("313521000011",71071);
		bocomm_gate.put("313551088886",71072);
		bocomm_gate.put("313581003284",71073);
		bocomm_gate.put("313584099990",71074);
		bocomm_gate.put("313602088017",71075);
		bocomm_gate.put("313611001018",71076);
		bocomm_gate.put("313614000012",71077);
		bocomm_gate.put("313653000013",71078);
		bocomm_gate.put("313656000019",71079);
		bocomm_gate.put("313658000014",71080);
		bocomm_gate.put("313659000016",71081);
		bocomm_gate.put("313701098010",71082);
		bocomm_gate.put("313731010015",71083);
		bocomm_gate.put("313821001016",71084);
		bocomm_gate.put("313851000018",71085);
		bocomm_gate.put("313871000007",71086);
		bocomm_gate.put("313881000002",71087);
		bocomm_gate.put("313882000012",71088);
		bocomm_gate.put("314305006665",71089);
		bocomm_gate.put("314305206650",71090);
		bocomm_gate.put("314305400015",71091);
		bocomm_gate.put("314305506621",71092);
		bocomm_gate.put("314305670002",71093);
		bocomm_gate.put("314581000011",71094);
		bocomm_gate.put("314588000016",71095);
		bocomm_gate.put("314653000011",71096);
		bocomm_gate.put("315456000105",71097);
		bocomm_gate.put("316331000018",71098);
		bocomm_gate.put("317110010019",71099);
		bocomm_gate.put("318110000014",71100);
		bocomm_gate.put("319361000013",71101);
		bocomm_gate.put("322290000011",71102);
		bocomm_gate.put("402100000018",71103);
		bocomm_gate.put("402301099998",71104);
		bocomm_gate.put("402332010004",71105);
		bocomm_gate.put("402361018886",71106);
		bocomm_gate.put("402391000068",71107);
		bocomm_gate.put("402521000032",71108);
		bocomm_gate.put("402584009991",71109);
		bocomm_gate.put("402602000018",71110);
		bocomm_gate.put("402611099974",71111);
		bocomm_gate.put("402641000014",71112);
		bocomm_gate.put("402731057238",71113);
		bocomm_gate.put("402871099996",71114);
		bocomm_gate.put("403100000004",71115);
		bocomm_gate.put("591110000016",71116);
		bocomm_gate.put("595100000007",71117);
		bocomm_gate.put("596110000013",71118);
		bocomm_gate.put("597100000014",71119);
		bocomm_gate.put("313221030104",71192);//增加盛京网关
	}
	

}
