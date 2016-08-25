<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <script>
        Ext.onReady(function () {

            function setConfig() {
                var tablespace = Ext.getCmp('table_space').getValue();
                var table = Ext.getCmp('tables').getValue();
                var config = Ext.getCmp('config').getValue();
                Ext.Ajax.request({
                    url: "<%=appPath%>/ImportAction_queryIcBySchemaAndTable.action",
                    method: 'post',
                    params: {schema: tablespace, table: table, cname: config},
                    success: function (req) {
                        var obj = Ext.util.JSON.decode(req.responseText);
                        Ext.getCmp("cname").setValue(obj[0].cname);
                        Ext.getCmp("separator").setValue(obj[0].separatorx);
                        Ext.getCmp("loadType").setValue(obj[0].loadType);
                        Ext.getCmp("rowkey").setValue(obj[0].rowkey);
                        Ext.getCmp("generator").setValue(obj[0].generator);
                        Ext.getCmp("algocolumn").setValue(obj[0].algocolumn);
                    }
                });
            }

            function submitImportTask() {
                var params = configPanel.getForm().getValues();
                Ext.Ajax.request({
                    url: "<%=appPath%>/ImportAction_importData.action",
                    method: 'post',
                    params: params
                    /*
                    success: function (form) {
                        var obj = Ext.util.JSON.decode(form.responseText);
                        if (obj.success) {

                        } else {
                            Ext.Msg.alert('错误', '导入任务提交失败!');
                        }
                    }
                    */
                });
                Ext.Msg.alert('提示', '导入任务已提交!');
            }

            var pageSize = 100;

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

            var dynamicGrid = new Ext.grid.DynamicGrid({
                title: '导入数据历史',
                storeUrl: "<%=appPath%>/ImportAction_queryImportHistory.action",
                width: '100%',
                height: 400,
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

            var config = new Ext.data.JsonStore({
                fields: ['cname'],
                url: "<%=appPath%>/ImportAction_queryConfigBySchemaAndTable.action",
                autoLoad: true,
                root: "root"
            });

            var configPanel = new Ext.FormPanel({
                labelAlign: 'left',
                buttonAlign: 'right',
                bodyStyle: 'padding:5px;',
                frame: true,
                labelWidth: 65,
                monitorValid: true,
                items: [{
                    layout: 'column', labelSeparator: ':',
                    xtype: 'fieldset',
                    layout: 'column',
                    autoHeight: true,
                    defaults: {layout: 'form', border: false, columnWidth: .5},
                    items: [
                        {
                            items: [{
                                xtype: 'combo',
                                fieldLabel: '表空间',
                                editable: false,
                                anchor: '90%',
                                id: 'table_space',
                                name: 'table_space',
                                emptyText: '请选择表空间',
                                store: tablespace,
                                displayField: 'TABLE_SCHEM',
                                typeAhead: true,
                                mode: 'local',
                                triggerAction: 'all',
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
                        },
                        {
                            items: [{
                                xtype: 'combo',
                                fieldLabel: '表名',
                                id: 'tables',
                                editable: false,
                                anchor: '90%',
                                store: tables,
                                emptyText: '请选择表',
                                displayField: 'TABLE_NAME',
                                typeAhead: true,
                                mode: 'local',
                                triggerAction: 'all',
                                listeners: {
                                    'select': function (obj, record, index) {
                                        var tablespace = Ext.getCmp('table_space').getValue();
                                        var table = Ext.getCmp('tables').getValue();
                                        config.baseParams = {'schema': tablespace, 'table': table};
                                        config.load();

                                        //显示导入历史数据
                                        var a = configPanel.getForm().getValues();
                                        var params = dynamicGrid.store.baseParams;
                                        Ext.apply(params, a);
                                        dynamicGrid.store.baseParams = params;
                                        dynamicGrid.store.load();

                                        //清空配置规则信息
                                        Ext.getCmp("config").setValue(null);
                                        Ext.getCmp("cname").setValue(null);
                                        Ext.getCmp("separator").setValue(null);
                                        Ext.getCmp("loadType").setValue(null);
                                        Ext.getCmp("rowkey").setValue(null);
                                        Ext.getCmp("generator").setValue(null);
                                        Ext.getCmp("algocolumn").setValue(null);
                                    }
                                }
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                enableKeyEvents: true,
                                fieldLabel: '输入路径',
                                emptyText: '请输入导入文件路径',
                                name: 'inputPath',
                                id: 'inputPath',
                                value: '',
                                anchor: '90%',
                            }]
                        },
                        {
                            items: [{
                                xtype: 'combo',
                                fieldLabel: '配置规则',
                                editable: false,
                                store: config,
                                name: 'config',
                                id: 'config',
                                anchor: '90%',
                                emptyText: '请选择配置规则',
                                valueField: 'cname',
                                displayField: 'cname',
                                typeAhead: true,
                                mode: 'local',
                                triggerAction: 'all',
                                listeners: {
                                    'select': function (obj, record, index) {
                                        setConfig();
                                    }
                                }
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: '配置名称',
                                editable: false,
                                name: 'cname',
                                id: 'cname',
                                disabled: 'true',
                                anchor: '90%'
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: '分隔符',
                                editable: false,
                                name: 'separator',
                                id: 'separator',
                                anchor: '90%',
                                disabled: true,
                                typeAhead: true
                            }]
                        }, {
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: '加载类型',
                                disabled: 'true',
                                editable: false,
                                name: 'loadType',
                                id: 'loadType',
                                anchor: '90%',
                                typeAhead: true
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                enableKeyEvents: true,
                                fieldLabel: 'rowkey',
                                disabled: 'true',
                                name: 'rowkey',
                                id: 'rowkey',
                                anchor: '90%',
                                mode: 'local'
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                enableKeyEvents: true,
                                fieldLabel: 'generator',
                                name: 'generator',
                                id: 'generator',
                                editable: false,
                                disabled: true,
                                anchor: '90%',
                                valueField: 'value',
                                typeAhead: true
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                enableKeyEvents: true,
                                fieldLabel: 'algocolumn',
                                name: 'algocolumn',
                                id: 'algocolumn',
                                disabled: 'true',
                                anchor: '90%',
                                mode: 'local',
                                editable: false
                            }]
                        },
                        {
                            items: [{
                                xtype: 'textfield',
                                enableKeyEvents: true,
                                fieldLabel: 'id',
                                name: 'id',
                                id: 'id',
                                anchor: '90%',
                                hidden: true
                            }]
                        }
                    ]
                }
                ],
                buttons: [{
                    text: '刷新导入历史',
                    cls: 'x-icon-btn',
                    id: 'refresh',
                    handler: function () {
                        var table_schema = Ext.getCmp("table_space").getValue();
                        if(Ext.isEmpty(table_schema)) {
                            Ext.Msg.alert('提示', '请选择表空间!');
                            return;
                        }
                        var a = configPanel.getForm().getValues();
                        var table = Ext.getCmp('tables').getValue();
                        var params = dynamicGrid.store.baseParams;
                        Ext.apply(params, a);
                        dynamicGrid.store.baseParams = params;
                        params.tables = table;
                        dynamicGrid.store.load();
                    }
                }, {
                    text: '提交导入任务',
                    cls: 'x-icon-btn',
                    id: 'add',
                    handler: function () {
                        var table_schema = Ext.getCmp("table_space").getValue();
                        var table = Ext.getCmp("tables").getValue();
                        var inputPath = Ext.getCmp("inputPath").getValue();
                        var config = Ext.getCmp("config").getValue();
                        if(Ext.isEmpty(table_schema)) {
                            Ext.Msg.alert('提示', '请选择表空间!');
                            return;
                        }
                        if(Ext.isEmpty(table)) {
                            Ext.Msg.alert('提示', '请选择表!');
                            return;
                        }
                        if(Ext.isEmpty(inputPath)) {
                            Ext.Msg.alert('提示', '请输入源文件路径!');
                            return;
                        }
                        if(Ext.isEmpty(config)) {
                            Ext.Msg.alert('提示', '请选择配置规则!');
                            return;
                        }
                        Ext.Msg.confirm('提示', '确定要提交导入任务？', function (btn) {
                            if (btn == 'yes') {
                                submitImportTask();
                            }
                        }, this);
                    }
                }]
            });

            //显示导入配置信息
            Ext.MyViewport = Ext.extend(Ext.Viewport, {
                xtype: "viewport",
                layout: "anchor",
                autoScroll: true,
                initComponent: function () {
                    this.items = [
                        configPanel,
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