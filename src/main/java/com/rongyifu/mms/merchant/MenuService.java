package com.rongyifu.mms.merchant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.service.MerchantService;

public class MenuService {

	private static List<MenuBean> merMenus = new ArrayList<MenuBean>();
	private static List<MenuBean> adminMenus = new ArrayList<MenuBean>();
	private static  List<Integer> myacountpid=new ArrayList<Integer>();//我的账户PID
	static MenuBean wdzh = null;//我的帐户
	private static SystemDao sysDao=new SystemDao();
	private static Logger logger = LoggerFactory.getLogger(MenuService.class);
	/**
	 * 查看用户权限
	 * @param auth 用户权限字段 （第0位=0为系统管理员=1为商户管理员）
	 * @return 对应的权限名称
	 */
	public static String queryUserAuth(String auth) {
		
		List<MenuBean> aList = auth.charAt(0) == '0' ? getAdminMenu() : getMerMenu();
		String authStr = "";
		for(MenuBean bean : aList){
			if(auth.charAt(bean.getId())=='1'){
				authStr += bean.getName()+ ",";
			}
			
		}
		if (authStr.endsWith(",")) {
			authStr = authStr.substring(0, authStr.length() - 1);
		}
        return authStr;
	}

	/**
	 * 查看用户已有的权限Id
	 * @param auth 用户权限字段
	 * @return 已有的权限Id，已有的权限Id
	 */
	public static String queryUserAuthIndex(String auth) {
		String authStr = "";
		for (int i = 1; i < auth.length(); i++) {
			char temp = auth.charAt(i);
			if (temp == '1') authStr += i + ",";
		}
		if (authStr.endsWith(",")) {
			authStr = authStr.substring(0, authStr.length() - 1);
		}

		return authStr;
	}
	/**
	 * 
	 * @param auth 用户权限字段
	 * @return 权限id的数组
	 */
	public static int[] queryUserAuthArr(String auth) {
		int[] authArray = {};
		for (int i = 1; i < auth.length(); i++) {
			char temp = auth.charAt(i);
			if (temp == '1')authArray[i]=i;
		}
		return authArray;
	}
	/**
	 * 生成修改用户权限字段，用于权限修改
	 * @param authstr 分配给用户权限字段index数组
	 * @param role 角色 系统管理员还是商户，也就是auth的第0位数字
	 * @return 对应的权限auth字段的值
	 */
	public static String genUserAuth(String[] authstr, int role) {
		String result = "";
		String authStr = String.valueOf(role);
		for (int i = 1; i < 200; i++) {
			authStr += "0";
		}
		char[] aAuth = authStr.toCharArray();//权限串字符数组形式
		for (String authIndex : authstr) {
			int temp = Integer.parseInt(authIndex);//权限下标
			if(aAuth.length>temp){
				aAuth[temp] = '1';
			}else{//不足的时候补齐
				int num=temp-aAuth.length;
				String ah="";
				for (int i = 0; i < num-1; i++) {
					ah=ah+"0";
				}
				authStr=authStr+ah;		
				aAuth = authStr.toCharArray();
					if(aAuth.length>temp){
						aAuth[temp] = '1';
				}
			}
		}
		for (char c : aAuth) {
			result += String.valueOf(c);
		}
		return result;
	}
	/**
	 * 得到用户权限修改后的权限字符
	 * @param authStr
	 * @return
	 */
	public  static String genUserAuths(String authStr) {
		// 权限初始化
		int authLength = Constant.AUTHLEN;
		char authArray[] = new char[authLength];
		authArray[0] = '1'; // 第一位必须为1，否则登陆后报“您没有权限访问该资源”错误
		for (int i = 1; i < authLength; i++) {
			authArray[i] = '0';
		}
		
		// 赋权
		String[] auths = authStr.split(",");
		for(String authIndex : auths){
			authArray[Integer.parseInt(authIndex)] = '1';
		}
		
		// 组织返回
		String result = "";
		for (char auth : authArray) {
			result += String.valueOf(auth);
		}
		return result;
	}
	/**
	 * 生成无任何权限的字段即默认字段
	 * @param role 角色 系统管理员还是商户，也就是auth的第0位数字
	 */
	public static String genDefaultUserAuth(int role) {
		StringBuffer result= new StringBuffer(role);
		for (int i = 1; i < 200; i++) {
			result.append("0");
		}
		return result.toString();
	}
	/**
	 * 清空我的账户权限
	 */
	public static String noneAuth(String allAuthStr,String mid,int operId){
		String result = "";
		String authStr ="";
		String [] cancelAuth=allAuthStr.split(",");//需要先置为零的权限
		authStr=new MerOperDao().getUserAuth(mid, operId);
		char[] aAuth = authStr.toCharArray();
		for (String authIndex : cancelAuth) {
			int temp = Integer.parseInt(authIndex);
			if(aAuth.length>temp){
				aAuth[temp] = '0';
			}else{
				int num=temp-aAuth.length;
				String ah="";
				for (int i = 0; i <=num; i++) {
					ah=ah+"0";
				}
				aAuth = authStr.toCharArray();
			}
		}
		for (char c : aAuth) {
			result += String.valueOf(c);
		}
		return result;
		
	}
	/**
	 * 生成有全部权限字段，即超级管理元的auth
	 * @param role 角色 系统管理员还是商户，也就是auth的第0位数字
	 */
	public static String genAllUserAuth(String role) {
		StringBuffer result= new StringBuffer(role);
		for (int i = 1; i < 200; i++) {
			result.append("1");
		}
		return result.toString();
	}
	
