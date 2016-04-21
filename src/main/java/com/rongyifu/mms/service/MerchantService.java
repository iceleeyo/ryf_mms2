package com.rongyifu.mms.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.io.FileTransfer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.bean.FeeCalcMode;
import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.bean.MinfoH;
import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.bean.OperLog;
import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerFeeDao;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.dao.OperAuthDao;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.datasync.MerInfoAlterSync;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.merchant.MenuBean;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.utils.Base64;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MerchantService {

	private MerInfoDao merInfoDao = new MerInfoDao();
	private OperAuthDao operAuthDao = new OperAuthDao();
	private MerOperDao merOperDao = new MerOperDao();
	private MerFeeDao merFeeDao = new MerFeeDao();
	
	/**
	 * @param ids
	 * @param status
	 * @return
	 * 批量审核
	 */
	public String batchCheckGateConfig(List<Integer> ids,Integer status){
		String msg = "FAIL";
		Set<String> mids = new HashSet<String>();
		try {
			for (Integer id : ids) {
				String mid = checkGateConfig1(status, id);
				if(StringUtils.isNotBlank(mid)){
					mids.add(mid);
				}
			}
			if(CollectionUtils.isNotEmpty(mids)){
				for (String mid : mids) {
					//刷新管理平台缓存
					RYFMapUtil util = RYFMapUtil.getInstance();
					util.refreshFeeCalcModel(mid);
				}
				//刷新ewp缓存 ewp是刷新整个fee_calc_mode整个表 刷新一次就好了
				Map<String, Object> p = new HashMap<String, Object>();
		 		p.put("t", "fee_calc_mode");
		 		p.put("k", "");
		 		p.put("f", "");
		 		EWPService.refreshEwpETS(p);
			}
			msg= "OK";
		} catch (Exception e) {
			LogUtil.printErrorLog("batchCheckGateConfig: " + ids.toString(), e);
		}
		merInfoDao.saveOperLog("批量审核商户网关配置申请", ids.toString() + " 操作结果：" + msg);
		return msg;
	}
	
	private String checkGateConfig1(Integer status, Integer id){
		String mid = null;
		String rslt=merInfoDao.checkGateConfig(status, id);
		if(rslt.indexOf("[")!=-1){
			mid = rslt.substring(rslt.indexOf("[")+1,rslt.indexOf("]"));
		}
		return mid;
	}
	
	private boolean refresh(String mid){
		RYFMapUtil util = RYFMapUtil.getInstance();
		util.refreshFeeCalcModel(mid);
		Map<String, Object> p = new HashMap<String, Object>();
 		p.put("t", "fee_calc_mode");
 		p.put("k", "");
 		p.put("f", "");
 		return EWPService.refreshEwpETS(p);
	}
	
	/**
	 * @param id 申请记录主键
	 * @param status 审核状态
	 * @return
	 * 商户网关配置审核
	 */
	public String checkGateConfig(Integer status, Integer id){
		String msg = "审核失败";
		String rslt=merInfoDao.checkGateConfig(status, id);
		if(rslt.contains("OK")){
			msg="审核成功";
			if(rslt.indexOf("[")!=-1){
				String mid = rslt.substring(rslt.indexOf("[")+1,rslt.indexOf("]"));
				refresh(mid);
			}
		}
		merInfoDao.saveOperLog("商户网关配置审核", msg+",status:"+status+",operId:"+id);
		return msg;
	}
	
	/**
	 * @param mid
	 * @param mName
	 * @param status
	 * @param state
	 * @return
	 * 申请查询
	 */
	public CurrentPage<FeeCalcMode> showGateConfigApply(String mid,String mName,Integer pageNo,Integer status,Integer state,Integer type){
		return merInfoDao.showGateConfigApply(mid,mName,pageNo,status,state,type);
	}
	
	public String batchToggleGateApply(List<String> rytGateList,Integer toState){
		String msg = "FAIL";
		try {
			int[] rst = merInfoDao.batchToggleGateApply(rytGateList,toState);
			if(null != rst){
				msg = "OK";
			}
			merInfoDao.saveOperLog("批量网关开关申请", rytGateList.toString() + " 操作结果：" + msg);
		} catch (Exception e) {
			LogUtil.printErrorLog("batchToggleGateApply", e);
		}
		return msg;
	}	
	
	/**
	 * @param type 申请类型
	 * @param param 参数
	 * @return
	 * 商户网关配置申请
	 */
	public String merGateConfigApply(Map<String,String> params){
		String msg = "FAIL";
		int rslt = 0;
		try {
			Integer type = Integer.valueOf(params.get("type"));
			String mid = params.get("mid");
			Integer rytGate = Integer.valueOf(params.get("rytGate"));
			if(merInfoDao.isGateConfigApplyExist(mid, rytGate, type)){
				return "存在未审核的申请";
			}
			if(type == 0){//网关开关申请
				Integer state = Integer.valueOf(params.get("state"));
				int currentState = merInfoDao.queryGateState(mid,rytGate);
				if(currentState == state){
					rslt = merInfoDao.addGateOpenCloseApply(mid, state, rytGate);
					if(rslt != 0){
						msg="OK";
					}
				}else{
					msg=currentState==0?"网关已关闭":"网关已启用";
				}
				merInfoDao.saveOperLog("网关开关申请", "网关号：" + rytGate + " 操作结果：" + msg);
			}else if(type == 1){//网关配置申请
				Integer gid = Integer.valueOf(params.get("gid"));
				String calcMode = params.get("calcMode");
				Long effectiveTime = Long.valueOf(params.get("effectiveTime"));
				rslt = merInfoDao.addGateConfigApply(mid, rytGate, gid, calcMode, effectiveTime);
				if(rslt != 0){
					msg="OK";
				}
				merInfoDao.saveOperLog("网关配置申请", "网关号：" + rytGate + " 支付渠道：" + gid + " 操作结果：" + msg);
			}
		} catch (NumberFormatException e) {
			LogUtil.printErrorLog("merGateConfigApply", e);
		}
		return msg;
	}
	
	/**
	 * @return
	 * 审核权限申请
	 */
	public String doCheckAuthApply(String mid,Integer operId,Integer status){
		String msg = "FAIL";
		mid = Ryt.sql(mid);
		try {
			if(status == 1){//审核通过
				LoginUser user=operAuthDao.getLoginUser();
				if(!"1".equals(user.getMid()) || null == mid){//如果不是管理员 则将mid设置成当前mid
					mid=user.getMid();
				}
				boolean authFlag=MenuService.hasThisAuth(user.getAuth(), 48);//id=48的为操作员权限修改
				if(!"1".equals(user.getMid())||!authFlag)return "您的权限不足！";
				if(user.getOperId().equals(operId)){
					return msg = "权限分配失败！无法审核自己的申请";
				}
//				String authApply = operAuthDao.checkMenu(mid, operId);
				int rowcount = operAuthDao.effectAuthChange(mid,operId);
				if(rowcount == 1){
					msg = "OK";
				}
			}else{
				int rowcount = operAuthDao.cancelAddMenu(mid, operId);
				if(rowcount == 1){
					msg = "OK";
				}
			}
		}catch(Exception e){
			LogUtil.printErrorLog("doCheckAuthApply", e);
		}
		merOperDao.saveOperLog("操作员权限审核", "mid:"+mid+" operId:"+operId+" "+(status == 1?"审核通过":"审核失败"));
		return msg;
	}
	
	/**
	 * @param mid
	 * @param operId
	 * @param pageNo
	 * @return 分页查询权限修改申请
	 */
	public CurrentPage<OperInfo> showOperApply(String mid,Integer operId, Integer pageNo){
		return operAuthDao.showOperApply(mid, operId, pageNo);
	}

	public String updateMerRsaFile(String mid, String mname, String rsaFile) {
		if (Ryt.empty(mid) || Ryt.empty(mname) || Ryt.empty(rsaFile)) return "参数输入错误";
		Minfo mer = null;
		try {
			mer = merInfoDao.getOneMinfo(mid);
		} catch (Exception e) {
			return "商户不存在";
		}

		if (null == mer) return "商户不存在";
		if (mer.getMstate() == 1 || mer.getExpDate() < DateUtil.today()) return "商户为关闭状态，不允许修改";
		if (Ryt.empty(mer.getPublicKey())) return "商户未导入RSA公钥，不允许修改";
		if (!mer.getName().equals(mname.trim())) return "商户号与名称不匹配，请检查输入是否正确";
		merOperDao.saveOperLog("更新商户公钥", "商户号："+mid+" RSA文件："+rsaFile);
		return doImportRSAFile(mid, rsaFile.trim());
	}

	public String addMerRsaFile(String mid, String rsaFile) {
		if (Ryt.empty(mid) || Ryt.empty(rsaFile)) return "参数输入错误";
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
				LogUtil.printErrorLog("doImportRSAFile", e);
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

	/**
	 * 商户网关配置
	 * @param mid
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getMerGateConfigInfo(String mid,Short transMode) {
		//  minfo_gate_edit.jsp中调用
		List aList = new ArrayList();
		List<FeeCalcMode> merFeeCalcModes = merInfoDao.queryMerFeeCalcModes(mid,null);
		List<RYTGate>  merNotSupportGates = new ArrayList<RYTGate>();
		for (RYTGate obj : RYFMapUtil.getRytAllGates()) {
			if ((obj.getTransMode() == transMode+0)&&!inListElement(obj.getGate(),merFeeCalcModes)) {
				merNotSupportGates.add(obj);
			}
		}
		aList.add(merNotSupportGates);
		aList.add(merFeeCalcModes);
		return aList;
	}
	/**
	 * 商户网关配置
	 * 增加搜索条件 网关类型 transMode
	 * @param mid
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getMerGateConfigInfo2(String mid,Short transMode) {
		List aList = new ArrayList();
		List<FeeCalcMode> merFeeCalcModes = merInfoDao.queryMerFeeCalcModes(mid,transMode);
		List<RYTGate>  merNotSupportGates = new ArrayList<RYTGate>();
		List<RYTGate> gateList = merInfoDao.query("select gate,stat_flag,trans_mode,gate_name from ryt_gate where trans_mode="+transMode, RYTGate.class);
		for (RYTGate gate : gateList) {
			if (!inListElement(gate.getGate(),merFeeCalcModes)) {
				merNotSupportGates.add(gate);
			}
		}
		aList.add(merNotSupportGates);
		aList.add(merFeeCalcModes);
		return aList;
	}

	
	private boolean inListElement(int gateId,List<FeeCalcMode> aList) {
		for (FeeCalcMode i : aList) {
			if(i.getGate() == gateId){
				return true;
			}
		}
		return false;
	}
	
	
	public String addGatesToMer(String mid, String[] gates) {
		// minfo_gate_edit.jsp中调用
		if (null == gates || gates.length == 0) return "请选择网关";
		for (String gate : gates) {
			merOperDao.saveOperLog("网关添加至商户", "商户号："+mid+" 网关号："+gate);
			if (Ryt.isNumber(gate)) merInfoDao.addGatesToMer(mid, Integer.parseInt(gate));
		}
		return AppParam.SUCCESS_FLAG;
	}

	public String addMerFeeModels(String mid, int transModel, String calcModel) {
		// minfo_gate_edit.jsp中调用
		merInfoDao.editMerFeelModels(mid, transModel, calcModel);
		return AppParam.SUCCESS_FLAG;
	}

	// mid,gate,authType
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAuthTypeList(String mid, int gate, int gid, String calcMode) {
		//minfo_gate_edit.jsp中调用
		List aList = new ArrayList();
		aList.add(mid);
		aList.add(gate);
		aList.add(gid);
		aList.add(calcMode);
    	merOperDao.saveOperLog("网关配置", "商户号："+mid+" 网关号："+gate+" 渠道号："+gid+" 手续费公式："+calcMode);
		aList.add(merInfoDao.getAuthTypeListByGate(gate));
		return aList;
	}

	public String configMerGate(String mid, int gate, int gid, String calcMode) {
		// minfo_gate_edit.jsp中调用
	   merOperDao.saveOperLog("修改网关手续费", "商户号："+mid+" 网关号："+gate+" 渠道号："+gid+" 手续费公式："+calcMode);
	   int row=  merInfoDao.updateFeeCalcMode(mid, gate, gid, calcMode);
	   if(row==1){
			return AppParam.SUCCESS_FLAG;
	   }else{
		    return "修改异常！";
	   }
	}

	/**
	 * 商户风险信息增加或者修改
	 * @param minfo
	 * @return
	 */
	public String editMinfoFX(Minfo minfo) {
		//search_edit_info.jsp中调用
		try {
			merOperDao.saveOperLog("商户风险信息修改", "商户为"+minfo.getId());
			merInfoDao.editMinfoFX(minfo);
			Map<String,Object> p = new HashMap<String,Object>();
			p.put("t","minfo");
			p.put("k", minfo.getId());
			return  EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "修改成功,刷新ETS失败";
		} catch (Exception e) {
			LogUtil.printErrorLog("editMinfoFX", e);
			return "增加或修改失败";
		}
	}

	/**
	 * 商户联系人信息增加或者修改
	 * @param minfo
	 * @return
	 */
	public String editMinfoContact(MinfoH minfo) {
		//edit_Minfo.jsp和search_edit_info.jsp中调用
		try {
			merOperDao.saveOperLog("修改商户联系人", "修改商户为"+minfo.getId());
			int row=merInfoDao.editMinfoContact(minfo);
			
			//商户信息同步至账户系统线程  yang.yaofeng 2014-11-17
			Thread trd=new Thread(new MerInfoSync2YCK(minfo.getId()));
			trd.start();
			
			return row==1?AppParam.SUCCESS_FLAG:"修改失败";
		} catch (Exception e) {
			LogUtil.printErrorLog("editMinfoContact", e);
			return "修改失败";
		}
	}

	/**
	 * 权限修改申请
	 * @param menu
	 * @return
	 * @throws Exception 
	 */
	public String addMenuApply(String mid,int operId,String menu){
		mid = Ryt.sql(mid);
		String msg = "";
		LoginUser user=operAuthDao.getLoginUser();
		if(!"1".equals(user.getMid()) || null == mid){//如果不是管理员 则将mid设置成当前mid
			mid=user.getMid();
		}
		boolean authFlag=MenuService.hasThisAuth(user.getAuth(), 48);//id=48的为操作员权限修改
		if(!"1".equals(user.getMid())||!authFlag)return "您的权限不足！";
//			if(operId==user.getOperId()){
//				return msg = "权限分配失败！无法自己给自己分配权限";
//			}
		try {
			String currentAuthStr =  operAuthDao.getAuthStr(mid, operId);
			boolean isApplyExist = operAuthDao.isApplyExist(mid,operId);
			//是否存在申请
			if(isApplyExist){
				msg = "存在未审核的申请！";
			}else if(menu.equals(currentAuthStr)){
				//对比是否做出更改
				msg = "未做出更改";
			}else{
				int role = operAuthDao.getRole(mid,operId);
				String authstr ="";
				if (menu.equals("")) {
					authstr = MenuService.genDefaultUserAuth(role);
				} else {
					String[] authId = menu.split(",");
					authstr = MenuService.genUserAuth(authId, role);
				}
				int rowCount =  operAuthDao.addMenuApply(authstr, mid,operId);
				if(rowCount == 1){
					msg = "权限分配申请成功！";
				}else{
					msg = "找不到商户号为 " + mid + " 操作员号为 " + operId + " 的操作员！";
				}
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("addMenuApply", e);
			msg = "权限分配申请失败！";
		}
//		String mid,int operId,String menu
		merOperDao.saveOperLog("操作员权限申请", msg);
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
			auth=removeMyAcountAuth(auth);
		}
		try {
			merOperDao.saveOperLog("新增操作员", "商户为"+minfo_id+" 新增操作员"+oper_mid+ " 管理端操作员"+operid);
			merOperDao.add(action, ostate, oper_email, oper_tel, oper_name, operpass, operid, minfo_id, mtype, role,auth);
		} catch (Exception e) {
			LogUtil.printErrorLog("addOperInfo", e);
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
			LogUtil.printErrorLog("deleteOperInfo", e);
			return "false";
		}
		return AppParam.SUCCESS_FLAG;
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
			merOperDao.saveOperLog("修改操作员", "商户为"+mid+" 操作员"+operid);
			merOperDao.edit(ostate, oper_email, oper_tel, oper_name, operid, mid, mtype);
		} catch (Exception e) {
			LogUtil.printErrorLog("editOperInfo", e);
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
	public String editPass(String mid_, String oper_id, String opass, String npass) {
		//search_chang_oper_pass.jsp(jsp,admin)中调用
		try {
			int operId = Integer.parseInt(oper_id);
			if (Ryt.empty(npass)) return "请输入新密码。";
			String oldPass = merOperDao.getOldPass(0, operId, mid_);
			if (!oldPass.equals(opass)) {
				return "原密码错误！";
			}
			merOperDao.saveOperLog("修改商户密码", "商户为"+mid_+" 操作员"+oper_id);
			return operAuthDao.updateOperPwd(npass, mid_, operId, 0) ? "修改成功!" : "修改失败";
		} catch (Exception e) {
			LogUtil.printErrorLog("editPass", e);
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
			l = new SystemDao().getMessage(notice);
		} catch (Exception e) {
			LogUtil.printErrorLog("getMessage", e);
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
			LogUtil.printErrorLog("getMessageById", e);
		}
		return null;
	}

	/**
	 * 获得单个商户对象 查询商户基本信息
	 * @param mid
	 * @return
	 */
	public Minfo getOneMinfo(String mid) {
		//search_edit_info.jsp中调用
		Minfo m = null;
		try {
			m = merInfoDao.getOneMinfo(mid);
		} catch (Exception e) {
			LogUtil.printErrorLog("getOneMinfo", e);
			return null;
		}
		return m;
	}

	/**
	 *修改商户基本信息
	 *@param 商户对象
	 *@return 成功或失败信息
	 */
	public String editMinfos(Minfo minfo) {
		//search_edit_info.jsp中调用
		try {
			merOperDao.saveOperLog("修改商户基本信息", "修改商户"+minfo.getId());
			merInfoDao.editMinfos(minfo);
		} catch (Exception e) {
			LogUtil.printErrorLog("editMinfos: mid="+minfo.getId(), e);
			return "修改失败！";
		}
		
		//商户信息同步至账户系统线程
		Thread trd=new Thread(new MerInfoSync2YCK(minfo.getId()));
		trd.start();
		
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "minfo");
		p.put("k", minfo.getId());
		return EWPService.refreshEwpETS(p) ? "修改成功!" : "资料修改成功，刷新ewp失败!";
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
		if(minfo.getMerTradeType()==null||"".equals(minfo.getMerTradeType())){
			return "请填写行业ID";
		}
		
		if(minfo.getProvId()==null||"".equals(minfo.getProvId())){
			return "请填写区号";
		}
		String provid=""+minfo.getProvId();
		String strNo=createNumber(provid);
		minfo.setId(strNo);//设置商户ID
		String mid ;
		String gate=minfo.getGateId().toString();
		try {
			merOperDao.saveOperLog("新增商户基本信息", "商户名称"+minfo.getName()+" 简称"+minfo.getAbbrev());
			if("1".equals(minfo.getMerType())){
				Integer gate1=Integer.parseInt("71"+gate.substring(2));
				minfo.setGateId(gate1);
			}else{
				Integer gate1=Integer.parseInt("72"+gate.substring(2));
				minfo.setGateId(gate1);
			}
			mid =""+merInfoDao.addMinfoBase(minfo);
			
			//商户信息同步至账户系统线程  yang.yaofeng 2014-11-17
			Thread trd=new Thread(new MerInfoSync2YCK(mid));
			trd.start();
			
		} catch (Exception e) {
			LogUtil.printErrorLog("addMinfos", e);
			return "增加失败";
		}

		RYFMapUtil obj = RYFMapUtil.getInstance();
		obj.refreshMinfoMap(mid);

		return String.valueOf(mid);
		//  这里不需要刷新ets
	}

	/**
	 * 判断商户号mid是否存在
	 */
	public String checkMidExist(String mid) {// add_info.jsp中调用
		return merInfoDao.checkMidExist(mid) ? AppParam.SUCCESS_FLAG : "该商户号不存在，请重新输入！";
	}

	/**
	 * 根据状态标志，结算方式，联机查询，所在省份，商户名，商户号查找商户信息
	 * @return
	 */
	public CurrentPage<Minfo> getMinfos(String mid, String name, String prov, String liqPeriod, String liq_type,
					String stat_flag, int pageNo) {
		return merInfoDao.getMinfos(mid, name, prov, liqPeriod, liq_type, stat_flag, pageNo);
	}

	/**
	 * 手续费维护
	 * @param mid
	 * @param model
	 * @param editedID
	 * @param addedID
	 * @return
	 */
	public String addCalcModel(String mid, String model, String editedID, String addedID) {// fee_manage.jsp(admin)中调用

		try {
			merFeeDao.addCalcModel(mid, model, editedID, addedID);
			return AppParam.SUCCESS_FLAG;
		} catch (Exception e) {
			LogUtil.printErrorLog("addCalcModel", e);
			return "false";
		}
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
	 * 根据operId返回List集合里为OperInfo对象
	 * @param mid
	 * @param operId
	 * @return
	 */
	public List<OperInfo> getOneOpersObject(String mid, Integer operId) {
		// search_edit_oper.jsp中调用
		return merOperDao.getOneOpersObject(mid, operId);
	}

	/**
	 * 操作员重置密码
	 * @param mid
	 * @param oper_id
	 * @param npass
	 * @return
	 */
	public String setPass(String mid, int oper_id, String npass) {
		// search_set_oper_pass.jsp中调用
		try {
			merOperDao.saveOperLog("操作员密码重置", "商户为"+mid+" 操作员"+oper_id);
			merOperDao.setPass(mid, oper_id, npass);
		} catch (Exception e) {
			LogUtil.printErrorLog("setPass", e);
			return "重置密码失败!";
		}
		return "重置密码成功!";
	}

	/**
	 * 根据mid返回对应的融易通操作员的下拉框字符 （新增）
	 * @param mid
	 * @return
	 */
	public Map<Integer, String> showOPers(String mId, String method) {
		Map<Integer, String> opers = merOperDao.getHashOper(mId);
		return opers;
	}
	
	public FileTransfer downloadOperLog(Integer bdate,Integer edate,String mid,String operName){
		try {
			ArrayList<String[]> list = new ArrayList<String[]>();
			String[] title = {"商户号","商户简称","操作员号","操作员名","系统日期","系统时间","操作员IP","操作","操作结果"};
			list.add(title);
			CurrentPage<OperLog> page = merOperDao.queryForDownload(mid, operName, bdate, edate);
			List<OperLog> logList = page.getPageItems();
				for (int j = 0; j < logList.size(); j++) {
					OperLog log = logList.get(j);
				String[] strArr = { log.getMid(), log.getName(),
						log.getOperId() + "", log.getOper_name(),
						log.getSysDate() + "", Ryt.getStringTime(log.getSysTime()),
						log.getOperIp(), log.getAction(), log.getActionDesc() };
					list.add(strArr);
				}
				String filename = "oper_log_" + DateUtil.getIntDateTime() + ".xlsx";
				return new DownloadFile().downloadXLSXFileBase(list, filename, "系统日志");
		} catch (Exception e) {
			LogUtil.printErrorLog("downloadOperLog", e);
			return null;
		}
	}

	/**
	 * 融易付操作员日志(新增)
	 * @param mid
	 * @param name
	 * @param sdate
	 * @param edate
	 * @param pageIndex
	 * @return
	 */
	public CurrentPage<OperLog> getOperLog4DWR(String mid, String name, int sdate, int edate, int pageIndex) {
	
		return merOperDao.getOperLog4DWR(mid, name, sdate, edate, pageIndex);
	}

	/**
	 * 根据mid,operId查询菜单权限
	 * @param mid
	 * @return
	 * @throws Exception 
	 */
	public List<MenuBean> searchOperAuth(String mid,int operId) throws Exception {
		List<MenuBean> authInfoList = null;
		if ("1".equals(mid)) {
			authInfoList = MenuService.getAdminMenu(); // 管理员
		} else {
			authInfoList = MenuService.getMerMenu(); // 普通商户
			
		}
		String authStr=operAuthDao.checkMenu(mid, operId);
		System.out.println("authStr=" + authStr);
		setAuthInfo(authStr, authInfoList);
		return authInfoList;
	}

	/**
	 *  按钮权限分配查询
	 * @param mid
	 * @param operId
	 * @param authStr
	 * @return
	 */
	public List<MenuBean> searchButtonAuth(String mid,int operId){
		List<MenuBean> beanList = searchButtonAuth1(mid, operId);
		String authStr=operAuthDao.checkMenu(mid, operId);
		setAuthInfo(authStr, beanList);
		return beanList;
	}
	
	public List<MenuBean> searchButtonAuthApply(String mid,int operId){
		List<MenuBean> beanList = searchButtonAuth1(mid, operId);
		Map<String,Object> map = operAuthDao.getAuthApply(mid, operId);
		String applyStr = (String)map.get("auth_change");
		String authStr = (String)map.get("auth");
		setAuthInfo(applyStr, beanList);
		setChanges(authStr, applyStr,beanList);
		return beanList;
	}
	
	private List<MenuBean> searchButtonAuth1(String mid,int operId){
		List<MenuBean> beanList=new ArrayList<MenuBean>();
		//MenuBean rootBean0=new MenuBean(0, "按钮权限分配");
		MenuBean buttonBean1=new MenuBean(0, 99, "更改历史订单支付渠道");
		MenuBean buttonBean2=new MenuBean(0, 98, "查询待经办订单"); 
		MenuBean buttonBean3=new MenuBean(0, 75, "查询待提现订单");
		MenuBean buttonBean4=new MenuBean(0, 101, "网银支付");//和商户后台 出款交易管理id重复
		MenuBean buttonBean5=new MenuBean(0, 102, "线下充值支付");//和商户后台 线下充值交易查询id重复
		MenuBean buttonBean6=new MenuBean(0, 103, "账户余额支付");
		MenuBean buttonBean7=new MenuBean(0, 105, "查看支付卡号");
		MenuBean buttonBean8=new MenuBean(0, 190, "周期外商户结算");//结算发起界面优化新加按钮权限
		MenuBean buttonBean9=new MenuBean(0, 191, "支付渠道新增");//支付渠道新增增加权限控制
		MenuBean buttonBean10=new MenuBean(0, 192, "明细查询权限");//拥有查询权限的管理员可以看到完整的卡号信息 	普通管理员只能看到前六后四中间星号“*”
		beanList.add(buttonBean1);
		beanList.add(buttonBean2);
		beanList.add(buttonBean3);
		beanList.add(buttonBean4);
		beanList.add(buttonBean5);
		beanList.add(buttonBean6);
		beanList.add(buttonBean7);
		beanList.add(buttonBean8);
		beanList.add(buttonBean9);
		beanList.add(buttonBean10);
		if(!"1".equals(mid)){
			beanList.remove(buttonBean7);
		}
		return beanList;
	}
	
	private void setChanges(String authStr,String applyStr,List<MenuBean> authInfoList){
		for (int i = 0; i < authInfoList.size(); i++) {
			MenuBean menuBean=authInfoList.get(i);
			menuBean.setIsChanged(null);
			int id=menuBean.getId();
			Boolean checked=MenuService.hasThisAuth(authStr,id);//是否有权限
			Boolean checked1=MenuService.hasThisAuth(applyStr,id);//是否有权限
			menuBean.setIsChanged(!checked==checked1);
			if(menuBean.getChildren()!=null){
				setChanges(authStr, applyStr, menuBean.getChildren());
			}
		}
	}
	/**
	 * 设置是否选择（即是否有权限）
	 * @param authArr
	 * @param authInfoList
	 */
	private void setAuthInfo(String authStr,List<MenuBean> authInfoList){
		for (int i = 0; i < authInfoList.size(); i++) {
			MenuBean menuBean=authInfoList.get(i);
			menuBean.setChecked(false);
			int id=menuBean.getId();
			Boolean checked=MenuService.hasThisAuth(authStr,id);//是否有权限
			menuBean.setChecked(checked);
			if(menuBean.getChildren()!=null){
				setAuthInfo(authStr, menuBean.getChildren());
			}
		}
	}

	/**
	 * 查询银行手续费
	 * @param mid
	 * @param bank
	 * @return
	 */
	public List<FeeCalcMode> searchGatesFee(String mid, int bank) {
		return merFeeDao.searchGatesFee(mid, bank);
	}

	/**
	 * 关闭网关
	 */
	public int closeGate(String mid, int gate) {
		merOperDao.saveOperLog("商户网关关闭", "商户号"+mid+" 网关号"+gate);
		return merInfoDao.closeGate(mid, gate);
	}
	/**
	 * 商户开启
	 * @param mid
	 * @return
	 */
	public String merOpen(String mid) {
		String msg =merOperDao.merValid(mid);
		if (msg != null && msg.equals(AppParam.SUCCESS_FLAG)) {
			int effectRow = merOperDao.merOpen(mid);
			if (effectRow == 1) {
				/* 存管系统不上，先注释*/ 
				//merOperDao.addCgTask( "A", String.valueOf(mid));
				// 刷新ets
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("t", "minfo");
				p.put("k", mid);
				boolean temp=merOperDao.hasTransliqAcc(mid);			
				if(temp){
					merOperDao.saveOperLog("商户开启", "开启商户号为"+mid);
				}else{
				StringBuffer sql=new StringBuffer("insert into acc_infos (uid,aid,init_date,aname,state,acc_type) values('");
				sql.append(mid).append("','").append(mid).append("',").append(DateUtil.today()).append(",'电银账户',1,2)");
				merOperDao.saveOperLog("商户开启", "开启商户号为"+mid);
				merOperDao.getJdbcTemplate().update(sql.toString());
				}
				return EWPService.refreshEwpETS(p) ? "开启成功!" : "开启成功，刷新ewp失败!";
			}
		}
		return msg;
	}
	
	/**
	 * 开启交易账户
	 * @param mid
	 * @return
	 * @throws Exception 
	 */
	public String transAccOpen(String mid) throws Exception{
		mid = Ryt.checkSingleQuotes(mid);
		
		if(!merOperDao.hasMid(mid))
			return "商户不存在操作员,请新增商户操作员!";
		
		if(!merOperDao.merIsOpen(mid))
			return "商户未开启!";
		//如果存在交易账户，则开启
		if(merOperDao.hasTransAcc(mid)){
			if(merOperDao.hasTransAcc_State(mid)){
				return "商户已经开启交易账户！";
			}
			merOperDao.saveOperLog("交易账户开启", "商户号"+mid);
			int flag=operAuthDao.openJYZH(mid);
			if(flag==1)
				return "开启成功！";
			return "开启失败！";
		}
		int accNum = merOperDao.countAccNum(mid);
		
		Map<String, Object> minfo = merOperDao.queryMerInfo(mid);
		String aid = genAccId(String.valueOf(minfo.get("prov_id")));
		String abbrev = (String)minfo.get("abbrev");
		String aname = accNum > 2 ? abbrev + accNum : abbrev; // 防止账户名称重复
		int today=DateUtil.today();
		int definedDate=Integer.parseInt(DateUtil.returnDefinedDate(7));  //7天以后的日期
		String []sqls=new String[2];
		String title="交易账户开启通知！--"+today;
		String content="尊敬的用户，您好。您的交易账户，请您尽快设置您的交易密码！";
		//消息通知
		StringBuffer sql=new StringBuffer("insert into mms_notice(begin_date,end_date,title,content,oper_id,mid) values(");
		sql.append(today).append(","+definedDate).append(",'").append(title+"','").append(content+"',").append(1+",").append(Ryt.addQuotes(mid)).append(")");
		sqls[0]=sql.toString();
		int[] flag = merOperDao.saveAccInfo(mid, aid, aname,"0", "1",sqls);
		return flag[0] == 1 ? "开启成功!" : "开启失败!";
	}
	
	/**
	 * 生成新账号ID：地域(3位)+6位随机数
	 * @param areaId
	 * @return
	 */
	public String genAccId(String areaId){
		String newId = null;
		while(true){
			newId = areaId + Ryt.gen6Rand();
			if(!merOperDao.hasAccByAid(newId))
				break;
		};
		return newId;
	}
	
	/**
	 * 更新ets表商户表
	 */
	public String refreshEwpETSMinfo(String id){
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "minfo");
		p.put("k", id);
		return EWPService.refreshEwpETS(p) ? "刷新成功!" : "刷新ewp失败!";
	}
	/**
	 * 根据mid查询商户属于哪个类别
	 */
	public int getCategoryByMid(String mid){
		return merInfoDao.getCategoryByMid(mid);
	}
	/**
	 * 根据mid修改商户类别
	 */
	public String editCategoryByMid(String mid,int category){
		try {
			int effectRow = merInfoDao.editCategoryByMid(mid, category);
			if (effectRow < 1) {
				return "修改失败!请重试。";
			}
		} catch (Exception e) {
			return "修改异常!请重试。";
		}
		return AppParam.SUCCESS_FLAG;
	}
	/**
	 * 查询商户重要信息
	 * @param mid
	 * @return
	 */
	public Minfo getImportentMsgByMid(String mid){
		mid = Ryt.sql(mid);
		return merInfoDao.getImportentMsgByMid(mid);
	}
	
	/**
	 * 申请修改商户重要信息
	 * @param minfo
	 * @return
	 */
	public String applyUppdateMerImportantInfo(MinfoH minfo){
		String mid = minfo.getId();
		//1.判断是否有未审核的申请
		boolean isApplyExists = merInfoDao.queryUncheckedApplyCount(minfo.getId());
		if(isApplyExists){
			return "该商户存在未审核的修改申请。";
		}
		
		if(merInfoDao.isExistMinfoName(minfo.getName(),minfo.getAbbrev(),mid)){
			return "商户简称("+minfo.getAbbrev()+")已经存在。";
		}
		
		int mertype =merOperDao.queryMerType(mid);
		String gate=minfo.getGateId().toString();
//		处理结算银行的，如果商户类型是企业，则结算时走的用对公代付，网关号是72开头的5位数，如果是个人，则是对私代付，网关号是71开头的5位数
		if(mertype==1){
			Integer gate1=Integer.parseInt("71"+gate.substring(2));
			minfo.setGateId(gate1);
		}else{
			Integer gate1=Integer.parseInt("72"+gate.substring(2));
			minfo.setGateId(gate1);
		}
		int count = merInfoDao.addImpChangeApply(minfo);
		merOperDao.saveOperLog("商户重要信息修改申请", "商户["+minfo.getId()+"]重要信息修改申请" + (count == 1?"成功":"失败"));
		if(count == 1){
			return "申请成功";
		}else{
			return "申请失败";
		}
	}

	/**
	 * 商户重要信息修改申请分页查询
	 * @param mid
	 * @return
	 */
	public CurrentPage<Minfo> queryCheckPage(int pageNo,String mid,String mname,int status){
		return merInfoDao.queryCheckPage(pageNo,AppParam.getPageSize(),mid, mname, status);
	}
	
	/**
	 * @return
	 * 商户重要信息申请审核
	 */
	public String doCheck(MinfoH minfo){//id为申请的主键 不是mid
		String msg = "审核失败";
		int rowCount = merInfoDao.updateApplyInfo(minfo);//更新申请记录的状态
		if(rowCount==1){
			Integer isEffectNow = minfo.getIsEffectNow();
			if(minfo.getStatus() == 1 && null != isEffectNow && 1 == isEffectNow){//立即生效
				//更新minfo表
				MinfoH applyInfo = merInfoDao.getApplyInfoByIdH(Integer.valueOf(minfo.getId()));
				merInfoDao.effectiveChange(applyInfo);
				msg = "审核成功";
				//商户信息同步至账户系统线程
				Thread trd=new Thread(new MerInfoSync2YCK(applyInfo.getMid()));
				trd.start();
				
			}else{
				msg = "审核成功";
			}
		}
		merOperDao.saveOperLog("商户重要信息审核", "[" + minfo.getId() + "]" + (minfo.getStatus() == 1 ? "审核通过" : "审核不通过"));
		return msg;
	}
	
	/**
	 * @param status
	 * @param ids
	 * @return
	 * 批量审核
	 */
	public String doBatchCheck(short status,  int[] ids){
		String msg = "审核失败";
		String idList = "";
		for(int i=0 ; i<ids.length;i++){
			try {
				if(i == 0)
					idList += ids[i];
				else
					idList += "," + ids[i];
				
				final int id = ids[i];
				MinfoH temp = new MinfoH();
				temp.setStatus(status);
				temp.setId(id+"");
				int rowCount = merInfoDao.updateApplyInfo(temp);
				if(rowCount == 1 && status==1){//批量审批通过后立即生效 
					MinfoH applyInfo = merInfoDao.getApplyInfoByIdH(id);
					merInfoDao.effectiveChange(applyInfo);
					msg = "审核成功";
					
					//商户信息同步至账户系统线程
					Thread trd=new Thread(new MerInfoSync2YCK(applyInfo.getMid()));
					trd.start();
				}
				msg = "审核成功";
			} catch (Exception e) {
				LogUtil.printErrorLog("doBatchCheck", e);
			}
		}
		
		merOperDao.saveOperLog("商户重要信息批量审核", "[" + idList + "]" + msg);
		return "OK";
	}
	
	/**
	 * @param id
	 * @return
	 * 审核详情
	 */
	public Map<String,Object> showCheckDetails(int id){
		return merInfoDao.querycheckInfoPair(id);
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
		merOperDao.saveOperLog("修改商户重要信息", "商户为"+mid);
		int mertype =merOperDao.queryMerType(mid);
		String gate=minfo.getGateId().toString();
		if(mertype==1){
			Integer gate1=Integer.parseInt("71"+gate.substring(2));
			minfo.setGateId(gate1);
		}else{
			Integer gate1=Integer.parseInt("72"+gate.substring(2));
			minfo.setGateId(gate1);
		}
		int row=merOperDao.updateImportantMsg(minfo);
		if (row == 1) {
			/* 存管系统不上，先注释*/ 
//			merOperDao.addCgTask( "M", String.valueOf(mid));
			RYFMapUtil.getInstance().refreshMinfoMap(mid+"");//刷新缓存
			// 刷新ets
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "minfo");
			p.put("k", mid);
			backStr= EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG : "修改成功，但刷新ewp失败!";
		}
		return backStr;
	}
	
	//关闭商户
	public String closeMinfo(String mid,String loginMid,int loginName){
		merOperDao.saveOperLog("商户关闭", "商户为" + mid + " 登陆商户" + loginMid);
		int mstate = merOperDao.querymstate(mid);
		String res = null;
		if (mstate == 0) {
			res = merOperDao.closeMer(mid, loginMid, loginName);
			// 商户关闭，删除证书配置
//			new CertManagerDao().deleteCert(mid);
			if (res.equals(AppParam.SUCCESS_FLAG)) {
				/* 存管系统不上，先注释 */
				// merOperDao.addCgTask( "D", String.valueOf(mid));
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("t", "minfo");
				p.put("k", mid);
				res = EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG
						: "刷新ewp失败!";
			}
		}else{
			res="商户已经关闭";
		}
		return res;
	}
	/**
	 * 上传操作员手册文件
	 * @param uploadFile
	 * @param fileFormat
	 * @return
	 * @throws IOException
	 */
	public String uploadUserManual(InputStream uploadFile, String fileFormat) throws IOException{
		 String saveUrl=Ryt.getParameter("SettlementFilePath");
		 File file = new File(saveUrl + "operManual." + fileFormat);   
		 byte[] b = new byte[1024];   
		 FileOutputStream foutput = new FileOutputStream(file); 
		 int buff=0;
		 while((buff=uploadFile.read(b))!=-1){
			 foutput.write(b, 0, buff);
		 }   
		 uploadFile.close();
		 foutput.flush();   
		 foutput.close();   
		 return "文件上传成功！";  
	}
	/**
	 * 所查询所有商户
	 * @return 所有商户
	 */
	public Map<String,String> selectallMid(){
		Map<String, String> map=new HashMap<String, String>();
		List<Minfo>  list=merInfoDao.allMid();
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getId(), list.get(i).getName());
		}
		return map;
	}
	/**
	 * 获得单个商户对象的操作员map
	 * @param mid 商户号
	 * @return 单个商户对象的操作员map
	 */
	public Map<Integer, String> getAllopersMap(String mid){
		return merInfoDao.getAllopersMap(mid);
	}
	/**
	 * 生成帐号
	 * @param provId 省份ID
	 * @return 生成的帐号 账户ID生产策略（省份（3位)+6位随机数）
	 */
	public String createNumber(String provId){
		String randStr=createRandomStr();
		String strNo="";
		strNo=provId+randStr;
		Map<String, String> map=new HashMap<String, String>();
		List<Minfo> lists=merInfoDao.allMid();
		for (int i = 0; i < lists.size(); i++) {
			map.put(lists.get(i).getId()+"",lists.get(i).getId()+"");
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
	 * @return 6位随机数
	 */
	private String createRandomStr(){
		String randStr="";
		Random random=new Random();
		for (int i = 0; i < 6; i++) {
			int r=random.nextInt(10);
			randStr=randStr+r;
		}
		return randStr;
	}

	
	/**
	 * 商户权限修改（我的账户）
	 * @param mid 商户id
	 * @param operId 操作员id
	 * @return
	 * @throws Exception
	 */
	public List<MenuBean> searchOperAuths(String mid,int operId) throws Exception{
		List<MenuBean> authInfoList = searchOperAuths1(mid, operId);
		String authStr=operAuthDao.checkMenu(mid, operId);
		setAuthInfo(authStr, authInfoList);
		return authInfoList;
	}

	/**
	 * @param mid
	 * @param operId
	 * @return
	 * @throws Exception
	 * 审核更改权限
	 */
	public List<MenuBean> searchOperAuthsApply(String mid,int operId) throws Exception{
		List<MenuBean> authInfoList = searchOperAuths1(mid, operId);
		Map<String,Object> map = operAuthDao.getAuthApply(mid, operId);
		String applyStr = (String)map.get("auth_change");
		String authStr = (String)map.get("auth");
		setAuthInfo(applyStr, authInfoList);
		setChanges(authStr, applyStr,authInfoList);
		return authInfoList;
	}
	
	private List<MenuBean> searchOperAuths1(String mid,int operId) throws Exception {
		if(StringUtils.isNotBlank(mid) && "1".equals(mid)){
			return MenuService.getAdminMenu();
		}else{
			return MenuService.getMerMenu();
		}
	}
	/**
	 * 后台修改指定商户指定操作员权限
	 * @param menu
	 * @return
	 * @throws Exception 
	 */
	public String addMenus(String allAuthStr,String mid,int operId,String menu){
		String msg = "";
		LoginUser user=operAuthDao.getLoginUser();
		String myMid=user.getMid();
		boolean authFlag=MenuService.hasThisAuth(user.getAuth(), 48);//id=48的为操作员权限修改
		if(!"1".equals(myMid)||!authFlag)return "您的权限不足！";
		try {
			String authstr ="";
	    	merOperDao.saveOperLog("商户操作员权限分配", "商户为"+mid+" 管理端操作员"+operId);
			if (menu.equals("")) {
				authstr = MenuService.noneAuth(allAuthStr,mid,operId);
				msg = "权限已清空！";
				operAuthDao.addMenu(authstr, mid,operId);
			} else {
				authstr = MenuService.genUserAuths(menu);
				msg = "分配成功！";
				operAuthDao.addMenu(authstr, mid,operId);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog("addMenus", e);
			msg = "权限分配失败！";
		}
		return msg;
	}
	
	/**
	 * 后台修改指定商户权限
	 * @param menu
	 * @return
	 * @throws Exception 
	 */
	public String addMerMenus(String allAuthStr,String mid,int operId,String menu){
		String msg = "";
		LoginUser user=operAuthDao.getLoginUser();
		String myMid=user.getMid();
		boolean authFlag=MenuService.hasThisAuth(user.getAuth(), 130);//id=130的为商户权限修改
		if(!"1".equals(myMid)||!authFlag)return "您的权限不足！";
		
		Map<String, Object> params = LogUtil.createTreeMap();
		params.put("allAuthStr", allAuthStr);
		params.put("mid", mid);
		params.put("operId", operId);
		params.put("menu", menu);
		
		try {
			String authstr ="";
	    	merOperDao.saveOperLog("商户操作员权限分配", "商户为"+mid+" 管理端操作员"+operId);
			if ("".equals(menu)) {
				authstr = MenuService.noneAuth(allAuthStr,mid,operId);
				msg = "权限已清空！";
				operAuthDao.addMenu(authstr, mid,operId);
			} else {
				authstr = MenuService.genUserAuths(menu);				
				msg = "分配成功！";
				operAuthDao.addMenu(authstr, mid,operId);
			}
			
			params.put("authstr", authstr);
			params.put("msg", msg);
			LogUtil.printInfoLog("addMerMenus", params);
		} catch (Exception e) {
			msg = "权限分配失败！";
			params.put("msg", msg);
			LogUtil.printErrorLog("addMerMenus", params, e);
		}
		return msg;
	}
	/**
	 *  是否拥有按钮权限
	 * @param mid
	 * @param operId
	 * @param authStr
	 * @return
	 */
	public boolean [] hasButtonAuth(int []codes){
		boolean []retArr=new boolean[codes.length];
		LoginUser loginuser=operAuthDao.getLoginUser();
		String mid=loginuser.getMid();
		int operId=loginuser.getOperId();
		String authStr=operAuthDao.checkMenu(mid, operId);
		for (int i = 0; i < codes.length; i++) {
			retArr[i]=MenuService.hasThisAuth(authStr,codes[i]);
		}
		return retArr;
	}
	/**
	 * 移除我的账户权限
	 * @param auth 
	 * @return
	 */
	public String removeMyAcountAuth(String auth){
		List<Integer> list=MenuService.getMyaccountPid();
		String strSuth="";
		char []autharry=auth.toCharArray();
		for (int i = 0; i < list.size(); i++) {
			Integer temp=list.get(i);
			if(autharry.length>temp){
				autharry[temp]='0';
			}
		}
		for (int i = 0; i < autharry.length; i++) {
			strSuth=strSuth+autharry[i];
		}
		return strSuth;
	}
	
	/**
	 * 查询商户功能配置信息
	 * @param mid 商户号
	 * @return
	 * @throws Exception 
	 */
	public List<String> getZHGNByMid(String mid) throws Exception{
		List<String> infoList=new ArrayList<String>();
		Minfo minfo=merInfoDao.selectMinfoConfigByMid(mid);
		if(null==minfo)
			throw new Exception("商户不存在!");
		infoList.add(minfo.getDfConfig());
		infoList.add(minfo.getDkConfig());
		infoList.add(minfo.getMultiProcessDay());
		infoList.add(minfo.getMultiProcessTime());
		return infoList;
	}
	
	public String updateZHGNPZ(String mid,String dfCFGStr,String dkCFGStr,String weekStr,String sysHandleTimesStr){
		String msg = "";
		if(merInfoDao.updateZHGNPZ(mid, dfCFGStr, dkCFGStr, weekStr, sysHandleTimesStr)==1){
			msg = "success";
		}else{
			msg = "failed";
		}
		merInfoDao.saveOperLog("账户功能配置", msg);
		return msg;
	}
	//刷新账户功能配置
	public boolean reflushsavaConfig() {
		try {
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "minfo");
			p.put("k", "");
			p.put("f", "");
			return EWPService.refreshEwpETS(p) ? true : false;
		} catch (Exception e) {
			LogUtil.printErrorLog("reflushsavaConfig", e);
			return false;
		}

	}
	public Minfo checkmidiscunzai(String mid) throws Exception{ 
		//Minfo mer = merInfoDao.getOneMinfo(mid);
		return merInfoDao.getOneMinfo(mid);
	}
	
	public List<RYTGate> querygate(String bankname,String bankNo){
		return merInfoDao.querygate(bankname,bankNo);
	}
	
	//查询联行号
	
	public CurrentPage<BankNoInfo> queryBKNo(Integer PageNo,String gate,String bkname ){
		return merInfoDao.queryBKNo(PageNo,gate,bkname);
	}
	
	public CurrentPage<OperLog> getMidOperLog(String mid, String operId, int sdate, int edate, int pageIndex, int pageSize) {
		
		return merOperDao.getMidOperLog(mid, operId, sdate, edate, pageIndex,pageSize);
	}
	
	public static void syncMinfo(String mid){
		Thread trd=new Thread(new MerInfoSync2YCK(mid));
		trd.start();
	}
	
	/**
	 * 同步账户系统商户信息线程类
	 * @author yang.yaofeng
	 *
	 */
	static class MerInfoSync2YCK implements Runnable{
		private String mid;//商户号

		public MerInfoSync2YCK(String mid){
			this.mid=mid;
		}
		
		@Override
		public void run() {
			new MerInfoAlterSync().syncMinfo(mid);//商户信息同步
		}
	}
}
