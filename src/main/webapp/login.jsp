<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/common.jsp"%>
<% 
	//应用上下文地址
	Object user = request.getSession().getAttribute("user");
	if(user != null){
		response.sendRedirect(appPath + "/index.jsp");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>用户登录</title>
<script>
	//开始
	Ext.onReady(function fn() {
		var myLoginForm = new Ext.FormPanel(
				{ //定义一个FormPanel对象
// 					labelWidth: 70,
					width : 330,
// 					height: 140,
					frame : true, //设置窗体为圆角
					renderTo : "loginForm", //设置渲染的Div
					title : "登录窗口",
					method : "POST",
					//draggable: true,
					//以下两个为Submit的主要方法
					onSubmit : function(){
						alert();
					},
					submit : function() {

						this.getEl().dom.action = "#";
						this.getEl().dom.submit()
						//Ext.MessageBox.alert("Form submit","Submit SUCCESS!!!");
					},
					items : [ //设置FormPanel的子对象
					{
						fieldLabel : "用户名", //标签内容
						xtype : "textfield", //对象的类型,默认为 textfield
						allowBlank : false, //是否允许为空,默认为 true
						blankText : "用户名不能为空", //显示错误提示信息
 						value: 'admin',
						name : "username",
						id : "username"
					}, {
						fieldLabel : "密码",
						xtype : "textfield",
						allowBlank : false,
						blankText : "密码不能为空",
						inputType : "password", //输入类型为 password
 						value: 'admin',
						name : "password",
						id : "password",
						enableKeyEvents: true,
						listeners : {
							keypress : function(obj, e){
								if(event.keyCode == 13){
									submitForm();
								}
							}
						}
					} ],
					buttons:[
							{
							text : "登录",
							handler : function() {
								submitForm();
							}
							
						}]
				});
		//以下为显示验证提示的主要设置
		Ext.QuickTips.init();
		Ext.form.Field.prototype.msgTarget = 'side';
		
		function submitForm(){
			var username=Ext.getCmp('username').getValue();
			var password=Ext.getCmp('password').getValue();
			Ext.Ajax.request({
                url: appPath + '/login.action',
                params: {username: username, password: password },
                method: 'POST',
                success: function (response, options) {
                	var msg = eval("("+response.responseText+")");
                	
					if(msg.success){
						window.location.href=appPath + '/index.jsp';
					}else{
						Ext.Msg.alert("提示","登录失败");
					}
                },
                failure: function (response, options) {
                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
                }
            });
		}
	});
</script>
</head>
<body>
	<!-- 设置对应的Div -->
	<div id="loginForm" style="margin: 60px auto; width: 500px;"></div>
</body>
</html>