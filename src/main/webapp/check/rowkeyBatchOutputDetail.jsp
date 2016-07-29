<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <style type=text/css>
  </style>
  <script>
    //解析URL中参数
    function GetRequest() {
      var url = location.search; //获取url中"?"符后的字串
      var theRequest = new Object();
      if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        var strs = str.split("&");
        for(var i = 0; i < strs.length; i ++) {
          theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
        }
      }
      return theRequest;
    }

    Ext.onReady(function(){

      var Request = new Object();
      Request = GetRequest();
      var task_id = Request['task_id'];
      var dst_path = Request['dst_path'];
      var columns_str  = Request['columns_str'];
      var total_count = Request['total_count'];

      var pageSize = 20;
      var gid = null;

      /**
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
                    title: '下载',
                    xtype: 'fieldset',
                    layout:'column',
                    //collapsible: true,
                    autoHeight: true,
                    defaults:{layout: 'form',border:false,columnWidth:.5},
                    items:[
                      {items: [{xtype:'textfield',emptyText: '',fieldLabel: '下载地址',name: 'download_url'
                      }]}
                    ],
                    buttons: [{
                      text:'下载',
                      cls: 'x-icon-btn',
                      handler: function() {}
                    }]
                  }
                ]
              }
      );
      **/

      var dynamicGrid = new Ext.grid.DynamicGrid({
        title: '详细数据',
        //renderTo: 'dynamic-grid',
        storeUrl: appPath + "/RowkeyBatchDeatilQueryAction_query.action",
        width : '100%',
        height: 500,
        rowNumberer: false,
        checkboxSelModel: false,
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
            //searchPanel,
            dynamicGrid
          ], Ext.MyViewport.superclass.initComponent.call(this);
        }
      });

      var viewport = new Ext.MyViewport();

      gid = null;
      dynamicGrid.store.baseParams.gid = null;
      dynamicGrid.store.load({ params: {start: 0, limit: dynamicGrid.getBottomToolbar().pageSize,task_id:task_id,dst_path:dst_path,columns_str:columns_str,total_count:total_count},
        callback: function(record, options, success){
          if(success){
              gid = dynamicGrid.store.reader.jsonData.extInfo.gid;
              dynamicGrid.store.baseParams.gid = gid;
          }
        }});
    });
  </script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>