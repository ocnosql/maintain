package com.ailk.core.base.action;

import com.ailk.model.ValueSet;
import com.opensymphony.xwork2.ActionSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class BaseAction extends ActionSupport {

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

    public void bindParams(ValueSet vs, HttpServletRequest request){
        Enumeration e = request.getParameterNames();
        while(e.hasMoreElements()){
            String name = (String) e.nextElement();
            vs.put(name, request.getParameter(name));
        }
    }
	
	
}
