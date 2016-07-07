package com.ailk.model.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class RedisConfigReader{

	private static SAXReader reader = new SAXReader();
	private static Map<String, RedisConfig> configMap;

	public static void afterPropertiesSet(){
		// TODO Auto-generated method stub
		try{
			readConfig("classpath:redisConfig.xml");
		}catch(Exception e){
			throw new RuntimeException("init redis config exception.", e);
		}
	}
	
	public static void readConfig(String path) throws Exception{
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
	    Resource[] resources = resolver.getResources(path); 
	    for(Resource resource:resources){
	    	convert2Config(resource);
	    }
	}
	
	public static void convert2Config(Resource resource) throws IOException, DocumentException{
		configMap = new LinkedHashMap<String, RedisConfig>();
		InputStream inputStream = resource.getInputStream();
		Document document = null;
		document = reader.read(inputStream);
		Element root = document.getRootElement();
		for(Iterator<Element> it = root.elementIterator("redis"); it.hasNext();){
			Element redis = it.next();
			Element name = redis.element("name");
			Element host = redis.element("host");
			Element port = redis.element("port");
			
			RedisConfig config = new RedisConfig();
			config.setName(name.getText());
			config.setHost(host.getText());
			config.setPort(Integer.parseInt(port.getText()));
			configMap.put(name.getText(), config);
		}
	}

	public static Map<String, RedisConfig> getConfigMap() {
		if(configMap == null){
			afterPropertiesSet();
		}
		return configMap;
	}
	

}