	/**
	 * 登录商户的第一级菜单
	 * @param mid
	 * @param authStr
	 * @return
	 */
	public  List<String> getLoginUserFirstMenu() {

		LoginUser loginUser=sysDao.getLoginUser();
		String mid=loginUser.getMid();
		String authStr=loginUser.getAuth();//1111101001010010100101111111111
		List<MenuBean> alllist = "1".equals(mid)? getAdminFirstMenu() : getMerFirstMenu();
		List<String> userlist = new ArrayList<String>();
		int count=0;
		for (MenuBean auth : alllist) {
			if (hasThisAuth(authStr, auth.getId())) {
				count++;
				StringBuilder strBuff=new StringBuilder();
				strBuff .append("<a id=\"menu")
						.append(count)
						.append("\" class=\"\" name=\"menu\" style=\"cursor:pointer;\" onclick=\"switchmodTag('menu")
						.append(count).append("','")
						.append(auth.getName()).append("',")
						.append(auth.getId())
						.append(");this.blur();\" target=\"content1\">")
						.append(auth.getName()).append("</a>");
				userlist.add(strBuff.toString());
			}
		}
		return userlist;
	}
	/**
	 * 根据第一级菜单得到商户子菜单(商户平台)
	 * @param authIndex
	 * @param thisName
	 * @param authStr
	 * @return
	 * @throws Exception 
	 */
	public String getMerLeftMenu(int menuId) throws Exception {
		LoginUser loginUser=sysDao.getLoginUser();
		if(loginUser==null)throw new Exception("您未登陆，或登陆已经超时！");
		String authStr=loginUser.getAuth();
		String mid=loginUser.getMid();
		if("1".equals(mid))throw new Exception("对不起，您的权限不足，不能操作商户平台！");
		return verifyAndCreatMenu(authStr, menuId, merMenus);
	}
	/**
	 * 根据第一级菜单得到管理员子菜单(管理平台)
	 * @param authIndex
	 * @param thisName
	 * @param authStr
	 * @return
	 * @throws Exception 
	 */
	public String getAdminLeftMenu(int menuId) throws Exception {
		LoginUser loginUser=sysDao.getLoginUser();
		if(loginUser==null)throw new Exception("您未登陆，或登陆已经超时！");
		String authStr=loginUser.getAuth();
		String mid=loginUser.getMid();
		if(!"1".equals(mid))throw new Exception("对不起，您的操作权限不足！");
		return verifyAndCreatMenu(authStr, menuId, adminMenus);
	}
	/**
	 * 验证权限
	 * @param authStr
	 * @param menuId
	 * @param menusList
	 * @return
	 * @throws Exception
	 */
	private String verifyAndCreatMenu(String authStr,int menuId,List<MenuBean> menusList) throws Exception{
		if (!hasThisAuth(authStr, menuId)) {throw new Exception("你没有有操作权限，请联系管理员！");}
		MenuBean menuBean1=null;
		for (MenuBean menu1 : menusList) {//一级菜单中获取二级菜单bean
			if (menu1.getId() == menuId) {
					menuBean1=menu1;
			} 
		}
		StringBuffer strBuff=new StringBuffer();
			if(menuBean1.getChildren().get(0).getChildren()!=null){//有二级菜单
				for (MenuBean menuBean2 : menuBean1.getChildren()) {
					if (hasThisAuth(authStr, menuBean2.getId())) {
						strBuff.append(createMenu(menuBean2,authStr));
					}
				}
			}else{//缺二级菜单的
				strBuff.append(createMenu(menuBean1, authStr));
			}
		return strBuff.toString();
	}
	//创建三级菜单和二级菜单
	private StringBuffer createMenu(MenuBean menuBean2,String authStr){
		StringBuffer strbuff = new StringBuffer();
		if(menuBean2.getChildren()==null)return strbuff;
		strbuff.append("<h2 class=\"nav_2\">").append(menuBean2.getName()).append("</h2>");
		strbuff.append("<ul class=\"nav_3\">");
		for (MenuBean menuBean : menuBean2.getChildren()) {
			if (hasThisAuth(authStr, menuBean.getId())) {//判断权限
				strbuff.append("<li>");
				strbuff.append("<a href=\"../" + menuBean.getJsp() + "\" target='content2'>" + menuBean.getName() + "</a>");
				strbuff.append("</li>");
			}
		}
		strbuff.append("</ul>");
		return strbuff;
	}
	/**
	 * 返回首次登陆时页面显示的系统通知（显示标题）
	 * @return
	 */
	public List<String> queryMMSNotice() {
		String mid=sysDao.getLoginUser().getMid();
		List<MMSNotice> noticeList=sysDao.queryMMSNotice(mid);
		List<String> hrefList=new ArrayList<String>();
		if("1".equals(mid)){
			genNoticeHref(noticeList, hrefList);
		}else{
			for (int i = 0; i < noticeList.size(); i++) {
				MMSNotice notice=noticeList.get(i);
				String title=notice.getTitle().length()>10? notice.getTitle().substring(0,10)+"..." : notice.getTitle();
				hrefList.add("<a target='content2' href='M_0_notice.jsp?id="+notice.getId()+"'>"+title+"</a>");
			}
		}
		return hrefList ;
	}

