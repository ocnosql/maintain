<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.ailk.util.PropertiesUtil,org.apache.commons.lang.StringUtils" %>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
var restUri = [];
<%
String restList = PropertiesUtil.getProperty("runtime.properties", "drquery.rest.list");
if(StringUtils.isNotEmpty(restList)){
	String[] restArr = restList.split(",");
	for(String rest : restArr){
		out.println("restUri.push(['"+ rest + " - "+PropertiesUtil.getProperty("runtime.properties", rest) +"','"+ rest +"']);");
	}
}
%>
Ext.onReady(function(){
var pageSize = 3000;
var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '详单列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: 'redis!query.action?type=drquery&m=getUrls&t='+new Date(),  
    width : '100%',  
    height: 700,  
    rowNumberer: true,  
    //checkboxSelModel: true,  
    sm: new Ext.grid.CheckboxSelectionModel({singleSelect:false}),  
    
    bbar : new Ext.PagingToolbar({  
    	//plugins: new Ext.ux.Andrie.pPageSize(), 
        pageSize : pageSize,  
        displayInfo : true,  
        displayMsg : '显示第{0}到{1}条数据,共{2}条',  
        emptyMsg : "没有数据",  
        beforePageText : "第",  
        afterPageText : '页 共{0}页, 每页显示'+ pageSize +'条'  
    })  
});


var searchPanel = new Ext.FormPanel(
		{
			labelAlign:'left',
			buttonAlign:'right',
			bodyStyle:'padding:5px;',
			frame:true,
			labelWidth:65,
			monitorValid:true,
			buttons: [{
				text:'查 询',
				cls: 'x-icon-btn',
				handler: function(){
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize}, 
	               callback: function(record, options, success){
	            	   
	               }}); 
				}
			},{
				text:'全选',
				cls: 'x-icon-btn',
				handler: function(){
					dynamicGrid.getSelectionModel().selectAll();
				}
			},{
				text:'刷新',
				cls: 'x-icon-btn',
				handler: function(){
					var records = dynamicGrid.getSelectionModel().getSelections();
					if(records.length == 0){
						Ext.Msg.alert('提示', '请选择至少一条记录！');
						return;
					}
					var urls = [];
					for(var i = 0; i < records.length; i++){
						urls.push(records[i].get('实例列表'));
					}
					Ext.Ajax.request({
					    url: appPath + '/redis!refreshCache.action?&t='+new Date(), 
					    method:'post',
					    params:{
					     urls: urls.join(',')
					   },
					   success:function(req){
						  var obj = Ext.util.JSON.decode(req.responseText);
					      if(obj.success){  
					    	  var data = obj.data;
					    	  //var n = dynamicGrid.getStore().getTotalCount();
					    	  for(var i = 0; i < records.length; i++){
					    		  var record = records[i];
					    		  for(var j = 0; j < data.length; j++){
					    			  if(data[j].url.indexOf(record.get('实例列表')) != -1 ){
					    				  if(data[j].success == true){
					    					  record.set('状态', '成功');
					    				  }else{
					    					  record.set('状态', '失败');
					    				  }
					    				  
					    			  }
					    		  }
					    	  }
					    	  
					      }else{ 
					    	  Ext.Msg.alert('错误','刷新失败，' + obj.message);
					      }
					   }
					 });
				}
			}]
		}
);//FormPanel

Ext.MyViewport=Ext.extend(Ext.Viewport ,{
	xtype:"viewport",
	layout:"anchor",
	autoScroll: true,
	initComponent: function(){
		this.items=[
		    searchPanel,
		    dynamicGrid
		];
		Ext.MyViewport.superclass.initComponent.call(this);
	}
});

var viewport = new Ext.MyViewport();
dynamicGrid.getStore().load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize}});
}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>