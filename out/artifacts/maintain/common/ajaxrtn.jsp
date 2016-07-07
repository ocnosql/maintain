<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	if(request.getAttribute("ajaxStr") != null){
		out.write((String)request.getAttribute("ajaxStr"));
		//System.out.println(request.getAttribute("ajaxStr"));
	}
	
%>