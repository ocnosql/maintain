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
String restList = PropertiesUtil.getProperty("runtime.properties", "drquery.service.list");
if(StringUtils.isNotEmpty(restList)){
	String[] restArr = restList.split(",");
	for(String rest : restArr){
	    String[] addArr = PropertiesUtil.getProperty("runtime.properties", rest).split(",");
	    for(String add : addArr) {
	        out.println("restUri.push(['"+ rest + " - "+ add +"','"+ add +"']);");
	    }

	}
}
%>
var tables = [];
tables.push(['--None--','']);
<%
String list = PropertiesUtil.getProperty("runtime.properties", "zd.tables.prefix");
System.out.println("---" + list);
if(StringUtils.isNotEmpty(list)){
	
	String[] tableArr = list.split(",");
	for(String table : tableArr){
		out.println("tables.push(['"+ table + "','"+ table +"']);");
	}
}
%>
Ext.onReady(function(){
var pageSize = 3000;
var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: 'redis!query.action?type=drquery_zd&t='+new Date(),  
    width : '100%',  
    height: 500,  
    rowNumberer: true,  
    //checkboxSelModel: true,  
    sm: new Ext.grid.CheckboxSelectionModel(),  
    
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
			items:[
				{
					layout:'column',labelSeparator:':',
					title: '<g3:i18n codeId="查询条件"/>',
					xtype: 'fieldset',
					layout:'column',
					//collapsible: true,
					autoHeight: true,
					defaults:{layout: 'form',border:false,columnWidth:.33},
					items:[
						{items: [{xtype:'textfield',fieldLabel: 'rowkey',id: 'rowkey', name:'rowkey',anchor:'90%', value:'1300032787',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									if(event.keyCode == 13){
										 var a =searchPanel.getForm().getValues(); 
							               var params = dynamicGrid.store.baseParams; //baseParams每次HTTP请求都会携带的参数，就保存在这个对象身上
							               Ext.apply(params,a); 
							               dynamicGrid.store.baseParams = params; 
							               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize},
								               callback: function(r, options, success){
								               		alert(dynamicGrid.store.reader.jsonData)
								                }})
									}
								}
							}
							}]},
							{columnWidth:.33,items: [{
					            xtype: 'combo',
					            fieldLabel: 'tablePrefix',
					            hiddenName: 'tablePrefixName',
					            forceSelection : true,
					            editable: false,
					            anchor:'93.5%',
					            store: new Ext.data.SimpleStore({
					                fields: ['text','value'],
					                data: tables
					            }),
					            valueField: 'value',
					            displayField: 'text',
					            typeAhead: true,
					            mode: 'local',
					            triggerAction: 'all',
					            selectOnFocus: true,
					            allowBlank: false,
					            listeners: {  
					                afterRender: function(combo) {  
										var firstValue = combo.getStore().reader.arrayData[0][1];  
										combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示  
					                }    
					            } 
					        }]},
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: 'tableName',name: 'tableName',value:'201406',anchor:'90%',
								listeners : {
									keypress : function(obj, e){
										if(event.keyCode == 13){
											 var a =searchPanel.getForm().getValues(); 
								               var params = dynamicGrid.store.baseParams; 
								               Ext.apply(params,a); 
								               dynamicGrid.store.baseParams = params; 
								               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} })
										}
									}
								}}]},
						     								
						        {columnWidth:1.03,items: [{
						            xtype: 'combo',
						            fieldLabel: 'rest',
						            hiddenName: 'restName',
						            forceSelection : true,
						            editable: false,
						            anchor:'93.5%',
						            store: new Ext.data.SimpleStore({
						                fields: ['text','value'],
						                data: restUri
						            }),
						            valueField: 'value',
						            displayField: 'text',
						            typeAhead: true,
						            mode: 'local',
						            triggerAction: 'all',
						            selectOnFocus: true,
						            allowBlank: false,
						        }]},
						        
						        {columnWidth:1.06,items: [{xtype:'textfield',fieldLabel: 'opId', name:'opId',anchor:'90%', value:'10190705',
									enableKeyEvents:true
									
								}]},
						        
						        {columnWidth:1.06,items: [{xtype:'textfield',fieldLabel: 'orgId', name:'orgId',anchor:'90%', value:'',
									enableKeyEvents:true
									
								}]},
						        
						        {columnWidth:1.06,items: [{xtype:'textfield',fieldLabel: 'where条件', name:'cond',anchor:'90%', value:'',
									enableKeyEvents:true
									
								}]}
								
					]//items
				}
			],
			buttons: [{
				text:'查 询',
				cls: 'x-icon-btn',
				handler: function(){
				   var a =searchPanel.getForm().getValues(); 
	               var params = dynamicGrid.store.baseParams; 
	               Ext.apply(params,a); 
	               dynamicGrid.store.baseParams = params; 
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
				}
			},{
				text: '复 位',
				cls: 'x-icon-btn',
				handler: function(){
					searchPanel.getForm().reset();
					var vals =searchPanel.getForm().getValues(); 
					dynamicGrid.store.baseParams = {};
					var params = dynamicGrid.store.baseParams; 
		            Ext.apply(params, vals); 
		            dynamicGrid.store.baseParams = params; 
					//dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
				}
			},
			{
				text:'全选',
				cls: 'x-icon-btn',
				handler: function(){
					dynamicGrid.getSelectionModel().selectAll();
				}
			},{
				text:'复制',
				cls: 'x-icon-btn',
				handler: function(){
					Utils.copySelectedRows(dynamicGrid, 'OCNOSQL');
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
		],
		Ext.MyViewport.superclass.initComponent.call(this);
	}
});

var viewport = new Ext.MyViewport();
}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>