package com.rongyifu.mms.bean;

import java.beans.PropertyDescriptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.rongyifu.mms.modules.sysmanage.service.MonitorConfigService.MonitorTypes;


/**
* @ClassName: MonitorConfig
* @Description: 监控配置
* @author li.zhenxing
* @date 2014-9-22 下午2:07:47
*/ 
public class MonitorConfig {
	
	private Integer id;
	private Integer type;
	private String targetId;
	private String targetName;
	private Integer successN;
	private Integer successM;
	private Integer busyIntervalS;
	private Integer idleIntervalS;
	private Integer judgerIndexS;
	private Integer failN;
	private Integer failM;
	private Integer busyIntervalF;
	private Integer idleIntervalF;
	private Integer judgerIndexF;
	private Integer continualFailN;
	private Integer continualFailM;
	private Integer busyIntervalC;
	private Integer idleIntervalC;
	private Integer judgerIndexC;
	private Integer gateId;
	//private Integer waitN;
	//private Integer waitM;
	//private Integer judgerIndexW;
	// Integer sumFailN;
	//private Integer sumFailM;
	//private Integer judgerIndexSf;
	
	public Integer getGateId() {
		return gateId;
	}

	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}

	public void validate() throws Exception{
		BeanWrapper bw = new BeanWrapperImpl(this);
		PropertyDescriptor[] pds = bw.getPropertyDescriptors();
		for (int i = 0; i < pds.length; i++) {
			String pname = pds[i].getName();
			Object value = bw.getPropertyValue(pname);
			if("id".equals(pname)||"targetName".equals(pname)){
				continue;
			}else if("targetId".equals(pname)){
				if(value == null || StringUtils.isBlank(value.toString())){
					throw new Exception("配置不合法");
				}
			}else if("type".equals(pname) || pname.startsWith("judger")){
				if(!"0".equals(value.toString())&&!"1".equals(value.toString())){
					throw new Exception("配置不合法");
				}
			}else{
				if(null == value){
					throw new Exception("配置不合法");
					
				}else if(Integer.class.equals(pds[i].getPropertyType())){
					int intVal = ((Integer)value).intValue();
					if(intVal<=0){
						if(!"gateId".equals(pname))
						throw new Exception("配置不合法");
					}
				}
			}
		}
		if(judgerIndexS == 0 && successM <= successN){
			throw new Exception("当成功订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		}else if(judgerIndexS == 1 && (successM >= successN)){
			throw new Exception("当成功订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		}
		if(judgerIndexF==0 && (failM<=failN)){
			throw new Exception("当失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		}else if(judgerIndexF == 1 && (failM >= failN)){
			throw new Exception("当失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		}
		if(judgerIndexC==0 && (continualFailM<=continualFailN)){
			throw new Exception("当连续失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		}else if(judgerIndexC == 1 && (continualFailM >= continualFailN)){
			throw new Exception("当连续失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		}
//		if(judgerIndexW==0 && (waitM<=waitN)){
//			throw new Exception("当待支付订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
//		}else if(judgerIndexW == 1 && (waitM >= waitN)){
//			throw new Exception("当待支付订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
//		}
//		if(judgerIndexSf==0 && (sumFailM<=sumFailN)){
//			throw new Exception("当累计失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
//		}else if(judgerIndexSf == 1 && (sumFailM >= sumFailN)){
//			throw new Exception("当累计失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
//		}
	}
	
	public int getBusyInterval(MonitorTypes mt){
		int interval = 0;
		switch(mt){
			case SUCCESS:{
				interval = getBusyIntervalS();
			};
			break;
			case FAIL:{
				interval = getBusyIntervalF();
			};
			break;
			case CONTINUAL_FAIL:{
				interval = getBusyIntervalC();
			};
			break;
		}
		return interval;
	}
	
	public int getIdleInterval(MonitorTypes mt){
		int interval = 0;
		switch(mt){
			case SUCCESS:{
				interval = getIdleIntervalS();
			};
			break;
			case FAIL:{
				interval = getIdleIntervalF();
			};
			break;
			case CONTINUAL_FAIL:{
				interval = getIdleIntervalC();
			};
			break;
		}
		return interval;
	}
	
	public int getJudgerIndex(MonitorTypes mt) {
		int index = 0;
		switch(mt){
			case SUCCESS:{
				index = getJudgerIndexS();
			};
			break;
			case FAIL:{
				index = getJudgerIndexF();
			};
			break;
			case CONTINUAL_FAIL:{
				index = getJudgerIndexC();
			};
			break;
			case WAIT:{//使用默认
			};
			break;
			case TOTAL_FAIL:{//使用默认
			};
			break;
		}
		return index;
	}
	
	public int getM(MonitorTypes mt){
		int m = -1;
		switch(mt){
			case SUCCESS:{
				m = getSuccessM();
			};
			break;
			case FAIL:{
				m = getFailM();
			};
			break;
			case CONTINUAL_FAIL:{
				m = getContinualFailM();
			};
			break;
//			case WAIT:{
//				m = getWaitM();
//			};
//			break;
//			case TOTAL_FAIL:{
//				m = getSumFailM();
//			};
//			break;
		}
		return m;
	}
	
	public int getN(MonitorTypes mt){
		int  n = -1;
		switch(mt){
			case SUCCESS:{
				n = getSuccessN();
			};
			break;
			case FAIL:{
				n = getFailN();
			};
			break;
			case CONTINUAL_FAIL:{
				n = getContinualFailN();
			};
			break;
//			case WAIT:{
//				n = getWaitN();
//			};
//			break;
//			case TOTAL_FAIL:{
//				n = getSumFailN();
//			};
		}
		return n;
	}
	
	public Integer getBusyIntervalS() {
		return busyIntervalS;
	}
	public void setBusyIntervalS(Integer busyIntervalS) {
		this.busyIntervalS = busyIntervalS;
	}
	public Integer getIdleIntervalS() {
		return idleIntervalS;
	}
	public void setIdleIntervalS(Integer idleIntervalS) {
		this.idleIntervalS = idleIntervalS;
	}
	public Integer getJudgerIndexS() {
		return judgerIndexS;
	}
	public void setJudgerIndexS(Integer judgerIndexS) {
		this.judgerIndexS = judgerIndexS;
	}
	public Integer getBusyIntervalF() {
		return busyIntervalF;
	}
	public void setBusyIntervalF(Integer busyIntervalF) {
		this.busyIntervalF = busyIntervalF;
	}
	public Integer getIdleIntervalF() {
		return idleIntervalF;
	}
	public void setIdleIntervalF(Integer idleIntervalF) {
		this.idleIntervalF = idleIntervalF;
	}
	public Integer getJudgerIndexF() {
		return judgerIndexF;
	}
	public void setJudgerIndexF(Integer judgerIndexF) {
		this.judgerIndexF = judgerIndexF;
	}
	public Integer getBusyIntervalC() {
		return busyIntervalC;
	}
	public void setBusyIntervalC(Integer busyIntervalC) {
		this.busyIntervalC = busyIntervalC;
	}
	public Integer getIdleIntervalC() {
		return idleIntervalC;
	}
	public void setIdleIntervalC(Integer idleIntervalC) {
		this.idleIntervalC = idleIntervalC;
	}
	public Integer getJudgerIndexC() {
		return judgerIndexC;
	}
	public void setJudgerIndexC(Integer judgerIndexC) {
		this.judgerIndexC = judgerIndexC;
	}
//	public Integer getWaitN() {
//		return waitN;
//	}
//	public void setWaitN(Integer waitN) {
//		this.waitN = waitN;
//	}
//	public Integer getWaitM() {
//		return waitM;
//	}
//	public void setWaitM(Integer waitM) {
//		this.waitM = waitM;
//	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	public Integer getSuccessN() {
		return successN==null?0:successN;
	}
	public void setSuccessN(Integer successN) {
		this.successN = successN;
	}
	public Integer getSuccessM() {
		return successM==null?0:successM;
	}
	public void setSuccessM(Integer successM) {
		this.successM = successM;
	}
	public Integer getFailN() {
		return failN==null?0:failN;
	}
	public void setFailN(Integer failN) {
		this.failN = failN;
	}
	public Integer getFailM() {
		return failM==null?0:failM;
	}
	public void setFailM(Integer failM) {
		this.failM = failM;
	}
	public Integer getContinualFailN() {
		return continualFailN==null?0:continualFailN;
	}
	public void setContinualFailN(Integer continualFailN) {
		this.continualFailN = continualFailN;
	}
	public Integer getContinualFailM() {
		return continualFailM==null?0:continualFailM;
	}
	public void setContinualFailM(Integer continualFailM) {
		this.continualFailM = continualFailM;
	}
//	public Integer getSumFailN() {
//		return sumFailN==null?0:sumFailN;
//	}
//	public void setSumFailN(Integer sumFailN) {
//		this.sumFailN = sumFailN;
//	}
//	public Integer getSumFailM() {
//		return sumFailM==null?0:sumFailM;
//	}
//	public void setSumFailM(Integer sumFailM) {
//		this.sumFailM = sumFailM;
//	}
	public String getTargetName() {
		return targetName==null?"":targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

//	public Integer getJudgerIndexW() {
//		return judgerIndexW;
//	}
//
//	public void setJudgerIndexW(Integer judgerIndexW) {
//		this.judgerIndexW = judgerIndexW;
//	}
//
//	public Integer getJudgerIndexSf() {
//		return judgerIndexSf;
//	}
//
//	public void setJudgerIndexSf(Integer judgerIndexSf) {
//		this.judgerIndexSf = judgerIndexSf;
//	}
}
