<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <style type=text/css>
        .x-form-file-wrap {
            position: relative;
            height: 22px;
        }
        .x-form-file-wrap .x-form-file {
            position: absolute;
            right: 0;
            -moz-opacity: 0;
            filter:alpha(opacity: 0);
            opacity: 0;
            z-index: 2;
            height: 22px;
        }
        .x-form-file-wrap .x-form-file-btn {
            position: absolute;
            right: 0;
            z-index: 1;
        }
        .x-form-file-wrap .x-form-file-text {
            position: absolute;
            left: 0;
            z-index: 3;
            color: #777;
        }
    </style>

    <script>

        Ext.onReady(function(){
            var pageSize = 100;
            var gid = null;

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

            var searchPanel = new Ext.FormPanel(
                    {
                        labelAlign:'left',
                        buttonAlign:'right',
                        bodyStyle:'padding:5px;',
                        frame:true,
                        labelWidth:65,
                        monitorValid:true,
                        fileUpload: true,
                        enctype:'multipart/form-data',
                        url:appPath + "/RowkeyBatchOutputQueryAction_generatetask.action",
                        items:[
                            {
                                layout:'column',labelSeparator:':',
                                title: '创建hive表结构',
                                xtype: 'fieldset',
                                layout:'column',
                                //collapsible: true,
                                autoHeight: true,
                                defaults:{layout: 'form',border:false,columnWidth:.5},
                                items:[

                                    {items: [{
                                        xtype:'combo',
                                        fieldLabel: '表空间',
                                        store : tablespace,
                                        displayField:'TABLE_SCHEM',
                                        mode: 'local',
                                        triggerAction: 'all',
                                        id: 'table_schem',
                                        name:'table_schem',
                                        anchor:'80%',
                                        emptyText:'请选择表空间',
                                        enableKeyEvents:true,
                                        listeners : {
                                            'select' : function(obj, record, index){
                                                var ts = record.get('TABLE_SCHEM');
                                                tables.baseParams = {'tablespace' : ts};
                                                tables.load();
                                            },
                                            'change' : function() {
                                                Ext.getCmp('table_name').setValue('');
                                            }
                                        }
                                    }]},

                                    {items: [{
                                        xtype:'combo',
                                        enableKeyEvents:true,
                                        store : tables,
                                        fieldLabel: '表',
                                        id:'table_name',
                                        name: 'table_name',
                                        emptyText:'请选择表',
                                        displayField:'TABLE_NAME',
                                        mode: 'local',
                                        triggerAction: 'all',
                                        anchor:'80%'
                                    }]
                                    }
//                                    {items: [{xtype:'textarea',fieldLabel: '过滤条件',id: 'sql', name:'sql',anchor:'90%', value:'',
//                                        enableKeyEvents:true,
//                                        listeners : {
//                                            keypress : function(obj, e){
//                                            }
//                                        }
//                                    }
//                                    ],columnWidth:1.05}
                                ]//items
                            }
                        ],
                        buttons: [
                            {
                                text:'提交',
                                cls: 'x-icon-btn',
                                handler: function(){
                                    formSubmit();
                                }
                            }
                            ]
                    }
            );//FormPanel


            function formSubmit(){
                var table_name=Ext.getCmp('table_name').getValue();
                var table_schem=Ext.getCmp('table_schem').getValue();
                if(table_schem==''){
                    Ext.Msg.alert('提示信息',"表空间不能为空!");
                    return;
                }
                if(table_name==''){
                    Ext.Msg.alert('提示信息',"表不能为空!");
                    return;
                }
                Ext.Msg.confirm('系统提示','确定要提交吗？',function(btn){
                    if(btn=='yes'){
                        var a =searchPanel.getForm().getValues();
                        Ext.Ajax.request({
                            url: appPath + "/CreateTableAction_createTable2.action",
                            method:'post',
                            params:a ,
                            success:function(req){
                                var obj = Ext.util.JSON.decode(req.responseText);
                                if(obj.success){
                                    //Ext.getCmp('rowkey').setValue(obj.rowkey);
                                    Ext.Msg.alert('返回信息',obj.message);
                                }else{
                                    Ext.Msg.alert('错误',obj.message);
                                }
                            }
                        });
                    }
                },this);

            }

            Ext.MyViewport=Ext.extend(Ext.Viewport ,{
                xtype:"viewport",
                layout:"anchor",
                autoScroll: true,
                initComponent: function(){
                    this.items=[
                        searchPanel
                    ], Ext.MyViewport.superclass.initComponent.call(this);
                }
            });

            var viewport = new Ext.MyViewport();
            //Ext.getCmp('filepath_server').hide();

        });
    </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>