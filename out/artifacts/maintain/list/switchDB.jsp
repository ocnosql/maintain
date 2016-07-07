<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.ailk.model.redis.RedisConfigReader,com.ailk.model.redis.RedisConfig,java.util.*,org.apache.commons.lang.StringUtils,com.ailk.util.PropertiesUtil" %>    
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
var pageSize = 200;
var restUri = [];
Ext.onReady(function(){
<%
String restList = PropertiesUtil.getProperty("runtime.properties", "drquery.switchDB_zd.list");
if(StringUtils.isNotEmpty(restList)){
	String[] restArr = restList.split(",");
	for(String rest : restArr){
		out.println("restUri.push(['"+ rest + " - "+PropertiesUtil.getProperty("runtime.properties", rest) +"','"+ rest +"']);");
	}
}
%>
var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    storeUrl: 'redis!query.action?type=switchDB&t='+new Date(),  
    width : '100%',  
    height: 500,  
    rowNumberer: true,  
    //checkboxSelModel: true,  
    sm: new Ext.grid.CheckboxSelectionModel(),  
    bbar : new Ext.PagingToolbar({  
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
						defaults:{layout: 'form',border:false,columnWidth:.33},
						items:[

							{items: [{
					            xtype: 'combo',
					            //id: 'redisName',
					            fieldLabel: 'URL',
					            hiddenName: 'url',
					            hiddenValue: 'url',
					            forceSelection : true,
					            editable: false,
					            anchor:'90%',
					            store: new Ext.data.SimpleStore({
					                fields: ['text','value'],
					            	data: restUri
					            }),
					            valueField: 'value',
					            displayField: 'text',
					            typeAhead: true,
					            mode: 'local',
					            triggerAction: 'all',
					            selectOnFocus: true,
					            allowBlank: false,
					            listeners: {
					                afterRender: function(combo) {  //组件销毁之前触发
										var firstValue = combo.getStore().reader.arrayData[0][1];
										combo.setValue(firstValue);
										//alert(firstValue);
										loadData('getURL');
					                },
					                select: function(combo, record ){   //根据列表的索引数选择下拉列表中的一项
					                	loadData('getURL');
					                }
					            }
					        }]}
						]
					}
				],
			buttons: [{
				text:'刷 新',
				cls: 'x-icon-btn',
				handler: function(){
				       loadData('drf'); 
				   }
			}]
		}
);//FormPanel

function loadData(val) {
	var a = searchPanel.getForm().getValues(); 
     var params = dynamicGrid.store.baseParams; 
     params.method = val;
     Ext.apply(params,a);     //把a属性复制到params里  
     dynamicGrid.store.baseParams = params;//把params装到store的“参数盒子”里  每次HTTP请求都会带上这个参数，本来它是一个对象的形式，请求时会转化为参数
     dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} });//ajax
}

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