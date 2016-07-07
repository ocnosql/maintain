package com.ailk.service.impl.business;

import java.util.List;
import java.util.Map;

import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.pool.PoolConstants;
import com.ailk.service.IQueryService;

public class DealTimeMonitorService implements IQueryService{

	BaseDao dao = new BaseDao(PoolConstants.CSQRY);
	
	@Override
	public ResultDTO loadData(ValueSet vs) {
		
		String type = vs.getString("queryType");
		
		String sql = " select cdr_name \"业务类型\", " + 
			         "   count(1) \"批次数量\"," + 
			         "   sum((end_time - begin_time) * 24 * 60 * 60) \"总时长(秒)\"," + 
			         "   sum(deal_sum) \"文件总数\"," + 
			         "   round(avg((end_time - begin_time)*24 * 60 * 60), 2) \"每批处理平均时长(秒)\"," + 
			         "   max ((end_time - begin_time)*24 * 60 * 60) \"每批处理最大时长(秒)\", " + 
			         "   min ((end_time - begin_time)*24 * 60 * 60) \"每批处理最小时长(秒)\"," + 
			         "   round(sum((end_time - begin_time) * 24 * 60 * 60) / sum(deal_sum), 2) \"每个文件平均处理时长(秒)\"" + 
			         "   from qry_task_inst " + 
			         " where length(cdr_name) > 1";
		
		if("HIS".equals(type)){
			sql += " and end_time >= to_date('" + vs.getString("startTime") + "', 'yyyy-MM-dd hh24:mi:ss')" + 
		           " and end_time <= to_date('" + vs.getString("endTime") + "', 'yyyy-MM-dd hh24:mi:ss')";
		}else{
			sql += " and end_time >= ( sysdate - 5 /( 24* 60 ))";
		}
		sql += " group by cdr_name";
		
		List<Map> records = dao.query(sql);
		
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setRecords(records);
		dto.setTotalCount(records.size());

		
		return dto;
	}
}
