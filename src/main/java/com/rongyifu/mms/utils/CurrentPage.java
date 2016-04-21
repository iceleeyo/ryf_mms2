package com.rongyifu.mms.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentPage<E> implements Serializable {

	/**
	 * 远程调用需要序列化
	 */
	private static final long serialVersionUID = 867440080945083927L;
	private long pageAmtSum;
	private long sysAmtFeeSum;

	private int pageNumber;
	private int pagesAvailable;
	private int pageSize;
	private int pageTotle;
	private List<E> pageItems = new ArrayList<E>();

	private Map<String,Object> sumResult = new HashMap<String,Object>();
 	
	
	public long getSysAmtFeeSum() {
		return sysAmtFeeSum;
	}

	public void setSysAmtFeeSum(long sysAmtFeeSum) {
		this.sysAmtFeeSum = sysAmtFeeSum;
	}
	public long getPageAmtSum() {
		return pageAmtSum;
	}

	public void setPageAmtSum(long pageAmtSum) {
		this.pageAmtSum = pageAmtSum;
	}

	public int getPageTotle() {
		return pageTotle;
	}

	public void setPageTotle(int pageTotle) {
		this.pageTotle = pageTotle;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setPagesAvailable(int pagesAvailable) {
		this.pagesAvailable = pagesAvailable;
	}

	public void setPageItems(List<E> pageItems) {
		this.pageItems = pageItems;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPagesAvailable() {
		return pagesAvailable;
	}

	public List<E> getPageItems() {
		return pageItems;
	}

	public void setSumResult(String key, Object o) {
		if(o==null) return;
		sumResult.put(key, o);
	}
	
	public long getSumLongResult(String key) {
		try {
			return Long.parseLong(key);
		} catch (Exception e) {
			System.err.println(key);
			e.printStackTrace();
			return 0l;
		}
	}
	
	public int getSumIntResult(String key) {
		try {
			return Integer.parseInt(key);
		} catch (Exception e) {
			System.err.println(key);
			e.printStackTrace();
			return 0;
		}
	}


	public Map<String, Object> getSumResult() {
		return sumResult;
	}
/*
	public void setSumResult(Map<String, Object> sumResult) {
		this.sumResult = sumResult;
	}
	*/
}
