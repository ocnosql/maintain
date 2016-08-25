<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
	Ext.onReady(function(){
		var pageSize = 15;
		Ext.Ajax.timeout = 180000;

		var tablespace = new Ext.data.JsonStore({
			fields: ['TABLE_SCHEM'],
			url : appPath + "/CommonAction_getTableSpaces.action",
			autoLoad : true,
			root : "root"
		});

		var tables = new Ext.data.JsonStore({
			fields: ['TABLE_NAME'],
			url : appPath + "/CommonAction_getTables.action",
			autoLoad : true,
			root : "root"
		});

		var searchPanel = new Ext.FormPanel({
			labelAlign:'left',
			buttonAlign:'right',
			bodyStyle:'padding:5px;',
			frame:true,
			labelWidth:65,
			monitorValid:true,
			items:[{
				layout:'column',
				labelSeparator:':',
				title: '查询 ',
				xtype: 'fieldset',
				layout:'column',
				autoHeight: true,
				defaults:{layout: 'form',border:false,columnWidth:.5},
				items:[{
					items:[{
						xtype:'combo',
						fieldLabel: '表空间',
						store : tablespace,
						displayField:'TABLE_SCHEM',
						mode: 'local',
						triggerAction: 'all',
						id: 'table_space',
						name:'table_space',
						anchor:'80%',
						emptyText:'请选择表空间',
						enableKeyEvents:true,
						listeners : {
							'select' : function(obj, record, index){
								var ts = record.get('TABLE_SCHEM');
								tables.baseParams = {'tablespace' : ts};
								tables.load();

								var a = searchPanel.getForm().getValues();
								var table = Ext.getCmp('tables').getValue();
								var params = dynamicGrid.store.baseParams;
								Ext.apply(params, a);
								params.tables = table;
								dynamicGrid.store.baseParams = params;
								dynamicGrid.store.load();
							}
						}
					}]}, {
					items: [{
						xtype:'combo',
						enableKeyEvents:true,
						store : tables,
						fieldLabel: '表',
						id:'tables',
						name: 'tables',
						emptyText:'请选择表',
						displayField:'TABLE_NAME',
						mode: 'local',
						triggerAction: 'all',
						anchor:'80%',
						listeners : {
							'select' : function(obj, record, index){
								var a = searchPanel.getForm().getValues();
								var params = dynamicGrid.store.baseParams;
								Ext.apply(params, a);
								dynamicGrid.store.baseParams = params;
								dynamicGrid.store.load();
							}
						}
					}]}]}]
				}
		);

		var dynamicGrid = new Ext.grid.DynamicGrid({
			title: '数据删除历史记录',
			storeUrl: appPath + "/DeleteAction_queryHistory.action",
			width : '100%',
			height: 530,
			rowNumberer: true,
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
		function deleteData(){
			var sql = 'select * from ' + Ext.getCmp('tables').getValue() + ' ' + Ext.getCmp('whereis').getValue();
			var table = Ext.getCmp("tables").getValue();
			var whereis = Ext.getCmp("whereis").getValue();
			Ext.Ajax.request({
				url: appPath + '/DeleteAction_delete.action',
				method:'post',
				params:{
					sql : sql,
					table : table,
					whereis : whereis
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