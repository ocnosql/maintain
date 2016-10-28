package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("BODY")
public class UpdateAppAcctSoap_RspFBODY {

    public UpdateAppAcctSoap_RspFBODY(){}

    /**
     *执行失败的操作中的关键标志，此处填写变更失败的从帐号登录名。	String	16
     */
    @XStreamAlias("KEY")
    public String KEY;

    /**
     * 错误代码，暂不填	String	8
     */
    @XStreamAlias("ERRCODE")
    public String ERRCODE;

    /**
     *错误描述	String	64
     */
    @XStreamAlias("ERRDESC")
    public String ERRDESC;


    public UpdateAppAcctSoap_RspFBODY(String KEY, String ERRCODE, String ERRDESC){
        this.KEY = KEY;
        this.ERRCODE = ERRCODE;
        this.ERRDESC = ERRDESC;
    }

    public String getKEY() {
        return KEY;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public String getERRCODE() {
        return ERRCODE;
    }

    public void setERRCODE(String ERRCODE) {
        this.ERRCODE = ERRCODE;
    }

    public String getERRDESC() {
        return ERRDESC;
    }

    public void setERRDESC(String ERRDESC) {
        this.ERRDESC = ERRDESC;
    }
}
