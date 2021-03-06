<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<%@ include file="common/head.jsp"%>
<script><!--
var mainPanel;
Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = '<%=appPath%>/css/lib/images/s.gif';


MainPanel = function(){	
	MainPanel.superclass.constructor.call(this, {
		id:'doc-body',
		region:'center',
		margins:'0 5 5 0',
		resizeTabs: true,
		minTabWidth: 135,
		tabWidth: 135,
		plugins: new Ext.ux.TabCloseMenu(),
		enableTabScroll: true,
		activeTab: 0
		
		/*items: {
			id:'首页',
			title: '首页',
			iconCls:'icon-docs',
			html:'<iframe id="index_frame" src="<%=appPath%>/menu!menuList.action" frameborder="0" scrolling="auto" onload="iframeOnLoad(this.id)" style="border:0px none;width:100%;height:100%"></iframe>',
			autoScroll: true
		}*/
	
	});
};

Ext.extend(MainPanel, Ext.TabPanel, {

	initEvents : function(){
		MainPanel.superclass.initEvents.call(this);
		this.body.on('click', this.onClick, this);
	},

	onClick: function(e, target){

	}

});


mainPanel = new MainPanel();

var treePanel = new Ext.tree.TreePanel({
	//title: '我的任务',
	//autoScroll: true,
	//enableDD: true,
	//iconCls: 'x-panel-header-mytask',
	rootVisible:true,
	loader: new Ext.tree.TreeLoader({   
        <%--dataUrl:'<%=appPath%>/menu/menuList.action'--%>
        dataUrl:'<%=appPath%>/MenuAction_query.action'
    }),   
    root: new Ext.tree.AsyncTreeNode({   
        id:'-1',text:'云详单'   
    })

	
});

var viewport = new Ext.Viewport({  
    layout:'border',  
    items:[  
           /*{  
               region:'north',  
               contentEl: 'rightDiv',  
               split:true, 
               frame:true,
               border:true,
               margins:'0 0 0 0',  
               collapsible : false,  
               titlebar: false,  
               animate: true,
               height:20,
           },*/
        mainPanel,
        {  
            region:'west',  
            //contentEl: 'leftDiv',  
            split:true,  
            frame:true,
            border:true,
            collapsible:true,
			autoScroll:true,
            width: 200,  
            title: '菜单',
            items: [treePanel],
                                //height: 500,  
            //minSize: 500,  
            //maxSize: 500,  
            //margins:'0 0 0 0',  
                                titlebar: true
        }]  
});

    Ext.QuickTips.init();
    viewport.doLayout();
	var rootNode = treePanel.getRootNode();
    rootNode.expand(true);
	var isClickedTableschem = false;
	treePanel.on('click', function(node, e){
		if(node.attributes.murl=="tableschem"){
			if(isClickedTableschem) return;
			isClickedTableschem = true;
			node.removeAll();
			//获取所有的表空间
			Ext.Ajax.request({
				url: appPath + "/MenuAction_getTableSchemTree.action?id="+node.id,
				method: 'GET',
				success: function (response, options) {
					var obj = Ext.util.JSON.decode(response.responseText);
					//Ext.MessageBox.alert('成功', response+'从服务端获取结果: ' + response.responseText+"  "+obj.length);
					for(var i=0;obj!=undefined&&obj!=null&&i<obj.length;i++){
						var temp = obj[i];
						for(var j=0;temp!=undefined&&temp!=null&&j<temp.children.length;j++){
							node.appendChild(temp.children);
						}
					}
					node.render();
					node.expand(true);
					treePanel.render();
					isClickedTableschem = false;
				},
				failure: function (response, options) {
					//Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
					isClickedTableschem = false;
				}
			});
		}
         if(node.isLeaf()){
			onItemCheck(node.attributes, false);
         }
    });
	var hasExpanded = false;
	rootNode.on('expand', function(node){
		if(!hasExpanded){ 
			onItemCheck(treePanel.getRootNode().childNodes[0].attributes, true);
			hasExpanded = true;
		}
    });

});
function onItemCheck(item, flush){
	url = '<iframe id="' + item.text + '" src="'+item.murl+'" frameborder="0" scrolling="auto" style="border:0px none;width:100%;" height="100%"></iframe>';
//	flush = true;
	if(flush==true){
		mainPanel.remove(new Ext.Panel({id:item.text+"tab"}));
	}
	
	var p = mainPanel.add(new Ext.Panel({
//		id:item.text+"tab",
        id: Utils.uuid(),
		title:item.text,
		closable: true,
		autoScroll:true,
		html: url
	}));

	mainPanel.setActiveTab(p);
}
--></script>
</head>
<body>
</body>
</html>