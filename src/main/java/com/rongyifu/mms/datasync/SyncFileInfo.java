package com.rongyifu.mms.datasync;

public class SyncFileInfo {

	/**
	 * 业务类型
	 */
	private String servicetype;
	/**
	 * 文件路径
	 */
	private String filePath;
	/**
	 * 文件后缀
	 */
	private String fileSuffix;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 报警时间
	 */
	private String alarmTime;
	/**
	 * 文件名长度
	 */
	private int fileNameLen;

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public int getFileNameLen() {
		return fileNameLen;
	}

	public void setFileNameLen(int fileNameLen) {
		this.fileNameLen = fileNameLen;
	}
}
