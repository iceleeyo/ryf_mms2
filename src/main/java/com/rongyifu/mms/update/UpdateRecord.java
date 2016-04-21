package com.rongyifu.mms.update;

import java.util.Date;

public class UpdateRecord {
	
	private Integer id;
	
	private String table;
	
	private String idColum;//table表中被更新字段的唯一标识
	
	private Object idColumValue;//该记录对应的idCulum的值
	
	private Integer status;
	
	private Date effectiveTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getIdColum() {
		return idColum;
	}

	public void setIdColum(String idColum) {
		this.idColum = idColum;
	}

	public Object getIdColumValue() {
		return idColumValue;
	}

	public void setIdColumValue(Object idColumValue) {
		this.idColumValue = idColumValue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
}
