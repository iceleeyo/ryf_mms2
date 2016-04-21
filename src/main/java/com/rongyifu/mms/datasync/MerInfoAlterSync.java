package com.rongyifu.mms.datasync;

import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.rongyifu.mms.bean.Minfo;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.MerInfoDao;
import com.rongyifu.mms.db.PubDao;
import com.rongyifu.mms.ewp.EWPService;
import com.rongyifu.mms.ewp.SendEmailServer;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;
import com.rongyifu.mms.utils.RYFMapUtil;

public class MerInfoAlterSync {
	
	//特定加密串 (特定加密串+商户号做MD5的值就是签名字段的值)
	private final static String SPECIFIED_STR="QYYCKSHXXTB";
			
	/**
	 * 商户信息同步 （商户新增、修改时调用）
	 * @param mid
	 * @return
	 */
	public JSONObject syncMinfo(String mid){
		String msg = "同步失败";
		JSONObject json = new JSONObject();
		MerInfoDao merInfoDao=new MerInfoDao();
		Minfo minfo=null;
		try {
			//根据商户号查询商户信息
			minfo=merInfoDao.getOneMinfo(Ryt.sql(mid));
			//构建请求参数信息
			Map<String, String> requestParaMap = getSyncReqBodyMap(minfo);
			
			//此处根据是不是P2P托管业务 进行判断选择同步的url（账务系统、托管系统二选一，清算系统都需要同步）
			String userId = "";
			if(Minfo.IS_NOT_P2P == minfo.getIsPtop()){
				userId = syncZhxt(minfo, requestParaMap);
			}else{
				userId = syncP2P(minfo, requestParaMap);
			}
			
			//处理账户系统和p2p托管系统的响应的userId
			json.put("userId", userId);

			//userId同步给pos系统 清算系统
			requestParaMap.put("user_id", userId);
			
			//同步给清算系统
            syncQsxt(minfo, requestParaMap);
            
			//商户信息同步给pos管理平台
            syncPos(minfo, requestParaMap);
			
			//同步完成之后刷新mms缓存
			RYFMapUtil.getInstance().refreshMinfoMap(mid);
			msg = "同步成功";
		} catch (Exception e) {
			msg = e.getMessage();
			LogUtil.printErrorLog("MerInfoAlterSync", "syncMinfo", msg, e);
			sendWarnMail(mid, msg);
		}
		json.put("msg", msg);
		return json;
	}
	/**
	 * 商户信息同步（ewp调用）
	 * @param mid
	 * @return
	 * @throws Exception 
	 */
	public JSONObject syncMinfo2(String mid) throws Exception{
		String msg = "同步失败";
		JSONObject json = new JSONObject();
		MerInfoDao merInfoDao=new MerInfoDao();
		Minfo minfo=null;
		try {
			//根据商户号查询商户信息
			minfo=merInfoDao.getOneMinfo(Ryt.sql(mid));
			//构建请求参数信息
			Map<String, String> requestParaMap = getSyncReqBodyMap(minfo);
			
			//此处根据是不是P2P托管业务 进行判断选择同步（账务系统、托管系统二选一，清算系统都需要同步）
			String userId = "";
			if(Minfo.IS_NOT_P2P == minfo.getIsPtop()){
				userId = syncZhxt(minfo, requestParaMap);
			}else{
				userId = syncP2P(minfo, requestParaMap);
			}
			
			//处理账户系统和p2p托管系统的响应的userId
			json.put("userId", userId);

			//userId同步给pos系统 清算系统
			requestParaMap.put("user_id", userId);
			
			//同步给清算系统
            syncQsxt(minfo, requestParaMap);
            
			//同步完成之后刷新mms缓存
			RYFMapUtil.getInstance().refreshMinfoMap(mid);
			msg = "同步成功";
		} catch (Exception e) {
			msg = e.getMessage();
			LogUtil.printErrorLog("MerInfoAlterSync", "syncMinfo2", msg, e);
			sendWarnMail(mid, msg);
			throw new Exception(e);
		}
		json.put("msg", msg);
		return json;
	}
	
