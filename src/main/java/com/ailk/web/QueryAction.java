package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.ext.JsonResult;
import com.ailk.model.ext.ResultBuild;
import com.ailk.oci.ocnosql.common.rowkeygenerator.RowKeyGenerator;
import com.ailk.service.IQueryService;
import com.ailk.service.QueryServiceFactory;
import com.ailk.service.impl.DrqueryService;
import com.ailk.util.GeneratorMD5;
import com.ailk.util.ShellUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class QueryAction extends BaseAction{
	
	private static Log log = LogFactory.getLog(QueryAction.class);

	/*{  
	    'metaData': {  
	        'totalProperty': 'total',  
	        'root': 'records',  
	        'id': 'id',  
	        'fields': [  
	            {'name': 'id', 'type': 'int'},  
	            {'name': 'name', 'type': 'string'}  
	        ]  
	    },  
	    'success': true,  
	    
	    'total': 50,  
	    'records': [  
	        {'id': '1', 'name': 'AAA'},  
	        {'id': '2', 'name': 'BBB'}  
	    ],  
	    'columns': [  
	        {'header': '#', 'dataIndex': 'id'},  
	        {'header': 'User', 'dataIndex': 'name'}  
	    ]  
	} */ 
	private static final long serialVersionUID = 1L;

	private String type;

	
	public String queryData(){
		/*Map result = new HashMap();
		Map map = new HashMap();
		
		List fieldList = new ArrayList();
		map.put("totalProperty", "total");
		map.put("root", "records");
		map.put("id", "id");
		map.put("fields", fieldList);
		
		
		
		Map field = new HashMap();
		field.put("name", "id");
		field.put("type", "int");
		
		Map field2 = new HashMap();
		field2.put("name", "name");
		field2.put("type", "string");
		
		fieldList.add(field);
		fieldList.add(field2);
		
		result.put("metaData", map);
		result.put("success", true);
		result.put("total", 2);
		
		Map records = new HashMap();
		records.put("id", "1");
		records.put("name", "AA");
		
		Map records2 = new HashMap();
		records2.put("id", "2");
		records2.put("name", "BB");
		
		List recordList = new ArrayList();
		recordList.add(records);
		recordList.add(records2);
		
		result.put("records", recordList);
		
		Map column = new HashMap();
		column.put("header", "#");
		column.put("dataIndex", "id");
		
		Map column2 = new HashMap();
		column2.put("header", "User");
		column2.put("dataIndex", "name");
		
		List columnList = new ArrayList();
		columnList.add(column);
		columnList.add(column2);
		
		result.put("columns", columnList);*/
		
//		Gson gs = new Gson();
//		this.setAjaxStr(gs.toJson(result));
//		System.out.println(this.getAjaxStr());
		
		List<Map> list = new ArrayList<Map>();
		String[] fields = new String[20];
		for(int i = 0; i < 100; i++){
			Map record = new HashMap();
			for(int j = 0; j < 20 ; j++){
				record.put("SRC_FIELD_" + j, "VALUE_" + i+"_" + j);
				fields[j] = "SRC_FIELD_" + j;
			}
			list.add(record);
		}
	
		int startIndex = Integer.parseInt(start);
		int stopIndex = startIndex + Integer.parseInt(limit);
		if(stopIndex >= list.size()){
			stopIndex = list.size();
		}
		try{
			Gson gs = new Gson();
			this.setAjaxStr(gs.toJson(ResultBuild.buildResult(fields, list.subList(startIndex, stopIndex), list.size())));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//System.out.println(this.getAjaxStr());
		return AJAXRTN;
	}
	
	public String query(){
		IQueryService service = QueryServiceFactory.getQueryService(type);
		ValueSet vs = new ValueSet();
		bindParams(vs, ServletActionContext.getRequest());
		Gson gs = new Gson();
		try{
			long startTime = System.currentTimeMillis();
			ResultDTO dto = service.loadData(vs);
			long endTime = System.currentTimeMillis();
			log.info("query token: " + (endTime - startTime) + "ms");
			JsonResult result = null;
			if(dto.isSuccess()){
				if(!dto.isHasPaged()){
					if(StringUtils.isNotEmpty(vs.getString("exp"))){
						dto.setRecords(ShellUtil.filterData(vs.getString("exp"), dto.getRecords()));
					}
					result = ResultBuild.buildResult(dto.getRecords(), Integer.parseInt(start), Integer.parseInt(limit));
				}else{
					result = ResultBuild.buildResult(dto.getRecords(), dto.getTotalCount());
				}
				if(dto.getExtInfo() != null){
					result.setExtInfo(dto.getExtInfo());
				}
				this.setAjaxStr(gs.toJson(result));
			}else{
				this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(dto.getMessage())));
			}
			long endTime2 = System.currentTimeMillis();
			log.info("convert records to json token: " + (endTime2 - endTime) + "ms");
		}catch(Throwable ex){
			//ex.printStackTrace();
			log.error("查询出现异常", ex);
			//ResultBuild.buildFailed(ex);
			this.setAjaxStr(gs.toJson(ResultBuild.buildFailed(ex)));
		} 
		return AJAXRTN;
	}
	
	
	public String queryRowkey(){
		String rowkey = ServletActionContext.getRequest().getParameter("billId"); 
		//String hashType = ServletActionContext.getRequest().getParameter("hashType");
        RowKeyGenerator generator = new GeneratorMD5();
        try{
			if(generator!=null){
	        	rowkey = (String) generator.generatePrefix(rowkey);
	        }
			this.setAjaxStr("{\"success\": true, \"rowkey\": \""+ rowkey +"\"}");
        }catch(Exception e){
        	e.printStackTrace();
        	this.setAjaxStr("{\"success\": false}");
        }
		return AJAXRTN;
	}

    public String refreshCache(){
        Gson gs = new Gson();
        Map result = new HashMap();
        try{
            DrqueryService service = new DrqueryService();
            ValueSet vs = new ValueSet();
            bindParams(vs, ServletActionContext.getRequest());
            result.put("success", true);
            result.put("data", service.refreshCache(vs));
            setAjaxStr(gs.toJson(result));
        }catch(Exception e){
            log.error("", e);
            Map msg = new HashMap();
            msg.put("success", false);
            msg.put("message", e.getMessage());
            setAjaxStr(gs.toJson(msg));
        }
        return AJAXRTN;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void bindParams(ValueSet vs, HttpServletRequest request){
		Enumeration e = request.getParameterNames();
		while(e.hasMoreElements()){
			String name = (String) e.nextElement();
			vs.put(name, request.getParameter(name));
		}
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
