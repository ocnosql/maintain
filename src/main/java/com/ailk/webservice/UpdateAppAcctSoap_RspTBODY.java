package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("BODY")
public class UpdateAppAcctSoap_RspTBODY {

    public UpdateAppAcctSoap_RspTBODY(){}

    /**
     *变更模式，可选值为add、delete、change、chgstatus、resetpwd，分别帐号新增、帐号修改、帐号删除、帐号加/解锁、重置密码操作。	String	16
     */
    @XStreamAlias("MODIFYMODE")
    public String MODIFYMODE;

    /**
     * 用户标识	String	16
     */
    @XStreamAlias("USERID")
    public String USERID;

    /**
     *从帐号登录名	String	16
     */
    @XStreamAlias("LOGINNO")
    public String LOGINNO;

    /**
     * 应用系统执行结果信息0成功 非0失败	String	16
     */
    @XStreamAlias("RSP")
    public String RSP;

    /**
     * 错误描述	String	64
     */
    @XStreamAlias("ERRDESC")
    public String ERRDESC;

    public UpdateAppAcctSoap_RspTBODY(String MODIFYMODE, String USERID, String LOGINNO, String RSP, String ERRDESC){
        this.MODIFYMODE = MODIFYMODE;
        this.USERID = USERID;
        this.LOGINNO = LOGINNO;
        this.RSP = RSP;
        this.ERRDESC = ERRDESC;
    }

    public String getMODIFYMODE() {
        return MODIFYMODE;
    }

    public void setMODIFYMODE(String MODIFYMODE) {
        this.MODIFYMODE = MODIFYMODE;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getLOGINNO() {
        return LOGINNO;
    }

    public void setLOGINNO(String LOGINNO) {
        this.LOGINNO = LOGINNO;
    }

    public String getRSP() {
        return RSP;
    }

    public void setRSP(String RSP) {
        this.RSP = RSP;
    }

    public String getERRDESC() {
        return ERRDESC;
    }

    public void setERRDESC(String ERRDESC) {
        this.ERRDESC = ERRDESC;
    }
}