	/**
	 * 商户信息同步（外部系统调用推送商户信息）
	 * @param mid
	 * @param syncType
	 * @return
	 * @throws Exception 
	 */
	public JSONObject syncMinfo(String mid, String syncType) throws Exception{
		String msg = "同步失败";
		JSONObject json = new JSONObject();
		MerInfoDao merInfoDao=new MerInfoDao();
		
		//根据商户号查询商户信息
		Minfo minfo=merInfoDao.getOneMinfo(Ryt.sql(mid));
		//构建请求参数信息
		Map<String, String> requestParaMap = getSyncReqBodyMap(minfo);
		
		// 获取user_id，如果minfo表中没有，则从账户系统/P2P系统获取
		String userId = "";
		if(Ryt.empty(minfo.getUserId()) || syncType.indexOf("3") != -1){
			if(Minfo.IS_NOT_P2P == minfo.getIsPtop()){
				userId = syncZhxt(minfo, requestParaMap);
			}else{
				userId = syncP2P(minfo, requestParaMap);
			}
		} else
			userId = minfo.getUserId();
		
		//处理账户系统和p2p托管系统的响应的userId
		json.put("userId", userId);
		//添加账户系统或p2p托管系统返回的userId
		requestParaMap.put("user_id", userId);
		
		//同步给pos系统
		if(syncType.indexOf("1") != -1)
			syncPos(minfo, requestParaMap);
		
		//同步给清算系统
		if(syncType.indexOf("2") != -1)
			syncQsxt(minfo, requestParaMap);
        
		msg = "同步成功";
		json.put("msg", msg);
		return json;
	}
	
