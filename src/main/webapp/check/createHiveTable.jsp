<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <script>
        /*!
         * Ext JS Library 3.4.0
         * Copyright(c) 2006-2011 Sencha Inc.
         * licensing@sencha.com
         * http://www.sencha.com/license
         */
        Ext.onReady(function(){
            var c_index=0;
            // 添加按钮
            var newDept_action = new Ext.Action({
                cls: 'x-btn-text-icon bmenu',
//                icon: 'icon-add',
                text: '添加新的一列++',
                handler: function(){
                    id = id + 1;
                    c_index=c_index+1;
                    //添加新的fieldSet
                    var org_fieldSet = new Ext.Panel({
                        //column布局控件开始
                        id: 'org_fieldSet_' + c_index,
                        layout: 'column',
                        border: false,
                        items: [//组件开始
                            {
                                columnWidth: .2,
                                layout: 'form',
                                border: false,
                                items: [{
                                    //为空
                                    blankText: '列名不能为空',
                                    emptyText: '',
                                    editable: false,
                                    triggerAction: 'all',
                                    allowBlank: false,
                                    //为空
                                    xtype: 'textfield',
                                    fieldLabel: '列名',
                                    id: 'org_field_colName_' + c_index,
                                    name: 'org_field_colName_' + c_index,
                                    anchor: '90%'
                                }]
                            } //组件结束
                            , //按钮开始
                            {
                                columnWidth: .2,
                                layout: 'form',
                                border: false,
                                items: [{

                                    xtype: 'button',
                                    text: '删除',
                                    value: c_index,
                                    scope: this,
                                    handler: function(obj){
                                        var del_id = obj.value;
                                        //var field_1 = Ext.getCmp('org_field_orgName_' + del_id);
                                        var fieldSet_1 = Ext.getCmp('org_fieldSet_' + del_id);
                                        //删除一行
                                        simple.remove(fieldSet_1, true);

                                    }
                                }]
                            } //按钮结束
                        ]

                        //column布局控件结束
                    });
                    //添加fieldSet
                    simple.add(org_fieldSet);
                    //重新剧新
                    simple.doLayout();
                },
                iconCls: 'blist'
            });
            var first_Org_fieldSet = new Ext.Panel({

                //column布局控件开始

                id: 'org_fieldSet_' + id,
                layout: 'column',
                border: false,
                items: [//组件开始
//                    {
//                        columnWidth: .2,
//                        layout: 'form',
//                        border: false,
//                        items: [{
//                            //为空
//                            blankText: '列名不能为空',
//                            emptyText: '',
//
//                            editable: false,
//                            triggerAction: 'all',
//                            allowBlank: false,
//                            //为空
//
//                            xtype: 'textfield',
//                            fieldLabel: '列名',
//                            id: 'org_field_orgName_' + id,
//                            name: 'org_field_orgName_' + id,
//                            anchor: '90%'
//
//                        }]
//                    } //组件结束
//                    ,
                    {
                        columnWidth: .2,
                        layout: 'form',
                        border: false,
                        items: [{
                            //为空
                            blankText: '表的名称',
                            emptyText: '',
                            editable: false,
                            triggerAction: 'all',
                            allowBlank: false,
                            xtype: 'textfield',
                            fieldLabel: '表的名称',
                            id: 'org_field_tableName',
                            anchor: '90%'
                        }]
                    } //组件结束
//                    ,
//                    {
//                        columnWidth: .2,
//                        layout: 'form',
//                        border: false,
//                        items: [{
//                            //为空
//                            blankText: '分区名称不能为空',
//                            emptyText: '',
//
//                            editable: false,
//                            triggerAction: 'all',
//                            allowBlank: false,
//                            //为空
//
//                            xtype: 'textfield',
//                            fieldLabel: '分区名称',
//                            id: 'org_field_partition',
//                            anchor: '90%'
//                        }]
//                    } //组件结束
                    ,
                    {
                        columnWidth: .2,
                        layout: 'form',
                        border: false,
                        items: [{
                            //为空
                            blankText: '列名不能为空',
                            emptyText: '',

                            editable: false,
                            triggerAction: 'all',
                            allowBlank: false,
                            //为空

                            xtype: 'textfield',
                            fieldLabel: '列名',
                            id: 'org_field_colName_' + c_index,
                            name: 'org_field_colName_' + c_index,
                            anchor: '90%'

                        }]
                    } //组件结束
                ]

                //column布局控件结束
            });
            //定义表单
            var simple = new Ext.FormPanel({
                labelAlign: 'left',
                title: '创建表',
                buttonAlign: 'right',
                bodyStyle: 'padding:5px',
                //width: 600,
                autoHeight: true,
                autoWidth: true,
                //
                frame: true,
                labelWidth: 80,
                // items: [ ]        ,
                buttons: [{
                    text: '保存',
                    type: 'submit',
                    //定义表单提交事件
                    handler: function(){
                        if (simple.form.isValid()) {//验证合法后使用加载进度条
                            Ext.MessageBox.show({
                                title: '请稍等',
                                msg: '正在加载...',
                                progressText: '',
                                width: 300,
                                progress: true,
                                closable: false,
                                animEl: 'loding'
                            });
                            //控制进度速度
                            var f = function(v){
                                return function(){
                                    var i = v / 11;
                                    Ext.MessageBox.updateProgress(i, '');
                                };
                            };
                            for (var i = 1; i < 13; i++) {
                                setTimeout(f(i), i * 150);
                            }
                            var a =simple.getForm().getValues();
                            //提交到服务器操作
                            simple.form.doAction('submit', {
                                url: '<%=appPath%>/CreateTableAction_createTable.action',//文件路径
                                method: 'post',//提交方法post或get
                                params: a,
                                //提交成功的回调函数
                                success: function(form, action){
                                    Ext.Msg.alert('返回信息', action.result.message);
//                                    if (action.result.success == true) {
//
//                                        Ext.MessageBox.show({
//                                            title: '系统提示信息',
//                                            msg: '添加成功!',
//                                            buttons: Ext.MessageBox.OK,
//                                            icon: Ext.MessageBox.INFO,
//                                            fn: function(btn, text){
//
//                                            }
//                                        });
//                                    }
//                                    else if(action.result.success == false){
//                                        Ext.Msg.alert('添加错误', action.result.message);
//                                    }
                                }
                                ,
                                //提交失败的回调函数
                                failure: function(){
                                   Ext.Msg.alert('错误', '服务器出现错误请稍后再试！');
                                }
                            });
                        }
                    }
                }, {
                    text: '重置',
                    handler: function(){
                        simple.form.reset();
                    }//重置表单
                }, {
                    text: '取消',
                    handler: function(){
                        win.close();
                    }//重置表单
                }]
            });
            //添加第一个fieldSet
            simple.add(first_Org_fieldSet);
            //菜单面板
            var panel = new Ext.Panel({
                bodyStyle: 'width:100%',
                autoWidth: true,
                autoHeight: true,
                //autoScroll: true,
                renderTo: Ext.getBody(),
                //

                title: '',
                bodyStyle: 'padding:10px;',
                tbar: [{
                    xtype: 'tbseparator'
                }, newDept_action, { // <-- Add the action directly to a toolbar
                    xtype: 'tbseparator'
                }],
                items: [simple]

            });

            // return panel;
            simple.render(document.body);

        });
    </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>