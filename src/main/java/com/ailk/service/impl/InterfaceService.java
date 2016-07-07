package com.ailk.service.impl;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;
import com.ailk.service.IQueryService;
import com.ailk.util.PropertiesUtil;
import com.ailk.util.RestUtil;

public class InterfaceService implements IQueryService{

	private static Log log = LogFactory.getLog(InterfaceService.class);
	
	@Override
	public ResultDTO loadData(ValueSet vs) {
		
		String itemName = vs.getString("itemName");//接口名 如:QUERYDISPLAYDATE_2003
//		String restName = vs.getString("restName");
		String restUri = vs.getString("uri");
//		if(StringUtils.isEmpty(restUri)){
//			restUri = PropertiesUtil.getProperty("runtime.properties", "drquery.rest.default");
//		}else{
//			restUri = PropertiesUtil.getProperty("runtime.properties", restUri);
//		}
//		
//		if(StringUtils.isEmpty(restUri)){
//			throw new RuntimeException("DRQuery uri property '"+ restUri +"' is not set, please check the property  in the file 'runtime.properties'.");
//		}
		log.info("request uri: " + restUri + itemName);
		
		String returnData = null;
		try {
			returnData = RestUtil.post(restUri + itemName, vs.getString("retXml"), "GB2312");
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		
		ResultDTO dto = new ResultDTO();
		dto.setSuccess(true);
		dto.setMessage(returnData);
		
		return dto;
	}

	
	public static void main(String[] args) {
		String xml = "<REQ_PARAM><PUB_INFO><SYS_OP_ID>10010852</SYS_OP_ID><SYS_PASSWORD>7c6a180b360e4c</SYS_PASSWORD><OP_ID>10058805</OP_ID><OP_ORG_ID>10002268</OP_ORG_ID><CLIENT_IP>202.195.242.5</CLIENT_IP><MAC_ADDRESS>74-E5-0B-3B-0F-54</MAC_ADDRESS><REQ_SERIAL_NO>2013051304323651</REQ_SERIAL_NO><MENU_ID>18001</MENU_ID></PUB_INFO><BUSI_INFO><BILL_IDS><BILL_ID>15158133881</BILL_ID></BILL_IDS><BILL_MONTHS><BILL_MONTH>201406</BILL_MONTH></BILL_MONTHS><REGION_CODE>571</REGION_CODE><USER_ID>5011827199</USER_ID></BUSI_INFO></REQ_PARAM>";
		try {
			String returnData = RestUtil.post("http://20.26.17.225:8888/billAction?actionId=ESB_QRY_BILL_002", xml, "GB2312");
			System.out.println(returnData);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
}
