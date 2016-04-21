package com.rongyifu.mms.bean;

import java.util.HashMap;
import java.util.Map;

import com.rongyifu.mms.utils.DateUtil;

public class SettleMinfo {
	
	private Integer id;
	private String mid;// 商户号
	private String name;// 商户简称
	private int liqType;// 结算方式
	private int liqPeriod;// 结算周期
	private int liqLimit;// 结算满足的额度
	private String lastBatch; // 最后批次号 bigint(20)
	private int lastLiqDate;// 最后结算日期 int(11)
	private boolean flag;// 可以发起结算的标志
	private String msg;// 不能发起结算的原因
	private int mstate;// 状态
	private int expDate;// 截至日期
	private Integer liqDate;// 发起日期
	private Integer liqTime;// 发起时间
	private String resule;// 发起结算的结果
	private boolean perioded;// 结算周期内
	private Integer reason;//失败原因码
	private Integer category;//商户类型码
	private String liqTime1;

	public int getMstate() {
		return mstate;
	}

	public String getResule() {
		return resule;
	}

	public void setResule(String resule) {
		this.resule = resule==null ? "" : resule;
	}

	public boolean isPerioded() {
		return perioded;
	}

	public void setPerioded(boolean perioded) {
		this.perioded = perioded;
	}

	public void setMstate(int mstate) {
		this.mstate = mstate;
	}

	public int getExpDate() {
		return expDate;
	}
	
	public void setExpDate(int expDate) {
		this.expDate = expDate;
	}

	public SettleMinfo() {
		super();
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLiqType() {
		return liqType;
	}

	public void setLiqType(int liqType) {
		this.liqType = liqType;
	}

	public int getLiqPeriod() {
		return liqPeriod;
	}

	public void setLiqPeriod(int liqPeriod) {
		this.liqPeriod = liqPeriod;
	}

	public int getLiqLimit() {
		return liqLimit;
	}

	public void setLiqLimit(int liqLimit) {
		this.liqLimit = liqLimit;
	}

	public String getLastBatch() {
		return lastBatch;
	}

	public void setLastBatch(String lastBatch) {
		this.lastBatch = lastBatch;
	}

	public int getLastLiqDate() {
		return lastLiqDate;
	}

	public void setLastLiqDate(int lastLiqDate) {
		this.lastLiqDate = lastLiqDate;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg==null ? "" : msg;
	}

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLiqDate() {
		return liqDate;
	}

	public void setLiqDate(Integer liqDate) {
		this.liqDate = liqDate;
	}

	public String getLiqTime1() {
		if(null != liqTime){
			liqTime1 = DateUtil.getStringTime(liqTime);
		}
		return liqTime1;
	}

	public Integer getLiqTime() {
		return liqTime;
	}

	public void setLiqTime(Integer liqTime) {
		this.liqTime = liqTime;
	}

	public void setLiqTime1(String liqTime1) {
		this.liqTime1 = liqTime1;
	}
	
	public Map<String, String> getKvMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", String.valueOf(id));
		map.put("mid", mid);
		map.put("name", name);
		map.put("liqType", String.valueOf(liqType));
		map.put("liqPeriod", String.valueOf(liqPeriod));
		map.put("liqLimit", String.valueOf(liqLimit));
		map.put("lastBatch", lastBatch);
		map.put("lastLiqDate", String.valueOf(lastLiqDate));
		map.put("flag", String.valueOf(flag));
		map.put("msg", msg);
		map.put("mstate", String.valueOf(mstate));
		map.put("expDate", String.valueOf(expDate));
		map.put("liqDate", String.valueOf(liqDate));
		map.put("liqTime", String.valueOf(liqTime));
		map.put("resule", resule);
		map.put("perioded", String.valueOf(perioded));
		map.put("reason", String.valueOf(reason));
		map.put("category", String.valueOf(category));
		map.put("liqTime1", liqTime1);
		return map;
	}
}
