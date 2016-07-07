<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.ailk.model.redis.RedisConfigReader,com.ailk.model.redis.RedisConfig,java.util.*" %>    
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
var pageSize = 200;
Ext.onReady(function(){
var redisConfig = [];
<%
Map redisConfig = RedisConfigReader.getConfigMap();
Iterator it = redisConfig.keySet().iterator();
while(it.hasNext()){
	String name = (String)it.next();
	RedisConfig cfg = (RedisConfig) redisConfig.get(name);
	out.println("redisConfig.push(['"+ name + " - " + cfg.getHost() +":"+ cfg.getPort() + "','" + name + "']);");
}
%>
var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: 'redis!query.action?type=redis&t='+new Date(),  
    width : '100%',  
    height: 500,  
    //rowNumberer: true,  
    //checkboxSelModel: true,  
    sm: new Ext.grid.CheckboxSelectionModel(),  
    
    bbar : new Ext.PagingToolbar({  
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
						{items: [{xtype:'textfield',fieldLabel: 'key',name:'key',anchor:'90%',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									if(event.keyCode == 13){
										 var a =searchPanel.getForm().getValues(); 
							               var params = dynamicGrid.store.baseParams; 
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
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: 'hashkey',name: 'hashkey',anchor:'90%',
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
						{items: [{
				            xtype: 'combo',
				            //id: 'redisName',
				            fieldLabel: 'Redis IP',
				            hiddenName: 'redisName',
				            hiddenValue: 'redis1',
				            forceSelection : true,
				            editable: false,
				            anchor:'90%',
				            store: new Ext.data.SimpleStore({
				                fields: ['text','value'],
				                //data: [['Redis1 - 20.26.12.15','Redis1'],['Redis2 - 20.26.12.16','Redis2']]
				            	data: redisConfig
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
				        }]}
					]//items
				}
			],
			buttons: [{
				text:'查 询',
				cls: 'x-icon-btn',
				handler: function(){
				   var a =searchPanel.getForm().getValues(); //得到表单内容的值 
	               var params = dynamicGrid.store.baseParams; 
	               Ext.apply(params,a);     //把a复制到params里  
	               dynamicGrid.store.baseParams = params;   //把params装到store的“参数盒子”里
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
				}
			},{
				text: '复 位',
				cls: 'x-icon-btn',
				handler: function(){
					searchPanel.getForm().reset();
					var combo = Ext.getCmp('redisName');
					combo.setValue(combo.getStore().reader.arrayData[0][1]);
					var vals = searchPanel.getForm().getValues(); 
					dynamicGrid.store.baseParams = {};
					var params = dynamicGrid.store.baseParams; 
		            Ext.apply(params, vals); 
		            dynamicGrid.store.baseParams = params; 
					dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
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