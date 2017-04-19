<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String isAjaxRequest = (String) request.getAttribute("isAjaxRequest");

if("1".equals(isAjaxRequest)){
	if(request.getAttribute("ajaxStr") != null){
		out.write((String)request.getAttribute("ajaxStr"));
		//System.out.println(request.getAttribute("ajaxStr"));
	}
}else{%>
	<script>
		/**
		 if(window.parent != null){
			window.top.location.href = '<%=request.getContextPath()%>/login.jsp';
		}else{
			window.location.href='<%=request.getContextPath()%>/login.jsp';
		}
		 **/
		var errorMsg = "会话失效，请重新登录！";
		var isSessionTimeout = '1';
		if(window.parent != null){
			window.top.location.href = '<%=request.getContextPath()%>/login.jsp?isSessionTimeout='+isSessionTimeout;
		}else{
			window.location.href = '<%=request.getContextPath()%>/login.jsp?isSessionTimeout='+isSessionTimeout;
		}
	</script>
<%}%>