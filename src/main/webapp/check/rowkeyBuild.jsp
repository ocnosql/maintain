<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){

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
                        title: '',
                        xtype: 'fieldset',
                        layout:'column',
                        //collapsible: true,
                        autoHeight: true,
                        defaults:{layout: 'form',border:false,columnWidth:.5},
                        items:[
                            {items: [{xtype:'textfield',fieldLabel: '电话号码',id: 'phoneNum', name:'phoneNum',anchor:'90%', value:'',
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
                    handler: function() {
                        gid = null;
                        var a = searchPanel.getForm().getValues();

                        Ext.Ajax.request({
                            url: appPath + '/RowkeyQueryAction_getmd5.action',
                            method:'post',
                            params:{
                                phoneNum: Ext.getCmp('phoneNum').getValue()
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
                }]
            }
    );//FormPanel

    var resultPanel = new Ext.FormPanel(
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
                        title: '',
                        xtype: 'fieldset',
                        layout:'column',
                        //collapsible: true,
                        autoHeight: true,
                        defaults:{layout: 'form',border:false,columnWidth:.5},
                        items:[
                            {items: [{xtype:'textfield',fieldLabel: '查询前缀',id: 'rowkey', name:'rowkey',anchor:'90%', value:'',
                                enableKeyEvents: false,disabled: false,readOnly:true,
                                        listeners : {
                                    keypress : function(obj, e){

                                    }
                                }
                            }],columnWidth:1.05}
                        ]//items
                    }
                ]
            }
    );

    Ext.MyViewport=Ext.extend(Ext.Viewport ,{
        xtype:"viewport",
        layout:"anchor",
        autoScroll: true,
        initComponent: function(){
            this.items=[
                searchPanel,
                resultPanel
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