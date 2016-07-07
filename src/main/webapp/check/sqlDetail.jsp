<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var pageSize = 3000;

var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: appPath + "/check/result.jsp?queryType=sqlDetail",  
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
						
						{items: [{xtype:'textarea', disabled: true, fieldLabel: 'sql语句',id: 'busiType', name:'busiType',anchor:'90%', 
							value:'select count(1) as total_count from dr_query20151201',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}],columnWidth:1.05}
					]//items
				}
			]
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