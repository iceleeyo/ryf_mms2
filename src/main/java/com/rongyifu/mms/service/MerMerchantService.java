package com.rongyifu.mms.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.MinfoH;
import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.merchant.MenuBean;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MerMerchantService {
	
	private MerInfoDao merInfoDao = new MerInfoDao();
	private MerOperDao merOperDao = new MerOperDao();
	private OperAuthDao operAuthDao = new OperAuthDao();
	
	private String doImportRSAFile(String mid, String fileText) {
		// C#格式的xml文件
		if (fileText.charAt(0) == '<' && fileText.charAt(12) == '>') {
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(fileText);
				Element root = doc.getRootElement();
				String Modulus = root.element("Modulus").getText();
				String Exponent = root.element("Exponent").getText();
				String pubKey = getPublicKey(Modulus, Exponent);
				int id = merInfoDao.updateMerPubKey(pubKey, mid);
				if (id != 1) return "操作失败";
				return AppParam.SUCCESS_FLAG;

			} catch (Exception e) {
				return "文件内容错误";
			}

		} else if (fileText.indexOf("<") == -1 && fileText.indexOf(">") == -1) {

			int id = merInfoDao.updateMerPubKey(fileText, mid);
			if (id != 1) return "操作失败";
			return AppParam.SUCCESS_FLAG;

		} else {
			return "上传的文件格式错误";
		}
	}
	
	private String getPublicKey(String modulus, String publicExponent) throws Exception {
		byte[] m1 = Base64.decode(modulus);
		byte[] e1 = Base64.decode(publicExponent);

		BigInteger m = new BigInteger(m1);
		BigInteger e = new BigInteger(e1);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return Base64.encode(publicKey.getEncoded());
	}
	
	public String addMerRsaFile(String mid, String rsaFile) {
		if (mid.trim().length()==0|| Ryt.empty(rsaFile)) return "参数输入错误";
		Minfo mer = null;
		try {
			mer = merInfoDao.getOneMinfo(mid);
		} catch (Exception e) {
			return "商户不存在";
		}
		if (null == mer) return "商户不存在";
		if (!Ryt.empty(mer.getPublicKey())) return "商户已导入RSA公钥";
		return doImportRSAFile(mid, rsaFile.trim());
	}
	
	/**
	 * 获得单个商户对象
	 * @param mid
	 * @return
	 */
	public Minfo getOneMinfo(String mid) {
		//search_edit_info.jsp中调用
		Minfo m = null;
		try {
			m = merInfoDao.getOneMinfo(mid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return m;
	}
	/**
	 * 查询商户重要信息
	 * @param mid
	 * @return
	 */
	public Minfo getImportentMsgByMid(String mid){
		
		return merInfoDao.getImportentMsgByMid(mid);
	}
	
	/**
	 * 修改商户重要信息
	 * @param mid
	 * @param minfoName
	 * @param abbrev
	 * @param category
	 * @param codeExpDate
	 * @return
	 */
	public String updateMinfoImportantData(Minfo minfo){
		String mid=minfo.getId();
		if(merInfoDao.isExistMinfoName(minfo.getName(),minfo.getAbbrev(),mid)){
			return "商户简称("+minfo.getAbbrev()+")已经存在。";
		}
		String backStr="修改失败！";
		 int row=merOperDao.updateImportantMsg(minfo);
		if (row == 1) {
			/* 存管系统不上，先注释*/ 
//			merOperDao.addCgTask( "M", String.valueOf(mid));
			RYFMapUtil.getInstance().refreshMinfoMap(mid);//刷新缓存
			// 刷新ets
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "minfo");
			p.put("k", mid);
			backStr= EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "修改成功，但刷新ewp失败!";
		}
		return backStr;
	}
	
	/**
	 *修改商户基本信息
	 *@param 商户对象
	 *@return 成功或失败信息
	 */
	public String editMinfos(Minfo minfo) {
		//search_edit_info.jsp中调用
		try {
			merInfoDao.editMinfos(minfo);
		} catch (Exception e) {
			e.printStackTrace();
			return "修改失败！";
		}
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "minfo");
		p.put("k", minfo.getId());
		return EWPService.refreshEwpETS(p) ? "修改成功!" : "资料修改成功，刷新ewp失败!";
	}
	
	/**
	 * 商户联系人信息增加或者修改
	 * @param minfo
	 * @return
	 */
	public String editMinfoContact(MinfoH minfo) {
		//edit_Minfo.jsp和search_edit_info.jsp中调用
		try {
			int row=merInfoDao.editMinfoContact(minfo);
			return row==1?AppParam.SUCCESS_FLAG:"修改失败";
		} catch (Exception e) {
			e.printStackTrace();
			return "修改失败";
		}
	}
	
	/**
	 *添加商户基本信息增加
	 *@param 商户对象
	 *@return 成功或失败信息
	 */
	public String addMinfos(MinfoH minfo, String modelflg) { //  add_info.jsp中调用
		if (merInfoDao.checkMerName(minfo.getName(), "0", minfo.getAbbrev())) { // 验证商户名是否被占用
			return "商户简称("+minfo.getAbbrev()+")已被占用！";
		}
		String mid;
		try {
			mid =""+merInfoDao.addMinfoBase(minfo);
		} catch (Exception e) {
			e.printStackTrace();
			return "增加失败";
		}

		RYFMapUtil obj = RYFMapUtil.getInstance();
		obj.refreshMinfoMap(mid);

		return String.valueOf(mid);
		//  这里不需要刷新ets
	}
	
	/**
	 * 后台修改用户权限
	 * @param menu
	 * @return
	 */
	public String addMenu(int operId,String menu,String menu_nc){
		String msg = "";
		LoginUser user=operAuthDao.getLoginUser();
		String mid=user.getMid();
		boolean authFlag=MenuService.hasThisAuth(user.getAuth(), 22);//id=22的为操作员权限修改
		if(!authFlag)return "您的权限不足！";
		if(operId==user.getOperId()){
			return "权限分配失败！无法自己给自己分配权限";
		}
		try {
			int role = operAuthDao.getRole(mid,operId);
			String authstr ="";
			if (menu.equals("")) {
				authstr = MenuService.genDefaultUserAuth(role);
				msg = "权限已清空！";
				operAuthDao.addMenu(authstr, mid,operId); 
			} else {
				String[] authId = menu.split(",");
				String authOld=operAuthDao.findAuth(String.valueOf(mid), String.valueOf(operId));
//				authstr = MenuService.genUserAuth(authId, role);
				authstr = MenuService.genUserAuth(authId, role, authOld,menu_nc);
				msg = "分配成功！";
				operAuthDao.addMenu(authstr, mid,operId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "权限分配失败！";
		}
		return msg;
	}
	
	/**
	 * 根据id查询该操作员现有的权限
	 * @param mid
	 * @param oid
	 * @return
	 */
	public String checkMenu(String mid, int oid) {
		//edit_oper_auth.jsp(jsp/merchant)中调用
		String auth = operAuthDao.checkMenu(mid, oid);
		if (auth != null) {
			return MenuService.queryUserAuthIndex(auth);
		} else {
			return "noAuth";
		}
	}
	
	/**
	 * 根据mid返回对应的融易通操作员的下拉框字符 （新增）
	 * @return
	 * @throws Exception 
	 */
	public Map<Integer, String> showOPers() throws Exception {
		String mid=merOperDao.getLoginUserMid();
		Map<Integer, String> opers = merOperDao.getHashOper(mid);
		return opers;
	}
	
	/**
	 * 根据operId查询权限
	 * @param operId
	 * @return
	 * @throws Exception 
	 */
	public List<MenuBean> searchOperAuth(int operId) throws Exception {
		String mid=merOperDao.getLoginUserMid();
		int myid=merOperDao.getLoginUser().getOperId();
//		List<MenuBean> authInfoList = MenuService.getMerMenu(); // 普通商户
		List<MenuBean> authInfoList =new ArrayList<MenuBean>();
		authInfoList=MenuService.getMerMenu2();
			//我的权限字符串
			String myauthStr=operAuthDao.checkMenu(mid, myid);
			//操作员权限字符串
			String authStr=operAuthDao.checkMenu(mid, operId);
				//判断是否包含我的账户
				if(!(myauthStr.charAt(30)+"").trim().equals("1")){
					if(authInfoList.size()>4){
						authInfoList.remove(4);
					}
				}else if(authInfoList.size()<=4){
					
				}
			setAuthInfo(authStr, authInfoList);
		return authInfoList;
	}
	/**
	 * 设置是否选择（即是否有权限）
	 * @param authArr
	 * @param authInfoList
	 */
	private void setAuthInfo(String authStr,List<MenuBean> authInfoList){
		for (int i = 0; i < authInfoList.size(); i++) {
			MenuBean menuBean=authInfoList.get(i);
			int id=menuBean.getId();
			Boolean checked=MenuService.hasThisAuth(authStr,id);//是否有权限
			authInfoList.get(i).setChecked(checked);
			if(menuBean.getChildren()!=null){
				setAuthInfo(authStr, menuBean.getChildren());
			}
		}
	}
	/**
	 * 增加操作员
	 * @param oper_email Email地址
	 * @param oper_tel 电话号码
	 * @param oper_name 操作员名字
	 * @param operpass 密码
	 * @param login_name 操作员登录名
	 * @param minfo_id 商户ID
	 * @param role 操作员角色
	 * minfo_id 要添加操作员的商户号
	 * oper_mid 操作员的商户号  
	 * @return 返回增加操作的结果
	 */
	public String addOperInfo(String action, String ostate, String oper_email, String oper_tel, String oper_name,
					String operpass, int operid, String minfo_id,String oper_mid) {
		//operManage.jsp(jsp，admin)中调用
		if(!"1".equals(minfo_id)&&"1".equals(oper_mid)&&operAuthDao.isExistOper(minfo_id))return "此商户已增加有操作员，不能再添加！";
		if(!"1".equals(oper_mid)&&operAuthDao.queryOperNum(minfo_id)>=4){return "操作员数量已大于等于四个，不能再添加！";};
		int mtype = 0;
		if (action.equals("credit")) {
			mtype = 1;
		}
		RYFMapUtil obj = RYFMapUtil.getInstance();
		if (!obj.getMerMap().containsKey(minfo_id)) {
			return "该商户不存在";
		};
		if (operAuthDao.hasOper(operid, minfo_id, mtype) > 0) {
			return "操作员号已被占用，请重新输入";
		}
//		operpass = MD5.getMD5(operpass.getBytes());
		int role = 1;
		String auth="";
		if ("1".equals(minfo_id)) {
			role = 0;
		}else if(!"1".equals(minfo_id)&&"1".equals(oper_mid)){
			auth=MenuService.genAllUserAuth("1");
		}else{
			
			for (int i = 1; i < Constant.AUTHLEN; i++) {
				auth=auth+"0";
			}
			auth="1"+auth;
		}
		try {
			merOperDao.add(action, ostate, oper_email, oper_tel, oper_name, operpass, operid, minfo_id, mtype, role,auth);
		} catch (Exception e) {
			return "操作异常，请重新再试或与管理员联系！";
		}
		return AppParam.SUCCESS_FLAG;
	}
	
	/**
	 * 删除操作员
	 * @param oper_id 操作员ID
	 * @return 返回删除操作的结果
	 */
	public String deleteOperInfo(String mid, int oper_id) {
		//operManage.jsp(jsp/merchant)中调用
		try {
			merOperDao.deleteOper(mid, oper_id);
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		return AppParam.SUCCESS_FLAG;
	}
	
	/**
	 * 根据operName返回List集合里为OperInfo对象 (修改)
	 * @param mid
	 * @param operName
	 * @param t
	 * @return
	 */
	public CurrentPage<OperInfo> getOpers4Object(String mid, String operName, int pageNo) {
		//search_edit_oper.jsp中调用
		return merOperDao.getOpers4Object(mid, operName, pageNo);
	}
	
	/**
	 * 修改操作员信息
	 * @param oper_email Email地址
	 * @param oper_tel 电话号码
	 * @param oper_name 操作员名字
	 * @param operid 操作员ID
	 * @return 返回增修改操作的结果
	 */
	public String editOperInfo(String ostate, String oper_email, String oper_tel, String oper_name, String operid,
					String mid, int mtype) {
		//operManage.jsp(jsp/merchant)中调用
		try {
			merOperDao.edit(ostate, oper_email, oper_tel, oper_name, operid, mid, mtype);
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}

		return AppParam.SUCCESS_FLAG;
	}
	
	/**
	 * 修改密码
	 * @param mid_
	 * @param oper_id
	 * @param opass
	 * @param npass
	 * @return
	 */
	public String editPass(String mid, String oper_id, String opass, String npass) {
		//search_chang_oper_pass.jsp(jsp,admin)中调用
		try {
			int operId = Integer.parseInt(oper_id);
		
			if (Ryt.empty(npass)) return "请输入新密码。";
			String oldPass = merOperDao.getOldPass(0, operId, mid);
			if (!oldPass.equals(opass)) {
				return "原密码错误！";
			}
			return operAuthDao.updateOperPwd(npass, mid, operId, 0) ? "修改成功!" : "修改失败";
		} catch (Exception e) {
			return "修改异常";
		}
	}
	
	/**
	 * 查询消息通知（查询）
	 * @param notice
	 * @return
	 */
	public List<MMSNotice> getMessage(MMSNotice notice) {
		//queryMessage.jsp(jsp/merchant)中调用
		List<MMSNotice> l = null;
		try {
			notice.setMid(merOperDao.getLoginUser().getMid());
			l = new SystemDao().getMessage(notice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}
	
	/**
	 * 根据Id查找 消息通知
	 * @param id
	 * @return
	 */
	public MMSNotice getMessageById(int id) {
		//queryMessage.jsp(jsp/merchant)中调用
		if (id == 0) return null;
		try {
			return new SystemDao().getMessageById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 操作手册下载
	 * @return 
	 * @throws Exception 
	 * @throws Exception 
	 */
	public FileTransfer downLoadUserManual() throws Exception{
		 String saveurl =Ryt.getParameter("SettlementFilePath");
		   File file=new File(saveurl+"/operManual.doc");
		   ByteArrayOutputStream  bytebuffer = new ByteArrayOutputStream();
			try {
				InputStream is = new FileInputStream(file);
			   int buff=0;
			   byte[] b = new byte[1024]; 
				while((buff=is.read(b))!=-1){
					  bytebuffer.write(b, 0, buff);
				}
			   is.close();
			} catch (FileNotFoundException e) {
					throw new Exception("找不到你要下载的文件！");
			} catch (IOException e) {
					throw new Exception("io异常，文件读取失败！");
			}
		   FileTransfer filetransfer=new FileTransfer("operManual.doc", "application/doc",bytebuffer.toByteArray());
		   return filetransfer;
	}
//	private List<String> getUploadFiles(String url){
//	File file=new File(url);
//	File[] fileList=file.listFiles();
//	List<String> fileNameList=new ArrayList<String>();
//	for (int i = 0; i < fileList.length; i++) {
//		fileNameList.add(fileList[i].getName());
//	}
//	return fileNameList;
//}
}
