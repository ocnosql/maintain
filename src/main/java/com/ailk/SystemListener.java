package com.ailk;

import com.ailk.pool.Config;
import com.ailk.pool.ConnFactory;
import com.ailk.pool.DBConnHolder;
import com.ailk.pool.PoolManager;
import com.ailk.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SystemListener implements ServletContextListener{//监听web生命周期
	
	private final static Log log = LogFactory.getLog(SystemListener.class);
	private final static String RUNTIME_PROPERTIES = "runtime.properties";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("init system begin");
//		log.info("init uniformStructure...");
//		UniformStructureReader.afterPropertiesSet();
//		log.info("init redis config...");
//		RedisConfigReader.afterPropertiesSet();
//		log.info("init report config...");
//		ReportConfigReader.afterPropertiesSet();
		initPool();
//		log.info("init interfaceCache...");
//		InterfaceCache.afterPropertiesSet();
//		log.info("init system done.");
	}
	
	
	public void initPool(){
        log.info("init pool...");
		String dataSourceList = get("dataSource.list");
		if(StringUtils.isEmpty(dataSourceList)){
			return;
		}
		
		String[] dataSources = dataSourceList.split(",");
		
		for(String ds : dataSources){
			//DBConnHolder dbHolder = new DBConnHolder(getInt(ds + ".dataBaseID"), get("sysfile"));
            String driver = get(ds + ".driverClass");
            String url = get(ds + ".url");
            String username = get(ds + ".username");
            String passwd = get(ds + ".passwd");

            DBConnHolder dbHolder = new DBConnHolder(driver, url, username, passwd);
			ConnFactory factory = new ConnFactory(dbHolder);
			Config config = new Config();
			
			config.maxActive = Integer.parseInt(get(ds + ".pool.maxActive"));
			config.maxIdle = Integer.parseInt(get(ds + ".pool.maxIdle"));
			config.minIdle = Integer.parseInt(get(ds + ".pool.minIdle"));
			config.maxWait = Integer.parseInt(get(ds + ".pool.maxWait"));
			config.testOnBorrow = Boolean.valueOf(get(ds + ".pool.testOnBorrow"));
			config.testWhileIdle = Boolean.valueOf(get(ds + ".pool.testWhileIdle"));
			config.minEvictableIdleTimeMillis = Integer.parseInt(get(ds + ".pool.minEvictableIdleTimeMillis"));
			config.numTestsPerEvictionRun = Integer.parseInt(get(ds + ".pool.numTestsPerEvictionRun"));
			config.timeBetweenEvictionRunsMillis = Integer.parseInt(get(ds + ".pool.timeBetweenEvictionRunsMillis"));
			
			PoolManager.createPool(ds, factory, config);
			log.info("init pool '"+ ds +"'...");
		}
	}
	
	
	public String get(String propertyName){
		return PropertiesUtil.getProperty(RUNTIME_PROPERTIES, propertyName);
	}
	
	
	public int getInt(String propertyName){
		return Integer.parseInt(PropertiesUtil.getProperty(RUNTIME_PROPERTIES, propertyName));
	}

}
