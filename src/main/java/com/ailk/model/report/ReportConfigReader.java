package com.ailk.model.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ReportConfigReader {

	private static SAXReader reader = new SAXReader();
	private static Map<String, ReportConfig> configMap = new LinkedHashMap<String, ReportConfig>();

	public static void afterPropertiesSet(){
		// TODO Auto-generated method stub
		try{
			readConfig("classpath:reportConfig.xml");
		}catch(Exception e){
			throw new RuntimeException("init report config exception.", e);
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
		InputStream inputStream = resource.getInputStream();
		Document document = null;
		document = reader.read(inputStream);
		Element root = document.getRootElement();
		for(Iterator<Element> it = root.elementIterator("report"); it.hasNext();){
			Element redis = it.next();
			Element name = redis.element("name");
			Element sql = redis.element("sql");
			Element desc = redis.element("desc");
			
			ReportConfig config = new ReportConfig();
			config.setName(name.getText());
			config.setSql(sql.getText());
			config.setDesc(desc.getText());
			configMap.put(name.getText(), config);
		}
	}

	public static Map<String, ReportConfig> getConfigMap() {
		return configMap;
	}
}
