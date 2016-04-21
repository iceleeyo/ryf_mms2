package com.rongyifu.mms.bean;
import com.rongyifu.mms.common.*;
import com.rongyifu.mms.dao.SystemDao;

@SuppressWarnings("serial")
public class CardAuth implements java.io.Serializable {
	private Long id;
	private String field;
	private Short fieldType;
	private Integer date;
	private Short authType;
    SystemDao sysDao=new SystemDao();
	public Short getAuthType() {
		return authType;
	}
	public void setAuthType(Short authType) {
		this.authType = authType;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public String getField() {
        LoginUser user=sysDao.getLoginUser();
		return Ryt.getProperty(user,field);
	}
	public void setField(String field) {
		this.field = field;
	}
	public Short getFieldType() {
		return fieldType;
	}
	public void setFieldType(Short fieldType) {
		this.fieldType = fieldType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    public String getField(int ex){
        return Ryt.desDec(field);
    }
}
