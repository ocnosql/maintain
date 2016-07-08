package com.ailk.model;

import java.util.HashMap;

public class ValueSet extends HashMap{

	public void addParam(Object paramName, Object value){
		this.put(paramName, value);
	}
	
	public String getString(Object name){
		return (String) this.get(name);
	}
	
	public long getLong(Object name){
		Object value = this.get(name);
		if(value instanceof Long){
			return (Long) value;
		}else{
			return Long.parseLong(getString(name));
		}
	}

    public int getInt(Object name) {
        Object value = this.get(name);
        if(value instanceof Integer){
            return (Integer) value;
        }else{
            return Integer.parseInt(getString(name));
        }
    }
}
