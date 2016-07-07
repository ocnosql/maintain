<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ include file="/common/common.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<% 
	//应用上下文地址
	//String appPath = request.getContextPath();
	Object user = request.getSession().getAttribute("user");
	if(user == null){
		//response.sendRedirect(appPath + "/login.jsp");
		response.sendRedirect(appPath + "/common/redirect.jsp");
  	}
%>



