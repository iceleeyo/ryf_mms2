package com.rongyifu.mms.datasync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileData {

	private int dataSum;                 // 其他系统提供的数据总行数
	private int dataRows;                // RYF统计的数据总行数
	private boolean isSuccess = true;    // 处理结果标识：true 处理成功；false 处理失败
	private List<IDataSync> datas = new ArrayList<IDataSync>();             // 正确的数据
	private List<String> errorDatas = new ArrayList<String>();              // 错误的数据
	private Map<String, Object> merInfo = new HashMap<String, Object>();    // 商户信息
	
	public int getDataSum() {
		return dataSum;
	}

	public void setDataSum(int dataSum) {
		this.dataSum = dataSum;
	}

	public List<IDataSync> getDatas() {
		return datas;
	}

	public void setDatas(List<IDataSync> datas) {
		this.datas = datas;
	}

	public List<String> getErrorDatas() {
		return errorDatas;
	}

	public void setErrorDatas(List<String> errorDatas) {
		this.errorDatas = errorDatas;
	}

	public int getDataRows() {
		return dataRows;
	}

	public void setDataRows(int dataRows) {
		this.dataRows = dataRows;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Map<String, Object> getMerInfo() {
		return merInfo;
	}

	public void setMerInfo(Map<String, Object> merInfo) {
		this.merInfo = merInfo;
	}

}
