package com.ailk.pool;

import org.apache.commons.pool.impl.GenericObjectPool;

public class Pool extends GenericObjectPool{
	
	private ConnFactory factory;
	private Config config;
	private String name;

	Pool(String name, ConnFactory factory, Config config){
		super(factory, config);
	}

	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
