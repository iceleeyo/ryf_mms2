package com.rongyifu.mms.modules.sysmanage.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.CollectionUtils;

import com.rongyifu.mms.bean.MonitorConfig;
import com.rongyifu.mms.bean.MonitorData;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.sysmanage.dao.MonitorConfigDao;
import com.rongyifu.mms.modules.sysmanage.dao.MonitorDataDao;
import com.rongyifu.mms.modules.transaction.dao.QueryMerTodayDao;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.LogUtil;

/**
* @ClassName: MonitorConfigService
* @Description: 监控配置service
* @author li.zhenxing
* @date 2014-9-22 下午2:56:51
*/ 
public class MonitorConfigService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8651496307814856265L;
	/**
	 * 正常
	 */
	private static final int NOMAL = 0;
	/**
	 * 警告
	 */
	private static final int WARNING = 1;
	/**
	 * 紧急
	 */
	private static final int DANGEROUS = 2;
	/**
	 * 未知
	 */
	private static final int UNKNOWN = 3;
	
	private static SimpleDateFormat date = new SimpleDateFormat();
	public static final Map<Integer,String> transModeMap;
	private static final Judge[] JUDGERS;
	
	
	private QueryMerTodayDao tlogDao = new QueryMerTodayDao();
	private MonitorConfigDao mCfgDao = new MonitorConfigDao();
	private MonitorDataDao mDataDao = new MonitorDataDao();
	
	
	static{
		Map<Integer,String> map = new HashMap<Integer,String>();
		map.put(1, "个人网银支付");
		map.put(2, "消费充值卡支付");
		map.put(3, "信用卡支付");
		map.put(5, "增值业务");
		map.put(6, "语音支付");
		map.put(7, "企业网银支付");
		map.put(8, "手机WAP支付");
		map.put(10, "POS支付");
		map.put(11, "对私代付");
		map.put(12, "对公代付");
		map.put(13, "B2B充值");
		map.put(15, "对私代扣");
		map.put(16, "对公提现");
		map.put(17, "对私提现");
		map.put(18, "快捷支付");
		transModeMap = Collections.unmodifiableMap(map);
		
		JUDGERS = new Judge[2];
		
		JUDGERS[0] =  new Judge(){ //超过报警值报警
			@Override
			public int judge(int currentValue, int warnValue, int dangerValue) throws Exception {
				int status = UNKNOWN;
				if(currentValue < warnValue){
					status = NOMAL;
				}else if(currentValue < dangerValue && currentValue >= warnValue){
					status = WARNING;
				}else if(currentValue >= dangerValue){
					status = DANGEROUS;
				}
				return status;
			}
		};
		
		JUDGERS[1] = new Judge(){//未达报警值报警
			@Override
			public int judge(int currentValue, int warnValue, int dangerValue) throws Exception {
				int status = UNKNOWN;
				if(currentValue <= dangerValue){
					status = DANGEROUS;
				}else if(currentValue > dangerValue && currentValue <= warnValue){
					status = WARNING;
				}else if(currentValue > warnValue){
					status = NOMAL;
				}
				return status;
			}
		};
	}
	
	/**
	* @title: 监控接口入口
	*/ 
	public String doMonitor(Map<String,String> params){
		String outStr = "ERROR";
		try {
			String type = params.get("type");//监控类型
			String targetId = params.get("targetId");//监控对象id
			String monitorType = params.get("monitorType");//监控类型
			String gateId = params.get("gate");//监控类型
			if(StringUtils.isBlank(type) || StringUtils.isBlank(targetId) || StringUtils.isBlank(monitorType)){
				outStr = "ERROR,参数错误!";
			}else{
				outStr = genMonitorData(type, targetId, monitorType,gateId);
			}
		} catch (Exception e) {
			LogUtil.printErrorLog(getClass().getName(), "doPost", e.getMessage(), params);
			outStr+=","+e.getMessage();
		}
		return outStr;
	}

	/**
	* @title: 生成监控数据
	*/ 
	private String genMonitorData(String type,String targetId,String monitorTypeStr,String gateId) throws Exception {
		String msg = "";
		String today = today();
		MonitorTypes mt = getMonitorTypes(monitorTypeStr);
		
		if("0".equals(type)||"1".equals(type)){
				//查询监控配置
			MonitorConfig mCfg=null ;
			if("0".equals(type)){
				mCfg = mCfgDao.queryByTypeAndTarget(type, targetId,gateId);
			}
			else{
				 gateId="0";
				mCfg = mCfgDao.queryByTypeAndTarget(type, targetId,gateId);
			}	
				if(null != mCfg){
					//查询监控数据
					String sql = getSql(type, targetId,gateId ,mt, mCfg, today);
					int count;
					if (MonitorTypes.CONTINUAL_FAIL == mt) {//连续失败
						//拿到所有成功或失败的订单 未包含初始状态和待支付状态的订单
						List<Integer> list = tlogDao.queryForList(sql, Integer.class);
						//统计指定时间段内最大连续失败次数
						count = countMaxContinualFail(list);
					}else{
						count = tlogDao.queryForInt(sql);
					}
					Map<String, Object> map = mDataDao.queryMaxAndMin(mCfg.getId() + "", mt.getCode(), today);
					int max = Integer.valueOf(map.get("MAX").toString());
					int min = Integer.valueOf(map.get("MIN").toString());
					//组装monitorData
					MonitorData md = new MonitorData();
					md.setMax(Integer.valueOf(max));
					md.setMin(Integer.valueOf(min));
					md.setConfigId(mCfg.getId());
					md.setMonitorType(mt.getCode());
					md.setSysDate(Integer.valueOf(today));
					md.setSysTime(DateUtil.getCurrentUTCSeconds());
					md.setX(count);
					//生成报文
					msg = generateMsg(mCfg,md,mt,type);//此方法会调用md参数的setStatus()方法
					//持久化
					mDataDao.add(md);
				}else{
					throw new Exception("监控配置不存在!");
				}
			}
		return msg;
	}
	
	//	状态码|描述信息| fail_count=当前值(X)；警告值(N)；紧急值(M)；最小值(Min)；最大值(Max) ??最小值(Min);最大值(Max)是什么
	//	0 |描述信息1| fail_count= X;N;M;Min;Max
	//		----	状态码说明		----
	//	0： 0 <= 当前值(X) < 警告值(N)
	//	1：警告值(N) <= 当前值(X) < 紧急值(M)
	//	2：当前值(X) >= 紧急值(M)
	//	3：以上情况都不是
	//	--------	描述信息	-----------
	//	描述信息1：网关为80001的工商银行(M)在Y分钟内交易成功X笔
	//	描述信息2：网关为80001的工商银行(M)在Y分钟内交易失败X笔
	//	描述信息3：网关为80001的工商银行(M)在Y分钟内交易连续失败X笔
	//	描述信息4：网关为80001的工商银行(M)在4014-09-01 15:30:46时间订单总数X笔
	//	描述信息5：网关为80001的工商银行(M)在4014-09-01 15:30:46失败订单总数X笔
	private String generateMsg(MonitorConfig mc,MonitorData md,MonitorTypes mt,String type) throws Exception{
		String temp = "";
		String targetName=mc.getTargetName();
		String time="";
		String gateName="";
		if("0".equals(type)){//监控交易类型
			temp = mt.getGatePattern();
			targetName = transModeMap.get(Integer.valueOf(mc.getTargetId()));
			//根据交易银行id获取交易银行名字
			 gateName = mDataDao.getRYtGateById(mc.getGateId());
		}else{//监控商户
			temp = mt.getMerPattern();
		}
		if(mt.getCode() == 3||mt.getCode() == 4){
			time = dateTime();
		}else{
			time = getInterval(mc,mt)+"分钟";
		}
		String info = MessageFormat.format(temp, new Object[]{targetName,time,md.getX(),gateName+""});
		
		Integer m = mc.getM(mt);//紧急值
		Integer n = mc.getN(mt);//警告值
		
		int judgerIndex = mc.getJudgerIndex(mt);
		
		int status = JUDGERS[judgerIndex].judge(md.getX(), n, m);
		
		md.setStatus(status);//设置监控状态
		
		StringBuffer bfr = new StringBuffer().append(status).append("|").append(info);
		bfr.append("|fail_count=").append(md.getX()).append(";").append(n).append(";").append(m).append(";");
		bfr.append(md.getMin()).append(";").append(md.getMax());
		return bfr.toString();
	}
	
	private MonitorTypes getMonitorTypes(String monitorTypeStr) throws Exception{
		MonitorTypes mt = null;
		if(StringUtils.isBlank(monitorTypeStr)){
			throw new Exception("错误的监控类型, monitorType = "+monitorTypeStr);
		}else{
			try {
				int monitorType = Integer.valueOf(monitorTypeStr);
				mt = MonitorTypes.getByCode(monitorType);
				if(null == mt){
					throw new Exception("错误的监控类型, monitorType = "+monitorTypeStr);
				}
			} catch (Exception e) {
				throw new Exception("错误的监控类型, monitorType = "+monitorTypeStr);
			}
		}
		return mt;
	}
	
	private int getStartSeconds(MonitorConfig mCfg,MonitorTypes mt){
		//获取监控时间
		int interval = getInterval(mCfg,mt);
		//获取时间单位
		int nowSeconds = DateUtil.getCurrentUTCSeconds();
		int startTime = nowSeconds - interval * 60;//监控时间单位为分钟
		if (startTime < 0)//只监控当天的,当计算出来的起始时间<0 则从0开始 
			startTime = 0;
		return startTime;
	}
	
	private int getInterval(MonitorConfig mCfg,MonitorTypes mt){
		int interval = 0;
		if(isBusy()){
			interval = mCfg.getBusyInterval(mt);
		}else{
			interval = mCfg.getIdleInterval(mt);
		}
		return interval;
	}
	
	/**
	 * 统计给定的按时间排序的订单中最大连续失败的订单数
	 */ 
	private int countMaxContinualFail(List<Integer> list){
		int max = 0;
		if(CollectionUtils.isEmpty(list)){
			return max;
		}
		//循环统计连续失败
		int count = 0;//连续失败订单数
		int index = 0;//已经判断的hlog个数
		int size = list.size();
		for (Integer tstat : list) {
			index ++;
			if(tstat == (short)2){
				//遇到成功的订单时 判断累计连续失败的的订单数是否超过了之前记录的最大累计失败数max 如果大于 更新max
				if(max < count){
					max = count;
					if(max >= size - index){//已经统计出来的最大失败订单数超过剩下没判断的订单数 则没有必要继续判断
						break;
					}
				}
				count = 0;//重置count
				continue;
			}
			count++;
		}
		//最后需要在此判断count是否大于max 因为可能存在最后超过max个连续失败订单
		if(max < count){
			max = count;
		}
		return max;
	}
	
	/**
	* 判断闲时忙时 0-6点为闲时
	*/ 
	private static boolean isBusy(){
		boolean flag = true;
		Calendar cld = Calendar.getInstance();
		int hour = cld.get(Calendar.HOUR_OF_DAY);//获取当前时间24小时制的小时数
		if(hour>=0 && hour<6){
			flag = false;
		}
		return flag;
	}
	
	private interface Judge{
		int judge(int currentValue,int warnValue,int dangerValue) throws Exception;
	}
	
	/**
	 *	成功订单	在Y分钟时间段内，订单状态为“交易成功”的订单
			例如：
			“0 |描述信息1| fail_count= X;N;M;Min;Max”
		失败订单	在Y分钟时间段内，订单状态连续为“交易失败”的订单
			例如：
			“0 |描述信息2| fail_count= X;N;M;Min;Max”
		连续失败	在Y分钟时间段内，订单状态为“交易失败”的订单
			例如：
			“0 |描述信息3| fail_count= X;N;M;Min;Max” 
		总订单数量	在当前时间，订单总量
			例如：
			“0 |描述信息4| fail_count= X;N;M;Min;Max”
		失败订单数量	在当前时间，订单状态为“交易失败”的订单数量 
			例如：
			“0 |描述信息5| fail_count= X;N;M;Min;Max”
	 *
	 */
	public enum MonitorTypes{
		/**
		 * 成功订单
		 */
		SUCCESS(0,"成功订单","交易类型{0},交易银行{3},在{1}内交易成功{2}笔","商户{0}在{1}内交易成功{2}笔"),
		
		/**
		 * 失败订单
		 */
		FAIL(1,"失败订单","交易类型{0},交易银行{3},在{1}内交易失败{2}笔","商户{0}在{1}内交易失败{2}笔"),
		
		/**
		 * 连续失败
		 */
		CONTINUAL_FAIL(2,"连续失败订单","交易类型{0},交易银行{3},在{1}内交易连续失败{2}笔","商户{0}在{1}内交易连续失败{2}笔"),
		
		/**
		 * 当天待支付订单总数
		 */
		WAIT(3,"待支付订单","交易类型{0}截至{1}共有待支付订单{2}笔","商户{0}截至{1}共有待支付订单{2}笔"),
		
		/**
		 * 当天累计失败订单数量
		 */
		TOTAL_FAIL(4,"累计失败订单","交易类型{0}截至{1}共有失败订单{2}笔","商户{0}截至{1}共有失败订单{2}笔");
		
		MonitorTypes(int code,String desc,String gatePattern,String merPattern){
			this.code = code;
			this.desc = desc;
			this.gatePattern = gatePattern;
			this.merPattern = merPattern;
		}
		
		private int code;
		private String desc;
		private String gatePattern;
		private String merPattern;
		
		public int getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
		public String getGatePattern() {
			return gatePattern;
		}
		public String getMerPattern() {
			return merPattern;
		}
		public static MonitorTypes getByCode(int code){
			MonitorTypes[] mts = MonitorTypes.values();
			for (MonitorTypes mt : mts) {
				if(mt.getCode() ==  code){
					return mt;
				}
			}
			return null;
		}
	}
	
	private String today(){
		synchronized (date) {
			date.applyPattern("yyyyMMdd");
			return date.format(new Date());
		}
	}
	
	private String dateTime(){
		synchronized (date) {
			date.applyPattern("yyyy-MM-dd HH:mm:ss");
			return date.format(new Date());
		}
	}
	
	/**
	* @return
	*/ 
	private String getSql(String targetType,String targetId,String gateId, MonitorTypes mt,MonitorConfig mCfg,String today){
		StringBuilder sbr = new StringBuilder();
		if(MonitorTypes.CONTINUAL_FAIL == mt ){
			sbr.append("SELECT t.tstat FROM tlog t");
		}else{
			sbr.append("SELECT COUNT(*) FROM tlog t");
		}
		sbr.append(" WHERE 1 = 1");
		if(!"0".equals(gateId)){
			sbr.append(" AND t.gate=").append(gateId);
		}
		
		if(MonitorTypes.SUCCESS == mt){//成功
			sbr.append(" AND t.tstat = 2");
		}else if(MonitorTypes.FAIL == mt || MonitorTypes.TOTAL_FAIL == mt){
			sbr.append(" AND (t.tstat = 3 or t.tstat = 4)");//失败包含交易失败（3）和请求银行失败（4）两种情况
		}else if(MonitorTypes.CONTINUAL_FAIL == mt){//判断连续失败 只查询成功和失败的订单 
			sbr.append(" AND (t.tstat = 2 or t.tstat = 3 or t.tstat = 4)");//失败包含交易失败（3）和请求银行失败（4）两种情况
		}else if(MonitorTypes.WAIT == mt){//待支付
			sbr.append(" AND t.tstat = 1");
		}
		sbr.append(" AND t.sys_date=").append(today);
		if (!(MonitorTypes.WAIT == mt||MonitorTypes.TOTAL_FAIL == mt)) {//查询当天累计不需要sys_time条件
			int startTime = getStartSeconds(mCfg,mt);
			if(0 != startTime){
				sbr.append(" AND t.sys_time>=").append(startTime);
			}
		}
		if("0".equals(targetType)){//交易类型
			sbr.append(" AND t.type=").append(targetId);
		}else{
			sbr.append(" AND t.mid =").append(Ryt.addQuotes(targetId));
		}
		if (MonitorTypes.CONTINUAL_FAIL == mt) {
			sbr.append(" order by t.sys_date DESC,t.sys_time DESC");
		}
		return sbr.toString();
	}

	public String addConfig(MonitorConfig mc){
		String msg = "添加失败";
		try {
			mc.validate();
			int count = mCfgDao.addConfig(mc);
			if(count == 1){
				msg = "添加成功";
			}
		} catch (DuplicateKeyException e) {
			if (mc.getType()==0) {
				msg += ",该交易方式的监控配置已存在";
			}else if(mc.getType()==1){
				msg += ",该商户的监控配置已存在";
			}
		}catch (Exception e) {
			msg += ", " + e.getMessage();
		}
		return msg;
	}
	
	/**
	* @Title: queryConfigPage
	* @Description: 分页查询配置
	* @author li.zhenxing
	* @param @param pageNo
	* @return CurrentPage<MonitorConfig>    返回类型
	*/ 
	public CurrentPage<MonitorConfig> queryConfigPage(Integer pageNo){
		Map<String,String> params = new HashMap<String,String>();
		LogUtil.printInfoLog(this.getClass().getName(), "queryConfigPage", "SELECT mc.*,m.abbrev targetName FROM monitor_config mc left join minfo m on mc.target_id=m.id", params);
		CurrentPage<MonitorConfig> page = mCfgDao.queryForCurrPage("SELECT mc.*,m.abbrev targetName FROM monitor_config mc left join minfo m on mc.target_id=m.id", pageNo, MonitorConfig.class);
		return page;
	}
	
	/**
	* @Title: modifyConfig
	* @Description: 更新配置
	* @author li.zhenxing
	* @param @param mc
	* @return boolean    返回类型
	*/ 
	public String modifyConfig(MonitorConfig mc){
		String msg = "更新失败";
		try {
			mc.validate();
			int count = mCfgDao.update(mc);
			if (count == 1) {
				msg = "更新成功";
			}
		} catch (Exception e) {
			msg += ", "+e.getMessage();
			LogUtil.printErrorLog(getClass().getSimpleName(), "modifyConfig", e.getMessage(), e);
		}
		return msg;
	}
	
	public MonitorConfig getConfigById(Integer id){
		try {
			return mCfgDao.getConfigById(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public String delConfig(Integer id){
		try {
			return mCfgDao.delConfig(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	/**
	* @Title: isMerNoExists
	* @Description: 检查商户号是否存在
	* @author li.zhenxing
	* @param @param merNo
	* @return boolean    返回类型
	*/ 
	public boolean isMerNoExists(String merNo){
		int count = mCfgDao.isMerNoExists(merNo);
		if(count == 1){
			return true;
		}else{
			return false;
		}
	}
}
