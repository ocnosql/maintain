package com.ailk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.core.exception.ScriptRuntimeException;

public class ShellUtil {
	
	private static Log log = LogFactory.getLog(ShellUtil.class);
	
	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static ScriptEngine engine = manager.getEngineByName("javascript");
	
	public static boolean execute(String exp){
		try {
			Boolean flag = (Boolean)engine.eval(exp);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("execute javaScript exception.", e);
		}
	}
	
	public static List<Map> filterData(String exp, List<Map> records){
		List<Map> result = new ArrayList<Map>();
		List<String> params = ParseUtil.parseSql(exp);
		
		for(Map record : records){
			String exp_ = exp;
			for(String param : params){
				String param_ = param.substring(2, param.length() - 1);
				if(!record.containsKey(param_)){
					throw new ScriptRuntimeException("变量['"+ param_ +"']不存在，请检查表达式");
				}
				exp_ = exp_.replace(param, "'" + record.get(param_) + "'");
			}
			//log.debug("" + exp + " --> " + exp_ + "");
			if(execute(exp_)){
				result.add(record);
			}
		}
		return result;
	}
	
	
	
	public static void main(String[] args){
		String exp = "${SRC_CALL_TYPE} == 'ddd' || ${SRC_ROAM_TYPE} == 1";
		Map record1 = new HashMap();
		record1.put("SRC_CALL_TYPE", "ssss");
		record1.put("SRC_ROAM_TYPE", 1);
		
		Map record2 = new HashMap();
		record2.put("SRC_CALL_TYPE", "ddd");
		record2.put("SRC_ROAM_TYPE", 1);
		
		
		List<Map> list = new ArrayList<Map>();
		list.add(record1);
		list.add(record2);
		
		List result = filterData(exp, list);
		
		System.out.println(result);
	}
}
