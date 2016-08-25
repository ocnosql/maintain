<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var pageSize = 100;

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
						{items: [{xtype:'textarea',fieldLabel: 'SQL',id: 'sql', name:'sql',anchor:'90%', value:'',
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
				text:'提交',
				cls: 'x-icon-btn',
				handler: function(){
					//Ext.Msg.alert('提示', '任务提交成功', function () {
						//window.location='sqlDetail.jsp';
						queryRowkey();
					//});
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
		    searchPanel
		],
		Ext.MyViewport.superclass.initComponent.call(this);
	}
});

var viewport = new Ext.MyViewport();

function queryRowkey(){
	var sql=Ext.getCmp('sql').getValue();
	if(sql==''||sql=="undefined"){
		Ext.Msg.alert('提示信息',"不能为空!");
		return;
	}
	Ext.Msg.confirm('系统提示','确定要提交吗？',function(btn){
		if(btn=='yes'){
			Ext.Ajax.request({
				url: appPath + "/NoRowkeyQueryAction_taskSubmit.action",
				method:'post',
				params:{
				 sql: Ext.getCmp('sql').getValue()
			   },
			   success:function(req){
				  var obj = Ext.util.JSON.decode(req.responseText);
				  if(obj.success){
					  //Ext.getCmp('rowkey').setValue(obj.rowkey);
					  Ext.Msg.alert('成功',obj.message);
				  }else{
					  Ext.Msg.alert('错误',obj.message);
				  }
			   }
			 });
		}else{

		}
	},this);

}


}); 
</script>
</head>
<body>

<div id="dynamic-grid"></div>


</body>
</html>