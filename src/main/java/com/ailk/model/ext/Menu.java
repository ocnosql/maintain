package com.ailk.model.ext;

import java.util.ArrayList;
import java.util.List;

public class Menu {

	private Integer id;
	private String text;
	private String cls = "folder";
	private String murl;
	//private boolean checked;
	private boolean leaf;
	private List<Menu> children = new ArrayList<Menu>();
	private Integer pid;
	
	public void addChild(Menu menu){
		this.children.add(menu);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	
	public boolean isLeaf() {
		//leaf = children.size()==0 ? true : false;
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	
	public String getMurl() {
		return murl;
	}

	public void setMurl(String murl) {
		this.murl = murl;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}
	
	
}

