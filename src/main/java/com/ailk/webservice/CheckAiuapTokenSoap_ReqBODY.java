package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/24.
 */
@XStreamAlias("BODY")
public class CheckAiuapTokenSoap_ReqBODY {

    public CheckAiuapTokenSoap_ReqBODY(){}

    /**
     * 从请求嵌套URL中接受的凭证参数，外围应用系统将其转发给4A管理平台进行校验。	String	200
     */
    @XStreamAlias("TOKEN")
    public String TOKEN;

    /**
     * 为4A侧生成的代表应用从帐号的唯一标识，与应用侧的用户标识或登录名无关
     */
    @XStreamAlias("APPACCTID")
    public String APPACCTID;


    public CheckAiuapTokenSoap_ReqBODY(String TOKEN, String APPACCTID){
        this.TOKEN = TOKEN;
        this.APPACCTID = APPACCTID;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getAPPACCTID() {
        return APPACCTID;
    }

    public void setAPPACCTID(String APPACCTID) {
        this.APPACCTID = APPACCTID;
    }
}
