package com.rongyifu.mms.bean;

import java.util.List;


public class SettleResultBean {

	private boolean flag;// 对账成功与否的标志
	private String errMsg;// 对账失败的原因
	private int total;// 对账记录
	private List<SettleHlog> success;// 对账成功记录
	private List<SettleHlog> suspect;// 可疑交易记录
	private List<SettleHlog> fail;// 失败交易记录

	private int finish;// 已对账交易 记录
	private int exception;// 对账异常 记录

	public SettleResultBean() {
		super();
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.flag = false;
		this.errMsg = errMsg;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

	public int getException() {
		return exception;
	}

	public void setException(int exception) {
		this.exception = exception;
	}

	public List<SettleHlog> getSuccess() {
		return success;
	}

	public void setSuccess(List<SettleHlog> success) {
		this.success = success;
	}

	public List<SettleHlog> getSuspect() {
		return suspect;
	}

	public void setSuspect(List<SettleHlog> suspect) {
		this.suspect = suspect;
	}

	public List<SettleHlog> getFail() {
		return fail;
	}

	public void setFail(List<SettleHlog> fail) {
		this.fail = fail;
	}

}
