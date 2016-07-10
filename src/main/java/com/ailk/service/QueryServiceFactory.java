package com.ailk.service;

import com.ailk.service.impl.*;
import com.ailk.service.impl.business.DealNumMonitorService;
import com.ailk.service.impl.business.DealTimeMonitorService;
import com.ailk.service.impl.business.DownLoadMonitorService;
import com.ailk.service.impl.business.InterfaceMonitorService;

public class QueryServiceFactory {

	public static IQueryService getQueryService(String queryType){
		IQueryService queryService = null;
		if("redis".equals(queryType)){
		} else if("ocnosql".equals(queryType)){
		}else if("drquery".equals(queryType)){
			queryService = new DrqueryService();
		}else if("drquery_zd".equals(queryType)){
			queryService = new DrqueryZdService();
		}else if("drqueryMonitor".equals(queryType)){
			queryService = new DrqueryMonitorService();
		}else if("ocdcMonitor".equals(queryType)){
			queryService = new OCDCMonitorService();
		}else if("ocdcMonitorDetail".equals(queryType)){
		}else if("interfaceService".equals(queryType)){
			queryService = new InterfaceService();
		}else if("dealNumMonitorService".equals(queryType)){
			queryService = new DealNumMonitorService();
		}else if("dealTimeMonitorService".equals(queryType)){
			queryService = new DealTimeMonitorService();
		}else if("interfaceMonitorService".equals(queryType)){
			queryService = new InterfaceMonitorService();
		}else if("downLoadMonitorService".equals(queryType)){
			queryService = new DownLoadMonitorService();
		} else if("ocnosql_zd".equals(queryType)){
			//queryService = new ZdQueryService();
		}else if("switchDB".equals(queryType)){
		}
		
		return queryService;
	}
}
