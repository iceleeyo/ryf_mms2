package com.rongyifu.mms.modules.merchant.service;

import java.util.Map;

import com.rongyifu.mms.bean.DynamicToken;
import com.rongyifu.mms.modules.merchant.SystemType;
import com.rongyifu.mms.modules.merchant.dao.DynamicTokenDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DynamicCodeUtils;
import com.rongyifu.mms.utils.LogUtil;

public class TokenManagementService {
	
	private DynamicTokenDao dao = new DynamicTokenDao();
	
	/**
	 * 启用/禁用
	 */
	public String toggleStatus(int currentStatus,int id){
		String msg = "设置失败";
		int toStatus = 0;
		try {
			toStatus = currentStatus == DynamicToken.STATUS_NOT_ACTIVE?DynamicToken.STATUS_ACTIVE:DynamicToken.STATUS_NOT_ACTIVE;
			if(1 == dao.setStatus(toStatus,id)) msg = "设置成功";
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "toggleStatus", e.getMessage(), e);
		}
		
		String logMsg = "id=" + id + " status=" + toStatus + " 操作结果：" + msg;
		dao.saveOperLog("令牌启用/禁用", logMsg);
		
		return msg; 
	}
	
	/**
	 * 分页查询
	 */
	public CurrentPage<DynamicToken> queryConfigForPage(DynamicToken token,int pageNo){
		return dao.queryConfigForPage(token, pageNo);
	}

	public DynamicToken queryConfigById(int id){
		return dao.queryConfigById(id);
	}
	
	/**
	 * 添加token
	 */
	public String addToken(DynamicToken token){
		String msg = "新增失败";
		//1.判断token是否已经激活 若未激活 先激活token 若已激活 看该token是否已经和其他token
		try{
			if(!dao.isRegistered(token.getTokenSn())){//未和令牌服务器绑定,先进行绑定操作
				String userName = genUserName(token);
				token.setUserName(userName);
				if(DynamicCodeUtils.tokenBind(userName, token.getTokenSn())){
					if(0 == dao.addToken(token)) 
						throw new Exception("保存动态令牌绑定信息失败");
				}
			}
			if(0 == dao.addTokenConfig(token)) 
				throw new Exception("保存动态令牌配置信息失败");
			msg = "新增成功";
		}catch (Exception e) {
			LogUtil.printErrorLog(getClass().getCanonicalName(), "addToken", e.getMessage(), e);
			msg += ", " + e.getMessage();
		}
		
		saveOperLog(token, "动态令牌新增绑定", msg);
		
		return msg;
	}
	
	/**
	 * 根据规则生成和动态令牌绑定的userName
	 * @throws Exception 
	 */
	private String genUserName(DynamicToken token) throws Exception{
		return SystemType.getByCode(token.getSystem())+"_"+token.getMid()+"_"+token.getOperId();
	}
	
	/**
	 * 修改token
	 */
	public String modifyTokenConfig(DynamicToken token){
		String msg = "修改失败";
		try{
			int count = dao.modifyTokenConfig(token);
			if(1 == count) msg = "修改成功";
		}catch(Exception e){
			LogUtil.printErrorLog(getClass().getCanonicalName(), "addToken", e.getMessage(), e);
			msg += ", " + e.getMessage();
		}
		
		saveOperLog(token, "动态令牌修改绑定", msg);
		
		return msg;
	}
	
	public Map<Integer,String> getSystemTypeMap(){
		return SystemType.getTypeMap();
	}
	
	/**
	 * 保存操作日志	
	 * @param token
	 * @param action
	 * @param msg
	 */
	private void saveOperLog(DynamicToken token, String action, String msg){
		String logMsg = "商户号：" + token.getMid() + " 操作员：" + token.getOperId() + " 令牌序列号：" + token.getTokenSn() + " 操作结果：" + msg;
		dao.saveOperLog(action, logMsg);
	}
}
