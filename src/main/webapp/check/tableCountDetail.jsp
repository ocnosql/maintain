<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var pageSize = 15;

/* Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '../../examples/ux'); */

var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: appPath + "/check/result.jsp?queryType=tableCountDetail",  
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
					title: '稽核条件',
					xtype: 'fieldset',
					layout:'column',
					//collapsible: true,
					autoHeight: true,
					defaults:{layout: 'form',border:false,columnWidth:.5},
					items:[
						{items: [{xtype:'textfield', disabled: true, fieldLabel: '开始时间',id: 'billId', name:'billId',anchor:'90%', value:'20151201',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						{items: [{xtype:'textfield', disabled: true,enableKeyEvents:true,fieldLabel: '结束时间',name: 'startDate',value:'20151202',anchor:'90%',
								listeners : {
									keypress : function(obj, e){
										
									}
								}}]},
/* 								{items: [{
						            xtype: 'combo',
						            //id: 'hashType',
						            fieldLabel: '范围',
						            hiddenName: 'hashType',
						            forceSelection : true,
						            editable: false,
						            anchor:'90%',
						            store: new Ext.data.SimpleStore({
						                fields: ['text','value'],
						                data: [['所有业务类型','all'],['指定业务类型','some']]
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
						        }]}, */
						        
								{items: [{xtype:'textarea', disabled: true, fieldLabel: '过滤条件',id: 'usernumbers', name:'usernumbers',anchor:'90%', value:'CALL_TYPE =\'1\' AND BILL_MONTH=\'201512\'',
									enableKeyEvents:true,
									listeners : {
										keypress : function(obj, e){
											
										}
									}
									}]},

								{items: [{xtype:'checkboxgroup', disabled: true, fieldLabel: '过滤无用记录',id: '2', name:'2',anchor:'90%',
									items: [
									        {boxLabel: '去除重复记录', name: 'cb-auto-1', checked: true},
									        {boxLabel: '过滤删除标识记录', name: 'cb-auto-2', checked: true}
									        ]
									}]}
					]//items
				}
			]
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

dynamicGrid.store.load({ params: { start: 0, 
    limit: dynamicGrid.getBottomToolbar().pageSize
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