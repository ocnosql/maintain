<%@ page import="com.ailk.model.InterfaceCache,java.util.*"%>
<%@ page import="com.ailk.util.PropertiesUtil,org.apache.commons.lang.StringUtils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var uri = "";//查询的uri
var itemName = ""; //查询接口名 如:QUERYDISPLAYDATE_2003
var restUri = [];
<%
String restList = PropertiesUtil.getProperty("runtime.properties", "drquery.action.list");
if(StringUtils.isNotEmpty(restList)){
	String[] restArr = restList.split(",");
	for(String rest : restArr){
	    String[] addArr = PropertiesUtil.getProperty("runtime.properties", rest).split(",");
	    for(String add : addArr) {
	        out.println("restUri.push(['"+ rest + " - "+ add +"','"+ add +"']);");
	    }
	}
}
%>

var interfaceMap_xd = [];	
var interfaceMap_zd = [];  
var interArr_xd = new Array();	
var interArr_zd = new Array();		

var interArr = [];	
<%
Map interfaceMap = InterfaceCache.getInterfaceMap();
Iterator it = interfaceMap.keySet().iterator();
while(it.hasNext()){
	String interfaceName = (String)it.next();//接口名  如: 详单查询接口（2003）
	Map valueMap =  (Map)interfaceMap.get(interfaceName); 
	Iterator iter = valueMap.keySet().iterator();
	out.println("var interValue = new Array();");
	out.println("interValue.push(\""+interfaceName+"\");");
	out.println("interValue.push(\""+(String)valueMap.get("itemName")+"\");");
	while(iter.hasNext()){
		String name = (String)iter.next();//name
		String value = (String)valueMap.get(name);//value
		String itemName = (String)valueMap.get("itemName");
		//out.println("interValue[1]=\""+itemName+"\";");
		if(!"itemName".equals(name)){
			out.println("interValue.push(\""+value+"("+name+")"+"\");");	
		}
	}

	
// 	out.println("console.log(interValue);");
	out.println("interfaceMap_xd.push(['"+ interfaceName + "','" + interfaceName + "']);");
	out.println("interArr_xd.push(interValue);");
}

 interfaceMap = InterfaceCache.getInterfaceMap2();//账单
 it = interfaceMap.keySet().iterator();
while(it.hasNext()){
	String interfaceName = (String)it.next();
	Map valueMap =  (Map)interfaceMap.get(interfaceName);
	Iterator iter = valueMap.keySet().iterator();
	out.println("var interValue = new Array();");
	out.println("interValue.push(\""+interfaceName+"\");");
	out.println("interValue.push(\""+(String)valueMap.get("itemName")+"\");");
	while(iter.hasNext()){
		String name = (String)iter.next();
		String value = (String)valueMap.get(name);
		String itemName = (String)valueMap.get("itemName");
		//out.println("interValue[1]=\""+itemName+"\";");
		if(!"itemName".equals(name)){
			out.println("interValue.push(\""+value+"("+name+")"+"\");");	
		}
	}
	
// 	out.println("console.log(interValue);");
	out.println("interfaceMap_zd.push(['"+ interfaceName + "','" + interfaceName + "']);");
	out.println("interArr_zd.push(interValue);");
}
%>

var retText = new Ext.form.TextArea({
	name : 'retTextArea',
	id : 'retTextArea',
	fieldLabel :'查询结果',
	height: 250,
    anchor: '100%',
	scope : this,
	enableKeyEvents : true
});

var queryType;

var searchPanel = new Ext.FormPanel(
		{
			labelAlign:'left',
			buttonAlign:'center',
			bodyStyle:'padding:5px;',
			frame:true,
			labelWidth:90,
			monitorValid:true,
			height:330,
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
					{items: [{
						    xtype: 'combo',
						    id: 'type',
						    //listWidth :400,
 						    emptyText:'接口类型',
						    fieldLabel:'接口类型',
						    forceSelection : true,
						    editable: false,
						    anchor:'90%',
						    store: new Ext.data.SimpleStore({
						        fields: ['text','value'],
						    	data: [["详单","xd"],["账单","zd"]]
						    }),
						    valueField: 'value',
						    displayField: 'text',
						    typeAhead: true,
						    mode: 'local',
						    triggerAction: 'all',
						    selectOnFocus: true,
						    allowBlank: false,
						    listeners: {
						    	"select": function(combo, record ){
						    		Ext.getCmp('interfaceName').setValue('');
						    		var v = combo.getValue();
						    		queryType = v;
						    		if(v == 'xd'){
						    			Ext.getCmp('interfaceName').getStore().loadData(interfaceMap_xd);
						    			interArr = interArr_xd;
						    			
						    		} else {
						    			Ext.getCmp('interfaceName').getStore().loadData(interfaceMap_zd);
						    			interArr = interArr_zd;
						    		}
						    	},
						    	afterRender: function(combo) {
						    		
						    	}
						    }
						}]},
					
						{items: [{
						    xtype: 'combo',
						    id: 'interfaceName',
 						    emptyText:'请选择接口',
						    fieldLabel: '接口名称',
						    forceSelection : true,
						    editable: false,
						    anchor:'90%',
						    store: new Ext.data.SimpleStore({
						        fields: ['text','value'],
						    	data: []
						    }),
						    valueField: 'value',
						    displayField: 'text',
						    typeAhead: true,
						    mode: 'local',
						    triggerAction: 'all',
						    selectOnFocus: true,
						    allowBlank: false,
						    listeners: {
						    	"select":function(combo, record ){
						    		Ext.getCmp('paramLayout').removeAll();//清空接口参数文本域 
						    		changeValue(combo.getValue());	//重新给出接口参数
						    	},
						        afterRender: function(combo) {
									//var firstValue = combo.getStore().reader.arrayData[0][1];
									//combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
						        }
						    }
						}]},
						{columnWidth:1.05,items: [{
						    xtype: 'combo',
						    id: 'restid',
						    //listWidth :400,
 						    emptyText:'请选择测试地址',
						    fieldLabel: '测试地址',
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
						    listeners: {				//监听选择事件 
						    	"select":function(combo, record ){
						    		
						    		uri = combo.getValue();
						    	}
						    }
						}]}
					]//items
				},{
					layout:'column',labelSeparator:':',
					id: 'paramLayout',
					title: '<g3:i18n codeId="查询条件"/>',
					xtype: 'fieldset',
					layout:'column',
					labelWidth:160,
					autoHeight: true,
					defaults:{layout: 'form',border:false,columnWidth:.33},
					items:[]//items
				}
			],
			buttons: [{
				text:'提 交',
				cls: 'x-icon-btn',
				handler: function(){
				   var a = searchPanel.getForm().getValues();
				   delete a['interfaceName'];
				   delete a['restid'];
				   delete a['type'];
				   var retXml = makeQryXml(a);	 //组装查询报文   a是表单组件值
				   Ext.Ajax.request({
		                url: appPath + '/cloud-default/interface',
		                params: { uri: uri, retXml: retXml, itemName:itemName },
		                method: 'POST',
		                success: function (response, options) {
		                	var msg = eval("("+response.responseText+")");
		                	Ext.getCmp('retTextArea').setValue(msg.data);
		                },
		                failure: function (response, options) {
		                    Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
		                }
		            });
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
		    retText
		],
		Ext.MyViewport.superclass.initComponent.call(this);
	}
});

