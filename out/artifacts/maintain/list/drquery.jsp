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
    storeUrl: 'redis!query.action?type=drquery&t='+new Date(),  
    width : '100%',  
    height: 400,  
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
					            id: 'billType_',
					            fieldLabel: 'billType',
					            hiddenName: 'billType',
					            forceSelection : true,
					            editable: false,
					            anchor:'90%',
					            store: new Ext.data.SimpleStore({
					                fields: ['value','text'],
					                data: [
					                       ["0","全部话单"],
					                       ["1","GSM"],
					                       ["11","  VPMN"],
					                       ["111","    本地VPMN"],
					                       ["1111","      本地主叫VPMN"],
					                       ["1112","      本地被叫VPMN"],
					                       ["112","    VPMN省内漫游"],
					                       ["113","    VPMN省际漫游"],
					                       ["12","  普通语音"],
					                       ["121","    本地普通语音"],
					                       ["1211","      本地普通网内语音"],
					                       ["12111","        本地普通网内语音主叫"],
					                       ["12112","        本地普通网内语音被叫"],
					                       ["1212","      本地普通网外语音"],
					                       ["12121","        本地普通网外语音主叫"],
					                       ["12122","        本地普通网外语音被叫"],
					                       ["122","    省内普通语音"],
					                       ["123","    省际普通语音"],
					                       ["124","    国际普通语音"],
					                       ["13","  自助服务"],
					                       ["2","数据业务"],
					                       ["21","SMS"],
					                       ["211","  国际SMS"],
					                       ["212","  网内SMS"],
					                       ["213","  网外SMS"],
					                       ["214","  梦网SMS"],
					                       ["22","MMS"],
					                       ["221","  国际MMS"],
					                       ["222","  普通MMS"],
					                       ["23","SP"],
					                       ["231","  SP按次"],
					                       ["232","  SP包月"],
					                       ["24","GPRS"],
					                       ["241","  省内GPRS"],
					                       ["242","  省际GPRS"],
					                       ["243","  国际GPRS"],
					                       ["25","增值业务"],
					                       ["3","月基本费"],
					                       ["5","集团PBX详单"],
					                       ["6","企业信息机详单"],
					                       ["7","集团400业务详单"],
					                       ["9","集团WLAN详单"]]
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
										var firstValue = combo.getStore().reader.arrayData[0][0];  
										combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示  
					                }    
					            } 
					        }]},
							{items: [{xtype:'textfield',fieldLabel: '开始日期',name: 'fromDate',value: Utils.getCurrentMonthFirstDay(),anchor:'90%'}]},
							{items: [{xtype:'textfield',fieldLabel: '结束日期',name: 'thruDate',value:Utils.getCurrentMonthLastDay(),anchor:'90%'}]},
							{items: [{xtype:'textfield',fieldLabel: '手机号',id: 'billId', name:'billId',anchor:'90%', value:'18601134210'}]},
							{items: [{
					            xtype: 'combo',
					            //id: 'impType',
					            fieldLabel: 'impType',
					            hiddenName: 'impType',
					            forceSelection : true,
					            editable: false,
					            anchor:'90%',
					            store: new Ext.data.SimpleStore({
					                fields: ['text','value'],
					                data: [['实时详单','CUR'],['历史详单','HIS']]
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
					        {items: [{
					            xtype: 'combo',
					            //id: 'queryType',
					            fieldLabel: 'queryType',
					            hiddenName: 'queryType',
					            forceSelection : true,
					            editable: false,
					            anchor:'90%',
					            store: new Ext.data.SimpleStore({
					                fields: ['text','value'],
					                data: [['区分详单查询','00'],['详单打印','09'],['保密天使业务','90']]
					            }),
					            valueField: 'value',
					            displayField: 'text',
					            typeAhead: true,
					            mode: 'local',
					            triggerAction: 'all',
					            selectOnFocus: true,
					            allowBlank: false,
					            listeners: {  
					                c: function(combo) {  
										var firstValue = combo.getStore().reader.arrayData[0][1];  
										combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示  
					                }    
					            } 
					        }]},
						
							{items: [{xtype:'textfield',fieldLabel: 'userId', id: 'userId', name:'userId',anchor:'90%', value:''
							}]},
							
							{columnWidth:.67,items: [{
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
					            listeners: {  
					                afterRender: function(combo) {  
										var firstValue = combo.getStore().reader.arrayData[0][1];  
										combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示  
					                }    
					            } 
					        }]},
					        {items: [{xtype:'textfield',fieldLabel: 'regionCode',name: 'regionCode',value:'571',anchor:'90%'}]}
								
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
	               dynamicGrid.store.baseParams.dbType = "ocnosqlDataSource";
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize}, 
	               callback: function(record, options, success){
	            	   if(success){
	            	   		createSummary();
	            	   }
	               }}); 
				}
			},{
				text:'全选',
				cls: 'x-icon-btn',
				handler: function(){
					dynamicGrid.getSelectionModel().selectAll();
				}
			},{
				text:'复制',
				cls: 'x-icon-btn',
				handler: function(){
					Utils.copySelectedRows(dynamicGrid);
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

var _store = null, _store2 = null, _store3 = null;
function createSummary(){
	var sumGrid = Ext.getCmp('sumGrid'), sumGrid2 = Ext.getCmp('sumGrid2'), sumGrid3 = Ext.getCmp('sumGrid3');
	if(sumGrid){
		viewport.remove(sumGrid);
	}
	if(sumGrid2){
		viewport.remove(sumGrid2);
	}
	if(sumGrid3){
		viewport.remove(sumGrid3);
	}
	if(!dynamicGrid.store.reader.jsonData.extInfo || dynamicGrid.store.reader.jsonData.extInfo.sums.length == 0){
		return; 
	}
	var sumData = dynamicGrid.store.reader.jsonData.extInfo.sums;
	var dataArr = [], fields = [], columns = [], obj = {};
	var dataArr2 = [], fields2 = [], columns2 = [], obj2 = {};
	var dataArr3 = [], fields3 = [], columns3 = [], obj3 = {};
	for(var i= 0; i < sumData.length; i++){
		//if(sumData[i].fee == 0){
		//	continue;
		//}
		if(sumData[i].type == 0){
			obj[sumData[i].accCode] = sumData[i].fee;
			fields.push({name: sumData[i].accCode, mapping: sumData[i].accCode});
			columns.push({header: sumData[i].accName, width:200, sortable:true, dataIndex: sumData[i].accCode});
		}else if(sumData[i].type == 1){
			obj2[sumData[i].accCode] = sumData[i].fee;
			fields2.push({name: sumData[i].accCode, mapping: sumData[i].accCode});
			columns2.push({header: sumData[i].accName, width:200, sortable:true, dataIndex: sumData[i].accCode});
		}else if(sumData[i].type == 2){
			obj3[sumData[i].accCode] = sumData[i].fee;
			fields3.push({name: sumData[i].accCode, mapping: sumData[i].accCode});
			columns3.push({header: sumData[i].accName, width:200, sortable:true, dataIndex: sumData[i].accCode});
		}
	} 
	dataArr.push(obj);
	dataArr2.push(obj2);
	dataArr3.push(obj3);
	if(fields.length > 0){
		_store = new Ext.data.ArrayStore({ 
			data: dataArr,
			fields: fields
		});
		var grid = new Ext.grid.GridPanel({
			id: 'sumGrid',
			store: _store,
			columns: columns,
			sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
		    height:100,
		    frame:true,
		    title:'上月汇总',
		    iconCls:'icon-grid'
		});
		viewport.add(grid);
	}
	if(fields2.length > 0){
		_store2 = new Ext.data.ArrayStore({ 
			data: dataArr2,
			fields: fields2
		});
		var grid = new Ext.grid.GridPanel({
			id: 'sumGrid2',
			store: _store2,
			columns: columns2,
			sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
		    height:100,
		    frame:true,
		    title:'1号到昨天汇总',
		    iconCls:'icon-grid'
		});
		viewport.add(grid);
	}
	if(fields3.length > 0){
		_store3 = new Ext.data.ArrayStore({ 
			data: dataArr3,
			fields: fields3
		});
		var grid = new Ext.grid.GridPanel({
			id: 'sumGrid3',
			store: _store3,
			columns: columns3,
			sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
		    height:100,
		    frame:true,
		    title:'当天汇总',
		    iconCls:'icon-grid'
		});
		viewport.add(grid);
	}
	viewport.doLayout(true);
}



}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>