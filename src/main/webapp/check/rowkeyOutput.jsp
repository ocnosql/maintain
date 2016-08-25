<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <script>

    Ext.onReady(function(){
      var pageSize = 15;
      var gid = null;

      /* Ext.Loader.setConfig({enabled: true});
       Ext.Loader.setPath('Ext.ux', '../../examples/ux'); */

      var tablespace = new Ext.data.JsonStore({
        fields: ['TABLE_SCHEM'],
        //url : appPath + "deleteQuery!getTableSpaces.action",
        url : appPath + "/CommonAction_getTableSpaces.action",
        autoLoad : true,
        root : "root"
      });

      var tables = new Ext.data.JsonStore({
        fields: ['TABLE_NAME'],
        //url : appPath + "deleteQuery!getTables.action",
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
                items:[
                  {
                    layout:'column',labelSeparator:':',
                    title: '查询条件',
                    xtype: 'fieldset',
                    layout:'column',
                    //collapsible: true,
                    autoHeight: true,
                    defaults:{layout: 'form',border:false,columnWidth:.5},
                    items:[
                      {items: [{xtype:'textfield',enabled: true,fieldLabel: '电话号码',id: 'phonenum', name:'phonenum',anchor:'90%', value:'',
                        enableKeyEvents:true,
                        listeners : {
                          keypress : function(obj, e){
                          }
                        }
                      }]},

                      {items: [{
                        xtype:'combo',
                        fieldLabel: '表空间',
                        store : tablespace,
                        displayField:'TABLE_SCHEM',
                        mode: 'local',
                        triggerAction: 'all',
                        id: 'table_schem',
                        name:'table_schem',
                        anchor:'90%',
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
                        anchor:'90%'
                      }]
                      },
                      {items: [{xtype:'textarea',fieldLabel: '过滤条件',id: 'sql', name:'sql',anchor:'90%', value:'',
                        enableKeyEvents:true,
                        listeners : {
                          keypress : function(obj, e){
                          }
                        }
                      }
                      ],columnWidth:1.05}
                    ]//items
                  }
                ],
                buttons: [{
                  text:'查询',
                  cls: 'x-icon-btn',
                  handler: function() {
                    //检查是否输入 电话号码、表空间、表以及过滤条件。过滤条件可为空
                    if(Ext.getCmp('phonenum').getValue()==null||Ext.getCmp('phonenum').getValue()==''){
                      Ext.MessageBox.alert('提示', "请输入需要查询的手机号码!");
                      return ;
                    }
                    if(Ext.getCmp('table_schem').getValue()==null||Ext.getCmp('table_schem').getValue()==''){
                      Ext.MessageBox.alert('提示', "请选择表空间!");
                      return ;
                    }
                    if(Ext.getCmp('table_name').getValue()==null||Ext.getCmp('table_name').getValue()==''){
                      Ext.MessageBox.alert('提示', "请选择表!");
                      return ;
                    }

                    gid = null;
                    dynamicGrid.store.baseParams.gid = null;
                    var a = searchPanel.getForm().getValues();
                    var params = dynamicGrid.store.baseParams;
                    Ext.apply(params, a);
                    dynamicGrid.store.baseParams = params;
                    dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize},
                      callback: function(record, options, success){
                        if(success){
                          gid = dynamicGrid.store.reader.jsonData.extInfo.gid;
                          dynamicGrid.store.baseParams.gid = gid;
                        }
                      }});
                  }
                }, {
                  text:'导出文本',
                  cls: 'x-icon-btn',
                  handler: function(){
                    if(dynamicGrid.store.reader.jsonData==null){
                      Ext.MessageBox.alert('提示', "请先查询，再导出!");
                      return;
                    }else{
                      window.location.href = appPath + "/rowkeyDownload.action?gid="+gid;
                    }
                    //dynamicGrid.getSelectionModel().selectAll();
                  }
                },{
                  text:'导出Excel',
                  cls: 'x-icon-btn',
                  handler: function(){
                    if(dynamicGrid.store.reader.jsonData==null){
                      Ext.MessageBox.alert('提示', "请先查询，再导出!");
                      return;
                    }else{
                      window.location.href = appPath + "/rowkeyDownload_excel.action?gid="+gid;
                    }
                    //Utils.copySelectedRows(dynamicGrid);
                  }
                }]
              }
      );//FormPanel

      var dynamicGrid = new Ext.grid.DynamicGrid({
        title: '数据展示列表',
        //renderTo: 'dynamic-grid',
        storeUrl: appPath + "/RowkeyOutputQueryAction_query.action",
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

    });
  </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>