	private void genNoticeHref(List<MMSNotice> noticeList, List<String> hrefList) {
		Map<Integer, List<MMSNotice>> map=new HashMap<Integer, List<MMSNotice>>();
		for (MMSNotice notice : noticeList) {
			if(notice.getNoticeType()==1 || notice.getNoticeType()==2){
				if(map.containsKey(notice.getBeginDate())){
					map.get(notice.getBeginDate()).add(notice);
				}else{
					List<MMSNotice> ls=new ArrayList<MMSNotice>();
					ls.add(notice);
					map.put(notice.getBeginDate(), ls);
				}
			}else{
				String title=notice.getTitle().length()>10? notice.getTitle().substring(0,10)+"..." : notice.getTitle();
				hrefList.add("<a target='content2' href='M_0_notice.jsp?id="+notice.getId()+"'>"+title+"</a>");
			}
		}
		if(!map.isEmpty()){
			Set<Integer> keys=map.keySet();
			for (Integer key : keys) {
				String notice_1="";//商户合约到期通知
				String notice_2="";//商户组织机构代码到期通知
				List<MMSNotice> ls= map.get(key);
				if(ls.isEmpty())continue;
				for(MMSNotice notice : ls){
					if(notice.getNoticeType()==1){
						notice_1+=notice.getId()+",";
					}else if(notice.getNoticeType()==2){
						notice_2+=notice.getId()+",";
					}
				}
				
				String title1="商户合约到期通知-"+key;
				title1=title1.length()>10 ? title1.substring(0, 10)+"..." : title1;
				String title2="商户组织机构代码到期通知-"+key;
				title2=title2.length()>10 ? title2.substring(0, 10)+"..." : title2;
				if(!Ryt.empty(notice_1))hrefList.add("<a target='content2' href='M_0_notice.jsp?id="+notice_1.substring(0, notice_1.length()-1)+"'>"+title1+"</a>");
				if(!Ryt.empty(notice_2))hrefList.add("<a target='content2' href='M_0_notice.jsp?id="+notice_2.substring(0, notice_2.length()-1)+"'>"+title2+"</a>");
			}
		}
		return;
	}
	/**
	 * 判断是否有改权限
	 * @param authIndex 用户权限字段Index
	 * @return
	 */
	public static boolean hasThisAuth(String auth, int authIndex) {
		try {
			if(auth.length()>=authIndex){
				return auth.charAt(authIndex) == '1';
			}
			return false;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	
	/**
	 * 判断登录用户是否有改权限
	 * @param authIndex 用户权限字段Index
	 * @return
	 */
	public static boolean hasThisAuth(int authIndex) {
		try {
			String auth=sysDao.getLoginUser().getAuth();
			return hasThisAuth(auth,authIndex);
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	private static List<MenuBean> getMerFirstMenu() {
		List<MenuBean> merFirstMenu = new ArrayList<MenuBean>();
		for (MenuBean bean : merMenus) {
			if (bean.getPid() == null) merFirstMenu.add(bean);
		}
		return merFirstMenu;
	}

	private static List<MenuBean> getAdminFirstMenu() {
		List<MenuBean> adminFirstMenu = new ArrayList<MenuBean>();
		for (MenuBean bean : adminMenus) {
			if (bean.getPid() == null) adminFirstMenu.add(bean);
		}
		return adminFirstMenu;
	}

	public static List<MenuBean> getMerMenu() {
		return merMenus;
	}

	public static List<MenuBean> getMerMenu2() {
		List<MenuBean> list = new ArrayList<MenuBean>();
		for (int i = 0; i < merMenus.size(); i++) {
			list.add(merMenus.get(i));
		}
		return list;
	}
	public static List<MenuBean> getAdminMenu() {
		return adminMenus;
	}

	static {

		// **商户菜单********************************************
		MenuBean jtgl = new MenuBean(1, "交易管理");
		MenuBean mxcx = new MenuBean(1, 2, "M", "MXCX", "历史收款明细查询");
		MenuBean dbcx = new MenuBean(1, 3, "M", "DBCX", "历史单笔查询");
//		MenuBean hzcx = new MenuBean(1, 4, "M", "HZCX", " 汇总查询");
		MenuBean shysrz = new MenuBean(1, 5, "M", "SHYSRZ", "商户原始日志");
		MenuBean dtjtcx = new MenuBean(1, 6, "M", "DTJYCX", "当天收款交易查询");
		MenuBean sjddsc = new MenuBean(1, 27, "M", "SJZFGL", "手机支付管理");
		MenuBean dfmxcx = new MenuBean(1, 101, "M", "DFLSMXCX", "出款交易管理");//和按钮权限的网银支付id重复
		MenuBean xxczcx = new MenuBean(1, 102, "M", "XXCZCX", "线下充值交易查询");//和按钮权限的线下充值支付id重复
		MenuBean posdtjycx = new MenuBean(1, 155, "M", "POSDTJYCX", "POS当天交易查询");
		MenuBean poslsjycx = new MenuBean(1, 156, "M", "POSLSJYCX", "POS历史交易查询");
		
		jtgl.addChild(dtjtcx);
		jtgl.addChild(mxcx);
		jtgl.addChild(dbcx);
		jtgl.addChild(shysrz);
		jtgl.addChild(sjddsc);
		jtgl.addChild(dfmxcx);
		jtgl.addChild(xxczcx);
		jtgl.addChild(posdtjycx);
		jtgl.addChild(poslsjycx);
		
		MenuBean tkgl = new MenuBean(7, "退款管理");
		MenuBean tkcx = new MenuBean(7, 8, "M", "TKCX", "退款查询");
		MenuBean pltksq = new MenuBean(7, 4, "M", "PLSQTK", "批量申请退款");
		MenuBean tksq = new MenuBean(7, 9, "M", "SQTK", "单笔申请退款");
		MenuBean tkqr = new MenuBean(7, 10, "M", "TKQR", "退款确认");
		tkgl.addChild(tkcx);
		tkgl.addChild(pltksq);
		tkgl.addChild(tksq);
		tkgl.addChild(tkqr);

		MenuBean jsgl = new MenuBean(11, "结算管理");
		MenuBean dzdxz = new MenuBean(11, 12, "M", "DZDXZ", "成功交易文件下载 ");
		MenuBean zhjsdcx = new MenuBean(11, 13, "M", "SHJSDCX", "商户结算单查询");
		MenuBean yecx = new MenuBean(11, 14, "M", "YECX", "余额查询");
		MenuBean zhlscx = new MenuBean(11, 15, "M", "ZHLSCX_28", "账户流水查询");
		MenuBean sgdzcx = new MenuBean(11, 29, "M", "SGDZCX", "手工调账查询");
		
		
//		MenuBean ZHJYLSCX = new MenuBean(11, 40, "M", "ZHJYLSCX", "账户流水查询(2.8新)");
		
		jsgl.addChild(dzdxz);
		jsgl.addChild(zhjsdcx);
		jsgl.addChild(yecx);
		jsgl.addChild(zhlscx);
//		jsgl.addChild(ZHJYLSCX);
		jsgl.addChild(sgdzcx);

		MenuBean shgl = new MenuBean(16, "商户管理");
		MenuBean shxx = new MenuBean(16, 17, "M", "SHXX", "商户信息");
		MenuBean czypz = new MenuBean(16, 21, "M", "CZYPZ", "操作员配置");
		
		shgl.addChild(shxx);
		shgl.addChild(czypz);
		MenuBean shzs = new MenuBean(17, 18, "M", "SHZS", "商户证书");
		MenuBean shxxcx = new MenuBean(17, 19, "M", "SHXXCX", "商户信息查询");
		MenuBean shxxxg = new MenuBean(17, 20, "M", "SHXXXG", "商户信息修改");
		MenuBean shzlxz = new MenuBean(17, 28, "M", "SHZLXZ", "商户资料下载");
		//银行账户维护
		MenuBean yhzhwh = new MenuBean(17, 41, "M", "YHZHWH", "银行账户维护");
		//企业客户信息
		MenuBean khxxwh = new MenuBean(17, 42, "M", "KHXXWH", "客户信息维护");
		
		shxx.addChild(shzs);
		shxx.addChild(shxxcx);
		shxx.addChild(shxxxg);
		shxx.addChild(shzlxz);
		shxx.addChild(yhzhwh);
		shxx.addChild(khxxwh);
		
		MenuBean czyqxxg = new MenuBean(21, 22,"M", "CZYQXXG", "操作员权限修改");
		MenuBean czyqxcx = new MenuBean(21, 23,"M", "CZYQXCX", "操作员权限查询");
		MenuBean czygl = new MenuBean(21, 24,"M", "CZYGL", "操作员管理");
		MenuBean mmxg = new MenuBean(21, 25,"M", "XGMM", "修改密码");
		MenuBean tzxxcx = new MenuBean(21, 26,"M", "TZXXCX", "通知信息查询");
		MenuBean m_szfwxx = new MenuBean(21, 44,"M", "SZFWXX", "防伪信息设置");
		MenuBean czyrzcx = new MenuBean(21, 170,"M", "CZYRZCX", " 操作员日志查询");
		//max ： 29
		czypz.addChild(czyqxxg);
		czypz.addChild(czyqxcx);
		czypz.addChild(czygl);
		czypz.addChild(mmxg);
		czypz.addChild(tzxxcx);
		czypz.addChild(m_szfwxx);
		czypz.addChild(czyrzcx);
		
		
		wdzh = new MenuBean(30, "我的账户");
//		MenuBean ZHXXWH = new MenuBean(30, 31, "M", "ZHXXWH", "账户信息维护 ");
//		MenuBean ZHYECX = new MenuBean(30, 32, "M", "ZHYECX", "账户余额查询");
		MenuBean ZHCZ = new MenuBean(30, 33, "M", "ZHCZ", "账户充值");
		MenuBean ZHTX = new MenuBean(30, 34, "M", "ZHTX", "账户提现");
		MenuBean ZFMMWH = new MenuBean(30, 35, "M", "ZFMMWH", "支付密码维护");
//		MenuBean WYFK = new MenuBean(30, 36, "M", "WYFK", "付款到电银账户");
//		MenuBean DBDK = new MenuBean(30, 120, "M", "DBDK", "单笔代扣");
//		MenuBean PLDK = new MenuBean(30, 121, "M", "PLDK", "批量代扣");
		MenuBean FKDQYYHK = new MenuBean(30, 43, "M", "FKDQYYHK", "单笔付款到企业银行账户");
		MenuBean PLFKDQYYHK = new MenuBean(30, 100, "M", "PLFKDQYYHK", "批量付款到企业银行账户");
		MenuBean DBFKDYHK = new MenuBean(30, 37, "M", "DBFKDYHK", "单笔付款到个人银行账户");
		MenuBean PLFKDYHK = new MenuBean(30, 38, "M", "PLFKDYHK", "批量付款到个人银行账户");
		MenuBean SCPZ = new MenuBean(30, 107, "M", "SCPZ", "上传线下充值凭证");
		MenuBean DFQR = new MenuBean(30, 129, "M", "DFQR", "代付确认");
//		MenuBean ZHJYMXCX = new MenuBean(30, 39, "M", "ZHJYMXCX", "账户交易明细查询");
//		MenuBean ZHSZMXCX = new MenuBean(30, 109, "M", "ZHSZMXCX", "账户收支明细查询");
		

		
//		wdzh.addChild(ZHXXWH);
//		wdzh.addChild(ZHSZMXCX);
//		wdzh.addChild(ZHYECX);
		wdzh.addChild(ZHCZ);
		wdzh.addChild(ZHTX);
		wdzh.addChild(ZFMMWH);
//		wdzh.addChild(WYFK);
//		wdzh.addChild(DBDK);
//		wdzh.addChild(PLDK);
		wdzh.addChild(FKDQYYHK);
		wdzh.addChild(PLFKDQYYHK);
		wdzh.addChild(DBFKDYHK);
		wdzh.addChild(PLFKDYHK);
//		wdzh.addChild(ZHJYMXCX);
//		wdzh.addChild(ZHJYLSCX);
		wdzh.addChild(DFQR);
		//添加上传凭证
		wdzh.addChild(SCPZ);
		merMenus.add(jtgl);
		merMenus.add(tkgl);
		merMenus.add(jsgl);
		merMenus.add(shgl);
		//我的账户一级菜单
		merMenus.add(wdzh);
		
       // max 40
		
		
	
		
		
		// **admin菜单********************************************
		// TODO 94 可用 
		MenuBean a_jygl = new MenuBean(1, "交易管理");
		MenuBean a_dtjcx = new MenuBean(1, 6, "A", "DTJYCX", "当天收款交易查询");
		MenuBean a_mxcx = new MenuBean(1, 2, "A", "MXCX", "历史收款明细查询");
		MenuBean a_ddjycx = new MenuBean(1, 144, "A", "DDJYCX", "掉单交易查询");
		MenuBean a_dbcx = new MenuBean(1, 3, "A", "DBCX", "历史单笔查询");
		MenuBean a_xykzfcx = new MenuBean(1, 4, "A", "XYKZFCX", "信用卡支付查询 ");
		MenuBean a_shysry = new MenuBean(1, 5, "A", "SHYSRZ", "商户原始日志");
		MenuBean a_sjfxcx = new MenuBean(1, 42, "A", "SJFXCX", "数据分析查询");
		MenuBean a_sbjybfcx = new MenuBean(1, 67, "A", "SBJYBFCX", "失败交易备份查询");
		
		
		
		a_jygl.addChild(a_dtjcx);
		a_jygl.addChild(a_mxcx);
		a_jygl.addChild(a_dbcx);
		a_jygl.addChild(a_shysry);
		a_jygl.addChild(a_xykzfcx);
		a_jygl.addChild(a_sjfxcx);
		a_jygl.addChild(a_sbjybfcx);
		a_jygl.addChild(a_ddjycx);
		
		MenuBean a_xxczjycx = new MenuBean(8, 119, "A", "XXCZCX", "线下充值交易查询");
		MenuBean a_sgdzcx = new MenuBean(15, 27, "A", "SGDZCX", "手工调账查询");
		MenuBean a_tkgcx = new MenuBean(8, 9, "A", "TKCX", "退款查询");
		MenuBean a_tkyxjb = new MenuBean(8, 12, "A", "TKJB", "退款经办");
		MenuBean a_tkyxsh = new MenuBean(8, 13, "A", "TKSH", "退款审核");
		MenuBean a_ljtk=new MenuBean(8,124,"A","LJTK","联机退款");
		
		MenuBean a_aflsmxcx = new MenuBean(8, 118, "A", "DFLSMXCX", "出款交易查询");
		MenuBean a_jybbtjcc = new MenuBean(8, 132, "A", "JYBBTJCX", "交易报表统计查询");
		MenuBean a_dfjycx = new MenuBean(1, 150, "A", "DFJYCX", "代付交易查询");
		MenuBean a_dfjgtb = new MenuBean(1, 151, "A", "DFJGTB", "代付结果同步");
		MenuBean a_sddrdd = new MenuBean(1, 154, "A", "SDDRDD", "手动导入订单信息");
		MenuBean a_ljtkjgcx = new MenuBean(1, 167, "A", "LJTKJGCX", "联机退款结果查询");
		
		a_jygl.addChild(a_sgdzcx);
		a_jygl.addChild(a_tkgcx);
		a_jygl.addChild(a_tkyxjb);
		a_jygl.addChild(a_tkyxsh);
		a_jygl.addChild(a_aflsmxcx);
		a_jygl.addChild(a_xxczjycx);
		a_jygl.addChild(a_ljtk);
		a_jygl.addChild(a_ljtkjgcx);
		a_jygl.addChild(a_jybbtjcc);
		a_jygl.addChild(a_dfjycx);
		a_jygl.addChild(a_dfjgtb);
		a_jygl.addChild(a_sddrdd);
		//TODO 8 可以用
//		MenuBean a_tkgl = new MenuBean(8, "退款管理");
//		MenuBean a_tkgcx = new MenuBean(8, 9, "A", "TKCX", "退款查询");
//		MenuBean a_tkyxjb = new MenuBean(8, 12, "A", "TKJB", "退款经办");
//		MenuBean a_tkyxsh = new MenuBean(8, 13, "A", "TKSH", "退款审核");
//		a_tkgl.addChild(a_tkgcx);
//		a_tkgl.addChild(a_tkyxjb);
//		a_tkgl.addChild(a_tkyxsh);
	
		MenuBean a_jsgl = new MenuBean(15, "结算管理");
		MenuBean a_dz = new MenuBean(15, 16, "A", "DZ", "对账");
		MenuBean a_jsfq = new MenuBean(15, 18, "A", "JSFQ", "结算发起");
		MenuBean a_jssbcx = new MenuBean(15, 145, "A", "JSSBCX", "结算发起失败查询");
		MenuBean a_jszb = new MenuBean(15, 19, "A", "JSZB", "结算制表");
		MenuBean a_jsqr = new MenuBean(15, 20, "A", "JSQR", "结算确认");
		MenuBean a_shjsdzpz = new MenuBean(15, 163, "A", "SHJSDTZPZ", "商户结算单通知配置");
		MenuBean a_shjsdzjgcx = new MenuBean(15, 164, "A", "SHJSDTZJGCX", "商户结算单通知结果查询");
		MenuBean a_shjsdtb = new MenuBean(15, 161, "A", "SHJSDTB", "商户结算单同步");
		MenuBean a_ysyhjyk = new MenuBean(15, 21, "A", "YSYHJYK", "应收银行交易款");
		MenuBean a_shjsdcx = new MenuBean(15, 22, "A", "SHJSDCX", "商户结算单查询 ");
		MenuBean a_yecx = new MenuBean(15, 23, "A", "YECX", "余额查询");
		MenuBean a_zhlscx = new MenuBean(15, 24, "A", "ZHLSCX_28", "账户流水查询");
		
		MenuBean a_ddsgtj = new MenuBean(15, 25, "A", "DDSGTJ", "掉单手工提交");
//		MenuBean a_sgdz = new MenuBean(15, 116, "A", "SGDZ", "手工调账");
		MenuBean a_sgdzqq = new MenuBean(15, 26, "A", "SGDZQQ", "手工调账请求 ");
//		MenuBean a_sgdzcx = new MenuBean(15, 27, "A", "SGDZCX", "手工调账查询");
		MenuBean a_sgdzsh = new MenuBean(15, 28, "A", "SGDZSH", "手工调账审核");
		MenuBean a_syb = new MenuBean(15, 29, "A", "SYB", "收益表");
		MenuBean a_shrjsb = new MenuBean(15, 30, "A", "SHRJSB", "商户日结算表");
		MenuBean a_sdzjgcl = new MenuBean(15, 66, "A", "DZJGCL", "对账结果处理");
		
		
		MenuBean a_gdyshjyk = new MenuBean(15, 74, "A", "WGDYSHJYK", "网关对应商户交易款");
		MenuBean a_hkjgcx = new MenuBean(15, 127, "A", "HKJGCX", "划款结果查询");
		MenuBean a_shzwcx = new MenuBean(15, 129, "A", "SHZWCX", "商户账务查询");
		MenuBean a_autosettlement=new MenuBean(15,131,"A","ZDJSHKJG","划款结果处理");
		MenuBean a_posSyncConfig=new MenuBean(15,146,"A","PosSyncConfig","POS数据同步配置");
		MenuBean a_zjmxcx = new MenuBean(15,147,"A","ZJMXCX","资金明细查询");
		MenuBean a_shwgjytjcx = new MenuBean(15,166,"A","SHWGJYTJCX","商户网关交易统计查询");
		a_jsgl.addChild(a_dz);//            
		a_jsgl.addChild(a_sdzjgcl);
		a_jsgl.addChild(a_jsfq);
		a_jsgl.addChild(a_jssbcx);
		a_jsgl.addChild(a_jszb);
		a_jsgl.addChild(a_jsqr);
		a_jsgl.addChild(a_shjsdzpz);
		a_jsgl.addChild(a_shjsdzjgcx);
		a_jsgl.addChild(a_shjsdtb);
		a_jsgl.addChild(a_ysyhjyk);
		a_jsgl.addChild(a_shjsdcx);
		a_jsgl.addChild(a_yecx);
		a_jsgl.addChild(a_zhlscx);
		a_jsgl.addChild(a_autosettlement);
		a_jsgl.addChild(a_ddsgtj);
//		a_jsgl.addChild(a_sgdz);
		a_jsgl.addChild(a_sgdzqq);
//		a_jsgl.addChild(a_sgdzcx);
		a_jsgl.addChild(a_sgdzsh);
		a_jsgl.addChild(a_syb);
		a_jsgl.addChild(a_shrjsb);
		a_jsgl.addChild(a_gdyshjyk);
		a_jsgl.addChild(a_hkjgcx);
		a_jsgl.addChild(a_shzwcx);
		a_jsgl.addChild(a_posSyncConfig);
		a_jsgl.addChild(a_zjmxcx);
		a_jsgl.addChild(a_shwgjytjcx);

		// 31 商户管理 left.jsp
		MenuBean a_shgl = new MenuBean(31, "商户管理");
		MenuBean a_shxx = new MenuBean(31,32,"商户信息");
		MenuBean a_shpz = new MenuBean(31,37,"商户配置");
		MenuBean a_czypz = new MenuBean(31,43,"操作员配置");
		
		
		
		
		//为三级菜单的加上标示3
		MenuBean a_shxz = new MenuBean(32, 33,"A", "SHXZ", "商户新增");
		MenuBean a_shzs = new MenuBean(32, 34, "A", "SHZS", "商户证书");
		MenuBean a_shxxcx = new MenuBean(32, 35, "A", "SHXXCX", "商户信息查询");
		MenuBean a_shxxxg = new MenuBean(32, 36,"A", "SHJBXXXG", "商户基本信息修改");
		MenuBean a_shzyxxxgsq = new MenuBean(32, 38,"A", "SHZYXXXG", "商户重要信息修改申请");
		MenuBean a_shzyxxxgsh = new MenuBean(32, 136,"A", "SHZYXXXGSH", "商户重要信息修改审核");
		
		MenuBean a_SHZLSC = new MenuBean(32, 89, "A", "SHZLSC", "商户资料上传");
//		MenuBean a_shzsgl = new MenuBean(32, 160, "A", "SHZSGL", "商户证书管理");
		a_shxx.addChild(a_shxz);
		a_shxx.addChild(a_shzs);
		a_shxx.addChild(a_shxxcx);
		a_shxx.addChild(a_shxxxg);
		a_shxx.addChild(a_shzyxxxgsq);
		a_shxx.addChild(a_shzyxxxgsh);
		a_shxx.addChild(a_SHZLSC);
//		a_shxx.addChild(a_shzsgl);
		
		MenuBean a_sxfcx = new MenuBean(37, 39, "A", "SXFCX", "手续费查询");
		MenuBean a_shwgpzsq = new MenuBean(37, 68, "A", "SHWGPZSQ", "商户网关配置申请");
		MenuBean a_shwgpzsh = new MenuBean(37, 137, "A", "SHWGPZSH", "商户网关配置审核");
//		MenuBean a_shwgpzcx = new MenuBean(37, 138, "A", "SHWGPZCX", "商户网关配置查询");
		MenuBean a_zhgnpz = new MenuBean(37, 122, "A", "ZHGNPZ", "账户功能配置");
		MenuBean a_shfxxspz = new MenuBean(37, 69,"A", "SHFXXSPZ", "商户风险系数配置");
		MenuBean a_shkq = new MenuBean(37, 40,"A", "SHKQ", "商户开启");
		MenuBean a_shgb = new MenuBean(37,73,"A", "SHGB", "商户关闭");
//		MenuBean a_plxgwgflsq = new MenuBean(37,140,"A","PLXGWGFLSQ","按类型修改网关费率申请");
//		MenuBean a_plxgwgflsh = new MenuBean(37,141,"A","PLXGWGFLSH","按类型修改网关费率审核");
		a_shpz.addChild(a_sxfcx);
		a_shpz.addChild(a_shkq);
		a_shpz.addChild(a_shwgpzsq);
		a_shpz.addChild(a_shwgpzsh);
//		a_shpz.addChild(a_shwgpzcx);
		a_shpz.addChild(a_shfxxspz);
//		a_shpz.addChild(a_plxgwgflsq);
//		a_shpz.addChild(a_plxgwgflsh);
		a_shpz.addChild(a_shgb);
		a_shpz.addChild(a_zhgnpz);
		
		MenuBean a_xgmm = new MenuBean(43, 51, "A", "XGMM", "修改密码");
		MenuBean a_czyxz = new MenuBean(43, 44,"A", "CZYXZ", "操作员新增");
		MenuBean a_czyxg = new MenuBean(43, 45, "A", "CZYXG", "操作员修改");
		MenuBean a_czycx = new MenuBean(43, 46, "A", "CZYCX", "操作员查询");
		MenuBean a_czymmcz = new MenuBean(43, 47,"A", "CZYMMCZ", "操作员密码重置");
		MenuBean a_czyqxxgsq = new MenuBean(43, 48,"A", "CZYQXXGSQ", "操作员权限修改申请");
		MenuBean a_czyqxxgsh = new MenuBean(43, 139,"A", "CZYQXXGSH", "操作员权限修改审核");
		MenuBean a_czyqxcx = new MenuBean(43, 49,"A", "CZYQXCX", "操作员权限查询");
		MenuBean a_czyrzcx = new MenuBean(43, 50,"A", "CZYRZCX", "操作员日志查询");
		MenuBean a_xtzymktj = new MenuBean(43, 156,"A", "XTZYMKTJ", "系统重要模块统计");//系统重要模块统计
		MenuBean a_xtyhdlfx = new MenuBean(43, 157,"A", "XTYHDLFX", "系统用户登录分析");//系统用户登录分析
		MenuBean a_shqxxg = new MenuBean(43, 130,"A", "SHQXXG", "商户权限修改");
		MenuBean a_dtlpgl = new MenuBean(43, 158,"A", "DTLPGL", "动态令牌管理");
		MenuBean a_szfwxx = new MenuBean(43, 159,"A", "SZFWXX", "防伪信息设置");
		a_czypz.addChild(a_xgmm);
		a_czypz.addChild(a_czyxz);
		a_czypz.addChild(a_czyxg);
		a_czypz.addChild(a_czycx);
		a_czypz.addChild(a_czymmcz);
		a_czypz.addChild(a_czyqxxgsq);
		a_czypz.addChild(a_czyqxxgsh);
		a_czypz.addChild(a_czyqxcx);
		a_czypz.addChild(a_czyrzcx);
		a_czypz.addChild(a_xtzymktj);
		a_czypz.addChild(a_xtyhdlfx);
		a_czypz.addChild(a_shqxxg);
		a_czypz.addChild(a_dtlpgl);
		a_czypz.addChild(a_szfwxx);
		a_shgl.addChild(a_shxx);
		a_shgl.addChild(a_shpz);
		a_shgl.addChild(a_czypz);  
		
		MenuBean a_fxgl = new MenuBean(52, "风险管理");
		MenuBean a_kyjycx = new MenuBean(52, 53, "A", "KYJYCX", "可疑交易查询");
		MenuBean a_hmdgl = new MenuBean(52, 54, "A", "HMDGL", "黑名单管理");
		MenuBean a_bmdgl = new MenuBean(52, 55, "A", "BMDGL", "白名单管理");
		MenuBean a_ssjyxs = new MenuBean(52, 56, "A", "SSJYXS", "实时交易显示");
		MenuBean a_fxjylr = new MenuBean(52, 70, "A", "FXJYLR", "风险交易录入");
		MenuBean a_FXJYSH = new MenuBean(52, 71, "A", "FXJYSH", "风险交易审核");
		MenuBean a_FXJYCL = new MenuBean(52, 72, "A", "FXJYCL", "风险交易处理");
		a_fxgl.addChild(a_kyjycx);
		a_fxgl.addChild(a_hmdgl);
		a_fxgl.addChild(a_bmdgl);
		a_fxgl.addChild(a_ssjyxs);
		a_fxgl.addChild(a_fxjylr);
		a_fxgl.addChild(a_FXJYSH);
		a_fxgl.addChild(a_FXJYCL);

		MenuBean a_xtpz = new MenuBean(57, "系统配置");
		MenuBean a_ZJYHWG = new MenuBean(57, 58, "A", "ZFQDWH", "支付渠道维护");
		MenuBean a_XGYHGW = new MenuBean(57, 59, "A", "YHWGWH", "银行网关维护");
		MenuBean a_SJKWH = new MenuBean(57, 60, "A", "SJKWH", "数据库维护");
		MenuBean a_TZXXXZ = new MenuBean(57, 61, "A", "TZXXXZ", "通知信息新增");
		MenuBean a_TZXXXG = new MenuBean(57, 62, "A", "TZXXXG", "通知信息修改");
		MenuBean a_XTCSPZ = new MenuBean(57, 64, "A", "XTCSPZ", "系统参数配置");
		MenuBean a_PLXGWGQDSQ = new MenuBean(57, 142, "A", "PLXGWGQDSQ", "批量修改网关渠道申请");
		MenuBean a_PLXGWGQDSH = new MenuBean(57, 143, "A", "PLXGWGQDSH", "批量修改网关渠道审核");
		MenuBean a_YQZLWGWH = new MenuBean(90, 77, "A", "YQZLWGWH", "银企直连网关维护");
		MenuBean a_SHIPPZ = new MenuBean(90, 78, "A", "SHIPPZ", "商户IP配置");
		MenuBean a_DFYHZHYEBJ = new MenuBean(90, 123, "A", "DFYHZHYEBJ", "代付银行账户余额报警配置");
		MenuBean a_DFSBBJPZ = new MenuBean(90, 133, "A", "DFSBBJPZ", "代付失败报警配置");
		MenuBean a_JKPZ = new MenuBean(90, 148, "A", "JKPZ", "监控配置");
		MenuBean a_B2EQBALANCE=new MenuBean(90,155,"A","B2EQBALANCE","银企直连余额查询");
		MenuBean a_DFWGPZ=new MenuBean(90,152,"A","DFWGPZ","代付网关配置");
		MenuBean a_LHHWH=new MenuBean(90,153,"A","LHHWH","联行号维护");
		a_xtpz.addChild(a_ZJYHWG);
		a_xtpz.addChild(a_XGYHGW);
		a_xtpz.addChild(a_SJKWH);
		a_xtpz.addChild(a_TZXXXZ);
		a_xtpz.addChild(a_TZXXXG);
		a_xtpz.addChild(a_XTCSPZ);
		a_xtpz.addChild(a_PLXGWGQDSQ);
		a_xtpz.addChild(a_PLXGWGQDSH);
		a_xtpz.addChild(a_DFYHZHYEBJ);
		a_xtpz.addChild(a_YQZLWGWH);
		a_xtpz.addChild(a_SHIPPZ);
		a_xtpz.addChild(a_DFSBBJPZ);
		a_xtpz.addChild(a_JKPZ);
		a_xtpz.addChild(a_B2EQBALANCE);
		a_xtpz.addChild(a_DFWGPZ);
		a_xtpz.addChild(a_LHHWH);
		
		MenuBean a_zdjs = new MenuBean(65, "自动结算");
		//MenuBean a_YHZHLR = new MenuBean(65, 7, "A", "YHZHLR", "银行账号录入");
//		MenuBean a_YHZHXG = new MenuBean(65, 10, "A", "YHZHXG", "银行账号修改");
//		MenuBean a_YHYECX= new MenuBean(65, 11, "A", "YHYECX", "银行余额查询");
		MenuBean a_HKLR = new MenuBean(65, 14, "A", "HKLR", "划款录入");
		MenuBean a_HKQR = new MenuBean(65, 17, "A", "HKQR", "划款确认");
		MenuBean a_HKJGCL= new MenuBean(65, 88, "A", "HKJGCL", "划款结果处理");
		MenuBean a_DBHKCX = new MenuBean(65, 41, "A", "DBHKCX", "单笔划款查询");
		MenuBean a_LSHKMXCX = new MenuBean(65, 63, "A", "LSHKMXCX", "划款明细查询");
		
//		a_zdjs.addChild(a_YHZHLR);
		
		//TODO 7 ，10，11可以使用
		
//		a_zdjs.addChild(a_YHZHXG);
//		a_zdjs.addChild(a_YHYECX);
		a_zdjs.addChild(a_HKLR);
		a_zdjs.addChild(a_HKQR);
		a_zdjs.addChild(a_HKJGCL);
		a_zdjs.addChild(a_DBHKCX);
		a_zdjs.addChild(a_LSHKMXCX);
		
		//MenuBean a_bfjgl = new MenuBean(87, "备份金管理");
		//MenuBean a_BFJYHGL = new MenuBean(87, 75, "A", "BFJYHGL", "备份金银行关联");
//		MenuBean a_YHYECX_$ = new MenuBean(87, 76, "A", "YHYECX", "备份金银行余额查询");
//		MenuBean a_SHLSCX= new MenuBean(87, 77, "A", "SHLSCX", "商户流水查询");
//		MenuBean a_JYCX = new MenuBean(87, 78, "A", "JYCX", "备份金银行交易查询");
//		MenuBean a_CCTKSQ = new MenuBean(87, 79, "A", "CCTKSQ", "差错退款申请");
//		MenuBean a_CCTKJB = new MenuBean(87, 80, "A", "CCTKJB", "差错退款经办");
//		MenuBean a_CCTKSH = new MenuBean(87, 81, "A", "CCTKSH", "差错退款审核");
//		MenuBean a_SHZJGJSQ = new MenuBean(87, 82, "A", "SHZJGJSQ", "商户资金归集申请");
//		MenuBean a_SHZJGJSH = new MenuBean(87, 83, "A", "SHZJGJSH", "商户资金归集审核");
//		MenuBean a_YHDZSQ = new MenuBean(87, 84, "A", "YHDZSQ", "备份金银行调帐申请");
//		MenuBean a_YHDZSH = new MenuBean(87, 85, "A", "YHDZSH", "备份金银行调帐审核");
//		MenuBean a_SCCGSJ = new MenuBean(87, 86, "A", "SCCGSJ", "生成存管数据");
		//a_bfjgl.addChild(a_BFJYHGL);
		
//		a_bfjgl.addChild(a_YHYECX_$);
//		a_bfjgl.addChild(a_SHLSCX);
//		a_bfjgl.addChild(a_JYCX);
//		a_bfjgl.addChild(a_CCTKSQ);
//		a_bfjgl.addChild(a_CCTKJB);
//		a_bfjgl.addChild(a_CCTKSH);
//		a_bfjgl.addChild(a_SHZJGJSQ);
//		a_bfjgl.addChild(a_SHZJGJSH);
//		a_bfjgl.addChild(a_YHDZSQ);
//		a_bfjgl.addChild(a_YHDZSH);
//		a_bfjgl.addChild(a_SCCGSJ);
		
		//账户管理
		MenuBean a_zhgl = new MenuBean(90, "账户管理");
//		MenuBean a_GRRZWH = new MenuBean(90, 91, "A", "GRRZWH", "个人认证处理");
//		MenuBean a_DBHKJGCX = new MenuBean(90, 91, "A", "DFFXJYCX", "代发风险交易查询");
		MenuBean a_ZHXXWH = new MenuBean(90, 92, "A", "ZHXXWH", "账户信息维护");
//		MenuBean a_ZHJYMXCX = new MenuBean(90, 93, "A", "ZHJYMXCX", "账户交易明细查询");
//		MenuBean a_SHYECX = new MenuBean(90, 112, "A", "SHYECX", "商户余额查询");
		MenuBean a_ZHFXPZ = new MenuBean(90, 96, "A", "ZHFXPZ", "账户交易风险配置");
//		MenuBean a_ZHSZMX = new MenuBean(90, 108, "A", "ZHSZMXCX", "账户收支明细查询");
		MenuBean a_TXCL = new MenuBean(90, 95, "A", "TXCL", "提现经办");
//		MenuBean a_DFJB = new MenuBean(90, 75, "A", "DFJB", "代发经办");
//		MenuBean a_DFJB = new MenuBean(90, 76, "A", "DFJB", "代发经办");
//		MenuBean a_GRXXWH = new MenuBean(90, 97, "A", "GRXXWH", "个人信息维护");
		MenuBean a_ZHMMCZ = new MenuBean(90, 104, "A", "ZHMMCZ", "支付密码重置");
		MenuBean a_SPPZ = new MenuBean(90, 106, "A", "SPPZ", "线下充值凭证审批");
		MenuBean a_TXSH = new MenuBean(90, 110, "A", "TXCL", "提现审核");
//		MenuBean a_DFJBSH = new MenuBean(90, 111, "A", "DFJBSH", "代发审核");
//		MenuBean a_ZHLSCX = new MenuBean(90, 113, "A", "ZHLSCX", "账户流水查询");
		MenuBean a_XXCZJB = new MenuBean(90, 114, "A", "XXCZJB", "线下充值经办");
		MenuBean a_XXCZSH = new MenuBean(90, 115, "A", "XXCZSH", "线下充值审核");
		MenuBean a_SGDF=new MenuBean(90,125,"A","SGDF","手工代付申请");
		MenuBean a_SGDFSH=new MenuBean(90,135,"A","SGDFSH","手工代付审核");
		MenuBean a_SGSYNCDF=new MenuBean(90,126,"A","SGSYNCDF","手工同步代付结果");
		MenuBean a_ZDDFXXWH=new MenuBean(90,128,"A","ZDDFXXWH","自动代付信息维护");
		MenuBean a_SGDFCX=new MenuBean(90,149,"A","SGDFCX","手工代付查询");
		
		MenuBean a_KJZFYHXX=new MenuBean(90,162,"A","KJZFYHXX","快捷支付用户信息查询");
//		a_zhgl.addChild(a_GRXXWH);
//		a_zhgl.addChild(a_DBHKJGCX);
//		a_zhgl.addChild(a_ZHJYMXCX);
//		a_zhgl.addChild(a_ZHSZMX);
//		a_zhgl.addChild(a_SHYECX);
		a_zhgl.addChild(a_ZHFXPZ);
//		a_zhgl.addChild(a_ZHLSCX);
		a_zhgl.addChild(a_ZHXXWH);
		a_zhgl.addChild(a_ZHMMCZ);
//		a_zhgl.addChild(a_GRRZWH);
//		a_zhgl.addChild(a_ZHJYLSCX);
		a_zhgl.addChild(a_TXCL);
		a_zhgl.addChild(a_TXSH);
/*		a_zhgl.addChild(a_DFJB);
		a_zhgl.addChild(a_DFJBSH);*/
		a_zhgl.addChild(a_XXCZJB);
		a_zhgl.addChild(a_XXCZSH);
		a_zhgl.addChild(a_SPPZ);
		a_zhgl.addChild(a_SGDF);
		a_zhgl.addChild(a_SGDFSH);
		a_zhgl.addChild(a_SGSYNCDF);
		a_zhgl.addChild(a_ZDDFXXWH);
		a_zhgl.addChild(a_SGDFCX);
		a_zhgl.addChild(a_KJZFYHXX);
		adminMenus.add(a_jygl);// 交易管理
//		adminMenus.add(a_tkgl);// 退款管理
		adminMenus.add(a_jsgl);// 结算管理
		adminMenus.add(a_zdjs);// 自动结算
		//adminMenus.add(a_bfjgl);//备付金管理
		adminMenus.add(a_shgl);// 商户管理
		adminMenus.add(a_fxgl);// 风险管理
		adminMenus.add(a_xtpz);// 系统配置
		adminMenus.add(a_zhgl);// 账户管理

		
	}
	public static void main(String[] args) {
		isAuthIdConflict();
	}
	public static boolean isAuthIdConflict(){
		boolean flag = false;
		Comparator<MenuBean> comparator = new Comparator<MenuBean>(){
			@Override
			public int compare(MenuBean o1, MenuBean o2) {
				if(o1 == o2){
					return 0;
				}else if(o1.getId() <= o2.getId()){
					return -1;
				}else{
					return 1;
				}
			}
			
		};
		//检查商户菜单
		Map<Integer,Integer> countMap = new HashMap<Integer,Integer>();
		//增加按钮权限统计 @see	MerchantService#searchButtonAuth1(String mid,int operId)
		List<MenuBean> menus =  new MerchantService().searchButtonAuth("1", 0);
		menus.addAll(adminMenus);
		doCount(countMap,menus);
		Set<MenuBean> conflictMenus = new TreeSet<MenuBean>(comparator);
		getConflictMenus(getConflictIds(countMap),menus,conflictMenus);
		logger.info("检查是否存在重复的菜单项");
		if(CollectionUtils.isNotEmpty(conflictMenus)){
			flag = true;
			for (MenuBean menuBean : conflictMenus) {
				logger.info("重复的管理后台菜单项  "+menuBean.getId()+"：name = "+menuBean.getName()+", " + "jsp = "+menuBean.getJsp());
			}
		}
		//检查管理菜单
		countMap = new HashMap<Integer,Integer>();
		//增加按钮权限统计 @see	MerchantService#searchButtonAuth1(String mid,int operId)
//		merMenus.addAll(buttonMenus); //商户后台 不检查按钮权限和菜单权限的冲突
		doCount(countMap,merMenus);
		conflictMenus = new TreeSet<MenuBean>(comparator);
		getConflictMenus(getConflictIds(countMap),merMenus,conflictMenus);
		if(CollectionUtils.isNotEmpty(conflictMenus)){
			flag = true;
			for (MenuBean menuBean : conflictMenus) {
				logger.info("重复的商户后台菜单项  "+menuBean.getId()+"：name = "+menuBean.getName()+", " + "jsp = "+menuBean.getJsp());
			}
		}
		if(!flag){
			logger.info("没有重复的菜单项");
		}
		return flag;
	}
	
	private static void doCount(Map<Integer,Integer> countMap, List<MenuBean> list){
		for (MenuBean menuBean : list) {
			if(CollectionUtils.isNotEmpty(menuBean.getChildren())){
				doCount(countMap,menuBean.getChildren());
			}
			Integer count = countMap.get(menuBean.getId());
			if(null == count){
				countMap.put(menuBean.getId(), 1);
			}else{
				countMap.put(menuBean.getId(), count + 1);
			}
		}
	}
	
	private static List<Integer> getConflictIds(Map<Integer,Integer> countMap){
		List<Integer> conflictIds = new ArrayList<Integer>();
		Set<Entry<Integer, Integer>> entries = countMap.entrySet();
		for (Entry<Integer, Integer> entry : entries) {
			if(entry.getValue()>1){
				conflictIds.add(entry.getKey());
			}
		}
		return conflictIds;
	}
	
	private static void getConflictMenus(List<Integer> ids, List<MenuBean> list, Set<MenuBean> conflictMenus) {
		for (MenuBean menuBean : list) {
			if (CollectionUtils.isNotEmpty(menuBean.getChildren())) {
				getConflictMenus(ids, menuBean.getChildren(), conflictMenus);
			}
			for (Integer cid : ids) {
				if (cid.equals(menuBean.getId())) {
					conflictMenus.add(menuBean);
				}
			}
		}
	}

	public static List<Integer> getMyaccountPid(){
		List<MenuBean> childrens=wdzh.getChildren();
		myacountpid.add(wdzh.getChildren().get(0).getPid());
		for (int i = 0; i < childrens.size(); i++) {
			myacountpid.add(childrens.get(i).getId());
		}
		return myacountpid;
	}
	/***
	 * 权限修改  
	 * @param authstr
	 * @param role
	 * @param authOld
	 * @return
	 */
	public static String genUserAuth(String[] authstr, int role,String authOld,String menu_nc) {
		String retAuth="";
		char[] aAuth = authOld.toCharArray();
		for (String authIndex : authstr) {
			int temp = Integer.parseInt(authIndex);
			if(aAuth.length>temp){
				aAuth[temp] = '1';
			}else{
				int num=temp-aAuth.length;
				String ah="";
				for (int i = 0; i < num-1; i++) {
					ah=ah+"0";
				}
				authOld=authOld+ah;		
				aAuth = authOld.toCharArray();
					if(aAuth.length>temp){
						aAuth[temp] = '1';
				}
			}
		}
//		修改置空权限
		if(!menu_nc.equals("")){
			String[] auth_nc=menu_nc.split(",");
			for(String mNcIndex:auth_nc){
				int temp = Integer.parseInt(mNcIndex);
				aAuth[temp]='0';
			}
		}
		for (char c : aAuth) {
			retAuth += String.valueOf(c);
		}
		return retAuth;
	}

}
