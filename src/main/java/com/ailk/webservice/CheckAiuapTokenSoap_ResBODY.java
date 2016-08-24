package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/24.
 */
@XStreamAlias("BODY")
public class CheckAiuapTokenSoap_ResBODY {

    public CheckAiuapTokenSoap_ResBODY(){}

    /**
     * 成功标识0成功 非0失败	String	16
     */
    @XStreamAlias("RSP")
    public String RSP;

    /**
     * 当前登录主帐号。应用侧获取当前主帐号，应用记录审计日志时需要把当前主帐号发送到4A	String	20
     */
    @XStreamAlias("MAINACCTID")
    public String MAINACCTID;

    /**
     * 当前从帐号登录名，当RSP为非0时，不返回此标签。与请求参数中的APPACCTID意义不同	String	16
     */
    @XStreamAlias("APPACCTID")
    public String APPACCTID;


    public CheckAiuapTokenSoap_ResBODY(String RSP, String MAINACCTID, String APPACCTID){
        this.RSP = RSP;
        this.MAINACCTID = MAINACCTID;
        this.APPACCTID = APPACCTID;
    }

    public String getRSP() {
        return RSP;
    }

    public void setRSP(String RSP) {
        this.RSP = RSP;
    }

    public String getMAINACCTID() {
        return MAINACCTID;
    }

    public void setMAINACCTID(String MAINACCTID) {
        this.MAINACCTID = MAINACCTID;
    }

    public String getAPPACCTID() {
        return APPACCTID;
    }

    public void setAPPACCTID(String APPACCTID) {
        this.APPACCTID = APPACCTID;
    }
}
