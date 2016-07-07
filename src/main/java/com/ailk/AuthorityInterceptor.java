package com.ailk;

import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthorityInterceptor extends AbstractInterceptor{

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        Map session = ctx.getSession();
        Boolean user = (Boolean) session.get("user");
        if(user!=null && user == true){
            return invocation.invoke();
        }
        //没有登陆，将服务器提示设置成一个HttpServletRequest属性
        ctx.put("tip","您还没有登录，请登陆系统");
        ServletActionContext.getRequest().setAttribute("ajaxStr", "{\"success\": false, \"isSessionTimeout\": 1}");
        ServletActionContext.getRequest().setAttribute("isAjaxRequest", ServletActionContext.getRequest().getParameter("isAjaxRequest"));
        
		return "redirect";
	}
	
}