	/**
	 * @param minfo 商户信息
	 * @return 请求的map
	 */
	public Map<String, String> getSyncReqBodyMap(Minfo minfo){
		Map<String, String> requestParaMap=new TreeMap<String, String>();
		requestParaMap.put("acc_name", minfo.getEmail0());//账户名
		requestParaMap.put("org_id", minfo.getId());//商户号
		requestParaMap.put("name", minfo.getName());//商户名
		requestParaMap.put("type", "2");//固定传2 普通账户
		requestParaMap.put("group_acc", "");//所属集团账户名,暂为空
		requestParaMap.put("short_name", minfo.getAbbrev());//商户简称
		requestParaMap.put("addr", minfo.getAddr());//商户地址
		requestParaMap.put("start_date", minfo.getBeginDate()+"");//合同开始日期
		requestParaMap.put("end_date", minfo.getExpDate()+"");//合同结束日期
		requestParaMap.put("corporation", minfo.getCorpName());//法人
		requestParaMap.put("reg_code", minfo.getRegCode());//工商登记号
		requestParaMap.put("province", minfo.getProvId()+"");//省份ID
		requestParaMap.put("industry", minfo.getMerTradeType()+"");//所属行业
		requestParaMap.put("status", minfo.getMstate()+"");//商户状态
		requestParaMap.put("contactor", minfo.getContact0());//主联系人
		requestParaMap.put("telephone", minfo.getTel0());//商户主联系电话
		requestParaMap.put("phone", minfo.getCell0());//商户主联系人手机
		requestParaMap.put("contactor1", minfo.getContact1());//商户财务人员
		requestParaMap.put("telephone1", minfo.getTel1());//商户财务人员联系电话
		requestParaMap.put("phone1", minfo.getCell1());//商户财务人员联系手机
		requestParaMap.put("email1", minfo.getEmail1());//商户财务人员email
		requestParaMap.put("contactor2", minfo.getContact2());//商户技术联系人
		requestParaMap.put("telephone2", minfo.getTel2());//商户技术联系人电话
		requestParaMap.put("phone2", minfo.getCell2());//商户技术联系人手机
		requestParaMap.put("email2", minfo.getEmail2());//商户技术联系人email
		requestParaMap.put("contactor3", minfo.getContact3());//商户运行联系人
		requestParaMap.put("telephone3", minfo.getTel3());//商户运行联系人电话
		requestParaMap.put("phone3", minfo.getCell3());//商户运行联系人手机
		requestParaMap.put("email3", minfo.getEmail3());//商户运行联系人email
		requestParaMap.put("contactor4", minfo.getContact4());//商户市场联系人
		requestParaMap.put("telephone4", minfo.getTel4());//商户市场联系人电话
		requestParaMap.put("phone4", minfo.getCell4());//商户市场联系人手机
		requestParaMap.put("email4", minfo.getEmail4());//商户市场联系人email
		requestParaMap.put("contactor5", minfo.getContact5());//商户市场联系人
		requestParaMap.put("telephone5", minfo.getTel5());//商户市场联系人电话
		requestParaMap.put("phone5", minfo.getCell5());//商户市场联系人手机
		requestParaMap.put("email5", minfo.getEmail5());//商户市场联系人email
		requestParaMap.put("cred", minfo.getIdType());//证件类型
		requestParaMap.put("cred_no", minfo.getIdNo());//身份证号
		requestParaMap.put("open_amount", minfo.getBeginFee()+"");//开通费
		requestParaMap.put("minfos_year", minfo.getAnnualFee()+"");//年费
		requestParaMap.put("is_refund", minfo.getRefundFlag()+"");//是否允许退款
		requestParaMap.put("bank_date", minfo.getOpenDate()+"");//开户银行日期
		requestParaMap.put("bank_name", minfo.getBankName());//开户银行名
		requestParaMap.put("bank_acct", minfo.getBankAcct());//开户银行帐号
		requestParaMap.put("bank_province", minfo.getBankProvId()+"");//开户银行省份
		requestParaMap.put("bank_user", minfo.getBankAcctName());//开户帐号名称
		requestParaMap.put("bank_sub", minfo.getBankBranch());//开户银行支行名称
		requestParaMap.put("cocontractor", minfo.getSignatory());//签约人
		requestParaMap.put("minfos_amount", minfo.getCautionMoney()+"");//商户保证金
		requestParaMap.put("minfos_fax", minfo.getFaxNo());//商户传真
		requestParaMap.put("organization", minfo.getCorpCode());//组织机构代码
		requestParaMap.put("organization_date", minfo.getCodeExpDate()+"");//组织机构代码有效期
		requestParaMap.put("minfos_remark", minfo.getMdesc());//商户描述
		requestParaMap.put("bank_no", minfo.getOpenBkNo());//银行行号 用于自动结算
		
		requestParaMap.put("category", minfo.getCategory()+"");//商户类别
		requestParaMap.put("pbk_acc_no", minfo.getPbkAccNo());//银行账号 用于自动代付
		requestParaMap.put("pbk_branch", minfo.getPbkBranch());//开户银行支行名 用于自动代付
		requestParaMap.put("pbk_prov_id", minfo.getPbkProvId());//开户银行省份 用于自动代付
		requestParaMap.put("pbk_acc_name", minfo.getPbkAccName());//开户账号名 用于自动代付
		requestParaMap.put("pbk_gate_id", minfo.getPbkGateId()+"");//网关号 用于自动代付
		requestParaMap.put("pbk_name", minfo.getPbkName());//开户银行名 用于自动代付
		requestParaMap.put("pbk_no", minfo.getPbkNo());//联行号 用于自动代付
		requestParaMap.put("web_url", minfo.getWebUrl());//商户服务器地址
		requestParaMap.put("refund_fee", minfo.getRefundFee()+"");//退款是否退手续费
		requestParaMap.put("gate_id", minfo.getGateId()+"");//网关号
		requestParaMap.put("liq_limit", minfo.getLiqLimit()+"");//结算最少金额
		requestParaMap.put("liq_obj", minfo.getLiqObj()+"");//结算账户类型
		requestParaMap.put("liq_period", minfo.getLiqPeriod()+"");//结算周期
		requestParaMap.put("liq_state", minfo.getLiqState()+"");//结算状态
		requestParaMap.put("liq_type", minfo.getLiqType()+"");//结算方式
		requestParaMap.put("liq_time", "24");//结算时间点
		requestParaMap.put("mer_type", minfo.getMerType());//商户类型
		requestParaMap.put("man_liq", minfo.getManLiq()+"");//是否手工结算
		
		// 自动结算
		// 结算对象：银行卡（0）
		// 手工结算：关闭（0）
		int isAutoLiq = 0;
		if(0 == minfo.getLiqObj() && 0 == minfo.getManLiq()){
			isAutoLiq = 1;
		}
		requestParaMap.put("is_auto_liq", isAutoLiq+"");//是否自动结算	0 非自动结算	1自动结算
		// 自动代付
		// 结算对象：电银账户（1）
		// 自动代付状态：开启（1）
		int isAutoDf = 0;
		if(1 == minfo.getLiqObj() && 1 == minfo.getAutoDfState()){
			isAutoDf = 1;
		}
		requestParaMap.put("is_auto_df", isAutoDf+"");//是否自动代付	0 非自动代付	1自动代付
		
		return requestParaMap;
	}
	
