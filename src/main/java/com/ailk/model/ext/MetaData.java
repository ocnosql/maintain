package com.ailk.model.ext;

import java.util.ArrayList;
import java.util.List;

public class MetaData {

	private String totalProperty = "total";
	private String root = "records";
	//private String id = "id";
	private List fields = new ArrayList();
	public String getTotalProperty() {
		return totalProperty;
	}
	public void setTotalProperty(String totalProperty) {
		this.totalProperty = totalProperty;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	
	public List getFields() {
		return fields;
	}
	public void setFields(List fields) {
		this.fields = fields;
	} 
	
	
}
