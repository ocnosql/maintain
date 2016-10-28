package com.ailk.service.impl;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;
import com.ailk.util.PropertiesUtil;
import com.ailk.util.RestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrqueryService implements IQueryService{
	
	private static Log log = LogFactory.getLog(DrqueryService.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResultDTO loadData(ValueSet vs) {
		if("getUrls".equals(vs.getString("m"))){
			return getUrls(vs);
		}
		String restName = vs.getString("restName");
		String restUri = null;
		if(StringUtils.isEmpty(restName)){
			restUri = PropertiesUtil.getProperty("runtime.properties", "drquery.rest.default");
		}else{
			restUri = PropertiesUtil.getProperty("runtime.properties", restName);
		}
		
		if(StringUtils.isEmpty(restUri)){
			throw new RuntimeException("DRQuery uri property '"+ restName +"' is not set, please check the property  in the file 'runtime.properties'.");
		}

//		int start = Integer.parseInt(vs.getString("start"));
//		int limit = Integer.parseInt(vs.getString("limit"));
//
		vs.put("startIndex", vs.getString("start"));
		vs.put("offset", vs.getString("limit"));
		vs.remove("t");
		vs.remove("start");
		vs.remove("limit");
		vs.remove("type");
		vs.remove("restName");
//		if("HIS".equals(vs.get("impType"))){
//			vs.put("billMonth", vs.getString("thruDate").substring(0, 6));
//		}
        String url = buildURL(restUri, vs);
        log.info("request uri: " + url);
		String returnData = null;
		try {
			returnData = RestUtil.postWithJson(url, Collections.EMPTY_MAP);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		//log.info("return " + returnData+ " records");
		//System.out.println(returnData);
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true); 
		List<Map> list = new ArrayList<Map>();
		if(StringUtils.isNotEmpty(returnData)){
	    	JSONObject json =  JSONObject.fromObject(returnData);
	        if("0".equals(json.getString("result"))){
	        	if(!(json.get("data") instanceof net.sf.json.JSONNull)){
//		            JSONObject replyDisInfo = json.getJSONObject("replyDisInfo");
//		            JSONArray cdrDisplays = replyDisInfo.getJSONArray("cdrDisplays");
//		            for(int i = 0; i < cdrDisplays.size(); i++){
//		            	Map record = new LinkedHashMap();
//		            	JSONArray fields = cdrDisplays.getJSONArray(i);
//		            	for(int j = 0; j < fields.size(); j++){
//		            		JSONObject field = fields.getJSONObject(j);
//		            		record.put(field.getString("name"), field.getString("value"));
//		            	}
//		            	list.add(record);
//		            }
//		            dto.setRecords(list);
//		            dto.setTotalCount(json.getJSONObject("stats").getLong("totalCount"));
//		            Map extInfo = new HashMap();
//		            extInfo.put("sums", replyDisInfo.getJSONArray("sums"));
//		            dto.setExtInfo(extInfo);
                    JSONArray data = json.getJSONArray("data");
                    for(int i = 0; i < data.size(); i++) {
                        Map record = new LinkedHashMap();
                        JSONObject fields = data.getJSONObject(i);
                        Iterator it = fields.keys();
                        while(it.hasNext()) {
                            Object key = it.next();
                            record.put(key, fields.get(key));
                        }
		            	list.add(record);
                    }
                    dto.setRecords(list);
                    dto.setTotalCount(json.getJSONObject("pageInfo").getLong("totalCount"));
                    dto.setHasPaged(true);
                    dto.setSuccess(true);
	        	}
	        }else{
	        	dto.setSuccess(false);
	        	dto.setMessage(json.getJSONObject("resMsg").getString("hint"));
	        }
		}
		dto.setHasPaged(true);
		log.info("query complete.");
		return dto;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResultDTO getUrls(ValueSet vs){
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setHasPaged(true);
		List<Map> list = new ArrayList<Map>();
		String restList = PropertiesUtil.getProperty("runtime.properties", "drquery.rest.list");
		if(StringUtils.isNotEmpty(restList)){
			String[] restArr = restList.split(",");
			for(String rest : restArr){
				Map map = new LinkedHashMap();
				map.put("实例列表", PropertiesUtil.getProperty("runtime.properties", rest));
				map.put("状态", "");
				list.add(map);
			}
		}
		dto.getFieldInfo().put("实例列表", 500d);
		dto.setRecords(list);
		return dto;
	}
	
	
	
	public List refreshCache(ValueSet vs){
		String[] urls = vs.getString("urls").split(",");
		ExecutorService service = Executors.newFixedThreadPool(urls.length);
		CountDownLatch runningThreadNum = new CountDownLatch(urls.length);
		List<ProcessRequest> prList = new ArrayList<ProcessRequest>();
		
		for(int i = 0; i < urls.length; i++){
			ProcessRequest pr = new ProcessRequest(urls[i] + "query/refreshCache", runningThreadNum); 
			service.execute(pr);
			prList.add(pr);
		}
		
		try {
			runningThreadNum.await();
		} catch (InterruptedException e) {
		}
		
		if(!service.isShutdown()){
			service.shutdown();
		}
		List list = new ArrayList();
		for(ProcessRequest pr : prList){
			list.add(pr.getResult());
		}
		return list;
	}
	
	
	class ProcessRequest implements Runnable{
		
		String url;
		Map result;
		CountDownLatch runningThreadNum;
		
		public ProcessRequest(String url, CountDownLatch runningThreadNum){
			this.url = url;
			this.runningThreadNum = runningThreadNum;
		}

		@Override
		public void run() {
			try{
				result = processResult(RestUtil.post(url, "", "utf-8"));
			}catch(Exception e){
				log.error("connecting host/"+ url +" exception. ", e);
				result.put("success", false);
				result.put("message", e.getMessage());
				result.put("url", url);
			}finally{
				runningThreadNum.countDown();
			}
			
		}
		
		public Map processResult(String result){
			Map returnMap = new LinkedHashMap();
			if(StringUtils.isNotEmpty(result)){
				try{
					JSONObject json=  JSONObject.fromObject(result);
					returnMap.put("success", json.getBoolean("success"));
					returnMap.put("url", url);
				}catch(Exception e){
					log.error(e);
					returnMap.put("message", e.getMessage());
					returnMap.put("success", false);
					returnMap.put("url", url);
				}
//				JSONObject json=  JSONObject.fromObject(result);
//				returnMap.put(url, json);
//				returnMap.put("success", json.getBoolean("success"));
			}
			return returnMap;
		}

		public Map getResult() {
			return result;
		}
	}

    public String buildURL(String baseURL, ValueSet vs) {
        Iterator it = vs.keySet().iterator();
        StringBuffer sb = new StringBuffer(baseURL).append("?");
        while(it.hasNext()) {
            Object key = it.next();
            Object value = vs.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() -1);
        return sb.toString();
    }

}
