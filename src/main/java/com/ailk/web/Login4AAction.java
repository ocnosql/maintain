package com.ailk.web;

import com.ailk.core.base.action.BaseAction;
import com.ailk.model.ValueSet;
import com.ailk.util.DateUtil;
import com.ailk.webservice.*;
import com.google.gson.Gson;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login4AAction extends BaseAction {
    public static final Log LOG = LogFactory.getLog(Login4AAction.class);
    private static final long serialVersionUID = 1L;
	private static String AJAXRTN = "ajax_rtn";
	private String ajaxStr;

	public static final  String DATEFORMAT_PATTERN = "yyyyMMddHHmmss";

	
	public String query(){
		return AJAXRTN;
	}

	public String login4A(){

		/**
		String APPACCTID = "2000185534";
		String TOKEN = "32|-101|62|-100|29|-74|-57|95|65|28|105|55|16|111|78|-110|101|-7|57|55|-26|-14|-118|31|77|22|-49|50|-1|-5|86|-64|89";
        **/

		ValueSet vs = new ValueSet();
		bindParams(vs, ServletActionContext.getRequest());

		String APPACCTID = vs.getString("appAcctId");
		String TOKEN = vs.getString("token");

		WsClient4A wsClient4A = new WsClient4A();
		CheckAiuapTokenSoap_ReqUSERREQ reqUSERREQ = new CheckAiuapTokenSoap_ReqUSERREQ();

		CheckAiuapTokenSoap_ReqHEAD reqHEAD = new CheckAiuapTokenSoap_ReqHEAD();
		reqHEAD.setCODE("");
		reqHEAD.setSID("");
		reqHEAD.setTIMESTAMP(DateUtil.format(new Date(), DATEFORMAT_PATTERN));
		reqHEAD.setSERVICEID(wsClient4A.get("ws.serviceid4A"));

		CheckAiuapTokenSoap_ReqBODY reqBODY = new CheckAiuapTokenSoap_ReqBODY();
		reqBODY.setTOKEN(TOKEN);
		reqBODY.setAPPACCTID(APPACCTID);

		reqUSERREQ.setHEAD(reqHEAD);
		reqUSERREQ.setBODY(reqBODY);

		LOG.info("--------------输入---------------------");
		LOG.info(XStreamHandle.toXml(reqUSERREQ, false));
		LOG.info("--------------输出---------------------");
		CheckAiuapTokenSoap_ResUSERRSP resUSERRSP = wsClient4A.getCheckAiuapTokenSoapRes(reqUSERREQ);
		LOG.info(XStreamHandle.toXml(resUSERRSP,false));

		//测试时放开  正式使用时关闭
		/**
		CheckAiuapTokenSoap_ResBODY resBODY = resUSERRSP.getBODY();
		resBODY.setRSP("0");
		resUSERRSP.setBODY(resBODY);
		 **/
		LOG.info("RSP:"+resUSERRSP.getBODY().getRSP().toString());
		if(resUSERRSP!=null&&resUSERRSP.getBODY()!=null&&resUSERRSP.getBODY().getRSP()!=null&&"0".equals(resUSERRSP.getBODY().getRSP())){
			//鉴权成功
			ServletActionContext.getRequest().getSession().setAttribute("user", true);
			Map map = new HashMap();
			map.put("success", true);
			Gson gs = new Gson();
			this.setAjaxStr(gs.toJson(map));
			LOG.info("鉴权成功");
			return AJAXRTN;
		}else{
			//鉴权失败
			Map map = new HashMap();
			map.put("success", false);
			Gson gs = new Gson();
			this.setAjaxStr(gs.toJson(map));
			LOG.info("鉴权失败");
			return AJAXRTN;
		}
	}

	public static String getAJAXRTN() {
		return AJAXRTN;
	}

	public static void setAJAXRTN(String aJAXRTN) {
		AJAXRTN = aJAXRTN;
	}

	public String getAjaxStr() {
		return ajaxStr;
	}

	public void setAjaxStr(String ajaxStr) {
		this.ajaxStr = ajaxStr;
	}


}
