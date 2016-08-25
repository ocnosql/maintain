package com.ailk.webservice;

import com.ailk.model.ResultDTO;
import com.ailk.model.ws.User;
import com.ailk.service.impl.UserService;
import com.ailk.util.DateUtil;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by scj on 2016/8/22.
 */
public class WsServer4A {

    public static final Log LOG = LogFactory.getLog(WsServer4A.class);

    public static final  String MODIFY_TYPE_ADD = "add";
    public static final  String MODIFY_TYPE_DELETE = "delete";
    public static final  String MODIFY_TYPE_CHANGE = "change";
    public static final  String MODIFY_TYPE_CHANGESTATUS = "chgstatus";
    public static final  String MODIFY_TYPE_RESETPWD = "resetpwd";
    public static final  String DATEFORMAT_PATTERN = "yyyyMMddHHmmss";

    public String UpdateAppAcctSoap(String RequestInfo){

        LOG.info("----------------UpdateAppAcctSoap输入-----------------------");
        LOG.info(XStreamHandle.toXml(XStreamHandle.toBean(RequestInfo, UpdateAppAcctSoap_ReqUSERMODIFYREQ.class),true));

        UpdateAppAcctSoap_ReqUSERMODIFYREQ request =  XStreamHandle.toBean(RequestInfo, UpdateAppAcctSoap_ReqUSERMODIFYREQ.class);
        UserService userService = new UserService();
        if(request.getBODY().getMODIFYMODE()!=null&& (
                MODIFY_TYPE_ADD.equals(request.getBODY().getMODIFYMODE())||
                        MODIFY_TYPE_DELETE.equals(request.getBODY().getMODIFYMODE())||
                        MODIFY_TYPE_RESETPWD.equals(request.getBODY().getMODIFYMODE())||
                        MODIFY_TYPE_CHANGESTATUS.equals(request.getBODY().getMODIFYMODE()))
                ){

            User user = new User();
            user.setName(request.getBODY().getUSERINFO().getLOGINNO());
            user.setPasswd(request.getBODY().getUSERINFO().getPASSWORD());
            user.setIs_enable(request.getBODY().getUSERINFO().getSTATUS());

            ResultDTO resultDTO = null;
            if(MODIFY_TYPE_ADD.equals(request.getBODY().getMODIFYMODE())){
                resultDTO = userService.addUser(user);
            }else if(MODIFY_TYPE_DELETE.equals(request.getBODY().getMODIFYMODE())){
                resultDTO = userService.deleteUser(user);
            }else if(MODIFY_TYPE_RESETPWD.equals(request.getBODY().getMODIFYMODE())){
                resultDTO = userService.resetUser(user);
            }else if(MODIFY_TYPE_CHANGESTATUS.equals(request.getBODY().getMODIFYMODE())){
                resultDTO = userService.changeStatusUser(user);
            }else{ resultDTO = new ResultDTO();}

            if(resultDTO.isSuccess()){
                UpdateAppAcctSoap_RspTUSERMODIFYRSP response = new UpdateAppAcctSoap_RspTUSERMODIFYRSP();

                UpdateAppAcctSoap_RspHEAD responseHead = new UpdateAppAcctSoap_RspHEAD();
                responseHead.setCODE(request.getHEAD().getCODE());
                responseHead.setSID(request.getHEAD().getSID());
                responseHead.setTIMESTAMP(DateUtil.format(new Date(), DATEFORMAT_PATTERN));
                responseHead.setSERVICEID(request.getHEAD().getSERVICEID());

                UpdateAppAcctSoap_RspTBODY responseBody = new UpdateAppAcctSoap_RspTBODY();
                responseBody.setMODIFYMODE(request.getBODY().getMODIFYMODE());
                responseBody.setUSERID(request.getBODY().getUSERINFO().getLOGINNO());
                //responseBody.setUSERID(resultDTO.getMessage());
                responseBody.setLOGINNO(request.getBODY().getUSERINFO().getLOGINNO());
                responseBody.setRSP("0");
                responseBody.setERRDESC("success");

                response.setHEAD(responseHead);
                response.setBODY(responseBody);
                LOG.info("----------------UpdateAppAcctSoap输出-----------------------");
                LOG.info(XStreamHandle.toXml(response,true));
                return  XStreamHandle.toXml(response,true);
            }else{
                //不可识别的操作类型
                UpdateAppAcctSoap_RspFUSERMODIFYRSP response = new UpdateAppAcctSoap_RspFUSERMODIFYRSP();

                UpdateAppAcctSoap_RspHEAD responseHead = new UpdateAppAcctSoap_RspHEAD();
                responseHead.setCODE("");
                responseHead.setSID("");
                responseHead.setTIMESTAMP(DateUtil.format(new Date(),DATEFORMAT_PATTERN));
                responseHead.setSERVICEID(request.getHEAD().getSERVICEID());

                UpdateAppAcctSoap_RspFBODY responseBody = new UpdateAppAcctSoap_RspFBODY();
                responseBody.setKEY(request.getBODY().getOPERATORID());
                responseBody.setERRCODE("");
                responseBody.setERRDESC(resultDTO.getMessage());

                response.setHEAD(responseHead);
                response.setBODY(responseBody);
                LOG.info("----------------UpdateAppAcctSoap输出-----------------------");
                LOG.info(XStreamHandle.toXml(response,true));
                return  XStreamHandle.toXml(response,true);
            }
        }else if(request.getBODY().getMODIFYMODE()!=null&&MODIFY_TYPE_CHANGE.equals(request.getBODY().getMODIFYMODE())){
            //本系统还没设计账号其他信息，所以直接返回成功
            UpdateAppAcctSoap_RspTUSERMODIFYRSP response = new UpdateAppAcctSoap_RspTUSERMODIFYRSP();

            UpdateAppAcctSoap_RspHEAD responseHead = new UpdateAppAcctSoap_RspHEAD();
            responseHead.setCODE("");
            responseHead.setSID("");
            responseHead.setTIMESTAMP(DateUtil.format(new Date(),DATEFORMAT_PATTERN));
            responseHead.setSERVICEID(request.getHEAD().getSERVICEID());

            UpdateAppAcctSoap_RspTBODY responseBody = new UpdateAppAcctSoap_RspTBODY();
            responseBody.setMODIFYMODE(request.getBODY().getMODIFYMODE());
            responseBody.setUSERID(request.getBODY().getUSERINFO().getUSERID());
            responseBody.setLOGINNO(request.getBODY().getUSERINFO().getLOGINNO());
            responseBody.setRSP("0");
            responseBody.setERRDESC("");

            response.setHEAD(responseHead);
            response.setBODY(responseBody);
            LOG.info("----------------UpdateAppAcctSoap输出-----------------------");
            LOG.info(XStreamHandle.toXml(response,true));
            return  XStreamHandle.toXml(response,true);
        }else{
            //不可识别的操作类型
            UpdateAppAcctSoap_RspFUSERMODIFYRSP response = new UpdateAppAcctSoap_RspFUSERMODIFYRSP();

            UpdateAppAcctSoap_RspHEAD responseHead = new UpdateAppAcctSoap_RspHEAD();
            responseHead.setCODE("");
            responseHead.setSID("");
            responseHead.setTIMESTAMP(DateUtil.format(new Date(),DATEFORMAT_PATTERN));
            responseHead.setSERVICEID(request.getHEAD().getSERVICEID());

            UpdateAppAcctSoap_RspFBODY responseBody = new UpdateAppAcctSoap_RspFBODY();
            responseBody.setKEY(request.getBODY().getOPERATORID());
            responseBody.setERRCODE("");
            responseBody.setERRDESC("不可识别的MODIFYMODE");

            response.setHEAD(responseHead);
            response.setBODY(responseBody);
            LOG.info("----------------UpdateAppAcctSoap输出-----------------------");
            LOG.info(XStreamHandle.toXml(response,true));
            return  XStreamHandle.toXml(response,true);
        }
        //return XStreamHandle.toXml("",true);
    }


}
