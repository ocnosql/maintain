Ext.grid.DynamicGrid = Ext.extend(Ext.grid.GridPanel, {  
    initComponent: function() {  
        //创建store  
        var ds = new Ext.data.Store({  
            url: this.storeUrl,  
            reader: new Ext.data.JsonReader()  
        });  
          
        //设置默认配置  
        var config = {  
            /*viewConfig: {  
                //forceFit: true ,
            }, */ 
            enableColLock: false,  
            loadMask: true,  
            border: true,  
            stripeRows: true,  
            ds: ds,  
            columns: []  
        };  
          
        //给分页PagingToolbar绑定store  
        this.bbar.bindStore(ds, true);  
          
        Ext.apply(this, config);  
        Ext.apply(this.initialConfig, config);  
        Ext.grid.DynamicGrid.superclass.initComponent.apply(this, arguments);  
    },  
      
    onRender: function(ct, position) {  
        this.colModel.defaultSortable = true;  
        Ext.grid.DynamicGrid.superclass.onRender.call(this, ct, position);  
  
        this.store.on('load', function() {  
        	this.el.mask('Loading...'); 
        	if(this.store.reader.jsonData.message != undefined){
        		alert('查询异常：' + this.store.reader.jsonData.message);
        	}
            if (typeof(this.store.reader.jsonData.columns) === 'object') {  
                var columns = [];  
                  
                if (this.rowNumberer) {  
                    columns.push(new Ext.grid.RowNumberer({width:40}));  
                }  
                  
                if (this.checkboxSelModel) {  
                    columns.push(new Ext.grid.CheckboxSelectionModel({singleSelect : false}));  
                }  
                  
                Ext.each(this.store.reader.jsonData.columns,   
                    function(column) {  
                        columns.push(column);  
                    }  
                );  
                      
                this.getColumnModel().setConfig(columns);  
            }  
            this.el.unmask(); 
        }, this); 

        this.store.on('loadexception', function(proxy, o, response) {
        	if(this.reader.jsonData.message != undefined){
        		alert('查询异常：' + this.reader.jsonData.message);
        	}else{
        		alert('查询异常');
        	}
        });
        //this.store.load({ params: { start: 0, limit: this.getBottomToolbar().pageSize}});  
    }  
}); 