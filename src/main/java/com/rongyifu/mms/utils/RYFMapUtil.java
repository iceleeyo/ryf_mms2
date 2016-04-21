package com.rongyifu.mms.utils;

import java.util.*;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.db.PubDao;

@SuppressWarnings("unchecked")
public class RYFMapUtil implements java.io.Serializable {
	
	@SuppressWarnings({ "rawtypes" })
	static PubDao dao = new PubDao();
	private static final long serialVersionUID = -7589021673801786010L;
	private static RYFMapUtil m_obj_RYFMap;
	// 此处用Hashtable
	private static Hashtable<String, Object> m_ryt_maps;
	private static final String MINFO_TABLE = "minfo";
	private static final String FEE_CALC_MODEL_TABLE = "fee_calc_model";
	//省份map
	private static TreeMap<Integer, String> provMap = new TreeMap<Integer, String>();
	//商户所属行业map
	private static TreeMap<Integer, String> merTradeType = new TreeMap<Integer, String>();
	//所有网关map
	private static Map<Integer,RYTGate> gateList = new TreeMap<Integer, RYTGate>();
	//证件map
	private static Map<String,String> idType = new TreeMap<String, String>();
	//支付渠道map
	private static Map<Integer, String> gateRoutMap = new HashMap<Integer, String>();
	//代付支付渠道map
	private static Map<Integer, String> gateRoutMap1 = new HashMap<Integer, String>();
	
	/**
	 * 实例初始化
	 */
	private RYFMapUtil() {
		try {
			if (m_obj_RYFMap == null) init();
		} catch (Exception e) {
			e.printStackTrace();
			m_obj_RYFMap = null;
		}
	}

