package com.ailk.model.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonResult {

	private MetaData metaData;
	private boolean success = true;
	private String message;
	
	private long total;
	
	private List<Map> records = new ArrayList<Map>();
	
	private List<Map> columns = new ArrayList<Map>();
	
	private Object extInfo;//其他信息

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<Map> getRecords() {
		return records;
	}

	public void setRecords(List<Map> records) {
		this.records = records;
	}

	public List<Map> getColumns() {
		return columns;
	}

	public void setColumns(List<Map> columns) {
		this.columns = columns;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(Object extInfo) {
		this.extInfo = extInfo;
	}

	
	
	
}
