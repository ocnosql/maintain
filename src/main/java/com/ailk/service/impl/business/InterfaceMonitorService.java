package com.ailk.service.impl.business;

import java.util.List;
import java.util.Map;

import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.pool.PoolConstants;
import com.ailk.service.IQueryService;
import com.ailk.util.DateUtil;

public class InterfaceMonitorService  implements IQueryService{

	BaseDao dao = new BaseDao(PoolConstants.CSQRY);
	
	private static final String TABLE_PREFIX = "qry_bill_log_";
	
	@Override
	public ResultDTO loadData(ValueSet vs) {
		
		String startDate = vs.getString("startDate").replace("-", "");
		String endDate = vs.getString("endDate").replace("-", "");
		List<String> dayList = DateUtil.getIntervMonth(startDate, endDate, "yyyyMMdd");
		
		String sql = " select t1.interface_type 接口名称, all_num 调用总次数, nvl(success_num,0) 成功次数, round(nvl(success_num,0)/all_num * 100, 2)||'%' 成功率 " +
				     " from (select t.interface_type, count(1) all_num from (${table}) t group by interface_type) t1" +
				     " 	left join" +
				     " (select t.interface_type,count(1) success_num from (${table}) t where t.is_success=1 group by interface_type) t2" +
				     " on t1.interface_type=t2.interface_type";
		
		String table = "";
		for(int i = 0; i < dayList.size(); i++){
			table += "select * from " + TABLE_PREFIX + dayList.get(i);
			 if(i != dayList.size() - 1){
				 table += " union ";
			 }
		 }
		
		sql = sql.replace("${table}", table);
		
		List<Map> records = dao.query(sql);
		
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setRecords(records);
		dto.setTotalCount(records.size());

		
		return dto;
	}
}