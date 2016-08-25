<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<%=appPath%>/js/DateTime/Spinner.css"/>
    <script type="text/javascript" src="<%=appPath%>/js/DateTime/Spinner.js"></script>
    <script type="text/javascript" src="<%=appPath%>/js/DateTime/SpinnerField.js"></script>
    <script type="text/javascript" src="<%=appPath%>/js/DateTime/DateTimeField.js"></script>
    <script>
        Ext.onReady(function(){
            var pageSize = 15;
            var dynamicGrid = new Ext.grid.DynamicGrid({
                id:'test',
                title: '数据展示列表',
                //renderTo: 'dynamic-grid',
                storeUrl:"<%=appPath%>/NoRowkeyQueryAction_queryTask.action",
                width : '100%',
                height: 500,
                rowNumberer: true,
                checkboxSelModel: true,
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
                                title: '<g3:i18n codeId="查询条件"/>',
                                xtype: 'fieldset',
                                layout:'column',
                                //collapsible: true,
                                autoHeight: true,
                                defaults:{layout: 'form',border:false,columnWidth:.5},
                                items:[
                                    {items:[{xtype:'datetimefield',fieldLabel: '开始日期',name: 'startDate',value:'',anchor:'50%'}]},
                                    {items:[{xtype:'datetimefield',fieldLabel: '结束日期',name: 'endDate',value:'',anchor:'50%'}]},
                                    {items: [{
                                        xtype: 'combo',
                                        //id: 'hashType',
                                        fieldLabel: '执行状态',
                                        hiddenName: 'status',
                                        //forceSelection : true,
                                        editable: false,
                                        anchor:'90%',
                                        store: new Ext.data.SimpleStore({
                                            fields: ['text','value'],
                                            data: [['成功','1'],['失败','2'],['执行中','0']]
                                        }),
                                        valueField: 'value',
                                        displayField: 'text',
                                        typeAhead: true,
                                        mode: 'local',
                                        triggerAction: 'all',
                                        selectOnFocus: true,
                                        //allowBlank: false,
                                            listeners: {
                                            afterRender: function(combo) {
                                                /* 		var firstValue = combo.getStore().reader.arrayData[0][1];
                                                 combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示   */
                                            }
                                        }
                                    }]}

                                ]//items
                            }
                        ],
                        buttons: [{
                            text:'查 询',
                            cls: 'x-icon-btn',
                            handler: function(){
                                var a =searchPanel.getForm().getValues();
                                var params = dynamicGrid.store.baseParams;
                                Ext.apply(params,a);
                                dynamicGrid.store.baseParams = params;
                                dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} });
                            }
                        },{
                            text: '详 情',
                            cls: 'x-icon-btn',
                            handler: function(){
                                selectClick();
                            }
                        },{
                         text: '复 位',
                         cls: 'x-icon-btn',
                         handler: function(){
                             searchPanel.getForm().reset();
                             var vals =searchPanel.getForm().getValues();
                             dynamicGrid.store.baseParams = {};
                             var params = dynamicGrid.store.baseParams;
                             Ext.apply(params, vals);
                             dynamicGrid.store.baseParams = params;
                             dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} });
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

            function selectClick(){
                if (dynamicGrid.getSelectionModel().hasSelection()){
                    var row = Ext.getCmp("test").getSelectionModel().getSelections();
//                debugger;
//                alert(row[0].json.C0);
                    var taskId=row[0].json.C0;
                    var status=row[0].json.C1;
                    if(status=='已完成'){
                        window.location=appPath+'/check/norowkeyQuery.jsp?taskId='+taskId;
                    }else{
                        alert('只能操作已完成的任务!');
                    }
                }else{
                    alert('请选中要操作的记录!');
                }
            }

            dynamicGrid.store.load({ params: { start: 0,
                limit: dynamicGrid.getBottomToolbar().pageSize
            }
            });


        });
    </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>