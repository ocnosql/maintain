package com.ailk.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.dao.BaseDao;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.ailk.core.base.action.BaseAction;
import com.google.gson.Gson;

public class AuthUserAction extends BaseAction {
    public static final Log LOG = LogFactory.getLog(AuthUserAction.class);
    private static final long serialVersionUID = 1L;
	private static String AJAXRTN = "ajax_rtn";
	private String ajaxStr;
	private String username;
	private String password;
	private boolean success;
	private String message;
	
	public String query(){
		return AJAXRTN;
	}

	public String execute() throws Exception {
		username = this.getUsername();
		password = this.getPassword();
        BaseDao dao = new BaseDao("ocnosql");
        try {
            List<Map> list = dao.query("select name, passwd from user where name='" + username + "' and passwd='" + password + "' and is_enable='1'");

            //		if ("admin".equals(username) && "admin".equals(password)) {
            if (list.size() > 0) {
                ServletActionContext.getRequest().getSession().setAttribute("user", true);
                this.success = true;
                //			this.message = "用户名:" + this.getUsername() + "密码是:" + this.getPassword();
                Map map = new HashMap();
                map.put("success", true);
                Gson gs = new Gson();
                this.setAjaxStr(gs.toJson(map));
                return AJAXRTN;
            } else {
                this.success = false;
                this.message = "对不起，未授权的用户不能登录改系统";
                Map map = new HashMap();
                map.put("success", false);
                Gson gs = new Gson();
                this.setAjaxStr(gs.toJson(map));
                return AJAXRTN;
            }
        } catch (Throwable e) {
            LOG.error("", e);
            throw new Exception(e);
        }
	}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
