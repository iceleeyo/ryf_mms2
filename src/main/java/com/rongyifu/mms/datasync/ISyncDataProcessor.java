package com.rongyifu.mms.datasync;

public interface ISyncDataProcessor {
	
	void process(int rowNum, String rowData);
}
