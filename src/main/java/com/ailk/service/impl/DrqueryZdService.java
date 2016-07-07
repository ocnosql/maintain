package com.ailk.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;
import com.ailk.util.PropertiesUtil;
import com.ailk.util.RestUtil;

public class DrqueryZdService implements IQueryService {

	private static Log log = LogFactory.getLog(DrqueryZdService.class);
	private List<Map> list= new ArrayList<Map>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResultDTO loadData(ValueSet vs) {
		// TODO Auto-generated method stub
		String rowkey=vs.getString("rowkey");
		String tablePrefixName=vs.getString("tablePrefixName");
		String tableName=vs.getString("tableName");
		String opId = vs.getString("opId");
		String orgId = vs.getString("orgId");
		String cond = vs.getString("cond");	
		String restName = vs.getString("restName");
		
		String restUri = null;
//		if(!StringUtils.isEmpty(restName)) {
//			restUri = PropertiesUtil.getProperty("runtime.properties", restName);
//		}else{
//			restUri = PropertiesUtil.getProperty("runtime.properties", "drquery.rest.default");
//		}
        restUri = restName;
		
		if(StringUtils.isEmpty(restUri)){
			throw new RuntimeException("DRQuery_Zd uri property '"+ restName +"' is not set, please check the property  in the file 'runtime.properties'.");
		}
		//rowkey=1300032787&tableName=201409&tablePrefixName=ACC_EMEND_INFO&opId=10190705&orgId=0
		restUri += "rowkey=" + rowkey + "&tablePrefixName=" + tablePrefixName + "&tableName=" + tableName + "&opId=" + opId
				   + "&orgId=" + orgId + "&cond=" + cond;
		log.info("request uri: " + restUri);
		
		int start = Integer.parseInt(vs.getString("start"));
		int limit = Integer.parseInt(vs.getString("limit"));
		
		vs.put("pageIndex", start / limit + 1);
		vs.put("pageSize", limit);
		vs.remove("t");
		vs.remove("start");
		vs.remove("limit");
		vs.remove("type");
		vs.remove("restName");

		String returnData = null;
		try {
			returnData = RestUtil.post(restUri, "", "gb2312");
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}

		ResultDTO dto = new ResultDTO();
		
		if(StringUtils.isNotEmpty(returnData)){
	    	JSONObject json =  JSONObject.fromObject(returnData);
		    	JSONArray ja = json.getJSONArray("records");		//records
	    		  for(int i = 0; i < ja.size(); i++){
	    			  Map record = new LinkedHashMap();
	    			  JSONObject jo=ja.getJSONObject(i);
	    			for (Object key : jo.keySet()) {
	    				record.put(key, jo.get(key));
	    	 }
	    			list.add(record);
	    		  }
		            dto.setSuccess(true);//SUCCESS
	    		  	dto.setTotalCount(json.getLong("total"));//TOTAL
		        	dto.setMessage(json.getString("message"));//message
		            dto.setRecords(list);
	        }else{
	        	dto.setSuccess(false);
	        }
		dto.setHasPaged(true);
		return dto;
	}

}
