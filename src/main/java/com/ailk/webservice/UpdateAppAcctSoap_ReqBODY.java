package com.ailk.webservice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by scj on 2016/8/23.
 */
@XStreamAlias("BODY")
public class UpdateAppAcctSoap_ReqBODY {

    public UpdateAppAcctSoap_ReqBODY(){}

    /**
     *发起操作的管理员从帐号登录名。	String	16
     */
    @XStreamAlias("OPERATORID")
    public String OPERATORID;

    /**
     * 发起操作的管理员从帐号密码。根据配置信息决定是否有此标签。如果对方需要认证信息，需要从帐号和密码，服务方根据此信息确认是合法的调用方，以保证调用安全性。	String	256
     */
    @XStreamAlias("OPERATORPWD")
    public String OPERATORPWD;

    /**
     * 发起操作的管理员客户端IP，可选属性	String	20
     */
    @XStreamAlias("OPERATORIP")
    public String OPERATORIP;

    /**
     * 变更模式，可选值为add、delete、change、chgstatus、resetpwd，分别帐号新增、帐号修改、帐号删除、帐号加/解锁、重置密码操作。
     * 备注：当值为delete时，只传入USERID标签；当值为chgstatus时，只传入USERID和STATUS标签；当值为resetpwd时，只传入USERID和PASSWORD标签，当为add时只传入除STATUS之外的全量标签，当为change时传入除STATUS和PASSWORD之外的全量标签。	String	16
     */
    @XStreamAlias("MODIFYMODE")
    public String MODIFYMODE;

    @XStreamAlias("USERINFO")
    public UpdateAppAcctSoap_ReqUSERINFO USERINFO;

    public UpdateAppAcctSoap_ReqBODY(String OPERATORID, String OPERATORPWD, String OPERATORIP, String MODIFYMODE, UpdateAppAcctSoap_ReqUSERINFO USERINFO){
        this.OPERATORID = OPERATORID;
        this.OPERATORPWD = OPERATORPWD;
        this.OPERATORIP = OPERATORIP;
        this.MODIFYMODE = MODIFYMODE;
        this.USERINFO = USERINFO;
    }

    public String getOPERATORID(){
        return this.OPERATORID;
    }

    public void setOPERATORID(String OPERATORID){
        this.OPERATORID = OPERATORID;
    }

    public String getOPERATORPWD(){
        return this.OPERATORPWD;
    }

    public void setOPERATORPWD(String OPERATORPWD){
        this.OPERATORPWD = OPERATORPWD;
    }

    public String getOPERATORIP(){
        return this.OPERATORIP;
    }

    public void setOPERATORIP(String OPERATORIP){
        this.OPERATORIP = OPERATORIP;
    }

    public String getMODIFYMODE(){
        return this.MODIFYMODE;
    }

    public void setMODIFYMODE(String MODIFYMODE){
        this.MODIFYMODE = MODIFYMODE;
    }

    public UpdateAppAcctSoap_ReqUSERINFO getUSERINFO() {
        return USERINFO;
    }

    public void setUSERINFO(UpdateAppAcctSoap_ReqUSERINFO USERINFO) {
        this.USERINFO = USERINFO;
    }
}
