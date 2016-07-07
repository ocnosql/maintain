<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<% 
	//应用上下文地址
	String appPath = request.getContextPath();
%>
<script type="text/javascript" charset="utf-8" >
	var appPath="<%=appPath%>";
</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="<%=appPath%>/css/lib/ext/ext-all.css" rel="stylesheet" type="text/css">
<link href="<%=appPath%>/css/lib/ext/xtheme-gray.css" rel="stylesheet" type="text/css">
<link href="<%=appPath%>/css/lib/ext/Spinner.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="<%=appPath%>/js/lib/ext/adapter/ext/ext-base-debug.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/lib/ext/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/lib/ext/TabCloseMenu.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/lib/ext_extend/DynamicGrid.js"></script>
<script type="text/javascript" src="<%=appPath%>/js/lib/ext_extend/andir.js"></script>

<script type="text/javascript" src="<%=appPath%>/js/common.js"></script>


<script>
Ext.Ajax.on('beforerequest', function(conn, options){
	if(options.params == undefined){
		options.params = {};
	}
	options.params.isAjaxRequest = "1";
}); 

Ext.Ajax.on('requestexception', function(conn, response, options ) {
	//alert('request exception');
	if(response.isTimeout && response.isTimeout == true){
		
	}
});

Ext.Ajax.on('requestcomplete', function(conn, response, options ) {
	var ret = response.responseText == null ? "" : response.responseText;
	try{
		result = eval('(' + ret + ')');
		if(result.isSessionTimeout && result.isSessionTimeout == 1){
			//options.params.isNeedCallback = false;
			options.callback = function(){};
			options.success = function(){};
			options.failure = function(){};
			//alert('会话失效，请重新登录！');
			if(window.parent != null){
				window.top.location.href = appPath + '/login.jsp';
			}else{
				window.location.href = appPath + '/login.jsp';
			}
			return;
		}

	}catch(e){
	}
	
	
});
</script>