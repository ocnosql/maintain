var __static_searchfield = null;

/*!
 * G3Cloud Ext JS Library 1.0.0
 * Copyright(c) 2006-2010 G3Cloud Inc.
 * suntaoathome@salesforcesoution.com
 * http://www.salesforcesolution.com/
 */
Ext.form.SearchField = Ext.extend(Ext.form.TriggerField, {

    /**
     * @cfg {String} triggerClass An additional CSS class used to style the trigger button.  The trigger will always
     * get the class <tt>'x-form-trigger'</tt> and <tt>triggerClass</tt> will be <b>appended</b> if specified
     * (defaults to <tt>'x-form-arrow-trigger'</tt> which displays a downward arrow icon).
     */
    triggerClass : 'x-form-search-trigger',
    
    /**
     * 选择后回调函数
     */
    callBackHandler : null,
    
    editable : false,
    
    hiddenField : null,
    
    hiddenName : null,
    
    hiddenValue : null,
    
    hiddenId : null,
    
    
    /**
     * 查找框路径
     */    
    searchUrl : null,
    
    searchWindow : null,

    // private
    initComponent : function(){
        Ext.form.SearchField.superclass.initComponent.call(this);
    },
    
    onRender : function(ct, position){
    	Ext.form.SearchField.superclass.onRender.call(this, ct, position);
        if(!this.hiddenName){
        	this.hiddenName = this.name;
        }
       	this.hiddenField = this.el.insertSibling(
       			{
       				tag:'input', 
       				type:'hidden', 
       				name: this.hiddenName,
       				value: this.hiddenValue,
               		id: (this.hiddenId || Ext.id())
               	}, 'before', true);
    },


    /**
     * @method onTriggerClick
     * @hide
     */
    // private
    // Implements the default empty TriggerField.onTriggerClick function
    onTriggerClick : function(){
    	if(Ext.isIE){
    		this.searchWindow = new Ext.Window(
    				{
    					title:"查找", 
    				    autoDestroy :false,
    				    autoHeight:true,
    					autoScroll:true,
    					width:700,
    					html:'<iframe id="index_frame" src="' + appPath + this.searchUrl + '" frameborder="0" scrolling="auto"  style="border:0px none;width:680px;height:480px"></iframe>',
    					maximizable:true, 
    					modal:true
    				});      		
    	}else{
    		this.searchWindow = new Ext.Window(
    				{
    					title:"查找", 
    				    autoDestroy :false, 
    					autoHeight:true, 
    					autoScroll:true,
    					html:'<iframe id="index_frame" src="' + appPath + this.searchUrl + '" frameborder="0" scrolling="auto" style="border:0px none;width:680px;height:80%"></iframe>',
    					maximizable:true, 
    					modal:true
    				});     		
    	}

		
		 __static_searchfield = this;
		 
		this.searchWindow.show();
    },
    setHiddenValue : function (value){
    	this.hiddenField.value = value;
    }
    

});
Ext.reg('searchfield', Ext.form.SearchField);