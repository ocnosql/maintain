<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/common.jsp"%>
<% 
	//应用上下文地址
	//Object user = request.getSession().getAttribute("user");
	//if(user != null){
		//response.sendRedirect(appPath + "/index.jsp");
	//}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>4A验证页面</title>
<script>

	//解析URL中参数
	function GetRequest() {
		var url = location.search; //获取url中"?"符后的字串
		var theRequest = new Object();
		if (url.indexOf("?") != -1) {
			var str = url.substr(1);
			var strs = str.split("&");
			for(var i = 0; i < strs.length; i ++) {
				theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
			}
		}
		return theRequest;
	}

	var Request = new Object();
	Request = GetRequest();
	var token = Request['token'];
	var appAcctId = Request['appAcctId'];
	var isSessionTimeout =  Request['isSessionTimeout'];

	//debugger;
	Ext.onReady(function(){
		if(isSessionTimeout==undefined||isSessionTimeout==''){

			//token = "tokenParam";
			//appAcctId = "appAcctIdParam";

			//进行验证  调用Login4AAction  并根据返回进行跳转
			Ext.Ajax.request({
				url: appPath +  '/login4A.action',
				method:'post',
				params:{
					token: token,
					appAcctId:appAcctId
				},
				success:function(req){
					var obj = Ext.util.JSON.decode(req.responseText);
					if(obj.success){
						Ext.Msg.alert('成功','鉴权成功');
						//进行跳转
						window.location.href=appPath + '/index.jsp';
					}else{
						Ext.Msg.alert('错误','鉴权失败');
						//window.close();
					}
				},
				failure: function (response, options) {
					Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
				}
			});

		}else{
			Ext.Msg.alert('提示','会话失效,请重新登陆');
			//Ext.MessageBox.alert('提示', '会话失效,请重新登陆');
			//alert('会话失效,请重新登陆');
		}
	});

</script>
</head>
<body>
	<!-- 设置对应的Div
	<div id="loginForm" style="margin: 60px auto; width: 500px;"></div>
	-->
</body>
</html>