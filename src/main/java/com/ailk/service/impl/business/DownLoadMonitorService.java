package com.ailk.service.impl.business;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.pool.PoolConstants;
import com.ailk.service.IQueryService;
import com.ailk.util.DateUtil;

public class DownLoadMonitorService implements IQueryService{
	
	BaseDao dao = new BaseDao(PoolConstants.CSQRY);

	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultDTO loadData(ValueSet vs) {
		String queryMonth = vs.getString("queryMonth");
		if(StringUtils.isEmpty(queryMonth)){
			throw new AppRuntimeException("查询月份不能为空");
		}
		String sql = " select cdrtype, count(1) 下载次数 from qry_download_order " +
				     " where is_large=1 " + 
				     " and insertdate>=to_date('" + queryMonth + "','yyyymm')"  + 
				     " and insertdate<to_date('"+ DateUtil.format(DateUtil.addMonth(DateUtil.parse(queryMonth, "yyyyMM"), 1), "yyyyMM") + "','yyyymm')" +
				     " group by cdrtype";
		
		List<Map> records = dao.query(sql);
		
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setRecords(records);
		dto.setTotalCount(records.size());
		
		return dto;
	}

}
