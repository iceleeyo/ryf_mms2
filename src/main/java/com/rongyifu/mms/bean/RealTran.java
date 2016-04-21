package com.rongyifu.mms.bean;

import java.util.List;

public class RealTran {
	
	
	private String warMsg;
	
	private boolean isWar;

	private int totle;
	private int succesTranCount;
	private List<Hlog> lastSuccesTranList;
	private List<Hlog> failTranList;
	boolean hasNewFailTran;

	private long lastFailTran;



	
	public String getWarMsg() {
		return warMsg;
	}

	public void setWarMsg(String warMsg) {
		this.warMsg = warMsg;
	}

	public boolean isWar() {
		return isWar;
	}

	public void setWar(boolean isWar) {
		this.isWar = isWar;
	}


	public RealTran() {
		super();
	}


	public long getLastFailTran() {
		return lastFailTran;
	}

	public void setLastFailTran(long lastFailTran) {
		this.lastFailTran = lastFailTran;
	}

	public List<Hlog> getLastSuccesTranList() {
		return lastSuccesTranList;
	}

	public void setLastSuccesTranList(List<Hlog> lastSuccesTranList) {
		this.lastSuccesTranList = lastSuccesTranList;
	}

	public boolean isHasNewFailTran() {
		return hasNewFailTran;
	}

	public void setHasNewFailTran(boolean hasNewFailTran) {
		this.hasNewFailTran = hasNewFailTran;
	}

	public List<Hlog> getSuccesTranList() {
		return lastSuccesTranList;
	}

	public void setSuccesTranList(List<Hlog> lastSuccesTranList) {
		this.lastSuccesTranList = lastSuccesTranList;
	}

	public int getTotle() {
		return totle;
	}

	public void setTotle(int totle) {
		this.totle = totle;
	}

	public int getSuccesTranCount() {
		return succesTranCount;
	}

	public void setSuccesTranCount(int succesTranCount) {
		this.succesTranCount = succesTranCount;
	}

	public List<Hlog> getFailTranList() {
		return failTranList;
	}

	public void setFailTranList(List<Hlog> failTranList) {
		this.failTranList = failTranList;
	}

}
