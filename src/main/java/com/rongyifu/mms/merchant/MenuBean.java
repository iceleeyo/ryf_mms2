package com.rongyifu.mms.merchant;

import java.util.ArrayList;
import java.util.List;

public class MenuBean {

	private Integer id;// 菜单id authIndex
	private String code;
	private String name;// 菜单名字
	private Integer pid;// 菜单父ID
	
	private Integer level;// 菜单等级

	private List<MenuBean> children;// 子菜单集合

	private String t;// 商户（M）,管理员菜单（A）

	private Boolean open=true;//树菜单默认展开
	
	private Boolean checked;//树菜单是否选择（即是否分配权限）
	
	private Boolean isChanged;//审核时需要用到 是否修改了该菜单权限
	
	public MenuBean() {
		super();
	}
	// 1级菜单  使用这个构造方法
	public MenuBean(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.level=1;
	}
	//2级菜单  使用这个构造方法
	public MenuBean(Integer pid, Integer id,String name) {
		super();
		this.pid = pid;
		this.id = id;
		this.name = name;
		this.level = 2;
		//this.code = code;
		//this.t = t;
	}
	//3级菜单  使用这个构造方法
	public MenuBean(Integer pid, Integer id, String t, String code, String name) {
		super();
		this.pid = pid;
		this.id = id;
		this.name = name;
		this.code = code;
		this.t = t;
		this.level = 3;
	}
	
	public void addChild(MenuBean bean) {
		List<MenuBean> childs = this.getChildren();
		if (childs == null) setChildren(new ArrayList<MenuBean>());
		this.getChildren().add(bean);
	}

	public String getJsp() {
		if(this.t==null) return null;
		if(this.t.equals("M")){
			return "mer/jsp/"+this.t + "_" + this.id + "_" + this.code + ".jsp";
		}else{
			return "admin/jsp/"+this.t + "_" + this.id + "_" + this.code + ".jsp";
		}
		
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}


	public List<MenuBean> getChildren() {
		return children;
	}
	public void setChildren(List<MenuBean> children) {
		this.children = children;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Boolean getIsChanged() {
		return isChanged;
	}
	public void setIsChanged(Boolean isChanged) {
		this.isChanged = isChanged;
	}
	
}
