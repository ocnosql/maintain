<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <script>
        Ext.onReady(function () {
            var pageSize = 100;
            var gid = null;
            Ext.Ajax.timeout = 180000;

            var tablespace = new Ext.data.JsonStore({
                fields: ['TABLE_SCHEM'],
                url: appPath + "/CommonAction_getTableSpaces.action",
                autoLoad: true,
                root: "root"
            });

            var tables = new Ext.data.JsonStore({
                fields: ['TABLE_NAME'],
                url: appPath + "/CommonAction_getTables.action",
                autoLoad: true,
                root: "root"
            });

            var grid = new Ext.grid.DynamicGrid({
                storeUrl: appPath + "/DeleteAction_queryData.action",
                width: '100%',
                height: 570,
                rowNumberer: true,
                sm: new Ext.grid.CheckboxSelectionModel(),
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    displayInfo: true,
                    displayMsg: '显示第{0}到{1}条数据,共{2}条',
                    emptyMsg: "没有数据",
                    beforePageText: "第",
                    afterPageText: '页 共{0}页, 每页显示' + pageSize + '条'
                })
            });

            var dynamicGrid = new Ext.grid.DynamicGrid({
                title: '任务列表',
                storeUrl: appPath + "/DeleteAction_queryHistory.action",
                width: '100%',
                height: 430,
                rowNumberer: true,
                checkboxSelModel: true,
                sm: new Ext.grid.CheckboxSelectionModel({singleSelect: true}),
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    displayInfo: true,
                    displayMsg: '显示第{0}到{1}条数据,共{2}条',
                    emptyMsg: "没有数据",
                    beforePageText: "第",
                    afterPageText: '页 共{0}页, 每页显示' + pageSize + '条'
                })
            });

            var searchPanel = new Ext.FormPanel({
                        labelAlign: 'left',
                        buttonAlign: 'right',
                        bodyStyle: 'padding:5px;',
                        frame: true,
                        labelWidth: 65,
                        monitorValid: true,
                        items: [{
                            layout: 'column',
                            labelSeparator: ':',
                            title: '删除条件',
                            xtype: 'fieldset',
                            layout: 'column',
                            autoHeight: true,
                            defaults: {layout: 'form', border: false, columnWidth: .5},
                            items: [{
                                items: [{
                                    xtype: 'combo',
                                    fieldLabel: '表空间',
                                    store: tablespace,
                                    displayField: 'TABLE_SCHEM',
                                    mode: 'local',
                                    triggerAction: 'all',
                                    id: 'table_space',
                                    editable: false,
                                    name: 'table_space',
                                    anchor: '80%',
                                    emptyText: '请选择表空间',
                                    enableKeyEvents: true,
                                    listeners: {
                                        'select': function (obj, record, index) {
                                            var ts = record.get('TABLE_SCHEM');
                                            tables.baseParams = {'tablespace': ts};
                                            tables.load();
                                        },
                                        'change': function () {
                                            Ext.getCmp('tables').setValue('');
                                        }
                                    }
                                }]
                            }, {
                                items: [{
                                    xtype: 'combo',
                                    enableKeyEvents: true,
                                    store: tables,
                                    fieldLabel: '表',
                                    id: 'tables',
                                    name: 'tables',
                                    editable: false,
                                    emptyText: '请选择表',
                                    displayField: 'TABLE_NAME',
                                    mode: 'local',
                                    triggerAction: 'all',
                                    anchor: '80%'
                                }]
                            }, {
                                items: [{
                                    xtype: 'textarea',
                                    fieldLabel: '过滤条件',
                                    id: 'whereis',
                                    name: 'whereis',
                                    anchor: '90%',
                                    value: '',
                                    enableKeyEvents: true
                                }],
                                columnWidth: 1.05
                            }]
                        }],
                        buttons: [{
                            text: '查询结果',
                            cls: 'x-icon-btn',
                            handler: function () {
                                var table_space = Ext.getCmp('table_space').getValue();
                                if (Ext.isEmpty(table_space)) {
                                    Ext.Msg.alert('提示', '请选择表空间!');
                                    return;
                                }
                                var a = searchPanel.getForm().getValues();
                                var table = Ext.getCmp('tables').getValue();
                                var params = dynamicGrid.store.baseParams;
                                Ext.apply(params, a);
                                dynamicGrid.store.baseParams = params;
                                params.tables = table;
                                dynamicGrid.store.load();
                            }
                        }, {
                            text: '数据查询任务提交',
                            id: 'query',
                            cls: 'x-icon-btn',
                            handler: function () {
                                submitQuery();
                            }
                        }, {
                            text: '数据详情',
                            id: 'detail',
                            cls: 'x-icon-btn',
                            //disabled : true,
                            handler: function () {
                                detail();
                            }
                        }, {
                            text: '数据删除任务提交',
                            cls: 'x-icon-btn',
                            //disabled : true,
                            handler: function () {
                                deleteData();
                            }
                        }]
                    }
            );

            Ext.MyViewport = Ext.extend(Ext.Viewport, {
                xtype: "viewport",
                layout: "anchor",
                autoScroll: true,
                initComponent: function () {
                    this.items = [
                        searchPanel,
                        dynamicGrid
                    ],
                            Ext.MyViewport.superclass.initComponent.call(this);
                }
            });

            Ext.MyViewportx = Ext.extend(Ext.Viewport, {
                xtype: "viewport",
                layout: "anchor",
                autoScroll: true,
                initComponent: function () {
                    this.items = [grid],
                            Ext.MyViewportx.superclass.initComponent.call(this);
                }
            });

            var viewport = new Ext.MyViewport();
            //viewport.hidden(true);

            //查询数据
            function submitQuery() {
                dynamicGrid.store.baseParams.gid = null;
                var table = Ext.getCmp('tables').getValue();
                var whereis = Ext.getCmp('whereis').getValue();
                var table_space = Ext.getCmp('table_space').getValue();
                if (Ext.isEmpty(table_space)) {
                    Ext.Msg.alert('提示', '请选择表空间!');
                    return;
                }
                if (Ext.isEmpty(table)) {
                    Ext.Msg.alert('提示', '请选择表!');
                    return;
                }
                if (Ext.isEmpty(whereis)) {
                    Ext.Msg.alert('提示', '请输入查询条件!');
                    return;
                }

                Ext.Msg.confirm('提示','确定提交数据查询任务?', function(btn) {
                    if(btn=='yes') {
                        var fulltablename = null;
                        if (table_space == 'DEFAULT') {
                            fulltablename = table;
                        } else {
                            fulltablename = table_space + "." + table;
                        }
                        var sql = 'select * from ' + fulltablename + ' ' + whereis;
                        Ext.Ajax.request({
                            url: appPath + '/DeleteAction_submitQuery.action',
                            method: 'post',
                            params: {
                                sql: sql,
                                table: table,
                                whereis: whereis,
                                table_space: table_space
                            }
                        });
                        Ext.Msg.alert('提示', '查询任务已提交!请点击"刷新结果"查看');
                    }
                },this);
            }

            //删除数据
            function deleteData() {
                var row = dynamicGrid.getSelectionModel().getSelected();
                if (typeof(row) == "undefined") {
                    Ext.Msg.alert('提示', '请选择一行记录再操作!');
                    return;
                }
                Ext.Msg.confirm('系统提示','确定要提交数据删除任务？', function(btn){
                    if(btn=='yes'){
                        var id = row.get("C0");
                        dynamicGrid.store.load();
                        Ext.Msg.alert('提示', '删除任务已提交!');
                        Ext.Ajax.request({
                            url: appPath + '/DeleteAction_delete.action',
                            method: 'post',
                            params: {
                                id: id
                            }
                        });
                    }
                },this);
            }

            function detail() {
                var row = dynamicGrid.getSelectionModel().getSelected();
                if (typeof(row) == "undefined") {
                    Ext.Msg.alert('提示', '请选择一行记录再操作!');
                    return;
                }
                var id = row.get("C0");
                new Ext.MyViewportx();
                var params = grid.store.baseParams;
                Ext.apply(params, id);
                grid.store.baseParams = params;
                grid.store.load({params: {start: 0, limit: grid.getBottomToolbar().pageSize, id: id}});
                var _window = new top.Ext.Window({
                    title: "数据详情",
                    width: 1000,
                    height: 600,
                    layout: "form",
                    labelWidth: 45,
                    closeAction: 'hide',
                    modal: true,
                    plain: true,
                    bodyStyle: "padding:5px",
                    items: [grid]
                });
                _window.show();
            }
        });
    </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>