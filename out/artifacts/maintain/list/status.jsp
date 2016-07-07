<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ailk.model.redis.RedisConfigReader,com.ailk.model.redis.RedisConfig,java.util.*" %>
<%@ include file="/common/head.jsp"%>
   

<script>
var redisConfig = [];
<%
String loading = appPath + "/images/loading.gif";
Map redisConfig = RedisConfigReader.getConfigMap();
Iterator it = redisConfig.keySet().iterator();
while(it.hasNext()){
	String name = (String)it.next();
	RedisConfig cfg = (RedisConfig) redisConfig.get(name);
	out.println("redisConfig.push(['"+ name +  "','" + cfg.getHost() + "']);");
}
%>
Ext.onReady(function(){
	function queryRedisRunningStatus(){
		Ext.Ajax.request({
		    url: appPath + '/status!getRedisRunningStatuts.action',
		    method:'post',
		    params:{
		     //billId: Ext.getCmp('billId').getValue()
		   },
		   success:function(req){
			  var obj = Ext.util.JSON.decode(req.responseText);
		      if(obj.success){  
		    	  createRedisStauts(obj.data);
		      }else{ 
		    	  //Ext.Msg.alert('错误','查询失败');
		      }
		   }
		 });
	}
	
	function queryRedisLoadStatus(){
		Ext.Ajax.request({
		    url: appPath + '/status!getRedisLoadStatuts.action',
		    method:'post',
		    params:{
		     //billId: Ext.getCmp('billId').getValue()
		   },
		   success:function(req){
			  var obj = Ext.util.JSON.decode(req.responseText);
		      if(obj.success){  
		    	  createLoadStauts(obj.data.status);
		    	  createLoadDate(obj.data.lastModifiedDate);
		      }else{ 
		    	  //Ext.Msg.alert('错误',obj.message);
		      }
		   }
		 });
	}
	
	function queryRedisLoadCount(){
		Ext.Ajax.request({
		    url: appPath + '/status!getRedisLoadCount.action',
		    method:'post',
		    params:{
			     //billId: Ext.getCmp('billId').getValue()
			},
		    success:function(req){
			  var obj = Ext.util.JSON.decode(req.responseText);
		      if(obj.success){  
		    	  createRedisCount(obj.data);
		      }else{ 
		    	  Ext.Msg.alert('错误',obj.message);
		      }
		   }
		 });
	}
	
	function queryDataSourceInfo(){
		Ext.Ajax.request({
		    url: appPath + '/status!getDateSourceInfo.action',
		    method:'post',
		    params:{
			     //billId: Ext.getCmp('billId').getValue()
			},
		    success:function(req){
		    	//alert(req.responseText)
			  var obj = Ext.util.JSON.decode(req.responseText);
		      if(obj.success){  
		    	  createDataSourceInfo(obj.data);
		      }else{ 
		    	  Ext.Msg.alert('错误',obj.message);
		      }
		   }
		 });
	}
	
	function createRedisStauts(data){
		for(var i in data){
			document.getElementById('runningStatus_' + i).innerHTML = (data[i] == true ? '正常运行' : '已停止');
		}
	}
	
	function createLoadStauts(data){
		for(var i in data){
			document.getElementById('loadStatus_' + i).innerHTML = (data[i] == true ? '加载成功' : '加载失败');
		}
	}
	
	function createLoadDate(data){
		for(var i in data){
			document.getElementById('loadDate_' + i).innerHTML = data[i];
		}
	}
	
	function createRedisCount(data){
		var redisNames = [];
		for(var i in data.redisRecordCount){
			redisNames.push(i);
		}
		var html = ['<table  border="1" cellspacing="0" bordercolor="#000000" width = "90%" style="border-collapse:collapse;"><tr><td></td><td>数据库记录数</td>'];
		for(var i = 0; i < redisNames.length; i++){
			html.push("<td>" + redisNames[i] + "</td>");
		}
		html.push("</tr>");
		var dbRecordCount = data.dbRecordCount;

		for(var j in dbRecordCount){
			html.push("<tr>");
			html.push("<td>"+ j +"</td>");
			html.push("<td>"+dbRecordCount[j]+"</td>");
			for(var k = 0; k < redisNames.length; k++){
				html.push("<td>");
				var recordList = data.redisRecordCount[redisConfig[k][0]];
				for(var n = 0; n < recordList.length; n++){
					if(recordList[n].busiType == j){
						html.push(recordList[n].count != dbRecordCount[j] ? "<font style='color:red'>"+ recordList[n].count  +"</font>": recordList[n].count );
						break;
					}
				}
				html.push("</td>");
			}
			html.push("</tr>");
		}
		html.push("</table>");
		document.getElementById('redisCount').innerHTML = html.join("");
	}
	
	
	function createDataSourceInfo(data){
		var html = ['<table  border="1" cellspacing="0" bordercolor="#000000" width = "90%" style="border-collapse:collapse;">'];
		html.push('');
		html.push('');
		var flag = false;
		var topHead = '<tr><td></td>';
		var head = '<tr><td></td>';
		for(k = 0; k < data.length; k++){
			for(var i in data[k]){
				var hostName = i;
				var hostInfo = data[k][i];
				html.push("<tr><td>" + hostName + "</td>");
				if(hostInfo.success){
					var dataSourceInfos = hostInfo.data;
					for(var j in dataSourceInfos){
						var dataSourceName = j;
						var ds = dataSourceInfos[j];
						for(var n in ds){
							html.push("<td>" + ds[n].value + "</td>");
							/*if(flag == false){
								head += "<td>" + ds[n].name + "</td>";
							}*/
						}
						/*if(flag == false){
							topHead += '<td colspan="4">';
							if(j == 'db'){
								topHead += '数据库连接池';
							}else if(j == 'ocnosql'){
								topHead += 'ocnosql连接池';
							}else{
								topHead += j;
							}
							topHead += '</td>';
						}*/
					}
					flag = true;
				}else{
					html.push("<td colspan='10'>" + hostInfo.message + "</td>");
					//html.push("<td colspan='10'></td>");
				}
				html.push("</tr>");
			}
		}
		//topHead += '</tr>';
		//head += "</tr>";
		topHead = '<tr><td></td><td colspan="5">数据库连接数</td><td colspan="5">ocnosql连接数</td></tr>';
		head = '<tr><td></td><td>最大连接数</td><td>最大空闲连接数</td><td>最小空闲连接数</td><td>当前连接数</td><td>当前空闲连接数</td><td>最大连接数</td><td>最大空闲连接数</td><td>最小空闲连接数</td><td>当前连接数</td><td>当前空闲连接数</td></tr>';
		html[1] = topHead;
		html[2] = head;
		html.push("</table>");
		document.getElementById('dataSourceInfo').innerHTML = html.join("");
	}
	
	function checkStatus(){
		try{
			queryRedisRunningStatus();
		}catch(e){
			
		}
		try{
			queryRedisLoadStatus();
		}catch(e){
			
		}
	}
	checkStatus();
	queryRedisLoadCount();
	queryDataSourceInfo();
	
	//window.setInterval(checkStatus, 10000); 
});
</script>
<style>
<!--
body,td{
	font-size:12px;
	padding: 3px
}
-->
</style>
<html>
<body>
<div id='redisStatus'>
<font style="font-weight:bold;">Redis运行状态：</font>
<%
it = redisConfig.keySet().iterator();
StringBuffer sb = new StringBuffer("<table  border='1' cellspacing='0' bordercolor='#000000' width = '90%' style='border-collapse:collapse;'><tr><td></td>");
while(it.hasNext()){
	String name = (String)it.next();
	RedisConfig cfg = (RedisConfig) redisConfig.get(name);
	sb.append("<td>").append(name + " - " + cfg.getHost()).append("</td>");
}
sb.append("</tr>");

