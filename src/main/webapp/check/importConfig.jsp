<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <script>
        Ext.onReady(function () {
            var pageSize = 20;

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

            var columns = new Ext.data.JsonStore({
                fields: ['COLUMN_NAME'],
                url: appPath + "/CommonAction_getColmns.action",
                autoLoad: true,
                root: "root"
            });

            var dynamicGrid = new Ext.grid.DynamicGrid({
                title: '配置列表',
                storeUrl: "<%=appPath%>/ImportAction_queryConfigList.action",
                width: '100%',
                height: 365,
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

            dynamicGrid.addListener('rowclick', clickRow);
            function clickRow(grid, rowindex, e) {
                grid.getSelectionModel().each(function (rec) {
                    var id = rec.get("C0");
                    Ext.Ajax.request({
                        url: appPath + '/ImportAction_queryConfigById.action',
                        method: 'post',
                        params: {
                            id: id
                        },
                        success: function (req) {
                            var obj = Ext.util.JSON.decode(req.responseText);
                            if (obj.success) {
                                Ext.getCmp('rowkey').setValue(obj.records[0].rowkey);
                                Ext.getCmp('cname').setValue(obj.records[0].cname);
                                Ext.getCmp('separator').setValue(obj.records[0].separatorx);
                                Ext.getCmp('loadType').setValue(obj.records[0].loadType);
                                //Ext.getCmp('inputPath').setValue(obj.records[0].inputPath);
                                //Ext.getCmp('outputPath').setValue(obj.records[0].outputPath);
                                Ext.getCmp('generator').setValue(obj.records[0].generator);
                                Ext.getCmp('algocolumn').setValue(obj.records[0].algocolumn);
                                Ext.getCmp('id').setValue(obj.records[0].id);
                            } else {
                                Ext.Msg.alert('错误', '数据获取失败!');
                            }
                        }
                    });
                });
            }

            var tablePanel = new Ext.FormPanel({
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
                                emptyText: '请选择表空间',
                                id: 'schema',
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
                                        Ext.getCmp('table').setValue('');
                                    }
                                }
                            }]
                        },
                        {
                            items: [{
                                xtype: 'combo',
                                fieldLabel: '表名',
                                id: 'table',
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
                                        Ext.getCmp('add').setDisabled(false);
                                        Ext.getCmp('edit').setDisabled(false);
                                        Ext.getCmp('save').setDisabled(false);
                                        var a = tablePanel.getForm().getValues();
                                        var params = dynamicGrid.store.baseParams;
                                        Ext.apply(params, a);
                                        dynamicGrid.store.baseParams = params;
                                        dynamicGrid.store.load();

                                        //加载显示列
                                        var schema = Ext.getCmp('schema').getValue();
                                        var table = Ext.getCmp('table').getValue();
                                        columns.baseParams = {'schema': schema, 'table': table};
                                        columns.load();
                                    }
                                }
                            }]
                        }]
                }]
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
                                xtype: 'textfield',
                                fieldLabel: '配置名称',
                                editable: false,
                                emptyText: '请输入配置名称',
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
                                value: '',
                                name: 'separator',
                                id: 'separator',
                                disabled: 'true',
                                emptyText: '请输入分隔符，以,号隔开',
                                anchor: '90%',
                                typeAhead: true,
                                triggerAction: 'all',
                                selectOnFocus: true
                            }]
                        }, {
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: 'callback',
                                disabled: 'true',
                                editable: false,
                                value: 'com.ailk.oci.ocnosql.common.rowkeygenerator.GenRKCallBackDefaultImpl',
                                name: 'callback',
                                id: 'callback',
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
                                value: 'C01',
                                anchor: '90%',
                                store: columns,
                                displayField: 'COLUMN_NAME',
                                emptyText: '请输入rowkey',
                                mode: 'local',
                                editable: false
                            }]
                        },
                        {
                            items: [{
                                xtype: 'combo',
                                enableKeyEvents: true,
                                fieldLabel: 'generator',
                                name: 'generator',
                                id: 'generator',
                                value: 'md5',
                                editable: false,
                                anchor: '90%',
                                store: new Ext.data.SimpleStore({
                                    fields: ['text', 'value'],
                                    data: [['md5', 'md5']]
                                }),
                                valueField: 'value',
                                emptyText: '请输入generator',
                                disabled: 'true',
                                displayField: 'text',
                                typeAhead: true,
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true
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
                                value: 'C01',
                                anchor: '90%',
                                store: columns,
                                emptyText: '请输入algocolumn',
                                displayField: 'COLUMN_NAME',
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
                    text: '新增',
                    disabled: true,
                    cls: 'x-icon-btn',
                    id: 'add',
                    handler: function () {
                        controlForm(false);
                        clearForm();
                    }
                }, {
                    text: '编辑',
                    cls: 'x-icon-btn',
                    disabled: true,
                    id: 'edit',
                    handler: function () {
                        controlForm(false);
                    }
                }, {
                    text: '保存',
                    disabled: true,
                    id: 'save',
                    cls: 'x-icon-btn',
                    handler: function () {
                        var cname = Ext.getCmp('cname').getValue();
                        var separator = Ext.getCmp('separator').getValue();
                        var callback = Ext.getCmp('callback').getValue();
                        var rowkey = Ext.getCmp('rowkey').getValue();
                        var generator = Ext.getCmp('generator').getValue();
                        var algocolumn = Ext.getCmp('algocolumn').getValue();
                        if(Ext.isEmpty(cname)) {
                            Ext.Msg.alert('提示', '请输入配置规则');
                            return;
                        }
                        if(Ext.isEmpty(separator)) {
                            Ext.Msg.alert('提示', '请输入分隔符');
                            return;
                        }
                        if(Ext.isEmpty(callback)) {
                            Ext.Msg.alert('提示', '请输入callback');
                            return;
                        }
                        if(Ext.isEmpty(rowkey)) {
                            Ext.Msg.alert('提示', '请输入rowkey');
                            return;
                        }
                        if(Ext.isEmpty(generator)) {
                            Ext.Msg.alert('提示', '请输入generator');
                            return;
                        }
                        if(Ext.isEmpty(algocolumn)) {
                            Ext.Msg.alert('提示', '请输入algocolumn');
                            return;
                        }
                        var a = configPanel.getForm().getValues();
                        var params = dynamicGrid.store.baseParams;
                        Ext.apply(params, a);
                        Ext.Ajax.request({
                            url: "<%=appPath%>/ImportAction_saveConfig.action",
                            method: 'post',
                            params: params,
                            success: function (form, action) {
                                var obj = Ext.util.JSON.decode(form.responseText);
                                if (obj.success) {
                                    Ext.Msg.alert('成功', '保存成功!');
                                    dynamicGrid.store.load();
                                } else {
                                    Ext.Msg.alert('错误', '保存失败!');
                                }
                            }
                        });
                    }
                }]
            });

            Ext.MyViewport = Ext.extend(Ext.Viewport, {
                xtype: "viewport",
                layout: "anchor",
                autoScroll: true,
                initComponent: function () {
                    this.items = [
                        tablePanel,
                        configPanel,
                        dynamicGrid
                    ],
                    Ext.MyViewport.superclass.initComponent.call(this);
                }
            });

            var viewport = new Ext.MyViewport();
            clearForm();
            function queryRowkey() {
                Ext.Ajax.request({
                    url: appPath + '/redis!queryRowkey.action',
                    method: 'post',
                    params: {
                        billId: Ext.getCmp('billId').getValue()
                    },
                    success: function (req) {
                        var obj = Ext.util.JSON.decode(req.responseText);
                        if (obj.success) {
                            Ext.getCmp('rowkey').setValue(obj.rowkey);
                        } else {
                            Ext.Msg.alert('错误', '查询失败');
                        }
                    }
                });
            }

            function controlForm(result) {
                Ext.getCmp('rowkey').setDisabled(result);
                Ext.getCmp('cname').setDisabled(result);
                Ext.getCmp('separator').setDisabled(result);
                Ext.getCmp('callback').setDisabled(result);
                Ext.getCmp('generator').setDisabled(result);
                Ext.getCmp('algocolumn').setDisabled(result);
                // Ext.getCmp('inputPath').setDisabled(result);
             // Ext.getCmp('outputPath').setDisabled(result);
            }

            function clearForm() {
                Ext.getCmp('rowkey').setValue(null);
                Ext.getCmp('cname').setValue(null);
                Ext.getCmp('separator').setValue(null);
                // Ext.getCmp('loadType').setValue(null);
                // Ext.getCmp('inputPath').setValue(null);
                // Ext.getCmp('outputPath').setValue(null);
                Ext.getCmp('generator').setValue(null);
                Ext.getCmp('algocolumn').setValue(null);
                Ext.getCmp('id').setValue(null);
            }
        });
    </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>