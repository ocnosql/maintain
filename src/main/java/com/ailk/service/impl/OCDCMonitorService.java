package com.ailk.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.pool.PoolConstants;
import com.ailk.service.IQueryService;

public class OCDCMonitorService implements IQueryService{
	
	private BaseDao dao = new BaseDao(PoolConstants.CSQRY);

	@Override
	public ResultDTO loadData(ValueSet vs) {
		String nodeName = vs.getString("nodeName");
		long start = vs.getLong("start");
		long limit = vs.getLong("limit");
		long end = start + limit;
		String sql = "select t.*, (select decode(count(1), 0, 0, 1) from qry_collection_def t1, qry_collection_col_def t2 where t1.coll_id=t2.coll_id and t1.coll_id=t.coll_id) has_detail from qry_task_inst t where 1= 1 ";
		if(StringUtils.isNotEmpty(nodeName)){
			sql +=  "and task_type='" + nodeName + "'";
		}
		if(StringUtils.isNotEmpty(vs.getString("startDate"))){
			sql += " and begin_time >= to_date('"+ vs.getString("startDate") +"','yyyyMMdd' )";
		}
		if(StringUtils.isNotEmpty(vs.getString("endDate"))){
			sql += " and end_time <= to_date('"+ vs.getString("endDate") +"','yyyyMMdd')";
		}
		if(StringUtils.isNotEmpty(vs.getString("status"))){
			sql +=  " and run_status='" + vs.getString("status") + "'";
		}
		sql += " order by begin_time desc";
		List<Map> list = dao.queryByPage(sql, start, end);
		
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setHasPaged(true);
		dto.setTotalCount(dao.queryTotalCount(sql));
		dto.setRecords(list);
		return dto;
	}

}