it = redisConfig.keySet().iterator();
sb.append("<tr><td>运行状态</td>");
while(it.hasNext()){
	String name = (String)it.next();
	sb.append("<td><div id='runningStatus_" + name + "'><img src='"+ loading +"'/></div></td>");
}
sb.append("</tr>");

it = redisConfig.keySet().iterator();
sb.append("<tr><td>局数据是否成功导入</td>");
while(it.hasNext()){
	String name = (String)it.next();
	RedisConfig cfg = (RedisConfig) redisConfig.get(name);
	sb.append("<td><div id='loadStatus_" +  cfg.getHost() + "'><img src='"+ loading +"'/></div></td>");
}
sb.append("</tr>");


it = redisConfig.keySet().iterator();
sb.append("<tr><td>最后导入时间</td>");
while(it.hasNext()){
	String name = (String)it.next();
	RedisConfig cfg = (RedisConfig) redisConfig.get(name);
	sb.append("<td><div id='loadDate_" +  cfg.getHost() + "'><img src='"+ loading +"'/></div></td>");
}
sb.append("</tr>");

sb.append("</table>");
out.println(sb.toString());
%>
</div>
<div><br></div>
<font style="font-weight:bold;">连接池信息：</font>
<div id='dataSourceInfo'>
数据源信息加载中<img src="<%=loading %>"/>
</div>
<br>
<font style="font-weight:bold;">局数据加载信息：</font>
<div id='redisCount'>
局数据条数加载中<img src="<%=loading %>"/>
</div>
</body>
</html>