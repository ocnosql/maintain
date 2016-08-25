<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <script type="text/javascript" src="<%=appPath%>/js/lib/ext/FileUploadField.js"></script>
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

      /* Ext.Loader.setConfig({enabled: true});
       Ext.Loader.setPath('Ext.ux', '../../examples/ux'); */

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
                    title: '查询条件',
                    xtype: 'fieldset',
                    layout:'column',
                    //collapsible: true,
                    autoHeight: true,
                    defaults:{layout: 'form',border:false,columnWidth:.5},
                    items:[

                      {items: [{
                        xtype:'textfield',
                        emptyText: '',
                        inputType: 'file',
                        fieldLabel: '号码文件',
                        id:'uploadfileid',
                        name: 'uploadfileid'
                      }]},

                      {buttons:[
                        {
                          text:'上传',
                          id:"upload_button",
                          cls: 'x-icon-btn',
                          handler: function() {

                            searchPanel.getForm().submit({//客户端的数据提交给服务器
                              url:appPath + "/RowkeyBatchOutputQueryAction_upload.action",
                              waitTitle:"Please wait",
                              waitMsg:"Uploading...",
                              //如果submit失敗，執行這一個function
                              failure:function(form1,action){
                                Ext.MessageBox.hide();
                                Ext.MessageBox.alert('提示',"没有生成任务，请确定已经选择文件");
                              },
                              success: function(form1,action){
                                if(action.result==undefined){
                                  Ext.MessageBox.hide();
                                  Ext.MessageBox.alert('提示',"上传失败");
                                  Ext.getCmp('filepath_server').setValue("");
                                }else{
                                  Ext.MessageBox.hide();
                                  Ext.MessageBox.alert('提示',"上传成功");
                                  Ext.getCmp('filepath_server').setValue(action.result.extInfo);
                                }
                              }
                            })
                          }
                        }
                      ]
                      },

                      {items: [{
                        xtype:'textfield',
                        fieldLabel: '文件地址',
                        id: 'filepath_server',
                        name:'filepath_server',
                        anchor:'90%',
                        value:'',
                        enableKeyEvents:true,
                        readOnly: true
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
                buttons: [
                  {
                    text:'查询任务',
                    id:"query_button",
                    cls: 'x-icon-btn',
                    listeners:{
                      click:function(){
                        var a = searchPanel.getForm().getValues();
                        var params = dynamicGrid.store.baseParams;
                        Ext.apply(params, a);
                        dynamicGrid.store.baseParams = params;
                        dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize},
                          callback: function(record, options, success){
                            if(success){
                            }
                          }});
                      }
                    }
                  },
                  {
                    text:'生成任务',
                    cls: 'x-icon-btn',
                    handler: function() {
                      if(Ext.getCmp('uploadfileid').getValue()==null||Ext.getCmp('uploadfileid').getValue()==''){
                        Ext.MessageBox.alert('提示', "请选择批量处理文件!");
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
                      if(Ext.getCmp('filepath_server').getValue()==null||Ext.getCmp('filepath_server').getValue()==''){
                        Ext.MessageBox.alert('提示', "请先上传文件!");
                        return ;
                      }

                      Ext.Msg.confirm('提示','确定生成任务？',
                              function(btn){
                                if(btn=='yes'){
                                  searchPanel.getForm().submit({//客户端的数据提交给服务器
                                    waitTitle:"请稍候",
                                    waitMsg:"正在提交表单数据，请稍候。。。。。。",
                                    //如果submit失敗，執行這一個function
                                    failure:function(form1,action){
                                      Ext.MessageBox.hide();
                                      Ext.MessageBox.alert('提示',"没有生成任务，请确定已经选择文件");
                                      Ext.getCmp('query_button').fireEvent('click');//刷新
                                    },
                                    success: function(form1,action){
                                      Ext.MessageBox.hide();
                                      Ext.MessageBox.alert('',"生成任务成功");
                                      Ext.getCmp('query_button').fireEvent('click');//刷新
                                    }
                                  })
                                }else{}
                              },this);

                      //searchPanel..setParameter("start",0);
                      //searchPanel.getForm().setParameter("limit",dynamicGrid.getBottomToolbar().pageSize);
                    }
                  },
                  {
                    text:'详情',
                    cls: 'x-icon-btn',
                    handler: function(){
                      //dynamicGrid.getSelectionModel().selectAll();
                      selectClick();
                    }
                  },
                  {
                    text:'导出',
                    cls: 'x-icon-btn',
                    handler: function(){
                      var row = dynamicGrid.getSelectionModel().getSelections();
                      if(row[0].json.C3=='失败'){
                        Ext.MessageBox.alert('提示', '该任务已经执行失败，请选择其他任务!');
                      }else if(row[0].json.C3=='进行中'){
                        Ext.MessageBox.alert('提示', '该任务正在进行中，请稍候再试!');
                        Ext.getCmp('query_button').fireEvent('click');//刷新
                      }else{
                        var dst_path = row[0].json.C4;
                        //window.location.href = appPath + "/RowkeyBatchOutputQueryAction_download.action?dst_path="+dst_path;
                        window.location.href = appPath + "/batchDownload.action?dst_path="+dst_path;
                      }
                    }
                  }]
              }
      );//FormPanel

      function selectClick(){
        if (dynamicGrid.getSelectionModel().hasSelection()){
          var row = dynamicGrid.getSelectionModel().getSelections();
          if(row[0].json.C3=='失败'){
            Ext.MessageBox.alert('提示', '该任务已经执行失败，请选择其他任务!');
          }else{
            var task_id = row[0].json.C0;
            var dst_path = row[0].json.C4;
            var columns_str =  row[0].json.C5;
            var total_count =  row[0].json.C6;
            //window.location=appPath+'/check/rowkeyBatchOutputDetail.jsp?task_id='+task_id+'&dst_path='+dst_path+'&columns_str='+columns_str+'&total_count='+total_count;
            var url = appPath+'/check/rowkeyBatchOutputDetail.jsp?task_id='+task_id+'&dst_path='+dst_path+'&columns_str='+columns_str+'&total_count='+total_count;
            var temp = {"text":"主键导出(批量)-详情","murl":url};
            window.parent.onItemCheck(temp,true);
          }
        }else{
          Ext.MessageBox.alert('提示', '请选中要操作的记录!');
        }
      }

      var dynamicGrid = new Ext.grid.DynamicGrid({
        title: '任务列表',
        //renderTo: 'dynamic-grid',
        storeUrl: appPath + "/RowkeyBatchOutputQueryAction_query.action",
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

      Ext.MyViewport=Ext.extend(Ext.Viewport ,{
        xtype:"viewport",
        layout:"anchor",
        autoScroll: true,
        initComponent: function(){
          this.items=[
            searchPanel,
            dynamicGrid
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