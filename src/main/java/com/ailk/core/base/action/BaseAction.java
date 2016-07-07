package com.ailk.core.base.action;

import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport{

	protected static String AJAXRTN = "ajax_rtn";
	protected String ajaxStr;
	protected String start;
	protected String limit;
	public static String getAJAXRTN() {
		return AJAXRTN;
	}
	public static void setAJAXRTN(String aJAXRTN) {
		AJAXRTN = aJAXRTN;
	}
	public String getAjaxStr() {
		return ajaxStr;
	}
	public void setAjaxStr(String ajaxStr) {
		this.ajaxStr = ajaxStr;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	
}