	/**
	 * 缓存内容的初始化
	 */
	private void init() throws Exception {
		try {
			m_ryt_maps = new Hashtable<String, Object>();
			refresh(MINFO_TABLE);
			refresh(FEE_CALC_MODEL_TABLE);
			initGateList();
			LogUtil.printInfoLog("缓存中已载入" + m_ryt_maps.size() + "个");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized void refresh(String tableName) throws Exception {

		if (tableName == null) return;
		// minfo 表
		if (tableName.equals(MINFO_TABLE)) {
			String str_SQL = " SELECT id,abbrev FROM minfo ";
			Map<String, String> resultMap = new HashMap<String, String>();
			List<Map<String, Object>> list = dao.getJdbcTemplate().queryForList(str_SQL);
			for (Map<String, Object> m : list) {
				if (m.get("id") != null && m.get("abbrev") != null) {
					resultMap.put(m.get("id").toString(), m.get("abbrev").toString());
				}
			}
			m_ryt_maps.put(tableName, resultMap);
			LogUtil.printInfoLog("已将[" + tableName + "]载入到缓存中");

		}
		// fee_calc_model
		if (tableName.equals(FEE_CALC_MODEL_TABLE)) {
            Map<String, List<Integer>> resultMap = new TreeMap<String, List<Integer>>();
			
			String sql = "select a.id, b.gate from minfo a, fee_calc_mode b where a.id = b.mid and trans_mode not in (11,12,16,17)";
			List<Map<String, Object>> midGateList = dao.getJdbcTemplate().queryForList(sql);
			for(Map<String, Object> item : midGateList){
				String itemMid = (String) item.get("id");
				Integer itemGate = (Integer) item.get("gate");
				
				List<Integer> gateList;
				if(resultMap.containsKey(itemMid))
					gateList = resultMap.get(itemMid);
				else
					gateList = new ArrayList<Integer>();
				
				gateList.add(itemGate);
				
				resultMap.put(itemMid, gateList);
			}
			
			m_ryt_maps.put(tableName, resultMap);
			LogUtil.printInfoLog("已将[" + tableName + "]载入到缓存中");
		}

	}

	/**
	 * 刷新单个缓存的内容
	 */
	private synchronized void refresh(String tableName, String mid) throws Exception {
		
		if (tableName == null) return;
		Object obj = m_ryt_maps.get(tableName);
		if(obj == null )  refresh(tableName);
		if (tableName.equals(MINFO_TABLE)) {
			Map<String, String> resultMap = (Map<String, String>)obj;
			String abbrev = dao.getJdbcTemplate().queryForObject("select abbrev from minfo where id = '" + mid+"'",String.class);
			resultMap.put(mid, abbrev);
			m_ryt_maps.put(MINFO_TABLE, resultMap);
		}
		if (tableName.equals(FEE_CALC_MODEL_TABLE)) {
			Map<String, List<Integer>> objMap = (TreeMap<String, List<Integer>>)obj;
			objMap.put(mid, queryMerGates(mid));
			m_ryt_maps.put(FEE_CALC_MODEL_TABLE, objMap);
		}
		
		
	}

	private  List<Integer> queryMerGates(String mid) {
		return dao.getJdbcTemplate().queryForList("select gate from fee_calc_mode where  trans_mode not  in (11,12,16,17) and mid = '"+mid+"'",Integer.class);
	}

	private Object getObjByName(String tableName) {
		return  m_ryt_maps.get(tableName);
	}
	

	static {
		provMap.put(110, "北京");
		provMap.put(120, "天津");
		provMap.put(130, "河北");
		provMap.put(140, "山西");
		provMap.put(150, "内蒙古");
		provMap.put(210, "辽宁");
		provMap.put(220, "吉林");
		provMap.put(230, "黑龙江");
		provMap.put(310, "上海市");
		provMap.put(320, "江苏省");
//		provMap.put(710, "苏州");
//		provMap.put(720, "宁波");
//		provMap.put(990, "深圳");
		provMap.put(330, "浙江");
		provMap.put(340, "安徽");
		provMap.put(350, "福建");
		provMap.put(360, "江西");
		provMap.put(370, "山东");
		provMap.put(410, "河南");
		provMap.put(420, "湖北");
		provMap.put(430, "湖南");
		provMap.put(440, "广东");
		provMap.put(450, "广西");
		provMap.put(460, "海南");
		provMap.put(500, "重庆");
		provMap.put(510, "四川");
		provMap.put(520, "贵州");
		provMap.put(530, "云南");
		provMap.put(540, "西藏");
		provMap.put(610, "陕西");
		provMap.put(620, "甘肃");
		provMap.put(630, "青海");
		provMap.put(640, "宁夏");
		provMap.put(650, "新疆");
		provMap.put(710, "台湾");
		provMap.put(810, "香港");
		provMap.put(820, "澳门");
		
		
		

//		gateList.put(key, value)
//		gateList.put(10001,new RYTGate(10001,"交通银行",0,0));
//		gateList.put(10400,new RYTGate(10400,"工商银行",0,0));
//		gateList.put(10300,new RYTGate(10300,"招商银行",0,0));
//		gateList.put(10605,new RYTGate(10605,"建设银行",0,0));
//		gateList.put(12100,new RYTGate(12100,"中国银行",0,0));
//		gateList.put(10500,new RYTGate(10500,"农业银行(借)",0,0));
//		gateList.put(10501,new RYTGate(10501,"农业银行(贷)",0,0));
//		
//		gateList.put(10900,new RYTGate(10900,"浦发银行",0,0));
//		gateList.put(12200,new RYTGate(12200,"中信银行",0,0));
//		gateList.put(12400,new RYTGate(12400,"光大银行",0,0));
//		gateList.put(10800,new RYTGate(10800,"兴业银行",0,0));
//		gateList.put(10700,new RYTGate(10700,"民生银行",0,0));
//		gateList.put(11300,new RYTGate(11300,"华夏银行",0,0));
//		gateList.put(48,new RYTGate(48,"东亚银行",0,0));
//		gateList.put(15,new RYTGate(15,"北京银行",0,0));
//		gateList.put(49,new RYTGate(49,"南京银行",0,0));
//		gateList.put(81,new RYTGate(81,"杭州银行",0,0));
//		gateList.put(84,new RYTGate(84,"上海银行",0,0));
//		gateList.put(87,new RYTGate(87,"平安银行",0,0));
//		gateList.put(51,new RYTGate(51,"邮政储蓄",0,0));
//		gateList.put(10802,new RYTGate(10802,"长沙银行",0,0));
//		gateList.put(12401,new RYTGate(12401,"浙商银行",0,0));
//		gateList.put(11100,new RYTGate(11100,"广东发展银行",0,0));
//		gateList.put(11000,new RYTGate(11000,"深圳发展银行",0,0));
//		gateList.put(11800,new RYTGate(11800,"广州市商业银行",0,0));
//		gateList.put(11700,new RYTGate(11700,"深圳市农村商业银行",0,0));
//		gateList.put(11500,new RYTGate(11500,"上海农村商业银行",0,0));
//		gateList.put(40,new RYTGate(40,"北京农村商业银行",0,0));
//		gateList.put(11600,new RYTGate(11600,"广州市农信社",0,0));
//		gateList.put(311,new RYTGate(311,"顺德农信社",0,0));
//		//-----------------交行独立支持的银行------------------------
//		gateList.put(10100,new RYTGate(10100,"渤海银行",0,0));
//		gateList.put(10200,new RYTGate(10200,"汉口银行",0,0));
//		gateList.put(10600,new RYTGate(10600,"温州银行",0,0));
//		gateList.put(10601,new RYTGate(10601,"宁波银行",0,0));
//		gateList.put(10602,new RYTGate(10602,"富滇银行",0,0));
//		gateList.put(10603,new RYTGate(10603,"河北银行",0,0));
//		gateList.put(10604,new RYTGate(10604,"齐鲁银行",0,0));
//		gateList.put(10606,new RYTGate(10606,"日照银行",0,0));
//		gateList.put(10607,new RYTGate(10607,"东莞银行",0,0));
//		gateList.put(10608,new RYTGate(10608,"广州银行",0,0));
//		gateList.put(10609,new RYTGate(10609,"青岛银行",0,0));
//		gateList.put(10801,new RYTGate(10801,"大连银行",0,0));
//		gateList.put(10803,new RYTGate(10803,"宁夏银行",0,0));
//		gateList.put(10804,new RYTGate(10804,"上饶银行",0,0));
//		gateList.put(10805,new RYTGate(10805,"江苏银行",0,0));
//		gateList.put(11400,new RYTGate(11400,"广西北部湾银行",0,0));
//		gateList.put(11601,new RYTGate(11601,"海南省农信社",0,0));
//		gateList.put(11602,new RYTGate(11602,"湖南省农信社",0,0));
//		gateList.put(11603,new RYTGate(11603,"江苏省农信社",0,0));
//		gateList.put(11604,new RYTGate(11604,"临汾市尧都区信合社",0,0));
//		gateList.put(11605,new RYTGate(11605,"珠海市农信社",0,0));
//		gateList.put(11801,new RYTGate(11801,"泰安市商业银行",0,0));
//		gateList.put(11802,new RYTGate(11802,"乌鲁木齐市商业银行",0,0));
//		gateList.put(11803,new RYTGate(11803,"威海市商业银行",0,0));
//		gateList.put(11804,new RYTGate(11804,"德州市商业银行",0,0));
//		gateList.put(11805,new RYTGate(11805,"成都农村商业银行",0,0));
//		gateList.put(11806,new RYTGate(11806,"驻马店市商业银行",0,0));
//		gateList.put(11807,new RYTGate(11807,"周口市商业银行",0,0));
//		gateList.put(11808,new RYTGate(11808,"重庆农村商业银行",0,0));
//		gateList.put(11809,new RYTGate(11809,"晋中市商业银行",0,0));
//		gateList.put(11900,new RYTGate(11900,"晋城市商业银行",0,0));
//		gateList.put(11901,new RYTGate(11901,"顺德农村商业银行",0,0));
//		
//		gateList.put(20000,new RYTGate(20000,"交通银行(企)",0,0));
//		gateList.put(20001,new RYTGate(20001,"中国银行(企)",0,0));
//		gateList.put(20002,new RYTGate(20002,"建设银行(企)",0,0));
//		gateList.put(20003,new RYTGate(20003,"工商银行(企)",0,0));
//		gateList.put(20004,new RYTGate(20004,"农业银行(企)",0,0));
//		gateList.put(20005,new RYTGate(20005,"广发银行(企)",0,0));
//		
//		
//		gateList.put(80001,new RYTGate(80001,"工商银行(M)",1,0));//
//		gateList.put(80002,new RYTGate(80002,"中国银行(M)",1,0));//
//		gateList.put(80003,new RYTGate(80003,"兴业银行(M)",1,0));//
//		gateList.put(80004,new RYTGate(80004,"建设银行(M)",1,0));//
//		//gateList.put(80007,new RYTGate(80007,"招商银行(M)",1,1));
//		gateList.put(80008,new RYTGate(80008,"广发银行(M)",1,0));//
//		gateList.put(80009,new RYTGate(80009,"光大银行(M)",1,0));//
//		gateList.put(80010,new RYTGate(80010,"民生银行(M)",1,0));//
//		gateList.put(80012,new RYTGate(80012,"浦发银行(M)",1,0));//
//		gateList.put(80013,new RYTGate(80013,"上海银行(M)",1,0));//
//		gateList.put(80014,new RYTGate(80014,"中信银行(M)",1,0));//
//		
//		//-----------------快钱独立支持的银行------------------------
//		gateList.put(80005,new RYTGate(80005,"农业银行(M)",1,0));
//		gateList.put(80006,new RYTGate(80006,"交通银行(M)",1,0));
//		gateList.put(80011,new RYTGate(80011,"华夏银行(M)",1,0));
//		gateList.put(80015,new RYTGate(80015,"平安银行(M)",1,0));
//		gateList.put(80016,new RYTGate(80016,"宁波银行(M)",1,0));
//		
//		
//		gateList.put(90000,new RYTGate(90000,"招商银行(W)",4,0));
//		gateList.put(90001,new RYTGate(90001,"交通银行(W)",4,0));
//		gateList.put(90002,new RYTGate(90002,"建设银行(W)",4,0));
//		gateList.put(90003,new RYTGate(90003,"工商银行(W)",4,0));
//		gateList.put(90004,new RYTGate(90004,"中国银行(W)",4,0));
//		gateList.put(90005,new RYTGate(90005,"兴业银行(W)",4,0));
//		
//		
//		
//		gateList.put(60000,new RYTGate(60000,"中国移动(C)",5,0));
//		gateList.put(60001,new RYTGate(60001,"中国联通(C)",5,0));
//		gateList.put(60002,new RYTGate(60002,"中国电信(C)",5,0));
//		
//		//语音快捷支付网关
//		gateList.put(50000,new RYTGate(50000,"中国银联(I)",6,0));
//		gateList.put(50001,new RYTGate(50001,"建设银行(I)",6,0));
//		gateList.put(50002,new RYTGate(50002,"招行银行(I)",6,0));
//		gateList.put(50003,new RYTGate(50003,"农业银行(I)",6,0));
//		gateList.put(50004,new RYTGate(50004,"交通银行(I)",6,0));
//		gateList.put(50005,new RYTGate(50005,"浦发银行(I)",6,0));
//		gateList.put(50006,new RYTGate(50006,"中国银行(I)",6,0));
//		gateList.put(50007,new RYTGate(50007,"中信银行(I)",6,0));
//		gateList.put(50008,new RYTGate(50008,"光大银行(I)",6,0));
//		gateList.put(50009,new RYTGate(50009,"华夏银行(I)",6,0));
//		gateList.put(50010,new RYTGate(50010,"邮储银行(I)",6,0));
//		gateList.put(50011,new RYTGate(50011,"农信社(I)",6,0));
//		
//		
//		gateList.put(50100,new RYTGate(50100,"工商银行(快)",6,0));
//		gateList.put(55000,new RYTGate(55000,"银联(手机支付)",6,0));
//		
//		//增值业务网关
//
//		gateList.put(30000,new RYTGate(30000,"北京银行-vas",4,0));
//		gateList.put(30001,new RYTGate(30001,"广发银行-vas",4,0));
//		gateList.put(30002,new RYTGate(30002,"兴业银行-vas",4,0));
//		gateList.put(30003,new RYTGate(30003,"恒丰银行-vas",4,0));
//		gateList.put(30004,new RYTGate(30004,"交通银行-vas",4,0));
//		gateList.put(30005,new RYTGate(30005,"哈尔滨银行-vas",4,0));
//		gateList.put(30006,new RYTGate(30006,"光大银行-vas",4,0));
//		gateList.put(30007,new RYTGate(30007,"江苏银行-vas",4,0));
//		gateList.put(30008,new RYTGate(30008,"农业银行-vas",4,0));
//		gateList.put(30009,new RYTGate(30009,"邮政银行-vas",4,0));
//		gateList.put(30010,new RYTGate(30010,"青岛银行-vas",4,0));
//		gateList.put(30011,new RYTGate(30011,"浙商银行-vas",4,0));
//		//广发大额支付
//		gateList.put(30012,new RYTGate(30012,"广发银行(大)-vas",4,0));
//		gateList.put(30013,new RYTGate(30013,"北京银行-pos",4,0));
//		
//		
//		gateList.put(40000,new RYTGate(40000,"交通银行(银企)",4,0));
//		gateList.put(40001,new RYTGate(40001,"中国银行(银企)",4,0));
		
		initGateList();

		merTradeType.put(100,"航空机票");
		merTradeType.put(101,"酒店/旅游");
		merTradeType.put(102,"服务/缴费");
		merTradeType.put(103,"综合商城");
		merTradeType.put(104,"金融/保险 ");
		merTradeType.put(105,"虚拟/游戏");
		merTradeType.put(106,"医药/保健");
		merTradeType.put(107,"教育/招生");
		merTradeType.put(108,"交友/咨询");
		merTradeType.put(110,"大宗商贸");
		merTradeType.put(111,"批发市场");
		merTradeType.put(109,"其他");
		
		
		

		idType.put("00","组织机构代码证");
		idType.put("01","营业执照");
		idType.put("02","事业单位登记证");
		idType.put("03","社会团体登记证");
		idType.put("04","民办非企业登记证");
		idType.put("05","外地常设机构登记证");
		idType.put("06","军队开户许可证");
		idType.put("07","批文");
		idType.put("08","外汇账户核准件");
		idType.put("09","证明");
		idType.put("10","开户许可证");
		idType.put("15","居民身份证");
		idType.put("16","临时身份证");
		idType.put("17","军人身份证件");
		idType.put("18","武警身份证件");
		idType.put("19","通行证");
		idType.put("20","护照");
		idType.put("21","其他");
		idType.put("22","临时户口");
		idType.put("23","户口簿");
		idType.put("24","边境证");
		
	}

	/****
	 * 初始化gatelist 集合
	 */
	private static void initGateList() {
		List<RYTGate> aList = dao.getJdbcTemplate().query("select gate,stat_flag,trans_mode,gate_name from ryt_gate  ", new BeanPropertyRowMapper(RYTGate.class));
		gateList.clear();
		for(RYTGate o : aList){
			if(filterGate(o.getTransMode(),String.valueOf(o.getGate()))){
				continue;
			}
			gateList.put(o.getGate(), o);
		}
		LogUtil.printInfoLog("gatelist集合初始化成功");
	}
	/**
	 * @return  所属行业的Map
	 */
	public static Map<Integer, String> getMerTradeType() {
		return merTradeType;
	}
	/**
	 * @return 省份Map
	 */
	public static TreeMap<Integer, String> getProvMap() {
		return provMap;
	}
	/**
	 * @return 证件类型 Map
	 */
	public static Map<String, String> getIdType() {
		return idType;
	}
	/**
	 * @return 融易通所支持的网关Map 8008 广发银行
	 */
	public static TreeMap<Integer, String> getGateMap() {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		for(Integer gate : gateList.keySet()){
			m.put(gate, gateList.get(gate).getGateName());
		}
		return m;
	}
	
	/**
	 * @return 融易通所支持的出款网关Map 8008 广发银行
	 */
	public static TreeMap<Integer, String> getGateMap1() {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		for(Integer gate : gateList.keySet()){
			m.put(gate, gateList.get(gate).getGateName());
		}
		return m;
	}
	/**
	 * 所有支付渠道网关
	 * @return
	 */
	public  static Map<Integer,String> getGateRouteMap(){
			if(gateRoutMap==null||gateRoutMap.size()==0){
				 gateRoutMap=dao.queryGateRouteToMap();
			}
		return gateRoutMap;
	}
	
	/**
	 * 所有代付支付渠道网关
	 * @return
	 */
	public  static Map<Integer,String> getDFGateRouteMap(){
			if(gateRoutMap1==null||gateRoutMap1.size()==0){
				 gateRoutMap1=dao.queryDFGateRouteToMap();
			}
		return gateRoutMap1;
	}
	public static void refreshGateRoutMap(){
		//TODO 修改、增加支付渠道网关刷新
		gateRoutMap=dao.queryGateRouteToMap();
	}
	/**
	 * 融易通所支持的全部银行网关
	 * @return
	 */
	public static Collection<RYTGate> getRytAllGates(){

		return gateList.values();
	}
	
	public static RYTGate getRYTGateById(int gate){
		return gateList.get(gate);
	}
	
	/**
	 * 刷新商户的网关列表
	 * @param mid
	 */
	public void refreshFeeCalcModel(String mid) {
		try {
			refresh(FEE_CALC_MODEL_TABLE, mid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 刷新商户的网关列表
	 * @param mid
	 */
	public void refreshFeeCalcModel() {
		try {
			refresh(FEE_CALC_MODEL_TABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷新商户Map  id-> abbrev 
	 * @param mid
	 */
	public void refreshMinfoMap(String mid) {
		try {
			refresh(MINFO_TABLE, mid);
		} catch (Exception e) {
		}
	}

	/**
	 * 得到商户已经开通的网关号
	 */
	public Map<Integer, String> getMerGatesMap(String mid) {
		Map<Integer, String> result=null;
		 try {
				Map<String,List<Integer> > obj = (Map<String,List<Integer>>)getObjByName(FEE_CALC_MODEL_TABLE);
				List<Integer> merGateList = obj.get(mid);
				 result = new TreeMap<Integer, String>();
				for (Integer gate : merGateList) {
					result.put(gate, getGateMap().get(gate));
				}
				
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	
		return result;
	}
	
	/**
	 * 得到商户Map
	 */
	public Map<Integer, String> getMerMap() {
		Object obj = getObjByName(MINFO_TABLE);
		if(obj == null ) try {
			refresh(MINFO_TABLE);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<Integer, String>();
		}
		return (Map<Integer, String>)obj;
	}
	
	/**
	 * 得到商户Map
	 */
	public Map<String, String> getMinfoMap() {
		Object obj = getObjByName(MINFO_TABLE);
		if(obj == null ) try {
			refresh(MINFO_TABLE);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
		return (Map<String, String>)obj;
	}
	
	/**
	 * 使用方法  RYFMapUtil obj = RYFMapUtil.getInstance();
	 * obj.XXXX();
	 * 
	 */
	public static synchronized RYFMapUtil getInstance() {
		if (m_obj_RYFMap == null) {
			m_obj_RYFMap = new RYFMapUtil();
		}
		return m_obj_RYFMap;
	}
	/**
	 * 得到企业银行的map
	 * @return
	 */
	public static TreeMap<Integer, String> getCompanyBkMap(){
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		for(Integer gate : gateList.keySet()){
			if(gate>=20000&&gate<30000){
			m.put(gate, gateList.get(gate).getGateName());
			}
		}
		return m;
	}
	
	/**
	 * 向gatelist中添加一个网关
	 * @param gate
	 * @param gateName
	 * @param transMode
	 */
	public static void putGate(int gate,String gateName,int transMode) {
		gateList.put(gate, new RYTGate(gate,gateName,transMode,0));
	}
	
	/**
	 * @return 融易通所支持的网关Map 8008 广发银行
	 */
	public static TreeMap<Integer, String> getGateMapByType(int type) {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		for(Integer gate : gateList.keySet()){
			if(gateList.get(gate).getTransMode()==type){
				m.put(gate, gateList.get(gate).getGateName());
			}else if(type == -1){
				m.put(gate, gateList.get(gate).getGateName());
			}
		}
		return m;
	}
	
	public static TreeMap<Integer, String> getGateMapByType1(Integer[] type) {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		   List<Integer> temp = Arrays.asList(type);
			for (Integer gate : gateList.keySet()) {
				if (temp.contains(gateList.get(gate).getTransMode())) {
					m.put(gate, gateList.get(gate).getGateName());
				} else{
					continue;
				}
			}
		return m;
	}
	/***
	 * 筛选出代付网关
	 * @param type
	 * @return
	 */
	public static TreeMap<Integer, String> getGateMapByType3(Integer[] type) {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		   List<Integer> temp = Arrays.asList(type);
			for (Integer gate : gateList.keySet()) {
				if (temp.contains(gateList.get(gate).getTransMode())) {
					m.put(gate, gateList.get(gate).getGateName());
				} else{
					continue;
				}
			}
		return m;
	}
	
	/***
	 * 筛选出代付网关
	 * @param type
	 * * @param gateName
	 * @return
	 */
	public static TreeMap<Integer, String> getGateMapByType3(Integer[] type,String gateName) {
		TreeMap<Integer, String> m = new TreeMap<Integer, String>();
		   List<Integer> temp = Arrays.asList(type);
			for (Integer gate : gateList.keySet()) {
				if (temp.contains(gateList.get(gate).getTransMode())) {
					if(gateList.get(gate).getGateName().contains(gateName)){
						m.put(gate, gateList.get(gate).getGateName());
					}
					
				} else{
					continue;
				}
			}
		return m;
	}
	
	/**
	 * 所有支付渠道网关
	 * @return
	 */
	public  static Map<Integer,String> getGateRouteMapByType( int type){
			if(gateRoutMap==null||gateRoutMap.size()==0){
				 gateRoutMap=dao.queryGateRouteToMap();
			}
		return gateRoutMap;
	}
	
	/***
	 * 过滤不显示的网关
	 * 交易类型为代付的，4开头的网关
	 * @param transType
	 * @param gate
	 * @return
	 */
	public static boolean filterGate(Integer transType,String gate){
		boolean res=false;
		if(transType==11 && Pattern.matches("^4\\d+", gate)){
			res=true;
		}else if(transType==12 && Pattern.matches("^4\\d+", gate)){
			res=true;
		}
		return res;
	}
}