	/**
	 * 发送异常邮件
	 * @param mid 商户号
	 * @param errMsg 错误原因
	 */
	public void sendExcpMail(String mid,String errMsg){
		
		String mmsip=ParamCache.getStrParamByName("mmsip");
		//手工同步链接
		String url=mmsip+"/mms/go?transCode=ZHXTXXTB&mid="+mid+"&sign="+MD5.getMD5((SPECIFIED_STR+mid).getBytes());
		
		//报警邮件标题
		String title="企业预存款账户信息同步失败-"+DateUtil.today();
		
		//报警邮件内容
		StringBuffer content=new StringBuffer();
		content.append("商户：").append(mid);
		content.append(" 企业预存款账户信息同步失败 点击以下链接手工同步,错误原因："+errMsg+"\n");
		content.append(" 手工同步链接:").append(url);
		
		String receive=ParamCache.getStrParamByName("MailTos");//收件人地址
		
		//发送报警邮件
		SendEmailServer ses=new SendEmailServer(content.toString(),title,receive);
		Thread thr=new Thread(ses);
		thr.start();
		
		
	}
	
	/**
	 * 处理返回信息
	 * @param response 返回报文
	 * @param mid 商户号
	 * @throws DocumentException 
	 */
	@SuppressWarnings("rawtypes")
	public String handleResponse(String response,Minfo minfo) throws Exception{
		String sysName = Minfo.IS_NOT_P2P == minfo.getIsPtop()?"账户系统":"P2P托管系统";
		if(StringUtils.isEmpty(response)) throw new Exception("请求"+sysName+"失败");
		String mid = Ryt.sql(minfo.getId());
		String userId = "";
		if(Minfo.IS_NOT_P2P == minfo.getIsPtop()){
			Document doc = null;
			doc = DocumentHelper.parseText(response);
			Element root = doc.getRootElement();
			String value = root.element("status").element("value").getText();
			if ("0".equals(value)){//同步成功
				  LogUtil.printInfoLog("MerInfoAlterSync", "handleResponse", "商户: "+mid+" "+sysName+" 商户信息同步成功!");
				  userId = root.element("userId").getText();
			}else{//同步失败
				//错误原因
				String errMsg = root.element("status").element("msg").getText();
				throw new Exception(sysName+" 商户信息同步失败, 错误原因："+errMsg);
			}
		}else{
			JSONObject responseJson = JSONObject.fromObject(response);
			String resCode = responseJson.getString("resCode");//{"tranCode":"000","resCode":"-100","resMsg":"unkonwn error"}
			if("000".equals(resCode)){
				LogUtil.printInfoLog("MerInfoAlterSync", "handleResponse", "商户: "+mid+" "+sysName+" 商户信息同步成功!");
				userId = responseJson.getString("mid");
			}else{
				throw new Exception(sysName+" 商户信息同步失败");
			}
		}
		if(StringUtils.isBlank(userId)){
			throw new Exception(sysName+"返回的userId为空");
		}
		if (!userId.equals(minfo.getUserId())) {
			String sql = "UPDATE minfo SET user_id = ? WHERE id = ?";
			int count = new PubDao().update(sql, new Object[] { userId, mid });
			if(0 == count){
				throw new Exception("更新userId失败, mid = "+ mid +", userId = "+ userId);
			}
		}
		return userId;
	}
	
	/**
	 * 手工同步
	 * @param sign 签名值
	 * @param mid 商户号
	 * @return
	 */
	public String syncSg(String sign,String mid){
		String md5Value=MD5.getMD5((SPECIFIED_STR+mid).getBytes());
		
		if(!md5Value.equals(sign)) return "签名错误，操作失败!";
		
		try {
			return syncMinfo(mid).getString("msg");//商户信息同步
		} catch (Exception e) {
			LogUtil.printErrorLog("MerInfoAlterSync", "syncSg", e.getMessage(), e);
			return "同步失败";
		}
	}
    
