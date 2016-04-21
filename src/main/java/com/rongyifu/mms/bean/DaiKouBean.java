package com.rongyifu.mms.bean;

import java.util.List;

public class DaiKouBean {
	
	private String batchNo;//批次号
	private List<DaiKou> sobjs;// 数据正确元素
	private List<DaiKou> fobjs;// 数据错误元素
	private boolean flag=false;//判断是否有失败数据
	private int sum_lines;
	private  double sum_amt; //交易金额
	private  String errMsg=null; //判断文件格式是否正确
	
	public double getSum_amt() {
		return sum_amt;
	}

	public void setSum_amt(double sum_amt) {
		this.sum_amt = sum_amt;
	}


	public int getSum_lines() {
		return sum_lines;
	}

	public void setSum_lines(int sum_lines) {
		this.sum_lines = sum_lines;
	}

	public List<DaiKou> getSobjs() {
		return sobjs;
	}

	public void setSobjs(List<DaiKou> sobjs) {
		this.sobjs = sobjs;
	}

	public List<DaiKou> getFobjs() {
		return fobjs;
	}

	public void setFobjs(List<DaiKou> fobjs) {
		this.fobjs = fobjs;
	}


	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
 
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}
