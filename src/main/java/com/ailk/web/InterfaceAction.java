package com.ailk.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;
import com.ailk.service.QueryServiceFactory;
import com.google.gson.Gson;

/**
 * 接口测试的Action
 * 
 * @author tony
 */
public class InterfaceAction extends BaseAction {

	private static Log log = LogFactory.getLog(InterfaceAction.class);

	private static final long serialVersionUID = 1L;
	private static String AJAXRTN = "ajax_rtn";
	private String ajaxStr;
	private String uri;
	private String retXml;
	private String itemName;

	public String execute() throws Exception {
		this.query();
		return AJAXRTN;
	}
	
	public String query(){
		IQueryService service = QueryServiceFactory.getQueryService("interfaceService");
		ValueSet vs = new ValueSet();
		vs.put("uri", this.getUri());  //接收前台ajax的name  
		vs.put("retXml", this.getRetXml());
		vs.put("itemName", this.getItemName());    
		ResultDTO dto = service.loadData(vs);       
		Map map = new HashMap();
		map.put("success", true);
		map.put("data", dto.getMessage());
		Gson gs = new Gson();
		this.setAjaxStr(gs.toJson(map));
		return null;
	}

	public static String getAJAXRTN() {
		return AJAXRTN;
	}

	public static void setAJAXRTN(String aJAXRTN) {
		AJAXRTN = aJAXRTN;
	}

	public String getAjaxStr() {
		return ajaxStr;
	}

	public void setAjaxStr(String ajaxStr) {
		this.ajaxStr = ajaxStr;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRetXml() {
		return retXml;
	}

	public void setRetXml(String retXml) {
		this.retXml = retXml;
	}
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}