    /**
     * 商户信息同步 → 账户系统
     * @param minfo
     * @param requestParaMap
     * @return
     * @throws Exception
     */
	private String syncZhxt(Minfo minfo, Map<String, String> requestParaMap) throws Exception {
		// 设置交易码
		requestParaMap.put("tranCode", "EN0001");
		// 调用接口
		String response = syncMinfo("ZHXT_SYNC_URL", requestParaMap);		
		
		LogUtil.printInfoLog("MerInfoAlterSync", "syncZhxt", "response: " + response);

		return handleResponse(response, minfo);
	}
    
	/**
     * 商户信息同步 → P2P资金托管
     * @param minfo
     * @param requestParaMap
     * @return
     * @throws Exception
     */
	private String syncP2P(Minfo minfo, Map<String, String> requestParaMap) throws Exception {
		// 设置交易码
		requestParaMap.put("tranCode", "TG0079");
		// 调用接口
		String response = syncMinfo("P2P_SYNC_URL", requestParaMap);
		
		LogUtil.printInfoLog("MerInfoAlterSync", "syncP2P", "response: " + response);
		
		// 返回处理
		return handleResponse(response, minfo);
	}
	
	/**
	 * 商户信息同步 → 清算系统
	 * @param minfo
	 * @param requestParaMap
	 * @throws Exception
	 */
	private void syncQsxt(Minfo minfo, Map<String, String> requestParaMap) throws Exception{
		// 清除交易码
		if(requestParaMap.containsKey("tranCode"))
			requestParaMap.remove("tranCode");
		// 调用接口
		String response = syncMinfo("QSXT_SYNC_URL", requestParaMap);
		
		LogUtil.printInfoLog("MerInfoAlterSync", "syncQsxt", "response: " + response);
		// 处理返回结果
		if(!"success".equals(response)){
			// 邮件报警
			sendWarnMail(requestParaMap.get("org_id"), "清算系统返回信息错误：" + response);
		} 
	}
	
	/**
	 * 商户信息同步 → pos管理平台
	 * @param minfo
	 * @param requestParaMap
	 * @throws Exception
	 */
	private void syncPos(Minfo minfo, Map<String, String> requestParaMap) throws Exception{
		// 清除交易码
		if(requestParaMap.containsKey("tranCode"))
			requestParaMap.remove("tranCode");
		// 调用接口
		String response = syncMinfo("POS_SYNC_URL", requestParaMap);
		
		LogUtil.printInfoLog("MerInfoAlterSync", "syncPos", "response: " + response);
		
		// 处理返回结果 <?xml version=”1.0” encoding=”utf-8”?><res><status><value>0</value><msg></msg></status></res>
		Document doc = DocumentHelper.parseText(response);
		Element root = doc.getRootElement();
		Element StatusElement = root.element("status");
		String value = StatusElement.element("value").getText();
		if (!"0".equals(value)){
			String msg = StatusElement.element("msg").getText();
			// 邮件报警
			sendWarnMail(requestParaMap.get("org_id"), "pos管理平台返回错误信息：" + msg);
		}
	}
	
	private int count = 0; // 用于控制商户信息的打印
	
	/**
	 * 同步商户信息
	 * @param urlName
	 * @param requestParamMap
	 * @return
	 * @throws Exception
	 */
	private String syncMinfo(String urlName, Map<String, String> requestParamMap) throws Exception{
		String reqUrl = ParamCache.getStrParamByName(urlName);
		if (StringUtils.isBlank(reqUrl))
			throw new Exception(urlName + "为空");
		
		if(count++ == 0)
			LogUtil.printInfoLog("MerInfoAlterSync", "syncMinfo", reqUrl, requestParamMap);
		
		return new String(Ryt.requestByPostwithURL(requestParamMap, reqUrl).getBytes(), "UTF-8");
	}
	
    /**
     * 发送报警邮件
     * @param mid
     * @param errorMsg
     * @return
     */
    private boolean sendWarnMail(String mid, String errorMsg){
    	String title = "商户信息同步报警-" + DateUtil.today();
    	String content = "商户号：" + mid;
    	content += "\n同步失败原因：\n\t" + errorMsg;
    	try {
	    	return EWPService.sendMail(content, title, null);
    	} catch(Exception e){
    		LogUtil.printErrorLog(getClass().getCanonicalName(), "sendWarnMail", content, e);
    	}
    	
    	return false;
    }
    
}
