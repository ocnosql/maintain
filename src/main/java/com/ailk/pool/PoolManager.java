package com.ailk.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PoolManager {
	
	public static Log log = LogFactory.getLog(PoolManager.class);
	
	private static Map<String, Pool> poolInstanceMap = new HashMap<String, Pool>();

	public static Pool createPool(String name, ConnFactory factory, Config config){
		if(StringUtils.isEmpty(name)){
			throw new PoolException("pool name cann't be null or emptye");
		}
		if(poolInstanceMap.containsKey(name)){
			throw new PoolException("pool name '" + name + "' is already exsits, please use another name.");
		}
		Pool pool = new Pool(name, factory, config);
		poolInstanceMap.put(name, pool);
		log.debug("create pool '"+ name +"' success.");
		
		return pool;
	}
	
	public static Pool getPool(String name){
		if(!poolInstanceMap.containsKey(name)){
			throw new PoolException("cann't get pool '"+ name +"', because it dose not exsit.");
		}
		return poolInstanceMap.get(name);
	}
}
