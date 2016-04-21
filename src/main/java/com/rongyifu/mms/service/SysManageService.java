package com.rongyifu.mms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;

import com.rongyifu.mms.bean.B2EGate;
import com.rongyifu.mms.bean.Gate;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.GlobalParams;
import com.rongyifu.mms.bean.MMSNotice;
import com.rongyifu.mms.bean.NewGate;
import com.rongyifu.mms.bean.RYTGate;
import com.rongyifu.mms.bean.VisitIpConfig;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import com.rongyifu.mms.datasync.DataSyncUtil;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class SysManageService {
	
	private SystemDao dao = new SystemDao();
	private Logger logger = Logger.getLogger(SysManageService.class);
	/**
	* @title: addGateRoute
	* @description: 渠道新增
	* @author li.zhenxing
	* @date 2014-11-10
	* @param gid
	* @param name
	* @param merNo
	* @param requestUrl
	* @param remark
	*/ 
	public String addGateRoute(Integer gid,String name,String merNo,String requestUrl,String remark){
		String msg = "添加失败";
		try {
			if(null != gid && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(merNo) && StringUtils.isNotBlank(requestUrl) && StringUtils.isNotBlank(remark)){
				int count  = dao.addGateRoute(gid, name, merNo, requestUrl, remark);
				dao.saveOperLog("支付渠道维护", "gid:"+gid+",name:"+name+",merNo:"+merNo+",requestUrl:"+requestUrl+",remark:"+remark);
				if(count == 1){
					msg = AppParam.SUCCESS_FLAG;
				}
			}
		} catch (DuplicateKeyException e) {
			logger.error(e.getMessage(), e);
			msg+=",支付渠道ID ["+gid+"] 已存在！";
		} catch (Exception ee) {
			logger.info(ee.getMessage(), ee);
		}
		return msg;
	}
	
	/**
	 * @param ids
	 * @param status
	 * @return 
	 * 批量启用
	 */
	public String checkBatchChangeRouteByGate(List<Integer> ids,Integer status){
		String msg = "FAIL";
		try {
			int[] rst = dao.checkBatchChangeRouteByGate(ids,status);
			if(1 == status){//审核成功
				//审核成功后刷新缓存
				RYFMapUtil util = RYFMapUtil.getInstance();
				util.refreshFeeCalcModel();
				//刷新ewp缓存 ewp是刷新整个fee_calc_mode整个表 刷新一次就好了
				Map<String, Object> p = new HashMap<String, Object>();
		 		p.put("t", "fee_calc_mode");
		 		p.put("k", "");
		 		p.put("f", "");
		 		EWPService.refreshEwpETS(p);
				msg = "OK";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return msg;
	}
	
	/**
	 * @param id
	 * @param status
	 * @return
	 * 审核 按网关 申请修改渠道
	 */
	public String checkChangeRouteByGate(Integer id, Integer status){
		String msg = "FAIL";
		try {
			if (id == null || status == null) {
				return msg;
			}
			int[] rslt = dao.checkChangeRouteByGate(id, status);
			if(null != rslt){
				if(1 == status){
					//审核成功后刷新缓存
					RYFMapUtil util = RYFMapUtil.getInstance();
					util.refreshFeeCalcModel();
					//刷新ewp缓存 ewp是刷新整个fee_calc_mode整个表 刷新一次就好了
					Map<String, Object> p = new HashMap<String, Object>();
			 		p.put("t", "fee_calc_mode");
			 		p.put("k", "");
			 		p.put("f", "");
			 		EWPService.refreshEwpETS(p);
				}
				msg = "OK";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
		}
		return msg;
	}
	
	/**
	 * @return
	 * 显示批量切换渠道申请信息
	 */
	public CurrentPage<Gate> showBatchRouteApply(Integer transMode,Integer rytGate, Integer pageNo ){
		return dao.queryApplyPage(pageNo,new AppParam().getPageSize(),transMode, rytGate);
	}
	
	
	/**
	 * @return
	 * 申请 按 网关切换渠道
	 */
	public String applySetRouteOfGate(List<Integer> gateList,Integer gid,Integer transMode){
		String flag = "FAIL";
		try {
			dao.applySetRouteOfGate(gateList, gid, transMode);
			flag = "OK";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return flag;
	}
	
	/**
	 * @param routeId 渠道id
	 * @return List<Gate>
	 * 根据渠道查询出所有支持该渠道的网关
	 */
	public List<Gate> queryGatesByRoute(Integer routeId,Integer transMode){
		StringBuilder sql = new StringBuilder("SELECT DISTINCT(g.ryt_gate), r.gate_name gateDesc from gates g, ryt_gate r where r.gate=g.ryt_gate and g.gid=").append(routeId);
		if(null != transMode && transMode != -1){
			sql.append(" AND g.trans_mode=").append(transMode);
		}
		return dao.query(sql.toString(), Gate.class);
	}

	/**
	 * 刷新商户的网关列表
	 * 
	 * @param mid
	 */
	public boolean refreshGateRoute() {
		try {
			//刷新mms的gate_route缓存
			RYFMapUtil.refreshGateRoutMap();
			//刷新ewp
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "gate_route");
			p.put("k", "");
			p.put("f", "");
			return EWPService.refreshEwpETS(p) ? true : false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean reflushMerIP() {
		try {
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("t", "visit_ip_config");
			p.put("k", "");
			p.put("f", "");
			return EWPService.refreshEwpETS(p) ? true : false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}

	}

	/**
	 * 根据Id查找 消息通知
	 * 
	 * @param id
	 * @return
	 */
	public MMSNotice getMessageById(int id) {
		if (id == 0)
			return null;
		try {
			return dao.getMessageById(id);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public List<Integer> getAuthorTypeByGate(int gate) {
		return dao.getAuthorTypeByGate(gate);
	}

	public List<NewGate> getGateByTJ(String gate_d, String type) {
		return dao.getGateByTJ(gate_d, type);
	}

	/**
	 * 查询所有参数
	 * 
	 * @return
	 */
	public List<GlobalParams> queryAllParams() {
		List<GlobalParams> list = new ArrayList<GlobalParams>();
		try {
			list = dao.queryAllParams();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return list;
	}

	/**
	 * 修改参数
	 * 
	 * @param globalParams
	 * @return
	 */
	public String editParam(GlobalParams globalParams) {
		try {
			dao.saveOperLog("系统参数配置", "[修改系统参数] 参数名："+globalParams.getParName()+" 参数值："+globalParams.getParValue());
			dao.editParam(globalParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "false";
		}
		ParamCache.refreshGolbalParams();
		ParamCache.refreshMinfo();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "global_params");
		p.put("k", globalParams.getParName());
		return EWPService.refreshEwpETS(p) ? AppParam.SUCCESS_FLAG
				: "资料修改成功，刷新ewp失败!";
	}

	/**
	 * 增加消息通知（系统配置页面）
	 */
	public String addOrEditMessage(MMSNotice notice, String type) {
		String msg = "ok";
		try {
			dao.addOrEditMessage(notice, type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			msg = "error";
		}
		return msg;
	}

	/**
	 * 修改消息通知（查询）
	 * 
	 * @param notice
	 * @return
	 */
	public List<MMSNotice> getMessage(MMSNotice notice) {
		List<MMSNotice> l = null;
		try {
			notice.setMid(dao.getLoginUser().getMid());
			l = dao.getMessage(notice);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return l;
	}

	/**
	 * 返回首次登陆时页面显示的系统通知（显示标题）
	 * 
	 * @return
	 */
	public List<MMSNotice> queryMMSNotice(String mid) {
		return dao.queryMMSNotice(mid);
	}

	/**
	 * 删除系统通知信息
	 * 
	 * @param id
	 * @return
	 */
	public String deleteNotice(String id) {
		return dao.deleteNotice(id);
	}

	/*
	 * 增加银行网关
	 * SysManageService.addGates(
	 * 				gate_name,bankgate,fee_model,ryt_Gate,
	 * 				transMode,gateRouteId,feeFlag)
	 */
	public String addGates(String gateName, String bankgate, String fee_model,
			int rytGate, int transMode, int gid, int feeFlag) {
		int ret = dao.gateIsExist(rytGate, gid);//
		if (ret > 0) {
			return "该网关已存在！";
		}
		try {
			int effectRow = dao.addGates(gateName, bankgate, fee_model,
					rytGate, transMode, gid, feeFlag);
			dao.saveOperLog("银行网关维护", "[新增网关配置] 网关号:"+bankgate+" 网关名称:"+gateName+" 支付渠道:"+gid+" 计费公式："+fee_model);
			return effectRow == 1 ? "增加成功!" : "增加失败，请核实数据是否正确。";

		} catch (DuplicateKeyException e) {

			return "该网关已存在！";

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			return "增加银行网关异常！";
		}
	}

	/**
	 * 根据交易方式查找对象的网关Map
	 * 
	 * @param transMode
	 * @return
	 */
	public Map<Integer, String> queryGatesMapByTransMode(String transMode)
			throws Exception {
		Map<Integer, String> objs = new TreeMap<Integer, String>();
		for (RYTGate obj : RYFMapUtil.getRytAllGates()) {
			if (obj.getTransMode() == Integer.parseInt(transMode))
				objs.put(obj.getGate(), obj.getGateName());
		}
		return objs;

	}

	public List<Gate> queryGates(int tranModel, int gateRouteId) {
		return dao.getGateByTJ(tranModel, gateRouteId);
	}

	/**
	 * 修改银行网关
	 * 
	 * @param id
	 * @param transModel
	 * @param rytGate
	 * @param gateId
	 * @param feeMode
	 * @return
	 */
	public String editGates(int gid, int id, int transModel, int rytGate,
			String gateId, String feeMode, int feeFlag) {
		int[] count = dao.editGates(gid, id, transModel, rytGate, gateId,
				feeMode, feeFlag);
		dao.saveOperLog("修改银行网关", "修改银行网关为"+gid);
		if (count == null) {
			return "修改失败,请重试！";
		}
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("t", "fee_calc_mode");
		p.put("k", "");
		p.put("f", "");
		boolean flag = EWPService.refreshEwpETS(p);
		return flag ? AppParam.SUCCESS_FLAG : "修改成功!刷新Ewp失败！";
	}

	/**
	 * 调用存储过程
	 * 
	 * @param procName
	 * @return
	 */
	public String callProc(String procName) {
		dao.saveOperLog("数据库维护", "调用存储过程为"+procName);
		String sql = null;
		if (procName.equals("switchdata")) {
			sql = "{call Switchdata()}";
			return switchdata(sql);
		} else if (procName.equals("collect")) {
			sql = "{call Collect(?)}";
			return collect(sql);
		} else if (procName.equals("dailyCollect")) {
			sql = "{call dailyCollection()}";
			return dailyCollect(sql);
		}
		return "";
	}

	public String collect(String sql) {
		try {
			dao.collect(sql);
		} catch (Exception e) {
			System.err.println("Exception is ");
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "ok";
	}

	public String dailyCollect(String sql) {
		try {
			dao.dailyCollect(sql);
		} catch (Exception e) {
			System.err.println("Exception is ");
			logger.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "ok";
	}

	public String switchdata(String sql) {
		return dailyCollect(sql);
	}
	
	/**
	 * 获得权限类型
	 * 
	 * @return
	 */
	public HashMap<Integer, String> getAuthType() {
		Map<Integer, String> map = AppParam.auth_type;
		HashMap<Integer, String> authType = new HashMap<Integer, String>();
		authType.putAll(map);
		return authType;
	}


	/**
	 * 网关新增
	 * 
	 * @param rytGate
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String addRytGate(Integer gate, Integer transMode, String gateName) {
		try {
			int effectRow = dao.addRytGate(gate, transMode, gateName);
			if (effectRow == 1) {
				RYFMapUtil obj = RYFMapUtil.getInstance();
				obj.putGate(gate, gateName,transMode);
				dao.saveOperLog("银行网关维护", "[新增网关] 网关号："+gate+" 网关名称："+gateName+" 交易类型："+transMode);
				return AppParam.SUCCESS_FLAG;
			} else {
				return "增加失败！";
			}
		} catch (DuplicateKeyException e) {
			return "此网关号存在，请重新输入其它可用网关号！";
		} catch (Exception e) {
			return "增加失败！" + e.getMessage();
		}
	}

	/**
	 * 支付网关表list
	 * 
	 * @return
	 */
	public List<GateRoute> queryGateRoute() {

		return dao.queryGateRouteList();
	}

	public String eidtRequestUrlByGid(Integer gid, String url) {

//		if (gid == 50100 || gid == 80002) {

			dao.getJdbcTemplate().update("update gate_route set request_url = '" + url + "' where gid = " + gid);

			dao.saveOperLog("支付渠道维护", "修改request_url为:"+url);
			
			return AppParam.SUCCESS_FLAG;
			
			
			
//		} else {
//
//			return "该支付渠道不允许修改";
//		}
	}

	/**
	 * 修改支付渠道名称
	 * 
	 * @return
	 */
	public String updateGateRouteBasicInfo(Integer gid,String name,String merNo,String requestUrl,String remark) {
		dao.saveOperLog("支付渠道维护", "gid:"+gid+",name:"+name+",merNo:"+merNo+",requestUrl:"+requestUrl+",remark:"+remark);
		int row = dao.updateGateRoute(gid,name,merNo,requestUrl,remark);
		if (row == 1) {
//			RYFMapUtil.refreshGateRoutMap();
			return AppParam.SUCCESS_FLAG;
		} else {
			return "修改失败！";
		}
	}

	/**
	 * 收款账户信息
	 * 
	 * @param gateRoute
	 * @return
	 */
	public String editeGateMassageByGid(GateRoute gateRoute) {
		int row = dao.eidtGateMassage(gateRoute);
		
		
		dao.saveOperLog("支付渠道维护", "修改id为"+gateRoute.getGid());
		
		
		
		if (row == 1) {
			return AppParam.SUCCESS_FLAG;
		} else {
			return "修改失败！";
		}
	}

	/**
	 * 备付金银行行号修改
	 * 
	 * @param gateRoute
	 * @return
	 */
	public String eidtBfBkNoByBkNo(Integer gid, String bfBkNo) {
		int row = dao.eidtBfBkNo(gid, bfBkNo);
		if (row == 1) {
			return AppParam.SUCCESS_FLAG;
		} else {
			return "修改失败！";
		}
	}

	/**
	 * 备付金银行行号下拉框查询
	 * 
	 * @param gateRoute
	 * @return
	 */
	public Map<String, String> getBkNoMap() {
		return dao.getBkNoMap();
	}
	/**
	 * 通过关键字搜索
	 * @param key
	 * @return
	 */
	public List<GateRoute> selectByKey(String key){
		return dao.selectByKey(key);
	}
	
	/**
	 * 查询商户ip配置
	 * @param key
	 * @return
	 */
	public CurrentPage<VisitIpConfig> queryMerIP(Integer pageNo, String mid,
			short type) {
		return dao.queryMerIP(pageNo, mid, type);

	}
	/**
	 * 新增商户ip配置
	 * @param key
	 * @return
	 */
	public String addMerIPConfig(String mid,String ip,short type){
		String msg="新增成功！";
//		ryf4.6 取消ip条数限制
//		int countmer=dao.countMer(mid); 
//		if(countmer>=5){
//			return  msg="一个商户配置的Ip不能超过5条"; 
//		}
	   try{
	   dao.addMerIPConfig(mid,ip,type);
	   }catch (Exception e) {
		// TODO: handle exception
	    return "操作异常，请重新再试或与管理员联系！";
	}
	   	dao.saveOperLog("新增商户IP配置", "操作成功-商户 "+mid+" 新增Ip "+ip);
		return msg;
		
	}
	
	public VisitIpConfig queryMerconfigByid(Integer id){
		return dao.queryMerconfigByid(id);
		
	}
	/**
	 * 修改商户ip配置
	 * @param key
	 * @return
	 */
	public String updateMerIPConfig(Integer id,String mid,String ip,short type){
		String msg="修改成功！";
//ryf4.6 取消ip条数限制
//		 int countmer=dao.countMer(mid); 
//		   if(countmer>=5){
//			  return  msg="一个商户配置的Ip不能超过5条"; 
//		   }
		try{
			   dao.updateMerIPConfig(id,mid,ip,type);
			   }catch (Exception e) {
				// TODO: handle exception
			    return "操作异常，请重新再试或与管理员联系！";
			}
		dao.saveOperLog("修改商户ip配置", "操作成功-id "+id+" ip "+ip);
		return msg;	
	}
	
	/**
	 * 删除商户ip配置
	 * @param key
	 * @return
	 */
	public String deleteMerIpConfig(Integer id){
		return dao.deleteMerIpConfig(id);
	}
	
	/**
	 * 查询代付风险管理信息
	 * @param gid
	 * @return
	 */
	public CurrentPage<B2EGate> queryDFBankInfo(String gid,int pageNo){
		return dao.queryDFBankInfo(gid,pageNo);
	}
	
	/**
	 * 代付银行余额报警初始化
	 * @return
	 */
	public Map<String, String> initDFYEBJ(){
		Map<String, String> map=new HashMap<String, String>();
		List<B2EGate> date= dao.initDFYEBJ();
		for (B2EGate b2eGate : date) {
			map.put(b2eGate.getGid()+"", b2eGate.getName());
		}
		return map;
	}
	/**
	 * 根据网关号查询信息
	 * @param gid 网关号
	 * @return
	 * @throws Exception 
	 */
	public B2EGate queryDFBankInfoByGid(String gid) throws Exception{
		if(Ryt.empty(gid))
			throw new Exception("网关号为空!");
		return dao.queryDFBankInfoByGid(gid);
	}
	
	/**
	 * 
	 * @param gid  网关id
	 * @return
	 * @throws Exception
	 */
	public GateRoute queryGateRoteByGid(String gid) throws Exception{
		if(Ryt.empty(gid))
			throw new Exception("网关号为空!");
		return dao.queryGateRoteByGid(gid);
	}
	
	/**
	 * 更新代付报警信息
	 * @param gid
	 * @param amt
	 * @param phone
	 * @param email
	 * @param state
	 * @return
	 * @throws Exception 
	 */
	public String updateConf(String gid,String amt,String phone,String email,String state) throws Exception{
		if(Ryt.empty(gid))
			throw new Exception("网关号为空!");
		if("1".equals(state) && "".equals(phone.trim()) && "".equals(email.trim())) 
			throw new Exception("请至少输入一个正确的手机号或邮箱!");
		int flag=dao.updateConf(gid,amt,phone,email,state);
		if(flag==1){
			dao.saveOperLog("更新代付报警信息", "操作成功-网关 "+gid);
			return "success";
		}
		else{
			dao.saveOperLog("更新代付报警信息", "操作失败-网关 "+gid);
			return "failed";
		}
	}
	
	/**
	 * 开启或关闭报警状态
	 * @param gid
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public String updateStateByGid(String gid,String state) throws Exception{
		if(Ryt.empty(gid))
			throw new Exception("网关号为空!");
		int flag = dao.updateStateByGid(gid, state);
		String action= state.equals("0")?"开启":"关闭";
		if (flag == 1){
			dao.saveOperLog(action, "操作成功-网关 "+gid);
			return "success";
		}
		dao.saveOperLog(action, "操作失败-网关 "+gid);
		return "failed";
	}
	
	public String isEmptyPhoneAndEmail(String gid)throws Exception{
		if(Ryt.empty(gid))
			throw new Exception("网关号为空!");
		B2EGate  gate=dao.queryDFBankInfoByGid(gid);
		if(null==gate)
			return "fail";
		if(!Ryt.empty(gate.getAlarmMail()) || !Ryt.empty(gate.getAlarmPhone()))
			return "pass";
		return "fail";
	}
	
	/**
	 * 同步文件上传
	 * @param paramMap 参数map
	 * @return
	 */
	public String[] uploadTBfile(Map<String, String> paramMap){
		String [] params={"",".txt"}; 
		
		String fileType=paramMap.get("fileType");
		
		if(!Ryt.empty(fileType)){
				if(fileType.equals("1"))
					params[0]=DataSyncUtil.POS_FILE_PATH;//VAS文件路径
				else if(fileType.equals("2"))
					params[0]=DataSyncUtil.VAS_FILE_PATH;//POS文件路径
				else if(fileType.equals("3"))
					params[0]=DataSyncUtil.PosDz_FILE_PATH;//Pos文件路径
				else
					params[0]="typeErr";
		}else{
			params[0]="typeErr";
		} 
		return params;
	}
	
	/**
	 * 根据Id查找 消息通知
	 * 
	 * @param id
	 * @return
	 */
	public MMSNotice getMessageById(String ids) {
		if (Ryt.empty(ids))
			return null;
		try {
			Map<String, String> map=new HashMap<String, String>();
			MMSNotice notice=new MMSNotice();
			List<MMSNotice> notices=dao.getMessageById(ids);
			String content="";
			String title=notices.get(0).getTitle();
			Integer noticeType=notices.get(0).getNoticeType();
			if(noticeType==1||noticeType==2){
				for (MMSNotice mmsNotice : notices) {
					content+=mmsNotice.getMid()+"-"+mmsNotice.getExpiration_date()+"|";
				}
			}
			content=StringUtils.removeEnd(content, "|");
			if(noticeType==1){
				content=handleContent(content);
				notice.setContent(new String("以下商户:(<br>"+content+"<br>),合同即将到期！"));
				notice.setTitle("商户合约到期-"+notices.get(0).getBeginDate());
			}else if(noticeType==2){
				content=handleContent(content);
				notice.setContent(new String("以下商户:(<br>"+content+"<br>),组织机构代码即将到期！"));
				notice.setTitle("商户组织机构代码到期-"+notices.get(0).getBeginDate());
			}else{
				notice.setContent(notices.get(0).getContent());
				notice.setTitle(title);
			}
			map.put("title", title);
			map.put("content", content);
			LogUtil.printInfoLog("SysManageService", "getMessageById", "params",map);
			return notice;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/***
	 * 分行 每行125个字符
	 * @param content
	 */
	private String handleContent(String content){
		String startStr="以下商户:(";
		Integer startStrLen=startStr.getBytes().length;
		String[] strs=content.split("\\|");
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			if((i % 5)==0 && i!=0){
				sb.append("<br>").append(StringUtils.leftPad("", startStrLen, "&"));
			}else if(i==0){
				sb.append(StringUtils.leftPad("", startStrLen, "&"));
			}
			sb.append(StringUtils.rightPad(strs[i], 24, "&")).append(("|"));
		}
		String result=StringUtils.removeEnd(sb.toString(), "|");
		return result.replace("&", "&nbsp;");
	}
}