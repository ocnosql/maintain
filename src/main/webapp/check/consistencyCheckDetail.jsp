<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var pageSize = 3000;

/* Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '../../examples/ux'); */

var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: appPath + "/check/result.jsp?queryType=consistencyCheckDetail",  
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
						{items: [{xtype:'textfield',disabled: true,fieldLabel: '开始时间',id: 'billId', name:'billId',anchor:'90%', value:'20151201',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						{items: [{xtype:'textfield',disabled: true,enableKeyEvents:true,fieldLabel: '结束时间',name: 'startDate',value:'20151202',anchor:'90%',
								listeners : {
									keypress : function(obj, e){
										
									}
								}}]},
								{items: [{
						            xtype: 'combo',
						            //id: 'hashType',
						            disabled: true,
						            fieldLabel: '稽核方式',
						            hiddenName: 'hashType',
						            forceSelection : true,
						            editable: false,
						            anchor:'90%',
						            store: new Ext.data.SimpleStore({
						                fields: ['text','value'],
						                data: [['全表稽核','all'],['按用户稽核','users']]
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
						                },
						                "select": function(combo, record ){
								    		var val = combo.getValue();
								    		if(val == 'all'){
								    			Ext.getCmp('usernumbers').hide();
								    			Ext.getCmp('usernumbers').setValue('');
								    		} else {
								    			Ext.getCmp('usernumbers').show();
								    		}
								    	}
								
						            } 
						        }]},
						        {items: [{
						            xtype: 'combo',
						            disabled: true,
						            //id: 'hashType',
						            fieldLabel: '选择科目',
						            hiddenName: '科目',
						            forceSelection : true,
						            editable: false,
						            anchor:'90%',
						            store: new Ext.data.SimpleStore({
						                fields: ['text','value'],
						                data: [['所有科目','all'],['指定科目','some']]
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
						                },
						                "select": function(combo, record ){
								    		var val = combo.getValue();
								    		if(val == 'all'){
								    			Ext.getCmp('kemu').hide();
								    			Ext.getCmp('kemu').setValue('');
								    		} else {
								    			Ext.getCmp('kemu').show();
								    		}
								    	}   
						            } 
						        }]}, 
						        
											{items: [{xtype:'textfield', disabled: true,/**inputType: 'file',*/ fieldLabel: '用户号码',id: 'usernumbers', name:'usernumbers',anchor:'90%',
												enableKeyEvents:true,
												listeners : {
													keypress : function(obj, e){
														
													}
												}
												}]},
								
											{items: [{xtype:'textfield',  disabled: true,fieldLabel: '科目',id: 'kemu', name:'kemu',anchor:'90%', value:'',
												enableKeyEvents:true,
												listeners : {
													keypress : function(obj, e){
														
													}
												}
												}]},
/* 											{items: [{xtype:'itemselector',fieldLabel: 'duoxuan',id: 'duoxuan', name:'duoxuan',anchor:'90%', 
												store: new Ext.data.SimpleStore({
									                fields: ['text','value'],
									                data: [['所有科目','all'],['输入科目','some']]
									            }),
									            displayField: 'text',
									            valueField: 'value',
									            allowBlank: false,
									            msgTarget: 'side',
									            fromTitle: 'Available',
									            toTitle: 'Selected',
												enableKeyEvents:true,
												listeners : {
													keypress : function(obj, e){
														
													}
												}
												}]}, */
												{items: [{xtype:'checkboxgroup',disabled: true,fieldLabel: '业务类型',id: '2', name:'2',anchor:'90%',
													items: [
													        {boxLabel: 'cs', name: 'cb-auto-1', checked: true},
													        {boxLabel: 'sms', name: 'cb-auto-2', checked: true},
													        {boxLabel: 'mms', name: 'cb-auto-3', checked: true},
													        {boxLabel: 'imsp', name: 'cb-auto-4', checked: true},
													        {boxLabel: 'ps', name: 'cb-auto-5', checked: true}
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

var viewport = new Ext.MyViewport();

function queryRowkey(){
	Ext.Ajax.request({
	    url: appPath + '/redis!queryRowkey.action',
	    method:'post',
	    params:{
	     billId: Ext.getCmp('billId').getValue()
	   },
	   success:function(req){
		  var obj = Ext.util.JSON.decode(req.responseText);
	      if(obj.success){  
	    	  Ext.getCmp('rowkey').setValue(obj.rowkey);
	      }else{ 
	    	  Ext.Msg.alert('错误','查询失败');
	      }
	   }
	 });
}
dynamicGrid.store.load({ params: { start: 0, 
    limit: dynamicGrid.getBottomToolbar().pageSize
 } 
}); 
Ext.getCmp('usernumbers').hide();
Ext.getCmp('kemu').hide();
}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>