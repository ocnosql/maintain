<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>


Ext.onReady(function(){
var pageSize = 200;
var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: 'redis!query.action?type=ocdcMonitor&t='+new Date(),  
    width : '100%',  
    height: 500,  
    //rowNumberer: true,  
    //checkboxSelModel: true,  
    sm: new Ext.grid.CheckboxSelectionModel(),  
    
    bbar : new Ext.PagingToolbar({  
    	//plugins: new Ext.ux.Andrie.pPageSize(), 
        pageSize : pageSize,  
        displayInfo : true,  
        displayMsg : '显示第{0}到{1}条数据,共{2}条',  
        emptyMsg : "没有数据",  
        beforePageText : "第",  
        afterPageText : '页 共{0}页,每页显示' + pageSize + '条'  
    })  
});

dynamicGrid.on("rowdblclick", function(/*Grid*/ grid, /*Number*/ rowIndex, /*Ext.EventObject*/ e ){
	//alert(grid.getStore().getAt(rowIndex).get("TASK_TYPE"));
	var hasDetail = grid.getStore().getAt(rowIndex).get("HAS_DETAIL");
	var title = grid.getStore().getAt(rowIndex).get("TASK_TYPE") + '节点明细';
	var collId = grid.getStore().getAt(rowIndex).get("COLL_ID");
	var batchId = grid.getStore().getAt(rowIndex).get("BATCH_ID");
	if(hasDetail != '0'){
		var item = {text: title, murl: 'list/ocdcMonitorDetail.jsp?collId=' + collId + '&batchId=' + batchId};
		parent.onItemCheck(item, true);
	}
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
						{items: [{
						            xtype: 'combo',
						            fieldLabel: '节点',
						            hiddenName: 'nodeName',
						            forceSelection : true,
						            editable: false,
						            anchor:'90%',
						            store: new Ext.data.SimpleStore({
						                fields: ['text','value'],
						                data: [['全部',''],['平行转换节点','TRANS'], ['加载节点','LOAD']]
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
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: '开始日期',name: 'startDate',value:'20121201',anchor:'90%',
								listeners : {
									
								}}]},
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: '结束日期',name: 'endDate',value:'20130131',anchor:'90%',
							listeners : {
								
							}}]},
							{items: [{
					            xtype: 'combo',
					            fieldLabel: '状态',
					            hiddenName: 'status',
					            forceSelection : true,
					            editable: false,
					            anchor:'90%',
					            store: new Ext.data.SimpleStore({
					                fields: ['text','value'],
					                data: [['全部',''],['成功','SUCCESS'], ['失败','FAIL']]
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
				   var a = searchPanel.getForm().getValues(); 
	               var params = dynamicGrid.store.baseParams; 
	               Ext.apply(params,a); 
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