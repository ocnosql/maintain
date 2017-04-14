package com.ailk.model.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class ResultBuild {

	public static JsonResult buildResult(List<Map> records, int startIndex, int limit){
		String[] fields = new String[0];
		if(records.size() > 0){
			Map record = records.get(0);
			Iterator it = record.keySet().iterator();
			fields = new String[record.keySet().size()];
			int i = 0;
			while(it.hasNext()){
				fields[i] = (String)it.next();
				i ++;
			}
			
			int stopIndex = startIndex + limit;
			if(stopIndex >= records.size()){
				stopIndex = records.size();
			}
			
			return buildResult(fields, records.subList(startIndex, stopIndex), records.size());
		}
		return buildResult(fields, records, records.size());
	}
	
	public static JsonResult buildResult(List<Map> records, long totalCount){
		if(records == null){
			records = new ArrayList<Map>();
		}
		return buildResult(records.size() > 0 ? createFieldHeader(records.get(0)) : new String[0], records, totalCount);
	}
	
	public static String buildResult(List<Map> records){
		return buildResult(records, null);
	}

	public static String buildResult(List<Map> records, Map<String, Integer> uiFeildLengthConfig) {
		if(records == null){
			records = new ArrayList<Map>();
		}
		Gson gs = new Gson();
		return gs.toJson(buildResult(records.size() > 0 ? createFieldHeader(records.get(0)) :
				new String[0], records, records.size(), uiFeildLengthConfig));
	}

	public static String buildResult2(List<Map> records,long totalCount){
		if(records == null){
			records = new ArrayList<Map>();
		}
		Gson gs = new Gson();
		return gs.toJson(buildResult(records.size() > 0 ? createFieldHeader(records.get(0)) :
				new String[0], records, totalCount));
	}

	public static String buildJson(String[] fields, List<String[]> records) {
		List<Map> list = new ArrayList();
		for(int i = 0; i < records.size(); i ++) {
			Map map = new LinkedHashMap();
			for(int j = 0; j < fields.length; j++) {
				map.put(fields[j], records.get(i)[j]);
			}
			list.add(map);
		}
		
		return buildResult(list);
	}

	public static String buildJson(String[] fields, List<String[]> records, long totalCount) {
		List<Map> list = new ArrayList();
		for(int i = 0; i < records.size(); i ++) {
			Map map = new LinkedHashMap();
			for(int j = 0; j < fields.length; j++) {
				map.put(fields[j], records.get(i)[j]);
			}
			list.add(map);
		}
		return buildResult2(list, totalCount);
	}

	public static JsonResult buildResult(String[] fields, List<Map> records, long totalCount) {
		return buildResult(fields, records, totalCount, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JsonResult buildResult(String[] fields, List<Map> records, long totalCount, Map<String, Integer> uiFeildLengthConfig){
		JsonResult result = new JsonResult();
		MetaData meta = new MetaData();
        Map<String, String> mapping = new LinkedHashMap<String, String>(fields.length);
        int index = 0;
		for(String fld : fields){
			Map field = new HashMap();
//			field.put("name", fld);
            field.put("name", "C" + index + "");
			field.put("type", "string");
			meta.getFields().add(field);
            mapping.put(fld, "C" + index + "");
            index ++;
		}
        List<Map> recordsCopy = new ArrayList<Map>(records.size());

        for(Map record : records) {
            Map recordCopy = new LinkedHashMap(record.size());
            for(Map.Entry entry : mapping.entrySet()) {
                recordCopy.put(entry.getValue(), record.get(entry.getKey()));
            }
            recordsCopy.add(recordCopy);
        }


		result.setMetaData(meta);
		result.setTotal(totalCount);
		result.getRecords().addAll(recordsCopy);
		
		for(int i=0; i < fields.length; i++){
			String fld = fields[i];
			Map column = new LinkedHashMap();
			//column.put("header", i + "_" + fld);
			column.put("header", fld);
			column.put("dataIndex", mapping.get(fld));
			if(uiFeildLengthConfig != null) {
				Integer fieldLength = uiFeildLengthConfig.get(fld);
				if(fieldLength != null && fieldLength > 0) {
					column.put("width", fieldLength);
				} else {
					column.put("width", fld.length() < 15 ? 130 : fld.length() * 8);
				}
			} else {
				column.put("width", fld.length() < 15 ? 130 : fld.length() * 8);
			}
			column.put("sortable", true);
			result.getColumns().add(column);
		}
		
		return result;
	}
	
	public static JsonResult buildFailed(String message){
		JsonResult result = new JsonResult();
		result.setSuccess(false);
		result.setMessage(message);
		return result;
	}
	
	public static JsonResult buildFailed(Throwable e){
		StringBuffer sb = new StringBuffer();
		//System.out.println(e.getMessage());
		sb.append(e.getClass().getName());
		Throwable t = e.getCause();
		if(t == null){
			sb.append(": ").append(e.getMessage());
		}
		while(t != null){
			sb.append(": ");
			sb.append(t.getClass().getName() + ": " + t.getMessage());
			t = t.getCause();
		}
		
		JsonResult result = new JsonResult();
		result.setSuccess(false);
		result.setMessage(sb.toString());
		//System.out.println(sb.toString());
		return result;
	}
	
	public static String[] createFieldHeader(Map record){
		Iterator it = record.keySet().iterator();
		String[] fields = new String[record.keySet().size()];
		int i = 0;
		while(it.hasNext()){
            String columnName = (String)it.next();
            //extjs无法处理列名中带.的列
            fields[i] = columnName;
			i ++;
		}
		return fields;
	}
	
	
}
