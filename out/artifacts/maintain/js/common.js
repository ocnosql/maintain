Utils = {
	monthDays: [31, new Date().getFullYear() / 4 ==0 ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
	getCurrentMonthFirstDay: function(){
		return  new Date().getFullYear() + this.getMonth() + '01';
	},
	getCurrentMonthLastDay: function(){
		return  new Date().getFullYear() + this.getMonth() + this.monthDays[new Date().getMonth()];
	},
	getMonth: function(){
		var month = new Date().getMonth() + 1;
		if(month < 10){
			month = '0' + month;
		}
		return month;
	},
	getDate: function(){
		var date = new Date().getDate();
		if(date < 10){
			date = '0' + date;
		}
		return date;
	},
	copyToClipboard: function(txt) {  
	     if(window.clipboardData) {  
	             window.clipboardData.clearData();  
	             window.clipboardData.setData("Text", txt);  
	     } else if(navigator.userAgent.indexOf("Opera") != -1) {  
	          window.location = txt;  
	     } else if (window.netscape) {  
	          try {  
	               netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");  
	          } catch (e) {  
	               alert("被浏览器拒绝！\n请在浏览器地址栏输入'about:config'并回车\n然后将'signed.applets.codebase_principal_support'设置为'true'");  
	          }  
	          var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);  
	          if (!clip)  
	               return;  
	          var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);  
	          if (!trans)  
	               return;  
	          trans.addDataFlavor('text/unicode');  
	          var str = new Object();  
	          var len = new Object();  
	          var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);  
	          var copytext = txt;  
	          str.data = copytext;  
	          trans.setTransferData("text/unicode",str,copytext.length*2);  
	          var clipid = Components.interfaces.nsIClipboard;  
	          if (!clip)  
	               return false;  
	          clip.setData(trans,null,clipid.kGlobalClipboard);  
	     }
    },
    /**
     * dataType: [CSV, OCNOSQL]
     */ 
	copySelectedRows: function(gridPanel, dataType){
		if(typeof dataType == 'undefined'){
			dataType = 'CSV';
		}
		if(gridPanel.store.reader.jsonData){
			
		}else{
			Ext.Msg.alert('提示', '请查询后再选择要复制的记录！');
			return;
		}
		var records = gridPanel.getSelectionModel().getSelections();
		if(records.length == 0){
			Ext.Msg.alert('提示', '请选择至少一条记录！');
			return;
		}
		var content = '';
		var fields = [];
		if(gridPanel.store.reader.jsonData.columns){
			var cols = gridPanel.store.reader.jsonData.columns;
			for(var i = 0; i < cols.length; i++){
				fields.push(cols[i].dataIndex);
				if(dataType == 'CSV'){
					if(i != cols.length -1){
						content += cols[i].dataIndex + ',';
					}else{
						content += cols[i].dataIndex;
					}
				}
			}
		}
		if(dataType == 'CSV'){
			//content += '<br>\r\t';
			content += '\r';
		}
		
		for(var i = 0; i < records.length; i++){
			record = records[i];
			if(dataType == 'OCNOSQL'){
				content += "put '${table}', '"+ record.get(fields[2]) +"', 'f:"+ new Date().getTime() + i + "','";
			}
			for(var j = 0; j < fields.length; j++){
				if(j != fields.length - 1){
					if(dataType == 'OCNOSQL'){
						if(j != 2)
							content += record.get(fields[j]) + ";";
					}else{
						content += "\"" + record.get(fields[j]).replace(/\"s/g, "\"\"") + "\",";
					}
				}else{
					if(dataType == 'OCNOSQL'){
						content += record.get(fields[j]);
					}else{
						content += "\"" + record.get(fields[j]).replace(/\"s/g, "\"\"") + "\"";
					}
					
				}
			}
			if(dataType == 'OCNOSQL'){
				content += "'";
			}
			//content += '<br>\r\t';
			content += '\r';
		}
		this.copyToClipboard(content); 
		var win = new Ext.Window({
			title: '复制数据',
			height: 600,
			width:1000,
			modal: true,
			autoScroll: true
		});
		var form = new Ext.FormPanel({
			//height: 600,
			//width:850,
			layout: 'anchor'
		});
		var retText = new Ext.form.TextArea({
			name : 'retTextArea',
			id : 'retTextArea',
			height: 560,
			//width: 850,
			anchor: '100%',
			scope : this,
			//readOnly: true,
			enableKeyEvents : true
		});
		retText.setValue(content);
		form.add(retText);
		win.add(form);
		win.doLayout();
		win.show();
		return content;
	}
};
