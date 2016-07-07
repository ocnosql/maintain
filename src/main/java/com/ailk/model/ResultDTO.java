package com.ailk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultDTO {

	private List<Map> records;
	
	private Map extInfo;   //额外信息
	
	private long totalCount;
	
	private boolean hasPaged;
	
	private boolean isSuccess;
	
	private String message;
	
	private Map<String, Double> fieldInfo = new HashMap();

	
	public List<Map> getRecords() {
		return records;
	}

	public void setRecords(List<Map> records) {
		this.records = records;
	}

	public Map getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(Map extInfo) {
		this.extInfo = extInfo;
	}

	

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isHasPaged() {
		return hasPaged;
	}

	public void setHasPaged(boolean hasPaged) {
		this.hasPaged = hasPaged;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Double> getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(Map<String, Double> fieldInfo) {
		this.fieldInfo = fieldInfo;
	}
	
	
}
