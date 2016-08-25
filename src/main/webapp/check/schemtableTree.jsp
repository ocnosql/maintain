<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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

    var pageSize = 15;
    var gid = null;
    var Request = new Object();
    Request = GetRequest();
    var tablename = Request['tablename'];
    var tableschem = Request['tableschem'];

    var dynamicGrid = new Ext.grid.DynamicGrid({
        title: '表信息列表',
        storeUrl: appPath + "/SchemtableQueryAction_query.action",
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
                dynamicGrid
            ], Ext.MyViewport.superclass.initComponent.call(this);
        }
    });

    var viewport = new Ext.MyViewport();

    //直接进行查询动作
    gid = null;
    dynamicGrid.store.baseParams.gid = null;
    var a = {tablename:tablename,tableschem:tableschem};
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

}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>