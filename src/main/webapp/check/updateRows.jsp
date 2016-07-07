<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script>
Ext.onReady(function(){
var pageSize = 3000;




//准备数据  
/* var data = [  
    {id:1,date:new Date(),name:"吕鹏",address:"中华人民共和国",bank:"中国光大银行",isFast:true},  
    {id:2,date:new Date(),name:"张三",address:"中华人民共和国",bank:"中国农业银行",isFast:false},  
    {id:3,date:new Date(),name:"李四",address:"中华人民共和国",bank:"中国商业银行",isFast:false},  
    {id:4,date:new Date(),name:"王五",address:"中华人民共和国",bank:"中国招商银行",isFast:false}  
]; 
 */
 var data = [  
             {fieldName: 'fee1', fieldValue: '5.8'},  
             {fieldName: 'fee2', fieldValue: '10.5'}
             
         ]; 
//Proxy  
var proxy = new Ext.data.MemoryProxy(data);  
//描述数据结构  
/* var Order = Ext.data.Record.create(  
    [  
        {name:"ID",type:"int",mapping:"id"},//编号  
        {name:"DATE",type:"date",mapping:"date"},//日期  
        {name:"NAME",type:"string",mapping:"name"},//姓名  
        {name:"ADDRESS",type:"string",mapping:"address"},//地址  
        {name:"BANK",type:"string",mapping:"bank"},//银行  
        {name:"ISFAST",type:"boolean",mapping:"isFast"}//银行  
    ]  
);   */
var Order = Ext.data.Record.create(  
	    [  
	        {name:"fieldName",type:"string",mapping:"fieldName"},
	        {name:"fieldValue",type:"string",mapping:"fieldValue"}
	    ]  
	); 

//Reader  
var reader = new Ext.data.JsonReader({},Order);  
//Store  列模型中的dataIndex必须和Human结构中的name属性保持一致  
var store = new Ext.data.Store({  
    proxy:proxy,  
    reader:reader,  
    autoLoad:true,  
    pruneModifiedRecords:true  
});  
  
//交易银行  
var banks = [  
    ["中国光大银行","中国光大银行"],  
    ["中国农业银行","中国农业银行"],  
    ["中国商业银行","中国商业银行"],  
    ["中国招商银行","中国招商银行"]  
]  
  
//列模型  
/* var cm = new Ext.grid.ColumnModel([  
    {header:"订单编号",dataIndex:"ID",width:60,editor:new Ext.grid.GridEditor(new Ext.form.NumberField({allowBlank:false}))},  
    {header:"下单日期",dataIndex:"DATE",width:140,renderer:new Ext.util.Format.dateRenderer("Y-m-d"),editor:new Ext.grid.GridEditor(new Ext.form.DateField({  
        format:"Y-m-d"  
    }))},  
    {header:"收货人姓名",dataIndex:"NAME",width:120,editor:new Ext.grid.GridEditor(new Ext.form.TextField())},  
    {header:"收货人地址",dataIndex:"ADDRESS",editor:new Ext.grid.GridEditor(new Ext.form.TextField())},  
    {header:"交易银行",dataIndex:"BANK",width:120,editor:new Ext.grid.GridEditor(new Ext.form.ComboBox({  
        store:new Ext.data.SimpleStore({  
            fields:["value","text"],  
            data:banks  
        }),  
        displayField:"text",  
        valueField:"value",  
        mode:"local",  
        triggerAction:"all",  
        readOnly:true,  
        emptyText:"请选择银行"  
          
    }))},  
    {header:"快递",dataIndex:"ISFAST",width:70,renderer:function(v){return v?"快递":"非快递"}
    ,editor:new Ext.grid.GridEditor(new Ext.form.Checkbox())
    }  
]);  */ 

var cm = new Ext.grid.ColumnModel([  
                                   {header:"字段名",dataIndex:"fieldName",width:120,editor:new Ext.grid.GridEditor(new Ext.form.TextField())},  
                                   {header:"字段值",dataIndex:"fieldValue",editor:new Ext.grid.GridEditor(new Ext.form.TextField())}, 
                               ]); 
//EditorGridPanel定义  
var grid = new Ext.grid.EditorGridPanel({  
	title: '设置更新字段', 
    store:store,  
    cm:cm,  
    //autoExpandColumn:"ADDRESS",  
    //width:400,  
    anchor:'90%',
    height: 180, 
    //autoHeight:true,  
    //renderTo:"a",  
    autoEncode:true, //提交时是否自动转码  
    tbar:[{  
        text:"添加字段",  
        cls:"x-btn-text-icon",  
        handler:function(){  
/*             var initValue = {  
                    ID:"",  
                    DATE:new Date(),  
                    NAME:"",  
                    ADDRESS:"",  
                    BANK:"",  
                    ISFAST:false  
                };  */ 
            var initValue = {  
                    fieldName:'',  
                    fieldValue:'',   
                };       
            var order = new Order(initValue);  
            grid.stopEditing();  
            grid.getStore().add(order);  
              
            //设置脏数据  
            order.dirty = true;  
            //只要一个字段值被修改了，该行的所有值都设置为脏读标记  
            order.modified = initValue;  
            if(grid.getStore().modified.indexOf(order) == -1){  
                grid.getStore().modified.push(order);  
            }  
        }  
          
    },{  
        text:"删除字段",  
        cls:"x-btn-text-icon",  
        handler:function(){  
            var sm = grid.getSelectionModel();  
            if(sm.hasSelection()){  
/*                 Ext.Msg.confirm("提示","真的要删除选中的行吗？",function(btn){  
                    if(btn == "yes"){  
                        var cellIndex = sm.getSelectedCell();//获取被选择的单元格的行和列索引  
                        var record = grid.getStore().getAt(cellIndex[0]);  
                        grid.getStore().remove(record);  
                    }  
                });  */
                var cellIndex = sm.getSelectedCell();//获取被选择的单元格的行和列索引  
                var record = grid.getStore().getAt(cellIndex[0]);  
                grid.getStore().remove(record);  
            }else{  
                Ext.Msg.alert("错误","请先选择删除的行，谢谢");  
            }  
        }  
    }/* ,"-",{  
        text:"保存",  
//      icon:"",  
        cls:"x-btn-text-icon",  
        handler:function(){  
            var store = grid.getStore();  
            //得到修改过的Recored的集合  
            var modified = store.modified.slice(0);  
            //将数据放到另外一个数组中  
            var jsonArray = [];  
            Ext.each(modified,function(m){  
                //alert(m.data.ADDRESS);//读取当前被修改的记录的地址  
                //m.data中保存的是当前Recored的所有字段的值json，不包含结构信息  
                jsonArray.push(m.data);  
            });  
              
            var r = checkBlank(modified);  
            if(!r){  
                return;  
            }else{  
                //通过ajax请求将修改的记录发送到服务器最终影响数据库  
                Ext.Ajax.request({  
                    method:"post",//最好不要用get请求  
                    url:"../../../editGridServlet",  
                    success:function(response,config){  
                        var json = Ext.util.JSON.decode(response.responseText);  
                        //grid.getStore().reload();  
                        Ext.Msg.alert("提交成功",json.msg);  
                    },  
                    params:{data:Ext.util.JSON.encode(jsonArray)}  
                      
                });  
            }  
        }  
    } */]  
});  
//删除一行时，同步数据库  
grid.getStore().on("remove",function(s,record,index){  
    var jsonArray = [record,data];//因为servlet只处理数组，所以先变成数组  
    if(record.data.ID != ""){  
        Ext.Ajax.request({  
            method:"post",  
            url:"../../../editGridServlet",  
            params:{data:Ext.util.JSON.encode(jsonArray),action:"delete"},  
            success:function(response,config){  
                var msg = Ext.util.JSON.decode(response.responseText);  
                //grid.getStore().reload();  
                Ext.Msg.alert("",msg.msg);  
            }  
        });  
    }  
});  
  
//验证是否输入的数据是有效的  
var checkBlank = function(modified/*所有编辑过的和新增加的Record*/){  
    var result = true;  
    Ext.each(modified,function(record){  
        var keys = record.fields.keys;//获取Record的所有名称  
        Ext.each(keys,function(name){  
            //根据名称获取相应的值  
            var value = record.data[name];  
            //找出指定名称所在的列索引  
            var colIndex = cm.findColumnIndex(name);  
            //var rowIndex = grid.getStore().indexOfId(record.id);  
              
            //根据行列索引找出组件编辑器  
            var editor = cm.getCellEditor(colIndex).field;  
            //验证是否合法  
            var r = editor.validateValue(value);  
            if(!r){  
                Ext.MessageBox.alert("验证","对不起，您输入的数据非法");  
                result = false;  
                return;  
            }  
              
        });  
    });  
    return result;  
}  



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
					title: '删除条件',
					xtype: 'fieldset',
					layout:'column',
					//collapsible: true,
					autoHeight: true,
					defaults:{layout: 'form',border:false,columnWidth:.5},
					items:[
						{items: [{xtype:'textfield',fieldLabel: '开始日期',id: 'billId', name:'billId',anchor:'90%', value:'20151201',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						{items: [{xtype:'textfield',enableKeyEvents:true,fieldLabel: '结束日期',name: 'startDate',value:'20151202',anchor:'90%',
								listeners : {
									keypress : function(obj, e){
										
									}
								}}]},

						        
						{items: [{xtype:'textfield',fieldLabel: '用户号码',id: 'usernumbers', name:'usernumbers',anchor:'90%', value:'18601134210,17606712167',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]},
						/* {items: [{
				            xtype: 'combo',
				            //id: 'hashType',
				            fieldLabel: '过滤条件',
				            hiddenName: 'hashType',
				            forceSelection : true,
				            editable: false,
				            anchor:'90%',
				            store: new Ext.data.SimpleStore({
				                fields: ['text','value'],
				                data: [['所有业务类型','all'],['指定业务类型','some']]
				            }),
				            valueField: 'value',
				            displayField: 'text',
				            typeAhead: true,
				            mode: 'local',
				            triggerAction: 'all',
				            selectOnFocus: true,
				            allowBlank: false,
				            listeners: {  
				                afterRender: function(combo) {  
									var firstValue = combo.getStore().reader.arrayData[0][1];  
									combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示  
				                }    
				            } 
				        }]}, */
				        
						{items: [{xtype:'textarea',fieldLabel: '过滤条件',id: 'busiType', name:'busiType',anchor:'90%', value:'CALL_TYPE =\'1\' AND BILL_MONTH=\'201512\'',
							enableKeyEvents:true,
							listeners : {
								keypress : function(obj, e){
									
								}
							}
							}]}
						,{items: [grid], columnWidth:1.055, anchor:'90%'}
					]//items
				}
			],
			buttons: [{
				text:'查询',
				cls: 'x-icon-btn',
				handler: function(){
				   var a =searchPanel.getForm().getValues(); 
	               var params = dynamicGrid.store.baseParams; 
	               Ext.apply(params,a); 
	               dynamicGrid.store.baseParams = params; 
	               dynamicGrid.store.load({ params: { start: 0, limit: dynamicGrid.getBottomToolbar().pageSize} }); 
				}
			}, {
				text:'修改',
				cls: 'x-icon-btn',
				handler: function(){
					Ext.Msg.alert('提示', '修改成功', function () {
					});
				}
			}]
		}
);//FormPanel

var dynamicGrid = new Ext.grid.DynamicGrid({  
    title: '数据展示列表',  
    //renderTo: 'dynamic-grid',  
    storeUrl: appPath + "/check/result.jsp?queryType=rowkeyQuery",  
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

function queryRowkey(){
	Ext.Ajax.request({
	    url: appPath + '/redis!queryRowkey.action',
	    method:'post',
	    params:{
	     billId: Ext.getCmp('billId').getValue()
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


}); 
</script>
</head>
<body>
<div id="dynamic-grid"></div>
<div id="a"></div>
</body>
</html>