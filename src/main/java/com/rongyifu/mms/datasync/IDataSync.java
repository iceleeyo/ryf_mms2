package com.rongyifu.mms.datasync;

import java.util.List;

public interface IDataSync {
	
	/**
	 * 获取同步数据拼成的sql
	 * @param flowNo		文件处理批次号
	 * @param serviceType	业务类型
	 * @return
	 */
	List<String> getSql(String flowNo, String serviceType);
}
