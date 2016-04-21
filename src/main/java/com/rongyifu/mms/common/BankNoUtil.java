package com.rongyifu.mms.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.service.ProvAreaService;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class BankNoUtil {

	private static List<String> all;
	private static Map<String, String> middleGate;//代付网关与银行网关互转网关
	private static List<String> daiKouProv;//代扣银行
	private static Map<Integer, String> bocGate;//中行本行行号
	@SuppressWarnings("rawtypes")
	private static PubDao dao;
	public static Map<String, String> getResultMap(String key) {
		List<String> result = new ArrayList<String>();
		if (key == null || key.length() == 0) {
			result = all;
		} else {

			for (String str : all) {
				if (str.contains(key))
					result.add(str);
			}
		}
		Map<String, String> resultMap = new HashMap<String, String>();

		for (String str : result) {
			resultMap.put(str.split(",")[0], str.split(",")[1]);
		}

		return resultMap;
	}
	
	public static Map<String, String> getDaiKouResultMap(String key) {
		List<String> result = new ArrayList<String>();
		if (key == null || key.length() == 0) {
			result = daiKouProv;
		} else {

			for (String str : daiKouProv) {
				if (str.contains(key))
					result.add(str);
			}
		}
		Map<String, String> resultMap = new HashMap<String, String>();

		for (String str : result) {
			resultMap.put(str.split(",")[0], str.split(",")[1]);
		}

		return resultMap;
	}
	
	//获取对私代付网关 trans_mode 11
	public static Map<String, String> getDaiFuGateMap() {
		Collection<RYTGate> collections=RYFMapUtil.getRytAllGates();
		Map<String, String> pubMap=new TreeMap<String, String>();
		for (RYTGate rytGate : collections) {
			if(rytGate.getTransMode().equals(11)){
				if(Pattern.matches("^4\\d+", String.valueOf(rytGate.getGate()))){
					continue;
				}
				pubMap.put(String.valueOf(rytGate.getGate()),rytGate.getGateName());
			}
		}
		return pubMap;
	}
	
	public static Map<String, String> getDaifuMiddletMap() {
		return middleGate;
	}
	
	//获取对公代付网关 trans_mode 12
	public static Map<String, String> getPubDaifuMap() {
		Collection<RYTGate> collections=RYFMapUtil.getRytAllGates();
		Map<String, String> pubMap=new TreeMap<String, String>();
		for (RYTGate rytGate : collections) {
			if(rytGate.getTransMode().equals(12)){
				if(Pattern.matches("^4\\d+", String.valueOf(rytGate.getGate()))){
					continue;
				}
				pubMap.put(String.valueOf(rytGate.getGate()),rytGate.getGateName());
			}
		}
		return pubMap;
	}
	
	//对公代付72001
	public static Map<String, String> getPubDFMiddletMap() {
		Map<String, String> pubMap=new HashMap<String, String>();				
		Set<String> keys= middleGate.keySet();
		for (String key : keys) {
			pubMap.put(key.replaceFirst("71","72"),middleGate.get(key));
		}
		return pubMap;
	}
	/***
	 * 中行五位联行号map
	 * @return
	 */
	public static Map<Integer, String> getBocGate(){
		return bocGate;
	}
	
	public BankNoUtil() {
		super();
	}

	static {

		all = new ArrayList<String>();

		all.add("313227000012,锦州银行,jzyh");
		all.add("313222080002,大连银行,dlyh");
		all.add("313223007007,鞍山市商业银行,asssyyh");
		all.add("313227600018,葫芦岛银行,hldyh");
		all.add("313452060150,青岛银行,qdyh");
		all.add("313453001017,齐商银行,qsyh");
		all.add("313456000108,烟台银行,ytyh");
		all.add("313461000012,济宁银行,jnyh");
		all.add("313463000993,泰安市商业银行,");
		all.add("313473070018,临商银行,lsyh");
		all.add("313455000018,东营市商业银行,dgssyyh");
		all.add("313465000010,威海市商业银行,whssyyh");
		all.add("313473200011,日照银行,rzyh");
		all.add("313463400019,莱商银行,lsyh");
		all.add("402361018886,安徽省农村信用社联合社,ahsncxys");
		all.add("313551088886,长沙银行,csyh");
		all.add("313421087506,南昌银行,ncyh");
		all.add("313433076801,上饶银行,sryh");
		all.add("313611001018,广西北部湾银行,gxbbwyh");
		all.add("313290000017,上海银行,shyh");
		all.add("310290000013,上海浦东发展银行,shpdfzyh");
		all.add("314305006665,苏州银行,jsyh");
		all.add("314305670002,张家港农村商业银行,zjgncsyyh");
		all.add("313332082914,宁波银行,nbyh");
		all.add("313336071575,湖州银行,hzyh");
		all.add("313393080005,厦门银行,xmyh");
		all.add("402871099996,黄河农村商业银行,hhncsyyh");
		all.add("402641000014,海南省农村信用社,hnsncxys");
		all.add("402521000032,湖北农信,hbyh");
		all.add("313882000012,昆仑银行,klyh");
		all.add("313168000003,晋城市商业银行,jcssyyh");
		all.add("313881000002,乌鲁木齐市商业银行,wlmqssyyh");
		all.add("313131000016,邢台银行,xtyh");
		all.add("313513080408,南阳市商业银行,nyssyyh");
		all.add("313229000008,阜新银行结算中心,bxyh");
		all.add("313504000010,漯河市商业银行,lhssyyh");
		all.add("313656000019,攀枝花市商业银行,pzhssyyh");
		all.add("313653000013,重庆银行股份有限公司,cqyh");
		all.add("314653000011,重庆农村商业银行,cqncsyyh");
		all.add("313121006888,河北银行股份有限公司,heyh");
		all.add("313127000013,邯郸市商业银行,hdssyyh");
		all.add("313141052422,承德银行,cdyh");
		all.add("313138000019,张家口市商业银行,zjkssyyh");
		all.add("313143005157,沧州银行,czyh");
		all.add("313658000014,德阳银行,dyyh");
		all.add("313659000016,绵阳市商业银行,jyssyyh");
		all.add("313521000011,汉口银行,hkyh");
		all.add("313731010015,富滇银行,fdyh");
		all.add("314305206650,昆山农村商业银行,ksncsyyh");
		all.add("313331000014,杭州银行,hzyh");
		all.add("316331000018,浙商银行,zsyh");
		all.add("313338707013,浙江稠州商业银行,zjczsyyh");
		all.add("313345010019,浙江泰隆商业银行,zjtlsyyh");
		all.add("313333007331,温州银行,wzyh");
		all.add("402332010004,鄞州银行,yzyh");//鄞 yin
		all.add("313191000011,内蒙古银行,nmgyh");
		all.add("313192000013,包商银行股份有限公司,bsyh");
		all.add("313205057830,鄂尔多斯银行,eedsyh");
		all.add("313241066661,吉林银行,jlyh");
		all.add("402611099974,广西农村信用社（合作银行）,gxncxys");
		all.add("313614000012,柳州银行,lzyh");
		all.add("596110000013,企业银行,qyyh");
		all.add("313345001665,台州银行,tzyh");
		all.add("313345400010,浙江民泰商业银行,zjmtsyyh");
		all.add("313337009004,绍兴银行,sxyh");
		all.add("315456000105,恒丰银行,hfyh");
		all.add("313468000015,德州银行,dzyh");
		all.add("313458000013,潍坊银行,wfyh");
		all.add("402301099998,江苏省农村信用社联合社,jssncxys");
		all.add("314305506621,常熟农村商业银行,csncsyyh");
		all.add("313301099999,江苏银行股份有限公司,jsyh");
		all.add("313301008887,南京银行,njyh");
		all.add("314305400015,吴江农村商业银行,wjncsyyh");
		all.add("313428076517,赣州银行,gzyh");
		all.add("319361000013,徽商银行,hsyh");
		all.add("402391000068,福建省农村信用社,fjsncxys");
		all.add("402731057238,云南省农村信用社,ynsncxys");
		all.add("313821001016,兰州银行股份有限公司,lzyh");
		all.add("313871000007,宁夏银行,nxyh");
		all.add("313851000018,青海银行,qhyh");
		all.add("313261099913,龙江银行,ljyh");
		all.add("313261000018,哈尔滨银行结算中心,hebyh");
		all.add("313491000232,郑州银行,zzyh");
		all.add("313492070005,开封市商业银行,kfssyyh");
		all.add("313493080539,洛阳银行,lyyh");
		all.add("313506082510,商丘市商业银行,sqssyyh");
		all.add("313701098010,贵阳银行,gyyh");
		all.add("313161000017,晋商银行网上银行,jsyh");
		all.add("102100099996,中国工商银行,zggs");
		all.add("103100000026,中国农业银行,zgny");
		all.add("104100000004,中国银行总行,zgyh");
//		all.add("104100004013,中国银行北京市分行,zgyh");
//		all.add("104110030012,中国银行天津市分行,zgyh");
//		all.add("104121004004,中国银行河北省分行营业部,zgyh");
//		all.add("104161003017,中国银行山西省分行,zgyh");
//		all.add("104191014056,中国银行内蒙古自治区分行营业部,zgyh");
//		all.add("104222017850,中国银行辽宁省分行,zgyh");
//		all.add("104241019993,中国银行吉林省分行,zgyh");
//		all.add("104261088880,中国银行黑龙江省分行,zgyh");
//		all.add("104290003033,中国银行上海市分行营业部,zgyh");
//		all.add("104301003011,中国银行江苏省分行,zgyh");
//		all.add("104331051296,中国银行浙江省分行,zgyh");
//		all.add("104361003012,中国银行安徽省分行,zgyh");
//		all.add("104391008884,中国银行福建省分行,zgyh");
//		all.add("104421073701,中国银行江西省分行,zgyh");
//		all.add("104452000010,中国银行山东省分行,zgyh");
//		all.add("104491062434,中国银行河南省分行,zgyh");
//		all.add("104521003012,中国银行湖北省分行,zgyh");//----47669
//		all.add("104551003013,中国银行湖南省分行,zgyh");
//		all.add("104581003017,中国银行广东省分行,zgyh");
//		all.add("104584000003,中国银行深圳市分行,zgyh");
//		all.add("104611010009,中国银行广西壮族自治区分行,zgyh");
//		all.add("104641003019,中国银行海南省分行,zgyh");
//		all.add("104651003017,中国银行四川省分行,zgyh");
//		all.add("104653086422,中国银行重庆市分行,zgyh");
//		all.add("104701088821,中国银行贵州省分行,zgyh");
//		all.add("104731003017,中国银行云南省分行,zgyh");
//		all.add("104770006003,中国银行西藏自治区分行,zgyh");
//		all.add("104791003010,中国银行陕西省分行,zgyh");
//		all.add("104821003018,中国银行甘肃省分行,zgyh");
//		all.add("104851003012,中国银行青海省分行,zgyh");
//		all.add("104871003010,中国银行宁夏分行,zgyh");
//		all.add("104881003013,中国银行新疆维吾尔自治区分行,zgyh");
		all.add("105100000017,中国建设银行,zgjsyh");
		all.add("301290000007,交通银行,jtyh");
		all.add("302100011000,中信银行,zxyh");
		all.add("303100000006,中国光大银行,zggdyh");
		all.add("304100040000,华夏银行,hxyh");
		all.add("305100000013,中国民生银行,zgmsyh");
		all.add("307584007998,深圳发展银行,szfzyh");
		all.add("308584000013,招商银行,zsyh");
		all.add("309391000011,兴业银行,xyyh");
		all.add("313100000013,北京银行,bjyh");
		all.add("313110000017,天津银行,tjyh");
		all.add("313581003284,广州银行,gzyh");
		all.add("307584007998,平安银行,payh");
		all.add("313602088017,东莞银行,dgyh");
		all.add("314581000011,广州农村商业银行,gzncsyyh");
		all.add("314588000016,顺德农村商业银行,sdncsyyh");
		all.add("317110010019,天津农商银行,tjnsyh");
		all.add("318110000014,渤海银行,bhyh");
		all.add("402100000018,北京农村商业银行,bjncsyyh");
		all.add("402584009991,深圳农商行,sznsh");
		all.add("402602000018,东莞农村商业银行,dgncsyyh");
		all.add("403100000004,中国邮政储蓄银行,zgyzcxyh");
		all.add("591110000016,外换银行（中国）有限公司,whyh");
		all.add("595100000007,新韩银行中国,xhyh");
		all.add("597100000014,韩亚银行,hyyh");
		all.add("313391080007,福建海峡银行,fjhxyh");
		all.add("322290000011,上海农村商业银行,shncsyyh");
		all.add("306581000003,广发银行股份有限公司,gdfzyh");
		
		daiKouProv=new ArrayList<String>();
		daiKouProv.add("70001,邮储银行,ycyh");
		daiKouProv.add("70002,中国工商银行,zggsyh");
		daiKouProv.add("70003,中国农业银行,zgnyyh");
		daiKouProv.add("70004,中国建设银行,zgjsyh");
		daiKouProv.add("70005,中信银行,zxyh");
		daiKouProv.add("70006,中国光大银行,zggdyh");
		daiKouProv.add("70007,中国民生银行,zgmsyh");
		daiKouProv.add("70008,广东发展银行,gdfzyh");
		daiKouProv.add("70009,招商银行,zhyh");
		daiKouProv.add("70010,兴业银行,xyyh");
		daiKouProv.add("70011,江西农信社,jxnxs");
		
		/*newAllGate=new HashMap<String, String>();*/
		
		/*newAllGate.put("71001","中国工商银行");
		newAllGate.put("71002","中国农业银行");
		newAllGate.put("71003","中国银行");
		newAllGate.put("71004","中国建设银行");
		newAllGate.put("71005","交通银行");
		newAllGate.put("71006","中信银行");
		newAllGate.put("71007","中国光大银行");
		newAllGate.put("71008","华夏银行");
		newAllGate.put("71009","中国民生银行");
		newAllGate.put("71010","广发银行股份有限公司");
		newAllGate.put("71011","深圳发展银行");
		newAllGate.put("71012","招商银行");
		newAllGate.put("71013","兴业银行");
		newAllGate.put("71014","上海浦东发展银行");
		newAllGate.put("71015","北京银行");
		newAllGate.put("71016","天津银行");
		newAllGate.put("71017","河北银行股份有限公司");
		newAllGate.put("71018","邯郸市商业银行");
		newAllGate.put("71019","邢台银行");
		newAllGate.put("71020","张家口市商业银行");
		newAllGate.put("71021","承德银行");
		newAllGate.put("71022","沧州银行");
		newAllGate.put("71023","晋商银行网上银行");
		newAllGate.put("71024","晋城市商业银行");
		newAllGate.put("71025","内蒙古银行");
		newAllGate.put("71026","包商银行股份有限公司");
		newAllGate.put("71027","鄂尔多斯银行");
		newAllGate.put("71028","大连银行");
		newAllGate.put("71029","鞍山市商业银行");
		newAllGate.put("71030","锦州银行");
		newAllGate.put("71031","葫芦岛银行");
		newAllGate.put("71032","阜新银行结算中心");
		newAllGate.put("71033","吉林银行");
		newAllGate.put("71034","哈尔滨银行结算中心");
		newAllGate.put("71035","龙江银行");
		newAllGate.put("71036","上海银行");
		newAllGate.put("71037","南京银行");
		newAllGate.put("71038","江苏银行股份有限公司");
		newAllGate.put("71039","杭州银行");
		newAllGate.put("71040","宁波银行");
		newAllGate.put("71041","温州银行");
		newAllGate.put("71042","湖州银行");
		newAllGate.put("71043","绍兴银行");
		newAllGate.put("71044","浙江稠州商业银行");
		newAllGate.put("71045","台州银行");
		newAllGate.put("71046","浙江泰隆商业银行");
		newAllGate.put("71047","浙江民泰商业银行");
		newAllGate.put("71048","福建海峡银行");
		newAllGate.put("71049","厦门银行");
		newAllGate.put("71050","南昌银行");
		newAllGate.put("71051","赣州银行");
		newAllGate.put("71052","上饶银行");
		newAllGate.put("71053","青岛银行");
		newAllGate.put("71054","齐商银行");
		newAllGate.put("71055","东营市商业银行");
		newAllGate.put("71056","烟台银行");
		newAllGate.put("71057","潍坊银行");
		newAllGate.put("71058","济宁银行");
		newAllGate.put("71059","泰安市商业银行");
		newAllGate.put("71060","莱商银行");
		newAllGate.put("71061","威海市商业银行");
		newAllGate.put("71062","德州银行");
		newAllGate.put("71063","临商银行");
		newAllGate.put("71064","日照银行");
		newAllGate.put("71065","郑州银行");
		newAllGate.put("71066","开封市商业银行");
		newAllGate.put("71067","洛阳银行");
		newAllGate.put("71068","漯河市商业银行");
		newAllGate.put("71069","商丘市商业银行");
		newAllGate.put("71070","南阳市商业银行");
		newAllGate.put("71071","汉口银行");
		newAllGate.put("71072","长沙银行");
		newAllGate.put("71073","广州银行");
		newAllGate.put("71074","平安银行");
		newAllGate.put("71075","东莞银行");
		newAllGate.put("71076","广西北部湾银行");
		newAllGate.put("71077","柳州银行");
		newAllGate.put("71078","重庆银行股份有限公司");
		newAllGate.put("71079","攀枝花市商业银行");
		newAllGate.put("71080","德阳银行");
		newAllGate.put("71081","绵阳市商业银行");
		newAllGate.put("71082","贵阳银行");
		newAllGate.put("71083","富滇银行");
		newAllGate.put("71084","兰州银行股份有限公司");
		newAllGate.put("71085","青海银行");
		newAllGate.put("71086","宁夏银行");
		newAllGate.put("71087","乌鲁木齐市商业银行");
		newAllGate.put("71088","昆仑银行");
		newAllGate.put("71089","苏州银行");
		newAllGate.put("71090","昆山农村商业银行");
		newAllGate.put("71091","吴江农村商业银行");
		newAllGate.put("71092","常熟农村商业银行");
		newAllGate.put("71093","张家港农村商业银行");
		newAllGate.put("71094","广州农村商业银行");
		newAllGate.put("71095","顺德农村商业银行");
		newAllGate.put("71096","重庆农村商业银行");
		newAllGate.put("71097","恒丰银行");
		newAllGate.put("71098","浙商银行");
		newAllGate.put("71099","天津农商银行");
		newAllGate.put("71100","渤海银行");
		newAllGate.put("71101","徽商银行");
		newAllGate.put("71102","上海农村商业银行");
		newAllGate.put("71103","北京农村商业银行");
		newAllGate.put("71104","江苏省农村信用社联合社");
		newAllGate.put("71105","鄞州银行");
		newAllGate.put("71106","安徽省农村信用社联合社");
		newAllGate.put("71107","福建省农村信用社");
		newAllGate.put("71108","湖北农信");
		newAllGate.put("71109","深圳农商行");
		newAllGate.put("71110","东莞农村商业银行");
		newAllGate.put("71111","广西农村信用社（合作银行）");
		newAllGate.put("71112","海南省农村信用社");
		newAllGate.put("71113","云南省农村信用社");
		newAllGate.put("71114","黄河农村商业银行");
		newAllGate.put("71115","中国邮政储蓄银行");
		newAllGate.put("71116","外换银行（中国）有限公司");
		newAllGate.put("71117","新韩银行中国");
		newAllGate.put("71118","企业银行");
		newAllGate.put("71119","韩亚银行");
		newAllGate.put("71123", "地区银行");*/
		
		middleGate=new HashMap<String, String>();
		middleGate.put("71001","102100099996");
		middleGate.put("71002","103100000026");
		middleGate.put("71003","104100000004");
		middleGate.put("71004","105100000017");
		middleGate.put("71005","301290000007");
		middleGate.put("71006","302100011000");
		middleGate.put("71007","303100000006");
		middleGate.put("71008","304100040000");
		middleGate.put("71009","305100000013");
		middleGate.put("71010","306581000003");
		middleGate.put("71011","307584007998");
		middleGate.put("71012","308584000013");
		middleGate.put("71013","309391000011");
		middleGate.put("71014","310290000013");
		middleGate.put("71015","313100000013");
		middleGate.put("71016","313110000017");
		middleGate.put("71017","313121006888");
		middleGate.put("71018","313127000013");
		middleGate.put("71019","313131000016");
		middleGate.put("71020","313138000019");
		middleGate.put("71021","313141052422");
		middleGate.put("71022","313143005157");
		middleGate.put("71023","313161000017");
		middleGate.put("71024","313168000003");
		middleGate.put("71025","313191000011");
		middleGate.put("71026","313192000013");
		middleGate.put("71027","313205057830");
		middleGate.put("71028","313222080002");
		middleGate.put("71029","313223007007");
		middleGate.put("71030","313227000012");
		middleGate.put("71031","313227600018");
		middleGate.put("71032","313229000008");
		middleGate.put("71033","313241066661");
		middleGate.put("71034","313261000018");
		middleGate.put("71035","313261099913");
		middleGate.put("71036","313290000017");
		middleGate.put("71037","313301008887");
		middleGate.put("71038","313301099999");
		middleGate.put("71039","313331000014");
		middleGate.put("71040","313332082914");
		middleGate.put("71041","313333007331");
		middleGate.put("71042","313336071575");
		middleGate.put("71043","313337009004");
		middleGate.put("71044","313338707013");
		middleGate.put("71045","313345001665");
		middleGate.put("71046","313345010019");
		middleGate.put("71047","313345400010");
		middleGate.put("71048","313391080007");
		middleGate.put("71049","313393080005");
		middleGate.put("71050","313421087506");
		middleGate.put("71051","313428076517");
		middleGate.put("71052","313433076801");
		middleGate.put("71053","313452060150");
		middleGate.put("71054","313453001017");
		middleGate.put("71055","313455000018");
		middleGate.put("71056","313456000108");
		middleGate.put("71057","313458000013");
		middleGate.put("71058","313461000012");
		middleGate.put("71059","313463000993");
		middleGate.put("71060","313463400019");
		middleGate.put("71061","313465000010");
		middleGate.put("71062","313468000015");
		middleGate.put("71063","313473070018");
		middleGate.put("71064","313473200011");
		middleGate.put("71065","313491000232");
		middleGate.put("71066","313492070005");
		middleGate.put("71067","313493080539");
		middleGate.put("71068","313504000010");
		middleGate.put("71069","313506082510");
		middleGate.put("71070","313513080408");
		middleGate.put("71071","313521000011");
		middleGate.put("71072","313551088886");
		middleGate.put("71073","313581003284");
		middleGate.put("71074","307584007998");
		middleGate.put("71075","313602088017");
		middleGate.put("71076","313611001018");
		middleGate.put("71077","313614000012");
		middleGate.put("71078","313653000013");
		middleGate.put("71079","313656000019");
		middleGate.put("71080","313658000014");
		middleGate.put("71081","313659000016");
		middleGate.put("71082","313701098010");
		middleGate.put("71083","313731010015");
		middleGate.put("71084","313821001016");
		middleGate.put("71085","313851000018");
		middleGate.put("71086","313871000007");
		middleGate.put("71087","313881000002");
		middleGate.put("71088","313882000012");
		middleGate.put("71089","314305006665");
		middleGate.put("71090","314305206650");
		middleGate.put("71091","314305400015");
		middleGate.put("71092","314305506621");
		middleGate.put("71093","314305670002");
		middleGate.put("71094","314581000011");
		middleGate.put("71095","314588000016");
		middleGate.put("71096","314653000011");
		middleGate.put("71097","315456000105");
		middleGate.put("71098","316331000018");
		middleGate.put("71099","317110010019");
		middleGate.put("71100","318110000014");
		middleGate.put("71101","319361000013");
		middleGate.put("71102","322290000011");
		middleGate.put("71103","402100000018");
		middleGate.put("71104","402301099998");
		middleGate.put("71105","402332010004");
		middleGate.put("71106","402361018886");
		middleGate.put("71107","402391000068");
		middleGate.put("71108","402521000032");
		middleGate.put("71109","402584009991");
		middleGate.put("71110","402602000018");
		middleGate.put("71111","402611099974");
		middleGate.put("71112","402641000014");
		middleGate.put("71113","402731057238");
		middleGate.put("71114","402871099996");
		middleGate.put("71115","403100000004");
		middleGate.put("71116","591110000016");
		middleGate.put("71117","595100000007");
		middleGate.put("71118","596110000013");
		middleGate.put("71119","597100000014");
		
		/****
		 * 中行五位行号 map  以省份Id为Key 以[行号（五位）,行名]为value
		 */
		bocGate=new HashMap<Integer, String>();
		  bocGate.put(540,"40600,中国银行西藏区分行");
		  bocGate.put(370,"43810,中国银行山东省分行");
		  bocGate.put(520,"48882,中国银行贵州省分行");
		  bocGate.put(420,"46405,中国银行湖北省分行");
		  bocGate.put(460,"47806,中国银行海南省分行");
		  bocGate.put(330,"45129,中国银行浙江省分行");
		  bocGate.put(130,"40740,中国银行河北省分行");
		  bocGate.put(210,"41785,中国银行辽宁省分行");
		  bocGate.put(640,"43347,中国银行宁夏区分行");
		  bocGate.put(630,"43469,中国银行青海省分行");
		  bocGate.put(150,"41405,中国银行内蒙古区分行");
		  bocGate.put(440,"47504,中国银行广东省分行");
		  bocGate.put(360,"47370,中国银行江西省分行");
		  bocGate.put(530,"49146,中国银行云南省分行");
		  bocGate.put(510,"48631,中国银行四川省分行");
		  bocGate.put(350,"45481,中国银行福建省分行");
		  bocGate.put(990,"47669,中国银行深圳市分行");
		  bocGate.put(140,"41041,中国银行山西省分行");
		  bocGate.put(120,"40202,中国银行天津市分行");
		  bocGate.put(310,"40303,中国银行上海市分行");
		  bocGate.put(500,"48642,中国银行重庆市分行");
		  bocGate.put(340,"44899,中国银行安徽省分行");
		  bocGate.put(610,"43016,中国银行陕西省分行");
		  bocGate.put(650,"43600,中国银行新疆区分行");
		  bocGate.put(220,"42208,中国银行吉林省分行");
		  bocGate.put(450,"48051,中国银行广西区分行");
		  bocGate.put(320,"44433,中国银行江苏省分行");
		  bocGate.put(410,"46243,中国银行河南省分行");
		  bocGate.put(430,"46955,中国银行湖南省分行");
		  bocGate.put(230,"42465,中国银行黑龙江省分行");
		  bocGate.put(110,"40142,中国银行北京市分行");
		  bocGate.put(620,"43251,中国银行甘肃省分行");
		  
		  dao=new PubDao();
	}
	
	/***
	 * 转换 渠道为中行本行的行号为五位
	 * 渠道为交行超级网银的行号 转换为总行行号 
	 * @return
	 */
	public static String[] getBankNo(String bkNo,String bkName,String gate,Integer accProv,Integer gid){
		String[] res=new String[2];
		String EGate=gate.substring(2);
		if(gid==40000){ //交行超级网银
				if(EGate.equals("003")){
					res[0]=bkNo;
					res[1]=bkName;
				}else{
					String bkNo2=BankNoUtil.getDaifuMiddletMap().get(gate);
					String bkName2=BankNoUtil.getDaiFuGateMap().get(gate);
					res[0]	=	bkNo2	==	null	?	bkNo	:	bkNo2;
					res[1]	=	bkName2	==	null	?	bkName	:	bkName2;
				}
		}else if(gid==40001){ //中行银企直连
				if(EGate.equals("003")){
					//转换成本行五位行号
					String bkInfo=BankNoUtil.getBocGate().get(accProv);
					if(null!=bkInfo){
						String[] bkInfo2=bkInfo.split(",");
						res[0]=bkInfo2[0];
						res[1]=bkInfo2[1];
					}
				}else{
					res[0]=bkNo;
					res[1]=bkName;
				}
		}else if(gid==40005){
			ProvAreaService service=new ProvAreaService();
			String bkName2=service.queryBkName(bkNo);
			res[0]=bkNo;
			if(!Ryt.empty(bkName2)){
				bkName=bkName2;
			}
			res[1]=bkName;
		}else{ //其他渠道
			res[0]=bkNo;
			res[1]=bkName;
		}
		LogUtil.printInfoLog("BankNoUtil", "getBankNo", "联行号："+res[0]+";联行名："+res[1]);
		return res;
	}
	
	/****
	 * 校验行号是否是盛京本行
	 * @param recBkNo
	 * @return
	 */
	public static Integer isSjBank(String recBkNo){
		Integer flag=1;
		StringBuffer sql=new StringBuffer("select count(id) from bank_no_info where gid=192 and super_bk_no='313221030104' ");
		sql.append(" and bk_no='").append(recBkNo).append("';");
		Integer tmpFlag=dao.queryForInt(sql.toString());
		if(tmpFlag==1){
			flag=0;
		}
		return flag;
	}
	
}
