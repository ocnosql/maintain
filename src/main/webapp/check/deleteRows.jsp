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
					title: '删除条件',
					xtype: 'fieldset',
					layout:'column',
					//collapsible: true,
					autoHeight: true,
					defaults:{layout: 'form',border:false,columnWidth:.5},
					items:[
						{items: [{xtype:'textfield',fieldLabel: '开始日期',id: 'billId', name:'billId',anchor:'90%', value:'20151201',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: '结束日期',name: 'startDate',value:'20151202',anchor:'90%',
								listeners : {
									keypress : function(obj, e){
										
									}
								}}]},

						        
						{items: [{xtype:'textfield',fieldLabel: '用户号码',id: 'usernumbers', name:'usernumbers',anchor:'90%', value:'18601134210,17606712167',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						/* {items: [{
				            xtype: 'combo',
				            //id: 'hashType',
				            fieldLabel: '过滤条件',
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
				        
						{items: [{xtype:'textarea',fieldLabel: '过滤条件',id: 'busiType', name:'busiType',anchor:'90%', value:'CALL_TYPE =\'1\' AND BILL_MONTH=\'201512\'',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}],columnWidth:1.05}
					]//items
				}
			],
			buttons: [{
				text:'查询',
				cls: 'x-icon-btn',
				handler: function(){
				   var a =searchPanel.getForm().getValues(); 
	               var params = dynamicGrid.store.baseParams; 
	               Ext.apply(params,a); 
	               dynamicGrid.store.baseParams = params; 
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
				}
			}, {
				text:'删除',
				cls: 'x-icon-btn',
				handler: function(){
					Ext.Msg.alert('提示', '删除成功', function () {
					});
				}
			}]
		}
);//FormPanel

var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: appPath + "/check/result.jsp?queryType=rowkeyQuery",  
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


}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>