var viewport = new Ext.MyViewport();

//组装查询报文
function makeQryXml(val){
	var retXml;
	if(queryType == 'xd') {
		var str="<MYROOT><PUB_INFO><PUBINFO_OPID>10101464</PUBINFO_OPID><PUBINFO_IP>127.0.0.1</PUBINFO_IP><PUBINFO_ORGID>0</PUBINFO_ORGID>"+
		"<PUBINFO_OPPASSWORD>7c6a180b36896a0a8c02787eeafb0e4c</PUBINFO_OPPASSWORD><PUBINFO_WEBPORT>18001</PUBINFO_WEBPORT></PUB_INFO><QUEST_PARAM>";
		for(var i in val){
			var tempStr = i.split("(");
			str=str+"<"+tempStr[0]+">"+val[i]+"</"+tempStr[0]+">";
		}
		str = str +"</QUEST_PARAM></MYROOT>";
	} else {
		var str = "<REQ_PARAM><PUB_INFO><SYS_OP_ID>10010852</SYS_OP_ID><SYS_PASSWORD>7c6a180b360e4c</SYS_PASSWORD><OP_ID>10058805</OP_ID>" + 
	              "<OP_ORG_ID>10002268</OP_ORG_ID><CLIENT_IP>202.195.242.5</CLIENT_IP><MAC_ADDRESS>74-E5-0B-3B-0F-54</MAC_ADDRESS>" +
	              "<REQ_SERIAL_NO>2013051304323651</REQ_SERIAL_NO><MENU_ID>18001</MENU_ID></PUB_INFO><BUSI_INFO>";
		for(var i in val){
			var tempStr = i.split("(");
			if(tempStr[0] == 'USER_IDS') {
				str += '<USER_IDS>';
				var vals = val[i].split(',');
				for(var n = 0; n < vals.length; n ++) {
				//	str = str+"<"+tempStr[0]+">" + vals[n] + "</" + tempStr[0] + ">";
					str = str+"<USER_ID>" + vals[n] + "</USER_ID>";
				}
				str += '</USER_IDS>';
 			} else if(tempStr[0] == 'BILL_MONTHS') {
				str += '<BILL_MONTHS>';
				var vals = val[i].split(',');
				for(var n = 0; n < vals.length; n ++) {
				//	str = str+"<"+tempStr[0]+">" + vals[n] + "</" + tempStr[0] + ">";
					str = str+"<BILL_MONTH>" + vals[n] + "</BILL_MONTH>";
				}
				str += '</BILL_MONTHS>';
			} else {
				
				str = str+"<"+tempStr[0]+">" + val[i] + "</" + tempStr[0] + ">";
			} 
		}
		str = str +"</BUSI_INFO></REQ_PARAM>";
	}
	
	//alert(str);
	retXml = str;
	return retXml;
}

//选择不同的接口名，展示不同的textfield个数
function changeValue(value){
	var val = [];
	for(var i=0;i<interArr.length;i++){
		itemName = interArr[i][1];
		if(value == (interArr[i][0])){
			val = interArr[i];
			break;
		}
	}
// 	console.log("itemName:"+itemName);
// 	console.log(val);
	for(var j=2;j<val.length;j++){//从第三个开始取，第一第二都是接口名
		Ext.getCmp('paramLayout').add(
			{items: [{
					xtype: 'textfield',
				    id: val[j],
				    fieldLabel: val[j],
				    editable: false,
				    anchor:'90%'
				}
			]});
	}
	Ext.getCmp('paramLayout').doLayout();
}
});

</script>
</head>
<body>
<div id="dynamic-grid"></div>
</body>
</html>