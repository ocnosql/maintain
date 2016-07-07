package com.ailk.pool;

public interface ConnHolder {

	public Object makeObject() throws Exception;
	
	public void destroyObject(Object obj) throws Exception;
	
	public boolean validateObject(Object obj);
	
	public void activateObject(Object obj) throws Exception;
	
	public void passivateObject(Object obj) throws Exception;

}
