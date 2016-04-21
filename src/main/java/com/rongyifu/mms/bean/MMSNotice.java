package com.rongyifu.mms.bean;

public class MMSNotice {
	private Integer id;

	private Integer beginDate;

	private Integer endDate;

	private String title;

	private String content;

	private Integer operId;
	
	
	private String mid ;
	
	private Integer expiration_date=0; //默认0
	
	private Integer noticeType=0; // 0,1,2      默认0
	
	
	

	public Integer getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Integer noticeType) {
		this.noticeType = noticeType;
	}

	public Integer getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(Integer expiration_date) {
		this.expiration_date = expiration_date;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Integer getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Integer beginDate) {
		this.beginDate = beginDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getEndDate() {
		return endDate;
	}

	public void setEndDate(Integer endDate) {
		this.endDate = endDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getOperId() {
		return operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
	}
}
