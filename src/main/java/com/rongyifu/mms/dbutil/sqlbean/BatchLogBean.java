package com.rongyifu.mms.dbutil.sqlbean;

import com.rongyifu.mms.dbutil.ISqlBean;

public class BatchLogBean implements  ISqlBean{
	private Integer id;
	private String mid;
	private Integer sys_date;
	private String batch_id;
 
	private Integer order_num;
	private Integer process_num;
	private Integer notify_mer;
	private Integer type;
	private Integer batch_state;
	
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "batch_log";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getSys_date() {
		return sys_date;
	}
	public void setSys_date(Integer sys_date) {
		this.sys_date = sys_date;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public Integer getOrder_num() {
		return order_num;
	}
	public void setOrder_num(Integer order_num) {
		this.order_num = order_num;
	}
	public Integer getProcess_num() {
		return process_num;
	}
	public void setProcess_num(Integer process_num) {
		this.process_num = process_num;
	}
	public Integer getNotify_mer() {
		return notify_mer;
	}
	public void setNotify_mer(Integer notify_mer) {
		this.notify_mer = notify_mer;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getBatch_state() {
		return batch_state;
	}
	public void setBatch_state(Integer batch_state) {
		this.batch_state = batch_state;
	}
	
}
