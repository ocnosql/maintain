package com.ailk.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.model.report.ReportConfig;
import com.ailk.model.report.ReportConfigReader;
import com.ailk.pool.Pool;
import com.ailk.pool.PoolConstants;
import com.ailk.pool.PoolManager;
import com.ailk.service.IQueryService;
import com.ailk.util.DateUtil;
import com.ailk.util.ParseUtil;

public class DrqueryMonitorService implements IQueryService{

	private static Log log = LogFactory.getLog(DrqueryMonitorService.class);
	
	private BaseDao dao = new BaseDao(PoolConstants.CSQRY);
	
	private static Map<String, Class> paramType = new HashMap<String, Class>();
	static{
		paramType.put("startDate", Date.class);
		paramType.put("endDate", Date.class);
		paramType.put("billId", String.class);
		paramType.put("table", Integer.class);
	}
	
	private static final String TABLE_PREFIX = "qry_bill_log_";
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultDTO loadData(ValueSet vs) {
		int start = Integer.parseInt(vs.getString("start"));
		int limit = Integer.parseInt(vs.getString("limit"));
		int end = start + limit;
		
		String sql = buildSQl(vs);
		List<Map> records = dao.queryByPage(sql, start, end);
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setHasPaged(true);
		dto.setTotalCount(dao.queryTotalCount(sql));
		dto.setRecords(records);
		return dto;
	}
	
	public String buildSQl(ValueSet vs){
		String retSQL = "";
		String reportName = vs.getString("reportName");
		String startDate = vs.getString("startDate");
		String endDate = vs.getString("endDate");
		String queryType = vs.getString("queryType");
		
		ReportConfig config = ReportConfigReader.getConfigMap().get(reportName);
		if(config == null){
			throw new AppRuntimeException("report config '" + reportName + "' is not found. please check the [reportConfig.properties].");
		}
		
		String sql = config.getSql().replace("\r", "").replace("\t", "").replace("\n", "");
		String sql_ = "";
		if("HIS".equals(queryType)){
			 List<String> dayList = DateUtil.getIntervMonth(startDate, endDate, "yyyyMMdd");
			 for(int i = 0; i < dayList.size(); i++){
				 sql_ += "select * from " + TABLE_PREFIX + dayList.get(i);
				 if(i != dayList.size() - 1){
					 sql_ += " union ";
				 }
			 }
			 vs.addParam("table", "(" + sql_ + ")");
			 retSQL = parseSql(sql, vs);
		}else{
			
		}
		log.info("generate sql: " + retSQL);
		return retSQL;
	}
	
	public String parseSql(String sql, ValueSet vs){
		List<String> params = ParseUtil.parseSql(sql);
		for(String param : params){
			String param_ = param.substring(2, param.length() - 1);
			String val = vs.getString(param_);
			if(paramType.get(param_).getName().equals(Date.class.getName())){
				sql = sql.replace(param, "to_date('"+ val +"', 'yyyyMMdd')");
			}else if(paramType.get(param_).getName().equals(String.class.getName())){
				sql = sql.replace(param, "'" + val + "'"); 
			}else{
				sql = sql.replace(param, val); 
			}
			
		}
		return sql;
	}
	
	public static void main(String[] args){
		try{
			List list = DateUtil.getIntervDate("20130104", "20130104", "yyyyMMddHH");
			System.out.println(list.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
