/*!
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns('My.UI');

/**
 * 	var cc = new My.UI.CheckboxGroup({ 
 *  				fieldLabel: 'Auto Layout',
 *  				name: 'obj.name',
 *  				dataUrl:'data.json'
 *  			 }); 
 *  cc.setValue('2,4,5');//设值 
 */

My.UI.CheckboxGroup=Ext.extend(Ext.form.CheckboxGroup,{ 
    columns:3, 
    dataUrl:'', //数据地址 
    labelFiled:'name', 
    valueFiled:'value', 
    setValue:function(val){ 
        if(val.split){ 
            val=val.split(', '); 
        } 
        this.reset(); 
        for(var i=0;i<val.length;i++){ 
            this.items.each(function(c){ 
                //debugger; 
                if(c.inputValue==val[i]){ 
                    c.setValue(true); 
                } 
            }); 
        } 
    }, 
    reset:function(){ 
        this.items.each(function(c){ 
            c.setValue(false); 
        }); 
    }, 
    
    getValue:function(){ 
        var val=[]; 
        this.items.each(function(c){ 
            if(c.getValue()==true) 
                val.push(c.inputValue); 
        }); 
        return val.join(', '); 
    }, 
    onRender:function(ct, position){        
       var items=[]; 
        if(!this.items){                        //如果没有指定就从URL获取 
        	var scope = this;
        	$.ajax({
        		type : 'post',
        		url : this.dataUrl,
        		async : false,
        		dataType : 'json',
        		success : function(returnData){
        			var data = returnData.data;
	        		for(var i=0;i<data.length;i++){ 
	                    var d = data[i]; 
	                    var chk = {boxLabel: d[scope.labelFiled], name: scope.name||'', inputValue: d[scope.valueFiled]}; 
	                    items.push(chk); 
	                }
        		},
        		failure:function(){
        			alert('服务器发生错误');
        		}
        	});
            this.items=items; 
        } 
        My.UI.CheckboxGroup.superclass.onRender.call(this, ct, position); 
    } 
}); 
Ext.reg('mycheckgroup',My.UI.CheckboxGroup); 